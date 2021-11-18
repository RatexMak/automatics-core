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
 * Enumerator representing the Execution mode
 * 
 * @author styles mangalasseri, TATA Elxsi
 *
 */
public enum ExecutionMode {

    UNKNOWN("UNKNOWN"),   
    RDKV("RDKV"),
    RDKB("RDKB"),
    MODE_FAILURE("MODE_FAILURE");

    /** Execution mode */
    private String mode;

    ExecutionMode(String type) {
	this.mode = type;
    }

    /**
     * Method to get execution mode
     * 
     * @return execution mode in String format
     */
    public String get() {
	return mode;
    }

    /**
     * Method to check whether the execution mode is equal to other
     * 
     * @param model
     *            Execution mode
     * @return true if both execution model is equal else false
     */
    public boolean equals(ExecutionMode model) {

	boolean status = false;

	if (model != null) {
	    status = this.mode.equals(model.get());
	}

	return status;
    }

    /**
     * 
     * Method to fetch executionMode
     * 
     * @param executionMode
     * @return
     */
    public static ExecutionMode getExecutionMode(String executionMode) {
	ExecutionMode execMode = ExecutionMode.RDKV;
	if (executionMode != null) {
	    execMode = Enums.getIfPresent(ExecutionMode.class, executionMode).orNull();
	}
	return execMode;
    }
}
