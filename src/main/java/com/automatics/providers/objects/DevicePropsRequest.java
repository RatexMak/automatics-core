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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for DeviceConfig Properties
 * 
 * @author Radhika
 *
 */
public class DevicePropsRequest {

    private String mac;
    
    @JsonProperty("deviceProps")
    private List<String> requestedPropsName;

    /**
     * @return the mac
     */
    public String getMac() {
	return mac;
    }

    /**
     * @param mac
     *            the mac to set
     */
    public void setMac(String mac) {
	this.mac = mac;
    }

    /**
     * @return the requestedPropsName
     */
    public List<String> getRequestedPropsName() {
	return requestedPropsName;
    }

    /**
     * @param requestedPropsName
     *            the requestedPropsName to set
     */
    public void setRequestedPropsName(List<String> requestedPropsName) {
	this.requestedPropsName = requestedPropsName;
    }

}
