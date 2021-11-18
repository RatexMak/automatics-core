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

package com.automatics.utils.tr69;

public class Tr69Constants {

    /** Represents the TR69 command to get/set the given setting. */
    public static final String TR69_COMMAND_FORMAT_HOSTNAME = "/usr/local/tr69/host-if -H";    

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_PORT_NUMBER = "-p 8081 -g";

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_GET_OPERATION = "-g";

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_SET_OPERATION = "-s";

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_SET_VALUE = "-v";

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_DELETE_OPERATION = "-d";

    /** The constant represents TR69 get command operation. */
    public static final String TR69_COMMAND_FOR_ADD_OPERATION = "-a";

    /** TR-069 Parameter for DeviceConfig.DeviceInfo.X_RDKCENTRAL-COM_PreferredGatewayType. */
    public static final String TR69_PARAM_PREFERED_GATEWAY = "DeviceConfig.DeviceInfo.X_RDKCENTRAL-COM_PreferredGatewayType";
    /** TR69 Value */
    public static String TR69_RESPONSE_VALUE = "Value";

}
