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
 * ECats Execute On Type enumeration to identify the box on which its need to be executed.
 *
 * @author Arjun P
 */
public enum ExecuteOnType {

    /** Execute On - Gateway. */
    GATEWAY("gateway"),

    /** Execute On - default configured Box. */
    DEFAULT("default");

    /** Build type instance. */
    private String executeOn;

    /**
     * Parameterized constructor.
     *
     * @param buildType
     */
    private ExecuteOnType(String executeOn) {
	this.executeOn = executeOn;
    }

    /**
     * Get device type of particular box.
     *
     * @return device type
     */
    public String executeOn() {
	return executeOn;
    }

}
