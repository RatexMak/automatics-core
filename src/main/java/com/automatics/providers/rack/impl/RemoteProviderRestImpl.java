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

import com.automatics.enums.RemoteControlType;
import com.automatics.providers.objects.RemoteProviderRequest;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.rack.AbstractRemoteProvider;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * RemoteProviderRestImpl class provides the implementations for different remote operations
 */
public class RemoteProviderRestImpl extends AbstractRemoteProvider {

    private static String BASE_URL;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteProviderRestImpl.class);

    private static String REMOTE_PRESS_KEY_PATH = "/remoteManagement/device/pressKey";
    private static String REMOTE_PRESS_KEY_AND_HOLD_PATH = "/remoteManagement/device/pressKeyAndHold";
    private static String REMOTE_SEND_TEXT_PATH = "/remoteManagement/device/sendText";
    private static String REMOTE_TUNE_PATH = "/remoteManagement/device/tune";

    public RemoteProviderRestImpl() {
	BASE_URL = TestUtils.getRackBaseUrl();
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

    /**
     * Does the presskey operations based on the command
     * 
     * @param string
     * @param enum
     * @return boolean
     */
    @Override
    public boolean pressKey(String command, RemoteControlType type) {
	StatusResponse statusResponse = null;
	boolean result = false;
	ResteasyClient client = getClient();
	String url = CommonMethods.getNormalizedUrl(BASE_URL + REMOTE_PRESS_KEY_PATH);
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	RemoteProviderRequest request = new RemoteProviderRequest();
	request.setMac(macAddress);
	request.setKeySet(device.getRemoteType());
	request.setCommand(command);
	request.setRemoteControlType(type);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
			if (statusResponse != null && statusResponse.getStatus().name() != null
				&& statusResponse.getStatus().name().equals("SUCCESS")) {
			    result = true;
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device press Key {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device press Key {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to do press Key {} ,status {}", request.getMac(), response.getStatus());
	    }
	}
	return result;
    }

    /**
     * Does the pressKeyAndHold operations based on the command
     * 
     * @param string
     * @param integer
     * @param enum
     * @return boolean
     */
    @Override
    public boolean pressKeyAndHold(String command, Integer count, RemoteControlType type) {
	StatusResponse statusResponse = null;
	boolean result = false;
	ResteasyClient client = getClient();
	String url = CommonMethods.getNormalizedUrl(BASE_URL + REMOTE_PRESS_KEY_AND_HOLD_PATH);
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	RemoteProviderRequest request = new RemoteProviderRequest();
	request.setMac(macAddress);
	request.setKeySet(device.getRemoteType());
	request.setCommand(command);
	request.setDelayInMilliSec(count);
	request.setRemoteControlType(type);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
			if (statusResponse != null && statusResponse.getStatus().name() != null
				&& statusResponse.getStatus().name().equals("SUCCESS")) {
			    result = true;
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device press Key and hold{} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device press Key and hold {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to do press Key and hold {} ,status {}", request.getMac(), response.getStatus());
	    }
	}
	return result;
    }

    /**
     * Does the sendText operations based on the string
     * 
     * @param string
     * @param enum
     * @return boolean
     */
    @Override
    public boolean sendText(String paramString, RemoteControlType type) {
	StatusResponse statusResponse = null;
	boolean result = false;
	ResteasyClient client = getClient();
	String url = CommonMethods.getNormalizedUrl(BASE_URL + REMOTE_SEND_TEXT_PATH);
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	RemoteProviderRequest request = new RemoteProviderRequest();
	request.setMac(macAddress);
	request.setKeySet(device.getRemoteType());
	request.setTextMsg(paramString);
	request.setRemoteControlType(type);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
			if (statusResponse != null && statusResponse.getStatus().name() != null
				&& statusResponse.getStatus().name().equals("SUCCESS")) {
			    result = true;
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device send Text {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device send Text {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to do send Text {} ,status {}", request.getMac(), response.getStatus());
	    }
	}
	return result;
    }

    /**
     * Does the tune operations based on the params
     * 
     * @param string
     * @param integer
     * @param boolean
     * @param enum
     * @return boolean
     */
    @Override
    public boolean tune(String channelNo, Integer delay, boolean isAutotune, RemoteControlType type) {
	StatusResponse statusResponse = null;
	boolean result = false;
	ResteasyClient client = getClient();
	String url = CommonMethods.getNormalizedUrl(BASE_URL + REMOTE_TUNE_PATH);
	String macAddress = device.getHostMacAddress();
	ResteasyWebTarget target = client.target(url);
	RemoteProviderRequest request = new RemoteProviderRequest();
	request.setMac(macAddress);
	request.setKeySet(device.getRemoteType());
	request.setChannelNo(channelNo);
	request.setAutoTune(false);
	request.setDelayInMilliSec(delay);
	request.setRemoteControlType(type);
	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			statusResponse = mapper.readValue(respData, StatusResponse.class);
			if (statusResponse != null && statusResponse.getStatus().name() != null
				&& statusResponse.getStatus().name().equals("SUCCESS")) {
			    result = true;
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json for device tune {} response via rest api",
				request.getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device tune {} response via rest api",
				request.getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to do tune {} ,status {}", request.getMac(), response.getStatus());
	    }
	}
	return result;
    }

}
