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

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.security.cert.X509Certificate;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.utils.CommonMethods;

/**
 * 
 * RestEasy implementation for Rest calls
 * 
 * @author Arun V S
 *
 */
public class RestEasyClientImpl implements RestClient {

    /**
     * Logger instance for {@link RestEasyClientImpl}
     */	
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEasyClientImpl.class);

    boolean trustAllSites = false;

    public RestEasyClientImpl() {
	trustAllSites = false;
    }

    public RestEasyClientImpl(boolean trustAllSites) {
	this.trustAllSites = trustAllSites;
    }

    /**
     * 
     * {@inheritDoc}
     * 
     */
    @Override
    public RestResponse executeAndGetResponse(RestRequest request) throws RestClientException {

	RestResponse ecatsResponse = new RestResponse();
	Response response = null;
	MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
	MultivaluedMap<String, Object> parameters = null;

	try {

	    /**
	     * 
	     * Creating reastEasy client and web target with TLS enabled
	     * 
	     */
	    LOGGER.info(RestClientConstants.LOGGER_PREFIX + "Processing request");
	    ApacheHttpClient4Engine httpEngine = null;
	    if (trustAllSites) {
		httpEngine = new ApacheHttpClient4Engine(createAllTrustingClient()) {
		    @Override
		    protected void loadHttpMethod(ClientInvocation request, HttpRequestBase httpMethod)
			    throws Exception {
			super.loadHttpMethod(request, httpMethod);
			httpMethod.setParams(new BasicHttpParams());
		    }
		};
	    } else {
		httpEngine = new ApacheHttpClient4Engine(CommonMethods.getTlsEnabledHttpClient()) {

		    @Override
		    protected void loadHttpMethod(ClientInvocation request, HttpRequestBase httpMethod)
			    throws Exception {
			super.loadHttpMethod(request, httpMethod);
			httpMethod.setParams(new BasicHttpParams());
		    }
		};
	    }

	    ResteasyClient client = new ResteasyClientBuilder().httpEngine(httpEngine)
		    .establishConnectionTimeout(request.getTimeoutInMilliSeconds(), TimeUnit.MILLISECONDS)
		    .socketTimeout(request.getTimeoutInMilliSeconds(), TimeUnit.MILLISECONDS).build();
	    LOGGER.debug(RestClientConstants.LOGGER_PREFIX + "Created client");
	    ResteasyWebTarget target = null;
	    if (request.getParams() != null) {
		parameters = new MultivaluedHashMap<>();
		for (String key : request.getParams().keySet()) {
		    parameters.add(key, request.getParams().get(key));
		}

		target = client.target(request.getTargetUrl()).queryParams(parameters);
		LOGGER.info("Creating target = " + target.getUri());
	    } else {
		target = client.target(request.getTargetUrl());
	    }
	    // ResteasyWebTarget target = client.target(request.getTargetUrl());
	    LOGGER.debug(RestClientConstants.LOGGER_PREFIX + "Processing Target");

	    /**
	     * Creating header map
	     * 
	     */
	    if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
		for (String key : request.getHeaders().keySet()) {
		    headers.add(key, request.getHeaders().get(key));
		}
	    }

	    /**
	     * Getting response from web target
	     * 
	     */
	    response = getResponseFromServer(target, request, headers);

	    LOGGER.info(RestClientConstants.LOGGER_PREFIX + "Response from  - " + request.getTargetUrl()
		    + "  is - " + response.getStatus());

	    if (null != response) {
		ecatsResponse.setResponse(response);
		ecatsResponse.setResponseCode(response.getStatus());
		ecatsResponse.setResponseBody(response.readEntity(String.class));
	    }

	} catch (Exception e) {
	    LOGGER.error(RestClientConstants.LOGGER_PREFIX + "Unable to process Rest request - " + e.getMessage());
	    throw new RestClientException(e.getMessage());
	}

	return ecatsResponse;
    }

    /**
     * 
     * Getting response from server based on request type
     * 
     * @param target
     *            {@code ResteasyWebTarget}
     * @param request
     *            {@code RestRequest}
     * @param headers
     *            Headers
     * @return {@code Response}
     */
    private Response getResponseFromServer(ResteasyWebTarget target, RestRequest request,
	    MultivaluedMap<String, Object> headers) {

	Response response = null;

	/**
	 * Getting response from web target
	 * 
	 */
	switch (request.getRequestType()) {

	case GET:
	    response = target.request().headers(headers).get();
	    break;
	case DELETE:
	    response = target.request().headers(headers).delete();
	    break;
	case HEAD:
	    response = target.request().headers(headers).head();
	    break;
	case OPTIONS:
	    response = target.request().headers(headers).options();
	    break;
	case PATCH:
	    response = target.request().headers(headers).method("PATCH",createEntity(request),Response.class);
	    break;
	case POST:
	    response = target.request().headers(headers).post(createEntity(request));
	    break;
	case PUT:
	    response = target.request().headers(headers).put(createEntity(request));
	    break;
	case TRACE:
	    response = target.request().headers(headers).trace();
	    break;
	}

	return response;
    }

    /**
     * 
     * Create all trusting http client
     * 
     * @return
     * @throws GeneralSecurityException
     */
    @SuppressWarnings("deprecation")
    private static HttpClient createAllTrustingClient() throws GeneralSecurityException {

	SSLContextBuilder builder = new SSLContextBuilder();
	builder.loadTrustMaterial(null, new TrustStrategy() {
	    @SuppressWarnings("unused")
	    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		return true;
	    }

	    @Override
	    public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
		    throws CertificateException {
		// TODO Auto-generated method stub
		return true;
	    }
	});

	SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
		SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSF).build();

	return httpClient;
    }

    private Entity<?> createEntity(RestRequest request) {
	if (request.getMediaType() == null && CommonMethods.isNull(request.getContent())) {
	    return Entity.entity(null, MediaType.WILDCARD_TYPE);
	} /*
	   * else if (request.getMediaType().equals(MediaType.MULTIPART_FORM_DATA_TYPE)) {
	   * GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
	   * request.getOutput()) { };
	   * 
	   * return Entity.entity(entity, request.getMediaType()); }
	   */else {
	    return Entity.entity(request.getContent(), request.getMediaType());
	}
    }

}
