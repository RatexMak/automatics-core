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
package com.automatics.connection.handler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.exceptions.SplunkConnectionFailedException;
import com.automatics.providers.connection.auth.ICrypto;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.splunk.HttpException;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * Class to Connect to Splunk , post query and retrieve the results
 * 
 * @author surajmathew
 *
 */
public class SplunkConnectionHandler {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SplunkConnectionHandler.class);

    private static Service service = null;
    private Job job = null;
    private final static int DEFAULT_PORT = 8089;

    private final static String SPLUNK_HOST = "splunk.host";
    private final static String SPLUNK_PORT = "splunk.port";
    private final static String SPLUNK_USER_NAME = "splunk.user";
    private final static String SPLUNK_PASSWORD = "splunk.password";
    private final static String SPLUNK_SCHEMA = "splunk.schema";
    private final static long SESSION_TIMEOUT_THRESHOLD = 2700000;
    private static long loginTime = (new Date()).getTime();

    // static SplunkConnectionHandler splunkConnectionHandler;
    /**
     * Masking the default constructor
     */
    private SplunkConnectionHandler() {
	// Masking constructor
    }

    /**
     * The static method to instantiate the {@link SplunkConnectionHandler} with the host port and login credentials
     * available at stb.probs
     * 
     */
    public static SplunkConnectionHandler getInstance() throws SplunkConnectionFailedException {

	AutomaticsTapApi automaticsTapApi = AutomaticsTapApi.getInstance();
	ICrypto crypto = BeanUtils.getCredentialCrypto();

	return getInstance(automaticsTapApi.getSTBPropsValue(SPLUNK_HOST),
		Integer.parseInt(automaticsTapApi.getSTBPropsValue(SPLUNK_PORT)),
		automaticsTapApi.getSTBPropsValue(SPLUNK_USER_NAME),
		crypto.decrypt(automaticsTapApi.getSTBPropsValue(SPLUNK_PASSWORD)),
		automaticsTapApi.getSTBPropsValue(SPLUNK_SCHEMA));
    }

    /**
     * The static method to instantiate the {@link SplunkConnectionHandler} with th given host port and login
     * credentials
     * 
     * @param splunkHostAddress
     * @param port
     * @param userName
     * @param password
     * @return splunkConnectionHandler
     * @throws SplunkConnectionFailedException
     */
    public static SplunkConnectionHandler getInstance(String splunkHostAddress, int port, String userName,
	    String password) throws SplunkConnectionFailedException {

	AutomaticsTapApi automaticsTapApi = AutomaticsTapApi.getInstance();
	return getInstance(splunkHostAddress, port, userName, password,
		automaticsTapApi.getSTBPropsValue(SPLUNK_SCHEMA));
    }

    /**
     * The static method to instantiate the {@link SplunkConnectionHandler} with th given host port and login
     * credentials
     * 
     * @param splunkHostAddress
     * @param port
     * @param userName
     * @param password
     * @return splunkConnectionHandler
     * @throws SplunkConnectionFailedException
     */
    public static SplunkConnectionHandler getInstance(String splunkHostAddress, int port, String userName,
	    String password, String schema) throws SplunkConnectionFailedException {

	SplunkConnectionHandler splunkConnectionHandler = new SplunkConnectionHandler();

	if (port <= 0) {
	    port = DEFAULT_PORT;
	}

	ServiceArgs serviceArgs = null;
	serviceArgs = new ServiceArgs();
	serviceArgs.setUsername(userName);
	serviceArgs.setPassword(password);
	serviceArgs.setScheme(schema);
	serviceArgs.setHost(splunkHostAddress);
	serviceArgs.setPort(port);
	// Login to splunk
	splunkConnectionHandler.login(serviceArgs);

	return splunkConnectionHandler;
    }

    /**
     * Login to splunk with the credentials provided.
     * 
     * @param serviceArgs
     * @throws SplunkConnectionFailedException
     */
    private void login(final ServiceArgs serviceArgs) throws SplunkConnectionFailedException {
	long currentTime = new Date().getTime();
	LOGGER.info("CHecking if service is already logged in :" + service);
	if (service != null) {
	    // LOGGER.info("Service Auth token = " + service.getToken());
	    LOGGER.info("Last login :" + loginTime);
	}
	LOGGER.info("Time since last login (TSH :2700000): = " + (currentTime - loginTime));
	if (service == null || CommonMethods.isNull(service.getToken())
		|| (currentTime - loginTime >= SESSION_TIMEOUT_THRESHOLD)) {

	    /*
	     * serviceArgs = new ServiceArgs(); serviceArgs.setUsername(user); serviceArgs.setPassword(password);
	     * serviceArgs.setScheme(schema); serviceArgs.setHost(host); serviceArgs.setPort(port);
	     */
	    if (currentTime - loginTime >= SESSION_TIMEOUT_THRESHOLD) {
		LOGGER.info("Existing session is about to expire.Initiating re-login");
		splunklogout();
	    }
	    StringBuffer errorMessage = splunkLogin(serviceArgs);
	    if (service == null) {
		if (!waitForSuccessFulLogin()) {
		    LOGGER.info("Retrying login to splunk after 1 minute");
		    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
		    errorMessage = splunkLogin(serviceArgs);
		    waitForSuccessFulLogin();
		}
	    }

	    // waiting for 5 seconds so that the errormessage string could be appended after receiving thread interrupt
	    AutomaticsUtils.sleep(1 * 5 * 1000);
	    if (errorMessage.length() > 0 || service == null) {
		throw new SplunkConnectionFailedException(errorMessage.toString());
	    }
	}
    }

    /**
     * Waits for login to happen
     * 
     * @return boolean whether success or failure
     */
    private boolean waitForSuccessFulLogin() {
	int retry = 5;
	boolean isLoginSucess = false;
	LOGGER.info("Connection to splunk failed after wait tme of 3 minutes");
	while (retry > 0) {
	    if (service == null) {
		LOGGER.info("Waiting for 30sec more");
		AutomaticsUtils.sleep(1 * 30 * 1000);
		retry--;
	    } else {
		isLoginSucess = true;
		loginTime = new Date().getTime();
		break;
	    }

	}
	return isLoginSucess;
    }

    /**
     * This method perform the login to splunk
     * 
     * @param serviceArgs
     *            Login arguments
     * @return Retruns any error during login
     * @throws SplunkConnectionFailedException
     */
    private StringBuffer splunkLogin(final ServiceArgs serviceArgs) throws SplunkConnectionFailedException {
	final StringBuffer errorMessage = new StringBuffer();
	Thread timerThread = new Thread() {
	    public void run() {
		try {
		    sleep(5);
		    LOGGER.info("Connecting to splunk service");
		    service = Service.connect(serviceArgs);
		    LOGGER.info("Connected to service = " + service);
		    loginTime = new Date().getTime();
		} catch (InterruptedException e) {
		    LOGGER.error("Splunk connection - Join operation interrupted.");
		    errorMessage.append("Failed to obtain splunk connection");
		}

	    }
	};

	try {
	    // Timer thread that for controlling the IC (image Compare) response time.
	    timerThread.setDaemon(true);
	    timerThread.start();

	    LOGGER.debug("WAIT FOR MAXIMUM 3 minutes to get SPLUNK connection.....");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(3 * 60 * 1000);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (Exception exception) {
	    LOGGER.error("FAILED TO CONNECT TO SPLUNK......!!!", exception);
	    throw new SplunkConnectionFailedException(exception.getMessage());
	}
	return errorMessage;
    }

    public void splunklogout() {

	if (service != null) {
	    service.logout();
	    service = null;
	}

	LOGGER.info("Splunk logged out..");

    }

    /**
     * Log out from Splunk
     */
    public void logout() {

	/*
	 * if (service != null) { service.logout(); }
	 * 
	 * LOGGER.info("Splunk logged out..");
	 */

    }

    /**
     * Execute the given search query. The Search will be of type BLOCKING and so the results will be obtained only
     * after completing the job.
     * 
     * <br>
     * 
     * @param query
     * @param jobargs
     */
    public void executeQuery(String query) {

	JobArgs jobargs = new JobArgs();

	jobargs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
	jobargs.setSearchMode(JobArgs.SearchMode.NORMAL);
	jobargs.setMaximumTime(600);// setting maximum time after which job will automatically finalize, if not
	// finalized yet
	jobargs.setAutoCancel(600);
	executeQuery(query, jobargs);

    }

    /**
     * Execute the given search query with the job arguments provided. <br>
     * 
     * @param query
     * @param jobargs
     */
    public void executeQuery(String query, final JobArgs jobargs) {

	final StringBuffer searchQuery_normal = new StringBuffer("search ");

	searchQuery_normal.append(query);

	// commenting the below code as we are initializing jobargs in previous method
	/*
	 * if(jobargs == null) {
	 * 
	 * jobargs = new JobArgs();
	 * 
	 * jobargs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING); jobargs.setSearchMode(JobArgs.SearchMode.NORMAL);
	 * 
	 * }
	 */

	LOGGER.info("SPLUNK - search query - " + query);
	Thread timerThread = new Thread() {

	    public void run() {
		try {
		    sleep(5);
		    if (service != null) {
			job = service.getJobs().create(searchQuery_normal.toString(), jobargs);
		    } else {
			LOGGER.error("Splunk session could not be successfully created");
		    }
		} catch (InterruptedException e) {
		    LOGGER.error("Splunk query execution - Join operation interrupted.");
		}

	    }
	};

	try {
	    // Timer thread that for controlling the IC (image Compare) response time.
	    timerThread.setDaemon(true);
	    timerThread.start();

	    LOGGER.debug("WAIT FOR MAXIMUM 10 minutes to get SPLUNK RESPONSE.");

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 10 minutes maximum. If no response
	     * is obtained, continue the below lines of code. If this thread is not used, and if Image Comparison ran
	     * into loop, then it will block the execution until it gets any data.
	     */
	    timerThread.join(10 * 60 * 1000);

	    // Interrupt the thread.
	    timerThread.interrupt();

	} catch (HttpException exception) {
	    LOGGER.error("Failed to initiate the splunk search - " + exception.getMessage());
	} catch (Exception e) {
	    LOGGER.error("Failed to initiate the splunk search - " + e.getMessage());
	}
    }

    /**
     * Wait for the Job to be Done. Its a blocking function, if the search type = BLOCKING
     */
    private boolean waitForJobTobeDone() {

	LOGGER.info("SPLUNK - Waiting for Job to be Finalized / Done / Failed");

	while (!job.isFinalized() && !job.isDone() && !job.isFailed()) {
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		LOGGER.trace("Wait interrupted for Splunk Query");
	    } catch (Exception e) {
		LOGGER.trace("Wait interrupted for Splunk Query", e);
	    }
	}

	LOGGER.info("SPLUNK - Wait completed");

	return true;
    }

    /**
     * Get the Job description's like ID, event count, result count and duration
     * 
     * @return jobDescription
     */
    public String jobDescription() {

	StringBuffer jobDescription = new StringBuffer();

	if (job != null) {
	    // Get properties of the job
	    jobDescription.append("Search job ID: " + job.getSid());
	    jobDescription.append("Number of events: " + job.getEventCount());
	    jobDescription.append("Number of results: " + job.getResultCount());
	    jobDescription.append("Search duration: " + job.getRunDuration() + " seconds");
	} else {
	    LOGGER.error("SPLUNK jobDescription() - No Job(s) initiated");
	}

	return jobDescription.toString();
    }

    /**
     * Get the search results as collection of strings, which is taken from the '_raw' event
     * 
     * @return searchResultsList
     */
    public Collection<String> getSearchResults() {

	Collection<String> searchResultsList = null;
	HashMap<String, String> eventMap = null;

	InputStream resultsNormalSearch = null;
	ResultsReaderXml resultsReaderNormalSearch = null;

	if (job != null && waitForJobTobeDone()) {
	    try {
		// Get the search results and use the built-in XML parser to display them
		resultsNormalSearch = job.getResults();
		resultsReaderNormalSearch = new ResultsReaderXml(resultsNormalSearch);

		while ((eventMap = resultsReaderNormalSearch.getNextEvent()) != null) {

		    String result = eventMap.get("_raw");

		    if (CommonMethods.isNotNull(result)) {

			if (searchResultsList == null) {
			    searchResultsList = new ArrayList<String>();
			}

			searchResultsList.add(result);
		    }
		}
	    } catch (Exception exception) {
		LOGGER.error("SPLUNK getSearchResults() - Failed to get search results.", exception);
	    }
	} else {
	    LOGGER.error("SPLUNK getSearchResults() - No Job(s) initiated");
	}

	return searchResultsList;
    }
}