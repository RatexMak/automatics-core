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
package com.automatics.device.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.restclient.RestClient;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;
import com.automatics.restclient.RestClientException;
import com.automatics.restclient.RestEasyClientImpl;
import com.automatics.restclient.RestRequest;
import com.automatics.restclient.RestResponse;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeviceConfigModelUtils {

    private DeviceModels deviceModels = null;

    private static DeviceConfigModelUtils deviceConfigModelUtils = null;

    private static Object lock = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConfigModelUtils.class);

    /**
     * Gets singleton instance of device config data
     * 
     * @return DeviceConfigModelUtils
     */
    public static DeviceConfigModelUtils getInstance() {
	if (deviceConfigModelUtils == null) {
	    synchronized (lock) {
		if (deviceConfigModelUtils == null) {
		    deviceConfigModelUtils = new DeviceConfigModelUtils();
		}
	    }
	}
	return deviceConfigModelUtils;
    }

    /**
     * Initialize device config
     */
    private DeviceConfigModelUtils() {

	String devices = readDeviceConfigFile();
	ObjectMapper mapper = new ObjectMapper();
	try {
	    deviceModels = mapper.readValue(devices, DeviceModels.class);
	} catch (JsonParseException e) {
	    LOGGER.error("Error parsing device configuration: {}", e.getMessage(), e);
	} catch (JsonMappingException e) {
	    LOGGER.error("Error parsing device configuration: {}", e.getMessage(), e);
	} catch (IOException e) {
	    LOGGER.error("Error reading device configuration: {}", e.getMessage(), e);
	}
    }

    /**
     * Send request for device configuration
     * 
     * @return device config in string format
     */
    public static String readDeviceConfigFile() {
	String url = AutomaticsPropertyUtility.getProperty(AutomaticsConstants.PROPERTY_DEVICE_CONFIG);

	LOGGER.info("INIT- Requesting device config from {}", url);

	String responseData = null;
	Map<String, String> headers = new HashMap<String, String>();
	headers.put("content-type", "application/json");

	RestRequest request = new RestRequest(url, HttpRequestMethod.GET, headers);
	RestClient restClient = new RestEasyClientImpl();
	try {
	    RestResponse response = restClient.executeAndGetResponse(request);
	    responseData = response.getResponseBody();
	} catch (RestClientException e) {
	    LOGGER.error("Error while fetching device config", e);
	}

	return responseData;
    }

    /**
     * Gets all device models
     * 
     * @return
     */
    public DeviceModels fetchDeviceModels() {
	return deviceModels;
    }

    public void setDeviceModels(DeviceModels deviceModels) {
	this.deviceModels = deviceModels;
    }
}
