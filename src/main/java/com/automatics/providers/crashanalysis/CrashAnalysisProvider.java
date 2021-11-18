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
package com.automatics.providers.crashanalysis;

import java.util.List;

import com.automatics.core.DeviceProcess;
import com.automatics.device.Dut;

/**
 * Provider for crash analysis
 * 
 * @author Radhika
 *
 */
public interface CrashAnalysisProvider {

    /**
     * Returns if crash analysis is enabled or not. Only if this API returns true, crash analysis will be performed by
     * core.
     * 
     * @param device
     * @return true if crash analysis is enabled, otherwise false
     */
    boolean isCrashAnalysisEnabled(Dut device);    

    /**
     * Get regex for crash. This regex can be verified against device log to identify if crash has occurred during trace
     * monitoring.
     * 
     * @param device
     * @return true if crash occurred, otherwise false
     */
    String getRegexForCrashLog(Dut device);

    /**
     * Get the list of regex to verify in device logs if crash has been uploaded successfully.
     * 
     * @param device
     * @return true if crash occurred, otherwise false
     */
    List<String> getRegexForSuccessfulCrashUpload(Dut device,
	    com.automatics.providers.crashanalysis.CrashType crashType, DeviceProcess deviceProcess);

    /**
     * Send request to initiate mini dump crash log fetching.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return true, if mini dump crash log fetching started; otherwise false.
     */
    boolean sendMiniDumpProcessRequest(CrashPortalRequest request);

    /**
     * Send request to initiate core dump crash log fetching.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return true, if core dump crash log fetching started; otherwise false.
     */
    boolean sendCoreDumpProcessRequest(CrashPortalRequest request);

    /**
     * Gets the mini dump crash data.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return mini dump crash data.
     */
    List<CrashDetails> getMiniDumpData(CrashPortalRequest request);

    /**
     * Gets the core dump crash data.
     * 
     * @param request
     *            instance of CrashPortalRequest
     * @return core dump crash data.
     */
    List<CrashDetails> getCoreDumpData(CrashPortalRequest request);

    /**
     * Download the mini dump or core dump crash data to specified location.
     * 
     * @param crashType
     * @param crashId
     * @param dumpDownloadLocation
     * @return true if dump file has been successfully downloaded to location
     */
    boolean downloadDump(CrashType crashType, String crashId, String dumpDownloadLocation);

    /**
     * Download the mini dump or core dump crash compressed data files to specified location.
     * 
     * @param crashType
     * @param crashId
     * @param dumpDownloadLocation
     * @return true if dump file has been successfully downloaded to location
     */
    boolean downloadCompressedDumpFiles(CrashType crashType, String crashId, String dumpDownloadLocation);

    /**
     * Verifies if crash file is available in crash portal
     * 
     * @param crashType
     * @param fileName
     * @return true if crash file is available in crash portal; otherwise false.
     */
    boolean isCrashFileAvailableInCrashPortal(CrashType crashType, String fileName);

    /**
     * Gets crash details data for the given crash file
     * 
     * @param crashType
     * @param fileName
     * @return CrashDetails data.
     */
    CrashDetails getDumpDataForGivenCrashFile(CrashType crashType, String fileName);

}
