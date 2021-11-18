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

package com.automatics.enums;

import com.google.common.base.Enums;

/**
 * Enumerator representing the test types.
 *
 * @author nagendra
 */
public enum AutomaticsTestTypes {

    /** Functional test. */
    FUNCTIONAL("FUNCTIONAL"),

    /** Smoke test. */
    SMOKE("SMOKE"),

    /** Sanity test. */
    SANITY("SANITY"),   

    /** Performance test. */
    PERFORMANCE("PERFORMANCE"),

    /** Full test. */
    FULL("FULL"),

    /** unmapped test. may be a added to testing cycle later. */
    UNMAPPED("UNMAPPED"),

    /** Defect automation test. may be a added to testing cycle later. */
    DEFECT_AUTOMATION("DEFECT_AUTOMATION"),

    /** Manual test, cannot be added as part of CI build. */
    MANUAL("MANUAL"),

    /** Test group for alive tests */
    SANITY_ALIVE("ALIVE"),

    /** Test group for quick tests */
    QUICK("QUICK"),

    /** Test Group for quick test CI builds */
    QUICK_CI("QUICK_CI"),

    /** Test group for witbee quick test */
    QUICK_WITBE("QUICK_WITBE"),

    /** Test group for RACK quick test */
    QUICK_RACK("QUICK_RACK"),   

    /** Test Group for Native flip test */
    NATIVE_FLIP("NATIVE_FLIP"),
    FAST_QUICK_CI("FAST_QUICK_CI"),

    FAST_QUICK("FAST_QUICK"),

    ACCEPTANCE_PENDING("ACCEPTANCE_PENDING"),

    ACCEPTANCE_WIFI("ACCEPTANCE_WIFI"),

    PP("PP"),

    INTERNAL("INTERNAL");

    private String testType;

    private AutomaticsTestTypes(final String testType) {
	this.testType = testType;
    }

    public String value() {
	return testType;
    }

    @Override
    public String toString() {
	return testType;
    }

    public static boolean isQt(String testTypeValue) {
	boolean isQt = false;
	AutomaticsTestTypes testTypeEnum = Enums.getIfPresent(AutomaticsTestTypes.class, testTypeValue).orNull();
	if (QUICK.equals(testTypeEnum) || QUICK_CI.equals(testTypeEnum) || QUICK_RACK.equals(testTypeEnum)
		|| FAST_QUICK.equals(testTypeEnum) || FAST_QUICK_CI.equals(testTypeEnum)) {
	    isQt = true;
	}
	return isQt;
    }

    public static boolean isIterative(String testType) {
	boolean isIterative = false;
	return isIterative;
    }
}
