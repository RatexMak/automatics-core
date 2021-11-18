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
package com.automatics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.SnmpConstants;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpProtocol;

/**
 * Utility class for SNMP related operations
 * 
 * @author Radhika
 *
 */
public class AutomaticsSnmpUtils {

    /** SLF4J logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsSnmpUtils.class);

    /**
     * Method that gets the required SnmpProtocol enum
     * 
     * @param requiredProtocol
     *            - String that represents the version
     * @return SnmpProtocol
     */
    public static SnmpProtocol getSnmpProtocolVersion(String requiredProtocol) {
	SnmpProtocol[] protocolArray = SnmpProtocol.values();
	for (SnmpProtocol snmpProtocol : protocolArray) {
	    if (snmpProtocol.getProtocolVersion().equals(requiredProtocol)) {
		return snmpProtocol;
	    }
	}
	return null;
    }

    /**
     * Checks if snmp v3 enabled via build appender
     * 
     * @return True if the snmpv3 enabled via build appender
     */
    public static boolean isSnmpV3EnabledUsingAppender() {
	boolean enabed = false;
	String appender = System.getProperty(AutomaticsConstants.BUILD_APPENDER);
	if (CommonMethods.isNotNull(appender)) {
	    appender = appender.toUpperCase();
	    if (appender.contains("SNMPV3")) {
		enabed = true;
	    }
	}

	LOGGER.info("Is SNMPv3 enabled using appender - " + enabed);
	return enabed;
    }

    /**
     * Returns snmp version
     * 
     *
     * @return SnmpProtocol snmp version
     */
    public static SnmpProtocol getSnmpProtocolVersion() {
	String snmpProtocol = System
		.getProperty(SnmpConstants.SYSTEM_PARAM_SNMP_VERSION, SnmpConstants.SNMP_VERSION_V2);

	SnmpProtocol snmpVersion = SnmpProtocol.SNMP_V2;
	if (SnmpProtocol.SNMP_V3.getProtocolVersion().equals(snmpProtocol)) {
	    snmpVersion = SnmpProtocol.SNMP_V3;
	}
	return snmpVersion;
    }

    /**
     * Returns copy of the snmp params so that the original request params will not be changed
     * 
     * @param snmpParams
     * @return Snmp params
     */
    public static SnmpParams getCopyOfSnmpRequest(SnmpParams snmpParams) {
	SnmpParams reqParams = new SnmpParams();
	reqParams.setCommandOption(snmpParams.getCommandOption());
	reqParams.setDataType(snmpParams.getDataType());
	reqParams.setGenerateNewKey(snmpParams.isGenerateNewKey());
	reqParams.setIpAddress(snmpParams.getIpAddress());
	reqParams.setMibOid(snmpParams.getMibOid());
	reqParams.setSnmpCommand(snmpParams.getSnmpCommand());
	reqParams.setSnmpCommunity(snmpParams.getSnmpCommunity());
	reqParams.setSnmpVersion(snmpParams.getSnmpVersion());
	reqParams.setValue(snmpParams.getValue());
	return reqParams;
    }

}
