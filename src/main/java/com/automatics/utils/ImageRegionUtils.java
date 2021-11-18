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
package com.automatics.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.region.ImageRegionInfo;
import com.automatics.region.RegionInfo;

/**
 * 
 * Utils for processing image region xmls
 *
 */
public class ImageRegionUtils {

    /** SLF4J LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageRegionUtils.class);

    /**
     * Gets region info for given region name
     * 
     * @param filepath
     *            RegionInfo xml file path
     * @param regionName
     *            Region Name
     * @return Return RegionInfo
     */
    public static RegionInfo getRegionInfo(String filepath, String regionName) {
	if ((regionName == null) || (filepath == null) || (filepath.isEmpty())) {
	    throw new IllegalArgumentException("xmlPath cannot be null or empty");
	}

	RegionInfo regionInfo = null;

	ImageRegionInfo imageRegionInfo = null;
	try {
	    imageRegionInfo = loadFromXML(ImageRegionUtils.class, filepath);
	} catch (FileNotFoundException e) {
	    LOGGER.error("Error reading file: {}", filepath, e);
	}
	LOGGER.debug("imageRegionInfo " + imageRegionInfo);
	if (imageRegionInfo != null) {
	    regionInfo = imageRegionInfo.getRegion(regionName);
	    if (regionInfo != null) {
		String relativeJPGPath = getRelativePathForJPG(regionInfo, filepath);
		regionInfo.setFilepath(relativeJPGPath);
		LOGGER.debug("regionInfo " + regionInfo + " regionName " + regionName);
	    }
	}

	return regionInfo;
    }

    /**
     * Parses the region info xml file
     * 
     * @param inputStream
     * @return Returns ImageRegionInfo
     * @throws JAXBException
     */
    public static ImageRegionInfo loadFromJaxbXML(InputStream inputStream) throws JAXBException {
	LOGGER.debug("Unmarshalling imageRegionInfo.");
	if (inputStream == null) {
	    LOGGER.debug("InputStream was null.");
	    throw new IllegalArgumentException("InputStream cannot be null.");
	}

	ImageRegionInfo imageRegionInfo = null;

	JAXBContext jContext = JAXBContext.newInstance(new Class[] { ImageRegionInfo.class });
	Unmarshaller unmarshaller = jContext.createUnmarshaller();

	imageRegionInfo = (ImageRegionInfo) unmarshaller.unmarshal(inputStream);

	return imageRegionInfo;
    }

    /**
     * Loads xml for region info
     * 
     * @param resourceClass
     *            Resource class for loading file
     * @param xmlPath
     *            Xml file path
     * @return Return ImageRegionInfo
     * @throws FileNotFoundException
     */
    private static final ImageRegionInfo loadFromXML(Class<?> resourceClass, String xmlPath)
	    throws FileNotFoundException {
	if ((xmlPath == null) || (xmlPath.isEmpty())) {
	    LOGGER.error("xmlPath cannot be null or empty");
	    throw new IllegalArgumentException("xmlPath cannot be null or empty");
	}

	ImageRegionInfo imageRegInfo = null;

	InputStream is = getInputStream(resourceClass, xmlPath);
	if (is != null) {
	    try {
		imageRegInfo = loadFromJaxbXML(is);
	    } catch (Exception e) {
		LOGGER.warn("Error loading serialized object: " + e.getMessage());
		LOGGER.warn("This may be a legacy file format.");
	    }
	    closInputStream(is);

	    LOGGER.debug("imageRegInfo " + imageRegInfo);
	}

	if (imageRegInfo == null) {
	    throw new FileNotFoundException(xmlPath + " could not be found on disk or as resource");
	}

	loadRefImage(resourceClass, imageRegInfo, xmlPath);

	return imageRegInfo;
    }

    /**
     * Close the stream
     * 
     * @param is
     *            InputStream
     */
    private static void closInputStream(InputStream is) {
	try {
	    is.close();
	} catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
    }

    /**
     * Gets the inputstream
     * 
     * @param resourceClass
     *            Resource class
     * @param xmlPath
     *            Xml file path
     * @return Gets the inputstream
     */
    private static InputStream getInputStream(Class<?> resourceClass, String xmlPath) {
	InputStream is = null;
	File xmlFile = new File(xmlPath);
	LOGGER.debug("xmlFile " + xmlFile);

	if (xmlFile.isFile()) {
	    LOGGER.debug("loading from disk");
	    try {
		is = new FileInputStream(xmlFile);
	    } catch (FileNotFoundException localFileNotFoundException) {
		is = null;
	    }
	} else {
	    LOGGER.debug("loading from resource");
	    is = loadResource(resourceClass, xmlPath);
	}

	return is;
    }

    /**
     * Loads the resource
     * 
     * @param resourceClass
     * @param path
     * @return Gets the inputstream
     */
    private static InputStream loadResource(Class<?> resourceClass, String path) {
	InputStream is = null;
	if ((resourceClass != null) && (path != null)) {
	    is = resourceClass.getResourceAsStream(path);

	    if ((is == null) && (!(path.startsWith("/")))) {
		is = resourceClass.getResourceAsStream("/" + path);
	    }
	}

	LOGGER.debug("The input stream is {} at path {}", is, path);
	return is;
    }

    /**
     * Loads the reference image mentioned in region info xml
     * 
     * @param resourceClass
     * @param iamgeRegioninfo
     * @param xmlFilePath
     */
    private static void loadRefImage(Class<?> resourceClass, ImageRegionInfo iamgeRegioninfo, String xmlFilePath) {
	List regionList = iamgeRegioninfo.getRegionInfoList();
	if ((regionList != null) && (!(regionList.isEmpty()))) {
	    RegionInfo region = (RegionInfo) regionList.get(0);
	    String file = getRelativePathForJPG(region, xmlFilePath);
	    BufferedImage refImage = null;
	    try {
		refImage = ImageIO.read(getInputStream(resourceClass, file));
	    } catch (IOException e) {
		LOGGER.error("Loading of image at:{} failed with error:{}", file, e.getMessage());
	    }
	    iamgeRegioninfo.setRefImage(refImage);
	} else {
	    LOGGER.error("There are no regions in the loaded ImageRegionInfo.Failed to load any image as well");
	}
    }

    /**
     * Gets the path for reference image
     * 
     * @param regInfo
     * @param filepath
     * @return
     */
    private static String getRelativePathForJPG(RegionInfo regInfo, String filepath) {
	String relativePath = null;
	if ((filepath != null) && (regInfo != null)) {
	    File xmlFile = new File(filepath);
	    File jpegFile = new File(regInfo.getFilepath());
	    if (xmlFile.getParent() == null) {
		relativePath = jpegFile.getName();
	    } else {
		relativePath = xmlFile.getParent() + System.getProperty("file.separator") + jpegFile.getName();
	    }
	}
	return relativePath;
    }

}
