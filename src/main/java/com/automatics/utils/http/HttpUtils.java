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
package com.automatics.utils.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanUtils class for http/https connection using Apache Client library
 * 
 * @author Radhika
 *
 */
public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Perform GET request
     * 
     * @param uri
     *            Request uri
     * @param headerParams
     *            Header params or null
     * @param responseHandler
     *            Handler for parsing response.
     * @return Response object with data and response code
     */
    public static HttpServerResponse executeGet(String uri, Map<String, String> headerParams,
	    ResponseHandler<HttpServerResponse> responseHandler) {

	HttpServerResponse response = null;
	HttpGet httpGet = new HttpGet(uri);

	// Set request headers if any available
	if (null != headerParams) {
	    for (String key : headerParams.keySet()) {
		httpGet.setHeader(key, headerParams.get(key));
	    }
	}
	CloseableHttpClient httpClient = getHttpClient();

	// Execute request
	try {
	    response = httpClient.execute(httpGet, responseHandler);
	} catch (IOException e) {
	    LOGGER.error("Exception while reading response from", uri, e);
	}
	return response;
    }

    /**
     * Perform GET request
     * 
     * @param uri
     *            Request uri
     * @param headerParams
     *            Header params or null
     * @return Response object with data and response code
     */
    public static HttpServerResponse executeGet(String uri, Map<String, String> headerParams) {

	HttpServerResponse response = new HttpServerResponse();
	HttpGet httpGet = new HttpGet(uri);

	// Set request headers if any available
	if (null != headerParams) {
	    for (String key : headerParams.keySet()) {
		httpGet.setHeader(key, headerParams.get(key));
	    }
	}
	CloseableHttpClient httpClient = getHttpClient();

	// Execute request
	try {
	    CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
	    if (null != httpResponse) {
		response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
		response.setResponseData(EntityUtils.toString(httpResponse.getEntity()));
	    }
	} catch (IOException e) {
	    LOGGER.error("Exception while reading response from", uri, e);
	}
	return response;
    }

    /**
     * Perform POST request
     * 
     * @param uri
     *            Request uri
     * @param postData
     *            Post data or null
     * @param headerParams
     *            Header params or null
     * @return Response object with data and response code
     */
    public static HttpServerResponse executePost(String uri, Map<String, String> headerParams, HttpEntity postData) {

	HttpServerResponse response = new HttpServerResponse();
	HttpPost httpPost = new HttpPost(uri);

	// Set request headers if any available
	if (null != headerParams) {
	    for (String key : headerParams.keySet()) {
		httpPost.setHeader(key, headerParams.get(key));
	    }
	}
	// Set post data
	if (null != postData) {
	    httpPost.setEntity(postData);
	}
	CloseableHttpClient httpClient = getHttpClient();

	// Execute request
	try {
	    CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
	    if (null != httpResponse) {
		response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
		if (null != httpResponse.getEntity())
		    response.setResponseData(EntityUtils.toString(httpResponse.getEntity()));
	    }
	} catch (IOException e) {
	    LOGGER.error("Exception while reading response from", uri, e);
	}

	return response;
    }

    /**
     * Perform POST request
     * 
     * @param uri
     *            Request uri
     * @param headerParams
     *            Header params or null
     * @param postData
     *            Post data or null
     * @param responseHandler
     *            Handler for parsing response.
     * @return Response object with data and response code
     */
    public static HttpServerResponse executePost(String uri, Map<String, String> headerParams, HttpEntity postData,
	    ResponseHandler<HttpServerResponse> responseHandler) {

	HttpServerResponse response = null;
	HttpPost httpPost = new HttpPost(uri);

	// Set request headers if any available
	if (null != headerParams) {
	    for (String key : headerParams.keySet()) {
		httpPost.setHeader(key, headerParams.get(key));
	    }
	}
	// Set post data
	if (null != postData) {
	    httpPost.setEntity(postData);
	}
	CloseableHttpClient httpClient = getHttpClient();

	// Execute request
	try {
	    response = httpClient.execute(httpPost, responseHandler);
	} catch (IOException e) {
	    LOGGER.error("Exception while reading response from", uri, e);
	}

	return response;
    }

    /**
     * Perform PUT request
     * 
     * @param uri
     *            Request uri
     * @param postData
     *            Post data or null
     * @param headerParams
     *            Header params or null
     * @return Response object with data and response code
     */
    public static HttpServerResponse executePut(String uri, Map<String, String> headerParams, HttpEntity putData) {

	HttpServerResponse response = new HttpServerResponse();
	HttpPut httpPut = new HttpPut(uri);

	// Set request headers if any available
	if (null != headerParams) {
	    for (String key : headerParams.keySet()) {
		httpPut.setHeader(key, headerParams.get(key));
	    }
	}
	// Set put data
	if (null != putData) {
	    httpPut.setEntity(putData);
	}
	CloseableHttpClient httpClient = getHttpClient();

	// Execute request
	try {
	    CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
	    if (null != httpResponse) {
		response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
		response.setResponseData(EntityUtils.toString(httpResponse.getEntity()));
	    }
	} catch (IOException e) {
	    LOGGER.error("Exception while reading response from", uri, e);
	}

	return response;
    }

    /**
     * Returns HttpClient instance
     * 
     * @return HttpClient instance
     */
    private static CloseableHttpClient getHttpClient() {
	CloseableHttpClient httpClient = HttpClients.createDefault();
	return httpClient;
    }

}
