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

/***
 * Class to hold the test step related information
 * 
 * @author surajmathew
 *
 */
public class Step {

    /** Holds the step status */
    private AlmStatus testStepStatus;

    /** Holds the step name */
    private String testStepName;

    /** Holds the step execution start date */
    private String testStepExecStartDate;

    /** Holds the step execution start time */
    private String testStepExecStartTime;

    public AlmStatus getTestStepStatus() {
	return testStepStatus;
    }

    public void setTestStepStatus(AlmStatus testStepStatus) {
	this.testStepStatus = testStepStatus;
    }

    public String getTestStepName() {
	return testStepName;
    }

    public void setTestStepName(String testStepName) {
	this.testStepName = testStepName;
    }

    public String getTestStepExecStartDate() {
	return testStepExecStartDate;
    }

    public void setTestStepExecStartDate(String testStepExecStartDate) {
	this.testStepExecStartDate = testStepExecStartDate;
    }

    public String getTestStepExecStartTime() {
	return testStepExecStartTime;
    }

    public void setTestStepExecStartTime(String testStepExecStartTime) {
	this.testStepExecStartTime = testStepExecStartTime;
    }

    public String jsonFormatObjectStep() {

	StringBuffer jsonBuffer = new StringBuffer();

	jsonBuffer.append("{\"TestStepName\": \"");
	jsonBuffer.append(testStepName);
	jsonBuffer.append("\",\"TestStepStatus\": \"");
	jsonBuffer.append(testStepStatus.getValue());
	jsonBuffer.append("\",\"TestStepExecStartDate\": \"");
	jsonBuffer.append(testStepExecStartDate);
	jsonBuffer.append("\",\"TestStepExecStartTime\": \"");
	jsonBuffer.append(testStepExecStartTime);
	jsonBuffer.append("\"}");

	return jsonBuffer.toString();
    }

    public void setAllStepData(String testStepName, String testStepStartDate, String testStepStartTime,
	    AlmStatus testSetpStatus) {
	this.testStepName = testStepName;
	this.testStepExecStartDate = testStepStartDate;
	this.testStepExecStartTime = testStepStartTime;
	this.testStepStatus = testSetpStatus;
    }
}