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
 * Holds dmcli related constants
 *
 */
public class DmcliConstants {

    /**
     * Dmcli param to apply settings for Wifi Radio parameters with odd index value
     */
    public static final String DEVICE_WIFI_RADIO_1_APPLY_SETTING = "Device.WiFi.Radio.1.X_CISCO_COM_ApplySetting";

    /**
     * Dmcli param to apply settings for Wifi Radio parameters with even index value
     */
    public static final String DEVICE_WIFI_RADIO_2_APPLY_SETTING = "Device.WiFi.Radio.2.X_CISCO_COM_ApplySetting";

    /**
     * Prefix for wifi radio related dmcli parameters
     */
    public static final String DMCLI_WIFI_RADIO = "Device.WiFi.Radio.";

    /**
     * Prefix for wifi ssid related dmcli parameters
     */
    public static final String DMCLI_WIFI_SSID = "Device.WiFi.SSID.";

    /**
     * Prefix for wifi access point related dmcli parameters
     */
    public static final String DMCLI_WIFI_ACCESSPOINT = "Device.WiFi.AccessPoint.";

}
