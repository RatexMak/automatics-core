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
package com.automatics.dataobjects;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.ExecutionStatus;

/**
 * Dataobject class to hold the execution result status
 * 
 * 
 *
 */
public class ExecutionResultStatusDO {

    /** Holds the job manager id */
    String jobManagerId;

    /** Holds the manual id */
    String manualId;

    /** Holds the step number */
    String stepNumber;

    /** Holds the test type */
    String testType;

    /** Holds the build name */
    String buildName;

    /** Holds the mac address */
    String macAddress;

    /** Holds the Execution Status */
    ExecutionStatus executionStatus;

    /** Holds the failure reason / remarks */
    String remarks;

    /** Holds the jira ticket number */
    String jiraTicketNumber;

    /** Indicates whether the remaining steps needs to be skipped */
    boolean skipRemainingSteps = false;

    /** Parameter added to identify different syndication models **/
    String partnerName;

    String automationId;

    String iterationNumber;

    long time;

    public long getTime() {
	return time;
    }

    public void setTime(long time) {
	this.time = time;
    }

    public String getIterationNumber() {
	return iterationNumber;
    }

    public void setIterationNumber(String iterationNumber) {
	this.iterationNumber = iterationNumber;
    }

    String gateway;

    public String getGateway() {
	return gateway;
    }

    public void setGateway(String gateway) {
	this.gateway = gateway;
    }

    public String getAutomationId() {
	return automationId;
    }

    public void setAutomationId(String automationId) {
	this.automationId = automationId;
    }

    public String getPartnerName() {
	return partnerName;
    }

    public void setPartnerName(String partnerName) {
	this.partnerName = partnerName;
    }

    /**
     * @return the manualId
     */
    public String getManualId() {
	return manualId;
    }

    /**
     * @param manualId
     *            the manualId to set
     */
    public void setManualId(String manualId) {
	this.manualId = manualId;
    }

    /**
     * @return the stepNumber
     */
    public String getStepNumber() {
	return stepNumber;
    }

    /**
     * @param stepNumber
     *            the stepNumber to set
     */
    public void setStepNumber(String stepNumber) {
	this.stepNumber = stepNumber;
    }

    /**
     * @return the testType
     */
    public String getTestType() {
	return testType;
    }

    /**
     * @param testType
     *            the testType to set
     */
    public void setTestType(String testType) {
	this.testType = testType;
    }

    /**
     * @return the buildName
     */
    public String getBuildName() {
	return buildName;
    }

    /**
     * @param buildName
     *            the buildName to set
     */
    public void setBuildName(String buildName) {
	this.buildName = buildName;
    }

    /**
     * @return the executionStatus
     */
    public ExecutionStatus getExecutionStatus() {
	return executionStatus;
    }

    /**
     * @param executionStatus
     *            the executionStatus to set
     */
    public void setExecutionStatus(ExecutionStatus executionStatus) {
	this.executionStatus = executionStatus;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
	return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    /**
     * @return the skipRemainingSteps
     */
    public boolean isSkipRemainingSteps() {
	return skipRemainingSteps;
    }

    /**
     * @param skipRemainingSteps
     *            the skipRemainingSteps to set
     */
    public void setSkipRemainingSteps(boolean skipRemainingSteps) {
	this.skipRemainingSteps = skipRemainingSteps;
    }

    /**
     * @return the jiraTicketNumber
     */
    public String getJiraTicketNumber() {
	return jiraTicketNumber;
    }

    /**
     * @param jiraTicketNumber
     *            the jiraTicketNumber to set
     */
    public void setJiraTicketNumber(String jiraTicketNumber) {
	this.jiraTicketNumber = jiraTicketNumber;
    }

    /**
     * @return the jobManagerId
     */
    public String getJobManagerId() {
	return jobManagerId;
    }

    /**
     * @param jobManagerId
     *            the jobManagerId to set
     */
    public void setJobManagerId(String jobManagerId) {
	this.jobManagerId = jobManagerId;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
	return macAddress;
    }

    /**
     * @param macAddress
     *            the macAddress to set
     */
    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    /**
     * Convert the object to json
     * 
     * @return jsonObject
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {

	JSONObject jsonObject = new JSONObject();

	jsonObject.put(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, jobManagerId);
	jsonObject.put("manualId", manualId);
	jsonObject.put("stepNumber", stepNumber);
	jsonObject.put("testType", testType);
	jsonObject.put("buildName", buildName);
	jsonObject.put("macAddress", macAddress);
	jsonObject.put("remarks", remarks);
	jsonObject.put("executionStatus", executionStatus.getStatus());
	jsonObject.put("skipRemaining", skipRemainingSteps);
	jsonObject.put("jiraTicketNumber", jiraTicketNumber);
	jsonObject.put("partnerName", partnerName);
	jsonObject.put("automationId", automationId);	
	return jsonObject;
    }

    /**
     * Convert the object to json
     * 
     * @return jsonObject
     * @throws JSONException
     */
    public JSONObject getCommonJSON() throws JSONException {

	JSONObject jsonObject = new JSONObject();

	jsonObject.put(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, jobManagerId);
	jsonObject.put("manualId", manualId);
	jsonObject.put("stepNumber", stepNumber);
	jsonObject.put("testType", testType);
	jsonObject.put("buildName", buildName);
	jsonObject.put("macAddress", macAddress);
	jsonObject.put("remarks", remarks);
	jsonObject.put("executionStatus", executionStatus.getStatus());
	jsonObject.put("skipRemaining", skipRemainingSteps);
	jsonObject.put("jiraTicketNumber", jiraTicketNumber);
	jsonObject.put("partnerName", partnerName);
	jsonObject.put("automationId", automationId);
	
	// additional parameters
	jsonObject.put("rdkType", "");
	jsonObject.put("stepExecTimeInSec", "");
	jsonObject.put("testCaseStatus", "");
	jsonObject.put("commandName", "");
	jsonObject.put("macStatus", "");
	jsonObject.put("runbookType", "");
	jsonObject.put("totalNoOfSteps", "");
	jsonObject.put("validationType", "");
	String jenkinsUrl = System.getProperty("BUILD_URL", "");
	if (!jenkinsUrl.isEmpty()) {
	    jenkinsUrl += "console";
	}
	jsonObject.put("jenkinsLog", jenkinsUrl);
	return jsonObject;
    }
}
