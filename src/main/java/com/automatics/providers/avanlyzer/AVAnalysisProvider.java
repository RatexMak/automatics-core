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
package com.automatics.providers.avanlyzer;

import java.util.List;

import com.automatics.dataobjects.AVFreezeRegion;
import com.automatics.dataobjects.AVMonitorData;
import com.automatics.device.Dut;
import com.automatics.enums.AVState;
import com.automatics.enums.VideoMetrics;

/**
 * Provider for AV monitoring
 * 
 * @author Radhika
 *
 */
public interface AVAnalysisProvider {

    /**
     * Start AV Monitoring
     * 
     * @param dut
     * @param monitorTaskName
     * @param durationInMinutes
     * @return AVMonitorData
     */
    AVMonitorData startAVMonitor(Dut dut, String monitorTaskName, long durationInMinutes);

    /**
     * Stop AV Monitoring
     * 
     * @param dut
     * @param monitorTaskName
     * @return AVMonitorData
     */
    AVMonitorData stopAVMonitor(Dut dut, String monitorTaskName);

    /**
     * Get AV Freeze regions
     * 
     * @param dut
     * @param avStatus
     * @param avMonitorTaskName
     * @param durationInSecs
     * @param startTime
     * @param endTime
     * @return AVFreezeRegion data
     */
    List<AVFreezeRegion> getAVFreezeRegions(Dut dut, AVState avStatus, String avMonitorTaskName, long durationInSecs,
	    long startTime, long endTime);

    /**
     * Get audio DB level value
     * 
     * @param dut
     * @return Get audio DB level value
     */
    List<Double> getAudioDBLevelValue(Dut dut);

    /**
     * Submit video analyzer job
     * 
     * @param jobName
     * @param refVideoUrl
     * @param testVideoUrl
     * @return Job Id
     */
    String submitVideoAnalyzerJob(String jobName, String refVideoUrl, String testVideoUrl);

    /**
     * Gets video analyzer job status
     * 
     * @param jobName
     * @param taskId
     * @return Video analyzer job status
     */
    String getVideoAnalyzerJobStatus(String jobName, String taskId);

    /**
     * Get video quality mean metric value
     * 
     * @param taskId
     * @param jobName
     * @param metric
     * @return Video quality mean metric value
     */
    double getVideoQualityMetricMeanValue(String taskId, String jobName, VideoMetrics metric);

    /**
     * Start video capturing
     * 
     * @param dut
     * @param jobName
     * @return true if video capturing started
     */
    boolean startMonitorVideoCapture(Dut dut, String jobName);

    /**
     * Stop video capturing
     * 
     * @param dut
     * @param jobName
     * @return Video capture url
     */
    String stopMonitorVideoCapture(Dut dut, String jobName);

}
