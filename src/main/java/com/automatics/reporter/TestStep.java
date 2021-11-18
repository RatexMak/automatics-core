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

import com.automatics.device.Dut;
import com.automatics.enums.ExecutionStatus;

/**
 * Represents the unique test step information which includes the manualId, automationId, stepNumber, execution status,
 * error message,
 *
 * @author Nipun
 * @author Selvaraj Mariyappan
 */
public class TestStep {

    /** The manual Test Id. */
    private String manualId = null;

    /** The automation test Id. */
    private String automationId = null;

    /** The manual Test step number. */
    private String stepNumber = null;

    /** The test step execution status. */
    private ExecutionStatus execStatus = ExecutionStatus.NOT_TESTED;

    /** The error message. */
    private String errorMessage = null;

    /** The test description. */
    private String description = null;

    /** The dut in which test are executing. */
    private Dut dut = null;

    private String testType = null;

    private String jiraId = null;

    /**
     * Constructor with parameters.
     *
     * @param manualId
     *            The manual test ID.
     * @param automationId
     *            The automation ID
     * @param stepNumber
     *            The step number.
     * @param execStatus
     *            The execution status.
     * @param errorMessage
     *            The error message.
     * @param infoMessage
     *            The info message.
     * @param description
     *            The description.
     * @param jiraId
     *            The Jira ID to be updated
     */
    public TestStep(String manualId, String automationId, String stepNumber, ExecutionStatus execStatus,
	    String errorMessage, String description, Dut dut, String jiraId) {
	this.manualId = manualId;
	this.automationId = automationId;
	this.stepNumber = stepNumber;
	this.execStatus = execStatus;
	this.errorMessage = errorMessage;
	this.description = description;
	this.dut = dut;
	this.jiraId = jiraId;
    }

    public TestStep(String stepNumber, ExecutionStatus stepStatus) {
	this.stepNumber = stepNumber;
	this.execStatus = stepStatus;
    }

    /**
     * @param automationTestId
     * @param stepNumber
     */
    public TestStep(String automationTestId, String stepNumber) {
	this.automationId = automationTestId;
	this.stepNumber = stepNumber;
    }

    /**
     * Get manual test Id.
     *
     * @return the manualId
     */
    public String getManualId() {
	return manualId;
    }

    /**
     * Set manual test Id.
     *
     * @param manualId
     *            the manualId to set
     */
    public void setManualId(String manualId) {
	this.manualId = manualId;
    }

    /**
     * Get automation test Id.
     *
     * @return the automationId
     */
    public String getAutomationId() {
	return automationId;
    }

    /**
     * Set automation test Id.
     *
     * @param automationId
     *            the automationId to set
     */
    public void setAutomationId(String automationId) {
	this.automationId = automationId;
    }

    /**
     * Get manual test step number.
     *
     * @return the stepNumber
     */
    public String getStepNumber() {
	return stepNumber;
    }

    /**
     * Set manual test step number.
     *
     * @param stepNumber
     *            the stepNumber to set
     */
    public void setStepNumber(String stepNumber) {
	this.stepNumber = stepNumber;
    }

    /**
     * Get execution status for particular test step.
     *
     * @return the execStatus
     */
    public ExecutionStatus getExecStatus() {
	return execStatus;
    }

    /**
     * Set execution status for particular test step.
     *
     * @param execStatus
     *            the execStatus to set
     */
    public void setExecStatus(ExecutionStatus execStatus) {
	this.execStatus = execStatus;
    }

    /**
     * Get the error message in the case of failure.
     *
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * Set the error message in the case of failure.
     *
     * @param errorMessage
     *            the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    /**
     * Get the description.
     *
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Set description.
     *
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Get the dut used for execution of particular test step.
     *
     * @return the dut The dut used.
     */
    public Dut getSettop() {
	return dut;
    }

    /**
     * Set the dut used for execution of particular test step.
     *
     * @param dut
     *            the dut to set
     */
    public void setSettop(Dut dut) {
	this.dut = dut;
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
     * @return the jira id corresponding to test step
     */

    public String getJiraId() {
	return jiraId;
    }

    /**
     * Set the Jira Id for the failure for that particular step.
     *
     * @param the
     *            jira Id to set
     */
    public void setJiraId(String jiraId) {
	this.jiraId = jiraId;
    }
}