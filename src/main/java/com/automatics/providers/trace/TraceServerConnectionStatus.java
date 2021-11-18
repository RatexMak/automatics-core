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
/**
 *
 */
package com.automatics.providers.trace;

/**
 * @author nagendra
 */
public enum TraceServerConnectionStatus {

    /** Trace connection established. */
    CONNECTED(1),

    /** Trace connection in progress. */
    CONNECTING(0),

    /** Trace connection lost. */
    DISCONNECTED(-1);

    private int status;

    private TraceServerConnectionStatus(int status) {
	this.status = status;
    }

    /**
     * Value associated with this enum.
     *
     * @return value
     */
    public int getStatus() {
	return status;
    }
}
