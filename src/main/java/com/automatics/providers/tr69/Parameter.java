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

package com.automatics.providers.tr69;

/**
 * 
 * Defines parameter for TR69 communication
 *
 */
public class Parameter {

    private String paramName;

    private String paramValue;

    private String dataType;

    public String getParamName() {
	return paramName;
    }

    public void setParamName(String paramName) {
	this.paramName = paramName;
    }

    public String getParamValue() {
	return paramValue;
    }

    public void setParamValue(String paramValue) {
	this.paramValue = paramValue;
    }

    public String getDataType() {
	return dataType;
    }

    public void setDataType(String dataType) {
	this.dataType = dataType;
    }

}
