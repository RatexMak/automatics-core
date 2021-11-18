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
package com.automatics.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.dataobjects.AVFreezeRegion;
import com.automatics.dataobjects.AVMonitorData;
import com.automatics.device.Dut;
import com.automatics.enums.AVState;
import com.automatics.enums.VideoMetrics;
import com.automatics.providers.avanlyzer.AVAnalysisProvider;

/**
 * AVMonitoringUtils
 *
 */
public class AVMonitoringUtils {

    /** SLF4J logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AVMonitoringUtils.class);

    private static AVAnalysisProvider avAnalysisProvider;

    static {
	avAnalysisProvider = BeanUtils.getAVAnalysisProvider();
    }

    public static final AVMonitorData startAVMonitor(Dut dut, String jobName, long durationInMinutes) {
	return avAnalysisProvider.startAVMonitor(dut, jobName, durationInMinutes);

    }

    public static final AVMonitorData stopAVFMonitor(Dut dut, String jobName) {
	return avAnalysisProvider.stopAVMonitor(dut, jobName);

    }

    public static final List<AVFreezeRegion> getAVFreezeRegions(Dut dut, AVState avStatus, String avMonitorTaskName,
	    long durationInSecs, long startTime, long endTime) {

	return avAnalysisProvider.getAVFreezeRegions(dut, avStatus, avMonitorTaskName, durationInSecs, startTime,
		endTime);
    }

    public static final List<Double> getAudioDBLevelValue(Dut dut) {
	return avAnalysisProvider.getAudioDBLevelValue(dut);
    }

    public static void appendFreezeRegionToList(Dut dut, long startTime, long endTime, long freezeDuration,
	    String avMonitorTaskName, List<AVFreezeRegion> finalVideoFreezeRegionList,
	    List<AVFreezeRegion> finalAudioFreezeRegionList) {
	List<AVFreezeRegion> videoFreezeRegion = avAnalysisProvider.getAVFreezeRegions(dut, AVState.NO_MOTION,
		avMonitorTaskName, freezeDuration, startTime, endTime);
	List<AVFreezeRegion> audioFreezeRegion = avAnalysisProvider.getAVFreezeRegions(dut, AVState.NO_AUDIO,
		avMonitorTaskName, freezeDuration, startTime, endTime);
	if (null != videoFreezeRegion && !videoFreezeRegion.isEmpty()) {
	    finalVideoFreezeRegionList.addAll(videoFreezeRegion);
	} else {
	    LOGGER.info("Couldn't find any video freeze regions");
	}
	if (null != audioFreezeRegion && !audioFreezeRegion.isEmpty()) {
	    finalAudioFreezeRegionList.addAll(audioFreezeRegion);
	} else {
	    LOGGER.info("Couldn't find any video freeze regions");
	}

    }

    public static boolean startMonitorVideoCapture(Dut dut, String jobName) {
	return avAnalysisProvider.startMonitorVideoCapture(dut, jobName);
    }

    public static String stopMonitorVideoCapture(Dut dut, String jobName) {
	return avAnalysisProvider.stopMonitorVideoCapture(dut, jobName);
    }

    public static String submitVideoAnalyzerJob(String jobName, String refVideoUrl, String testVideoUrl) {
	return avAnalysisProvider.submitVideoAnalyzerJob(jobName, refVideoUrl, testVideoUrl);
    }

    public static String getVideoAnalyzerJobStatus(String executionName, String taskId) {
	return avAnalysisProvider.getVideoAnalyzerJobStatus(executionName, taskId);
    }

    public static double getVideoQualityMetricMeanValue(String taskId, String jobName, VideoMetrics metric) {
	return avAnalysisProvider.getVideoQualityMetricMeanValue(taskId, jobName, metric);
    }

}
