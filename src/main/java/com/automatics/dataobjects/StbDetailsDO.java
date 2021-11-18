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
package com.automatics.dataobjects;

/**
 * Class to hold the data object values for STB details
 * 
 * @author surajmathew
 *
 */
public class StbDetailsDO {

    /** Variable to hold the software version of stb */
    private String softwareVersion;

    /** Variable to hold the image version of stb */
    private String imageVersion;

    /** Variable to hold the cable card firmware version of stb */
    private String cableCardFirmwareVersion;

    /** Variable to hold the ip address of stb */
    private String ipAddress;

    /** Variable to hold the eStb mac address */
    private String macAddress;

    /** Variable to hold the ecm Mac Address */
    private String ecmMacAddress;

    /** Variable to hold the ecm Ip address */
    private String ecmIpAddress;

    /** Variable to hold the head end info */
    private String headEnd;

    /** Variable to hold the MoCA MAC */
    private String mocaMac;

    /** Variable to hold the MoCA IP */
    private String mocaIp;

    /** Variable to hold the eth MAC */
    private String ethMac;

    /** Variable to hold the WIFI MAC */
    private String wifiMac;

    /** Variable to hold the DACInitTimestamp */
    private String dacInitTimestamp;

    /** Variable to hold the build_type */
    private String build_type;

    /** Variable to hold the model */
    private String model;

    /** Variable to hold the model_number */
    private String model_number;

    /** Variable to hold the serial_number */
    private String serial_number;

    /**
     * STB Details
     */
    public enum StbDetails {

	SOFTWARE_VERSION("software_version=(.*)[\n\r]"),
	CABLE_CARD_FIRMWARE_VERSION("cable_card_firmware_version=(.*)[\n\r]"),
	IMAGE_VERSION("imageVersion=(.*)[\n\r]"),
	ECM_MAC("ecm_mac=(.*)[\n\r]"),
	ECM_IP("ecm_ip=(.*)[\n\r]"),
	ESTB_MAC("estb_mac=(.*)[\n\r]"),
	ESTB_IP("estb_ip=(.*)[\n\r]"),
	MOCA_MAC("moca_mac=(.*)[\n\r]"),
	MOCA_IP("moca_ip=(.*)[\n\r]"),
	ETH_MAC("eth_mac=(.*)[\n\r]"),
	WIFI_MAC("wifi_mac=(.*)[\n\r]"),
	DAC_INIT_TIME_STAMP("DACInitTimestamp=(.*)[\n\r]"),
	BUILD_TYPE("build_type=(.*)[\n\r]"),
	MODEL("model=(.*)[\n\r]"),
	MODEL_NUMBER("model_number=(.*)[\n\r]"),
	SERIAL_NUMBER("serial_number=(.*)[\n\r]"),
	BOX_IP("boxIP=(.*)[\n\r]");

	private String regex;

	private StbDetails(String regex) {
	    this.regex = regex;
	}

	public String getRegex() {
	    return this.regex;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("StbDetailsDO [ecmIpAddress=");
	builder.append(ecmIpAddress);
	builder.append(", ecmMacAddress=");
	builder.append(ecmMacAddress);
	builder.append(", ipAddress=");
	builder.append(ipAddress);
	builder.append(", macAddress=");
	builder.append(macAddress);
	builder.append(", headEnd=");
	builder.append(headEnd);
	builder.append("]");
	return builder.toString();
    }

    public String getSoftwareVersion() {
	return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
	this.softwareVersion = softwareVersion;
    }

    public String getImageVersion() {
	return imageVersion;
    }

    public void setImageVersion(String imageVersion) {
	this.imageVersion = imageVersion;
    }

    public String getCableCardFirmwareVersion() {
	return cableCardFirmwareVersion;
    }

    public void setCableCardFirmwareVersion(String cableCardFirmwareVersion) {
	this.cableCardFirmwareVersion = cableCardFirmwareVersion;
    }

    public String getIpAddress() {
	return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
	return macAddress;
    }

    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    public String getEcmMacAddress() {
	return ecmMacAddress;
    }

    public void setEcmMacAddress(String ecmMacAddress) {
	this.ecmMacAddress = ecmMacAddress;
    }

    public String getEcmIpAddress() {
	return ecmIpAddress;
    }

    public void setEcmIpAddress(String ecmIpAddress) {
	this.ecmIpAddress = ecmIpAddress;
    }

    public String getHeadEnd() {
	return headEnd;
    }

    public void setHeadEnd(String headEnd) {
	this.headEnd = headEnd;
    }

    public String getMocaMac() {
	return mocaMac;
    }

    public void setMocaMac(String mocaMac) {
	this.mocaMac = mocaMac;
    }

    public String getMocaIp() {
	return mocaIp;
    }

    public void setMocaIp(String mocaIp) {
	this.mocaIp = mocaIp;
    }

    public String getEthMac() {
	return ethMac;
    }

    public void setEthMac(String ethMac) {
	this.ethMac = ethMac;
    }

    public String getWifiMac() {
	return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
	this.wifiMac = wifiMac;
    }

    public String getDacInitTimestamp() {
	return dacInitTimestamp;
    }

    public void setDacInitTimestamp(String dacInitTimestamp) {
	this.dacInitTimestamp = dacInitTimestamp;
    }

    public String getBuild_type() {
	return build_type;
    }

    public void setBuild_type(String build_type) {
	this.build_type = build_type;
    }

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getModel_number() {
	return model_number;
    }

    public void setModel_number(String model_number) {
	this.model_number = model_number;
    }

    public String getSerial_number() {
	return serial_number;
    }

    public void setSerial_number(String serial_number) {
	this.serial_number = serial_number;
    }

}