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

import com.fasterxml.jackson.annotation.JsonProperty;

public class DevicePropsResponse {

    private String mac;

    @JsonProperty("HEAD_END")  
    private String headEnd;

    @JsonProperty("FIRMWARE_VERSION")    
    private String firmwarename;

    @JsonProperty("ECM_IP_ADDRESS")    
    private String ecmIpAddress;

    @JsonProperty("ESTB_IP_ADDRESS")    
    private String estbIpAddress;

    public String getMac() {
	return mac;
    }

    public void setMac(String mac) {
	this.mac = mac;
    }

    public String getHeadEnd() {
	return headEnd;
    }

    public void setHeadEnd(String headEnd) {
	this.headEnd = headEnd;
    }

    /**
     * @return the firmwarename
     */
    public String getFirmwarename() {
	return firmwarename;
    }

    /**
     * @param firmwarename
     *            the firmwarename to set
     */
    public void setFirmwarename(String firmwarename) {
	this.firmwarename = firmwarename;
    }

    /**
     * @return the ecmIpAddress
     */
    public String getEcmIpAddress() {
	return ecmIpAddress;
    }

    /**
     * @param ecmIpAddress
     *            the ecmIpAddress to set
     */
    public void setEcmIpAddress(String ecmIpAddress) {
	this.ecmIpAddress = ecmIpAddress;
    }

    /**
     * @return the estbIpAddress
     */
    public String getEstbIpAddress() {
	return estbIpAddress;
    }

    /**
     * @param estbIpAddress
     *            the estbIpAddress to set
     */
    public void setEstbIpAddress(String estbIpAddress) {
	this.estbIpAddress = estbIpAddress;
    }

}
