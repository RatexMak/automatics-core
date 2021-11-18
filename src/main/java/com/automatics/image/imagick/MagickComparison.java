/**
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.automatics.image.imagick;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

import org.apache.log4j.Logger;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.core.Stream2BufferedImage;

/**
 * Class which holds the ImageMagick comparison utilities for crop, compare, standard deviation, color space conversion
 * etc.
 *
 * <p>
 * Used <code>im4java</code> library to interface <code>Java</code> calls to the underlying ImageMagick library.
 * </p>
 *
 * @author Rahul R.Prasad
 */
public class MagickComparison {

    /** Constant to store error value for comparison. */
    private static final double ERROR_VAL = Double.NaN;

    /**
     * Stores the NCC accuracy percentage above which the comparison is assumed as success. Currently the comparison
     * accuracy level set as 90%.
     */
    private static final double NCC_ACCURACY = 0.9;

    /**
     * Holds the RMSE error percentage between two images used for comparison. From experiments it has been inferred
     * that for constant color images, RMSE error less than 15% can be considered as matching images.
     */
    private static final double RMSE_ERROR_PERCENTAGE = 0.150;

    /** <code>Log4j</code> logger for {@link MagickComparison} class. */
    private static final Logger LOGGER = Logger.getLogger(MagickComparison.class);

    /** NCC comparison exit code pattern. */
    private static final Pattern NCC_PATTERN = Pattern
	    .compile("org.im4java.core.CommandException:\\s*([0-9]+\\.?[0-9]*)");

    /** RMSE comparison exit code pattern. */
    private static final Pattern RMSE_PATTERN = Pattern.compile("[0-9]+\\.?[0-9]*\\s*\\(([0-9]+\\.?[0-9]*)\\)");

    /** Holds the RMSE error value between two images used for comparison. */
    private double rmseError = RMSE_ERROR_PERCENTAGE;

    /**
     * Does the image comparison on the provided precaptured and live images.<br>
     * During testing, observed that <code>RMSE</code> metric is suited for constant color comparison, where as
     * <code>NCC</code> in gray scale colorspace is appropriate for complex images.<br>
     * In order to distinguish between constant color and complex images, it first finds the standard deviation(sigma)
     * of the pixel values of precaptured image. If it is zero or very close to zero(in case of gradient constant color)
     * assume that it is a constant color image. Otherwise considered it as a complex image.<br>
     * For complex images, it first does a highlight comparison on the mean scale image(1x1). Otherwise, it won't be
     * able to identify the color difference in the grayscaled NCC mode.
     *
     * @param preCapturedImg
     *            Precaptured image for comparison.
     * @param liveImg
     *            Live image for comparison
     *
     * @return true if both precaptured and live image matches. False, otherwise.
     *
     * @throws FailedTransitionException
     *             when it failed to do the comparison using ImageMagick.
     */
    public boolean compare(BufferedImage preCapturedImg, BufferedImage liveImg) {
	boolean compareStatus = false;
	double sigma = getSigmaOfImage(preCapturedImg) * 100;

	if (sigma < 1) {
	    compareStatus = doRmseComparison(preCapturedImg, liveImg);
	} else {

	    if (doHighlightColorComparison(preCapturedImg, liveImg)) {
		LOGGER.info("Highlight color comparison passed");
		compareStatus = doNccComparison(preCapturedImg, liveImg);
	    } else {
		LOGGER.info("Highlight color comparison failed");
	    }
	}

	LOGGER.info("ImageMagick comparison status :" + compareStatus);

	return compareStatus;
    }

    /**
     * Does the <code>NCC</code>(Normalized Cross Correlation) comparison.<br>
     * Note that first the cropped image is converted to grayscale mode, before doing this comparison.<br>
     * Command used : <code>compare -metric NCC live.jpg precaptured.jpg null:-</code>
     *
     * @see <a href="http://en.wikipedia.org/wiki/Cross-correlation#Normalized_cross-correlation"> NCC Metric</a>
     *
     * @param preCapturedImg
     *            Precaptured image for comparison.
     * @param liveImg
     *            Live image for comparison
     *
     * @return true if the image comparison has a minimum accuracy percentage defined by
     *         {@link MagickComparison#NCC_ACCURACY}. False otherwise.
     *
     * @throws FailedTransitionException
     *             when it failed to do the comparison using ImageMagick.
     */
    public boolean doNccComparison(BufferedImage preCapturedImg, BufferedImage liveImg) {
	LOGGER.info("Comparison using Grayscale NCC approach in ImageMagick");

	boolean status = false;

	/* convert precaptured image to grayscale mode. */
	BufferedImage grayPrecapImg = convertToGrayScale(preCapturedImg);

	/* convert live image to grayscale mode. */
	BufferedImage grayLiveImg = convertToGrayScale(liveImg);

	IMOperation operation = getOperation().configureCompareOps(Metric.NCC);

	/* Place holder for precaptured image, live image and save images. */
	operation.addImage();
	operation.addImage();
	operation.addImage("null:-");

	ImageCommand compareCmd = getCompareCommand();

	/*
	 * Since ImageCommand.run() is a void method, it throws the similarity value in the exception trace. eg:
	 * org.im4java.core.CommandException: org.im4java.core.CommandException: 1.00013. The value is usually
	 * normalized between 0 and 1. Value 1 means perfect match and value 0 means totally different.
	 */
	try {
	    compareCmd.run(operation, grayPrecapImg, grayLiveImg);
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Image comparison failed using ImageMagick.Approach used:NCC", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Image comparison failed using ImageMagick. Approach used:NCC", iex);
	} catch (IM4JavaException e) {
	    double compareAccuracy = parseComparisonExitCode(e.getLocalizedMessage(), Metric.NCC);
	    LOGGER.info(" NCC Comparison accuracy :: " + String.format("%.2f", (compareAccuracy * 100)) + " %");

	    if (!Double.isNaN(compareAccuracy) && compareAccuracy >= NCC_ACCURACY) {
		status = true;
	    }
	}

	return status;
    }

    /**
     * Does the <code>RMSE</code>(Root Mean Squared Error) RMSE error percentage calculation is typically useful, when
     * the image to be compared is of constant color. IM compute the pixel color difference and then square the pixel
     * differences. After that it finds the average of all squared differences and return the square root of that. In
     * our case, RMSE with less than 17% error offers good results for plain opaque color comparisons.<br>
     * Command used: <code>compare -metric RMSE live.jpg precap.jpg null:-</code>
     *
     * @param preCapturedImg
     *            Precaptured image for comparison.
     * @param liveImg
     *            Live image for comparison
     *
     * @return true if the pixel comparison has error percentage which is less than
     *         {@link MagickComparison#RMSE_ERROR_PERCENTAGE}.
     *
     * @throws FailedTransitionException
     *             when it failed to do the comparison using ImageMagick.
     */
    public boolean doRmseComparison(BufferedImage preCapturedImg, BufferedImage liveImg)
	    throws FailedTransitionException {
	LOGGER.info("Comparison using RMSE approach in ImageMagick");

	boolean status = false;

	IMOperation operation = getOperation().configureCompareOps(Metric.RMSE);
	LOGGER.info(" Confg done ");
	operation.addImage();
	operation.addImage();
	operation.addImage("null:-");

	ImageCommand compareCmd = getCompareCommand();

	LOGGER.info(" Comparison..!!!! ");

	try {
	    compareCmd.run(operation, preCapturedImg, liveImg);
	    File first = new File("preCapturedImg.png");
	    ImageIO.write(preCapturedImg, "png", first);

	    File secnd = new File("liveImg.png");
	    ImageIO.write(preCapturedImg, "png", secnd);

	    LOGGER.info(" No Exception !!!! ");
	    // Unlike NCC, for 100% match cases RMSE comparison won't throw IM4JavaException. So
	    // setting the status as true.
	    status = true;
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Image comparison failed using ImageMagick. Approach used:RMSE", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Image comparison failed using ImageMagick. Approach used:RMSE", iex);
	} catch (IM4JavaException imjex) {
	    double compareAccuracy = parseComparisonExitCode(imjex.getLocalizedMessage(), Metric.RMSE);
	    LOGGER.info(" RMSE error percentage :: " + String.format("%.2f", (compareAccuracy * 100)) + " %");

	    if (!Double.isNaN(compareAccuracy) && compareAccuracy <= rmseError) {
		status = true;
	    }

	} catch (Exception e) {

	    LOGGER.info(" Exception !!!! " + e);
	}

	return status;
    }

    /**
     * Method to identify standard deviation(sigma) of the input image pixel values. During ImageMagick experiments, it
     * is observed that for constant color images(highlights) <code>
     * RMSE</code> approach works well, whereas for complex images in NCC in grayscale mode offers promising results. So
     * there should be a logic which dynamically identify the image is constant color or not at run time and apply
     * appropriate algorithms.
     *
     * <p>
     * eg: <code>convert live.jpg -format "%[fx:standard_deviation]" info:</code>
     * </p>
     *
     * <p>
     * In order to identify a constant color image, first find the standard deviation of all pixel values. If the value
     * is converging to Zero, it is a constant color channel.
     * </p>
     * 
     * @param image
     *            Image on which standard deviation is to be applied.
     *
     * @return standard deviation of pixel values.
     *
     * @throws FailedTransitionException
     *             the image processing failed using ImageMagick.
     */
    public double getSigmaOfImage(BufferedImage image) throws FailedTransitionException {
	BufferedImage pureImage = getHueOfImage(image);
	double sigma = getStandardDeviation("jpg:-", pureImage);

	return sigma;
    }

    /**
     * Get the Hue(pure color) of the image.
     *
     * <p>
     * For constant color images having slight gradient, the sigma will not converge to Zero. So it will be difficult to
     * distinguish between constant color and complex images. So first convert the image in <code>RGB</code> colorspace
     * to <code>HSL</code> then extract the hue channel. The hue channel returns the pure color of the image. Then take
     * the standard deviation on the hue image. If it is Zero or converging closely to Zero, mark it as a constant color
     * image.<br>
     * Command used: <code>convert precap.jpg -colorspace HSL -channel r -separate +channel -format
     * "%[fx:standard_deviation]" info:</code>
     * </p>
     *
     * @param image
     *            Image on which we need to extract the Hue channel
     *
     * @return Hue image
     *
     * @throws FailedTransitionException
     *             Hue channel extraction from the image failed using ImageMagick.
     */
    public BufferedImage getHueOfImage(BufferedImage image) throws FailedTransitionException {
	IMOperation operation = getOperation().configureHueExtractionArgs();
	operation.addImage();
	operation.addImage("jpg:-");

	ImageCommand command = getConvertCommand();

	Stream2BufferedImage streamBuffer = getStreamBuffer();
	command.setOutputConsumer(streamBuffer);

	try {
	    command.run(operation, image);
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", iex);
	} catch (IM4JavaException imjex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", imjex);
	}

	return streamBuffer.getImage();
    }

    /**
     * Method to crop the image with specified bounds.<br>
     * Command used: <code>convert input.jpg -crop 104x20+56+345 cropped.jpg</code>
     *
     * @param image
     *            Input image which is to be cropped.
     * @param width
     *            width of sub image to be cropped
     * @param height
     *            height of sub image to be cropped
     * @param startX
     *            X position of sub image
     * @param startY
     *            Y position of sub image.
     *
     * @return cropped image.
     *
     * @throws FailedTransitionException
     *             when ImageMagick failed to crop the area from the input image.
     */
    public BufferedImage cropImage(BufferedImage image, int width, int height, int startX, int startY)
	    throws FailedTransitionException {
	IMOperation operation = getOperation().getIMOps();
	operation.addRawArgs("+repage");

	operation.addImage();
	operation.crop(width, height, startX, startY);
	operation.addImage("jpg:-");

	ImageCommand convert = getConvertCommand();
	Stream2BufferedImage streamBuffer = getStreamBuffer();
	convert.setOutputConsumer(streamBuffer);

	try {
	    convert.run(operation, image);
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Cropping of image failed using ImageMagick", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Cropping of image failed using ImageMagick", iex);
	} catch (IM4JavaException imjex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Cropping of image failed using ImageMagick", imjex);
	}

	return streamBuffer.getImage();
    }

    /**
     * Convert the image to the GrayScale colorspace.<br>
     * Command used: <code>convert live.jpg -colorspace gray live_gray.jpg</code><br>
     * Note that the input image used should be in the <code>RGB</code> colorspace.
     *
     * @param rgbImg
     *            Image to be converted to GrayScale mode
     *
     * @return GrayScale image.
     *
     * @throws FailedTransitionException
     *             when ImageMagick failed to convert image to the grayscale mode.
     */
    public BufferedImage convertToGrayScale(BufferedImage rgbImg) throws FailedTransitionException {
	IMOperation operation = getOperation().configureGrayScaleArgs();
	operation.addImage();
	operation.addImage("jpg:-");

	ImageCommand command = getConvertCommand();
	Stream2BufferedImage streamBuffer = getStreamBuffer();
	command.setOutputConsumer(streamBuffer);

	try {
	    command.run(operation, rgbImg);
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to convert image to the Grayscale colorspace using ImageMagick", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to convert image to the Grayscale colorspace using ImageMagick", iex);
	} catch (IM4JavaException imjex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to convert image to the Grayscale colorspace using ImageMagick", imjex);
	}

	return streamBuffer.getImage();
    }

    /**
     * Save the difference between precaptured and live image for error analysis. Black area in the difference image
     * indicates similar region. Regions which are different in color will be overlapped and displayed in their actual
     * colors.
     *
     * @param precaptured
     *            Precaptured image used for difference computation
     * @param liveImage
     *            Live image used for difference calculation.
     * @param outputDirectory
     *            Directory in which the difference image is to be stored.
     * @param regionName
     *            name of the region currently used for comparison
     *
     * @throws FailedTransitionException
     *             when ImageMagick failed to take the difference between precaptured and live image.
     */
    public void saveDiffImages(BufferedImage precaptured, BufferedImage liveImage, File outputDirectory,
	    String regionName) throws FailedTransitionException {
	IMOperation operation = getOperation().configureDiffImageArgs();

	operation.addImage();
	operation.addImage();
	operation.addImage(outputDirectory + File.separator + regionName + ".jpg");

	ImageCommand command = getConvertCommand();

	try {
	    command.run(operation, precaptured, liveImage);
	    LOGGER.info("Saved ImageMagick difference image :" + regionName);

	} catch (IOException ioex) {
	    LOGGER.error("Failed to save ImageMagick difference image");
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to create difference image for the region:" + regionName, ioex);
	} catch (InterruptedException iex) {
	    LOGGER.error("Failed to save ImageMagick difference image");
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to create difference image for the region:" + regionName, iex);
	} catch (IM4JavaException imjex) {
	    LOGGER.error("Failed to save ImageMagick difference image");
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to create difference image for the region:" + regionName, imjex);
	}
    }

    /**
     * Does the highlight color comparison of the image.<br>
     * It first finds the mean of the image by scaling it in 1x1 dimension. This process is applied for both live and
     * precaptured image. Then compare these two mean color images to verify the highlight or background color of the
     * image.<br>
     * Command used: <code>convert input.jpg -alpha off -depth 8 -scale 1x1 out.jpg</code>
     *
     * @param preCapturedImg
     *            precaptured image used for highlight identification.
     * @param liveImg
     *            live image used for highlight identification.
     *
     * @return true if two mean colored images matches. False, otherwise.
     *
     * @throws FailedTransitionException
     *             when ImageMagick failed to scale or compare the live and precapture images.
     */
    public boolean doHighlightColorComparison(BufferedImage preCapturedImg, BufferedImage liveImg)
	    throws FailedTransitionException {
	LOGGER.info("Comparing highlight color using ImageMagick");

	BufferedImage precapScaledImg = getScaledMeanColorImage(preCapturedImg);
	BufferedImage liveScaledImg = getScaledMeanColorImage(liveImg);
	boolean status = doRmseComparison(precapScaledImg, liveScaledImg);

	return status;
    }

    /**
     * Creates a new instance of {@link Stream2BufferedImage} class. This is mainly used for dependency injection in
     * testing environment.
     *
     * @return New instance of {@link Stream2BufferedImage} class.
     */
    protected Stream2BufferedImage getStreamBuffer() {
	return new Stream2BufferedImage();
    }

    /**
     * Creates a new instance of {@link ImageCommand} class. This is mainly used for dependency injection in testing
     * environment
     *
     * @return New instance of {@link ImageCommand} class.
     */
    protected ImageCommand getImageCommand() {
	return new ImageCommand();
    }

    /**
     * Creates a new instance of {@link Operation} class. This is used for dependency injection in testing environment.
     *
     * @return new instance of {@link Operation} class.
     */
    protected Operation getOperation() {
	return new Operation();
    }

    /**
     * Compute the standard deviation of the image.<br>
     * Command used: <code>identify -verbose live.jpg</code>
     *
     * @param imgName
     *            Name of the input image.
     * @param img
     *            input image for which sigma is to be computed.
     *
     * @return standard deviation for the input image pixels.
     *
     * @throws FailedTransitionException
     *             when ImageMagick failed to get image information.
     */
    private double getStandardDeviation(String imgName, BufferedImage img) throws FailedTransitionException {
	Info imageInfo = getImageInformation(imgName, img);
	String sd = imageInfo.getProperty("Channel statistics:Gray:standard deviation");
	double val = parseComparisonExitCode(sd, Metric.RMSE);

	// if the sigma computed is error(Double.NAN), then set it as with value '1' to denote there
	// are high deviation and probably it is a complex image and needs NCC algorithm.
	return (!Double.isNaN(val)) ? val : 1;
    }

    /**
     * Helper method to query the image information using ImageMagick.
     *
     * @param imgName
     *            Name of the input image.
     * @param img
     *            input image for which the image information is to be queried.
     *
     * @return {@link Info} object containing all image informations.
     *
     * @throws FailedTransitionException
     *             when ImageMagick fails to identify image information.
     */
    private Info getImageInformation(String imgName, BufferedImage img) throws FailedTransitionException {
	Info imageInfo = null;
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	try {
	    ImageIO.write(img, "jpg", baos);

	    InputStream is = new ByteArrayInputStream(baos.toByteArray());
	    imageInfo = new Info(imgName, is);

	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to get Image information using ImageMagick.", ioex);
	} catch (InfoException infoex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to get Image information using ImageMagick.", infoex);
	}

	return imageInfo;
    }

    /**
     * Helper method to parse comparison logs and return the comparison exit code. Comparison exit code helps us to
     * determine how similar the two images are.
     *
     * @param compareLogs
     *            comparison log to be parsed
     * @param mode
     *            Comparison mode, whether RMSE or NCC
     *
     * @return comparison exit code present in the logs. If failed to match the pattern, it returns
     *         {@link MagickComparison#ERROR_VAL} as result.
     */
    private double parseComparisonExitCode(String compareLogs, Metric mode) {
	double compareAccuracy = ERROR_VAL;
	LOGGER.info(" Image Comparison result using Magick  = " + compareLogs);

	if (compareLogs != null) {
	    Pattern pattern = (mode == Metric.NCC) ? NCC_PATTERN : RMSE_PATTERN;
	    Matcher matcher = pattern.matcher(compareLogs);

	    if (matcher.find()) {
		compareAccuracy = Double.parseDouble(matcher.group(1));
	    }
	}

	return compareAccuracy;
    }

    /**
     * Helper method to scale the image in the {@link Operation#configureScaledImageArgs()} configurations and return
     * the scaled images.
     *
     * @param image
     *            image to be scaled
     *
     * @return scaled image.
     *
     * @throws FailedTransitionException
     *             when scaling of image failed using ImageMagick.
     */
    private BufferedImage getScaledMeanColorImage(BufferedImage image) throws FailedTransitionException {
	IMOperation operation = getOperation().configureScaledImageArgs();
	operation.addImage();
	operation.addImage("jpg:-");

	ImageCommand command = getConvertCommand();

	Stream2BufferedImage streamBuffer = getStreamBuffer();
	command.setOutputConsumer(streamBuffer);

	try {
	    command.run(operation, image);
	} catch (IOException ioex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", ioex);
	} catch (InterruptedException iex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", iex);
	} catch (IM4JavaException imjex) {
	    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE,
		    "Failed to extract Hue channel using ImageMagick", imjex);
	}

	return streamBuffer.getImage();
    }

    /**
     * Creates a Compare command object used for Image comparison.
     *
     * @return {@link ImageCommand} object for image comparison.
     */
    private ImageCommand getCompareCommand() {
	ImageCommand compareCmd = getImageCommand();
	compareCmd.setCommand("compare");

	return compareCmd;
    }

    /**
     * Creates a Convert command object for various image processing operations.
     *
     * @return {@link ImageCommand} object for image processing.
     */
    private ImageCommand getConvertCommand() {
	ImageCommand convertCmd = getImageCommand();
	convertCmd.setCommand("convert");

	return convertCmd;
    }

    /**
     * Get t RMSE error percentage.
     *
     * @return RMSE error value
     */
    public double getRmseError() {
	return rmseError;
    }

    /**
     * Set RMSE Error value.
     *
     * @param rmseError
     *            The RMSE error value
     */
    public void setRmseError(double rmseError) {
	this.rmseError = rmseError;
    }

}
