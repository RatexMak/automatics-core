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

import com.automatics.device.config.DeviceConfig;
import com.automatics.device.config.DeviceConfigModelUtils;
import com.automatics.device.config.DeviceModels;
import com.automatics.enums.DeviceCategory;

/**
 * Utils class for device config data
 * 
 * @author Radhika
 *
 */
public class DeviceConfigUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConfigUtils.class);

    /**
     * Get device config for given model
     * 
     * @param automaticsModelName
     * @return DeviceConfig
     */
    public static DeviceConfig getDeviceObj(String automaticsModelName) {

	DeviceConfig deviceModel = null;
	LOGGER.debug("Finding device object from config for model {}", automaticsModelName);
	DeviceModels deviceModels = DeviceConfigModelUtils.getInstance().fetchDeviceModels();
	if (deviceModels != null && deviceModels.getDeviceModels() != null) {
	    for (DeviceConfig deviceObj : deviceModels.getDeviceModels()) {
		if (deviceObj.getAutomaticsModelName().equals(automaticsModelName)) {
		    LOGGER.debug("Found matching device object from config for model {}", automaticsModelName);
		    deviceModel = deviceObj;
		    break;
		}
	    }
	}
	return deviceModel;
    }

    /**
     * Get device config for given rack model
     * 
     * @param rackModel
     * @return DeviceConfig
     */
    public static DeviceConfig getDeviceConfigByRackModel(String rackModel) {

	DeviceConfig deviceModel = null;
	LOGGER.debug("Finding device object from config for rack model {}", rackModel);
	DeviceModels deviceModels = DeviceConfigModelUtils.getInstance().fetchDeviceModels();
	if (deviceModels != null && deviceModels.getDeviceModels() != null) {
	    String[] modelNames = null;
	    for (DeviceConfig deviceObj : deviceModels.getDeviceModels()) {
		modelNames = deviceObj.getRackModelNames();
		if (null != modelNames) {
		    for (String modelName : modelNames) {
			if (modelName.equalsIgnoreCase(rackModel)) {
			    LOGGER.info("Found matching device object from config for rack model {}", rackModel);
			    deviceModel = deviceObj;
			    return deviceModel;
			}
		    }
		}
	    }
	}
	return deviceModel;
    }

    /**
     * Get device config for given rack model
     * 
     * @param rackModel
     * @return DeviceConfig
     */
    public static DeviceConfig getDeviceConfigByRackOrAutomaticsModel(String modelName) {

	DeviceConfig deviceConfig = getDeviceObj(modelName);
	if (null == deviceConfig) {
	    LOGGER.debug("Finding device object from config for rack model {}", modelName);
	    DeviceModels deviceModels = DeviceConfigModelUtils.getInstance().fetchDeviceModels();
	    if (deviceModels != null && deviceModels.getDeviceModels() != null) {
		String[] modelNames = null;
		for (DeviceConfig deviceObj : deviceModels.getDeviceModels()) {
		    modelNames = deviceObj.getRackModelNames();
		    if (null != modelNames) {
			for (String model : modelNames) {
			    if (model.equalsIgnoreCase(modelName)) {
				LOGGER.info("Found matching device object from config for rack model {}", modelName);
				deviceConfig = deviceObj;
				return deviceConfig;
			    }
			}
		    }
		}
	    }
	}
	return deviceConfig;
    }

    public static boolean isDeviceBelongsToGivenGroup(String deviceGroup, String automaticsModelName) {
	return true;
    }

    /**
     * Get device object for the given model
     * 
     * @param modelName
     * @return
     */
    public static String getDeviceCategory(String modelName) {
	DeviceConfig deviceObj = getDeviceObj(modelName);
	if (null != deviceObj) {
	    return deviceObj.getCategory();
	}
	return null;
    }

    public static boolean isAccessibilityCheckNeeded(String automaticsModelName) {
	return getDeviceObj(automaticsModelName).isAccessbilityCheck();
    }

    public static boolean isClientDevice(String automaticsModelName) {
	return isRDKVClientDevice(automaticsModelName);
    }

    /**
     * Verifies if the device is RDKV client device
     * 
     * @param modelName
     *            device model
     * @return true if device is RDKV client otherwise false.
     */
    public static boolean isRDKVClientDevice(String modelName) {
	boolean isClient = false;
	DeviceConfig deviceConfig = getDeviceObj(modelName);
	if (null != deviceConfig && DeviceCategory.RDKV_CLIENT.name().equalsIgnoreCase(deviceConfig.getCategory())) {
	    isClient = true;
	}
	return isClient;
    }
}
