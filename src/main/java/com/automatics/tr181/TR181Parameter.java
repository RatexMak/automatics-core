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
package com.automatics.tr181;

import com.automatics.enums.TR181DataType;

/**
 * 
 * Defines TR181 parameter
 *
 */
public class TR181Parameter {

    protected String name;

    protected String tableName;

    protected int statusCode;

    protected String value;

    protected String index;

    protected TR181DataType tr181DataType;

    protected String protocolSpecificParamName;

    protected String protocolSpecificDataType;

    protected boolean isWritable;

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }


    public int getStatusCode(){
       return this.statusCode ;
    } 
    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
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

    /**
     * @return the isWritable
     */
    public boolean isWritable() {
	return isWritable;
    }

    /**
     * @param isWritable
     *            the isWritable to set
     */
    public void setWritable(boolean isWritable) {
	this.isWritable = isWritable;
    }

    /**
     * @return the tr181DataType
     */
    public TR181DataType getTr181DataType() {
	return tr181DataType;
    }

    /**
     * @param tr181DataType
     *            the tr181DataType to set
     */
    public void setTr181DataType(TR181DataType tr181DataType) {
	this.tr181DataType = tr181DataType;
    }

    /**
     * @return the protocolSpecificDataType
     */
    public String getProtocolSpecificDataType() {
	return protocolSpecificDataType;
    }

    /**
     * @param protocolSpecificDataType
     *            the protocolSpecificDataType to set
     */
    public void setProtocolSpecificDataType(String protocolSpecificDataType) {
	this.protocolSpecificDataType = protocolSpecificDataType;
    }

    /**
     * @return the protocolSpecificParamName
     */
    public String getProtocolSpecificParamName() {
	return protocolSpecificParamName;
    }

    /**
     * @param protocolSpecificParamName
     *            the protocolSpecificParamName to set
     */
    public void setProtocolSpecificParamName(String protocolSpecificParamName) {
	this.protocolSpecificParamName = protocolSpecificParamName;
    }

    /**
     * @return the index
     */
    public String getIndex() {
	return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(String index) {
	this.index = index;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
	return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    public String toString() {
	StringBuilder param = new StringBuilder();
	param.append("tableName=").append(tableName).append(", index=").append(index).append(", name=").append(name)
		.append(", protocolSpecificParamName=").append(protocolSpecificParamName).append(", value=")
		.append(value).append(", datatype=").append(tr181DataType).append(", statusCode=").append(statusCode);
	return param.toString();
    }

}
