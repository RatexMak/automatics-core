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

package com.automatics.reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.ExecutionResultStatusDO;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.enums.ExecuteOnType;
import com.automatics.enums.ExecutionStatus;
import com.automatics.enums.TestType;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.CommonMethods;

/**
 * Automatics result updator
 */
public class TestResultUpdator {

    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestResultUpdator.class);

    /** TestPlanExcelReporter instance. */
    private static TestResultUpdator testPlanExcelReporter = null;

    public static String errorMessageReason = "";

    public static Map<String, Collection<String>> ticketDetails = new HashMap<String, Collection<String>>();

    /** Hash map holds the test execution status. */
    private Map<String, ExecutionStatus> testStepsExecutionStatus = new LinkedHashMap<String, ExecutionStatus>();

    /**
     * This variable holds the list of execution test step status that were failed to get updated back to Automatics.
     * The system will re-attempt these at the end of execution.
     */
    private static Collection<String> pendingResultUpdates = null;

    /** Instance of Ecatstapapi. */
    AutomaticsTapApi ecatsTapApi = AutomaticsTapApi.getInstance();

    /**
     * Private constructor for avoid multiple instances.
     */
    private TestResultUpdator() {
	// Private constructor
    }

    /**
     * Get singleton instance for {@link TestResultUpdator}.
     *
     * @return The singleton instance of {@link TestResultUpdator}.
     */
    public static TestResultUpdator get() {

	if (testPlanExcelReporter == null) {
	    testPlanExcelReporter = new TestResultUpdator();
	}

	return testPlanExcelReporter;
    }

    /**
     * @param testId
     * @param testStepNumber
     * @param status
     * @param errorMessage
     * @param infoMessage
     */
    public synchronized void updateExecutionStatus(Dut dut, String testId, String testStepNumber,
	    ExecutionStatus status, String errorMessage, boolean blockExecution) {

	updateExecutionStatus(dut, testId, testStepNumber, status, errorMessage, blockExecution, false);
    }

    public synchronized void updateExecutionStatus(Dut dut, String testId, String testStepNumber,
	    ExecutionStatus status, String errorMessage, boolean blockExecution, boolean updateToElkDb) {

	String firmwareVersion = null;
	Device device = null;
	String ticketNumber = null;
	String finalTestTypeValue = null;
	String serviceType = System.getProperty(AutomaticsConstants.SERVICE_NAME, AutomaticsConstants.EMPTY_STRING);
	String testType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);

	// Getting partnerName
	String partnerName = System.getProperty("partnerName", AutomaticsConstants.EMPTY_STRING);

	ExecutionResultStatusDO executionResultStatusDO = null;

	LOGGER.debug("Service Type to update DB " + serviceType);

	if (ExecuteOnType.GATEWAY.equals(((Device) dut).getExecuteOn())) {
	    dut = (((Device) dut).getGateWaySettop());
	    device = (Device) dut;
	} else {
	    device = (Device) dut;
	}

	firmwareVersion = device.getFirmwareVersion();

	/*
	 * Disabled this logging due to confusion in during analysis of Quick Test.
	 */
	LOGGER.debug("Firmware version " + firmwareVersion);

	String testStepExecutionStatusKey = firmwareVersion + AutomaticsConstants.UNDERSCORE + testId
		+ AutomaticsConstants.UNDERSCORE + testStepNumber;

	LOGGER.debug("Test Step Execution Status Key : " + testStepExecutionStatusKey);

	boolean isUpdateRequired = false;
	boolean shouldRaiseJiraTicket = false;

	if (testStepsExecutionStatus.containsKey(testStepExecutionStatusKey)) {

	    if (ExecutionStatus.FAILED.equals(testStepsExecutionStatus.get(testStepExecutionStatusKey))
		    && (ExecutionStatus.PASSED.equals(status))) {

		testStepsExecutionStatus.put(testStepExecutionStatusKey, status);
		isUpdateRequired = true;
	    } else if (ExecutionStatus.FAILED.equals(testStepsExecutionStatus.get(testStepExecutionStatusKey))
		    && (ExecutionStatus.FAILED.equals(status))) {

		Collection<String> settopMacDetails = null;

		/*
		 * Since the failure occurred for more than once, we will definitely raise the ticket. If the failure
		 * occurs for the 3rd time also, the system will update the earlier raised ticket.
		 */
		if (ticketDetails.containsKey(testStepExecutionStatusKey.toUpperCase())) {

		    settopMacDetails = ticketDetails.get(testStepExecutionStatusKey.toUpperCase());

		    if (!settopMacDetails.contains(dut.getHostMacAddress())) {

			settopMacDetails.add(dut.getHostMacAddress());
		    }

		} else {
		    settopMacDetails = new ArrayList<String>();
		    settopMacDetails.add(dut.getHostMacAddress());
		}

		// Update the details
		ticketDetails.put(testStepExecutionStatusKey.toUpperCase(), settopMacDetails);

		/*
		 * Since the failure occurred for more than once, we will definitely raise the ticket. If the failure
		 * occurs for the 3rd time also, the system will update the earlier raised ticket.
		 */
		shouldRaiseJiraTicket = true;
	    }
	} else {
	    testStepsExecutionStatus.put(testStepExecutionStatusKey, status);
	    isUpdateRequired = true;

	    if (ExecutionStatus.FAILED.equals(status)) {

		List<Dut> lockedSettops = AutomaticsTapApi.getRackInitializerInstance().getLockedSettops();

		if (lockedSettops != null) {

		    Collection<String> settopMacDetails = null;

		    if (ticketDetails.containsKey(testStepExecutionStatusKey.toUpperCase())) {
			settopMacDetails = ticketDetails.get(testStepExecutionStatusKey.toUpperCase());

			if (!settopMacDetails.contains(dut.getHostMacAddress())) {

			    settopMacDetails.add(dut.getHostMacAddress());
			}

		    } else {
			settopMacDetails = new ArrayList<String>();
			settopMacDetails.add(dut.getHostMacAddress());
		    }

		    // Update the details
		    ticketDetails.put(testStepExecutionStatusKey.toUpperCase(), settopMacDetails);

		    /*
		     * If there exist only one dut for execution and if the test case is failed, then raise the ticket.
		     * If there are more than one settops's, then raise the ticket only on the second failure occurrence
		     * onwards.
		     */
		    if (lockedSettops.size() == 1) {

			// raise ticket
			shouldRaiseJiraTicket = true;
		    }
		}
	    }
	}

	LOGGER.debug("Updatation required for current execution : " + isUpdateRequired);

	if (shouldRaiseJiraTicket && CommonMethods.isNotNull(testType) && !SupportedModelHandler.isRDKB(dut)) {

	    errorMessageReason = errorMessage;

	    List<Dut> settopList = new ArrayList<Dut>();
	    settopList.add(dut);

	}

	LOGGER.debug("Ticket Number" + ticketNumber + " Update Need Status " + isUpdateRequired);
	if (isUpdateRequired) {

	    boolean isRdkb = SupportedModelHandler.isRDKB(dut);

	    // format the firmware image name according the protocol (IPV4 &IPV6) and execution mode
	    firmwareVersion = CommonMethods.formatFirmwareImageNameForReporting(CommonMethods.isIpv6Address(dut),
		    firmwareVersion, isRdkb);

	    if (AutomaticsTestBase.isGroupRun && CommonMethods.isNotNull(AutomaticsTestBase.getGroupsToRun())) {

		LOGGER.debug("Group enabled updatng");
		finalTestTypeValue = AutomaticsTestBase.getGroupsToRun();

	    } else if (AutomaticsTestBase.isComponentRun()
		    && CommonMethods.isNotNull(AutomaticsTestBase.getComponentsToRun())) {

		LOGGER.debug("Component Run enabled updatng");
		finalTestTypeValue = AutomaticsTestBase.getComponentsToRun();

	    } else if (CommonMethods.isNotNull(testType)) {

		finalTestTypeValue = TestType.valueOf(testType).get();
	    }

	    // Set the object with values
	    executionResultStatusDO = new ExecutionResultStatusDO();
	    executionResultStatusDO.setJobManagerId(System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID));
	    executionResultStatusDO.setManualId(testId);
	    executionResultStatusDO.setBuildName(firmwareVersion);
	    executionResultStatusDO.setExecutionStatus(status);
	    executionResultStatusDO.setRemarks(CommonMethods.cleanNonPrintableCharsFromTextContent(errorMessage));
	    executionResultStatusDO.setSkipRemainingSteps(blockExecution);
	    executionResultStatusDO.setStepNumber(testStepNumber);
	    executionResultStatusDO.setJiraTicketNumber(ticketNumber);
	    executionResultStatusDO.setTestType(finalTestTypeValue);
	    executionResultStatusDO.setMacAddress(dut.getHostMacAddress());
	    executionResultStatusDO.setPartnerName(partnerName);
	    // LOGGER.info("((Device)dut).getTestSessionDetails().getTestCaseTobeExecuted()="
	    // + ((Device) dut).getTestSessionDetails().getTestCaseTobeExecuted());
	    executionResultStatusDO.setAutomationId(((Device) dut).getTestSessionDetails().getTestCaseTobeExecuted());

	    // Invoke the result update via test manager
	    invokeExecutionResultUpdate(executionResultStatusDO);

	}
    }

    /**
     * Method to invoke the execution result update
     * 
     * @param executionResultStatusDO
     */
    private void invokeExecutionResultUpdate(ExecutionResultStatusDO executionResultStatusDO) {

	StringBuffer targetUrl = null;
	String contentValue = null;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	boolean shouldRetry = false;

	int retryAttempt = 0;

	if (executionResultStatusDO != null) {
	    try {
		// Set the post data content
		contentValue = executionResultStatusDO.toJSON().toString();
		LOGGER.info("Result Details ->" + contentValue);

		// Load the STB Property file
		AutomaticsPropertyUtility.loadProperties();

		serverCommunicator = new ServerCommunicator(LOGGER);

		targetUrl = new StringBuffer(AutomaticsPropertyUtility.getProperty("automatics.url"));
		targetUrl.append("updateManualIdStepWithStatus.htm");

		do {
		    shouldRetry = false;

		    // Get the build parameter details from tets manager for the given id
		    serverResponse = serverCommunicator.postDataToServer(targetUrl.toString(), contentValue, "POST",
			    120000, null);

		    if (serverResponse != null) {
			if (serverResponse.getResponseCode() == HttpStatus.SC_OK) {
			    LOGGER.info("Execution result updated successfully");
			} else if (serverResponse.getResponseCode() == HttpStatus.SC_NOT_ACCEPTABLE
				|| serverResponse.getResponseCode() == HttpStatus.SC_BAD_REQUEST) {

			    LOGGER.error("FAILED TO UPDATE EXECUTION RESULT." + serverResponse.getResponseStatus());
			} else {
			    LOGGER.error("FAILED TO UPDATE EXECUTION RESULT." + serverResponse.getResponseStatus());

			    if (retryAttempt < 3) {
				retryAttempt++;
				shouldRetry = true;

				LOGGER.info("[RETRY " + retryAttempt
					+ "] Attempting retry for result update after 10 seconds");

				// wait for 10 seconds
				AutomaticsUtils.sleep(10000);
			    } else {
				LOGGER.info("Maximum 3 retries reached. Will try to update at the end");

				addTestStatusRecordsToPendingList(contentValue);
			    }
			}
		    }
		} while (shouldRetry);

	    } catch (JSONException jsonException) {
		LOGGER.error(jsonException.getMessage() + ". Skipping update");
	    }
	} else {
	    LOGGER.error("Execution result status details cannot be null. Skipping update");
	}
    }

    /**
     * Method to add the existing execution status from db to the map
     * 
     * @param firmwareVersion
     * @param testId
     * @param testStepNumber
     * @param status
     */
    public void addTestStepStatus(String firmwareVersion, String testId, String testStepNumber, ExecutionStatus status) {
	String testStepExecutionStatusKey = firmwareVersion + AutomaticsConstants.UNDERSCORE + testId
		+ AutomaticsConstants.UNDERSCORE + testStepNumber;

	testStepsExecutionStatus.put(testStepExecutionStatusKey, status);
    }

    /**
     * Method to add the test step status that were failed to be updated to the list.
     * 
     * @param contentData
     */
    private static synchronized void addTestStatusRecordsToPendingList(String contentData) {

	if (pendingResultUpdates == null) {
	    pendingResultUpdates = new ArrayList<String>(0);
	}

	pendingResultUpdates.add(contentData);
    }

    /**
     * Method to reattempt pending status update
     * 
     * @param executionResultStatusDO
     */
    public static void reattemptPendingStatusUpdate() {

	StringBuffer targetUrl = null;

	JSONObject jsonObject = null;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	if (pendingResultUpdates != null) {
	    for (String contentValue : pendingResultUpdates) {
		try {
		    jsonObject = null;
		    jsonObject = new JSONObject(contentValue);

		    LOGGER.info("*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*");
		    LOGGER.info("[Final Status Retry] Manual Id : " + jsonObject.get("manualId") + ", Step Number : "
			    + jsonObject.get("stepNumber") + ", Status : " + jsonObject.get("executionStatus")
			    + ", Mac Address : " + jsonObject.get("macAddress"));

		    // Load the STB Property file
		    AutomaticsPropertyUtility.loadProperties();

		    serverCommunicator = new ServerCommunicator(LOGGER);

		    targetUrl = new StringBuffer(AutomaticsPropertyUtility.getProperty("automatics.url"));
		    targetUrl.append("updateManualIdStepWithStatus.htm");

		    // Get the build parameter details from tets manager for the given id
		    serverResponse = serverCommunicator.postDataToServer(targetUrl.toString(), contentValue, "POST",
			    120000, null);

		    if (serverResponse != null) {
			if (serverResponse.getResponseCode() == HttpStatus.SC_OK) {
			    LOGGER.info("[Final Status Retry] Execution result updated successfully");
			} else if (serverResponse.getResponseCode() == HttpStatus.SC_NOT_ACCEPTABLE
				|| serverResponse.getResponseCode() == HttpStatus.SC_BAD_REQUEST) {

			    LOGGER.error("[Final Status Retry] Failed to update execution result."
				    + serverResponse.getResponseStatus());
			} else {
			    LOGGER.error("[Final Status Retry] Update execution result failed."
				    + serverResponse.getResponseStatus());
			}
		    }

		} catch (JSONException jsonException) {
		    LOGGER.error(jsonException.getMessage() + ". Skipping Status retry update");
		}
	    }
	}

	pendingResultUpdates = null;
    }

    public synchronized void updateExecutionStatus(Dut dut, int iteration, String testId, String testStepNumber,
	    String execStatus, String errorMessage, boolean blockExecution) {

	String firmwareVersion = null;
	Device device = null;
	String finalTestTypeValue = null;
	String componentValue = AutomaticsTestBase.getComponentsToRun();
	String serviceType = System.getProperty(AutomaticsConstants.SERVICE_NAME, AutomaticsConstants.EMPTY_STRING);
	String testType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	String executionMode = AutomaticsTapApi.getCurrentExecutionMode();
	ExecutionStatus status = ExecutionStatus.getExecutionStatus(execStatus);
	// Getting partnerName
	String partnerName = System.getProperty("partnerName", AutomaticsConstants.EMPTY_STRING);

	ExecutionResultStatusDO executionResultStatusDO = null;

	LOGGER.debug("Service Type to update DB " + serviceType);

	if (ExecuteOnType.GATEWAY.equals(((Device) dut).getExecuteOn())) {
	    dut = (((Device) dut).getGateWaySettop());
	    firmwareVersion = AutomaticsTapApi.getInstance().getFirmwareVersion(dut);
	    device = (Device) dut;
	} else {
	    device = (Device) dut;
	    firmwareVersion = device.getFirmwareVersion();
	}

	if (CommonMethods.isNotNull(componentValue)
		&& (componentValue.contains("MIRROR_RACK") || componentValue.contains("CDN"))) {
	    /*
	     * As per the requirement, for mirror rack, we should update the results against the build name that was
	     * been given as input, not the one available in the box.
	     * 
	     * Not sure why such strange requirements. So as usual, patch works.
	     */
	    firmwareVersion = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_NAME, "")
		    .replace("-signed", "").replace(".bin", "");
	}

	/*
	 * Disabled this logging due to confusion in during analysis of Quick Test.
	 */
	LOGGER.debug("Firmware version " + firmwareVersion);

	String testStepExecutionStatusKey = firmwareVersion + AutomaticsConstants.UNDERSCORE + testId
		+ AutomaticsConstants.UNDERSCORE + testStepNumber;

	LOGGER.debug("Test Step Execution Status Key : " + testStepExecutionStatusKey);

	boolean isUpdateRequired = false;
	boolean shouldRaiseJiraTicket = false;

	if (testStepsExecutionStatus.containsKey(testStepExecutionStatusKey)) {

	    if (ExecutionStatus.FAILED.equals(testStepsExecutionStatus.get(testStepExecutionStatusKey))
		    && (ExecutionStatus.PASSED.equals(status))) {

		testStepsExecutionStatus.put(testStepExecutionStatusKey, status);
		isUpdateRequired = true;
	    } else if (ExecutionStatus.FAILED.equals(testStepsExecutionStatus.get(testStepExecutionStatusKey))
		    && (ExecutionStatus.FAILED.equals(status))) {

		Collection<String> settopMacDetails = null;

		/*
		 * Since the failure occurred for more than once, we will definitely raise the ticket. If the failure
		 * occurs for the 3rd time also, the system will update the earlier raised ticket.
		 */
		if (ticketDetails.containsKey(testStepExecutionStatusKey.toUpperCase())) {

		    settopMacDetails = ticketDetails.get(testStepExecutionStatusKey.toUpperCase());

		    if (!settopMacDetails.contains(dut.getHostMacAddress())) {

			settopMacDetails.add(dut.getHostMacAddress());
		    }

		} else {
		    settopMacDetails = new ArrayList<String>();
		    settopMacDetails.add(dut.getHostMacAddress());
		}

		// Update the details
		ticketDetails.put(testStepExecutionStatusKey.toUpperCase(), settopMacDetails);

		/*
		 * Since the failure occurred for more than once, we will definitely raise the ticket. If the failure
		 * occurs for the 3rd time also, the system will update the earlier raised ticket.
		 */
		shouldRaiseJiraTicket = true;
	    }
	} else {
	    testStepsExecutionStatus.put(testStepExecutionStatusKey, status);
	    isUpdateRequired = true;

	    if (ExecutionStatus.FAILED.equals(status)) {

		List<Dut> lockedSettops = AutomaticsTapApi.getRackInitializerInstance().getLockedSettops();

		if (lockedSettops != null) {

		    Collection<String> settopMacDetails = null;

		    if (ticketDetails.containsKey(testStepExecutionStatusKey.toUpperCase())) {
			settopMacDetails = ticketDetails.get(testStepExecutionStatusKey.toUpperCase());

			if (!settopMacDetails.contains(dut.getHostMacAddress())) {

			    settopMacDetails.add(dut.getHostMacAddress());
			}

		    } else {
			settopMacDetails = new ArrayList<String>();
			settopMacDetails.add(dut.getHostMacAddress());
		    }

		    // Update the details
		    ticketDetails.put(testStepExecutionStatusKey.toUpperCase(), settopMacDetails);

		    /*
		     * If there exist only one dut for execution and if the test case is failed, then raise the ticket.
		     * If there are more than one settops's, then raise the ticket only on the second failure occurrence
		     * onwards.
		     */
		    if (lockedSettops.size() == 1) {

			// raise ticket
			shouldRaiseJiraTicket = true;
		    }
		}
	    }
	}

	LOGGER.info("Updatation required for current execution : " + isUpdateRequired);

	if (shouldRaiseJiraTicket && CommonMethods.isNotNull(testType) && !SupportedModelHandler.isRDKB(dut)) {

	    errorMessageReason = errorMessage;

	    List<Dut> settopList = new ArrayList<Dut>();
	    settopList.add(dut);

	}
	if (isUpdateRequired) {

	    boolean isRdkb = SupportedModelHandler.isRDKB(dut);

	    // format the firmware image name according the protocol (IPV4 &IPV6) and execution mode
	    firmwareVersion = CommonMethods.formatFirmwareImageNameForReporting(CommonMethods.isIpv6Address(dut),
		    firmwareVersion, isRdkb);

	    if (AutomaticsTestBase.isGroupRun && CommonMethods.isNotNull(AutomaticsTestBase.getGroupsToRun())) {

		LOGGER.info("Group enabled updatng");
		finalTestTypeValue = AutomaticsTestBase.getGroupsToRun();

	    } else if (AutomaticsTestBase.isComponentRun()
		    && CommonMethods.isNotNull(AutomaticsTestBase.getComponentsToRun())) {

		LOGGER.info("Component Run enabled updatng");
		finalTestTypeValue = AutomaticsTestBase.getComponentsToRun();

	    } else if (CommonMethods.isNotNull(testType)) {

		finalTestTypeValue = TestType.valueOf(testType).get();
	    }

	    // Set the object with values
	    executionResultStatusDO = new ExecutionResultStatusDO();
	    executionResultStatusDO.setJobManagerId(System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID));
	    executionResultStatusDO.setManualId(testId);
	    executionResultStatusDO.setBuildName(firmwareVersion);
	    executionResultStatusDO.setExecutionStatus(status);
	    executionResultStatusDO.setRemarks(CommonMethods.cleanNonPrintableCharsFromTextContent(errorMessage));
	    executionResultStatusDO.setSkipRemainingSteps(blockExecution);
	    executionResultStatusDO.setStepNumber(testStepNumber);
	    executionResultStatusDO.setTestType(finalTestTypeValue);
	    executionResultStatusDO.setMacAddress(dut.getHostMacAddress());
	    executionResultStatusDO.setPartnerName(partnerName);

	    executionResultStatusDO.setAutomationId(((Device) dut).getTestSessionDetails().getTestCaseTobeExecuted());

	    // Invoke the result update via test manager
	    invokeExecutionResultUpdate(executionResultStatusDO);
	}
    }
}
