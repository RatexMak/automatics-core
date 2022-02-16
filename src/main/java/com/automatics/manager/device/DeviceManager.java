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
package com.automatics.manager.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.ConnectedDeviceInfo;
import com.automatics.device.Device;
import com.automatics.device.DeviceAccountInfo;
import com.automatics.device.Dut;
import com.automatics.providers.objects.DeviceAccountRequest;
import com.automatics.providers.objects.DeviceAccountResponse;
import com.automatics.providers.objects.DeviceAllocationResponse;
import com.automatics.providers.objects.DeviceObject;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.providers.objects.DeviceRequest;
import com.automatics.providers.objects.DeviceResponse;
import com.automatics.providers.objects.DeviceUpdateDurationRequest;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.objects.enums.DeviceAllocationStatus;
import com.automatics.providers.objects.enums.StatusMessage;
import com.automatics.providers.rack.DeviceProvider;
import com.automatics.utils.BeanConstants;
import com.automatics.utils.BeanUtils;

/**
 * DeviceConfig Manager. Entry point class for all device related APIs
 * 
 * @author Radhika
 *
 */
public class DeviceManager {
    private DeviceProvider deviceProvider = null;
    private static DeviceManager deviceManager = null;
    private static Object lock = new Object();
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManager.class);

    private DeviceManager() {
	deviceProvider = (DeviceProvider) BeanUtils.getPartnerProviderImpl(BeanConstants.PROP_KEY_DEVICE_MANAGER,
		BeanConstants.BEAN_ID_DEVICE_PROVIDER, DeviceProvider.class);
    }

    /**
     * Gets device manager instance
     * 
     * @return device manager instance
     */
    public static DeviceManager getInstance() {
	if (null == deviceManager) {
	    synchronized (lock) {
		if (null == deviceManager) {
		    deviceManager = new DeviceManager();
		}
	    }
	}
	return deviceManager;
    }

    /**
     * Get device
     * 
     * @param request
     * @return
     */
    public Device findRackDevice(String mac) {
	Device device = null;
	DeviceRequest request = new DeviceRequest();
	request.setMac(mac);
	LOGGER.info("INIT-{} Get device details", mac);
	DeviceResponse deviceResponse = deviceProvider.getDevice(request);
	if (null != deviceResponse && null != deviceResponse.getDevices() && !deviceResponse.getDevices().isEmpty()) {
	    device = convertDeviceResponseToDevice(deviceResponse.getDevices().get(0));
	    LOGGER.info("INIT-{} Obtained device details", mac);
	} else {
	    LOGGER.error("INIT-{} No device details obtained", mac);
	}
	return device;
    }

    public Map<String, String> getDeviceProperties(DevicePropsRequest request) {
	Map<String, String> devicePropsResponse = null;
	if (null != request) {
	    devicePropsResponse = deviceProvider.getDeviceProperties(request);
	}

	return devicePropsResponse;
    }

    public DeviceAccountInfo getAccountDetailsForDevice(String deviceAccountNum) {
	DeviceAccountInfo deviceAccount = null;
	DeviceAccountRequest request = new DeviceAccountRequest();
	request.setAccountNumber(deviceAccountNum);

	DeviceAccountResponse response = deviceProvider.getAccountDetailsForDevice(request);
	if (null != response) {
	    deviceAccount = convertAccountDetailsToDeviceAccount(response);
	}
	return deviceAccount;
    }

    public void stopTraceLoggingByRack(Dut device) {
    }

    public void startTraceLoggingByRack(Dut device) {
    }

    public boolean isLocked(Dut device) {
	boolean isLocked = true;
	DeviceRequest request = new DeviceRequest();
	request.setMac(device.getHostMacAddress());

	DeviceAllocationResponse allocResponse = deviceProvider.isLocked(request);

	if (null != allocResponse) {
	    if (allocResponse.getAllocationStatus() == DeviceAllocationStatus.AVAILABLE) {
		isLocked = false;
	    } else {
		LOGGER.info("DeviceConfig allocation details:  Mac Address-{} Locked by-{} Duration start-{} end-{}",
			device.getHostMacAddress(), allocResponse.getUserName(), allocResponse.getStart(),
			allocResponse.getEnd());
	    }
	}
	return isLocked;
    }

    public boolean lock(Dut device) {
	boolean lockSuccess = false;
	DeviceRequest request = new DeviceRequest();
	request.setMac(device.getHostMacAddress());
	LOGGER.info("INIT-{} Locking device", device.getHostMacAddress());
	StatusResponse statusResponse = deviceProvider.lock(request);
	if (null != statusResponse) {
	    if (StatusMessage.SUCCESS == statusResponse.getStatus()) {
		lockSuccess = true;
		LOGGER.info("INIT-{} Lock success", device.getHostMacAddress());
	    } else {
		LOGGER.info("INIT-{} Lock failed - {}", device.getHostMacAddress(), statusResponse.getErrorMsg());
	    }
	}
	return lockSuccess;
    }

    public void updateLockTime(Dut device, int durationInMins) {
	StatusResponse statusResponse = null;

	DeviceUpdateDurationRequest request = new DeviceUpdateDurationRequest();
	request.setMac(device.getHostMacAddress());
	request.setLockDurationInMins(durationInMins);

	statusResponse = deviceProvider.updateLockTime(request);
	if (null != statusResponse) {
	    if (statusResponse.getStatus() == StatusMessage.SUCCESS) {
		LOGGER.info("Successfully extended lock for device {}", device.getHostMacAddress());
	    } else {
		LOGGER.info("Failed to extend lock for device {}", device.getHostMacAddress(),
			statusResponse.getErrorMsg());
	    }
	}
    }

    public boolean release(Dut device) {
	boolean releaseSuccess = false;
	DeviceRequest request = new DeviceRequest();
	request.setMac(device.getHostMacAddress());
	StatusResponse statusResponse = deviceProvider.release(request);
	if (null != statusResponse) {
	    if (statusResponse.getStatus() == StatusMessage.SUCCESS) {
		releaseSuccess = true;
	    } else {
		LOGGER.info("Failed to release device {} {}", device.getHostMacAddress(), statusResponse.getErrorMsg());
	    }

	}
	return releaseSuccess;
    }

    /**
     * Converts device response to Device object.
     * 
     * @param deviceResponse
     *            DeviceResponse received from rest
     * @return Device instance
     */
    private Device convertDeviceResponseToDevice(DeviceObject deviceResponse) {
	Device device = new Device();
	device.setId(deviceResponse.getId());
	device.setName(deviceResponse.getName());
	device.setHardwareRevision(deviceResponse.getHardwareRevision());

	device.setHostMacAddress(deviceResponse.getHostMacAddress());
	device.setHostIp4Address(deviceResponse.getHostIp4Address());
	device.setHostIp6Address(deviceResponse.getHostIp6Address());
	device.setHostIpAddress(deviceResponse.getHostIp4Address());
	device.setClientIpAddress(deviceResponse.getClientIpAddress());
	device.setModel(deviceResponse.getModel());
	device.setManufacturer(deviceResponse.getManufacturer());
	device.setSerialNumber(deviceResponse.getSerialNumber());
	device.setRemoteType(deviceResponse.getRemoteType());
	device.setEcmIpAddress(deviceResponse.getEcmIpAddress());
	device.setErouterIpAddress(deviceResponse.getEstbIpAdress());
	// Gateway mac address separated by comma
	device.setGateWaySettopMacAddress(deviceResponse.getGatewayMac());
	device.setRackName(deviceResponse.getRackName());
	device.setSlotName(deviceResponse.getSlotName());
	device.setSlotNumber(deviceResponse.getSlotNumber());
	device.setRackServerHost(deviceResponse.getRackServerHost());
	device.setRackServerPort(deviceResponse.getRackServerPort());
	device.setHomeAccountGroupName(deviceResponse.getHomeAccountGroupName());
	device.setHomeAccountName(deviceResponse.getHomeAccountName());
	device.setHomeAccountNumber(deviceResponse.getHomeAccountNumber());
	device.setConnectedGateWaySettopMacs(deviceResponse.getGatewayMac());

	device.setDefaultRemoteControlType(deviceResponse.getDefaultRemoteControlType());
	device.setRemoteControlTypes(deviceResponse.getRemoteControlTypes());
	// Set device head end
	device.setHeadEnd(deviceResponse.getHeadend());

	device.setExtraProperties(deviceResponse.getExtraProperties());
	Map<String, String> extraProps = device.getExtraProperties();
	if (null != extraProps) {

	    // Setting extra props from partner
	    device.setExtraProperties(extraProps);

	    // Setting login credentials for non-rdk devices
	    device.setUsername(extraProps.get("username"));
	    device.setPassword(extraProps.get("password"));

	    // Get connected device info
	    ConnectedDeviceInfo connectedDeviceInfo = new ConnectedDeviceInfo();
	    connectedDeviceInfo.setConnectionType(extraProps.get("connectionType"));
	    connectedDeviceInfo.setWifiCapability(extraProps.get("wifiCapability"));
	    connectedDeviceInfo.setWifiMacAddress(extraProps.get("wifiMacAddress"));
	    connectedDeviceInfo.setDevicePortAddress(extraProps.get("devicePort"));
	    connectedDeviceInfo.setDeviceIpAddress(extraProps.get("deviceIp"));
	    connectedDeviceInfo.setOsType(extraProps.get("osType"));
	    device.setNodePort(extraProps.get("nodePort"));
	    device.setNatAddress(extraProps.get("deviceIp"));
	    device.setNatPort(extraProps.get("devicePort"));
	    device.setOsType(extraProps.get("osType"));
	    connectedDeviceInfo.setUserName(extraProps.get("username"));
	    connectedDeviceInfo.setPassword(extraProps.get("password"));
	    device.setConnectedDeviceInfo(connectedDeviceInfo);
	}
	return device;

    }

    /**
     * Converts device response to Device object.
     * 
     * @param deviceResponse
     *            DeviceResponse received from rest
     * @return Device instance
     */
    private DeviceAccountInfo convertAccountDetailsToDeviceAccount(DeviceAccountResponse deviceAccountDetails) {
	DeviceAccountInfo deviceAccount = null;
	if (null != deviceAccountDetails) {
	    deviceAccount = new DeviceAccountInfo();
	    deviceAccount.setAccountNumber(deviceAccountDetails.getAccountNumber());
	    deviceAccount.setHomeAccountGroup(deviceAccountDetails.getHomeAccountGroup());
	    deviceAccount.setName(deviceAccountDetails.getName());
	    deviceAccount.setPhoneNumber(deviceAccountDetails.getPhoneNumber());
	    deviceAccount.setAddress(deviceAccountDetails.getAddress());
	    deviceAccount.setId(deviceAccountDetails.getId());
	    List<Device> ecatsDeviceList = new ArrayList<Device>();
	    deviceAccount.setDevices(ecatsDeviceList);

	    if (null != deviceAccountDetails.getDevices()) {
		List<DeviceObject> deviceList = deviceAccountDetails.getDevices();
		for (DeviceObject deviceObject : deviceList) {

		    Device ecatsSettop = new Device();
		    ecatsSettop.setId(deviceObject.getId());
		    ecatsSettop.setName(deviceObject.getName());
		    ecatsSettop.setHardwareRevision(deviceObject.getHardwareRevision());

		    ecatsSettop.setHostMacAddress(deviceObject.getHostMacAddress());
		    ecatsSettop.setHostIp4Address(deviceObject.getHostIp4Address());
		    ecatsSettop.setHostIp6Address(deviceObject.getHostIp6Address());
		    ecatsSettop.setHostIpAddress(deviceObject.getHostIp4Address());
		    ecatsSettop.setClientIpAddress(deviceObject.getClientIpAddress());
		    ecatsSettop.setModel(deviceObject.getModel());
		    ecatsSettop.setManufacturer(deviceObject.getManufacturer());
		    ecatsSettop.setSerialNumber(deviceObject.getSerialNumber());
		    ecatsSettop.setRemoteType(deviceObject.getRemoteType());
		    // Gateway mac address separated by comma
		    ecatsSettop.setGateWaySettopMacAddress(deviceObject.getGatewayMac());
		    ecatsSettop.setExtraProperties(deviceObject.getExtraProperties());
		    ecatsSettop.setRackName(deviceObject.getRackName());
		    ecatsSettop.setSlotName(deviceObject.getSlotName());
		    ecatsSettop.setSlotNumber(deviceObject.getSlotNumber());
		    ecatsSettop.setRackServerHost(deviceObject.getRackServerHost());
		    ecatsSettop.setRackServerPort(deviceObject.getRackServerPort());
		    ecatsSettop.setHomeAccountGroupName(deviceObject.getHomeAccountGroupName());
		    ecatsSettop.setHomeAccountName(deviceObject.getHomeAccountName());
		    ecatsSettop.setHomeAccountNumber(deviceObject.getHomeAccountNumber());
		    ecatsSettop.setExtraProperties(deviceObject.getExtraProperties());
		    ecatsDeviceList.add(ecatsSettop);
		}
	    }
	}
	return deviceAccount;

    }

}
