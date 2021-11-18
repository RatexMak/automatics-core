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

/**
 * Pojo class holds the connected device information which are populated from extra properties defined in MDS.
 * 
 * @author smariy449
 *
 */
public class ConnectedDeviceInfo {

    /**
     * Instance variable to holds the Wi-Fi Mac address.
     */
    private String wifiMacAddress;

    /**
     * Instance variable to holds the Ethernet Mac address.
     */
    private String ethernetMacAddress;

    /**
     * Instance variable to holds the Connection Type.
     */
    private String connectionType;

    /**
     * Instance variable to holds the User Name.
     */
    private String userName;

    /**
     * Instance variable to holds the Password.
     */
    private String password;

    /**
     * Instance variable to holds the DeviceConfig IP Address.
     */
    private String deviceIpAddress;

    /**
     * Instance variable to holds the DeviceConfig Port Address.
     */
    private String devicePortAddress;

    /**
     * Instance variable to holds the Wi-Fi Capability.
     */
    private String wifiCapability;

    /**
     * Instance variable to holds the OS Type.
     */
    private String osType;

    /**
     * Holds the Xfi Username
     */
    private String XfiUserName = null;

    /**
     * Holds the Xfi password
     */
    private String XfiPassword = null;

    /**
     * @return XfiUserName
     */
    public String getXfiUserName() {
	return XfiUserName;
    }

    /**
     * @param xfiUserName
     */
    public void setXfiUserName(String xfiUserName) {
	XfiUserName = xfiUserName;
    }

    /**
     * @return XfiPassword
     */
    public String getXfiPassword() {
	return XfiPassword;
    }

    /**
     * @param xfiPassword
     */
    public void setXfiPassword(String xfiPassword) {
	XfiPassword = xfiPassword;
    }

    /**
     * @return the wifiMacAddress
     */
    public String getWifiMacAddress() {
	return wifiMacAddress;
    }

    /**
     * @param wifiMacAddress
     *            the wifiMacAddress to set
     */
    public void setWifiMacAddress(String wifiMacAddress) {
	this.wifiMacAddress = wifiMacAddress;
    }

    /**
     * @return the ethernetMacAddress
     */
    public String getEthernetMacAddress() {
	return ethernetMacAddress;
    }

    /**
     * @param ethernetMacAddress
     *            the ethernetMacAddress to set
     */
    public void setEthernetMacAddress(String ethernetMacAddress) {
	this.ethernetMacAddress = ethernetMacAddress;
    }

    /**
     * @return the connectionType
     */
    public String getConnectionType() {
	return connectionType;
    }

    /**
     * @param connectionType
     *            the connectionType to set
     */
    public void setConnectionType(String connectionType) {
	this.connectionType = connectionType;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
	return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * @return the deviceIpAddress
     */
    public String getDeviceIpAddress() {
	return deviceIpAddress;
    }

    /**
     * @param deviceIpAddress
     *            the deviceIpAddress to set
     */
    public void setDeviceIpAddress(String deviceIpAddress) {
	this.deviceIpAddress = deviceIpAddress;
    }

    /**
     * @return the devicePortAddress
     */
    public String getDevicePortAddress() {
	return devicePortAddress;
    }

    /**
     * @param devicePortAddress
     *            the devicePortAddress to set
     */
    public void setDevicePortAddress(String devicePortAddress) {
	this.devicePortAddress = devicePortAddress;
    }

    /**
     * @return the wifiCapability
     */
    public String getWifiCapability() {
	return wifiCapability;
    }

    /**
     * @param wifiCapability
     *            the wifiCapability to set
     */
    public void setWifiCapability(String wifiCapability) {
	this.wifiCapability = wifiCapability;
    }

    /**
     * @return the osType
     */
    public String getOsType() {
	return osType;
    }

    /**
     * @param osType
     *            the osType to set
     */
    public void setOsType(String osType) {
	this.osType = osType;
    }

    /**
     * Prints the connected client details.
     */
    @Override
    public String toString() {
	StringBuffer connectedDeviceDetails = new StringBuffer();
	connectedDeviceDetails.append("\nConnected DeviceConfig Info -> ").append("\nIP Address = ").append(deviceIpAddress)
		.append("\n").append(" Port Address = ").append(devicePortAddress).append("\n").append(" OS Type = ")
		.append(osType).append("\n").append(" User Name = ").append(userName).append(" Password = ")
		.append("********").append("\n").append(" Connection Type = ").append(connectionType).append("\n")
		.append(" Wi-Fi Capability = ").append(wifiCapability).append("\n").append(" Wi-Fi Mac Address = ")
		.append(wifiMacAddress).append("\n").append(" Ethernet Mac Address = ").append(ethernetMacAddress);

	return connectedDeviceDetails.toString();
    }

}
