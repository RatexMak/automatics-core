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

import com.automatics.core.RDKDevices;
import com.automatics.core.SupportedModelHandler;
import com.automatics.enums.DeviceClass;

/**
 * This class holds Home account details
 * 
 * @author reena
 */
public class DeviceAccount implements DutAccount {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private String accountNumber;

    private String name;

    private String phoneNumber;

    private List<DutInfo> availableDuts;

    private List<DutInfo> allocatedDuts;

    private boolean isAllocated;

    private DutInfo dutInfo;

    private String wisstUrl;

    private String quadAttenUrl;

    private String quadAttenDeviceId;

    private String quadAttenDeviceAlias;

    private String rackName;

    private String profileToBeActivatedForPODTests;

    private String defaultProfileToBeActiavted;

    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(DeviceAccount.class);

    public DeviceAccount(String accountNumber, List<DutInfo> availableDuts) {
	this.accountNumber = accountNumber;
	// Making sure we the caller reference is held here.
	this.availableDuts = new ArrayList<DutInfo>(availableDuts);
    }

    public DeviceAccount(String accountNumber, String name, String phoneNumber, List<DutInfo> availableDuts) {
	this.accountNumber = accountNumber;
	this.name = name;
	this.phoneNumber = phoneNumber;
	// Making sure we the caller reference is held here.
	this.availableDuts = new ArrayList<DutInfo>(availableDuts);
    }

    @Override
    public String getAccountNumber() {
	return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getPhoneNumber() {
	return phoneNumber;
    }

    @Override
    public DutInfo getPivotDut() {
	return dutInfo;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<DutInfo> getDevices() {
	return availableDuts;
    }

    public void setAllocatedDevicesList(List<DutInfo> settopMacs) {

	if (null == allocatedDuts) {
	    allocatedDuts = new ArrayList<DutInfo>();
	}
	if (null != settopMacs && !settopMacs.isEmpty()) {
	    for (DutInfo dut : settopMacs) {
		allocatedDuts.add(dut);
	    }
	}
	/**
	 * This means that all devices in the home account was allocated.
	 */
	if (allocatedDuts.size() == availableDuts.size()) {
	    isAllocated = true;
	}
    }

    public void setPivotDut(DutInfo dutInfo) {

	this.dutInfo = dutInfo;
    }

    @Override
    public List<DutInfo> getDeviceByModel(String model) {
	List<DutInfo> listOfMatchingBoxes = new ArrayList<DutInfo>();
	if (availableDuts == null || availableDuts.isEmpty()) {
	    return listOfMatchingBoxes;
	}
	for (DutInfo dut : availableDuts) {
	    if (dut.getModel().equalsIgnoreCase(model)) {
		listOfMatchingBoxes.add(dut);
	    }
	}
	return listOfMatchingBoxes;
    }

    @Override
    public boolean isLocked() {
	return isAllocated;
    }

    /**
     * Helper method to get the devices by given device class in the current home account
     * 
     * @param deviceClass
     *            The required device class
     * @return The list of devices.
     */
    public List<DutInfo> getDevicesByType(DeviceClass deviceClass) {
	LOGGER.info("STARTING METHOD: getDevicesByType");
	List<DutInfo> listOfMatchingBoxes = new ArrayList<DutInfo>();
	List<String> reqdDevices = RDKDevices.getDeviceModelsByGivenClass(deviceClass.getValue());

	LOGGER.debug("Models corresponding to the given class: " + reqdDevices);

	if (null != availableDuts || !availableDuts.isEmpty()) {
	    for (DutInfo dut : availableDuts) {
		// If any of the devices in the current home account is of the models returned, then add those in list
		// of matching boxes
		if (reqdDevices.contains(dut.getModel().toUpperCase())) {
		    listOfMatchingBoxes.add(dut);
		}
	    }
	}

	LOGGER.info("ENDING METHOD: getDevicesByType");
	return listOfMatchingBoxes;
    }

    public List<DutInfo> getWiFiDevicesByType(DeviceClass deviceClass) {
	LOGGER.debug("STARTING METHOD: getWiFiDevicesByType");
	List<DutInfo> listOfMatchingBoxes = new ArrayList<DutInfo>();
	List<String> reqdDevices = WiFiDevices.getDeviceModelsByGivenClass(deviceClass.getValue());

	LOGGER.info("Models corresponding to the given class: " + reqdDevices);

	if (null != availableDuts || !availableDuts.isEmpty()) {
	    for (DutInfo dut : availableDuts) {
		// If any of the devices in the current home account is of the models returned, then add those in list
		// of matching boxes
		if (reqdDevices.contains(dut.getModel().toUpperCase())) {
		    listOfMatchingBoxes.add(dut);
		}
	    }
	}

	LOGGER.debug("ENDING METHOD: getDevicesByType");
	return listOfMatchingBoxes;
    }

    /*
     * (non-Javadoc)
     * 
     * 
     */
    @Override
    public String getWisstUrl() {
	return wisstUrl;
    }

    /**
     * Set Wisst URL
     * 
     * @param wisstUrl
     */
    public void setWisstUrl(String wisstUrl) {
	this.wisstUrl = wisstUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * 
     */
    @Override
    public List<DutInfo> getAllRdkVGatewayDevices() {

	List<DutInfo> listOfRdkvGatewayDevices = new ArrayList<DutInfo>();
	if (availableDuts == null || availableDuts.isEmpty()) {
	    return listOfRdkvGatewayDevices;
	}

	for (DutInfo dut : availableDuts) {
	    if (SupportedModelHandler.isRDKV((Device) dut) && !(SupportedModelHandler.isRDKVClient((Device) dut))) {
		listOfRdkvGatewayDevices.add(dut);
	    }
	}
	return listOfRdkvGatewayDevices;
    }

    /*
     * (non-Javadoc)
     * 
     * 
     */
    @Override
    public List<DutInfo> getAllRdkVClientDevices() {

	List<DutInfo> listOfRdkvClientDevices = new ArrayList<DutInfo>();
	if (availableDuts == null || availableDuts.isEmpty()) {
	    return listOfRdkvClientDevices;
	}

	for (DutInfo dut : availableDuts) {
	    if (SupportedModelHandler.isRDKVClient((Device)dut)) {
		listOfRdkvClientDevices.add(dut);
	    }
	}
	return listOfRdkvClientDevices;
    }

    /*
     * (non-Javadoc)
     * 
     * 
     */
    @Override
    public List<DutInfo> getAllRdkBDevices() {
	List<DutInfo> listOfRdkBDevices = new ArrayList<DutInfo>();
	if (availableDuts == null || availableDuts.isEmpty()) {
	    return listOfRdkBDevices;
	}

	for (DutInfo dut : availableDuts) {
	    if (SupportedModelHandler.isRDKB((Device) dut)) {
		listOfRdkBDevices.add(dut);
	    }
	}
	return listOfRdkBDevices;
    }

    @Override
    public List<DutInfo> getAllRdkCClientDevices() {

	List<DutInfo> listOfRdkCClientDevices = new ArrayList<DutInfo>();
	if (availableDuts == null || availableDuts.isEmpty()) {
	    return listOfRdkCClientDevices;
	}

	for (DutInfo dut : availableDuts) {
	    if (SupportedModelHandler.isRDKC((Device) dut)) {
		listOfRdkCClientDevices.add(dut);
	    }
	}
	return listOfRdkCClientDevices;
    }

    /**
     * @return the quadAttenUrl
     */
    @Override
    public String getQuadAttenUrl() {
	return quadAttenUrl;
    }

    /**
     * @param quadAttenUrl
     *            the quadAttenUrl to set
     */
    public void setQuadAttenUrl(String quadAttenUrl) {
	this.quadAttenUrl = quadAttenUrl;
    }

    /**
     * @return the quadAttenDeviceId
     */
    @Override
    public String getQuadAttenDeviceId() {
	return quadAttenDeviceId;
    }

    /**
     * @param quadAttenDeviceId
     *            the quadAttenDeviceId to set
     */
    public void setQuadAttenDeviceId(String quadAttenDeviceId) {
	this.quadAttenDeviceId = quadAttenDeviceId;
    }

    /**
     * @return the quadAttenDeviceAlias
     */
    @Override
    public String getQuadAttenDeviceAlias() {
	return quadAttenDeviceAlias;
    }

    /**
     * @param quadAttenDeviceAlias
     *            the quadAttenDeviceAlias to set
     */
    public void setQuadAttenDeviceAlias(String quadAttenDeviceAlias) {
	this.quadAttenDeviceAlias = quadAttenDeviceAlias;
    }

    /**
     * @return the rackName
     */
    public String getRackName() {
	return rackName;
    }

    /**
     * @param rackName
     *            the rackName to set
     */
    public void setRackName(String rackName) {
	this.rackName = rackName;
    }

    /**
     * @return the profileToBeActivatedForPODTests
     */
    public String getProfileToBeActivatedForPODTests() {
	return profileToBeActivatedForPODTests;
    }

    /**
     * @param profileToBeActivatedForPODTests
     *            the profileToBeActivatedForPODTests to set
     */
    public void setProfileToBeActivatedForPODTests(String profileToBeActivatedForPODTests) {
	this.profileToBeActivatedForPODTests = profileToBeActivatedForPODTests;
    }

    /**
     * @return the defaultProfileToBeActiavted
     */
    public String getDefaultProfileToBeActiavted() {
	return defaultProfileToBeActiavted;
    }

    /**
     * @param defaultProfileToBeActiavted
     *            the defaultProfileToBeActiavted to set
     */
    public void setDefaultProfileToBeActiavted(String defaultProfileToBeActiavted) {
	this.defaultProfileToBeActiavted = defaultProfileToBeActiavted;
    }

}
