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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.DeviceCategory;

public class ExecutionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionUtils.class);

    /**
     * Decides if execution mode is home automtion.
     * 
     * @return
     */
    public static final boolean isHomeAutomationMode() {
	return new Boolean(System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
    }

    /**
     * Decides if client gateway is to be fetched based on config data
     * 
     * @return
     */
    public static final boolean isDeviceModelNeedsClientGateway(String automaticsModelName) {
	// TODO support reading from json file
	boolean clientGatewayNeeded = true;
	LOGGER.info("Client gateway needed", clientGatewayNeeded);
	return clientGatewayNeeded;

    }

    /**
     * Decides if device accessibility check needed based on config data
     * 
     * @return
     */
    public static final boolean isAccessibilityCheckNeeded(String automaticsModelName) {
	// TODO support reading from json file
	boolean accessibilityCheckNeeded = true;
	LOGGER.info("DeviceConfig accessibility check needed", accessibilityCheckNeeded);
	return accessibilityCheckNeeded;

    }

    /**
     * Decides if device is client device.
     * 
     * @return
     */
    public static final boolean isClientDevice(String automaticsModelName) {
	// TODO support reading from json file
	boolean isClientDevice = true;
	LOGGER.info("Is client device", isClientDevice);
	return isClientDevice;

    }

}
