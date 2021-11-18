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
package com.automatics.providers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.providers.objects.DeviceAccountRequest;
import com.automatics.providers.objects.DeviceAccountResponse;
import com.automatics.providers.objects.DeviceAllocationResponse;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.providers.objects.DevicePropsResponse;
import com.automatics.providers.objects.DeviceRequest;
import com.automatics.providers.objects.DeviceResponse;
import com.automatics.providers.objects.DeviceUpdateDurationRequest;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.rack.DeviceProvider;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Rest handler for device management
 * 
 * @author Radhika
 *
 */
public class DeviceManagerRestImpl implements DeviceProvider {
    private String BASE_URL = "";

    private static String GET_DEVICE_PATH = "/deviceManagement/getDeviceDetails";
    private static String GET_DEVICE_PROPS_PATH = "/deviceManagement/getDeviceProps";
    private static String GET_ACCOUNT_DETAILS_PATH = "/deviceManagement/getAccountDetails";
    private static String LOCK_DEVICE_PATH = "/deviceManagement/device/lock";
    private static String RELEASE_DEVICE_PATH = "/deviceManagement/device/releases";
    private static String LOCK_STATUS_PATH = "/deviceManagement/device/allocationStatus";
    private static String LOCK_UPDATE_PATH = "/deviceManagement/device/updateAllocationDuration";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagerRestImpl.class);

    public DeviceManagerRestImpl() {
	BASE_URL = TestUtils.getDeviceManagerUrl();
    }

    /**
     * Gets the device from rack.
     */
    @Override
    public DeviceResponse getDevice(DeviceRequest request) {
	DeviceResponse deviceResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + GET_DEVICE_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Fetching device details for", request.getMac(), " Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			deviceResponse = mapper.readValue(respData, DeviceResponse.class);

		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json data for device", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json data for device", request.getMac(),
				"response via rest api", e);
		    }
		}
	    }

	}
	return deviceResponse;
    }

    /**
     * Get device account details
     */
    @Override
    public DeviceAccountResponse getAccountDetailsForDevice(DeviceAccountRequest request) {
	DeviceAccountResponse accountResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + GET_ACCOUNT_DETAILS_PATH;
	ResteasyWebTarget target = client.target(url);

	LOGGER.info("Fetching account details for", request.getAccountNumber(), " Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			accountResponse = mapper.readValue(respData, DeviceAccountResponse.class);

		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json data for account", request.getAccountNumber(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json data for account  ", request.getAccountNumber(),
				"response via rest api", e);
		    }
		}
	    }

	}
	return accountResponse;
    }

    /**
     * Gets device properties
     */
    @Override
    public Map<String, String> getDeviceProperties(DevicePropsRequest request) {
	Map<String, String> result = new HashMap<String, String>();
	DevicePropsResponse deviceProps = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + GET_DEVICE_PROPS_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Fetching device props for", request.getMac(), "for props ", request.getRequestedPropsName(),
		" Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			deviceProps = mapper.readValue(respData, DevicePropsResponse.class);
			result.put("HEAD_END", deviceProps.getHeadEnd());
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json properties for device", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json properties for device", request.getMac(),
				"response via rest api", e);
		    }
		}
	    }

	}
	return result;
    }

    /**
     * Verifies if device is locked or not. Returns true if device is locked, otherwise false if available.
     */
    @Override
    public DeviceAllocationResponse isLocked(DeviceRequest request) {
	DeviceAllocationResponse allocResponse = null;

	ResteasyClient client = getClient();
	String url = BASE_URL + LOCK_STATUS_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Fetching lock status for device", request.getMac(), " Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			allocResponse = mapper.readValue(respData, DeviceAllocationResponse.class);
			LOGGER.info("DeviceConfig allocation status", request.getMac(), allocResponse.getAllocationStatus());
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device allocation", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device allocation", request.getMac(),
				"response via rest api", e);
		    }
		}
	    } else {
		LOGGER.info("Failed to get device allocation status", request.getMac(), response.getStatus());
	    }

	}
	return allocResponse;
    }

    @Override
    public StatusResponse lock(DeviceRequest request) {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + LOCK_DEVICE_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Locking device", request.getMac(), " Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device lock", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device lock", request.getMac(),
				"response via rest api", e);
		    }
		}
	    } else {
		LOGGER.info("Failed to lock device", request.getMac(), response.getStatus());
	    }

	}
	return statusResponse;
    }

    @Override
    public StatusResponse updateLockTime(DeviceUpdateDurationRequest request) {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + LOCK_UPDATE_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Updating lock duration for device", request.getMac(), " Url Path: ", url);

	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);

		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device lock", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device lock", request.getMac(),
				"response via rest api", e);
		    }
		}
	    } else {
		LOGGER.info("Failed to extend lock for device ", request.getMac(), response.getStatus(), "");
	    }
	}
	return statusResponse;
    }

    @Override
    public StatusResponse release(DeviceRequest request) {
	StatusResponse statusResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + RELEASE_DEVICE_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Releasing device", request.getMac(), " Url Path: ", url);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device release", request.getMac(),
				"response via rest api", e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device release", request.getMac(),
				"response via rest api", e);
		    }
		}
	    } else {
		LOGGER.info("Failed to release device", request.getMac(), response.getStatus());
	    }

	}
	return statusResponse;
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
