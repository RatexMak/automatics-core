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
 * Enumeration for Snmp Data type.
 *
 * @author selvarajm
 */
public enum SnmpDataType {

    /** Data type - string. */
    STRING("s"),

    /** Data type - integer. */
    INTEGER("i"),

    /** Data type - unsigned integer. */
    UNSIGNED_INTEGER("u"),

    /** Date Type - string a */
    STRING_A("a"),

    /** Date Type - Hexadecimal x */
    HEXADECIMAL("x");

    /** Data type. */
    private String type;

    /**
     * Constructor.
     *
     * @param type
     *            The data type.
     */
    private SnmpDataType(String type) {
	this.type = type;
    }

    /**
     * Get the data type.
     *
     * @return
     */
    public String getType() {
	return this.type;
    }

    @Override
    public String toString() {
	return this.type;
    }
}
