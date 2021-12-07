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

public class LinuxCommandConstants {
    public static final String COPY = "cp ";

    public static final String SED = "sed ";

    public static final String OPTION_I = " -i ";

    /** Linux command to disable closed captioning. */
    public static final String CMD_DISABLE_CLOSED_CAPTIONING = "/mnt/nfs/bin/vlapicaller mpeos_dbgSetLogLevelString \"\\\"LOG.MPE.CC\\\"\" \"\\\"NONE\\\"\"";

    /** Linux command to enable closed captioning. */
    public static final String CMD_ENABLE_CLOSED_CAPTIONING = "/mnt/nfs/bin/vlapicaller mpeos_dbgSetLogLevelString \"\\\"LOG.MPE.CC\\\"\" \"\\\"DEBUG\\\"\"";

    /** CMD no action command. */
    public static final String CMD_NO_ACTION = "";

    /** Linux command echo. */
    public static final String CMD_ECHO = "echo";

    /** Linux command to reboot the system. */
    public static final String CMD_REBOOT = "/sbin/reboot";

    /** The command to search for "deviceType" from the wbdevice.log. */
    public static final String CMD_GREP_DEVICE_TYPE_WB_DEVICE_LOG = "grep -i \"deviceType\" /opt/logs/wbdevice.log";

    /** Linux command - arp -a. */
    public static final String CMD_ARP = "/sbin/arp -n";

    /** Linux command - telnet. */
    public static final String CMD_TELNET = "telnet";

    /** Command to use get version name. */
    public static final String CMD_GREP_IMAGE_NAME_FROM_VERSION_FILE = "grep \"imagename\\(:\\|=\\)\" /version.txt";

    /** Command to get SI cache result. */
    public static final String CMD_PARSE_SI_CACHE = "/si_cache_parser_121 /tmp/mnt/diska3/persistent/si/SICache /tmp/mnt/diska3/persistent/si/SISNSCache";

    /** Command to get value of PERSISTENT_DIRECTORY. */
    public static final String CMD_GET_PERSISTENT_DIRECTORY = "grep -i ^PERSISTENT_DIR /etc/device.properties | awk '{print $1}' | cut -d '=' -f2";

    /** Command to get value of APP_PERSISTENT_PATH. */
    public static final String CMD_GET_APP_PERSISTENT_PATH = "grep -i ^APP_PERSISTENT_PATH /etc/device.properties | awk '{print $1}' | cut -d '=' -f2";

    /** Box manufacturer name Motorola. */
    public static final String TELNET_COMMAND = "telnet";

    /** The command for ssh to the production boxes. */
    public static final String CMD_LINUX_SUDO_SSH = "sudo ssh ";

    public static String CMD_GREP = " grep";

    /** Command start to execute TR69 Params */
    public static final String CURL_COMMAND_PARAM_LIST = "curl -d '{\"paramList\" : [{\"name\" : \"";

    public static final String CMD_GET_FLASHED_FILE_NAME = "cat /opt/cdl_flashed_file_name";

    /**
     * The constant holding command for seeing the process status of CCSP process
     */
    public static final String PS_COMMAND_FOR_CCSP_PROCESS = "ps | grep -i \"[Cc|S]sp\"";

    /** The command for listing the files in a directory. */
    public static final String CMD_LIST_FOLDER_FILES = "ls ";

    /** The command to search for Moca MAC from ifconfig. */
    public static final String CMD_GET_EROUTER_MAC_FROM_IFCONFIG_FOR_RDKB = "/sbin/ifconfig erouter0 | awk 'FNR == 1 {print}' | awk '{print $5}'";

    /** List of commands which requires long response wait time */
    public static final String[] ARRAY_COMMANDS_LONG_RESPONSE_TIME = { "find", "ps", "dmesg", "sshtoatom", "top",
	    "grep -ir" };

    /** first part of the sed command for switching Xconf URLs */
    public static final String SED_COMMAND_FIRST_PART = "sed -i 's#";

    /** Command to copy the rfc.properties file */
    public static final String CMD_CAT_RFC_PROPERTIES = "cat /opt/rfc.properties";

    /** Command to copy rfc.properties from etc to opt */
    public static String CMD_CP_RFC_PROPERTIES = "cp /etc/rfc.properties /opt/";

    /** Command to grep rfc in opt **/
    public static final String LS_OPT_RFC = "ls /opt/ | grep rfc";

    /** Linux command cd */
    public static final String LINUX_COMMAND_CD = "cd ";

    /** Command to fetch erouter details */
    public static final String COMMNAD_FETCH_EROUTER_INTERFACE_INFO = "/sbin/ifconfig erouter0";

    /** Linux Grep command */
    public static final String COMMAND_GREP = "grep";

    /** CAT command to read a file */
    public static final String COMMAND_CAT = "cat ";

    /**
     * Grep command to
     */
    public static String GREP_WIFI_ON = "ls -ltr /tmp/wifi-on";

    /** Constant checking if box have lxc container support */
    public static final String COMMAND_CONTAINER_SUPPORT = "cat /etc/device.properties  | grep -i \"CONTAINER\"";

    /**
     * String to check if box is wifi enabled
     */
    public static String GREP_WIFI_SUPPORT = "grep -i \"Broadcast Network interface\" /opt/logs/xdevice.log*";

    /**
     * Linux command - /sbin/ip -6 neigh show. for retrieving client ipv6 address from gateway
     */
    public static final String CMD_IP_NEIGH_SHOW = "/sbin/ip neigh show";

    /** The command to verify whether its a yocto Build or not */
    public static final String CMD_IS_YOCTO_BUILD = "if [ -f /etc/os-release ] ; then echo \"yes\" ; else echo \"no\" ; fi";

    /** * Constant for storing the command to retrieve output.json */
    public static final String CAT_COMMAND_TO_GET_OPT_OUPUT_JSON = "cat /opt/output.json";
    
    /** Constant holding the redirect operator. */
    public static String REDIRECT_OPERATOR = " > ";
    
    /** Linux command to sync. */
    public static final String CMD_SYNC = "sync";

}
