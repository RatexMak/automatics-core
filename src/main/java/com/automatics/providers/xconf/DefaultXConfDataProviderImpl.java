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

package com.automatics.providers.xconf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.XconfConstants;
import com.automatics.device.Dut;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.CommonMethods;

/**
 * 
 * Default implementation for XConf Data Provider
 *
 */
public class DefaultXConfDataProviderImpl implements XConfDataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXConfDataProviderImpl.class);

    /**
     * Gets the device mac address
     */
    @Override
    public String getDeviceMacAddress(Dut dut) {

	String macAddress = null;
	if (null != dut) {
	    macAddress = dut.getHostMacAddress();
	}
	LOGGER.info("DefaultXconfDataProvider mac address: {}", macAddress);
	return macAddress;
    }

    /**
     * Gets firmware file name
     */
    @Override
    public String getFirmwareFileName(Dut dut, String firmwareName) {
	LOGGER.info("DefaultXconfDataProvider firmwareFileName: {}", firmwareName);
	return firmwareName;
    }

    /**
     * Gets the firmware name
     */
    @Override
    public String getFirmwareName(Dut dut, String firmwareName) {
	LOGGER.info("DefaultXconfDataProvider firmwareName: {}", firmwareName);
	return firmwareName;
    }

    /**
     * Gets the location where firmwares are available
     */
    @Override
    public String getFirmwareLocation(Dut dut, String firmwareName, String protocol) {
	// Get the location where images are available.
	String frimwareLocation = AutomaticsTapApi.getSTBPropsValue(XconfConstants.PROP_KEY_XCONF_FIRMWARE_LOCATION);
	if (CommonMethods.isNull(frimwareLocation)) {
	    LOGGER.error("Error property " + XconfConstants.PROP_KEY_XCONF_FIRMWARE_LOCATION
		    + " not configured in Automatics Properties which is required for XConf image upgrade.");
	}
	return frimwareLocation;
    }

}
