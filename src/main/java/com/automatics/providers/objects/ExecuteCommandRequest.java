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

import java.util.List;

import com.automatics.providers.connection.DeviceConsoleType;
import com.automatics.providers.connection.ExecuteCommandType;

public class ExecuteCommandRequest {

    /**
     * deviceInfo
     */
    private DeviceInfo deviceInfo;
    /**
     * consoleType
     */
    private String consoleType;
    /**
     * excecuteCommandType
     */
    private String excecuteCommandType;
    /**
     * commandList
     */
    private List<String> commandList;
    /**
     * timeOutMilliSecs
     */
    private long timeOutMilliSecs;
    /**
     * options
     */
    private String[] options;

    /**
     * 
     * @return
     */
    public DeviceInfo getDeviceConnectionRequest() {
	return deviceInfo;
    }

    /**
     * 
     * @param deviceConnectionRequest
     */
    public void setDeviceConnectionRequest(DeviceInfo deviceConnectionRequest) {
	this.deviceInfo = deviceConnectionRequest;
    }

    /**
     * 
     * @return
     */
    public String getConsoleType() {
	return consoleType;
    }

    /**
     * 
     * @param consoleType
     */
    public void setConsoleType(String consoleType) {
	this.consoleType = consoleType;
    }

    /**
     * 
     * @return
     */
    public String getExcecuteCommandType() {
	return excecuteCommandType;
    }

    /**
     * 
     * @param excecuteCommandType
     */
    public void setExcecuteCommandType(String excecuteCommandType) {
	this.excecuteCommandType = excecuteCommandType;
    }

    /**
     * 
     * @return
     */
    public List<String> getCommandList() {
	return commandList;
    }

    /**
     * 
     * @param commandList
     */
    public void setCommandList(List<String> commandList) {
	this.commandList = commandList;
    }

    /**
     * 
     * @return
     */
    public long getTimeOutMilliSecs() {
	return timeOutMilliSecs;
    }

    /**
     * 
     * @param timeOutMilliSecs
     */
    public void setTimeOutMilliSecs(long timeOutMilliSecs) {
	this.timeOutMilliSecs = timeOutMilliSecs;
    }

    /**
     * 
     * @return
     */
    public String[] getOptions() {
	return options;
    }

    /**
     * 
     * @param options
     */
    public void setOptions(String[] options) {
	this.options = options;
    }

}
