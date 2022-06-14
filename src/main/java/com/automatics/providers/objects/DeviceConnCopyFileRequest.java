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

/**
 * 
 * @author 29014
 *
 */
public class DeviceConnCopyFileRequest {

    /**
     * deviceConnectionRequest
     */
    private DeviceInfo deviceConnectionRequest;
    /**
     * filePath
     */
    private String filePath;
    /**
     * destFilePath
     */
    private String destFilePath;

    /**
     * 
     * @return
     */
    public DeviceInfo getDeviceConnectionRequest() {
	return deviceConnectionRequest;
    }

    /**
     * 
     * @param deviceConnectionRequest
     */
    public void setDeviceConnectionRequest(DeviceInfo deviceConnectionRequest) {
	this.deviceConnectionRequest = deviceConnectionRequest;
    }

    /**
     * 
     * @return
     */
    public String getFilePath() {
	return filePath;
    }

    /**
     * 
     * @param filePath
     */
    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    /**
     * 
     * @return
     */
    public String getDestFilePath() {
	return destFilePath;
    }

    /**
     * 
     * @param destFilePath
     */
    public void setDestFilePath(String destFilePath) {
	this.destFilePath = destFilePath;
    }

}
