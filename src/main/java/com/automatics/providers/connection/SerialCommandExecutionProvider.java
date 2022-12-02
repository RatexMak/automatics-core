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
package com.automatics.providers.connection;

import java.util.List;

import com.automatics.device.Dut;

/**
 * 
 * Provider for executing commands from serial console
 *
 */
public interface SerialCommandExecutionProvider {

    /**
     * Executes the command on serial console of the device
     * 
     * @param dut
     * @param command
     * @param timeout
     * @return Command execution response
     */
    public String executeCommandInSerialConsole(Dut dut, String command, long timeout);

    /**
     * Executes the commands on serial console of the device
     * 
     * @param dut
     * @param command
     * @param timeout
     * @return Command execution responses
     */
    public List<String> executeCommandsInSerialConsole(Dut dut, List<String> commandList, long timeout);

}
