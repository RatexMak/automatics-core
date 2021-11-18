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
 * Enumeration for Snmp Protocols.
 *
 * @author malu.s
 */
public enum SnmpProtocol {

    /** Snmp Protocol version - v2 */
    SNMP_V2("v2"),

    /** Snmp Protocol version - v3 */
    SNMP_V3("v3")

    ;

    /** Data type. */
    private String protocolVersion;

    /**
     * Constructor.
     *
     * @param type
     *            The data type.
     */
    private SnmpProtocol(String protocolVersion) {
	this.protocolVersion = protocolVersion;
    }

    /**
     * Get the data type.
     *
     * @return
     */
    public String getProtocolVersion() {
	return this.protocolVersion;
    }

    @Override
    public String toString() {
	return this.protocolVersion;
    }
}
