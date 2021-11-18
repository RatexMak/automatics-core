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
 * Options to restart process
 * 
 * @author styles mangalasseri, TATA Elxsi
 * 
 */
public enum ProcessRestartOption {

    /** Option to restart using 'killall <process name>' command */
    KILL_ALL("killall "),
    /** Option to restart using 'killall -9 <process name>' command */
    KILL_ALL_9("killall -9 "),
    /** Option to restart using 'killall -11 <process name>' command */
    KILL_ALL_11("killall -11 "),
    /** Option to restart using kill -8 <process id> command */
    KILL_8("kill -8 "),
    /** Option to restart using kill -9 <process id> command */
    KILL_9("kill -9 "),
    /** Option to restart using kill -11 <process id> command */
    KILL_11("kill -11 ");

    /** Linux command */
    private String command;

    private ProcessRestartOption(String command) {
	this.command = command;
    }

    public String getCommand() {
	return this.command;
    }
}
