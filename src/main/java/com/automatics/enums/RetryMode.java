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
 * Defines the retry behavior if the global retry settings flags are used
 * 
 * @author nagendra
 *
 */
public enum RetryMode {
    /**
     * Retry will be attempted even if the default retry behaviour is false. This is applicable only if global retry
     * settings are used
     */
    FORCETRUE("FORCETRUE"),

    /**
     * Retry will not be attempted even if the default retry behaviour is true. This is applicable only if global retry
     * settings are used
     */
    FORCEFALSE("FORCEDFALSE"),

    /** Retry will be attempted if global setting is not false, if present */
    TRUE("TRUE"),

    /** Retry will not be attempted if global setting is false */
    FALSE("FALSE");

    String retryMode;

    private RetryMode(final String value) {
	retryMode = value;
    }

    /**
     * Gets the String value associated with this enum
     * 
     * @return String value
     */
    public String getValue() {
	return retryMode;
    }

    /**
     * String representation of the enum
     * 
     * @return String value
     */
    @Override
    public String toString() {
	return retryMode;
    }
}
