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
package com.automatics.providers.rack.impl;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.providers.objects.DeviceRequest;
import com.automatics.providers.objects.PowerStatusResponse;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.objects.enums.StatusMessage;
import com.automatics.providers.rack.AbstractPowerProvider;
import com.automatics.providers.rack.exceptions.PowerProviderException;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * PowerProviderRestImpl class provides the implementations for different power operations
 */
public class PowerProviderRestImpl extends AbstractPowerProvider {

    private static String BASE_URL;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerProviderRestImpl.class);

    private static String POWER_ON_PATH = "/powerManagement/device/power/on";
    private static String POWER_OFF_PATH = "/powerManagement/device/power/off";
    private static String POWER_STATUS_PATH = "/powerManagement/device/power/status";
    private static String POWER_CYCLE = "/powerManagement/device/power/powercycle";

    public PowerProviderRestImpl() {
	BASE_URL = TestUtils.getRackBaseUrl();
    }

    /**
     * Perform device power on
     */
    @Override
    public boolean powerOn() throws PowerProviderException {
	StatusResponse statusResponse = null;
	boolean isPowerOnSuccess = false;
	String url = CommonMethods.getNormalizedUrl(BASE_URL + POWER_ON_PATH);

	if (null != device) {
	    String macAddress = device.getHostMacAddress();
	    ResteasyClient client = getClient();
	    ResteasyWebTarget target = client.target(url);
	    DeviceRequest request = new DeviceRequest();
	    request.setMac(macAddress);
	    LOGGER.info("Sending request to power on device {} : {}", macAddress, url);
	    Response response = target.request().post(Entity.entity(request, "application/json"));
	    if (null != response) {
		if (response.getStatus() == HttpStatus.SC_OK) {
		    String respData = response.readEntity(String.class);
		    if (null != respData && !respData.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
			    LOGGER.info("Power on response: {}", respData);
			    statusResponse = mapper.readValue(respData, StatusResponse.class);
			    if (StatusMessage.SUCCESS == statusResponse.getStatus()) {
				isPowerOnSuccess = true;
			    }
			} catch (JsonProcessingException e) {
			    LOGGER.error("Exception parsing json for device power on {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to power on");
			} catch (IOException e) {
			    LOGGER.error("Exception parsing json for device power on {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to power on");
			}
		    }
		} else {
		    LOGGER.info("Failed to power on device {} ,status {}", request.getMac(), response.getStatus());
		}
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");
	}
	return isPowerOnSuccess;
    }

    /**
     * Perform device reboot
     * 
     */
    @Override
    public boolean reboot() throws PowerProviderException {
	StatusResponse statusResponse = null;
	boolean isRebootSuccess = false;
	String url = CommonMethods.getNormalizedUrl(BASE_URL + POWER_CYCLE);

	if (null != device) {
	    String macAddress = device.getHostMacAddress();
	    ResteasyClient client = getClient();
	    ResteasyWebTarget target = client.target(url);
	    DeviceRequest request = new DeviceRequest();
	    request.setMac(macAddress);
	    LOGGER.info("Sending request to reboot device {} : {}", macAddress, url);
	    Response response = target.request().post(Entity.entity(request, "application/json"));
	    if (null != response) {
		if (response.getStatus() == HttpStatus.SC_OK) {
		    String respData = response.readEntity(String.class);
		    if (null != respData && !respData.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
			    LOGGER.info("Reboot response: {}", respData);
			    statusResponse = mapper.readValue(respData, StatusResponse.class);
			    if (StatusMessage.SUCCESS == statusResponse.getStatus()) {
				isRebootSuccess = true;
			    }
			} catch (JsonProcessingException e) {
			    LOGGER.error("Exception parsing json for device power reboot {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to reboot");
			} catch (IOException e) {
			    LOGGER.error("Exception parsing json for device power reboot {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to reboot");
			}
		    }
		} else {
		    LOGGER.info("Failed to power reboot device {} ,status {}", request.getMac(), response.getStatus());
		}
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");
	}
	return isRebootSuccess;
    }

    /**
     * Get device power status
     */
    @Override
    public String getPowerStatus() throws PowerProviderException {
	PowerStatusResponse statusResponse = null;
	String powerStatus = null;
	String url = CommonMethods.getNormalizedUrl(BASE_URL + POWER_STATUS_PATH);

	if (null != device) {
	    String macAddress = device.getHostMacAddress();
	    ResteasyClient client = getClient();
	    ResteasyWebTarget target = client.target(url);
	    DeviceRequest request = new DeviceRequest();
	    request.setMac(macAddress);
	    LOGGER.info("Sending request to get power status on device {} : {}", macAddress, url);
	    Response response = target.request().post(Entity.entity(request, "application/json"));
	    if (null != response) {
		if (response.getStatus() == HttpStatus.SC_OK) {
		    String respData = response.readEntity(String.class);
		    if (null != respData && !respData.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
			    LOGGER.info("Power status response: {}", respData);
			    statusResponse = mapper.readValue(respData, PowerStatusResponse.class);
			} catch (JsonProcessingException e) {
			    LOGGER.error("Exception parsing json for device power status {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to get device power status");
			} catch (IOException e) {
			    LOGGER.error("Exception parsing json for device power status {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to get device power status");
			}
		    }
		} else {
		    LOGGER.info("Failed to get power status device {} ,status {}", request.getMac(),
			    response.getStatus());
		}
	    }

	    if (null != statusResponse && null != statusResponse.getStatus()) {
		powerStatus = statusResponse.getStatus().name();
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");
	}

	return powerStatus;

    }

    /**
     * Perform device power off
     */
    @Override
    public boolean powerOff() throws PowerProviderException {
	StatusResponse statusResponse = null;
	boolean isPowerOffSuccess = false;
	String url = CommonMethods.getNormalizedUrl(BASE_URL + POWER_OFF_PATH);

	if (null != device) {
	    String macAddress = device.getHostMacAddress();
	    ResteasyClient client = getClient();
	    ResteasyWebTarget target = client.target(url);
	    DeviceRequest request = new DeviceRequest();
	    request.setMac(macAddress);
	    LOGGER.info("Sending request to power off device {} : {}", macAddress, url);
	    Response response = target.request().post(Entity.entity(request, "application/json"));
	    if (null != response) {
		if (response.getStatus() == HttpStatus.SC_OK) {
		    String respData = response.readEntity(String.class);
		    if (null != respData && !respData.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
			    LOGGER.info("Power off response: {}", respData);
			    statusResponse = mapper.readValue(respData, StatusResponse.class);
			    if (StatusMessage.SUCCESS == statusResponse.getStatus()) {
				isPowerOffSuccess = true;
			    }
			} catch (JsonProcessingException e) {
			    LOGGER.error("Exception parsing json for device power off {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to power off");
			} catch (IOException e) {
			    LOGGER.error("Exception parsing json for device power off {} response via rest api",
				    request.getMac(), e);
			    throw new PowerProviderException("Failed to power off");
			}
		    }
		} else {
		    LOGGER.info("Failed to power off device {} ,status {}", request.getMac(), response.getStatus());
		}
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");
	}
	return isPowerOffSuccess;
    }

    /**
     * Gets rest easy client instance
     * 
     * @return ResteasyClient
     */
    private ResteasyClient getClient() {
	ResteasyClient client = new ResteasyClientBuilder().build();
	return client;
    }

}
