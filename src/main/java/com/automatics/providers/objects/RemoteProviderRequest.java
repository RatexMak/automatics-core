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

import com.automatics.enums.RemoteControlType;

public class RemoteProviderRequest {

    private String mac;

    private String keySet;

    private String command;
    
    private Integer delayInMilliSec;

    private String textMsg;
    
    private String channelNo;
    
    private boolean autoTune;
    
    private Integer repeatCount;
    
    private List<String> commandList;
    
    private RemoteControlType remoteControlType;
    
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
     * @return the keySet
     */
    public String getKeySet() {
	return keySet;
    }

    /**
     * @param keySet
     *            the keySet to set
     */
    public void setKeySet(String keySet) {
	this.keySet = keySet;
    }

    /**
     * @return the command
     */
    public String getCommand() {
	return command;
    }

    /**
     * @param command
     *            the command to set
     */
    public void setCommand(String command) {
	this.command = command;
    }
    
    /**
     * @return the delayInMilliSec
     */
    public Integer getDelayInMilliSec() {
        return delayInMilliSec;
    }

    /**
     * @param delayInMilliSec the delayInMilliSec to set
     */
    public void setDelayInMilliSec(Integer delayInMilliSec) {
        this.delayInMilliSec = delayInMilliSec;
    }

    /**
     * @return the textMsg
     */
    public String getTextMsg() {
        return textMsg;
    }

    /**
     * @param textMsg the textMsg to set
     */
    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    /**
     * @return the channelNo
     */
    public String getChannelNo() {
        return channelNo;
    }

    /**
     * @param channelNo the channelNo to set
     */
    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    /**
     * @return the autoTune
     */
    public boolean isAutoTune() {
        return autoTune;
    }

    /**
     * @param autoTune the autoTune to set
     */
    public void setAutoTune(boolean autoTune) {
        this.autoTune = autoTune;
    }

    public Integer getRepeatCount() {
	return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
	this.repeatCount = repeatCount;
    }

    /**
     * @return the commandList
     */
    public List<String> getCommandList() {
	return commandList;
    }

    /**
     * @param commandList the commandList to set
     */
    public void setCommandList(List<String> commandList) {
	this.commandList = commandList;
    }

    /**
     * @return the remoteControlType
     */
    public RemoteControlType getRemoteControlType() {
        return remoteControlType;
    }

    /**
     * @param remoteControlType the remoteControlType to set
     */
    public void setRemoteControlType(RemoteControlType remoteControlType) {
        this.remoteControlType = remoteControlType;
    }
    
}
