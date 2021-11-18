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

package com.automatics.constants;

public class ScreenResolutionConstants {

    /** SNMP response for HDMI output format 1080i */
    public static final String SNMP_RESPONSE_OUTPUT_FORMAT1080p = "= 5";
    /** SNMP response for HDMI output format 1080i */
    public static final String SNMP_RESPONSE_OUTPUT_FORMAT1080i = "= 4";
    /** SNMP response for HDMI output format 720p */
    public static final String SNMP_RESPONSE_OUTPUT_FORMAT720p = "= 3";
    /** SNMP response for HDMI output format 480p */
    public static final String SNMP_RESPONSE_OUTPUT_FORMAT480p = "= 2";
    public static final String SNMP_RESPONSE_OUTPUT_FORMAT480i = "= 1";

    /**
     * Output resolution names. Enum holds the Keys to send for setting the resolution and the text for verification.
     * 
     * @author Preethi U
     */
    public enum OutputResolutions {

	/** Output resolution 480i. */
	RESOLUTION_480I("480", "480i", ScreenResolutionConstants.SNMP_RESPONSE_OUTPUT_FORMAT480i),

	/** Output resolution 480p. */
	RESOLUTION_480P("480", "480p", ScreenResolutionConstants.SNMP_RESPONSE_OUTPUT_FORMAT480p),

	/** Output resolution 720p. */
	RESOLUTION_720P("720", "720p", ScreenResolutionConstants.SNMP_RESPONSE_OUTPUT_FORMAT720p),

	/** Output resolution 1080i. */
	RESOLUTION_1080I("1080", "1080i", ScreenResolutionConstants.SNMP_RESPONSE_OUTPUT_FORMAT1080i),

	/** Output resolution 1080p60. */
	RESOLUTION_1080P60("1080", "1080p60", ScreenResolutionConstants.SNMP_RESPONSE_OUTPUT_FORMAT1080p),

	/** Output resolution 1080p30. */
	RESOLUTION_1080P30("1080", "1080p30", null),

	/** Output resolution 576p. */
	RESOLUTION_576P("576", "576p", null),

	/** Output resolution 1080p. */
	RESOLUTION_1080P("1080", "1080p", null),

	/** Output resolution 2160p. */
	RESOLUTION_2160P("2160", "2160p", null);

	/** OutputResolutions value. */
	private String value;

	/** OutputResolutions name. */
	private String name;

	/** OutputResolutions id. */
	private String snmpValue;

	/**
	 * Enum constructor.
	 */
	private OutputResolutions(String value, String name, String id) {
	    this.value = value;
	    this.name = name;
	    this.snmpValue = id;
	}

	/**
	 * Method returns value corresponding to an item.
	 * 
	 * @return value value corresponding to an item
	 */

	public String getValue() {
	    return this.value;
	}

	/**
	 * Method returns name corresponding to an item.
	 * 
	 * @return value name corresponding to an item
	 */

	public String getName() {
	    return this.name;
	}

	/**
	 * Method to return id corresponding to an item
	 * 
	 * @return id corresponding to an item
	 */
	public String getSnmpValue() {
	    return snmpValue;
	}

    }

}
