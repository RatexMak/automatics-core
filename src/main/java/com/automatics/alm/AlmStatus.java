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

/**
 * Enum that holds the ALM status
 * 
 * @author surajmathew
 */
public enum AlmStatus {

    BLOCKED("Blocked"),
    FAILED("Failed"),
    NOT_APPLICABLE("N/A"),
    NO_RUN("No Run"),
    NOT_COMPLETED("Not Completed"),
    PASSED("Passed");

    String value;

    private AlmStatus(String value) {
	this.value = value;
    }

    public String getValue() {
	return this.value;
    }
}
