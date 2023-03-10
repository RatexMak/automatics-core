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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.automatics.constants.ReportsConstants;

/**
 * Common file utility methods.
 *
 * @author Pratheesh
 * @author Arjun P
 */
public class FileUtils {

    /** SLF4j logger. */
    protected static final java.util.logging.Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Retrieve the properties from a specified resource.
     *
     * @param resourceName
     *            fileName
     *
     * @return Returns the list of properties from the resource.
     *
     * @throws IOException
     *             If failed to read the resource file.
     */
    public static Properties getPropertiesFromResource(String resourceName) throws IOException {
	Properties properties = new Properties();

	InputStream inputStream = null;

	try {
		LOGGER.info("++++++++++++++++++++++++++++++ DEBUG load ++++++++++++++++++++++++++++++");
	    inputStream = loadResource(FileUtils.class, resourceName);
		LOGGER.info("resourceName: "+resourceName);
	    properties.load(inputStream);
		LOGGER.info("++++++++++++++++++++++++++++++ DEBUG load ++++++++++++++++++++++++++++++");
	} finally {

	    if (null != inputStream) {

		try {
		    inputStream.close();
		} catch (IOException e) {
		    LOGGER.error("Failed to close the stream.", e);
		}
	    }
	} // end try-catch-finally

	return properties;
    } // end method getPropertiesFromResource

    /**
     * Load the resource and return the stream.
     *
     * @param clazz
     *            Class where the resource file be found.
     * @param resourceName
     *            The absolute resource path.
     *
     * @return the input stream of read resource.
     */
    public static InputStream loadResource(Class clazz, String resourceName) {
	InputStream inputStream = null;

	if (clazz != null && resourceName != null) {
	    inputStream = clazz.getResourceAsStream(resourceName);

	    if (inputStream == null && !resourceName.startsWith("/")) {
		inputStream = clazz.getResourceAsStream("/" + resourceName);
	    }
	}

	return inputStream;
    }

    /**
     * Returns File instance for the given resource.
     *
     * @param clazz
     *            Class where the resource file be found.
     * @param resourcePath
     *            Full path to the resource.
     *
     * @return File instance of the given resource
     */
    public static File getResourceAsFile(Class clazz, String resourcePath) {

	File file = null;
	URL resourceUrl = ClassLoader.getSystemResource(resourcePath);

	// If resource doesn't exist, return a File instance with given resource path
	if (resourceUrl == null) {
	    LOGGER.info("Resource: " + resourcePath + " can't be found. Returning new File instance");

	    URL url = clazz.getResource("/");

	    try {
		URI uri = url.toURI();
		file = new File(uri.getPath() + resourcePath);

		// If failed to load we have to fianlly attempt loading of resource form the
		// test-classes directory.
		if (!file.exists()) {
		    LOGGER.info(String
			    .format("Failed to locate the file at location : %s. So doing a final attempt to load the resource from location : %s.",
				    uri.getPath() + resourcePath, System.getProperty(ReportsConstants.USR_DIR)
					    + ReportsConstants.TC_DIR));
		    file = new File(System.getProperty(ReportsConstants.USR_DIR) + ReportsConstants.TC_DIR + "/"
			    + resourcePath);
		}
	    } catch (URISyntaxException e) {

		LOGGER.error(e.getMessage(), e);
		throw new IllegalStateException("Failed form the URI.", e);
	    }

	} else { // File already exists in the path, return an existing File instance
	    LOGGER.info("Found resource: " + resourcePath);

	    try {
		file = new File(resourceUrl.toURI());
	    } catch (Exception e) {
		Assert.fail("Failed to load resource.", e);
	    }
	}

	return file;
    }

    /**
     * Method to copy files from one source location to a destination location.
     *
     * @param sourceDir
     *            - Source location of file
     * @param destinationDirectory
     *            - destination location
     *
     *            <p>
     *            Copies files from one location to another
     *            </p>
     */

    public static void copyFiles(File sourceDir, File destinationDir) {

	/* verify whether file exist in source location */
	if (!sourceDir.exists()) {
	    LOGGER.info("Source File Not Found!");
	}

	/* if file not exist then create one */
	if (!destinationDir.exists()) {

	    try {
		destinationDir.createNewFile();
		LOGGER.info("Destination file doesn't exist. Creating one!");
	    } catch (IOException e) {
		 LOGGER.error("IOException:", e);
	    }
	}

	FileChannel sourceChannel = null;
	FileChannel destinationChannel = null;

	try {

	    /**
	     * getChannel() returns unique FileChannel object associated a file output stream.
	     */
	    sourceChannel = new FileInputStream(sourceDir).getChannel();

	    destinationChannel = new FileOutputStream(destinationDir).getChannel();

	    if (null != destinationChannel && null != sourceChannel) {
		destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	    }

	} catch (FileNotFoundException e) {
	    LOGGER.error("FileNotFoundException:", e);
	} catch (IOException e) {
	    LOGGER.error("IOException:", e);
	} finally {

	    if (null != sourceChannel) {

		try {
		    sourceChannel.close();
		} catch (IOException e) {
		    LOGGER.error("IOException occurred", e);
		}
	    }

	    if (null != destinationChannel) {

		try {
		    destinationChannel.close();
		} catch (IOException e) {
		    LOGGER.error("IOException:", e);
		}
	    }

	}

    }

    /**
     * Helper method to copy a resource from a jar file and to save it to specified destination.
     *
     * @param resourceName
     *            Resource name to be copied
     * @param destny
     *            Destination to which file need to be copied
     *
     * @return
     */

    public static boolean copyResourceFromJarTo(String resourceName, String destny) {
	File targetFile = null;

	try {
	    LOGGER.info("Copying resources from " + resourceName);

	    InputStream is = FileUtils.class.getResourceAsStream(resourceName);

	    // System.out.println(FileUtils.class.getResource(resourceName).toURI().getPath());
	    LOGGER.info(new StringBuilder().append("Is input stream null ").append(is == null).toString());

	    if ((is == null) && (!(resourceName.startsWith("/")))) {

		is = FileUtils.class.getResourceAsStream(new StringBuilder().append("/").append(resourceName)
			.toString());
		LOGGER.info("\n\n"
			+ FileUtils.class.getResource(new StringBuilder().append("/").append(resourceName).toString())
				.getPath());
		LOGGER.info("\n\n"
			+ FileUtils.class.getResource(new StringBuilder().append("/").append(resourceName).toString())
				.getFile());
		LOGGER.info("Retried Stream value - " + (is == null));
	    }

	    byte[] buffer = new byte[is.available()];
	    is.read(buffer);
	    is.close();

	    targetFile = new File(destny);

	    OutputStream outStream = new FileOutputStream(targetFile);
	    outStream.write(buffer);
	    outStream.flush();
	    outStream.close();
	} catch (Exception e) {
	    LOGGER.info("Failed to copy the report template from the jar", e);
	}

	LOGGER.info(targetFile.getAbsolutePath());
	LOGGER.info("Read" + targetFile.canRead());
	LOGGER.info("Write" + targetFile.canWrite());
	LOGGER.info("Write" + targetFile.isFile());

	return targetFile.exists();
    }

}
