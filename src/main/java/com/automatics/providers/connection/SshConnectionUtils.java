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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LinuxCommandConstants;
import com.jcraft.jsch.JSchException;

public class SshConnectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshConnectionUtils.class);

    public static String executeCommand(SshConnection sshConnection, String command, long respTimeout)
	    throws IOException, InterruptedException, JSchException {
	sshConnection.send(command + AutomaticsConstants.NEW_LINE, (int) (respTimeout));

	return sshConnection.getSettopResponse(respTimeout);
    }

    public static String executeCommand(SshConnection sshConnection, String command, long respTimeout,
	    boolean bufferResponse) throws IOException, InterruptedException, JSchException {
	String response = "";
	sshConnection.send(command + AutomaticsConstants.NEW_LINE, (int) (respTimeout));
	if (bufferResponse) {
	    sshConnection.bufferResponse();
	} else {
	    response = sshConnection.getSettopResponse(respTimeout);
	}

	return response;
    }

    public static String executeCommand(SshConnection sshConnection, String command, String expectStr, String[] options)
	    throws IOException, InterruptedException, JSchException {
	return sshConnection.send(command, expectStr, options);

    }

    /**
     * Method to check if the command requires more wait time to get response
     * 
     * @param commandToBeExecuted
     * @return
     */
    public static boolean isCommandNeedMoreWaitTime(String commandToBeExecuted) {
	boolean status = false;

	for (String command : LinuxCommandConstants.ARRAY_COMMANDS_LONG_RESPONSE_TIME) {
	    if (commandToBeExecuted.contains(command)) {
		status = true;
	    }
	}
	LOGGER.debug("isCommandNeedMoreWaitTime - " + status);
	return status;
    }

}
