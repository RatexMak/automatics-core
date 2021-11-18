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
package com.automatics.providers.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.providers.rack.AbstractOcrProvider;
import com.automatics.providers.rack.exceptions.OcrException;
import com.automatics.region.OcrRegionInfo;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.NonRackUtils;

/**
 * 
 * Ocr provider implementation
 *
 */
public class AutomaticsOcrProviderImpl extends AbstractOcrProvider {

    private OcrServiceProvider ocrServiceProvider = null;

    Object lock = new Object();

    static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsOcrProviderImpl.class);

    /**
     * Initialize OcrService Provider
     */
    private void initOcrServiceProvider() {
	if (null == ocrServiceProvider) {
	    synchronized (lock) {
		if (null == ocrServiceProvider) {
		    ocrServiceProvider = BeanUtils.getOcrServiceProvider();
		}
	    }
	}
    }

    /**
     * Gets OCR text from current video image
     */
    @Override
    public String getOcrTextFromCurrentVideoImage(OcrRegionInfo ocrRegionInfo, boolean readFullImageRegion) {
	String ocrText = null;
	if (NonRackUtils.isDeskBoxTesting()) {
	    LOGGER.error("Skiping Rack OCR operation for Desk box");
	    return ocrText;
	}

	ocrText = readTextFromCurrentImage(ocrRegionInfo, readFullImageRegion);
	LOGGER.info("OCR text read from current image: {}", ocrText);
	return ocrText;
    }

    /**
     * Gets OCR text from given image
     */
    @Override
    public String getOcrTextFromImage(OcrRegionInfo ocrRegionInfo, BufferedImage image, boolean readFullImageRegion) {
	String ocrText = AutomaticsConstants.EMPTY_STRING;
	if (NonRackUtils.isDeskBoxTesting()) {
	    LOGGER.error("Skiping Rack OCR operation for Desk box");
	    return ocrText;
	}

	ocrText = readTextFromGivenImage(ocrRegionInfo, image, readFullImageRegion);
	LOGGER.info("OCR text read for device {} on current image : {}", device.getHostMacAddress(), ocrText);
	return ocrText;
    }

    /**
     * Wait for OCR region
     */
    @Override
    public boolean waitForOcrRegion(OcrRegionInfo ocrRegion) {
	boolean regionFound = false;

	if (null == ocrServiceProvider) {
	    initOcrServiceProvider();
	}
	if (null != ocrServiceProvider) {
	    regionFound = ocrServiceProvider.waitForOcrRegion(device, ocrRegion);
	}
	return regionFound;
    }

    /**
     * Gets color from current image
     */
    @Override
    public String extractColorFromGivenImage(OcrRegionInfo ocrRegionInfo, BufferedImage image) {
	String colorRead = AutomaticsConstants.EMPTY_STRING;
	String[] colorRegionSize = null;
	String imageLocation = null;
	try {
	    imageLocation = saveImage(device, image);

	    colorRegionSize = imageSize(imageLocation);
	    String[] colorRegionCoordinates = getRegionCoordinates(ocrRegionInfo);

	    if (null == ocrServiceProvider) {
		initOcrServiceProvider();
	    }
	    if (null != ocrServiceProvider) {
		colorRead = ocrServiceProvider.extractColorFromGivenImageRegion(device, colorRegionSize,
			colorRegionCoordinates, imageLocation);
	    }
	} catch (IOException e) {
	    LOGGER.error(e.getMessage(), e);
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}
	return colorRead;
    }

    /**
     * 
     * @param ocrRegionInfo
     * @param readFullImageRegion
     * @return
     */
    public String readTextFromCurrentImage(OcrRegionInfo ocrRegionInfo, boolean readFullImageRegion) {
	String textRead = AutomaticsConstants.EMPTY_STRING;

	try {
	    String imageLocation = captureCurrentScreenAndSave();
	    textRead = readText(ocrRegionInfo, imageLocation, readFullImageRegion);
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}

	return textRead;
    }

    /**
     * Reads text from given image
     * 
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @param image
     *            Image
     * @param readFullImageRegion
     *            if true, full image will be considered for OCR process, otherwise image size mentioned ocr region info
     *            will be considered
     * @return Text from given image
     */
    public String readTextFromGivenImage(OcrRegionInfo ocrRegionInfo, BufferedImage image, boolean readFullImageRegion) {
	String textRead = AutomaticsConstants.EMPTY_STRING;
	String imageLocation = null;
	try {
	    imageLocation = saveImage(device, image);
	    textRead = readText(ocrRegionInfo, imageLocation, readFullImageRegion);
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}
	return textRead;
    }

    /**
     * Reads text from given image
     * 
     * @param ocrRegionInfo
     * @param imageLocation
     * @param readFullImageRegion
     * @return
     */
    private String readText(OcrRegionInfo ocrRegionInfo, String imageLocation, boolean readFullImageRegion) {
	String textRead = AutomaticsConstants.EMPTY_STRING;
	String[] textRegionSize = null;
	boolean isReadFromRegion = !(readFullImageRegion);
	try {
	    if (isReadFromRegion) {
		textRegionSize = getRegionSize(ocrRegionInfo);
	    } else {
		textRegionSize = imageSize(imageLocation);
	    }

	    String[] textRegionCoordinates = getRegionCoordinates(ocrRegionInfo);
	    if (null == ocrServiceProvider) {
		initOcrServiceProvider();
	    }
	    if (null != ocrServiceProvider) {
		textRead = ocrServiceProvider.readTextFromGivenImageRegion(device, textRegionSize,
			textRegionCoordinates, imageLocation);
	    }
	} catch (OcrException | IOException e) {
	    LOGGER.error(e.getMessage(), e);
	}
	LOGGER.info("TEXT READ :" + textRead);
	return textRead;
    }

    /**
     * Save the image for ocr processing
     * 
     * @param dut
     * @param bufferedImage
     * @return
     * @throws Exception
     */
    private String saveImage(Dut dut, BufferedImage bufferedImage) throws Exception {

	String imageName = System.currentTimeMillis() + "_" + AutomaticsUtils.getCleanMac(dut.getHostMacAddress())
		+ "_OCR_PROCESSING_IN";
	String storedImage = AutomaticsTapApi.saveImages(dut, bufferedImage, imageName);
	LOGGER.info("Image location : " + storedImage);
	File image = new File(storedImage);
	if (image.exists() && image.isFile()) {
	    return storedImage;
	} else {
	    throw new Exception("Image could not be captured for OCR processing");
	}
    }

    /**
     * Gets the image size
     * 
     * @param imageLocation
     * @return Gets the image size
     * @throws IOException
     */
    private String[] imageSize(String imageLocation) throws IOException {
	BufferedImage buffImage = ImageIO.read(new File(imageLocation));
	LOGGER.info(" IMAGE WIDTH : {} HEIGHT : {}", buffImage.getWidth(), buffImage.getHeight());
	return new String[] { String.valueOf(buffImage.getWidth()), String.valueOf(buffImage.getHeight()) };
    }

    /**
     * Gets the region coordinates
     * 
     * @param ocrRegionInfo
     * @return Gets the region coordinates
     */
    private String[] getRegionCoordinates(OcrRegionInfo ocrRegionInfo) {
	int x = 0;
	int y = 0;
	if (ocrRegionInfo != null) {
	    x = (int) ocrRegionInfo.getX();
	    y = (int) ocrRegionInfo.getY();
	}
	LOGGER.info(" REGION COORDINATES : X = {} Y = {}", x, y);
	return new String[] { String.valueOf(x), String.valueOf(y) };
    }

    /**
     * Gets the region size
     * 
     * @param ocrRegionInfo
     * @return Gets the region size
     */
    private String[] getRegionSize(OcrRegionInfo ocrRegionInfo) {
	int width = 0;
	int height = 0;
	if (ocrRegionInfo != null) {
	    width = (int) ocrRegionInfo.getWidth();
	    height = (int) ocrRegionInfo.getHeight();
	}
	LOGGER.info(" REGION SIZE COORDINATES : W = {} H = {}", width, height);
	return new String[] { String.valueOf(width), String.valueOf(height) };
    }

    /**
     * Capture the current screen for ocr processing
     *
     * @return
     * @throws Exception
     */
    private String captureCurrentScreenAndSave() throws Exception {

	String imageName = AutomaticsUtils.getCleanMac(device.getHostMacAddress()) + "_OCR_PROCESSING_IN";
	String storedImage = AutomaticsTapApi.getInstance().captureAndSaveImage(device, imageName);

	if (CommonMethods.isNotNull(storedImage)) {
	    LOGGER.info("Stored Image Location : " + storedImage);
	    return storedImage;
	} else {
	    throw new Exception("Image could not be captured for OCR processing");
	}
    }

}
