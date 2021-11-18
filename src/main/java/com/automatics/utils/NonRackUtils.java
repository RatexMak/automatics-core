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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.enums.EndPoints;

public class NonRackUtils {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NonRackUtils.class);

    public static Map<String, String> macBhcModelNameMap = new HashMap<String, String>();

    public static JSONArray deviceArray = null;

    public static boolean isNonRack() {
	return System.getProperty("isNonRack", "N").equalsIgnoreCase("Y") || isDeskBoxTesting();
    }

    public static boolean isRack() {
	return !isNonRack();
    }

    public static boolean disableSettopTrace() {
	return (System.getProperty("skipDevicePreConfiguration", "N").equalsIgnoreCase("Y") || System.getProperty(
		"skipTrace", "N").equalsIgnoreCase("Y"));
    }

    public static boolean enableSettopTrace() {
	return !disableSettopTrace();
    }

    /**
     * 
     * Method to check whether job needs to be run as in a desk_box environment
     * 
     * @return Returns true if desk box env needs to be considered for execution
     */
    public static boolean isDeskBoxTesting() {
	boolean isDeskBoxTesting = false;
	String endPoint = System.getProperty("end_point", "");
	if (!endPoint.contains(EndPoints.RACK_DEVICE.toString())) {
	    LOGGER.info("Desk Box Testing enabled");
	    isDeskBoxTesting = true;
	}
	return isDeskBoxTesting;
    }

}
