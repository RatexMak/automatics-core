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
package com.automatics.providers.issuemanagement;

import java.util.List;

import com.automatics.device.Device;
import com.automatics.enums.IssueCreationRequestor;
import com.automatics.providers.crashanalysis.CrashDetails;

/**
 * Provider for automated ticket creation
 * 
 * @author Radhika
 *
 */
public interface IssueManagementProvider {

    /**
     * Get log urls to be attached in ticket.
     * 
     * @param requestor
     *            Issue Requestor
     * @param logBaseFolder
     *            Base folder where execution logs are available
     * @param device
     *            Device
     * @param crashDetails
     *            Crash Details
     * @return Log urls to be attached in ticket.
     */
    public List<String> getUrlsForAttachment(IssueCreationRequestor requestor, String logBaseFolder, Device device,
	    CrashDetails crashDetails);

    /**
     * Returns the device details to be included in automated ticket creation.
     * 
     * @param requestor
     *            Issue Requestor
     * @param device
     *            Device
     * @return Device details to be included in automated ticket creation
     */
    public String getDeviceDetails(IssueCreationRequestor requestor, Device device);

    /**
     * Gets the labels to be included in automated ticket creation.
     * 
     * @param requestor
     *            Issue Requestor
     * @param device
     *            Device
     * @param issueType
     *            IssueType
     * @return Device details to be included in automated ticket creation
     */
    public List<String> getLabels(IssueCreationRequestor requestor, Device device, CrashDetails crashDetails);

    /**
     * Get watcher list for ticket.
     * 
     * @param requestor
     *            Issue requestor
     * @param device
     *            Device
     * @param crashDetails
     *            CrashDetails
     * @return Watcher list
     */
    public List<String> getWatcherList(IssueCreationRequestor requestor, Device device, CrashDetails crashDetails);

    /**
     * Get search list for ticket.
     * 
     * @param requestor
     *            Issue requestor
     * @param device
     *            Device
     * @param crashDetails
     *            CrashDetails
     * @return Watcher list
     */
    public List<String> getSearchList(IssueCreationRequestor requestor, Device device, CrashDetails crashDetails);

}
