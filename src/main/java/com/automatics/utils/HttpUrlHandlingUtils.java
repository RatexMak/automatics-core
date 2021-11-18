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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;

public class HttpUrlHandlingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUrlHandlingUtils.class);

    /**
     * Privatised the constructor as it is a utility class.
     */
    private HttpUrlHandlingUtils() {
    }

    /**
     * Executes a get request to the provided URL.
     *
     * @param url
     *            Target URL.
     * @param parameterMap
     *            Map holding the parameter name and value.
     *
     * @return The response output.
     */
    public static String executeGet(String url, Map<String, String> parameterMap) {
	String responseString = "";
	String encodedParameter = createParameterString(parameterMap);
	String resolvedCompleteUrl = url + "?" + encodedParameter;
	LOGGER.info("Resolved URL : " + resolvedCompleteUrl);
	try {

	    URLConnection connection = new URL(resolvedCompleteUrl).openConnection();
	    InputStream is = connection.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer response = new StringBuffer();

	    while ((line = rd.readLine()) != null) {
		response.append(line);
		response.append('\r');
	    }

	    rd.close();
	    responseString = response.toString();

	} catch (UnsupportedEncodingException exc) {

	    LOGGER.error("Unsupported URL request : " + resolvedCompleteUrl, exc);
	    throw new TransitionException("Unsupported URL request........ : " + resolvedCompleteUrl, exc);
	} catch (IOException ioe) {
	    LOGGER.error("Failed to execute the URL request.", ioe);
	    throw new TransitionException("Failed to execute the URL request..... :" + resolvedCompleteUrl, ioe);
	}

	return responseString;
    }

    /**
     * Executes a POST request to the provided URL.
     *
     * @param url
     *            Target URL
     * @param urlParameters
     *            URL parameters.
     *
     * @return The response output.
     */
    public static String executePost(String url, String urlParameters) {
	String responseString = null;
	HttpURLConnection httpConn = null;

	try {
	    URL urlObj = new URL(url);
	    // Open connection
	    httpConn = (HttpURLConnection) urlObj.openConnection();
	    httpConn.setRequestMethod("POST");
	    httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	    httpConn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
	    httpConn.setRequestProperty("Content-Language", "en-US");

	    httpConn.setUseCaches(false);
	    httpConn.setDoInput(true);
	    httpConn.setDoOutput(true);

	    DataOutputStream dataOutStream = new DataOutputStream(httpConn.getOutputStream());
	    dataOutStream.writeBytes(urlParameters);
	    dataOutStream.flush();
	    dataOutStream.close();

	    InputStream inputStream = httpConn.getInputStream();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    String readLine;
	    StringBuffer data = new StringBuffer();

	    while (null != (readLine = reader.readLine())) {
		data.append(readLine);
		data.append('\r');
	    }

	    reader.close();

	    responseString = data.toString();

	} catch (Exception exc) {

	    LOGGER.error(String.format("Connection failure verify URL (%s) and parameter (%s).", url, urlParameters),
		    exc);

	    throw new TransitionException(String.format("Connection failure verify URL (%s) and parameter (%s).", url,
		    urlParameters));

	} finally {

	    if (null != httpConn) {
		httpConn.disconnect();
	    }
	}

	return responseString;
    }

    /**
     * Executes the URL get request and parse the XML response.
     *
     * @param url
     *            Target URL.
     * @param parameterMap
     *            Map holding the parameter name and value.
     *
     * @return Parsed XML response output.
     */
    public static Document executeGetAndParseXmlResponse(String url, Map<String, String> parameterMap) {

	Document doc = null;
	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	Process commandProcess = null;

	String targetUrl = null;
	String xmlResponse = null;

	boolean shouldTryWithCurl = false;

	try {
	    targetUrl = url + "?" + createParameterString(parameterMap);

	    LOGGER.info("TARGET URL  = " + targetUrl);

	    try {
		// Retry using java command
		LOGGER.info("Fetching using HTTP CLient mechanism");
		CommonMethods.disableSSL();
		serverCommunicator = new ServerCommunicator(LOGGER);
		serverResponse = serverCommunicator.postDataToServer(targetUrl, null, "GET", 15000, null);
		LOGGER.debug("Response from executing HTTP CLient - " + serverResponse.toString());
		if (serverResponse == null || serverResponse.getResponseCode() != 200) {
		    commandProcess = new ProcessBuilder("curl", "-G", "-X", "GET", url, "-d",
			    createParameterString(parameterMap)).start();
		    xmlResponse = getStringFromInputStream(commandProcess.getInputStream());
		    LOGGER.debug("Response obtained from CI server curl execution with process builder - "
			    + xmlResponse);
		}
		if (CommonMethods.isNull(xmlResponse)) {
		    shouldTryWithCurl = true;
		}
	    } catch (Exception e) {
		LOGGER.error("Exception while fetch and parse URL using HTTP CLient/Process builder - "
			+ e.getMessage());
		shouldTryWithCurl = true;
	    }

	    if (shouldTryWithCurl) {
		LOGGER.debug("Response from executing curl command - " + xmlResponse);
	    }
	    if (serverResponse != null && serverResponse.getResponseCode() == 200) {
		doc = pasrseXmlAndGetDocument(null, serverResponse);
	    } else if (CommonMethods.isNotNull(xmlResponse)) {
		doc = pasrseXmlAndGetDocument(xmlResponse, null);
	    } else {
		LOGGER.error("Failed to get data from the url - " + targetUrl);
	    }
	} catch (ParserConfigurationException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new TransitionException("Failed to parse the URL response..... : " + url, e);
	} catch (MalformedURLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new TransitionException("Malformed URL .......... : " + url, e);
	} catch (SAXException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new TransitionException("SAX parser failure on URL response ......... : " + url, e);
	} catch (IOException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new TransitionException("I/O failure on acessing URL........... : " + url, e);
	} finally {
	    if (null != commandProcess) {
		commandProcess.destroy();
	    }
	}
	return doc;
    }

    /**
     * 
     * Method which parses the respnse and returns the documnet object
     * 
     * @param xmlResponse
     *            XML Response as string
     * @param serverResponse
     *            Response as ServerResponse object
     * @return Document object
     * @throws ParserConfigurationException
     * @throws UnsupportedEncodingException
     * @throws SAXException
     * @throws IOException
     */
    private static Document pasrseXmlAndGetDocument(String xmlResponse, ServerResponse serverResponse)
	    throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = null;
	if (CommonMethods.isNotNull(xmlResponse)) {
	    doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xmlResponse.getBytes("utf-8"))));
	} else {
	    doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(serverResponse.getResponseStatus().getBytes(
		    "utf-8"))));
	}
	return doc;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream inputStream) {

	BufferedReader bufferedReader = null;
	StringBuilder streamData = new StringBuilder();

	String data;
	try {

	    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    while (null != (data = bufferedReader.readLine())) {
		streamData.append(data);
	    }

	} catch (IOException e) {
	    LOGGER.error("Exception occurred", e);
	} finally {
	    if (null != bufferedReader) {
		try {
		    bufferedReader.close();
		} catch (IOException e) {
		    LOGGER.error("Exception occurred", e);
		}
	    }
	}

	return streamData.toString();

    }

    /**
     * Creates the parameter string from the parameter Map.
     *
     * @param parameterMap
     *            Map holding the parameter name and value.
     *
     * @return The parameter string by joining parameter name and value by ampersand(&).
     */
    public static String createParameterString(Map<String, String> parameterMap) {
	boolean isFirst = true;

	StringBuilder paramBuilder = new StringBuilder();

	if (parameterMap != null) {

	    for (Map.Entry<String, String> entry : parameterMap.entrySet()) {

		if (entry.getKey() != null) {

		    if (!isFirst) {
			paramBuilder.append(AutomaticsConstants.DELIMITER_AMPERSAND);
		    } else {
			isFirst = false;
		    }

		    paramBuilder.append(entry.getKey()).append(AutomaticsConstants.DELIMITER_EQUALS)
			    .append(entry.getValue());
		}
	    }
	}

	LOGGER.debug("Parameters : " + paramBuilder.toString());
	return paramBuilder.toString();
    }

    /**
     * Executes a POST request to the provided URL.
     *
     * @param targetURL
     *            Target URL
     * @param urlParameters
     *            URL parameters.
     *
     * @return The response output.
     */
    public static String executePostWithJsonParam(String url, JSONObject jsonParams) {
	String responseString = null;
	HttpURLConnection httpConn = null;

	try {

	    URL urlObj = new URL(url);
	    // Open connection
	    httpConn = (HttpURLConnection) urlObj.openConnection();
	    httpConn.setRequestMethod("POST");
	    httpConn.setRequestProperty("Content-Type", "application/json");

	    httpConn.setRequestProperty("Content-Length",
		    "" + Integer.toString(jsonParams.toString().getBytes().length));
	    httpConn.setRequestProperty("Content-Language", "en-US");

	    httpConn.setUseCaches(false);
	    httpConn.setDoInput(true);
	    httpConn.setDoOutput(true);

	    // Send request
	    DataOutputStream outputStream = new DataOutputStream(httpConn.getOutputStream());
	    outputStream.writeBytes(jsonParams.toString());
	    outputStream.flush();
	    outputStream.close();

	    // Get Response
	    InputStream inputStream = httpConn.getInputStream();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    String dataRead;
	    StringBuffer data = new StringBuffer();

	    while (null != (dataRead = reader.readLine())) {
		data.append(dataRead);
		data.append('\r');
	    }

	    reader.close();

	    responseString = data.toString();

	} catch (Exception exc) {

	    LOGGER.error(
		    String.format("Connection failure verify url (%s) and json parameter (%s).", url,
			    jsonParams.toString()), exc);

	    throw new TransitionException(String.format("Connection failure verify url (%s) and json parameter (%s).",
		    url, jsonParams.toString()));

	} finally {

	    if (httpConn != null) {
		httpConn.disconnect();
	    }
	}

	return responseString;
    }

}
