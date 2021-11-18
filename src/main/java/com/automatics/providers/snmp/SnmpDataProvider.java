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
 * Provider for SNMP authorization
 * 
 * @author Radhika
 *
 */
public interface SnmpDataProvider {

    /**
     * Get authorization data required for snmp communication.
     * 
     * @param dut
     *            Device on snmp commands are executed
     * @param snmpParams
     *            Snmp params
     * @return Authentication data for snmp communication
     */
    public SnmpSecurityDetails getSnmpAuthorization(Dut dut, SnmpParams snmpParams);

    /**
     * Update snmp params data required for snmp communication.
     * 
     * @param dut
     *            Device on snmp commands are executed
     * @param snmpParams
     *            Snmp params
     * @return SnmpParams data for snmp communication
     */
    public SnmpParams updateSnmpParams(Dut dut, SnmpParams snmpParams);

}
