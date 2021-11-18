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
package com.automatics.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.TestSessionDO;
import com.automatics.enums.AutomaticsBuildType;
import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.enums.ExecuteOnType;
import com.automatics.enums.ExecutionMode;
import com.automatics.enums.RemoteControlType;
import com.automatics.enums.SshMechanism;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.connection.Connection;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.CommonMethods;

public class Device extends DutImpl {

    /** The logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Device.class);

    /** The IP address of client dut. */
    String clientIpAddress = null;

    /** The comma separated MAC address of all gateway dut connected to this device. */
    String connectedGateWaySettopMacs = null;

    /** The MAC address of gateway dut. */
    String gateWaySettopMacAddress = null;

    /** The gateway dut. */
    Dut gateWaySettop = null;

    /** DeviceConfig Id of the box. */
    String deviceId = null;

    String deviceExecutionLogUrl;

    /** Home account name */
    String homeAccountName;

    /** Home account number */
    String homeAccountNumber;

    /** Home account group name */
    String homeAccountGroupName;

    /** Rack server host */
    String rackServerHost;

    /** Rack server port */
    Integer rackServerPort;

    /** serviceAccountId of the box. */
    String serviceAccountId = null;

    /** Billing Account ID of the box. */
    String billingAccountId = null;

    /** Native Process Id of the box. */
    String nativeProcessId = null;

    /** Automation id of currently running test case. */
    String automationTestId = null;

    /** Test type of currently running test cases. */
    AutomaticsTestTypes testTypes = null;

    /** Test description of currently running test cases. */
    String testDescription = null;

    /** Firmware version during initialization. */
    String firmwareVersion = null;

    /** HDMI connected status. */
    boolean isHdmiConnected = false;

    /** Build type. */
    private AutomaticsBuildType buildType = null;

    /** Execute on type. */
    private ExecuteOnType executeOn = null;

    /** Guide version. */
    String guideVersion = null;

    /** List of Gateway Settops **/
    private ArrayList<Dut> gatewaySettops = null;

    /** List of connected devices **/
    private List<Dut> connectedDevices = null;

    /** Execution mode whether SP or IPLINEAR_OR_GRAM */
    private ExecutionMode executionMode = ExecutionMode.UNKNOWN;

    /** Ecm Mac of the box. */
    String ecmMac = null;

    /** HeadEnd of the box. */
    String headEnd = null;

    /** path to save images during execution */
    String imageSaveLocation = null;

    /** Username to connect to device */
    String username = null;

    /** Password to connect to device */
    String password = null;

    /** ecmIP Adress of the deviceS */
    String ecmIpAddress = null;

    /** natPort of the deviceS */
    String natPort = null;

    /** natAddress of the deviceS */
    String natAddress = null;

    /** Holds OS type of natted deviceS */
    String osType = null;

    /** erouter Ip Adress of the deviceS */
    String erouterIpAddress = null;

    /**
     * Holds the details of connected device
     */
    ConnectedDeviceInfo connectedDeviceInfo = null;

    /**
     * Holds the mtaMacAddress of device
     */
    String mtaMacAddress = null;

    /**
     * Holds the mtaIpAddress of device
     */
    String mtaIpAddress = null;
    /** Holds Selenium node natted port type of connected deviceS */
    String nodePort = null;

    boolean isReverseSsh = false;

    boolean isReverseAccess = false;

    String stbitBasicInfoUrl = null;

    private String estbIpAdress;

    SshMechanism sshmechanism;

    String accessMechanism;

    String deviceCategory;

    TestSessionDO testSessionDetails = null;

    // DefaultRemoteControlType
    private RemoteControlType defaultRemoteControlType;

    // Lists of RemoteControlType
    private List<RemoteControlType> remoteControlTypes;

    Map<String, Connection> persistentConnections;

    public SshMechanism getSshmechanism() {
	return sshmechanism;
    }

    public void setSshmechanism(SshMechanism sshmechanism) {
	this.sshmechanism = sshmechanism;
    }

    public String getAccessMechanism() {
	return accessMechanism;
    }

    public void setAccessMechanism(String accessMechanism) {
	this.accessMechanism = accessMechanism;
    }

    public boolean isSshMechanismGivenType(SshMechanism requiredType) {
	boolean istrue = false;
	if (requiredType.equals(this.getSshmechanism())) {
	    istrue = true;
	}
	return istrue;
    }

    /*
     * public boolean isAccessMechanismGivenType(AccessMechanism requiredType) { boolean istrue = false; if
     * (requiredType.equals(this.getAccessMechanism())) { istrue = true; } return istrue; }
     */

    public TestSessionDO getTestSessionDetails() {
	return testSessionDetails;
    }

    public void setTestSessionDetails(TestSessionDO testSessionDetails) {
	this.testSessionDetails = testSessionDetails;
    }

    public boolean isReverseAccess() {
	return isReverseAccess;
    }

    public void setReverseAccess(boolean isRevSsh) {
	this.isReverseAccess = isRevSsh;
	setAccessMechanism(SshMechanism.REVERSESSH.name());
    }

    public String getDeviceExecutionLogUrl() {
	return deviceExecutionLogUrl;
    }

    public void setDeviceExecutionLogUrl(String deviceExecutionLogUrl) {
	this.deviceExecutionLogUrl = deviceExecutionLogUrl;
    }

    public boolean isReverseSsh() {
	return isSshMechanismGivenType(SshMechanism.REVERSESSH);
    }

    public void setReverseSsh(boolean isReverseSsh) {
	this.isReverseSsh = isReverseSsh;
	setSshmechanism(SshMechanism.REVERSESSH);
    }

    /**
     * @param mtaMacAddress
     */
    public void setMtaMacAddress(String mtaMacAddress) {
	this.mtaMacAddress = mtaMacAddress;
    }

    /**
     * @param mtaIpAddress
     */
    public void setMtaIpAddress(String mtaIpAddress) {
	this.mtaIpAddress = mtaIpAddress;
    }

    /**
     * @return getOsType
     */

    public String getOsType() {
	return osType;
    }

    /**
     * Method sets setOsType
     * 
     * @param setOsType
     */
    public void setOsType(String osType) {
	this.osType = osType;
    }

    /**
     * @return NatPort
     */
    public String getNatPort() {
	return natPort;
    }

    /**
     * Method sets natport
     * 
     * @param natPort
     */
    public void setNatPort(String natPort) {
	this.natPort = natPort;
    }

    /**
     * @return NatAddress
     */
    public String getNatAddress() {
	return natAddress;
    }

    /**
     * Method sets natAddress
     * 
     * @param natAddress
     */
    public void setNatAddress(String natAddress) {
	this.natAddress = natAddress;
    }

    /**
     * Get the EcmIpAddress of dut .
     *
     * @return EcmIpAddress of dut.
     */

    public String getEcmIpAddress() {
	return ecmIpAddress;
    }

    public void setEcmIpAddress(String ecmIpAddress) {
	this.ecmIpAddress = ecmIpAddress;
    }

    /**
     * Get the IP address of client dut .
     *
     * @return The IP address of client dut.
     */
    public String getClientIpAddress() {
	return clientIpAddress;
    }

    /**
     * Set the IP address of the client dut.
     *
     * @param clientIpAddress
     *            The IP address of client dut
     */
    public void setClientIpAddress(String clientIpAddress) {
	this.clientIpAddress = clientIpAddress;
    }

    /**
     * Get the MAC address gateway dut.
     *
     * @return The MAC address of gateway dut.
     */
    public String getGateWaySettopMacAddress() {
	return getGateWaySettop().getHostMacAddress();
    }

    /**
     * Set the MAC address of gate way dut.
     *
     * @param hostMacAddress
     *            The MAC address of gateway dut.
     */
    public void setGateWaySettopMacAddress(String hostMacAddress) {
	this.gateWaySettopMacAddress = hostMacAddress;
    }

    /**
     * Get gateway dut.
     *
     * @return The {@link Dut} gateway dut.
     */
    public Dut getGateWaySettop() {
	return gateWaySettop;
    }

    /**
     * Set the gate way dut.
     *
     * @param host
     *            The {@link Dut} gateway dut.
     */
    public void setGateWaySettop(Dut host) {
	this.gateWaySettop = host;
    }

    /**
     * Get the Gateway Settops List.
     * 
     * @return the gatewaySettops
     */
    public ArrayList<Dut> getGatewaySettops() {
	return gatewaySettops;
    }

    /**
     * Set the Gateway Settops List.
     * 
     * @param gatewaySettops
     *            the gatewaySettops to set
     */
    public void addGatewaySettops(Dut gatewaySettop) {
	LOGGER.info("Adding a GW Dut to the list: " + gatewaySettop.getHostMacAddress());
	if (null == this.gatewaySettops) {
	    this.gatewaySettops = new ArrayList<Dut>();
	}
	this.gatewaySettops.add(gatewaySettop);
	LOGGER.info("GW ArrayList Size = " + this.gatewaySettops.size());
    }

    /**
     * Get device type of particular dut box.
     *
     * @return The device type.
     */
    public AutomaticsBuildType getBuildType() {
	return buildType;
    }

    /**
     * Set the device type for particular dut box.
     *
     * @param buildType
     *            The device type.
     */
    public void setBuildType(AutomaticsBuildType buildType) {
	this.buildType = buildType;
    }

    /**
     * Get device type of particular dut box.
     *
     * @return The device type.
     */
    public ExecuteOnType getExecuteOn() {
	return executeOn;
    }

    /**
     * Set the device type for particular dut box.
     *
     * @param buildType
     *            The device type.
     */
    public void setExecuteOn(ExecuteOnType executeOn) {
	this.executeOn = executeOn;
    }

    /**
     * sets the nativeProcessId of the set-top.
     * 
     * @param nativeProcessId
     *            nativeProcessId of the box.
     */
    public void setNativeProcessId(String nativeProcessId) {

	AutomaticsTapApi.estbMacNativeProcessIdMap.put(getHostMacAddress(), nativeProcessId);
    }

    /**
     * Set the device Id of the box.
     *
     * @param deviceId
     *            the deviceId to set
     */
    public void setDeviceId(String deviceId) {
	AutomaticsTapApi.estbMacDeviceIdMap.put(getHostMacAddress(), deviceId);
    }

    /**
     * Provides the device Id of the box.
     *
     * @return DeviceConfig Id of the box
     */
    public String getDeviceId() {

	deviceId = AutomaticsTapApi.estbMacDeviceIdMap.get(getHostMacAddress());

	if (null == deviceId) {
	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add("deviceId");
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    deviceId = response.get("deviceId");
	}

	return deviceId;
    }

    /**
     * Provides the device Id of the box.
     *
     * @return DeviceConfig Id of the box
     */
    public String getDeviceIdForIpDevices() {

	deviceId = AutomaticsTapApi.estbMacDeviceIdMap.get(getHostMacAddress());

	if (null == deviceId) {

	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add("deviceIdForIpDevices");
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    deviceId = response.get("deviceIdForIpDevices");
	}

	return deviceId;
    }

    /**
     * Provides the device Id of the box.
     *
     * @return billing account Id of the box
     */
    public String getBillingAccountId() {

	if (null == billingAccountId) {
	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add("billingAccountId");
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    billingAccountId = response.get("billingAccountId");
	}

	return billingAccountId;
    }

    /**
     * Get the automation test id of currently running test case.
     *
     * @return the automationTestId
     */
    public String getAutomationTestId() {
	return automationTestId;
    }

    /**
     * Set the automation test id of currently running test case.
     *
     * @param automationTestId
     *            the automationTestId to set
     */
    public void setAutomationTestId(String automationTestId) {
	this.automationTestId = automationTestId;
    }

    /**
     * Get the test type of currently running test case.
     *
     * @return the testTypes
     */
    public AutomaticsTestTypes getTestTypes() {
	return testTypes;
    }

    /**
     * Set the test type of currently running test case.
     *
     * @param testTypes
     *            the testTypes to set
     */
    public void setTestTypes(AutomaticsTestTypes testTypes) {
	this.testTypes = testTypes;
    }

    /**
     * Get the test description of currently running test case.
     *
     * @return the testDescription
     */
    public String getTestDescription() {
	return testDescription;
    }

    /**
     * Set the test description of currently running test case.
     *
     * @param testDescription
     *            the testDescription to set
     */
    public void setTestDescription(String testDescription) {
	this.testDescription = testDescription;
    }

    /**
     * Get the firmware version of dut during test initialization.
     *
     * @return the firmwareVersion
     */
    public String getFirmwareVersion() {
	LOGGER.debug("Firmware version={}", firmwareVersion);
	if (CommonMethods.isNull(firmwareVersion) && !SupportedModelHandler.isNonRDKDevice(this)) {
	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add("FIRMWARE_VERSION");
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    LOGGER.info("Fetching Firmware version");
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    firmwareVersion = response.get("FIRMWARE_VERSION");
	    LOGGER.info("Firmware version {}", firmwareVersion);
	}
	return firmwareVersion;
    }

    /**
     * Set the firmware version of dut during test initialization.
     *
     * @param firmwareVersion
     *            the firmwareVersion to set
     */
    public void setFirmwareVersion(String firmwareVersion) {
	this.firmwareVersion = firmwareVersion;
    }

    /**
     * Get the current guide version.
     *
     * @return
     */
    public String getGuideVersion() {
	return guideVersion;
    }

    /**
     * Set the guide version of dut during test initialisation.
     *
     * @param guideVersion
     */
    public void setGuideVersion(String guideVersion) {
	this.guideVersion = guideVersion;
    }

    /**
     * Get the HDMI connection status.
     *
     * @return the isHdmiConnected True if HDMI is connected, otherwise false.
     */
    public boolean isHdmiConnected() {
	return isHdmiConnected;
    }

    /**
     * Set the HDMI connection status for particular dut box.
     *
     * @param isHdmiConnected
     *            the isHdmiConnected to set
     */
    public void setHdmiConnected(boolean isHdmiConnected) {
	this.isHdmiConnected = isHdmiConnected;
    }

    /**
     * Method to get current execution mode is whether SP or IPLINEAR_OR_GRAM
     * 
     * @return ExecutionMode
     */
    public ExecutionMode getExecutionMode() {
	return executionMode;
    }

    /**
     * Method to set current execution mode
     * 
     * @param executionMode
     *            ExecutionMode
     */
    public void setExecutionMode(ExecutionMode executionMode) {
	this.executionMode = executionMode;
    }

    /**
     * Method to provide the list of connected devices dut object
     * 
     * @return connectedDeviceList
     */
    public List<Dut> getConnectedDeviceList() {

	// Here we are reusing the existing variable
	return connectedDevices;
    }

    /**
     * @param connectedDevices
     *            the connectedDevices to set
     */
    public void setConnectedDevices(List<Dut> connectedDevices) {
	this.connectedDevices = connectedDevices;
    }

    /**
     * Method to add the connected device dut instance to the list
     * 
     * @param connectedDevice
     */
    public void addConnectedDeviceToList(Dut connectedDevice) {

	if (connectedDevices == null) {
	    connectedDevices = new ArrayList<Dut>();
	}

	connectedDevices.add(connectedDevice);
    }

    /**
     * Provision the device IP address. It can be either ip4 or ip6 address.
     * 
     * @return IP4 or IP6 address.
     * @see Dut#getHostIp4Address()
     * @see Dut#getHostIp6Address()
     */
    public String getHostIpAddress() {
	String hostIpAddress = super.getHostIp4Address();
	if (StringUtils.isBlank(hostIpAddress)) {
	    hostIpAddress = super.getHostIp6Address();
	}
	return hostIpAddress;
    }

    /**
     * Provides the ecm Mac of the box.
     *
     * @return ecm Mac of the box
     */
    public String getEcmMac() {
	if (null == ecmMac) {
	    // Getting ecmmac
	    ecmMac = AutomaticsTapApi.estbMacEcmMap.get(getHostMacAddress());
	    if (null == ecmMac) {
		DevicePropsRequest request = new DevicePropsRequest();
		request.setMac(getHostMacAddress());
		List<String> requestedPropsName = new ArrayList<String>();
		requestedPropsName.add("ECM_MAC");
		request.setRequestedPropsName(requestedPropsName);

		DeviceManager deviceManager = DeviceManager.getInstance();
		Map<String, String> response = deviceManager.getDeviceProperties(request);
		ecmMac = response.get("ECM_MAC");
	    }
	}
	return ecmMac;
    }

    /**
     * Provides the Head end of the box.
     *
     * @return headEnd of the box
     */
    public String getHeadEnd() {

	headEnd = AutomaticsTapApi.estbMacHeadEndMap.get(hostMacAddress);
	LOGGER.info("Head end for device {} {}", hostMacAddress, headEnd);
	if (null == headEnd) {
	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(hostMacAddress);
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add(AutomaticsConstants.DEVICE_PROP_HEAD_END);
	    request.setRequestedPropsName(requestedPropsName);
	    LOGGER.debug("Fetching head end for device {}", hostMacAddress);
	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    if (null != response && null != response.get(AutomaticsConstants.DEVICE_PROP_HEAD_END)) {
		headEnd = response.get(AutomaticsConstants.DEVICE_PROP_HEAD_END);
		LOGGER.info("Head end for device {} {}", hostMacAddress, headEnd);
		AutomaticsTapApi.estbMacHeadEndMap.put(hostMacAddress, headEnd);
	    } else {
		LOGGER.error("Could not get head end for device {}", hostMacAddress);
	    }
	}

	return headEnd;
    }

    public void setHeadEnd(String headEnd) {
	this.headEnd = headEnd;
	if (null != headEnd) {
	    AutomaticsTapApi.estbMacHeadEndMap.put(hostMacAddress, headEnd);
	}
    }

    /**
     * Method to get the location of Broadband web pages
     * 
     * @return Webpage location in jenkins
     */
    public String getImageSaveLocation() {
	return imageSaveLocation;
    }

    public void setImageSaveLocation(String imageRedirectionFolder) {
	imageSaveLocation = imageRedirectionFolder;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * @return true for osType = Windows
     */
    public boolean isWindows() {
	boolean isWindows = false;
	if (this.osType != null
		&& this.osType.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_WINDOWS))) {
	    isWindows = true;
	}
	return isWindows;
    }

    /**
     * @return true for osType = Ios
     */
    public boolean isIoS() {
	boolean isIoS = false;
	if (this != null && this.osType != null
		&& this.osType.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_IOS))) {
	    isIoS = true;
	}
	return isIoS;
    }

    /**
     * @return true for osType = Linux
     */
    public boolean isLinux() {
	boolean isLinux = false;
	if (this != null && this.osType != null
		&& this.osType.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_LINUX))) {
	    isLinux = true;
	}
	return isLinux;
    }

    /**
     * @return true for osType = Android
     */
    public boolean isAndroid() {
	boolean isAndroid = false;
	if (this != null && this.osType != null
		&& this.osType.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_ANDROID))) {
	    isAndroid = true;
	}
	return isAndroid;
    }

    public boolean isRaspbianLinux() {
	boolean isRaspbianLinux = false;
	if (this != null && this.osType != null && this.osType
		.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_RASPBIAN_LINUX))) {
	    isRaspbianLinux = true;
	}
	return isRaspbianLinux;
    }

    /**
     * @return the connectedDeviceInfo
     */
    public ConnectedDeviceInfo getConnectedDeviceInfo() {
	return connectedDeviceInfo;
    }

    /**
     * @param connectedDeviceInfo
     *            the connectedDeviceInfo to set
     */
    public void setConnectedDeviceInfo(ConnectedDeviceInfo connectedDeviceInfo) {
	this.connectedDeviceInfo = connectedDeviceInfo;
    }

    //
    /**
     * Get Erouter IP address.
     * 
     * @return the erouterIpAddress
     */
    public String getErouterIpAddress() {
	return erouterIpAddress;
    }

    /**
     * 
     * @param erouterIpAddress
     *            the erouterIpAddress to set
     */
    public void setErouterIpAddress(String erouterIpAddress) {
	this.erouterIpAddress = erouterIpAddress;
    }

    /**
     * Get Selenium Natted Port
     * 
     * @return
     */
    public String getNodePort() {
	return nodePort;
    }

    /**
     * set Selenium Natted Port
     * 
     * @param nodePort
     */
    public void setNodePort(String nodePort) {
	this.nodePort = nodePort;
    }

    /**
     * @return true for osType = MacOS
     */
    public boolean isMacOS() {
	boolean isMacOS = false;
	if (this != null && this.osType != null
		&& this.osType.equals(AutomaticsPropertyUtility.getProperty(AutomaticsConstants.OS_KEY_MAC))) {
	    isMacOS = true;
	}
	return isMacOS;
    }

    public String getServiceAccountId() {

	serviceAccountId = AutomaticsTapApi.estbMacServiceAccountIdMap.get(getHostMacAddress());

	if (null == serviceAccountId) {

	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add("serviceAccountId");
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    serviceAccountId = response.get("serviceAccountId");
	}

	return serviceAccountId;
    }

    /**
     * Provides the nativeProcessId of the set-top.
     *
     * @return nativeProcessId of the box.
     */
    public String getNativeProcessId() {

	nativeProcessId = AutomaticsTapApi.estbMacNativeProcessIdMap.get(getHostMacAddress());

	if (null == nativeProcessId) {
	    DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add(AutomaticsConstants.NATIVE_PROCESS_ID);
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    nativeProcessId = response.get(AutomaticsConstants.NATIVE_PROCESS_ID);
	}
	return nativeProcessId;
    }

    /**
     * @return the homeAccountName
     */
    public String getHomeAccountName() {
	return homeAccountName;
    }

    /**
     * @param homeAccountName
     *            the homeAccountName to set
     */
    public void setHomeAccountName(String homeAccountName) {
	this.homeAccountName = homeAccountName;
    }

    /**
     * @return the homeAccountNumber
     */
    public String getHomeAccountNumber() {
	return homeAccountNumber;
    }

    /**
     * @param homeAccountNumber
     *            the homeAccountNumber to set
     */
    public void setHomeAccountNumber(String homeAccountNumber) {
	this.homeAccountNumber = homeAccountNumber;
    }

    /**
     * @return the homeAccountGroupName
     */
    public String getHomeAccountGroupName() {
	return homeAccountGroupName;
    }

    /**
     * @param homeAccountGroupName
     *            the homeAccountGroupName to set
     */
    public void setHomeAccountGroupName(String homeAccountGroupName) {
	this.homeAccountGroupName = homeAccountGroupName;
    }

    /**
     * @return the rackServerHost
     */
    public String getRackServerHost() {
	return rackServerHost;
    }

    /**
     * @param rackServerHost
     *            the rackServerHost to set
     */
    public void setRackServerHost(String rackServerHost) {
	this.rackServerHost = rackServerHost;
    }

    /**
     * @return the rackServerPort
     */
    public Integer getRackServerPort() {
	return rackServerPort;
    }

    /**
     * @param rackServerPort
     *            the rackServerPort to set
     */
    public void setRackServerPort(Integer rackServerPort) {
	this.rackServerPort = rackServerPort;
    }

    /**
     * @return the persistentConnections
     */
    public Map<String, Connection> getPersistentConnections() {
	return persistentConnections;
    }

    /**
     * @param connection
     *            name
     * @return the persistentConnection
     */
    public Connection getPersistentConnection(String connectionName) {
	Connection connection = null;
	if (null != persistentConnections && null != persistentConnections.get(connectionName)) {
	    connection = persistentConnections.get(connectionName);
	}
	return connection;
    }

    /**
     * @param persistentConnections
     *            the persistentConnections to set
     */
    public void setPersistentConnections(Map<String, Connection> persistentConnections) {
	this.persistentConnections = persistentConnections;
    }

    /**
     * @param persistentConnections
     *            the persistentConnections to set
     */
    public void addPersistentConnections(String connectionName, Connection connection) {
	if (null == this.persistentConnections) {
	    this.persistentConnections = new HashMap<String, Connection>();
	}
	this.persistentConnections.put(connectionName, connection);
    }

    /**
     * @return the deviceCategory
     */
    public String getDeviceCategory() {
	return deviceCategory;
    }

    /**
     * @param deviceCategory
     *            the deviceCategory to set
     */
    public void setDeviceCategory(String deviceCategory) {
	this.deviceCategory = deviceCategory;
    }

    /**
     * @return the connectedGateWaySettopMacs
     */
    public String getConnectedGateWaySettopMacs() {
	return connectedGateWaySettopMacs;
    }

    /**
     * @param connectedGateWaySettopMacs
     *            the connectedGateWaySettopMacs to set
     */
    public void setConnectedGateWaySettopMacs(String connectedGateWaySettopMacs) {
	this.connectedGateWaySettopMacs = connectedGateWaySettopMacs;
    }

    /**
     * @return the defaultRemoteControlType
     */
    public RemoteControlType getDefaultRemoteControlType() {
	return defaultRemoteControlType;
    }

    /**
     * @param defaultRemoteControlType
     *            the defaultRemoteControlType to set
     */
    public void setDefaultRemoteControlType(RemoteControlType defaultRemoteControlType) {
	this.defaultRemoteControlType = defaultRemoteControlType;
    }

    /**
     * @return the remoteControlTypes
     */
    public List<RemoteControlType> getRemoteControlTypes() {
	return remoteControlTypes;
    }

    /**
     * @param remoteControlTypes
     *            the remoteControlTypes to set
     */
    public void setRemoteControlTypes(List<RemoteControlType> remoteControlTypes) {
	this.remoteControlTypes = remoteControlTypes;
    }

    /**
     * @return the estbIpAdress
     */
    public String getEstbIpAdress() {
	return estbIpAdress;
    }

    /**
     * @param estbIpAdress
     *            the estbIpAdress to set
     */
    public void setEstbIpAdress(String estbIpAdress) {
	this.estbIpAdress = estbIpAdress;
    }

}