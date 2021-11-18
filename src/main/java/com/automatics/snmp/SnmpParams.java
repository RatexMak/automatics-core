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
package com.automatics.snmp;

/**
 * 
 * Defines snmp parameters
 *
 */
public class SnmpParams {
    
    SnmpProtocol snmpVersion = SnmpProtocol.SNMP_V2;

    SnmpCommand snmpCommand;    

    String snmpCommunity;

    String ipAddress;

    String mibOid;

    String commandOption;

    SnmpDataType dataType;

    String value;
    
    boolean generateNewKey;

    public SnmpCommand getSnmpCommand() {
	return snmpCommand;
    }

    public void setSnmpCommand(SnmpCommand snmpCommand) {
	this.snmpCommand = snmpCommand;
    }

    public String getSnmpCommunity() {
	return snmpCommunity;
    }

    public void setSnmpCommunity(String snmpCommunity) {
	this.snmpCommunity = snmpCommunity;
    }

    public String getIpAddress() {
	return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    public String getMibOid() {
	return mibOid;
    }

    public void setMibOid(String mibOid) {
	this.mibOid = mibOid;
    }

    public String getCommandOption() {
	return commandOption;
    }

    public void setCommandOption(String commandOption) {
	this.commandOption = commandOption;
    }

    public SnmpDataType getDataType() {
	return dataType;
    }

    public void setDataType(SnmpDataType dataType) {
	this.dataType = dataType;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }   

    /**
     * @return the generateNewKey
     */
    public boolean isGenerateNewKey() {
        return generateNewKey;
    }

    /**
     * @param generateNewKey the generateNewKey to set
     */
    public void setGenerateNewKey(boolean generateNewKey) {
        this.generateNewKey = generateNewKey;
    }

    /**
     * @return the snmpVersion
     */
    public SnmpProtocol getSnmpVersion() {
        return snmpVersion;
    }

    /**
     * @param snmpVersion the snmpVersion to set
     */
    public void setSnmpVersion(SnmpProtocol snmpVersion) {
        this.snmpVersion = snmpVersion;
    }


}
