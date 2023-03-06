/**
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.automatics.executor;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.dataobjects.TestSessionDO;
import com.automatics.device.ConnectedDeviceInfo;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.providers.objects.DeviceObject;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PythonScriptExecutor {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonScriptExecutor.class);

    /**
     * This method will execute the python test case
     * created either in python or using pytest
     *
     * @param lockedDevices
     * @param isTcPyTest
     * @throws IOException
     * @throws InterruptedException
     */
    public void executePythonScriptUsingProcess(boolean isTcPyTest, List<Dut> lockedDevices)
	    throws IOException, InterruptedException, JSONException {
	try {

	    for (final Dut device : lockedDevices) {

		String deviceJson = convertDeviceToJson((Device) device);
		LOGGER.info("Going to execute python script for the device {}",
			 device.getHostMacAddress());
		/**
		 * In some VM's the python command will be different. It can either be python or python3
		 * or a different command all together. So to bring flexibility this command is made
		 * configurable in automatics properties under the property "Python.exec.command".
		 * Also the wrapper python script name is also made configurable in automatics peops
		 * under the property "Python.wrapper.script"
		 */
		String pyCommandString = AutomaticsPropertyUtility.getProperty("Python.exec.command");
		String pyWrapperScript = AutomaticsPropertyUtility.getProperty("Python.wrapper.script");
		String rdkbExecJarname = AutomaticsPropertyUtility.getProperty("Python.rdkb.exec.jar.name");
		String rdkvExecJarname = AutomaticsPropertyUtility.getProperty("Python.rdkv.exec.jar.name");
		String pythonScriptCheckoutFolder = AutomaticsPropertyUtility.getProperty("Python.script.checkout.folder.name");

		JSONObject jsonObject = new JSONObject();
				jsonObject.put("automaticsPropsUrl",System.getProperty("automatics.properties.file"));
		jsonObject.put("isPyTestTc", Boolean.toString(isTcPyTest));
		if("RDKB".equalsIgnoreCase(AutomaticsTapApi.getInstance().getCurrentExecutionMode())){
		    jsonObject.put("execJarPath", System.getProperty(
			    ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR + "target/" + rdkbExecJarname);
		} else if("RDKV".equalsIgnoreCase(AutomaticsTapApi.getInstance().getCurrentExecutionMode())){
		    jsonObject.put("execJarPath", System.getProperty(
			    ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR + "target/" + rdkvExecJarname);
		}
		jsonObject.put("filterTestCaseIds",System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_ID));
		jsonObject.put("pythonTestScriptDir",System.getProperty(
			ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR + pythonScriptCheckoutFolder + "/**");
		LOGGER.info("Sending JSON String {} to python wrapper script : --->{}",jsonObject.toString(), pyWrapperScript);
		ProcessBuilder processBuilder = new ProcessBuilder(pyCommandString, "-m", pyWrapperScript, jsonObject.toString(), deviceJson);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		BufferedReader testScriptOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String testScriptLogs = null;
		LOGGER.info("Response from python script : ");
		while ((testScriptLogs = testScriptOut.readLine())!=null){
		    LOGGER.info(testScriptLogs);
		}
		BufferedReader testScriptError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String testScriptErrorLogs = null;
		while ((testScriptErrorLogs = testScriptError.readLine())!=null){
		    LOGGER.error("Error from python script : " + testScriptErrorLogs);
		}

		int exitCode = process.waitFor();
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage());
	    throw e;
	}
    }

    /**
     *
     * @param device
     * @return
     * @throws JsonProcessingException
     */
    private String convertDeviceToJson(Device device) throws JsonProcessingException {
	DeviceObject deviceObject = new DeviceObject();
	deviceObject.setId(device.getId());
	deviceObject.setName(device.getName());
	deviceObject.setHardwareRevision(device.getHardwareRevision());

	deviceObject.setHostMacAddress(device.getHostMacAddress());
	deviceObject.setHostIp4Address(device.getHostIp4Address());
	deviceObject.setHostIp6Address(device.getHostIp6Address());
	//deviceObject.setHostIpAddress(device.getHostIp4Address());
	deviceObject.setClientIpAddress(device.getClientIpAddress());
	deviceObject.setModel(device.getModel());
	deviceObject.setManufacturer(device.getManufacturer());
	deviceObject.setSerialNumber(device.getSerialNumber());
	deviceObject.setRemoteType(device.getRemoteType());
	deviceObject.setEcmIpAddress(device.getEcmIpAddress());
	//deviceObject.setErouterIpAddress(device.getEstbIpAdress());
	// Gateway mac address separated by comma
	//deviceObject.setGateWaySettopMacAddress(device.getGatewayMac());
	deviceObject.setRackName(device.getRackName());
	deviceObject.setSlotName(device.getSlotName());
	deviceObject.setSlotNumber(device.getSlotNumber());
	deviceObject.setRackServerHost(device.getRackServerHost());
	deviceObject.setRackServerPort(device.getRackServerPort());
	deviceObject.setHomeAccountGroupName(device.getHomeAccountGroupName());
	deviceObject.setHomeAccountName(device.getHomeAccountName());
	deviceObject.setHomeAccountNumber(device.getHomeAccountNumber());
	//deviceObject.setConnectedGateWaySettopMacs(device.getGatewayMac());

	deviceObject.setDefaultRemoteControlType(device.getDefaultRemoteControlType());
	deviceObject.setRemoteControlTypes(device.getRemoteControlTypes());
	// Set device head end
	//deviceObject.setHeadEnd(device.getHeadend());

	deviceObject.setExtraProperties(device.getExtraProperties());

	ObjectMapper objectMapper = new ObjectMapper();
	String deviceJson = objectMapper.writeValueAsString(deviceObject);

	return deviceJson;
    }

    /**
     *
     * @param deviceJson
     * @return
     * @throws IOException
     */
    public Device convertJsonToDevice(String deviceJson) throws IOException {
	ObjectMapper mapper = new ObjectMapper();
	DeviceObject deviceObject = mapper.readValue(deviceJson, DeviceObject.class);

	return convertJsonStringToDevice(deviceObject);
    }

    /**
     * Converts device response to Device object.
     *
     * @param deviceResponse
     *            DeviceResponse received from rest
     * @return Device instance
     */
    private Device convertJsonStringToDevice(DeviceObject deviceResponse) {
	Device device = new Device();
	TestSessionDO testSessionDetails = new TestSessionDO();
	testSessionDetails.setTestCaseTobeExecuted(deviceResponse.getId());
	device.setId(deviceResponse.getId());
	device.setName(deviceResponse.getName());
	device.setHardwareRevision(deviceResponse.getHardwareRevision());

	device.setHostMacAddress(deviceResponse.getHostMacAddress());
	device.setHostIp4Address(deviceResponse.getHostIp4Address());
	device.setHostIp6Address(deviceResponse.getHostIp6Address());
	device.setHostIpAddress(deviceResponse.getHostIp4Address());
	device.setClientIpAddress(deviceResponse.getClientIpAddress());
	device.setModel(deviceResponse.getModel());
	device.setManufacturer(deviceResponse.getManufacturer());
	device.setSerialNumber(deviceResponse.getSerialNumber());
	device.setRemoteType(deviceResponse.getRemoteType());
	device.setEcmIpAddress(deviceResponse.getEcmIpAddress());
	device.setErouterIpAddress(deviceResponse.getEstbIpAdress());
	// Gateway mac address separated by comma
	device.setGateWaySettopMacAddress(deviceResponse.getGatewayMac());
	device.setRackName(deviceResponse.getRackName());
	device.setSlotName(deviceResponse.getSlotName());
	device.setSlotNumber(deviceResponse.getSlotNumber());
	device.setRackServerHost(deviceResponse.getRackServerHost());
	device.setRackServerPort(deviceResponse.getRackServerPort());
	device.setHomeAccountGroupName(deviceResponse.getHomeAccountGroupName());
	device.setHomeAccountName(deviceResponse.getHomeAccountName());
	device.setHomeAccountNumber(deviceResponse.getHomeAccountNumber());
	device.setConnectedGateWaySettopMacs(deviceResponse.getGatewayMac());

	device.setDefaultRemoteControlType(deviceResponse.getDefaultRemoteControlType());
	device.setRemoteControlTypes(deviceResponse.getRemoteControlTypes());
	// Set device head end
	device.setHeadEnd(deviceResponse.getHeadend());

	device.setExtraProperties(deviceResponse.getExtraProperties());
	Map<String, String> extraProps = device.getExtraProperties();
	if (null != extraProps) {

	    // Setting extra props from partner
	    device.setExtraProperties(extraProps);

	    // Setting login credentials for non-rdk devices
	    device.setUsername(extraProps.get("username"));
	    device.setPassword(extraProps.get("password"));

	    // Get connected device info
	    ConnectedDeviceInfo connectedDeviceInfo = new ConnectedDeviceInfo();
	    connectedDeviceInfo.setConnectionType(extraProps.get("connectionType"));
	    connectedDeviceInfo.setWifiCapability(extraProps.get("wifiCapability"));
	    connectedDeviceInfo.setWifiMacAddress(extraProps.get("wifiMacAddress"));
	    connectedDeviceInfo.setDevicePortAddress(extraProps.get("devicePort"));
	    connectedDeviceInfo.setDeviceIpAddress(extraProps.get("deviceIp"));
	    connectedDeviceInfo.setOsType(extraProps.get("osType"));
	    device.setNodePort(extraProps.get("nodePort"));
	    device.setNatAddress(extraProps.get("deviceIp"));
	    device.setNatPort(extraProps.get("devicePort"));
	    device.setOsType(extraProps.get("osType"));
	    connectedDeviceInfo.setUserName(extraProps.get("username"));
	    connectedDeviceInfo.setPassword(extraProps.get("password"));
	    connectedDeviceInfo.setEthernetMacAddress(extraProps.get("ethernetMacAddress"));
	    device.setConnectedDeviceInfo(connectedDeviceInfo);
	}
	if(device.getNatAddress() == null)
	    device.setNatAddress(deviceResponse.getHostIp4Address());
	if(device.getNatPort() == null)
	    device.setNatPort("22");
	device.setTestSessionDetails(testSessionDetails);
	return device;
    }

    /**
     *
     * @param dut
     * @param command
     * @return
     */
    public String executeCommandUsingSsh(Device dut, String command){
	return AutomaticsTapApi.getInstance().executeCommandUsingSsh(dut,command);
    }
}
