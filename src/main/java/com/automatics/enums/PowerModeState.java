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
 * Various power mode values.
 */

public enum PowerModeState {

    /** On state. */
    ON("ON"),

    /** Off state. */
    OFF("OFF"),

    /** Standby state. */
    STANDBY("STANDBY"),

    /** deep sleep state. */
    DEEPSLEEP("DEEPSLEEP"),

    /** light sleep state. */
    LIGHTSLEEP("LIGHTSLEEP"),

    /**
     * Enter passive standby (passive but wakes for system updates)
     */
    SUPERECO("supereco"),

    /** active – enter active standby (awake, with a black screen shown) */
    ACTIVE("active"),

    /** wake sleep state. */
    WAKE("wake");

    /** The current state. */
    private String state = "";

    /**
     * Constructor with single parameter.
     * 
     * @param state
     *            current state
     */
    private PowerModeState(String state) {
	this.state = state;
    }

    /**
     * To get the current state.
     * 
     * @return the current state
     */
    public String getState() {
	return this.state;
    }
}