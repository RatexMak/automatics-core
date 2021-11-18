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
package com.automatics.providers.crashanalysis;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.CrashConstants;
import com.automatics.device.Dut;
import com.automatics.utils.AutomaticsPropertyUtility;

/**
 * Abstract class for crash analysis
 * 
 * @author Radhika
 *
 */
public abstract class AbstractCrashAnalysisProvider implements CrashAnalysisProvider {

    public static final String CRASH_REGEX_STRING = "(?i:minidump.*?SUCCESS|coredump.*?SUCCESS|minidump.*?SUCESS|coredump.*?SUCESS)";

    /**
     * Returns if crash analysis is enabled or not. Only if this API returns true, crash analysis will be performed by
     * core.
     * 
     * @param device
     * @return true if crash analysis is enabled, otherwise false
     */
    public boolean isCrashAnalysisEnabled(Dut device) {
	boolean performCrashAnalysis = false;
	if (AutomaticsConstants.BOOL_TRUE.equals(AutomaticsPropertyUtility
		.getProperty(CrashConstants.PROPERTY_ENABLE_CRASH_ANALYSIS))) {
	    performCrashAnalysis = true;
	}
	return performCrashAnalysis;
    }

    /**
     * Regular expression to verify if crash has occurred during trace monitoring
     */
    @Override
    public String getRegexForCrashLog(Dut device) {
	return CRASH_REGEX_STRING;
    }

}
