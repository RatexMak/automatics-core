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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.automatics.device.Dut;
import com.automatics.utils.CommonMethods;

/**
 * Value Object calss that holds the alm test case details
 * 
 * @author surajmathew
 *
 */
public class AlmTestDetails {

    /** Holds the dut object */
    Dut dutObject;

    /** Holds the ALM test set folder value */
    private String testSetFolderPath;

    /** Holds the ALM test set name value */
    private String testSetName;

    /** Test case object */
    private TestCase testCase;

    /** Holds the ALM test case comment */
    private StringBuffer comments = new StringBuffer();

    /**
     * Constructor
     * 
     * @param dutObject
     *            - Dut object
     * @param testId
     *            - ALM test id
     * @param testSetFolderPath
     *            - ALM test set folder path. Eg: Root/Archive/Automation/CCP
     * @param testSetName
     *            - The test set folder name
     */
    public AlmTestDetails(Dut dutObject, String testId, String testSetFolderPath, String testSetName) {

	testCase = new TestCase(testId);

	this.testSetFolderPath = testSetFolderPath;
	this.testSetName = testSetName;

	if (dutObject != null) {

	    comments.append("STB Mac: ");
	    comments.append(dutObject.getHostMacAddress());
	    comments.append(" \\nTested Firmware Details: ");
	    comments.append(dutObject.getFirmwareVersion());
	    comments.append("\\n\\n Execution Remarks:\\n");
	}
    }

    /**
     * Add the step execution information
     * 
     * @param stepName
     * @param testStepStatus
     * @param comments
     *            / remarks
     */
    public void addStepExecutionDetails(String stepName, AlmStatus testStepStatus, String comments) {

	// Variable to covert date / String into DD-MM-YYYY HH24MI
	SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

	// Variable to covert date / String into HH24:MI:SS
	SimpleDateFormat HH24MISS = new SimpleDateFormat("HH:mm:ss");

	// Current date time
	Date currentDate = new Date();

	if (CommonMethods.isNotNull(comments)) {
	    this.comments.append(stepName);
	    this.comments.append(" --> ");
	    this.comments.append(comments);
	    this.comments.append("\\n\\n");
	}

	testCase.addStep(stepName, YYYYMMDD.format(currentDate), HH24MISS.format(currentDate), testStepStatus,
		this.comments.toString());
    }

    /**
     * Generate the ALM Json
     */
    public String getALMJsonAsString() {

	StringBuffer jsonBuffer = new StringBuffer();

	jsonBuffer.append("{\"end_point\":\"alm\",\"alm_input\":");
	jsonBuffer.append("{\"CreateResultRequest\": {\"TestSetFolderPath\": \"");
	jsonBuffer.append(testSetFolderPath);
	jsonBuffer.append("\",\"LoginId\": \"\",");
	jsonBuffer.append("\"TestSetName\": \"");
	jsonBuffer.append(testSetName);
	jsonBuffer.append("\",\"Tests\": [");
	jsonBuffer.append(testCase.jsonFormatObjectTest());
	jsonBuffer.append("]}}}");

	return jsonBuffer.toString();
    }
}
