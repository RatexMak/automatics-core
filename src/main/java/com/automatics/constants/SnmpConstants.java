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

/**
 * Holds SNMP related constants
 * 
 * @author Radhika
 *
 */
public class SnmpConstants {

    public static String SNMP_VERSION_V2 = "v2";

    public static String SNMP_VERSION_V3 = "v3";

    public static String SNMP_VERSION_V3_DOCSIS = "v3_docsis";

    /** private string for snmp operations during community string tests. */
    public static final String PRIVATE_COMMUNITY_STRING = "private_community_string";

    /** private string for snmp operations during community string tests. */
    public static final String PUBLIC_COMMUNITY_STRING = "public_community_string";

    /**
     * Community string name to be used during MTA related snmp tests. This should be replaced with proper community
     * string by partner during test execution in SnmpDataProvider implementation
     */
    public static final String MTA_COMMUNITY_STRING = "MTA_COMMUNITY_STRING";

    /** Invalid Community String to verify snmp operations during negative test scenarios */
    public static final String INVALID_COMMUNITY_STRING = "PRIVATE";

    /** SNMP bin directory export command. */
    public static final String CMD_SNMP_BIN_DIR_EXPORT = "export SNMP_BIN_DIR=/mnt/nfs/bin/target-snmp/bin";

    /** MIBS export command. */
    public static final String CMD_MIBS_EXPORT = "export MIBS=ALL";

    /** MIB directory export command. */
    public static final String CMD_MIB_DIRS_EXPORT = "export MIBDIRS=$SNMP_BIN_DIR/../share/snmp/mibs";

    /** SNMP path export command. */
    public static final String CMD_SNMP_PATH_EXPORT = "export PATH=$PATH:$SNMP_BIN_DIR:";

    /** LD library path export command. */
    public static final String CMD_LD_LIBRARY_PATH_EXPORT = "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/mnt/nfs/bin/target-snmp/lib:/mnt/nfs/usr/lib";

    /** SNMP set command with community. */
    public static final String CMD_SNMP_SET_WITH_COMMUNITY = "snmpset -OQ -v 2c -c";

    /** SNMP set command to set server details for CDL. */
    public static final String CMD_SNMP_SET_DOCS_DEV_SW_SERVER_OID = ".1.3.6.1.2.1.69.1.3.1.0 a";

    /** SNMP set command to set upgrade bin file for CDL. */
    public static final String CMD_SNMP_SET_DOCS_DEV_SW_FILE_NAME_OID = ".1.3.6.1.2.1.69.1.3.2.0 s";

    /** SNMP set command to set admin status for CDL. */
    public static final String CMD_SNMP_SET_DOCS_DEV_SW_ADMIN_STATUS_OID = ".1.3.6.1.2.1.69.1.3.3.0 i 1";

    /** snmp v2. */
    public static final String SNMP_V2_COMMAND_OPTION = "-v 2c";

    /** snmp v3 . */
    public static final String SNMP_V3_COMMAND_OPTION = "-v 3";

    /** snmp command options. */
    public static final String SNMP_COMMAND_OPTIONS = "-OQ";

    /** snmp get command. */
    public static final String SNMP_COMMAND_GET = "snmpget ";

    /** snmp set command. */
    public static final String SNMP_COMMAND_SET = "snmpset ";

    /** snmp walk command. */
    public static final String SNMP_COMMAND_WALK = "snmpwalk ";

    /** snmp table command. */
    public static final String SNMP_COMMAND_TABLE = "snmptable ";

    /** Snmp Execution command option for V3 with timeout parameter */
    public static final String SNMP_COMMAND_TIMEOUT_PARAMTER_V3 = " -t 10 ";

    /** System variable to identify whether the test is for snmp v3 or not */
    public static final String SYSTEM_PARAM_IS_SNMP_V3 = "isSnmpV3";

    /** System variable to identify the version for SNMP, can be v2,v3,v4 etc */
    public static final String SYSTEM_PARAM_SNMP_VERSION = "snmpVersion";

    /** SNMP MIB for firmware version. */
    public static String SNMP_MIB_FW_VERSION = ".1.3.6.1.2.1.1.1";

    /** snmp response for unavailable OID **/
    public static final String NO_SUCH_OID_RESPONSE = "No Such Object available on this agent at this OID";

    /** System variable to identify the version for SNMP, can be v2,v3,v4 etc */
    public static final String SYSTEM_PARAM_ECM_SNMP_VERSION = "ecmSnmpVersion";

    /** SNMP Authentication error message */
    public static final String ERROR_STRING_INVALID_AUTH_KEY = "Authentication failure (incorrect password, community or key)";

    /** Private community. */
    public static final String PRIVATE = "private";

    /** public community. */
    public static final String PUBLIC = "public";

    /** custom community. */
    public static final String CUSTOM = "custom";

    /** Default Snmp port. */
    public static final String DEFAULT_SNMP_PORT = "161";

    /** Default Snmp protocol. */
    public static final String DEFAULT_SNMP_PROTOCOL = "udp";

    /** Default Snmp v6 protocol. */
    public static final String DEFAULT_SNMP_V6_PROTOCOL = "udp6";

    /** Props key of MIB OID for device reboot. */
    public static final String PROP_KEY_DEVICE_REBOOT_OID = "device.reboot.oid";

    /** Props key of MIB OID value for device reboot. */
    public static final String PROP_KEY_DEVICE_REBOOT_OID_VALUE = "device.reboot.oid.value";

    /** SNMP Response - Timeout: No Response */
    public static final String SNMP_RESPONSE_TIMEOUT = "Timeout: No Response";

    /** SNMP Response - Error in packet */
    public static final String SNMP_RESPONSE_ERROR_IN_PACKET = "Error in packet";

    /** SNMP Response - No Such Instance */
    public static final String SNMP_RESPONSE_NO_SUCH_INSTANCE = "No Such Instance";

    /** SNMP Response - No Such Object available */
    public static final String SNMP_RESPONSE_NO_SUCH_OBJECT_AVAILABLE = "No Such Object available";
}
