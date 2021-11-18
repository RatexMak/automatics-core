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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;

/**
 * @author rohinic
 *
 */
public class FrameworkHelperUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkHelperUtils.class);
    private static final String IDENTIFIER_MASTER_BUILD = "sprint";

    private static final String IDENTIFIER_STABLE_BUILD = "stable";

    private static final String IDENTIFIER_RELEASE_BUILD = "release";

    /**
     * This method is used to return the firmwareVersion branch.It returns three values - master - stable - release
     * 
     * @param dut
     *            Dut Object
     * @return "master" / "stable" / "release"
     */
    public static String getFirmwareVersionBranch(Dut dut) {
	String branchType = null;
	String firmwareVersion = dut.getFirmwareVersion();

	if (CommonMethods.isNotNull(firmwareVersion)) {

	    if (isSprintBuild(firmwareVersion)) {
		branchType = IDENTIFIER_MASTER_BUILD;
	    } else if (isStableBuild(firmwareVersion)) {
		branchType = IDENTIFIER_STABLE_BUILD;
	    } else {
		branchType = IDENTIFIER_RELEASE_BUILD;
	    }
	}
	return branchType;
    }

    /**
     * Verify if given build is stable build
     * 
     * @param firmwareVersion
     * @return true if stable build
     */
    public static boolean isStableBuild(String firmwareVersion) {
	boolean isExpectedType = false;
	String propRegExValue = AutomaticsPropertyUtility
		.getProperty(AutomaticsConstants.PROPERTY_STABLE_BUILD_KEYWORDS);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    List<String> regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    for (String keyValue : regExValues) {
		if (firmwareVersion.contains(keyValue)) {
		    isExpectedType = true;
		    break;
		}
	    }
	}
	return isExpectedType;
    }

    /**
     * Verify if given build is sprint build
     * 
     * @param firmwareVersion
     * @return true if sprint build
     */
    public static boolean isSprintBuild(String firmwareVersion) {
	boolean isExpectedType = false;
	String propRegExValue = AutomaticsPropertyUtility
		.getProperty(AutomaticsConstants.PROPERTY_SPRINT_BUILD_KEYWORDS);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    List<String> regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    for (String keyValue : regExValues) {
		if (firmwareVersion.contains(keyValue)) {
		    isExpectedType = true;
		    break;
		}
	    }
	}
	return isExpectedType;

    }

    /**
     * Verify if given build is release build
     * 
     * @param firmwareVersion
     * @return true if release build
     */
    public static boolean isReleaseBuild(String firmwareVersion) {
	return !(isSprintBuild(firmwareVersion) || isStableBuild(firmwareVersion));

    }

    /**
     * 
     * This method is used to check if valueToCheck is present in listString , which is a string of values separated by
     * comma. Method can be used while reading a property from st props and checking for a given value in it.
     * 
     * @param listString
     *            String separated by comma or without comma
     * @param valueToCheck
     *            Value to check
     * @return true/False is returned
     */
    public static boolean splitAndCheckValuePresent(String listString, String valueToCheck) {
	boolean isPresent = false;
	LOGGER.debug("Comparing : {} to {}", valueToCheck, listString);
	if (CommonMethods.isNotNull(listString) && CommonMethods.isNotNull(valueToCheck)) {
	    if (listString.contains(AutomaticsConstants.COMMA)) {
		String[] stringvalues = listString.split(AutomaticsConstants.COMMA);
		for (String eachValue : stringvalues) {

		    if (eachValue.toLowerCase().equals(valueToCheck.toLowerCase())) {
			isPresent = true;
			break;
		    }
		}
	    } else {
		if (listString.toLowerCase().equals(valueToCheck.toLowerCase())) {
		    isPresent = true;
		}
	    }
	}
	return isPresent;
    }

    public static boolean splitAndCheckSubstring(String listString, String valueToCheck) {
	boolean isPresent = false;
	LOGGER.debug("Comparing : {} to {}", valueToCheck, listString);
	if (CommonMethods.isNotNull(listString) && CommonMethods.isNotNull(valueToCheck)) {
	    if (listString.contains(AutomaticsConstants.COMMA)) {
		String[] stringvalues = listString.split(AutomaticsConstants.COMMA);
		for (String eachValue : stringvalues) {

		    if (valueToCheck.toLowerCase().contains(eachValue.toLowerCase())) {
			isPresent = true;
			break;
		    }
		}
	    } else {
		if (valueToCheck.toLowerCase().contains(listString.toLowerCase())) {
		    isPresent = true;
		}
	    }
	}
	return isPresent;
    }

    /**
     * 
     * Method to check if logging required by comapring it with calling thread name. For polling thread and connection
     * thread, console logging is generally disabled
     * 
     * @return Return boolean based on calling thread.
     */
    public static boolean isLoggingRequiredforCaller() {
	boolean isTrace = true;
	if (Thread.currentThread().getName().contains("PollingThread")
		|| Thread.currentThread().getName().contains("ConnectionThread")) {
	    isTrace = false;
	}
	return isTrace;
    }

    public static void iterateThroughtSettopAndRemoveMac(List<Dut> devicesSwitchedOff, Dut pivotRouter) {
	Iterator<Dut> iterator = devicesSwitchedOff.iterator();
	while (iterator.hasNext()) {
	    Dut dut = (Dut) iterator.next();
	    if (pivotRouter.getHostMacAddress().equals(dut.getHostMacAddress())) {
		iterator.remove();
	    }
	}
    }

    /**
     * API is used during init when device is not accessible
     * 
     * @param dut
     */
    public boolean rebootUsingRackProviderAndWait(Dut dut) {
	boolean isAccessible = false;
	LOGGER.info("\n<<<< Rebooting the device once since it is not accessible >>>>");
	try {
	    dut.powerOff();
	    dut.powerOn();
	    LOGGER.info("\n<<<< Waiting for device to come up >>>>");
	    AutomaticsUtils.sleep(AutomaticsConstants.TWO_MINUTES);
	    int retry = 5;
	    while (retry > 0) {
		retry--;
		isAccessible = CommonMethods.isSTBAccessible(dut);
		if (isAccessible) {
		    break;
		} else {
		    AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
		}
	    }

	} catch (Exception e) {
	    LOGGER.error("Unable to reboot device using Rack power provider . " + e.getMessage());
	}
	return isAccessible;
    }

}
