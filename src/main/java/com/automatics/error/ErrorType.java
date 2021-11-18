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

package com.automatics.error;

/**
 * List of error types.
 */
public enum ErrorType {

    OCR,
    IC,
    SNMP,
    IR_KEY,
    TRACE,
    RESOURCE_NOT_FOUND,
    CONNECTION,
    CONSOLE_OUTPUT,
    RESOURCE_KEY_NOT_FOUND,
    RESOURCE_NOT_READ,
    T2P,
    PROVIDER_ALLOCATION,
    JSON,
    DAC_RESPONSE,
    CONFIGURATION,
    EAS_RESPONSE,
    PRE_CONDITION,    
    ACS_COMMINICATION_ERROR,
    WEB_PA_COMMINICATION_ERROR,
    SECURITY;

    /** String to be suffixed with error type. */
    public static final String SUFFIX_ERROR_TYPE = ": ";

    /**
     * Return the classified error type.
     */
    @Override
    public String toString() {
	return super.toString() + SUFFIX_ERROR_TYPE;
    }
}
