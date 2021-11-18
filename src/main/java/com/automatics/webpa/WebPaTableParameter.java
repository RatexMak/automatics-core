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
package com.automatics.webpa;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

/**
 * Holds the parameter details of the table like name, value, data type, error message etc.
 * 
 * @author Susheela C
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebPaTableParameter {

    /**
     * Instance variable for parameter name.
     */
    @JsonProperty("name")
    @XmlElement(required = true)
    private String name;
    /**
     * Instance variable for parameter value.
     */
    @JsonProperty("value")
    @XmlElement(required = true)
    private String value;
    /**
     * Instance variable for parameter data type.
     */
    @JsonProperty("dataType")
    @XmlElement(required = true)
    private int dataType;

    /**
     * Instance variable for parameter count.
     */

    /**
     * Get the parameter name.
     * 
     * @return
     */
    public String getName() {
	return name;
    }

    /**
     * Set the parameter name.
     * 
     * @param name
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Get the parameter value.
     * 
     * @return
     */
    public String getValue() {
	return value;
    }

    /**
     * Set the parameter value.
     * 
     * @param value
     */
    public void setValue(String value) {
	this.value = value;
    }

    /**
     * Get the data type.
     * 
     * @return
     */
    public int getDataType() {
	return dataType;
    }

    /**
     * Set the data type.
     * 
     * @param dataType
     */
    public void setDataType(int dataType) {
	this.dataType = dataType;
    }

    public WebPaTableParameter fromJson(JSONObject webpaParam) throws FailedTransitionException {
	try {
	    if (webpaParam.has("name")) {
		this.setName(webpaParam.getString("name"));
	    }
	    if (webpaParam.has("value")) {
		this.setValue(webpaParam.getString("value"));
	    }
	    if (webpaParam.has("dataType")) {
		this.setDataType(webpaParam.getInt("dataType"));
	    }
	} catch (JSONException jsonExp) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jsonExp);
	}
	return this;
    }
}
