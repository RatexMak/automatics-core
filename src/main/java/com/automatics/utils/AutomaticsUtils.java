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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.enums.JobStatusValue;
import com.automatics.enums.ServiceType;
import com.automatics.enums.TestType;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.test.AutomaticsTestBase;

public class AutomaticsUtils {

    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsUtils.class);

    public static boolean isAutomaticsUpdated = false;

    /**
     * The instance variable to holds the json message to send to the Automatics
     */
    public static String jsonToAutomatics = "";

    /** The JSON key object 'result'. */
    public static final String JSON_KEY_OBJECT_RESULT = "result";

    /** The JSON key object 'buildImageName'. */
    public static final String JSON_KEY_OBJECT_BUILD_IMAGE_NAME = "buildImageName";

    /** The JSON key object 'settopList'. */
    public static final String JSON_KEY_OBJECT_SETTOP_LIST = "settopList";

    /** The JSON key object 'startTime'. */
    public static final String JSON_KEY_OBJECT_START_TIME = "startTime";

    /** The JSON key object 'completionTime'. */
    public static final String JSON_KEY_OBJECT_COMPLETION_TIME = "completionTime";

    /** The JSON key object 'build_name'. */
    public static final String JSON_KEY_OBJECT_BUILD_NAME = "build_name";

    /** The JSON key object 'type'. */
    public static final String JSON_KEY_OBJECT_TYPE = "type";

    /** The JSON key object 'tests'. */
    public static final String JSON_KEY_OBJECT_TESTS = "tests";

    /** The JSON key object 'test_id'. */
    private static final String JSON_KEY_OBJECT_TEST_ID = "test_id";

    /** The JSON key object 'reason'. */
    private static final String JSON_KEY_OBJECT_REASON = "reason";

    /** The JSON key object 'step_number'. */
    private static final String JSON_KEY_OBJECT_STEP_NUMBER = "step";

    /** The JSON key object 'status'. */
    public static final String JSON_KEY_OBJECT_STATUS = "status";

    private static final String JSON_KEY_OBJECT_COMPONENT = "components";

    /**
     * This map holds details of device and its job status during any failures..Before sending status to Automatics,this
     * map will be checked for data
     */
    public static HashMap<String, JobStatusValue> jobStatus = new HashMap<String, JobStatusValue>();

    public enum VALIDATIONS {	
	IRKEY_VALIDATION,
	CRASH_VALIDATIONS,
	STB_VALIDATION,
	ACTIVE_SERVICE_VALIDATION,
	FAILED_SERVICE_VALIDATION,
	AV_VERIFICATION
    }

    /**
     * Update the JSON formatted test results to Automatics. .
     * 
     * @param jsonToBePassed
     *            Test execution results with JSON format.
     */
    public static void updateAutomatics() {

	Thread updatorThread = new Thread() {

	    public void run() {

		String targetUrl = null;
		JSONObject jsonObject = null;

		boolean shouldRetry = false;

		int retryCount = 0;

		do {

		    try {

			jsonObject = new JSONObject(jsonToAutomatics);

			LOGGER.info("============= MESSAGE SEND TO AUTOMATICS ==============");
			try {
			    LOGGER.info("Tested build name : " + jsonObject.get("buildImageName"));
			} catch (JSONException jsonException) {
			    LOGGER.info("Json Exception for final update - " + jsonException.getMessage());
			}

			try {
			    LOGGER.info("Final execution status : " + jsonObject.get("status"));
			} catch (JSONException jsonException) {
			    LOGGER.info("Json Exception for final update - " + jsonException.getMessage());
			}
			try {
			    LOGGER.info("Final successful device list : " + jsonObject.get("settopList"));
			} catch (JSONException jsonException) {
			    LOGGER.info("Json Exception for final update - " + jsonException.getMessage());
			}
			try {
			    LOGGER.info("Job Id : " + jsonObject.get("JMD_ID"));
			} catch (JSONException jsonException) {
			    LOGGER.info("Json Exception for final update - " + jsonException.getMessage());
			}

			LOGGER.info("==============================================================\n");
			LOGGER.info(">>>[INIT]: Automatics JSON ->\n" + jsonToAutomatics);

			targetUrl = AutomaticsTapApi.getSTBPropsValue("automatics.url") + "executionResponse.htm";

			LOGGER.info(targetUrl);
			RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10 * AutomaticsConstants.ONE_MINUTE_INT).build();
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			HttpPost post = new HttpPost(targetUrl);

			// setting json object to post request.
			HttpEntity entity = new StringEntity(jsonToAutomatics.toString());

			post.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(entity);

			// this is your response:
			HttpResponse responseFromRDK = client.execute(post);

			StatusLine statusLine = responseFromRDK.getStatusLine();
			LOGGER.info("HTTP STATUS LINE : " + statusLine);
			LOGGER.info("HTTP STATUS CODE : " + statusLine.getStatusCode());
			isAutomaticsUpdated = true;
		    } catch (ClientProtocolException cpex) {
			LOGGER.error("Client protocol exception occured ", cpex);
		    } catch (UnknownHostException unknownHostException) {
			LOGGER.error("UnknownHostException occured ", unknownHostException);
		    } catch (IOException ioex) {
			LOGGER.error("IO exception occured ", ioex);
		    } catch (Exception generalException) {
			if (generalException instanceof InterruptedException) {
			    LOGGER.error("Update Thread interrupted after 5 mins.");
			} else {
			    LOGGER.error("Generic Exception occurred during automatics update.", generalException);
			}
		    }

		    if (!isAutomaticsUpdated && retryCount < 2) {
			try {
			    LOGGER.info("Waiting for 10 seconds before retry");
			    // wait for 10 seconds before retry attempt
			    Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
			}

			retryCount++;
			shouldRetry = true;
		    } else {
			shouldRetry = false;
		    }

		} while (shouldRetry);
	    }
	};

	try {

	    updatorThread.setDaemon(true);
	    updatorThread.start();

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 5 minutes. If no response is
	     * obtained, continue the below lines of code. If this thread is not used, in case of test manager
	     * accessibility or hang the system will not get exit
	     */
	    updatorThread.join(300000);

	    // Interrupt the thread.
	    updatorThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace(" Update Opertaion Interrupted as Automatics accessibility is having issue.");
	}
    }

    /**
     * Create JSON response with no box available status Special case fo CI
     */
    public static String formatJsonObjectToRespondAsBoxIssues() {

	JSONObject jsonToBePassed = new JSONObject();
	try {

	    long testStartedAt = System.currentTimeMillis();
	    jsonToBePassed.put("service",
		    System.getProperty(AutomaticsConstants.SERVICE_NAME, ServiceType.CI_VERIFICATION.get()));
	    jsonToBePassed.put(JSON_KEY_OBJECT_STATUS, "BOX_NOT_AVAILABLE");

	    jsonToBePassed.put(JSON_KEY_OBJECT_BUILD_IMAGE_NAME, System.getProperty("BUILD_NAME", " "));
	    jsonToBePassed.put(JSON_KEY_OBJECT_SETTOP_LIST, " ");
	    jsonToBePassed.put(JSON_KEY_OBJECT_START_TIME, testStartedAt);
	    jsonToBePassed.put(JSON_KEY_OBJECT_COMPLETION_TIME, testStartedAt);
	    String jmdId = System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0");
	    jsonToBePassed.put(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, Integer.parseInt(jmdId));
	    jsonToBePassed.put(AutomaticsConstants.SYSTEM_PROPERTY_UPDATE_RDK_PORTAL, true);

	    JSONObject result = new JSONObject();
	    result.put(JSON_KEY_OBJECT_BUILD_NAME, System.getProperty("BUILD_NAME", " "));
	    result.put(JSON_KEY_OBJECT_TYPE, "qt");
	    JSONArray component = new JSONArray();
	    JSONArray testResults = new JSONArray();
	    JSONObject testStepStatus = new JSONObject();

	    try {
		testStepStatus.put(JSON_KEY_OBJECT_TEST_ID, "TC-STB-QT-001");
		testStepStatus.put(JSON_KEY_OBJECT_STEP_NUMBER, "s1");
		testStepStatus.put(JSON_KEY_OBJECT_STATUS, "SKIPPED");
		testStepStatus.put(JSON_KEY_OBJECT_REASON, "BOX NOT AVAILABLE OR ACCESSIBLE");
		testStepStatus.put(JSON_KEY_OBJECT_COMPONENT, component);
	    } catch (JSONException jex) {
		LOGGER.info("JSON Exception occured. ", jex);
	    }

	    testResults.put(testStepStatus);

	    result.put(JSON_KEY_OBJECT_TESTS, testResults);
	    jsonToBePassed.put(JSON_KEY_OBJECT_RESULT, result);

	} catch (JSONException jex) {
	    LOGGER.info("JSON Exception occured. ", jex);

	    jex.printStackTrace();
	}

	return jsonToBePassed.toString();
    }

    /**
     * 
     * This method determines whether more than one reason is present for failure.If present,the status assigned will
     * become BOXES_UNUSABLE.Else the same status will be assigned to json to be passed to TM
     * 
     * @param jsonToBePassed
     *            json to be passed
     * @return JSON object
     * @throws JSONException
     */
    public static JSONObject getStatus(JSONObject jsonToBePassed) throws JSONException {
	LOGGER.info("Inside GETSTATUS###############################");
	if (jobStatus != null && !jobStatus.isEmpty()) {
	    for (String each : jobStatus.keySet()) {
		LOGGER.info("key " + each + " Value : " + jobStatus.get(each));
	    }
	    HashSet<JobStatusValue> statuses = new HashSet<JobStatusValue>(jobStatus.values());
	    if (statuses.size() > 1) {
		jsonToBePassed.put(AutomaticsTestBase.JSON_KEY_OBJECT_STATUS, JobStatusValue.BOXES_UNUSABLE);

		TestType testType = TestUtils.getTestType();
		if (!TestType.isQt(testType.name())) {
		    String settopList = TestUtils.getCommaSepDeviceMac();
		    if (CommonMethods.isNotNull(settopList)) {
			if (jobStatus.containsKey(settopList)) {
			    jsonToBePassed.put(AutomaticsTestBase.JSON_KEY_OBJECT_STATUS, jobStatus.get(settopList));
			}
		    }
		}
	    } else {
		// LOGGER.info("Status---------" + statuses.iterator().next());
		jsonToBePassed.put(AutomaticsTestBase.JSON_KEY_OBJECT_STATUS, statuses.iterator().next());
	    }
	} else {
	    jsonToBePassed.put(AutomaticsTestBase.JSON_KEY_OBJECT_STATUS, JobStatusValue.BOXES_UNUSABLE);
	}
	return jsonToBePassed;
    }

    /**
     * Update the final status to test manager. This method is written as a fail safe mechanism in situations where we
     * have not executed any tests.
     */
    public static void updateFinalStatusToAutomatics() {

	JSONObject jsonToBePassed = new JSONObject();
	String json = "";

	if (!AutomaticsUtils.isAutomaticsUpdated) {

	    if (CommonMethods.isNull(AutomaticsUtils.jsonToAutomatics)) {

		try {
		    LOGGER.info("Creating final execution status for {}", AutomaticsTestBase.testType);
		    String testFilterType = TestUtils.getTestTypeValue();
		    Set<String> finalDeviceMacs = AutomaticsTestBase.finalDeviceList;
		    LOGGER.info("Remaining locked settops: {}", finalDeviceMacs);

		    if (testFilterType.equals(AutomaticsTestTypes.QUICK_CI.value())
			    || testFilterType.equals(AutomaticsTestTypes.FAST_QUICK_CI.value())) {
			LOGGER.info("QUICK CI formatting New Json");
			json = AutomaticsUtils.formatJsonObjectToRespondAsBoxIssues();
		    } else {
			if (null == finalDeviceMacs || finalDeviceMacs.isEmpty()) {
			    LOGGER.info("Final devices do not exist");
			    jsonToBePassed.put(AutomaticsUtils.JSON_KEY_OBJECT_STATUS, "FAILURE");
			} else {
			    jsonToBePassed = AutomaticsUtils.getStatus(jsonToBePassed);
			    LOGGER.info("Processed assigning failure to status in Test executor.New status"
				    + jsonToBePassed.toString());
			}
			String jmdId = System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0");
			if (CommonMethods.isNull(jmdId)) {
			    jmdId = "0";
			}
			jsonToBePassed.put(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, Integer.parseInt(jmdId));
			jsonToBePassed.put(AutomaticsConstants.SYSTEM_PROPERTY_UPDATE_RDK_PORTAL,
				TestUtils.isUpdateRdkPortal());
			jsonToBePassed.put("service",
				System.getProperty(AutomaticsConstants.SERVICE_NAME, AutomaticsConstants.EMPTY_STRING));

			JSONObject result = new JSONObject();

			jsonToBePassed.put(AutomaticsUtils.JSON_KEY_OBJECT_RESULT, result);
			json = jsonToBePassed.toString();
		    }

		} catch (JSONException jex) {
		    LOGGER.error("Exception parsing json data: {}", jex.getMessage(), jex);
		}

		AutomaticsUtils.jsonToAutomatics = json;
	    }

	    // Update Automatics
	    AutomaticsUtils.updateAutomatics();
	}
    }

    /**
     * 
     * Method calls Automatics API to dynamically create a step in Test case for updating results /extra details
     * /remarks from framework
     * 
     * @return Status of operation is returned
     */
    public static boolean addPostStepInAutomatics(String step, String executionStatus, String testCaseId,
	    com.automatics.device.Dut dut, String description, String expected, String actions, String remarks) {
	LOGGER.debug("Inside addPostStepInAutomatics");
	boolean isSuccess = false;
	String testManagerUrl = AutomaticsPropertyUtility.getProperty("automatics.url");
	String updateStepEndPoint = "addPostConditionStep.htm";
	JSONObject jsonObj = new JSONObject();
	try {
	    String jmid = System.getProperty("JMD_ID");
	    if (CommonMethods.isNotNull(jmid)) {
		jsonObj.put("jobId", Integer.parseInt(jmid));
		jsonObj.put("macAddress", dut.getHostMacAddress());
		jsonObj.put("automationId", testCaseId);
		jsonObj.put("stepNumber", step);
		jsonObj.put("executionStatus", executionStatus);
		jsonObj.put("description", description);
		jsonObj.put("expected", expected);
		jsonObj.put("action", actions);
		jsonObj.put("remarks", remarks);

		LOGGER.info("POST CONDITION JSON to Automatics \n" + jsonObj.toString());
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		ServerCommunicator serverCommunicator = new ServerCommunicator(LOGGER);
		ServerResponse serverResponse = serverCommunicator.postDataToServer(
			testManagerUrl + updateStepEndPoint, jsonObj.toString(), "POST", 200000, headers);
		if (serverResponse != null && serverResponse.getResponseCode() == 200) {
		    LOGGER.info("POST CONDITION step {} added successfully in Automatics", step);
		    isSuccess = true;
		}

	    }
	} catch (JSONException e) {
	    LOGGER.error("Exception occured while adding step details to Automatics");
	}
	LOGGER.debug("Exiting addPostStepInAutomatics with status " + isSuccess);
	return isSuccess;
    }

    /**
     * Sleeps the current thread execution for the specified milliseconds.
     * 
     * @param milliseconds
     *            time to sleep in milliseconds
     */
    public static void sleep(long milliseconds) {

	try {
	    Thread.sleep(milliseconds);
	} catch (InterruptedException e) {
	    LOGGER.error("Sleep interrupted " + e.getMessage());
	}
    }

    /**
     * Strips off the ':' from the MAC string.
     * 
     * @param macAddress
     *            MAC address to be cleaned
     * 
     * @return MAC address with out ':'
     */
    public static String getCleanMac(String macAddress) {

	if (macAddress != null) {
	    return macAddress.replaceAll(":", "");
	}

	return AutomaticsConstants.EMPTY_STRING;

    }

    /**
     * Splits the provided string with separator and adds them to a list.
     * 
     * @param stringToSplit
     *            String to be split
     * @param separator
     *            separator to be used to split the string
     * 
     * @return List of string split
     */
    public static List<String> splitStringToList(String stringToSplit, String separator) {
	List<String> splitList = new ArrayList<String>();
	List<String> trim_List = new ArrayList<String>();

	if (null != stringToSplit && !stringToSplit.isEmpty()) {
	    splitList = new ArrayList(Arrays.asList(stringToSplit.split(separator)));
	}

	// Trim Spaces in String
	for (String splitString : splitList) {
	    trim_List.add(splitString.trim());
	}

	return trim_List;
    }

}
