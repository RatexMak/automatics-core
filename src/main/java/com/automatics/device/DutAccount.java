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

package com.automatics.device;

import java.util.List;

/**
 * Interface to hold home account details
 */
/**
 * @author reena
 *
 */
/**
 * @author bp-vmatha001
 *
 */
public interface DutAccount {

    /**
     * Get home account number
     *
     * @return the get DutAccount number
     */
    String getAccountNumber();

    /**
     * Get name of the account
     *
     * @return the account name
     */
    String getName();

    /**
     * Get locked devices
     * 
     * @return
     */
    List<DutInfo> getDevices();

    /**
     * Get the phone number associated with the homeAccount
     * 
     * @return
     */
    String getPhoneNumber();

    /**
     * Check if all dut/duts associated with the homeAccount is locked
     * 
     * @return
     */
    boolean isLocked();

    /**
     * Get device that matches the mentioned model
     * 
     * @param model
     * @return
     */
    List<DutInfo> getDeviceByModel(String model);

    /**
     * Get pivot DUT
     * 
     * @return dutInfo
     */
    DutInfo getPivotDut();

    /**
     * Get all the RDKV gateway devices
     * 
     * @return
     */
    List<DutInfo> getAllRdkVGatewayDevices();

    /**
     * List all RdkV client devices
     * 
     * @return
     */
    List<DutInfo> getAllRdkVClientDevices();

    /**
     * Get all RdkB devices
     * 
     * @return
     */
    List<DutInfo> getAllRdkBDevices();

    /**
     * Get Wisst URL
     * 
     * @return
     */
    String getWisstUrl();

    /**
     * List all RdkC client devices
     * 
     * @return
     */
    List<DutInfo> getAllRdkCClientDevices();

    /**
     * Get Wisst URL
     * 
     * @return
     */
    String getQuadAttenUrl();

    /**
     * Get Wisst URL
     * 
     * @return
     */
    String getQuadAttenDeviceId();

    /**
     * Get Wisst URL
     * 
     * @return
     */
    String getQuadAttenDeviceAlias();

}
