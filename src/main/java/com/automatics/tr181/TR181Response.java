/**
 * Copyright 2022 Comcast Cable Communications Management, LLC
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

public class TR181Response {

    private String tableRowNameWithIndex;

    private String tableRowIndex;

    private String response;

    /**
     * @return the tableRowIndex
     */
    public String getTableRowIndex() {
	return tableRowIndex;
    }

    /**
     * @param tableRowIndex
     *            the tableRowIndex to set
     */
    public void setTableRowIndex(String tableRowIndex) {
	this.tableRowIndex = tableRowIndex;
    }

    /**
     * @return the response
     */
    public String getResponse() {
	return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(String response) {
	this.response = response;
    }

    /**
     * @return the tableRowNameWithIndex
     */
    public String getTableRowNameWithIndex() {
	return tableRowNameWithIndex;
    }

    /**
     * @param tableRowNameWithIndex
     *            the tableRowNameWithIndex to set
     */
    public void setTableRowNameWithIndex(String tableRowNameWithIndex) {
	this.tableRowNameWithIndex = tableRowNameWithIndex;
    }

    public String toString() {
	StringBuilder param = new StringBuilder();
	param.append("tableRowNameWithIndex=").append(tableRowNameWithIndex).append(", tableRowIndex=")
		.append(tableRowIndex).append(", response=").append(response);
	return param.toString();
    }

}
