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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Device;
import com.automatics.device.Dut;

/**
 * Abstract class for SerialCommandExecutionProvider
 *
 */
public abstract class AbstractSerialCommandExecutionProvider implements SerialCommandExecutionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSerialCommandExecutionProvider.class);

    /**
     * Executes in serial console of device
     */
    @Override
    public List<String> executeCommandsInSerialConsole(Dut dut, List<String> commandList, long timeout) {

	List<String> responseList = new ArrayList<String>();
	String response = null;
	Device device = (Device) dut;
	for (String command : commandList) {
	    LOGGER.info("Executing command on serial console of device {}: {}", device.getHostMacAddress(), command);
	    response = executeCommandInSerialConsole(dut, command, timeout);
	    LOGGER.info("Response: {}", response);
	    responseList.add(response);
	}
	return responseList;
    }

}
