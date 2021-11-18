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

import com.automatics.error.IError;

/**
 * Runtime exception to handle the failures in any of the transitions.
 */
public class FailedTransitionException extends RuntimeException {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Detail of error occurred. */
    private IError error = null;

    /**
     * Creates a new exception object with error object.
     *
     * @param error
     *            Categorized cause of error.
     */
    public FailedTransitionException(IError error) {
	super(error.getMessage());
	this.error = error;
    }

    /**
     * Creates a new exception object with message and error object.
     *
     * @param error
     *            Categorized cause of error.
     * @param message
     *            Message to displayed.
     */
    public FailedTransitionException(IError error, String message) {
	super(error.getMessage() + message);
	this.error = error;
    }

    /**
     * Creates a new exception object with message, error object and received exception.
     *
     * @param error
     *            Categorized cause of error.
     * @param message
     *            Message to displayed.
     * @param cause
     *            Exception captured that need to be bundled.
     */
    public FailedTransitionException(IError error, String message, Throwable cause) {
	super(error.getMessage() + message, cause);
	this.error = error;
    }

    /**
     * Creates a new exception object with message from error object.
     *
     * @param error
     *            Categorized cause of error.
     * @param cause
     *            Exception captured that need to be bundled.
     */
    public FailedTransitionException(IError error, Throwable cause) {
	super(error.getMessage(), cause);
	this.error = error;
    }

    /**
     * Provides the error detail of the exception occurred.
     *
     * @return Detailed error information.
     */
    public IError getError() {
	return error;
    }
}
