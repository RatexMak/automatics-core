/**
 * Copyright 2022 Comcast Cable Communications Management, LLC
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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerManagerOperationResponse {

    private String powerManagerName;
    private String powerOperation;
    private String urlTemplate;
    private String requestType;
    private String requestBodyTemplate;
    private String responseCode;
    private String responseBodyTemplate;
    private String extraProperties;
    private int slot = 0;
    private String groupName;
    private Date createdDate;

    /**
     * @return the powerManagerName
     */
    public String getPowerManagerName() {
	return powerManagerName;
    }

    /**
     * @param powerManagerName
     *            the powerManagerName to set
     */
    public void setPowerManagerName(String powerManagerName) {
	this.powerManagerName = powerManagerName;
    }

    /**
     * @return the powerOperation
     */
    public String getPowerOperation() {
	return powerOperation;
    }

    /**
     * @param powerOperation
     *            the powerOperation to set
     */
    public void setPowerOperation(String powerOperation) {
	this.powerOperation = powerOperation;
    }

    /**
     * @return the urlTemplate
     */
    public String getUrlTemplate() {
	return urlTemplate;
    }

    /**
     * @param urlTemplate
     *            the urlTemplate to set
     */
    public void setUrlTemplate(String urlTemplate) {
	this.urlTemplate = urlTemplate;
    }

    /**
     * @return the requestType
     */
    public String getRequestType() {
	return requestType;
    }

    /**
     * @param requestType
     *            the requestType to set
     */
    public void setRequestType(String requestType) {
	this.requestType = requestType;
    }

    /**
     * @return the requestBodyTemplate
     */
    public String getRequestBodyTemplate() {
	return requestBodyTemplate;
    }

    /**
     * @param requestBodyTemplate
     *            the requestBodyTemplate to set
     */
    public void setRequestBodyTemplate(String requestBodyTemplate) {
	this.requestBodyTemplate = requestBodyTemplate;
    }

    /**
     * @return the responseCode
     */
    public String getResponseCode() {
	return responseCode;
    }

    /**
     * @param responseCode
     *            the responseCode to set
     */
    public void setResponseCode(String responseCode) {
	this.responseCode = responseCode;
    }

    /**
     * @return the responseBodyTemplate
     */
    public String getResponseBodyTemplate() {
	return responseBodyTemplate;
    }

    /**
     * @param responseBodyTemplate
     *            the responseBodyTemplate to set
     */
    public void setResponseBodyTemplate(String responseBodyTemplate) {
	this.responseBodyTemplate = responseBodyTemplate;
    }

    /**
     * @return the extraProperties
     */
    public String getExtraProperties() {
	return extraProperties;
    }

    /**
     * @param extraProperties
     *            the extraProperties to set
     */
    public void setExtraProperties(String extraProperties) {
	this.extraProperties = extraProperties;
    }

    /**
     * @return the slot
     */
    public int getSlot() {
	return slot;
    }

    /**
     * @param slot
     *            the slot to set
     */
    public void setSlot(int slot) {
	this.slot = slot;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
	return groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(String groupName) {
	this.groupName = groupName;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
	return createdDate;
    }

    /**
     * @param createdDate
     *            the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }

}
