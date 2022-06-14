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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * This class represents the REST API response for device connection provider request
 * 
 * @author Shameem M Shereef
 *
 */
public class DeviceConnProviderResponse {

    private String status;

    private String data;

    @JsonInclude(Include.NON_NULL)
    private String errorString;

    /**
     * 
     * @return
     */
    public String getStatus() {
	return status;
    }

    /**
     * 
     * @param status
     */
    public void setStatus(String status) {
	this.status = status;
    }

    /**
     * 
     * @return
     */
    public String getData() {
	return data;
    }

    /**
     * 
     * @param data
     */
    public void setData(String data) {
	this.data = data;
    }

    /**
     * 
     * @return
     */
    public String getErrorString() {
	return errorString;
    }

    /**
     * 
     * @param errorString
     */
    public void setErrorString(String errorString) {
	this.errorString = errorString;
    }

}
