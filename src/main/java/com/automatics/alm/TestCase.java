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
package com.automatics.alm;

import java.util.ArrayList;
import java.util.List;

import com.automatics.utils.CommonMethods;

/**
 * Class to hold the test case related information
 * 
 * @author surajmathew
 */
public class TestCase {

    /** Holds the test id */
    private String testId;

    /** Holds the test status */
    private AlmStatus testStatus;

    /** Holds the test execution start date */
    private String testExecutionStartDate;

    /** Holds the test execution start time */
    private String testExecutionStartTime;

    /** Holds the array of test steps */
    private List<Step> testSteps = new ArrayList<Step>();

    /** Holds the comments */
    private String comments;

    public TestCase(String testId) {
	this.testId = testId;
    }

    public String getTestId() {
	return testId;
    }

    public void setTestId(String testId) {
	this.testId = testId;
    }

    public AlmStatus getTestStatus() {
	return testStatus;
    }

    public void setTestStatus(AlmStatus testStatus) {
	this.testStatus = testStatus;
    }

    public String getTestExecutionStartDate() {
	return testExecutionStartDate;
    }

    public void setTestExecutionStartDate(String testExecutionStartDate) {
	this.testExecutionStartDate = testExecutionStartDate;
    }

    public String getTestExecutionStartTime() {
	return testExecutionStartTime;
    }

    public void setTestExecutionStartTime(String testExecutionStartTime) {
	this.testExecutionStartTime = testExecutionStartTime;
    }

    public List<Step> getTestSteps() {
	return testSteps;
    }

    public void setTestSteps(List<Step> testSteps) {
	this.testSteps = testSteps;
    }

    public void addStep(String stepName, String testStepStartDate, String testStepStartTime, AlmStatus testStepStatus,
	    String comments) {
	Step step = new Step();
	step.setAllStepData(stepName, testStepStartDate, testStepStartTime, testStepStatus);
	testSteps.add(step);
	this.testExecutionStartDate = testStepStartDate;
	this.testExecutionStartTime = testStepStartTime;
	this.testStatus = testStepStatus;
	this.comments = comments;
    }

    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    public String setStepsJsonString() {
	int count = 0;
	String stepListJson = null;

	if (testSteps != null && !testSteps.isEmpty()) {

	    stepListJson = "\"TestSteps\": [";

	    for (Step step : testSteps) {
		if (count == 0) {
		    stepListJson = stepListJson + step.jsonFormatObjectStep();
		} else {
		    stepListJson = stepListJson + "," + step.jsonFormatObjectStep();
		}
		count++;
	    }

	    stepListJson = stepListJson + "]";
	}

	return stepListJson;
    }

    public String jsonFormatObjectTest() {

	StringBuffer jsonBuffer = new StringBuffer();
	String stepDetails = "";

	stepDetails = this.setStepsJsonString();

	jsonBuffer.append("{\"TestId\": \"");
	jsonBuffer.append(testId);
	jsonBuffer.append("\",\"TestStatus\": \"");
	jsonBuffer.append(testStatus.getValue());
	jsonBuffer.append("\",\"TestExecutionStartDate\": \"");
	jsonBuffer.append(testExecutionStartDate);
	jsonBuffer.append("\",\"TestExecutionStartTime\": \"");
	jsonBuffer.append(testExecutionStartTime);
	jsonBuffer.append("\"");

	if (CommonMethods.isNotNull(comments)) {
	    jsonBuffer.append(",\"Comments\": \"");
	    jsonBuffer.append(comments);
	    jsonBuffer.append("\"");
	}

	if (CommonMethods.isNotNull(stepDetails)) {
	    jsonBuffer.append(",");
	    jsonBuffer.append(stepDetails);
	}

	jsonBuffer.append("}");

	return jsonBuffer.toString();
    }
}
