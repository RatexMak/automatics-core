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

public enum TestType {

    QUICK("qt"),

    QUICK_RACK("qt"),

    QUICK_CI("qt"),

    SANITY("1h"),

    SANITY_XI3("1h"),

    SMOKE("4h"),    

    FUNCTIONAL("2d"),    

    DEFECT_AUTOMATION("defect_automation"),

    UNMAPPED("unmapped"),

    NEW_FEATURE("NEW_FEATURE"),

    SUPPLEMENTARY("SUPPLEMENTARY"),

    GROUP_OR_AUTOID("GROUP_OR_AUTOID"),

    PERFORMANCE("PERFORMANCE"),

    NATIVE_FLIP("NATIVE_FLIP"),   

    FAST_QUICK_CI("fast_qt"),

    FAST_QUICK("fast_qt"),

    ACCEPTANCE_PENDING("ACCEPTANCE_PENDING"),

    ACCEPTANCE_WIFI("ACCEPTANCE_WIFI");    

    private String type;

    private TestType(String type) {
	this.type = type;
    }

    public String get() {
	return type;
    }

    public static TestType getIfPresent(String testTypeValue) {
	TestType testTypeEnum = Enums.getIfPresent(TestType.class, testTypeValue).orNull();
	return testTypeEnum;
    }

    public static boolean isQt(String testTypeValue) {
	boolean isQt = false;
	if (testTypeValue != null) {
	    TestType testTypeEnum = Enums.getIfPresent(TestType.class, testTypeValue).orNull();
	    if (QUICK.equals(testTypeEnum) || QUICK_CI.equals(testTypeEnum) || QUICK_RACK.equals(testTypeEnum)
		    || FAST_QUICK.equals(testTypeEnum) || FAST_QUICK_CI.equals(testTypeEnum)) {
		isQt = true;
	    }
	}
	return isQt;
    }   
}
