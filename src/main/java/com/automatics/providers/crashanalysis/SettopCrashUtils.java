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
package com.automatics.providers.crashanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.CrashConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.core.DeviceProcess;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Dut;
import com.automatics.enums.CrashFileGenerationDelay;
import com.automatics.enums.ProcessRestartOption;
import com.automatics.enums.StbProcess;
import com.automatics.exceptions.TestException;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;

/**
 * Device Crash Utils Class which contains the utility methods for crash related operations like crash
 * generation/verification etc..
 * 
 * @author Surya Teja
 *
 */
public class SettopCrashUtils {

    /** Text to retrieve crash file name from core_log.txt */
    public static final String TEXT_TO_RETRIEVE_CRASH_FILE_NAME_FROM_LOG_GILE = "Success Compressing the files,";

    /** Success string to verify the crash file upload */
    public static final String CORE_FILE_UPLOAD_SUCCESS_STRING = "Success uploading file";

    /** Success string to verify the core dump file upload */
    public static final String CORE_DUMP_UPLOAD_SUCCESS_STRING = "S3 coredump Upload is successful with TLS1.2";

    /** Success string to verify the mini dump file upload */
    public static final String MINI_DUMP_UPLOAD_SUCCESS_STRING = "S3 minidump Upload is successful with TLS1.2";

    /** server response if no process will killed */
    private static final String SERVER_RESPONSE_IF_NO_PROCESS_KILLED = "no process killed";

    /** variable to hold the value for process name */
    private static final String PROCESS_NAME = "<process_name>";

    /** Command to kill process */
    private static final String COMMAND_TO_KILL_PROCESS = "killall -11 " + PROCESS_NAME;

    /** Temporary core log folder path */
    private static final String CORE_LOG_FOLDER_TEMP_PATH = "/nvram/automation";

    /** Temporary core log folder path in RDKC */
    public static final String CORE_LOG_FOLDER_TEMP_PATH_RDKC = "/opt/automation";

    /** Previous log folder path in RDKV */
    public static final String PREVIOUS_LOG_FOLDER_PATH = "/opt/logs/PreviousLogs";

    /** Temporary core log file path */
    public static final String CORE_LOG_FILE_TEMP_PATH = CORE_LOG_FOLDER_TEMP_PATH + "/core_log.txt";

    /** Temporary core log file path */
    public static final String CORE_LOG_FILE_TEMP_PATH_RDKC = CORE_LOG_FOLDER_TEMP_PATH_RDKC + "/core_log.txt";

    /** Temporary core log file path in RDKV */
    public static final String CORE_LOG_FILE_TEMP_PATH_IN_RDKV = PREVIOUS_LOG_FOLDER_PATH + "/core_log.txt";

    private CrashAnalysisProvider crashAnalysisProvider;

    /** property keys to store crash details */
    public static final String PROPERTY_KEY_BUILD_ID = "PROPERTY_KEY_BUILD_ID";
    public static final String PROPERTY_KEY_CRASH_FILE_NAME = "PROPERTY_KEY_CRASH_FILE_NAME";
    public static final String PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS = "PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS";

    /** SLF4j logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SettopCrashUtils.class);

    public SettopCrashUtils() {
	// crash analysis provider
	crashAnalysisProvider = BeanUtils.getCrashAnalysisProvider();
    }

    /**
     * Method to perform pre-condition for crash verification
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            Instance of {@link Dut}
     * @param crashFolderPath
     *            crash folder path
     * 
     * @throws TestException
     *             during validation failures
     */
    public void perfomPreConditionForCrashVerification(AutomaticsTapApi tapApi, Dut dut) throws TestException {

	LOGGER.debug("Entering into perfomPreConditionForCrashVerification()");

	LOGGER.info("Executing precondition for crash details verification");

	if (SupportedModelHandler.isRDKV(dut)) {
	    // empty crash log file in RDKV
	    tapApi.executeCommandUsingSsh(dut, "> " + getCoreLogPath(dut) + ";mkdir -p " + PREVIOUS_LOG_FOLDER_PATH
		    + "; > " + CORE_LOG_FILE_TEMP_PATH_IN_RDKV);
	} else if (SupportedModelHandler.isRDKB(dut)) {
	    // empty crash log file
	    tapApi.executeCommandUsingSsh(dut, "> " + getCoreLogPath(dut) + ";mkdir -p " + CORE_LOG_FOLDER_TEMP_PATH
		    + "; > " + CORE_LOG_FILE_TEMP_PATH);
	} else if (SupportedModelHandler.isRDKC(dut)) {
	    // empty crash log file
	    tapApi.executeCommandUsingSsh(dut, "> " + getCoreLogPath(dut) + ";mkdir -p "
		    + CORE_LOG_FOLDER_TEMP_PATH_RDKC + "; > " + CORE_LOG_FILE_TEMP_PATH_RDKC);
	}

	LOGGER.debug("Exiting from perfomPreConditionForCrashVerification()");
    }

    /**
     * Method to perform pre-condition for crash verification in Atom console
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            Instance of {@link Dut}
     * @param crashFolderPath
     *            crash folder path
     */
    public void perfomPreConditionForCrashVerificationAtomConsole(AutomaticsTapApi tapApi, Dut dut)
	    throws TestException {

	LOGGER.debug("STARTING METHOD: perfomPreConditionForCrashVerificationAtomConsole()");

	LOGGER.info("Executing precondition for crash details verification in ATOM Console");

	CommonMethods.executeCommandInAtomConsole(dut, tapApi, "> " + getCoreLogPath(dut) + ";mkdir -p "
		+ CORE_LOG_FOLDER_TEMP_PATH + "; > " + CORE_LOG_FILE_TEMP_PATH);

	LOGGER.debug("ENDING METHOD: perfomPreConditionForCrashVerificationAtomConsole()");
    }

    /**
     * Method to retrieve crash file name from mini dump folder
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param crashFolderPath
     *            crash folder path
     * @param stbProcess
     *            STB Process which needs to verify
     * 
     * @return crash file name
     */
    public String getCrashFileFromMiniDumpFolder(AutomaticsTapApi tapApi, Dut dut, String crashFolderPath,
	    DeviceProcess stbProcess) {

	LOGGER.debug("STARTING METHOD: getCrashFileFromMiniDumpFolder()");

	// validation status
	boolean status = false;
	// maximum retry loop count
	final int maxLoopCount = 15;
	// crash file name
	String crashFileName = null;
	// command to get crash files
	String command = "ls -lt " + crashFolderPath + " | grep \".dmp.tgz\"" + " | head -n1";
	// server response
	String response = null;

	for (int index = 0; index < maxLoopCount; index++) {
	    // list files in crash folder
	    response = tapApi.executeCommandUsingSsh(dut, command);
	    // retrieve mini dump file
	    if (CommonMethods.isNotNull(response)) {
		crashFileName = CommonMethods.patternFinder(response, stbProcess.getRegexForMiniDumpFileFormat());

		status = CommonMethods.isNotNull(crashFileName);

		LOGGER.info(index + "/" + maxLoopCount + " - Presence of crash file in crash dump folder = " + status);

		if (status) {
		    break;
		}
	    }
	}

	LOGGER.info("Minidump file name retrieved from " + crashFolderPath + " is " + crashFileName);

	LOGGER.debug("ENDING METHOD: getCrashFileFromMiniDumpFolder()");

	return crashFileName;
    }

    /**
     * Method to retrieve crash file name from core dump folder
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param crashFolderPath
     *            crash folder path
     * @param stbProcess
     *            STB Process which needs to verify
     * @return
     */
    public String getCrashFileFromCoreDumpFolder(AutomaticsTapApi tapApi, Dut dut, String crashFolderPath,
	    DeviceProcess stbProcess) {

	LOGGER.debug("STARTING METHOD: getCrashFileFromCoreDumpFolder()");

	// validation status
	boolean status = false;
	// maximum retry loop count
	final int maxLoopCount = 5;
	// crash file name
	String crashFileName = null;
	// command to retrieve crash files from crash folder
	String command = "ls -lt " + crashFolderPath + " | grep \".core.tgz\"" + " | head -n1";
	// server response
	String response = null;

	for (int index = 0; index < maxLoopCount; index++) {
	    // list files in crash folder
	    response = tapApi.executeCommandUsingSsh(dut, command);

	    if (CommonMethods.isNotNull(response)) {

		crashFileName = CommonMethods.patternFinder(response, stbProcess.getRegexForMiniDumpFileFormat());
		status = CommonMethods.isNotNull(crashFileName);

		LOGGER.info(index + "/" + maxLoopCount + "Presents of crash file in crash dump folder = " + status);

		if (status) {
		    break;
		}
	    }
	}

	LOGGER.info("Coredump file name retrieved from " + crashFolderPath + " is " + crashFileName);

	LOGGER.debug("ENDING METHOD: getCrashFileFromCoreDumpFolder()");

	return crashFileName;
    }

    /**
     * Method to find crash in crash portal
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param crashType
     *            {@link CrashType}
     * @param fileName
     *            crash file name which needs to be checked in crash portal
     * @return true if crash found in crash portal else false
     */
    public boolean findCrashFileInCrashPortal(AutomaticsTapApi tapApi, Dut dut, CrashType crashType, String fileName) {

	LOGGER.debug("STARTING METHOD: findCrashFileInCrashPortal()");

	// crash file validation status
	boolean status = false;

	try {
	    // Find the core dump file in crash portal
	    status = crashAnalysisProvider.isCrashFileAvailableInCrashPortal(crashType, fileName);
	} catch (Exception e) {
	    LOGGER.error("Exception occurred while accessing crashportal " + e);
	}

	LOGGER.debug("ENDING METHOD: findCrashFileInCrashPortal()");

	return status;
    }

    /**
     * Method to verify crash file in crash portal. This method includes retry mechanisms if crash file is not available
     * in portal
     * 
     * @param crashType
     *            crash type
     * @param fileName
     *            file name to verify
     * @return crash verification status
     */
    public boolean verifyCrashFileInCrashPortal(AutomaticsTapApi tapApi, Dut dut, CrashType crashType, String fileName) {

	LOGGER.debug("STARTING METHOD: verifyCrashFileInCrashPortal()");

	// crash file validation status
	boolean status = false;
	// maximum loop count to verify the crash
	final int loopCount = 15;

	LOGGER.info("Going to verify the presence of crash file " + fileName + " in crash portal");
	// verifying the crash file in crash portal
	for (int index = 1; index <= loopCount; index++) {
	    status = findCrashFileInCrashPortal(tapApi, dut, crashType, fileName);
	    LOGGER.info(index + "/" + loopCount + "Presents of crash file in crash portal = " + status);
	    if (status) {
		break;
	    } else {
		LOGGER.info("Waiting one minute...!");
		tapApi.waitTill(AutomaticsConstants.ONE_MINUTE);
	    }
	}

	LOGGER.debug("ENDING METHOD: verifyCrashFileInCrashPortal()");

	return status;
    }

    /**
     * Method to return crash details from crash portal
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param crashType
     *            {@link CrashType}
     * @param fileName
     *            crash file name
     * @return processed crash details
     */
    public CrashDetails getCrashFileDetailsInCrashPortal(AutomaticsTapApi tapApi, Dut dut, CrashType crashType,
	    String fileName) {

	LOGGER.debug("STARTING METHOD: getCrashFileDetailsInCrashPortal()");

	// crash details from crash portal
	CrashDetails crashDetails = null;

	try {
	    // Get the core dump details from S3 crash portal
	    crashDetails = crashAnalysisProvider.getDumpDataForGivenCrashFile(crashType, fileName);
	} catch (Exception e) {
	    LOGGER.error("Exception occurred while accessing crashportal " + e);
	}
	LOGGER.info("crash details : " + crashDetails);

	LOGGER.debug("ENDING METHOD: getCrashFileDetailsInCrashPortal()");
	return crashDetails;
    }

    /**
     * Method to wait after restarting process
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param process
     *            STB Process
     */
    public void waitAfterProcessRestart(AutomaticsTapApi tapApi, Dut dut, DeviceProcess process) {

	LOGGER.debug("STARTING METHOD: waitAfterProcessRestart()");

	if ((process.getProcessName() == StbProcess.RMF_STREAMER.getProcessName())
		|| ((process.getProcessName() == StbProcess.CCSP_CR.getProcessName()))) {

	    LOGGER.info("Waiting after process restart");
	    // wait to process up
	    tapApi.waitTill(AutomaticsConstants.ONE_MINUTE);
	    // wait for IP Acquisition
	    CommonMethods.waitForEstbIpAcquisition(tapApi, dut);
	}

	LOGGER.debug("ENDING METHOD: waitAfterProcessRestart()");
    }

    /**
     * Method to generate error message while checking success upload message in crash file core_log.txt
     * 
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param dut
     *            instance of {@link Dut}
     * @param crashFileName
     *            crash file name
     */
    public void generateErrorMsgFromLog(AutomaticsTapApi tapApi, Dut dut) {

	LOGGER.debug("STARTING METHOD: generateErrorMsgFromLog()");

	// server response
	String response = null;
	// command
	String command = null;

	// command to execute for success upload of crash file
	command = "grep -i '" + CORE_FILE_UPLOAD_SUCCESS_STRING + "' " + getCoreLogPath(dut) + " | tail -n10";

	response = tapApi.executeCommandUsingSsh(dut, command);
	LOGGER.error(response);

	LOGGER.debug("ENDING METHOD: generateErrorMsgFromLog()");
    }

    /**
     * Method to return the path of core_log.txt
     * 
     * @param dut
     *            instance of Dut
     * @return core_log.txt file path
     */
    public static String getCoreLogPath(Dut dut) {
	// core file path
	String filePath = null;
	// command to execute for success upload of crash file
	if (SupportedModelHandler.isRDKB(dut)) {
	    filePath = CrashConstants.LOG_FILE_FOR_CRASHES_RDKB;
	} else if (SupportedModelHandler.isRDKV(dut) || SupportedModelHandler.isRDKC(dut)) {
	    filePath = CrashConstants.LOG_FILE_FOR_CRASHES_RDKV;
	}
	return filePath;
    }   

    /**
     * Method to restartProcess in ARM Console
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param stbProcess
     *            process to restart or kill
     * @param crashType
     *            crash type
     * @return crash details
     * @throws TestException
     *             throws if kill/restart is failed
     */
    public HashMap<String, String> restartProcessInArmConsole(Dut dut, AutomaticsTapApi tapApi,
	    DeviceProcess stbProcess, CrashType crashType) throws TestException {

	LOGGER.debug("STARTING METHOD: restartProcessInArmConsole()");

	// server response
	String response = null;
	// crash details
	HashMap<String, String> crashDetails = new HashMap<String, String>();

	// command to execute in STB to restart the process and list the crash files
	String command = null;

	if (SupportedModelHandler.isRDKV(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH_IN_RDKV + " &";
	} else if (SupportedModelHandler.isRDKB(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH + " &";
	} else if (SupportedModelHandler.isRDKC(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH_RDKC + " &";
	}

	// Update the process name
	command = command.replaceAll(PROCESS_NAME, stbProcess.getProcessName());

	// Execute the command and read the response
	response = tapApi.executeCommandUsingSsh(dut, command);

	if (CommonMethods.isNotNull(response) && response.contains(SERVER_RESPONSE_IF_NO_PROCESS_KILLED)) {
	    throw new TestException("Unable to find the process " + stbProcess.getProcessName() + " in STB "
		    + dut.getHostMacAddress() + " (" + dut.getModel() + ")");
	}

	// wait if the process require wait delay after process restart
	if (stbProcess.getCrashFileGenerationDelay() == CrashFileGenerationDelay.HIGH) {
	    // wait for STB Up and running
	    waitAfterProcessRestart(tapApi, dut, stbProcess);
	}
	crashDetails = waitForCrashFileGeneration(dut, tapApi, stbProcess, crashType, false);

	LOGGER.debug("ENDING METHOD: restartProcessInArmConsole()");

	return crashDetails;
    }

    /**
     * Method to restartProcess from ATOM Console
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param stbProcess
     *            Process to restart
     * @param crashType
     *            crash type
     * @return
     * @throws TestException
     *             if process is not killed or restarted
     */
    public HashMap<String, String> restartProcessInAtomConsole(Dut dut, AutomaticsTapApi tapApi,
	    DeviceProcess stbProcess, CrashType crashType) throws TestException {

	LOGGER.debug("STARTING METHOD: restartProcessInAtomConsole()");

	// server response
	String response = null;
	// crash details
	HashMap<String, String> crashDetails = new HashMap<String, String>();

	// command to execute in STB to restart the process and list the crash files
	String command = COMMAND_TO_KILL_PROCESS + ";tail -f " + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH
		+ " &";

	// Update the process name
	command = command.replaceAll(PROCESS_NAME, stbProcess.getProcessName());

	// Execute the command and read the response
	response = CommonMethods.executeCommandInAtomConsole(dut, tapApi, command);

	if (CommonMethods.isNotNull(response) && response.contains(SERVER_RESPONSE_IF_NO_PROCESS_KILLED)) {
	    throw new TestException("Unable to find the process " + stbProcess.getProcessName() + " in STB "
		    + dut.getHostMacAddress() + " (" + dut.getModel() + ")");
	}

	// wait if the process require wait delay after process restart
	if (stbProcess.getCrashFileGenerationDelay() == CrashFileGenerationDelay.HIGH) {
	    // wait for STB Up and running
	    waitAfterProcessRestart(tapApi, dut, stbProcess);
	}

	crashDetails = waitForCrashFileGeneration(dut, tapApi, stbProcess, crashType, true);

	LOGGER.debug("ENDING METHOD: restartProcessInAtomConsole()");

	return crashDetails;
    }

    /**
     * Method to retrieve process ID
     * 
     * @param dut
     *            instance of Dut
     * @param stbProcess
     *            STB Process details
     * @return process ID
     */
    public String getPidOfProcess(Dut dut, AutomaticsTapApi tapApi, DeviceProcess stbProcess,
	    boolean executeInAtomConsole) {
	LOGGER.debug("STARTING METHOD: getPidOfProcess()");
	// process id
	String processId = null;
	// Retrieve process id from STB
	if (executeInAtomConsole) {
	    processId = getPidOfProcessFromAtomConsole(dut, tapApi, stbProcess);
	} else {
	    processId = CommonMethods.getPidOfProcess(dut, tapApi, stbProcess.getProcessName());
	}
	LOGGER.debug("ENDING METHOD: getPidOfProcess()");
	return processId;
    }

    /**
     * Method to get PID of process from Atom console
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param stbProcess
     *            STB Process
     * @return PID of given process
     */
    public String getPidOfProcessFromAtomConsole(Dut dut, AutomaticsTapApi tapApi, DeviceProcess stbProcess) {
	// process id
	String processId = null;
	// store the server response
	String response = null;

	try {
	    response = CommonMethods.executeCommandInAtomConsole(dut, tapApi, "pidof " + stbProcess.getProcessName());

	    processId = CommonMethods.patternFinder(response, "(\\d+)");
	    if (CommonMethods.isNotNull(processId)) {
		processId = processId.trim();
		// validating whether PID is integer or not
		Integer.parseInt(processId);
	    }
	} catch (Exception exception) {
	    processId = null;
	}

	return processId;
    }

    /**
     * Method to retrieve device type for carsh details verification
     * 
     * @param dut
     *            instance of {@link Dut}
     * @return device type for crash details verification
     */
    public String getDeviceTypeForCrashDetailsVerfication(Dut dut, AutomaticsTapApi tapApi) {
	LOGGER.debug("STARTING METHOD: getDeviceTypeForCrashDetailsVerfication()");
	String deviceType = "box";
	deviceType += CommonMethods.getPropertyFromDeviceProperties(dut, tapApi, "BOX_TYPE", "BOX_TYPE=(\\w+)");

	LOGGER.info("ENDING METHOD: getDeviceTypeForCrashDetailsVerfication(): " + deviceType);
	return deviceType;
    }

    /**
     * Method to wait to retrieve all the crash file details from log file
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param stbProcess
     *            stb process which crashed/killed
     * @param crashType
     *            crash type
     * @param executeInAtomConsole
     *            true if execute in atom console else false
     * @return crash details with keys,
     * 
     *         PROPERTY_KEY_BUILD_ID,
     * 
     *         PROPERTY_KEY_CRASH_FILE_NAME,
     * 
     *         PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS
     */
    public HashMap<String, String> waitForCrashFileGeneration(Dut dut, AutomaticsTapApi tapApi,
	    DeviceProcess stbProcess, CrashType crashType, boolean executeInAtomConsole) {

	LOGGER.debug("STARTING METHOD: waitForCrashFileGeneration()");

	// maximum loop count to verify the crash file log details
	final int maximumLoopCount = 20;
	// pattern to retrieve buildId
	String patternForBuildId = "buildID\\s*is\\s*(\\w+)\\s+";
	// fallback check for build Id
	final String patternForBuildIdAlternative = "Removing file\\s(\\w+)_mac";
	// fallback check for build Id
	final String patternForBuildIdAlternativeNew = "Removing\\s(\\w+)_mac";

	// command to retrieve crash details from crash logs
	String command = null;
	// Command to retrieve crash details for RDKV
	if (SupportedModelHandler.isRDKV(dut)) {
	    command = "cat " + CORE_LOG_FILE_TEMP_PATH_IN_RDKV;
	} else if (SupportedModelHandler.isRDKB(dut)) {
	    command = "cat " + CORE_LOG_FILE_TEMP_PATH;
	} else if (SupportedModelHandler.isRDKC(dut)) {
	    command = "cat " + CORE_LOG_FILE_TEMP_PATH_RDKC;
	}

	// map to store crash details
	final HashMap<String, String> crashDetails = new HashMap<String, String>();

	// server response
	String response = null;
	// temporary variable to store the crash details values
	String value = null;
	// pattern to retrieve crash file name
	String patternForCrashFileName = TEXT_TO_RETRIEVE_CRASH_FILE_NAME_FROM_LOG_GILE;

	// retrieve crash file format
	if (crashType == CrashType.MINIDUMP) {
	    // update the regex of the crash file
	    patternForCrashFileName = "minidump file to be uploaded:";
	    patternForCrashFileName += stbProcess.getRegexForMiniDumpFileFormat();
	} else if (crashType == CrashType.COREDUMP) {
	    // update the regex of the crash file
	    patternForCrashFileName = "coredump file to be uploaded:";
	    patternForCrashFileName += stbProcess.getRegexForCoreDumpFileFormat();
	}

	// Continuously waiting for the success logs in core file logs
	for (int index = 1; index <= maximumLoopCount; index++) {

	    LOGGER.info(index + "/" + maximumLoopCount + "# trying to retrieve crash details");

	    if (executeInAtomConsole) {
		// Execute the command and read the response in ATOM Console
		response = CommonMethods.executeCommandInAtomConsole(dut, tapApi, command);
	    } else {
		// Execute the command and read the response in ARM Console
		response = tapApi.executeCommandUsingSsh(dut, command);
	    }
	    // If PreviousLogs/core_log.txt was not created, this will tail the real core_log.txt and adds to response -
	    if (SupportedModelHandler.isRDKV(dut)) {
		response += tapApi.executeCommandUsingSsh(dut, "tail -200 " + getCoreLogPath(dut));
	    }

	    // retrieve crash details and store in map
	    if (CommonMethods.isNotNull(response)) {

		// retrieve buildId
		if (!crashDetails.containsKey(PROPERTY_KEY_BUILD_ID)) {

		    patternForBuildId = AutomaticsPropertyUtility.getProperty("dump.data.buildId.matcher");

		    if (CommonMethods.isNotNull(patternForBuildId)) {
			List<String> regExValues = CommonMethods.splitStringByDelimitor(patternForBuildId,
				AutomaticsConstants.COMMA);
			List<Pattern> regexPackagePatterns = new ArrayList<Pattern>();
			for (String reg : regExValues) {

			    try {
				regexPackagePatterns.add(Pattern.compile(reg));
				value = CommonMethods.patternFinder(response, patternForBuildId);
				if (CommonMethods.isNotNull(value)) {
				    crashDetails.put(PROPERTY_KEY_BUILD_ID, value);
				    break;
				}
			    } catch (Exception e) {
				LOGGER.error("Exception while parsing pattern: {}", e);
			    }
			}
		    }
		}

		// retrieve crash file name
		if (!crashDetails.containsKey(PROPERTY_KEY_CRASH_FILE_NAME)) {
		    value = CommonMethods.caseInsensitivePatternFinder(response, patternForCrashFileName);

		    if (SupportedModelHandler.isRDKV(dut) && CommonMethods.isNull(value)) {
			value = CommonMethods.caseInsensitivePatternFinder(response,
				patternForCrashFileName.replace(",", ""));
		    }

		    if (CommonMethods.isNull(value)) {
			String alternativePatternsForDump = AutomaticsConstants.EMPTY_STRING;
			if (crashType == CrashType.MINIDUMP) {
			    alternativePatternsForDump = AutomaticsPropertyUtility.getProperty(
				    "alternative.minidump.filename.matcher", "");
			} else {
			    alternativePatternsForDump = AutomaticsPropertyUtility.getProperty(
				    "alternative.coredump.filename.matcher", "");
			}
			List<String> listOfPatterns = CommonMethods.splitStringByDelimitor(alternativePatternsForDump,
				AutomaticsConstants.COMMA);
			for (String eachPattern : listOfPatterns) {
			    value = CommonMethods.caseInsensitivePatternFinder(response, eachPattern);
			    if (CommonMethods.isNotNull(value))
				break;
			}
		    }
		    if (CommonMethods.isNotNull(value)) {
			crashDetails.put(PROPERTY_KEY_CRASH_FILE_NAME, value);
		    }
		}

		// retrieve crash file upload string
		if (!crashDetails.containsKey(PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS)) {
		    List<String> listOfPatterns = crashAnalysisProvider.getRegexForSuccessfulCrashUpload(dut,
			    crashType, stbProcess);
		    for (String eachPattern : listOfPatterns) {
			value = CommonMethods.caseInsensitivePatternFinder(response, eachPattern);
			if (CommonMethods.isNotNull(value)) {
			    crashDetails.put(PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS, value);
			    break;
			}
		    }
		}
	    }

	    // break the continues checking whether all the required crash details are received
	    if (crashDetails.containsKey(PROPERTY_KEY_BUILD_ID)
		    && crashDetails.containsKey(PROPERTY_KEY_CRASH_FILE_NAME)
		    && crashDetails.containsKey(PROPERTY_KEY_CRASH_FILE_UPLOAD_SUCCESS)) {
		break;
	    }

	    tapApi.waitTill(AutomaticsConstants.THIRTY_SECONDS);
	}

	LOGGER.debug("ENDING METHOD: waitForCrashFileGeneration()");

	return crashDetails;
    }

    /**
     * Method to perform post condition for crash verification test scripts
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param executeInAtomConsole
     *            true if execute the commands in atom console else false
     */
    public void performPostConditionForCrashVerificationTestScripts(Dut dut, AutomaticsTapApi tapApi,
	    boolean executeInAtomConsole) {

	// server response
	String response = null;
	// patter to retrieve process ids
	String pattern = "(\\d+).*\\s+tail -f \\/rdklogs\\/logs\\/core_log.txt";
	// command to execute
	String command = "";
	// matched string
	ArrayList<String> matchedString = null;

	LOGGER.debug("STARTING METHOD: performPostConditionForCrashVerificationTestScripts()");

	// command to kill all the tail background processes
	command = "ps | grep \"tail -f /rdklogs/logs/core_log.txt\" | grep -v grep";

	if (executeInAtomConsole) {
	    response = CommonMethods.executeCommandInAtomConsole(dut, tapApi, command);
	} else {
	    response = tapApi.executeCommandUsingSsh(dut, command);
	}

	// retrieve all the PID
	if (CommonMethods.isNotNull(response)) {
	    matchedString = CommonMethods.patternFinderToReturnAllMatchedString(response, pattern);
	}

	// reset command variable
	command = "";

	if (matchedString != null && !matchedString.isEmpty()) {

	    for (String pid : matchedString) {

		if (CommonMethods.isNotNull(pid)) {
		    pid = pid.trim();
		    try {
			// verifying whether the retrieved pid is valid or not
			Integer.parseInt(pid);
			command = command + "kill -9 " + pid + ";";
		    } catch (NumberFormatException numberFormatException) {
		    }
		}
	    }
	}

	// update command to remove the temporary folder
	command = command + "rm -rf " + CORE_LOG_FOLDER_TEMP_PATH;

	if (executeInAtomConsole) {
	    response = CommonMethods.executeCommandInAtomConsole(dut, tapApi, command);
	} else {
	    response = tapApi.executeCommandUsingSsh(dut, command);
	}
	LOGGER.debug("ENDING METHOD: performPostConditionForCrashVerificationTestScripts()");
    }

    /**
     * Method to restartProcess
     * 
     * @param dut
     *            instance of {@link Dut}
     * @param tapApi
     *            instance of {@link AutomaticsTapApi}
     * @param stbProcess
     *            process to restart or kill
     * @param crashType
     *            crash type
     * @return crash details
     * @throws TestException
     *             throws if kill/restart is failed
     */
    public boolean restartProcess(Dut dut, AutomaticsTapApi tapApi, DeviceProcess stbProcess) throws TestException {
	LOGGER.debug("STARTING METHOD: restartProcessInArmConsole()");
	// server response
	String response = null;
	// command to execute in STB to restart the process and list the crash
	// files
	String command = null;
	if (SupportedModelHandler.isRDKV(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH_IN_RDKV + " &";
	} else if (SupportedModelHandler.isRDKB(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH + " &";
	} else if (SupportedModelHandler.isRDKC(dut)) {
	    command = ProcessRestartOption.KILL_ALL_11.getCommand() + " " + PROCESS_NAME + ";tail -f "
		    + getCoreLogPath(dut) + " >> " + CORE_LOG_FILE_TEMP_PATH_RDKC + " &";
	}
	// Update the process name
	command = command.replaceAll(PROCESS_NAME, stbProcess.getProcessName());
	// Execute the command and read the response
	response = tapApi.executeCommandUsingSsh(dut, command);
	if (CommonMethods.isNotNull(response) && response.contains("no process killed")) {

	    throw new TestException("Unable to find the process " + stbProcess.getProcessName() + " in STB "
		    + dut.getHostMacAddress() + " (" + dut.getModel() + ")");

	}
	return true;
    }
}
