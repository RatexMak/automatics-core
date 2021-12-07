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

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LinuxCommandConstants;
import com.automatics.constants.XconfConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Dut;
import com.automatics.providers.xconf.XConfDataProvider;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;

/**
 * 
 * Utility class for XConf related activities.
 *
 */
public class XConfUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XConfUtils.class);

    /**
     * Utility method which creates a swupdate.conf in opt directory and insert the XCONF software update URL.
     *
     * @param tapEnv
     *            The tap api instance
     * @param settop
     *            The dut instance
     */
    public static void updatXconfUrlInDevice(AutomaticsTapApi tapEnv, Dut dut) {

	String xconfServerUrl = getXconfServerUrl(dut);
	String swUpdateConfFile = XconfConstants.SOFTWARE_UPDATE_CONF_FILE;

	if (SupportedModelHandler.isRDKB(dut)) {
	    swUpdateConfFile = "/nvram/swupdate.conf";
	}

	LOGGER.info("Updating xconf url {} in software update file at {}", xconfServerUrl, swUpdateConfFile);

	String consoleoutput = tapEnv.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_ECHO + " \""
		+ xconfServerUrl + "\"" + LinuxCommandConstants.REDIRECT_OPERATOR + swUpdateConfFile);
	LOGGER.info("Response of updating Xconf software update url : " + consoleoutput);

	tapEnv.waitTill(AutomaticsConstants.TEN_SECOND_IN_MILLIS);
	tapEnv.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_SYNC);

    }

    /**
     * Utility method to configure the XCONF HTTP Download firmware details. Here the "rebootImmediately" flag is set to
     * 'true' and "firmwareDownloadProtocol" is set to 'http'
     * 
     * @param eCatsTap
     *            The {@link ECatsTapApi} instance.
     * @param settop
     *            The settop to be used.
     * @param imageVersion
     *            The name of image version.
     */
    public static void configureXconfHttpDownloadFirmwareDetailsOnServer(Dut device, String imageVersion) {
	LOGGER.info("Updating xconf");
	// Increment the known reboot Counter.
	AutomaticsTapApi.incrementKnownRebootCounter(device.getHostMacAddress());

	configureXconfDownloadFirmwareDetails(device, imageVersion, true,
		XconfConstants.FIRMWARE_DOWNLOAD_PROTOCOL_HTTP);
    }

    /**
     * Helper method to configure the XCONF firmware configuration details.
     * 
     *
     * @param device
     *            The device to be used.
     * @param imageVersion
     *            The name of image version.
     * @param rebootImmediately
     *            The reboot immediately flag.
     * @param protocol
     *            The transport protocol used.
     * 
     * @return true if successfully done the firmware configurations.Otherwise false.
     */
    public static void configureXconfDownloadFirmwareDetails(final Dut device, final String imageVersion,
	    final boolean rebootImmediately, final String protocol) {

	configureXconfDownloadFirmwareDetails(device, imageVersion, rebootImmediately, protocol, null);

    }

    /**
     * Helper method to configure the XCONF firmware configuration details.
     * 
     * @param device
     *            The device to be used.
     * @param imageVersion
     *            The name of image version.
     * @param rebootImmediately
     *            The reboot immediately flag.
     * @param protocol
     *            The transport protocol used.
     * @param pdriImageName
     *            The name of PDRI image
     * 
     * @return FirmwareConfiguration The object which holds Xconf configurations
     * 
     * 
     */
    public static FirmwareConfigurations configureXconfDownloadFirmwareDetails(final Dut device, String imageVersion,
	    final boolean rebootImmediately, final String protocol, final String pdriImageName) {

	return configureXconfDownloadFirmwareDetails(device, imageVersion, rebootImmediately, protocol, pdriImageName,
		getFirmwareLocation(protocol, device, imageVersion));
    }

    /**
     * Helper method to configure the XCONF firmware configuration details.
     * 
     *
     * 
     * @param device
     *            The device to be used.
     * @param imageVersion
     *            The name of image version.
     * @param rebootImmediately
     *            The reboot immediately flag.
     * @param protocol
     *            The transport protocol used.
     * @param pdriImageName
     *            The name of PDRI image
     * 
     * @return FirmwareConfiguration The object which holds Xconf configurations
     * 
     * 
     */
    public static FirmwareConfigurations configureXconfDownloadFirmwareDetails(final Dut device, String imageVersion,
	    final boolean rebootImmediately, final String protocol, final String pdriImageName,
	    String firmwareLocation) {

	return configureXconfDownloadFirmwareDetails(device, imageVersion, rebootImmediately, protocol, pdriImageName,
		firmwareLocation, 0);
    }

    /**
     * Helper method to configure the XCONF firmware configuration details.
     * 
     * 
     * @param device
     *            The device to be used.
     * @param imageVersion
     *            The name of image version.
     * @param rebootImmediately
     *            The reboot immediately flag.
     * @param protocol
     *            The transport protocol used.
     * @param pdriImageName
     *            The name of PDRI image
     * 
     * @return FirmwareConfiguration The object which holds Xconf configurations
     * 
     * 
     */
    public static FirmwareConfigurations configureXconfDownloadFirmwareDetails(final Dut device, String imageVersion,
	    final boolean rebootImmediately, final String protocol, final String pdriImageName, String firmwareLocation,
	    int upgradeDelay) {

	// XCONF Configuration details
	return configureXconfDownloadFirmwareDetails(device, imageVersion, rebootImmediately, protocol, pdriImageName,
		firmwareLocation, upgradeDelay, false);

    }

    /**
     * Configure XConf firmware download details to Xconf simulator
     * 
     * @param dut
     * @param imageVersion
     * @param rebootImmediately
     * @param protocol
     * @param pdriImageName
     * @param firmwareLocation
     * @param upgradeDelay
     * @param factoryResetImmedietely
     * @return
     */
    public static FirmwareConfigurations configureXconfDownloadFirmwareDetails(final Dut dut, String imageVersion,
	    final boolean rebootImmediately, final String protocol, final String pdriImageName, String firmwareLocation,
	    int upgradeDelay, boolean factoryResetImmediately) {
	return configureXconfDownloadFirmwareDetails(dut, imageVersion, rebootImmediately, protocol, pdriImageName,
		firmwareLocation, upgradeDelay, factoryResetImmediately, null);

    }

    public static FirmwareConfigurations configureXconfDownloadFirmwareDetails(final Dut dut, String imageVersion,
	    final boolean rebootImmediately, final String protocol, final String pdriImageName, String firmwareLocation,
	    int upgradeDelay, boolean factoryResetImmedietely, JSONObject additionalParams) {
	LOGGER.info("Starting method configureXconfDownloadFirmwareDetails");

	String macAddress = dut.getHostMacAddress();
	String firmwareWithExtension = imageVersion;
	String firmwareWithoutExtension = imageVersion;

	/*
	 * If Partner wants to override the default mac address used in Xconf, then get the mac from XConfDataProvider
	 */
	XConfDataProvider xConfDataProvider = BeanUtils.getXConfDataProvider();
	if (null != xConfDataProvider) {
	    String macFromPartner = xConfDataProvider.getDeviceMacAddress(dut);

	    if (CommonMethods.isNotNull(macFromPartner)) {
		macAddress = macFromPartner;
	    }
	}
	// XCONF Configuration details
	FirmwareConfigurations configuration = new FirmwareConfigurations();
	// XCONF Connection handler
	XconfConnectionHandler connectionHandler = XconfConnectionHandler.get();

	if (CommonMethods.isNull(macAddress)) {
	    LOGGER.error(
		    "Unable to perform CDL configuration in XCONF Server since not able to retrieved MAC Address for the given device '"
			    + dut.getHostMacAddress() + "'");
	} else {

	    macAddress = macAddress.toUpperCase();

	    configuration.seteStbMac(macAddress);
	    configuration.setRebootImmediately(rebootImmediately);
	    configuration.setFactoryResetImmediately(factoryResetImmedietely);
	    configuration.setFirmwareDownloadProtocol(protocol);
	    if (null != xConfDataProvider) {
		String imageWithExtension = xConfDataProvider.getFirmwareFileName(dut, imageVersion);
		if (CommonMethods.isNotNull(imageWithExtension)) {
		    firmwareWithExtension = imageWithExtension;
		}

		String imageWithoutExtension = xConfDataProvider.getFirmwareName(dut, imageVersion);
		if (CommonMethods.isNotNull(imageWithoutExtension)) {
		    firmwareWithoutExtension = imageWithoutExtension;
		}
	    }

	    configuration.setFirmwareFilename(firmwareWithExtension);
	    configuration.setFirmwareVersion(firmwareWithoutExtension);
	    configuration.setFirmwareLocation(firmwareLocation);
	    configuration.setPdriImageName(pdriImageName);

	    if (protocol.equalsIgnoreCase(AutomaticsConstants.FIRMWARE_DOWNLOAD_PROTOCOL_TFTP)) {
		String tftpFirmwareLocation = getFirmwareLocation(protocol, dut, imageVersion);
		LOGGER.info(AutomaticsConstants.FIRMWARE_DOWNLOAD_PROTOCOL_TFTP + " firmware location: {}",
			tftpFirmwareLocation);

		configuration.setIpv6cloudFWLocation(tftpFirmwareLocation);
	    } else {
		configuration.setIpv6cloudFWLocation("");
	    }

	    if (upgradeDelay > 0) {
		configuration.setDelayDownload(upgradeDelay);
	    }
	    configuration.setAdditionalConfigurations(additionalParams);
	    connectionHandler.sendMessage(configuration);
	}
	
	return configuration;
    }

    /**
     * Get the firmware download location based on transport protocol used.
     * 
     * @param protocol
     *            The transport protocol used.
     * @param device
     *            The device to be used.
     * 
     * @return The firmware location.
     */
    public static String getFirmwareLocation(final String protocol, final Dut device, String imageName) {
	String firmwareLocation = null;
	XConfDataProvider xConfDataProvider = BeanUtils.getXConfDataProvider();

	if (null != xConfDataProvider) {
	    firmwareLocation = xConfDataProvider.getFirmwareLocation(device, imageName, protocol);

	} else {
	    LOGGER.error("XConfDataProvider is not configured. So could not get firmware download location.");
	}
	return firmwareLocation;

    }

    /**
     * Utility method which gets the XConf server url
     * 
     * 
     * @param dut
     *            The device instance
     */

    public static String getXconfServerUrl(Dut dut) {
	XconfConnectionHandler connectionHandler = XconfConnectionHandler.get();
	return connectionHandler.getXconfServerUrl();
    }

}
