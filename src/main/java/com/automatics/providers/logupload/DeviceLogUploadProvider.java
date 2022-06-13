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
package com.automatics.providers.logupload;

import com.automatics.device.Device;

/**
 * Provider that supports uploading device logs to Partner cloud system
 * 
 * @author radhikas
 *
 */
public interface DeviceLogUploadProvider {

    /**
     * Get the name of log file in Partner Cloud system
     * 
     * @param device
     *            Device
     * @return Get the name of log file uploaded in Partner Cloud system
     */
    public String getLogFileNameInCloud(Device device);

    /**
     * Get the complete url to access the log file in Partner Cloud system
     * 
     * @param device
     *            Device
     * @param logFileName
     *            Name of log file
     * @return Complete url of the log file
     */
    public String getLogFilePathInCloud(Device device, String logFileName);

    /**
     * Download the log file from Partner Cloud system and save it in given location
     * 
     * @param device
     * @param logFilPath
     *            Complete url of the log file
     * @param logSaveLocationDirectory
     *            Location to which logs to be saved
     * @return Return true if logs are downloaded successfully; otherwise false
     */
    public boolean downloadLogFromCloud(Device device, String logFilPath, String logSaveLocationDirectory);

}
