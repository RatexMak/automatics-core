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
 * 
 * Constants for Selenium related features
 *
 */
public class SeleniumConstants {
    
    /** Selenium register command for linux/raspberrybi os */
    public static final String SELENIUM_REGISTER_CMD_FOR_LIUNX_OS = "selenium.reg.cmd.for.linux.os";

    /** Selenium register command for mac os */
    public static final String SELENIUM_REGISTER_CMD_FOR_MAC_OS = "selenium.reg.cmd.for.mac.os";

    /** Selenium register command for windows os */
    public static final String SELENIUM_REGISTER_CMD_FOR_WONDOWS_OS = "selenium.reg.cmd.for.windows.os";    

    /** Constant hold replace string for chrome driver location */
    public static final String REPLACE_STRING_CHROME_DRIVER_LOCATION = "<CHROME_DRIVER_LOCATION>";
    
    /** Constant hold process id of selenium */
    public static final String PID_OF_SELENIUM = "ps -ef | grep selenium";

    /** Constant hold expected pattern for process id selenium on Linux OS */
    public static final String EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_LINUX = "\\S+\\s+(\\d+).*java -jar";

    /** Constant hold expected pattern for process id selenium on MAC OS */
    public static final String EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_MAC = "\\d+\\s+(\\d+).*java -jar";

    /** Constant hold expected pattern for process id xvfb nohub */
    public static final String EXPECTED_PATTERN_FOR_XVFB_PROCESS_NOHUB = ".*?\\s+(\\d+).*/bin/sh /usr/bin/xvfb-run nohup";

    /** Constant hold expected pattern for process id xvfb */
    public static final String EXPECTED_PATTERN_FOR_XVFB_PROCESS = ".*?\\s+(\\d+).*Xvfb :\\d+\\s+-screen";    

    /** Constant hold process id of java */
    public static final String GET_PID_OF_JAVA = "pidof \"java\" | tr -d \"\\n\" |tr -d \"\\r\"";

    /** Constant hold process id of xvfb */
    public static final String GET_PID_OF_XVFB = "ps -ef | grep xvfb";    

    /** Constant hold log file seleniumstdout.log */
    public static final String SELENIUM_STDOUT_LOG_FILENAME = "//seleniumstdout.log";    

    /** Selenium Hub to Node Conection */
    public static String STRING_HUB = "/wd/hub";
    
    /** Constant hold replace string for vbs file location */
    public static final String REPLACE_STRING_VBS_FILE_LOCATION = "<VBS_FILE_LOCATION>";

    /** Constant hold replace string for SH file location */
    public static final String REPLACE_STRING_SH_FILE_LOCATION = "<SH_FILE_LOCATION>";    

    /** Constant hold java kill command for windows */
    public static final String JAVA_KILL_CMD_FOR_WINDOWS = "taskkill /im java.exe /f";    

    /** Constant hold java kill command for windows */
    public static final String GREP_CMD_JAVA_PROCESS_FOR_WINDOWS = "tasklist | grep 'java.exe'";    

    /** Constant for holding the windows os */
    public static final String OS_WINDOWS = "WINDOWS";

    /** Constant for holding linux os */
    public static final String OS_LINUX = "LINUX";

    /** Raspbian Linux OS */
    public static final String OS_RASPBIAN_LINUX = "RASPBIAN LINUX";

}
