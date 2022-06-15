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
package com.automatics.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.automatics.utils.CommonMethods;

/**
 * Class to communicate with the server
 * 
 * @author surajmathew
 */
public class ServerCommunicator {

    private Logger LOGGER;
    private boolean isLogRequired = true;

    public ServerCommunicator() {
	isLogRequired = false;
    }

    public ServerCommunicator(Logger LOGGER) {
	this.LOGGER = LOGGER;
    }

    /**
     * Method to post the data to server
     * 
     * @param target
     * @param content
     * @param requestType
     * @param timeoutInMilliSeconds
     * @return
     */
    public ServerResponse postDataToServer(String target, String content, String requestType,
	    long timeoutInMilliSeconds, Map<String, String> headers) {

	int responseCode = -1;
	HttpURLConnection connection = null;
	DataOutputStream out = null;
	ServerResponse serverResponse = null;

	try {

	    URL url;
	    URI uri;

	    try {
		url = new URI(target).normalize().toURL();
		uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
			url.getQuery(), url.getRef());
		target = uri.toASCIIString();		

	    } catch (URISyntaxException e1) {
		LOGGER.error("postDataToServer->" + e1.getMessage());
	    }

	    if (isLogRequired) {
		LOGGER.debug("Sending " + requestType + " message to server - " + target);
	    }

	    connection = getNewServerConnection(target, requestType, timeoutInMilliSeconds);

	    if (headers != null && !headers.isEmpty()) {
		for (String key : headers.keySet()) {
		    connection.setRequestProperty(key, headers.get(key));
		}
	    }

	    InputStream inputStream = null;

	    long startTime = System.currentTimeMillis();
	    long endTime = -1;
	    long timeTaken = -1;

	    // Write out the bytes of the content string to the stream.
	    if (CommonMethods.isNotNull(requestType) && (!requestType.equalsIgnoreCase("GET"))) {
		if (CommonMethods.isNotNull(content)) {
		    out = new DataOutputStream(connection.getOutputStream());
		    out.writeBytes(content);
		    out.flush();
		    out.close();
		} else {
		    if (requestType.equalsIgnoreCase("POST")) {
			if (isLogRequired) {
			    LOGGER.debug("CONTENT IS NULL FOR THE POST MESSAGE...!!!");
			}

			serverResponse = new ServerResponse();
			serverResponse.setResponseCode(responseCode);
			serverResponse
				.setResponseStatus("POST DATA IS MANDATORY FOR QUERY WITH REQUEST TYPE - \"POST\"");
			serverResponse.setTimeTaken(timeTaken);

			return serverResponse;
		    }
		}
	    }

	    try {
		// Read response from the input stream.
		responseCode = connection.getResponseCode();

	    } catch (ConnectException e) {
		if (isLogRequired) {
		    LOGGER.debug("CONNECTION TO SERVER FAILED...!!!");
		    LOGGER.debug(e.getMessage());
		}

		serverResponse = new ServerResponse();
		serverResponse.setResponseCode(responseCode);
		serverResponse.setResponseStatus("CONNECTION TO SERVER FAILED...!!!");
		serverResponse.setTimeTaken(timeTaken);

		return serverResponse;
	    }

	    if (responseCode >= 200 && responseCode < 300) {
		if (CommonMethods.isNotNull(connection.getContentType())
			&& !connection.getContentType().equalsIgnoreCase("application/octet-stream")) {
		    inputStream = connection.getInputStream();
		}
	    } else {
		inputStream = connection.getErrorStream();
	    }
	    endTime = System.currentTimeMillis();
	    timeTaken = endTime - startTime;

	    serverResponse = new ServerResponse();
	    serverResponse.setResponseCode(responseCode);

	    if (CommonMethods.isNotNull(connection.getContentType())
		    && !connection.getContentType().equalsIgnoreCase("application/octet-stream")) {

		serverResponse.setResponseStatus(readResponseFromInputStream(inputStream));
	    } else {
		serverResponse.setResponseStatus("application/octet-stream");
	    }

	    serverResponse.setTimeTaken(timeTaken);

	} catch (IOException e) {
	    if (isLogRequired) {
		LOGGER.debug("io exception when sending message to server...!!!" + e.getMessage());
	    }
	    serverResponse = new ServerResponse();
	    serverResponse.setResponseCode(responseCode);
	    serverResponse.setResponseStatus(e.getMessage());

	} finally {
	    // close the output stream
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    if (isLogRequired) {
			LOGGER.debug("EXCEPTION WHILE CLOSING THE OUTPUT STREAM...!!!");
			LOGGER.debug(e.getMessage());
		    }
		}
	    }

	    // close the connection
	    if (connection != null) {
		connection.disconnect();
	    }
	}

	return serverResponse;
    }

    /**
     * Method to post the file data to server
     * 
     * @param target
     * @param content
     * @param requestType
     * @param timeoutInMilliSeconds
     * @return
     */
    public ServerResponse postFileToServer(String target, File content, String requestType, long timeoutInMilliSeconds,
	    Map<String, String> headers) {

	int responseCode = -1;
	int thisLine;

	HttpURLConnection connection = null;
	DataOutputStream out = null;
	ServerResponse serverResponse = null;

	try {

	    if (isLogRequired) {
		LOGGER.debug("Sending " + requestType + " file message to server - " + target);
	    }

	    connection = getNewServerConnection(target, requestType, timeoutInMilliSeconds);

	    if (headers != null && !headers.isEmpty()) {
		for (String key : headers.keySet()) {
		    connection.setRequestProperty(key, headers.get(key));
		}
	    }

	    InputStream inputStream = null;
	    FileInputStream fin = null;

	    long startTime = System.currentTimeMillis();
	    long endTime = -1;
	    long timeTaken = -1;

	    // Write out the bytes of the content string to the stream.
	    if (CommonMethods.isNotNull(requestType) && requestType.equalsIgnoreCase("POST")) {
		if (content != null) {
		    out = new DataOutputStream(connection.getOutputStream());

		    fin = new FileInputStream(content);

		    while ((thisLine = fin.read()) != -1) {
			out.writeByte(thisLine);
			out.flush();
		    }

		    out.close();

		    if (isLogRequired) {
			LOGGER.info("File data send...");
		    }
		} else {
		    if (isLogRequired) {
			LOGGER.debug("CONTENT IS NULL FOR THE POST MESSAGE...!!!");
		    }

		    serverResponse = new ServerResponse();
		    serverResponse.setResponseCode(responseCode);
		    serverResponse.setResponseStatus("POST DATA IS MANDATORY FOR QUERY WITH REQUEST TYPE - \"POST\"");
		    serverResponse.setTimeTaken(timeTaken);

		    return serverResponse;
		}
	    }

	    try {
		// Read response from the input stream.
		responseCode = connection.getResponseCode();

	    } catch (ConnectException e) {
		if (isLogRequired) {
		    LOGGER.debug("CONNECTION TO SERVER FAILED...!!!");
		    LOGGER.debug(e.getMessage());
		}

		serverResponse = new ServerResponse();
		serverResponse.setResponseCode(responseCode);
		serverResponse.setResponseStatus("CONNECTION TO SERVER FAILED...!!!");
		serverResponse.setTimeTaken(timeTaken);

		return serverResponse;
	    }

	    if (responseCode >= 200 && responseCode < 300) {
		if (CommonMethods.isNotNull(connection.getContentType())
			&& !connection.getContentType().equalsIgnoreCase("application/octet-stream")) {
		    inputStream = connection.getInputStream();
		}
	    } else {
		inputStream = connection.getErrorStream();
	    }

	    endTime = System.currentTimeMillis();
	    timeTaken = endTime - startTime;

	    serverResponse = new ServerResponse();
	    serverResponse.setResponseCode(responseCode);

	    if (CommonMethods.isNotNull(connection.getContentType())
		    && !connection.getContentType().equalsIgnoreCase("application/octet-stream")) {

		serverResponse.setResponseStatus(readResponseFromInputStream(inputStream));
	    } else {
		serverResponse.setResponseStatus("application/octet-stream");
	    }

	    serverResponse.setTimeTaken(timeTaken);

	} catch (IOException e) {
	    if (isLogRequired) {
		LOGGER.debug("io exception when sending message to server...!!!" + e.getMessage());
	    }
	    serverResponse = new ServerResponse();
	    serverResponse.setResponseCode(responseCode);
	    serverResponse.setResponseStatus(e.getMessage());
	} finally {
	    // close the output stream
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    if (isLogRequired) {
			LOGGER.debug("EXCEPTION WHILE CLOSING THE OUTPUT STREAM...!!!");
			LOGGER.debug(e.getMessage());
		    }
		}
	    }

	    // close the connection
	    if (connection != null) {
		connection.disconnect();
	    }
	}

	return serverResponse;
    }

    /**
     * read the response string from a given input stream.
     * 
     * @param responseCode
     * @param inputStream
     * @param length
     * @return
     * @throws IOException
     */
    private String readResponseFromInputStream(InputStream inputStream) throws IOException {

	String response = "";

	try {
	    if (inputStream != null) {

		int thisLine;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
		    while ((thisLine = inputStream.read()) != -1) {
			bos.write(thisLine);
		    }

		    bos.flush();

		    byte[] buffer = bos.toByteArray();
		    Charset iso88591charset = Charset.forName("UTF-8");

		    response = new String(buffer, iso88591charset);

		} finally {
		    if (bos != null) {
			bos.close();
		    }
		    inputStream.close();
		}
	    }
	} catch (Exception e) {
	    if (isLogRequired) {
		LOGGER.debug("readResponseFromInputStream():", e);
	    }
	}

	if (isLogRequired) {
	    LOGGER.debug("Obtained the following response from server: " + response);
	}

	return response;
    }

    /**
     * Method to obtain a new server connection
     * 
     * @param target
     * @param requestType
     * @param timeoutInMilliSeconds
     * @return
     * @throws IOException
     */
    private HttpURLConnection getNewServerConnection(String target, String requestType, long timeoutInMilliSeconds)
	    throws IOException {

	final URL url = new URL(target);
	final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// Set connection parameters.
	conn.setDoInput(true);
	conn.setDoOutput(true);
	conn.setUseCaches(false);
	conn.setConnectTimeout(15 * 1000);
	conn.setRequestMethod(requestType);
	conn.setReadTimeout((int) timeoutInMilliSeconds);

	Map<String, String> headers = new HashMap<String, String>();
	headers.put("Content-Type", "application/xml");
	/*
	 * if(responseType != null){ headers.put("Accept", responseType.getContentType()); }
	 */
	headers.put("Connection", "Close");

	Set<String> keys = headers.keySet();
	for (String key : keys) {
	    conn.setRequestProperty(key, headers.get(key));
	}

	return conn;
    }
}
