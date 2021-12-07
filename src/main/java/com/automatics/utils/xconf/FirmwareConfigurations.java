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
package com.automatics.utils.xconf;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.FrameworkHelperUtils;

/**
 * Configures the XCONF firmware configuration details before executing XCONF SNMP or XCONF DIFD related test cases.
 * 
 * @author Selvaraj Mariyappan
 */
public class FirmwareConfigurations {

    /** The JSON key object for eStbMac. */
    private static final String KEY_OBJECT_E_STB_MAC = "eStbMac";

    /** The JSON key object for 'rebootImmediately'. */

    private static final String KEY_OBJECT_REBOOT_IMMEDIATELY = "rebootImmediately";

    /** The JSON key object for 'factoryResetImmediately'. */

    private static final String KEY_OBJECT_FACTORY_RESET_IMMEDIATELY = "factoryResetImmediately";

    /** The JSON key object for 'upgradeDelay'. */
    private static final String KEY_OBJECT_UPGRADE_DELAY = "upgradeDelay";

    /** The JSON key object for 'firmwareLocation'. */
    private static final String KEY_OBJECT_FIRMWARE_LOCATION = "firmwareLocation";

    /** The JSON key object for 'firmwareVersion'. */
    private static final String KEY_OBJECT_FIRMWARE_VERSION = "firmwareVersion";

    /** The JSON key object for 'firmwareFilename'. */
    private static final String KEY_OBJECT_FIRMWARE_FILENAME = "firmwareFilename";

    /** The JSON key object for 'firmwareDownloadProtocol'. */
    private static final String KEY_OBJECT_FIRMWARE_DOWNLOAD_PROTOCOL = "firmwareDownloadProtocol";

    /** The JSON key object for 'ipv6firmwareLocation'. */
    private static final String KEY_OBJECT_IPV6_FIRMWARE_LOCATION = "ipv6FirmwareLocation";

    /** The JSON key object for 'xconfServerConfig'. */
    private static final String KEY_OBJECT_XCONF_SERVER_CONFIGURATION = "xconfServerConfig";

    /** Holds the JSON key for PDRI image */
    private static final String KEY_OBJECT_PDRI_IMAGE_NAME = "additionalFwVerInfo";

    /** The JSON key object for 'delayDownload'. */
    private static final String KEY_OBJECT_DELAY_DOWNLOAD = "delayDownload";

    /** The firmware download protocol. */
    private String firmwareDownloadProtocol = null;

    /** The firmware filename. */
    private String firmwareFilename = null;

    /** The firmware location. */
    private String firmwareLocation = null;

    /** The firmware version. */
    private String firmwareVersion = null;

    /** The upgrade delay. */
    private int upgradeDelay = 0;

    /** The reboot immediately flag. */
    private boolean rebootImmediately = false;

    /** The reboot immediately flag. */
    private boolean factoryResetImmediately = false;

    /** The eSTB mac address. */
    private String eStbMac = null;

    /** The IPv6 cloud location. */
    private String ipv6cloudFWLocation = null;

    /** Holds the PDRI image name */
    private String pdriImageName = null;

    /** The delayDownload delay. */
    private int delayDownload = 0;
    
    private JSONObject additionalConfigurations = null;

    public JSONObject getAdditionalConfigurations() {
        return additionalConfigurations;
    }

    public void setAdditionalConfigurations(JSONObject json) {
        this.additionalConfigurations = json;
    }

    /**
     * Get firmware download protocol.
     * 
     * @return the firmwareDownloadProtocol
     */
    public String getFirmwareDownloadProtocol() {
	return firmwareDownloadProtocol;
    }

    /**
     * Set the firmware download protocol.
     * 
     * @param firmwareDownloadProtocol
     *            the firmwareDownloadProtocol to set
     */
    public void setFirmwareDownloadProtocol(String firmwareDownloadProtocol) {
	this.firmwareDownloadProtocol = firmwareDownloadProtocol;
    }

    /**
     * get the firmware file name.
     * 
     * @return the firmwareFilename
     */
    public String getFirmwareFilename() {
	return firmwareFilename;
    }

    /**
     * Set the firmware file name.
     * 
     * @param firmwareFilename
     *            the firmwareFilename to set
     */
    public void setFirmwareFilename(String firmwareFilename) {
	this.firmwareFilename = firmwareFilename;
    }

    /**
     * Get the firmware location.
     * 
     * @return the firmwareLocation
     */
    public String getFirmwareLocation() {
	return firmwareLocation;
    }

    /**
     * Set firmware location.
     * 
     * @param firmwareLocation
     *            the firmwareLocation to set
     */
    public void setFirmwareLocation(String firmwareLocation) {
	this.firmwareLocation = firmwareLocation;
    }

    /**
     * Get the firmware version.
     * 
     * @return the firmwareVersion
     */
    public String getFirmwareVersion() {
	return firmwareVersion;
    }

    /**
     * Set the firmware version.
     * 
     * @param firmwareVersion
     *            the firmwareVersion to set
     */
    public void setFirmwareVersion(String firmwareVersion) {
	this.firmwareVersion = firmwareVersion;
    }

    /**
     * Get the upgrade delay.
     * 
     * @return the upgradeDelay
     */
    public int getUpgradeDelay() {
	return upgradeDelay;
    }

    /**
     * Set upgrade delay.
     * 
     * @param upgradeDelay
     *            the upgradeDelay to set
     */
    public void setUpgradeDelay(int upgradeDelay) {
	this.upgradeDelay = upgradeDelay;
    }

    /**
     * Get reboot immediately flag.
     * 
     * @return the rebootImmediately
     */
    public boolean getRebootImmediately() {
	return rebootImmediately;
    }

    /**
     * Set reboot immediately flag.
     * 
     * @param rebootImmediately
     *            the rebootImmediately to set
     */
    public void setRebootImmediately(boolean rebootImmediately) {
	this.rebootImmediately = rebootImmediately;
    }

    public boolean isFactoryResetImmediately() {
	return factoryResetImmediately;
    }

    public void setFactoryResetImmediately(boolean factoryResetImmediately) {
	this.factoryResetImmediately = factoryResetImmediately;
    }

    /**
     * Get eSTB Mac address.
     * 
     * @return the eStbMac
     */
    public String getEstbMac() {
	return eStbMac;
    }

    /**
     * Set the eSTB Mac address.
     * 
     * @param eStbMac
     *            the eStbMac to set
     */
    public void seteStbMac(String eStbMac) {
	this.eStbMac = eStbMac;
    }

    /**
     * Get ipv6 cloud firmware location.
     * 
     * @return the ipv6cloudFWLocation
     */
    public String getIpv6cloudFWLocation() {
	return ipv6cloudFWLocation;
    }

    /**
     * Set ipv6 cloud firmware location.
     * 
     * @param ipv6cloudFWLocation
     *            the ipv6cloudFWLocation to set
     */
    public void setIpv6cloudFWLocation(String ipv6cloudFWLocation) {
	this.ipv6cloudFWLocation = ipv6cloudFWLocation;
    }

    /**
     * @return the pdriImageName
     */
    public String getPdriImageName() {
	return pdriImageName;
    }

    /**
     * @param pdriImageName
     *            the pdriImageName to set
     */
    public void setPdriImageName(String pdriImageName) {
	this.pdriImageName = pdriImageName;
    }

    /**
     * @return the delayDownload
     */
    public int getDelayDownload() {
	return delayDownload;
    }

    /**
     * @param delayDownload
     *            the delayDownload to set
     */
    public void setDelayDownload(int delayDownload) {
	this.delayDownload = delayDownload;
    }

    /**
     * Convert the XCONF firmware configuration to JSON format.
     * 
     * @return {@link JSONObject} The XCONF firmware configuration in JSON format.
     * 
     * @throws JSONException
     *             if any exception happen.
     */
    public JSONObject toJson() throws JSONException {

	JSONObject xconfJsonObject = new JSONObject();

	xconfJsonObject.put(KEY_OBJECT_FIRMWARE_DOWNLOAD_PROTOCOL, getFirmwareDownloadProtocol());
	xconfJsonObject.put(KEY_OBJECT_FIRMWARE_FILENAME, getFirmwareFilename());
	xconfJsonObject.put(KEY_OBJECT_FIRMWARE_VERSION, getFirmwareVersion());
	xconfJsonObject.put(KEY_OBJECT_FIRMWARE_LOCATION, getFirmwareLocation());
	// Sending ipv6FirmwareLocation only for tftp protocol
	if (AutomaticsConstants.FIRMWARE_DOWNLOAD_PROTOCOL_TFTP.equalsIgnoreCase(getFirmwareDownloadProtocol())) {
	    xconfJsonObject.put(KEY_OBJECT_IPV6_FIRMWARE_LOCATION, getIpv6cloudFWLocation());
	}

	// xconfJsonObject.put(KEY_OBJECT_UPGRADE_DELAY, getUpgradeDelay());
	xconfJsonObject.put(KEY_OBJECT_REBOOT_IMMEDIATELY, getRebootImmediately());

	if (!getRebootImmediately() && isFactoryResetImmediately()) {
	    xconfJsonObject.put(KEY_OBJECT_FACTORY_RESET_IMMEDIATELY, isFactoryResetImmediately());
	}

	if (CommonMethods.isNotNull(getPdriImageName())) {
	    xconfJsonObject.put(KEY_OBJECT_PDRI_IMAGE_NAME, getPdriImageName());
	}

	// Support delay download option
	if (getDelayDownload() > 0) {
	    xconfJsonObject.put(KEY_OBJECT_DELAY_DOWNLOAD, getDelayDownload());
	}
	
	xconfJsonObject = FrameworkHelperUtils.get().mergeJsons(xconfJsonObject,additionalConfigurations);
	JSONObject mockServerConfig = new JSONObject();

	mockServerConfig.put(KEY_OBJECT_E_STB_MAC, getEstbMac());
	mockServerConfig.put(KEY_OBJECT_XCONF_SERVER_CONFIGURATION, xconfJsonObject);

	return mockServerConfig;
    }
}
