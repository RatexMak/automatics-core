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
import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.providers.objects.DeviceRequest;
import com.automatics.providers.objects.PowerStatus;
import com.automatics.providers.objects.PowerStatusResponse;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.rack.AbstractPowerProvider;
import com.automatics.providers.rack.exceptions.PowerProviderException;
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

    @Override
    public URI getPowerLocator() {
	return null;
    }

    @Override
    public void powerOn() throws PowerProviderException {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + POWER_ON_PATH;
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	DeviceRequest request = new DeviceRequest();
	request.setMac(macAddress);
	LOGGER.info("Power on device {}", macAddress);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device power on {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device power on {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to power on device {} ,status {}", request.getMac(), response.getStatus());
	    }
	}
    }

    @Override
    public void reboot() throws PowerProviderException {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + POWER_CYCLE;
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	DeviceRequest request = new DeviceRequest();
	request.setMac(macAddress);
	LOGGER.info("Power reboot of device {}", macAddress);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device power reboot {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device power reboot {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to power reboot device {} ,status {}", request.getMac(), response.getStatus());
	    }
	}

    }

    @Override
    public String getPowerStatus() {
	PowerStatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + POWER_STATUS_PATH;
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	DeviceRequest request = new DeviceRequest();
	request.setMac(macAddress);
	LOGGER.info("Power status of device {}", macAddress);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, PowerStatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device power status {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device power status {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to get power status device {} ,status {}", request.getMac(), response.getStatus());
	    }
	}

	if (null != statusResponse) {
	    return statusResponse.getStatus().name();
	} else {
	    return PowerStatus.OFF.name();
	}

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

    @Override
    public void powerOff() throws PowerProviderException {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + POWER_OFF_PATH;
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	DeviceRequest request = new DeviceRequest();
	request.setMac(macAddress);
	LOGGER.info("Power off device {}", macAddress);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device power off {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device power off {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to power off device {} ,status {}", request.getMac(), response.getStatus());
	    }

	}
    }

}
