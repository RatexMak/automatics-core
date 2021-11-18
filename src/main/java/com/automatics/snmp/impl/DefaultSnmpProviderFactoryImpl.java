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

import com.automatics.providers.snmp.SnmpProvider;
import com.automatics.providers.snmp.SnmpProviderFactory;
import com.automatics.snmp.SnmpProtocol;
import com.automatics.utils.AutomaticsSnmpUtils;

/**
 * Default Factory implementation for SnmpProviderFactory. Creates instance of SnmpProvider based on snmp version or
 * build appender.
 * 
 * @author Radhika
 *
 */
public class DefaultSnmpProviderFactoryImpl implements SnmpProviderFactory {

    /**
     * Gets default implementation of snmp provider. snmpv2
     */
    @Override
    public SnmpProvider getSnmpProvider() {
	SnmpProvider snmpProvider = new Snmpv2ProviderImpl();
	return snmpProvider;
    }

    /**
     * Gets implementation of snmp provider based on snmp version or build appender.
     */
    @Override
    public SnmpProvider getSnmpProvider(SnmpProtocol snmpVersion) {
	SnmpProvider snmpProvider = null;

	if (AutomaticsSnmpUtils.isSnmpV3EnabledUsingAppender()) {
	    snmpVersion = SnmpProtocol.SNMP_V3;
	}

	if (null == snmpVersion) {
	    return new Snmpv2ProviderImpl();
	}
	switch (snmpVersion) {
	case SNMP_V2:
	    snmpProvider = new Snmpv2ProviderImpl();
	    break;
	case SNMP_V3:
	    snmpProvider = new Snmpv3ProviderImpl();
	    break;
	default:
	    snmpProvider = new Snmpv2ProviderImpl();
	    break;
	}
	return snmpProvider;
    }

}
