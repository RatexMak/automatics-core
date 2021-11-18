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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.device.Dut;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.NonRackUtils;

public abstract class AbstractVideoProvider extends AbstractBaseProvider implements VideoProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVideoProvider.class);
    
    public AbstractVideoProvider() {
	super();
    }

    public AbstractVideoProvider(Dut device) {
	super(device);

    }

    public String getImageSaveLocation() {
	String imageLoc = null;
	if (NonRackUtils.isNonRack()) {
	    imageLoc = System.getProperty(ReportsConstants.USR_DIR)
		    + AutomaticsConstants.PATH_SEPARATOR
		    + AutomaticsConstants.TARGET_FOLDER
		    + AutomaticsConstants.PATH_SEPARATOR
		    + "images"
		    + AutomaticsConstants.PATH_SEPARATOR
		    + AutomaticsUtils.getCleanMac(this.getDevice().getHostMacAddress()
			    + AutomaticsConstants.PATH_SEPARATOR + "images");
	    LOGGER.info("Obtained jenkins location for image save :" + imageLoc);
	} else {
	    imageLoc = "";
	}
	return imageLoc;
    }
    
}
