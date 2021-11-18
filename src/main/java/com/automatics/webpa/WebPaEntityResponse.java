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

import com.automatics.constants.AutomaticsConstants;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

/**
 * Holds the WebPA response.
 * 
 * @author Susheela C
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebPaEntityResponse {

    /**
     * Instance variable of parameter list.
     */
    @JsonProperty("parameters")
    @XmlElement(required = true)
    private List<WebPaEntity> parameters = null;

    /**
     * Instance variable of status code.
     */
    @JsonProperty("statusCode")
    @XmlElement(required = true)
    private int statusCode = 0;

    /**
     * Get Status code
     * 
     * @param statusCode
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
     * Set the status code.
     * 
     * @param parameters
     */
    public List<WebPaEntity> getParameters() {
	return parameters;
    }

    public void setParameters(WebPaEntity webPaEnity) {
	if (null == parameters) {
	    parameters = new ArrayList<WebPaEntity>();
	    parameters.add(webPaEnity);
	} else {
	    parameters.add(webPaEnity);
	}
    }

    /**
     * WebPA Server response will be of the following format:
     * 
     * {"parameters":[{"name":"DeviceConfig.SelfHeal.ConnectivityTest.PingServerList.IPv4PingServerTable.","message":"Success"
     * }],"statusCode":200}
     * 
     * We need to get the
     * 
     * @param parameters
     * 
     * @param statusCode
     * 
     * @throws JSONException
     * 
     * @throws FailedTransitionException
     */
    public WebPaEntityResponse fromJson(String webpaResponse) throws FailedTransitionException {
	try {

	    JSONObject response = new JSONObject(webpaResponse);
	    if (response.has(AutomaticsConstants.STRING_STATUSCODE)) {
		this.setStatusCode(response.getInt("statusCode"));
	    }
	    if (response.has(AutomaticsConstants.STRING_PARAMETERS)) {
		JSONArray arrayOfResponse = response.getJSONArray("parameters");
		for (int index = 0; index < arrayOfResponse.length(); index++) {
		    JSONObject jsonParam = arrayOfResponse.getJSONObject(index);
		    WebPaEntity param = new WebPaEntity();
		    this.setParameters(param.fromJson(jsonParam));
		}
	    }
	} catch (JSONException jasonExp) {
	    throw new FailedTransitionException(GeneralError.INCORRECT_JSON, jasonExp);
	}
	return this;
    }
}
