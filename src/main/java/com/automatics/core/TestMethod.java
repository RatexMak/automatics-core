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

package com.automatics.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.AutomaticsTestTypes;

/**
 * Represents a Test method. A method tagged with org.testng.annotations.Test will be represented by this object. Test
 * and Test details associated with a test will stored
 *
 * @author nagendra
 */
public class TestMethod {

    /** Name of the test. */
    private String testName = AutomaticsConstants.EMPTY_STRING;

    /** Unique identifier assigned to the test. */
    private String testUID;

    /** Tags associated with the test. */
    private List<String> tags = new ArrayList<String>();

    /** Box types associated with the tests. Reserved for future use. */
    private List<String> runOnBoxTypes = new ArrayList<String>();

    /** Whether the test is enabled or not. */
    private boolean enabled = false;

    /** Name of the test method. */
    private String testMethodName = AutomaticsConstants.EMPTY_STRING;

    private List<AutomaticsTestTypes> testType;

    /**
     * Default constructor.
     */
    public TestMethod() {

    }

    /**
     * Creates the TestMethod initialized with the provided test details.
     *
     * @param testMethodName
     *            The Method object
     * @param testUID
     *            The unique identifier for the test
     * @param testName
     *            Name of the test
     * @param tagsToAdd
     *            Tags associated with this test
     * @param runOnBoxTypesToAdd
     *            Box types associated with this test
     * @param enabled
     *            Enabled status
     */
    public TestMethod(String testMethodName, String testUID, String testName, String[] tagsToAdd,
	    String[] runOnBoxTypesToAdd, boolean enabled) {
	this.testMethodName = testMethodName;
	this.testUID = testUID;
	this.testName = testName;

	if (tagsToAdd != null) {
	    this.tags.addAll(Arrays.asList(tagsToAdd));
	}

	if (runOnBoxTypesToAdd != null) {
	    this.runOnBoxTypes.addAll(Arrays.asList(runOnBoxTypesToAdd));
	}

	this.enabled = enabled;
    }

    /**
     * Adds a tag.
     *
     * @param tag
     *            The tag to add
     *
     * @return true (as specified by Collections.add)
     */
    public boolean addTag(String tag) {
	return this.tags.add(tag);
    }

    /**
     * Adds all the tags to the list.
     *
     * @param tagsToAdd
     *            Tags to be added
     *
     * @return true (as specified by Collections.add)
     */
    public boolean addTags(String[] tagsToAdd) {
	return this.tags.addAll(Arrays.asList(tagsToAdd));
    }

    /**
     * Adds a Box Type.
     *
     * @param runOnBoxType
     *            The box type to add
     *
     * @return true (as specified by Collections.add)
     */
    public boolean addRunOnBoxType(String runOnBoxType) {
	return this.runOnBoxTypes.add(runOnBoxType);
    }

    /**
     * Adds all the runOnBoxTypes to the list.
     *
     * @param runOnBoxTypesToAdd
     *            Box types to be added
     *
     * @return true (as specified by Collections.add)
     */
    public boolean addRunOnBoxType(String[] runOnBoxTypesToAdd) {
	return this.runOnBoxTypes.addAll(Arrays.asList(runOnBoxTypesToAdd));
    }

    /**
     * Sets the enabled status.
     *
     * @param enabled
     *            The enabled status
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    /**
     * Sets the name of this test.
     *
     * @param testName
     *            Name to set
     */
    public void setTestName(String testName) {
	this.testName = testName;
    }

    /**
     * Set the unique ID for this test.
     *
     * @param testUID
     *            The unique ID to set
     */
    public void setTestUID(String testUID) {
	this.testUID = testUID;
    }

    /**
     * Gets the name associated with this test.
     *
     * @return Test name
     */
    public String getTestName() {
	return testName;
    }

    /**
     * Gets the unique ID associated with this test.
     *
     * @return Unique test ID
     */
    public String getTestUID() {
	return testUID;
    }

    /**
     * Gets the tags associated with this test.
     *
     * @return The list of Tags
     */
    public List<String> getTags() {
	return tags;
    }

    /**
     * Gets the box types associated with this test.
     *
     * @return The box types
     */
    public List<String> getRunOnBoxTypes() {
	return runOnBoxTypes;
    }

    /**
     * Checks whether the test is enabled.
     *
     * @return Enabled status
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Checks whether the test contains the specified tag.
     *
     * @param tag
     *            The tag to check
     *
     * @return true if test contains the specified tag
     */
    public boolean containsTag(String tag) {
	return this.tags.contains(tag);
    }

    /**
     * Checks whether the test contains all the specified tags.
     *
     * @param tagsToCheck
     *            The tags to check
     *
     * @return true if test contains all the specified tags
     */
    public boolean containsTags(List<String> tagsToCheck) {
	return this.tags.containsAll(tagsToCheck);
    }

    /**
     * Checks whether the test contains the specified box type.
     *
     * @param runOnBoxType
     *            The box type to check
     *
     * @return true if test contains the specified box type
     */
    public boolean containsRunOnBoxType(String runOnBoxType) {
	return this.runOnBoxTypes.contains(runOnBoxType);
    }

    /**
     * Checks whether the test contains al the specified box types.
     *
     * @param runOnBoxTypesToCheck
     *            The box types to check
     *
     * @return true if test contains all the specified box types
     */
    public boolean containsRunOnBoxTypes(List<String> runOnBoxTypesToCheck) {
	return this.runOnBoxTypes.containsAll(runOnBoxTypesToCheck);
    }

    /**
     * Gets the type of this test.
     *
     * @return the testType
     */
    public List<AutomaticsTestTypes> getTestType() {
	return testType;
    }

    /**
     * Sets the type of this test.
     *
     * @param testType
     *            the testType to set
     */
    public void setTestType(List<AutomaticsTestTypes> testType) {
	this.testType = testType;
    }

    /**
     * Gets Test Method name for the test.
     *
     * @return the test method name
     */
    public String getTestMethodName() {
	return testMethodName;
    }

    /**
     * Sets Method name for the test.
     *
     * @param testMethodName
     *            the testMethodname to set
     */
    public void setTestMethodName(String testMethodName) {
	this.testMethodName = testMethodName;
    }

}
