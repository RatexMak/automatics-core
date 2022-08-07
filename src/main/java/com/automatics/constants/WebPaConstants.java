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

public class WebPaConstants {
    public static final String WEBPA_PARAMETER_FOR_SERIAL_NUMBER = "Device.DeviceInfo.SerialNumber";

    /** Constant for WEBPA REBOOT ENABLE */
    public static final String PROP_KEY_RDKB_WEBPA_REBOOT_ENABLE = "rdkb.webpa.reboot.enable";

    /** WebPa parameter for device reboot. The value should be of string type - "Device" */
    public static final String WEBPA_PARAM_DEVICE_CONTROL_DEVICE_REBOOT = "Device.X_CISCO_COM_DeviceControl.RebootDevice";

    /** WEBA PA PARAM NAME For uploading log to log server **/
    public static final String WEBPA_PARAMETER_TRIGGER_UPLOAD = "Device.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.Logging.xOpsDMUploadLogsNow";

    /** WEBA PA PARAM NAME For checking upload status to log server **/
    public static final String WEBPA_PARAMETER_UPLOAD_STATUS = "Device.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.Logging.xOpsDMLogsUploadStatus";

    /**
     * WebPa table for Device.WiFi.
     */
    public static final String WEBPA_TABLE_DEVICE_WIFI = "Device.WiFi";

    /** WebPa index for 2.4GHz Private SSID. */
    public static final String WEBPA_INDEX_2_4_GHZ_PRIVATE_SSID = "10001";

    /** WebPa index for 5GHz Private SSID. */
    public static final String WEBPA_INDEX_5_GHZ_PRIVATE_SSID = "10101";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10002 = "10002";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10102 = "10102";

    /** webpa parameter for public wifi webpa index for 2.4 GHz */
    public static final String WEBPA_INDEX_2_4_GHZ_PUBLIC_WIFI = "10003";

    /** webpa parameter for public wifi webpa index for 5 GHz */
    public static final String WEBPA_INDEX_5_GHZ_PUBLIC_WIFI = "10103";

    /** WebPa index for 2.4 GHz Open LNF SSID Access Point 1. */
    public static final String WEBPA_INDEX_2_4_GHZ_OPEN_LNF_AP1 = "10004";

    /** WebPa index for 5 GHz Open LNF SSID Access Point 2. */
    public static final String WEBPA_INDEX_5_GHZ_OPEN_LNF_AP2 = "10104";

    /** WebPa index for 2.4 GHz Public SSID Access Point 2. */
    public static final String WEBPA_INDEX_2_4_GHZ_PUBLIC_SSID_AP2 = "10005";

    /** WebPa index for 2.4 GHz Public SSID Access Point 2. */
    public static final String WEBPA_INDEX_5_GHZ_PUBLIC_SSID_AP2 = "10105";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10006 = "10006";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10106 = "10106";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10007 = "10007";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10107 = "10107";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10008 = "10008";

    /** WebPa index for Access Point */
    public static final String WEBPA_INDEX_10108 = "10108";

    /** Constant to hold 2.4GHZ index */
    public static final String RADIO_24_GHZ_INDEX = "10000";

    /** Constant to hold 5GHZ index */
    public static final String RADIO_5_GHZ_INDEX = "10100";

}
