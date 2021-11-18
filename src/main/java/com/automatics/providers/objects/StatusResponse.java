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

import com.automatics.providers.objects.enums.StatusMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * StatusResponse class
 * 
 * 
 * @author Raja M
 */

public class StatusResponse {

    private String mac;

    private StatusMessage status;

    // private int statusCode;

    @JsonInclude(Include.NON_NULL)
    private String errorMsg;

    /**
     * @return the mac
     */
    public String getMac() {
	return mac;
    }

    /**
     * @param mac
     *            the mac to set
     */
    public void setMac(String mac) {
	this.mac = mac;
    }

    /**
     * @return the status
     */
    public StatusMessage getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(StatusMessage status) {
	this.status = status;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
	return errorMsg;
    }

    /**
     * @param errorMsg
     *            the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    /**
     * @return the statusCode
     */
    // public int getStatusCode() {
    // return statusCode;
    // }
    //
    // /**
    // * @param statusCode the statusCode to set
    // */
    // public void setStatusCode(int statusCode) {
    // this.statusCode = statusCode;
    // }

    /**
     * @return the response
     */
    // public Object getResponse() {
    // return response;
    // }
    //
    // /**
    // * @param response the response to set
    // */
    // public void setResponse(Object response) {
    // this.response = response;
    // }

    /**
     * @return the response
     */
    /*
     * public Response getResponse() { return response; }
     *//**
     * @param response
     *            the response to set
     */
    /*
     * public void setResponse(Response response) { this.response = response; }
     */

}
