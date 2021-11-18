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
 * Defines Security Details foe snmpv3
 * 
 * @author Radhika
 *
 */
public class SnmpSecurityDetails {

    /*
     * Security name - input of param '-u'
     * snmp v3 specific
     */
    private String securityName;

    /*
     * Security level - input of param '-l' Expected Values: (noAuthNoPriv|authNoPriv|authPriv)
     * snmp v3 specific
     */
    private Snmpv3SecurityLevel securityLevel;

    /*
     * Security options for snmp v3.
     * Holds all security options like authentication mechanism, privacy options etc
     */
    private String securityOptions;
    
    /*
     * Snmp Protocol to be used for communication
     * Applicable for snmpv2 and snmpv3
     * */
    private String snmpProtocol;
    
    /*
     * Snmp port to be used for communication
     * Applicable for snmpv2 and snmpv3
     */
    private String snmpPort;
    
    /*
     * Community string for snmp v2
     */
    private String community;

    /**
     * @return the community
     */
    public String getCommunity() {
        return community;
    }

    /**
     * @param community the community to set
     */
    public void setCommunity(String community) {
        this.community = community;
    }

    /**
     * @return the securityName
     */
    public String getSecurityName() {
	return securityName;
    }

    /**
     * @param securityName
     *            the securityName to set
     */
    public void setSecurityName(String securityName) {
	this.securityName = securityName;
    }

    /**
     * @return the securityLevel
     */
    public Snmpv3SecurityLevel getSecurityLevel() {
	return securityLevel;
    }

    /**
     * @param securityLevel
     *            the securityLevel to set
     */
    public void setSecurityLevel(Snmpv3SecurityLevel securityLevel) {
	this.securityLevel = securityLevel;
    }

    /**
     * @return the securityOptions
     */
    public String getSecurityOptions() {
	return securityOptions;
    }

    /**
     * @param securityOptions
     *            the securityOptions to set
     */
    public void setSecurityOptions(String securityOptions) {
	this.securityOptions = securityOptions;
    }

    /**
     * @return the snmpProtocol
     */
    public String getSnmpProtocol() {
        return snmpProtocol;
    }

    /**
     * @param snmpProtocol the snmpProtocol to set
     */
    public void setSnmpProtocol(String snmpProtocol) {
        this.snmpProtocol = snmpProtocol;
    }

    /**
     * @return the snmpPort
     */
    public String getSnmpPort() {
        return snmpPort;
    }

    /**
     * @param snmpPort the snmpPort to set
     */
    public void setSnmpPort(String snmpPort) {
        this.snmpPort = snmpPort;
    }

}
