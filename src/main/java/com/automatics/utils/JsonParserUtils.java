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

import java.util.ArrayList;
import java.util.List;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON parser utility class.
 *
 * @author Pratheesh TK
 */
public class JsonParserUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParserUtils.class);

    private static final String JSON_MESSAGE_BEGINING_STR = "{\"";

    private static final String JSON_MESSAGE_ENDING_STR = "Connection closed by foreign host";

    /**
     * Retrieving boolean value from JSON object.
     *
     * @param jsonObj
     *            JSON object.
     * @param key
     *            Key corresponding to the value returned from JSON object.
     *
     * @return boolean value corresponding to key of JSON object.
     */
    public static boolean getBooleanFromJsonObject(JSONObject jsonObj, String key) {

	boolean value = false;

	try {
	    value = jsonObj.getBoolean(key);
	} catch (JSONException exc) {
	    LOGGER.info(exc.getMessage(), exc);
	}

	return value;
    }

    /**
     * Retrieving string value from JSON object.
     *
     * @param jsonObj
     *            JSON object.
     * @param key
     *            Key corresponding to the value returned from JSON object.
     *
     * @return value returned corresponding to the key of JSON object.
     */
    public static String getStringFromJsonObject(JSONObject jsonObject, String key) {

	try {
	    return jsonObject.getString(key);
	} catch (JSONException exc) {
	    LOGGER.info(exc.getMessage(), exc);
	}

	return null;
    }

    /**
     * Retrieving string value from JSON object.
     *
     * @param jsonObj
     *            JSON object.
     * @param mainKey
     *            Key corresponding to the JSON object which contain the subkey.
     * @param subKey
     *            Keys corresponding to the value returned from JSON object.
     *
     * @return value returned corresponding to the last key of JSON object.
     */
    public static String getStringFromJsonObject(JSONObject jsonObject, String mainKey, String subKey) {

	JSONObject tmpJsonObject = jsonObject;
	String value = null;

	try {

	    tmpJsonObject = tmpJsonObject.getJSONObject(mainKey);

	    value = tmpJsonObject.getString(subKey);

	} catch (JSONException exc) {
	    LOGGER.info(exc.getMessage(), exc);
	}

	return value;
    }

    /**
     * Retrieving string value from JSON message.
     *
     * @param jsonMessage
     *            JSON message.
     * @param key
     *            Key corresponding to the value returned from JSON object.
     *
     * @return value returned corresponding to the key of JSON object.
     */
    public static String getStringFromJsonMessage(String jsonMessage, String key) {

	String keyValue = null;

	try {
	    JSONObject jsonObject = new JSONObject(jsonMessage);
	    keyValue = jsonObject.getString(key);
	} catch (JSONException exc) {
	    LOGGER.info(exc.getMessage(), exc);
	}

	return keyValue;
    }

    /**
     * Retrieving string value from JSON String.
     *
     * @param jsonmsg
     *            json String.
     * @param key
     *            Key corresponding to the value returned from JSON object.
     *
     * @return value returned corresponding to the key of JSON object.
     */
    public static int getIntegerFromJsonMessage(String jsonMsg, String key) {
	int value = -1;

	try {
	    JSONObject jsonObj = new JSONObject(jsonMsg);
	    value = getIntegerFromJsonObject(jsonObj, key);
	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return value;
    }

    /**
     * Retrieving integer value from JSON object.
     *
     * @param jsonObj
     *            JSON object.
     * @param key
     *            Key corresponding to the value returned from JSON object.
     *
     * @return value returned corresponding to the key of JSON object.
     */
    public static int getIntegerFromJsonObject(JSONObject jsonObj, String key) {

	try {
	    return jsonObj.getInt(key);
	} catch (JSONException exc) {
	    LOGGER.info(exc.getMessage(), exc);
	}

	return -1;
    }

    /**
     * This message will return json object corresponding to the key.
     *
     * @param t2pmessage
     *
     * @return json object corresponding to the key
     */
    public static JSONObject getJsonObjectFromT2PResponseMessage(String jsonKeyValue, String t2PConsoleOutput) {

	String jsonMessage = getJsonMessageFromT2PConsoleOutput(jsonKeyValue, t2PConsoleOutput);
	JSONObject jsonObjectForKey = null;

	try {
	    JSONObject jsonObj = new JSONObject(jsonMessage);
	    jsonObjectForKey = jsonObj.getJSONObject(jsonKeyValue);
	} catch (JSONException exc) {
	    LOGGER.error(exc.getMessage(), exc);
	    throw new FailedTransitionException(GeneralError.CONSOLE_OUTPUT_COMPARISON_FAILURE, exc);
	}

	return jsonObjectForKey;
    }

    /**
     * Helper method to extract JSON message from the t2p response message.
     *
     * @param jsonMessageKey
     *            ,t2PResponseMessage
     *
     * @return JSON message
     */
    private static String getJsonMessageFromT2PConsoleOutput(String jsonMessageKey, String t2PResponseMessage) {
	int beginIndex = t2PResponseMessage.indexOf(JSON_MESSAGE_BEGINING_STR + jsonMessageKey);
	int endIndex = t2PResponseMessage.lastIndexOf(JSON_MESSAGE_ENDING_STR);

	LOGGER.info(String.format("Begin Index : %s\nEnd Index : %s", beginIndex, endIndex));

	if (-1 == beginIndex || endIndex <= beginIndex) {
	    throw new FailedTransitionException(GeneralError.CONSOLE_OUTPUT_COMPARISON_FAILURE,
		    "Not able to find the required JSON message for the passed JSON key : " + jsonMessageKey);
	}

	String jsonMessage = t2PResponseMessage.substring(beginIndex, endIndex);
	LOGGER.debug("Parsed Json message : " + jsonMessage);

	return jsonMessage;
    }

    /**
     * Helper method to get the list of objects from a JSON object , response which contains the specified value in a
     * given property key.
     *
     * @param jsonObject
     *            The json object for list response
     * @param subObjectKey
     *            The key used for splitting in to another set of json object array
     * @param propertyKey
     *            The property in the json object to be verified
     * @param expectedValueForKey
     *            The value for the property key
     *
     * @return Returns the array of json objects which have the expected value in the given key property.
     */
    public static List<JSONObject> getJsonObjectArrayForExpectedValueWithSpecificKeyFromJsonResponse(
	    JSONObject jsonObject, String subObjectKey, String propertyKey, String expectedValueForKey) {
	List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();

	try {

	    // Splits the main object to array of objects
	    JSONArray subObjectArray = jsonObject.getJSONArray(subObjectKey);
	    LOGGER.info("sub object array length -->" + subObjectArray.length());

	    for (int i = 0; i < subObjectArray.length(); i++) {
		JSONObject subObject = subObjectArray.getJSONObject(i);

		if (subObject.getString(propertyKey).contentEquals(expectedValueForKey)) {
		    jsonObjectList.add(subObject);
		}
	    }

	} catch (JSONException e) {
	    LOGGER.info(e.getMessage(), e);
	}

	// Returns the json objects list containing the specific key value pair
	return jsonObjectList;

    }

}
