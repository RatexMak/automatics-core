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

package com.automatics.utils;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.constants.WebPaConstants;
import com.automatics.device.Device;
import com.automatics.providers.logupload.DeviceLogUploadProvider;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.webpa.WebPaParameter;

/**
 * This class hold apis for helping log upload to log server / execution jenkins servers for analysis of failures
 * 
 * @author rohinic
 *
 */
public class LogUploadUtils {

    private static DeviceLogUploadProvider deviceLogProvider;

    /** The constant for boolean data type value . */
    public static final int DATA_TYPE_VALUE_BOOLEAN = 3;

    /** The constant for boolean value true. */
    public static final String DATA_VALUE_BOOLEAN_TRUE = "true";

    public static final Logger LOGGER = LoggerFactory.getLogger(LogUploadUtils.class);

    static {
	deviceLogProvider = BeanUtils.getDeviceLogUploadProvider();
    }

    /**
     * Fetches device logs from Partner cloud system and download it in given location
     * 
     * @param device
     * @param tapEnv
     * @param testCaseId
     * @return Return true if log fetch was success
     */
    public static boolean fetchDeviceLogs(Device device, AutomaticsTapApi tapEnv, String testCaseId) {
	boolean fetchSuccess = false;
	String uploadedLogFileName = triggerAndWaitLogUploadToComplete(device, tapEnv);

	LOGGER.info("Device log uploaded filename = " + uploadedLogFileName);

	if (CommonMethods.isNotNull(uploadedLogFileName)) {
	    LOGGER.info("Uploaded file to log server :" + uploadedLogFileName);

	    // Location in jenkins where logs to be saved
	    String logSaveLocation = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
		    + AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + testCaseId
		    + AutomaticsConstants.PATH_SEPARATOR
		    + AutomaticsUtils.getCleanMac(device.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR
			    + ReportsConstants.LOG_DIR.replace("/", "") + AutomaticsConstants.PATH_SEPARATOR);

	    LOGGER.info("Copying device logs to {}", logSaveLocation);
	    File logSaveLocationDirectory = new File(logSaveLocation);

	    if (!logSaveLocationDirectory.isDirectory()) {
		LOGGER.info("Created a new directory {}", logSaveLocationDirectory.mkdirs());
	    }

	    String logUploadedPath = getLogFilePathInCloud(device, uploadedLogFileName);
	    if (CommonMethods.isNotNull(logUploadedPath)) {
		fetchSuccess = downloadLogFromCloud(device, logUploadedPath,
			logSaveLocationDirectory + File.separator + uploadedLogFileName);
	    } else {
		LOGGER.error("Failed to get log uploaded location");
	    }

	}
	return fetchSuccess;
    }

    /**
     * 
     * Method to download the upload file from log server
     * 
     * @param logFilPath
     *            Path of file in cloud passed as input to the log server tool
     * @param logSaveLocationDirectory
     *            Location where the logs has to be downloaded
     * @return
     * 
     */
    public static boolean downloadLogFromCloud(Device device, String logFilPath, String logSaveLocationDirectory) {
	boolean isSuccess = false;

	if (null != deviceLogProvider) {
	    isSuccess = deviceLogProvider.downloadLogFromCloud(device, logFilPath, logSaveLocationDirectory);
	}
	LOGGER.info("Is log download success: {}", isSuccess);

	return isSuccess;

    }

    /**
     * Initiates the upload process and returns the name of uploaded log file. The logs are uploaded by setting webpa
     * for RDKV devices
     * 
     * @param device
     *            Device object
     * @param tapEnv
     *            Tap Environment
     * @return Returns name of the uploaded log file
     */
    public static String triggerAndWaitLogUploadToComplete(Device device, AutomaticsTapApi tapEnv) {
	LOGGER.info("Inside triggerLogUploadToProdServer.....");
	String uploadedFileName = AutomaticsConstants.EMPTY_STRING;

	boolean isTriggerSuccess = triggerLogUpload(device, tapEnv);

	if (isTriggerSuccess) {

	    // Check if log upload is completed.
	    if (isLogUploadCompleted(device, tapEnv)) {
		uploadedFileName = getLogFileNameInCloud(device, tapEnv);
	    } else {
		LOGGER.error("Failed, device log upload is not completed.");
	    }
	} else {
	    LOGGER.error("Failed to trigger device log upload");
	}

	return uploadedFileName;
    }

    /**
     * 
     * Method to get LogFilePathInCloud
     * 
     * @param fileName
     *            Name of log file uploaded by device
     * @return
     *
     */
    public static String getLogFilePathInCloud(Device device, String logFileName) {

	String fileUploadedPath = AutomaticsConstants.EMPTY_STRING;
	if (null != deviceLogProvider) {
	    fileUploadedPath = deviceLogProvider.getLogFilePathInCloud(device, logFileName);
	}
	LOGGER.info("Device log uploaded file path = ", fileUploadedPath);

	return fileUploadedPath;

    }

    /**
     * 
     * Get the log uploaded file by searching for its filename in device logs
     * 
     * @param device
     *            Device object
     * @param tapEnv
     *            Tap environment
     * @return Returns the name of the file
     */
    public static String getLogFileNameInCloud(Device device, AutomaticsTapApi tapEnv) {

	LOGGER.info("Fetching device log filename");
	String fileName = AutomaticsConstants.EMPTY_STRING;
	if (null != deviceLogProvider) {
	    fileName = deviceLogProvider.getLogFileNameInCloud(device);
	}

	LOGGER.info("Device log uploaded filename = " + fileName);
	return fileName;
    }

    /**
     * 
     * Checks whether upload is completed using webpa. This will wait for 2.5mins or 180 secs for completion of log
     * upload to cloud server
     * 
     * @param device
     *            Device
     * @param tapEnv
     *            AutomaticsTapApi
     * 
     * @return Return true if device logs has been uploaded successfully to cloud
     */
    public static boolean isLogUploadCompleted(Device device, AutomaticsTapApi tapEnv) {

	LOGGER.info("Checking device log upload is completed");
	boolean success = false;
	tapEnv.waitTill(AutomaticsConstants.THIRTY_SECONDS);
	LOGGER.info("Sleep done for 30 seconds");
	int retry = 6;
	while (retry > 0) {
	    String response = tapEnv.executeWebPaCommand(device, WebPaConstants.WEBPA_PARAMETER_UPLOAD_STATUS);
	    if (response != null) {
		LOGGER.info("Device Log Upload Status: {}", response);
		if (response.contains("Complete")) {
		    success = true;
		    break;
		} else {
		    retry--;
		    tapEnv.waitTill(AutomaticsConstants.THIRTY_SECONDS);
		    LOGGER.info("Sleep done for 30 seconds");
		}
	    }
	}
	return success;
    }

    /**
     * Initiates device log upload to Partner specific cloud storage system
     * 
     * @param device
     * @param tapEnv
     * @return Return true, if log upload trigger is success.
     */
    public static boolean triggerLogUpload(Device device, AutomaticsTapApi tapEnv) {

	LOGGER.info("Triggering device log upload");
	boolean isTriggerSuccess = false;
	List<WebPaParameter> params = tapEnv.setWebPaParams(device, WebPaConstants.WEBPA_PARAMETER_TRIGGER_UPLOAD,
		DATA_VALUE_BOOLEAN_TRUE, DATA_TYPE_VALUE_BOOLEAN);

	if (params != null) {
	    WebPaParameter response = params.get(0);

	    if ("Success".equals(response.getMessage())) {
		LOGGER.info("Log Upload Trigger Success.");
		isTriggerSuccess = true;
	    }
	}

	return isTriggerSuccess;

    }

}
