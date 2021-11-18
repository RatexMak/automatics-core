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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeConversionUtils {

    final static Logger LOGGER = LoggerFactory.getLogger(TimeConversionUtils.class);

    /**
     * 
     * Method to get time in millis in a timezone from date string .This method can be used for time related comparisons
     * 
     * @param timetoCOnvert
     *            Time to be converted
     * @param dateFormat
     *            Format in which date string is formatted
     * @param zone
     *            Time zone
     * @return Time in millis for that zone
     */
    public static long converStringToTimeInMillis(String timetoCOnvert, String dateFormat, String zone) {
	SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
	formatter.setTimeZone(TimeZone.getTimeZone(zone));
	Date date;
	long nowInMillis = 0;
	try {
	    if (CommonMethods.isNotNull(timetoCOnvert)) {
		date = formatter.parse(timetoCOnvert);
		nowInMillis = date.getTime();
	    }
	} catch (ParseException e) {
	    LOGGER.error(e.getMessage());
	}
	LOGGER.debug("Time in millis = " + nowInMillis);
	return nowInMillis;
    }

}
