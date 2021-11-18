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
package com.automatics.providers.imageupgrade;

/**
 * 
 * Request object for image upgrade
 *
 */
public class ImageRequestParams {

    private String communityString;

    private String snmpImageDownloadServerIp;

    private String firmwareToBeDownloaded;

    private String xConfServerUrl;

    private boolean rebootImmediately;

    /**
     * @return the rebootImmediately
     */
    public boolean isRebootImmediately() {
	return rebootImmediately;
    }

    /**
     * @param rebootImmediately
     *            the rebootImmediately to set
     */
    public void setRebootImmediately(boolean rebootImmediately) {
	this.rebootImmediately = rebootImmediately;
    }

    /**
     * @return the xConfServerUrl
     */
    public String getxConfServerUrl() {
	return xConfServerUrl;
    }

    /**
     * @param xConfServerUrl
     *            the xConfServerUrl to set
     */
    public void setxConfServerUrl(String xConfServerUrl) {
	this.xConfServerUrl = xConfServerUrl;
    }

    /**
     * @return the communityString
     */
    public String getCommunityString() {
	return communityString;
    }

    /**
     * @param communityString
     *            the communityString to set
     */
    public void setCommunityString(String communityString) {
	this.communityString = communityString;
    }

    /**
     * @return the snmpDownloadServerIp
     */
    public String getSnmpImageDownloadServerIp() {
	return snmpImageDownloadServerIp;
    }

    /**
     * @param snmpDownloadServerIp
     *            the snmpDownloadServerIp to set
     */
    public void setSnmpImageDownloadServerIp(String snmpDownloadServerIp) {
	this.snmpImageDownloadServerIp = snmpDownloadServerIp;
    }

    /**
     * @return the firmwareToBeDownloaded
     */
    public String getFirmwareToBeDownloaded() {
	return firmwareToBeDownloaded;
    }

    /**
     * @param firmwareToBeDownloaded
     *            the firmwareToBeDownloaded to set
     */
    public void setFirmwareToBeDownloaded(String firmwareToBeDownloaded) {
	this.firmwareToBeDownloaded = firmwareToBeDownloaded;
    }

}
