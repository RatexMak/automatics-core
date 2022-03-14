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

    private String firmwareToBeDownloaded;

    /**
     * Location from where build/images can be downloaded for image upgrade eg: http://partner-vm/Images
     **/
    private String firmwareLocation;

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

    /**
     * @return the firmwareLocation
     */
    public String getFirmwareLocation() {
	return firmwareLocation;
    }

    /**
     * @param firmwareLocation
     *            the firmwareLocation to set
     */
    public void setFirmwareLocation(String firmwareLocation) {
	this.firmwareLocation = firmwareLocation;
    }

}
