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
package com.automatics.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.device.config.DeviceConfig;
import com.automatics.device.config.DeviceConfigModelUtils;
import com.automatics.device.config.DeviceModels;
import com.automatics.enums.DeviceCategory;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.DeviceConfigUtils;
import com.automatics.utils.AutomaticsPropertyUtility;

/**
 * This class hold all model related operations and other config data associated with stb properties. Any model related
 * operation has to be strictly placed in this class, making use of available methods.
 * 
 * @author rohinic
 *
 */
public class SupportedModelHandler {

    static final Logger LOGGER = LoggerFactory.getLogger(SupportedModelHandler.class);

    /**
     * The variables are intentionally given private so that , all methods that access these variables such as methods
     * to identify device type or model, are placed strictly inside this class.Thus the class becomes a single point
     * were model related operations can be contained
     * 
     */

    private static String frameworkSupportedModels = null;

    private static String rdkbModels = null;

    private static String rdkvGWModels = null;

    private static String rdkvCLModels = null;

    private static String rdkcModels = null;

    private static String ecbModels = null;

    private static String nucModels = null;

    // Properties configured in stb.properties
    private static final String STB_PROPS_MODEL_MAPPING = "name.model.device.mapping.";

    public static void initializeSupportedModelInformation() {
	StringBuilder supportedModels = new StringBuilder();
	StringBuilder rdkvGWModelsBuilder = new StringBuilder();
	StringBuilder rdkvClientModelsBuilder = new StringBuilder();
	StringBuilder rdkbModelsBuilder = new StringBuilder();
	StringBuilder rdkcModelsBuilder = new StringBuilder();
	StringBuilder ecbModelsBuilder = new StringBuilder();
	StringBuilder nucModelsBuilder = new StringBuilder();
	DeviceModels deviceModels = DeviceConfigModelUtils.getInstance().fetchDeviceModels();
	if (deviceModels != null && deviceModels.getDeviceModels() != null) {
	    for (DeviceConfig deviceConfig : deviceModels.getDeviceModels()) {
		supportedModels.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		if (DeviceCategory.RDKV_GATEWAY.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    rdkvGWModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		} else if (DeviceCategory.RDKV_CLIENT.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    rdkvClientModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		} else if (DeviceCategory.RDKB.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    rdkbModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		} else if (DeviceCategory.RDKC.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    rdkcModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		} else if (DeviceCategory.ECB.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    ecbModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		} else if (DeviceCategory.NUC.name().equalsIgnoreCase(deviceConfig.getCategory())) {
		    nucModelsBuilder.append(deviceConfig.getAutomaticsModelName()).append(AutomaticsConstants.COMMA);
		}

	    }
	}

	frameworkSupportedModels = supportedModels.toString();
	rdkvGWModels = rdkvGWModelsBuilder.toString();
	rdkvCLModels = rdkvClientModelsBuilder.toString();
	rdkcModels = rdkcModelsBuilder.toString();
	rdkbModels = rdkbModelsBuilder.toString();
	ecbModels = ecbModelsBuilder.toString();
	nucModels = nucModelsBuilder.toString();
	LOGGER.info("frameworkSupportedModels=" + frameworkSupportedModels);
	LOGGER.info("rdkvGWModels=" + rdkvGWModels);
	LOGGER.info("rdkvCLModels=" + rdkvCLModels);
	LOGGER.info("rdkbModels=" + rdkbModels);
	LOGGER.info("rdkcModels=" + rdkcModels);

	LOGGER.debug("<=================================>");
	LOGGER.debug("Framework supported models=" + frameworkSupportedModels);
	LOGGER.debug("<=================================>");
    }

    public static void main(String[] args) {
	AutomaticsPropertyUtility.loadProperties();
	initializeSupportedModelInformation();
    }

    /**
     * Method to check if device is of type RDKV GW
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isRDKVGateway(Dut dut) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(rdkvGWModels) && rdkvGWModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type RDKV CL
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isRDKVClient(Dut dut) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(rdkvCLModels) && rdkvCLModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type RDKB
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isRDKB(Dut dut) {
	boolean isValid = false;	
	if (CommonMethods.isNotNull(rdkbModels) && rdkbModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type RDKC
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isRDKC(Dut dut) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(rdkcModels) && rdkcModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type ECB
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isECB(Dut dut) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(ecbModels) && ecbModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type NUC
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isNUC(Dut dut) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(nucModels) && nucModels.contains(dut.getModel())) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check if device is of type RDKV
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isRDKV(Dut dut) {
	return (isRDKVClient(dut) || isRDKVGateway(dut));
    }

    /**
     * Method to check if device is of type non-RDK
     * 
     * @param dut
     *            Dut Object
     * @return True - > If device is of required type . False - > If device is of different type
     */
    public static boolean isNonRDKDevice(Dut dut) {
	boolean isValid = false;
	if (!isRDKB(dut) && !isRDKC(dut) && !isRDKV(dut)) {
	    isValid = true;
	    LOGGER.info(" DeviceConfig [" + dut.getHostMacAddress() + "] is identified as NON RDK DeviceConfig.");
	}
	return isValid;
    }

    /**
     * 
     * The below methods maps rack models to Automation core supported model values. This is done during initialization.
     * The property is set in dut Descriptor.This descriptor reference gets set in Dut object.
     * 
     * @param settopdesc
     * @return
     */
    public static Dut mapModel(Dut device) {

	if (!CommonMethods.isNull(device.getModel())) {

	    String modelFromRack = (device.getModel()).trim();
	    com.automatics.device.config.DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceConfigByRackModel(modelFromRack);
	    if (null == deviceConfig) {
		LOGGER.info(">>>[INIT]: No device config mapped for rack model {}. Proceeding with rack model",
			modelFromRack);
	    } else {
		LOGGER.info(">>>[INIT]: Found device config mapped for rack model {}", modelFromRack);
		LOGGER.info(">>>[INIT]: Mapping rack model {} to  automatics model {}", modelFromRack,
			deviceConfig.getAutomaticsModelName());
		device.setModel(deviceConfig.getAutomaticsModelName());
	    }
	} else {
	    LOGGER.error(">>>[INIT]: DeviceConfig model provided by rack is null");
	}

	return device;
    }

    /**
     * 
     * The below methods maps  rack models to Automation core supported model values. This is done during initialization.
     * The property is set in dut Descriptor.This descriptor reference gets set in Dut object.
     * 
     * @param settopdesc
     * @return
     */
    public static String mapToAutomaticsModelName(String rackModelName, String deviceMacAddress) {
	
	String automaticsCoreMappedModel = null;
	if (!CommonMethods.isNull(rackModelName)) {

	    String modelFromRack = rackModelName.trim();
	    List<String> frameworkSupportedModelList = Arrays.asList(frameworkSupportedModels
		    .split(AutomaticsConstants.COMMA));

	    LOGGER.info("Mapping current model " + modelFromRack + " to automatics core for " + deviceMacAddress);
	    for (String frameoworkModel : frameworkSupportedModelList) {
		String mapping = AutomaticsPropertyUtility.getProperty(STB_PROPS_MODEL_MAPPING + frameoworkModel);
		if (mapping.contains(modelFromRack)) {
		    automaticsCoreMappedModel = frameoworkModel;
		    break;
		}
	    }
	    if (CommonMethods.isNull(automaticsCoreMappedModel)) {
		LOGGER.debug("No mapping available for this device " + deviceMacAddress + " with model "
			+ modelFromRack);
		LOGGER.debug("Proceeding with RACK model");
		automaticsCoreMappedModel = modelFromRack;
	    } else {
		LOGGER.info("Mapped model for " + deviceMacAddress + " is :" + automaticsCoreMappedModel);
	    }
	}
	return automaticsCoreMappedModel;
    }

    public static boolean isRDKB(String model) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(rdkbModels) && rdkbModels.contains(model)) {
	    isValid = true;
	}
	return isValid;
    }

    /**
     * Method to check whether given dut is Wifi cable The check is purely model value based
     * 
     * @param dut
     *            instance of Dut
     * @return true if device model is wifi cable
     */
    public static boolean isWifiDeviceBasedOnModel(final Dut dut) {
	boolean isValid = false;
	DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceObj(dut.getModel());
	if (null != deviceConfig && deviceConfig.getGroups() != null) {
	    List<String> groups = new ArrayList<>(Arrays.asList(deviceConfig.getGroups()));
	    if (groups.contains("WIFI")) {
		isValid = true;
	    }
	}
	return isValid;
    }

    public static boolean isDeviceModelNameInGroup(final Dut dut, String groupName) {
	boolean isPresent = false;
	DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceObj(dut.getModel());
	if (null != deviceConfig && deviceConfig.getGroups() != null) {
	    List<String> groups = new ArrayList<>(Arrays.asList(deviceConfig.getGroups()));
	    if (groups.contains(groupName)) {
		isPresent = true;
	    }
	}
	return isPresent;
    }  

}
