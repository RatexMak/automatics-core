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

package com.automatics.utils.tr69;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.tap.AutomaticsTapApi;

public class Tr69CommandHandler {

    /** SLF4j logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tr69CommandHandler.class);

    public static String getValue(AutomaticsTapApi automaticsTapApi, Dut dut, String reqdCommand) {
	return executeTr69Command(automaticsTapApi, dut, Tr69Constants.TR69_COMMAND_FOR_GET_OPERATION, reqdCommand, null);

    }

    public static String setValue(AutomaticsTapApi automaticsTapApi, Dut dut, String reqdCommand, String setValue) {
	return executeTr69Command(automaticsTapApi, dut, Tr69Constants.TR69_COMMAND_FOR_SET_OPERATION, reqdCommand,
		setValue);
    }

    /**
     * Method to execute TR69 command to get he.
     *
     * @param automaticsTapApi
     * @param dut
     * @param command
     * @param getValue
     *
     * @return
     */
    public static String executeTr69Command(AutomaticsTapApi automaticsTapApi, Dut dut, String tr69command,
	    String reqdCommand, String setValue) {
	String executeCommand = Tr69Constants.TR69_COMMAND_FORMAT_HOSTNAME + " " + dut.getHostIp4Address() + " "
		+ tr69command + " " + reqdCommand;

	if (Tr69Constants.TR69_COMMAND_FOR_SET_VALUE == tr69command) {
	    executeCommand += " " + Tr69Constants.TR69_COMMAND_FOR_SET_VALUE + " " + setValue;
	}

	String commadResult = automaticsTapApi.executeCommandUsingSsh(dut, executeCommand);
	LOGGER.info("Result: " + commadResult);

	return parseTr69Command(commadResult);
    }

    /**
     * Helper method to parse TR 69 command.
     *
     * @param commandResult
     *            The result string
     *
     * @return the result
     */
    private static String parseTr69Command(String commandResult) {
	String value = null;
	String reqEx = "Value\\s+:\\s+([\\w\\W]+)";

	if (null != commandResult && !commandResult.isEmpty()) {
	    Pattern pattern = Pattern.compile(reqEx);

	    Matcher matcher = pattern.matcher(commandResult);

	    if (matcher.find()) {
		value = matcher.group(1);
		value = value.replaceAll(AutomaticsConstants.SYMBOL_QUOTES, AutomaticsConstants.EMPTY_STRING);
		value = value.replaceAll(AutomaticsConstants.DELIMITER_HASH, AutomaticsConstants.EMPTY_STRING);
		value = value.replaceAll(AutomaticsConstants.NEW_LINE, AutomaticsConstants.EMPTY_STRING);
	    }
	}

	return value;
    }

}
