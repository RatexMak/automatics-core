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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.dataobjects.AVFreezeRegion;
import com.automatics.dataobjects.AVMonitorData;
import com.automatics.device.Dut;
import com.automatics.enums.AVMonitorStatus;
import com.automatics.enums.AVState;
import com.automatics.enums.VideoMetrics;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;
import com.automatics.restclient.RestClientException;
import com.automatics.restclient.RestEasyClientImpl;
import com.automatics.restclient.RestRequest;
import com.automatics.restclient.RestResponse;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.TestUtils;

/**
 * 
 * AutomaticsAVAnalyzeProviderImpl
 */
public class AutomaticsAVAnalysisProviderImpl implements AVAnalysisProvider {

    /** SLF4J logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsAVAnalysisProviderImpl.class);

    private static final int CONNECTION_TIMEOUT = 60000;

    private static String START_AV_MONITOR = "/avAnalyzer/startAVMonitor";
    private static String STOP_AV_MONITOR = "/avAnalyzer/stopAVMonitor";
    private static String GET_AV_FREEZE = "/avAnalyzer/getAVFreezeRegions";
    private static String GET_AUDIO_DB_LEVEL = "/avAnalyzer/getAudioDbValue";
    private static String SUBMIT_VIDEO_QUALITY_CHECK = "/avAnalyzer/submitVideoAnalyzerJob";
    private static String GET_VIDEO_QUALITY_JOB_STATUS = "/avAnalyzer/getVideoAnalyzerJobStatus";
    private static String GET_VIDEO_QUALITY_METRICS_VALUE = "/avAnalyzer/getVideoQualityMetrics";
    private static String START_VIDEO_CAPTURE = "/avAnalyzer/startVideoCapture";
    private static String STOP_VIDEO_CAPTURE = "/avAnalyzer/stopVideoCapture";
    private static String BASE_URL;

    /**
     * Constructor for
     */
    public AutomaticsAVAnalysisProviderImpl() {
	BASE_URL = TestUtils.getAVAnalyzerUrl();
	if (CommonMethods.isNull(BASE_URL)) {
	    LOGGER.error("Base url configured for AV Analyzer is empty or null");
	}
    }

    /**
     * Start AV Monitoring
     */
    @Override
    public AVMonitorData startAVMonitor(Dut dut, String monitorTaskName, long durationInMinutes) {

	AVMonitorData monitorData = new AVMonitorData();
	AVMonitorStatus avMonitorStatus = AVMonitorStatus.START_FAILED;
	Map<String, String> headers = new HashMap<String, String>();
	// Setting request body

	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("mac", dut.getHostMacAddress());
	    if (CommonMethods.isNotNull(monitorTaskName)) {
		jsonObject.put("monitorTaskName", monitorTaskName);
	    }
	    if (durationInMinutes > 0) {
		jsonObject.put("durationInMins", durationInMinutes);
	    }

	    String url = BASE_URL + START_AV_MONITOR;
	    LOGGER.info("Sending request to start AV Monitoring: {} \n {}", url, jsonObject);
	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, headers);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		LOGGER.info("Response code when starting AV Freeze region: {} \n", response.getResponseCode());
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    avMonitorStatus = AVMonitorStatus.STARTED;
		    String data = response.getResponseBody();
		    LOGGER.info(data);
		    if (CommonMethods.isNotNull(data)) {
			JSONObject obj = new JSONObject(data);
			monitorData.setMonitorName((String) obj.get("monitorTaskName"));
			// Optional fields
			monitorData.setAvMonitorAnalyzeUrl(obj.getString("avAnalyzeUrl"));
			monitorData.setAvMonitorReportUrl(obj.getString("avReportUrl"));
		    }
		} else if (response.getResponseCode() == HttpStatus.SC_CONFLICT) {
		    avMonitorStatus = AVMonitorStatus.ALREADY_RUNNING;
		    LOGGER.info("AV Monitoring for freeze check already running for device: {} ",
			    dut.getHostMacAddress());
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request for AV Monitor", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for AV Monitor", e);
	}

	LOGGER.info("Is AV Monitoring started for freeze check: {} ", avMonitorStatus);
	monitorData.setStatus(avMonitorStatus);
	return monitorData;

    }

    /**
     * Get AV freeze regions
     */
    @Override
    public List<AVFreezeRegion> getAVFreezeRegions(Dut dut, AVState avState, String avMonitorName, long durationInSecs,
	    long startTime, long endTime) {

	List<AVFreezeRegion> freezeRegionList = null;
	// Setting request body

	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("mac", dut.getHostMacAddress());
	    jsonObject.put("monitorTaskName", avMonitorName);
	    jsonObject.put("durationInSecs", durationInSecs);
	    jsonObject.put("startTime", startTime);
	    jsonObject.put("endTime", endTime);
	    jsonObject.put("avState", avState.getState());

	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + GET_AV_FREEZE;
	    LOGGER.info("Sending request to get AV freeze: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		LOGGER.info("Response code when getting AV Freeze region: {} \n", response.getResponseCode());
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String data = response.getResponseBody();
		    LOGGER.info(data);
		    if (CommonMethods.isNotNull(data)) {
			JSONObject obj = new JSONObject(data);
			JSONArray jsonArray = obj.getJSONArray("freezeRegions");

			if (null != jsonArray && jsonArray.length() > AutomaticsConstants.CONSTANT_0) {
			    freezeRegionList = new ArrayList<AVFreezeRegion>();
			    AVFreezeRegion region = null;

			    for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				region = new AVFreezeRegion();
				region.setDeviceName(jsonObj.getString("deviceName"));
				region.setStartTime(jsonObj.getLong("startTime"));
				region.setEndTime(jsonObj.getLong("endTime"));
				region.setDuration(jsonObj.getLong("duration"));
				freezeRegionList.add(region);
			    }
			}
		    }
		}
	    } else {
		LOGGER.error("Error getting av freeze data");
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing av freeze data", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error getting av freeze data", e);
	}
	return freezeRegionList;
    }

    /**
     * Get audio DB level value
     */
    @Override
    public List<Double> getAudioDBLevelValue(Dut dut) {
	RestEasyClientImpl restClient = new RestEasyClientImpl();
	List<Double> audioDbLevelValues = new ArrayList<Double>();
	String url = BASE_URL + GET_AUDIO_DB_LEVEL;

	RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	request.setMediaType(MediaType.APPLICATION_JSON_TYPE);

	try {
	    JSONObject jsonObject = new JSONObject();
	    jsonObject.put("mac", dut.getHostMacAddress());
	    request.setContent(jsonObject.toString());

	    LOGGER.info("Sending request to get audio DB values: {} \n {}", url, jsonObject);

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		LOGGER.info("Response code when getting audio DB values: {} \n", response.getResponseCode());
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String data = response.getResponseBody();
		    LOGGER.info(data);
		    if (CommonMethods.isNotNull(data)) {
			JSONArray jsonArray = new JSONArray(data);

			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObj = jsonArray.getJSONObject(i);
			    audioDbLevelValues.add(jsonObj.getDouble("avgAudioDbLevel"));
			}
		    }
		} else {
		    LOGGER.error("Failed to get the audio DB level.!!!");
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Failed to get audio db level: ", e);
	} catch (RestClientException e) {
	    LOGGER.error("Failed to get audio db level: ", e);
	}
	return audioDbLevelValues;
    }

    /**
     * Stop AV monitoring
     */
    @Override
    public AVMonitorData stopAVMonitor(Dut dut, String monitorTaskName) {

	AVMonitorData data = new AVMonitorData();
	AVMonitorStatus avMonitorStatus = AVMonitorStatus.STOP_FAILED;
	Map<String, String> headers = new HashMap<String, String>();
	// Setting request body

	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("mac", dut.getHostMacAddress());
	    if (CommonMethods.isNotNull(monitorTaskName)) {
		jsonObject.put("monitorTaskName", monitorTaskName);
	    }

	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + STOP_AV_MONITOR;
	    LOGGER.info("Sending request to stop AV monitoring: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, headers);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		LOGGER.info("Response code when stopping AV monitoring: {} \n", response.getResponseCode());
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    avMonitorStatus = AVMonitorStatus.STOPPED;
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response for AV Monitor stop", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for AV Monitor stop", e);
	}

	LOGGER.info("Is AV Monitoring stopped for freeze check: {} ", avMonitorStatus);
	data.setStatus(avMonitorStatus);
	return data;
    }

    /**
     * Submit video analyzer job
     */
    @Override
    public String submitVideoAnalyzerJob(String jobName, String refVideoUrl, String testVideoUrl) {
	String taskId = null;

	// Setting request body
	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("jobName", jobName);
	    jsonObject.put("refVideoUrl", refVideoUrl);
	    jsonObject.put("testVideoUrl", testVideoUrl);

	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + SUBMIT_VIDEO_QUALITY_CHECK;
	    LOGGER.info("Sending request to start video quality compare job: {} \n {}", url, jsonObject);
	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String respData = response.getResponseBody();
		    if (CommonMethods.isNotNull(respData)) {
			JSONObject jsonData = new JSONObject(respData);
			taskId = jsonData.getString("taskId");
		    }
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response for Video quality check", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for Video quality check", e);
	}

	return taskId;
    }

    /**
     * Gets video analyzer job status
     */
    @Override
    public String getVideoAnalyzerJobStatus(String jobName, String taskId) {
	String taskStatus = null;

	// Setting request body
	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("jobName", jobName);
	    jsonObject.put("taskId", taskId);

	    RestEasyClientImpl restClient = new RestEasyClientImpl();
	    String url = BASE_URL + GET_VIDEO_QUALITY_JOB_STATUS;
	    LOGGER.info("Sending request to get video quality compare job status: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String respData = response.getResponseBody();
		    if (CommonMethods.isNotNull(respData)) {
			JSONObject jsonData = new JSONObject(respData);
			LOGGER.info(jsonData.toString());
			taskStatus = jsonData.getString("status");
		    }
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response for Video quality check", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for Video quality check", e);
	}

	return taskStatus;

    }

    /**
     * Gets video quality mean metric value
     */
    @Override
    public double getVideoQualityMetricMeanValue(String jobName, String taskId, VideoMetrics metric) {
	double metricValue = 0;

	// Setting request body
	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("videoMetric", metric.name());
	    jsonObject.put("taskId", taskId);
	    jsonObject.put("jobName", jobName);

	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + GET_VIDEO_QUALITY_METRICS_VALUE;
	    LOGGER.info("Sending request to get video quality metric value: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String respData = response.getResponseBody();
		    if (CommonMethods.isNotNull(respData)) {
			JSONObject jsonData = new JSONObject(respData);
			metricValue = jsonData.getDouble("metricValue");
		    }
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response for Video quality check", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for Video quality check", e);
	}

	return metricValue;

    }

    /**
     * Starts capturing video
     */
    @Override
    public boolean startMonitorVideoCapture(Dut dut, String jobName) {
	boolean isCapturingStarted = false;

	// Setting request body
	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("mac", dut.getHostMacAddress());
	    jsonObject.put("jobName", jobName);
	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + START_VIDEO_CAPTURE;
	    LOGGER.info("Sending request to start video capturing: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String respData = response.getResponseBody();
		    if (CommonMethods.isNotNull(respData)) {
			JSONObject jsonData = new JSONObject(respData);
			String taskStatus = jsonData.getString("status");
			LOGGER.info("Video capturing start status: {}", taskStatus);
			if ("STARTED".equals(taskStatus)) {
			    isCapturingStarted = true;
			}

		    }
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response while starting video capturing", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request while starting video capturing", e);
	}

	return isCapturingStarted;

    }

    /**
     * Stops capturing video
     */
    @Override
    public String stopMonitorVideoCapture(Dut dut, String jobName) {
	String videoCaptureUrl = null;

	// Setting request body
	JSONObject jsonObject = new JSONObject();
	try {
	    jsonObject.put("mac", dut.getHostMacAddress());
	    jsonObject.put("jobName", jobName);
	    RestEasyClientImpl restClient = new RestEasyClientImpl();

	    String url = BASE_URL + STOP_VIDEO_CAPTURE;
	    LOGGER.info("Sending request to stop video capture: {} \n {}", url, jsonObject);

	    RestRequest request = new RestRequest(url, HttpRequestMethod.POST, null);
	    request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    request.setContent(jsonObject.toString());

	    RestResponse response = restClient.executeAndGetResponse(request);

	    if (null != response) {
		if (response.getResponseCode() == HttpStatus.SC_OK) {
		    String respData = response.getResponseBody();
		    if (CommonMethods.isNotNull(respData)) {
			JSONObject jsonData = new JSONObject(respData);
			String taskStatus = jsonData.getString("status");
			LOGGER.info("Video capture stop status: {}", taskStatus);
			if ("STOPPED".equals(taskStatus)) {
			    videoCaptureUrl = jsonData.getString("videoUrl");
			}

		    }
		}
	    }
	} catch (JSONException e) {
	    LOGGER.error("Error parsing json request/response while stopping video capture", e);
	} catch (RestClientException e) {
	    LOGGER.error("Error sending request for stopping video capture", e);
	}

	return videoCaptureUrl;
    }

}
