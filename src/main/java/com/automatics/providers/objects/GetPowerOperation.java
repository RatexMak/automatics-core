/**
 * Copyright 2022 Comcast Cable Communications Management, LLC
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

package com.automatics.providers.objects;

public class GetPowerOperation {

    private String macAddress;

    // Valid values"ON, OFF, POWER_STATUS, POWER_CYCLE"
    private String powerOperation;

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
	return macAddress;
    }

    /**
     * @param macAddress
     *            the macAddress to set
     */
    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    /**
     * @return the powerOperation
     */
    public String getPowerOperation() {
	return powerOperation;
    }

    /**
     * @param powerOperation
     *            the powerOperation to set
     */
    public void setPowerOperation(String powerOperation) {
	this.powerOperation = powerOperation;
    }

}