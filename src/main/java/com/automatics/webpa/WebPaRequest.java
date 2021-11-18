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

import org.codehaus.jettison.json.JSONArray;

/**
 * Holds the information required to set or get the TR-069 from/to WebPA server.
 * 
 * 'parameters' required for getting values form WebPA server. 'paramDetails' used for setting values in WebPA server.
 * 'isSet' is used to identify the whether set or get operation.
 * 
 * @author smariy003c
 *
 */
public class WebPaRequest {

    /**
     * Flag to identify the whether set or get operation.
     */
    private boolean isSet = false;

    /**
     * TR-069 Parameter list to be set get from WebPA server.
     */
    private String[] parameters = null;

    /**
     * STB MAC Address.
     */
    private String macAddress = null;
    /**
     * TR-069 Parameter list to be set in WebPA server.
     */
    private JSONArray paramDetails = null;

    /**
     * boolean to decide whether request should fetch wild card capable SAT
     */
    private boolean isWildCardSupportRequired = false;

    public boolean isWildCardSupportRequired() {
	return isWildCardSupportRequired;
    }

    public void setWildCardSupportRequired(boolean isWildCardSupportRequired) {
	this.isWildCardSupportRequired = isWildCardSupportRequired;
    }

    /**
     * Flag to check whether set or get operation.
     * 
     * @return true if the operation is set, otherwise false.
     */
    public boolean isSetOperation() {
	return isSet;
    }

    /**
     * Set the flag based on operation requested.
     * 
     * @param isSet
     *            true for set operation, otherwise false.
     */
    public void setSetOperation(boolean isSet) {
	this.isSet = isSet;
    }

    /**
     * Get the list for TR-069 parameter.
     * 
     * @return Array for parameter list.
     */
    public String[] getParameters() {
	return parameters;
    }

    /**
     * Set the list for TR-069 parameter.
     * 
     * @return Array for parameter list.
     */
    public void setParameters(String[] parameters) {
	this.parameters = parameters;
    }

    /**
     * Get STB Mac Address.
     * 
     * @return STB Mac Address.
     */
    public String getMacAddress() {
	return macAddress;
    }

    /**
     * Set STB Mac Address.
     * 
     * @param macAddress
     *            STB Mac Address.
     */
    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    /**
     * Get the TR-069 Parameter details
     * 
     * @return {@link JSONArray} of parameter details.
     */
    public JSONArray getParamDetails() {
	return paramDetails;
    }

    /**
     * Set the TR-069 Parameter details to be set in WebPA server.
     * 
     * @param paramDetails
     *            {@link JSONArray} of parameter details.
     */
    public void setParamDetails(JSONArray paramDetails) {
	this.paramDetails = paramDetails;
    }

}
