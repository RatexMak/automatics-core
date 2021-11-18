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
package com.automatics.appenders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.enums.ProcessRestartOption;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.CommonMethods;

/**
 * Class that manipulates the operations for configuring the box based on user input
 * 
 * 
 * 
 */
public class BuildAppenderManager {
    // variable that stores the min reboot count value for build appenders to get applied
    private static final int MIN_REBOOT_COUNT = 1;
    // variable that stores the max reboot count value for build appenders to get applied
    private static final int MAX_REBOOT_COUNT = 2;
    // variable that stores the Max wait time in between reboots
    private static final int MAX_WAIT_TIME_IN_MILLIS = 300000;
    // SLF4j logger instance.
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildAppenderManager.class);
    // variable that keeps the reboot count for rebooting the box after setting RFC params.
    private ArrayList<Integer> rebootCountList = new ArrayList<Integer>();
    // variable that keeps the list of process to be restarted after applying a particular RFC param
    private ArrayList<String> restartProcessList = new ArrayList<String>();
    // variable that keeps the list of services to be restarted after applying a particular RFC param
    private ArrayList<String> restartServiceList = new ArrayList<String>();
    // variable to hold the build manager instance
    private static BuildAppenderManager buildManager = null;
    
    private BuildAppenderManager() {
	LOGGER.info("Inside private constructor");
    }

    public static BuildAppenderManager getInstance() {
	if (null == buildManager) {
	    buildManager = new BuildAppenderManager();
	}
	return buildManager;
    }

    public ArrayList<Integer> getRebootCountList() {
	LOGGER.info("Size of reboot Count List : " + this.rebootCountList);
	return this.rebootCountList;
    }

    public void setRebootCountList(ArrayList<Integer> rebootCountList) {
	this.rebootCountList = rebootCountList;
    }

    public ArrayList<String> getRestartProcessList() {
	LOGGER.info("Size of RestartProcess List : " + this.restartProcessList);
	return this.restartProcessList;
    }

    public void setRestartProcessList(ArrayList<String> restartProcessList) {
	this.restartProcessList = restartProcessList;
    }

    public ArrayList<String> getRestartServiceList() {
	LOGGER.info("Size of RestartService List : " + this.restartServiceList);
	return this.restartServiceList;
    }

    public void setRestartServiceList(ArrayList<String> restartServiceList) {
	this.restartServiceList = restartServiceList;
    }

    /**
     * Method that applies the RFC params mentioned by user as build Appender. Once params are applied, the box will be
     * rebooted/services will be restarted based on configuration mentioned by user in stb.props
     * 
     * @param tapEnv
     * @param dut
     * @param appenders
     * @return
     */
    public Map<String, String> applySettings(AutomaticsTapApi tapEnv, Dut dut, ArrayList<String> appenders) {
	String statusMessage = "";
	Map<String, String> statusMap = new HashMap<>();
	boolean isRFCAppenderPresent = false;
	// iterate the build appenders in the list and configure box accordingly
	for (String buildAppender : appenders) {
	    LOGGER.info("Going to configure " + buildAppender + " in device with MAC : " + dut.getHostMacAddress());
	    
	    BuildAppender buildAppenderInstance = BuildAppenderFactory.get(tapEnv, dut, buildAppender);
	    if (buildAppenderInstance instanceof RFCBuildAppender) {
		isRFCAppenderPresent = true;
	    }
	    if (null != buildAppenderInstance) {
		// iterate each appender and apply whatever possible.. incase one fails, try with next. Verification is
		// done further
		statusMessage = buildAppenderInstance.applySettings(tapEnv, dut);
		if (!statusMessage.equals(AutomaticsConstants.OK)) {
		    statusMessage = "Failed to apply appender: " + buildAppender + " Reason : " + statusMessage;
		}
		statusMap.put(buildAppender, statusMessage);
		LOGGER.info("Status of applying appender : " + buildAppender + ":" + statusMessage);
	    } else {
		// did not configure any appenders..
		statusMap.put("default", "OK");
		// continue with other build appenders
		continue;
	    }
	}
	// No need to reset the box to apply settings even if it failed to properly enable any one build appender
	boolean applyAppender = false;
	for (String result : statusMap.values()) {
	    if (result.contains(AutomaticsConstants.OK)) {
		applyAppender = true;
	    } else {
		applyAppender = false;
		break;
	    }
	}
    //DeviceConfig reset operations only applciable for RFC
	if (applyAppender && isRFCAppenderPresent) {
	    resetBoxToApplysettings(tapEnv, dut, statusMessage);
	}
	return statusMap;
    }

    /**
     * Method that resets the box, either does some, reboots or restart a servive etc, so that whatever settings we
     * enabled will be applied
     * 
     * @param tapEnv
     * @param dut
     * @param statusMessage
     * @param rebootStatus
     * @return
     */
    private String resetBoxToApplysettings(AutomaticsTapApi tapEnv, Dut dut, String statusMessage) {
	boolean rebootStatus = false;
	// First check if more than one reboot is required, 2 is considered as the maximum reboot count.
	// Need to alter code if there is any difference
	LOGGER.info("Contents of reboot/restart service/ restart process list : ");
	LOGGER.info("Size of Reboot count list : " + this.rebootCountList.size());
	LOGGER.info("Size of Restart process count list : " + this.restartProcessList.size());
	LOGGER.info("Size of Reboot service list : " + this.restartServiceList.size());
	if (this.rebootCountList.contains(MAX_REBOOT_COUNT)) {
		// just do two reboots, with a wait time of 5 mins under the assumption that all other required settings will be enabled within the wait time frame.
		// with this
		for (int i = 0; i < MAX_REBOOT_COUNT; i++) {
			rebootStatus = CommonMethods.waitForEstbIpAcquisition(tapEnv, dut);
			if (!rebootStatus) {
				statusMessage = "Failed to reboot the box after applying build appender.";
				break;
			}else {
				try {
					LOGGER.info("Going to Wait for 5 Mins to download RFC settings from the xconf");
					Thread.sleep(MAX_WAIT_TIME_IN_MILLIS);
				}catch (InterruptedException e) {
					LOGGER.error("Thread Sleep Error"+e.getMessage());
				}
			}
		}
		if (rebootStatus) {
			LOGGER.info("Successfully rebooted the box twice after applying build appender");
		}
	} else {
	    // There are 3 use cases, as of now
	    // 1. need to do one reboot
	    // 2. need to restart given services alone
	    // 3. need to restart given services and do a reboot

	    // check if there are any services to be restarted, then just restart it, in case no reboots are
	    // required.
	    if (!restartProcessList.isEmpty()) {
		LOGGER.info("Restart given process and then reboot the box if requried");
		// restart the process and reboot the box, if user configured so, else just reboot the box.
		// This would suffice
		if (rebootCountList.size() > 0 && rebootCountList.contains(MIN_REBOOT_COUNT)) {
		    statusMessage = restartProcess(tapEnv, dut);
		    LOGGER.info("Going to reboot the box, after restarting given process");
		    rebootStatus = CommonMethods.waitForEstbIpAcquisition(tapEnv, dut);
		    if (rebootStatus) {
			LOGGER.info("Successfully rebooted the box after restarting process");
		    }
		} else {
		    statusMessage = restartProcess(tapEnv, dut);
		    LOGGER.info("Successfully restarted given services.");
		}

	    } else if (!restartServiceList.isEmpty()) {
		LOGGER.info("Restart given services and then reboot the box if requried");
		// restart the service and reboot the box, if user configured so, else just reboot the box.
		// This would suffice
		if (rebootCountList.size() > 0 && rebootCountList.contains(MIN_REBOOT_COUNT)) {
		    statusMessage = restartServices(tapEnv, dut);
		    LOGGER.info("Going to reboot the box, after restarting given services");
		    rebootStatus = CommonMethods.waitForEstbIpAcquisition(tapEnv, dut);
		    if (rebootStatus) {
			LOGGER.info("Successfully rebooted the box after restarting services");
		    }
		} else {
		    statusMessage = restartServices(tapEnv, dut);
		    LOGGER.info("Successfully restarted given services.");
		}

	    } else if (rebootCountList.size() > 0 && rebootCountList.contains(MIN_REBOOT_COUNT)) {
		// just reboot the box if rebootCountList has entries in it and user has configured value 1
		LOGGER.info("Going to Reboot the box, after applying RFC params");
		rebootStatus = CommonMethods.waitForEstbIpAcquisition(tapEnv, dut);
		if (rebootStatus) {
		    LOGGER.info("Successfully rebooted the box after build params");
		}
	    } else {
		LOGGER.info("Reboot count is not mentioned");
	    }

	}

	return statusMessage;
    }

    /**
     * Method that iterates the services stored in restartServiceList, and restart the mentioned services one by one
     * 
     * @param tapEnv
     * @param dut
     * @return OK, if restart was success, else return the corresponding error message
     */
    private String restartProcess(AutomaticsTapApi tapEnv, Dut dut) {
	String statusMessage = AutomaticsConstants.OK;
	for (String service : restartProcessList) {
	    LOGGER.info("Restarting service : " + service);
	    boolean restartServiceStatus = CommonMethods.restartProcess(dut, tapEnv, ProcessRestartOption.KILL_11,
		    service);
	    if (!restartServiceStatus) {
		statusMessage = "Failed to restart the service : " + service + " after applying RFC Params";
	    } else {
		LOGGER.info("Successfully restarted the services");
	    }
	}
	return statusMessage;
    }

    /**
     * Method that iterates the services stored in restartServiceList, and restart the mentioned services one by one
     * 
     * @param tapEnv
     * @param dut
     * @return OK, if restart was success, else return the corresponding error message
     */
    private String restartServices(AutomaticsTapApi tapEnv, Dut dut) {
	String statusMessage = AutomaticsConstants.OK;
	boolean restartServiceStatus = false;
	for (String service : restartServiceList) {
	    LOGGER.info("Restarting service : " + service);
	    restartServiceStatus = CommonMethods.restartService(tapEnv, dut, service);
	    if (!restartServiceStatus) {
		statusMessage += "\nFailed to restart the service : " + service + " after applying RFC Params";
	    } else {
		LOGGER.info("Successfully restarted the services");
	    }
	}
	return statusMessage;
    }

    /**
     * Method that verifies from log whether the build appenders configured were properly enabled in box or not
     * 
     * @param tapEnv
     * @param dut
     * @param appenders
     * @return OK, if verifications was success, else corresponding error message
     */
    public Map<String, String> verifySettings(AutomaticsTapApi tapEnv, Dut dut, ArrayList<String> appenders) {
	String statusMessage = AutomaticsConstants.OK;
	Map<String, String> statusMap = new HashMap<String, String>();
	// iterate the build appenders in the list and configure box accordingly
	for (String buildAppender : appenders) {
	    LOGGER.info("Going to verify the appender : " + buildAppender + " in Box with MAC : "
		    + dut.getHostMacAddress());
	    BuildAppender buildAppenderInstance = BuildAppenderFactory.get(tapEnv, dut, buildAppender);
	    if (null != buildAppenderInstance) {
		statusMessage = buildAppenderInstance.verifySettings(tapEnv, dut);
		if (!statusMessage.equals(AutomaticsConstants.OK)) {
		    statusMessage = "Failed to verify application of appender: " + buildAppender + " Reason : "
			    + statusMessage;
		}
		statusMap.put(buildAppender, statusMessage);
		LOGGER.info("Status of verifying appender : " + buildAppender + ":" + statusMessage);
	    } else {
		// did not configure any appenders..
		statusMap.put("default", "OK");
		// continue with other build appenders
		continue;
	    }
	}
	return statusMap;
    }

    /**
     * Method that resets the user configured build appenders from box
     * 
     * @param tapEnv
     * @param dut
     * @param appenders
     * @return OK, if verifications was success, else corresponding error message
     */
    public Map<String, String> resetSettings(AutomaticsTapApi tapEnv, Dut dut, ArrayList<String> appenders) {
	String statusMessage = AutomaticsConstants.OK;
	Map<String, String> statusMap = new HashMap<>();
	boolean isRFCFeaturePresent = false;
	// iterate the build appenders in the list and configure box accordingly
	for (String buildAppender : appenders) {
	    // for RFC feature reset we don't have to iterate each and remove one by one. Instead we can do the same
	    // using one rest call which will reboot the box after doing the rest call
	    if (buildAppender.toLowerCase().contains("rfc")) {
		isRFCFeaturePresent = true;
		continue;
	    }
	    LOGGER.info("Going to reset the appender : " + buildAppender + " in Box with MAC : "
		    + dut.getHostMacAddress());
	    BuildAppender buildAppenderInstance = BuildAppenderFactory.get(tapEnv, dut, buildAppender);
	    if (null != buildAppenderInstance) {
		statusMessage = buildAppenderInstance.resetSettings(tapEnv, dut);
		if (!statusMessage.equals(AutomaticsConstants.OK)) {
		    statusMessage = "Failed to reset appender: " + buildAppender + " Reason : " + statusMessage;
		}
		LOGGER.info("Status of reset appender : " + buildAppender + ":" + statusMessage);
		statusMap.put(buildAppender, statusMessage);
	    }
	}
	// now check if we need to reset RFC settings
	if (isRFCFeaturePresent) {
	    statusMessage = disableEntireRFCFeatures(tapEnv, dut);
	}
	return statusMap;
    }

    /**
     * Method that calls the REST APIs to disable the entire RFC feature configured in the box
     * 
     * @param tapEnv
     * @param dut
     * @param rfcFeature
     * @param url
     * @return
     */
    protected String disableEntireRFCFeatures(AutomaticsTapApi tapEnv, Dut dut) {
	String statusMessage = AutomaticsConstants.OK;
	int responseCode = 0;
	try {
	    // fetch the REST API to enable the RFC feature, from stb.properties
	    responseCode = CommonMethods.clearParamsInServer(dut, tapEnv, true, null);
	    if (responseCode != HttpStatus.SC_OK) {
		statusMessage = "Failed to get a proper response after resetting the RFC params. Obtained response as :  "
			+ responseCode;
		LOGGER.error(statusMessage);
	    }
	} catch (Exception e) {
	    statusMessage = "RFC Feature Rules for are not properly configured in stb.props. Error Message : "
		    + e.getMessage();
	}

	return statusMessage;
    }

    /**
     * Method that verifies from log whether the build appenders configured were properly disabled/reset from box
     * 
     * @param tapEnv
     * @param dut
     * @param appenders
     * @return OK, if reset verification was success, else corresponding error message
     */
    public Map<String, String> verifyResetSettings(AutomaticsTapApi tapEnv, Dut dut, ArrayList<String> appenders) {
	String statusMessage = AutomaticsConstants.OK;
	Map<String, String> statusMap = new HashMap<>();
	// iterate the build appenders in the list and configure box accordingly
	for (String buildAppender : appenders) {
	    LOGGER.info("Going to verify the reset of appender : " + buildAppender + " in Box with MAC : "
		    + dut.getHostMacAddress());
	    BuildAppender buildAppenderInstance = BuildAppenderFactory.get(tapEnv, dut, buildAppender);
	    if (null != buildAppenderInstance) {
		statusMessage = buildAppenderInstance.verifyReset(tapEnv, dut);
		if (!statusMessage.equals(AutomaticsConstants.OK)) {
		    statusMessage = "Failed to verify reset of appender: " + buildAppender + " Reason : "
			    + statusMessage;
		}
		LOGGER.info("Status of verification of resetting appender : " + buildAppender + ":" + statusMessage);
		statusMap.put(buildAppender, statusMessage);
	    }
	}
	return statusMap;
    }

}
