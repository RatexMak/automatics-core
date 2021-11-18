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
package com.automatics.device;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.core.DeviceAccountHandler;
import com.automatics.core.DeviceAccountRestClient;
import com.automatics.core.SupportedModelHandler;
import com.automatics.enums.DeviceClass;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.rack.RackDeviceValidationManager;
import com.automatics.rack.RackInitializer;
import com.automatics.tap.AutomaticsTapApi;

/**
 * Class for initializing connected devices
 * 
 * @author anandam.s
 *
 */
public class ConnectedDevices {

    /**
     * RDKB device
     */
    Dut broadBandDevice;

    /**
     * Home Account number of RDKB device
     */
    String homeAccountNumber = null;

    /**
     * Parameter to avoid re-initialization
     */
    boolean isConnectedDevicesLocked;

    /**
     * list of locked connected devices
     */
    List<DutInfo> lockedSettops = new ArrayList<DutInfo>();
    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectedDevices.class);

    /**
     * Rack initializer instance
     */
    RackInitializer rackInitializerInstance = AutomaticsTapApi.getRackInitializerInstance();

    /**
     * Constructor
     * 
     * @param broadBandDevice
     */
    public ConnectedDevices(Dut broadBandDevice) {
	this.broadBandDevice = broadBandDevice;
	this.homeAccountNumber = new DeviceAccountHandler(rackInitializerInstance)
		.getHomeAccount(broadBandDevice.getHostMacAddress());
    }

    /**
     * Get all the connected devices of RDKB device
     * 
     * @return
     */
    public List<String> getAllConnectedDevices() {

	LOGGER.info("Getting devices for home account {}", homeAccountNumber);
	List<String> settopMacs = DeviceAccountRestClient.getSettopMacsFromHomeAccount(homeAccountNumber);
	LOGGER.info("Devices for home account {} are {}", homeAccountNumber, settopMacs);

	if (null != settopMacs && !settopMacs.isEmpty()) {
	    LOGGER.info("Obtained all connected device macs for the account");
	}
	// Remove device from the list
	settopMacs.remove(broadBandDevice.getHostMacAddress());
	return settopMacs;

    }

    /**
     * check if all connected devices are locked
     * 
     * @return
     */
    public boolean isConnectedDevicesLocked() {
	return isConnectedDevicesLocked;

    }

    /**
     * set all the devices as locked
     * 
     * @param status
     */
    public void setConnectedDevicesLocked(boolean status) {
	isConnectedDevicesLocked = status;
    }

    /**
     * convert Dut info to dut instance
     * 
     * @param devices
     * @return
     */
    public List<Dut> convertDutInfoToSettopInstance(List<DutInfo> devices) {
	List<Dut> duts = new ArrayList<Dut>();
	for (DutInfo device : devices) {
	    duts.add((Device) device);
	}
	return duts;
    }

    /**
     * lock all the connected devices
     * 
     * @return
     */
    public List<DutInfo> lockConnectedDevices() {
	if (!this.isConnectedDevicesLocked) {

	    ConnectedDevices connectedDevices = new ConnectedDevices(broadBandDevice);
	    // lock the connected devices
	    LOGGER.info("Locking connected devices");
	    RackDeviceValidationManager manger = new RackDeviceValidationManager(rackInitializerInstance);
	    lockedSettops = manger.manageSettopLocking(connectedDevices.getAllConnectedDevices());
	    for (DutInfo dutObject : lockedSettops) {
		startTraceForLockedClients((Device) dutObject);
	    }
	    this.setConnectedDevicesLocked(true);
	}
	return lockedSettops;
    }

    /**
     * 
     * Method to start the trace of connected clients if not already started
     * 
     * @param settopsLocked
     */
    private void startTraceForLockedClients(Dut device) {
	// Starting trace for those devices, whose trace was not initated .
	LOGGER.info("Starting trace from provider for connected clients if not already started");
	LOGGER.info("=========================================================================");
	if (!SupportedModelHandler.isNonRDKDevice(device)) {
	    TraceProvider traceProvider = device.getTrace();

	    LOGGER.info("sshTrace.isMonitoringStarted() for {} = {}", device.getHostMacAddress(),
		    traceProvider.isMonitoringStarted());
	    if (!traceProvider.isMonitoringStarted()) {
		try {
		    if (AutomaticsTapApi.isSerialConsoleExecutionRequired()) {
			device.getTrace().startTrace();
		    } else {
			(device.getTrace()).startTrace();
			(device.getTrace()).startBuffering();
		    }
		} catch (Exception e) {
		    LOGGER.error("Exception while starting trace for {},{}" + device.getHostMacAddress(),
			    e.getMessage());

		}
	    }

	} else {
	    LOGGER.info("Trace not applicable for Non RDK Devices - " + device.getHostMacAddress());
	}

    }

    public List<DutInfo> getWifiDeviceOfType(List<DutInfo> settopsLocked, DeviceClass deviceClass) {
	DeviceAccount ecatsHomeAct = new DeviceAccount(homeAccountNumber, settopsLocked);
	List<DutInfo> wifiDeviceList = ecatsHomeAct.getWiFiDevicesByType(deviceClass);
	return wifiDeviceList;
    }

    /**
     * Method to get home account number
     * 
     * @return
     */
    public String getHomeAccountNumber() {
	return homeAccountNumber;
    }

    public void performInitForDevices(List<Dut> flexRouterList) {
	for (Dut device : flexRouterList) {
	    startTraceForLockedClients(device);
	}
    }
}
