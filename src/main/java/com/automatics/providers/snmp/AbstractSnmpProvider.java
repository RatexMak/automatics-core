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

package com.automatics.providers.snmp;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.SnmpConstants;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpSecurityDetails;
import com.automatics.utils.CommonMethods;

/**
 * Abstract class for handling the snmp related functionality such as snmpget, snmpset and snmpwalk.
 * 
 * @author Selvaraj Mariyappan, malu.s
 * 
 */
public abstract class AbstractSnmpProvider implements SnmpProvider {

    /**
     * Format the snmp output
     * 
     * @param output
     * @return Formatted snmp response
     */
    @Override
    public String parseSnmpOutput(final String snmpOutput) {
	String snmpResult = null;
	String[] requiredValues = snmpOutput.split("=");

	if (requiredValues.length > 0) {
	    snmpResult = requiredValues[requiredValues.length - 1];
	} else {
	    throw new FailedTransitionException(GeneralError.SNMP_COMPARISON_FAILURE,
		    "SNMP command not executed properly : snmp output is = " + snmpOutput);
	}

	return snmpResult.replaceAll("\"", "").trim();
    }

    /**
     * Get the formatted IP address for SNMP command execution.
     * 
     * @param ipAddress
     * @param securityDetails
     * 
     * @return formatted IP address for SNMP command.
     */
    @Override
    public String formatHostIpAddress(String givenAddress, SnmpSecurityDetails securityDetails) {

	String snmpPort = SnmpConstants.DEFAULT_SNMP_PORT;
	String snmpProtocol = SnmpConstants.DEFAULT_SNMP_PROTOCOL;

	if (CommonMethods.isIpv6Address(givenAddress)) {
	    snmpProtocol = SnmpConstants.DEFAULT_SNMP_V6_PROTOCOL;
	}

	if (CommonMethods.isNotNull(securityDetails.getSnmpPort())) {
	    snmpPort = securityDetails.getSnmpPort();
	}

	if (CommonMethods.isNotNull(securityDetails.getSnmpProtocol())) {
	    snmpProtocol = securityDetails.getSnmpProtocol();
	}

	String formatIpAddress = givenAddress;
	if (CommonMethods.isIpv6Address(givenAddress)) {
	    formatIpAddress = snmpProtocol + ":[" + givenAddress.trim() + "]:" + snmpPort;
	} else {
	    formatIpAddress = snmpProtocol + ":" + givenAddress.trim() + ":" + snmpPort;
	}
	return formatIpAddress;
    } 
    
    /**
     * Appends additional options
     * 
     * @param snmpParams
     *            SnmpParams
     * @param optionVal
     *            Additional option to be appended
     */
    protected void appendOption(SnmpParams snmpParams, String optionVal) {
	StringBuilder option = new StringBuilder();
	if (CommonMethods.isNotNull(snmpParams.getCommandOption())) {
	    option.append(snmpParams.getCommandOption()).append(AutomaticsConstants.SPACE);
	}
	option.append(optionVal);
	snmpParams.setCommandOption(option.toString());
    }

}
