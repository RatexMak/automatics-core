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

/**
 * Enumerator representing the test step status.
 *
 * @author nipun
 */
public enum ExecutionStatus {

    PASSED("PASS", 0),
    FAILED("FAIL", 1),
    PRECONDITION_FAILED("Pre-Fail", 2),
    NOT_TESTED("NT", 3),
    NOT_RUN("NR", 4),
    NOT_APPLICABLE("NA", 5);

    String status;
    int value;

    private ExecutionStatus(String status, int value) {
	this.status = status;
	this.value = value;
    }

    public String getStatus() {
	return status;
    }

    public int getValue() {
	return value;
    }

    public static ExecutionStatus getExecutionStatus(String statusValue) {

	for (ExecutionStatus executionStatus : ExecutionStatus.values()) {
	    if (executionStatus.getStatus().equals(statusValue)) {
		return executionStatus;
	    }
	}

	return null;
    }
}
