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
package com.automatics.providers.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * AccountDevices class
 * 
 * 
 * @author Raja M
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceResponse {

    private List<DeviceObject> deviceObjects;

    private String errorMsg;

    private String remarks;

    /**
     * @return the deviceObjects
     */
    public List<DeviceObject> getDevices() {
	return deviceObjects;
    }

    /**
     * @param deviceObjects
     *            the deviceObjects to set
     */
    public void setDevices(List<DeviceObject> deviceObjects) {
	this.deviceObjects = deviceObjects;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
	return errorMsg;
    }

    /**
     * @param errorMsg
     *            the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
	return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

}
