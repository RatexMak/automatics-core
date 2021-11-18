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
package com.automatics.providers.objects;

import com.automatics.providers.objects.enums.DeviceAllocationStatus;

/**
 * DeviceAllocationResponse class
 * 
 * 
 * @author Raja M
 */

public class DeviceAllocationResponse {

    private String allocationId;

    private DeviceAllocationStatus allocationStatus;

    private String userName;

    private String start;

    private String end;

    private String lastModifiedDate;

    /**
     * @return the allocationId
     */
    public String getAllocationId() {
	return allocationId;
    }

    /**
     * @param allocationId
     *            the allocationId to set
     */
    public void setAllocationId(String allocationId) {
	this.allocationId = allocationId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * @return the start
     */
    public String getStart() {
	return start;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(String start) {
	this.start = start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
	return end;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(String end) {
	this.end = end;
    }

    /**
     * @return the lastModifiedDate
     */
    public String getLastModifiedDate() {
	return lastModifiedDate;
    }

    /**
     * @param lastModifiedDate
     *            the lastModifiedDate to set
     */
    public void setLastModifiedDate(String lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * @return the allocationStatus
     */
    public DeviceAllocationStatus getAllocationStatus() {
	return allocationStatus;
    }

    /**
     * @param allocationStatus
     *            the allocationStatus to set
     */
    public void setAllocationStatus(DeviceAllocationStatus allocationStatus) {
	this.allocationStatus = allocationStatus;
    }

}
