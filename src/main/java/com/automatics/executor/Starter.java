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

package com.automatics.executor;

import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;
import com.automatics.providers.TestInitilizationProvider;
import com.automatics.rack.RackInitializer;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.BeanUtils;

/**
 * This is the entry point to Automatics core. The main method which starts the TestExecuter is included in this class
 *
 * @author smithabg
 */
public class Starter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

    /**
     * This is the main method of the class which runs the test case.
     *
     * @param args
     *            - String[]
     */
    public static void main(String[] args) {

	LOGGER.info("Starting the execution process for the given test cases.");

	BeanUtils.startContext();
	if (args != null && args.length > 0) {
	    LOGGER.info("****Command Line Arguement " + args[0]);
	}

	try {

	    // Load the build parameters from Automatics
	    loadBuildParameters();

	    // Adding shutdown hook
	    addPreShutDownOperations();

	    TestExecutor testExecutor = new TestExecutor();
	    // Entry point to suite
	    testExecutor.executeTestSuite();

	    Thread systemTerminationThread = new Thread("JVM Terminator") {
		public void run() {
		    try {
			sleep(5000);

			/*
			 * If some thread's are still in active state, Then the program will not exit.
			 * 
			 * Ideally, if no threads are active after printing '******** EXECUTION COMPLETED *********',
			 * then the program will exit while this daemon thread is in sleep state. That is, the program
			 * will not print '******** TERMINATING JVM *********'
			 * 
			 * But, if some threads are active even after printing '******** EXECUTION COMPLETED *********',
			 * then the program will wait for 5 seconds and print '******** TERMINATING JVM *********'
			 * followed by System.exit(0) which terminates the JVM. This is added as a fail-safe mechanism.
			 */
			LOGGER.info("********  TERMINATING JVM *********");
			System.exit(0);

		    } catch (InterruptedException e) {
			// Do nothing
		    }
		}
	    };

	    systemTerminationThread.setDaemon(true);
	    systemTerminationThread.start();

	    LOGGER.info("********  EXECUTION COMPLETED *********");
	} catch (Exception e1) {
	    LOGGER.error("Exception: {}", e1.getMessage());
	}

    }

    // Whenever SIGTERM is received by JVM(on clicking jenkins job abort), the shutdown hook will be executed
    private static void addPreShutDownOperations() {
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		System.out.println("Inside Add Shutdown Hook");
		List<Dut> lockedDevices = RackInitializer.getLockedSettops();
		if (null != lockedDevices && !lockedDevices.isEmpty()) {
		    AutomaticsTapApi.getRackInitializerInstance().releaseDevices();
		}

		TestInitilizationProvider testInitilizationProvider = BeanUtils.getTestInitializationProvider();
		if (null != testInitilizationProvider) {
		    testInitilizationProvider.performPostExecutionShudownOperations();
		}
		BeanUtils.closeContext();
	    }
	});

	LOGGER.info("Shut Down Hook Attached.");

    }

    /**
     * Method to load the build parameters from test manager. This is introduced to overcome the dependency on jenkins
     * job - build parameters whena new entry is to be added.
     * 
     * @throws Exception
     */
    private static void loadBuildParameters() throws Exception {

	StringBuffer targetUrl = null;
	String contentValue = null;

	JSONObject jsonObject = null;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	Iterator iterator = null;

	// Load the STB Property file
	AutomaticsPropertyUtility.loadProperties();

	serverCommunicator = new ServerCommunicator();

	String automaticsUrl = AutomaticsPropertyUtility.getProperty("automatics.url");
	if (null == automaticsUrl) {
	    LOGGER.error("Automatics url not configured in Props. Cannot continue execution");
	    throw new Exception();
	}
	targetUrl = new StringBuffer(AutomaticsPropertyUtility.getProperty("automatics.url"));
	targetUrl.append("getJMTriggerParameterDetails.htm");

	// Set the post data content
	contentValue = "id=" + System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0");

	// Get the build parameter details from tets manager for the given id
	serverResponse = serverCommunicator.postDataToServer(targetUrl.toString(), contentValue, "POST", 60000, null);

	if (serverResponse != null) {
	    if (serverResponse.getResponseCode() == HttpStatus.SC_OK) {
		if (CommonMethods.isNotNull(serverResponse.getResponseStatus())) {

		    try {
			LOGGER.info("Automatics Response - " + serverResponse.getResponseStatus());

			jsonObject = new JSONObject(serverResponse.getResponseStatus());

			LOGGER.info("\n\n***************** Automatics BUILD PARAMETERS *****************");

			iterator = jsonObject.keys();

			while (iterator.hasNext()) {

			    // get the next key entry
			    contentValue = (String) iterator.next();

			    // Set the key value pair to system variable
			    System.setProperty(contentValue, jsonObject.getString(contentValue));

			    LOGGER.info("     " + contentValue + "     |     " + jsonObject.getString(contentValue));
			}

			LOGGER.debug("\n\n*********************************************\n\n");

		    } catch (JSONException jsonException) {
			LOGGER.error("Error while processing build parameters from Automatics.");
		    }
		}
	    }
	}
    }

}
