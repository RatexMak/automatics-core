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
package com.automatics.providers.imageupgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.XconfConstants;
import com.automatics.device.Dut;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.xconf.XConfUtils;

/**
 * 
 * Performs image upgrade using xconf
 *
 */
public abstract class AbstractXConfImageUpgradeProvider implements ImageUpgradeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXConfImageUpgradeProvider.class);

    private int firmwareUpgradeDelay = 0;

    private boolean factoryResetImmediately = false;

    private String pdriImageName = null;

    private String firmwareDownloadProtocol = XconfConstants.FIRMWARE_DOWNLOAD_PROTOCOL_HTTP;

    @Override
    public String performImageUpgrade(boolean rebootImmediately, String imageToBeDownloaded, Dut device) {
	XConfRequestParams params = new XConfRequestParams();
	params.setFirmwareToBeDownloaded(imageToBeDownloaded);
	String xConfSimulatorUrl = XConfUtils.getXconfServerUrl(device);
	params.setxConfServerUrl(xConfSimulatorUrl);
	params.setRebootImmediately(rebootImmediately);
	return performImageUpgrade(params, device);

    }

    /**
     * Gets the image download url
     * 
     * @param dut
     * @return
     */
    public String getImageDownloadUrl(ImageRequestParams request, Dut dut) {
	String imageDownloadLocation = XConfUtils.getFirmwareLocation(XconfConstants.FIRMWARE_DOWNLOAD_PROTOCOL_HTTP,
		dut, request.getFirmwareToBeDownloaded());
	LOGGER.info("Xconf image download location: {}", imageDownloadLocation);
	return imageDownloadLocation;
    }

    @Override
    public String performImageUpgrade(ImageRequestParams request, Dut device) {
	String response = "";
	AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();

	String firmwareLocation = getImageDownloadUrl(request, device);

	// Step-1 Sending config data to XConf simulator
	LOGGER.info("Sending XConf configuration data to XConf Simulator for device: {}", device.getHostMacAddress());
	// Increment the known reboot Counter.
	AutomaticsTapApi.incrementKnownRebootCounter(device.getHostMacAddress());
	sendXConfFirmwareDownloadDetails(device, request.getFirmwareToBeDownloaded(), request.isRebootImmediately(),
		firmwareDownloadProtocol, pdriImageName, firmwareLocation, firmwareUpgradeDelay,
		factoryResetImmediately);

	// Step-2 Set XConf simulator software update url in device
	XConfUtils.updatXconfUrlInDevice(tapEnv, device);

	// Step-3 Reboot if reboot flag set to true
	LOGGER.info("XConf Reboot now: {}", request.isRebootImmediately());
	if (request.isRebootImmediately()) {
	    LOGGER.info("XConf Rebooting now: {}", device.getHostMacAddress());
	    boolean rebootStatus = CommonMethods.rebootAndWaitForIpAccusition(device, tapEnv);
	    if (rebootStatus) {
		response = "Successfully triggered XConf Code Download";
	    } else {
		response = "IP not acquired after reboot";
	    }
	} else {
	    LOGGER.info("Applied XConf settings. Not Rebooting device now");
	    response = "Applied XConf settings. Not Rebooting device now";
	}

	return response;
    }

    /**
     * Sends Xconf config details to xconf simulator
     * 
     * @param dut
     * @param imageVersion
     * @param rebootImmediately
     * @param protocol
     * @param pdriImageName
     * @param firmwareLocation
     * @param upgradeDelay
     * @param factoryResetImmedietely
     */
    public void sendXConfFirmwareDownloadDetails(final Dut dut, String imageVersion, final boolean rebootImmediately,
	    final String protocol, final String pdriImageName, String firmwareLocation, int upgradeDelay,
	    boolean factoryResetImmedietely) {
	XConfUtils.configureXconfDownloadFirmwareDetails(dut, imageVersion, rebootImmediately, protocol, pdriImageName,
		firmwareLocation, upgradeDelay, factoryResetImmedietely);

    }

    public int getFirmwareUpgradeDelay() {
	return firmwareUpgradeDelay;
    }

    public void setFirmwareUpgradeDelay(int firmwareUpgradeDelay) {
	this.firmwareUpgradeDelay = firmwareUpgradeDelay;
    }

    public boolean isFactoryResetImmediately() {
	return factoryResetImmediately;
    }

    public void setFactoryResetImmediately(boolean factoryResetImmediately) {
	this.factoryResetImmediately = factoryResetImmediately;
    }

    public String getPdriImageName() {
	return pdriImageName;
    }

    public void setPdriImageName(String pdriImageName) {
	this.pdriImageName = pdriImageName;
    }

    public String getFirmwareDownloadProtocol() {
	return firmwareDownloadProtocol;
    }

    public void setFirmwareDownloadProtocol(String firmwareDownloadProtocol) {
	this.firmwareDownloadProtocol = firmwareDownloadProtocol;
    }

}
