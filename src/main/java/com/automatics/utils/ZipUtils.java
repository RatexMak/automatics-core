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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZipUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);

    private ZipUtils() {
	// Empty private constructor
    }

    public static void unzipResource(String resourceName, String destination) {
	LOGGER.debug("Extracting HTML report resources");
	InputStream is = ZipUtils.class.getResourceAsStream(resourceName);
	if (is == null && !resourceName.startsWith("/")) {
	    is = ZipUtils.class.getResourceAsStream("/" + resourceName);
	}
	ZipInputStream zis = new ZipInputStream(is);
	ZipEntry zipEntry;
	byte[] buf = new byte[1024];

	try {
	    while ((zipEntry = zis.getNextEntry()) != null) {
		// for each entry to be extracted
		String copyEntry = destination + zipEntry.getName();
		copyEntry = copyEntry.replace('/', File.separatorChar);
		copyEntry = copyEntry.replace('\\', File.separatorChar);
		LOGGER.trace("Extracting HTML report item " + copyEntry);
		int n;
		FileOutputStream fileoutputstream;
		File newFile = new File(copyEntry);
		if (zipEntry.isDirectory()) {
		    if (!newFile.exists() && !newFile.mkdirs()) {
			break;
		    }
		    continue;
		}

		fileoutputstream = new FileOutputStream(copyEntry);

		while ((n = zis.read(buf, 0, 1024)) > -1) {
		    fileoutputstream.write(buf, 0, n);
		}

		fileoutputstream.close();
		zis.closeEntry();
	    }

	} catch (IOException e) {
	    LOGGER.error("IOException while extracting HTML report resources", e);
	} finally {
	    try {
		zis.close();
	    } catch (IOException e) {
		LOGGER.error("IOException while closing HTML report resources", e);
	    }
	}

    }

    public static void main(String[] args) {
	String resourceName = "htmlreport.zip";
	String destination = "E:/unzipTest/";
	unzipResource(resourceName, destination);
    }

}
