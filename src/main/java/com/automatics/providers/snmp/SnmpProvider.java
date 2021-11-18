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

import com.automatics.device.Dut;
import com.automatics.snmp.SnmpParams;
import com.automatics.snmp.SnmpSecurityDetails;

/**
 * Provider for SNMP related operations
 * 
 * @author Radhika
 *
 */
public interface SnmpProvider {

    /**
     * Execute snmp walk command.
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * 
     * @param snmpParams
     *            Snmp Parameters
     * @param community
     *            The community string
     * @param mib
     *            The MIB name or OID
     * @param commandOption
     *            The command option.
     * 
     * @return {@link String} The output of snmp walk command.
     */
    public abstract String doWalk(Dut dut, SnmpParams snmpParams);

    /**
     * Execute snmp get command.
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * @param ipAddress
     *            The target address
     * @param community
     *            The community string
     * @param mib
     *            The MIB name or OID
     * @param commandOption
     *            The command option.
     * 
     * @return {@link String} The output snmp get command.
     */
    public abstract String doGet(Dut dut, SnmpParams snmpParams);

    /**
     * Execute snmp set command.
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * 
     * @param ipAddress
     *            The target address
     * @param community
     *            The community string
     * @param mib
     *            The MIB name or OID
     * @param commandOption
     *            The command option.
     * @param dataType
     *            The data type to be set.
     * @param data
     *            The data value to be set
     * 
     * @return {@link String} The output of snmp set command.
     */
    public abstract String doSet(Dut dut, SnmpParams snmpParams);    

    /**
     * Perform snmp operation
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * @param ipAddress
     * @param community
     * @param mib
     * @param commandOption
     * @return
     */
    public abstract String doTable(Dut dut, SnmpParams snmpParams);

    /**
     * Execute snmp bulk walk command.
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * @param ipAddress
     *            The target address
     * @param community
     *            The community string
     * @param mib
     *            The MIB name or OID
     * @param commandOption
     *            The command option.
     * @param string
     * @param snmpDataType
     * 
     * @return {@link String} The output of snmp walk command.
     */
    public abstract String doBulkWalk(Dut dut, SnmpParams snmpParams);

    /**
     * Execute snmp bulk get command.
     * 
     * @param dut
     *            test device on which snmp command to be executed
     * @param ipAddress
     *            The target address
     * @param community
     *            The community string
     * @param mib
     *            The MIB name or OID
     * @param commandOption
     *            The command option.
     * 
     * @return {@link String} The output snmp get command.
     */
    public abstract String doBulkGet(Dut dut, SnmpParams snmpParams);

    /**
     * Format the host ip address with snmp protocol and port. if ipv6 address, then return formated ip address for
     * using it in snmp command
     * 
     * @param ipAddress
     * @param securityDetails
     * 
     *
     * @return Formatted ip address
     */
    public String formatHostIpAddress(String ipAddress, SnmpSecurityDetails securityDetails);

    /**
     * Format the snmp output
     * 
     * @param output
     * @return Formatted snmp response
     */
    public String parseSnmpOutput(final String snmpOutput);

}
