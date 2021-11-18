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

import java.lang.reflect.Method;

import com.automatics.annotations.TestDetails;
import com.automatics.utils.RetryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

/**
 * This class implements the retry mechanism for test methods which require retry.
 *
 * @author nagendra
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int count = 0;

    /**
     * Checks whether the test should be retried. Test retry depends on the global retry flag and test level retry flag.
     * Test will not be retried if the maximum retry attempt exceeds
     *
     * @param result
     *            ITestResult object provided by TestNG call-back
     *
     * @return true
     */
    public boolean retry(ITestResult result) {
	LOGGER.info("Going to Retry " + count);
	ITestNGMethod method = result.getMethod();
	ConstructorOrMethod constructorOrMethod = method.getConstructorOrMethod();
	Method testMethod = constructorOrMethod.getMethod();
	TestDetails testDetailsAnnotation = testMethod.getAnnotation(TestDetails.class);
	int retryCount = testDetailsAnnotation.retryCount();
	/*
	 * Checks to see if the test belongs to RETRY_GROUP. Test will be retried based on the global retry flag and the
	 * retry mode specified for the test
	 */
	if (RetryUtils.shouldTestBeRetried(result)) {
	    // Retry validation logic
	    int currentRetry = RetryUtils.getRetriesFromTestResult(result);
	    /* Current retry will be -1 if the test method is not a subclass of AutomaticsTestBase */
	    LOGGER.info("Retry Attempt #" + count + " - Retrying " + result.getName());
	    return (currentRetry != -1 && (retryCount >= currentRetry));
	}

	return false;
    }
}
