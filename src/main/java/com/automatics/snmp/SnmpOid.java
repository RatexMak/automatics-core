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
 * 
 * Representation of SNMP OID Parameters.
 * 
 * @author Selvaraj Mariyappan
 *
 */
public class SnmpOid {

    String mibOid;

    SnmpDataType dataType;

    String value;

    /**
     * @return the mibOid
     */
    public String getMibOid() {
	return mibOid;
    }

    /**
     * @param mibOid
     *            the mibOid to set
     */
    public void setMibOid(String mibOid) {
	this.mibOid = mibOid;
    }

    /**
     * @return the dataType
     */
    public SnmpDataType getDataType() {
	return dataType;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public void setDataType(SnmpDataType dataType) {
	this.dataType = dataType;
    }

    /**
     * @return the value
     */
    public String getValue() {
	return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
	this.value = value;
    }
}
