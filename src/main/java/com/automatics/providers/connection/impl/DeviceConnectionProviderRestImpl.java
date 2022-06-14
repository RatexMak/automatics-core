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
package com.automatics.providers.connection.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.providers.connection.AbstractDeviceConnectionProvider;
import com.automatics.providers.connection.Connection;
import com.automatics.providers.connection.DeviceConsoleType;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.providers.objects.DeviceConnCopyFileRequest;
import com.automatics.providers.objects.DeviceConnProviderResponse;
import com.automatics.providers.objects.DeviceInfo;
import com.automatics.providers.objects.ExecuteCommandRequest;
import com.automatics.resource.IServer;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Shameem M Shereef
 *
 */
public class DeviceConnectionProviderRestImpl extends AbstractDeviceConnectionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConnectionProviderRestImpl.class);

    private String BASE_URL = "";

    private static String COPY_FILE_PATH = "/deviceConnection/copyfile";
    private static String EXEC_DEVICE_COMMAND_PATH = "/deviceConnection/execute";

    public DeviceConnectionProviderRestImpl() {
	BASE_URL = TestUtils.getDeviceConnectionProviderUrl();
    }

    /**
     * 
     */
    private DeviceConnProviderResponse copyFile(String macAddress, String ipAddress, String fileToCopy,
	    String remoteLocation) {
	DeviceConnProviderResponse deviceResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + COPY_FILE_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Fetching device details for {}  Url Path: {}", macAddress, url);

	DeviceConnCopyFileRequest deviceConnCopyFileRequest = new DeviceConnCopyFileRequest();
	deviceConnCopyFileRequest.setDestFilePath(remoteLocation);
	deviceConnCopyFileRequest.setFilePath(fileToCopy);
	deviceConnCopyFileRequest.setDeviceConnectionRequest(setDeviceConnParams(macAddress, ipAddress));

	Response response = target.request().post(Entity.entity(deviceConnCopyFileRequest, "application/json"));

	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		LOGGER.info("Response: {}", respData);

		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			deviceResponse = mapper.readValue(respData, DeviceConnProviderResponse.class);
			if (deviceResponse.getData() != null) {
			    LOGGER.info("Failed to execute command to device {} : error: {}", macAddress,
				    deviceResponse.getErrorString());
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json data for device {}",
				deviceConnCopyFileRequest.getDeviceConnectionRequest().getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json data for device {}",
				deviceConnCopyFileRequest.getDeviceConnectionRequest().getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to get copy file to device {} : Status: {}",
			deviceConnCopyFileRequest.getDeviceConnectionRequest().getMac(), response.getStatus());
	    }
	}

	return deviceResponse;
    }

    /**
     * 
     */
    private DeviceConnProviderResponse execute(String macAddress, String ipAddress, String executeCommandType,
	    long timeOutMilliSecs, List<String> commandList, String[] options, String consoleType) {
	DeviceConnProviderResponse deviceResponse = null;
	ResteasyClient client = getClient();
	String url = BASE_URL + EXEC_DEVICE_COMMAND_PATH;
	ResteasyWebTarget target = client.target(url);
	LOGGER.info("Fetching device details for {}  Url Path: {}", macAddress, url);

	ExecuteCommandRequest execCommandReq = new ExecuteCommandRequest();
	execCommandReq.setDeviceConnectionRequest(setDeviceConnParams(macAddress, ipAddress));
	execCommandReq.setCommandList(commandList);
	execCommandReq.setConsoleType(consoleType);
	execCommandReq.setExcecuteCommandType(executeCommandType);
	execCommandReq.setOptions(options);
	execCommandReq.setTimeOutMilliSecs(timeOutMilliSecs);

	Response response = target.request().post(Entity.entity(execCommandReq, "application/json"));

	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);
		LOGGER.info("Response: {}", respData);

		if (null != respData && !respData.isEmpty()) {
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			deviceResponse = mapper.readValue(respData, DeviceConnProviderResponse.class);
			if (deviceResponse.getData() != null) {
			    LOGGER.info("Failed to execute command to device {} : error: {}",
				    execCommandReq.getDeviceConnectionRequest().getMac(),
				    deviceResponse.getErrorString());
			}
		    } catch (JsonProcessingException e) {
			LOGGER.error("Exception parsing json data for device {}",
				execCommandReq.getDeviceConnectionRequest().getMac(), e);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json data for device {}",
				execCommandReq.getDeviceConnectionRequest().getMac(), e);
		    }
		}
	    } else {
		LOGGER.info("Failed to execute command to device {} : Status: {}",
			execCommandReq.getDeviceConnectionRequest().getMac(), response.getStatus());
	    }
	}

	return deviceResponse;
    }

    @Override
    public String executeInsideAtomConsoleUsingExpect(Dut dut, String atomServerIp, String command) {
	return null;
    }

    @Override
    public String execute(IServer hostDetails, List<String> commands, long timeOutMilliSecs) {
	return null;
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
     * The below object will hold the info required to connect to a particular device like MAC and IP Address
     * 
     * @param macAddress
     * @param ipAddress
     * @return
     */
    private DeviceInfo setDeviceConnParams(String macAddress, String ipAddress) {
	DeviceInfo deviceConnReq = new DeviceInfo();
	deviceConnReq.setIpAddress(ipAddress);
	deviceConnReq.setMac(macAddress);
	return deviceConnReq;
    }

    @Override
    public boolean copyFile(Device device, String fileToCopy, String remoteLocation) {
	DeviceConnProviderResponse response = copyFile(device.getHostMacAddress(), device.getHostIpAddress(),
		fileToCopy, remoteLocation);
	boolean isFileCopied = false;
	if (response != null && response.getData() != null)
	    isFileCopied = true;

	return isFileCopied;
    }

    @Override
    public Connection getConnection(Device device) {
	return null;
    }

    @Override
    public String execute(Device device, String command) {
	List<String> commandList = new ArrayList<String>();
	commandList.add(command);
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(), null, 30000,
		commandList, null, null);
	return response.getData();
    }

    @Override
    public String execute(Device device, List<String> commandList) {
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(), null, 30000,
		commandList, null, null);
	return response.getData();
    }

    @Override
    public String execute(Device device, ExecuteCommandType executeCommandType, List<String> commandList) {
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(),
		executeCommandType.toString(), 30000, commandList, null, null);
	return response.getData();
    }

    @Override
    public String execute(Dut dut, String command, String expectStr, String[] options) {
	List<String> commandList = new ArrayList<String>();
	commandList.add(command);
	DeviceConnProviderResponse response = execute(dut.getHostMacAddress(), dut.getHostIpAddress(), null, 30000,
		commandList, options, null);
	return response.getData();
    }

    @Override
    public String execute(Device device, String command, DeviceConsoleType consoleType, long timeOutMilliSecs) {
	List<String> commandList = new ArrayList<String>();
	commandList.add(command);
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(), null,
		timeOutMilliSecs, commandList, null, consoleType.toString());
	return response.getData();
    }

    @Override
    public String execute(Device device, List<String> commandList, DeviceConsoleType consoleType) {
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(), null, 30000,
		commandList, null, consoleType.toString());
	return response.getData();
    }

    @Override
    public String execute(Device device, List<String> commandList, DeviceConsoleType consoleType,
	    long timeOutMilliSecs) {
	DeviceConnProviderResponse response = execute(device.getEcmMac(), device.getEcmIpAddress(), null, timeOutMilliSecs,
		commandList, null, consoleType.toString());
	return response.getData();
    }

    @Override
    public String execute(Device device, Connection deviceConnnection, String command) {
	return null;
    }

    @Override
    public String execute(Device device, Connection deviceConnnection, ExecuteCommandType executeCommandType,
	    String command) {
	return null;
    }

    @Override
    public String execute(String hostIp, String command, long timeOutMilliSecs, String connectionType) {
	return null;
    }
}
