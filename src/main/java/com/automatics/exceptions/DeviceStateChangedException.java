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
package com.automatics.exceptions;

import com.automatics.error.ErrorType;

/**
 * Custom Exception thrown during device state changes while test is running , is defined here. DeviceConfig build, Av , any
 * other pre-set param required for tests is found to be changed while tests is running,this exceptions has to be thrown
 * 
 * @author rohinic
 *
 */
public class DeviceStateChangedException extends RuntimeException {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception object with message.
     *
     * @param message
     *            Message to displayed.
     */
    public DeviceStateChangedException(String message) {
	super(message);
    }

    /**
     * Creates a new exception object with message and error type.
     *
     * @param errorType
     *            The {@link ErrorType}.
     * @param message
     *            Message to displayed.
     */
    public DeviceStateChangedException(ErrorType errorType, String message) {
	super(errorType + message);
    }

    /**
     * Creates a new exception object with message, error type and received exception.
     *
     * @param errorType
     *            The {@link ErrorType}
     * @param message
     *            Message to displayed.
     * @param cause
     *            Exception captured that need to be bundled.
     */
    public DeviceStateChangedException(ErrorType errorType, String message, Throwable cause) {
	super(errorType + message, cause);
    }

    /**
     * Creates a new exception object with message from error type.
     *
     * @param errorType
     *            The {@link ErrorType}
     * @param cause
     *            Exception captured that need to be bundled.
     */
    public DeviceStateChangedException(ErrorType errorType, Throwable cause) {
	super(errorType + "", cause);
    }
}
