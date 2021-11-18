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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Holds the Web PA response.
 * 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebPaServerResponse {
    /**
     * Instance variable of status code.
     */
    private int statusCode = 0;

    /**
     * Instance variable of status code.
     */
    private String row = "";

    /**
     * Instance variable of parameter list.
     */
    private List<WebPaParameter> params = null;

    /**
     * Instance variable of error message.
     */
    private String message = "";

    /**
     * Get Status code
     */
    public int getStatusCode() {
	return statusCode;
    }

    /**
     * Set the status code.
     * 
     * @param statusCode
     */
    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    /**
     * Get the list of parameters.
     * 
     * @return
     */
    public List<WebPaParameter> getParams() {
	return params;
    }

    /**
     * Set the list of parameters.
     * 
     * @param params
     */
    public void setParams(List<WebPaParameter> params) {
	this.params = params;
    }

    /**
     * @return the error message
     */
    public String getMessage() {
	return message;
    }

    /**
     * @param message
     *            the error message to set
     */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * @param row
     *            row name to set
     */
    public void setRow(String row) {
	this.row = row;
    }

    /**
     * @return row name
     */
    public String getRow() {
	return row;
    }

    /**
     * WebPA Server response will be of the format
     * {"parameters":[{"name":"param name"
     * ,"message":"Success"}],"statusCode":200} We need to get the
     * 
     * @param webpaResponse
     * @return
     * @throws JSONException
     */
    public WebPaServerResponse fromJson(String webpaResponse) throws FailedTransitionException {
	try {
	    JSONObject response = new JSONObject(webpaResponse);

	    if (response.has("statusCode")) {
		this.setStatusCode(response.getInt("statusCode"));
	    } else if (response.has("code")) {
		this.setStatusCode(response.getInt("code"));
	    }
	    if (response.has("parameters")) {
		List<WebPaParameter> paramList = new ArrayList<WebPaParameter>();

		JSONArray arrayOfResponse = response.getJSONArray("parameters");
		for (int index = 0; index < arrayOfResponse.length(); index++) {
		    JSONObject jsonParam = arrayOfResponse.getJSONObject(index);
		    WebPaParameter param = new WebPaParameter();
		    paramList.add(param.fromJson(jsonParam));
		    String message = paramList.get(index).getMessage();
		    if (message != null && message != "") {
			this.setMessage(message);
		    }
		}
		this.setParams(paramList);
	    }
	    if (response.has("message")) {
		this.setMessage(response.getString("message"));
	    }
	    if (response.has("row")) {
		this.setRow(response.getString("row"));
	    }
	} catch (JSONException jasonExp) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jasonExp);
	}
	return this;
    }
}
