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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

/**
 * Holds the parameter details like name, value, data type, error message etc.
 * 
 * @author smariy003c
 * 
 */
public class WebPaParameter {

    /**
     * WebPA supported data type.
     *
     */
    public enum DataType {
	STRING,
	INT,
	UNSIGNEDINT,
	BOOLEAN,
	DATETIME,
	BASE64,
	LONG,
	UNSIGNEDLONG,
	FLOAT,
	DOUBLE,
	BYTE,
	INVALID;
    };

    /**
     * Instance variable for parameter name.
     */
    private String name;
    /**
     * Instance variable for parameter value.
     */
    private String value;
    /**
     * Instance variable for parameter data type.
     */
    private int dataType;

    /**
     * Instance variable for attribute
     */
    private JSONObject attribute;

    /**
     * Instance variable for parameter count.
     */
    private int parameterCount;

    /**
     * Instance variable for parameter error message.
     */
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

    /**
     * Get the get attribute.
     * 
     * @return
     */
    public JSONObject getAttribute() {
	return attribute;
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
     * Set the parameter count.
     * 
     * @param parameterCount
     */
    public void setParameterCount(int parameterCount) {
	this.parameterCount = parameterCount;
    }

    /**
     * Set the attribute.
     * 
     * @param attribute
     */
    public void setAttribute(JSONObject attribute) {
	this.attribute = attribute;
    }

    /**
     * Get the error message
     * 
     * @return
     */
    public String getMessage() {
	return message;
    }

    /**
     * Set the error message.
     * 
     * @param message
     */
    public void setMessage(String message) {
	this.message = message;
    }

    public JSONObject toJson() {
	JSONObject webpaParam = new JSONObject();
	try {
	    webpaParam.put("name", name);
	    webpaParam.put("value", value);
	    webpaParam.put("dataType", dataType);
	    webpaParam.put("parameterCount", parameterCount);
	    webpaParam.put("message", message);
	} catch (JSONException jex) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jex);
	}
	return webpaParam;
    }

    public WebPaParameter fromJson(JSONObject webpaParam) {
	try {
	    this.name = webpaParam.getString("name");
	    if (webpaParam.has("value")) {
		this.value = webpaParam.getString("value");
	    }
	    if (webpaParam.has("dataType")) {
		this.dataType = webpaParam.getInt("dataType");
	    }
	    if (webpaParam.has("parameterCount")) {
		this.parameterCount = webpaParam.getInt("parameterCount");
	    }

	    if (webpaParam.has("message")) {
		this.message = webpaParam.getString("message");
	    }

	} catch (JSONException jex) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jex);
	}
	return this;
    }

    public JSONObject toJsonAttribute() {
	JSONObject webpaParam = new JSONObject();
	try {
	    webpaParam.put("name", name);
	    webpaParam.put("attributes", attribute);

	} catch (JSONException jex) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jex);
	}
	return webpaParam;
    }

    public int getDataTypeInt(String dataTypeName) {
	return DataType.valueOf(dataTypeName.toUpperCase()).ordinal();
    }

}
