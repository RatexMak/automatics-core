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
package com.automatics.providers.rack;

import com.automatics.providers.rack.exceptions.PowerProviderException;

/**
 * Interface for power related operations on device.
 * 
 *
 */
public abstract interface PowerProvider extends BaseProvider {

    /**
     * Power on device.
     * 
     * @return Return true, if device power on is success; otherwise false.
     * @throws PowerProviderException
     *             when exception occurs during device power on
     */
    public abstract boolean powerOn() throws PowerProviderException;

    /**
     * Power off device.
     * 
     * @return Return true, if device power off is success; otherwise false.
     * @throws PowerProviderException
     *             when exception occurs during device power off
     */
    public abstract boolean powerOff() throws PowerProviderException;

    /**
     * Reboot the device.
     * 
     * @return Return true, if device power reboot is success; otherwise false.
     * @throws PowerProviderException
     *             when exception occurs during device reboot
     */
    public abstract boolean reboot() throws PowerProviderException;

    /**
     * Get the device power status
     * 
     * @return Return power status 'ON' or 'OFF'. If no response obtained, then null will be returned.
     * @throws PowerProviderException
     *             when exception occurs while fetching device power status
     */
    public abstract String getPowerStatus() throws PowerProviderException;
}