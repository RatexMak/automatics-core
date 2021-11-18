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
package com.automatics.webpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.exceptions.TestException;
import com.automatics.providers.webpa.WebpaProvider;
import com.automatics.restclient.RestClient;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;
import com.automatics.restclient.RestClientException;
import com.automatics.restclient.RestEasyClientImpl;
import com.automatics.restclient.RestRequest;
import com.automatics.restclient.RestResponse;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.google.gson.Gson;

/**
 * Establish connection with WebPA server and collect the data from the server. Purpose of this to validate the WebPA
 * related functionality.
 * 
 *
 * 
 */
public class WebPaConnectionHandler {

    /**
     * The logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebPaConnectionHandler.class);

    /**
     * WebPA retry count in case of failure.
     */
    public static final int WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE = 4;

    /**
     * WebPA param get or set failure
     */
    private static final int WEBPA_PARAM_FAILURE = 520;

    /**
     * Connection timeout.
     */
    private static final int CONNECTION_TIMEOUT = 60000;

    /**
     * Single instance of webpa connection handler.
     */
    private static WebPaConnectionHandler webPaConnHandler = null;

    /**
     * Get the WebPaConnectionHandler instance.
     * 
     * @return reference of WebPaConnectionHandler instance
     */
    public static WebPaConnectionHandler get() {
	if (null == webPaConnHandler) {
	    webPaConnHandler = new WebPaConnectionHandler();
	}
	return webPaConnHandler;
    }

    private WebPaConnectionHandler() {
	/*
	 * Do nothing.
	 */
    }

    /**
     * enum to store the type of Webpa request
     */
    public enum WebPaType {

	GET,
	SET,
	POST,
	PUT,
	DELETE
    }

    /**
     * Utility method to get WebPa parameter values using WebPa
     *
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param parameter
     *            String representing the Table Name
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Raja
     */

    public WebPaEntityResponse getWebPaTableParamValue(Dut dut, String parameter) throws RestClientException {
	LOGGER.info("Started method getWebPaTableParamValue()");

	// Variable to store the response
	RestResponse response = null;
	WebPaEntityResponse webPaEntityResponse = new WebPaEntityResponse();

	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {

		LOGGER.debug("STARTING METHOD: getWebPaTableParamValue");
		String completeUrl = getFormattedWebPaUrl(dut, parameter, WebPaType.GET);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.GET);
		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.GET, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		response = restClient.executeAndGetResponse(request);

		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA GET REQUEST: " + statusCode);

		String responseAsString = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", responseAsString);

		if (CommonMethods.isNotNull(responseAsString)) {

		    if (CommonMethods.isValidJsonString(responseAsString)) {
			webPaEntityResponse = webPaEntityResponse.fromJson(responseAsString);
		    }
		}

		if (HttpStatus.SC_OK == response.getResponseCode() || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + responseAsString);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + responseAsString);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}

	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: getWebPaTableParamValue");
	return webPaEntityResponse;
    }

    /**
     * Utility method to get WebPa parameter values using WebPa
     *
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param parameter
     *            String representing the Table Name
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Raja
     */
    public WebPaServerResponse getWebPaParamValue(String macAddress, String[] parameters) {
	// Variable to store the response
	RestResponse response = null;
	WebPaServerResponse webPaServerResponse = new WebPaServerResponse();
	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.debug("STARTING METHOD: getWebPaParamValue");

		LOGGER.debug("STARTING METHOD: getWebPaTableParamValue");
		StringBuffer completeUrl = new StringBuffer();
		String restUrl = getServerURL();
		completeUrl = completeUrl.append(restUrl).append(macAddress.replaceAll(":", "")).append("/config")
			.append("?names=").append(convertToCommaSeparatedList(parameters));

		Map<String, String> headers = fetchAuthHeaders(WebPaType.GET);
		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl.toString(), HttpRequestMethod.GET, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		response = restClient.executeAndGetResponse(request);

		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA GET REQUEST: " + statusCode);

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA GET REQUEST: " + statusCode);

		String responseAsString = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", responseAsString);
		if (CommonMethods.isNotNull(responseAsString)) {

		    if (CommonMethods.isValidJsonString(responseAsString)) {
			webPaServerResponse = webPaServerResponse.fromJson(responseAsString);
		    }
		}
		if (HttpStatus.SC_OK == statusCode || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + responseAsString);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + responseAsString);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: getWebPaParamValueUsingRestApi");
	return webPaServerResponse;
    }

    /**
     * Utility method to put WebPa parameter values
     * 
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * @param paramKeyValue
     *            Map representing the parameter which is in key value pair.
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @throws JSONException
     * 
     * @author Raja
     */
    public WebPaServerResponse putWebPaParameterValue(Dut dut, String tableName,
	    Map<String, HashMap<String, List<String>>> paramKeyValue) {
	// Variable to store the response
	RestResponse response = null;
	JSONObject paramDetailsJson = null;
	WebPaServerResponse webpaResponse = new WebPaServerResponse();

	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.debug("STARTING METHOD: putWebPaParameterValue");
		paramDetailsJson = new JSONObject();
		JSONObject tmpJsonObject = null;
		List<String> valuesList = null;
		for (String key : paramKeyValue.keySet()) {
		    Map<String, List<String>> mapOfValues = paramKeyValue.get(key);
		    tmpJsonObject = new JSONObject();
		    for (String keyValue : mapOfValues.keySet()) {
			valuesList = mapOfValues.get(keyValue);
			tmpJsonObject.put(keyValue, valuesList.get(0));
		    }
		    paramDetailsJson.put(key, tmpJsonObject);
		}
		String completeUrl = getFormattedWebPaUrl(dut, tableName, WebPaType.PUT);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.PUT);

		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.PUT, headers);

		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		request.setContent(paramDetailsJson.toString());

		response = restClient.executeAndGetResponse(request);

		LOGGER.info("JSON PAYLOAD DATA BUILD FOR WEBPA PUT REQUEST: " + paramDetailsJson.toString());
		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA PUT REQUEST: " + statusCode);
		String serverResponse = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", serverResponse);

		if (CommonMethods.isNotNull(serverResponse)) {

		    if (CommonMethods.isValidJsonString(serverResponse)) {
			webpaResponse = webpaResponse.fromJson(serverResponse);
		    }
		}
		if (HttpStatus.SC_OK == statusCode || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + serverResponse);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + serverResponse);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (JSONException e) {
		LOGGER.error("FOLLOWING JSONException OCCURED WHILE WEBPA PUT REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA PUT REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA PUT REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: putWebPaParameterValue");
	return webpaResponse;
    }

    /**
     * Utility method to post WebPa parameter values using WebPa
     * 
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param tableName
     *            String representing the Table Name
     * @param paramKeyValue
     *            Map representing the parameter which is in key value pair.
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @throws JSONException
     * 
     * @author Raja
     * @throws RestClientException
     */

    public WebPaServerResponse postWebPaParameterValue(Dut dut, String tableName,
	    Map<String, List<String>> paramKeyValue) {
	// Variable to store the response
	RestResponse response = null;
	JSONObject jsonObject = null;
	WebPaServerResponse webpaResponse = new WebPaServerResponse();

	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.debug("STARTING METHOD: postWebPaParameterValue");
		jsonObject = new JSONObject();
		for (String key : paramKeyValue.keySet()) {
		    List<String> valuesList = paramKeyValue.get(key);
		    jsonObject.put(key, valuesList.get(0));
		}
		String completeUrl = getFormattedWebPaUrl(dut, tableName, WebPaType.POST);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.POST);

		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.POST, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		request.setContent(jsonObject.toString());
		response = restClient.executeAndGetResponse(request);

		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA POST REQUEST: " + statusCode);
		String serverResponse = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", serverResponse);

		if (CommonMethods.isNotNull(serverResponse)) {
		    if (CommonMethods.isValidJsonString(serverResponse)) {
			webpaResponse = webpaResponse.fromJson(serverResponse);
		    }
		}
		if (HttpStatus.SC_CREATED == statusCode || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + serverResponse);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + serverResponse);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA POST REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA POST REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: postWebPaParameterValue");
	return webpaResponse;
    }

    /**
     * Utility method to delete using WebPa
     * 
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param parameter
     *            String representing the Table Name along with row id.
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Raja
     */
    public WebPaServerResponse deleteWebPaParameterValue(Dut dut, String parameter) {
	// Variable to store the response
	RestResponse response = null;
	WebPaServerResponse webPaServerResponse = new WebPaServerResponse();
	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.debug("STARTING METHOD: deleteWebPaParameterValue");
		String completeUrl = getFormattedWebPaUrl(dut, parameter, WebPaType.DELETE);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.DELETE);

		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.DELETE, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		response = restClient.executeAndGetResponse(request);

		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA DELETE REQUEST: " + statusCode);
		String serverResponse = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", serverResponse);

		if (CommonMethods.isNotNull(serverResponse)) {
		    if (CommonMethods.isValidJsonString(serverResponse)) {
			webPaServerResponse = webPaServerResponse.fromJson(serverResponse);
		    }
		}
		if (HttpStatus.SC_OK == statusCode || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + serverResponse);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + serverResponse);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (FailedTransitionException e) {
		LOGGER.error(
			"FOLLOWING FailedTransitionException OCCURED WHILE WEBPA DELETE REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA DELETE REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: deleteWebPaParameterValue");
	return webPaServerResponse;
    }

    /**
     * Utility method to set WebPa parameter values using WebPa
     * 
     * @param dut
     *            The dut to be validated.
     * @param parameters
     *            The parameter list.
     * @return WebPa Parameter response.
     * @throws JSONException
     */
    public WebPaServerResponse setWebPaParameterValue(Dut dut, List<WebPaParameter> parameters) {
	RestResponse response = null;
	WebPaServerResponse webPaServerResponse = new WebPaServerResponse();
	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.debug("STARTING METHOD: setWebPaParameterValue");
		WebPaParameterList paramList = new WebPaParameterList();
		paramList.setList(parameters);
		String json = new Gson().toJson(paramList);

		String completeUrl = getFormattedWebPaUrl(dut, null, WebPaType.SET);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.SET);

		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.PATCH, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		request.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		request.setContent(json.toString());

		response = restClient.executeAndGetResponse(request);
		int statusCode = response.getResponseCode();

		LOGGER.info("RESPONSE RECIEVED FOR WEBPA SET REQUEST: " + statusCode);
		String serverResponse = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", serverResponse);

		if (CommonMethods.isNotNull(serverResponse)) {
		    if (CommonMethods.isValidJsonString(serverResponse)) {
			webPaServerResponse = webPaServerResponse.fromJson(serverResponse);
		    }
		}
		if (HttpStatus.SC_OK == statusCode || WEBPA_PARAM_FAILURE == statusCode) {

		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + serverResponse);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + serverResponse);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA SET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA SET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: setWebPaParameterValue");
	return webPaServerResponse;
    }

    /**
     * Utility method to get WebPa parameter values
     *
     * @param tapEnv
     *            AutomaticsTapApi
     * @param dut
     *            The device under test
     * @param parameter
     *            String representing the Table Name
     * 
     * @return The response of the execution of WebPA parameter.
     * 
     * @author Raja
     */
    public WebPaServerResponse getWebPaParamValue(Dut dut, String[] parameters) {
	// Variable to store the response
	RestResponse response = null;
	WebPaServerResponse webPaServerResponse = new WebPaServerResponse();
	for (int retryCount = 0; retryCount < WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE; retryCount++) {
	    if (retryCount > 0) {
		LOGGER.error("Failed to get the HTTP response from webpa. Retrying attempt - " + retryCount);
	    }
	    try {
		LOGGER.info("STARTING METHOD: getWebPaParamValue");
		LOGGER.info("STARTING METHOD4 : dut {}", dut);
		String completeUrl = getFormattedWebPaUrl(dut, convertToCommaSeparatedList(parameters), WebPaType.GET);

		Map<String, String> headers = fetchAuthHeaders(WebPaType.GET);
		RestClient restClient = new RestEasyClientImpl();
		RestRequest request = new RestRequest(completeUrl, HttpRequestMethod.GET, headers);
		request.setTimeoutInMilliSeconds(CONNECTION_TIMEOUT);
		response = restClient.executeAndGetResponse(request);

		int statusCode = response.getResponseCode();
		LOGGER.info("RESPONSE RECIEVED FOR WEBPA GET REQUEST: " + statusCode);

		String responseAsString = response.getResponseBody();
		LOGGER.info("WEBPA RESPONSE : {}", responseAsString);

		if (CommonMethods.isNotNull(responseAsString)) {
		    if (CommonMethods.isValidJsonString(responseAsString)) {
			webPaServerResponse = webPaServerResponse.fromJson(responseAsString);
		    }
		}
		if (HttpStatus.SC_OK == statusCode || WEBPA_PARAM_FAILURE == statusCode) {
		    break;
		} else if (retryCount > WEBPA_RETRY_COUNT_IN_CASE_OF_FAILURE) {
		    LOGGER.error("Got server error response -> Status code =" + statusCode + ", Message = "
			    + responseAsString);
		    throw new FailedTransitionException(GeneralError.TR_069_WEB_PA_COMMINICATION_ERROR,
			    "Status code =" + statusCode + ", Message = " + responseAsString);
		} else if (statusCode == 404 || statusCode == 530 || statusCode == 531) {
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		} else {
		    // We are seeing large number of failures because of WebPA - 404
		    // errors and 530 errors which are related with connectivity between WebPA
		    // server and CPE device which is expected to resume within seconds or minutes.
		    // Instead of marking the test case as failure, wait for 30 seconds and try once
		    // again.
		    AutomaticsUtils.sleep(AutomaticsConstants.TEN_SECONDS);
		}
	    } catch (FailedTransitionException e) {
		LOGGER.error("FOLLOWING FailedTransitionException OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    } catch (Exception e) {
		LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE WEBPA GET REQUEST: " + e.getMessage());
		throw new TestException(e.getMessage());
	    }
	}
	LOGGER.debug("ENDING METHOD: getWebPaParamValueUsingRestApi");
	return webPaServerResponse;
    }

    private String getServerURL() {
	return AutomaticsPropertyUtility.getProperty("WEBPA_SERVER_URL", "");
    }

    /**
     * Convert the String array to list of string with command separated. Also append starting and and ending braces.
     * 
     * @param parameters
     *            Array of parameters
     * @return String in which parameters are separated by comma
     */
    private String convertToCommaSeparatedList(final String[] parameters) {
	int parametersLength = parameters.length;
	String requiredParams = null;
	/*
	 * If only one parameter, we don't need process anything.
	 */
	if (parametersLength > 1) {
	    StringBuffer paramList = new StringBuffer();
	    for (int index = 0; index < parametersLength; index++) {
		paramList.append(parameters[index]);
		if (index != parametersLength - 1) {
		    paramList.append(",");
		}
	    }
	    requiredParams = paramList.toString();
	} else {
	    requiredParams = parameters[0];
	}
	return requiredParams;
    }

    /**
     * Helper method to format the webpa Rest API URL
     * 
     * @param dut
     *            The dut to be used.
     * @param parameter
     *            The parameter list.
     * @param webpaType
     *            The flag to check whether get or set operation.
     * @return The formatted webpa rest API.
     * 
     * @author Raja
     */
    private String getFormattedWebPaUrl(Dut dut, String parameter, WebPaType webpaType) {
	LOGGER.debug("STARTING METHOD: getFormattedWebPaUrl");
	StringBuffer completeUrl = new StringBuffer();
	try {
	    // Getting webpa rest Url "webpa.restapi.url"
	    WebpaProvider webpaProvider = BeanUtils.getWebpaProvider();

	    String restUrl = getServerURL();
	    completeUrl = completeUrl.append(restUrl)
		    .append((webpaProvider.getDeviceMacAddress(dut)).replaceAll(":", ""));

	    switch (webpaType) {
	    case PUT: {
		completeUrl.append("/config/");
		completeUrl.append(parameter);
		break;
	    }
	    case POST: {
		completeUrl.append("/config/");
		completeUrl.append(parameter);
		break;
	    }
	    case GET: {
		completeUrl.append("/config").append("?names=");
		completeUrl.append(parameter);
		break;
	    }
	    case DELETE: {
		completeUrl.append("/config/");
		completeUrl.append(parameter);
		break;
	    }
	    case SET: {
		completeUrl.append("/config");
		break;
	    }
	    default: {
		completeUrl.append("/config").append("?names=");
		completeUrl.append(parameter);
		break;

	    }
	    }
	    LOGGER.info("COMPLETE URL BUILD FOR \"" + webpaType + "\" IS : " + completeUrl.toString());

	} catch (Exception e) {
	    LOGGER.error("FOLLOWING EXCEPTION OCCURED WHILE BUILDING URL FOR \"" + webpaType + "\" WEBPA REQUEST: "
		    + e.getMessage());
	    throw new TestException(e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getFormattedWebPaUrl");
	return completeUrl.toString();
    }

    /**
     * Utility method to get the AuthHeaders from client for WebPa Rest API
     */

    private Map<String, String> fetchAuthHeaders(WebPaType methodType) {
	WebpaProvider webpaProvider = BeanUtils.getWebpaProvider();
	return webpaProvider.getRequestHeaderAuthData(methodType);
    }
}
