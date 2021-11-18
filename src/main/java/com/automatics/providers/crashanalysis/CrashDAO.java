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
package com.automatics.providers.crashanalysis;

import com.automatics.utils.CommonMethods;

public class CrashDAO {

    public CrashDAO(String id, String signature, String dateCrashed, String dateReported, String imageName,
	    String reason, String macAddress, String appName, String model, String reportUrl, CrashType type,
	    String trace) {
	super();
	this.id = id;
	this.signature = signature;
	this.dateCrashed = dateCrashed;
	this.dateReported = dateReported;
	this.imageName = imageName;
	this.reason = reason;
	this.appName = appName;
	this.macAddress = macAddress;
	this.reportUrl = reportUrl;
	this.model = model;
	this.type = type;
	this.stackTace = trace;
    }

    /** Crash details **/
    String id;
    String imageName;
    String appName;
    String signature;
    String dateCrashed;
    String dateReported;
    String reason;
    String macAddress;
    String reportUrl;
    String dumpFile;
    String model;
    CrashType type;
    String stackTace;
    String jiraTicket;

    public String getStackTace() {
	return stackTace;
    }

    public void setStackTace(String stackTace) {
	this.stackTace = stackTace;
    }

    public CrashType getType() {
	return type;
    }

    public void setType(CrashType type) {
	this.type = type;
    }

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getDumpFile() {
	return dumpFile;
    }

    public void setDumpFile(String dumpFile) {
	this.dumpFile = dumpFile;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getImageName() {
	return imageName;
    }

    public void setImageName(String imageName) {
	this.imageName = imageName;
    }

    public String getAppName() {
	return appName;
    }

    public void setAppName(String appName) {
	this.appName = appName;
    }

    public String getSignature() {
	return signature;
    }

    public void setSignature(String signature) {
	this.signature = signature;
    }

    public String getDateCrashed() {
	return dateCrashed;
    }

    public void setDateCrashed(String dateCrashed) {
	this.dateCrashed = dateCrashed;
    }

    public String getDateReported() {
	return dateReported;
    }

    public void setDateReported(String dateReported) {
	this.dateReported = dateReported;
    }

    public String getReason() {
	return reason;
    }

    public void setReason(String reason) {
	this.reason = reason;
    }

    public String getMacAddress() {
	return macAddress;
    }

    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    public String getReportUrl() {
	return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
	this.reportUrl = reportUrl;
    }

    public String getJiraTicket() {
	return jiraTicket;
    }

    public void setJiraTicket(String jiraTicket) {
	this.jiraTicket = jiraTicket;
    }

    @Override
    public String toString() {
	StringBuffer retVal = new StringBuffer();
	retVal.append("*+Crash details+*").append("\n- MAC : " + macAddress).append("\n- Crash portal id : " + id)
		.append("\n- ImageName : " + imageName).append("\n- AppName : ").append(appName)
		.append("\n- Date Crashed :").append(dateCrashed).append("\n- Signature : ").append(signature)
		.append("\n- Reason : ").append(reason).append("\n- Crash Report Url : ").append(reportUrl);
	// .append("\n- Core/Dump File : ").append(dumpFile.replace("ws", "artifact"));
	if (CommonMethods.isNotNull(dumpFile)) {
	    retVal.append("\n- Core/Dump File : ").append(dumpFile.replace("ws", "artifact"));
	}
	return retVal.toString();

    }

    public void destroy() {
	this.destroy();
    }

}
