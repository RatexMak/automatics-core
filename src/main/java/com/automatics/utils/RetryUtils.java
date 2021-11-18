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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import com.automatics.annotations.TestDetails;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.RetryMode;
import com.automatics.test.AutomaticsTestBase;

/**
 * @author nagendra
 */
public class RetryUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryUtils.class);
    private static boolean globalRetryFlag = true;
    private static boolean isRetryModeSet = true;
    // Read the global retry settings
    static {
	String defaultRetryString = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_RETRY_TESTS);
	LOGGER.info("The default retry system property is " + defaultRetryString);
	if (null != defaultRetryString && !defaultRetryString.isEmpty()
		&& (defaultRetryString.equalsIgnoreCase(AutomaticsConstants.STRING_TRUE))) {
	    isRetryModeSet = true;
	    globalRetryFlag = Boolean.valueOf(defaultRetryString);
	} else {
	    isRetryModeSet = false;
	}
    }

    /*
     * The constructor is being hidden to avoid creation of this class
     */
    private RetryUtils() {
	/* Empty constructor */
    }

    /**
     * This method analyses the test result and determines whether the test belongs to retry group or not. <br>
     * If global retry setting is false then only the tests those have retry mode set as FORCETRUE will be retried.<br>
     * If global retry setting is true, all tests except the tests that have retry mode set as FORCEFALSE will be
     * retried
     *
     * @param tr
     *            - Test result object
     *
     * @return true/false
     */
    public static boolean shouldTestBeRetried(ITestResult tr) {
	ITestNGMethod method = tr.getMethod();
	ConstructorOrMethod constructorOrMethod = method.getConstructorOrMethod();
	Method testMethod = constructorOrMethod.getMethod();
	TestDetails testDetailsAnnotation = testMethod.getAnnotation(TestDetails.class);
	boolean result = false;
	if (isRetryModeSet) {
	    if (globalRetryFlag) {
		/*
		 * if global settings for retry is true and retry mode of test is forced false then retry should not be
		 * attempted otherwise test should be retried
		 */
		result = !(testDetailsAnnotation.retryMode() == RetryMode.FORCEFALSE);
	    } else {
		/*
		 * if global settings for retry id false and retry mode of test is forced true then retry should be
		 * attempted otherwise test should not be retried
		 */
		result = (testDetailsAnnotation.retryMode() == RetryMode.FORCETRUE);
	    }
	} else {
	    /*
	     * If global retry flags are not set then obey the retry flags specified in the test level.
	     */
	    if (testDetailsAnnotation.retryMode() == RetryMode.FORCEFALSE
		    || testDetailsAnnotation.retryMode() == RetryMode.FALSE) {
		result = false;
	    } else {
		result = true;
	    }
	}
	return result;
    }

    /**
     * Returns number of retries from test result.
     *
     * @param tr
     *            - Test Result object
     *
     * @return retry count
     */
    public static int getRetriesFromTestResult(ITestResult tr) {

	ITestNGMethod method = tr.getMethod();
	ConstructorOrMethod constructorOrMethod = method.getConstructorOrMethod();
	LOGGER.info("&& TEST Method class " + constructorOrMethod.getDeclaringClass().getName());
	LOGGER.info("&& TEST Method Super class " + constructorOrMethod.getDeclaringClass().getSuperclass().getName());
	Class tempClass = constructorOrMethod.getDeclaringClass();
	int result = -1;

	// Check whether the class is a subclass of AutomaticsTestBase
	try {
	    tempClass.asSubclass(AutomaticsTestBase.class);
	    result = method.getCurrentInvocationCount();
	} catch (ClassCastException ex) {
	    LOGGER.info("Test class: " + tr.getName()
		    + " doesn't extend from the base test of the framework. Disabling test retries...");
	}
	LOGGER.info("&& Current Invocation count = " + result);
	return result;
    } // end method getRetriesFromTestResult
}
