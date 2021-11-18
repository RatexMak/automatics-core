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

import java.util.Date;

public class CrashDetails {
    private String id;
    private String app;
    private String mac;
    private String version;
    private String deviceModel;
    /**Data crashed expected in format "EEE MMM dd HH:mm:ss z yyyy" **/
    private Date dateCrashed;
    private Date dateReported;
    private Date dateBuilt;
    private String signature;
    private String failedReason;
    private String filename;
    private String deviceType;
    private String crashedReason;
    private String stackTrace;
    private boolean isMinidump;
    private boolean isCoredump;

    public boolean isMinidump() {
	return this.isMinidump;
    }

    public void setMinidump(boolean isMinidump) {
	this.isMinidump = isMinidump;
    }

    public boolean isCoredump() {
	return this.isCoredump;
    }

    public void setCoredump(boolean isCoredump) {
	this.isCoredump = isCoredump;
    }

    public String getStackTrace() {
	return this.stackTrace;
    }

    public void setStackTrace(String stackTrace) {
	this.stackTrace = stackTrace;
    }

    public String getCrashedReason() {
	return this.crashedReason;
    }

    public void setCrashedReason(String crashedReason) {
	this.crashedReason = crashedReason;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getApp() {
	return this.app;
    }

    public void setApp(String app) {
	this.app = app;
    }

    public String getMac() {
	return this.mac;
    }

    public void setMac(String mac) {
	this.mac = mac;
    }

    public String getVersion() {
	return this.version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public String getDeviceModel() {
	return this.deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
	this.deviceModel = deviceModel;
    }

    public Date getDateCrashed() {
	return this.dateCrashed;
    }

    public void setDateCrashed(Date dateCrashed) {
	this.dateCrashed = dateCrashed;
    }

    public Date getDateReported() {
	return this.dateReported;
    }

    public void setDateReported(Date dateReported) {
	this.dateReported = dateReported;
    }

    public Date getDateBuilt() {
	return this.dateBuilt;
    }

    public void setDateBuilt(Date dateBuilt) {
	this.dateBuilt = dateBuilt;
    }

    public String getSignature() {
	return this.signature;
    }

    public void setSignature(String signature) {
	this.signature = signature;
    }

    public String getFailedReason() {
	return this.failedReason;
    }

    public void setFailedReason(String failedReason) {
	this.failedReason = failedReason;
    }

    public String getFilename() {
	return this.filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }

    public String getDeviceType() {
	return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
    }

    public String toString() {
	return "CrashDetails [id=" + this.id + ", app=" + this.app + ", mac=" + this.mac + ", version=" + this.version
		+ ", deviceModel=" + this.deviceModel + ", dateCrashed=" + this.dateCrashed + ", dateReported="
		+ this.dateReported + ", dateBuilt=" + this.dateBuilt + ", signature=" + this.signature
		+ ", failedReason=" + this.failedReason + ", filename=" + this.filename + ", deviceType="
		+ this.deviceType + ", crashedReason=" + this.crashedReason + ", stackTrace=" + this.stackTrace
		+ ", isMinidump=" + this.isMinidump + ", isCoredump=" + this.isCoredump + "]";
    }
}
