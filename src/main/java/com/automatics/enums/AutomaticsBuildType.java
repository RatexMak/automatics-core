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
 * ECats Build type enumeration to identify the build type.
 *
 * @author Selvaraj Mariyappan
 */
public enum AutomaticsBuildType {

    /** Build type - RDK. */
    RDK("rdk"),

    /** Build type - Ocap. */
    OCAP("ocap");

    /** Build type instance. */
    private String buildType;

    /**
     * Parameterized constructor.
     *
     * @param buildType
     */
    private AutomaticsBuildType(String buildType) {
	this.buildType = buildType;
    }

    /**
     * Get device type of particular box.
     *
     * @return device type
     */
    public String getType() {
	return buildType;
    }

}
