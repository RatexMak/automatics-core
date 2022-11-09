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
package com.automatics.snmp.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.SnmpConstants;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.providers.snmp.AbstractSnmpProvider;
import com.automatics.providers.snmp.SnmpDataProvider;
import com.automatics.snmp.SnmpCommand;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpSecurityDetails;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;

/**
 * Class that handles the SNMP v3 commands
 * 
 * @author malu.s, Radhika
 * 
 */
public class Snmpv3ProviderImpl extends AbstractSnmpProvider {

    /** SLF4J logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Snmpv3ProviderImpl.class);

    private DeviceConnectionProvider connectionProvider;

    private SnmpDataProvider snmpDataProvider;

    public Snmpv3ProviderImpl() {
	connectionProvider = BeanUtils.getDeviceConnetionProvider();
	snmpDataProvider = BeanUtils.getSnmpDataProvider();
    }

    /**
     * Perform snmp get operation
     */
    public String doGet(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, AutomaticsConstants.LINUX_CMD_TEN_SECONDS_WAIT_TIME);
	return execute(dut, snmpParams);
    }

    /**
     * Perform snmp set operation
     */
    public String doSet(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, AutomaticsConstants.LINUX_CMD_TEN_SECONDS_WAIT_TIME);
	return execute(dut, snmpParams);
    }

    /**
     * Perform snmp walk operation
     */
    public String doWalk(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, AutomaticsConstants.LINUX_CMD_TEN_SECONDS_WAIT_TIME);
	return execute(dut, snmpParams);
    }

    /**
     * Perform doTable via snmp walk operation
     */
    public String doTable(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, SnmpConstants.SNMP_COMMAND_TIMEOUT_PARAMTER_V3);
	return execute(dut, snmpParams);
    }

    /*
     * Perform snmp bulk walk operation
     */
    public String doBulkWalk(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, AutomaticsConstants.SNMP_WAIT_TIME_THIRTY_SECONDS);
	return execute(dut, snmpParams);
    }

    /*
     * Perform snmp bulk get operation
     */
    public String doBulkGet(Dut dut, SnmpParams snmpParams) {
	// Add additional option
	appendOption(snmpParams, AutomaticsConstants.SNMP_WAIT_TIME_THIRTY_SECONDS);
	return execute(dut, snmpParams);
    }

    /**
     * Get the default parameters required for SNMPv3
     * 
     * @param security
     * @param timeOutOption
     * 
     * @return
     */
    private String createFormattedCommand(SnmpSecurityDetails security, SnmpParams snmpParams) {
	StringBuilder command = new StringBuilder();

	command.append(snmpParams.getSnmpCommand()).append(AutomaticsConstants.SPACE)
		.append(SnmpConstants.SNMP_V3_COMMAND_OPTION).append(AutomaticsConstants.SPACE).append(" -u ")
		.append(security.getSecurityName()).append(AutomaticsConstants.SPACE)
		.append(security.getSecurityOptions()).append(" -l ").append(security.getSecurityLevel().getValue())
		.append(AutomaticsConstants.SPACE).append(snmpParams.getCommandOption())
		.append(AutomaticsConstants.SPACE)
		.append(super.formatHostIpAddress(snmpParams.getIpAddress(), security))
		.append(AutomaticsConstants.SPACE).append(snmpParams.getMibOid());

	if (SnmpCommand.SET.equals(snmpParams.getSnmpCommand()) && !snmpParams.isMultiOid()) {
	    command.append(AutomaticsConstants.SPACE).append(snmpParams.getDataType()).append(AutomaticsConstants.SPACE)
		    .append(snmpParams.getValue());
	}

	return command.toString();
    }

    /**
     * Executes snmp command
     * 
     * @param dut
     * @param snmpParams
     * @return Execution response
     */
    public String execute(Dut dut, SnmpParams snmpParams) {
	String snmpCommand = null;
	String result = null;
	int retry = 2;

	appendOption(snmpParams, SnmpConstants.SNMP_COMMAND_OPTIONS);
	// Get snmp data from partner
	snmpParams = snmpDataProvider.updateSnmpParams(dut, snmpParams);
	SnmpSecurityDetails security = snmpDataProvider.getSnmpAuthorization(dut, snmpParams);

	do {

	    snmpCommand = createFormattedCommand(security, snmpParams);

	    List<String> commands = new ArrayList<String>();
	    commands.add(snmpCommand);

	    String rawResult = connectionProvider.execute((Device) dut, ExecuteCommandType.SNMP_COMMAND, commands);
	    if (retryRequiredOnAuthKeyExpiry(rawResult)) {
		LOGGER.debug("Fetch new keys as existing authentication key has expired");
		snmpParams.setGenerateNewKey(true);
		security = snmpDataProvider.getSnmpAuthorization(dut, snmpParams);
	    } else {
		if (snmpCommand.contains(SnmpCommand.GET.getName()) || snmpCommand.contains(SnmpCommand.SET.getName())) {
		    result = parseSnmpOutput(rawResult);
		} else {
		    result = rawResult;
		}
		break;
	    }
	    retry--;
	} while (retry == 1);
	return result;
    }

    /**
     * Method to check if auth key refetch is required
     * 
     * @param result
     *            snmp output
     * @return true if authentication key is expired and requires new key; otherwise false
     */
    private boolean retryRequiredOnAuthKeyExpiry(String result) {
	boolean isRetryRequired = false;
	if (CommonMethods.isNotNull(result) && result.contains(SnmpConstants.ERROR_STRING_INVALID_AUTH_KEY)) {
	    isRetryRequired = true;
	}
	return isRetryRequired;
    }

}
