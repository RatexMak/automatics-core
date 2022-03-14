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

package com.automatics.tap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.automatics.alm.AlmTestDetails;
import com.automatics.annotations.TestDetails;
import com.automatics.constants.AVConstants;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.DataProviderConstants;
import com.automatics.constants.LinuxCommandConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.constants.WebPaConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.ChannelDetailsDO;
import com.automatics.device.ConnectedDevices;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.device.DutAccount;
import com.automatics.device.DutInfo;
import com.automatics.enums.ChannelTypes;
import com.automatics.enums.ExecutionMode;
import com.automatics.enums.ExecutionStatus;
import com.automatics.enums.RackType;
import com.automatics.enums.RemoteControlType;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.exceptions.TestException;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;
import com.automatics.image.imagick.MagickComparison;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.CodeDownloadProvider;
import com.automatics.providers.DeviceAccessValidator;
import com.automatics.providers.RdkVideoDeviceProvider;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.DeviceConsoleType;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.providers.connection.SerialCommandExecutionProvider;
import com.automatics.providers.crashanalysis.CrashAnalysisProvider;
import com.automatics.providers.crashanalysis.CrashDetails;
import com.automatics.providers.crashanalysis.CrashPortalRequest;
import com.automatics.providers.imageupgrade.ImageRequestParams;
import com.automatics.providers.imageupgrade.ImageUpgradeMechanism;
import com.automatics.providers.imageupgrade.ImageUpgradeProvider;
import com.automatics.providers.imageupgrade.ImageUpgradeProviderFactory;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.providers.rack.DeviceProvider;
import com.automatics.providers.rack.ImageCompareProvider;
import com.automatics.providers.rack.OcrProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RemoteProvider;
import com.automatics.providers.rack.exceptions.ImageCompareException;
import com.automatics.providers.rack.exceptions.OcrException;
import com.automatics.providers.rack.exceptions.PowerProviderException;
import com.automatics.providers.snmp.SnmpProvider;
import com.automatics.providers.snmp.SnmpProviderFactory;
import com.automatics.providers.tr69.Parameter;
import com.automatics.providers.tr69.TR69Provider;
import com.automatics.providers.trace.ConnectionTraceProvider;
import com.automatics.providers.trace.SerialTraceProvider;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.rack.RackDeviceValidationManager;
import com.automatics.rack.RackInitializer;
import com.automatics.region.ImageCompareRegionInfo;
import com.automatics.region.OcrRegionInfo;
import com.automatics.region.RegionInfo;
import com.automatics.reporter.TestResultUpdator;
import com.automatics.resource.IResourceLocator;
import com.automatics.resource.IServer;
import com.automatics.resource.ResourceLocatorImpl;
import com.automatics.snmp.SnmpCommand;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpProtocol;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsSnmpUtils;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanConstants;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.FileUtils;
import com.automatics.utils.FrameworkHelperUtils;
import com.automatics.utils.ImageRegionUtils;
import com.automatics.utils.NonRackUtils;
import com.automatics.webpa.WebPaConnectionHandler;
import com.automatics.webpa.WebPaEntityResponse;
import com.automatics.webpa.WebPaParameter;
import com.automatics.webpa.WebPaServerResponse;
import com.github.mustachejava.Code;

/**
 * This is the parent TAP API class for test environment.
 * 
 * @author TATA
 */
public class AutomaticsTapApi {

    /** SLF4J LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsTapApi.class);

    public static RackInitializer rackInitializerInstance = null;

    private static SnmpProviderFactory snmpProviderFactoryInstance = null;

    private static RdkVideoDeviceProvider rdkVideoDeviceProvider = null;

    private static int reConnectCount = 0;

    /** Identifies the beginning of image name in the telnet out put. */
    private static final String IDENTIFIER_FOR_BEGINNING_OF_IMAGE_NAME = "imagename";

    /** Resource selector. */
    private IResourceLocator resourceLocator = new ResourceLocatorImpl("app");

    /** The constant holds the image name format. */
    private static final String OCR_IMAGE_SAVE_FORMAT = "%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS";

    /** The constant holds the first compared image location. */
    private static final String FIRST_COMPARED_IMAGE_FILE_EXTENSION = "-first.jpg";

    /** The constant holds the last compared image location. */
    private static final String LAST_COMPARED_IMAGE_FILE_EXTENSION = "-last.jpg";

    private static final String DATE_TIME_FORMAT = "yyyy.MM.dd-HH.mm.ss";

    private static final String IMAGE_FILE_FORMAT = "jpg";

    private CrashAnalysisProvider crashAnalysisProvider = null;

    /** JPG image format name. */
    private String imageFormatName = "JPEG";

    private static final String CMD_TO_VERIFY_KEYSIMULATOR = " if [ -f /usr/bin/keySimulator ] ; then echo \"yes\" ; else echo \"no\" ; fi";

    private static final String KEY_SIMULATOR = "/usr/bin/keySimulator";

    /** The constant holds stb property name to configure partners for rdkv */
    private static final String STB_PROPERTY_RDKV_PARTNER = "rdkv.valid.partners";

    /** The constant holds stb property name to configure partners for rdkb */
    private static final String STB_PROPERTY_RDKB_PARTNER = "rdkb.valid.partners";

    /**
     * Variable to check if Initialization has started, so that same thread does not try to re-initialize from elsewhere
     **/
    private static boolean isRackInitialized = false;

    private static int counter = 0;

    /** Variable for holding the timeout values. */
    private Map<String, Long> waitValues = new HashMap<String, Long>();

    /** Number of retries attempt to be made. */
    protected int retries = 2;

    /** Holds the known reboot count */
    private static Map<String, Integer> knownRebootCount = new HashMap<String, Integer>();

    /**
     * The regex used to extract the camera #.
     */
    public static final Pattern CAMERA_PATTERN = Pattern.compile(".*camera=([1-9]+).*");

    private long IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS = 10 * 60 * 1000;

    /**
     * The regex used to extract the camera # for one ip racks.
     */
    public static final Pattern CAMERA_PATTERN_ONE_IP = Pattern.compile(".*connectionport=([1-9]+).*");

    // Zephyr integration tool url
    private static final String ZEPHYR_INTEGRATION_TOOL_URL = "zit.update.url";

    private static final Map<String, TreeMap<Integer, ChannelDetailsDO>> channelDetailsMap = new HashMap<String, TreeMap<Integer, ChannelDetailsDO>>();

    /** Map to hold estbMac with out colon **/
    private static final Map<String, String> estbMacWithOutColonMap = new HashMap<String, String>();

    /**
     * Flag to indicates the initialization status of connected clients.
     */
    private static boolean isConnectedDevicesInitalized = false;

    /** Map for channel number and locator Id. */
    public static Map<String, String> channelNumberLocatorIdMap = new HashMap<String, String>();

    /** Map for estb mac and device Id. */
    public static Map<String, String> estbMacDeviceIdMap = new HashMap<String, String>();

    /** Map for estb mac and service Account Id. */
    public static Map<String, String> estbMacServiceAccountIdMap = new HashMap<String, String>();

    /** Map for estb mac and native process Id. */
    public static Map<String, String> estbMacNativeProcessIdMap = new HashMap<String, String>();

    /** Map for estb mac and ecm mac. */
    public static Map<String, String> estbMacEcmMap = new HashMap<String, String>();

    /** Map for estb mac and headEnd. */
    public static Map<String, String> estbMacHeadEndMap = new HashMap<String, String>();

    /** Instance of {@link Code AutomaticsTapApi} */
    private static AutomaticsTapApi tapApi = null;

    private static DeviceConnectionProvider deviceConnectionProvider;

    private static DeviceAccessValidator deviceAccessValidator;

    /**
     * Constructor for AutomaticsTapApi
     * 
     * @param tapEnv
     *            {@link Code AutomaticsTapApi}
     */
    private AutomaticsTapApi() {

	LOGGER.info("Creating new instance for AutomaticsTapApi");

	deviceConnectionProvider = BeanUtils.getDeviceConnetionProvider();

	deviceAccessValidator = BeanUtils.getDeviceAccessValidator();
	getRackInitializerInstance();

    }

    /**
     * Singleton method to get the AutomaticsTapApi object
     * 
     * @param tapEnv
     *            {@link Code AutomaticsTapApi}
     * @return Reference for {@link Code AutomaticsTapApi}
     */
    public static synchronized AutomaticsTapApi getInstance() {

	LOGGER.debug("Starting AutomaticsTapApi.getInstance().");

	if (null == tapApi) {
	    LOGGER.info("AutomaticsTapApi instance not available. Creating new instance");

	    tapApi = new AutomaticsTapApi();
	}
	LOGGER.debug("Ending AutomaticsTapApi.getInstance().");

	return tapApi;
    }

    /**
     * Return the retry count.
     * 
     * @return the retry count.
     */
    protected int getRetries() {
	return retries;
    }

    private static Object lockObject = new Object();

    /**
     * Get the <code>RackInitializer</code> instance.
     * 
     * @return the singleton instance of {@link RackInitializer} object.
     */
    public static RackInitializer getRackInitializerInstance() {

	if (!isRackInitialized) {

	    synchronized (lockObject) {

		if (null == rackInitializerInstance && !isRackInitialized) {

		    isRackInitialized = true;

		    rackInitializerInstance = new RackInitializer();
		    rackInitializerInstance.initializeRack();
		    List<Dut> lockedSettops = null;
		    Boolean isAccountBasedTest = new Boolean(
			    System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
		    if (isAccountBasedTest) {
			lockedSettops = rackInitializerInstance.getLockedDevicesInAccountBasedTest();
		    } else {
			lockedSettops = rackInitializerInstance.getLockedSettops();
		    }
		    // Channel data fetch from grid web service
		    // for AccountTest

		    if (lockedSettops != null && !lockedSettops.isEmpty()) {
			loadChannelDetailsMap(lockedSettops);
		    }

		}
	    }
	} else {
	    LOGGER.debug("Rack Initializer Instance Already Initialized");
	}

	return rackInitializerInstance;
    }

    /**
     * Method to load the channel details map for each locked box
     */
    public static void loadChannelDetailsMap(List<Dut> lockSettops) {
	TreeMap<Integer, ChannelDetailsDO> channelDetailsMapForDevice = null;
	for (Dut device : lockSettops) {
	    if (SupportedModelHandler.isRDKV(device)) {
		if (null == rdkVideoDeviceProvider) {
		    rdkVideoDeviceProvider = BeanUtils.getRdkVideoDeviceProvider();
		}
		if (null != rdkVideoDeviceProvider) {
		    LOGGER.info("About to get channel data");
		    channelDetailsMapForDevice = rdkVideoDeviceProvider.getChannelData(device);
		    channelDetailsMap.put(device.getHostMacAddress(), channelDetailsMapForDevice);
		}
	    }
	}
    }

    /**
     * Method to get current execution mode from the VM Parameter
     * 
     * @return execute mode
     */
    public static String getCurrentExecutionMode() {

	LOGGER.debug("STARTING METHOD: getCurrentExecutionMode()");

	String executionMode = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_EXECUTION_MODE,
		AutomaticsConstants.EXECUTION_MODE_RDKV);

	LOGGER.debug("ENDING METHOD: getCurrentExecutionMode()");

	return executionMode;
    }

    /**
     * Uses the key to extract the value for the key value pair defined in the <b>stb.properties</b> file.
     * 
     * @param propertyKey
     *            property key
     * @return STB property value
     */
    public static String getSTBPropsValue(String propertyKey) {

	// Load the STB Property file
	AutomaticsPropertyUtility.loadProperties();

	String propertyValue = AutomaticsPropertyUtility.getProperty(propertyKey);

	if (null == propertyValue || propertyValue.trim().length() == 0) {
	    throw new FailedTransitionException(GeneralError.PROP_KEY_NOT_FOUND,
		    "Key not found on Automatics properties : " + propertyKey);
	}

	return propertyValue;
    }

    /**
     * Method to retrieve eSTB MAC address
     * 
     * @param dut
     *            instance of {@link Dut}
     * @return eSTB/eCM MAC Address without colon
     */
    public String getStbMacIdForIpDeviceWithoutColon(Dut dut) {

	// eSTB MAC Address of STB
	String macID = null;

	if (estbMacWithOutColonMap.containsKey(dut.getHostMacAddress())) {
	    macID = estbMacWithOutColonMap.get(dut.getHostMacAddress());
	}
	if (CommonMethods.isNull(macID)) {

	    if (SupportedModelHandler.isRDKB(dut)) { // For RDK B devices, we
						     // won't get mac address
						     // from STBDetails
		macID = ((Device) dut).getEcmMac();
	    } else if (SupportedModelHandler.isRDKC(dut)) {
		macID = dut.getHostMacAddress();
	    } else if (SupportedModelHandler.isRDKVClient(dut)) {
		macID = ((Device) dut).getEcmMac();
	    } else {
		macID = dut.getHostMacAddress();
	    }

	    if (CommonMethods.isNull(macID)) {

		if (SupportedModelHandler.isRDKVClient(dut)) {
		    DeviceProvider deviceManager = (DeviceProvider) BeanUtils.getPartnerProviderImpl(
			    BeanConstants.PROP_KEY_DEVICE_MANAGER, BeanConstants.BEAN_ID_DEVICE_PROVIDER,
			    DeviceProvider.class);
		    DevicePropsRequest request = new DevicePropsRequest();
		    request.setMac(dut.getHostMacAddress());
		    List<String> propsRequested = new ArrayList<String>();
		    propsRequested.add("ECM_MAC");
		    request.setRequestedPropsName(propsRequested);
		    Map<String, String> data = deviceManager.getDeviceProperties(request);
		    if (null != data && null != data.get("ECM_MAC")) {
			macID = data.get("ECM_MAC");
		    }

		} else {
		    macID = dut.getHostMacAddress();
		}
	    }

	    if (CommonMethods.isNotNull(macID)) {
		macID = macID.replaceAll(AutomaticsConstants.COLON, AutomaticsConstants.EMPTY_STRING).toUpperCase();
	    }

	    if (!estbMacWithOutColonMap.containsKey(dut.getHostMacAddress()) && CommonMethods.isNotNull(macID)) {
		estbMacWithOutColonMap.put(dut.getHostMacAddress(), macID);
	    }
	}

	return macID;
    }

    /**
     * Increment the known reboot counter
     */
    public static synchronized void incrementKnownRebootCounter(String macAddress) {

	int currentRebootCount = 0;

	if (knownRebootCount.containsKey(macAddress)) {
	    currentRebootCount = knownRebootCount.get(macAddress);
	}

	currentRebootCount++;

	knownRebootCount.put(macAddress, currentRebootCount);

    }

    public void waitAfterHardRebootInitiated(Dut dut) {
	CommonMethods.waitForEstbIpAcquisition(this, dut);
    }

    /**
     * Method to execute a single WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameter
     *            WebPA command to be executed.
     * 
     * @return the WebPa command response
     */
    public List<WebPaParameter> setWebPaParamsWithWildCard(Dut dut, String parameter, String value, int dataType) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.setWebPaParams");
	WebPaParameter webPaParam = new WebPaParameter();

	webPaParam.setDataType(dataType);
	webPaParam.setName(parameter);
	webPaParam.setValue(value);

	List<WebPaParameter> webPaParameters = new ArrayList<WebPaParameter>();
	webPaParameters.add(webPaParam);

	WebPaServerResponse webPaSetResponse = setWebPaParameterValuesWithWildCard(dut, webPaParameters);

	LOGGER.info("WebPA response : " + webPaSetResponse);

	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.setWebPaParams");
	return webPaSetResponse.getParams();
    }

    /**
     * Helper method to set WebPa parameter values
     * 
     * @param dut
     *            The device under test
     * @param webPaParameters
     *            The WebPa parameter to set
     * @return The response of the execution of webpa parameter.
     */
    public WebPaServerResponse setWebPaParameterValuesWithWildCard(Dut dut, List<WebPaParameter> webPaParameters) {
	LOGGER.debug("STARTING METHOD: setWebPaParameterValues");
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);
	LOGGER.info("ENDING METHOD: setWebPaParameterValues : " + serverResponse.getMessage());
	return serverResponse;
    }

    /**
     * Method to execute a single WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameter
     *            WebPA command to be executed.
     * 
     * @return the WebPa command response
     */
    public String executeWebPaCommandWithWildCardSupport(Dut dut, String parameter) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.executeWebPaCommand");
	// for the storing response from executeWebPaCommands API
	List<String> response = null;
	// stores the result of the WebPA comand execution
	String commandResult = null;

	LOGGER.debug("WebPA Command to be run on client " + parameter);
	String[] commandArray = new String[] { parameter };
	response = executeWebPaCommandsWithWildCardSupport(dut, commandArray);

	// retrieving the first index of the response so as to get the value of
	// the parameter if the response array is
	// of length 1
	if (response.size() == 1) {
	    commandResult = response.get(response.size() - 1);

	}
	LOGGER.info("WebPA response : " + commandResult);

	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.executeWebPaCommand");
	return commandResult;
    }

    /**
     * Method to execute multiple WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameterArray
     *            List containing the WebPA params to be executed
     * @param value
     * 
     * @return List containing the response of all the parameteres
     */
    public List<String> executeWebPaCommandsWithWildCardSupport(Dut dut, String[] parameterArray) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.executeWebPaCommands");

	LOGGER.debug("Length of Array " + parameterArray.length);
	LOGGER.debug("Command Array" + parameterArray[parameterArray.length - 1]);
	List<String> response = getTR69ParameterValuesUsingWebPAWithWildCard(dut, parameterArray);
	LOGGER.debug("Response.size " + response.size());
	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.executeWebPaCommands");
	return response;
    }

    /**
     * Get the TR-069 parameter values using WebPA.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The value corresponding to TR-069 parameter
     */
    public List<String> getTR69ParameterValuesUsingWebPAWithWildCard(Dut dut, String[] parameter) {
	List<String> tr69ParamResponse = new ArrayList<String>();

	String commandsCommaSeparated = FrameworkHelperUtils.convertToCommaSeparatedList(parameter);
	LOGGER.info("About to get values for TR69 params {} via WebPa", commandsCommaSeparated);

	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut, parameter);

	List<WebPaParameter> params = serverResponse.getParams();
	if (null != params && !params.isEmpty()) {
	    for (WebPaParameter webPaParameter : params) {
		tr69ParamResponse.add(webPaParameter.getValue());
	    }
	}

	LOGGER.info("TR69 Response from WebPa: {}", tr69ParamResponse);
	return tr69ParamResponse;
    }

    /**
     * Get the values of multiple TR-181 paramters using webPa
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            list of parameters
     * @return The map with value corresponding to each TR-181 parameter
     */
    public Map<String, String> executeMultipleWebPaGetCommands(Dut dut, String[] parameters) {

	Map<String, String> tr69ParamResponse = new HashMap<String, String>();
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut, parameters);

	List<WebPaParameter> params = serverResponse.getParams();

	if (null != params && !params.isEmpty()) {
	    for (WebPaParameter webPaParameter : params) {
		tr69ParamResponse.put(webPaParameter.getName(), webPaParameter.getValue());
	    }
	}

	LOGGER.debug("COMPLETED METHOD: AutomaticsTapApi.executeMultipleWebPaCommands");
	return tr69ParamResponse;
    }

    /**
     * Executes linux command in Given Dut box.
     * 
     * @param dut
     *            Set-top to which the command to be executed
     * @param command
     *            Linux command.
     * 
     * @return the output of command executed.
     */
    public String executeCommandInSettopBox(Dut dut, String command) {

	String response = null;

	if (isSerialConsoleExecutionRequired()) {

	    response = CommonMethods.executeCommandInSerialConsole(dut, command);
	} else {
	    String[] commands = new String[] { command };

	    response = executeCommand(dut, commands);
	}

	return response;
    }

    /**
     * Executes linux commands using telnet.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param commands
     *            Linux commands.
     * 
     * @return the output of linux commands executed.
     */
    public String executeCommand(Dut dut, String[] commands) {
	String response = null;

	List<String> commandList = Arrays.asList(commands);
	if (null != deviceConnectionProvider) {
	    response = deviceConnectionProvider.execute((Device) dut, commandList, DeviceConsoleType.ARM);
	}
	return response;

    }

    /**
     * Executes linux commands using telnet.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param commands
     *            Linux commands.
     * 
     * @return the output of linux commands executed.
     */
    public String executeCommandUsingSsh(Dut dut, String[] commands) {

	String response = null;

	if (null != deviceConnectionProvider) {
	    List<String> commandList = Arrays.asList(commands);
	    response = deviceConnectionProvider.execute((Device) dut, commandList, DeviceConsoleType.ARM);
	}
	return response;

    }

    /**
     * Execute command on device
     * 
     * @param dut
     * @param command
     * @return
     */
    public String executeCommand(Dut dut, String command) {

	return executeCommandUsingSsh(dut, command);
    }

    public String executeCommandUsingSsh(Dut dut, String command) {

	String[] commands = new String[] { command };

	return executeCommandUsingSsh(dut, commands);
    }

    /**
     * Method to check if the command execution is required through serial console or not
     * 
     * @return
     */
    public static boolean isSerialConsoleExecutionRequired() {
	boolean isSerialConsoleExecution = System.getProperty("isSerialConsoleExecution", "false")
		.equalsIgnoreCase("true");
	LOGGER.debug("isSerialConsoleExecutionRequired : " + isSerialConsoleExecution);
	return isSerialConsoleExecution;
    }

    /**
     * Method to execute a single WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameter
     *            WebPA command to be executed.
     * 
     * @return the WebPa command response
     */
    public List<WebPaParameter> setWebPaParams(Dut dut, String parameter, String value, int dataType) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.setWebPaParams");
	WebPaParameter webPaParam = new WebPaParameter();

	webPaParam.setDataType(dataType);
	webPaParam.setName(parameter);
	webPaParam.setValue(value);

	List<WebPaParameter> webPaParameters = new ArrayList<WebPaParameter>();
	webPaParameters.add(webPaParam);

	WebPaServerResponse webPaSetResponse = setWebPaParameterValues(dut, webPaParameters);

	LOGGER.info("WebPA response : " + webPaSetResponse);

	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.setWebPaParams");
	return webPaSetResponse.getParams();
    }

    /**
     * Helper method to set WebPa parameter values
     * 
     * @param dut
     *            The device under test
     * @param webPaParameters
     *            The WebPa parameter to set
     * @return The response of the execution of webpa parameter.
     */
    public WebPaServerResponse setWebPaParameterValues(Dut dut, List<WebPaParameter> webPaParameters) {
	LOGGER.debug("STARTING METHOD: setWebPaParameterValues");
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);
	LOGGER.info("ENDING METHOD: setWebPaParameterValues : " + serverResponse.getMessage());
	return serverResponse;
    }

    /**
     * Sleeps till the specified time elapses.
     * 
     * @param milliseconds
     *            Milliseconds to wait for
     */
    public void waitTill(long milliseconds) {
	AutomaticsUtils.sleep(milliseconds);
    }

    /**
     * Method to execute a single WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameter
     *            WebPA command to be executed.
     * 
     * @return the WebPa command response
     */
    public String executeWebPaCommand(Dut dut, String parameter) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.executeWebPaCommand");
	// for the storing response from executeWebPaCommands API
	List<String> response = null;
	// stores the result of the WebPA comand execution
	String commandResult = null;

	LOGGER.debug("WebPA Command to be run on client " + parameter);
	String[] commandArray = new String[] { parameter };
	response = executeWebPaCommands(dut, commandArray);

	// retrieving the first index of the response so as to get the value of
	// the parameter if the response array is
	// of length 1
	if (response.size() == 1) {
	    commandResult = response.get(response.size() - 1);

	}
	LOGGER.info("WebPA response : " + commandResult);

	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.executeWebPaCommand");
	return commandResult;
    }

    /**
     * Method to execute multiple WebPA response and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param parameterArray
     *            List containing the WebPA params to be executed
     * @param value
     * 
     * @return List containing the response of all the parameteres
     */
    public List<String> executeWebPaCommands(Dut dut, String[] parameterArray) {

	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.executeWebPaCommands");

	LOGGER.debug("Length of Array " + parameterArray.length);
	LOGGER.debug("Command Array" + parameterArray[parameterArray.length - 1]);
	List<String> response = getTR69ParameterValuesUsingWebPA(dut, parameterArray);
	LOGGER.debug("Response.size " + response.size());
	LOGGER.debug("ENDING METHOD: AutomaticsTapApi.executeWebPaCommands");
	return response;
    }

    /**
     * Get the TR-069 parameter values using WebPA.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The value corresponding to TR-069 parameter
     */
    public List<String> getTR69ParameterValuesUsingWebPA(Dut dut, String[] parameter) {
	List<String> tr69ParamResponse = new ArrayList<String>();

	String commandsCommaSeparated = FrameworkHelperUtils.convertToCommaSeparatedList(parameter);
	LOGGER.info("About to get values for TR69 params {} via WebPa", commandsCommaSeparated);

	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut, parameter);

	if (null != serverResponse) {
	    List<WebPaParameter> params = serverResponse.getParams();
	    if (null != params && !params.isEmpty()) {
		for (WebPaParameter webPaParameter : params) {
		    tr69ParamResponse.add(webPaParameter.getValue());
		}
	    }
	} else {
	    LOGGER.info("WebPA Response is null.");
	}

	LOGGER.info("TR69 Response via WebPa: {}", tr69ParamResponse);

	return tr69ParamResponse;
    }

    /**
     * Update the execution status based on current test steps.
     * 
     * @param dut
     *            The dut to be used.
     * @param testId
     *            The manual test ID
     * @param testStepNumber
     *            The manual test step number.
     * @param status
     *            The execution status.
     * @param errorMessage
     *            The error message.
     * @param blockExecution
     *            The flag to throw an exception to block further execution of test cases.
     */
    public void updateExecutionStatus(Dut dut, String testId, String testStepNumber, boolean status,
	    String errorMessage, boolean blockExecution) {
	updateExecutionStatus(dut, testId, testStepNumber, status, errorMessage, blockExecution, false);
	ExecutionStatus testStatus = status ? ExecutionStatus.PASSED : ExecutionStatus.FAILED;
	updateExecutionForAllStatus(dut, testId, testStepNumber, testStatus, errorMessage, blockExecution);
    }

    public void updateExecutionStatus(Dut dut, String testId, String testStepNumber, boolean status,
	    String errorMessage, boolean blockExecution, boolean updateToElkDb) {

	ExecutionStatus testStatus = status ? ExecutionStatus.PASSED : ExecutionStatus.FAILED;
	updateExecutionForAllStatus(dut, testId, testStepNumber, testStatus, errorMessage, blockExecution,
		updateToElkDb);
    }

    /**
     * Update the execution status based on current test steps.
     * 
     * @param dut
     *            The dut to be used.
     * @param testId
     *            The manual test ID
     * @param testStepNumber
     *            The manual test step number.
     * @param status
     *            The execution status.
     * @param errorMessage
     *            The error message.
     * @param blockExecution
     *            The flag to throw an exception to block further execution of test cases.
     */
    public void updateExecutionForAllStatus(Dut dut, String testId, String testStepNumber, ExecutionStatus status,
	    String errorMessage, boolean blockExecution) {
	updateExecutionForAllStatus(dut, testId, testStepNumber, status, errorMessage, blockExecution, false);
	String imageName = AutomaticsConstants.EMPTY_STRING;
	imageName = "UI_after_" + testId + "_" + testStepNumber;

	if (!SupportedModelHandler.isRDKB(dut) && !SupportedModelHandler.isRDKC(dut)) {
	    // Stores the image after each step
	    if (status.equals(ExecutionStatus.PASSED)) {
		captureAndSaveImage(dut, imageName);
	    } else if (status.equals(ExecutionStatus.FAILED)) {
		captureAndSaveImage(dut, imageName + "(Test_Step_Failed)");
	    }
	}
	if (status.equals(ExecutionStatus.FAILED)) {
	    LOGGER.error("Error Message: " + errorMessage);
	} else {
	    errorMessage = AutomaticsConstants.EMPTY_STRING;
	}
	LOGGER.info("[STB MAC : " + dut.getHostMacAddress() + "][ Manual test ID : " + testId + "] [step Number : "
		+ testStepNumber + "][ Execution status : " + status.getStatus() + "] [Error Message : " + errorMessage
		+ "]");

	/**
	 * Methods to seperate test case ids in case of xi3 boxes Methods to seperate step numbet ids in case of xi3
	 * boxes
	 */
	String[] testCaseIdArray = testId.split(AutomaticsConstants.COMMA);
	String[] stepNumberArray = testStepNumber.split(AutomaticsConstants.COMMA);

	if (testCaseIdArray.length > 1) {
	    testId = (SupportedModelHandler.isRDKVClient(dut)) ? testCaseIdArray[1].trim() : testCaseIdArray[0].trim();
	} else {
	    testId = testCaseIdArray[0].trim();
	}

	if (stepNumberArray.length > 1) {

	    testStepNumber = (SupportedModelHandler.isRDKVClient(dut)) ? stepNumberArray[1].trim()
		    : stepNumberArray[0].trim();

	} else {

	    testStepNumber = stepNumberArray[0].trim();
	}

	TestResultUpdator.get().updateExecutionStatus(dut, testId, testStepNumber, status, errorMessage,
		blockExecution);

	if (blockExecution && (ExecutionStatus.FAILED.equals(status) || ExecutionStatus.NOT_TESTED.equals(status)
		|| ExecutionStatus.NOT_APPLICABLE.equals(status))) {

	    throw new TestException(errorMessage);
	}
    }

    public void updateExecutionForAllStatus(Dut dut, String testId, String testStepNumber, ExecutionStatus status,
	    String errorMessage, boolean blockExecution, boolean updateToElkDb) {
	String imageName = AutomaticsConstants.EMPTY_STRING;
	imageName = "UI_after_" + testId + "_" + testStepNumber;

	if (!SupportedModelHandler.isRDKB(dut) && !SupportedModelHandler.isRDKC(dut)) {
	    // Stores the image after each step
	    if (status.equals(ExecutionStatus.PASSED)) {
		captureAndSaveImage(dut, imageName);
	    } else if (status.equals(ExecutionStatus.FAILED)) {
		captureAndSaveImage(dut, imageName + "(Test_Step_Failed)");
	    }
	}
	if (status.equals(ExecutionStatus.FAILED)) {
	    LOGGER.error("Error Message: " + errorMessage);
	} else {
	    errorMessage = AutomaticsConstants.EMPTY_STRING;
	}
	LOGGER.info("[STB MAC : " + dut.getHostMacAddress() + "][ Manual test ID : " + testId + "] [step Number : "
		+ testStepNumber + "][ Execution status : " + status.getStatus() + "] [Error Message : " + errorMessage
		+ "]");

	/**
	 * Methods to seperate test case ids in case of xi3 boxes Methods to seperate step numbet ids in case of xi3
	 * boxes
	 */
	String[] testCaseIdArray = testId.split(AutomaticsConstants.COMMA);
	String[] stepNumberArray = testStepNumber.split(AutomaticsConstants.COMMA);

	if (testCaseIdArray.length > 1) {
	    testId = (SupportedModelHandler.isRDKVClient(dut)) ? testCaseIdArray[1].trim() : testCaseIdArray[0].trim();
	} else {
	    testId = testCaseIdArray[0].trim();
	}

	if (stepNumberArray.length > 1) {

	    testStepNumber = (SupportedModelHandler.isRDKVClient(dut)) ? stepNumberArray[1].trim()
		    : stepNumberArray[0].trim();

	} else {

	    testStepNumber = stepNumberArray[0].trim();
	}
	TestResultUpdator.get().updateExecutionStatus(dut, testId, testStepNumber, status, errorMessage, blockExecution,
		updateToElkDb);

	if (blockExecution && (ExecutionStatus.FAILED.equals(status) || ExecutionStatus.NOT_TESTED.equals(status)
		|| ExecutionStatus.NOT_APPLICABLE.equals(status))) {

	    throw new TestException(errorMessage);
	}
    }

    /**
     * Executes linux command using telnet.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param command
     *            Linux command.
     * 
     * @return the output of command executed.
     */
    public String executeCommandUsingSsh(Dut dut, String command, long timeout) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    response = deviceConnectionProvider.execute((Device) dut, command, DeviceConsoleType.ARM, timeout);
	}
	return response;
    }

    /**
     * Method to capture the cureent screen.
     * 
     * @param dut
     *            The dut where the image to be captured
     * 
     * @return The buffered image
     */
    public BufferedImage captureCurrentScreen(Dut dut) {
	BufferedImage image = null;
	if (null != dut.getVideo()) {
	    image = dut.getVideo().getVideoImage();
	}
	return image;
    }

    public String executeCommandOnAtom(Dut device, String command) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    response = deviceConnectionProvider.execute((Device) device, command, DeviceConsoleType.ATOM,
		    AutomaticsConstants.THIRTY_SECONDS);
	}
	return response;
    }

    /**
     * Helper method to capture and save images .
     * 
     * @param settop
     *            The settop instance from which image need to be captured
     * @param string
     *            The string to be represented as image name
     *
     */
    public String captureAndSaveImage(Dut dut, String imageName) {
	String imageLocation = AutomaticsConstants.EMPTY_STRING;
	imageName = System.currentTimeMillis() + "_" + imageName;
	BufferedImage capturedScreen = captureCurrentScreen(dut);
	String savedLocation = saveImages(dut, capturedScreen, imageName);
	File file = new File(savedLocation);
	if (file.exists() && file.isFile()) {
	    imageLocation = savedLocation;
	}
	return imageLocation;
    }

    /**
     * Helper method to save images
     * 
     * @param dut
     *            The dut instance from which image need to be captured
     * @param beforeTest
     *            The buffered image captured
     * @param string
     *            The string to be represented as image name
     */

    public static String saveImages(Dut dut, BufferedImage bufferedImage, String imageName) {
	File outputFile = null;
	File outputDirectory = null;

	String firmwareVersion = (null != dut.getFirmwareVersion() ? dut.getFirmwareVersion()
		: AutomaticsConstants.EMPTY_STRING);

	try {
	    if (SupportedModelHandler.isRDKB(dut) || SupportedModelHandler.isRDKC(dut)) {

		outputDirectory = new File(((Device) dut).getImageSaveLocation());
	    } else {
		if (NonRackUtils.isNonRack()) {
		    outputDirectory = new File(
			    System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
				    + AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + "images"
				    + AutomaticsConstants.PATH_SEPARATOR + AutomaticsUtils.getCleanMac(
					    dut.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR + "images"));
		    LOGGER.info("Obtained jenkins location for image save :" + outputDirectory);
		} else {
		    if (null != dut.getImageCompareProvider()) {
			outputDirectory = new File(dut.getImageCompareProvider().getImageSaveLocation());
		    } else {
			outputDirectory = new File(
				System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
					+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR
					+ AutomaticsUtils.getCleanMac(
						dut.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR)
					+ AVConstants.IMAGE_COMPARE_FOLDER);
		    }
		}
	    }

	    if (!outputDirectory.exists()) {
		outputDirectory.mkdirs();
	    }
	    outputFile = new File(outputDirectory,
		    firmwareVersion.replaceAll("\\.", AutomaticsConstants.EMPTY_STRING) + "_" + imageName + ".png");
	    ImageIO.write(bufferedImage, "PNG", outputFile);

	} catch (Exception e) {
	    LOGGER.info("Error occured while saving the image: {}", e.getMessage());
	}

	return outputDirectory + File.separator + firmwareVersion.replaceAll("\\.", AutomaticsConstants.EMPTY_STRING)
		+ "_" + imageName + ".png";
    }

    /**
     * Creates a parallel stb data provider. Tests will execute in stbs in parallel.
     * 
     * @param method
     *            The test method reference injected by TestNG
     * @param testContext
     *            The test context injected by TestNG
     * 
     * @return The data provider
     */
    @DataProvider(name = DataProviderConstants.PARALLEL_DATA_PROVIDER, parallel = true)
    public static Iterator<Object[]> settopParallelDataProvider(Method method, ITestContext testContext) {
	return createSettopDataProvider(false, method, testContext);
    }

    /**
     * Creates a parallel data provider for connected devices configuration. Tests will execute in stbs in parallel.
     * 
     * @param method
     *            The test method reference injected by TestNG
     * @param testContext
     *            The test context injected by TestNG
     * 
     * @return The data provider
     */
    @DataProvider(name = DataProviderConstants.CONNECTED_CLIENTS_DATA_PROVIDER, parallel = true)
    public static Iterator<Object[]> settopConnectedClientParallelDataProvider(Method method,
	    ITestContext testContext) {
	return createSettopDataProvider(true, method, testContext);
    }

    /**
     * Creates a sequential stb data provider. Tests will execute in stbs in sequence.
     * 
     * @param method
     *            The test method reference injected by TestNG
     * @param testContext
     *            The test context injected by TestNG
     * 
     * @return The data provider
     */
    @DataProvider(name = DataProviderConstants.SEQUENTIAL_DATA_PROVIDER)
    public static Iterator<Object[]> settopSequentialDataProvider(Method method, ITestContext testContext) {
	return createSettopDataProvider(false, method, testContext);
    }

    /**
     * Helper method to create dut provider for parallel, sequential and connected client scenarios.
     * 
     * @param isConnectedClient
     *            Flag to check whether it is connected client provider.
     * @param method
     *            The test method reference injected by TestNG
     * @param testContext
     *            The test context injected by TestNG
     * @return The data provider
     */
    private static Iterator<Object[]> createSettopDataProvider(boolean isConnectedClient, Method method,
	    ITestContext testContext) {

	if (!rackInitializerInstance.isRackIntialized()) {
	    rackInitializerInstance.initializeRack();
	}

	/*
	 * This parameter used by framework team, so using same for backward-compatibility.
	 */
	System.setProperty(AutomaticsConstants.SYSTEM_PROPERTY_INITIALIZE_CONNECTED_DEVICES,
		Boolean.toString(isConnectedClient));

	List<Object[]> settopToBeReturned = new ArrayList<Object[]>();
	List<Dut> requiredSettops = getRequiredSettops(method, testContext);

	for (Dut dut : requiredSettops) {

	    /*
	     * This portion of code is applicable only for RDKB connected client setup.
	     */
	    // Modifiying above condition, since we need to run
	    // security test cases for RDK-V which
	    // is meant to run in connected client setup
	    if (isConnectedClient && !isConnectedDevicesInitalized) {
		ConnectedDevices connectedDevices = new ConnectedDevices(dut);
		List<DutInfo> lockedDevices = connectedDevices.lockConnectedDevices();
		List<Dut> duts = connectedDevices.convertDutInfoToSettopInstance(lockedDevices);
		if (duts.size() > 0) {
		    StringBuilder clientMacs = new StringBuilder();

		    for (Dut clientDevice : duts) {
			clientMacs.append(clientDevice.getHostMacAddress()).append(" ");
		    }

		    LOGGER.info("NUMBER OF CONNECTED CLIENTS ASSOCIATED WITH DEVICE " + dut.getHostMacAddress() + " IS "
			    + duts.size());

		    LOGGER.info("CONNECTED CLIENTS ASSOCIATED WITH DEVICE {} IS {}", dut.getHostMacAddress(),
			    clientMacs.toString());

		    ((Device) dut).setConnectedDevices(duts);

		} else {
		    /*
		     * Devices with zero connected client should not be added to available device list.
		     */
		    LOGGER.error("CONNECTED CLIENTS ASSOCIATED WITH DEVICE '" + dut.getHostMacAddress()
			    + "' IS NOT AVAILABLE, SO SKIPING FROM AVAILABLE DEVICE FOR TEST EXECUTION");
		    continue;
		}
	    }
	    settopToBeReturned.add(new Object[] { dut });
	}

	/*
	 * If connected devices are initialized, we dont need to lock the devices again for use.
	 */
	if (isConnectedClient && !isConnectedDevicesInitalized) {
	    isConnectedDevicesInitalized = true;
	}

	return settopToBeReturned.iterator();
    }

    /**
     * Get the required settops.
     * 
     * @param method
     *            the method to be executed
     * @param iTestContext
     *            the current itestcontext
     * 
     * @return the list of required settops.
     */
    public static synchronized List<Dut> getRequiredSettops(Method method, ITestContext iTestContext) {
	Boolean IsAccountBasedTest = new Boolean(
		System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
	List<Dut> lockedSettops = new ArrayList<Dut>();
	if (IsAccountBasedTest) {
	    lockedSettops = rackInitializerInstance.getLockedDevicesInAccountBasedTest();
	} else {
	    lockedSettops = rackInitializerInstance.getLockedSettops();
	}
	List<Dut> requiredSettops = new ArrayList<Dut>();

	for (Dut dut : lockedSettops) {
	    LOGGER.info("Doing initialization for {}", dut.getHostMacAddress());
	    TestDetails detail = method.getAnnotation(TestDetails.class);

	    /**
	     * OCR, IC, etc initializations are not required for RDKB/C devices.
	     */
	    if (!SupportedModelHandler.isRDKB(dut) && !SupportedModelHandler.isRDKC(dut)) {

		LOGGER.info("Test uid={}", detail.testUID());

		// Setting image save location for Imagecompare
		AutomaticsTestBase.setRunningTestUid(detail.testUID());
		if (null != dut.getImageCompareProvider()) {
		    String imageCompareSaveLocation = getImageRedirectionFolder(dut, detail.testUID(), true);
		    dut.getImageCompareProvider().setImageSaveLocation(imageCompareSaveLocation);
		}

		// Setting image save location for Closed Caption

		if (null != dut.getImageCompareProvider()) {
		    String closedCpationImageSaveLocation = getCCImageRedirectionFolder(dut, detail.testUID(), true);
		    dut.getImageCompareProvider().setClosedCaptionImageSaveLocation(closedCpationImageSaveLocation);
		}

		// Setting image save location for OCR
		if (null != dut.getOcrProvider()) {
		    String ocrSaveLocation = getImageRedirectionFolder(dut, detail.testUID(), false);
		    dut.getOcrProvider().setImageSaveLocation(ocrSaveLocation);
		}

	    } else {
		((Device) dut).setImageSaveLocation(getImageRedirectionFolder(dut, detail.testUID(), true));
	    }

	    String[] boxTypesRequired = detail.runOnBoxTypes();
	    RackType[] rackType = detail.runOnRackType();

	    boolean shouldAddBox = false;

	    if (boxTypesRequired.length != 0 || rackType.length != 0) {

		shouldAddBox = true;

		if (shouldAddBox) {
		    requiredSettops.add(dut);
		}
	    } else {
		requiredSettops = lockedSettops;
	    }
	}

	return requiredSettops;
    }

    /**
     * Get the image redirection directory. mo
     * 
     * @param dut
     *            The dut instance
     * @param testUId
     *            The currently executing test id.
     * @param isImageCompare
     *            True if the image location is for IC, else OCR.
     * 
     * @return name of redirection folder.
     */
    public static String getImageRedirectionFolder(Dut dut, String testUId, boolean isImageCompare) {
	String imageFolder = isImageCompare ? AVConstants.IMAGE_COMPARE_FOLDER : AVConstants.OCR_IMAGE_FOLDER;
	String imageSaveLocation = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + testUId
		+ AutomaticsConstants.PATH_SEPARATOR + AutomaticsUtils
			.getCleanMac(dut.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR + imageFolder);

	return imageSaveLocation;
    }

    /**
     * Get the closed caption image redirection directory.
     * 
     * @param dut
     *            The dut instance
     * @param testUId
     *            The currently executing test id.
     * @param isImageCompare
     *            True if the image location is for IC, else OCR.
     * 
     * @return name of redirection folder.
     */
    public static String getCCImageRedirectionFolder(Dut dut, String testUId, boolean isImageCompare) {
	String imageFolder = isImageCompare ? AVConstants.IMAGE_COMPARE_FOLDER : AVConstants.OCR_IMAGE_FOLDER;
	String imageSaveLocation = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + imageFolder
		+ AutomaticsConstants.PATH_SEPARATOR + testUId + AutomaticsConstants.PATH_SEPARATOR
		+ AVConstants.CLOSEDCAPTION_IMAGE_FOLDER + AutomaticsConstants.PATH_SEPARATOR;

	return imageSaveLocation;
    }

    /**
     * This method is used to get the wait value using given Key.
     * 
     * @param dut
     *            The dut instance
     * @param key
     *            The platform key
     * 
     * @return The wait time corresponding to the particular platform.
     */
    public long getWaitValue(Dut dut, String key) {
	String platformKey = key + getPlatform(dut);

	Long waitValue = waitValues.get(platformKey);

	if (waitValue == null) {
	    LOGGER.debug("Platform key value got resolved is : " + platformKey);
	    waitValue = Long.parseLong(getSTBPropsValue(platformKey));
	    waitValues.put(platformKey, waitValue);
	}

	return waitValue;
    }

    public String getPlatform(Dut dut) {
	return (dut.getManufacturer() + '_' + dut.getModel()).toLowerCase();
    }

    /**
     * Get the TR-069 parameter values using WebPA.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The value corresponding to TR-069 parameter
     */
    public WebPaServerResponse getTR69ParameterValuesUsingWebPA(Dut dut, String parameter) {
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut,
		new String[] { parameter });

	return serverResponse;
    }

    /**
     * Utility method to set new IP Address. The IP address is
     * 
     * @param dut
     *            The Dut under test
     * @param newIpAddress
     *            The new Address to set
     */
    public void setNewIpAddress(Dut dut, String newIpAddress) {
	LOGGER.debug("STARTING METHOD: setNewIpAddress");

	LOGGER.info("IPv4={}", dut.getHostIp4Address());
	LOGGER.info("New IP={}", newIpAddress);

	if (CommonMethods.isNull(dut.getHostIp4Address()) || !(dut.getHostIp4Address().equals(newIpAddress.trim()))) {
	    Device ecatsSettop = (Device) dut;

	    if (SupportedModelHandler.isRDKVClient(dut)) {
		LOGGER.info("Is client device");
		ecatsSettop.setClientIpAddress(newIpAddress);
	    }
	    ecatsSettop.setHostIpAddress(newIpAddress);
	    ecatsSettop.setHostIp4Address(newIpAddress);
	    if (CommonMethods.isIpv6Address(newIpAddress)) {
		ecatsSettop.setHostIp6Address(newIpAddress);
	    } else {
		// Incase dut is in IPv4 mode, we can clear the
		// IPv6 address
		ecatsSettop.setHostIp6Address(null);
	    }
	}

	LOGGER.debug("ENDING METHOD: setNewIpAddress");
    }

    /**
     * Creates AccountTest data provider
     * 
     * @param method
     *            The test method reference injected by TestNG
     * @param testContext
     *            The test context injected by TestNG
     * @return
     */
    @DataProvider(name = DataProviderConstants.ACCOUNT_DATA_PROVIDER, parallel = true)
    public static Iterator<Object[]> settopAccountDataProvider(Method method, ITestContext testContext) {
	return createSettopAccountDataProvider(method, testContext);
    }

    private static Iterator<Object[]> createSettopAccountDataProvider(Method method, ITestContext testContext) {

	// RACK need to be initialised only if STB is required for tests
	if (!rackInitializerInstance.isRackIntialized()) {
	    rackInitializerInstance.initializeRack();
	}

	List<DutAccount> lockedHomeAccounts = rackInitializerInstance.getLockedHomeAccounts();
	List<Object[]> accountsToBeReturned = new ArrayList<Object[]>();
	for (DutAccount account : lockedHomeAccounts) {
	    accountsToBeReturned.add(new Object[] { account });
	}
	// This is called just to set the OCR path folder path
	getRequiredSettops(method, testContext);

	return accountsToBeReturned.iterator();
    }

    /**
     * Hard powers ON a dut device outlet using the WTI device.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @throws PowerProviderException
     *             if an exception occurs during operation
     */
    public void powerOn(Dut dut) throws PowerProviderException {

	if (!NonRackUtils.isNonRack()) {
	    dut.getPower().powerOn();
	}

    }

    /**
     * Hard powers OFF a dut device outlet using the WTI device.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @throws PowerProviderException
     *             if an exception occurs during operation
     */
    public void powerOff(Dut dut) throws PowerProviderException {
	if (!NonRackUtils.isNonRack()) {
	    dut.getPower().powerOff();
	}
    }

    /**
     * Return the ON or Off status state of a WTI device power outlet.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @return The power state returned by the WTI or Netboost power strips.
     */
    public String getPowerStatus(Dut dut) {
	String powerStatus = null;
	if (NonRackUtils.isNonRack()) {
	    return powerStatus;
	} else {
	    if (null != dut.getPower()) {
		try {
		    powerStatus = dut.getPower().getPowerStatus();
		} catch (PowerProviderException e) {
		    LOGGER.error("Exception while getting device power status: {}", e.getMessage());
		}
	    }
	    return powerStatus;
	}
    }

    /**
     * Start trace method. This will add an entry to the accessHistory table. It will invoke the Trace servlet's start
     * method. If the servlet invocation is success, trace load table is also updated.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @throws Exception
     *             if an exception occurs while starting trace provider
     */

    public void startTrace(Dut dut) throws Exception {
	if (!NonRackUtils.isNonRack()) {
	    dut.getTrace().startTrace();
	}
    }

    /**
     * Stop Trace method.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @throws Exception
     *             if an exception occurs while stopping trace provider
     */

    public void stopTrace(Dut dut) throws Exception {
	if (!NonRackUtils.isNonRack()) {
	    dut.getTrace().stopTrace();
	}
    }

    /**
     * This method will return the status of trace operation.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @return The current trace operation status
     * 
     * @throws Exception
     *             if an exception occurs while retrieving trace status
     */
    public String getTraceStatus(Dut dut) throws Exception {
	if (NonRackUtils.isNonRack()) {
	    return null;
	} else {
	    return dut.getTrace().getTraceStatus();
	}
    }

    /**
     * Trace blocks while waiting/searching for an expression. Note:- Make sure you call the startBuffering() if you
     * need to search in buffer
     * 
     * @param dut
     *            The {@link Dut} object
     * @param regExpression
     *            Regular expression to wait for.
     * @param timeoutInMilliSeconds
     *            timeout for which to wait for
     * 
     * @return true if the searched text is found, else will return false
     */
    public boolean searchAndWaitForTrace(Dut device, String regExpression, long timeoutInMilliSeconds) {
	boolean isSuccess = false;

	boolean searchFromStart = false;
	boolean returnOnFirstMatch = false;
	boolean printLogs = true;

	try {
	    if (null != device.getTrace()) {
		String traceFound = device.getTrace().searchAndWaitForTrace(regExpression, timeoutInMilliSeconds,
			searchFromStart, returnOnFirstMatch, printLogs);
		isSuccess = (null != traceFound);
	    }
	} catch (IOException ioe) {

	    LOGGER.error("searchAndWaitForTrace(): Failed to receive the expected trace due to read failure.", ioe);
	}

	return isSuccess;
    }

    /**
     * Trace blocks while waiting/searching for a string.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            Text string to wait for.
     * @param timeoutInMilliSeconds
     *            timeout for which to wait for
     * 
     * @return true if searched text is found before timeout elapses
     */
    public boolean waitForTraceString(Dut device, String stringText, long timeoutInMilliSeconds) {

	boolean isSuccess = false;

	try {
	    if (null != device.getTrace()) {
		isSuccess = device.getTrace().waitForTraceString(stringText, timeoutInMilliSeconds);
	    }
	} catch (IOException ioe) {

	    LOGGER.error("Failed to receive the expected trace due to read failure.", ioe);
	}

	return isSuccess;
    }

    /**
     * Send text to the trace stream.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            String to text to send to trace.
     */
    public void sendTraceString(Dut dut, String stringText) {

	try {
	    if (!NonRackUtils.isNonRack()) {
		boolean isHexString = false;

		TraceProvider provider = dut.getSerialTrace();
		if (null != provider && provider instanceof SerialTraceProvider) {
		    ((SerialTraceProvider) provider).sendTraceString(stringText, isHexString);
		} else {
		    throw new UnsupportedOperationException("Trace sending not supported");
		}
	    }
	} catch (UnsupportedOperationException uoe) {
	    LOGGER.error("Failed to send the text to the trace stream.", uoe);
	}
    }

    /**
     * Send text to the trace stream.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            String to text to send to trace.
     * @param isHexString
     *            Is the string to send in hex format.
     */
    public void sendTraceString(Dut dut, String stringText, boolean isHexString) {

	try {
	    if (!NonRackUtils.isNonRack()) {
		TraceProvider provider = dut.getSerialTrace();
		if (null != provider && provider instanceof SerialTraceProvider) {
		    ((SerialTraceProvider) provider).sendTraceString(stringText, isHexString);
		} else {
		    throw new UnsupportedOperationException("Trace sending not supported");
		}
	    }

	} catch (UnsupportedOperationException uoe) {
	    LOGGER.error("Failed to send the text to the trace stream.", uoe);
	}
    }

    /**
     * Send a byte to the trace stream.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param traceByte
     *            bytes to send to trace
     */
    public void sendTraceByte(Dut dut, byte traceByte) {

	try {
	    if (!NonRackUtils.isNonRack()) {
		byte[] traceBytes = new byte[] { traceByte };
		TraceProvider provider = dut.getSerialTrace();
		if (null != provider && provider instanceof SerialTraceProvider) {
		    ((SerialTraceProvider) provider).sendTraceBytes(traceBytes);
		} else {
		    throw new UnsupportedOperationException("Trace sending not supported");
		}

	    }
	} catch (UnsupportedOperationException uoe) {
	    LOGGER.error("Failed to send the byte to the trace stream.", uoe);
	}
    }

    /**
     * Perform a remote command and afterwards delay for the specified time, for a specified number of times.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param count
     *            - The number of times the remote command with the delay to be performed.
     * @param command
     *            - Remote command
     * @param delay
     *            - Delay time in milliseconds after command.
     */
    public void pressKeyForDiagnostic(Dut dut, Integer count, String command, Integer delay, RemoteControlType type) {

	for (int noOfTimes = 0; noOfTimes < count; noOfTimes++) {
	    LOGGER.info("Sending key [" + command + "] using remote with delay [" + delay + "].");

	    boolean sent = false;
	    int attempts = 0;

	    for (attempts = 0; !sent && (attempts < retries); attempts++) {

		sent = dut.getRemote().pressKeyAndHold(command, delay, type);

		if (!sent) {
		    LOGGER.error(String.format("%d attempt of [%s] key send failed...............", (attempts + 1),
			    command));
		}
	    }
	    LOGGER.info("Presskey status - " + sent);
	    if (!sent) {
		throw new FailedTransitionException(GeneralError.KEY_SEND_FAILURE,
			"Failed to send key [" + command + "] using remote with delay [" + delay + "].");
	    }
	}
    }

    /**
     * Press key for integers ranging from 0 - 9.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param command
     *            - Integer 0 to 9
     */
    public void pressKey(Dut dut, Integer command, RemoteControlType type) {
	LOGGER.info("Sending numeral [" + command + "] using remote.");

	boolean sent = false;
	int attempts = 0;

	for (attempts = 0; !sent && (attempts < retries); attempts++) {
	    if (!NonRackUtils.isNonRack()) {
		sent = dut.getRemote().pressKey(command.toString(), type);
	    }

	    if (!sent) {
		LOGGER.error(
			String.format("%d attempt of [%s] key send failed...............", (attempts + 1), command));
	    }
	}
    }

    /**
     * Press key and hold it down for the specified count.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param command
     *            - Key to be sent.
     * @param count
     *            - Number of times key should be repeated.
     */
    public void pressKeyAndHold(Dut dut, String command, Integer count, RemoteControlType type) {
	LOGGER.info("Sending key [" + command + "] and holding it for [" + count + "] time.");

	dut.getRemote().pressKeyAndHold(command, count, type);
    }

    /**
     * Text entry for Astro remote. The string would be parsed and the individual characters would be transformed to
     * corresponding key codes in the Astro remote.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param text
     *            - Text Message
     * 
     * @return true is text is sent
     */
    public boolean sendText(Dut dut, String text, RemoteControlType type) {
	return dut.getRemote().sendText(text, type);
    }

    /**
     * Get the image compare provider for the given dut box.
     * 
     * @param dut
     *            the given {@link Dut} instance
     * 
     * @return the {@link ImageCompareProvider} instance
     */
    public ImageCompareProvider getImageCompareProvider(Dut dut) {
	return dut.getImageCompareProvider();
    }

    /**
     * Get the ocr provider for the given dut box.
     * 
     * @param dut
     *            the given {@link Dut} instance
     * 
     * @return the {@link OcrProvider} instance
     */
    public OcrProvider getOcrProvider(Dut dut) {
	return dut.getOcrProvider();
    }

    /**
     * Get the power provider for the given dut box.
     * 
     * @param dut
     *            the given {@link Dut} instance
     * 
     * @return the {@link PowerProvider} instance
     */
    public PowerProvider getPowerProvider(Dut dut) {
	return dut.getPower();
    }

    /**
     * Get the trace provider for the given dut box.
     * 
     * @param dut
     *            the given {@link Dut} instance
     * 
     * @return the {@link TraceProvider} instance
     */
    public TraceProvider getTraceProvider(Dut dut) {
	return dut.getTrace();
    }

    /**
     * Get the remote provider for the given dut box.
     * 
     * @param dut
     *            the given {@link Dut} instance
     * 
     * @return the {@link RemoteProvider} instance
     */
    public RemoteProvider getRemoteProvider(Dut dut) {
	return dut.getRemote();
    }

    /**
     * After any key press, before an OCR check or an image compare this method enable the test to wait for a platform
     * dependent time period which ensures the loading of overlay so that OCR check won't fail.
     * 
     * @param dut
     *            Set-top box object.
     */
    public void waitBeforeUIVerification(Dut dut) {
	waitTill(getWaitValue(dut, AutomaticsConstants.WAIT_BEFORE_UI_VERIFICATION_PROP_KEY));
    }

    /**
     * After key press for a within page transition(eg:- UP) this method enable the test to wait for a platform
     * dependent time period which ensures the highlight of a section in overlay.
     * 
     * @param dut
     *            Set-top box object.
     */
    public void waitAfterWithinPageTransition(Dut dut) {
	waitTill(timeoutAfterWithinPageTransition(dut));
    }

    /**
     * After key press for an across page transition(eg:- SELECT) this method enable the test to wait for a platform
     * dependent time period which ensures the next overlay loading.
     * 
     * @param dut
     *            Set-top box object.
     */
    public void waitAfterAcrossPageTransition(Dut dut) {
	waitTill(getWaitValue(dut, AutomaticsConstants.WAIT_AFTER_ACROSS_PAGE_PROP_KEY));
    }

    /**
     * Method provides a platform dependent duration of time the test have to wait after a successful within page
     * transition(eg:- UP).
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return A platform dependent timeout for a within page transition.
     */
    public long timeoutAfterWithinPageTransition(Dut dut) {
	return getWaitValue(dut, AutomaticsConstants.WAIT_AFTER_WITHIN_PAGE_PROP_KEY);
    }

    /**
     * Executes the SNMP command and return the result.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param snmpCommandParam
     *            SNMP command Param.
     * 
     * @return SNMP command executed output.
     */
    public String executeSnmpCommand(Dut dut, SnmpParams snmpCommandParam) {

	List<SnmpParams> snmpCommandParams = new ArrayList<SnmpParams>();
	snmpCommandParams.add(snmpCommandParam);

	return executeSnmpCommands(dut, snmpCommandParams);
    }

    /**
     * Executes the SNMP command and return the result.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param snmpCommandParam
     *            SNMP command Param.
     * 
     * @return SNMP command executed output.
     */
    public String executeSnmpCommand(Dut dut, SnmpParams snmpCommandParam, String tableIndex) {

	List<SnmpParams> snmpCommandParams = new ArrayList<SnmpParams>();
	snmpCommandParams.add(snmpCommandParam);

	return executeSnmpCommands(dut, snmpCommandParams);
    }

    /**
     * Executes the list of SNMP commands and return the result.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param snmpCommandParams
     *            .
     * 
     * @return SNMP commands executed output.
     */
    public String executeSnmpCommands(Dut dut, List<SnmpParams> snmpCommandParams) {
	StringBuffer snmpCommandOutput = new StringBuffer();

	SnmpProtocol snmpVersion = AutomaticsSnmpUtils.getSnmpProtocolVersion();

	if (null == snmpProviderFactoryInstance) {
	    synchronized (lockObject) {
		if (null == snmpProviderFactoryInstance) {
		    snmpProviderFactoryInstance = BeanUtils.getSnmpFactoryProvider();
		}
	    }
	}

	LOGGER.info("Snmp protocol version: {}", snmpVersion);
	SnmpProvider snmpProviderImpl = snmpProviderFactoryInstance.getSnmpProvider(snmpVersion);

	for (SnmpParams snmpParamObj : snmpCommandParams) {
	    int retryCount = -1;
	    String result = null;
	    do {
		SnmpParams snmpParams = AutomaticsSnmpUtils.getCopyOfSnmpRequest(snmpParamObj);

		retryCount++;
		snmpParams.setSnmpVersion(snmpVersion);
		LOGGER.info("Performing {} on device {}", snmpParams.getSnmpCommand(), dut.getHostMacAddress());

		switch (snmpParams.getSnmpCommand()) {
		case GET:
		    result = snmpProviderImpl.doGet(dut, snmpParams);
		    break;
		case WALK:

		    result = snmpProviderImpl.doWalk(dut, snmpParams);
		    break;

		case TABLE:

		    result = snmpProviderImpl.doTable(dut, snmpParams);
		    break;
		case SET:

		    result = snmpProviderImpl.doSet(dut, snmpParams);
		    break;
		case BULKWALK:
		    result = snmpProviderImpl.doBulkWalk(dut, snmpParams);
		    break;
		case BULKGET:
		    result = snmpProviderImpl.doBulkGet(dut, snmpParams);
		    break;
		default:
		    break;
		}
	    } while (CommonMethods.getRetries(result, snmpVersion, retryCount));
	    snmpCommandOutput.append(result);
	}
	return snmpCommandOutput.toString();
    }

    /**
     * Executes the list of SNMP commands with different table index (other than zero )and return the result.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param snmpParams
     *            SNMP Params
     * @param snmpOptions
     *            SNMP options.
     * 
     * @return SNMP commands executed output.
     */
    public String executeSnmpCommands(Dut dut, SnmpParams snmpParams, String tableIndex) {

	StringBuffer snmpCommandOutput = new StringBuffer();

	SnmpProtocol snmpVersion = AutomaticsSnmpUtils.getSnmpProtocolVersion();

	int retryCount = -1;

	if (null == snmpProviderFactoryInstance) {
	    synchronized (lockObject) {
		if (null == snmpProviderFactoryInstance) {
		    snmpProviderFactoryInstance = BeanUtils.getSnmpFactoryProvider();
		}
	    }

	}

	SnmpProvider snmpProviderImpl = snmpProviderFactoryInstance.getSnmpProvider(snmpVersion);
	snmpParams.setSnmpVersion(snmpVersion);

	if (null != snmpProviderImpl) {
	    do {
		retryCount++;
		SnmpParams snmpParamObj = AutomaticsSnmpUtils.getCopyOfSnmpRequest(snmpParams);

		if (snmpParamObj.getSnmpCommand().equals(SnmpCommand.GET)) {
		    snmpParamObj.setMibOid(snmpParamObj.getMibOid() + "." + tableIndex);

		    snmpCommandOutput.append(snmpProviderImpl.doGet(dut, snmpParamObj));
		}

		if (snmpParamObj.getSnmpCommand().equals(SnmpCommand.SET)) {

		    snmpParamObj.setMibOid(snmpParamObj.getMibOid() + "." + tableIndex);
		    snmpCommandOutput.append(snmpProviderImpl.doSet(dut, snmpParamObj));
		}
	    } while (CommonMethods.getRetries(snmpCommandOutput.toString(), snmpVersion, retryCount));
	}
	return snmpCommandOutput.toString();
    }

    /**
     * Triggering code download(CDL) using SNMP commands by connecting SSH to the box.
     * 
     * @param device
     *            DeviceConfig on which CDL to be performed
     * @param binFileName
     *            Image name
     * @param rebootImmedeitly
     *            boolean value
     * @param ImageUpgradeMechanism
     *            mechanism
     * 
     * @return CDL SNMP command output.
     */
    public String triggerCodeDownload(Dut device, String binFileName, boolean rebootImmedeitly,
	    ImageUpgradeMechanism downloadType) {

	ImageUpgradeProviderFactory imageupgradeProviderFactory = BeanUtils.getImageUpgradeProviderFactory();
	ImageUpgradeProvider imageProvider = imageupgradeProviderFactory.getImageUpgradeProvider(downloadType, device);
	String response = null;
	if (null != imageProvider) {
	    response = imageProvider.performImageUpgrade(rebootImmedeitly, binFileName, device);
	} else {
	    LOGGER.error("ImageDownloadProvider is null. Image upgrade cannot be done!!!");
	}

	return response;
    }

    /**
     * Perform image upgrade on device.
     * 
     * @param device
     *            Device on which image upgrade to be done
     * @param imageRequestParams
     *            Holds input for image upgrade
     * @param downloadType
     *            Type of image upgrade
     * @return Response of image upgrade
     */
    public String performImageUpgrade(Dut device, ImageRequestParams imageRequestParams,
	    ImageUpgradeMechanism downloadType) {

	ImageUpgradeProviderFactory imageupgradeProviderFactory = BeanUtils.getImageUpgradeProviderFactory();
	ImageUpgradeProvider imageProvider = imageupgradeProviderFactory.getImageUpgradeProvider(downloadType, device);
	String response = null;
	if (null != imageProvider) {
	    response = imageProvider.performImageUpgrade(imageRequestParams, device);
	} else {
	    LOGGER.error("ImageUpgradeProvider is null. Image upgrade cannot be done!!!");
	}

	return response;
    }

    /**
     * Executing linux command by connecing SSH to the server.
     * 
     * @param serverDetails
     *            Linux server details.
     * @param command
     *            Linux command to be executed on the server.
     * 
     * @return the response of the command executed on the server.
     */
    public String executeCommandUsingSshConnection(IServer serverDetails, String command) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    List<String> commands = new ArrayList<String>();
	    commands.add(command);
	    response = deviceConnectionProvider.execute(serverDetails, commands, AutomaticsConstants.THIRTY_SECONDS);
	}
	return response;
    }

    /**
     * Execute command to enable debug trace for closed captioning.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     */
    public void enableClosedCaptioningTrace(Dut dut) {
	executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_ENABLE_CLOSED_CAPTIONING);
    }

    /**
     * Execute command to disable debug trace for closed captioning.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     */
    public void disableClosedCaptioningTrace(Dut dut) {
	executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_DISABLE_CLOSED_CAPTIONING);
    }

    /**
     * convert Image object to BufferedImage
     * 
     * @param image
     * @return BufferedImage object
     */
    public BufferedImage convertImageToBufferedImage(Image image) {

	LOGGER.info("STARTING IMAGE CAPTURE USING - java.awt.Image ");
	BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	Graphics bg = bi.getGraphics();
	bg.drawImage(image, 0, 0, null);
	bg.dispose();

	LOGGER.info("COMPLETED IMAGE CAPTURE USING - java.awt.Image ");
	return bi;
    }

    /**
     * Method provides a platform dependent duration of time the test have to wait for channel tuning completed.
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return A platform dependent timeout for a channel tuning complete.
     */
    public void waitChannelTuneComplete(Dut dut) {
	waitTill(getWaitValue(dut, AVConstants.WAIT_CHANNEL_TUNE_COMPLETE_PROP_KEY));
    }

    /**
     * Method provides a platform dependent duration of time the test have to wait for initiate channel tune process.
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return A platform dependent timeout for a initiate channel tune process.
     */
    public void waitInitiateChannelTune(Dut dut) {
	waitTill(getWaitValue(dut, AVConstants.WAIT_INITIATE_CHANNEL_TUNE_PROP_KEY));
    }

    /**
     * Method provides a platform dependent duration of time the test have to wait for launch any screen.
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return A platform dependent timeout for launch any screen.
     */
    public void waitLaunchAnyScreen(Dut dut) {
	waitTill(getWaitValue(dut, AVConstants.WAIT_LAUNCH_ANY_SCREEN_PROP_KEY));
    }

    /**
     * Retrieves the trace line with the given matching string.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            The string text to be searched for
     * @param timeoutInMilliSeconds
     *            Time in milliseconds to wait for the searching string.
     * 
     * @return Trace line containing the matching string
     */
    public String searchAndGetTraceLineWithMatchingString(Dut dut, String stringText, long timeoutInMilliSeconds) {
	String traceLine = searchAndGetTraceLineWithMatchingString(dut, stringText, timeoutInMilliSeconds, false);
	return traceLine;
    }

    /**
     * Retrieves the trace line with the given matching string.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            The string text to be searched for
     * @param timeoutInMilliSeconds
     *            Time in milliseconds to wait for the searching string.
     * @param isFirstMatch
     *            Whether check for first match
     * @return Trace line containing the matching string
     */
    public String searchAndGetTraceLineWithMatchingString(Dut device, String stringText, long timeoutInMilliSeconds,
	    boolean isFirstMatch) {
	String traceLine = null;
	boolean searchFromStart = false;
	boolean printLogs = true;

	try {

	    if (null != device.getTrace()) {
		traceLine = ((ConnectionTraceProvider) device.getTrace()).searchAndWaitForTrace(stringText,
			timeoutInMilliSeconds, searchFromStart, isFirstMatch, printLogs);
	    }
	} catch (IOException ioe) {

	    LOGGER.error("Failed to receive the expected trace due to read failure.", ioe);
	}

	return traceLine;
    }

    /**
     * Search in the trace log from start for the matching string and returns the string.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param stringText
     *            The string text to be searched for
     * @param timeoutInMilliSeconds
     *            Time in milliseconds to wait for the searching string.
     * 
     * @return Trace line containing the matching string
     */
    public String searchAndFindLineWithMatchingStringFromStart(Dut device, String stringText,
	    long timeoutInMilliSeconds) {
	String traceLine = null;
	boolean findFirstMatch = false;
	boolean searchFromStart = true;
	boolean printLogs = true;

	try {
	    if (null != device.getTrace()) {
		traceLine = device.getTrace().searchAndWaitForTrace(stringText, timeoutInMilliSeconds, searchFromStart,
			findFirstMatch, printLogs);
	    }

	} catch (IOException ioe) {

	    LOGGER.error("Failed to receive the expected trace due to read failure.", ioe);
	}

	return traceLine;
    }

    /**
     * Wait after hard reboot initiated based on box model.
     * 
     * @param dut
     *            The dut to be verified
     */
    public void waitAfterHardRebootCompleted(Dut dut) {

	LOGGER.info(" AutomaticsConstants.PROP_KEY_WAIT_AFTER_HARD_REBOOT_COMPLETED = "
		+ AVConstants.PROP_KEY_WAIT_AFTER_HARD_REBOOT_COMPLETED);
	LOGGER.info(" get value :  = " + getWaitValue(dut, AVConstants.PROP_KEY_WAIT_AFTER_HARD_REBOOT_COMPLETED));
	waitTill(getWaitValue(dut, AVConstants.PROP_KEY_WAIT_AFTER_HARD_REBOOT_COMPLETED));
    }

    /**
     * Clear trace client buffer.
     * 
     * @param dut
     *            The dut where the trace buffer to be cleared.
     */
    public void cleanupTraceBuffer(Dut device) {

	if (null != device.getTrace()) {
	    device.getTrace().clearBuffer();
	}
    }

    /**
     * Retrieves the last trace line.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @return Last trace line
     */
    public String getLastTraceLineFromLog(Dut device) {
	String traceLine = null;

	if (null != device.getTrace()) {
	    traceLine = ((ConnectionTraceProvider) device.getTrace()).getLastTraceLine();

	}

	return traceLine;
    }

    /**
     * Get the TR-069 parameter values using WebPA.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            List of TR-069 parameter of type WebPaParameter
     * @return The status message of setting the corresponding to TR-069 parameter
     */
    public Map<String, String> executeMultipleWebPaSetCommands(Dut dut, List<WebPaParameter> webPaParameters) {
	LOGGER.debug("STARTING METHOD: AutomaticsTapApi.executeMultipleWebPaCommands");
	Map<String, String> webPAParamResponse = new HashMap<String, String>();
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);

	List<WebPaParameter> params = serverResponse.getParams();

	if (null != params && !params.isEmpty()) {
	    for (WebPaParameter webPaParameter : params) {
		webPAParamResponse.put(webPaParameter.getName(), webPaParameter.getMessage());
	    }
	}

	LOGGER.debug("COMPLETED METHOD: AutomaticsTapApi.executeMultipleWebPaCommands");
	return webPAParamResponse;
    }

    /**
     * Set the TR-069 parameter value using WebPA.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The status of execution.
     */
    public String setTR69ParameterValuesUsingWebPA(Dut dut, List<WebPaParameter> webPaParameters) {

	LOGGER.info("Setting TR69 param values using WebPa");

	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);
	LOGGER.info("serverResponse[Message] : " + serverResponse.getMessage() + "\nserverResponse[StatusCode] "
		+ serverResponse.getStatusCode());
	return serverResponse.getMessage();
    }

    /**
     * Get the known reboot counter value
     * 
     * @return knownRebootCounter
     */
    public static synchronized int getKnownRebootCounterValue(String macAddress) {

	int currentRebootCount = 0;

	if (knownRebootCount.containsKey(macAddress)) {
	    currentRebootCount = knownRebootCount.get(macAddress);
	}

	return currentRebootCount;
    }

    /**
     * Helper method to capture and save images .
     * 
     * @param dut
     *            The dut instance from which image need to be captured
     * @param string
     *            The string to be represented as image name
     * @param boolean
     *            The boolean to be represented as flag for using default path
     */
    public void captureAndSaveImage(Dut dut, String imageName, boolean useDefaultPath) {

	LOGGER.debug("UseDefaultPath: " + useDefaultPath);

	String defaultPath = getImagePathFromProperties();

	if (!useDefaultPath || CommonMethods.isNull(defaultPath)) {
	    captureAndSaveImage(dut, imageName);
	} else {
	    imageName = System.currentTimeMillis() + "_" + imageName;
	    BufferedImage capturedScreen = captureCurrentScreen(dut);
	    saveImages(dut, capturedScreen, imageName, defaultPath);
	}
    }

    public String getImagePathFromProperties() {
	Properties imageCompareProperties;
	String saveLocation = "";
	try {
	    imageCompareProperties = FileUtils
		    .getPropertiesFromResource(AVConstants.IMAGE_COMPARE_PROPERTIES_FILE_NAME);

	    // Get the save location value
	    saveLocation = imageCompareProperties.getProperty(AutomaticsConstants.IMAGE_COMPARE_SAVELOCATION);
	} catch (IOException e) {
	    LOGGER.error("Exception occured in getImagePathFromProperties");
	    e.printStackTrace();
	}
	return saveLocation;

    }

    /**
     * Helper method to save images
     * 
     * @param dut
     *            The dut instance from which image need to be captured
     * @param BufferedImage
     *            The buffered image captured
     * @param String
     *            The string to be represented as image name
     * @param String
     *            The string to be represented as imagePath
     */

    public static void saveImages(Dut dut, BufferedImage bufferedImage, String imageName, String imagePath) {

	if (null != bufferedImage) {

	    File outputFile = null;
	    File outputDirectory = null;

	    try {

		outputDirectory = new File(imagePath);

		if (!outputDirectory.exists()) {
		    outputDirectory.mkdirs();
		}
		outputFile = new File(outputDirectory,
			dut.getFirmwareVersion().replaceAll("\\.", AutomaticsConstants.EMPTY_STRING) + "_" + imageName
				+ ".png");
		ImageIO.write(bufferedImage, "PNG", outputFile);

	    } catch (Exception e) {
		LOGGER.error("Exception occured in saveImages" + e.getMessage());
	    }
	}
    }

    /**
     * Method to reboot the stb with out delay.
     * 
     * @param dut
     *            dut to test
     */
    public void rebootWithoutWait(Dut dut) {

	LOGGER.info("Going to reboot. 1 minute wait after that");
	try {
	    executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
	    if (deviceAccessValidator.isDeviceAccessible(dut)) {
		executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
	    }

	    if (deviceAccessValidator.isDeviceAccessible(dut)) {
		try {
		    if (null != dut.getPower()) {
			dut.getPower().reboot();
		    } else {
			LOGGER.info("Reboot cannot be done via Power switch as Power Provider is not initialized");
		    }
		} catch (Exception e) {
		    LOGGER.info("Failed to reboot via all methods exiting");
		}
	    } else {
		AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());
	    }
	} catch (Exception ppex) {
	    LOGGER.error("Failed to reboot the box using the command. Rebooting using 'reboot' power switch");
	    try {
		if (null != dut.getPower()) {
		    dut.getPower().reboot();
		    // Increment the known reboot Counter.
		    AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());
		} else {
		    LOGGER.info("Reboot cannot be done via Power switch as Power Provider is not initialized");
		}
	    } catch (Exception e) {
		LOGGER.error(
			"Exception Catched - Exception occured while dut reboot throws Power Provider Exception and Telnet command execution also throws exception since box is already rebooted.",
			e);
	    }

	} finally {

	    // Fix for clear trace buffer issue
	    if (!SupportedModelHandler.isRDKB(dut)) {
		/** Restart Trace **/
		dut.getTrace().stopBuffering();
	    }
	}
    }

    /**
     * Method to check whether WebPA property is enabled or not. If enabled use WebPA for querying TR-069 parameter,
     * otherwise use ACS webservice to query TR-069 parameter.
     * 
     * @return true if WebPA is enabled, otherwise false.
     */
    public boolean isWebPaEnabled() {
	return Boolean.parseBoolean(System.getProperty("isWebPA", "false"));
    }

    /**
     * Executes linux interactive commands using SSH.
     * 
     * @param dut
     *            {@link Dut}
     * @param command
     *            Command to be executed.
     * @param expectStr
     *            String to expected during interactive execution.
     * @param options
     *            Options to be passed during interactive execution.
     * 
     * @return Execution result
     */
    public String executeCommandUsingSsh(Dut dut, String command, String expectStr, String[] options) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    response = deviceConnectionProvider.execute(dut, command, expectStr, options);
	}
	return response;
    }

    /**
     * This function returns the screen as BufferedImage
     * 
     *
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @return BufferedImage the image captured
     */
    public BufferedImage getCurrentScreen(Dut dut) {
	return dut.getVideo().getVideoImage();
    }

    /**
     * This function checks if dut is part of RFDisconnectSd rack This checks the entry for rack in Automatics
     * properties file entry
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @return boolean whether part of RFDisconnectSd
     */
    public static boolean isDeviceRFDisconnectSd(Dut dut) {

	LOGGER.info("Entering into isDeviceRFDisconnectSd()");

	boolean isCatsRfDisconnSd = false;
	try {
	    String rackList = AutomaticsPropertyUtility.getProperty("RFDisconnectSd.rack.list");

	    if (CommonMethods.isNotNull(rackList)) {
		List<String> racks = Arrays.asList(rackList.split("\\s*,\\s*"));
		String rackName = dut.getRackName();
		LOGGER.info("RackName: " + rackName);

		for (String rack : racks) {
		    if (rack.equalsIgnoreCase(rackName)) {
			isCatsRfDisconnSd = true;
			LOGGER.info("RackName: " + rackName + " is RF-DISCONNECT-SD");
		    }
		}
	    } else {
		LOGGER.info("RFDisconnectSd.rack.list property is null or not exist in Automatics properties");
	    }
	} catch (Exception e) {
	    LOGGER.error("Exiting from isDeviceRFDisconnectSd() error: " + e.getMessage(), e);
	}

	LOGGER.info("Exiting from isDeviceRFDisconnectSd()" + isCatsRfDisconnSd);

	return isCatsRfDisconnSd;
    }

    /**
     * This function that invokes URL and returns data
     * 
     * @param myURL
     *            URL to be invoked
     * @return String Text retruned from URL invocation
     */
    public static String callURL(String url) {
	LOGGER.debug("Entering method callURL");
	LOGGER.info("Requested URL:" + url);
	StringBuilder sb = new StringBuilder();
	URLConnection urlConnection = null;
	InputStreamReader inputStream = null;
	try {
	    URL urlObj = new URL(url);

	    // Get url connection
	    urlConnection = urlObj.openConnection();
	    if (null != urlConnection)
		urlConnection.setReadTimeout(60 * 1000);
	    if (null != urlConnection && null != urlConnection.getInputStream()) {
		inputStream = new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset());
		BufferedReader bf = new BufferedReader(inputStream);

		// Read content
		if (null != bf) {
		    int value;
		    while ((value = bf.read()) != -1) {
			sb.append((char) value);
		    }
		    bf.close();
		}
	    }
	    inputStream.close();
	} catch (Exception e) {
	    throw new RuntimeException("Exception while calling URL:" + url, e);
	}
	LOGGER.debug("Exiting from method callURL");
	return sb.toString();
    }

    /**
     * This method returns a client device that is connected to the given gateway device <code>serverSettop</code> after
     * locking it.
     * 
     * @param tapEnv
     * @param serverSettop
     * @author malu.s
     */
    public static List<Dut> lockAnyClientDeviceConnectedToGateway(Dut serverSettop) {
	Dut clientInstance = null;
	AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();

	List<String> clientMacList = new ArrayList<String>();
	List<Dut> clientSettops = new ArrayList<Dut>();

	try {
	    LOGGER.info("Host IP address : " + ((Device) serverSettop).getHostIpAddress());

	    if (CommonMethods.isIpv6Address(serverSettop)) {
		clientMacList = getMacsInIPV6Setup(tapEnv, serverSettop);
	    } else {
		clientMacList = getMacsInIPV4Setup(tapEnv, serverSettop);
	    }

	    if (clientMacList.isEmpty()) {
		throw new TestException("Failed to obtain any client device connected to gateway");
	    } else {
		boolean alreadyLocked = false;
		// Initialize and Lock connected Client Devices
		// We need only single client, prefer the one
		// which is already locked
		ArrayList<Dut> lockedSettops = (ArrayList<Dut>) rackInitializerInstance.getLockedSettops();
		for (Dut lockedSettop : lockedSettops) {
		    LOGGER.info("Already locked dut instance : " + lockedSettop.getHostMacAddress());
		    if (clientMacList.contains(lockedSettop.getHostMacAddress().toLowerCase())) {
			LOGGER.info("Dut " + lockedSettop.getHostMacAddress()
				+ " is already locked. So breaking from loop");
			alreadyLocked = true;
			clientSettops.add(lockedSettop);
			break;
		    }
		}
		if (!alreadyLocked) {
		    for (String device : clientMacList) {
			LOGGER.info("Going to check client instance : " + device);
			// check if dut is already locked and is in use, if
			// so return the same instance.

			LOGGER.info("Going to lock client dut : " + device);
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(device);
			RackDeviceValidationManager rackDeviceValidationManager = new RackDeviceValidationManager(
				rackInitializerInstance);
			List<DutInfo> clientDuts = rackDeviceValidationManager.manageSettopLocking(newList);
			if (null != clientDuts && clientDuts.size() > 0) {
			    clientInstance = (Device) clientDuts.get(0);
			    clientSettops.add(clientInstance);
			    rackInitializerInstance.addToSettopLockedForUse(clientInstance);
			    LOGGER.info("Adding dut : " + clientInstance.getHostMacAddress()
				    + " to already locked dut list and breaking from loop");
			    // break out from loop, since we got one client
			    // device
			    break;
			}
		    }
		}
	    }
	} catch (Exception e) {
	    LOGGER.info("ENDING METHOD: lockAnyClientDeviceConnectedToGateway()");
	    throw new TestException(e.getMessage());
	}
	LOGGER.info("ENDING METHOD: AutomaticsTapApi.lockAnyClientDeviceConnectedToGateway() : " + clientInstance);
	return clientSettops;
    }

    /**
     * Get Mac List in IPV6 setup
     * 
     * @param tapEnv
     * @param serverSettop
     * @return
     */
    private static List<String> getMacsInIPV6Setup(AutomaticsTapApi tapEnv, Dut serverSettop) {

	boolean clientFound = false;
	List<String> macList = new ArrayList<String>();

	LOGGER.info("Identified as IPv6 device");
	// for ipv6 devices we need to use command : /sbin/ip -6 neigh
	// show

	String response = tapEnv.executeCommandUsingSsh(serverSettop, "/sbin/ip -6 neigh show | grep 2603");
	LOGGER.info("Response for command : neigh show  : " + response);
	if (CommonMethods.isNotNull(response)) {
	    String patternForConnectedClient = "(2603.*)REACHABLE";
	    Pattern pattern = Pattern.compile(patternForConnectedClient);
	    Matcher matcher = pattern.matcher(response);
	    while (matcher.find()) {
		String connectedClientDetails = matcher.group();
		LOGGER.info(connectedClientDetails);
		if (CommonMethods.isNotNull(connectedClientDetails)) {
		    String patternForConnectedClientMac = "lladdr\\s(\\w*:\\w*:\\w*:\\w*:\\w*:\\w*)";

		    String macAddress = CommonMethods.patternFinder(connectedClientDetails,
			    patternForConnectedClientMac);

		    if (macAddress != null) {

			macAddress.replaceAll("\\s+", "");
			macList.add(macAddress.toLowerCase());

			clientFound = true;
		    }

		} else {
		    throw new TestException("Failed to get a proper client device connected to gateway");
		}
	    }

	    if (!clientFound && !response.contains("STALE")) {

		// Client may have IPV4 configuration under IPV6 Gateway
		macList = getMacsInIPV4Setup(tapEnv, serverSettop);
	    }
	} else {
	    // Client may have IPV4 configuration under IPV6 Gateway
	    macList = getMacsInIPV4Setup(tapEnv, serverSettop);
	}

	return macList;
    }

    /**
     * Get Mac List in IPV4 setup
     * 
     * @param tapEnv
     * @param serverSettop
     * @return
     */
    private static List<String> getMacsInIPV4Setup(AutomaticsTapApi tapEnv, Dut serverSettop) {

	List<String> macList = new ArrayList<String>();

	// send command 'arp' to list the connected devices
	LOGGER.info("Identified as IPv4 device");
	String response = tapEnv.executeCommandUsingSsh(serverSettop, "/sbin/arp -n | grep 169");
	LOGGER.info("Response for command : arp  : " + response);
	if (CommonMethods.isNotNull(response)) {
	    String patternForConnectedClientDetails = "(169.*)ether";
	    try {
		Pattern pattern = Pattern.compile(patternForConnectedClientDetails);
		Matcher matcher = pattern.matcher(response);
		while (matcher.find()) {
		    String connectedClientDetails = matcher.group();
		    LOGGER.info(connectedClientDetails);
		    if (CommonMethods.isNotNull(connectedClientDetails)) {
			String patternForConnectedClientMac = "at\\s(\\w*:\\w*:\\w*:\\w*:\\w*:\\w*)";

			String macAddress = CommonMethods.patternFinder(connectedClientDetails,
				patternForConnectedClientMac);

			if (macAddress != null) {

			    macAddress.replaceAll("\\s+", "");
			    macList.add(macAddress.toLowerCase());
			}

		    } else {
			throw new TestException("Failed to get a proper client device connected to gateway");
		    }
		}
	    } catch (Exception e) {
		LOGGER.error(e.getMessage());
	    }
	}

	return macList;
    }

    /**
     * Method to get TR69 response using host-if method
     * 
     * @param dut
     *            {@link Dut}
     * @param tr69Param
     *            TR69 Parameter
     * @return TR69 reponse value
     */
    public String getTr69CommandResponseUsingHostIf(Dut dut, String tr69Param) {

	LOGGER.info("Get Tr69 parameter {} using host-if command", tr69Param);

	String tr69CommandValue = null;

	try {
	    String response = executeCommandUsingSsh(dut, "host-if -H 127.0.0.1 -p 56981 -g \"" + tr69Param + "\"");

	    if (CommonMethods.isNotNull(response)) {

		tr69CommandValue = CommonMethods.patternFinder(response, "Value\\s*:\\s*\\\"(.*)\\\"");
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getTr69CommandResponseUsingHostIf()", exception);
	}

	LOGGER.info("Tr69 response using host-if command: {}", tr69CommandValue);

	return tr69CommandValue;
    }

    /**
     * Method to set TR69 value using host-if method
     * 
     * @param dut
     *            {@link Dut}
     * @param tr69Param
     *            TR69 Parameter
     * @return TR69 reponse value
     */
    public String setTr69ParameterUsingHostIf(Dut dut, String tr69Param, String value) {

	LOGGER.info("Setting TR69 param value using HostIf commmand: param={} value={}", tr69Param, value);
	String response = null;
	try {
	    response = executeCommandUsingSsh(dut,
		    "host-if -H 127.0.0.1 -p 56981 -s \"" + tr69Param + "\" -v \"" + value + "\"");

	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getTr69CommandResponseUsingHostIf()", exception);
	}

	LOGGER.info("TR69 Response: {}", response);

	return response;
    }

    /**
     * Method to reboot the device with out wait and returns the verification status
     * 
     * @param dut
     *            The dut instance to be checked
     * 
     *            Returns true if its able to reboot the device
     */

    public boolean rebootWithoutWaitAndGetTheRebootStatus(Dut dut) {

	boolean rebootStatus = false;

	LOGGER.info("Going to reboot.");
	try {
	    executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
	    if (deviceAccessValidator.isDeviceAccessible(dut)) {
		LOGGER.info("Going to reboot via command second time.");
		executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
	    } else {
		rebootStatus = true;
	    }

	    if (!rebootStatus) {
		try {
		    if (null != dut.getPower()) {
			LOGGER.info("Going to reboot via power code as command failed twice.");
			dut.getPower().reboot();
			waitTill(AutomaticsConstants.TWO_SECONDS);
		    } else {
			LOGGER.info("Cannot reboot via power code Power Provider is not initialized.");
		    }

		} catch (Exception e) {
		    LOGGER.info("Failed to reboot via all methods exiting");
		} finally {
		    rebootStatus = !deviceAccessValidator.isDeviceAccessible(dut);
		}
	    } else {
		AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());
		rebootStatus = true;
	    }
	} catch (Exception ppex) {
	    LOGGER.error("Failed to reboot the box using the command. Rebooting using 'reboot' power switch");
	    try {
		if (null != dut.getPower()) {
		    LOGGER.info("Going to reboot via power code as command failed twice and Power Switch failed once.");
		    dut.getPower().reboot();
		    // Increment the known reboot Counter.
		    AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());
		} else {
		    LOGGER.info("Cannot reboot via power code Power Provider is not initialized.");
		}
	    } catch (Exception e) {
		LOGGER.error(
			"Exception Catched - Exception occured while dut reboot throws Power Provider Exception and Telnet command execution also throws exception since box is already rebooted.",
			e);
	    } finally {
		rebootStatus = !deviceAccessValidator.isDeviceAccessible(dut);
	    }
	} finally {
	    // Fix for clear trace buffer issue
	    if (!SupportedModelHandler.isRDKB(dut) && !SupportedModelHandler.isRDKC(dut)) {
		dut.getTrace().stopBuffering();
	    }
	}
	LOGGER.info("Final Reboot Status." + rebootStatus);
	return rebootStatus;

    }

    /**
     * Method to update the execution results to ALM
     * 
     * @param almTestDetails
     */
    public void updateExecutionStatusToALM(AlmTestDetails almTestDetails) {

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	String zephyrIntegrationToolUrl = null;

	if (almTestDetails != null) {

	    try {
		zephyrIntegrationToolUrl = getSTBPropsValue(ZEPHYR_INTEGRATION_TOOL_URL);
		serverCommunicator = new ServerCommunicator();

		LOGGER.debug("zephyrIntegrationToolUrl-" + zephyrIntegrationToolUrl);

		serverResponse = serverCommunicator.postDataToServer(zephyrIntegrationToolUrl,
			almTestDetails.getALMJsonAsString(), "POST", AutomaticsConstants.ONE_MINUTE, null);

		LOGGER.info("Obtained the following response for ALM update - " + serverResponse);

	    } catch (FailedTransitionException failedTransitionException) {
		LOGGER.error("ZIT url not obtained from stb.properties. Skipping ALM update",
			failedTransitionException);
	    }
	}
    }

    /**
     * Method to get the channel details DO for the given channel.
     * 
     * @param settopObject
     * @param channelNumber
     * @return channelDetailsDO - null will be returned, if no matching entries are found
     */
    public ChannelDetailsDO getChannelDetailsForChannelNumber(Dut settopObject, String channelNumber) {

	ChannelDetailsDO channelDetailsDO = null;

	if (channelDetailsMap.containsKey(settopObject.getHostMacAddress())) {

	    channelDetailsDO = channelDetailsMap.get(settopObject.getHostMacAddress())
		    .get(Integer.parseInt(channelNumber));
	}

	return channelDetailsDO;
    }

    /**
     * Method to get the channel details DO for the given locator uri
     * 
     * @param settopObject
     * @param locatorUri
     * @return channelDetailsDO - null will be returned, if no matching entries are found
     */
    public ChannelDetailsDO getChannelDetailsForLocatorUri(Dut settopObject, String locatorUri) {

	ChannelDetailsDO channelDetailsDO = null;

	TreeMap<Integer, ChannelDetailsDO> channelDetailsMapForBox = null;

	if (channelDetailsMap.containsKey(settopObject.getHostMacAddress())) {

	    channelDetailsMapForBox = channelDetailsMap.get(settopObject.getHostMacAddress());

	    for (Integer channelNumber : channelDetailsMapForBox.keySet()) {

		channelDetailsDO = channelDetailsMapForBox.get(channelNumber);

		if ((CommonMethods.isNotNull(channelDetailsDO.getLocatorUri())
			&& channelDetailsDO.getLocatorUri().equalsIgnoreCase(locatorUri))
			|| (CommonMethods.isNotNull(channelDetailsDO.getLocatorUriCIF())
				&& channelDetailsDO.getLocatorUriCIF().equalsIgnoreCase(locatorUri))) {
		    break;
		} else {
		    channelDetailsDO = null;
		}
	    }
	}

	return channelDetailsDO;
    }

    /**
     * Method to get the channel details DO for the given ocapLocator
     * 
     * @param settopObject
     * @param ocapLocator
     *            - Eg: ocap://0x23ab
     * @return channelDetailsDO - null will be returned, if no matching entries are found
     */
    public ChannelDetailsDO getChannelDetailsForOcapLocator(Dut settopObject, String ocapLocator) {

	ChannelDetailsDO channelDetailsDO = null;

	TreeMap<Integer, ChannelDetailsDO> channelDetailsMapForBox = null;

	if (channelDetailsMap.containsKey(settopObject.getHostMacAddress())) {

	    channelDetailsMapForBox = channelDetailsMap.get(settopObject.getHostMacAddress());

	    for (Integer channelNumber : channelDetailsMapForBox.keySet()) {

		channelDetailsDO = channelDetailsMapForBox.get(channelNumber);

		if (CommonMethods.isNotNull(channelDetailsDO.getOcapLocator())
			&& channelDetailsDO.getOcapLocator().equalsIgnoreCase(ocapLocator)) {
		    break;
		} else {
		    channelDetailsDO = null;
		}
	    }
	}

	return channelDetailsDO;
    }

    /**
     * Method to get the list of all channel numbers
     * 
     * @param settopObject
     * @param channelTypes
     * @return List<String> channelNumberList
     */
    public List<String> getListOfAllChannelNumbers(Dut settopObject, ChannelTypes channelTypes) {

	List<String> channelNumberList = null;

	ChannelDetailsDO channelDetailsDO = null;

	TreeMap<Integer, ChannelDetailsDO> channelDetailsMapForBox = null;

	boolean shouldAdd = false;

	if (channelDetailsMap.containsKey(settopObject.getHostMacAddress())) {

	    channelDetailsMapForBox = channelDetailsMap.get(settopObject.getHostMacAddress());

	    for (Integer channelNumber : channelDetailsMapForBox.keySet()) {

		shouldAdd = false;

		channelDetailsDO = channelDetailsMapForBox.get(channelNumber);

		if (ChannelTypes.VIDEO_ON_DEMAND.compareTo(channelTypes) == 0 && channelDetailsDO.isVodChannel()) {
		    shouldAdd = true;
		} else if (ChannelTypes.HIGH_DEFINITION.compareTo(channelTypes) == 0 && !channelDetailsDO.isVodChannel()
			&& channelDetailsDO.isHdChannel()) {
		    shouldAdd = true;
		} else if (ChannelTypes.STANDARD_DEFINITION.compareTo(channelTypes) == 0
			&& !channelDetailsDO.isVodChannel() && !channelDetailsDO.isHdChannel()) {
		    shouldAdd = true;
		}

		if (shouldAdd) {
		    if (channelNumberList == null) {
			channelNumberList = new ArrayList<String>();
		    }

		    channelNumberList.add(channelDetailsDO.getChannelNumber());
		}
	    }
	}

	return channelNumberList;
    }

    /**
     * Method to update wifi performance test data to Automatics
     * 
     * @param dutAccount
     * @param dut
     *            :- dut instance
     * @param performanceDataJson
     *            :- wifi performance test parameters and values
     */
    public void updatePerformanceTestDataToAutomatics(DutAccount dutAccount, Dut dut, JSONObject performanceDataJson) {

	StringBuffer targetUrl = null;
	String contentValue = null;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	try {

	    // Set the post data content
	    contentValue = getPerformanceTestJsonToUpdateAutomatics(dutAccount, dut, performanceDataJson);

	    LOGGER.debug("Result Details ->" + contentValue);

	    // Load the STB Property file
	    AutomaticsPropertyUtility.loadProperties();

	    serverCommunicator = new ServerCommunicator(LOGGER);

	    targetUrl = new StringBuffer(AutomaticsPropertyUtility.getProperty("automatics.url"));
	    targetUrl.append("updatePerformanceTest.htm");

	    // Get the build parameter details from tets manager for the given
	    // id
	    serverResponse = serverCommunicator.postDataToServer(targetUrl.toString(), contentValue, "POST", 120000,
		    null);

	    if (serverResponse != null) {
		if (serverResponse.getResponseCode() == HttpStatus.SC_OK) {
		    LOGGER.info("Wifi Performanc Test Data updated successfully");
		} else {
		    LOGGER.error("FAILED TO UPDATE WIFI PERFORMANCE TEST DATA IN AUTOMATICS."
			    + serverResponse.getResponseStatus());
		}
	    }

	} catch (Exception exception) {
	    String errorMessage = "Excpetion caught while updating wifi performance test data to Automatinc---> ";
	    LOGGER.error(errorMessage, exception.getMessage());
	    throw new TestException(errorMessage + exception.getMessage());
	}
    }

    /**
     * Method to create the payload to update wifi performance test data to Automatics
     * 
     * @param dutAccount
     *            :- Home acount details of Pivot DUT
     * @param dut
     *            :- Dut instance
     * @param performanceDataJson
     *            :- wifi performance params and values
     * @return
     */
    private String getPerformanceTestJsonToUpdateAutomatics(DutAccount dutAccount, Dut dut,
	    JSONObject performanceDataJson) {

	JSONObject dutDetails = null;
	JSONObject deviceDetails = null;
	JSONObject payLoad = null;

	String JOB_ID = "JOB_ID";
	String BUILD_NAME = "BUILD_NAME";
	String MAC = "MAC";

	String DUT_Details = "DUT Details";
	String DEVICE_DETAILS = "DeviceConfig Details";
	String TEST_DATA = "Test data";

	try {

	    payLoad = new JSONObject();
	    dutDetails = new JSONObject();

	    dutDetails.put(JOB_ID,
		    Integer.parseInt((System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0"))));
	    dutDetails.put(BUILD_NAME,
		    ((dutAccount.getPivotDut().getFirmwareVersion() != null)
			    ? dutAccount.getPivotDut().getFirmwareVersion()
			    : ""));
	    dutDetails.put(MAC,
		    ((dutAccount.getPivotDut().getHostMacAddress() != null)
			    ? dutAccount.getPivotDut().getHostMacAddress()
			    : ""));

	    deviceDetails = new JSONObject();

	    deviceDetails.put(BUILD_NAME, ((dut.getFirmwareVersion() != null) ? dut.getFirmwareVersion() : ""));
	    deviceDetails.put(MAC, ((dut.getHostMacAddress() != null) ? dut.getHostMacAddress() : ""));

	    payLoad.put(DUT_Details, dutDetails);
	    payLoad.put(DEVICE_DETAILS, deviceDetails);
	    payLoad.put(TEST_DATA, performanceDataJson);

	} catch (JSONException jsonException) {
	    LOGGER.error(jsonException.getMessage() + ". Issue with Wifi performance test data json");
	}

	return payLoad.toString();
    }

    /**
     * Method to execute commands on natted clients in One IP Architecture
     * 
     * @param dut
     *            :- Dut instance
     * @param command
     *            :- Command to execute
     * @return response of execution
     */
    public String executeCommandOnOneIPClients(Dut dut, String command) {

	return executeCommandOnOneIPClients(dut, new String[] { command });
    }

    /**
     * Method to execute commands on natted clients in One IP Architecture
     * 
     * @param dut
     *            :- Dut instance
     * @param commands
     *            :- Commands to be executed on client.
     * @return response of execution
     */
    public String executeCommandOnOneIPClients(Dut dut, String[] commands) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    List<String> commandList = Arrays.asList(commands);
	    response = deviceConnectionProvider.execute((Device) dut, commandList);
	}
	return response;
    }

    /**
     * Method to execute commands on natted clients in One IP Architecture
     * 
     * @param dut
     *            :- Dut instance
     * @param commands
     *            :- Commands to be executed on client.
     * @return response of execution
     */
    public String executeCommandOnOneIPClients(Dut dut, String[] commands, int timeout) {
	return executeCommandOnOneIPClients(dut, commands);
    }

    /**
     * Created for CUJO execution Executes command in connected client and prints result to console
     * 
     * @param dut
     *            Dut object
     * @param command
     *            COmmand to execute
     * @return
     */
    public void executeCommandOnOneIPClientsAndPrintToConsole(Dut dut, String command) {
	executeCommandOnOneIPClients(dut, command);

    }

    /**
     * Helper method to update entries in dynamic table by providing Table Name and their Parameter Name & Value pairs
     * (WebPA PUT)
     * 
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * @param paramKeyValue
     *            Map representing the parameter which is in key value pair.
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Susheela C
     */
    public WebPaServerResponse putWebpaTableParamUsingRestApi(Dut dut, String tableName,
	    Map<String, HashMap<String, List<String>>> paramKeyValue) {
	LOGGER.debug("STARTING METHOD: putWebpaTableParamUsingRestApi");
	WebPaServerResponse response = new WebPaServerResponse();
	try {
	    if (!paramKeyValue.isEmpty()) {
		response = WebPaConnectionHandler.get().putWebPaParameterValue(dut, tableName, paramKeyValue);
	    } else {
		throw new TestException("Empty paramKeyValue received...");
	    }
	} catch (Exception exception) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA PUT PARAMS REQUEST: "
		    + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}
	LOGGER.debug("ENDING METHOD: putWebpaTableParamUsingRestApi");
	return response;
    }

    /**
     * Helper method to add new entry to dynamic table by providing Table Name and their Parameter Name & Value pairs
     * (WebPA POST)
     * 
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * @param paramKeyValue
     *            Map representing the parameter which is in key value pair.
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Susheela C
     */
    public WebPaServerResponse postWebpaTableParamUsingRestApi(Dut dut, String tableName,
	    Map<String, List<String>> paramKeyValue) {
	LOGGER.debug("STARTING METHOD: postWebpaTableParamUsingRestApi");
	WebPaServerResponse response = null;
	try {
	    if (!paramKeyValue.isEmpty()) {
		response = WebPaConnectionHandler.get().postWebPaParameterValue(dut, tableName, paramKeyValue);
	    } else {
		throw new TestException("Empty paramKeyValue received...");
	    }
	} catch (Exception exception) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA POST PARAMS REQUEST: "
		    + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}
	LOGGER.debug("ENDING METHOD: postWebpaTableParamUsingRestApi");
	return response;
    }

    /**
     * Helper method to add new entry to dynamic table by providing Table Name and their Parameter Name & Value pairs
     * (WebPA GET)
     * 
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Susheela C
     */
    public WebPaEntityResponse getWebPaTableParamValuesUsingRestApi(Dut dut, String tableName) {
	LOGGER.debug("STARTING METHOD: getWebPaTableParamValuesUsingRestApi");
	WebPaEntityResponse response = null;
	try {
	    response = WebPaConnectionHandler.get().getWebPaTableParamValue(dut, tableName);
	} catch (Exception exception) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA GET PARAMS REQUEST: "
		    + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getWebPaTableParamValuesUsingRestApi");
	return response;
    }

    /**
     * Helper method to add new entry to dynamic table by providing Table Name and their Parameter Name & Value pairs
     * (WebPA GET)
     * 
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Susheela C
     */
    public WebPaServerResponse getWebPaParamValuesUsingRestApi(Dut dut, String tableName) {
	LOGGER.debug("STARTING METHOD: getWebPaParamValuesUsingRestApi");
	WebPaServerResponse response = null;
	try {
	    response = WebPaConnectionHandler.get().getWebPaParamValue(dut, new String[] { tableName });
	} catch (Exception exception) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA GET PARAMS REQUEST: "
		    + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getWebPaParamValuesUsingRestApi");
	return response;
    }

    /**
     * Helper method to delete entries in dynamic table by providing Table Name (WebPA DELETE)
     * 
     * @param dut
     *            The device under test
     * @param tableNameWithRow
     *            String representing the Table Name along with row id.
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Susheela C
     */
    public WebPaServerResponse deleteTableRowUsingRestApi(Dut dut, String tableNameWithRow) {
	LOGGER.debug("STARTING METHOD: deleteTableRowUsingRestApi");
	WebPaServerResponse response = null;
	try {
	    response = WebPaConnectionHandler.get().deleteWebPaParameterValue(dut, tableNameWithRow);
	} catch (Exception exception) {
	    LOGGER.error(
		    "FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA DELETE REQUEST: " + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}
	LOGGER.debug("ENDING METHOD: deleteTableRowUsingRestApi");
	return response;
    }

    /**
     * Helper method to set WebPa parameter values
     * 
     * @param dut
     *            The device under test
     * @param webPaParameters
     *            The WebPa parameter to set
     * @return The response of the execution of webpa parameter.
     * 
     * @author Susheela C
     */
    public WebPaServerResponse setWebPaParameterValuesUsingRestApi(Dut dut, List<WebPaParameter> webPaParameters) {
	WebPaServerResponse response = null;
	try {
	    LOGGER.debug("STARTING METHOD: setWebPaParameterValuesUsingRestApi");
	    response = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);

	} catch (Exception exception) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURRED WHILE PROCESSING WEBPA SET REQUEST: " + exception.getMessage());
	    throw new TestException(exception.getMessage());
	}

	LOGGER.debug("ENDING METHOD: setWebPaParameterValuesUsingRestApi");
	return response;
    }

    /**
     * Get the TR-069 parameter values using WebPA and executing the webpa command
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The value corresponding to TR-069 parameter
     * 
     * @author sgunas200
     */
    public boolean getWebPaAttributeValues(Dut dut, String[] parameter) {
	LOGGER.debug("STARTING METHOD: getTR69ParameterValuesUsingWebPA");
	boolean status = false;
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut, parameter);
	if (serverResponse.getStatusCode() == HttpStatus.SC_OK) {
	    status = true;
	}
	LOGGER.debug("ENDING METHOD: getTR69ParameterValuesUsingWebPA");
	return status;
    }

    /**
     * Helper method to set WebPa Attribute Values and Execute the command
     * 
     * @param dut
     *            The device under test
     * @param webPaParameters
     *            The WebPa parameter to set
     * @return The response of the execution of webpa parameter.
     * 
     * @author sgunas200
     */
    public boolean setWebPaAttributeValues(Dut dut, List<WebPaParameter> webPaParameters) {
	LOGGER.debug("STARTING METHOD: setWebPaAttributeValues");
	boolean status = false;
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().setWebPaParameterValue(dut, webPaParameters);
	if (serverResponse.getStatusCode() == HttpStatus.SC_OK) {
	    status = true;
	}
	LOGGER.debug("ENDING METHOD: setWebPaAttributeValues");
	return status;
    }

    /**
     * Executes the command & the options inside the ATOM Console. The Command to be
     * 
     * @param dut
     *            {@link Dut}
     * @param command
     *            Command to be executed.
     * @param expectStr
     *            String to expected during interactive execution.
     * @param options
     *            Options to be passed during interactive execution.
     * 
     * @return Execution result
     */
    public String executeCommandInsideAtomConsoleUsingExpect(Dut dut, String atomServerIp, String command) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    response = deviceConnectionProvider.executeInsideAtomConsoleUsingExpect(dut, atomServerIp, command);
	}
	return response;
    }

    /**
     * method to get the iponly channels from grid web service
     * 
     * @param dut
     * @return
     */
    public ArrayList<String> getIpOnlyChannels(Dut dut) {

	ArrayList<String> ipOnlyChannels = new ArrayList<String>();
	TreeMap<Integer, ChannelDetailsDO> channelMap = null;

	if (channelDetailsMap.containsKey(dut.getHostMacAddress())) {
	    channelMap = channelDetailsMap.get(dut.getHostMacAddress());
	    for (Integer channel : channelMap.keySet()) {
		if (CommonMethods.isNull(channelMap.get(channel).getOcapLocator())) {
		    ipOnlyChannels.add(channelMap.get(channel).getChannelNumber());
		}
	    }
	}
	LOGGER.info("IP Channels : " + ipOnlyChannels.toString());
	return ipOnlyChannels;
    }

    /**
     * Method to verify the ip only channel from grid web service
     * 
     * @param dut
     * @param channelNumber
     * @return
     */
    public boolean isIpOnlyChannel(Dut dut, String channelNumber) {

	boolean isIpOnlyChannel = false;
	TreeMap<Integer, ChannelDetailsDO> channelMap = null;

	if (CommonMethods.isNotNull(channelNumber) && channelDetailsMap.containsKey(dut.getHostMacAddress())) {
	    channelMap = channelDetailsMap.get(dut.getHostMacAddress());
	    isIpOnlyChannel = CommonMethods.isNull(channelMap.get(Integer.parseInt(channelNumber)).getOcapLocator());
	}
	LOGGER.info("Is " + channelNumber + " IP Only Channel? " + isIpOnlyChannel);
	return isIpOnlyChannel;
    }

    /**
     * Trace blocks while waiting/searching for an expression from start. Note:- Make sure you call the startBuffering()
     * if you need to search in buffer
     * 
     * @param dut
     *            The {@link Dut} object
     * @param regExpression
     *            Regular expression to wait for.
     * @param timeoutInMilliSeconds
     *            timeout for which to wait for
     * 
     * @return true if the searched text is found, else will return false
     */
    public boolean searchAndWaitForTraceFromStart(Dut dut, String stringText, long timeoutInMilliSeconds) {
	boolean status = false;
	status = CommonMethods
		.isNotNull(searchAndFindLineWithMatchingStringFromStart(dut, stringText, timeoutInMilliSeconds));
	return status;
    }

    /**
     * The below method executes commands on can connected XW4 devices.
     * 
     * @param camDevice
     *            Setttop object of cam device
     * @param commandList
     *            Commcasd list to be executed
     */
    public static String executeCommandInRDKCConnectedXW4(Dut camDevice, String command) {
	String[] commandList = { command };
	return executeCommandInRDKCConnectedXW4((Device) camDevice, commandList);
    }

    /**
     * The below method executes commands on can connected XW4 devices.
     * 
     * @param camDevice
     *            Setttop object of cam device
     * @param commandList
     *            Commcasd list to be executed
     */
    public static String executeCommandInRDKCConnectedXW4(Dut camDevice, String[] commands) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    List<String> commandList = Arrays.asList(commands);
	    response = deviceConnectionProvider.execute((Device) camDevice, commandList);
	}
	return response;
    }

    /**
     * Method to execute the WebPA command and get the appropriate error message in case of any failure.
     * 
     * @param dut
     *            The {@link Dut} to be used.
     * @param parameters
     *            The TR-181 parameter to be verified.
     * @return List of {@link WebPaParameter} with WebPA response.
     */
    public List<WebPaParameter> executeWebPaGetCommand(Dut dut, String... parameters) {
	List<WebPaParameter> params = null;
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut, parameters);

	if (serverResponse.getStatusCode() == HttpStatus.SC_OK) {
	    params = serverResponse.getParams();
	} else {
	    JSONObject errorMessage = new JSONObject();
	    try {
		errorMessage.put("statusCode", serverResponse.getStatusCode());
		errorMessage.put("message", serverResponse.getMessage());
	    } catch (JSONException e) {
		LOGGER.error("JSON Exception while processing JSON Response from WebPA", e);
	    }

	    throw new TestException(errorMessage.toString());
	}
	return params;
    }

    /**
     * Method that locks the given dut instance.
     * 
     * @param dut
     */
    public boolean lockDevice(Dut dut) {
	boolean lockStatus = false;
	DeviceManager deviceManager = DeviceManager.getInstance();
	if (!deviceManager.isLocked(dut)) {
	    deviceManager.lock(dut);
	    LOGGER.info("Box locked");
	    lockStatus = true;
	} else {
	    lockStatus = false;
	    LOGGER.info("Box already locked");
	}
	return lockStatus;
    }

    /**
     * Method to press key using key simulator
     * 
     * @param dut
     * @param tapEnv
     * @param remoteCommand
     * @return status
     * 
     * @author pganes200
     */
    public boolean pressKeyUsingKeySimulator(Dut dut, String remoteCommand) {
	boolean status = false;
	String response = executeCommandUsingSsh(dut, CMD_TO_VERIFY_KEYSIMULATOR).trim();
	if (response.equalsIgnoreCase("Yes")) {
	    response = executeCommandUsingSsh(dut, KEY_SIMULATOR + " " + remoteCommand);
	    status = response.contains("Sending Key");
	} else {
	    LOGGER.error("KeySimulator is not available in the DeviceConfig");
	}

	return status;
    }

    /**
     * Method to press key using key simulator
     * 
     * @param dut
     * @param tapEnv
     * @param remoteCommand
     * @param repeatCount
     * @return status
     * 
     * @author smalu001c
     */
    public boolean pressKeyUsingKeySimulator(Dut dut, String remoteCommand, int repeatCount) {
	boolean status = false;
	for (int i = 0; i < repeatCount; i++) {
	    status &= pressKeyUsingKeySimulator(dut, remoteCommand);
	    waitTill(AutomaticsConstants.ONE_SECOND * 3);
	}

	return status;
    }

    /**
     * API to check if the given value has been provided as build appender to the test cases
     * 
     * @param value
     *            Expected build appender value
     * @return True/False based on whether build appender found or not
     */
    public boolean isExpectedBuildAppender(String value) {
	boolean isGivenValue = false;
	String userProvidedValue = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_APPENDER);
	if (CommonMethods.isNotNull(userProvidedValue)) {
	    if (userProvidedValue.contains(AutomaticsConstants.COMMA)) {
		String[] appenderList = userProvidedValue.split(AutomaticsConstants.COMMA);
		for (String appenderValue : appenderList) {
		    if (CommonMethods.isNotNull(appenderValue) && appenderValue.trim().equalsIgnoreCase(value)) {
			isGivenValue = true;
			break;
		    }
		}
	    } else {
		if (CommonMethods.isNotNull(userProvidedValue) && userProvidedValue.trim().equalsIgnoreCase(value)) {
		    isGivenValue = true;
		}
	    }
	}
	return isGivenValue;
    }

    /**
     * 
     * Method to check if given partner ID is valid
     * 
     * @param dut
     *            Dut object
     * @param partnerId
     *            partner Id string
     * @return
     */
    public boolean isPartnerIdValidForRdkDevice(Dut dut, String partnerId) {
	boolean isValid = false;
	if (CommonMethods.isNotNull(partnerId)) {
	    partnerId = partnerId.toLowerCase() + AutomaticsConstants.COMMA;
	    if (SupportedModelHandler.isRDKV(dut)) {
		String configuredPartners = AutomaticsPropertyUtility.getProperty(STB_PROPERTY_RDKV_PARTNER)
			+ AutomaticsConstants.COMMA;
		if (CommonMethods.patternMatcher(configuredPartners, partnerId)) {
		    isValid = true;
		}
	    } else if (SupportedModelHandler.isRDKB(dut)) {
		String configuredPartners = AutomaticsPropertyUtility.getProperty(STB_PROPERTY_RDKB_PARTNER)
			+ AutomaticsConstants.COMMA;
		if (CommonMethods.patternMatcher(configuredPartners, partnerId)) {
		    isValid = true;
		}
	    }
	}
	return isValid;
    }

    /**
     * Executes commands within device.
     * 
     * @param dut
     *            Device in which command to be executed.
     * @param commands
     *            Linux command.
     * 
     * @return the output of linux commands executed.
     */
    public String executeCommandAndReturnResponseWithBanner(Dut dut, String command) {
	String response = null;
	if (null != deviceConnectionProvider) {
	    List<String> commandList = new ArrayList<>();
	    commandList.add(command);
	    response = deviceConnectionProvider.execute((Device) dut, ExecuteCommandType.COMMAND_RESPONSE_WITH_BANNER,
		    commandList);
	}
	return response;
    }

    public boolean compareImageOnTargetRegion(Dut dut, RegionInfo regionInfo, long timeOut) {

	boolean status = false;
	BufferedImage referenceImage = regionInfo.getRefImage();
	BufferedImage liveImage = dut.getVideo()
		.getVideoImage(new Dimension(referenceImage.getWidth(), referenceImage.getHeight()));

	BufferedImage currentImage = liveImage.getSubimage(regionInfo.getX(), regionInfo.getY(), regionInfo.getWidth(),
		regionInfo.getHeight());
	BufferedImage expectedImage = referenceImage.getSubimage(regionInfo.getX(), regionInfo.getY(),
		regionInfo.getWidth(), regionInfo.getHeight());

	MagickComparison magickComparison = new MagickComparison();

	if (magickComparison.compare(expectedImage, currentImage)) {
	    status = true;
	} else {
	    saveImages(dut, currentImage, expectedImage, null, null);
	}

	return status;
    }

    private synchronized int getFailureCount() {
	return ++counter;
    }

    /**
     * Saves actual and expected image.
     *
     * @param actual
     *            Actual image
     * @param expected
     *            Expected image
     * @param targetInfo
     *            Targeted region info
     * @param refInfo
     *            Actual region info
     */
    public void saveImages(Dut dut, BufferedImage actual, BufferedImage expected, RegionInfo targetInfo,
	    ImageCompareRegionInfo refInfo) {
	LOGGER.debug("saveImages()");

	String imageSaveDirectory = dut.getImageCompareProvider().getImageSaveLocation();

	if ((imageSaveDirectory == null) || (imageSaveDirectory.isEmpty())) {
	    LOGGER.warn("[IMAGECOMPARE] imagecompare directory not set");

	    return;
	}

	LOGGER.info("imageSaveLocation : " + imageSaveDirectory);

	File outputDir = new File(imageSaveDirectory);

	int failureCount = getFailureCount();
	String filename = String.format("%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS-%2$04d",
		new Object[] { Calendar.getInstance(), Integer.valueOf(failureCount) });

	if (refInfo != null) {
	    filename = filename + "-" + refInfo.getName();
	}

	LOGGER.debug("filename " + filename);

	File actualFile = new File(outputDir, filename + "-actual.jpg");
	File expectedFile = new File(outputDir, filename + "-expected.jpg");

	if ((outputDir.isDirectory()) || (outputDir.mkdirs())) {

	    if (saveImage(actualFile, actual, (targetInfo != null) ? targetInfo : refInfo)) {
		LOGGER.info("[IMAGECOMPARE] Saved actual image: " + actualFile.getName());
	    } else {
		LOGGER.error("[IMAGECOMPARE] Failed to save actual image.");
	    }

	    if (saveImage(expectedFile, expected, refInfo)) {
		LOGGER.info("[IMAGECOMPARE] Saved expected image: " + expectedFile.getName());
	    } else {
		LOGGER.error("[IMAGECOMPARE] Failed to save expected image.");
	    }
	} else {
	    LOGGER.error("[IMAGECOMPARE] Failed to create imagecompare directory.");
	}
    }

    /**
     * Method to save any image and marks the reqion.
     *
     * @param outputFile
     *            Output file name
     * @param image
     *            Image to save
     * @param info
     *            Region info
     *
     * @return True if image is successfully saved
     */
    private boolean saveImage(File outputFile, BufferedImage image, RegionInfo info) {
	boolean savedFile = false;

	try {

	    if (info != null) {
		Graphics g = image.getGraphics();
		g.setXORMode(Color.RED);

		if (g instanceof Graphics2D) {
		    Stroke stroke = new BasicStroke(1.5F, 2, 2);
		    ((Graphics2D) g).setStroke(stroke);
		}

		g.drawRect(info.getX(), info.getY(), info.getWidth(), info.getHeight());
	    }

	    savedFile = ImageIO.write(image, "JPEG", outputFile);
	} catch (IOException ioe) {
	    LOGGER.error(ioe.getMessage(), ioe);
	}

	return savedFile;
    }

    /**
     * API to execute command in serial console
     * 
     * @param dut
     *            Dut object
     * @param command
     *            Command to execute
     * @param timeout
     *            Time to complete execution and retrieve result
     * @return
     */

    public String executeCommandInSerialConsole(Dut dut, String command, long timeout) {
	SerialCommandExecutionProvider serialCommandExecutionProvider = BeanUtils.getSerialCommandExecutionProvider();
	return serialCommandExecutionProvider.executeCommandInSerialConsole(dut, command, timeout);
    }

    /**
     * Checks if the expected image from the specified region is on the current screen. Timeouts are disregarded.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param icRegionXmlPath
     *            The image compare region xml path
     * @param icRegionName
     *            The image compare region name
     * @param preCapturedImage
     *            The precaptured reference image
     * 
     * @return true if the expected image for the region is on the current screen. Otherwise false is returned
     */
    public boolean isRegionOnScreenNow(final Dut dut, final String icRegionXmlPath, final String icRegionName,
	    final BufferedImage preCapturedImage) {

	final boolean[] responseData = new boolean[1];

	// Timer thread that for controlling the IC (image Compare) response time.
	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    ImageCompareRegionInfo icRegionInfo = (ImageCompareRegionInfo) ImageRegionUtils
			    .getRegionInfo(getResourceLocator().getResource(icRegionXmlPath, dut), icRegionName);
		    responseData[0] = dut.getImageCompareProvider().isRegionOnScreenNow(preCapturedImage, icRegionInfo);

		    LOGGER.info("isRegionOnScreenNow(Dut,String,String) - Obtained image comapre response as - "
			    + responseData[0]);
		} catch (ImageCompareException e) {
		    LOGGER.error("image comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE, e);
		}

	    }
	};

	try {
	    timerThread.start();
	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET IMAGE COMPARISON RESPONSE.");
	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("isRegionOnScreenNow(). Join operation interrupted.");
	}

	return responseData[0];

    }

    /**
     * Waits for the specified region to be on screen for the specified timeout. This function uses the match percent,
     * RGB tolerances, and x & y tolerances from the imgXMLPath file.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param imageXml
     *            The image xml file name.
     * @param regionName
     *            The name of the region to be compared.
     * @param timeOut
     *            The timeout in milliseconds.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public boolean waitForImageRegion(final Dut dut, final String imageXml, final String regionName,
	    final long timeOut) {
	final boolean[] responseData = new boolean[1];

	if (NonRackUtils.isNonRack()) {
	    return false;
	}
	// Timer thread that for controlling the IC (image Compare) response
	// time.

	Thread timerThread = new Thread() {

	    public void run() {
		try {

		    responseData[0] = dut.getImageCompareProvider().waitForImageRegion(imageXml, regionName, timeOut);
		    LOGGER.info("waitForImageRegion(Dut,String,String) - Obtained IC response as - " + responseData[0]);
		} catch (ImageCompareException e) {
		    LOGGER.error("image comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE, e);
		}
	    }
	};
	try {
	    timerThread.start();
	    LOGGER.info("WAIT FOR MAXIMUM " + (timeOut + AutomaticsConstants.THIRTY_SECONDS / 60000)
		    + " minutes TO GET IMAGE COMPARISON RESPONSE.");
	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(timeOut + AutomaticsConstants.THIRTY_SECONDS);
	    // Interrupt the thread.
	    timerThread.interrupt();
	} catch (InterruptedException e) {
	    LOGGER.trace("waitForImageRegion(Dut,String,String,long). Join operation interrupted.");
	}
	return responseData[0];

    }

    /**
     * Compares the current image with specified image to be on screen. This function uses the match percent, RGB
     * tolerances, and x & y tolerances from the imgXMLPath file.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param sourceImage
     *            The source image.
     * @param imageXml
     *            The referenced image to be compared.
     * @param regionName
     *            The name of the region to be compared.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public boolean compareImages(Dut dut, BufferedImage bufferedImage, String imageXml, String regionName) {
	final boolean[] responseData = new boolean[1];

	// Timer thread that for controlling the IC (image Compare) response time.
	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    ImageCompareRegionInfo regionInfo = (ImageCompareRegionInfo) ImageRegionUtils
			    .getRegionInfo(getResourceLocator().getResource(imageXml, dut), regionName);

		    responseData[0] = dut.getImageCompareProvider().compareImages(bufferedImage,
			    regionInfo.getRefImage(), regionInfo);
		    LOGGER.info("compareImages(Image,Image,String) - Obtained IC response as - " + responseData[0]);
		} catch (ImageCompareException e) {
		    LOGGER.error("image comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE, e);
		}

	    }
	};

	try {
	    timerThread.start();
	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET IMAGE COMPARISON RESPONSE.");
	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("isRegionOnScreenNow(). Join operation interrupted.");
	}

	return responseData[0];

    }

    /**
     * Compare image on the specified region to be on screen using image magic algorithm. This function takes expected
     * images and co-ordinates from .
     * 
     * @param dut
     *            The {@link Dut} object
     * @param imageXml
     *            The image xml file name.
     * @param regionName
     *            The name of the region to be compared.
     * @param timeOut
     *            The timeout in milliseconds.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public boolean compareImageOnTargetRegion(final Dut dut, final String imageXml, final String regionName,
	    final long timeOut) {

	final boolean[] responseData = new boolean[1];

	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    RegionInfo regionInfo = ImageRegionUtils
			    .getRegionInfo(getResourceLocator().getResource(imageXml, dut), regionName);

		    responseData[0] = compareImageOnTargetRegion(dut, regionInfo, timeOut);
		    LOGGER.info("compareImageOnTargetRegionUsingImageMagic() - Obtained IC response as - "
			    + responseData[0]);
		} catch (ImageCompareException e) {
		    LOGGER.error("image comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE, e);
		}
	    }
	};

	try {
	    timerThread.start();
	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET IMAGE COMPARISON RESPONSE.");
	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("compareImageOnTargetRegionUsingImageMagic(). Join operation interrupted.");
	}
	return responseData[0];

    }

    /**
     * Compare image on the specified region to be on screen using image magic algorithm. This function takes expected
     * images and co-ordinates from .
     * 
     * @param dut
     *            The {@link Dut} object
     * @param imageXml
     *            The image xml file name.
     * @param regionName
     *            The name of the region to be compared.
     * @param timeOut
     *            The timeout in milliseconds.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public boolean compareImageOnTargetRegionUsingImageMagicRmseAlgorithm(final Dut dut, final String imageXml,
	    final String regionName, final long timeOut) {
	final boolean[] responseData = new boolean[1];

	// Timer thread that for controlling the IC (image Compare) response
	// time.
	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    RegionInfo regionInfo = ImageRegionUtils
			    .getRegionInfo(getResourceLocator().getResource(imageXml, dut), regionName);

		    responseData[0] = compareImageOnTargetRegionUsingAlgorithm(dut, regionInfo, timeOut);

		    LOGGER.info("compareImageOnTargetRegionUsingImageMagicRmseAlgorithm() - Obtained IC response as - "
			    + responseData[0]);
		} catch (ImageCompareException e) {
		    LOGGER.error("image comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.IMAGE_COMPARE_FAILURE, e);
		}

	    }
	};

	try {

	    timerThread.start();

	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET IMAGE COMPARISON RESPONSE.");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("compareImageOnTargetRegionUsingImageMagicRmseAlgorithm(). Join operation interrupted.");
	}

	return responseData[0];

    }

    public boolean compareImageOnTargetRegionUsingAlgorithm(Dut dut, RegionInfo regionInfo, long timeOut) {

	boolean status = false;
	BufferedImage referenceImage = regionInfo.getRefImage();
	BufferedImage liveImage = dut.getVideo()
		.getVideoImage(new Dimension(referenceImage.getWidth(), referenceImage.getHeight()));

	BufferedImage currentImage = liveImage.getSubimage(regionInfo.getX(), regionInfo.getY(), regionInfo.getWidth(),
		regionInfo.getHeight());
	BufferedImage expectedImage = referenceImage.getSubimage(regionInfo.getX(), regionInfo.getY(),
		regionInfo.getWidth(), regionInfo.getHeight());

	MagickComparison magickComparison = new MagickComparison();

	if (magickComparison.doRmseComparison(expectedImage, currentImage)) {
	    status = true;
	} else {
	    saveImages(dut, currentImage, expectedImage, null, null);
	}

	return status;

    }

    /**
     * Method which detects the presence of closed caption on dut video.
     * 
     * <p>
     * Capture a sequence of images for a specified time range for a static video screen and calculate the fixel change
     * between frames , if the overall pixel change goes above a specified range it is assumed that there is motion and
     * CC is present.
     * </p>
     * 
     * <p>
     * The video on screen will be changing along with CC text, to distinguish between Closed Caption change and video
     * change, the video sequence will be captured after bringing up a static UI screen on which the changing area will
     * be minimum.
     * </p>
     * 
     * <p>
     * The changing area will be masked, if closed caption is present there will be pixel change in consecutive frames.
     * </p>
     * 
     * @param dut
     *            The {@link Dut} object
     * @param imageSequenceDuration
     *            A specific time range till the image has to be captured
     * @param xmlOfStaticScreen
     *            XML of any static screen on which the changing area will be minimal, and which can further be masked
     *            to detect the closed caption text.
     * @param regionsToBeMasked
     *            Variable list of regions present in the static screen which has to be masked.
     * 
     * @return true if closed caption is present, false otherwise.
     */

    public boolean detectClosedCaptionOnSettopVideo(Dut dut, long imageSequenceDuration, String xmlOfStaticScreen,
	    String... regionsToBeMasked) {

	// Location where closed caption images are to be saved
	String ccImageSaveLocation = dut.getImageCompareProvider().getClosedCaptionImageSaveLocation();
	File ccImageDir = new File(ccImageSaveLocation);

	// The CC images are saved in /ccImageSaveLocation/HostMacAddress
	String savedCCimagesFolder = ccImageSaveLocation + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsUtils.getCleanMac(dut.getHostMacAddress()) + AutomaticsConstants.PATH_SEPARATOR;
	ccImageDir.mkdirs();

	BufferedImage actualImage = null;
	BufferedImage expectedImage = null;
	long currentTime = System.currentTimeMillis();

	// Clean destination folder before proceedings.
	if (ccImageDir.exists()) {

	    try {
		org.apache.commons.io.FileUtils.cleanDirectory(ccImageDir);
	    } catch (IOException ioex) {
		LOGGER.error("Failed to clean the directory before saving new images.", ioex);
	    }
	}
	// Capture a sequence of frames for a particular duration
	while (System.currentTimeMillis() < (currentTime + imageSequenceDuration)) {
	    saveVideoImage(ccImageSaveLocation, dut);
	}
	// Store the list of saved images in an array
	File[] savedImages = new File(savedCCimagesFolder).listFiles();
	int matchedSamples = 0;
	// if images are saved mask the moving regions
	if (savedImages.length > 0) {
	    maskRegion(savedImages, dut, xmlOfStaticScreen, regionsToBeMasked);
	    MagickComparison magicComparison = new MagickComparison();
	    magicComparison.setRmseError(AVConstants.RMSE_ERROR_CLOSED_CAPTION);

	    for (int index = 0; index < savedImages.length; index++) {

		try {
		    // get the first and last frame from the masked images
		    actualImage = ImageIO.read(savedImages[index]);
		    expectedImage = ImageIO.read(savedImages[0]);
		} catch (IOException e) {
		    LOGGER.error(e.getMessage(), e);
		}
		// Compare the first image with all samples.
		if (magicComparison.doRmseComparison(expectedImage, actualImage)) {
		    matchedSamples++;
		    LOGGER.info("matchedSamples =" + matchedSamples);
		}
	    }

	}
	LOGGER.info("matchedSamples %% " + matchedSamples);
	LOGGER.info("savedImages.length / 2 " + savedImages.length / 2);
	LOGGER.info("Matches " + (matchedSamples <= savedImages.length / 2));
	return (matchedSamples <= savedImages.length / 2);
    }

    /**
     * Provides the resource locator instance.
     * 
     * @return the resource locator.
     */
    public IResourceLocator getResourceLocator() {
	return resourceLocator;
    }

    /**
     * Sets the resource locator instance.
     * 
     * @param IResourceLocator
     */
    public void setResourceLocator(IResourceLocator resourceLocator) {
	this.resourceLocator = resourceLocator;
    }

    /**
     * Waits for the specified ocr region to be on screen using the specific region timeout.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param ocrRegionInfo
     *            The ocr region metadata.
     * 
     * @return true if the ocr region was found within the region specific timeout. Otherwise false is returned.
     * 
     * @throws OCRException
     *             if an exception occurs during OCR operation
     */
    public boolean waitForOcrRegion(final Dut dut, final OcrRegionInfo ocrRegionInfo) throws OcrException {

	final boolean[] isRegionFound = new boolean[1];

	if (NonRackUtils.isNonRack()) {
	    return false;
	}

	// Timer thread that for controlling the OCR (Optical character
	// recognition) response time.
	Thread timerThread = new Thread() {

	    public void run() {
		isRegionFound[0] = dut.getOcrProvider().waitForOcrRegion(ocrRegionInfo);
		LOGGER.info("waitForOcrRegion() - Obtained OCR response as - " + isRegionFound);
	    }
	};

	try {

	    timerThread.start();

	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET OCR RESPONSE.");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if OCR ran into loop, then
	     * it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("waitForOCRRegion(). Join operation interrupted.");
	}

	return isRegionFound[0];
    }

    /**
     * Waits for the specified ocr region to be on screen using the specific region timeout.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param ocrXml
     *            Ocr xml name.
     * @param regionName
     *            The ocr region name.
     * 
     * @return true if the ocr region was found within the region specific timeout. Otherwise false is returned.
     */
    public boolean waitForOcrRegion(final Dut dut, final String ocrXml, final String regionName) {

	final boolean[] responseData = new boolean[1];

	if (NonRackUtils.isNonRack()) {
	    return false;
	}

	// Timer thread that for controlling the OCR (Optical character
	// recognition) response time.
	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    OcrRegionInfo ocrRegionInfo = (OcrRegionInfo) ImageRegionUtils
			    .getRegionInfo(getResourceLocator().getResource(ocrXml, dut), regionName);
		    responseData[0] = dut.getOcrProvider().waitForOcrRegion(ocrRegionInfo);

		    LOGGER.info("waitForOCRRegion(Dut,String,String) - Obtained OCR response as - " + responseData[0]);
		} catch (OcrException e) {
		    LOGGER.error("OCR comparision failed.", e);
		    throw new FailedTransitionException(GeneralError.OCR_FAILURE, e);
		}
	    }
	};

	try {

	    timerThread.start();

	    LOGGER.debug("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET OCR RESPONSE.");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if OCR ran into loop, then
	     * it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("waitForOCRRegion(Dut,String,String)). Join operation interrupted.");
	}

	return responseData[0];

    }

    /**
     * Waits for the ocr region to be on screen using the specific retry count.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param xmlFileName
     *            Reference xml file name.
     * @param ocrRegionName
     *            The region Name specified in xml
     * @param expectedText
     *            The text to search for in the given region. Overrides the contents in the ocr region meta data.
     * @param retryCount
     *            The maximum retry count.
     * @param timeOut
     *            Time out in seconds for the ocr comparison.
     * 
     * @return true if the the specified text is found within the region in specific timeout. Otherwise false is
     *         returned.
     */
    public boolean waitForTextInOCRRegionWithRetry(Dut dut, String xmlFileName, String ocrRegionName,
	    String expectedText, int retryCount, int timeOut) {

	boolean status = false;

	if (NonRackUtils.isNonRack()) {
	    return false;
	}

	for (int i = 1; i <= retryCount; i++) {

	    LOGGER.info("Ocr retry : " + retryCount);

	    if (waitForTextInOCRRegion(dut, xmlFileName, ocrRegionName, expectedText, timeOut)) {
		status = true;

		break;
	    }
	}

	return status;
    }

    /**
     * Waits for the ocr region to be on screen using the specific region timeout.
     * 
     * @param dut
     *            The dut instance
     * @param ocrXml
     *            The ocr xml name
     * @param regionName
     *            The region Name specified in xml
     * @param text
     *            The text to search for in the given region.
     * @param timeOut
     *            The specific region timeout.
     * 
     * @return true if the comparison succeeded otherwise false
     */

    public boolean waitForTextInOCRRegion(final Dut dut, final String ocrXml, final String regionName,
	    final String text, final int timeOut) {

	final OcrRegionInfo ocrRegionInfo = (OcrRegionInfo) ImageRegionUtils
		.getRegionInfo(getResourceLocator().getResource(ocrXml, dut), regionName);
	BufferedImage firstImage = null;
	final boolean[] responseData = new boolean[1];

	// Timer thread that for controlling the OCR (Optical character
	// recognition) response time.
	Thread timerThread = new Thread() {

	    public void run() {

		try {
		    String result = AutomaticsConstants.EMPTY_STRING;
		    long startTime = new Date().getTime();
		    long endTime = startTime + (timeOut * 1000L);
		    boolean readFromFullImage = false;

		    while (endTime - startTime >= 0) {
			result = dut.getOcrProvider().getOcrTextFromCurrentVideoImage(ocrRegionInfo, readFromFullImage);

			if (CommonMethods.isNotNull(result) && result.contains(text)) {
			    LOGGER.info(
				    "waitForTextInOCRRegion(Dut,OCRRegion,String,long) - Obtained OCR response as - "
					    + true);
			    responseData[0] = true;
			    break;
			} else {
			    AutomaticsUtils.sleep(500);
			}
			startTime = new Date().getTime();
		    }
		} catch (OcrException e) {
		    LOGGER.error("OCR comparision failed.", e);
		}
	    }
	};

	try {

	    firstImage = tapApi.captureCurrentScreen(dut);
	    timerThread.start();

	    LOGGER.info("WAIT FOR MAXIMUM " + (IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS / 60000)
		    + " minutes TO GET OCR RESPONSE.");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if OCR ran into loop, then
	     * it will block the execution until it gets any data.
	     */
	    timerThread.join(IMAGE_COMPARISON_MAX_THREAD_WAIT_TIME_IN_MILLISECONDS);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace("waitForTextInOCRRegion(Dut,String,String,String,long). Join operation interrupted.");
	}
	LOGGER.debug("out of try");

	BufferedImage lastImage = tapApi.captureCurrentScreen(dut);
	if (!responseData[0]) {
	    saveImagesAfterMarking(firstImage, lastImage, ocrRegionInfo, dut);
	}

	return responseData[0];
    }

    /**
     * Method will get the text in the specified region and check for the expected text using the regular expression
     * provided
     * 
     * @param dut
     *            Dut box under test
     * @param ocrXml
     *            Reference XML
     * @param regionName
     *            Region Name
     * @param expectedRegex
     *            Regular expression to validate
     * @param retryCount
     *            Number of retry attempts
     * @return status True if Regex found on the region. false otherwise
     */
    public boolean verifyOcrTextUsingRegularExpression(final Dut dut, final String ocrXml, final String regionName,
	    String expectedRegex, int retryCount) {
	boolean ocrStatus = false;
	LOGGER.info("Capturing The Current Screen");
	BufferedImage screen = tapApi.captureCurrentScreen(dut);
	LOGGER.info("Captures The screen Verifying whether its available or not");
	if (screen != null) {
	    LOGGER.debug(" Screen Captured Sending it for verification");
	}
	for (int i = 1; i <= retryCount; i++) {

	    String text = getTextFromImageRegion(dut, screen, ocrXml, regionName);
	    // String text = getOcrTextInRegion(dut, ocrXml, regionName);
	    LOGGER.debug("Attempt : " + i + " : OCR TEXT READ from screen : " + text);

	    ocrStatus = CommonMethods.validateTextUsingRegularExpression(text, expectedRegex);

	    if (ocrStatus) {
		LOGGER.info("Regex matching success");
		break;
	    }
	}
	LOGGER.info("verifyOcrTextUsingRegularExpression >> " + ocrStatus);
	return ocrStatus;
    }

    /**
     * Method to read text from a region
     * 
     * @param dut
     *            instance of STB
     * @param image
     *            image
     * @param ocrXml
     *            OCR XML File name
     * @param regionName
     *            OCR Region name
     * @return text captured from given region
     */
    public String getTextFromImageRegion(Dut dut, BufferedImage image, String ocrXml, String regionName) {
	// Text which is captured from the given image
	String text = null;
	// Sub image
	BufferedImage subImage = null;

	OcrRegionInfo ocrRegionInfo = (OcrRegionInfo) ImageRegionUtils
		.getRegionInfo(getResourceLocator().getResource(ocrXml, dut), regionName);
	ocrRegionInfo.setX(0);
	ocrRegionInfo.setY(0);
	ocrRegionInfo.setWidth(image.getWidth());
	ocrRegionInfo.setHeight(image.getHeight());
	ocrRegionInfo.setUrl("sample_url");
	ocrRegionInfo.setExpectedText("sample_expected");
	ocrRegionInfo.setFilepath(ocrXml);
	ocrRegionInfo.setName(regionName);
	ocrRegionInfo.setSuccessTolerance(80);
	ocrRegionInfo.setTimeout(30);
	ocrRegionInfo.setXTolerance(90);
	ocrRegionInfo.setYTolerance(90);

	if (ocrRegionInfo != null) {
	    // Croping image
	    subImage = image.getSubimage(ocrRegionInfo.getX(), ocrRegionInfo.getY(), ocrRegionInfo.getWidth(),
		    ocrRegionInfo.getHeight());
	    /**
	     * Decides if text to be read from region mentioned in xml or from full source image.
	     **/
	    boolean readFullImageRegion = true;
	    // Retreive text from image
	    text = dut.getOcrProvider().getOcrTextFromImage(ocrRegionInfo, subImage, readFullImageRegion);
	}

	LOGGER.info("getTextFromImageRegion() - Text captured from the image - " + text);

	saveImage(dut, image, ocrRegionInfo);

	return text;
    }

    /**
     * Method to read text from image
     * 
     * @param dut
     *            instance of STB
     * @param image
     *            image
     * @return text which is captured from the image
     */
    public String getAllTextFromImage(Dut dut, BufferedImage image) {

	LOGGER.debug("STARTING METHOD: getAllTextFromImage()");

	// Text which is captured from the given image
	String text = null;

	try {

	    // Creating dummy OCR Region info for CATS API
	    OcrRegionInfo ocrRegionInfo = new OcrRegionInfo();
	    ocrRegionInfo.setX(0);
	    ocrRegionInfo.setY(0);
	    ocrRegionInfo.setWidth(image.getWidth());
	    ocrRegionInfo.setHeight(image.getHeight());
	    ocrRegionInfo.setUrl("sample_url");
	    ocrRegionInfo.setExpectedText("sample_expected");
	    ocrRegionInfo.setFilepath("sample_file_path");
	    ocrRegionInfo.setName("sample_name");
	    ocrRegionInfo.setSuccessTolerance(80);
	    ocrRegionInfo.setTimeout(30);
	    ocrRegionInfo.setXTolerance(90);
	    ocrRegionInfo.setYTolerance(90);

	    // Formatting BufferedImage properties for OCR APIs
	    BufferedImage formattedImg = new BufferedImage(image.getWidth(), image.getHeight(),
		    BufferedImage.TYPE_INT_RGB);
	    formattedImg.getGraphics().drawImage(image, 0, 0, null);

	    boolean readFromFullImage = false;
	    text = dut.getOcrProvider().getOcrTextFromImage(ocrRegionInfo, formattedImg, readFromFullImage);

	    LOGGER.info("getTextFromImage() - Text captured from the image - " + text);

	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getAllTextFromImage() - " + exception.getMessage());
	    text = null;
	}

	LOGGER.info("ENDING METHOD: getAllTextFromImage()");
	return text;
    }

    /**
     * Helper method to process images and extract the color on the screen
     * 
     * @param dut
     * @param bufferedImage
     * @return color of the image
     */
    public String processImageAndExtractColor(Dut dut, BufferedImage bufferedImage) {

	LOGGER.info("STARTING METHOD: extractColorFromImage()");
	String capturedColors = null;
	if (bufferedImage != null) {
	    LOGGER.info("Processing image");
	    Long startTime = System.currentTimeMillis();
	    capturedColors = getColorFromImage(dut, bufferedImage);
	    Long endTime = System.currentTimeMillis();
	    LOGGER.info("image extract color process time -{} ", endTime - startTime);

	}
	LOGGER.info("Ending Method: extractColorFromImage");
	return capturedColors;
    }

    /**
     * Method to extract color from image
     * 
     * @param dut
     *            instance of STB
     * @param image
     *            image
     * @return Name of color which is captured from the image
     */
    public String getColorFromImage(Dut dut, BufferedImage image) {

	LOGGER.info("STARTING METHOD: getColorFromImage()");

	// Text which is captured from the given image
	String color = null;

	try {

	    // Creating dummy OCR Region info for CATS API
	    OcrRegionInfo ocrRegionInfo = new OcrRegionInfo();
	    ocrRegionInfo.setX(0);
	    ocrRegionInfo.setY(0);
	    ocrRegionInfo.setWidth(image.getWidth());
	    ocrRegionInfo.setHeight(image.getHeight());
	    ocrRegionInfo.setUrl("sample_url");
	    ocrRegionInfo.setExpectedText("sample_expected");
	    ocrRegionInfo.setFilepath("sample_file_path");
	    ocrRegionInfo.setName("sample_name");
	    ocrRegionInfo.setSuccessTolerance(80);
	    ocrRegionInfo.setTimeout(30);
	    ocrRegionInfo.setXTolerance(90);
	    ocrRegionInfo.setYTolerance(90);

	    // Formatting BufferedImage properties
	    BufferedImage formattedImg = new BufferedImage(image.getWidth(), image.getHeight(),
		    BufferedImage.TYPE_INT_RGB);
	    formattedImg.getGraphics().drawImage(image, 0, 0, null);

	    color = dut.getOcrProvider().extractColorFromGivenImage(ocrRegionInfo, image);

	    LOGGER.info("getColorFromImage() - Color captured from the image - " + color);

	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getColorFromImage() - " + exception.getMessage());
	    color = null;
	}

	LOGGER.info("ENDING METHOD: getColorFromImage()");
	return color;
    }

    /**
     * Helper method to verify AV using image processing
     * 
     * @param dut
     * @return true if AV is present, else false
     */
    public boolean processImagesAndVerifyAV(Dut dut) {
	LOGGER.info("STARTING METHOD: processImagesAndVerifyAV()");
	boolean status = false;

	String capturedColors[] = new String[5];
	for (int i = 0; i < 5; i++) {
	    BufferedImage bufferedImage = tapApi.captureCurrentScreen(dut);
	    if (bufferedImage != null) {
		capturedColors[i] = processImageAndExtractColor(dut, bufferedImage);
		if (CommonMethods.isNotNull(capturedColors[i])) {
		    LOGGER.info("Not able to capture any color");
		}
	    }

	    AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
	}
	for (int i = 1; i < capturedColors.length; i++) {
	    if (capturedColors[i - 1].equalsIgnoreCase(capturedColors[i])) {
		status = false;
		LOGGER.info("Screen freeze is observed while verifying AV");
		break;
	    } else {
		status = true;
	    }
	}
	LOGGER.info("Ending Method: processImagesAndVerifyAV");
	return status;
    }

    /**
     * Helper method to verify whether blue screen is observed using image processing
     * 
     * @param dut
     * @param bufferedImage
     * @return true if blue screen is observed, else false
     */
    public boolean processImagesAndVerifyNoInputSignalScreen(Dut dut, BufferedImage bufferedImage) {
	LOGGER.info("STARTING METHOD: processImagesAndVerifyNoInputSignalScreen()");
	boolean status = false;
	String capturedColors = null;
	int bluePercentage = 0;
	if (bufferedImage != null) {
	    capturedColors = processImageAndExtractColor(dut, bufferedImage);
	    if (CommonMethods.isNotNull(capturedColors)) {
		String bluePattern = "blue\\s*=\\s*(\\d*.\\d*)";
		String bluePercent = CommonMethods.patternFinder(capturedColors, bluePattern);
		if (CommonMethods.isNotNull(bluePercent)) {
		    try {
			bluePercentage = Integer.parseInt(bluePercent);
		    } catch (Exception e) {
			try {
			    bluePercentage = (int) Double.parseDouble(bluePercent);
			} catch (Exception ex) {
			    bluePercentage = (int) Float.parseFloat(bluePercent);
			}
		    }
		    LOGGER.info("Obtained Blue percent Int value as : " + bluePercentage);
		    if (bluePercentage >= 90) {
			LOGGER.info("No input signal screen found");
			status = true;
		    }
		}
	    }
	}
	LOGGER.info("Ending Method: processImagesAndVerifyNoInputSignalScreen : " + status);
	return status;
    }

    /**
     * Helper method to verify whether black screen is observed using image processing
     * 
     * @param dut
     * @param bufferedImage
     * @return true if black screen is observed, else false
     */
    public boolean processImageAndVerifyBlackScreen(Dut dut, BufferedImage bufferedImage) {
	LOGGER.info("STARTING METHOD: processImageAndVerifyBlackScreen()");
	boolean status = false;
	String colorToValidate = "black=100.0";
	String capturedColors = null;
	if (bufferedImage != null) {
	    capturedColors = processImageAndExtractColor(dut, bufferedImage);
	    if (CommonMethods.isNotNull(capturedColors) && capturedColors.contains(colorToValidate)) {
		status = true;
		LOGGER.info("Black screen is observed");
	    }
	}
	LOGGER.info("Ending Method: processImageAndVerifyBlackScreen : " + status);
	return status;
    }

    /**
     * 
     * Helper method to process images and verify if screen is frozen
     * 
     * @param dut
     * @return return true if screen is frozen, else false
     */
    public boolean processImagesAndVerifyFrozenScreen(Dut dut) {
	LOGGER.info("STARTING METHOD: processImagesAndVerifyFrozenScreen()");
	boolean status = false;
	String capturedColors = null;
	String capturedColors_after_five_sec = null;
	BufferedImage bufferedImage = tapApi.captureCurrentScreen(dut);
	if (bufferedImage != null) {
	    capturedColors = processImageAndExtractColor(dut, bufferedImage);
	}
	if (CommonMethods.isNotNull(capturedColors)) {
	    capturedColors = "";
	    LOGGER.info("Not able to capture any color");
	}
	AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
	bufferedImage = tapApi.captureCurrentScreen(dut);
	if (bufferedImage != null) {
	    capturedColors_after_five_sec = processImageAndExtractColor(dut, bufferedImage);
	}
	if (CommonMethods.isNotNull(capturedColors_after_five_sec)) {
	    capturedColors_after_five_sec = "";
	    LOGGER.info("Not able to capture any color after 5 seconds to verify AV freeze");
	} else {
	    if (capturedColors.equalsIgnoreCase(capturedColors_after_five_sec)) {
		status = false;
		LOGGER.info("Screen freeze is observed");
	    } else {
		status = true;
	    }
	}
	LOGGER.info("Ending Method: processImagesAndVerifyFrozenScreen");
	return status;
    }

    /**
     * Method to save image locally
     * 
     * @param dut
     *            instance of STB
     * @param image
     *            image
     * @param info
     *            OCRRegionInfo
     * @return image file
     */
    public File saveImage(Dut dut, BufferedImage image, OcrRegionInfo info) {

	File outputFile = null;
	File outputDirectory = null;

	try {
	    outputDirectory = new File(dut.getOcrProvider().getImageSaveLocation());

	    if (!outputDirectory.exists()) {
		outputDirectory.mkdirs();
	    }
	    outputFile = new File(outputDirectory, Long.toString(System.currentTimeMillis()) + ".png");

	    saveImage(outputFile, image, info, "PNG");

	    LOGGER.info("Successfully saved the file to the location " + outputFile.getAbsolutePath());
	} catch (Exception exception) {
	    LOGGER.error("saveImage() failed.", exception);
	}
	return outputFile;

    }

    /**
     * Read the resource properties file and return the key value pair.
     * 
     * @param dut
     *            Set-top box object.
     * @param propFileName
     *            Property file name.
     * 
     * @return the properies.
     */
    public Properties loadResourceProperties(Dut dut, String propFileName) {

	Properties props = null;

	try {
	    props = FileUtils.getPropertiesFromResource(getResourceLocator().getResource(propFileName, dut));
	} catch (IOException ioe) {
	    LOGGER.error(ioe.getMessage(), ioe);
	    throw new FailedTransitionException(GeneralError.PROVIDED_RESOURCE_NOT_FOUND, ioe);
	}

	return props;
    }

    /**
     * Helper method to save images
     * 
     * @param firstImage
     *            the first text image before ocr staring
     * @param lastImage
     *            The last test image after ocr comparison
     * @param ocrRegionInfo
     *            the region info to be verified
     * @param dut
     *            The dut info
     */
    public void saveImagesAfterMarking(BufferedImage firstImage, BufferedImage lastImage, OcrRegionInfo ocrRegionInfo,
	    Dut dut) {
	LOGGER.info("Saving OCR image as comparison result is false");
	if (null != firstImage) {
	    markAndSaveOcrImage(dut, firstImage, ocrRegionInfo, true);
	}
	if (null != lastImage) {
	    markAndSaveOcrImage(dut, lastImage, ocrRegionInfo, false);
	}
    }

    /**
     * TODO: This method is implemented as the current OCR saving mechanism is deprecated by CATS Remove this call once
     * confirmation comes from cats side. Helper method to store the ocr images upon ocr comparison failure
     * 
     * @param dut
     *            The dut instance to be verified
     * @param Image
     *            Image on which to be marked and saved
     * @param ocrRegionInfo
     *            Region info based on which the image has to be saved
     * @param isFirstImage
     *            This flag is set to true if its the first image to be marked and saved
     */
    private void markAndSaveOcrImage(Dut dut, BufferedImage image, OcrRegionInfo ocrRegionInfo, boolean isFirstImage) {

	String ocrSaveLocation = dut.getOcrProvider().getImageSaveLocation();
	LOGGER.debug("OCR Save location " + ocrSaveLocation);
	File outputDir = new File(ocrSaveLocation);

	if (!outputDir.isDirectory()) {
	    LOGGER.debug("created a new directory " + outputDir.mkdirs());
	}

	String formatedImageName = getFormattedImageName(ocrRegionInfo, isFirstImage, dut.getFirmwareVersion());
	File outputFile = new File(outputDir, formatedImageName);

	boolean isImageSaved = saveImage(outputFile, image, ocrRegionInfo);

	LOGGER.info("Image Saved Successfully " + isImageSaved + " name " + formatedImageName);

    }

    /**
     * Helper method to save the image with specified file name.
     * 
     * @param outputFile
     *            the specified file name
     * @param image
     *            the buffered image
     * @param info
     *            the ocr region information.
     * @param imageFormat
     *            PNG/JPEG
     * @return true if the image is saved to specific folder, false if any exception happens.
     */
    private boolean saveImage(File outputFile, BufferedImage image, OcrRegionInfo info, String imageFormat) {

	boolean savedFile = false;

	try {

	    if (null != info) {

		// draw compare region on image
		Graphics g = image.getGraphics();
		g.setXORMode(Color.RED);

		if (g instanceof Graphics2D) {
		    Stroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
		    ((Graphics2D) g).setStroke(stroke);
		}

		g.drawRect(info.getX(), info.getY(), info.getWidth(), info.getHeight());
	    }

	    savedFile = ImageIO.write(image, imageFormat, outputFile);

	} catch (IOException ioe) {
	    LOGGER.error(ioe.getMessage(), ioe);
	}

	return savedFile;
    }

    /**
     * Helper method to save the image with specified file name.
     * 
     * @param outputFile
     *            the specified file name
     * @param image
     *            the buffered image
     * @param info
     *            the ocr region information.
     * 
     * @return true if the image is saved to specific folder, false if any exception happens.
     */
    private boolean saveImage(File outputFile, BufferedImage image, OcrRegionInfo info) {

	boolean savedFile = false;

	try {

	    if (null != info) {

		// draw compare region on image
		Graphics g = image.getGraphics();
		g.setXORMode(Color.RED);

		if (g instanceof Graphics2D) {
		    Stroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
		    ((Graphics2D) g).setStroke(stroke);
		}

		g.drawRect(info.getX(), info.getY(), info.getWidth(), info.getHeight());
	    }

	    savedFile = ImageIO.write(image, "JPEG", outputFile);
	} catch (IOException ioe) {
	    LOGGER.error(ioe.getMessage(), ioe);
	}

	return savedFile;
    }

    /**
     * Helper method to get the formatted image name. The Image name will be "OCR-[ Date-Time stamp ]-[ region name
     * ]-first/last.jpg".
     * 
     * @param ocrRegionInfo
     *            the OCR region information.
     * @param isFirstImage
     *            true if the image is first compared, false if it is last compared.
     * 
     * @return the formatted image name.
     */
    private String getFormattedImageName(OcrRegionInfo ocrRegionInfo, boolean isFirstImage, String firmwareVersion) {
	StringBuilder imageName = new StringBuilder();
	imageName.append(String.format(OCR_IMAGE_SAVE_FORMAT, Calendar.getInstance()));
	imageName.append("_" + firmwareVersion);

	if (ocrRegionInfo != null) {
	    imageName.append("-").append(ocrRegionInfo.getName());
	}

	if (isFirstImage) {
	    imageName.append(FIRST_COMPARED_IMAGE_FILE_EXTENSION);
	} else {
	    imageName.append(LAST_COMPARED_IMAGE_FILE_EXTENSION);
	}
	return imageName.toString();
    }

    /**
     * Direct tune to a channel that is up to 4 digits in length.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param channel
     *            - Channel number to tune to.
     * @return boolean
     */
    public boolean tuneToChannel(Dut dut, String channel, boolean isAutoTune, RemoteControlType type) {
	LOGGER.info("tuneToChannel() : Tuning to channel number :  " + channel);
	return dut.getRemote().tune(channel, AutomaticsConstants.DEFAULT_DELAY, isAutoTune, type);
    }

    /**
     * 
     * @param dut
     *            Dut object
     * 
     *            This method takes the dut object as input and fetched the channellist configured in stbprops for
     *            tuning.
     */
    public void tuneToChannelAndReadResponse(Dut dut) {
	try {
	    String baseChannels = AutomaticsPropertyUtility
		    .getProperty(AutomaticsConstants.PROPERTY_FOR_AUTOMATION_BASE_CHANNEL_LIST);
	    String[] baseChannelList = null;

	    if (CommonMethods.isNotNull(baseChannels)) {
		baseChannelList = baseChannels.split(",");
		// retrieve channel number from Automatics properties
		Device device = (Device) dut;
		tuneToChannelWithAleadyTunedHandled(dut, baseChannelList, false, device.getDefaultRemoteControlType());
		// waiting to complete channel tuning
		tapApi.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);
	    }
	} catch (Exception e) {
	    LOGGER.error("Tuning to channel failed " + e);
	}
    }

    /**
     * @param dut
     * 
     *            Dut object
     * @param channelList
     * 
     *            CHannel list
     * 
     *            This method tales dut object and channel list as input.It truies to tune to each channel until a
     *            response other than success or alreadytunes or ca error in obtained.
     */
    private void tuneToChannelWithAleadyTunedHandled(Dut dut, String[] channelList, boolean isAutoTune,
	    RemoteControlType type) {
	LOGGER.info("tuneToChannel() : Tuning to channel number :  " + channelList);
	for (String channel : channelList) {
	    tuneToChannel(dut, String.valueOf(channel), isAutoTune, type);
	}
    }

    /**
     * Perform a remote command and afterwards delay for the specified time in milliseconds.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param command
     *            - Remote command for transmission.
     * @param delay
     *            - Delay time in milliseconds after command.
     */
    public long pressKey(Dut dut, String command, Integer delay, RemoteControlType type) {
	LOGGER.info("Sending key [" + command + "] using remote with delay [" + delay + "].");

	boolean sent = false;
	int attempts = 0;
	long pressKeyTime = 0L;

	for (attempts = 0; !sent && (attempts < retries); attempts++) {

	    sent = dut.getRemote().pressKeyAndHold(command, delay, type);

	    if (!sent) {
		LOGGER.error(
			String.format("%d attempt of [%s] key send failed...............", (attempts + 1), command));
	    } else {
		pressKeyTime = System.currentTimeMillis();
	    }
	}

	LOGGER.info("Presskey status - " + sent);
	if (!sent) {
	    throw new FailedTransitionException(GeneralError.KEY_SEND_FAILURE,
		    "Failed to send key [" + command + "] using remote with delay [" + delay + "].");
	}

	return pressKeyTime;
    }

    /**
     * Press keys with a delay between. This delay will NOT account for delays associated with physical hardware sending
     * the key.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param commands
     *            - List of keys to be sent.
     * @param delay
     *            - Delay in milliseconds between key presses.
     */
    public void pressKeys(Dut dut, List<String> commands, Integer delay, RemoteControlType type) {
	for (String remoteCommand : commands) {
	    pressKey(dut, remoteCommand, delay, type);
	}
    }

    /**
     * Perform a set of remote commands and afterwards delay for the specified time, for a specified number of times.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param count
     *            - The number of times the remote commands with the delay to be performed.
     * @param delay
     *            - Delay time in milliseconds after each command.
     * @param commands
     *            - Array of Remote Commands
     */
    public void pressKey(Dut dut, Integer count, Integer delay, String[] commands, RemoteControlType type) {
	List<String> remoteCommandList = Arrays.asList(commands);
	for (int noOfTimes = 0; noOfTimes < count; noOfTimes++) {
	    pressKeys(dut, remoteCommandList, delay, type);
	    tapApi.waitTill(delay);
	}
    }

    /**
     * Perform a remote command and afterwards delay for the specified time, for a specified number of times.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param count
     *            - The number of times the remote command with the delay to be performed.
     * @param command
     *            - Remote command
     * @param delay
     *            - Delay time in milliseconds after command.
     */
    public void pressKey(Dut dut, Integer count, String command, Integer delay, RemoteControlType type) {
	for (int noOfTimes = 0; noOfTimes < count; noOfTimes++) {
	    pressKey(dut, command, delay, type);
	    tapApi.waitTill(delay);
	}
    }

    /**
     * Press Key on the remote.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param command
     *            - Key to be sent.
     */
    public long pressKey(Dut dut, String command, RemoteControlType type) {

	LOGGER.info("Sending key [" + command + "] using remote.");

	long pressKeyTime = 0L;
	boolean sent = dut.getRemote().pressKey(command, type);

	if (sent) {
	    pressKeyTime = System.currentTimeMillis();
	}

	return pressKeyTime;
    }

    /**
     * Perform a remote command for a specified number of times.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param count
     *            - The number of times the remote command to be performed.
     * @param command
     *            - Remote command
     */
    public void pressKey(Dut dut, Integer count, String command, RemoteControlType type) {

	for (int noOfTimes = 0; noOfTimes < count; noOfTimes++) {
	    pressKey(dut, command, type);
	}
    }

    /**
     * Press arbitrary list of keys. If a direct tune is needed, don't use this method as a way of sending tunes.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param commands
     *            - List of keys to be sent.
     */
    public void pressKeys(Dut dut, List<String> commands, RemoteControlType type) {

	for (String remoteCommand : commands) {
	    pressKey(dut, remoteCommand, type);
	}
    }

    /**
     * 
     * Method to save Video Images
     * 
     * @param filePath
     * @param dut
     */
    public void saveVideoImage(String filePath, Dut dut) {
	LOGGER.info("saveVideoImage");
	if (null == filePath || filePath.isEmpty()) {
	    LOGGER.error("Saving video image failed. The file location cannot be null or empty: " + filePath);
	} else {
	    String imagePath = filePath;
	    File imagefile = new File(imagePath);

	    if (imagefile.exists() && imagefile.isDirectory()) {
		imagePath = imagePath + System.getProperty("file.separator") + getFolderAndFileName(dut);
		imagefile = new File(imagePath);
	    }

	    try {
		org.apache.commons.io.FileUtils.forceMkdir(imagefile.getAbsoluteFile().getParentFile());
		ImageIO.write(tapApi.captureCurrentScreen(dut), IMAGE_FILE_FORMAT, imagefile);
		LOGGER.info("Saved video image to file: " + imagefile.getAbsolutePath());
	    } catch (Exception e) {
		LOGGER.error("Saving video image failed. Error in creating file: '" + imagePath + "'.", e);
	    }
	}
	LOGGER.info(" end saveVideoImage");
    }

    private String getFolderAndFileName(Dut dut) {
	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
	String dateString = dateFormat.format(date);

	return getCleanMac(dut) + System.getProperty("file.separator") + dateString + "." + IMAGE_FILE_FORMAT;
    }

    private String getCleanMac(Dut dut) {
	String cleanMac = "";
	try {
	    String hostMacAddress = dut.getHostMacAddress();
	    cleanMac = hostMacAddress.trim().replace(":", "").toUpperCase();
	} catch (ClassCastException e) {
	    LOGGER.error("VideoProvider doesn't have a parent dut.");
	}
	return cleanMac;
    }

    /**
     * Mask the moving regions in the captured screen.
     * 
     * @param savedImages
     *            list of images whose regions have to be masked
     * @param dut
     *            The {@link Dut} object
     * @param xmlOfStaticScreen
     *            XML of static screen having the co-ordinates of the regions that has to masked.
     * @param regionsToBeMasked
     *            Array of regions to be masked.
     */
    public void maskRegion(File[] savedImages, Dut dut, String xmlOfStaticScreen, String[] regionsToBeMasked) {
	BufferedImage image = null;

	for (File imagePath : savedImages) {

	    try {
		image = ImageIO.read(imagePath);

		// Mask the region with black color
		int blackRgb = Color.black.getRGB();

		for (String regionName : regionsToBeMasked) {

		    // Get the co-ordinates of the regions to be masked
		    RegionInfo maskRegionInfo = ImageRegionUtils
			    .getRegionInfo(tapApi.getResourceLocator().getResource(xmlOfStaticScreen, dut), regionName);

		    for (int xCoord = maskRegionInfo.getX(); xCoord < (maskRegionInfo.getX()
			    + maskRegionInfo.getWidth()); xCoord++) {

			for (int yCoord = maskRegionInfo.getY(); yCoord < (maskRegionInfo.getY()
				+ maskRegionInfo.getHeight()); yCoord++) {
			    image.setRGB(xCoord, yCoord, blackRgb);
			}
		    }

		    try {
			ImageIO.write(image, imageFormatName, imagePath);
		    } catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		    }

		}

	    } catch (IOException e) {
		e.printStackTrace();
	    }

	}
    }

    /**
     * Read the text document resource line by line and return read values in an array list.
     * 
     * @param dut
     *            Set-top box object.
     * @param fileName
     *            File name of the text document.
     * 
     * @return the readed line by line text.
     */
    public List<String> loadTextDocumentLineByLine(Dut dut, String fileName) {

	// Line by line text
	List<String> lineByLineText = new ArrayList<String>();
	BufferedReader fileBufferedReader = null;

	try {
	    fileBufferedReader = new BufferedReader(new FileReader(
		    FileUtils.getResourceAsFile(getClass(), tapApi.getResourceLocator().getResource(fileName, dut))));

	    String line = null;

	    while ((line = fileBufferedReader.readLine()) != null) {
		lineByLineText.add(line);
	    }
	} catch (FileNotFoundException exc) {
	    LOGGER.error(exc.getMessage(), exc);
	    throw new FailedTransitionException(GeneralError.PROVIDED_RESOURCE_NOT_FOUND, exc);
	} catch (IOException ioe) {
	    LOGGER.error(ioe.getMessage(), ioe);
	    throw new FailedTransitionException(GeneralError.FAILED_RESOURCE_READ, ioe);
	} finally {

	    if (fileBufferedReader != null) {

		try {
		    fileBufferedReader.close();
		} catch (IOException exc) {
		    LOGGER.error("Failed to close the opened input stream of the property file.");
		}
	    }
	}
	return lineByLineText;
    }

    /**
     * Gets the firmware version from STB through SNMP.
     * 
     * @param dut
     *            The STB whose firmware version has to be retrieved
     * 
     * @return The firmware version
     */
    public String getFirmwareVersion(final Dut dut) {

	LOGGER.info("getFirmwareVersion(): For DEVICE : " + dut.getHostMacAddress());

	String imageVersion = "";

	try {

	    if (!SupportedModelHandler.isNonRDKDevice(dut)) {
		String response = executeCommandInSettopBox(dut,
			LinuxCommandConstants.CMD_GREP_IMAGE_NAME_FROM_VERSION_FILE);
		if (CommonMethods.isNotNull(response)) {
		    imageVersion = parseOutputToGetImageVersionFromVersionFile(response);
		}

		LOGGER.info("CURRENT IMAGE VERSION FROM Version.txt : " + imageVersion);

		/**
		 * If RDKV devices we need to check whether cdl_flashed_filename and version.txt having same version
		 */
		if (!SupportedModelHandler.isRDKB(dut)) {

		    String imageFilename = getCdlFlashedImageFileName(dut);
		    LOGGER.info("CURRENT FLASHED IMAGE NAME from cdl_flashed_filename : " + imageFilename);
		    if (CommonMethods.isNotNull(imageVersion) && CommonMethods.isNotNull(imageFilename)
			    && imageFilename.contains(imageVersion)) {
			imageVersion = imageFilename;
		    }

		}

		LOGGER.info("SUCCESSFULLY RETRIEVED FIRMWARE VERSION : " + imageVersion);

		if (CommonMethods.isNull(imageVersion)) {

		    reConnectCount++;
		    if (reConnectCount < 2) {

			LOGGER.info("Since the firmware obtained is invalid, going for a retry..");

			imageVersion = getFirmwareVersion(dut);
		    }
		}
	    }
	} catch (Exception ise) {
	    LOGGER.error("Not able to collect firmware details for " + dut.getHostMacAddress() + " SSH may be down ",
		    ise);

	    waitTill(AutomaticsConstants.ONE_MINUTE);
	    reConnectCount++;

	    if (reConnectCount > 2) {
		return "";
	    }

	    return getFirmwareVersion(dut);
	}

	return imageVersion;
    }

    /**
     * Utility method to get the build name before cdl.
     * 
     * @param dut
     *            Dut instance
     * 
     * @return the flashed file name
     */

    public String getCdlFlashedImageFileName(Dut dut) {

	String flashedFileName = executeCommandInSettopBox(dut, AutomaticsConstants.CMD_GET_FLASHED_FILE_NAME);

	LOGGER.info("Flashed file name : " + flashedFileName);

	String flashedVersionName = "";

	if (null != flashedFileName && !flashedFileName.isEmpty()) {
	    flashedFileName = flashedFileName.trim();
	    flashedVersionName = flashedFileName.replace(AutomaticsConstants.BINARY_BUILD_IMAGE_EXTENSION,
		    AutomaticsConstants.EMPTY_STRING);
	}

	LOGGER.info("Flashed File Version Name :" + flashedVersionName);

	return flashedVersionName;

    }

    /**
     * Helper method to get the image version name from the version.txt.
     * 
     * @param versionFileDetails
     *            The snmp command output.
     * 
     * @return The mib value
     */
    private String parseOutputToGetImageVersionFromVersionFile(String versionFileDetails) {
	String imageVersion = null;
	boolean isImageVersionFound = false;

	// For line by line parsing.
	String[] splittedTopCommandOutput = versionFileDetails.split("\n");
	LOGGER.info("versionFileDetails : " + versionFileDetails);
	for (String lineByLineOutput : splittedTopCommandOutput) {
	    String trimedLineOutput = lineByLineOutput.trim();
	    LOGGER.info("Line by line output : " + trimedLineOutput);

	    // Skipping empty line and line beginning with hash.
	    if (!trimedLineOutput.startsWith(AutomaticsConstants.DELIMITER_HASH)
		    && !AutomaticsConstants.EMPTY_STRING.equals(trimedLineOutput)
		    && trimedLineOutput.startsWith(IDENTIFIER_FOR_BEGINNING_OF_IMAGE_NAME) && !isImageVersionFound) {
		imageVersion = trimedLineOutput.replaceFirst(IDENTIFIER_FOR_BEGINNING_OF_IMAGE_NAME,
			AutomaticsConstants.EMPTY_STRING);
		imageVersion = imageVersion.replaceFirst("(:|=)", AutomaticsConstants.EMPTY_STRING);
		isImageVersionFound = true;
		break;
	    }
	}

	return imageVersion;
    }

    /**
     * Validate whether the device is having AV
     * 
     * @param Dut
     * @param string
     */
    public boolean validateAV(Dut device) {
	boolean isAVPresent = false;
	if (null == rdkVideoDeviceProvider) {
	    rdkVideoDeviceProvider = BeanUtils.getRdkVideoDeviceProvider();
	}

	if (null != rdkVideoDeviceProvider) {
	    isAVPresent = rdkVideoDeviceProvider.validateAV(device);
	}
	return isAVPresent;
    }

    /**
     * Method to execute the TR69 command and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param command
     *            TR69 command to be executed.
     * 
     * @return string
     */
    public String executeTr69Command(Dut dut, String command) {
	long timeOut = 60;
	// Default retry
	int retryCount = 3;

	String commadResult = executeTr69CommandWithRetry(dut, command, timeOut, retryCount);

	LOGGER.debug("Result: " + commadResult);

	return commadResult;
    }

    /**
     * Method to execute the TR69 command and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param command
     *            TR69 command to be executed.
     * @param timeOut
     *            In seconds
     * @param value
     * @param retryCount
     *            Max retry count
     * 
     * @return TR69 output
     */
    public String executeTr69CommandWithRetry(final Dut dut, String command, long timeOut, int retryCount) {
	String tr69Response = "";
	for (int i = 1; i <= retryCount; i++) {
	    try {
		tr69Response = executeTr69CommandWithTimeOut(dut, command, timeOut);
	    } catch (Exception e) {
		LOGGER.error(e.getMessage());
		// throw back exception if final attempt to get tr69 param too
		// got failed.
		if (i == retryCount) {
		    throw new TestException(e.getMessage());
		}
	    }
	    if (CommonMethods.isNotNull(tr69Response)) {
		break;
	    } else {
		LOGGER.info("TR69 RESPONSE IS NULL. Waiting for 30s before retry");
		// RTAUTO-1887 : Wait for 30 seconds and then retry
		AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
	    }
	}

	return tr69Response;
    }

    /**
     * Method to execute the TR69 command and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param commands
     *            TR69 commands to be executed.
     * 
     * @return TR69 output
     */
    public String executeTr69Command(final Dut dut, String[] commands) {

	String commandResults = null;

	try {
	    String commandsCommaSeparated = FrameworkHelperUtils.convertToCommaSeparatedList(commands);
	    LOGGER.info("About to get values for params {} via TR69", commandsCommaSeparated);
	    TR69Provider tr69Provider = BeanUtils.getTR69Provider();
	    if (null != tr69Provider) {

		List<String> response = tr69Provider.getTr69ParameterValues(dut, commands);

		if (response != null && response.size() == 1) {
		    commandResults = response.get(response.size() - 1);
		}

		LOGGER.info("TR69 - Result: " + commandResults);
	    } else {
		LOGGER.error(BeanConstants.BEAN_NOT_FOUND_LOG, "TR69Provider", BeanConstants.BEAN_ID_TR69_PROVIDER);
	    }

	} catch (Exception e) {
	    LOGGER.info("ERROR ON EXECUTE TR69 .. " + e.getMessage());
	}

	return commandResults;
    }

    /**
     * Method to execute the TR69 command and return the result.
     * 
     * @param dut
     *            Dut instance in which test need to be executed
     * @param command
     *            TR69 command to be executed.
     * @param timeOut
     *            Max time out in seconds
     * 
     * @return TR69 output
     */
    public String executeTr69CommandWithTimeOut(final Dut dut, String command, long timeOut) {

	final String[] commandArray = new String[] { command };
	String commandResults = null;

	try {
	    LOGGER.info("About to get values for params {} via TR69", command);
	    TR69Provider tr69Provider = BeanUtils.getTR69Provider();
	    if (null != tr69Provider) {

		List<String> response = tr69Provider.getTr69ParameterValues(dut, commandArray);
		if (response != null && response.size() == 1) {
		    LOGGER.debug("TR69 - Response.size " + response.size());
		    commandResults = response.get(response.size() - 1);
		}

		LOGGER.info("TR69 - Result: " + commandResults);
	    } else {
		LOGGER.error(BeanConstants.BEAN_NOT_FOUND_LOG, "TR69Provider", BeanConstants.BEAN_ID_TR69_PROVIDER);
	    }

	} catch (Exception e) {
	    LOGGER.info("ERROR ON EXECUTE TR69 .. " + e.getMessage());
	}

	return commandResults;
    }

    /**
     * Gets the mini dump crash data.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return mini dump crash data.
     */
    public List<CrashDetails> getMiniDumpData(CrashPortalRequest crashPortalRequest) {
	if (crashAnalysisProvider == null) {
	    // Crash analysis provider
	    crashAnalysisProvider = BeanUtils.getCrashAnalysisProvider();
	}
	return crashAnalysisProvider.getMiniDumpData(crashPortalRequest);
    }

    /**
     * Gets the core dump crash data.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return core dump crash data.
     */
    public List<CrashDetails> getCoreDumpData(CrashPortalRequest crashPortalRequest) {
	if (crashAnalysisProvider == null) {
	    // Crash analysis provider
	    crashAnalysisProvider = BeanUtils.getCrashAnalysisProvider();
	}
	return crashAnalysisProvider.getCoreDumpData(crashPortalRequest);
    }

    /**
     * Validate the image file existence in server
     *
     * @param dut
     *            {@link Dut}
     * @param buildNameForCDL
     *            Build name
     * @return True is the image available in server
     */
    public boolean isImageAvailableInCDL(Dut dut, ImageUpgradeMechanism downloadType, String buildNameForCDL) {
	ImageUpgradeProviderFactory imageupgradeProviderFactory = BeanUtils.getImageUpgradeProviderFactory();
	ImageUpgradeProvider imageProvider = imageupgradeProviderFactory.getImageUpgradeProvider(downloadType, dut);
	boolean response = false;
	if (null != imageProvider) {
	    response = imageProvider.isImageAvailableInCDL(dut, buildNameForCDL);
	} else {
	    LOGGER.error("ImageDownloadProvider is null. Image check cannot be done!!!");
	}
	return response;
    }

    /**
     * Method to retrieve current execution mode
     * 
     * @param dut
     *            {@link Dut}
     * @return current execution mode
     */
    public String getCurrentExecutionMode(Dut dut) {
	LOGGER.debug("STARTING METHOD: getCurrentExecutionMode()");
	String executionMode = ExecutionMode.MODE_FAILURE.get();

	// retrieve channel locator URL
	if (null == rdkVideoDeviceProvider) {
	    rdkVideoDeviceProvider = BeanUtils.getRdkVideoDeviceProvider();
	}
	if (null != rdkVideoDeviceProvider) {
	    String channelLocatorUrl = rdkVideoDeviceProvider.getCurrentChannelLocatorUrl(dut);
	    if (CommonMethods.isNotNull(channelLocatorUrl)) {
		if ((CommonMethods.patternMatcher(channelLocatorUrl,
			AutomaticsPropertyUtility
				.getProperty(AutomaticsConstants.PATTERN_FOR_IPLINEAR_CHANNEL_LOCATOR_URL)))
			|| (CommonMethods.patternMatcher(channelLocatorUrl, AutomaticsPropertyUtility
				.getProperty(AutomaticsConstants.PATTERN_FOR_IPLINEAR_CHANNEL_LOCATOR_URL_MPD)))) {
		    executionMode = "IPLINEAR_OR_GRAM";
		}
		// }
		else if (CommonMethods.patternMatcher(channelLocatorUrl,
			AutomaticsConstants.PATTERN_FOR_QAM_CHANNEL_LOCATOR_URL)) {
		    executionMode = "SP";
		} else {
		    LOGGER.error("DeviceConfig mode could not be verified. Hence setting mode as unknown");
		    executionMode = ExecutionMode.UNKNOWN.get();
		}

	    }
	}

	LOGGER.info("Execution mode of device after initialization is " + executionMode);
	return executionMode;
    }

    /**
     * Get the latest build image version
     * 
     * @param Dut
     * @param boolean
     */
    public String getLatestBuildImageVersionForCdlTrigger(Dut dut, boolean isDowngradeAllowed) {
	LOGGER.debug("STARTING METHOD: getLatestBuildImageVersionForCdlTrigger()");
	CodeDownloadProvider codeDownloadProvider = BeanUtils.getCodeDownloadProvider();
	if (null == codeDownloadProvider) {
	    LOGGER.error("Code Download Provider Instance is null");
	    return null;
	} else {
	    return codeDownloadProvider.getLatestBuildImageVersionForCdlTrigger(dut, isDowngradeAllowed);
	}
    }

    /**
     * Get the latest available image
     * 
     * @param String
     * @param String
     */
    public String getLatestAvailableImage(String imageNamePrefix, String buildType) {
	LOGGER.debug("STARTING METHOD: getLatestAvailableImage()");
	CodeDownloadProvider codeDownloadProvider = BeanUtils.getCodeDownloadProvider();
	if (null == codeDownloadProvider) {
	    LOGGER.error("Code Download Provider Instance is null");
	    return null;
	} else {
	    return codeDownloadProvider.getLatestAvailableImage(imageNamePrefix, buildType);
	}
    }

    /**
     * Hard power cycles a dut outlet OFF and then ON using the WTI device.
     * 
     * @param dut
     *            The {@link Dut} object
     * 
     * @throws FailedTransitionException
     *             if an exception occurs during operation
     */
    public void reboot(Dut dut) {

	try {

	    if (SupportedModelHandler.isRDKB(dut) && AutomaticsPropertyUtility
		    .getProperty(WebPaConstants.PROP_KEY_RDKB_WEBPA_REBOOT_ENABLE, "N").equalsIgnoreCase("Y")
		    && CommonMethods.isSTBAccessible(dut)) {
		boolean isStbRebooted = false;
		try {
		    tapApi.setWebPaParams(dut, WebPaConstants.WEBPA_PARAM_DEVICE_CONTROL_DEVICE_REBOOT,
			    AutomaticsConstants.DEVICE, AutomaticsConstants.CONSTANT_0);
		    isStbRebooted = CommonMethods.isSTBRebooted(tapApi, dut, AutomaticsConstants.THIRTY_SECONDS,
			    AutomaticsConstants.CONSTANT_10);
		} catch (Exception e) {
		    LOGGER.error(e.getMessage());
		}
		if (!isStbRebooted) {
		    if (CommonMethods.isSTBAccessible(dut)) {
			tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
		    } else {
			// Some time device is not getting rebooted because
			// of connectivity issues. So
			// forcefully rebooting with power switch.
			if (!NonRackUtils.isNonRack()) {
			    dut.getPower().reboot();
			}
		    }
		}
	    } else if (CommonMethods.isSTBAccessible(dut)) {
		tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_REBOOT);
	    } else {
		// Some time device is not getting rebooted because of
		// connectivity issues. So
		// forcefully rebooting with power switch.
		if (!NonRackUtils.isNonRack()) {
		    dut.getPower().reboot();
		}
	    }
	    if (SupportedModelHandler.isRDKVClient(dut) || SupportedModelHandler.isRDKB(dut)) {

		/*
		 * Gets the new ip address once after the reboot
		 */
		// Wait for box to reboot and get the video.
		tapApi.waitAfterHardRebootInitiated(dut);

	    }
	    // Increment the known reboot Counter.
	    AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());

	} catch (Exception ppex) {
	    LOGGER.error(
		    "Failed to reboot the box using the command. Rebooting using 'reboot' power switch. ERROR MESSAGE: "
			    + ppex.getMessage());

	    try {

		if (!NonRackUtils.isNonRack()) {
		    dut.getPower().reboot();

		    // Increment the known reboot Counter.
		    AutomaticsTapApi.incrementKnownRebootCounter(dut.getHostMacAddress());

		    if (SupportedModelHandler.isRDKVClient(dut) || SupportedModelHandler.isRDKB(dut)) {

			/*
			 * Gets the new ip address once after the reboot
			 */
			// Wait for box to reboot and get the video.
			tapApi.waitAfterHardRebootInitiated(dut);

		    }
		}
	    } catch (Exception e) {
		LOGGER.error(
			"Exception Catched - Exception occured while dut reboot throws Power Provider Exception and command execution also throws exception since box is already rebooted. ERROR MESSAGE:"
				+ e.getMessage());
	    }
	}
    }

    /**
     * Get the TR-069 parameter values.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return TR69 parameter values
     */
    public String getTR69ParameterValues(Dut dut, List<String> paramNameList) {

	String[] commands = null;

	if (null != paramNameList) {
	    commands = paramNameList.toArray(new String[paramNameList.size()]);
	}

	return executeTr69Command(dut, commands);
    }

    /**
     * Set the TR-069 parameter values.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            TR-069 parameter
     * @return The status of execution.
     */
    public String setTR69ParameterValues(Dut dut, List<Parameter> parameterList) {

	String response = "Failed to set TR69 param value";
	TR69Provider tr69Provider = BeanUtils.getTR69Provider();

	LOGGER.info("About to set values for TR69 params {}", parameterList);
	if (null != tr69Provider) {
	    for (int i = 1; i <= 2; i++) {
		response = tr69Provider.setTr69ParameterValues(dut, parameterList);

		LOGGER.info("TR69 set response {}", response);

		// Check response
		if (CommonMethods.isNotNull(response)) {
		    break;
		} else if (i == 2) {
		    LOGGER.info("Failed to set TR 069 param after retry. No respone received");
		} else {
		    AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
		}
	    }
	} else {
	    LOGGER.error(AutomaticsConstants.BEAN_NOT_FOUND_LOG, "TR69Provider", BeanConstants.BEAN_ID_TR69_PROVIDER);
	}

	return response;
    }

}
