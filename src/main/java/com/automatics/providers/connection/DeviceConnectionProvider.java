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

import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.resource.IServer;

/**
 * 
 * Provider for device connection
 *
 */
public interface DeviceConnectionProvider {

    /**
     * Copy file to device
     * @param device
     * @param fileToCopy
     * @param remoteLocation
     * @return
     */
    public boolean copyFile(Device device, String fileToCopy, String remoteLocation);

    /**
     * To get connection to device
     * @param device
     * @return
     */
    public Connection getConnection(Device device);

    /**
     * Execute commands in device
     * @param device
     * @param command
     * @return
     */
    public String execute(Device device, String command);

    /**
     * Execute commands in device
     * @param device
     * @param commandList
     * @return
     */
    public String execute(Device device, List<String> commandList);

    /**
     * Execute commands in device
     * @param device
     * @param executeCommandType
     * @param commandList
     * @return
     */
    public String execute(Device device, ExecuteCommandType executeCommandType, List<String> commandList);

    /**
     * Execute commands in device
     * @param dut
     * @param command
     * @param expectStr
     * @param options
     * @return
     */
    public String execute(Dut dut, String command, String expectStr, String[] options);

    /**
     * Execute commands in device
     * @param dut
     * @param atomServerIp
     * @param command
     * @return
     */
    public String executeInsideAtomConsoleUsingExpect(Dut dut, String atomServerIp, String command);

    /**
     * Execute commands in given device console
     * @param device
     * @param command
     * @param consoleType
     * @param timeOutMilliSecs
     * @return
     */
    public String execute(Device device, String command, DeviceConsoleType consoleType, long timeOutMilliSecs);

    /**
     * Execute commands in given device console
     * @param device
     * @param commandList
     * @param consoleType
     * @return
     */
    public String execute(Device device, List<String> commandList, DeviceConsoleType consoleType);

    /**
     * Execute commands in given device console
     * @param device
     * @param commandList
     * @param consoleType
     * @param timeOutMilliSecs
     * @return
     */
    public String execute(Device device, List<String> commandList, DeviceConsoleType consoleType, long timeOutMilliSecs);

    /**
     *  Execute commands using given device connection
     * @param device
     * @param deviceConnnection
     * @param command
     * @return
     */
    public String execute(Device device, Connection deviceConnnection, String command);

    /**
     * Execute commands using given device connection
     * @param device
     * @param deviceConnnection
     * @param executeCommandType
     * @param command
     * @return
     */
    public String execute(Device device, Connection deviceConnnection, ExecuteCommandType executeCommandType,
	    String command);

    /**
     * Execute commands on given host
     * @param hostDetails
     * @param commands
     * @param timeOutMilliSecs
     * @return
     */
    public String execute(IServer hostDetails, List<String> commands, long timeOutMilliSecs);

    /**
     * Execute commands on given host
     * @param hostIp
     * @param command
     * @param timeOutMilliSecs
     * @param connectionType
     * @return
     */
    public String execute(String hostIp, String command, long timeOutMilliSecs, String connectionType);
}
