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

public abstract class AbstractImageCompareProvider extends AbstractBaseProvider implements ImageCompareProvider {

    private String closedCaptionImageDirectory;

    private String imageLocationDirectory;

    public AbstractImageCompareProvider() {
	super();
    }

    public AbstractImageCompareProvider(Dut device) {
	super(device);
	// This image location will be overwritten during runtime to save images in test specific directories
	closedCaptionImageDirectory = System.getProperty(ReportsConstants.USR_DIR)
		+ AutomaticsConstants.PATH_SEPARATOR + AutomaticsConstants.TARGET_FOLDER
		+ AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR)
		+ AVConstants.CLOSEDCAPTION_IMAGE_FOLDER;
	
	imageLocationDirectory = System.getProperty(ReportsConstants.USR_DIR)
		+ AutomaticsConstants.PATH_SEPARATOR + AutomaticsConstants.TARGET_FOLDER
		+ AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR)
		+ AVConstants.IMAGE_COMPARE_FOLDER;

    }

    public void setClosedCaptionImageSaveLocation(String directoryPath) {
	closedCaptionImageDirectory = directoryPath;
    }

    public String getClosedCaptionImageSaveLocation() {
	return closedCaptionImageDirectory;
    }

    public void setImageSaveLocation(String directoryPath) {
	imageLocationDirectory = directoryPath;
    }

    public String getImageSaveLocation() {
	return imageLocationDirectory;
    }

}
