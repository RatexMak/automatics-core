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
package com.automatics.providers.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.automatics.enums.RemoteControlType;

/**
 * DeviceConfig class
 * 
 * 
 * @author Raja M
 */

public class DeviceObject {

    private String id;

    private String name;

    private String hardwareRevision;

    private String hostMacAddress;

    private String hostIp4Address;

    private String hostIp6Address;

    private String clientIpAddress;

    private String model;

    private String manufacturer;

    private String serialNumber;

    private String unitAddress;

    private String remoteType;

    private String mcardMacAddress;

    private String mcardSerialNumber;

    private String estbMacAddress;

    private String ecmMacAddress;

    private String estbIpAdress;

    private String ecmIpAddress;

    private String headend;

    private String gatewayMac;

    protected Map<String, String> extraProperties = new HashMap<String, String>();

    private String rackId;

    private String deviceType;

    private String rackName;

    private String slotName;

    private String slotNumber;

    private String settopGroupName;

    private String homeAccountName;

    private String homeAccountNumber;

    private String homeAccountGroupName;

    private String rackServerHost;

    private Integer rackServerPort;

    private String status;

    private List<String> rackGroups;

    private List<String> features;

    private List<String> components;

    private String wanMacAddress;
    
    private RemoteControlType defaultRemoteControlType;

    private List<RemoteControlType> remoteControlTypes;
    /**
     * Holds the mtaMacAddress of device
     */
    String mtaMacAddress = null;

    /**
     * Holds the mtaIpAddress of device
     */
    String mtaIpAddress = null;

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the hardwareRevision
     */
    public String getHardwareRevision() {
	return hardwareRevision;
    }

    /**
     * @param hardwareRevision
     *            the hardwareRevision to set
     */
    public void setHardwareRevision(String hardwareRevision) {
	this.hardwareRevision = hardwareRevision;
    }

    /**
     * @return the hostMacAddress
     */
    public String getHostMacAddress() {
	return hostMacAddress;
    }

    /**
     * @param hostMacAddress
     *            the hostMacAddress to set
     */
    public void setHostMacAddress(String hostMacAddress) {
	this.hostMacAddress = hostMacAddress;
    }

    /**
     * @return the hostIp4Address
     */
    public String getHostIp4Address() {
	return hostIp4Address;
    }

    /**
     * @param hostIp4Address
     *            the hostIp4Address to set
     */
    public void setHostIp4Address(String hostIp4Address) {
	this.hostIp4Address = hostIp4Address;
    }

    /**
     * @return the hostIp6Address
     */
    public String getHostIp6Address() {
	return hostIp6Address;
    }

    /**
     * @param hostIp6Address
     *            the hostIp6Address to set
     */
    public void setHostIp6Address(String hostIp6Address) {
	this.hostIp6Address = hostIp6Address;
    }

    /**
     * @return the clientIpAddress
     */
    public String getClientIpAddress() {
	return clientIpAddress;
    }

    /**
     * @param clientIpAddress
     *            the clientIpAddress to set
     */
    public void setClientIpAddress(String clientIpAddress) {
	this.clientIpAddress = clientIpAddress;
    }

    /**
     * @return the model
     */
    public String getModel() {
	return model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
	this.model = model;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
	return manufacturer;
    }

    /**
     * @param manufacturer
     *            the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
	this.manufacturer = manufacturer;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
	return serialNumber;
    }

    /**
     * @param serialNumber
     *            the serialNumber to set
     */
    public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
    }

    /**
     * @return the unitAddress
     */
    public String getUnitAddress() {
	return unitAddress;
    }

    /**
     * @param unitAddress
     *            the unitAddress to set
     */
    public void setUnitAddress(String unitAddress) {
	this.unitAddress = unitAddress;
    }

    /**
     * @return the remoteType
     */
    public String getRemoteType() {
	return remoteType;
    }

    /**
     * @param remoteType
     *            the remoteType to set
     */
    public void setRemoteType(String remoteType) {
	this.remoteType = remoteType;
    }

    /**
     * @return the estbMacAddress
     */
    public String getEstbMacAddress() {
	return estbMacAddress;
    }

    /**
     * @param estbMacAddress
     *            the estbMacAddress to set
     */
    public void setEstbMacAddress(String estbMacAddress) {
	this.estbMacAddress = estbMacAddress;
    }

    /**
     * @return the ecmMacAddress
     */
    public String getEcmMacAddress() {
	return ecmMacAddress;
    }

    /**
     * @param ecmMacAddress
     *            the ecmMacAddress to set
     */
    public void setEcmMacAddress(String ecmMacAddress) {
	this.ecmMacAddress = ecmMacAddress;
    }

    /**
     * @return the headend
     */
    public String getHeadend() {
	return headend;
    }

    /**
     * @param headend
     *            the headend to set
     */
    public void setHeadend(String headend) {
	this.headend = headend;
    }

    /**
     * @return the gatewayMac
     */
    public String getGatewayMac() {
	return gatewayMac;
    }

    /**
     * @param gatewayMac
     *            the gatewayMac to set
     */
    public void setGatewayMac(String gatewayMac) {
	this.gatewayMac = gatewayMac;
    }

    /**
     * @return the extraProperties
     */
    public Map<String, String> getExtraProperties() {
	return extraProperties;
    }

    /**
     * @param extraProperties
     *            the extraProperties to set
     */
    public void setExtraProperties(Map<String, String> extraProperties) {
	this.extraProperties = extraProperties;
    }

    /**
     * @return the rackId
     */
    public String getRackId() {
	return rackId;
    }

    /**
     * @param rackId
     *            the rackId to set
     */
    public void setRackId(String rackId) {
	this.rackId = rackId;
    }

    /**
     * @return the deviceType
     */
    public String getDeviceType() {
	return deviceType;
    }

    /**
     * @param deviceType
     *            the deviceType to set
     */
    public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
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
     * @return the slotName
     */
    public String getSlotName() {
	return slotName;
    }

    /**
     * @param slotName
     *            the slotName to set
     */
    public void setSlotName(String slotName) {
	this.slotName = slotName;
    }

    /**
     * @return the slotNumber
     */
    public String getSlotNumber() {
	return slotNumber;
    }

    /**
     * @param slotNumber
     *            the slotNumber to set
     */
    public void setSlotNumber(String slotNumber) {
	this.slotNumber = slotNumber;
    }

    /**
     * @return the settopGroupName
     */
    public String getSettopGroupName() {
	return settopGroupName;
    }

    /**
     * @param settopGroupName
     *            the settopGroupName to set
     */
    public void setSettopGroupName(String settopGroupName) {
	this.settopGroupName = settopGroupName;
    }

    /**
     * @return the homeAccountName
     */
    public String getHomeAccountName() {
	return homeAccountName;
    }

    /**
     * @param homeAccountName
     *            the homeAccountName to set
     */
    public void setHomeAccountName(String homeAccountName) {
	this.homeAccountName = homeAccountName;
    }

    /**
     * @return the homeAccountNumber
     */
    public String getHomeAccountNumber() {
	return homeAccountNumber;
    }

    /**
     * @param homeAccountNumber
     *            the homeAccountNumber to set
     */
    public void setHomeAccountNumber(String homeAccountNumber) {
	this.homeAccountNumber = homeAccountNumber;
    }

    /**
     * @return the homeAccountGroupName
     */
    public String getHomeAccountGroupName() {
	return homeAccountGroupName;
    }

    /**
     * @param homeAccountGroupName
     *            the homeAccountGroupName to set
     */
    public void setHomeAccountGroupName(String homeAccountGroupName) {
	this.homeAccountGroupName = homeAccountGroupName;
    }

    /**
     * @return the rackServerHost
     */
    public String getRackServerHost() {
	return rackServerHost;
    }

    /**
     * @param rackServerHost
     *            the rackServerHost to set
     */
    public void setRackServerHost(String rackServerHost) {
	this.rackServerHost = rackServerHost;
    }

    /**
     * @return the rackServerPort
     */
    public Integer getRackServerPort() {
	return rackServerPort;
    }

    /**
     * @param rackServerPort
     *            the rackServerPort to set
     */
    public void setRackServerPort(Integer rackServerPort) {
	this.rackServerPort = rackServerPort;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
	this.status = status;
    }

    /**
     * @return the rackGroups
     */
    public List<String> getRackGroups() {
	return rackGroups;
    }

    /**
     * @param rackGroups
     *            the rackGroups to set
     */
    public void setRackGroups(List<String> rackGroups) {
	this.rackGroups = rackGroups;
    }

    /**
     * @return the features
     */
    public List<String> getFeatures() {
	return features;
    }

    /**
     * @param features
     *            the features to set
     */
    public void setFeatures(List<String> features) {
	this.features = features;
    }

    /**
     * @return the components
     */
    public List<String> getComponents() {
	return components;
    }

    /**
     * @param components
     *            the components to set
     */
    public void setComponents(List<String> components) {
	this.components = components;
    }

    /**
     * @return the wanMacAddress
     */
    public String getWanMacAddress() {
	return wanMacAddress;
    }

    /**
     * @param wanMacAddress
     *            the wanMacAddress to set
     */
    public void setWanMacAddress(String wanMacAddress) {
	this.wanMacAddress = wanMacAddress;
    }

    /**
     * @return the estbIpAdress
     */
    public String getEstbIpAdress() {
	return estbIpAdress;
    }

    /**
     * @param estbIpAdress
     *            the estbIpAdress to set
     */
    public void setEstbIpAdress(String estbIpAdress) {
	this.estbIpAdress = estbIpAdress;
    }

    /**
     * @return the ecmIpAddress
     */
    public String getEcmIpAddress() {
	return ecmIpAddress;
    }

    /**
     * @param ecmIpAddress
     *            the ecmIpAddress to set
     */
    public void setEcmIpAddress(String ecmIpAddress) {
	this.ecmIpAddress = ecmIpAddress;
    }

    /**
     * @return the mcardMacAddress
     */
    public String getMcardMacAddress() {
	return mcardMacAddress;
    }

    /**
     * @param mcardMacAddress
     *            the mcardMacAddress to set
     */
    public void setMcardMacAddress(String mcardMacAddress) {
	this.mcardMacAddress = mcardMacAddress;
    }

    /**
     * @return the mCardSerialNumber
     */
    public String getMcardSerialNumber() {
	return mcardSerialNumber;
    }

    /**
     * @param mCardSerialNumber
     *            the mCardSerialNumber to set
     */
    public void setMcardSerialNumber(String mCardSerialNumber) {
	this.mcardSerialNumber = mCardSerialNumber;
    }

    /**
     * @return the defaultRemoteControlType
     */
    public RemoteControlType getDefaultRemoteControlType() {
	return defaultRemoteControlType;
    }

    /**
     * @param defaultRemoteControlType the defaultRemoteControlType to set
     */
    public void setDefaultRemoteControlType(RemoteControlType defaultRemoteControlType) {
	this.defaultRemoteControlType = defaultRemoteControlType;
    }
    
    /**
     * @return the remoteControlTypes
     */
    public List<RemoteControlType> getRemoteControlTypes() {
        return remoteControlTypes;
    }

    /**
     * @param remoteControlTypes the remoteControlTypes to set
     */
    public void setRemoteControlTypes(List<RemoteControlType> remoteControlTypes) {
        this.remoteControlTypes = remoteControlTypes;
    }

    /**
     *
     * @return
     */
    public String getMtaMacAddress() {
	return mtaMacAddress;
    }

    /**
     *
     * @param mtaMacAddress
     */
    public void setMtaMacAddress(String mtaMacAddress) {
	this.mtaMacAddress = mtaMacAddress;
    }

    /**
     *
     * @return
     */
    public String getMtaIpAddress() {
	return mtaIpAddress;
    }

    /**
     *
     * @param mtaIpAddress
     */
    public void setMtaIpAddress(String mtaIpAddress) {
	this.mtaIpAddress = mtaIpAddress;
    }
}
