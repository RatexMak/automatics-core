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
package com.automatics.device;

import java.net.URI;
import java.util.Map;

public abstract interface DutInfo {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getHostIpAddress();

    public abstract void setHostIpAddress(String hostIpAddress);

    public abstract String getHostIp4Address();

    public abstract void setHostIp4Address(String hostIp4Address);

    public abstract String getHostIp6Address();

    public abstract void setHostIp6Address(String hostIp6Address);

    public abstract String getHostMacAddress();

    public abstract void setHostMacAddress(String hostMacAddress);

    public abstract String getMcardMacAddress();

    public abstract void setMcardMacAddress(String mcardMacAddress);

    public abstract String getManufacturer();

    public abstract void setManufacturer(String manufacturer);

    public abstract String getModel();

    public abstract void setModel(String model);

    public abstract String getMake();

    public abstract void setMake(String make);

    public abstract String getFirmwareVersion();

    public abstract void setFirmwareVersion(String firmwareVersion);

    public abstract String getSerialNumber();

    public abstract void setSerialNumber(String serialNumber);

    public abstract String getMCardSerialNumber();

    public abstract void setMCardSerialNumber(String mCardSerialNumber);

    public abstract String getUnitAddress();

    public abstract void setUnitAddress(String unitAddress);

    public abstract String getHardwareRevision();

    public abstract void setHardwareRevision(String getHardwareRevision);

    public abstract String getRemoteType();

    public abstract void setRemoteType(String getRemoteType);

    public abstract String getContent();

    public abstract void setContent(String getContent);

    public abstract URI getPowerPath();

    public abstract void setPowerPath(URI powerPath);

    public abstract URI getRemotePath();

    public abstract void setRemotePath(URI remotePath);

    public abstract URI getRfRemotePath();

    public abstract void setRfRemotePath(URI rfRemotePath);

    public abstract URI getAudioPath();

    public abstract void setAudioPath(URI audioPath);

    public abstract URI getClickstreamPath();

    public abstract void setClickstreamPath(URI clickstreamPath);

    public abstract URI getTracePath();

    public abstract void setTracePath(URI tracePath);

    public abstract URI getVideoPath();

    public abstract void setVideoPath(URI videoPath);

    public abstract URI getVideoSelectionPath();

    public abstract void setVideoSelectionPath(URI videoSelectionPath);

    public abstract URI getClusterPath();

    public abstract void setClusterPath(URI clusterPath);

    public abstract Map<String, String> getExtraProperties();

    public abstract void setExtraProperties(Map<String, String> extraProperties);

    public abstract String findExtraProperty(String paramString);

}