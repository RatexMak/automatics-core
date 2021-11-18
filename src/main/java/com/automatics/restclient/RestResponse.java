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
package com.automatics.restclient;

import javax.ws.rs.core.Response;

/**
 * 
 * Ecats Rest response
 * 
 * @author Arun V S
 *
 */
public class RestResponse {

    /**
     * 
     * Response from rest client
     * 
     */
    Response response;

    /**
     * Response body String
     * 
     */
    String responseBody;

    /**
     * 
     * Response code
     * 
     */
    int responseCode;

    /**
     * Get {@code Response}
     * 
     * @return
     */
    public Response getResponse() {
	return response;
    }

    /**
     * 
     * Set response
     * 
     * @param response
     *            {@code Response}
     */
    public void setResponse(Response response) {
	this.response = response;
    }

    /**
     * Get responseBody
     * 
     * @return responseBody
     */
    public String getResponseBody() {
	return responseBody;
    }

    /**
     * Set responseBody
     * 
     * @param responseBody
     */
    public void setResponseBody(String responseBody) {
	this.responseBody = responseBody;
    }

    /**
     * Get response code
     * 
     * @return Return code
     */
    public int getResponseCode() {
	return responseCode;
    }

    /**
     * 
     * Set Response code
     * 
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
	this.responseCode = responseCode;
    }

}
