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
package com.automatics.dataobjects;

import com.automatics.enums.AVMonitorStatus;

/**
 * Set Video/Audio monitor parameters
 * 
 * 
 */
public class AVMonitorData {

    /** variable to hold runId */
    private String runId;

    /** variable to hold deviceId */
    private String deviceId;

    /** variable to hold monitorName */
    private String monitorName;

    /** variable to hold slotName */
    private String slotName;

    private AVMonitorStatus status;

    private String avMonitorReportUrl;

    private String avMonitorAnalyzeUrl;

    public AVMonitorData() {

    }

    /**
     * Constructor
     * 
     * @param runId
     * @param deviceId
     * @param monitorName
     */
    public AVMonitorData(String runId, String deviceId, String monitorName) {
	this.runId = runId;
	this.deviceId = deviceId;
	this.monitorName = monitorName;
    }

    /**
     * Constructor
     * 
     * @param runId
     * @param deviceId
     * @param monitorName
     * @param slotName
     */
    public AVMonitorData(String runId, String deviceId, String monitorName, String slotName) {
	this.runId = runId;
	this.deviceId = deviceId;
	this.monitorName = monitorName;
	this.slotName = slotName;
    }

    /**
     * Get the runId
     * 
     * @return runId
     */

    public String getRunId() {
	return runId;
    }

    /**
     * @param runId
     *            the runId to set
     */
    public void setRunId(String runId) {
	this.runId = runId;
    }

    /**
     * Get the device id
     * 
     * @return deviceId
     */

    public String getDeviceId() {
	return deviceId;
    }

    /**
     * @param deviceId
     *            the deviceId to set
     */
    public void setDeviceId(String deviceId) {
	this.deviceId = deviceId;
    }

    /**
     * Get the monitor name
     * 
     * @return monitorName
     */
    public String getMonitorName() {
	return monitorName;
    }

    /**
     * @param monitorName
     *            the monitorName to set
     */
    public void setMonitorName(String monitorName) {
	this.monitorName = monitorName;
    }

    /**
     * Get the slotName name
     * 
     * @return slotName
     */
    public String getSlotName() {
	return slotName;
    }

    /**
     * @param slotName
     *            the slotName to set
     */
    public void setSlotName(String slotName) {
	this.slotName = slotName;
    }

    /**
     * @return the status
     */
    public AVMonitorStatus getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(AVMonitorStatus status) {
	this.status = status;
    }

    /**
     * @return the avMonitorReportUrl
     */
    public String getAvMonitorReportUrl() {
	return avMonitorReportUrl;
    }

    /**
     * @param avMonitorReportUrl
     *            the avMonitorReportUrl to set
     */
    public void setAvMonitorReportUrl(String avMonitorReportUrl) {
	this.avMonitorReportUrl = avMonitorReportUrl;
    }

    /**
     * @return the avMonitorAnalyzeUrl
     */
    public String getAvMonitorAnalyzeUrl() {
	return avMonitorAnalyzeUrl;
    }

    /**
     * @param avMonitorAnalyzeUrl
     *            the avMonitorAnalyzeUrl to set
     */
    public void setAvMonitorAnalyzeUrl(String avMonitorAnalyzeUrl) {
	this.avMonitorAnalyzeUrl = avMonitorAnalyzeUrl;
    }
}
