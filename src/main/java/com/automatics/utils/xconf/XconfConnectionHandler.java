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

package com.automatics.utils.xconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.XconfConstants;
import com.automatics.device.Device;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.restclient.RestClient;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;
import com.automatics.restclient.RestEasyClientImpl;
import com.automatics.restclient.RestRequest;
import com.automatics.restclient.RestResponse;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;

/**
 * Establish connection with XCONF server and sends the configuration details to the server. This configuration details
 * will send back to the dut once it is connected.
 * 
 * @author Selvaraj Mariyappan
 */
public class XconfConnectionHandler {

    /** The SLF4j Logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(XconfConnectionHandler.class);

    private DeviceConnectionProvider connectionProvider = null;

    private static final String LOGGER_PREFIX = "[XCONF] : ";

    /**
     * Single instance of xconf connection handler.
     */
    private static XconfConnectionHandler xconfConnHandler = null;

    /**
     * Get the XconfConnectionHandler instance.
     * 
     * @return reference of XconfConnectionHandler instance
     */
    public static XconfConnectionHandler get() {
	if (null == xconfConnHandler) {
	    xconfConnHandler = new XconfConnectionHandler();
	}
	return xconfConnHandler;
    }

    private XconfConnectionHandler() {
	connectionProvider = BeanUtils.getDeviceConnetionProvider();
    }

    /**
     * Send the firmware configuration details to XCONF server.
     * 
     * @param configuration
     *            The firmware configuration details.
     * 
     *
     */
    public void sendMessage(FirmwareConfigurations configuration) {

	int retry = 2;
	RestResponse response = null;
	String responseAsString = null;
	String xconfServerUrl = getXconfServerUrl();

	try {
	    LOGGER.info(LOGGER_PREFIX + "SERVER URL = {}", xconfServerUrl);

	    String jsonTobeSent = configuration.toJson().toString();
	    LOGGER.info(LOGGER_PREFIX + "Request : {}", jsonTobeSent);

	    Map<String, String> headers = new HashMap<String, String>();
	    RestClient restClient = new RestEasyClientImpl();
	    RestRequest request = new RestRequest(xconfServerUrl, HttpRequestMethod.PUT, headers);
	    request.setContent(jsonTobeSent);
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);

	    while (retry >= 0) {

		response = restClient.executeAndGetResponse(request);
		responseAsString = response.getResponseBody();
		int statusCode = response.getResponseCode();

		LOGGER.info(LOGGER_PREFIX + "Response : {}", responseAsString);
		LOGGER.info(LOGGER_PREFIX + "Status Code: {}", statusCode);

		if (HttpStatus.SC_OK != statusCode && retry == 0) {
		    throw new FailedTransitionException(GeneralError.FAILED_CONFIGURATION,
			    "Unable to configure the firmware configuration in xconf server. HTTP Status code = "
				    + statusCode);
		} else if (HttpStatus.SC_BAD_GATEWAY == statusCode) {
		    LOGGER.info(LOGGER_PREFIX + "Request was unsuccessful.Retrying after 5 seconds");
		    AutomaticsUtils.sleep(AutomaticsConstants.FIVE_SECONDS);
		} else if (HttpStatus.SC_OK == statusCode) {
		    break;
		}
		retry--;
	    }

	} catch (Exception ex) {
	    LOGGER.info("Excpetion occured while sending request PUT request " + ex);
	    LOGGER.info("Retrying the request via curl command");
	    try {

		StringBuilder url = new StringBuilder();

		url.append("curl -H 'Content-Type: application/json' -X PUT '").append(xconfServerUrl).append("' -d '")
			.append(configuration.toJson().toString()).append("'");
		String urlToSend = url.toString();
		LOGGER.info(LOGGER_PREFIX + " Command to Send : {}", urlToSend);
		List<String> commands = new ArrayList<String>();
		commands.add(urlToSend);

		Device dummyDevice = new Device();
		responseAsString = connectionProvider.execute(dummyDevice, ExecuteCommandType.XCONF_CONFIG_UPDATE,
			commands);
		LOGGER.info(LOGGER_PREFIX + "Response : {}", responseAsString);

	    } catch (Exception eX) {
		throw new FailedTransitionException(GeneralError.FAILED_CONFIGURATION,
			"Unable to configure the firmware configuration in xconf server. ", eX);
	    }
	}
    }

    /**
     * Get the XCONF server software update URL from property file.
     * 
     * @return XCONF server software update URL
     */
    public String getXconfServerUrl() {
	String xconfServerUrl = AutomaticsTapApi.getSTBPropsValue(XconfConstants.PROP_KEY_XCONF_SIMULATOR_SERVER_URL);

	LOGGER.info(" XCONF server software update url  :  " + xconfServerUrl);

	if (null == xconfServerUrl || xconfServerUrl.isEmpty()) {
	    throw new FailedTransitionException(GeneralError.PROVIDED_RESOURCE_NOT_FOUND,
		    "XCONF SERVER software update url should not be null or empty");
	}

	return xconfServerUrl;
    }

}
