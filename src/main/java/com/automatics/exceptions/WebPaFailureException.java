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

/**
 * Customized exception for WebPa failures
 * 
 * @author divya.rs
 *
 */
public class WebPaFailureException extends Exception {

    /**
     * Generated serial Version UID
     */
    private static final long serialVersionUID = -8274836171500288893L;

    public WebPaFailureException() {
	super("Unable to execute webPa commands...!!!");
    }

    /**
     * Creates a new exception object with message.
     *
     * @param message
     *            Message to displayed.
     */
    public WebPaFailureException(String message) {
	super(message);
    }

}
