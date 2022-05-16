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

package com.automatics.constants;

import org.slf4j.ext.XLogger.Level;

/**
 * This class defines the constants used across the testcase.
 * 
 * @author smithabg
 */
public class AutomaticsConstants {

    /** Underscore. */
    public static final char UNDERSCORE = '_';

    /** Comma. */
    public static final String COMMA = ",";

    /** Space. */
    public static final String SPACE = " ";

    /** Dot. */
    public static final char DOT = '.';

    /** New line character. */
    public static final String NEW_LINE = "\n";

    /** Symbol quotes ("). */
    public static final String SYMBOL_QUOTES = "\"";
    /** Asterisk. */
    public static final String ASTERISK = "\\*";

    /** Forward slash */
    public static final String FORWARD_SLASH = "/";

    /** Colon. */
    public static final String COLON = ":";

    /** Semi-Colon. */
    public static final String SEMICOLON = ";";

    /** Delimiter Ampersand. */
    public static final String DELIMITER_AMPERSAND = "&";

    /** Delimiter Equals. */
    public static final String DELIMITER_EQUALS = "=";

    /** Delimiter Hash. */
    public static final String DELIMITER_HASH = "#";

    public static final String HYPHEN = "-";

    public static final String REBOOT_COUNT = "2";

    /** Class key in properties file. */
    public static final String CLASS_KEY = "classes";

    /** ClassName key in MDC. */
    public static final String CLASS_NAME = "ClassName";

    /** Suite Name. */
    public static final String SUITE_NAME = "MySuite";

    /** Test Name. */
    public static final String TEST_NAME = "MyTest";

    /** Boolean true string. */
    public static final String BOOL_TRUE = "true";

    /** String representation for boolean true. */
    public static final String STRING_TRUE = "true";

    /** String representation for boolean false. */
    public static final String STRING_FALSE = "false";

    /** Annotation type string. */
    public static final String ANN_TYPE = "JDK";

    /** Status Success. */
    public static final String SUCCESS = "Success";

    /** Status Failed. */
    public static final String FAILED = "Failed";

    /** Status Skipped. */
    public static final String SKIPPED = "Skipped";

    /** Empty string. */
    public static final String EMPTY_STRING = "";

    /** Rolling file Max File Size. */
    public static final String MAXFILESIZE = "10MB";

    /** Rolling file Maximum Backup Index. */
    public static final int MAXBACKUPINDEX = 100;

    /** Rolling file Priority Level. */
    public static final Level THRESHOLD = Level.DEBUG;

    /** Test Retry count. */
    public static final int RETRY_COUNT = 3;

    /** Test Retry count for 8 iterations. */
    public static final int RETRY_COUNT_EIGHT = 8;

    /** Test Retry group. */
    public static final String RETRY_GROUP = "Retry";

    /** System path separator. */
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");

    /** One second in millisecond representation. */
    public static final long ONE_SECOND = 1000;

    /** Ten seconds in millisecond representation. */
    public static final long TEN_SECONDS = 10000;

    /** Ten seconds in millisecond representation. */
    public static final long FIVE_SECONDS = 5000;

    /** Two seconds in millisecond representation. */
    public static final long TWO_SECONDS = 2000;

    /** Fifteen seconds in millisecond representation. */
    public static final long FIFTEEN_SECONDS = 15000;

    /** One minute in millisecond representation. */
    public static final long ONE_MINUTE = 60 * ONE_SECOND;

    /** One minute in millisecond representation. */
    public static final long TWO_MINUTES = 2 * ONE_MINUTE;

    /** Eight minute in millisecond representation. */
    public static final long EIGHT_MINUTES = 480 * ONE_SECOND;

    /** Thirty seconds in millisecond representation. */
    public static final long THIRTY_SECONDS = 30 * ONE_SECOND;

    /** Thirty seconds in millisecond representation. */
    public static final long FORTY_FIVE_SECONDS = 45 * ONE_SECOND;

    /** Dut lock success status. */
    public static final int SETTOP_LOCK_SUCCESS = 1;

    /** Dut lock already locked status. */
    public static final int SETTOP_ALREADY_LOCKED = 0;

    /** Dut lock failed status. */
    public static final int SETTOP_LOCK_FAILED = -1;

    /** Key for wait before UI verification. */
    public static final String WAIT_BEFORE_UI_VERIFICATION_PROP_KEY = "wait.before.uiverification_";

    /** Key for wait after within page. */
    public static final String WAIT_AFTER_WITHIN_PAGE_PROP_KEY = "wait.after.withinpage_";

    /** Key for wait after across page. */
    public static final String WAIT_AFTER_ACROSS_PAGE_PROP_KEY = "wait.after.acrosspage_";

    /** TestNG verbose level. */
    public static final int TEST_VERBOSE_LEVEL = 10;

    /** Failed images redirection folder. */
    public static final String TARGET_FOLDER = "target";

    /** System property key for the excluded groups. */
    public static final String SYSTEM_PROPERTY_EXCLUDED_GROUP = "excludedGroups";

    /** System property key for the included groups. */
    public static final String SYSTEM_PROPERTY_INCLUDED_GROUP = "includedGroups";

    /** System property key for the Test ID filter. */
    public static final String SYSTEM_PROPERTY_FILTER_TEST_ID = "filterTestIds";

    /** System property key for the Test Tag filter. */
    public static final String SYSTEM_PROPERTY_FILTER_TEST_TAG = "filterTags";

    /** System property key for the Test class filter. */
    public static final String SYSTEM_PROPERTY_FILTER_TEST_CLASS = "filterTestClass";

    /** System property key for the Test Type filter. */
    public static final String SYSTEM_PROPERTY_FILTER_TEST_TYPE = "filterTestType";

    /** System property key for the build appender. */
    public static final String SYSTEM_PROPERTY_BUILD_APPENDER = "buildAppender";

    /** System property key for the required dut list. */
    public static final String SYSTEM_PROPERTY_SETTOP_LIST = "settopList";
    /**
     * System property key for identifying execution based on account/list of settops
     */
    public static final String SYSTEM_PROPERTY_ACCOUNT_EXECUTION = "isAccount";

    /** System property key for the server dut list. */
    public static final String SYSTEM_PROPERTY_SERVER_SETTOP = "serverSettop";

    /** System property for connected devices */
    public static final String SYSTEM_PROPERTY_CONNECTED_DEVICES_LIST = "connectedDevicesList";

    /** System property for connected devices */
    public static final String SYSTEM_PROPERTY_INITIALIZE_CONNECTED_DEVICES = "initializeConnectedDevice";

    /** System property key for the device type. */
    public static final String SYSTEM_PROPERTY_AED_TYPE = "aedType";

    /** System property key for executeOnType . */
    public static final String SYSTEM_PROPERTY_EXECUTE_ON_TYPE = "executeOn";

    /** System property key for the send report. */
    public static final String SYSTEM_PROPERTY_SEND_REPORT = "sendReport";

    /** System property key for the send report. */
    public static final String SYSTEM_PROPERTY_AED_STATUS = "aedType";

    /** System property key for controlling retry. */
    public static final String SYSTEM_PROPERTY_RETRY_TESTS = "retryByDefault";

    /** System property key for controlling retry count. */
    public static final String SYSTEM_PROPERTY_RETRY_COUNT = "retryCountDefault";

    /** System property key for the enabling RDK Portal update. */
    public static final String SYSTEM_PROPERTY_UPDATE_RDK_PORTAL = "updateRdkPortal";

    /** System property key for identifying HDMI connection status. */
    public static final String SYSTEM_PROPERTY_HDMI_CONNECTION_STATUS = "hdmi";

    /** Default image drift x direction. */
    public static final int DEFAULT_IMAGE_DRIFT_X = 10;

    /** Default image drift y direction. */
    public static final int DEFAULT_IMAGE_DRIFT_Y = 10;

    /** Key for image compare property 'driftX'. */
    public static final String PROP_KEY_IMAGE_COMPARE_DRIFT_X = "driftX";

    /** Key for image compare property 'driftY'. */
    public static final String PROP_KEY_IMAGE_COMPARE_DRIFT_Y = "driftY";

    /** Message for audio/video status present in XG1 or RNG boxes. */
    public static final String AUDIO_VIDEO_STATUS_PRESENT = "started=y";

    /** Stb connection types. */
    public static final String STB_CONNECTION_TYPE = "stb.connection.type";

    /** Regular expression for retriving the device type . */
    public static final String REGULAR_EXPRESSION_DEVICE_TYPE = "deviceType=([a-zA-Z]+)";

    /** Pattern matcher group one. */
    public static final int PATTERN_MATCHER_GROUP_ONE = 1;

    /** Build type - delia. */
    public static final String BUILD_TYPE_DELIA = "delia";

    /** Build type - ocap. */
    public static final String BUILD_TYPE_OCAP = "ocap";

    /** System property key for the device type. */
    public static final String SYSTEM_PROPERTY_BUILD_TYPE = "buildType";

    /** Test type - aed. */
    public static final String AED_TYPE_AED = "aed";

    /** Test type - normal. */
    public static final String AED_TYPE_NORMAL = "normal";

    /** String representing SI parser. */
    public static final String SI_CACHE_PARSER = "si_cache_parser_121";

    /** Regular expression for SI cache result. */
    public static final String REGEX_SI_CACHE_LOCATORID_CHANNELNUMBER = "RChannelVCN#([0-9]{6})-SRCID#([0-9 a-f]{6})";

    /** Post fix for hd channel locator ID. */
    public static final String POSTFIX_HD_CHANNEL = "_hd";

    /** Regular expression for SI cache result. */
    public static final String REGEX_ECM_ESTB_MACID = "IPv4=(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})";

    /** Property key for CMTS IP. */
    public static final String CMTS_IP = "cmts.ip";

    /** Property key for CMTS Username. */
    public static final String CMTS_USERNAME = "cmts.username";

    /** Property key for CMTS Password. */
    public static final String CMTS_PASSWORD = "cmts.password";

    /** Command to execute to get the IP details. */
    public static final String CMD_GET_CMTS_IP_ADDRESS = "show cable modem <ecmMac> verbose";

    /** Format for ecm Mac in execution command. */
    public static final String FORMAT_GET_CMTS_IP_ECM_MAC = "<ecmMac>";

    /** Property key for CMTS IP. */
    public static final String ESTB_MAC = "estb.mac.";

    /** ocap ri location in /var folder. */
    public static final String VAR_LOGS_OCAPRI_LOG_TXT = " /var/logs/ocapri_log.txt";

    /** Box manufacturer name Motorola. */
    public static final String TELNET_RESPONSE_END_STRING = "Connection closed by foreign host";

    /** Property key for Prod build testing . */
    public static final String PROPERTY_IS_PROD_BUILD_TEST = "isProdBuildTest";

    /** Holds Flash file name. */
    public static final String CMD_GET_FLASHED_FILE_NAME = "cat /opt/cdl_flashed_file_name";

    /** T2P Message. */
    public static final String T2PMESSAGE = "t2p:msg";

    /** Model name for ECB devices. */
    public static final String DEVICE_MODEL_ECB = "ECB";

    /** Model name for NUC devices. */
    public static final String DEVICE_MODEL_NUC = "NUC";

    /** Property key for plant and dac details. */
    public static final String PLANT_DAC_DETAILS = "plant.dac.details";

    /** Constant for PASS string. */
    public static final String PASS = "PASS";

    /** Constant for FAIL string. */
    public static final String FAIL = "FAIL";

    /** System property key for the send report to customer distribution. */
    public static final String SYSTEM_PROPERTY_SEND_REPORT_CUSTOMER = "sendReportCustomer";

    /** Pattern for identifying the guide launch operation. */
    public static final String REGULAR_EXPRESSION_SEARCH_GUIDE_VERSION = "__PLOG__ using (\\w\\d)";

    /** Single space character. */
    public static final String SINGLE_SPACE_CHARACTER = " ";

    /** New line delimiter. */
    public static final String DELIMITER_NEW_LINE = "\n";

    /** No such file or directory. */
    public static final String NO_SUCH_FILE_OR_DIRECTORY = "No such file or directory";

    /** Default execute on type value as default box model. */
    public static final String EXECUTE_ON_DEFAULT = "default";

    /** Execute on type value as default box model. */
    public static final String EXECUTE_ON_GATEWAY = "gateway";

    /** Partial property key for device id. */
    public static final String PARTIAL_PROP_KEY_DEVICE_ID = "device.id.";

    /** Localhost. */
    public static String LOCAL_HOST = "localhost";

    /** Localhost IP. */
    public static String LOCAL_HOST_IP = "127.0.0.1";

    /**
     * The string to check for colon with space.
     */
    public static final String COLON_WITH_SPACE = ": ";

    /**
     * The string to identify the sudo password prompt message.
     */
    public static final String SUDO_PASS_WORD_PROMPT = "[sudo] password for ";

    /**
     * The string to identify the warning message for new connection.
     */
    public static final String WARNING_MESSAGE_FOR_NEW_CONNECTION = "Are you sure you want to continue connecting (yes/no)? ";

    /** More than 2 days of allocation period */
    public static final int SETTOP_DEFAULT_LOCK_TIME = 3000;

    /** BUILD_NAME system property. */
    public static final String BUILD_NAME_SYSTEM_PROPERTY = "BUILD_NAME";

    /** Job Manager details id. */
    public static final String JOB_MANAGER_DETAILS_ID = "JMD_ID";

    /** Service Name details. */
    public static final String SERVICE_NAME = "SERVICE_NAME";

    /** binary build image extension. */
    public static final String BINARY_BUILD_IMAGE_EXTENSION = ".bin";

    /** Log file location */
    public static final String SETTOP_LOG_DIRECTORY = System.getProperty(ReportsConstants.USR_DIR) + PATH_SEPARATOR
	    + TARGET_FOLDER + PATH_SEPARATOR + "logs" + PATH_SEPARATOR;

    /** cpu memory usage file location */
    public static final String SETTOP_CPU_MEMORY_USAGE_DIRECTORY = System.getProperty(ReportsConstants.USR_DIR)
	    + PATH_SEPARATOR + TARGET_FOLDER + PATH_SEPARATOR + "cpu_memory_usage" + PATH_SEPARATOR;

    /** Test category coloumn value for AED test */
    public static final String AED_TEST = "AED";

    /** System Property build name which has to be checked */
    public static final String SYSTEM_PROPERTY_BUILD_NAME = "BUILD_NAME";

    /** Dut lock gateway. */
    public static final int SETTOP_LOCK_GATEWAY = 2;

    public static final CharSequence NO_HOST_ERROR = "route";

    /** Reg ex to get the mac address */
    public static final String REG_EX_MAC_ADDRESS_FORMAT = "\\w+:\\w+:\\w+:\\w+:\\w+:\\w+";

    /** Test Type Name For CI QUICK Test */
    public static final String QUICK_CI = "CI_QUICK_TEST";

    /** Constant pattern to be used to identify the component run **/
    public static final String PATTERN_TO_IDENTIFY_COMPONENT_RUN = "[^a-z0-9,_ ]";

    /** Constant key pattern used to get the list of test in a component **/
    public static final String PROP_KEY_VALUE_FOR_GETTING_LIST_OF_COMPONENT = "tests.in.component.";

    /** The Ipv6 extension for build name */
    public static final String BUILD_NAME_IPV6_EXTENSION = "_IPV6";

    /** The WIFI extension for build name */
    public static final String BUILD_NAME_WIFI_EXTENSION = "_WIFI";

    /** System property key for the execution mode. */
    public static final String SYSTEM_PROPERTY_EXECUTION_MODE = "executionMode";

    /** Automation execution mode RDKV */
    public static final String EXECUTION_MODE_RDKV = "RDKV";

    /** Automation execution mode ACCOUNT */
    public static final String EXECUTION_MODE_ACCOUNT = "ACCOUNT_TEST";

    /** SLAAC IP FORMAT */
    public static final String SLAAC_IP_FORMAT = "2601";

    /** Automation execution mode RDKB */
    public static final String EXECUTION_MODE_RDKB = "RDKB";

    /** Constant for holding value */
    public static final String BUILD_APPENDER = "buildAppender";

    /** Parameter name of home account number */
    public static final String PARAM_HOME_ACCOUNT_NUMBER = "homeAccountNumber";

    /** Parameter name of devices */
    public static final String PARAM_DEVICES = "devices";

    /** Parameter name of mac address */
    public static final String PARAM_MACADDRESS = "macAddr";

    /** Property value of Grid webservice URL */
    public static final String GRID_WEB_SERVICE_URL = "grid.webservice.url";

    /** Property key ssh to atom. */
    public static final String PROPERTY_SSH_TO_ATOM_UTILITY = "utility.sshtoatom";

    /** Property key PROPERTY_KEY_FOR_ATOM_IP. */
    public static final String PROPERTY_KEY_FOR_ATOM_IP = "sshtoatom.ip.";

    /** Property key PROPERTY_SSH_TO_ATOM_PATTERN_ATOM_IP. */
    public static final String PROPERTY_SSH_TO_ATOM_PATTERN_ATOM_IP = "pattern.atom.ip";

    /** Timout value for remote SSH. */
    public static final long REMOTE_SSH_TIMEOUT = 2 * ONE_MINUTE;

    /** Property key PROPERTY_KEY_FOR_AV_WAIT_MINUTES. */
    public static final String PROPERTY_KEY_FOR_AV_WAIT_MINUTES = "av.wait.minutes";

    /** Props key for moca self version command. */
    public static final String PROP_KEY_CMD_MOCA_SELF_VERSION = "cmd.moca.self.version";

    /** Props key for XG1 moca self version Regex. */
    public static final String PROP_KEY_REGEX_MOCA_SELF_VERSION_XG1 = "regex.moca.self.version.xg1";

    /** Props key for moca self version Regex. */
    public static final String PROP_KEY_REGEX_MOCA_SELF_VERSION = "regex.moca.self.version";

    /** Constant representing Moca version 1.1 */
    public static final String MOCA_VERSION_1_1 = "0x11";

    /** Constant representing Moca version 1.1 */
    public static final String MOCA_VERSION_1DOT1 = "1.1";

    /** Constant representing Moca version 2.0 */
    public static final String MOCA_VERSION_2_0 = "0x20";

    /** Constant representing Moca version 2.0 */
    public static final String MOCA_VERSION_2POINT0 = "2.0";

    /**
     * string that indicates wifi is connected.
     */
    public static String WIFI_WLAN_STRING = "wlan0";

    /**
     * string that indicates wifi is connected.
     */
    public static String WIFI_ON_STRING = "/tmp/wifi-on";

    /** Status for lxc container enabled */
    public static final String STATUS_CONTAINER_SUPPORT_ENABLED = "CONTAINER_SUPPORT=true";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_LINUX = "connected.client.ostype.LINUX";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_WINDOWS = "connected.client.ostype.WINDOWS";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_RASPBIAN_LINUX = "connected.client.ostype.RASPBIANLINUX";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_IOS = "connected.client.ostype.IOS";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_ANDROID = "connected.client.ostype.ANDROID";

    /** String value GET */
    public static final String GET = "GET";

    /** String value statusCode */
    public static final String STRING_STATUSCODE = "statusCode";

    /** String value parameters */
    public static final String STRING_PARAMETERS = "parameters";

    /** file location for device.properties file */
    public static final String DEVICE_PROPERTIES_FILE_PATH = "/etc/device.properties";

    /** string Linux pipe */
    public static final String LINUX_PIPE_SYMBOL = "|";

    /** Remove command to remove a file/directory */
    public static final String COMMAND_REMOVE = "rm -rf ";

    /** command to execute shell script */
    public static final String COMMAND_SH = "sh ";
    /** Constant to hold reverse_ssh_flag */

    /** INVALID IP RETURNED FROM WEBPA **/
    public static final String INVALID_IP = "0.0.0.0";

    /** HTTP link */
    public static String STRING_HTTP = "http://";

    /** RDKV DeviceConfig CATEGORY */
    public static final String RDK_DEVICE_CATEGORY_RDKV = "RDKV";

    /** RDKB DeviceConfig CATEGORY */
    public static final String RDK_DEVICE_CATEGORY_RDKB = "RDKB";

    /** RDKC DeviceConfig CATEGORY */
    public static final String RDK_DEVICE_CATEGORY_RDKC = "RDKC";

    /**
     * Constant for specifying image compare location
     */

    public static final String IMAGE_COMPARE_SAVELOCATION = "imageSaveLocation";

    /**
     * Constant for specifying image name before and after test suite
     */

    public static final String IMAGENAME_BEFORE_TEST = "VerifyAV_BeforeTest_Image";

    public static final String IMAGENAME_AFTER_TEST = "VerifyAV_AfterTest_Image";
    /**
     * Linux command to give wait time
     */
    public static final String LINUX_CMD_TEN_SECONDS_WAIT_TIME = "-t 10";

    /** property variable to map automation base channel list in stb.properties */
    public static final String PROPERTY_FOR_AUTOMATION_BASE_CHANNEL_LIST = "automation.base.channel.list";

    /** SNMP MIB for firmware version Pattern */
    public static String SOFTWARE_VERSION_PATTERN = "SW_REV:\\s*(.*)";

    /** Constant for replacement string */
    public static final String[] REPLACE = { "", "", "", "", "", "", "" };

    /** String to replace in FEATURE_NAME pay load data **/
    public static final String CONSTANT_REPLACE_FEATURE_NAME = "FEATURE_NAME";

    /** String to replace in ESTB pay load data **/
    public static final String CONSTANT_REPLACE_STBMAC = "ESTB_MAC_ADDRESS";

    /** String to replace in ESTB pay load data **/
    public static final String CONSTANT_REPLACE_EROUTERMAC = "EROUTER_MAC_ADDRESS";
    /** Constant for number 200 */
    public static final int CONSTANT_200 = 200;

    /** Constant for default tune in millisec. */
    public static final int DEFAULT_DELAY = 30;

    /** rfc.properties **/
    public static final String RFC_PROPERTIES = "rfc.properties";

    /** Pattern of getting the Log upload URL from dcm.properties */
    public static final String PATTERN_FOR_CONFIG_DEV_SERVER_URL = "RFC_CONFIG_DEV_SERVER_URL=(\\S+)";

    /** Pattern of updating the RFC server url value */
    public static final String UPDATED_PATTERN_FOR_RFC_SERVER_URL = "RFC_CONFIG_SERVER_URL=(\\S+)";

    /** final part of the sed command with rfc properties as the file where url is to be replaced */
    public static final String SED_COMMAND_LAST_PART_WITH_RFCSCRIPT_LOG = "#g' ";

    /** String ok */
    public static final String OK = "OK";

    /**
     * The string to identify the alternate warning message for new connection.
     */
    public static final String ALTERNATE_WARNING_MESSAGE_FOR_NEW_CONNECTION = "Do you want to continue connecting? (y/n)";

    /**
     * The constant to represent image.
     */
    public static String APPENDER_NATIVE_TEST = "NATIVE";

    /** OS Type key to be read from property file */
    public static final String OS_KEY_MAC = "connected.client.ostype.MACOS";

    /** Reverse ssh failure message for 504 Gateway Timeout */
    public static final String REV_SSH_FAILURE_GATEWAY_TIME_OUT = "504 Gateway Timeout";

    /** Reverse ssh failure message for Connection refused */
    public static final String REV_SSH_FAILURE_CONNECTION_REFUSED = "Connection refused";

    /** Reverse ssh technincal failures */
    public static final String[] REV_SSH_COMMON_FAILURES = new String[] { REV_SSH_FAILURE_GATEWAY_TIME_OUT,
	    REV_SSH_FAILURE_CONNECTION_REFUSED };

    /** Property key reverse ssh retry count */
    public static final String PROP_REVSSH_RETRY_COUNT_ON_FAIL = "revssh.retry.count";

    /** Constant for holding the value 'isgateway' */
    public static final String IS_GATEWAY = "isgateway";

    /** Constant for holding the value 'yes' */
    public static final String YES = "yes";

    /** Five minute in millisecond representation. */
    public static final long FIVE_MINUTES = 5 * 60 * ONE_SECOND;

    /** Constant for holding the value 'no' */
    public static final String NO = "no";

    /** Constant to hold POST request */
    public static final String POST_REQUEST = "POST";

    /** Constant to hold PUT request */
    public static final String PUT_REQUEST = "PUT";

    /** string for holding the pcido confirm command */
    public static final String PCIDO_CONFIRM = "/usr/local/sbin/pcido_b confirm -s0 -w";

    /** string for holding the pcido report command */
    public static final String PCIDO_REPORT = "/usr/local/sbin/pcido_b report";

    /** string for holding the pcido confirm command success string */
    public static final String PCIDO_CONFIRM_SUCCESS_STRING1 = "pcido: Confirm";

    /** string for holding the pcido confirm command success string */
    public static final String PCIDO_CONFIRM_SUCCESS_STRING2 = "Success";

    /** string for holding the pcido confirm command success string */
    public static final String PCIDO_FIRMWARE_CONFIRMED_STRING = "PCI0-0: Confirmed";

    /** Twenty seconds in millisecond representation. */
    public static final long TWENTY_SECONDS = 20000;

    /** Constant holding the redirect operator. */
    public static String REDIRECT_OPERATOR = " > ";

    /** Linux command to sync. */
    public static final String CMD_SYNC = "sync";

    /** The constant for firmware download protocol 'http'. */
    public static final String FIRMWARE_DOWNLOAD_PROTOCOL_HTTP = "http";

    /** String to store secured protocol **/
    public static final String STRING_SECURED_PROTOCOL = "https";

    /** The constant for firmware download protocol 'http'. */
    public static final String FIRMWARE_DOWNLOAD_PROTOCOL_TFTP = "tftp";

    /** Pattern to verify mac address. */
    public static final String PATTERN_TO_FIND_COLON_SEPERATED_MACADDRESS = "(\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2})";

    /** Constant for holding MAC os */
    public static final String OS_MAC = "MAC";

    /** Constant hold replace string for jar file location */
    public static final String REPLACE_STRING_JAR_FILE_LOCATION = "<JAR_FILE_LOCATION>";

    /** Constant hold replace string for natted ip address */
    public static final String REPLACE_STRING_NATTED_IP_ADDRESS = "<NATTED_IP_ADDRESS>";

    /** Constant hold replace string for SH file location */
    public static final String REPLACE_STRING_USER_NAME = "<USER_NAME>";

    /** Forward double slash */
    public static final String FORWARD_DOUBLE_SLASH = "//";

    /** System property to disable Process check while waiting for IP acquistion. */
    public static final String SYSTEM_PROPERTY_DISABLE_PROCESS_CHECK = "DISABLE_PROCESS_CHECK";

    /** Thirty seconds in millisecond representation. */
    public static final int THIRTY_SECONDS_INT = 30000;

    /** One minute in millisecond representation. */
    public static final int ONE_MINUTE_INT = 60000;
    public static final String GENERIC_RESOURCE_LOCATION = "config.param.gen.utility.vm.path";
    /** Constant for number 1 */
    public static final int CONSTANT_1 = 1;

    /** Command to make a file executable */
    public static final String CMD_MAKE_FILE_EXECUTABLE = "chmod a+x ";

    /** String value to store device and reboot reason */
    public static final String DEVICE = "DeviceConfig";

    /** Constant for number 0 */
    public static final int CONSTANT_0 = 0;

    /** Constant for number 5 */
    public static final int CONSTANT_5 = 5;

    /** Constant for number 10 */
    public static final int CONSTANT_10 = 10;

    /**
     * Linux command to give wait time
     */
    public static final String SNMP_WAIT_TIME_THIRTY_SECONDS = " -t 30 ";

    public static final String SYMBOL_SINGLE_QUOTE = "'";

    /** BUILD_NAME system property. */
    public static final String DOWNGRADE_GA_BUILD_SYSTEM_PROPERTY = "DOWNGRADE_GA_BUILD";

    /** System property key for the included groups. */
    public static final String INCLUDED_GROUP_CDN = "CDN";

    public static final String EMPTY_LINE_REMOVER_REGEX = "(?m)^\\s+$";

    public static final String SYSTEM_PARAM_DISABLE_DHC_STATUS_UPDATE = "disableDhcStatusUpdate";

    /** Constant for default ocr wait time in seconds. */
    public static final int DEFAULT_OCR_WAIT = 30;

    public static final String XML_ELEMENT_TAG_NAME_BILLING_ACCOUNT_ID = "billingAccountId";

    /** XML element location ID tag name. */
    public static final String XML_ELEMENT_TAG_NAME_SERVICE_ACCOUNT_ID = "serviceAccountId";

    /** One second in millisecond representation. */
    public static final long ONE_SECOND_IN_MILLIS = 1000;

    /** One Minute in millisecond representation. */
    public static final long ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS;

    /** Ten minute in millisecond representation. */
    public static final long TEN_MINUTE_IN_MILLIS = 10 * ONE_MINUTE_IN_MILLIS;

    /** Constant for string ProcessPID */
    public static final String PROCESS_PID = "ProcessPID";

    /** Constant for special character : */
    public static final String COLON_SEPARATOR = "\\\":\\\"";
    /** Constant represents the youtube application url during fling **/
    public static final String LAUNCH_METRICS = "LaunchMetrics";

    /** constant to hold represent the host mac address of device */
    public static final String MAC_ADDRESS = "<MAC_ADDRESS>";

    /** constant represents the ENGLISH word */
    public static final String ENGLISH = "English";

    /** constant represents the error code RDK-03033. */
    public static final String ERROR_CODE_RDK_03033 = "RDK-03033";

    /** Pattern for matching device ID from splunk event logs */
    public static final String DEVICE_ID_MATCH_PATTERN = "client:authz:device:id=";

    /** Pattern for getting device ID from splunk event logs */
    public static final String DEVICE_ID_FETCH_PATTERN = "client:authz:device:id=\\[(\\w+)\\]";

    /** Constant for double quote - " */
    public static final String DOUBLE_QUOTE = "\"";
    /** Constant to store HDMI0 port name */
    public static final String PORT_HDMI0 = "HDMI0";

    /** Two second in millisecond representation. */
    public static final long TWO_SECOND_IN_MILLIS = 2 * ONE_SECOND_IN_MILLIS;

    /** Ten seconds in millisecond representation. */
    public static final long TEN_SECOND_IN_MILLIS = 5 * TWO_SECOND_IN_MILLIS;

    /** Thirty seconds in millisecond representation. */
    public static final long THIRTY_SECOND_IN_MILLIS = 3 * TEN_SECOND_IN_MILLIS;

    /** Text for "true". */
    public static final String TRUE = "true";

    /** Text for "false". */
    public static final String FALSE = "false";

    /** int value -1 */
    public static final int INT_VALUE_MINUS_ONE = -1;
    /** Constant for number 2 */
    public static final int CONSTANT_2 = 2;

    /** Constant for number 3 */
    public static final int CONSTANT_3 = 3;

    /** Constant for number 4 */
    public static final int CONSTANT_4 = 4;

    /** Constant for number 6 */
    public static final int CONSTANT_6 = 6;

    /** Constant for number 8 */
    public static final int CONSTANT_8 = 8;

    /** Constant for number 9 */
    public static final int CONSTANT_9 = 9;

    /** The JSON key object 'type'. */
    public static final String JSON_KEY_OBJECT_TYPE = "type";

    /** Three minutes in milli seconds. */
    public static final long THREE_MINUTES = 3 * 60 * 1000;
    /** wpeframework service name */
    public static final String SERVICE_NAME_WPEFRAMEWORK = "wpeframework";

    /** Constant to store string Disable **/
    public static final String DISABLE = "Disable";
    /** Constant to store string Enable **/
    public static final String ENABLE = "Enable";
    /** Variable for status value - 'Enabled' */
    public static final String STATUS_VALUE_ENABLED = "Enabled";

    /** Variable for status value - 'Disabled' */
    public static final String STATUS_VALUE_DISABLED = "Disabled";

    /** Reverse SSH connection name stored in persistent connections of Device **/
    public static final String REV_SSH_CONNECTION_NAME = "revSsh";

    public static final String ANSI_REGEX = "(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]";

    /** Property key reverse ssh host server. */
    public static final String PROPERTY_REVERSE_SSH_HOST_SERVER = "reversessh.jump.server";

    /** Property that holds keyword for Automatics PROD build. */
    public static final String PROPERTY_PROD_BUILD_KEYWORDS = "automatics.build.production";

    /** Property that holds keyword for Automatics STABLE build. */
    public static final String PROPERTY_STABLE_BUILD_KEYWORDS = "automatics.build.stable";

    /** Property that holds keyword for Automatics SPRINT build. */
    public static final String PROPERTY_SPRINT_BUILD_KEYWORDS = "automatics.build.sprint";

    /** Property that holds device_config.json path. */
    public static final String PROPERTY_DEVICE_CONFIG = "device.props";

    /** Constant to hold ISWILDCARD string */
    public static final String ISWILDCARD = "isWildCard";

    /** Constant to hold REMOTECOMMAND SELECT string */
    public static final String SELECT = "SELECT";

    /** Constant to hold REMOTECOMMAND EXIT string */
    public static final String EXIT = "EXIT";

    /** Constant for JSON Attribute xmediagateways */
    public static final String XMEDIA_GATEWAYS = "xmediagateways";

    /** Automatics properties key to delete all feature rules including the immutable one corresponding to the STB **/
    public static final String PROP_KEY_RFC_DELETE_ALL_SETTINGS_URL = "proxy.xconf.rfc.delete.all.settings.url";

    /** Automatics properties key to delete all feature rules except the immutable one corresponding to the STB **/
    public static final String PROP_KEY_RFC_DELETE_UPDATE_SETTINGS_URL = "proxy.xconf.rfc.delete.update.settings.url";

    /** Automatics properties key to remove particular feature rules corresponding to the STBB **/
    public static final String PROP_KEY_RFC_DELETE_FEATURE_SETTINGS_URL = "proxy.xconf.rfc.delete.feature.settings.url";

    /** Pattern for QAM Channel locator URL **/
    public static final String PATTERN_FOR_QAM_CHANNEL_LOCATOR_URL = "pattern.channel.locator.url.qam";

    /** Pattern for IPLINEAR Channel locator URL **/
    public static final String PATTERN_FOR_IPLINEAR_CHANNEL_LOCATOR_URL = "pattern.channel.locator.url.iplinear";

    /** Pattern for IPLINEAR Channel locator URL **/
    public static final String PATTERN_FOR_IPLINEAR_CHANNEL_LOCATOR_URL_MPD = "pattern.channel.locator.url.iplinear.mpd";

    /** Device big name first enable **/
    public static final String DEVICE_BIG_FIRST_ENABLE = "device.big.first.enable";

    /** Device control retrieve **/
    public static final String DEVICE_CONTROL_RETRIEVE = "device.control.retrieve";

    /** xconf ssrdownload */
    public static final String XCONFDOWNLOAD = "xconf.download";

    /** Restart service */
    public static final String RESTART_SERVICE = "restart.service";

    /** Native Process Id */
    public static final String NATIVE_PROCESS_ID = "nativeProcessId";

    /** Bean not initialized/found error message */
    public static final String BEAN_NOT_FOUND_LOG = "{} not configured for bean '{}'";

    /** Json key name for property HEAD_END */
    public static final String DEVICE_PROP_HEAD_END = "HEAD_END";

    /** Json key name for property FIRMWARE_VERSION */
    public static final String DEVICE_PROP_FIRMWARE_VERSION = "FIRMWARE_VERSION";

    /** Json key name for property ECM_IP_ADDRESS */
    public static final String DEVICE_PROP_ECM_IP_ADDRESS = "ECM_IP_ADDRESS";

    /** Json key name for property ESTB_IP_ADDRESS */
    public static final String DEVICE_PROP_ESTB_IP_ADDRESS = "ESTB_IP_ADDRESS";

    /** Json key name for property MTA_IP_ADDRESS */
    public static final String DEVICE_PROP_MTA_IP_ADDRESS = "MTA_IP_ADDRESS";

    /** Json key name for property DEVICE_ID */
    public static final String DEVICE_PROP_DEVICE_ID = "DEVICE_ID";

    /** Json key name for property DEVICE_ID_FOR_IP_DEVICE */
    public static final String DEVICE_PROP_DEVICE_ID_FOR_IP_DEVICE = "DEVICE_ID_FOR_IP_DEVICE";

    /** Json key name for property BILLING_ACCOUNT_ID */
    public static final String DEVICE_PROP_BILLING_ACCOUNT_ID = "BILLING_ACCOUNT_ID";

    /** Json key name for property ECM_MAC_ADDRESS */
    public static final String DEVICE_PROP_ECM_MAC_ADDRESS = "ECM_MAC_ADDRESS";

    /** Json key name for property SERVICE_ACCOUNT_ID */
    public static final String DEVICE_PROP_SERVICE_ACCOUNT_ID = "SERVICE_ACCOUNT_ID";

    /** Json key name for property NATIVE_PROCESS_ID */
    public static final String DEVICE_PROP_NATIVE_PROCESS_ID = "NATIVE_PROCESS_ID";

    /** DEFAULT Access method for TR181 data model */
    public static final String DEFAULT_TR181_ACCESS_METHOD = "DEFAULT_TR181_ACCESS_METHOD";

}