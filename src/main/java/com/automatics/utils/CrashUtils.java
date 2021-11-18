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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.automatics.providers.crashanalysis.CrashDetails;
import com.automatics.providers.crashanalysis.CrashType;

/**
 * 
 * Utils for Crash management related operations
 *
 */
public class CrashUtils {

    /** Templates for ticket details **/
    public static final String SUMMARY_TEMPLATE = "[AUTO][<model>][<branch>]<app> crash with fingerprint <signature> due to <reason>";
    public static final String DESCRIPTION_TEMPLATE = "Observed crash in below device during execution of <step> of test case <testcaseid>.\nExecution logs: <buildurl>\n\nDetails of crash is as below.\n\n*+Device Details+*\n<details>\n";
    public static final String LABEL_TEMPLATE = "cpeautoCrash_<app>_Signature";
    public static final String STACKTRACE_TEMPLATE = "{noformat}<stacktrace>{noformat}";

    /**
     * Gets the crash type
     * 
     * @param crash
     * @return crash type
     */
    public static CrashType getCrashType(CrashDetails crash) {
	CrashType type = CrashType.MINIDUMP;
	if (crash.isCoredump()) {
	    type = CrashType.COREDUMP;
	}
	return type;
    }

    /**
     * 
     * Formats date and time
     * 
     * @param startTime
     *            Time in string
     * @param requestTimeFormat
     *            Format of specified time
     * @param id
     *            Zone id of specified time
     * @return Reformats time to yyyy/MM/dd/HH
     */
    public static String formatTimeForProcessApi(String startTime, DateTimeFormatter requestTimeFormat, ZoneId id) {
	String requiredFormat = "yyyy/MM/dd/HH";
	LocalDateTime ldt = LocalDateTime.parse(startTime, requestTimeFormat);
	ZonedDateTime availableDateTime = ldt.atZone(id);
	ZoneId utcId = ZoneId.of("UTC");
	ZonedDateTime utcDateTime = availableDateTime.withZoneSameInstant(utcId);
	DateTimeFormatter format = DateTimeFormatter.ofPattern(requiredFormat);
	String formattedUtcDateTime = format.format(utcDateTime);
	return formattedUtcDateTime;
    }

}
