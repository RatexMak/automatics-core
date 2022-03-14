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
package com.automatics.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.automatics.appenders.BuildAppenderManager;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LinuxCommandConstants;
import com.automatics.constants.SnmpConstants;
import com.automatics.constants.XconfConstants;
import com.automatics.core.ResponseObject;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.StbDetailsDO;
import com.automatics.dataobjects.StbDetailsDO.StbDetails;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.device.DutImpl;
import com.automatics.device.config.DeviceConfig;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.ExecutionMode;
import com.automatics.enums.ProcessRestartOption;
import com.automatics.enums.RemoteControlType;
import com.automatics.enums.ServiceType;
import com.automatics.enums.TestType;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.exceptions.TestException;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.DeviceAccessValidator;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.providers.rack.exceptions.PowerProviderException;
import com.automatics.providers.snmp.SnmpProvider;
import com.automatics.providers.snmp.SnmpProviderFactory;
import com.automatics.providers.trace.AbstractTraceProviderImpl;
import com.automatics.providers.trace.SerialTraceProvider;
import com.automatics.providers.trace.TraceServerConnectionStatus;
import com.automatics.rack.RackDeviceValidationManager;
import com.automatics.rack.RackInitializer;
import com.automatics.snmp.SnmpCommand;
import com.automatics.snmp.SnmpDataType;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpProtocol;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.tr69.Tr69Constants;
import com.automatics.utils.xconf.XConfUtils;
import com.automatics.webpa.WebPaConnectionHandler;
import com.automatics.webpa.WebPaParameter;
import com.automatics.webpa.WebPaServerResponse;
import com.fasterxml.jackson.databind.JsonNode;

public class CommonMethods {

    /** SLF4J logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonMethods.class);

    /** Empty string. */
    private static final String EMPTY_STRING = "";

    /** Colon as separator. */
    private static final String COLON_SEPERATOR = ":";

    // / ** Location of /opt/rfc.properties */
    public static final String FILE_NAME_RFC_PROPERTIES = "rfc.properties";
    /** file path for opt directory */
    public static final String NVRAM_PATH = "/nvram/";

    /** constant represents the status code 200 */
    public static final String STATUS_CODE_SUCCESS = "status_code:200";

    /** File path where ini files for RFC features will be generated */
    public static String OPT_RFC_PATH_WITH_RFC_EXTENTION = "<RFCPATH>/.RFC_";

    /** File extension .ini for RFC features */
    public static final String FILE_EXTENSION_INI = ".ini";

    /** File path where list files for RFC features will be generated */
    public static final String TMP_RFC_PATH_WITH_RFC_EXTENTION = "/tmp/RFC/.RFC_";

    /** File extension .list for RFC features */
    public static final String FILE_EXTENTION_LIST = ".list";

    /** Enable flag for RFC feature */
    public static final String ENABLE_FLAG_FOR_RFC = "ENABLE";

    // /** Property to hold box ip model */
    public static final String PROP_KEY_BOXIP_BOXMAC_MODEL = "get.boxip.boxmac.model";

    /** Effective Immediate flag for RFC feature */
    public static final String EFFECTIVE_IMMEDIATE_FLAG_FOR_RFC = "effectiveImmediate";

    /** Constant to hold single under score character */
    public static final String CHARACTER_UNDER_SCORE = "_";

    /** Export logs from RFC ini file */
    public static final String LOG_EXPORT_RFC = "export RFC_";
    /** Equals delimiter. */
    public static final String DELIMITER_EQUALS = "=";
    /** Text for "true". */
    public static final String TRUE = "true";
    /** Single space character. */
    public static final String SINGLE_SPACE_CHARACTER = " ";
    /** Linux Command 'cat' to display the content. */
    public static final String CMD_LINUX_CAT = "cat";

    /** 30 seconds in Millis */
    @SuppressWarnings("unused")
    private static final int THIRTY_SECONDS_MILLIS = 3000;

    private static DeviceConnectionProvider deviceConnectionProvider;

    /** keyword for jenkins JOB_NAME */
    public static final String JENKINS_JOB_NAME = "JOB_NAME";

    /** keyword for jenkins BUILD_TAG */
    public static final String JENKINS_BUILD_TAG = "BUILD_TAG";

    /** keyword for jenkins HUDSON_URL */
    public static final String JENKINS_HUDSON_URL = "HUDSON_URL";

    /** keyword for mac address */
    public static final String MAC_ADDRESS_KEYWORD = "<MAC>";

    /** variable holding group number 1 for pattern matching */
    public static final int PATTERN_MATCHER_GROUP_ONE = 1;

    /** Variable to hold the model name of RDKB device */
    static final String RDKB_DEVICE_MODEL_PROPERTY = "rdkb.device.model";

    /**
     * System command to get the erouter0 Mac Address.
     */
    private static final String CMD_GET_EROUTER_MAC_ADDRESS = "/sbin/ifconfig erouter0 | grep -i HWaddr";

    /**
     * Command to get ESTB MAC address for gateway devices.
     */
    private static final String CMD_GET_ESTB_MAC_ADDRESS = "cat /tmp/.estb_mac";

    /** grep command. */
    public static final String GREP_COMMAND = "grep -i ";
    /** RFC Variable INI File */
    public static final String FILE_RFC_VARIABLE_INI = "/rfcVariable.ini";
    /** Cmd out put string "No such file or directory". */
    public static final String NO_SUCH_FILE_OR_DIRECTORY = "No such file or directory";

    /** Command to fetch the reboot reason from /opt/logs/rebootInfo.log */
    private static final String CMD_GET_REBOOT_REASON_FROM_REBOOT_INFO_LOG = "cat /opt/logs/rebootInfo.log | grep RebootReason | tail -1";

    /**
     * Command to get reboot reason from /opt/logs/PreviousLogs/rebootInfo.log file
     */
    public static final String CMD_GET_REBOOT_REASON_FROM_PREVIOUS_REBOOT_INFO_LOG = "cat /opt/logs/PreviousLogs/rebootInfo.log | grep RebootReason | tail -1";;

    /** Process WPENetworkProcess */
    public static final String PROCESS_WPE_NETWORK_PROCESS = "WPENetworkProcess";

    /** Process WPEWEBProcess */
    public static final String PROCESS_WPE_WEB_PROCESS = "WPEWebProcess";

    /** Process rtrmfplayer */
    public static final String PROCESS_RTRMFPLAYER = "rtrmfplayer";

    /** Command to stop rfc config */
    public static final String CMD_STOP_RFC_CONFIG = "systemctl stop rfc-config";

    /** RFC PATH pattern from /etc/rfc.properties */
    public static final String RFC_PATH_PATTERN = "\\S+\\=\"(\\S+)\"";

    /** TR181_STORE_FILENAME pattern from /etc/rfc.properties */
    public static final String TR181_STORE_FILENAME_PATTERN = "\\S\\=\"(\\S+)";

    /** constant represents the rfcVariable.ini **/
    public static final String RFC_VARIABLE_INI = "rfcVariable.ini";

    /** constant represents the rfc path key info in /etc/rfc.properties **/
    public static final String RFC_PATH_KEY = "RFC_PATH";

    /** constant represents the rfc RAM path key info in /etc/rfc.properties **/
    public static final String RFC_RAM_PATH_KEY = "RFC_RAM_PATH";

    /**
     * constant represents the TR181_STORE_FILENAME key info in /etc/rfc.properties
     **/
    public static final String TR181_STORE_FILENAME_KEY = "TR181_STORE_FILENAME";

    /**
     * constant represents the rfc path string which will replaced with the actual path
     **/
    public static final String TEMP_RFC_PATH = "<RFCPATH>";

    /**
     * constant represents the rfc RAM path string which will replaced with the actual path
     **/
    public static final String TEMP_RFC_RAM_PATH = "<RFCRAMPATH>";

    /** constant represents the persistence path String **/
    public static final String PERSISTENT_PATH = "$PERSISTENT_PATH";

    /** Location of /opt/rfc.properties */
    public static final String FILE_PATH_ETC_RFC_PROPERTIES = "/etc/rfc.properties";

    /** file path for opt directory */
    public static final String OPT_PATH = "/opt/";

    /** Constant for the command to get the 'UpTime' from the device */
    public static String CMD_GET_UPTIME_FROM_DEVICE = "uptime | awk '{print \\$3}'";

    /** store value IP */
    public static final String IP = "IP";

    /** store value playback mode linear */
    public static final String PLAYBACK_MODE_LINEAR = "playback_mode_linear";

    /** store value VOD */
    public static final String PLAYBACK_MODE_VOD = "playback_mode_vod";

    /** PC data service header Content-Type */
    public static final String PC_DS_HEADER_CONTENT_TYPE = "Content-Type";

    /** PC data service header User-Agent */
    public static final String PC_DS_HEADER_USER_AGENT = "User-Agent";

    /** PC data service header Accept */
    public static final String PC_DS_HEADER_ACCEPT = "Accept";

    /** PC data service header Accept-Features */
    public static final String PC_DS_HEADER_ACCEPT_FEATURES = "Accept-Features";

    /** PC data service header Authorization */
    public static final String PC_DS_HEADER_AUTHORIZATION = "Authorization";

    /** PC data service SAT token type */
    public static final String TOKEN_TYPE_BEARER = "Bearer ";

    /** constant represents the service inactive status **/
    public static final String SERVICE_INACTIVE_STATUS = "inactive";

    /** constant represents the service active status **/
    public static final String SERVICE_ACTIVE_STATUS = "active";

    private static final String SERVICE_STATUS = "Active: (\\S+)";

    private static final String SYSTEMCTL_STATUS = "systemctl status ";

    public static final String DELAY_BETWEEN_REBOOTS = "buildAppender.delay.reboot";
    /**
     * properties that hold the models ca certificate expire models
     */
    private static final String STB_PROPS_CATEGORY_RDKV_DOCSIS_EXPIRY_CANDIDATE = "category.model.docsis.expiry.candidate.RDKV";

    /**
     * stb property for additional wait time required before checking ip connectivity
     */
    private static final String DOCSIS_EXPIRY_TIME = "docsis.expiry.time";

    public static final String CMD_TO_GET_EROUTER0_CONFIGURATION = "/sbin/ifconfig erouter0";
    /**
     * regular expression to get erouter0 ipv4 address
     */
    public static final String REGEX_TO_GET_EROUTER0_IPV4ADDRESS = "inet addr:([\\d.]+)";

    /**
     * regular expression to get erouter0 ipv6 address
     */
    public static final String REGEX_TO_GET_EROUTER0_IPV6ADDRESS = "inet6 addr: ([\\w:]+)/[\\w:]+ Scope:Global";

    public static final String RFC_TR69ENABLE = "RFC_#TR69ENABLE";
    public static final String TR69 = "TR69";
    public static final String RFC_PARAMETER = "rfc_#";

    /** Cmd out put string "No route to host". */
    public static final String NO_ROUTE_TO_HOST = "No route to host";

    /**
     * Method to get the eCM Mac address corresponding to the given eSTB Mac from RACK
     * 
     * @param eStbMacAddress
     * @return ecmMacAddress
     */
    public static String getEcmMacFromRack(String eStbMacAddress) {

	LOGGER.trace("Entering getEcmMacFromRack()");

	String ecmMacAddress = null;

	DeviceManager manager = DeviceManager.getInstance();
	Device device = manager.findRackDevice(eStbMacAddress);
	ecmMacAddress = device.getMcardMacAddress();

	LOGGER.info("getEcmMacFromRack() - Obtained eCM Mac Address from RACK as " + ecmMacAddress + " for eSTB Mac - "
		+ eStbMacAddress);

	return ecmMacAddress;
    }

    /**
     * Get the STB IP address from RACK.
     * 
     * @param stbDetailsDO
     */
    public static void loadEstbIpAddressFromRack(StbDetailsDO stbDetailsDO) {

	LOGGER.trace("Entering loadEstbIpAddressFromRack()");

	DeviceManager manager = DeviceManager.getInstance();
	Device device = manager.findRackDevice(stbDetailsDO.getMacAddress());
	stbDetailsDO.setIpAddress(device.getHostIpAddress());
	LOGGER.info("ESTB IP Address for STB " + stbDetailsDO.getMacAddress() + " from RACK is - "
		+ device.getHostIpAddress());

	LOGGER.trace("Exiting loadEstbIpAddressFromRack()");
    }

    /**
     * Parses xml string based on jaxb generated class
     * 
     * @param rootElement
     *            - Jaxb generated class
     * @param xmlData
     *            - Xml data
     * @return Returns a java object holding xml data
     */
    @SuppressWarnings("unchecked")
    public static <T> T unMarshallXml(Class<T> rootElement, String xmlData) {

	LOGGER.trace("Entering unMarshallXml()");

	T data = null;

	try {

	    JAXBContext context = JAXBContext.newInstance(rootElement);
	    Unmarshaller unmarshal = context.createUnmarshaller();
	    ByteArrayInputStream input = new ByteArrayInputStream(xmlData.getBytes());
	    data = (T) unmarshal.unmarshal(input);

	} catch (Exception exception) {
	    LOGGER.error("unMarshallXml() - Exception while unmarshalling XML data.\n" + xmlData, exception);
	}

	LOGGER.trace("Exiting unMarshallXml()");

	return data;
    }

    /**
     * Helper method to extract the JSON value using key.
     * 
     * @param propertyKey
     *            The JSON Key.
     * @param jsonDetails
     *            The JSON message.
     * @return JSON node value
     */
    public static String extractJsonProperty(String propertyKey, JsonNode jsonDetails) {
	if (jsonDetails != null && null != jsonDetails.path(propertyKey)) {
	    propertyKey = jsonDetails.path(propertyKey).textValue();
	    propertyKey = propertyKey != null ? propertyKey.trim() : AutomaticsConstants.EMPTY_STRING;
	} else {
	    LOGGER.error("[MDS QUERY LOG]:No Field - '" + propertyKey + "' in device details");
	}
	return propertyKey;
    }

    /**
     * Method to execute list of linux commands based on expect strings in serial console and read response.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param list
     *            of commands list of Linux commands.
     * @param list
     *            of expectStrings list of expect strings after each command
     * @param delay
     *            any delay time to be provided before starting sending the list of commands
     * 
     * @return the consolidated output of linux commands executed.
     * @author Susheela_C
     */
    public static String executeCommandInSerialConsoleInteractiveMode(Dut dut, String[] commands,
	    String[] expectStrings, long delay) throws Exception {

	LOGGER.debug("Entering into executeCommandInSerialConsoleInteractiveMode()");

	String response = null;
	String consolidatedResponse = "";
	SerialTraceProvider traceProvider = null;
	boolean isHexString = false;

	try {
	    Device device = (Device) dut;
	    traceProvider = (SerialTraceProvider) device.getSerialTrace();
	    if (null != traceProvider) {

		traceProvider.stopBuffering();
		traceProvider.stopTrace();
		traceProvider.startTrace();
		traceProvider.startBuffering();

		AutomaticsUtils.sleep(AutomaticsConstants.FIFTEEN_SECONDS);
		if (traceProvider.getTraceStatus().equals(TraceServerConnectionStatus.CONNECTED.name())) {

		    traceProvider.sendTraceString("root", isHexString);
		    // delay is introduced if a wait time is needed before executing
		    // commands after starting trace
		    AutomaticsUtils.sleep(delay);
		    for (int commandIndex = 0; commandIndex < commands.length; commandIndex++) {
			traceProvider.sendTraceString(formatSerialConsoleCommand(commands[commandIndex]), isHexString);
			AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
			traceProvider.sendTraceString(" ", isHexString);
			AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
			response = traceProvider.getBufferData();
			LOGGER.info("Response after command " + commands[commandIndex] + " : " + response);

			LOGGER.info("Expecting string : " + expectStrings[commandIndex]);
			if (null != response && !response.isEmpty()) {
			    if (response.contains(expectStrings[commandIndex])) {
				LOGGER.info(
					"Expect string present after command " + commands[commandIndex] + " execution");
			    } else {
				LOGGER.error("Unable to identify the expect string in the response received");
				consolidatedResponse = null;
				break;
			    }
			}
			consolidatedResponse = consolidatedResponse + response;

		    }
		} else {
		    LOGGER.error("Unable to connect to the serial console of the STB - " + dut.getHostMacAddress());
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in executeCommandInSerialConsoleInteractiveMode()", exception);
	    throw exception;
	} finally {
	    try {
		if (traceProvider != null) {
		    // ALSO send a command to come to command prompt
		    traceProvider.stopTrace();
		}
	    } catch (Exception e) {
		LOGGER.error(
			"Exception occured while closing the serial console in executeCommandInSerialConsoleInteractiveMode()",
			e);
	    }
	}
	LOGGER.info("Serial console response is - " + consolidatedResponse);

	LOGGER.debug("Exiting from executeCommandInSerialConsoleInteractiveMode()");

	return consolidatedResponse;
    }

    /**
     * Method to execute linux command. This method is used to execute the command from inside box.
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param command
     *            linux command to execute
     * @return command response from stb
     */
    public static String executeCommand(Dut dut, String command, long timeout) {

	LOGGER.debug("STARTING METHOD: CommonMethods.executeCommand()");

	String response = null;
	// Instance of SshConnectionHandler
	// SshConnectionHandler sshConnectionHandler = null;
	try {
	    // sshConnectionHandler = SshConnectionHandler.get();
	    LOGGER.debug("Executing command - " + command);
	    response = deviceConnectionProvider.execute((Device) dut, command);
	    // response = sshConnectionHandler.execute(dut, command);
	    LOGGER.debug("Response - " + response);
	} catch (Exception exception) {
	    LOGGER.error("Exception occured - CommonMethods.execute() - " + exception.getMessage(), exception);
	}

	LOGGER.debug("ENDING METHOD: CommonMethods.executeCommand()");

	return response;
    }

    /**
     * Method to retrieve value of response at given position
     * 
     * @param response
     *            response to be validated
     * @param reqItemPosition
     *            position of item whose value is to be retrieved
     * @return list of item values
     * @author Soumya V
     */
    public static List<String> getItemValueAtGivenPositionOfResponse(String response, int reqItemPosition) {
	LOGGER.info("STARTING METHOD: CommonMethods.getItemValueAtGivenPositionOfResponse()");
	List<String> itemValueList = new ArrayList<String>();
	try {
	    int itemNumber = 0;
	    if (CommonMethods.isNotNull(response)) {
		for (String value : response.split("\n")) {
		    for (String valueItr : value.split(" ")) {
			if (CommonMethods.isNotNull(valueItr)) {
			    if (itemNumber == reqItemPosition) {
				itemValueList.add(valueItr);
				break;
			    }
			    itemNumber++;
			}
		    }
		    itemNumber = 0;
		}
	    }
	} catch (Exception e) {
	    LOGGER.info("EXCEPTION : " + e.getMessage() + e.getStackTrace());
	}
	LOGGER.info("ENDING METHOD: CommonMethods.getItemValueAtGivenPositionOfResponse()");
	return itemValueList;
    }

    /**
     * Method to set Preferred Gateway Type
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param type
     *            Preferred Gateway Type Eg: XG1, XB3
     * @return boolean if successfully set given gateway type
     */
    public static boolean setPreferredGatewayType(Dut dut, AutomaticsTapApi tapEnv, String type) {

	LOGGER.debug("STARTING METHOD: setPreferredGatewayType()");

	boolean status = false;
	String preferredGatewayType = null;

	LOGGER.info("Going to set Preferred Gateway Type as " + type);

	tapEnv.executeCommandUsingSsh(dut, "host-if -H 127.0.0.1 -p 56981 -s \""
		+ Tr69Constants.TR69_PARAM_PREFERED_GATEWAY + "\" -v \"" + type + "\"");

	preferredGatewayType = tapEnv.executeCommandUsingSsh(dut,
		"host-if -H 127.0.0.1 -p 56981 -g \"" + Tr69Constants.TR69_PARAM_PREFERED_GATEWAY + "\"");

	if (CommonMethods.isNotNull(preferredGatewayType)) {

	    status = CommonMethods.patternMatcher(preferredGatewayType, "Value\\s*:\\s*\"" + type + "\"");

	    if (status) {
		LOGGER.info("successfully set Preferred Gateway Type as " + type + " for the Dut "
			+ dut.getHostMacAddress());
	    } else {
		LOGGER.error("ERROR: Unable to set Preferred Gateway Type as " + type + " for the Dut "
			+ dut.getHostMacAddress());
	    }
	}

	LOGGER.debug("ENDING METHOD: setPreferredGatewayType()");

	return status;
    }

    /**
     * Checks if the execution is CI build
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean isCiBuildExecution() {

	String serviceType = System.getProperty(AutomaticsConstants.SERVICE_NAME, "");

	return serviceType.contains(ServiceType.CI_VERIFICATION.get());
    }

    /**
     * helper method to obtain the string for the specific pattern without case
     * 
     * @param response
     * @param patternToMatch
     * @return string found
     */
    public static String caseInsensitivePatternFinder(String response, String patternToMatch) {

	LOGGER.debug("STARTING METHOD: caseInsensitivePatternFinder()");

	// matched string
	String matchedString = "";
	// instance of pattern for match
	Pattern pattern = null;
	// Instance of matcher
	Matcher matcher = null;

	try {

	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(patternToMatch)) {
		pattern = Pattern.compile(patternToMatch, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(response);
		if (matcher.find()) {
		    matchedString = matcher.group(PATTERN_MATCHER_GROUP_ONE);
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in caseInsensitivePatternFinder()", exception);
	}

	LOGGER.info("pattern matched string is -" + matchedString);

	LOGGER.debug("ENDING METHOD: caseInsensitivePatternFinder()");

	return matchedString;
    }

    /**
     * Method to execute command in Atom console
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param command
     *            command to execute
     * @return server response
     */
    public static String executeCommandInAtomConsole(Dut dut, AutomaticsTapApi tapApi, String command,
	    String atomServerIp) throws TestException {
	return tapApi.executeCommandOnAtom(dut, command);
    }

    /**
     * Method to retrieve atom server ip from STB
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @return Atom server Ip
     */
    public static String getAtomServerIp(Dut dut, AutomaticsTapApi tapApi) {
	return getPropertyFromDeviceProperties(dut, tapApi, "ATOM_IP", "ATOM_IP=(\\d+\\.\\d+\\.\\d+\\.\\d+)");
    }

    /**
     * Method to verify whether the STB is having PROD image or not
     * 
     * @param tapEnv
     * @param dut
     * @return
     */
    public static boolean isProdImage(AutomaticsTapApi tapEnv, Dut dut) {
	// validation status
	boolean status = false;
	// retrieve current firmware version
	String firmwareVersion = dut.getFirmwareVersion();

	if (CommonMethods.isNotNull(firmwareVersion)) {
	    String prosBuildKeys = AutomaticsPropertyUtility
		    .getProperty(AutomaticsConstants.PROPERTY_PROD_BUILD_KEYWORDS);
	    if (CommonMethods.isNotNull(prosBuildKeys)) {
		List<String> prodKeyList = CommonMethods.splitStringByDelimitor(prosBuildKeys,
			AutomaticsConstants.COMMA);
		for (String prodKey : prodKeyList) {
		    if (firmwareVersion.contains(prodKey)) {
			status = true;
			break;
		    }
		}

	    }
	}
	return status;
    }

    /**
     * Method to restart trace
     * 
     * @param dut
     *            instance of {@link Dut}
     */
    public static void restartSshTraceProvider(Dut dut) {

	try {

	    if (null != dut.getTrace() && dut.getTrace() instanceof AbstractTraceProviderImpl) {
		((AbstractTraceProviderImpl) dut.getTrace()).stopTrace();
		AutomaticsUtils.sleep(AutomaticsConstants.FIFTEEN_SECONDS);
		((AbstractTraceProviderImpl) dut.getTrace()).startTrace();
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception caught while restarting dut trace server. " + e.getMessage(), e);
	}
    }

    /**
     * 
     * Method to check if the given box have LXC container support
     * 
     * @param dut
     *            {@code Dut}
     * @param tapEnv
     *            {@code AutomaticsTapApi}
     * @return lxc support status
     */
    public static boolean isBoxHavingLxcSupport(Dut dut, AutomaticsTapApi tapEnv) {

	LOGGER.debug("Starting method isBoxHavingLxcSupport");
	boolean status = false;

	String response = tapEnv.executeCommandUsingSsh(dut, LinuxCommandConstants.COMMAND_CONTAINER_SUPPORT);
	LOGGER.info("Response from STB - " + response);
	if (CommonMethods.isNotNull(response)) {
	    status = response.contains(AutomaticsConstants.STATUS_CONTAINER_SUPPORT_ENABLED);
	}

	LOGGER.info("Is lxc supported  : " + status);
	LOGGER.debug("Ending method isBoxHavingLxcSupport");

	return status;

    }

    /**
     * Executes the commands by login to the particular server.
     * 
     * @param serverDetails
     *            The server details from where the command to be executed.
     * @param commands
     *            Single Linux command
     * 
     * @return The command response.
     */
    @Deprecated
    public static String execute(Dut dut, String command) {
	return deviceConnectionProvider.execute((Device) dut, command);
    }

    /**
     * Method to check whether atom sync is available in RDKB DeviceConfig
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @return true if atom syync is available else false
     */
    public static boolean isAtomSyncAvailable(Dut dut, AutomaticsTapApi tapApi) {
	LOGGER.debug("STARTING METHOD: isAtomSyncAvailable");
	// validation status
	boolean status = false;
	// read atom sync value form device.properties
	String atomSyncValue = getPropertyFromDeviceProperties(dut, tapApi, "ATOM_SYNC", "ATOM_SYNC=(\\w+)");
	// validate
	if (CommonMethods.isNotNull(atomSyncValue)) {
	    status = atomSyncValue.trim().equalsIgnoreCase("yes");
	}
	LOGGER.debug("ENDING METHOD: isAtomSyncAvailable");
	return status;
    }

    /**
     * Method to retrieve property form /etc/device.properties
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @return property value from /etc/device.properties
     */
    public static String getPropertyFromDeviceProperties(Dut dut, AutomaticsTapApi tapApi, String propertyKey,
	    String patternToMatchTheValue) {

	LOGGER.debug("STARTING METHOD: getPropertyFromDeviceProperties()");

	// hold the property value
	String value = null;
	// loop count
	int maxLoopCount = 3;
	String command = LinuxCommandConstants.COMMAND_CAT + AutomaticsConstants.SPACE
		+ AutomaticsConstants.DEVICE_PROPERTIES_FILE_PATH + AutomaticsConstants.SPACE
		+ AutomaticsConstants.LINUX_PIPE_SYMBOL + AutomaticsConstants.SPACE + LinuxCommandConstants.COMMAND_GREP
		+ AutomaticsConstants.SPACE;
	LOGGER.info("command to be executed to get device properties is " + command);
	for (int index = 0; index < maxLoopCount; index++) {
	    String response = tapApi.executeCommandUsingSsh(dut, command + propertyKey);
	    if (CommonMethods.isNotNull(response)) {
		value = CommonMethods.patternFinder(response, patternToMatchTheValue);
	    }
	    if (CommonMethods.isNotNull(value)) {
		break;
	    }
	}

	LOGGER.debug("ENDING METHOD: getPropertyFromDeviceProperties()");

	return value;
    }

    /**
     * helper method that executes the command in atom console if available else runs the command in arm console and
     * returns the response
     * 
     * @param dut
     *            instance
     * @param tapApi
     * @param command
     * @param isAtomAvailable
     * @return string response of the command
     * 
     */
    public static String executeCommandInAtomConsoleIfAtomIsPresentElseInArm(Dut dut, AutomaticsTapApi tapApi,
	    String command, boolean isAtomSyncAvailable) {

	LOGGER.debug("STARTING METHOD: executeCommandInAtomConsoleIfAtomIsPresentElseInArm");
	String response = null;

	if (isAtomSyncAvailable) {
	    response = tapApi.executeCommandOnAtom(dut, command);
	} else {
	    response = tapApi.executeCommandUsingSsh(dut, command);
	}
	LOGGER.debug("ENDING METHOD: executeCommandInAtomConsoleIfAtomIsPresentElseInArm");
	return response;
    }

    /**
     * helper method to obtain the number of matched strings for a particular pattern
     * 
     * @param response
     * @param patternToMatch
     * @return count
     */

    public static int numberOfMatchesFound(String response, String patternToMatch) {

	LOGGER.debug("STARTING METHOD: numberOfMatchesFound");
	// instance of pattern for match
	Pattern pattern = null;
	// Instance of matcher
	Matcher matcher = null;
	// variable to count the no.of matches
	int count = 0;

	try {

	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(patternToMatch)) {

		pattern = Pattern.compile(patternToMatch);
		matcher = pattern.matcher(response);

		while (matcher.find()) {
		    count++;
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in patternMatcher()", exception);
	}

	LOGGER.info("No.of matches found is -" + count);

	LOGGER.debug("ENDING METHOD: numberOfMatchesFound");

	return count;

    }

    /**
     * method to get erouter0 ipv4 address
     * 
     * @param Dut
     *            dut
     * @param EcatsTapApi
     *            tapEnv
     * @return erouter0ipv4address
     * 
     */

    public static String getErouter0ipv4Address(Dut dut, AutomaticsTapApi tapEnv) {

	LOGGER.debug("STARTING METHOD: getErouter0ipv4Address");

	String erouter0ipv4Address = null;

	String response = tapEnv.executeCommandUsingSsh(dut, CMD_TO_GET_EROUTER0_CONFIGURATION);

	erouter0ipv4Address = CommonMethods.patternFinder(response, REGEX_TO_GET_EROUTER0_IPV4ADDRESS);

	LOGGER.info("Erouter0ipv4 address -" + erouter0ipv4Address);

	LOGGER.debug("ENDING METHOD getErouter0ipv4Address");
	if (CommonMethods.isNull(erouter0ipv4Address)) {
	    throw new TestException("erouter0 ipv4 address is obtained as null from /sbin/ifconfig erouter0");
	}
	return erouter0ipv4Address;

    }

    /**
     * method to get erouter0 ipv6 address
     * 
     * @param Dut
     *            dut
     * @param EcatsTapApi
     *            tapEnv
     * @return erouter0ipv6address
     * 
     */
    public static String getErouter0ipv6Address(Dut dut, AutomaticsTapApi tapEnv) {

	LOGGER.debug("STARTING METHOD getErouter0ipv6Address");

	String erouter0ipv6Address = null;

	String response = tapEnv.executeCommandUsingSsh(dut, CMD_TO_GET_EROUTER0_CONFIGURATION);

	erouter0ipv6Address = CommonMethods.patternFinder(response, REGEX_TO_GET_EROUTER0_IPV6ADDRESS);

	LOGGER.info("Erouter0ipv6 address -" + erouter0ipv6Address);

	LOGGER.debug("ENDING METHOD getErouter0ipv6Address");
	if (CommonMethods.isNull(erouter0ipv6Address)) {
	    throw new TestException("erouter0 ipv6 address is obtained as null from /sbin/ifconfig erouter0");
	}
	return erouter0ipv6Address;

    }

    /**
     * @param fileName
     *            Name of resource file located in main/resource in ecats
     * @param logLocation
     *            Location in VM where file has to be copied
     * @return Returns status of copying as boolean
     * @throws IOException
     */
    public static boolean copyResourceFileToVM(String fileName, String logLocation) throws IOException {
	boolean returnStatus = false;
	InputStream resourceInputStream = null;
	BufferedInputStream bufferedResourceInputStream = null;
	BufferedReader bufferedResourceReader = null;
	BufferedWriter bufferedResourceWriter = null;
	try {
	    if (!checkIfFileExists(fileName)) {
		LOGGER.info("Copying resource file " + fileName);
		resourceInputStream = (CommonMethods.class.getClassLoader().getResourceAsStream(fileName));
		bufferedResourceInputStream = new BufferedInputStream(resourceInputStream);
		bufferedResourceReader = new BufferedReader(
			new InputStreamReader(bufferedResourceInputStream, StandardCharsets.UTF_8));
		File resourceFileCopyObject = new File(logLocation);
		bufferedResourceWriter = new BufferedWriter(new FileWriter(resourceFileCopyObject));
		String line = bufferedResourceReader.readLine();
		while (line != null) {
		    bufferedResourceWriter.write(line);
		    bufferedResourceWriter.newLine();
		    line = bufferedResourceReader.readLine();
		}
		returnStatus = checkIfFileExists(logLocation);
		LOGGER.info("Status of Copying ecats resource file " + fileName + " to " + logLocation + " is "
			+ returnStatus);
	    }
	} catch (Exception e) {
	    LOGGER.error("Caught exception while fetching script file" + e.getMessage());
	} finally {
	    if (bufferedResourceWriter != null) {
		bufferedResourceWriter.close();
	    }
	    if (bufferedResourceReader != null) {
		bufferedResourceReader.close();
	    }
	}
	return returnStatus;
    }

    /**
     * CHeck if the input file is present in Vm or not
     * 
     * @param fileName
     *            Complete path to file
     * @return Returns Boolean status based on file found or not.
     */
    public static boolean checkIfFileExists(String fileName) {
	boolean isExist = false;
	File fileObject = new File(fileName);
	if (fileObject != null && fileObject.isFile() && fileObject.exists()) {
	    isExist = true;
	}
	return isExist;
    }

    /**
     * This method splits a given string by the input delimitor.
     * 
     * @param inputString
     *            String to be split
     * @param delimitor
     *            Delimitor
     * @return Return list of split strings
     */
    public static List<String> splitStringByDelimitor(String inputString, String delimitor) {
	List<String> splitString = new ArrayList<String>();
	if (CommonMethods.isNotNull(inputString)) {
	    if (inputString.contains(delimitor)) {
		splitString.addAll(Arrays.asList(inputString.split(delimitor)));
	    } else {
		splitString.add(inputString);
	    }
	}
	return splitString;
    }

    /**
     * Method that just locks the given dut box
     * 
     * @param eCatsSettop
     * @param tapEnv
     * @return
     */
    public static boolean lockSettop(Dut eCatsSettop, AutomaticsTapApi tapEnv) {
	boolean lockStatus = false;
	try {
	    DeviceManager deviceManager = DeviceManager.getInstance();
	    deviceManager.lock(eCatsSettop);
	    lockStatus = true;
	} catch (Exception e) {
	    LOGGER.error("Attemp to lock dut " + eCatsSettop.getHostMacAddress() + " failed." + e.getMessage());
	}
	LOGGER.info("Lock status : " + eCatsSettop.getHostMacAddress() + " : " + lockStatus);
	return lockStatus;
    }

    /**
     * Method that just release the given dut box
     * 
     * @param eCatsSettop
     * @param tapEnv
     * @return
     */
    public static boolean unlockSettop(Dut eCatsSettop, AutomaticsTapApi tapEnv) {
	boolean lockStatus = false;
	try {
	    DeviceManager deviceManager = DeviceManager.getInstance();
	    deviceManager.release(eCatsSettop);
	    lockStatus = true;
	} catch (Exception e) {
	    LOGGER.error("Attemp to unlock dut " + eCatsSettop.getHostMacAddress() + " failed." + e.getMessage());
	}
	LOGGER.info("UnLock status : " + eCatsSettop.getHostMacAddress() + " : " + lockStatus);
	return lockStatus;
    }

    /**
     * Method to post the pay load data
     * 
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @param dut
     *            {@link Dut}
     * @param payLoadData
     *            Pay load data from STB props
     * @return http post - server response
     */
    public static int postPayLoadData(AutomaticsTapApi tapEnv, Dut dut, String payLoadData) {
	// HTTP client with TLS support
	HttpClient httpClient = null;
	// Default status code
	int statusCode = -1;
	// HTTP response
	HttpResponse response = null;
	try {
	    LOGGER.info("Creating HTTP client with TLS enabled");
	    httpClient = CommonMethods.getTlsEnabledHttpClient();
	    // Getting ESTB MAC address from STB
	    String stbMacAddress = null;
	    if (SupportedModelHandler.isRDKB(dut)) {
		stbMacAddress = dut.getHostMacAddress();
	    } else {
		stbMacAddress = CommonMethods.getSTBDetails(dut, tapEnv, StbDetails.ESTB_MAC);
	    }
	    if (CommonMethods.isNull(stbMacAddress)) {
		if (SupportedModelHandler.isRDKVClient(dut)) {
		    stbMacAddress = ((Device) dut).getEcmMac();
		} else if (SupportedModelHandler.isRDKVGateway(dut)) {
		    stbMacAddress = dut.getHostMacAddress();
		}
	    }
	    if (CommonMethods.isNotNull(stbMacAddress)) {
		// Updating the ESTB MAC address from the STB property file to
		// pay load data
		payLoadData = payLoadData.replace(AutomaticsConstants.CONSTANT_REPLACE_STBMAC, stbMacAddress);
		LOGGER.info("sendMessage : PROXY XCONF SERVER URL = " + payLoadData);
		HttpDelete request = new HttpDelete(payLoadData);
		LOGGER.info("Request: " + request);
		response = httpClient.execute(request);
		LOGGER.info("Response " + response.toString());
		StatusLine status = response.getStatusLine();
		statusCode = status.getStatusCode();
		LOGGER.info("sendMessage : XCONF SERVER STATUS = " + status);
		LOGGER.info("Status Code: " + statusCode);
		if (HttpStatus.SC_OK != status.getStatusCode()) {
		    LOGGER.error("Posting the configuration in xconf server failed. HTTP Status = " + status);
		}
	    } else {
		LOGGER.error("Failed to get ESTB MAC ADDRESS from STB");
	    }
	} catch (Exception ex) {
	    LOGGER.error("Exception occured while posting the delete request " + ex.getMessage());
	} finally {
	    if (null != httpClient) {
		httpClient.getConnectionManager().shutdown();
	    }
	}
	return statusCode;
    }

    /**
     * 
     * Helper method to copy and to update the rfc.properties with the Proxy XCONF update url
     * 
     * @param dut
     *            instance of STB
     * @param tapApi
     *            instance AutomaticsTapApi
     * @return true if file is copied and updated with proxy xconf url
     */
    public static boolean copyAndUpdateRfcPropertiesNewXconfUrl(Dut dut, AutomaticsTapApi tapApi, String xconfUrl) {
	String tempPathOfRfc = null;
	if (SupportedModelHandler.isRDKB(dut)) {
	    tempPathOfRfc = NVRAM_PATH + FILE_NAME_RFC_PROPERTIES;
	} else {
	    tempPathOfRfc = OPT_PATH + FILE_NAME_RFC_PROPERTIES;
	}
	return copyAndUpdateRfcPropertiesNewXconfUrl(dut, tapApi, xconfUrl, tempPathOfRfc);

    }

    /**
     * Utility Method to validate the search text in the file /opt/<RFC_PATH>/rfcVariable.ini.
     * 
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @param dut
     *            {@link Dut}
     * @param searchText
     *            String representing the text to be searched.
     * 
     * @return Boolean representing the result of the search.
     */
    public static boolean verifyRfcParam(AutomaticsTapApi tapEnv, Dut dut, String searchText) {
	String searchCommand = GREP_COMMAND + searchText + AutomaticsConstants.SINGLE_SPACE_CHARACTER
		+ getRFCPath(dut, tapEnv) + FILE_RFC_VARIABLE_INI;
	boolean result = searchLogFiles(tapEnv, dut, searchCommand);
	LOGGER.info("THE SEARCH TEXT " + searchText + " IS PRESENT IN RFC VARIABLE INI: " + result);
	return result;
    }

    /**
     * Utility method to search the String in the log files.
     * 
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @param dut
     *            {@link Dut}
     * @param searchCommand
     *            Command to be executed for search.
     * 
     * @return Boolean representing the result of search operation.
     */
    public static boolean searchLogFiles(AutomaticsTapApi tapEnv, Dut dut, String searchCommand) {
	String response = tapEnv.executeCommandUsingSsh(dut, searchCommand);
	LOGGER.info("LOG SEARCH RESPONSE = " + response);
	boolean result = CommonMethods.isNotNull(response) && !response.contains(NO_SUCH_FILE_OR_DIRECTORY)
		&& !response.contains(NO_ROUTE_TO_HOST)
		&& !response.contains(AutomaticsConstants.REV_SSH_FAILURE_CONNECTION_REFUSED);
	LOGGER.info("LOG SEARCH RESULT = " + result);
	return result;
    }

    /**
     * Method that checks for the reboot reasons in reboot info log and previous reboot info log
     * 
     * @param tapEnv
     * @param dut
     * @param rebootReason
     * @return
     */
    public static ResponseObject validateRebootReason(AutomaticsTapApi tapEnv, Dut dut, String rebootReasonProperty) {
	ResponseObject response = null;
	String validationStatus = null;
	String rebootReasonString = "RebootReason";
	String pattern = "PreviousRebootReason\\s*:\\s*(.*)";
	String validRebootReasons = AutomaticsPropertyUtility.getProperty(rebootReasonProperty);
	String currentRebootReason = tapEnv.executeCommandUsingSsh(dut, CMD_GET_REBOOT_REASON_FROM_REBOOT_INFO_LOG);
	if (isNull(currentRebootReason) && currentRebootReason.contains(NO_SUCH_FILE_OR_DIRECTORY)) {
	    currentRebootReason = tapEnv.executeCommandUsingSsh(dut,
		    CMD_GET_REBOOT_REASON_FROM_PREVIOUS_REBOOT_INFO_LOG);
	}
	// Ignore scenario if reboot reason is empty
	// if (isNotNull(currentRebootReason)) {
	// Fetch the reboot reason from log
	String rebootReason = patternFinder(currentRebootReason, pattern);
	LOGGER.info("Obtained reboot reason as : " + rebootReason);
	if (isNull(rebootReason)) {
	    LOGGER.info("Found empty reboot reason. Ignoring this");
	    validationStatus = AutomaticsConstants.OK;
	    response = new ResponseObject(true, validationStatus);
	    return response;
	} else {
	    // iterate the various reboot reasons and verify required reboot
	    // reason is present or not
	    if (isNotNull(validRebootReasons) && validRebootReasons.contains(AutomaticsConstants.COMMA)) {
		String[] reasonArray = validRebootReasons.split(AutomaticsConstants.COMMA);
		for (String reason : reasonArray) {
		    if (currentRebootReason.toLowerCase().trim().contains(reason.toLowerCase().trim())) {
			LOGGER.info("Found valid reboot reason");
			validationStatus = AutomaticsConstants.OK;
			response = new ResponseObject(true, validationStatus);
			return response;
		    }
		}
	    }
	}
	if (isNull(validationStatus)) {
	    // Check if the obtained reboot reason, does not contain any
	    // specific errors. We can ignore such conditions
	    // also eg : "PreviousRebootReason: 19.00 RebootReason"
	    if (rebootReason.contains(rebootReasonString)) {
		// split using string reboot reason
		String[] actualReason = rebootReason.split(rebootReasonString);
		// in case of enpty contents we will get response as
		// "19.00 RebootReason", if we split with RebootReason
		// o/p will be [19.00]
		if (null != actualReason && actualReason.length <= 1) {
		    // we can ignore considering this as an empty scenario
		    LOGGER.info("Found empty reboot reason. Ignoring this");
		    validationStatus = AutomaticsConstants.OK;
		    response = new ResponseObject(true, validationStatus);
		    return response;
		}
	    }
	    validationStatus = "Obtained reboot reason as : " + currentRebootReason
		    + " but we expected one of the reboot reasons listed below : \n" + validRebootReasons;
	    // +
	    // ". \n If reboot reason is empty,it can be a valid defect also. Please verify DELIA-25967";
	    LOGGER.error(validationStatus);
	    response = new ResponseObject(false, validationStatus);
	}
	return response;
    }

    /**
     * Method used exclusively for setting GRAM configuration. Will power off gateway
     * 
     * @param tapEnv
     * @param dut
     * @throws ServiceInstantiationException
     * @throws ProviderCreationException
     * @throws PowerProviderException
     */
    public static void powerOffGateway(AutomaticsTapApi tapEnv, Dut dut) throws PowerProviderException {

	LOGGER.info("Trying to poweroff the gateway ");

	RackInitializer rackInitializerInstance = AutomaticsTapApi.getRackInitializerInstance();
	RackDeviceValidationManager manager = new RackDeviceValidationManager(rackInitializerInstance);

	Device device = (Device) dut;

	DeviceCategory deviceCategory = TestUtils.getDeviceCategory(device);
	manager.performProviderWiring(deviceCategory, device);

	Dut gateWaySettop = (device).getGateWaySettop();
	tapEnv.powerOff(gateWaySettop);
	tapEnv.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);

	boolean isGwAccessible = isSTBAccessible(gateWaySettop);
	if (isGwAccessible) {
	    throw new FailedTransitionException(GeneralError.PRE_CONDITION_FAILURE,
		    "Quiting GRAM execution. Unable to turn off the Gateway device");
	}
	LOGGER.info("Successfully powered off the gateway.Restarting the Client DeviceConfig");
    }

    /**
     * Helper method to validate the string whether valid JSON Object or not.
     * 
     * @param givenString
     *            The given string.
     * @return True if given string is valid JSON. Otherwise its false.
     */
    public static boolean isValidJsonString(String givenString) {

	boolean isValidJson = false;

	try {
	    new JSONObject(givenString);
	    isValidJson = true;
	} catch (JSONException jsone) {
	    LOGGER.error("Invalid JSON Object String, checking whether its JSON ARRAY or not ", jsone.getMessage());
	    try {
		new JSONArray(givenString);
		isValidJson = true;
	    } catch (JSONException jse) {
		isValidJson = false;
		LOGGER.error("Invalid JSON ARRAY String, so marking as invalid string", jse.getMessage());
	    }
	}

	return isValidJson;
    }

    /**
     * get the firmware version for RDK devices
     * 
     * @param dut
     *            Dut to be tested {@link Dut}
     * 
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @return firmware version
     */

    public static String getRdkFirmwareVersionFromWebpa(Dut dut, AutomaticsTapApi tapEnv) {
	String softwareVersion = null;
	try {
	    LOGGER.info("getRdkFirmwareVersionFromWebpa");
	    softwareVersion = tapEnv.executeWebPaCommand(dut, "Device.DeviceInfo.X_RDKCENTRAL-COM_FirmwareFilename");
	} catch (Exception e) {
	    LOGGER.error("Exception while getting firmware version using snmp");
	}
	return softwareVersion;
    }

    /**
     * Execute Snmp get command on eSTB.
     * 
     * @param eCatsTap
     *            The ecats instance.
     * @param dut
     *            The dut to be used.
     * @param mibOrOid
     *            The snmp MIB name or oid
     * 
     * @return The snmp command response
     */

    public static String snmpGetOnEstb(AutomaticsTapApi eCatsTap, Dut dut, String ipAddress, String mibOrOid) {
	String response = null;
	String snmpProtocol = System.getProperty("snmpVersion", SnmpProtocol.SNMP_V2.toString());

	SnmpProtocol snmpVersion = SnmpProtocol.SNMP_V2;
	if (SnmpProtocol.SNMP_V3.getProtocolVersion().equals(snmpProtocol)) {
	    snmpVersion = SnmpProtocol.SNMP_V3;
	}

	SnmpProviderFactory providerFactory = BeanUtils.getSnmpFactoryProvider();
	SnmpProvider snmpProviderImpl = providerFactory.getSnmpProvider(snmpVersion);
	if (null != snmpProviderImpl) {
	    SnmpParams snmpParams = new SnmpParams();
	    snmpParams.setSnmpCommand(SnmpCommand.GET);
	    snmpParams.setSnmpVersion(snmpVersion);
	    snmpParams.setIpAddress(ipAddress);
	    snmpParams.setMibOid(mibOrOid);
	    response = snmpProviderImpl.doGet(dut, snmpParams);
	}
	return response;
    }

    /**
     * Execute Snmp walk command on eSTB.
     * 
     * @param dut
     *            The dut to be used.
     * @param serverDetails
     *            The server details where the commands to be executed.
     * @param mibOrOid
     *            The MIB name or oid
     * 
     * @return The snmp command response.
     */
    public static String snmpWalkOnEstb(AutomaticsTapApi eCatsTap, Dut dut, String mibOrOid) {
	String response = null;
	String snmpProtocol = System.getProperty(SnmpConstants.SYSTEM_PARAM_SNMP_VERSION,
		SnmpProtocol.SNMP_V2.toString());

	SnmpProtocol snmpVersion = SnmpProtocol.SNMP_V2;
	if (SnmpProtocol.SNMP_V3.getProtocolVersion().equals(snmpProtocol)) {
	    snmpVersion = SnmpProtocol.SNMP_V3;
	}

	SnmpProviderFactory providerFactory = BeanUtils.getSnmpFactoryProvider();
	SnmpProvider snmpProviderImpl = providerFactory.getSnmpProvider(snmpVersion);

	if (null != snmpProviderImpl) {
	    SnmpParams snmpParams = new SnmpParams();
	    snmpParams.setSnmpCommand(SnmpCommand.WALK);
	    snmpParams.setSnmpVersion(snmpVersion);
	    snmpParams.setIpAddress(((Device) dut).getEcmIpAddress());
	    snmpParams.setMibOid(mibOrOid);
	    response = snmpProviderImpl.doWalk(dut, snmpParams);
	}
	return response;
    }

    /**
     * 
     * Checks if Remote provider is available
     * 
     * 
     * @param dut
     * @return
     */
    public static boolean isRemoteProviderAvailable(Dut dut) {
	boolean isAvailable = false;
	isAvailable = null != dut.getRemote();

	return isAvailable;
    }

    /**
     * 
     * Checks if Power provider is available
     * 
     * 
     * @param dut
     * @return
     */
    public static boolean isPowerProviderAvailable(Dut dut) {
	boolean isAvailable = false;
	isAvailable = null != dut.getPower();
	return isAvailable;
    }

    /**
     * 
     * Checks if OCR provider is available
     * 
     * 
     * @param dut
     * @return
     */
    public static boolean isOcrProviderAvailable(Dut dut) {
	boolean isAvailable = false;
	isAvailable = null != dut.getOcrProvider();
	return isAvailable;
    }

    /**
     * 
     * Checks if Video provider is available
     * 
     * 
     * @param dut
     *            {@code Dut}
     * @return
     */
    public static boolean isVideoProviderAvailable(Dut dut) {
	boolean isAvailable = false;
	isAvailable = null != dut.getVideo();
	return isAvailable;
    }

    /**
     * 
     * Checks if Trace provider is available
     * 
     * 
     * @param dut
     * @return
     */
    public static boolean isSerialProviderAvailable(Dut dut) {
	boolean isAvailable = false;
	isAvailable = null != dut.getTrace();
	return isAvailable;
    }

    /**
     * 
     * Returns the device category enum type assigned to each type of device
     * 
     * @param eCatsSettop
     *            , DeviceConfig under test
     * 
     * @return DeviceCategory
     */
    public static DeviceCategory getDeviceCategory(DutImpl eCatsSettop) {
	DeviceCategory deviceType = DeviceCategory.UNKNOWN;
	if (SupportedModelHandler.isRDKB(eCatsSettop)) {
	    deviceType = DeviceCategory.RDKB;
	} else if (SupportedModelHandler.isRDKC(eCatsSettop)) {
	    deviceType = DeviceCategory.RDKC;
	} else if (SupportedModelHandler.isECB(eCatsSettop)) {
	    deviceType = DeviceCategory.ECB;
	} else if (SupportedModelHandler.isNonRDKDevice(eCatsSettop)) {
	    deviceType = DeviceCategory.NON_RDK;
	} else if (SupportedModelHandler.isRDKVClient(eCatsSettop)) {
	    deviceType = DeviceCategory.RDKV_CLIENT;
	} else {
	    deviceType = DeviceCategory.RDKV_GATEWAY;
	}
	return deviceType;
    }

    /**
     * Method to get string value from data service get response
     * 
     * @param response
     * @param nodeName
     *            is the name of the nodes returned by the Jason response after executing preference get data service
     * @return DeviceConfig Setting value
     */
    public static String getStringValueForNodeNameFromJasonResponse(Document doc, String nodeName) {
	String value = null;

	try {
	    String deviceIDTagValue = doc.getElementsByTagName(nodeName).item(0).getTextContent();

	    if (null != deviceIDTagValue) {
		value = deviceIDTagValue.substring(deviceIDTagValue.lastIndexOf(AutomaticsConstants.FORWARD_SLASH) + 1);
	    }
	} catch (Exception e) {
	    LOGGER.info("Failed to parse the node value of " + nodeName + " due to " + e);
	}
	LOGGER.info("Node value of  " + nodeName + " is " + value);
	return value;
    }

    /**
     * Method that counts the string
     * 
     * @param searchString
     * @param dut
     * @return return the count of the expected string from receive rlogs.
     */
    public static int countOfExpectedString(AutomaticsTapApi tapEnv, Dut dut, String searchString, String fileName) {
	String response = "";
	int count = 0;
	try {

	    if (CommonMethods.isNotNull(searchString)) {
		response = tapEnv.executeCommandUsingSsh(dut,
			"grep -i -c " + "\"" + searchString + "\"" + " " + fileName + "*");
		if (CommonMethods.isNotNull(response)) {
		    String[] fileList = response.split("\n");
		    // Get the count from all rolled over files
		    for (String file : fileList) {
			if (file.contains(":")) {
			    count = count + Integer.parseInt(file.split(":")[1].trim());
			} else {
			    count = Integer.parseInt(file);
			}
		    }

		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception while parsing response:  ", e);
	    count = 0;
	}
	LOGGER.info("Count of String " + searchString + " in " + fileName + " is : " + count);
	return count;
    }

    /**
     * 
     * Method to get Swupdate location for RDKV,B and C
     * 
     * @param dut
     *            Dut object
     * @return Returns location
     */
    public static String getSwUpdateFile(Dut dut) {
	String swUpdateConfFile = XconfConstants.SOFTWARE_UPDATE_CONF_FILE;
	if (SupportedModelHandler.isRDKB(dut)) {
	    swUpdateConfFile = "/nvram/swupdate.conf";
	}
	return swUpdateConfFile;
    }

    /**
     * Helper method
     * 
     * @param dut
     * @param tapApi
     * @param rfcBuildAppenderList
     * @return
     */
    public static ResponseObject postRfcBuildAppenderJsonProfilesFromAppenderList(Dut dut, AutomaticsTapApi tapApi,
	    ArrayList<String> rfcBuildAppenderList) {

	LOGGER.info("Entering postRfcBuildAppenderJsonProfilesFromAppendderList()");
	JSONObject buildAppenderProfiles = new JSONObject();
	JSONArray featureProfileArray = new JSONArray();
	JSONArray finalFeatureArrayList = new JSONArray();
	String featureProfileList = "";
	boolean status = false;
	String errorMessage = "Failed to set the RFC profiles from build appender list.";
	ResponseObject response = new ResponseObject(status, errorMessage);
	JSONObject rfcFeatureEnableData = null;
	try {
	    for (String rfcBuildAppender : rfcBuildAppenderList) {
		rfcBuildAppender = rfcBuildAppender.split("#")[1];
		String featureRfcProfile = null;
		if (!rfcBuildAppender.toLowerCase().contains("disable")) {
		    featureRfcProfile = AutomaticsTapApi
			    .getSTBPropsValue("rfc.settings." + rfcBuildAppender + ".enable");
		} else {
		    featureRfcProfile = AutomaticsTapApi
			    .getSTBPropsValue("rfc.settings." + rfcBuildAppender + ".disable");
		}

		featureRfcProfile = featureRfcProfile.split("-#")[1];
		rfcFeatureEnableData = new JSONObject(featureRfcProfile);
		featureProfileArray = new JSONArray(rfcFeatureEnableData.getString("features"));

		for (int i = 0; i < featureProfileArray.length(); i++) {
		    // rfcFeatureEnableData =
		    // featureProfileArray.getJSONObject(i);
		    // featureProfileList += rfcFeatureEnableData.toString();
		    finalFeatureArrayList.put(featureProfileArray.getJSONObject(i));
		}
	    }

	    buildAppenderProfiles.put("estbMacAddress", rfcFeatureEnableData.getString("estbMacAddress"));
	    buildAppenderProfiles.put("features", finalFeatureArrayList);

	    LOGGER.info("The build appender profiles are " + buildAppenderProfiles.toString());
	    if (SupportedModelHandler.isRDKB(dut)) {
		status = CommonMethods.postRfcSettingsToXconf(tapApi, dut, buildAppenderProfiles.toString(),
			AutomaticsTapApi.getSTBPropsValue("proxy.xconf.rfc.update.url"));
	    } else {
		status = CommonMethods.postRfcSettingsToXconf(tapApi, dut, buildAppenderProfiles.toString(),
			AutomaticsTapApi.getSTBPropsValue("proxy.xconf.rfc.update.immutable.url"));
	    }
	    if (!status) {
		errorMessage += "Failed to get a proper response after setting the RFC params from mock RFC server";
		LOGGER.error(errorMessage);
	    }
	} catch (Exception exception) {
	    errorMessage += exception.getMessage();
	    LOGGER.error(errorMessage);
	}

	response.setStatus(status);
	response.setErrorMessage(errorMessage);

	LOGGER.info("Exiting postRfcBuildAppenderJsonProfilesFromAppendderList()");
	return response;
    }

    /**
     * 
     * Removes newline and carriage return from a string
     * 
     * @param stringValue
     * @return
     */
    public static String stripNewLine(String stringValue) {
	String strippedContent = AutomaticsConstants.EMPTY_STRING;
	if (isNotNull(stringValue)) {
	    strippedContent = stringValue
		    .replaceAll(System.getProperty("line.separator"), AutomaticsConstants.EMPTY_STRING)
		    .replaceAll("\\r", AutomaticsConstants.EMPTY_STRING);
	}
	return strippedContent;
    }

    /**
     * Method to check if the given value is null or not
     * 
     * @param value
     * @return
     */
    public static boolean isNotNull(String value) {

	boolean isNotNull = !isNull(value);

	return isNotNull;
    }

    /**
     * Method to check if the given value is null or not
     * 
     * @param value
     * @return
     */
    public static boolean isNull(String value) {

	return ((value == null) || (value.trim().length() == 0));
    }

    /**
     * Method to disable SSL
     */
    public static void disableSSL() {

	LOGGER.debug("Entering disableSSL()");

	SSLContext secureContext = null;

	// creating custom class implementing the trust manager locally required only for this method
	class CertificateTrustManager implements X509TrustManager {

	    @Override
	    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
	    }

	    @Override
	    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
	    }

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
		return null;
	    }
	}
	// Create a certificate trust manager that does not validate certificate chains
	TrustManager[] trustCerts = new TrustManager[] { new CertificateTrustManager() };

	// Installing all-trusting the trust manager
	try {
	    secureContext = SSLContext.getInstance("SSL");
	    secureContext.init(null, trustCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(secureContext.getSocketFactory());
	} catch (Exception exception) {
	    LOGGER.error("Exception while disabling SSL for RACK" + exception.getMessage());
	    LOGGER.debug("Exception while disabling SSL for RACK", exception);
	}

	LOGGER.debug("Exiting disableSSL()");
    }

    /**
     * marshal the given object to an xml String.
     * 
     * @param obj
     *            - the object to be converted to xml string
     * @param format
     *            - if true, the xml string will be formatted
     * @return xmlDataAsString
     */
    public static String marshalXMLObject(Object obj, boolean format) {

	LOGGER.trace("Entering marshalXMLObject()");

	String xmlDataAsString = null;

	try {
	    JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    StringWriter sw = new StringWriter();
	    // output pretty printed
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);

	    jaxbMarshaller.marshal(obj, sw);

	    xmlDataAsString = sw.getBuffer().toString();

	} catch (JAXBException exception) {
	    LOGGER.trace("marshalXMLObject() - Exception while marshalling XML object.", exception);
	}

	LOGGER.info("Marshelled request XML - \n" + xmlDataAsString);

	LOGGER.trace("Exiting marshalXMLObject()");

	return xmlDataAsString;
    }

    /**
     * get all the child files with no / specific extension. if extension = null, then all files in that folder will be
     * returned. Else those files with that specified extension will be returned.
     * 
     * @param folder
     *            - the folder whose child files need to be obtained.
     * @param extension
     *            - the file extension without dot operator
     * @return {@link File} [] - list of child folders.
     */
    public static File[] getChildFilesWithSpecifiedExtension(File folder, final String extension) {

	File[] fileList = folder.listFiles(new FilenameFilter() {

	    @Override
	    public boolean accept(File directory, String fileName) {

		if (isNotNull(extension)) {
		    return fileName.endsWith(extension);
		} else {
		    return true;
		}
	    }
	});

	if (fileList == null) {
	    fileList = new File[] {};
	}

	return fileList;
    }

    /**
     * Method to check whether given IP address is ipv6 address.
     * 
     * @param ipAddress
     *            The given ip address.
     * @return true if the given address is ipv6
     * @see CommonMethods#isIpv6Address(Dut)
     */
    public static boolean isIpv6Address(String ipAddress) {

	boolean isIPv6 = false;

	if (StringUtils.isNotBlank(ipAddress)) {
	    isIPv6 = InetAddressUtils.isIPv6Address(ipAddress);
	}

	return isIPv6;
    }

    /**
     * Method to check whether given IP address is ipv4 address.
     * 
     * @param ipAddress
     *            The given ip address.
     * @return true if the given address is ipv4
     * @see CommonMethods#isIpv6Address(Dut) for ipv6 check
     */
    public static boolean isIpv4Address(String ipAddress) {

	boolean isIPv4 = false;

	if (StringUtils.isNotBlank(ipAddress)) {
	    isIPv4 = InetAddressUtils.isIPv4Address(ipAddress);
	}

	return isIPv4;
    }

    /**
     * Validate whether the ip6 address specified for the dut is a IP6 address. It will not consider dut objects ip4
     * value for any validation.
     * 
     * @param dut
     *            device whose IP need to be validated.
     * @return true if its a ipv6 address, false otherwise.
     * @see CommonMethods#isIpv6Address(String)
     */
    public static boolean isIpv6Address(Dut dut) {

	String ipAddress = null;
	if (SupportedModelHandler.isRDKVClient(dut)) {
	    ipAddress = ((Device) dut).getClientIpAddress();
	} else {
	    ipAddress = dut.getHostIp6Address();
	}
	return isIpv6Address(ipAddress);
    }

    /**
     * Checks if the NUC devices are accessible or not
     * 
     * @param dut
     * @return
     */
    public static boolean isNattedClientsAccessible(Dut dut) {
	boolean isSTBAccessible = true;
	Device device = (Device) dut;
	if (!device.getModel().equalsIgnoreCase(AutomaticsConstants.DEVICE_MODEL_ECB)) {

	    LOGGER.info("Going to verify whether the stb with mac (" + dut.getHostMacAddress()
		    + ") is accessible using the IP address - " + ((Device) dut).getNatAddress() + ":"
		    + ((Device) dut).getNatPort());
	    String commandResponse = null;

	    if (deviceConnectionProvider == null) {
		deviceConnectionProvider = BeanUtils.getDeviceConnetionProvider();
	    }

	    if (null != deviceConnectionProvider) {

		commandResponse = deviceConnectionProvider.execute(device, LinuxCommandConstants.ECHO_TEST_CONNECTION);

		if (CommonMethods.isNull(commandResponse)
			|| commandResponse.indexOf(LinuxCommandConstants.RESPONSE_TEST_CONNECTION) == -1) {
		    LOGGER.error("\n*************************************\n UNABLE TO ACCESS THE STB ("
			    + dut.getHostMacAddress() + ") USING THE IP " + ((Device) dut).getNatAddress() + ":"
			    + ((Device) dut).getNatPort() + "\n*************************************");

		    isSTBAccessible = false;
		}
	    } else {
		LOGGER.info("DeviceConnectionProvider is null.");
	    }
	} else {
	    isSTBAccessible = true;
	    LOGGER.info("ECB device. Skipping accessible verification. " + dut.getHostMacAddress());
	}
	LOGGER.info("Exiting method isNattedClientsAccessible. Status - " + isSTBAccessible);
	return isSTBAccessible;
    }

    /**
     * Method to search the regular expression in a given string
     * 
     * @param inputString
     *            Input string for verification
     * @param regex
     *            Regular expression to validate
     * 
     * @return true if the regex is present in the response else false
     */
    public static boolean validateTextUsingRegularExpression(String inputString, String regex) {

	boolean status = false;

	// /LOGGER.debug("REG-EX : " + regex);

	try {
	    Pattern pattern = Pattern.compile(regex);

	    Matcher matcher = pattern.matcher(inputString);

	    if (matcher.find()) {
		status = true;
		LOGGER.debug("Found match : " + matcher.group());
	    }
	} catch (Exception exception) {
	    LOGGER.error(exception.getMessage());
	    status = false;
	}

	LOGGER.debug("Regex Validation status: " + status);

	return status;
    }

    //
    /**
     * Method to check if the IP is pointing to the box with the same mac
     * 
     * @param dut
     * @return isActualIpObtainedForBox
     */
    public static boolean isActualIpObtainedForBox(Dut dut) {

	boolean isActualIpObtainedForBox = false;

	String responseValue = null;

	if (!SupportedModelHandler.isRDKVClient(dut)) {

	    try {

		String command = CMD_GET_ESTB_MAC_ADDRESS;
		if (SupportedModelHandler.isRDKB(dut)) {
		    command = CMD_GET_EROUTER_MAC_ADDRESS;
		}

		// get jump server connection
		// responseValue = SshConnectionHandler.get().execute(dut,
		// command);

		if (CommonMethods.isNotNull(responseValue)
			&& responseValue.trim().equalsIgnoreCase(dut.getHostMacAddress())) {

		    isActualIpObtainedForBox = true;
		} else if (CommonMethods.isNotNull(responseValue)
			&& StringUtils.countMatches(responseValue.trim(), ":") == 5) {
		    LOGGER.error("\n\nIP (" + ((Device) dut).getHostIpAddress()
			    + ") doesn't seems to point to this box whose eSTB mac is - " + dut.getHostMacAddress()
			    + "\n\n");
		} else {
		    /*
		     * Marking it as true, because the control comes here if the command output is nowhere equal to an
		     * expected value. Like 'no file found', 'unable to access'..etc
		     * 
		     * In such scenarios, we dont want the execution to be blocked.
		     */
		    isActualIpObtainedForBox = true;
		}
	    } catch (Exception exception) {
		LOGGER.debug("isActualIpObtainedForBox() - Exception ", exception);
		LOGGER.error("isActualIpObtainedForBox() - Exception " + exception.getMessage());
	    }
	} else {
	    isActualIpObtainedForBox = true;
	}

	LOGGER.info("isActualIpObtainedForBox - " + isActualIpObtainedForBox);
	return isActualIpObtainedForBox;
    }

    /**
     * Method to identify whether its Connected clients for connectivity check.
     * 
     * @param dut
     *            The DUT which connected in connected client setup.
     * @return True if the given device is having specified connected client os type.
     */
    public static boolean isConnectivityCheckRequiredForNonRdkDevices(com.automatics.device.Dut device) {

	Device dut = (Device) device;

	boolean isConnectedClient = false;
	List<String> ostypes = AutomaticsPropertyUtility.getPropsWithGivenPrefix("connected.client.ostype.");
	StringBuilder osNames = new StringBuilder();
	for (String os : ostypes) {
	    osNames.append(AutomaticsPropertyUtility.getProperty(os) + ",");
	}
	LOGGER.info(">>>>>>>>>>>>>>>>>>>>>OS types configured = " + osNames);
	LOGGER.info(">>>>>>>>>>>>>>>>>>>>>DeviceConfig OS = " + dut.getOsType());
	if (dut != null && dut.getOsType() != null && osNames.toString().contains(dut.getOsType())) {
	    LOGGER.info(" DeviceConfig [" + dut.getHostMacAddress() + "] requires connectivity check.");
	    isConnectedClient = true;
	}
	return isConnectedClient;
    }

    /**
     * Method to obtain the IP address of the host machine.
     * 
     * @return ipAddress
     */
    public static String getLocalHostIpAddress() {

	String ipAddress = null;

	/** Windows operating system */
	String WINDOWS_OPERATING_SYSTEM = "WINDOWS";

	String operatingSystem = System.getProperty("os.name").toLowerCase();

	if (operatingSystem.contains(WINDOWS_OPERATING_SYSTEM.toLowerCase())) {

	    try {
		ipAddress = InetAddress.getLocalHost().getHostAddress();
	    } catch (UnknownHostException e) {
		LOGGER.info("Error while getting IP address in Windows.", e);
	    }

	} else {
	    // If not windows, the Local host address gathering mechanism is
	    // different.
	    try {
		NetworkInterface ni = NetworkInterface.getByName("eth0");

		if (ni == null) {
		    ni = NetworkInterface.getByName("em1");

		    if (ni == null) {
			ni = NetworkInterface.getByName("eth1");
		    } else {
			LOGGER.debug("Obtained the IP address from EM1");
		    }
		} else {
		    LOGGER.debug("Obtained the IP address from ETHO");
		}

		if (ni == null) {
		    LOGGER.info("Unable to obtain IP address from ETH0, EM1, ETH1");
		} else {

		    Enumeration<InetAddress> ias = ni.getInetAddresses();
		    InetAddress iaddress;

		    do {
			iaddress = ias.nextElement();
		    } while (!(iaddress instanceof Inet4Address));

		    ipAddress = iaddress.getHostAddress();
		}
	    } catch (SocketException e) {
		LOGGER.info("Error while getting IP address in LINUX / MAC.", e);
	    }
	}

	return ipAddress;
    }

    /**
     * Method to get formatted mac address with colon (:) separated.
     * 
     * @param inputMac
     * @return Formatted Mac address
     */
    public static String getFormattedMacAddressWithColon(String inputMac) {
	char ch;
	String formattedAdress = "";

	try {
	    boolean isFormatted = patternMatcher(inputMac, AutomaticsConstants.REG_EX_MAC_ADDRESS_FORMAT);
	    if (!isFormatted) {
		// To remove partial formated String (:)
		inputMac = inputMac.replaceAll(":", "");
		inputMac = inputMac.trim();
		for (int i = 0; i < inputMac.length(); i++) {
		    ch = inputMac.charAt(i);

		    if (i % 2 == 0 && i != 0) {
			formattedAdress += ':';
		    }
		    formattedAdress += ch;
		}
	    } else {
		LOGGER.info("Mac adress is already formatted");
		formattedAdress = inputMac;
	    }
	} catch (Exception e) {
	    LOGGER.info("Exception on formatting mac address. Taking actual mac adress");
	    formattedAdress = inputMac;
	}

	LOGGER.info("Input address : " + inputMac);
	LOGGER.info("Formatted address : " + formattedAdress);

	return formattedAdress;
    }

    /**
     * helper method to obtain the string for the specific pattern
     * 
     * @param response
     * @param patternToMatch
     * @return string found
     */
    public static boolean patternMatcher(String response, String patternToMatch) {

	// status of pattern matching
	boolean matchedStatus = false;
	// instance of pattern for match
	Pattern pattern = null;
	// Instance of matcher
	Matcher matcher = null;

	try {

	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(patternToMatch)) {

		pattern = Pattern.compile(patternToMatch);
		matcher = pattern.matcher(response);

		if (matcher.find()) {
		    matchedStatus = true;
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in patternMatcher()", exception);
	}

	// LOGGER.debug("pattern matched status is -" + matchedStatus);

	return matchedStatus;
    }

    /**
     * helper method to obtain the string for the specific pattern
     * 
     * @param response
     * @param patternToMatch
     * @return string found
     */
    public static String patternFinder(String response, String patternToMatch) {

	// LOGGER.debug("STARTING METHOD: patternFinder()");

	// matched string
	String matchedString = "";
	// instance of pattern for match
	Pattern pattern = null;
	// Instance of matcher
	Matcher matcher = null;

	try {

	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(patternToMatch)) {
		pattern = Pattern.compile(patternToMatch);
		matcher = pattern.matcher(response);
		if (matcher.find()) {
		    matchedString = matcher.group(PATTERN_MATCHER_GROUP_ONE);
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in patternFinder()", exception);
	}

	// LOGGER.info("pattern matched string is -" + matchedString);

	// LOGGER.debug("ENDING METHOD: patternFinder()");

	return matchedString;
    }

    /**
     * helper method to obtain the string for the specific pattern
     * 
     * @param response
     * @param patternToMatch
     * @return if String fouund , matched string is returned.Else input string is returned
     * 
     */
    public static String patternFinder(String response, String patternToMatch, int group) {
	String matchedString = "";
	// instance of pattern for match
	Pattern pattern = null;
	// Instance of matcher
	Matcher matcher = null;
	try {
	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(patternToMatch)) {
		pattern = Pattern.compile(patternToMatch, Pattern.DOTALL);
		matcher = pattern.matcher(response);
		if (matcher.find()) {
		    matchedString = matcher.group(group);
		} else {
		    matchedString = response;
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in patternFinder()", exception);
	}
	return matchedString;
    }

    /**
     * 
     * 
     * helper method to return all the matched string
     * 
     * @param response
     *            string content to search pattern
     * @param patternToMatch
     *            pattern to match
     * @return retrieved list of string else null
     */
    public static ArrayList<String> patternFinderToReturnAllMatchedString(String response, String patternToMatch) {

	LOGGER.info("STARTING METHOD: patternFinderToReturnAllMatchedString()");

	ArrayList<String> matchedString = null;

	try {
	    Pattern pattern = Pattern.compile(patternToMatch);
	    Matcher matcher = pattern.matcher(response);

	    matchedString = new ArrayList<String>();

	    while (matcher.find()) {

		int groupCount = matcher.groupCount();

		for (int i = 1; i <= groupCount; i++) {
		    matchedString.add(matcher.group(i));
		}
	    }

	    LOGGER.debug("pattern matched string are -" + matchedString);

	} catch (Exception exception) {
	    LOGGER.info("Exception occured in patternFinderToReturnAllMatchedString() " + exception.getMessage());
	    exception.printStackTrace();
	}

	LOGGER.info("ENDING METHOD: patternFinderToReturnAllMatchedString()");

	return matchedString;
    }

    /**
     * Method to executes linux commands in serial console and read response.
     *
     * used to execute a ASCII command
     *
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param command
     *            Linux commands.
     *
     * @param delay
     *            Optional parameter used to wait after command execution till response.
     *
     * @return the output of linux commands executed.
     */
    public static String executeCommandInSerialConsole(Dut dut, String command, long... delay) {

	String response = executeCommandInSerialConsole(dut, command, false, delay);
	return response;

    }

    /**
     * Method to executes linux commands in serial console and read response.
     * 
     * @param dut
     *            Set-top to which the telnet to be connected.
     * @param command
     *            Linux commands.
     * @param isHexCommand
     *            true if command is HEX
     * 
     * @param delay
     *            Optional parameter used to wait after command execution till response.
     * 
     * @return the output of linux commands executed.
     */
    public static String executeCommandInSerialConsole(Dut dut, String command, boolean isHexCommand, long... delay) {

	LOGGER.info("Entering into executeCommandInSerialConsole()");

	String response = null;
	SerialTraceProvider traceProviderImpl = null;

	LOGGER.info("Sending command to serial console - " + command);

	try {
	    Device device = (Device) dut;
	    boolean isHexString = false;
	    traceProviderImpl = (SerialTraceProvider) device.getSerialTrace();
	    if (null != traceProviderImpl) {

		traceProviderImpl.startTrace();
		traceProviderImpl.startBuffering();

		AutomaticsUtils.sleep(AutomaticsConstants.FIFTEEN_SECONDS);

		if (traceProviderImpl.getTraceStatus().equals(TraceServerConnectionStatus.CONNECTED.name())) {
		    traceProviderImpl.sendTraceString("root", isHexString);
		    traceProviderImpl.getBufferData();
		    traceProviderImpl.sendTraceString(formatSerialConsoleCommand(command), isHexCommand);

		    // If delay is given, then wait for that delay before getting
		    // the bufferData
		    long waitTime = delay.length > 0 ? delay[0] : AutomaticsConstants.FIVE_SECONDS;
		    AutomaticsUtils.sleep(waitTime);
		    response = traceProviderImpl.getBufferData();
		    response = formatSerialConsoleResponse(command, response);
		} else {
		    LOGGER.error("Unable to connect to the serial console of the STB - " + dut.getHostMacAddress());
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in executeCommandInSerialConsole()");
	    exception.printStackTrace();
	} finally {
	    try {
		if (traceProviderImpl != null) {
		    traceProviderImpl.stopTrace();
		}
	    } catch (Exception e) {
		LOGGER.error("Exception occured while closing the serial console in executeCommandInSerialConsole()");
		e.printStackTrace();
	    }
	}
	LOGGER.info("Serial console response is - " + response);

	LOGGER.info("Exiting from executeCommandInSerialConsole()");

	return response;
    }

    /**
     * Method to format the serial console command
     * 
     * @param command
     *            command to be formatted during serial console execution
     * @return formatted serial console command
     */
    private static String formatSerialConsoleCommand(String command) {
	return command;
    }

    /**
     * Format serial console response
     * 
     * @param command
     *            command which is executed
     * @param response
     *            serial console response for the command
     * @return formatted response
     */
    private static String formatSerialConsoleResponse(String command, String response) {

	LOGGER.debug("Entering into formatSerialConsoleResponse()");

	// unwanted response coming during serial console command execution
	String unwantedResponse = "-sh: root: command not found";

	try {
	    if (CommonMethods.isNotNull(response) && CommonMethods.isNotNull(command)) {

		// remove the unwanted response if exists
		if (response.contains(unwantedResponse)) {
		    response = response.substring(response.lastIndexOf(unwantedResponse), response.length());
		    response = response.replace(unwantedResponse, "").trim();
		}

		// bug fix
		// replace extra \r\n from the command response during long
		// linux command execution
		if (!response.contains(command)) {
		    response = response.replaceFirst("\r\n\\s*", "").trim();
		}

		if (response.contains(command)) {
		    response = response.substring(response.lastIndexOf(command), response.length());
		    response = response.replace(command, "").trim();
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in formatSerialConsoleResponse()", exception);
	}

	LOGGER.debug("Exiting from formatSerialConsoleResponse()");
	LOGGER.info("Response after formatiing : " + response);
	return response;
    }

    /**
     * Method to format the image name according execution mode and protocol(IPV4 & IPV6) for reporting purpose
     * 
     * @param isIpv6
     *            true if STB is in IPV6 Mode else false
     * @param firmwareVersion
     *            firmware image name for formatting
     * @param isRdkb
     *            true if it is RDKB devices, otherwise it is false.
     * @return updated image name
     */
    public static String formatFirmwareImageNameForReporting(boolean isIpv6, String firmwareVersion, boolean isRdkb) {

	LOGGER.debug("STARTING METHOD: formatFirmwareImageNameForReporting()");

	// variable to hold the execution mode, SP,IPLINEAR and GRAM
	String executionMode = null;
	String buildAppender = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_APPENDER);

	if (CommonMethods.isNotNull(firmwareVersion)) {

	    // read the execution mode from the configuration
	    executionMode = AutomaticsTapApi.getCurrentExecutionMode();

	    if (isRdkb) {

		String buildName = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_NAME);
		if (CommonMethods.isNotNull(buildName)) {
		    if (buildName.toLowerCase().contains(firmwareVersion.toLowerCase()) || isQuickTestConfiguration()) {
			firmwareVersion = extractBuildNameWithoutExtension(buildName);
		    }
		}
	    }

	    if (executionMode.equals(AutomaticsConstants.EXECUTION_MODE_ACCOUNT)) {
		firmwareVersion = firmwareVersion + AutomaticsConstants.UNDERSCORE
			+ AutomaticsConstants.EXECUTION_MODE_ACCOUNT;
	    }

	    if (isIpv6) {
		firmwareVersion = firmwareVersion + AutomaticsConstants.BUILD_NAME_IPV6_EXTENSION;
	    }
	    if (CommonMethods.isNotNull(buildAppender)) {
		firmwareVersion = firmwareVersion + AutomaticsConstants.UNDERSCORE + buildAppender;
	    }
	}

	LOGGER.debug("image name for reporting = " + firmwareVersion);

	LOGGER.debug("ENDING METHOD: formatFirmwareImageNameForReporting()");

	return firmwareVersion;
    }

    /**
     * Method to get STB details using /lib/rdk/getSTBDetails.sh
     * 
     * @param dut
     * @param tapApi
     * @param stbDetails
     * @return
     */
    public static String getSTBDetails(Dut dut, AutomaticsTapApi tapApi, StbDetails stbDetails) {
	String details = null;
	StbDetailsDO stbDetailsDO = null;

	if (!SupportedModelHandler.isRDKC((Device) dut)) {
	    stbDetailsDO = CommonMethods.getSTBDetails(dut, tapApi);

	    if (stbDetailsDO != null) {

		switch (stbDetails) {

		case CABLE_CARD_FIRMWARE_VERSION:
		    details = stbDetailsDO.getCableCardFirmwareVersion();
		    break;

		case ECM_IP:
		    details = stbDetailsDO.getEcmIpAddress();
		    break;

		case ECM_MAC:
		    details = stbDetailsDO.getEcmMacAddress();
		    break;

		case ESTB_IP:
		    details = stbDetailsDO.getIpAddress();
		    break;

		case ESTB_MAC:
		    details = stbDetailsDO.getMacAddress();
		    break;

		case ETH_MAC:
		    details = stbDetailsDO.getEthMac();
		    break;

		case MOCA_IP:
		    details = stbDetailsDO.getMocaIp();
		    break;

		case MOCA_MAC:
		    details = stbDetailsDO.getMocaMac();
		    break;

		case SOFTWARE_VERSION:
		    details = stbDetailsDO.getSoftwareVersion();
		    break;
		case IMAGE_VERSION:
		    details = stbDetailsDO.getImageVersion();
		    break;
		case WIFI_MAC:
		    details = stbDetailsDO.getWifiMac();
		    break;
		case DAC_INIT_TIME_STAMP:
		    details = stbDetailsDO.getDacInitTimestamp();
		    break;
		case BUILD_TYPE:
		    details = stbDetailsDO.getBuild_type();
		    break;
		case MODEL:
		    details = stbDetailsDO.getModel();
		    break;
		case MODEL_NUMBER:
		    details = stbDetailsDO.getModel_number();
		    break;
		case SERIAL_NUMBER:
		    details = stbDetailsDO.getSerial_number();
		    break;

		default:
		    details = null;
		    break;
		}
	    }
	} else {

	    switch (stbDetails) {
	    case ESTB_MAC:
		details = dut.getHostMacAddress();
		break;

	    case MODEL:
		details = dut.getModel();
		break;
	    }
	}
	return details;
    }

    /**
     * Method to get STB details using /lib/rdk/getSTBDetails.sh
     */
    public static StbDetailsDO getSTBDetails(Dut dut, AutomaticsTapApi tapApi) {
	/* Server reponse */
	String response = null;
	/* stb details */
	String stbDetails = null;
	/* pattern to match stb details */
	String patternToMatch = null;
	/*
	 * String to store the export commands to export the paths so as to execute commands like curl from outside the
	 * box
	 */
	String exportPaths = "export PATH=$PATH:/usr/bin:/bin:/usr/local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/sbin:/usr/sbin:.;export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/bin:/usr/local/lib:/usr/local/bin:/usr/local/lib:/usr/local/lib/gstreamer-0.10;";
	/* command to get STB details */
	String command = "sh /lib/rdk/getSTBDetails.sh;cat /tmp/.deviceDetails.cache";
	/* instance of StbDetailsDO */
	StbDetailsDO settopDetails = null;

	try {

	    response = tapApi.executeCommandUsingSsh(dut, exportPaths + command);

	    if (isNotNull(response)) {

		settopDetails = new StbDetailsDO();

		// software_version
		patternToMatch = StbDetails.SOFTWARE_VERSION.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setSoftwareVersion(stbDetails);
		}

		// imageVersion
		patternToMatch = StbDetails.IMAGE_VERSION.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setImageVersion(stbDetails);
		}
		// cable_card_firmware_version
		patternToMatch = StbDetails.CABLE_CARD_FIRMWARE_VERSION.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setCableCardFirmwareVersion(stbDetails);
		} else {
		    stbDetails = patternFinder(response, "cableCardVersion=(.*)[\n\r]");
		    if (isNotNull(stbDetails)) {
			settopDetails.setCableCardFirmwareVersion(stbDetails);
		    }
		}
		// ecm_mac
		patternToMatch = StbDetails.ECM_MAC.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setEcmMacAddress(stbDetails);
		}
		// ecm_ip
		patternToMatch = StbDetails.ECM_IP.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setEcmIpAddress(stbDetails);
		}
		// estb_mac
		patternToMatch = StbDetails.ESTB_MAC.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setMacAddress(stbDetails);
		}
		// estb_ip
		patternToMatch = StbDetails.ESTB_IP.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setIpAddress(stbDetails);
		}
		// moca_ip
		patternToMatch = StbDetails.MOCA_IP.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setMocaIp(stbDetails);
		} else if (isNull(settopDetails.getMocaIp()) && (AutomaticsPropertyUtility
			.getProperty(PROP_KEY_BOXIP_BOXMAC_MODEL, AutomaticsConstants.EMPTY_STRING)
			.contains(dut.getModel()))) {
		    patternToMatch = StbDetails.BOX_IP.getRegex();
		    stbDetails = patternFinder(response, patternToMatch);
		    if (isNotNull(stbDetails)) {
			settopDetails.setMocaIp(stbDetails);
		    }
		}
		// eth_mac
		patternToMatch = StbDetails.ETH_MAC.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setEthMac(stbDetails);
		}
		// wifi_mac
		patternToMatch = StbDetails.WIFI_MAC.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setWifiMac(stbDetails);
		}
		// moca_mac
		patternToMatch = StbDetails.MOCA_MAC.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setMocaMac(stbDetails);
		} else if (isNull(settopDetails.getMocaMac()) && (AutomaticsPropertyUtility
			.getProperty(PROP_KEY_BOXIP_BOXMAC_MODEL, AutomaticsConstants.EMPTY_STRING)
			.contains(dut.getModel()))) {
		    if (isNotNull(settopDetails.getEthMac())) {
			settopDetails.setMocaMac(settopDetails.getEthMac());
		    } else if (isNotNull(settopDetails.getWifiMac())) {
			settopDetails.setMocaMac(settopDetails.getWifiMac());
		    }
		}
		// DACInitTimestamp
		patternToMatch = StbDetails.DAC_INIT_TIME_STAMP.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setDacInitTimestamp(stbDetails);
		}
		// build_type
		patternToMatch = StbDetails.BUILD_TYPE.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setBuild_type(stbDetails);
		}
		// model
		patternToMatch = StbDetails.MODEL.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setModel(stbDetails);
		}
		// model_number
		patternToMatch = StbDetails.MODEL_NUMBER.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setModel_number(stbDetails);
		}
		// serial_number
		patternToMatch = StbDetails.SERIAL_NUMBER.getRegex();
		stbDetails = patternFinder(response, patternToMatch);
		if (isNotNull(stbDetails)) {
		    settopDetails.setSerial_number(stbDetails);
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getSTBDetails() :" + exception.getMessage(), exception);
	}

	return settopDetails;
    }

    /**
     * Method to create a file, and save it with the given contents
     * 
     * @param fileLocation
     * @param data
     */
    public static void createAndSaveFileWithContents(File fileLocation, String data) {

	FileWriter fileWriter = null;

	try {
	    fileWriter = new FileWriter(fileLocation);
	    fileWriter.append(data);
	    fileWriter.flush();

	} catch (IOException ioException) {
	    LOGGER.debug("****=> Failed to create the file with Channel Grid Web Service output."
		    + ioException.getMessage());
	} finally {
	    if (fileWriter != null) {
		try {
		    fileWriter.close();
		} catch (IOException ioException) {
		    LOGGER.debug("****=> Failed to close the file with Channel Grid Web Service output."
			    + ioException.getMessage());
		}
	    }
	}
    }

    /**
     * Retrieves the process id of the process given as parameter.
     * 
     * @param eCatsTap
     *            The {@link AutomaticsTapApi} reference.
     * @param dut
     *            The dut to be used.
     * @param processName
     *            The process name of which pid is to be found
     * 
     * @return Process Id of the process,null if not found
     */
    public static String getPidOfProcess(Dut dut, AutomaticsTapApi automaticsTapApi, String processName) {

	LOGGER.debug("STARTING METHOD: getPidOfProcess()");
	// shell command
	String shellCommand = null;
	// PID
	String pid = null;

	try {
	    if (CommonMethods.isNotNull(processName)) {
		shellCommand = "pidof \"" + processName + "\"";
		pid = automaticsTapApi.executeCommandUsingSsh(dut, shellCommand);
		if (CommonMethods.isNotNull(pid)) {
		    pid = pid.trim();
		    String[] pids = pid.split(AutomaticsConstants.SINGLE_SPACE_CHARACTER);
		    // validating whether PID is integer or not
		    for (String process : pids) {
			Integer.parseInt(process.trim());
		    }
		}
	    } else {
		LOGGER.error("Given process name is null");
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in getPidOfProcess()," + exception.getMessage());
	    pid = null;
	}

	LOGGER.info("PID of '" + processName + "' is: " + pid);

	LOGGER.debug("ENDING METHOD: getPidOfProcess()");
	return pid;
    }

    /**
     * Method used to validate mac address
     * 
     * @param mac
     * @return
     */
    public static boolean isMacValid(String mac) {
	Pattern p = Pattern.compile("^([a-fA-F0-9]{2}[:-]){5}[a-fA-F0-9]{2}$");
	Matcher m = p.matcher(mac);
	return m.find();
    }

    /**
     * Helper method to extract binary extension from build name.
     * 
     * @param availableImageVersion
     *            The given image name.
     * @return Formatted image name.
     */
    public static String extractBuildNameWithoutExtension(String availableImageVersion) {
	if ((null != availableImageVersion) && (!(availableImageVersion.isEmpty()))
		&& !availableImageVersion.contains(".ccs")) {
	    String[] imageName = availableImageVersion.split(".bin");

	    if ((imageName.length >= 1) && (!(imageName[0].isEmpty()))) {
		availableImageVersion = imageName[0];
	    }
	}

	LOGGER.debug(new StringBuilder().append("extractImageVersionNameWithoutExtension : image name = ")
		.append(availableImageVersion).toString());

	return availableImageVersion;
    }

    /**
     * Method is used to get result by executing webPA command.
     * 
     * @param dut
     *            Dut object
     * @param parameter
     *            Parameter to fetch
     * @return Returns response
     */
    public static String executeWebPA(Dut dut, String parameter) {
	StringBuilder tr69ParamResponse = new StringBuilder();
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(dut,
		new String[] { parameter });

	List<WebPaParameter> params = serverResponse.getParams();

	if (null != params && !params.isEmpty()) {
	    for (WebPaParameter webPaParameter : params) {
		tr69ParamResponse.append(webPaParameter.getValue());
	    }
	}
	return tr69ParamResponse.toString();
    }

    public static String executeWebPA(String macAddress, String parameter) {
	StringBuilder tr69ParamResponse = new StringBuilder();
	WebPaServerResponse serverResponse = WebPaConnectionHandler.get().getWebPaParamValue(macAddress,
		new String[] { parameter });

	List<WebPaParameter> params = serverResponse.getParams();

	if (null != params && !params.isEmpty()) {
	    for (WebPaParameter webPaParameter : params) {
		tr69ParamResponse.append(webPaParameter.getValue());
	    }
	}
	return tr69ParamResponse.toString();
    }

    /**
     * Utility method to check whether current test configuration 'filterTestType' is quick Test or not.
     * 
     * @return True if the configuration is Quick test, otherwise false.
     */
    public static boolean isQuickTestConfiguration() {
	boolean isQuickTest = false;

	String testType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	if (CommonMethods.isNotNull(testType)) {
	    isQuickTest = TestType.isQt(testType);
	}

	return isQuickTest;
    }

    /**
     * Method to execute command in Atom console
     *
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param command
     *            command to execute
     * @return server response
     */
    public static String executeCommandInAtomConsole(Dut dut, AutomaticsTapApi tapApi, String command)
	    throws TestException {
	return tapApi.executeCommandOnAtom(dut, command);
    }

    /**
     * Method to get HTTP client with TLS support
     * 
     * @return {@code HttpClient}
     */
    public static HttpClient getTlsEnabledHttpClient() {

	HttpClient httpClient = null;
	LOGGER.debug("STARTING Method - getTlsEnabledHttpClient()");

	try {
	    SSLContext sslContext = SSLContext.getDefault();
	    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
		    new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null, new NoopHostnameVerifier());
	    httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
	} catch (Exception e) {
	    LOGGER.error("Failed to get the http client. " + e);
	}

	LOGGER.debug("COMPLETED Method - getTlsEnabledHttpClient()");
	return httpClient;
    }

    /**
     * Method to override xconf url in dcm.properties and post RFC settings to XCONF.
     *
     * @param tapEnv
     *            The ecats dut.
     * @param dut
     *            The {@link Dut} object.
     * @param payLoadData
     *            String representing the Payload Data for enabling/ disabling the feature.
     *
     * @param xconfUrl
     *            XCONF url
     *
     * @return Boolean result of the operation.
     */
    public static boolean postRfcSettingsToXconf(AutomaticsTapApi tapEnv, Dut dut, String payLoadData,
	    String xconfUrl) {
	LOGGER.info("STARTING METHOD: postRfcSettingsToXconf");
	boolean result = false;
	String tempPathOfRfc = null;
	if (SupportedModelHandler.isRDKB(dut)) {
	    tempPathOfRfc = NVRAM_PATH + FILE_NAME_RFC_PROPERTIES;
	} else {
	    tempPathOfRfc = OPT_PATH + AutomaticsConstants.FORWARD_SLASH + FILE_NAME_RFC_PROPERTIES;
	}
	if (null != payLoadData) {
	    String xconfGetUrl = AutomaticsTapApi.getSTBPropsValue(XconfConstants.PROP_KEY_UPDATE_URL_RFC_PROPERTIES);
	    LOGGER.info("XCONF URL: " + xconfGetUrl);
	    if (CommonMethods.isNotNull(xconfGetUrl)) {
		result = copyRfcPropertiesFromEtcToOpt(dut, tapEnv, tempPathOfRfc)
			&& copyAndUpdateRfcPropertiesNewXconfUrl(dut, tapEnv, xconfGetUrl, tempPathOfRfc);
		LOGGER.info("COPY & UPDATE RFC PROPERTIES WITH XCONF URL: " + result);
	    }
	    if (result) {
		result = updatepayLoadDataAndPostToXconfDCMServer(tapEnv, dut, payLoadData, xconfUrl);
	    }
	}
	LOGGER.info("STATUS OF POSTING FEATURE IN XCONF USING RFC: " + result);
	LOGGER.debug("ENDING METHOD: postRfcSettingsToXconf");
	return result;
    }

    /**
     *
     * Method to update payload data and post using RFC settings to XCONF.
     *
     * @param tapEnv
     *            The ecats dut.
     * @param dut
     *            The {@link Dut} object.
     * @param payLoadData
     *            String representing the Payload Data for enabling/ disabling the feature.
     *
     * @param xconfUrl
     *            XCONF url
     *
     * @return Boolean result of the operation.
     *
     */
    public static boolean updatepayLoadDataAndPostToXconfDCMServer(AutomaticsTapApi tapEnv, Dut dut, String payLoadData,
	    String xconfUrl) {

	LOGGER.debug("STARTING METHOD: updatepayLoadDataAndPostToXconfDCMServer");
	JSONObject jsonObj = null;
	boolean result = false;
	LOGGER.info("PAY LOAD DATA: " + payLoadData);
	try {
	    jsonObj = new JSONObject(payLoadData);
	    result = true;
	} catch (JSONException jsonException) {
	    result = false;
	    LOGGER.error("EXCEPTION OCCURRED WHILE GENERATING THE JSON OBJECT FROM PAYLOAD DATA: "
		    + jsonException.getMessage());
	}
	if (result) {
	    String stbMacAddress = null;
	    if (SupportedModelHandler.isRDKB(dut)) {
		stbMacAddress = dut.getHostMacAddress();
	    } else {
		stbMacAddress = CommonMethods.getSTBDetails(dut, tapEnv, StbDetails.ESTB_MAC);
	    }
	    LOGGER.info("STB MAC ADDRESS: " + stbMacAddress);
	    if (null != jsonObj && CommonMethods.isNotNull(stbMacAddress)) {
		if (!SupportedModelHandler.isRDKB(dut)) {
		    payLoadData = payLoadData.replace(AutomaticsConstants.CONSTANT_REPLACE_STBMAC, stbMacAddress);
		} else {
		    payLoadData = payLoadData.replace(AutomaticsConstants.CONSTANT_REPLACE_EROUTERMAC, stbMacAddress);
		}
		LOGGER.info("PAY LOAD DATA: " + payLoadData);
		int responseCode = postDataToProxyXconfDcmServer(dut, tapEnv, payLoadData, xconfUrl);
		LOGGER.info("RESPONSE CODE: " + responseCode);
		result = responseCode == AutomaticsConstants.CONSTANT_200;
	    } else {
		result = false;
		LOGGER.info("UNABLE TO OBTAIN STB MAC ADDRESS.");
	    }
	} else {
	    LOGGER.error("Failed to fetch RFC feature from stb.properties");
	}
	LOGGER.info("STATUS OF POSTING FEATURE IN XCONF USING RFC: " + result);

	LOGGER.debug("ENDING METHOD: updatepayLoadDataAndPostToXconfDCMServer");
	return result;
    }

    /**
     * Helper method to copy the file from etc to dcm and verify whether the file has been copied
     *
     * @param dut
     *            {@link Dut}
     * @param tapApi
     *            {@link AutomaticsTapApi}
     * @return true if the file has been copied, else false
     */
    public static boolean copyRfcPropertiesFromEtcToOpt(Dut dut, AutomaticsTapApi tapApi) {

	LOGGER.debug("STARTING METHOD : copyRfcPropertiesFromEtcToOpt");
	// stores the status of whether the file is copied from etc to opt
	boolean status = false;
	// store the command response
	String response = null;

	// copying the file from etc to opt
	tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_CP_RFC_PROPERTIES);

	// executing the command ls /opt | grep dcm and verifying that the file
	// has been copied
	response = tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.LS_OPT_RFC);
	status = CommonMethods.isNotNull(response) && response.contains(AutomaticsConstants.RFC_PROPERTIES);

	if (!status) {
	    throw new TestException("Unable to copy rfc.properties from /etc/ to /opt");
	}

	LOGGER.debug("ENDING METHOD : copyRfcPropertiesFromEtcToOpt");
	return status;
    }

    /**
     * Helper method to copy the file from etc to dcm and verify whether the file has been copied
     *
     * @param dut
     *            {@link Dut}
     * @param tapApi
     *            {@link AutomaticsTapApi}
     * @return true if the file has been copied, else false
     */
    public static boolean copyRfcPropertiesFromEtcToOpt(Dut dut, AutomaticsTapApi tapApi, String tempPathOfRfc) {

	LOGGER.debug("STARTING METHOD : copyRfcPropertiesFromEtcToOpt");
	// stores the status of whether the file is copied from etc to opt
	boolean status = false;
	// store the command response
	String response = null;

	tapApi.executeCommandUsingSsh(dut,
		LinuxCommandConstants.COPY + FILE_PATH_ETC_RFC_PROPERTIES + AutomaticsConstants.SPACE + tempPathOfRfc);
	// executing the command ls /opt | grep dcm and verifying that the file
	// has been copied
	response = tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.CMD_LIST_FOLDER_FILES + tempPathOfRfc);
	status = CommonMethods.isNotNull(response) && response.contains(AutomaticsConstants.RFC_PROPERTIES);

	if (!status) {
	    throw new TestException("Unable to copy rfc.properties from /etc/ to /opt");
	}

	LOGGER.debug("ENDING METHOD : copyRfcPropertiesFromEtcToOpt");
	return status;
    }

    /**
     *
     * Helper method to copy and to update the rfc.properties with the Proxy XCONF update url
     *
     * @param dut
     *            instance of STB
     * @param tapApi
     *            instance AutomaticsTapApi
     * @return true if file is copied and updated with proxy xconf url
     */
    public static boolean copyAndUpdateRfcPropertiesNewXconfUrl(Dut dut, AutomaticsTapApi tapApi, String xconfUrl,
	    String tempPathOfRfc) {

	LOGGER.debug("STARTING METHOD: copyAndUpdateRfcPropertiesNewXconfUrl");
	// for storing command responses
	String response = null;
	// for storing the status
	boolean status = false;

	copyRfcPropertiesFromEtcToOpt(dut, tapApi, tempPathOfRfc);
	response = tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.COMMAND_CAT + tempPathOfRfc);
	String logUploadUrl = CommonMethods.patternFinder(response,
		AutomaticsConstants.PATTERN_FOR_CONFIG_DEV_SERVER_URL);
	// with RDKALL-80 coming into picture, there won't be any
	// RFC_CONFIG_DEV_SERVER_URL property key
	// in
	// rfc.properties now.
	// so updating the code to check for "RFC_CONFIG_SERVER_URL" instead.
	if (CommonMethods.isNull(logUploadUrl)) {
	    LOGGER.info("Property \"RFC_CONFIG_DEV_SERVER_URL\" is not found in rfc.properties");
	    LOGGER.info("Checking for the property key \"RFC_CONFIG_SERVER_URL\" instead");
	    logUploadUrl = CommonMethods.patternFinder(response,
		    AutomaticsConstants.UPDATED_PATTERN_FOR_RFC_SERVER_URL);
	}
	LOGGER.info("RFC Server URL to be replaced in rfc.properties:: " + logUploadUrl);

	LOGGER.info("Replacing the url using 'sed' command..........");
	// replacing using 'sed' command
	String cmdForReplaceUrl = LinuxCommandConstants.SED_COMMAND_FIRST_PART + logUploadUrl.trim()
		+ AutomaticsConstants.DELIMITER_HASH + xconfUrl
		+ AutomaticsConstants.SED_COMMAND_LAST_PART_WITH_RFCSCRIPT_LOG + tempPathOfRfc;

	tapApi.executeCommandUsingSsh(dut, cmdForReplaceUrl);

	// Checking whether LOG_UPLOAD_URL is replaced with sed
	LOGGER.info("Checking whether  RFC Server URL is replaced with proxy xconf url!!");
	response = tapApi.executeCommandUsingSsh(dut, LinuxCommandConstants.COMMAND_CAT + tempPathOfRfc);

	// to alert the execution regarding various outcomes of this test
	if (CommonMethods.isNotNull(response) && response.contains(xconfUrl)) {
	    LOGGER.info("********Succesfully updated the url********");
	    status = true;
	} else {
	    throw new TestException(
		    "Failed to update the RFC Server URL with proxy xconf url in " + tempPathOfRfc + "/rfc.properties");
	}

	LOGGER.debug("ENDING METHOD: copyAndUpdateRfcPropertiesNewXconfUrl");
	return status;

    }

    /**
     * helper method to post dcm data to proxy server for updating dcm properties
     *
     * @param dut
     *            {@link Dut}
     * @param tapApi
     *            {@link AutomaticsTapApi}
     * @return http post - server response
     */
    public static int postDataToProxyXconfDcmServer(Dut dut, AutomaticsTapApi tapApi, String rfcSettings,
	    String proxyDcmServerUpdateUrl) {

	LOGGER.debug("STARTING METHOD : postDataToProxyXconfDcmServer");

	HttpClient httpClient = null;
	HttpResponse response = null;
	int statusCode = -1;

	try {

	    LOGGER.info("Creating HTTP client with TLS enabled");
	    httpClient = CommonMethods.getTlsEnabledHttpClient();

	    LOGGER.info("sendMessage : PROXY XCONF SERVER URL = " + proxyDcmServerUpdateUrl);

	    HttpPost request = new HttpPost(proxyDcmServerUpdateUrl);
	    LOGGER.info("Request:" + request);
	    // String jsonTobeSent = rfcSettings;
	    LOGGER.info("Content to send : " + rfcSettings);
	    HttpEntity params = new StringEntity(rfcSettings);
	    LOGGER.info("Params:" + params);
	    request.addHeader("content-type", "application/json");
	    request.setEntity(params);
	    LOGGER.info("Entity Set");
	    response = httpClient.execute(request);
	    LOGGER.info("Response" + response.toString());
	    StatusLine status = response.getStatusLine();
	    statusCode = status.getStatusCode();
	    LOGGER.info("sendMessage : XCONF SERVER STATUS = " + status);
	    LOGGER.info("Status Code:" + status.getStatusCode());
	    if (HttpStatus.SC_OK != status.getStatusCode()) {
		throw new FailedTransitionException(GeneralError.FAILED_CONFIGURATION,
			"Unable to configure the firmware configuration in xconf server. HTTP Status code = " + status);
	    }

	} catch (Exception ex) {

	    LOGGER.info("Excpetion occured during testing retrying with direct put " + ex);

	    throw new FailedTransitionException(GeneralError.FAILED_CONFIGURATION,
		    "Unable to configure the firmware configuration in xconf server. ", ex);

	} finally {
	    if (null != httpClient) {
		httpClient.getConnectionManager().shutdown();
	    }
	}

	return statusCode;
    }

    /**
     * Method that restarts the given service
     *
     * @param tapEnv
     * @param dut
     * @param service
     * @return
     */
    public static boolean restartService(AutomaticsTapApi tapEnv, Dut dut, String service) {
	boolean restartServiceStatus = false;
	try {
	    if (SupportedModelHandler.isRDKC(dut)) {
		String response = tapEnv.executeCommandUsingSsh(dut,
			"/etc/init.d/[SERVICE_NAME] restart".replace("[SERVICE_NAME]", service));
		if (CommonMethods.isNotNull(response) && response.contains("Starting service " + service)) {
		    restartServiceStatus = true;
		}
	    } else {
		tapEnv.executeCommandUsingSsh(dut, "systemctl restart " + service);
		for (int retry = 0; retry <= 2; retry++) {
		    String cmdOutput = tapEnv.executeCommandUsingSsh(dut, SYSTEMCTL_STATUS + service);
		    String serviceStatus = patternFinder(cmdOutput, SERVICE_STATUS);
		    if (serviceStatus.equals(SERVICE_ACTIVE_STATUS)) {
			restartServiceStatus = true;
			break;
		    } else {
			tapEnv.waitTill(AutomaticsConstants.THIRTY_SECONDS);
		    }
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Failed to restart the service : " + service);
	}
	return restartServiceStatus;
    }

    /**
     * This utility method will take RFC feature name as input and determine whether ini file has been created with
     * enable and effective immediate flag as true
     * 
     * @param tapEnv
     *            instance of AutomaticsTapApi
     * @param dut
     *            instance of Dut
     * @param rfcFeatureName
     *            RFC feature name
     * @return true if the ini file has been created for the feature under /opt/RFC location,else false
     * @author spriya200
     */
    public static boolean isRFCIniFileCreated(AutomaticsTapApi tapEnv, Dut dut, String rfcFeatureName) {
	boolean status = false;
	// ArrayList to store the parameters that we want to search in ini file
	List<String> parameterList = new ArrayList<String>();
	// getting the RFC Path dynamically
	OPT_RFC_PATH_WITH_RFC_EXTENTION = OPT_RFC_PATH_WITH_RFC_EXTENTION.replaceAll(TEMP_RFC_PATH,
		getRFCPath(dut, tapEnv));
	String iniFilePath = concatStringUsingStringBuffer(OPT_RFC_PATH_WITH_RFC_EXTENTION, rfcFeatureName,
		FILE_EXTENSION_INI);

	boolean iniFileCreated = isFileExists(dut, tapEnv, iniFilePath);

	String rfcVariableEnable = concatStringUsingStringBuffer(LOG_EXPORT_RFC, ENABLE_FLAG_FOR_RFC,
		CHARACTER_UNDER_SCORE, rfcFeatureName, DELIMITER_EQUALS, TRUE);
	String rfcVariableEffectiveImmediate = concatStringUsingStringBuffer(LOG_EXPORT_RFC, rfcFeatureName,
		CHARACTER_UNDER_SCORE, EFFECTIVE_IMMEDIATE_FLAG_FOR_RFC, DELIMITER_EQUALS, TRUE);

	parameterList.add(rfcVariableEnable);
	parameterList.add(rfcVariableEffectiveImmediate);

	// if the ini file is created, check for effectiveImmediate and enable
	// flag
	if (iniFileCreated) {
	    String response = tapEnv.executeCommandUsingSsh(dut, CMD_LINUX_CAT + SINGLE_SPACE_CHARACTER + iniFilePath);
	    status = isGivenStringListAvailableInCommandOutput(response, parameterList);
	}

	return status;
    }

    /**
     * Method to verify if the commandOutput contains the given list of strings
     * 
     * @param commandOutput
     *            Output of the command
     * @param stringVerifications
     *            List of Strings to be verified
     * @return status true if the log is present else false
     */
    public static boolean isGivenStringListAvailableInCommandOutput(String commandOutput,
	    List<String> stringVerifications) {
	boolean isFound = false;
	try {
	    isFound = isGivenStringListAvailable(commandOutput, stringVerifications);
	} catch (Exception ex) {
	    isFound = false;
	    LOGGER.error(ex.getMessage());
	}
	return isFound;
    }

    /**
     * Method to verify if the commandOutput contains the given list of strings
     * 
     * @param commandOutput
     *            Output of the command
     * @param stringVerifications
     *            List of Strings to be verified
     * @return status true if the log is present else throw the exception with actual.
     */
    public static boolean isGivenStringListAvailable(String commandOutput, List<String> stringVerifications) {
	LOGGER.debug("STARTING METHOD: isGivenStringListAvailable()");
	// Validation status
	boolean status = false;
	if (CommonMethods.isNotNull(commandOutput)) {
	    // Loop through the list and verify if all the entries are present
	    // in the
	    // command response
	    for (String entry : stringVerifications) {
		status = false;
		if (commandOutput.contains(entry)) {
		    status = true;
		} else {
		    LOGGER.error(entry + "is not present in command output");
		    throw new TestException(entry + "is not present in command output \n" + commandOutput);
		    // break;
		}

		LOGGER.info("Presence of parameter " + entry + " in the command output : " + status);
	    }

	} else {
	    LOGGER.error("The response of command is null");
	    throw new TestException("The response of command is null");
	}
	LOGGER.debug("ENDING METHOD: isGivenStringListAvailable()");
	return status;
    }

    /**
     * Method to verify given file is exists or not
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param completeFilePath
     *            complete file path
     * @return true if file present else false
     */
    public static boolean isFileExists(Dut dut, AutomaticsTapApi tapEnv, String completeFilePath) {

	LOGGER.info("STARTING METHOD: isFileExists()");

	boolean status = false;

	String response = tapEnv.executeCommandUsingSsh(dut,
		"if [ -f " + completeFilePath + " ] ; then echo \"true\" ; else echo \"false\" ; fi");

	if (CommonMethods.isNotNull(response)) {
	    status = response.trim().equals("true");
	}

	LOGGER.info("ENDING METHOD: isFileExists()");
	return status;
    }

    /**
     * Method to build command using string buffer.
     * 
     * @param values
     *            , N number of arguments
     * 
     * @author Arunkumar (Ajayac200)
     */
    public static String concatStringUsingStringBuffer(String... values) {
	LOGGER.debug("Entering into concatStringUsingStringBuffer");
	StringBuffer command = new StringBuffer();
	for (String cmd : values) {
	    command.append(cmd);
	}
	LOGGER.debug("Exiting into concatStringUsingStringBuffer");
	return command.toString();
    }

    /**
     * Utility method to retrive the RFC Path
     * 
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @return the RFC path
     * 
     * @author gcheru200
     */

    public static String getRFCPath(Dut dut, AutomaticsTapApi tapEnv) throws TestException {
	LOGGER.info("ENTERING: getRFCPath");
	String response = null;
	String pattern = RFC_PATH_PATTERN;
	String rfcPath = null;
	response = tapEnv.executeCommandUsingSsh(dut,
		GREP_COMMAND + RFC_PATH_KEY + SINGLE_SPACE_CHARACTER + FILE_PATH_ETC_RFC_PROPERTIES);
	if (CommonMethods.isNotNull(response)) {
	    rfcPath = CommonMethods.patternFinder(response, pattern);
	    if (CommonMethods.isNotNull(rfcPath)) {
		rfcPath = rfcPath.replace(PERSISTENT_PATH, OPT_PATH);
	    } else {
		throw new TestException("Failed to get the RFC path from rfc.properties");
	    }
	} else {
	    throw new TestException("Failed to get the RFC path from rfc.properties");
	}
	LOGGER.info("EXITING: getRFCPath");
	return rfcPath.trim();
    }

    /**
     * Utility methods to verify the currently running firmware version using sysDescr SNMP MIBs.
     * 
     * @param dut
     *            The dut to be validated.
     * @param buildImageWithoutExtension
     *            The build name to be validated.
     * @return true if build name matches with sysDescr SW VER.
     */
    public static boolean verifyDeviceAccessUsingSysDescrSnmpCommand(Dut dut) {

	boolean isAccesible = false;
	AutomaticsTapApi automaticsTapApi = AutomaticsTapApi.getInstance();

	LOGGER.info("Native flip test enabled. Verifying STB access using SNMP sysDescr");
	String snmpSystemDescrOutput = snmpWalkOnEstb(automaticsTapApi, dut, ".1.3.6.1.2.1.1.1.0");

	if (CommonMethods.isNotNull(snmpSystemDescrOutput)
		&& snmpSystemDescrOutput.contains("SNMPv2-MIB::sysDescr.0")) {
	    isAccesible = true;
	}
	LOGGER.info("Native flip test enabled : isAccesible - " + isAccesible);
	return isAccesible;
    }

    /**
     * Method to wait for certain amount of time and return true
     *
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     *
     * @param waitDuration
     *            long integer specifying the time in milliseconds to wait for
     */
    public static boolean hasWaitForDuration(AutomaticsTapApi tapEnv, long waitDuration) {
	LOGGER.debug("ENTERING METHOD waitAndReturnTrue");
	LOGGER.info("Waiting for " + waitDuration / 1000 + " seconds");
	tapEnv.waitTill(waitDuration);
	LOGGER.debug("EXITING METHOD waitAndReturnTrue");
	return true;
    }

    /**
     * 
     * Method to check for number of retries for snmpv3
     * 
     * @param commandOutput
     *            Result obtained from v3 execution
     * @param snmpProtocol
     *            Protocol
     * @param retryCount
     *            Current retryValue
     * @return whether retry is required or not
     */
    public static boolean getRetries(String commandOutput, SnmpProtocol snmpProtocol, int retryCount) {
	boolean shouldRetry = false;
	int retry = 0;
	if (SnmpProtocol.SNMP_V3.equals(snmpProtocol)) {
	    try {
		retry = Integer.parseInt(System.getProperty("snmpv3.retry.count", "1"));
	    } catch (NumberFormatException e) {
		retry = 1;
	    }
	    if ((CommonMethods.isNull(commandOutput) || commandOutput.contains("Timeout")) && retry > 0
		    && retryCount < retry) {
		shouldRetry = true;
	    }
	}
	return shouldRetry;
    }

    public static long getTimeInMillisFromTimesStampString(String log, String timeStampRegex, String requiredFormat,
	    ZoneId zoneID) {
	LOGGER.info("STARTING METHOD: CommonUtils.getTimeInMillisFromYoctoJournalTrace()");
	ZonedDateTime availableDateTime = null;
	try {
	    // string is of the form "2019-04-11 05:16:19 2019/10/14-11:03:45";
	    Pattern pattern = Pattern.compile(timeStampRegex);
	    Matcher matcher = pattern.matcher(log);
	    if (matcher.find()) {
		// String requiredFormat = "yyyy/MM/dd-HH:mm:ss";
		DateTimeFormatter requiredFormat1 = DateTimeFormatter.ofPattern(requiredFormat);
		LocalDateTime ldt = LocalDateTime.parse(matcher.group(0), requiredFormat1);
		availableDateTime = ldt.atZone(zoneID);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	LOGGER.info("ENDING METHOD: CommonUtils.getTimeInMillisFromYoctoJournalTrace()");
	// Get the time in milliseconds
	return availableDateTime.toEpochSecond();
    }

    /**
     * Executes command on the jenkins server
     * 
     * @param command
     * @return Command execution Response
     */
    public static String executeCommandInExecutionServer(String command) {
	String s = null;
	StringBuffer response = new StringBuffer();
	Process p = null;
	try {
	    p = Runtime.getRuntime().exec(new String[] { "bash", "-c", command });
	    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    while ((s = stdInput.readLine()) != null) {
		response.append(s).append(AutomaticsConstants.NEW_LINE);
	    }
	} catch (IOException e) {
	    LOGGER.error("Exception during execution of command in execution server " + e.getMessage());
	}
	LOGGER.info("Executing command \n" + command);
	LOGGER.info("-----------------------------EXECUTION SERVER RESPONSE-----------------------------------------\n"
		+ response.toString());
	LOGGER.info(
		"\n-----------------------------EXECUTION SERVER RESPONSE-----------------------------------------");
	return response.toString();
    }

    //

    public static String formatTimeInMillisToUTC(DateTimeFormatter formatter, long time, ZoneId zone) {
	java.time.Instant instant = java.time.Instant.ofEpochSecond(time);
	ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zone);
	// DateTimeFormatter formatter = DateTimeFormatter.ofPattern (format);
	String output = formatter.format(zdt);
	return output;
    }

    /**
     * add extra wait time
     *
     * @param tapEnv
     * @param dut
     */
    public static void addExtraWaitTimeForCACertificateExpiryCandidate(AutomaticsTapApi tapEnv, Dut dut) {
	// Add extra wait time for CA certificate expiry
	// candidates
	boolean extraWaitTimeRequired = isDocsisExpiryCandidate(dut);
	long defaultWaitTime = 0;
	try {
	    String waitTime = AutomaticsTapApi.getSTBPropsValue(DOCSIS_EXPIRY_TIME);
	    if (isNotNull(waitTime)) {
		defaultWaitTime = Integer.parseInt(waitTime.trim());
	    }

	} catch (FailedTransitionException exception) {
	    // choose default wait time ~8minutes
	    defaultWaitTime = 8;
	}
	if (extraWaitTimeRequired) {
	    defaultWaitTime = defaultWaitTime * AutomaticsConstants.ONE_MINUTE;
	    LOGGER.info("Waiting for additional milliseconds : " + defaultWaitTime);
	    tapEnv.waitTill(defaultWaitTime);
	}

    }

    /**
     * verify whether box model is CA certificate expiry candidate
     *
     * @param tapEnv
     * @param dut
     */
    public static boolean isDocsisExpiryCandidate(Dut dut) {
	boolean isDocsisExpiryCandidate = false;
	String docsisExpiryModels = AutomaticsPropertyUtility
		.getProperty(STB_PROPS_CATEGORY_RDKV_DOCSIS_EXPIRY_CANDIDATE, AutomaticsConstants.EMPTY_STRING);
	if (isNotNull(docsisExpiryModels) && docsisExpiryModels.contains(dut.getModel())) {
	    isDocsisExpiryCandidate = true;
	}
	LOGGER.info("Comparing dut model - " + dut.getModel() + " - with docsis expiry model candidates : "
		+ docsisExpiryModels + ". Is the current model a candidate? " + isDocsisExpiryCandidate);
	return isDocsisExpiryCandidate;
    }

    public static String cleanNonPrintableCharsFromTextContent(String text) {
	if (isNotNull(text)) {
	    // strips off all non-ASCII characters
	    text = text.replaceAll("[^\\x00-\\x7F]", "");

	    // erases all the ASCII control characters
	    text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

	    // removes non-printable characters from Unicode
	    text = text.replaceAll("\\p{C}", "");

	    return text.trim();
	} else {
	    return text;
	}
    }

    /**
     * Method to find the number of occurence of a pattern in a string.
     * 
     * @param string
     *            string on which pattern is to be searched
     * @param regex
     *            regex to be searched
     * @return count No.of ocurences
     */
    public static int countMatches(String string, String regex) {
	LOGGER.info("STARTING METHOD: countMatches()");
	int count = 0;
	int position = 0;
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(string);

	while (matcher.find(position)) {
	    count++;
	    position = matcher.start() + 1;

	}
	LOGGER.info("STARTING METHOD: countMatches()");
	return count;
    }

    /**
     * Method to verify if the commandOutput contains the given string
     * 
     * @param commandOutput
     *            Output of the command
     * @param stringVerication
     *            String to be verified
     * @return status true if the log is present else false
     */

    public static boolean isGivenStringAvailableInCommandOutput(String commandOutput, String stringVerication) {

	LOGGER.debug("STARTING METHOD: isGivenStringAvailableInCommandOutput()");
	// Validation status
	boolean status = false;
	if (CommonMethods.isNotNull(commandOutput) && commandOutput.contains(stringVerication)) {
	    status = true;
	}
	LOGGER.info("Presence of parameter " + stringVerication + " in the command output : " + status);
	LOGGER.debug("ENDING METHOD: isGivenStringAvailableInCommandOutput()");
	return status;
    }

    public static String removeDifferentSignedExtensionsInRequestedBuildName(String currentLoadedFirmwareName) {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * Method to check whether the STB is rebooted. 
     * and see if we get the response.
     *
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param delay
     *            delay in between the retry
     * @param maxLoopCount
     *            maximum loop count
     * @return true if STB rebooted else false
     */
    public static boolean isSTBRebooted(AutomaticsTapApi tapApi, Dut dut, long delay, int maxLoopCount) {
	// STB accessible status
	boolean status = false;
	// validation for device reboot
	for (int index = 1; index <= maxLoopCount; index++) {
	    LOGGER.info(index + "/" + maxLoopCount + "# verify whether the device is rebooted or not");
	    // verifying whether device is accessible or not
	    if (!isSTBAccessible(dut)) {
		status = true;
		break;
	    }

	    if (delay > 0) {
		LOGGER.info("Waiting " + (delay / 1000) + " seconds to device reboot");
	    }

	    tapApi.waitTill(delay);
	}
	LOGGER.info("Exiting method isSTBRebooted. Status - " + status);
	return status;
    }

    /**
     * Method to check whether the STB is accessible.
     * 
     * @param dut
     * @return isSTBAccessible
     */
    public static boolean isSTBAccessible(Dut dut) {

	boolean isAccessible = false;
	DeviceAccessValidator deviceAccessValidator = BeanUtils.getDeviceAccessValidator();

	// verifying whether device is accessible or not
	if (null != deviceAccessValidator) {
	    isAccessible = deviceAccessValidator.isDeviceAccessible(dut);
	} else {
	    LOGGER.error("Could not verify if device is accessible since "
		    + BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR + " is not configured.");
	}
	return isAccessible;
    }

    /**
     * Method to check whether the device is accessible. This is cross verified by sending an echo test_connection
     * command and see if we get the response.
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param delay
     *            delay in between the retry
     * @param maxLoopCount
     *            maximum loop count
     * @return true if STB is accessible else false
     */
    public static boolean isSTBAccessible(AutomaticsTapApi tapApi, Dut dut, long delay, int maxLoopCount) {
	// Device accessible status
	boolean status = false;

	for (int index = 1; index <= maxLoopCount; index++) {
	    LOGGER.info(index + "/" + maxLoopCount + "# verify whether the device is accessible or not");
	    // verifying whether device is accessible or not
	    if (isSTBAccessible(dut)) {
		status = true;
		break;
	    }

	    if (delay > 0) {
		LOGGER.info("Waiting " + (delay / 1000) + " seconds to device up");
	    }

	    tapApi.waitTill(delay);
	}
	LOGGER.info("Exiting method isSTBAccessible. Status - " + status);
	return status;
    }

    /**
     * Waits for box IP acquisition. The time will be specific for box models, which we will take from stb properties.
     * To that we will add a buffer time. An initial wait time of 1/3rd of total time will be first applied. Then we'll
     * verifies IP. If not accessible a wait time for 30 seconds and recheck IP acquisition using same IP for next 1/3rd
     * of total time. If not accessible, we will check with STB Infra tool for any IP change, and verifies IP acqusition
     * for rest of time
     *
     * @param tapEnv
     * @param dut
     * @return true if STB is accessible else false;
     */
    public static boolean waitForEstbIpAcquisition(AutomaticsTapApi tapEnv, Dut dut) {
	boolean isIpAcquired = false;
	DeviceAccessValidator deviceAccessValidator = BeanUtils.getDeviceAccessValidator();

	// verifying whether device is accessible or not
	if (null != deviceAccessValidator) {
	    isIpAcquired = deviceAccessValidator.waitForIpAcquisitionAfterReboot(dut);
	} else {
	    LOGGER.error("Could not verify if device ip is acquired since "
		    + BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR + " is not configured.");
	}
	return isIpAcquired;

    }

    /**
     * This Method is to check whether device is a Wifi Capable Connected Client devices
     * 
     * @param dut
     *            Dut object
     * @return True if device is Wifi capable.Else false
     * 
     */
    public static boolean isRdkvWifiCapableClientDevice(Dut dut) {
	LOGGER.debug("STARTING METHOD:  isRdkvWifiCapableClientDevice()");
	return SupportedModelHandler.isWifiDeviceBasedOnModel(dut);
    }

    /**
     * Get the Set-top box details[Make, model and MAC].
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return Returns the Set-top box detail with make, model and MAC.
     */
    public static String getSettopDetails(Dut dut) {

	String settopDetails = ((dut.getModel()) != null) ? (dut.getModel() + '-') : "";

	return settopDetails + getSettopId(dut);
    }

    /**
     * Returns the Stb Id (MAC address with out : separated)
     * 
     * @param dut
     *            Set-top box object.
     * 
     * @return Returns the Stb Id (MAC address with out : separated) or null if stb is null
     */
    public static String getSettopId(Dut dut) {
	String macWithColunReplaced = getSettopMacAddressWithColonReplaced(dut, EMPTY_STRING);

	return (null != macWithColunReplaced) ? macWithColunReplaced.toUpperCase() : null;
    }

    /**
     * Returns the Stb Id (MAC address with : separated)
     *
     * @param dut
     *            Set-top box object.
     * @param stringToReplaceColon
     *            String that need to replace colon.
     *
     * @return Returns the Stb Id (MAC address with : separated) or null if stb is null
     */
    public static String getSettopMacAddressWithColonReplaced(Dut dut, String stringToReplaceColon) {
	return ((dut != null) ? dut.getHostMacAddress().replace(COLON_SEPERATOR, stringToReplaceColon) : null);
    }

    /**
     * Helper method to verify whether the build is yocto or not.
     * 
     * 
     * @param dut
     *            The dut instance
     * @return true if build is yocto
     * 
     */
    public static boolean isYoctoBuild(Dut dut) {
	LOGGER.info("STARTING METHOD: isYoctoBuild()");
	boolean isYocto = false;
	String yoctoString = null;
	if (AutomaticsTapApi.isSerialConsoleExecutionRequired()) {
	    yoctoString = CommonMethods.executeCommandInSerialConsole(dut, LinuxCommandConstants.CMD_IS_YOCTO_BUILD);
	} else {
	    if (deviceConnectionProvider == null) {
		deviceConnectionProvider = BeanUtils.getDeviceConnetionProvider();
	    }

	    if (null != deviceConnectionProvider) {
		yoctoString = deviceConnectionProvider.execute((Device) dut, LinuxCommandConstants.CMD_IS_YOCTO_BUILD);
	    }
	}
	if (CommonMethods.isNotNull(yoctoString)) {
	    yoctoString = yoctoString.toUpperCase();

	    if (yoctoString.contains("YES"))
		isYocto = true;
	}

	LOGGER.info("ENDING METHOD: isYoctoBuild() " + isYocto);
	return isYocto;
    }

    /**
     * execute.compare.property.s6={"commandToExecute":"cat /etc/common.properties | grep -i
     * INIT_SYSTEM","expectedValue":"INIT_SYSTEM=s6"}
     * 
     * This is a generic method to execute a command in settop and compare its result with expected value. Both command
     * and response is configurable in stb properties as given above.
     * 
     * 
     * @param stbPropertyLocater
     *            Propertu against which the command and value has to be configured in stb properties
     * @return Returns true is reponse and expetced value are matching
     */
    public static boolean isS6Enabled(Dut device, String stbPropertyLocater) {
	String propertyName = "execute.compare.property." + stbPropertyLocater;
	boolean isExpectedValueMatching = false;
	try {
	    if (CommonMethods.isNotNull(stbPropertyLocater)) {
		String value = AutomaticsPropertyUtility.getProperty(propertyName, AutomaticsConstants.EMPTY_STRING);
		LOGGER.debug("Read property value = " + value);
		if (CommonMethods.isNotNull(value)) {
		    JSONObject object = new JSONObject(value);
		    String command = object.getString("commandToExecute");
		    String expectedValue = object.getString("expectedValue");
		    // LOGGER.info("expected value ={}==", expectedValue);
		    if (CommonMethods.isNotNull(command)) {
			String response = AutomaticsTapApi.getInstance().executeCommandUsingSsh(device, command);
			// LOGGER.info("response value ={}==", response);
			if (CommonMethods.isNotNull(response) && response.trim().equals(expectedValue.trim())) {
			    isExpectedValueMatching = true;
			}
		    }
		} else {
		    LOGGER.error("Missing property !! '{}'", propertyName);
		}
	    } else {
		LOGGER.error(
			"STb property key configuration for 'execute and compare' feature is empty.Unable to proceed");
	    }
	} catch (Exception e) {
	    LOGGER.error("Error while executing command and checking its value !! Property : {} , {} ", propertyName,
		    e.getMessage());
	}
	return isExpectedValueMatching;
    }

    /**
     * Method that validates IR key and return status valid error details as required
     * 
     * @param tapEnv
     * @param dut
     * @return
     * @author malu
     */
    public static ResponseObject validateKeyWithResponse(AutomaticsTapApi tapEnv, Dut dut, String command,
	    RemoteControlType type) {
	LOGGER.info("Inside validateKey");
	boolean status = false;
	String errorMessage = "Failed to validate Key. ";
	ResponseObject response = new ResponseObject(status, errorMessage);
	try {
	    // we can check both api result and log
	    status = dut.getRemote().pressKey("EXIT", type);
	    if (!status) {
		errorMessage += "API to invoke keypress 'dut.getRemote().pressKey()' returned status false. "
			+ "So seems there is a valid key failure, please raise a defect. ";
	    } else {
		boolean searchStatus = tapEnv.searchAndWaitForTrace(dut, command,
			AutomaticsConstants.ONE_MINUTE_IN_MILLIS);
		if (!searchStatus) {
		    errorMessage += "Log '" + command + "sent to server' was not found in log. "
			    + "This can also be a valid issue, please confirm once manually and raise defect. ";
		} else {
		    // Obtained log that confirms key press was updated in log
		    status = true;
		}
	    }
	} catch (Exception e) {
	    errorMessage += "Exception occurred while calling API, pressKey(), please raise a defect. "
		    + e.getMessage();
	    LOGGER.error("Exception during Key press : ", e);
	}
	LOGGER.info("Status of keypress : " + status + " Error message if any : " + errorMessage);
	response.setStatus(status);
	response.setErrorMessage(errorMessage);
	return response;
    }

    /**
     * Method that parses output.json of client device and returns whether gateway box is connected or not to it
     * 
     * @param tapEnv
     * @param dut
     * @return
     */
    public static boolean isGatewayConnected(AutomaticsTapApi tapEnv, Dut dut) {
	boolean isGatewayConnected = false;

	try {
	    JSONArray outputJsonDetails = parseOutputJson(dut, tapEnv);

	    for (int i = 0; i < outputJsonDetails.length(); i++) {
		String isGateway = outputJsonDetails.getJSONObject(i).getString(AutomaticsConstants.IS_GATEWAY);
		if (isGateway.equals(AutomaticsConstants.YES)) {
		    LOGGER.info("Gateway details are found in client's output.json.");
		    isGatewayConnected = true;
		    break;
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception in isGatewayConnected() : " + e.getMessage());
	}
	LOGGER.info("Status of gateway connection on checking output.json of client device : " + isGatewayConnected);
	return isGatewayConnected;
    }

    /**
     * Method that parses the output.json file and returns the JSON Array Required values can be parsed out from this
     * JSON array as per requirement
     * 
     * @param dut
     * @return
     * @throws JSONException
     * @author malu.s
     * @param tapEnv
     */
    public static JSONArray parseOutputJson(Dut dut, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD: parseOutputJson()");
	String outputJson = null;
	JSONArray connectedDeviceDetails = null;
	try {
	    DeviceConfig deviceConfig = DeviceConfigUtils.getDeviceObj(dut.getModel());
	    if (deviceConfig.getOutputPath() != null) {
		outputJson = tapEnv.executeCommandUsingSsh(dut, deviceConfig.getOutputPath());
	    } else {
		outputJson = tapEnv.executeCommandUsingSsh(dut,
			LinuxCommandConstants.CAT_COMMAND_TO_GET_OPT_OUPUT_JSON);
	    }
	    if (CommonMethods.isNull(outputJson)) {
		throw new TestException("Failed to execute command cat /opt/output.json. Obtained empty response");
	    }
	    outputJson.replaceAll(AutomaticsConstants.SINGLE_SPACE_CHARACTER, AutomaticsConstants.EMPTY_STRING);
	    JSONObject upnpConnectedDeviceDetails;
	    upnpConnectedDeviceDetails = new JSONObject(outputJson);
	    connectedDeviceDetails = upnpConnectedDeviceDetails.getJSONArray(AutomaticsConstants.XMEDIA_GATEWAYS);
	} catch (JSONException e) {
	    throw new TestException("Failed to execute command. Obtained response : " + outputJson);
	}
	LOGGER.info("ENDING METHOD: parseOutputJson() , connected device details : " + connectedDeviceDetails);
	return connectedDeviceDetails;
    }

    /**
     * Method that parses output.json of client device and returns the no: of client devices connected to it
     * 
     * @param tapEnv
     * @param dut
     * @return
     */
    public static int connectedClientCount(AutomaticsTapApi tapEnv, Dut dut) {
	int connectedClientCount = 0;

	try {
	    JSONArray outputJsonDetails = parseOutputJson(dut, tapEnv);

	    for (int i = 0; i < outputJsonDetails.length(); i++) {
		String isClient = outputJsonDetails.getJSONObject(i).getString(AutomaticsConstants.IS_GATEWAY);
		if (AutomaticsConstants.NO.equals(isClient)) {
		    connectedClientCount++;
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception in connectedClientCount() : " + e.getMessage());
	}
	LOGGER.info("Count of connected clients on checking output.json of client device : " + connectedClientCount);
	return connectedClientCount;
    }

    /**
     * Helper method to clear params in Server
     * 
     * @param dut
     *            {@link Dut}
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @param allSettings
     *            If set to true will remove all feature rules including the immutable ones corresponding to the STB. If
     *            set to false it deletes all the feature rules except for the immutable ones. If featureName is set,
     *            allSettings param will be void
     * @param featureName
     *            To be used in case only a particular feature rule is to be deleted, else set it as null
     * 
     * @return http post - server response
     */
    public static int clearParamsInServer(Dut dut, AutomaticsTapApi tapEnv, boolean allSettings,
	    String... featureName) {
	return clearParamsInServer(dut, tapEnv, allSettings, featureName,
		Integer.parseInt(AutomaticsConstants.REBOOT_COUNT));
    }

    /**
     * Helper method to clear params in Server
     * 
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @param dut
     *            {@link Dut}
     * @param featureName
     *            Feature names that needs to be deleted
     * @param rebootCount
     *            Number of reboots required after deleting the feature rule in Xconf server
     * @return http post - server response
     */
    public static int clearParamsInServer(Dut dut, AutomaticsTapApi tapEnv, boolean allSettings, String[] featureName,
	    int rebootCount) {
	LOGGER.debug("STARTING METHOD : clearMultipleFeaturesInProxyXconfDcmServer");
	// Pay load data
	String payLoadData = null;
	// reboot value
	int reboot = 0;
	// Default status code
	int statusCode = -1;
	try {
	    if (allSettings && featureName == null) {
		// URL to remove all feature rules including the immutable one
		// corresponding to the STB
		payLoadData = AutomaticsPropertyUtility
			.getProperty(AutomaticsConstants.PROP_KEY_RFC_DELETE_ALL_SETTINGS_URL);
		statusCode = CommonMethods.postPayLoadData(tapEnv, dut, payLoadData);
	    } else if (!allSettings && featureName == null) {
		// URL to remove all feature rules except the immutable one
		// corresponding to the STB
		payLoadData = AutomaticsPropertyUtility
			.getProperty(AutomaticsConstants.PROP_KEY_RFC_DELETE_UPDATE_SETTINGS_URL);
		statusCode = CommonMethods.postPayLoadData(tapEnv, dut, payLoadData);
	    } else if (featureName != null) {
		for (String feature : featureName) {
		    // URL to remove particular feature rules corresponding to
		    // the STB
		    payLoadData = AutomaticsPropertyUtility
			    .getProperty(AutomaticsConstants.PROP_KEY_RFC_DELETE_FEATURE_SETTINGS_URL);
		    payLoadData = payLoadData.replace(AutomaticsConstants.CONSTANT_REPLACE_FEATURE_NAME, feature);
		    statusCode = CommonMethods.postPayLoadData(tapEnv, dut, payLoadData);
		}
	    }
	} catch (Exception ex) {
	    LOGGER.error("Exception occured during testing with delete " + ex.getMessage());
	} finally {
	    for (reboot = 0; reboot < rebootCount; reboot++) {
		// Rebooting to clear RFC settings in the STB
		tapEnv.waitAfterHardRebootInitiated(dut);
	    }
	}
	LOGGER.debug("ENDING METHOD : clearMultipleFeaturesInProxyXconfDcmServer");
	return statusCode;
    }

    /**
     * This method parses the build appenders provided, and configures the box accordingly.. This also configures box
     * based on the execution mode set in Automatics
     * 
     * @param tapEnv
     * @param dut
     * @return
     */
    public static Map<String, String> applyBuildAppenders(AutomaticsTapApi tapEnv, Dut dut, String buildAppender) {
	boolean rebootStatus = false;
	boolean skipRFC = false;
	TestType testType = null;
	BuildAppenderManager manager = BuildAppenderManager.getInstance();
	Map<String, String> buildAppenderStatusMap = new HashMap<String, String>();
	Map<String, String> rfcAppenderMap = new HashMap<String, String>();
	ArrayList<String> appenderList = fetchAppenders(buildAppender);
	ArrayList<String> rfcAppenders = fetchRfcBuildAppenders(appenderList);
	boolean resetRFCPresent = appenderList.contains("RESET_ALL_RFC");
	int rebootDelay = 3;// value in minutes
	String delayBetweenReboot = AutomaticsPropertyUtility.getProperty(DELAY_BETWEEN_REBOOTS,
		String.valueOf(rebootDelay));
	// LOGGER.info("delayBetweenReboot=" + delayBetweenReboot);
	try {
	    if (CommonMethods.isNotNull(delayBetweenReboot)) {
		rebootDelay = Integer.parseInt(delayBetweenReboot);
	    }
	} catch (Exception e) {
	    LOGGER.error(
		    "Going for default value 3, since 'buildAppender.delay.reboot' was not present in automatics.props");
	}
	// LOGGER.info("Fetching filterTestType = ");
	String testFilterType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	// LOGGER.info("testFilterType = " + testFilterType);
	if (CommonMethods.isNotNull(testFilterType)) {
	    try {
		testType = TestType.valueOf(testFilterType);
		// LOGGER.info("testFilterType = " + testType);
	    } catch (IllegalArgumentException e) {
		LOGGER.error(e.getMessage());
	    }
	} else {
	    LOGGER.error("Provide a valid Test type: Obtained test type as : " + testFilterType);
	    return buildAppenderStatusMap;
	}
	LOGGER.info("Current Test type : " + testType);

	// Need to support multiple build appenders. For this
	// first separate RFC and non-RFC appenders.
	LOGGER.info("Applying RFC build appenders with size : " + rfcAppenders.size());
	// Skip RFC appenders if RESET_ALL_RFC appender is
	// present for child jobs.
	skipRFC = resetRFCPresent && !TestType.isQt(testType.name());
	LOGGER.info("Reset RFC Appender is present : " + resetRFCPresent);
	LOGGER.info("Current Test Type" + testType);
	LOGGER.info("Can we skip setting RFC params ? " + skipRFC);

	if (rfcAppenders.size() > 0 && !skipRFC) {
	    // Include TR69, if RFC_TR69 is present
	    if (appenderList.contains(RFC_TR69ENABLE) && !appenderList.contains(TR69)) {
		LOGGER.info("Adding build appender 'TR69' as 'RFC_#TR69ENABLE' is present.");
		appenderList.add(TR69);
		LOGGER.info("Added Appender : " + TR69);
	    }
	    rfcAppenders = getListOfUnappliedAppenders(manager, dut, tapEnv, rfcAppenders);
	    // Post all RFC appenders
	    if (rfcAppenders.size() > 0) {
		ResponseObject response = CommonMethods.postRfcBuildAppenderJsonProfilesFromAppenderList(dut, tapEnv,
			rfcAppenders);
		if (response.getStatus()) {
		    boolean skipFurthurVerification = false;
		    if (SupportedModelHandler.isRDKB(dut)) {
			tapEnv.setWebPaParams(dut,
				AutomaticsPropertyUtility.getProperty(AutomaticsConstants.DEVICE_BIG_FIRST_ENABLE),
				"false", 3);
			AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
			List<WebPaParameter> result = tapEnv.setWebPaParams(dut,
				AutomaticsPropertyUtility.getProperty(AutomaticsConstants.DEVICE_CONTROL_RETRIEVE), "1",
				2);
			WebPaParameter params = result.get(0);
			if (params != null && params.getMessage().equals("Success")) {
			    boolean isVerificationSuccess = false;
			    LOGGER.info("Sleeping for 5 minte before verifying rfc ");
			    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE * 5);
			    int waitIteration = 2;
			    while (waitIteration > 0) {
				rfcAppenderMap = manager.verifySettings(tapEnv, dut, rfcAppenders);
				List<String> values = new ArrayList<String>(rfcAppenderMap.values());
				int iterator = 0;
				LOGGER.info("values=" + values);
				for (; iterator < values.size(); iterator++) {
				    if (!values.get(iterator).equals(AutomaticsConstants.OK)) {
					break;
				    }
				}
				if (iterator == values.size()) {
				    LOGGER.info("BUILD APPENDER STATUS LOOKS GOOD" + rfcAppenderMap.toString());
				    isVerificationSuccess = true;
				    break;
				} else {
				    // if (waitIteration != 0) {
				    LOGGER.info("Sleeping for 5 minte and retrying ");
				    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE * 5);
				    // }
				    waitIteration--;
				}
			    }
			    if (!isVerificationSuccess) {
				LOGGER.info("RFC settings is not applied yet, proceeding to reboot");
				try {
				    dut.getPower().reboot();
				} catch (PowerProviderException e) {
				    e.printStackTrace();
				}
				rebootStatus = CommonMethods.isSTBAccessible(dut);
			    } else {
				skipFurthurVerification = true;
			    }
			}
		    } else if (SupportedModelHandler.isRDKC(dut)) {
			rebootStatus = waitForEstbIpAcquisition(tapEnv, dut);
			String uptimeString = tapEnv.executeCommandUsingSsh(dut, CMD_GET_UPTIME_FROM_DEVICE,
				AutomaticsConstants.THIRTY_SECONDS);
			if (CommonMethods.isNotNull(uptimeString)) {
			    int uptimeInMinutes = Integer.parseInt(uptimeString.trim());
			    if (uptimeInMinutes < 7) {
				LOGGER.info(
					"Waiting for a {} minutes since 7 minutes delay is observed in fetching new RFC",
					7 - uptimeInMinutes);
				AutomaticsUtils.sleep((7 - uptimeInMinutes) * 60000);
			    }
			}
		    } else {
			// reboot the box twice to ensure RFC values were
			// updated.
			for (int index = 0; index < 2; index++) {
			    if (SupportedModelHandler.isRDKV(dut)) {
				rebootStatus = tapEnv.validateAV(dut);
			    }
			    if (!rebootStatus) {
				LOGGER.error("Failed to perform reboot during iteration {} after posting RFC values",
					(index + 1));
				break;
			    } else {
				// Add delay between reboots
				if (index == 0) {
				    // buildAppender.delay.reboot- convert to
				    // milli seconds
				    rebootDelay = (int) (rebootDelay * AutomaticsConstants.ONE_MINUTE);
				    LOGGER.info("Waiting for " + rebootDelay + "minutes between reboots");
				    tapEnv.waitTill(rebootDelay);
				}
			    }
			    LOGGER.info("Reboot status after iteration " + (index + 1));
			}
		    }
		    if (!skipFurthurVerification) {

			if (rebootStatus) {
			    LOGGER.info(
				    "Successfully rebooted the device twice and verified AV after applying entire RFC features. Now perform validation");
			    if (appenderList.contains("RFC_#CREDENTIALS")) {
				LOGGER.info("Enabling RDM service!.");

				tapEnv.executeCommandUsingSsh(dut,
					AutomaticsPropertyUtility.getProperty(AutomaticsConstants.XCONFDOWNLOAD));
				LOGGER.info("Restarting the RDM service.");
				CommonMethods.restartService(tapEnv, dut,
					AutomaticsPropertyUtility.getProperty(AutomaticsConstants.RESTART_SERVICE));
			    }
			    rfcAppenderMap = manager.verifySettings(tapEnv, dut, rfcAppenders);
			    buildAppenderStatusMap.putAll(rfcAppenderMap);
			    LOGGER.info("Append all the RFC param verifications to final map : size : "
				    + buildAppenderStatusMap.size());
			} else {
			    LOGGER.info("Failed to perform reboot during iteration after posting RFC values");
			    buildAppenderStatusMap.put("RFC",
				    "Failed to perform reboot during iteration after posting RFC values");
			}
		    }
		} else {
		    LOGGER.error("Failed to post the RFC configurations.");
		    buildAppenderStatusMap.put("RFC", "Failed to post the RFC configurations.");
		}
	    } else {
		LOGGER.info("No RFC configurations to be applied.");
	    }
	} else {
	    LOGGER.info("No RFC configurations to be applied.");
	}

	// apply all the non-RFC build appenders
	ArrayList<String> nonRfcAppenders = fetchNonRfcBuildAppenders(appenderList);
	LOGGER.info("Apply and verify non-RFC build appenders with size : " + nonRfcAppenders.size());
	if (nonRfcAppenders.size() > 0 && AutomaticsTestBase.requireEnvSetup) {

	    buildAppenderStatusMap.putAll(manager.applySettings(tapEnv, dut, nonRfcAppenders));
	    boolean buildAppenderFailed = false;
	    Iterator it = buildAppenderStatusMap.entrySet().iterator();
	    while (it.hasNext()) {
		Map.Entry pair = (Map.Entry) it.next();
		LOGGER.info(
			"Final status of applying build appender : " + pair.getKey() + " Value : " + pair.getValue());
		if (pair.getValue() != AutomaticsConstants.OK) {
		    buildAppenderFailed = true;
		    break;
		}
	    }
	    if (!buildAppenderFailed) {
		buildAppenderStatusMap.putAll(manager.verifySettings(tapEnv, dut, nonRfcAppenders));
		LOGGER.info("Append all the NON-RFC param verifications to final map : size : "
			+ buildAppenderStatusMap.size());
	    } else {
		LOGGER.info("Exiting execution as build appender for Non-RFC params were not applied correctly");
	    }
	} else {
	    LOGGER.info("NON RFC build appender need not be applied for current execution");
	}

	// Now call the BuildAppender Manager, which iterates the appenders in
	// given list and configures the box
	// accordingly.
	Iterator it = buildAppenderStatusMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry pair = (Map.Entry) it.next();
	    LOGGER.info("Final status of applying and verification of build appender : " + pair.getKey() + " Value : "
		    + pair.getValue());
	}
	return buildAppenderStatusMap;
    }

    /**
     * Method that parses out the non-RFC build appenders from the given list
     *
     * @param appenderList
     * @return list of non-rfc appenders
     */
    public static ArrayList<String> fetchNonRfcBuildAppenders(ArrayList<String> appenderList) {
	ArrayList<String> normalAppendersList = new ArrayList<String>();
	if (null != appenderList && appenderList.size() > 0) {
	    for (String appender : appenderList) {
		if (!(appender.toLowerCase().contains(RFC_PARAMETER))) {
		    normalAppendersList.add(appender);
		    LOGGER.info("Added non-RFC Appender : " + appender);
		}
	    }
	}
	return normalAppendersList;
    }

    /**
     * Method that parses out the RFC build appenders from the given list
     *
     * @param appenderList
     * @return list of non-rfc appenders
     */
    public static ArrayList<String> fetchRfcBuildAppenders(ArrayList<String> appenderList) {
	ArrayList<String> rfcAppenders = new ArrayList<String>();
	if (null != appenderList && appenderList.size() > 0) {
	    for (String appender : appenderList) {
		if (appender.toLowerCase().contains(RFC_PARAMETER)) {
		    rfcAppenders.add(appender);
		    LOGGER.info("Added RFC Appender : " + appender);
		}
	    }
	}
	return rfcAppenders;
    }

    /**
     * Method that iterates and fill the list build appenders
     * 
     * @return
     */
    public static ArrayList<String> fetchAppenders(String buildAppender) {
	// first configure box based on the execution mode set from Automatics
	String executionMode = AutomaticsTapApi.getCurrentExecutionMode();
	// Store all the required BUILD_APPENDER
	ArrayList<String> appenderList = new ArrayList<String>();
	appenderList.add(executionMode);
	LOGGER.info("Added Execution Mode : " + executionMode);

	if (CommonMethods.isNotNull(buildAppender)) {
	    String[] appenderArray = buildAppender.split(AutomaticsConstants.COMMA);
	    for (String details : appenderArray) {
		appenderList.add(details);
		LOGGER.info("Added Appender : " + details);
	    }
	}
	if (appenderList.contains(RFC_TR69ENABLE) && !appenderList.contains(TR69)) {
	    LOGGER.info("Adding build appender 'TR69' as 'RFC_#TR69ENABLE' is present.");
	    appenderList.add(TR69);
	    LOGGER.info("Added Appender : " + TR69);
	}
	return appenderList;
    }

    /**
     * 
     * Method to identify already applied appenders
     * 
     * @param manager
     * @param dut
     * @param tapEnv
     * @param rfcAppenders
     * @return
     */
    public static ArrayList<String> getListOfUnappliedAppenders(BuildAppenderManager manager, Dut dut,
	    AutomaticsTapApi tapEnv, ArrayList<String> rfcAppenders) {
	LOGGER.info("-------------- > Checking if RFC is already  < -------------- ");
	ArrayList<String> newList = new ArrayList<String>();
	Map<String, String> rfcAppenderMap = manager.verifySettings(tapEnv, dut, rfcAppenders);
	for (String appender : rfcAppenderMap.keySet()) {
	    if (!rfcAppenderMap.get(appender).equals(AutomaticsConstants.OK)) {
		newList.add(appender);
	    } else {
		LOGGER.info("Appender {} will be akipped as it is already applied in the device", appender);
	    }
	}
	return newList;
    }

    /**
     * This method parses the build appenders provided, and configures the box accordingly.. This also configures box
     * based on the execution mode set in Automatics
     * 
     * @param tapEnv
     * @param dut
     * @return
     */
    public static Map<String, String> applyBuildAppenders(AutomaticsTapApi tapEnv, Dut dut) {
	Map<String, String> statusMap = new HashMap<String, String>();
	String buildAppenders = System.getProperty(AutomaticsConstants.BUILD_APPENDER);
	ArrayList<String> appenderList = CommonMethods.fetchAppenders(buildAppenders);
	// Now call the BuildAppender Manager, which iterates the appenders in
	// given list and configures the box
	// accordingly.
	BuildAppenderManager manager = BuildAppenderManager.getInstance();
	statusMap = manager.applySettings(tapEnv, dut, appenderList);
	if (statusMap.values().contains(AutomaticsConstants.OK)) {
	    statusMap = manager.verifySettings(tapEnv, dut, appenderList);
	}
	Iterator it = statusMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry pair = (Map.Entry) it.next();
	    LOGGER.info("Final status of applying and verification of build appender : " + pair.getKey() + " Value : "
		    + pair.getValue());
	}
	// Include TR69, if RFC_TR69 is present
	if (appenderList.contains(RFC_TR69ENABLE) && !appenderList.contains(TR69)) {
	    LOGGER.info("Adding build appender 'TR69' as 'RFC_#TR69ENABLE' is present.");
	    appenderList.add(TR69);
	    LOGGER.info("Added Appender : " + TR69);
	}
	return statusMap;
    }

    /**
     * Method to restart the process
     * 
     * @param dut
     *            Instane of {@link Dut}
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param option
     *            Restart option {@link ProcessRestartOption}
     * @param processName
     *            process name
     * @return true if successfully resterted else false
     */
    public static boolean restartProcess(Dut dut, AutomaticsTapApi tapApi, ProcessRestartOption option,
	    String processName) {

	LOGGER.info("Restarting process ");
	// Process restart status
	boolean restartStatus = false;
	// loop count
	final int loopCount = 4;
	// PID of process before restart
	String pidBeforeRestart = null;
	// PID of process after restart
	String pidAfterRestart = null;
	// command to execute
	String command = null;
	// common command format for killall
	final String killAllFormat = "KILL_ALL";

	try {
	    if (option != null) {
		// Retrieve process PID
		pidBeforeRestart = getPidOfProcess(dut, tapApi, processName);

		if (CommonMethods.isNotNull(pidBeforeRestart)) {

		    // Generate command to kill process
		    if (option.name().contains(killAllFormat)) {
			command = option.getCommand() + processName;
		    } else {
			command = option.getCommand() + pidBeforeRestart;
		    }

		    for (int i = 0; i < loopCount; i++) {

			tapApi.executeCommandInSettopBox(dut, command);
			tapApi.waitTill(AutomaticsConstants.FIVE_SECONDS);
			pidAfterRestart = getPidOfProcess(dut, tapApi, processName);

			if (pidBeforeRestart.equals(pidAfterRestart)) {
			    LOGGER.info("Unable to restart '" + processName + "' process. Retrying ...!!");
			} else {
			    LOGGER.info("Successfully restarted '" + processName + "' process.");
			    restartStatus = true;
			    break;
			}
		    }
		}
	    }
	} catch (Exception exception) {
	    LOGGER.error("Exception occured in restartProcess()", exception);
	}

	if (!restartStatus) {
	    LOGGER.error("Unable to restart '" + processName + "' process in " + dut.getHostMacAddress());
	}
	return restartStatus;

    }

    /**
     * Utility method to reboot the device using SNMP and verify that the device is UP after executing the command
     *
     * @param dut
     *            The device under test
     * @param tapEnv
     *            {@link AutomaticsTapApi}
     * @throws TestException
     *             Thows exception if failed to get proper resoponse of if the device is still accessible after
     *             executing the command)
     */
    public static boolean rebootUsingSnmpAndVerify(Dut dut) {

	boolean status = false;
	boolean stbAccessible = true;
	AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();

	SnmpParams snmpParams = new SnmpParams();
	snmpParams.setIpAddress(((Device) dut).getEcmIpAddress());

	String deviceRebootOid = AutomaticsTapApi.getSTBPropsValue(SnmpConstants.PROP_KEY_DEVICE_REBOOT_OID);
	if (CommonMethods.isNull(deviceRebootOid)) {
	    LOGGER.error(
		    "Device reboot via SNMP will fail as OID for reboot is not configured in Automatics Props in property: {}",
		    SnmpConstants.PROP_KEY_DEVICE_REBOOT_OID);
	}
	snmpParams.setMibOid(deviceRebootOid);

	String deviceRebootOidValue = AutomaticsTapApi.getSTBPropsValue(SnmpConstants.PROP_KEY_DEVICE_REBOOT_OID_VALUE);
	if (CommonMethods.isNull(deviceRebootOidValue)) {
	    LOGGER.error(
		    "Device reboot via SNMP will fail as OID value for reboot is not configured in Automatics Props in property: {}",
		    SnmpConstants.PROP_KEY_DEVICE_REBOOT_OID_VALUE);
	}
	snmpParams.setValue(deviceRebootOidValue);
	snmpParams.setCommandOption(SnmpConstants.SNMP_COMMAND_OPTIONS);
	snmpParams.setDataType(SnmpDataType.INTEGER);
	snmpParams.setSnmpCommand(SnmpCommand.SET);
	String response = tapEnv.executeSnmpCommand(dut, snmpParams);

	if (CommonMethods.isNotNull(response) && !response.contains(SnmpConstants.NO_SUCH_OID_RESPONSE)) {
	    for (int i = 0; i < 2; i++) {
		// Checking whether the device is up. If the device is still up,
		// then reboot did not happen.
		stbAccessible = isSTBAccessible(dut);
		if (stbAccessible) {
		    LOGGER.error("Device is still up after executing snmp command to reset the device. Retry: " + i);
		} else {
		    LOGGER.info("Successfully initiated reboot on device usin snmp");
		    status = true;
		    break;
		}
		// Wait for some time for the device to get reboot
		LOGGER.info("Waiting for 30 seconds for the device to initiate reboot");
		tapEnv.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);
	    }

	} else {
	    LOGGER.error("Failed to reset the device using snmp. Response : " + response);
	}

	return status;
    }

    /**
     * Helper method to reboot the box,wait for IP Acquisition and restart the trace
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @return rebootFinished true if ip acquired after reboot else false
     * 
     */
    public static boolean rebootAndWaitForIpAccusition(Dut dut, AutomaticsTapApi tapEnv) {
	LOGGER.info("STARTING METHOD: rebootAndWaitForIpAccusition()");
	Boolean rebootFinished = false;

	// Reboot the box
	tapEnv.reboot(dut);

	if (!(SupportedModelHandler.isRDKVClient(dut) || SupportedModelHandler.isRDKB(dut))) {
	    // Wait to bring the box power UP(Box gets IP)
	    rebootFinished = waitForEstbIpAcquisition(tapEnv, dut);
	}

	rebootFinished = isSTBAccessible(dut);
	LOGGER.info("ENDING METHOD: rebootAndWaitForIpAccusition()");
	return rebootFinished;
    }

    /**
     * Utility method which updates swupdate.conf in opt directory and insert the XCONF software update URL. This is
     * expected to call prior to all CDL scenarios
     *
     * @param tapEnv
     *            The ecats tap api instance to access the cats properties
     * @param dut
     *            The set-top instance
     * @param buildNameToBeTriggerred
     */
    public static void updateXConfWithSettings(AutomaticsTapApi tapEnv, Dut dut, String buildNameToBeTriggerred) {
	// Configure Mock XConf server with required configurations
	XConfUtils.configureXconfHttpDownloadFirmwareDetailsOnServer(dut, buildNameToBeTriggerred);

	// Update config in device
	XConfUtils.updatXconfUrlInDevice(tapEnv, dut);
    }

    /**
     * Checking whether the setup is in ETHWAN mode.
     * 
     * @return true, if ethwan mode
     */
    public static boolean isRunningEthwanMode() {
	boolean isTrue = false;
	String executionMode = AutomaticsTapApi.getCurrentExecutionMode();
	if (CommonMethods.isNotNull(executionMode)) {
	    try {
		ExecutionMode executionModeFromTm = ExecutionMode.valueOf(executionMode);

	    } catch (Exception e) {
		LOGGER.debug("Could not verify ETHWAN mode");
	    }
	}
	return isTrue;
    }
	/**
     * This method fetched STB IP from Device Manager Properties
     * 
     * @param dut
     *            Dut object
     * @return Returns the STB IP address
     */
    public static String getandSetErouterIpAddress(Dut dut) {
		LOGGER.info("Inside getandSetErouterIpAddress");
		String estbIPAddress = null;
		DevicePropsRequest request = new DevicePropsRequest();
	    request.setMac(dut.getHostMacAddress());
	    List<String> requestedPropsName = new ArrayList<String>();
	    requestedPropsName.add(AutomaticsConstants.DEVICE_PROP_ESTB_IP_ADDRESS);
	    request.setRequestedPropsName(requestedPropsName);

	    DeviceManager deviceManager = DeviceManager.getInstance();
	    Map<String, String> response = deviceManager.getDeviceProperties(request);
	    if(response != null && !response.isEmpty()) {
		    estbIPAddress = response.get(AutomaticsConstants.DEVICE_PROP_ESTB_IP_ADDRESS);
		    if(isNotNull(estbIPAddress)) {
		    	((Device) dut).setErouterIpAddress(estbIPAddress);
		    }	    	
	    } else {
			LOGGER.info("Response from device manager is NULL for MAC : " + dut.getHostMacAddress() + " and property : "
					+ AutomaticsConstants.DEVICE_PROP_ESTB_IP_ADDRESS);
		}
	    LOGGER.info("Exiting getandSetErouterIpAddress with ip =" + estbIPAddress);
		return estbIPAddress;
    }
}
