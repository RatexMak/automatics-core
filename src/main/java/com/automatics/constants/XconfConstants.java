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
 * Constants for Xconf related features
 *
 */
public class XconfConstants {

    /** stb properties key that holds the value of dev url to be updated in rfc.properties */
    public static final String PROP_KEY_UPDATE_URL_RFC_PROPERTIES = "proxy.xconf.dev.url.rfc.properties";    

    /** Constant holding the location of swupdate.conf file. */
    public static final String SOFTWARE_UPDATE_CONF_FILE = "/opt/swupdate.conf";
    
    /** Constant holding the xconf simulator server url. */
    public static final String PROP_KEY_XCONF_SIMULATOR_SERVER_URL = "xconf.simulator.url"; 
    
    /** Constant holding the xconf firmware location. */
    public static final String PROP_KEY_XCONF_FIRMWARE_LOCATION = "xconf.firmware.location"; 
    
    
    /** The constant for firmware download protocol 'http'. */
    public static final String FIRMWARE_DOWNLOAD_PROTOCOL_HTTP = "http";
    
    /** The constant for firmware download protocol 'https'. */
    public static final String FIRMWARE_DOWNLOAD_PROTOCOL_HTTPS = "https";
    
    /** The constant for firmware download protocol 'tftp'. */
    public static final String FIRMWARE_DOWNLOAD_PROTOCOL_TFTP = "tftp";
   


}
