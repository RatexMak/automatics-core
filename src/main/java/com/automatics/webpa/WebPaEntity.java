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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

/**
 * Holds the parameter details like name, value, data type, message, parameterCount etc.
 * 
 * @author Susheela C
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebPaEntity {

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
    private List<WebPaTableParameter> value;
    /**
     * Instance variable for parameter data type.
     */
    @JsonProperty("dataType")
    @XmlElement(required = true)
    private int dataType;
    /**
     * Instance variable for parameter count.
     */
    @JsonProperty("parameterCount")
    @XmlElement(required = true)
    private int parameterCount;

    /**
     * Instance variable for parameter error message.
     */
    @JsonProperty("message")
    @XmlElement(required = true)
    private String message;

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
     * @param value
     */

    public List<WebPaTableParameter> getValue() {
	return value;

    }

    /**
     * Set the parameter value.
     * 
     * @param value
     */
    public void setValue(WebPaTableParameter webPaParameterValue) {
	if (null == value) {
	    value = new ArrayList<WebPaTableParameter>();
	    value.add(webPaParameterValue);
	} else {
	    value.add(webPaParameterValue);

	}
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

    /**
     * Get the parameter count.
     * 
     * @return
     */
    public int getParameterCount() {
	return parameterCount;
    }

    /**
     * Set the parametercount.
     * 
     * @param parameterCount
     */
    public void setParameterCount(int parameterCount) {
	this.parameterCount = parameterCount;
    }

    /**
     * Get the message
     * 
     * @return message
     */
    public String getMessage() {
	return message;
    }

    /**
     * Set the message.
     * 
     * @param message
     */
    public void setMessage(String message) {
	this.message = message;
    }

    public WebPaEntity fromJson(JSONObject webPaEntity) throws FailedTransitionException {
	try {
	    if (webPaEntity.has("name")) {
		this.setName(webPaEntity.getString("name"));
	    }

	    if (webPaEntity.has("dataType")) {
		this.setDataType(webPaEntity.getInt("dataType"));
	    }

	    if (webPaEntity.has("parameterCount")) {
		this.setParameterCount(webPaEntity.getInt("parameterCount"));
	    }

	    if (webPaEntity.has("message")) {
		this.setMessage(webPaEntity.getString("message"));
	    }
	    if (webPaEntity.has("value")) {
		JSONArray arrayOfWebPaParameter = webPaEntity.getJSONArray("value");
		for (int index = 0; index < arrayOfWebPaParameter.length(); index++) {
		    JSONObject jsonParam = arrayOfWebPaParameter.getJSONObject(index);
		    WebPaTableParameter param = new WebPaTableParameter();
		    this.setValue(param.fromJson(jsonParam));
		}

	    }
	} catch (JSONException jasonExp) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jasonExp);
	}
	return this;
    }
}
