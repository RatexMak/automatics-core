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
package com.automatics.providers.rack;

import com.automatics.constants.AVConstants;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.device.Dut;
import com.automatics.utils.AutomaticsUtils;

/**
 * Abstract class for OCR
 * 
 * @author Radhika
 *
 */
public abstract class AbstractOcrProvider extends AbstractBaseProvider implements OcrProvider {

    protected String imageLocationDirectory;

    public AbstractOcrProvider() {
	super();
	// This image location will be overwritten during runtime to save images in test specific directories
	setDefaultImageSaveLocation();

    }

    public AbstractOcrProvider(Dut device) {
	super(device);
	// This image location will be overwritten during runtime to save images in test specific directories
	setDefaultImageSaveLocation();
    }

    /**
     * Sets location for saving images.
     */
    @Override
    public void setImageSaveLocation(String directoryPath) {
	imageLocationDirectory = directoryPath;
    }

    /**
     * Gets the saved image location
     */
    @Override
    public String getImageSaveLocation() {
	return imageLocationDirectory;
    }

    /**
     * Sets the default image location
     */
    private void setDefaultImageSaveLocation() {
	if (null != device){
	imageLocationDirectory = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR)
		+ AVConstants.OCR_IMAGE_FOLDER;
	} else {
	    imageLocationDirectory = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
			+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR
			+ AutomaticsConstants.PATH_SEPARATOR
			+ AVConstants.OCR_IMAGE_FOLDER;
	}
    }
}
