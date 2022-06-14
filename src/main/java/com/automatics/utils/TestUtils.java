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

import java.util.Map;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.CrashConstants;
import com.automatics.constants.IssueManagementConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.device.DutImpl;
import com.automatics.enums.AutomaticsBuildType;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.ExecuteOnType;
import com.automatics.enums.ExecutionMode;
import com.automatics.enums.TestType;

/**
 * 
 * Test related utils class.
 *
 */
public class TestUtils {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private static Scheduler reverseSshPortCleanerSchedule;

    /** Boolean which determines whether to use reverse ssh or not for client devices */
    public static Boolean REVERSE_SSH_FLAG = false;

    public static void setReverseSshSchedulerForTheJob(Scheduler schedulerObject) {
	LOGGER.info("Setting scheduler " + schedulerObject);
	reverseSshPortCleanerSchedule = schedulerObject;
    }

    public static Scheduler getReverseSshSchedulerForTheJob() {
	LOGGER.info("Getting scheduler " + reverseSshPortCleanerSchedule);
	return reverseSshPortCleanerSchedule;
    }

    /**
     * Method to get the execute on type to specify which is the testing model for client server test.
     * 
     * @param dut
     *            The dut instance.
     * 
     * @return The execute on type
     */
    public static ExecuteOnType getExecuteOnType(Dut dut) {
	ExecuteOnType executeOn = null;

	String executeOnTypeProperty = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_EXECUTE_ON_TYPE,
		AutomaticsConstants.EXECUTE_ON_DEFAULT);

	if (AutomaticsConstants.EXECUTE_ON_GATEWAY.equalsIgnoreCase(executeOnTypeProperty)) {
	    executeOn = ExecuteOnType.GATEWAY;
	} else {
	    executeOn = ExecuteOnType.DEFAULT;
	}

	LOGGER.info(": current Execute On type = " + executeOn.executeOn());

	return executeOn;
    }

    /**
     * Method to get the current build type.
     * 
     * @param dut
     *            The dut instance.
     * 
     * @return The current build type
     */
    public static AutomaticsBuildType getBuildType(Dut dut) {
	AutomaticsBuildType buildType = null;

	String buildTypeProperty = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_TYPE);

	if (AutomaticsConstants.BUILD_TYPE_OCAP.equalsIgnoreCase(buildTypeProperty)
		|| (SupportedModelHandler.isRDKVClient(dut))) {
	    buildType = AutomaticsBuildType.OCAP;
	    ((Device) dut).setBuildType(buildType);
	} else {
	    buildType = AutomaticsBuildType.RDK;
	}

	LOGGER.info("getBuildType(): current build type = " + buildType.getType());

	return buildType;
    }

    /**
     * 
     * This method returns the device category based on device model
     * 
     * @param device
     * @return Returns enum corresponding to device Type
     */
    public static DeviceCategory getDeviceCategory(DutImpl device) {
	DeviceCategory deviceType = DeviceCategory.UNKNOWN;
	if (SupportedModelHandler.isRDKB(device)) {
	    deviceType = DeviceCategory.RDKB;
	} else if (SupportedModelHandler.isRDKC(device)) {
	    deviceType = DeviceCategory.RDKC;
	} else if (SupportedModelHandler.isECB(device)) {
	    deviceType = DeviceCategory.ECB;
	} else if (SupportedModelHandler.isNonRDKDevice(device)) {
	    deviceType = DeviceCategory.NON_RDK;
	} else if (SupportedModelHandler.isRDKVClient(device)) {
	    deviceType = DeviceCategory.RDKV_CLIENT;
	} else {
	    deviceType = DeviceCategory.RDKV_GATEWAY;
	}
	return deviceType;
    }

    /**
     * Update rdk portal or not
     * 
     * @return true rdk portal or not.
     */
    public static boolean isUpdateRdkPortal() {
	return Boolean.parseBoolean(System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_UPDATE_RDK_PORTAL, "false"));
    }

    /**
     * Update rdk portal or not
     * 
     * @return true rdk portal or not.
     */
    public static String getBuildAppender() {
	return System.getProperty(AutomaticsConstants.BUILD_APPENDER);
    }

    /**
     * Get the execution mode in which test to be executed
     * 
     * @return execution mode in which test to be executed
     */
    public static String getExecutionModeName() {
	return System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_EXECUTION_MODE);
    }

    /**
     * Get the execution mode in which test to be executed
     * 
     * @return execution mode in which test to be executed
     */
    public static ExecutionMode getExecutionMode() {
	String executionMode = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_EXECUTION_MODE,
		AutomaticsConstants.EMPTY_STRING);
	return ExecutionMode.getExecutionMode(executionMode);
    }

    /**
     * Get the test type
     * 
     * @return test type
     */
    public static String getTestTypeValue() {
	return System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE,
		AutomaticsConstants.EMPTY_STRING);
    }

    /**
     * Get the test type
     * 
     * @return test type
     */
    public static TestType getTestType() {
	String testFilterType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE,
		AutomaticsConstants.EMPTY_STRING);
	return TestType.valueOf(testFilterType);
    }

    /**
     * Get the build name
     * 
     * @return build name
     */
    public static String getBuildName() {
	return System.getProperty(AutomaticsConstants.BUILD_NAME_SYSTEM_PROPERTY, AutomaticsConstants.EMPTY_STRING);

    }

    /**
     * Get the device macs
     * 
     * @return device macs
     */
    public static String getCommaSepDeviceMac() {
	return System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SETTOP_LIST, AutomaticsConstants.EMPTY_STRING);

    }

    public static String getRackBaseUrl() {
	return AutomaticsPropertyUtility.getProperty("RACK_BASE_URL", AutomaticsConstants.EMPTY_STRING);

    }

    /**
     * Gets the base url for device manager
     * 
     * @return base url for device manager
     */
    public static String getDeviceManagerUrl() {
	return AutomaticsPropertyUtility.getProperty("DEVICE_MANAGER_BASE_URL", AutomaticsConstants.EMPTY_STRING);

    }

    /**
     * Gets the base url for AV analysis
     * 
     * @return base url for av analyzer
     */
    public static String getAVAnalyzerUrl() {
	return AutomaticsPropertyUtility.getProperty("AV_ANALYZER_BASE_URL", AutomaticsConstants.EMPTY_STRING);

    }

    /**
     * Gets the base url for power provider
     * 
     * @return base url for power provider
     */
    public static String getPowerProviderUrl() {
	return AutomaticsPropertyUtility.getProperty("POWER_PROVIDER_BASE_URL", AutomaticsConstants.EMPTY_STRING);

    }

    public static String getRestPathWithParamValues(String baseUrl, String path, Map<String, String> params) {
	String url = baseUrl;
	if (null != url & !url.trim().isEmpty()) {
	    for (String key : params.keySet()) {
		path = path.replace("{" + key + "}", params.get(key));
	    }
	    url += path;
	}
	return url;
    }

    /**
     * This method checks if there are any exclusion list defined in stb props.
     * 
     * @param testUID
     *            Test case ID
     * @return Status of verification
     */
    public static boolean isTestCaseExcluded(String testUID) {
	boolean shouldBlockCaAnalysis = false;
	if (CommonMethods.isNotNull(testUID)) {
	    String testCasesToExclude = AutomaticsPropertyUtility.getProperty("crash.analysis.exclusionlist");
	    String testCasesToExcludeRegex = AutomaticsPropertyUtility
		    .getProperty("crash.analysis.exclusionlist.regex");
	    LOGGER.debug("CrashAnalysis exclusiong list = {},{}" + testCasesToExclude, testCasesToExcludeRegex);
	    if (CommonMethods.isNotNull(testCasesToExclude) && testCasesToExclude.contains(testUID)) {
		shouldBlockCaAnalysis = true;
	    }
	    if (!shouldBlockCaAnalysis) {
		if (CommonMethods.isNotNull(testCasesToExcludeRegex)) {
		    String[] testCaseIdOrRegex = testCasesToExcludeRegex.split(AutomaticsConstants.COMMA);
		    for (String eachValue : testCaseIdOrRegex) {
			String testcase = eachValue.replaceAll("\\*", "").replaceAll("\\.", "");
			if (testUID.contains(testcase)) {
			    shouldBlockCaAnalysis = true;
			    break;
			}
		    }
		}
	    }
	} else {
	    shouldBlockCaAnalysis = true;
	}
	return shouldBlockCaAnalysis;
    }

    /**
     * Returns if crash analysis is enabled or not. Only if this API returns true, crash analysis will be performed by
     * core.
     * 
     * @return true if crash analysis is enabled, otherwise false
     */
    public static boolean isCrashAnalysisEnabled() {
	boolean performCrashAnalysis = false;
	if ("true".equals(AutomaticsPropertyUtility.getProperty(CrashConstants.PROPERTY_ENABLE_CRASH_ANALYSIS))) {
	    performCrashAnalysis = true;
	}
	return performCrashAnalysis;
    }

    /**
     * Returns true if automated issue management is enabled. This will allow automated tickets to be created during
     * failures.
     * 
     * @return true if automated issue management is enabled, otherwise false
     */
    public static boolean isAutomatedIssueManagementEnabled() {
	boolean performCrashAnalysis = false;
	if ("true".equals(
		AutomaticsPropertyUtility.getProperty(IssueManagementConstants.PROPERTY_ENABLE_ISSUE_MANAGEMENT))) {
	    performCrashAnalysis = true;
	}
	return performCrashAnalysis;
    }
    
    /**
     * Gets the base url for device connection provider
     * 
     * @return base url for device connection provider
     */
    public static String getDeviceConnectionProviderUrl() {
	return AutomaticsPropertyUtility.getProperty("DEVICE_CONNECTION_PROVIDER_URL", AutomaticsConstants.EMPTY_STRING);

    }

}
