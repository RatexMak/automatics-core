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

package com.automatics.executor;

import java.util.Map;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.automatics.annotations.TestDetails;
import com.automatics.device.Device;
import com.automatics.device.DeviceAccount;
import com.automatics.device.DutAccount;
import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.utils.CommonMethods;

/**
 * This is the listener class which generates a customised report on test results.
 *
 * @author Nagendra Kumar
 * @author Smitha bg
 * @author Pratheesh T.K.
 * @author Selvaraj Mariyappan
 */

public class AutomaticsTestListener extends TestListenerAdapter {

    /** SLF4J logger implementation. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsTestListener.class);
    
    /** The Execution ID associated with this test run. */
    private String executionId;

    /**
     * Gets the execution id associated with this listener.
     *
     * @return the executionId
     */
    public String getExecutionId() {
	return executionId;
    }

    /**
     * Sets the execution id for this listener.
     *
     * @param executionId
     *            the executionId to set
     */
    public void setExecutionId(String executionId) {
	this.executionId = executionId;
    }

    /**
     * Extract and provides the test name with parameter details from the test result.
     *
     * @param testResult
     *            test result object.
     *
     * @return Test name with parameter details.
     */
    private String getTestDetails(ITestResult testResult) {
	StringBuilder testDetails = new StringBuilder();
	String testResultName = testResult.getName();

	if (null != testResultName) {
	    testDetails.append(testResultName);
	} else {
	    testDetails.append("**Unknown**");
	}

	for (Object testParameter : testResult.getParameters()) {

	    if (testParameter != null) {
		testDetails.append(" - ").append(testParameter.toString());
	    } else {
		testDetails.append(" - null");
	    }
	}	

	return testDetails.toString();
    }

    @Override
    public void onTestStart(ITestResult testResult) {

	LOGGER.info("<a name=\"" + testResult.getTestClass().getName() + "." + testResult.getName() + "\">STARTED - "
		+ getTestDetails(testResult) + "</a>");
	Device device = null;
	DeviceAccount homeAccount = null;
	Object[] params = testResult.getParameters();

	if ((params != null) && (params.length > 0)) {

	    if (params[0] instanceof Device || params[0] instanceof DutAccount) {

		if (params[0] instanceof Device) {
		    device = (Device) params[0];
		} else if (params[0] instanceof DutAccount) {
		    homeAccount = (DeviceAccount) params[0];
		    LOGGER.info("Home Account obtained at AutomaticsTestListener : " + homeAccount.getAccountNumber());
		    device = (Device) homeAccount.getPivotDut();
		}

		// Create a cell and put a value in it.
		TestDetails testDetailsAnnotation = testResult.getMethod().getConstructorOrMethod().getMethod()
			.getAnnotation(TestDetails.class);

		String settopId = CommonMethods.getSettopId(device);
		String testUid = testDetailsAnnotation.testUID();

		AutomaticsTestTypes[] testTypes = testDetailsAnnotation.testType();

		Thread.currentThread().setName(testUid + " : " + settopId);

		/*
		 * Set the automation ID and description for reporting purpose.
		 */
		device.setAutomationTestId(testUid);
		device.setTestDescription(testDetailsAnnotation.testDecription());

		if (testTypes.length > 0) {
		    device.setTestTypes(testDetailsAnnotation.testType()[0]);
		}

		if (null != device.getTrace()) {
		    LOGGER.debug("Started appending log");

		    device.getTrace().insertIntoTrace(
			    " \n<a name=\"" + testResult.getTestClass().getName() + "." + testResult.getName()
				    + "\">********* Starting Test " + testResult.getName() + " Test ID = " + testUid
				    + " , Dut MAC ID = " + settopId + "********* </a>\n", Level.WARN);
		}
	    }
	}
    }   

    /**
     * Invoked after all the tests have run and all their Configuration methods have been called.
     *
     * @param testContext
     *            test context.
     */
    @Override
    public final void onFinish(ITestContext testContext) {
	LOGGER.info("Finished testing class: " + testContext.getClass().getName().toString());
    }

    /**
     * Invoked after the test class is instantiated and before any configuration method is called.
     *
     * @param testContext
     *            test context.
     */
    @Override
    public final void onStart(ITestContext testContext) {
	LOGGER.info("Starting testing on class: " + testContext.getClass().getName());
	super.onStart(testContext);
    }

    /**
     * Invoked each time a method fails but has been annotated with successPercentage and this failure still keeps it
     * within the success percentage requested.
     *
     * @param testResult
     *            containing information about the run test.
     */
    @Override
    public final void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
	LOGGER.info("FAILED - " + getTestDetails(testResult) + " (but was within success percentage).");
	super.onTestFailedButWithinSuccessPercentage(testResult);
    }

    /**
     * Publishes the details of STBs used in testing to the TestReport. The details include Dut MAC, Dut Model and
     * Version of firmware loaded
     *
     * @param settopDetails
     *            The Map whose key is STB MAC and value should be a String array whose first element should be model
     *            name and second element should be the firmware version
     */
    public void publishSettopDetailsToReport(Map<String, String[]> settopDetails) {
	LOGGER.info("Adding STB details to the test report");

    }

}
