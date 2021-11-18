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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.exceptions.TestException;
import com.automatics.utils.AutomaticsPropertyUtility;

/**
 * Class to hold and get the details of an RDK DeviceConfig.
 * 
 * This class helps to identify the different class and model of devices. In case a new model came, there is no need to
 * modify, Need to add a property in stb.properties in following format
 * 
 * RDKDevices.[DeviceConfig]=[Manufacture]_[Model]_[DevicePrefix]_[DeviceClass] 
 * 
 * @author divya.rs
 * 
 */
public class RDKDevices {
    // List of RDK DeviceConfig details
    private static List<RdkDeviceDetails> rdkDevices = null;

    /** SLF4J logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RDKDevices.class);

    /**
     * Method to get the RDK device details read from the property file
     * 
     * @return
     */
    public synchronized static List<RdkDeviceDetails> getDevices() {
	if (null == rdkDevices) {
	    // Get all device info from stb.properties
	    List<String> devices = AutomaticsPropertyUtility.getPropsWithGivenPrefix("RDKDevices.");

	    // Populate the device details in the variable rdkDevices
	    populateDeviceModels(devices);
	}

	return rdkDevices;
    }

    /**
     * Helper method to populate the device details
     * 
     * @param devices
     */
    private static void populateDeviceModels(List<String> devices) throws TestException {
	if (null == rdkDevices) {
	    rdkDevices = new ArrayList<RDKDevices.RdkDeviceDetails>();
	    RdkDeviceDetails rdkDeviceDetails = null;
	    String deviceDetails = null;

	    String manufacture = null;
	    String model = null;
	    String devicePrefix = null;
	    String deviceClass = null;

	    for (String device : devices) {
		deviceDetails = AutomaticsPropertyUtility.getProperty(device);
		String[] split = deviceDetails.split(AutomaticsConstants.HYPHEN);
		// DeviceConfig details & DeviceConfig class are separated using "-". So if there is no 2 items after splitting
		// using "-", then there is no need to proceed
		if (null != split && split.length > 1) {
		    deviceClass = split[1];
		    String[] split2 = split[0].split(String.valueOf(AutomaticsConstants.UNDERSCORE));
		    // The device details like manufacture, model & device prefix are separated by "_"
		    if (null != split2 && split2.length > 2) {
			manufacture = split2[0];
			model = split2[1];
			devicePrefix = split2[2];
		    } else {
			throw new TestException(
				"Failed to parse the device details. The device details and device class are not separated by '_': "
					+ device);
		    }
		} else {
		    throw new TestException(
			    "Failed to parse the device details. The device details and device class are not separated by '-': "
				    + device);
		}
		LOGGER.debug("deviceClass>>>>>>>>>>>>>>>" + deviceClass);
		rdkDeviceDetails = new RdkDeviceDetails(manufacture, model, devicePrefix, deviceClass);
		rdkDevices.add(rdkDeviceDetails);
	    }
	}

    }

    /**
     * Utility method to get the devices Models corresponding to a given device class
     * 
     * @param deviceClass
     *            The required device class
     * @return The list of device models.
     */
    public static List<String> getDeviceModelsByGivenClass(String deviceClass) {
	List<RdkDeviceDetails> devices = getDevices();
	List<String> reqdDevices = new ArrayList<String>();
	for (RdkDeviceDetails device : devices) {
	    if (device.getDeviceClass().equalsIgnoreCase(deviceClass) || device.getDeviceClass().contains(deviceClass)) {
		// LOGGER.debug("Adding device model.............{}",device.getModel());
		reqdDevices.add(device.getModel());
	    }
	}
	LOGGER.debug("Required models {}", reqdDevices);
	return reqdDevices;
    }

    /**
     * Utility method to get the devices Prefixes corresponding to a given device class
     * 
     * @param deviceClass
     *            The required device class
     * @return The list of device models.
     */
    public static List<String> getDevicePrefixesByGivenClass(String deviceClass) {
	List<RdkDeviceDetails> devices = getDevices();
	List<String> reqdDevices = new ArrayList<String>();
	for (RdkDeviceDetails device : devices) {
	    if (device.getDeviceClass().equalsIgnoreCase(deviceClass) || device.getDeviceClass().contains(deviceClass)) {
		// LOGGER.debug("Adding device prefix.............{}",device.getModel());
		reqdDevices.add(device.devicePrefix);
	    }
	}
	LOGGER.debug("DeviceConfig added : ", reqdDevices);
	return reqdDevices;
    }

    /**
     * Class to hold the device details
     * 
     * @author divya.rs
     *
     */
    public static class RdkDeviceDetails {

	String manufacture;
	String model;
	String devicePrefix;
	String deviceClass;

	RdkDeviceDetails(String manufacture, String model, String devicePrefix, String deviceClass) {
	    this.manufacture = manufacture;
	    this.model = model;
	    this.devicePrefix = devicePrefix;
	    this.deviceClass = deviceClass;
	}

	public String getManufacture() {
	    return manufacture;
	}

	public String getModel() {
	    return model;
	}

	public String getDevicePrefix() {
	    return devicePrefix;
	}

	public String getDevicePrefix(Dut dut) {
	    return devicePrefix;
	}

	/**
	 * @return the deviceClass
	 */
	public String getDeviceClass() {
	    return deviceClass;
	}

	/**
	 * @param deviceClass
	 *            the deviceClass to set
	 */
	public void setDeviceClass(String deviceClass) {
	    this.deviceClass = deviceClass;
	}

    }

}
