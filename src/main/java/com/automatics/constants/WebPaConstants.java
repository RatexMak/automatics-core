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
}
