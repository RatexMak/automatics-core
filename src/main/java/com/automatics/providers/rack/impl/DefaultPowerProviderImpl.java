/**
 * Copyright 2022 Comcast Cable Communications Management, LLC
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
package com.automatics.providers.rack.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.providers.objects.GetPowerOperation;
import com.automatics.providers.objects.PowerManagerOperationResponse;
import com.automatics.providers.objects.PowerStatus;
import com.automatics.providers.rack.AbstractPowerProvider;
import com.automatics.providers.rack.exceptions.PowerProviderException;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

/**
 * Power provider that fetches remote power manager details from Device Manager to manage power operations on device.
 * 
 * @author radhikas
 *
 */
public class DefaultPowerProviderImpl extends AbstractPowerProvider {

    private static final String KEY_RESPONSE_CONTENT_TYPE = "responseContentType";

    private static final String JSON_RESPONSE_CONTENT = "json";
    private static final String XML_RESPONSE_CONTENT = "xml";
    private static final String TEXT_RESPONSE_CONTENT = "text";

    private static final String KEY_REQUEST_HEADER = "requestHeader";

    private static final String KEY_REQUEST_CONTENT_TYPE = "requestContentType";

    private static final String GET_POWER_DETAILS = "/powerOperations/getDetails";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPowerProviderImpl.class);

    @Override
    public boolean powerOn() throws PowerProviderException {
	return performPowerOperation("ON");
    }

    @Override
    public boolean powerOff() throws PowerProviderException {
	return performPowerOperation("OFF");
    }

    @Override
    public boolean reboot() throws PowerProviderException {
	return performPowerOperation("POWER_CYCLE");
    }

    @Override
    public String getPowerStatus() throws PowerProviderException {
	Response response = null;
	String powerStatus = PowerStatus.OFF.name();

	if (null != device) {

	    PowerManagerOperationResponse powerOperation = getPowerOperationDetails(device.getHostMacAddress(),
		    "POWER_STATUS");
	    if (null != powerOperation) {

		// Send power operation request
		response = sendPowerOperationRequest(powerOperation);

		// Get power status response
		String customStatusValue = getPowerResponse(response, powerOperation);

		powerStatus = mapCustomPowerStatusValue(customStatusValue);

	    } else {
		LOGGER.error("Power Operation returned null for device.");
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");

	}

	return powerStatus;
    }

    /**
     * Gets power operation details for the device
     * 
     * @param macAddress
     * @param powerOperation
     * @return
     * @throws PowerProviderException
     */
    public PowerManagerOperationResponse getPowerOperationDetails(String macAddress, String powerOperation)
	    throws PowerProviderException {

	PowerManagerOperationResponse powerResponse = null;

	ResteasyClient client = getClient();
	String url = CommonMethods.getNormalizedUrl(TestUtils.getDeviceManagerUrl() + GET_POWER_DETAILS);

	LOGGER.info("Sending request to Device Manager to get device power details: {}", url);
	ResteasyWebTarget target = client.target(url);

	GetPowerOperation request = new GetPowerOperation();
	request.setMacAddress(macAddress);
	request.setPowerOperation(powerOperation);
	LOGGER.info(
		"Sending request to Device Manager to get Power Operation Details for mac address {} and power operation {}",
		macAddress, powerOperation);

	Response response = target.request().post(Entity.entity(request, "application/json"));
	if (null != response) {
	    if (response.getStatus() == HttpStatus.SC_OK) {
		String respData = response.readEntity(String.class);

		LOGGER.info("Power Operation response: {}", respData);
		if (CommonMethods.isNotNull(respData)) {

		    ObjectMapper mapper = new ObjectMapper();
		    try {
			powerResponse = mapper.readValue(respData, PowerManagerOperationResponse.class);
		    } catch (IOException e) {
			LOGGER.error("Exception parsing json for device power operations {} response via rest api",
				macAddress, e);
			throw new PowerProviderException("Failed to get power operations details");

		    }
		}
	    }
	}

	return powerResponse;

    }

    public String parseXmlResponse(String respData, String template) {

	String customStatus = null;

	try {

	    XPathFactory xPathfactory = XPathFactory.newInstance();
	    customStatus = xPathfactory.newXPath().evaluate(template, new InputSource(new StringReader(respData)));

	} catch (Exception e) {
	    LOGGER.error("Error parsing xml using xpath: {}", e.getMessage());
	}
	return customStatus;

    }

    public String parseJsonResponse(String respData, String template) {

	String response = JsonPath.read(respData, template);
	LOGGER.info("Json value read using json path: {} - {}", template, response);
	return response;

    }

    private String getPowerSwitchUrl(PowerManagerOperationResponse powerOperation) throws PowerProviderException {

	String urlTemplate = powerOperation.getUrlTemplate();
	LOGGER.info("Power Swtich Url Template : {}", urlTemplate);
	if (CommonMethods.isNull(urlTemplate)) {
	    throw new PowerProviderException("Power Switch Url Template is null/empty.");
	}

	if (urlTemplate.contains("{MAC_ADDRESS}")) {
	    urlTemplate = urlTemplate.replace("{MAC_ADDRESS}", device.getHostMacAddress());
	}

	if (urlTemplate.contains("{SLOT_NUMBER}")) {
	    urlTemplate = urlTemplate.replace("{SLOT_NUMBER}", Integer.toString(powerOperation.getSlot()));
	}

	if (urlTemplate.contains("{GROUP_NAME}")) {
	    urlTemplate = urlTemplate.replace("{GROUP_NAME}", powerOperation.getGroupName());
	}

	LOGGER.info("Power Swtich Url: {}", urlTemplate);
	return urlTemplate;

    }

    private String getRequestBody(PowerManagerOperationResponse powerOperation) throws PowerProviderException {

	String requestBodyTemplate = powerOperation.getRequestBodyTemplate();
	LOGGER.info("Power Swtich Request Body Template : {}", requestBodyTemplate);
	if (CommonMethods.isNotNull(requestBodyTemplate)) {

	    if (requestBodyTemplate.contains("{MAC_ADDRESS}")) {
		requestBodyTemplate = requestBodyTemplate.replaceAll("\\{MAC_ADDRESS\\}", device.getHostMacAddress());
	    }

	    if (requestBodyTemplate.contains("{SLOT_NUMBER}")) {
		requestBodyTemplate = requestBodyTemplate.replaceAll("\\{SLOT_NUMBER\\}",
			Integer.toString(powerOperation.getSlot()));
	    }

	    if (requestBodyTemplate.contains("{GROUP_NAME}")) {
		requestBodyTemplate = requestBodyTemplate.replaceAll("\\{GROUP_NAME\\}", powerOperation.getGroupName());
	    }

	    requestBodyTemplate = requestBodyTemplate.replaceAll(AutomaticsConstants.SYMBOL_SINGLE_QUOTE,
		    AutomaticsConstants.DOUBLE_QUOTE);
	    LOGGER.info("Power Swtich Request Body: {}", requestBodyTemplate);
	}
	return requestBodyTemplate;

    }

    private String getContentType(String extraProperties) throws PowerProviderException {

	String contentType = getValueFromExtraProperties(extraProperties, KEY_REQUEST_CONTENT_TYPE);

	if (CommonMethods.isNull(contentType)) {
	    contentType = AutomaticsConstants.JSON_CONTENT_TYPE;
	}

	LOGGER.info("Request Content Type : {}", contentType);
	return contentType;

    }

    private int getSuccessResponseCode(String expectedResponseCode) throws PowerProviderException {

	int responseCode = AutomaticsConstants.CONSTANT_200;

	LOGGER.info("Power Switch Expected Response Code configured: {}", expectedResponseCode);

	if (CommonMethods.isNotNull(expectedResponseCode)) {

	    try {
		responseCode = Integer.parseInt(expectedResponseCode);
	    } catch (NumberFormatException e) {
		LOGGER.error("Error parsing response code for Power Switch");
	    }
	}

	LOGGER.info("Power Switch Expected Response Code : {}", responseCode);
	return responseCode;

    }

    private boolean validateResponseBody(String responseBodyTemplate) {
	boolean validate = false;
	if (CommonMethods.isNotNull(responseBodyTemplate)) {
	    validate = true;
	}

	return validate;
    }

    private boolean performPowerOperation(String operation) throws PowerProviderException {
	Response response = null;
	boolean isSuccess = false;

	if (null != device) {

	    PowerManagerOperationResponse powerOperation = getPowerOperationDetails(device.getHostMacAddress(),
		    operation);
	    if (null != powerOperation) {

		// Send power operation request
		response = sendPowerOperationRequest(powerOperation);

		// Get power response
		String customStatusValue = getPowerResponse(response, powerOperation);

		isSuccess = mapCustomPowerValue(customStatusValue, operation);

	    } else {
		LOGGER.error("Power Operation returned null for device.");
	    }
	} else {
	    LOGGER.error("PowerProvider is not initialized with device");

	}

	return isSuccess;
    }

    private Response sendPowerOperationRequest(PowerManagerOperationResponse powerOperation)
	    throws PowerProviderException {

	String requestBody = null;
	String contentType = null;
	Response response = null;

	String requestType = powerOperation.getRequestType();
	String url = getPowerSwitchUrl(powerOperation);
	MultivaluedMap<String, Object> headers = getRequestHeader(powerOperation.getExtraProperties());

	ResteasyClient client = getClient();
	ResteasyWebTarget target = client.target(url);

	LOGGER.info("Sending power request for device {} : {}", device.getHostMacAddress(), url);

	switch (requestType) {
	case "GET":
	    response = target.request().headers(headers).get();
	    break;

	case "POST":
	    contentType = getContentType(powerOperation.getExtraProperties());
	    requestBody = getRequestBody(powerOperation);

	    LOGGER.info("Request body : {}", requestBody);
	    response = target.request().headers(headers).post(Entity.entity(requestBody, contentType));
	    break;

	case "PUT":
	    contentType = getContentType(powerOperation.getExtraProperties());
	    requestBody = getRequestBody(powerOperation);

	    LOGGER.info("Request body : {}", requestBody);
	    response = target.request().headers(headers).put(Entity.entity(requestBody, contentType));
	    break;

	case "DELETE":
	    response = target.request().headers(headers).delete();
	    break;
	}

	return response;
    }

    private String getPowerResponse(Response response, PowerManagerOperationResponse powerOperation)
	    throws PowerProviderException {

	String customResponse = null;

	if (null != response) {

	    // Get response code for Power Switch
	    int expectedResponseCode = getSuccessResponseCode(powerOperation.getResponseCode());

	    if (response.getStatus() == expectedResponseCode) {

		String respData = response.readEntity(String.class);

		LOGGER.info("Power switch response code: {}", response.getStatus());
		LOGGER.info("Power switch response: {}", respData);

		if (validateResponseBody(powerOperation.getResponseBodyTemplate())) {

		    String responseType = getValueFromExtraProperties(powerOperation.getExtraProperties(),
			    KEY_RESPONSE_CONTENT_TYPE);

		    if (CommonMethods.isNull(responseType)) {
			responseType = TEXT_RESPONSE_CONTENT;
		    }

		    switch (responseType) {
		    case JSON_RESPONSE_CONTENT:
			customResponse = parseJsonResponse(respData, powerOperation.getResponseBodyTemplate());

			break;

		    case XML_RESPONSE_CONTENT:

			customResponse = parseXmlResponse(respData, powerOperation.getResponseBodyTemplate());

			break;
		    case TEXT_RESPONSE_CONTENT:
			Pattern pattern = Pattern.compile(powerOperation.getResponseBodyTemplate());
			Matcher matcher = pattern.matcher(respData);
			if (matcher.find()) {
			    customResponse = matcher.group(1);
			}
			break;

		    default:
			break;
		    }

		    LOGGER.info("Power switch expected response: {}", powerOperation.getResponseBodyTemplate());

		} else {
		    LOGGER.info("Power response body template is not available.");
		}
	    } else {
		LOGGER.info("Failed to apply power operation on device {} ,status {}", device.getHostMacAddress(),
			response.getStatus());
	    }
	} else {
	    LOGGER.error("Power operation response is null.");
	}

	return customResponse;
    }

    /**
     * Gets rest easy client instance
     * 
     * @return ResteasyClient
     */
    private ResteasyClient getClient() {
	ResteasyClient client = new ResteasyClientBuilder().build();
	return client;
    }

    private String getValueFromExtraProperties(String extraProps, String propertyName) {
	String value = null;

	if (CommonMethods.isNotNull(extraProps)) {

	    String[] propKeyValues = extraProps.split(AutomaticsConstants.COMMA);
	    for (String propKeyValue : propKeyValues) {
		if (propKeyValue.contains(propertyName)) {
		    String[] keyValue = propKeyValue.split(AutomaticsConstants.DELIMITER_EQUALS);
		    if (keyValue.length > 1) {
			value = keyValue[1];
		    }
		    LOGGER.info("Key={}, Value={}", keyValue[0], value);
		    break;
		}
	    }
	}

	return value;
    }

    private MultivaluedMap<String, Object> getRequestHeader(String extraProps) {

	MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
	int startIndex = extraProps.indexOf(KEY_REQUEST_HEADER);
	int offset = (KEY_REQUEST_HEADER + AutomaticsConstants.DELIMITER_EQUALS + "{").length();

	if (startIndex > -1) {
	    extraProps = extraProps.substring(startIndex);

	    if (extraProps.length() > offset) {
		String commaSepKeyValues = extraProps.substring(offset, extraProps.indexOf("}"));
		String[] keyValues = commaSepKeyValues.split(AutomaticsConstants.COMMA);

		List<Object> valueList = null;
		for (String keyValue : keyValues) {
		    String[] keyValueArray = keyValue.split(AutomaticsConstants.DELIMITER_EQUALS);

		    if (keyValueArray.length > 1) {
			valueList = new ArrayList<Object>();
			valueList.add(keyValueArray[1]);
			headers.put(keyValueArray[0], valueList);
		    }
		}
	    }

	}

	return headers;
    }

    private String mapCustomPowerStatusValue(String customStatusValue) {

	PowerStatus powerStatus = PowerStatus.OFF;
	String powerOnValues = AutomaticsPropertyUtility.getProperty(AutomaticsConstants.PROP_POWER_ON_VALUES);

	List<String> onValues = CommonMethods.splitStringByDelimitor(powerOnValues, AutomaticsConstants.COMMA);
	onValues.add("true");
	onValues.add(PowerStatus.ON.name());

	for (String onValue : onValues) {
	    if (onValue.equalsIgnoreCase(customStatusValue)) {
		powerStatus = PowerStatus.ON;
		break;
	    }
	}

	LOGGER.info("Power status partner value: {} and it is mapped to power status: {}", customStatusValue,
		powerStatus.name());
	return powerStatus.name();

    }

    private boolean mapCustomPowerValue(String customValue, String powerOperation) {

	boolean isOperationSuccess = false;

	String propertyName = AutomaticsConstants.PROP_POWER_ON_VALUES;
	if ("OFF".equals(powerOperation)) {
	    propertyName = AutomaticsConstants.PROP_POWER_OFF_VALUES;
	} else if ("POWER_CYCLE".equals(powerOperation)) {
	    propertyName = AutomaticsConstants.PROP_POWER_CYCLE_VALUES;
	}
	String powerValues = AutomaticsPropertyUtility.getProperty(propertyName);

	List<String> onValues = CommonMethods.splitStringByDelimitor(powerValues, AutomaticsConstants.COMMA);
	onValues.add("true");
	onValues.add("success");

	for (String onValue : onValues) {
	    if (onValue.equalsIgnoreCase(customValue)) {
		isOperationSuccess = true;
		break;
	    }
	}

	LOGGER.info("Power operation value: {} and it is mapped to : {}", customValue, isOperationSuccess);
	return isOperationSuccess;

    }

}
