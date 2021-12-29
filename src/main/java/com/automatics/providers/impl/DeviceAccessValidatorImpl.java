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

package com.automatics.providers.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.device.config.DeviceConfig;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.SshMechanism;
import com.automatics.providers.DeviceAccessValidator;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.DeviceConfigUtils;

/**
 * 
 * Default implementation of device access validator. If required partner can override this class in
 * partner-applicationContext.xml by mapping the custom implementation class against bean name 'deviceAccessValidator'.
 *
 */
public class DeviceAccessValidatorImpl implements DeviceAccessValidator {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAccessValidatorImpl.class);

    /**
     * Verify if accessibility check of device is required or not by parsing device_config.json
     */
    @Override
    public boolean isAccessibilityCheckRequired(DeviceCategory deviceCategory, Dut device) {
	boolean checkAccessibilityRequired = false;
	Device dut = (Device) device;
	DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceConfigByRackOrAutomaticsModel(device.getModel());
	if (null != deviceConfig) {
	    checkAccessibilityRequired = deviceConfig.isAccessbilityCheck();
	    LOGGER.info("Setting accessibility check required to {} for device {} from device config",
		    checkAccessibilityRequired, dut.getAccessMechanism());
	}

	LOGGER.info("Accessibility check required for device {} {} ", dut.getAccessMechanism(),
		checkAccessibilityRequired);
	return checkAccessibilityRequired;
    }

    /**
     * Sets access mechanism for device by parsing device_config.json
     */
    @Override
    public void setAccessMechanismForDevice(DeviceCategory deviceCategory, Dut device) {
	Device dut = (Device) device;

	// Read access mechanism from device_config.json
	DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceConfigByRackOrAutomaticsModel(device.getModel());
	if (null != deviceConfig) {
	    String accesMechanism = deviceConfig.getAccessibleMechanism();
	    dut.setAccessMechanism(accesMechanism);
	    LOGGER.info("Setting access mechanism for device {} {}", dut.getAccessMechanism(), accesMechanism);
	} else {
	    LOGGER.info("Setting default SSH access mechanism for device {}", dut.getAccessMechanism());
	    dut.setAccessMechanism(SshMechanism.SSH.name());
	}

    }

    /**
     * Verify if device is accessible. Return true if device is accessible.
     */
    @Override
    public boolean isDeviceAccessible(DeviceCategory deviceCategory, Dut device) {
	return isDeviceAccessible(device);

    }

    /**
     * Verify if device is accessible. Return true if device is accessible.
     */
    @Override
    public boolean isDeviceAccessible(Dut device) {
	boolean deviceAccessible = false;
	DeviceConnectionProvider deviceConnectionProvider = BeanUtils.getDeviceConnetionProvider();
	if (null != deviceConnectionProvider) {
	    String response = deviceConnectionProvider.execute((Device) device, "echo Test Connection");
	    LOGGER.info("Response of accessibility check: {}", response);
	    if (CommonMethods.isNotNull(response) && response.contains("Test Connection")) {
		deviceAccessible = true;
	    }

	} else {
	    LOGGER.error("DeviceConnectionProvider is not configured. So could not check device accessibility.");
	}
	return deviceAccessible;
    }

    /**
     * Verify if device is accessible after reboot.
     */
    @Override
    public boolean waitForIpAcquisitionAfterReboot(Dut device) {
	return waitForDeviceIpAcquisition(device);
    }

    /**
     * Wait for the device ip acquisition after device reboot.
     * 
     * @param dut
     *            Device
     * @return Return true if device is accessible with existing ip after reboot
     */
    private boolean waitForDeviceIpAcquisition(Dut dut) {
	// Status of the IP Acquisition
	boolean status = false;
	long totalTime = System.currentTimeMillis();
	int currentValue = 0;
	try {
	    boolean isDeviceAccessible = false;
	    AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();

	    // Get the wait time after device hard reboot
	    DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceObj(dut.getModel());
	    long maxWaitTimeInMilliSec = 240000;
	    if (null != deviceConfig && deviceConfig.getWaitTimeAfterHardReboot() > 0) {
		LOGGER.info("Reboot wait time: {}", deviceConfig.getWaitTimeAfterHardReboot());
		maxWaitTimeInMilliSec = deviceConfig.getWaitTimeAfterHardReboot();
	    }

	    LOGGER.info(maxWaitTimeInMilliSec + " - Maximum waiting time to recover IP as per configuration : "
		    + (maxWaitTimeInMilliSec / (1000 * 60)) + " minutes");
	    // Add a buffer time/safety margin of 100-150% to process
	    maxWaitTimeInMilliSec = maxWaitTimeInMilliSec + (maxWaitTimeInMilliSec / 2);
	    long maxWaitTimeInSeconds = maxWaitTimeInMilliSec / 1000;

	    LOGGER.info("Maximum waiting time to recover IP after adding buffer time : "
		    + (maxWaitTimeInMilliSec / (1000 * 60)) + " minutes");

	    // 1. First Delay - For the device to be up. Wait for 1/3 of total delay without any checking.
	    long initialDelay = maxWaitTimeInMilliSec / 3;
	    LOGGER.info("Waiting for :" + (initialDelay / (1000 * 60)) + "minutes, initial wait time");
	    tapEnv.waitTill(initialDelay);

	    // 2. Second Delay - For some time and verify if device is accessible with existing ip
	    LOGGER.info("Wait for another " + (maxWaitTimeInSeconds / 3)
		    + "seconds to verify IP acquisition using existing IP ");

	    while (currentValue < maxWaitTimeInSeconds / 3) {
		LOGGER.info("Current Value - " + currentValue + " Max Value : " + (maxWaitTimeInSeconds / 3));
		long timeTaken = System.currentTimeMillis();
		isDeviceAccessible = this.isDeviceAccessible(dut);
		if (isDeviceAccessible) {
		    // verified that device is accessible successfully
		    LOGGER.info("Device is accessible using IP : IPv4:" + dut.getHostIp4Address() + " IPv6: "
			    + dut.getHostIp6Address() + " for Build : " + dut.getFirmwareVersion() + " Mac Address : "
			    + dut.getHostMacAddress());
		    status = true;
		    break;
		} else {
		    // wait for 30 seconds and then check if device is accessible
		    LOGGER.info("Waiting for 30 seconds to check if device is accessible");
		    tapEnv.waitTill(AutomaticsConstants.THIRTY_SECONDS);
		    timeTaken = (System.currentTimeMillis() - timeTaken) / 1000;
		    // increment time taken for checking whether device is accessible using
		    currentValue += timeTaken;

		    LOGGER.info("Time taken to complete one round verificaiton using existing IP : " + timeTaken
			    + " currentValue : " + currentValue);
		}
	    }

	} catch (Exception e) {
	    LOGGER.error("Exception in waitForEstbIpAcquisition() : ", e);
	}

	LOGGER.info("Completed execution of waitForEstbIpAcquisition() : Status : " + status + ". Time taken : "
		+ ((System.currentTimeMillis() - totalTime) / 60000) + " minutes");
	return status;
    }

}
