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
package com.automatics.restclient;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;

/**
 * Entity class for rest call request
 * 
 * @author Arun V S
 *
 */
public class RestRequest {

    /**
     * Targeted rest end point url
     * 
     */
    private String targetUrl;

    /**
     * Body for rest request
     * 
     */
    private String content;

    /**
     * 
     * Request Type {@code HttpRequestMethod}
     * 
     */
    private HttpRequestMethod requestType;

    /**
     * Request timeout in milliseconds
     * 
     */
    private long timeoutInMilliSeconds = AutomaticsConstants.TEN_SECONDS;

    /**
     * Request headers
     * 
     */
    private Map<String, String> headers;

    /**
     * Params
     */
    private Map<String, String> params;

    /**
     * Request Media Type {@code MediaType}
     * 
     */
    private MediaType mediaType;

    /**
     * 
     * Constructor for {@code RestRequest}
     * 
     * @param targetUrl
     *            End point URL
     * @param requestType
     *            Request Type
     * @param headers
     *            Request headers
     */
    public RestRequest(String targetUrl, HttpRequestMethod requestType, Map<String, String> headers) {
	this.targetUrl = targetUrl;
	this.requestType = requestType;
	this.headers = headers;
    }

    /**
     * 
     * Constructor for {@code RestRequest}
     * 
     * @param targetUrl
     *            End point URL
     * @param requestType
     *            Request Type
     * @param headers
     *            Request headers
     */
    public RestRequest(String targetUrl, HttpRequestMethod requestType, Map<String, String> headers,
	    Map<String, String> params) {
	this.targetUrl = targetUrl;
	this.requestType = requestType;
	this.headers = headers;
	this.params = params;
    }

    /**
     * Set targetUrl
     * 
     * @return targetUrl
     */
    public String getTargetUrl() {
	return targetUrl;
    }

    /**
     * Get targetUrl
     * 
     * @param target
     */
    public void setTargetUrl(String target) {
	this.targetUrl = target;
    }

    /**
     * Get content
     * 
     * @return content
     */
    public String getContent() {
	return content;
    }

    /**
     * Set content
     * 
     * @param content
     */
    public void setContent(String content) {
	this.content = content;
    }

    /**
     * Get requestType
     * 
     * @return requestType
     */
    public HttpRequestMethod getRequestType() {
	return requestType;
    }

    /**
     * Set requestType
     * 
     * @param requestType
     */
    public void setRequestType(HttpRequestMethod requestType) {
	this.requestType = requestType;
    }

    /**
     * Get timeoutInMilliSeconds
     * 
     * @return timeoutInMilliSeconds
     */
    public long getTimeoutInMilliSeconds() {
	return timeoutInMilliSeconds;
    }

    /**
     * 
     * Set timeoutInMilliSeconds
     * 
     * @param timeoutInMilliSeconds
     */
    public void setTimeoutInMilliSeconds(long timeoutInMilliSeconds) {
	this.timeoutInMilliSeconds = timeoutInMilliSeconds;
    }

    /**
     * Get headers
     * 
     * @return headers
     */
    public Map<String, String> getHeaders() {
	return headers;
    }

    /**
     * Set headers
     * 
     * @param headers
     */
    public void setHeaders(Map<String, String> headers) {
	this.headers = headers;
    }

    /**
     * 
     * Get mediaType
     * 
     * @return mediaType
     */
    public MediaType getMediaType() {
	return mediaType;
    }

    /**
     * set mediaType
     * 
     * @param mediaType
     */
    public void setMediaType(MediaType mediaType) {
	this.mediaType = mediaType;
    }

    public Map<String, String> getParams() {
	return params;
    }

    public void setParams(Map<String, String> params) {
	this.params = params;
    }

}
