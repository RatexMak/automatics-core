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

package com.automatics.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.SnmpConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Dut;
import com.automatics.resource.IServer;
import com.automatics.snmp.SnmpProtocol;
import com.automatics.tap.AutomaticsTapApi;

/**
 * Class to get VM details where Snmp execution can be hosted
 * 
 * @author selvarajm
 * @author Arjun P
 */
public class SnmpHostVM implements IServer {

    /** SLF4J logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpHostVM.class);
    private static SnmpHostVM hostVm;

    private String hostIp;
    private String password;
    private String userId;

    private SnmpHostVM(AutomaticsTapApi eCatsTap, Dut dut) {
	String snmpVersion = System.getProperty(SnmpConstants.SYSTEM_PARAM_SNMP_VERSION,
		SnmpProtocol.SNMP_V2.toString());
	snmpVersion = snmpVersion.trim();
	boolean useSnmpV3Host = false;
	// if its an RDK-B device u have to use the SNMP V3 host vm for all snmp operations
	if (null != dut && SupportedModelHandler.isRDKB(dut)) {
	    LOGGER.info(" SnmpHostVM : SnmpHostVMDevice is RDK-B");
	    useSnmpV3Host = true;
	}
	if (useSnmpV3Host || SnmpProtocol.SNMP_V3.toString().equalsIgnoreCase(snmpVersion)) {
	    LOGGER.info(" SnmpHostVM : Using SNMPV3 host even though SNMP Version is " + snmpVersion);
	    hostIp = AutomaticsTapApi.getSTBPropsValue("snmp.v3.host.vm.ip");
	    password = AutomaticsTapApi.getSTBPropsValue("snmp.v3.host.vm.password");
	    userId = AutomaticsTapApi.getSTBPropsValue("snmp.v3.host.vm.id");
	} else {
	    hostIp = AutomaticsTapApi.getSTBPropsValue("snmp.host.vm.ip");
	    password = AutomaticsTapApi.getSTBPropsValue("snmp.host.vm.password");
	    userId = AutomaticsTapApi.getSTBPropsValue("snmp.host.vm.id");
	}
	LOGGER.info("SNMP Host VM IP : " + hostIp);
    }

    public static SnmpHostVM getInstance(AutomaticsTapApi eCatsTap) {

	if (null == hostVm) {
	    hostVm = new SnmpHostVM(eCatsTap, null);
	}

	return hostVm;
    }

    public static SnmpHostVM getInstance(AutomaticsTapApi eCatsTap, Dut dut) {

	if (null == hostVm) {
	    hostVm = new SnmpHostVM(eCatsTap, dut);
	}

	return hostVm;
    }

    @Override
    public String getHostIp() {
	return hostIp;
    }

    @Override
    public String getPassword() {
	return password;
    }

    @Override
    public String getUserId() {
	return userId;
    }

}
