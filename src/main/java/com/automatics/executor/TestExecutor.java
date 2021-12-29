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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.DataProviderConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.constants.TraceProviderConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.core.TestMethod;
import com.automatics.device.config.DeviceConfigModelUtils;
import com.automatics.device.config.DeviceModels;
import com.automatics.providers.TestInitilizationProvider;
import com.automatics.rack.RackInitializer;
import com.automatics.reporter.TestResultUpdator;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.TestParserUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class executes the given list of test cases in multiple STBs.
 * 
 * @author smithabg
 * @author Selvaraj Mariyappan
 * @author Arjun Prakash
 */
public class TestExecutor {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutor.class);

    private static TestInitilizationProvider testInitilizationProvider;

    private static RackInitializer rackInitializerInstance = null;

    /**
     * The method which executes the test cases.
     */
    public void executeTestSuite() {
	LOGGER.info("Start Execution Test Suite");

	try {
	    testInitilizationProvider = BeanUtils.getTestInitializationProvider();
	    createAndCleanTraceDirectory();
	    // Perform framework and test environment initialization
	    performPreExecutionInitialization();

	    // Entry point for device initialization
	    rackInitializerInstance = AutomaticsTapApi.getRackInitializerInstance();

	    // Create test suite and execute tests
	    prepareAndExecuteTestSuite();

	    // Perform framework and test environment clean up
	    performPostExecutioCleanup();

	} catch (Throwable failed) {
	    LOGGER.error("Execution thrown error - ", failed.getMessage());
	    if (rackInitializerInstance != null) {
		LOGGER.info("Releasing devices.");
		rackInitializerInstance.releaseDevices();
	    }
	} finally {

	    LOGGER.info("Updating final execution status to Automatics");
	    // Update the status to Automatics, if not already done.
	    AutomaticsUtils.updateFinalStatusToAutomatics();

	    // Reattempt the pending test step status update again to Automatics
	    TestResultUpdator.reattemptPendingStatusUpdate();
	}
    }

    public void performPreExecutionInitialization() throws Exception {
	// Verify if device configuration exists and if it is valid
	// Verifies if stb props configured
	validateConfigFiles();
	SupportedModelHandler.initializeSupportedModelInformation();
	if (null != testInitilizationProvider) {
	    testInitilizationProvider.performPreExecutionInitialization();
	}
    }

    void performPostExecutioCleanup() {
	// Perform partner side post execution cleanup
	if (null != testInitilizationProvider) {
	    testInitilizationProvider.performPostExecutionCleanup();
	}

	// Releasing devices if any exists
	rackInitializerInstance.releaseDevices();
    }

    void prepareAndExecuteTestSuite() {
	XmlSuite suite = null;
	XmlTest test = null;
	TestNG testng = null;
	List<XmlSuite> suites = null;
	List<XmlClass> classes = null;
	TestListenerAdapter automaticsTestListener = null;
	// Creating the XMLSuite for the test cases.
	LOGGER.debug("Creating the XMLSuite for the test cases");

	suites = new ArrayList<XmlSuite>();
	suite = new XmlSuite();
	suite.setName(AutomaticsConstants.SUITE_NAME);
	suite.setVerbose(AutomaticsConstants.TEST_VERBOSE_LEVEL);
	suite.setDataProviderThreadCount(DataProviderConstants.DATA_PROVIDER_PARALLEL_THREAD_COUNT);

	// Creating list of XMLClasses of the classes under test.
	LOGGER.debug("Creating list of XMLClasses of the classes under test.");

	classes = getXmlClasses();

	// Creating an XMLTest for the created list of XMLClasses.
	LOGGER.debug("Creating an XMLTest for the created list of XMLClasses.");

	test = new XmlTest(suite);
	test.setName(AutomaticsConstants.TEST_NAME);
	test.setXmlClasses(classes);
	// test.setPreserveOrder(AutomaticsConstants.BOOL_TRUE);

	/* Setting this to false so that there isn't any predefined order of execution. */
	test.setPreserveOrder(false);

	suites.add(suite);

	// Defining the custom listener for the execution.
	automaticsTestListener = new AutomaticsTestListener();

	/*
	 * Defining and running the TestNG object with the created XmlSuite.
	 */
	testng = new TestNG();
	testng.addListener(automaticsTestListener);
	testng.setAnnotationTransformer(new AutomaticsAnnotationTransformer());
	testng.setXmlSuites(suites);
	testng.run();
    }

    /**
     * This method creates a list of included methods corresponding to the tests to be run for each test class.
     * 
     * @param clazz
     *            - String which denotes the name of the class
     * 
     * @return the list of included methods.
     */
    private List<XmlInclude> getIncludedMethods(Class clazz) {

	List<XmlInclude> includedMethods = new ArrayList<XmlInclude>();

	// Obtaining the list of test methods to be run in the given class.
	LOGGER.debug("Obtaining the list of test methods to be run in the given class.");

	List<TestMethod> methods = TestParserUtils.parseTestClass(clazz);

	// Adding the included methods into the return list.
	LOGGER.debug("Adding the included methods into the return list.");

	int index = 0;

	for (TestMethod method : methods) {
	    String methodName = method.getTestMethodName();
	    includedMethods.add(index, new XmlInclude(methodName));
	    index++;
	}

	return includedMethods;
    }

    /**
     * This method creates a list of XmlClasses corresponding to the classes under test.
     * 
     * @return list of XML classes.
     */
    private List<XmlClass> getXmlClasses() {

	List<XmlClass> xmlClasses = new ArrayList<XmlClass>();
	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

	// Obtaining the list of test classes.
	LOGGER.debug("Obtaining the list of test classes.");
	classes = TestParserUtils.obtainTestClasses();

	for (Class clazz : classes) {
	    XmlClass xmlClass = new XmlClass(clazz, false);

	    // xmlClass.setExcludedMethods(excludedMethods)
	    // Obtaining the list of test methods to be run in the given class.
	    List<XmlInclude> includedMethods = getIncludedMethods(clazz);
	    xmlClass.setIncludedMethods(includedMethods);

	    if (includedMethods.size() > 0) {
		xmlClasses.add(xmlClass);
	    }
	}

	return xmlClasses;
    }

    /**
     * This method sets the executor class loader to the URL class Loader.
     */
    public void setClassLoader() {

	// Setting the executor class loader to a URL classloader.
	LOGGER.debug("Setting the executor class loader to a URL classloader.");

	try {
	    File file = new File(System.getProperty(ReportsConstants.USR_DIR) + ReportsConstants.TC_DIR);
	    URL url;
	    url = file.toURI().toURL();

	    URL[] urls = new URL[] { url };
	    ClassLoader cl = new URLClassLoader(urls);
	    Thread.currentThread().setContextClassLoader(cl);
	} catch (MalformedURLException mue) {
	    LOGGER.error("Excpetion occurred while setting the classloader: " + mue);
	}
    }

    /**
     * Creates the directory to store trace logs. If directory already exists then the contents will be cleared
     */
    private void createAndCleanTraceDirectory() {
	LOGGER.info("Cleaning the trace log directory");

	File directory = new File(TraceProviderConstants.SETTOP_TRACE_DIRECTORY);

	try {
	    FileUtils.forceMkdir(directory);
	    FileUtils.cleanDirectory(directory);
	} catch (IOException e) {
	    LOGGER.error("Error while creating dut trace directory", e);
	}
    }

    private void validateConfigFiles() throws Exception {
	// Checking stb props
	AutomaticsPropertyUtility.readStbProps();

	AutomaticsPropertyUtility.loadProperties();

	// Verify if device configuration exists and if it is valid
	LOGGER.info(">>>[INIT]: Validating config files");
	try {
	    String response = DeviceConfigModelUtils.readDeviceConfigFile();
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.readValue(response, DeviceModels.class);
	} catch (JsonParseException e) {
	    LOGGER.error("Error parsing device configuration: {}. Cannot continue execution.", e.getMessage());
	    throw new Exception("Error parsing device configuration");
	} catch (JsonMappingException e) {
	    LOGGER.error("Error parsing device configuration: {}. Cannot continue execution.", e.getMessage());
	    throw new Exception("Error parsing device configuration");
	} catch (IOException e) {
	    LOGGER.error("Error reading device configuration: {}. Cannot continue execution.", e.getMessage());
	    throw new Exception("Error parsing device configuration");
	} catch (Exception e) {
	    LOGGER.error("DeviceConfig props not configured or not valid: {}. Cannot continue execution.",
		    e.getMessage());
	    throw new Exception("Error parsing device configuration");
	}

    }

}
