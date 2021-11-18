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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PowerStatusResponse {

    private String mac;

    private PowerStatus status;

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
    public PowerStatus getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(PowerStatus status) {
	this.status = status;
    }

    /**
     * @return the errorMsg
     */
    // public String getErrorMsg() {
    // return errorMsg;
    // }
    //
    // /**
    // * @param errorMsg the errorMsg to set
    // */
    // public void setErrorMsg(String errorMsg) {
    // this.errorMsg = errorMsg;
    // }

}
