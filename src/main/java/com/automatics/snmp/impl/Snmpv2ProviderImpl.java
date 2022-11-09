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
 * Class that handles the SNMPv2 commands
 * 
 * @author malu.s
 * 
 */
public class Snmpv2ProviderImpl extends AbstractSnmpProvider {

    private DeviceConnectionProvider connectionProvider;

    private SnmpDataProvider snmpDataProvider;

    public Snmpv2ProviderImpl() {
	connectionProvider = BeanUtils.getDeviceConnetionProvider();
	snmpDataProvider = BeanUtils.getSnmpDataProvider();
    }

    /**
     * Performs snmpget
     */
    @Override
    public String doGet(Dut dut, SnmpParams snmpParams) {

	String snmpCommand = getFormattedCommand(dut, snmpParams);
	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Performs snmpset
     */
    @Override
    public String doSet(Dut dut, SnmpParams snmpParams) {

	appendOption(snmpParams, AutomaticsConstants.LINUX_CMD_TEN_SECONDS_WAIT_TIME);
	String snmpCommand = getFormattedCommand(dut, snmpParams);

	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Performs snmpwalk
     */
    @Override
    public String doWalk(Dut dut, SnmpParams snmpParams) {

	String snmpCommand = getFormattedCommand(dut, snmpParams);
	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Performs snmpwalk
     */
    @Override
    public String doTable(Dut dut, SnmpParams snmpParams) {

	String snmpCommand = getFormattedCommand(dut, snmpParams);
	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Performs snmp bulk walk
     */
    @Override
    public String doBulkWalk(Dut dut, SnmpParams snmpParams) {

	String snmpCommand = getFormattedCommand(dut, snmpParams);
	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Performs snmp bulk get
     */
    @Override
    public String doBulkGet(Dut dut, SnmpParams snmpParams) {

	String snmpCommand = getFormattedCommand(dut, snmpParams);
	return execute(dut, snmpCommand, snmpParams);
    }

    /**
     * Gets formatted command
     * 
     * @param dut
     *            device
     * @param snmpParams
     *            Snmp params
     * @return Returns formatted command
     */
    private String getFormattedCommand(Dut dut, SnmpParams snmpParams) {
	StringBuilder command = new StringBuilder();
	appendOption(snmpParams, SnmpConstants.SNMP_COMMAND_OPTIONS);

	// Get updated params data; includes ip address for snmp command execution
	snmpParams = snmpDataProvider.updateSnmpParams(dut, snmpParams);

	// Get authentication data for snmp command execution
	SnmpSecurityDetails securityDetails = snmpDataProvider.getSnmpAuthorization(dut, snmpParams);

	String community = snmpParams.getSnmpCommunity();
	if (null != securityDetails && CommonMethods.isNotNull(securityDetails.getCommunity())) {
	    community = securityDetails.getCommunity();
	}

	command.append(snmpParams.getSnmpCommand()).append(AutomaticsConstants.SPACE)
		.append(SnmpConstants.SNMP_V2_COMMAND_OPTION).append(AutomaticsConstants.SPACE).append(" -c ")
		.append(community).append(AutomaticsConstants.SPACE).append(snmpParams.getCommandOption())
		.append(AutomaticsConstants.SPACE)
		.append(super.formatHostIpAddress(snmpParams.getIpAddress(), securityDetails))
		.append(AutomaticsConstants.SPACE).append(snmpParams.getMibOid());

	/*
	 * In case of Multiple SNMP parameter set on single command, we only form mibOid but not data type and values
	 * fields in SnmpParams. Ignore the duplicate value set here.
	 */
	if (SnmpCommand.SET.equals(snmpParams.getSnmpCommand()) && !snmpParams.isMultiOid()) {
	    command.append(AutomaticsConstants.SPACE).append(snmpParams.getDataType()).append(AutomaticsConstants.SPACE)
		    .append(snmpParams.getValue());
	}
	return command.toString();
    }

    /**
     * Executes the snmp command
     * 
     * @param dut
     *            Device
     * @param snmpCommand
     *            Snmp Command
     * @return Execution response
     */
    private String execute(Dut dut, String snmpCommand, SnmpParams snmpParams) {
	List<String> commands = new ArrayList<String>();
	commands.add(snmpCommand);
	String response = connectionProvider.execute((Device) dut, ExecuteCommandType.SNMP_COMMAND, commands);

	if (!snmpParams.isMultiOid() && (snmpParams.getSnmpCommand().equals(SnmpCommand.GET)
		|| snmpParams.getSnmpCommand().equals(SnmpCommand.SET))) {
	    return parseSnmpOutput(response);
	}
	return response;
    }
}
