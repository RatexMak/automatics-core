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

package com.automatics.test;

import com.automatics.constants.LoggingConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.automatics.annotations.TestDetails;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LoggingConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.constants.TraceProviderConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.TestSessionDO;
import com.automatics.device.Device;
import com.automatics.device.DeviceAccount;
import com.automatics.device.Dut;
import com.automatics.device.DutAccount;
import com.automatics.device.DutInfo;
import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.enums.BuildTypeChanges;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.ExecutionMode;
import com.automatics.enums.JobStatusValue;
import com.automatics.enums.TestType;
import com.automatics.env.TestEnvData;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.executor.RetryAnalyzer;
import com.automatics.logger.HtmlLogGenerator;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.TestInitilizationProvider;
import com.automatics.providers.trace.ConnectionTraceProvider;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.rack.RackInitializer;
import com.automatics.restclient.RestClient;
import com.automatics.restclient.RestClientConstants.HttpRequestMethod;
import com.automatics.restclient.RestClientException;
import com.automatics.restclient.RestEasyClientImpl;
import com.automatics.restclient.RestRequest;
import com.automatics.restclient.RestResponse;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.NonRackUtils;
import com.automatics.utils.TestUtils;

/**
 * Automatics base test class
 */
@Test(retryAnalyzer = RetryAnalyzer.class)
public class AutomaticsTestBase {

    private static TestInitilizationProvider testInitilizationProvider = null;

    /** The instance variable to holds the test started time. */
    private static long testStartedAt = 0L;

    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsTestBase.class);

    /** Automatics rack initialization. */
    protected static RackInitializer rackInitializerInstance = AutomaticsTapApi.getRackInitializerInstance();

    /** Automatics Tap initialization. */
    protected static AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();

    public HashMap<String, Boolean> genericTestCaseMap = new HashMap<String, Boolean>();

    public static Set<String> finalDeviceList = new HashSet<String>();
    public static String genericTestCase = null;
    public static String stepStatus = "BEFORE_SUITE";

    public static String headEnd = null;

    /**
     * The instance variable to holds the json message to send to the test manager.
     */
    public static String runningTestUid = null;

    public static boolean requireEnvSetup = true;

    public static String settopList = null;

    /** The class variable which contains whether its a component run */
    public static boolean isComponentRun = false;

    /** Class variable containing the components which are running */
    public static String componentsToRun = null;

    /** The JSON key object 'status'. */
    public static final String JSON_KEY_OBJECT_STATUS = "status";

    /** The JSON key object 'buildImageName'. */
    public static final String JSON_KEY_OBJECT_BUILD_IMAGE_NAME = "buildImageName";

    /** The JSON key object 'settopList'. */
    public static final String JSON_KEY_OBJECT_SETTOP_LIST = "settopList";

    /** The JSON key object 'startTime'. */
    public static final String JSON_KEY_OBJECT_START_TIME = "startTime";

    /** The JSON key object 'completionTime'. */
    public static final String JSON_KEY_OBJECT_COMPLETION_TIME = "completionTime";

    /** The JSON key object 'build_name'. */
    public static final String JSON_KEY_OBJECT_BUILD_NAME = "build_name";

    /** The JSON key object 'type'. */
    public static final String JSON_KEY_OBJECT_TYPE = "type";

    /** The JSON key object 'tests'. */
    public static final String JSON_KEY_OBJECT_TESTS = "tests";

    public static TestType testType = TestType.QUICK;

    public static String testBuildName = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_BUILD_NAME, "");

    public static String groups = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_INCLUDED_GROUP, "");

    public static String serviceType = System.getProperty(AutomaticsConstants.SERVICE_NAME, "");

    public static String testFilterType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);

    /** Boolean which determines whether it it an Account based execution or not */
    public static Boolean isAccountTest = new Boolean(
	    System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));

    public static String testId = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_ID);

    /** System props for enable html log */
    public static final String SYSTEM_PROPS_ENABLE_HTML_LOG = "enableHtmlLog";

    /** The static variable to store the build status **/
    private static boolean skipOnNonExpectedBuild = false;

    /** The class variable contains group run enabled or disabled status */
    public static boolean isGroupRun = false;

    /** Class variable containing the group of included test cases */
    public static String groupsToRun = null;

    public static HashMap<String, String> crashAnalysisHelperMap = new HashMap<String, String>();

    public static ExecutorService crashAnalysisExecutor = Executors.newFixedThreadPool(10);

    public static List<Future<?>> futures = new ArrayList<Future<?>>();

    /**
     * Set Skip on build value for reporting
     * 
     * @param skipOnNonExpectedBuild
     */
    public static void setSkipOnNonExpectedBuild(boolean skipOnUnExpectedBuild) {
	skipOnNonExpectedBuild = skipOnUnExpectedBuild;
    }

    public static boolean getSkipOnNonExpectedBuild() {
	return skipOnNonExpectedBuild;
    }

    public static boolean isGroupRun() {
	return isGroupRun;
    }

    public static void setGroupRun(boolean isGrpRun) {
	isGroupRun = isGrpRun;
    }

    public static String getGroupsToRun() {
	return groupsToRun;
    }

    public static void setGroupsToRun(String grpsToRun) {
	groupsToRun = grpsToRun;
    }

    public static DeviceCategory deviceCategory;

    /**
     * Before suite initialization.
     * 
     * @param context
     *            Initial test context.
     */
    @BeforeSuite(alwaysRun = true)
    protected void performBeforeSuiteInit(ITestContext context) {
	final RackInitializer rackInitializerInstance = AutomaticsTapApi.getRackInitializerInstance();
	ExecutorService executorService = Executors.newFixedThreadPool(10);
	stepStatus = "BEFORE_SUITE";
	settopList = TestUtils.getCommaSepDeviceMac();
	final List<Dut> lockedDevices = new ArrayList<Dut>();
	LOGGER.info(">>>[BEFORE_SUITE]: Perform before suite initialization");
	if (null == testInitilizationProvider) {
	    testInitilizationProvider = BeanUtils.getTestInitializationProvider();
	}

	if (isAccountTest) {
	    lockedDevices.addAll(rackInitializerInstance.getLockedDevicesInAccountBasedTest());
	} else {
	    lockedDevices.addAll(RackInitializer.getLockedSettops());
	    if (null != lockedDevices && !lockedDevices.isEmpty()) {
		// Test code to access dut object from @BeforeGroup/Test etc
		LOGGER.info("Adding locked devices to dut object in AutomaticsTestBase");
		context.setAttribute("dut", lockedDevices);
	    }
	}
	LOGGER.info("Locked Settops: " + lockedDevices.size());

	testType = TestUtils.getTestType();
	testStartedAt = System.currentTimeMillis();

	if (null != lockedDevices && !lockedDevices.isEmpty()) {

	    // Get the device category
	    deviceCategory = CommonMethods.getDeviceCategory((Device) lockedDevices.get(0));

	    for (final Dut device : lockedDevices) {
		executorService.execute(new Runnable() {

		    @Override
		    public void run() {

			TestEnvData testEnvData = new TestEnvData();
			if ((!device.getModel().equalsIgnoreCase(AutomaticsConstants.DEVICE_MODEL_ECB))
				&& !SupportedModelHandler.isNonRDKDevice(device)) {

			    if (CommonMethods.isNotNull(groups)) {
				setConfigurationsForGroupRun();

			    } else if (isComponentRun) {
				if (CommonMethods.isNotNull(testId)) {
				    LOGGER.info("Component Execution is selected with Test Ids " + testId
					    + " For Components " + getComponentsToRun());
				    String imageName = TestUtils.getBuildName();
				    device.setFirmwareVersion(imageName);
				    testType = TestType.valueOf(TestType.UNMAPPED.name());
				} else {
				    LOGGER.error("No test case found to execute as part of this component");
				    throw new FailedTransitionException(GeneralError.PRE_CONDITION_FAILURE,
					    "No Test Case IDs found across the components which mapped : "
						    + componentsToRun);
				}
			    } else if (null != testFilterType && !testFilterType.isEmpty()) {
				setConfigurationsBasedOntestType(device, lockedDevices);
			    }
			} else {
			    LOGGER.info("Else of main : testFilterType : " + testFilterType + "----testType = "
				    + testType + "isNonRDK device? " + SupportedModelHandler.isNonRDKDevice(device));
			}
			boolean isAvPresent = false;

			if (!AutomaticsTestBase.isAccountTest && !SupportedModelHandler.isRDKB(device)
				&& !SupportedModelHandler.isRDKC(device)) {

			    isAvPresent = performAVCheck(device, true);
			}
			testEnvData.setAvPresent(isAvPresent);

			LOGGER.info("INIT-{} Perform before suite initialization", device.getHostMacAddress());
			if (null != testInitilizationProvider) {
			    testInitilizationProvider.performBeforeSuiteInitialization(device, testEnvData);
			} else {
			    LOGGER.info(
				    "Skipping partner specific before suite initialization as it is not configured.");
			}

			if (!AutomaticsTestBase.isAccountTest && !SupportedModelHandler.isRDKB(device)
				&& !SupportedModelHandler.isRDKC(device)) {
			    if (!isAvPresent) {
				AutomaticsTestBase.releaseSettop(device);
			    } else {
				startDeviceTrace(device);
			    }
			} else {
			    startDeviceTrace(device);
			}
		    }
		});
	    }
	}

	// Ensure no new tasks are submitted
	executorService.shutdown();
	// Exit only when all threads have completed execution
	while (!executorService.isTerminated()) {
	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
		LOGGER.trace(e.getMessage());
	    }
	}
    }

    /**
     * Before method initialization
     * 
     * @param data
     *            Test data
     * @param itestResult
     *            Test Result
     */
    @BeforeMethod(alwaysRun = true)
    protected void performBeforeMethodInit(Object[] data, ITestResult itestResult) {

	stepStatus = "BEFORE_METHOD";
	Device device = getTestDevice(data);
	if (null != device) {
	    LOGGER.info(">>>[BEFORE_METHOD]: Perform before method initialization {}", device.getHostMacAddress());
	    TestDetails testDetailsAnnotation = itestResult.getMethod().getConstructorOrMethod().getMethod()
		    .getAnnotation(TestDetails.class);
	    String testCaseTobeExecuted = testDetailsAnnotation.testUID();
	    TestSessionDO testSessionDetails = null;

	    if (device.getTestSessionDetails() == null || !device.getTestSessionDetails().getTestCaseLastExecuted()
		    .equals(testDetailsAnnotation.testUID())) {
		testSessionDetails = new TestSessionDO();
	    } else {
		testSessionDetails = device.getTestSessionDetails();
	    }
	    testSessionDetails.setTestCaseTobeExecuted(testCaseTobeExecuted);
	    /*
	     * We need to capture the execution time of each automation id's. For that, the start and end time of
	     * executing an automation id will be sent to Automatics.
	     */
	    testSessionDetails.setStartTime("UTC", "yyyy-MM-dd HH:mm:ss");
	    LOGGER.info(">>>[BEFORE_METHOD]: Sending test exection start time to Automatics {}",
		    device.getHostMacAddress());
	    captureAutomationScriptExecutionTime(device, testCaseTobeExecuted, true);
	    device.setTestSessionDetails(testSessionDetails);

	    MDC.put(LoggingConstants.LOGGER_LOG_FILE_NAME_KEY, CommonMethods.getSettopDetails(device));
	    MDC.put(LoggingConstants.LOGGER_DEVICE_MAC_KEY, device.getHostMacAddress());

	    TraceProvider provider = device.getTrace();
	    if (null != provider && provider instanceof ConnectionTraceProvider
		    && CommonMethods.isNotNull(((ConnectionTraceProvider) provider).getTraceFileName())) {
		MDC.put(LoggingConstants.LOGGER_TRACE_FILE_KEY,
			((ConnectionTraceProvider) provider).getTraceFileName());
		MDC.put(LoggingConstants.LOGGER_JMD_ID_KEY, System.getProperty("JMD_ID"));
	    }

	    TestEnvData testEnvData = new TestEnvData();
	    // Perform partner specific before method initialization
	    if (null != testInitilizationProvider) {
		testInitilizationProvider.performBeforeMethodInitialization(device, testEnvData);
	    } else {
		LOGGER.info("Skipping partner specific before method initialization as it is not configured.");
	    }

	    LOGGER.info(">>>[BEFORE_METHOD]: Extending allocation before method for testType : {} {}", testType,
		    device.getHostMacAddress());
	    extendDeviceAllocationDuration(device);

	    // Clear trace buffering
	    clearDeviceTraceBuffer(device);
	} else {
	    MDC.remove(LoggingConstants.LOGGER_LOG_FILE_NAME_KEY);
	    MDC.remove(LoggingConstants.LOGGER_DEVICE_MAC_KEY);
	    MDC.remove(LoggingConstants.LOGGER_TRACE_FILE_KEY);
	    MDC.remove(LoggingConstants.LOGGER_JMD_ID_KEY);
	}
    }

    private void clearDeviceTraceBuffer(Dut device) {
	TraceProvider traceProvider = null;

	if (device instanceof DutAccount) {
	    DeviceAccount homeAccount = null;
	    LOGGER.info(">>>[BEFORE_METHOD]: Clear device trace buffer for {}", device.getHostMacAddress());
	    homeAccount = (DeviceAccount) device;

	    if ((homeAccount.getDevices() != null) && !(homeAccount.getDevices().isEmpty())) {
		for (DutInfo connectedDut : homeAccount.getDevices()) {
		    Device connectedDevice = (Device) connectedDut;
		    if (!SupportedModelHandler.isNonRDKDevice(connectedDevice)) {
			traceProvider = connectedDevice.getTrace();
			if (null != traceProvider) {
			    connectedDevice.getTrace().stopBuffering();
			    connectedDevice.getTrace().startBuffering();
			}

		    }
		}
	    }
	} else {
	    traceProvider = device.getTrace();
	    if (null != traceProvider) {
		traceProvider.stopBuffering();
		traceProvider.startBuffering();
	    }
	}
    }

    /**
     * Before after method clean up
     * 
     * @param data
     *            Test data
     * @param itestResult
     *            Test Result
     * 
     */
    @AfterMethod(alwaysRun = true)
    protected void performAfterMethodCleanup(Object[] data, ITestResult testResult) {
	stepStatus = "AFTER_METHOD";

	Device device = getTestDevice(data);
	LOGGER.info(">>>[AFTER_METHOD]: Clear device trace buffer for {}", device.getHostMacAddress());
	if (null != device) {

	    // Setting test execution completion time
	    device.getTestSessionDetails().setEndTime();
	    TestDetails testDetailsAnnotation = testResult.getMethod().getConstructorOrMethod().getMethod()
		    .getAnnotation(TestDetails.class);

	    String testUID = testDetailsAnnotation.testUID();
	    copyLogsToUidRelatedFolders(testUID, device, testResult);
	    device.getTestSessionDetails().setTestCaseLastExecuted(testUID);

	    LOGGER.info(">>>[AFTER_METHOD]: Sending test execution completion time for {}", device.getHostMacAddress());
	    captureAutomationScriptExecutionTime(device, testDetailsAnnotation.testUID(), false);

	    LOGGER.info("Restarting Trace in after method");
	    clearDeviceTraceBuffer(device);
	    try {

		if (!TestType.isQt(testType.name())) {

		    LOGGER.info("Going for if build changed : Test Type : " + testType.name());
		    String buildName = TestUtils.getBuildName();
		    BuildTypeChanges hasBuildChanged = BuildTypeChanges.NO_CHANGE;
		    if (null != testInitilizationProvider) {
			hasBuildChanged = testInitilizationProvider.getBuildChangeStatus(device, buildName);
		    } else {
			LOGGER.info(
				"Skipping build change verification as partner specific initialization is not configured.");
		    }
		    LOGGER.info(">>>[AFTER_METHOD]: Build Change Status: {}", hasBuildChanged);
		    if (!hasBuildChanged.equals(BuildTypeChanges.NO_CHANGE)) {
			updateJobStatus(device.getHostMacAddress(), JobStatusValue.BUILD_CHANGED_AFTER_TEST);
		    }
		}

	    } catch (Exception e) {
		LOGGER.error("Failed to start the trace ");
	    }

	    TestEnvData testEnvData = new TestEnvData();
	    testEnvData.setTestDetails(testDetailsAnnotation);

	    if (null != testInitilizationProvider) {
		testInitilizationProvider.performAfterMethodCleanup(device, testEnvData);
	    } else {
		LOGGER.info("Skipping parter specific after method clean up as it is not configured.");
	    }
	}
    }

    /**
     * After suite initialization.
     * 
     * @param context
     *            Initial test context.
     */
    @AfterSuite(alwaysRun = true)
    protected void performAfterSuiteCleanup(ITestContext context) {

	stepStatus = "AFTER_SUITE";
	List<Dut> lockedSettops = null;
	List<Dut> lockedPivotDUTs = null;
	String formattedBuildName = null;
	String buildName = null;
	boolean isIpv6 = false;
	boolean isRdkB = false;

	LOGGER.info(">>>[AFTER-SUITE]: Performing after suite cleanup");

	// Perform build verification
	if (null == testInitilizationProvider) {
	    testInitilizationProvider = BeanUtils.getTestInitializationProvider();
	}

	if (isAccountTest) {
	    lockedSettops = rackInitializerInstance.getLockedDevicesInAccountBasedTest();
	    // Fetching locked Pivot DUTs to be sent to Automatics
	    lockedPivotDUTs = rackInitializerInstance.getLockedPivotDevicesInAccountBasedTest();
	} else {
	    lockedSettops = RackInitializer.getLockedSettops();
	}
	LOGGER.info(">>>[AFTER-SUITE]: Locked devices after suite execution {}", lockedSettops.size());

	// Performing trace stop and getting job status for each device
	if (null != lockedSettops) {
	    TestEnvData testEnvData = new TestEnvData();

	    TestType testType = getTriggeredTestType();

	    BuildTypeChanges hasBuildChanged = BuildTypeChanges.SKIP_CHECK;

	    for (final Dut device : lockedSettops) {

		if (!SupportedModelHandler.isNonRDKDevice(device)) {

		    // Need to check if build got changed only for non-QT devices
		    if (!TestType.isQt(testType.name())) {
			if (isAccountTest) {
			    hasBuildChanged = BuildTypeChanges.SKIP_CHECK;
			    for (Dut pivot : lockedPivotDUTs) {
				if (pivot.getHostMacAddress().equals(device.getHostMacAddress())) {
				    finalDeviceList.add(device.getHostMacAddress());
				    buildName = device.getFirmwareVersion();
				    if (SupportedModelHandler.isRDKB(pivot)) {
					isRdkB = true;
				    }
				    if (CommonMethods.isIpv6Address(pivot)) {
					isIpv6 = true;
				    }
				    break;
				} else {
				    releaseSettop(device);
				    continue;
				}
			    }
			} else {
			    buildName = device.getFirmwareVersion();
			    if (SupportedModelHandler.isRDKB(device)) {
				isRdkB = true;
			    }
			    if (CommonMethods.isIpv6Address(device)) {
				isIpv6 = true;
			    }
			    // check for build change in device
			    LOGGER.info(">>>[AFTER-SUITE]: Verifying if build changed after test");

			    if (null != testInitilizationProvider) {
				hasBuildChanged = testInitilizationProvider.getBuildChangeStatus(device, testBuildName);
			    } else {
				LOGGER.info(
					"Skipping build change verification as partner specific initialization is not configured.");
				hasBuildChanged = BuildTypeChanges.NO_CHANGE;
			    }
			    LOGGER.info(">>>[AFTER-SUITE]: Build Change Status: {}", hasBuildChanged);

			    if (hasBuildChanged.equals(BuildTypeChanges.NO_CHANGE)) {
				finalDeviceList.add(device.getHostMacAddress());
			    } else {
				if (!hasBuildChanged.equals(BuildTypeChanges.SKIP_CHECK)) {
				    if (hasBuildChanged.equals(BuildTypeChanges.UNABLE_TO_DETERMINE)) {
					LOGGER.info(
						">>>[AFTER-SUITE]: Unable to verify build on the device as same as the testing firmware.Device could be down.");
					updateJobStatus(device.getHostMacAddress(), JobStatusValue.SSH_FAIL);
				    } else if (hasBuildChanged.equals(BuildTypeChanges.BUILD_CHANGED_TO_PROD)) {
					LOGGER.info(">>>[AFTER-SUITE]: Build changed to PRODUCTION version.");
					updateJobStatus(device.getHostMacAddress(),
						JobStatusValue.BUILD_CHANGED_AFTER_TEST);
				    } else if (hasBuildChanged.equals(BuildTypeChanges.BUILD_CHANGED_TO_NON_PROD)) {
					LOGGER.info(">>>[AFTER-SUITE]: Build changed to non-PRODUCTION version.");
					updateJobStatus(device.getHostMacAddress(),
						JobStatusValue.BUILD_CHANGED_AFTER_TEST);
				    }
				}
			    }
			}

		    } else {
			LOGGER.info(">>>[AFTER-SUITE]: Build changed check not needed for QT.");
			finalDeviceList.add(device.getHostMacAddress());
		    }

		    try {
			TraceProvider connectionBasedTrace = device.getTrace();
			if (null != connectionBasedTrace) {
			    connectionBasedTrace.stopBuffering();
			    connectionBasedTrace.stopTrace();
			}
		    } catch (Exception e) {
			LOGGER.error("Error stoppping trace for device {}", device.getHostMacAddress());
		    }

		    // Stopping serial trace
		    try {
			TraceProvider serialTrace = device.getSerialTrace();
			if (null != serialTrace) {
			    serialTrace.stopBuffering();
			    serialTrace.stopTrace();
			}
		    } catch (Exception e) {
			LOGGER.error("Error stoppping trace for device {}", device.getHostMacAddress());
		    }
		    boolean isAvPresent = false;
		    if (!AutomaticsTestBase.isAccountTest && !SupportedModelHandler.isRDKB(device)
			    && !SupportedModelHandler.isRDKC(device)) {
			isAvPresent = performAVCheck(device, false);
		    }

		    testEnvData.setAvPresent(isAvPresent);

		    if (null != testInitilizationProvider) {
			testInitilizationProvider.performAfterSuiteCleanup(device, testEnvData);
		    } else {
			LOGGER.info("Skipping parter specific after suite clean up as it is not configured.");
		    }
		    releaseSettop(device);
		}
	    }
	}

	/*
	 * For CI validation cases to send BOX NOT AVAILABLE status to RDK Portal
	 */
	if (testFilterType.equals(AutomaticsTestTypes.QUICK_CI.value())
		|| testFilterType.equals(AutomaticsTestTypes.FAST_QUICK_CI.value())) {
	    if (finalDeviceList == null || finalDeviceList.isEmpty()) {
		AutomaticsUtils.jsonToAutomatics = AutomaticsUtils.formatJsonObjectToRespondAsBoxIssues();
		LOGGER.info(
			"Json To Automatics as box not accessible or available " + AutomaticsUtils.jsonToAutomatics);
	    }

	}

	/**
	 * Formats Json for test other than QT
	 */
	if ((!TestType.isQt(testType.name()))) {

	    if (CommonMethods.isNull(buildName)) {
		buildName = TestUtils.getBuildName();
	    }
	    formattedBuildName = formatFirmwareNameForReporting(buildName, isIpv6, isRdkB);

	    // Preparing result for Automatics
	    try {
		// Get json response for updating status to Automatics orchestration
		AutomaticsUtils.jsonToAutomatics = generateFinalJsonResponse(formattedBuildName);
		LOGGER.info("[AFTER-SUITE:]JSON message to Automatics: {}", AutomaticsUtils.jsonToAutomatics);
	    } catch (JSONException jex) {
		LOGGER.error("Exception while generating final json response", jex);
	    }
	}

	if (isHtmlLoggingEnabled()) {
	    String entireLogsLocation = AutomaticsConstants.SETTOP_LOG_DIRECTORY;
	    String testingBuild = System.getProperty(AutomaticsConstants.BUILD_NAME_SYSTEM_PROPERTY, "").trim();
	    List<String> listOfTestCases = new ArrayList<String>(
		    Arrays.asList(System.getProperty("filterTestIds").split(",")));
	    List<String> listOfDuts = new ArrayList<String>(Arrays.asList(System.getProperty("settopList").split(",")));
	    (new HtmlLogGenerator()).parseAndGenerateHTMLLog(listOfTestCases, listOfDuts, entireLogsLocation,
		    testingBuild);
	}
    }

    private static String formatFirmwareNameForReporting(String buildName, boolean isIpv6, boolean isRdkB) {
	String tempFirmwareVersion = buildName;
	if (CommonMethods.isNotNull(buildName) && isRdkB) {
	    // format the firmware image name according the protocol (IPV4 &IPV6) and execution mode
	    tempFirmwareVersion = CommonMethods.formatFirmwareImageNameForReporting(isIpv6, buildName, isRdkB);
	}
	return tempFirmwareVersion;
    }

    private static String generateFinalJsonResponse(String firmwareVersion) throws JSONException {

	long testEndedAt = System.currentTimeMillis();
	JSONObject jsonToBePassed = new JSONObject();

	jsonToBePassed.put(JSON_KEY_OBJECT_STATUS, getFinalJobStatus());

	String serviceType = System.getProperty(AutomaticsConstants.SERVICE_NAME, AutomaticsConstants.EMPTY_STRING);
	jsonToBePassed.put("service", serviceType);
	jsonToBePassed.put(JSON_KEY_OBJECT_BUILD_IMAGE_NAME, firmwareVersion);

	JSONArray jsonSettopList = new JSONArray();
	for (String mac : finalDeviceList) {
	    jsonSettopList.put(mac);
	}

	jsonToBePassed.put(JSON_KEY_OBJECT_SETTOP_LIST, jsonSettopList);
	jsonToBePassed.put(JSON_KEY_OBJECT_START_TIME, testStartedAt);
	jsonToBePassed.put(JSON_KEY_OBJECT_COMPLETION_TIME, testEndedAt);
	String jmdId = System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0");
	jsonToBePassed.put(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, Integer.parseInt(jmdId));

	Boolean updateRdkPortal = TestUtils.isUpdateRdkPortal();
	jsonToBePassed.put(AutomaticsConstants.SYSTEM_PROPERTY_UPDATE_RDK_PORTAL, updateRdkPortal);
	JSONObject result = new JSONObject();
	result.put(JSON_KEY_OBJECT_BUILD_NAME, firmwareVersion);

	if (isComponentRun()) {
	    result.put(JSON_KEY_OBJECT_TYPE, "ci");
	} else {
	    result.put(JSON_KEY_OBJECT_TYPE, getTestTypeName());
	}
	result.put(JSON_KEY_OBJECT_TESTS, new JSONArray());
	jsonToBePassed.put(AutomaticsUtils.JSON_KEY_OBJECT_RESULT, result);
	return jsonToBePassed.toString();
    }

    /**
     * 
     * This method determines whether more than one reason is present for failure.If present,the stus assigned will
     * become BOXES_UNUSABLE.Else the same status will be assigned to json to be passed to Automatics
     * 
     * @param jsonToBePassed
     *            jsonto be passed
     * @return JSON object
     * @throws JSONException
     */
    public static JobStatusValue getFinalJobStatus() {
	JobStatusValue jobStatusValue = null;

	if ((!TestType.isQt(testType.name()))) {
	    if (finalDeviceList != null && !finalDeviceList.isEmpty()) {
		jobStatusValue = JobStatusValue.COMPLETED;
	    } else {
		if (AutomaticsUtils.jobStatus != null && !AutomaticsUtils.jobStatus.isEmpty()) {
		    for (String each : AutomaticsUtils.jobStatus.keySet()) {
			LOGGER.info("key " + each + " Value : " + AutomaticsUtils.jobStatus.get(each));
		    }
		    HashSet<JobStatusValue> statuses = new HashSet<JobStatusValue>(AutomaticsUtils.jobStatus.values());
		    if (statuses.size() > 1) {
			jobStatusValue = JobStatusValue.BOXES_UNUSABLE;

			if (!TestType.isQt(testType.name())) {
			    if (CommonMethods.isNotNull(settopList)) {
				if (AutomaticsUtils.jobStatus.containsKey(settopList)) {
				    jobStatusValue = AutomaticsUtils.jobStatus.get(settopList);
				}
			    }
			}
		    } else {
			jobStatusValue = statuses.iterator().next();
		    }
		} else {
		    jobStatusValue = JobStatusValue.BOXES_UNUSABLE;
		}
	    }
	}
	return jobStatusValue;
    }

    private static String getTestTypeName() {
	String testTypename = null;
	String testTypeProperty = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	TestType testTypeTriggered = TestType.UNMAPPED;

	if (CommonMethods.isNotNull(testTypeProperty)) {
	    testTypeTriggered = TestType.valueOf(testTypeProperty);
	}

	if (!TestType.isQt(testTypeTriggered.name())) {
	    if (isGroupRun) {
		testTypename = getGroupsToRun();
	    } else if (isComponentRun) {
		testTypename = getComponentsToRun();
	    } else {
		testTypename = testType.get();
	    }
	}
	return testTypename;
    }

    private static TestType getTriggeredTestType() {
	String testFilterType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	TestType testType = TestType.QUICK;
	// Set test type
	if (CommonMethods.isNotNull(testFilterType)) {
	    LOGGER.info("Value of intial testType : " + testType);
	    testType = TestType.valueOf(testFilterType);
	    LOGGER.info("Value of TestType provided by job : " + testType);
	} else {
	    LOGGER.info("Failed to parse testType : " + testType + " : testFilterType" + testFilterType);
	}
	return testType;
    }

    /**
     * This method is used to update job status in map.The map is later used to pass the same to TM
     * 
     * @param deviceMac
     *            Mac of the device
     * @param status
     *            Status message
     */
    public synchronized static void updateJobStatus(String deviceMac, JobStatusValue status) {
	LOGGER.info("Updating jobstatus for " + deviceMac + " as " + status);
	AutomaticsUtils.jobStatus.put(deviceMac, status);
	LOGGER.info("All Values:");
	for (String each : AutomaticsUtils.jobStatus.keySet()) {
	    LOGGER.info("key " + each + " Value : " + AutomaticsUtils.jobStatus.get(each));
	}
    }

    public static String getRunningTestUid() {
	return runningTestUid;
    }

    public static void setRunningTestUid(String runningTestUId) {
	runningTestUid = runningTestUId;
    }

    public static boolean isComponentRun() {
	return isComponentRun;
    }

    public static void setComponentRun(boolean isCmpntRun) {
	isComponentRun = isCmpntRun;
    }

    public static String getComponentsToRun() {
	return componentsToRun;
    }

    public static void setComponentsToRun(String cmpntsToRun) {
	componentsToRun = cmpntsToRun;
    }

    /**
     * Method to get the final status from Automatics
     * 
     * API : POST https://host:port/Automatics/getExecutionStatus.htm?jobManagerId=1113218
     * 
     * @return Final status
     */
    public static String getFinalJobStatusFromAutomatics(AutomaticsTapApi tapEnv) {

	String finalStatus = null;
	String finalUrl = null;
	String targetUrl = null;
	String jmdId = null;
	String responseBody = null;
	Map<String, String> headers = null;

	RestRequest request = null;
	RestClient client = null;
	RestResponse response = null;

	try {

	    client = new RestEasyClientImpl();

	    targetUrl = AutomaticsTapApi.getSTBPropsValue("automatics.url");
	    jmdId = System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID);
	    headers = new HashMap<>();
	    headers.put("Content-Type", "application/json");

	    finalUrl = targetUrl + AutomaticsConstants.FORWARD_SLASH + "getExecutionStatus.htm?jobManagerId=" + jmdId;

	    request = new RestRequest(finalUrl, HttpRequestMethod.POST, headers);
	    request.setContent("test");
	    request.setMediaType(MediaType.APPLICATION_JSON_TYPE);

	    response = client.executeAndGetResponse(request);

	    if (response.getResponseCode() == AutomaticsConstants.CONSTANT_200) {
		responseBody = response.getResponseBody();
		if (CommonMethods.isNotNull(responseBody)) {
		    try {
			JSONObject json = new JSONObject(responseBody);
			finalStatus = json.getString("status");
		    } catch (JSONException e) {
			LOGGER.error("Invalid response message from from automatics. ", e.getMessage());
		    }
		}
	    } else {
		LOGGER.error("Invalid response from automatics. Code - ",
			response.getResponseCode() + " message - " + response.getResponseBody());
	    }

	} catch (RestClientException e) {
	    LOGGER.error("Failed to get the job status from automatics. ", e);
	}

	LOGGER.info("JOB Status for JMD_ID -  " + jmdId + " from automatics is - " + finalStatus);
	return finalStatus;
    }

    /**
     * Helper method to test whether the test need to be continued or not
     * 
     * @param testBuildName
     *            Buildname which we are testing
     * @param lockedSettops
     *            List of dut boxes need to be verified
     * @return
     */
    private boolean verifyIfBuildChangedInDevice(String testBuildName, List<Dut> lockedDevices) {

	boolean isBuildChanged = true;
	testBuildName = testBuildName.replace("sey", "sdy");
	LOGGER.info("[BEFORE-SUITE:]Expected build in device is {}", testBuildName);
	for (Dut device : lockedDevices) {
	    if (isAccountTest) {
		if (!System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SETTOP_LIST).toUpperCase()
			.contains(device.getHostMacAddress().toUpperCase())) {
		    continue;
		}
	    }
	    LOGGER.info("[BEFORE-SUITE:]Verifying if build changed before test");
	    BuildTypeChanges buildTypeChange = null;
	    if (null != testInitilizationProvider) {
		buildTypeChange = testInitilizationProvider.getBuildChangeStatus(device, testBuildName);
	    } else {
		LOGGER.info("Skipping build change verification as partner specific initialization is not configured.");
		buildTypeChange = BuildTypeChanges.NO_CHANGE;
	    }
	    LOGGER.info("[BEFORE-SUITE:]Build Change Status: {}", buildTypeChange);

	    if (buildTypeChange.equals(BuildTypeChanges.NO_CHANGE)) {
		isBuildChanged = false;
	    } else if (BuildTypeChanges.SKIP_CHECK.equals(buildTypeChange)) {
		isBuildChanged = false;
	    } else if (BuildTypeChanges.UNABLE_TO_DETERMINE.equals(buildTypeChange)) {
		LOGGER.info("Releasing device {}", device.getHostMacAddress());
		isBuildChanged = true;
		LOGGER.error("DeviceConfig {} is not accessible. Setting status as {}", device.getHostMacAddress(),
			JobStatusValue.SSH_FAIL);
		LOGGER.error("DEVICE HAS GONE DOWN BEFORE TEST!!");
		updateJobStatus(device.getHostMacAddress(), JobStatusValue.SSH_FAIL);
		releaseSettop(device);
	    } else {
		isBuildChanged = true;
		LOGGER.error("DeviceConfig {} is not accessible. Setting status as {}", device.getHostMacAddress(),
			JobStatusValue.BUILD_CHANGED_BEFORE_TEST);
		LOGGER.error("DEVICE HAS GONE DOWN BEFORE TEST!!");
		updateJobStatus(device.getHostMacAddress(), JobStatusValue.BUILD_CHANGED_BEFORE_TEST);
		releaseSettop(device);
	    }
	}

	return isBuildChanged;
    }

    private void setConfigurationsBasedOntestType(final Dut dut, List<Dut> lockedDevices) {
	if (!TestType.isQt(testType.name())) {
	    Device device = ((Device) dut);
	    LOGGER.info(">>>[BEFORE_SUITE]: Verifying if build loaded in device as expected");
	    if (CommonMethods.isNotNull(testBuildName)
		    && (verifyIfBuildChangedInDevice(testBuildName, lockedDevices))) {
		updateJobStatus(dut.getHostMacAddress(), JobStatusValue.BUILD_CHANGED_BEFORE_TEST);
		setSkipOnNonExpectedBuild(true);

		throw new FailedTransitionException(GeneralError.PRE_CONDITION_FAILURE,
			"LOCKED boxes are not GOOD to proceed test " + testBuildName);
	    } else {
		LOGGER.info(">>>[BEFORE_SUITE]: Build in device is as expected");
	    }

	    LOGGER.info(">>>[BEFORE_SUITE]: Setting appropritate build appender based on executionMode");
	    try {
		String buildAppender = TestUtils.getBuildAppender();
		ExecutionMode executionMode = TestUtils.getExecutionMode();
		if (null != testInitilizationProvider) {
		    testInitilizationProvider.setExecutionModeInDevice(dut, executionMode, buildAppender);
		} else {
		    LOGGER.info(
			    "Skipping setting of execution mode in device as partner specific initialization is not configured.");
		}
		device.setExecutionMode(executionMode);
	    } catch (Exception e) {
		LOGGER.info(">>>[BEFORE_SUITE]: Releasing device {}", dut.getHostMacAddress());
		releaseSettop(dut);
		throw new FailedTransitionException(GeneralError.PRE_CONDITION_FAILURE,
			"Failed to setup device with mentioned BuildAppender/Execution Mode");
	    }
	} else {
	    LOGGER.info(">>>[BEFORE_SUITE]: Build check and setting execution mode/appender is not needed for {}",
		    testType.name());
	}
    }

    private void setConfigurationsForGroupRun() {
	setGroupRun(true);
	setGroupsToRun(groups);
	LOGGER.info("Test Included Groups" + groups);
    }

    /**
     * Helper method to release the settops
     * 
     * @param catsInitializerInstance
     * @param dut
     */
    public static void releaseSettop(Dut device) {
	RackInitializer rackInitializer = AutomaticsTapApi.getRackInitializerInstance();
	// If it a Account execution, need to release all the settops belong to that home account
	if (!isAccountTest) {
	    // remove from locked devices
	    rackInitializer.removeLockedSettop(device);
	    // Release locked STB
	    rackInitializer.releaseSettop(device);
	} else {
	    rackInitializer.releaseHomeAccount(device);
	    rackInitializer.removeLockedHomeAccount(device);
	}
    }

    /**
     * Gets test device from context
     * 
     * @param params
     *            TestNG params
     * @return Device
     */
    private Device getTestDevice(Object[] params) {
	Device device = null;
	if ((params != null) && (params.length >= 1)) {
	    if (params[0] instanceof Dut) {
		device = (Device) params[0];
	    } else if (params[0] instanceof DutAccount) {
		DeviceAccount homeAccount = (DeviceAccount) params[0];
		LOGGER.info("Home Account : " + homeAccount.getAccountNumber());
		device = (Device) homeAccount.getPivotDut();
	    }
	}
	return device;
    }

    private void extendDeviceAllocationDuration(Dut device) {
	DeviceAccount homeAccount = null;
	try {
	    // allocation extension for Account Test
	    if (device instanceof DutAccount) {
		LOGGER.info("Extending allocation for all devices in Home account");
		homeAccount = (DeviceAccount) device;
		if ((homeAccount.getDevices() != null) && !(homeAccount.getDevices().isEmpty())) {
		    for (DutInfo connectedDut : homeAccount.getDevices()) {
			LOGGER.info("Extending allocation for " + connectedDut.getHostMacAddress());
			Device connectedDevice = (Device) connectedDut;
			extendAllocationForDevice(connectedDevice);
		    }
		} else {
		    LOGGER.error("Failed to extend allocation for devices in Home account.Device list is null/empty");
		}
	    } else {
		if (!SupportedModelHandler.isRDKB(device) && !SupportedModelHandler.isRDKC(device)
			&& !NonRackUtils.isNonRack()) {

		    if (SupportedModelHandler.isRDKVClient(device)) {
			try {
			    Dut gatewaySettop = ((Device) device).getGateWaySettop();
			    if (gatewaySettop != null && gatewaySettop.isLocked()) {
				LOGGER.info("Extending allocation of connected gateway box in before method");
				extendAllocationForDevice(gatewaySettop);
			    }

			} catch (Exception e) {
			    LOGGER.error("Issue with Extending gateway dut box alloction" + e);
			}
		    }
		} else {
		    extendAllocationForDevice(device);
		    if (((Device) device).getConnectedDeviceList() != null
			    && !((Device) device).getConnectedDeviceList().isEmpty()) {
			for (Dut connectedSettopObject : ((Device) device).getConnectedDeviceList()) {
			    extendAllocationForDevice(connectedSettopObject);
			}
		    }
		}
	    }

	} catch (Exception e) {
	    LOGGER.error(
		    "Issue with Extending dut box alloction - " + device.getHostMacAddress() + "." + e.getMessage());
	}
    }

    private void extendAllocationForDevice(Dut dut) {

	if (((Device) dut).isLocked()) {
	    DeviceManager.getInstance().updateLockTime(dut, AutomaticsConstants.SETTOP_DEFAULT_LOCK_TIME);
	}
    }

    /**
     * Method to capture the automation script execution time (start and end) and send to Automatics.
     * 
     * @param dut
     * @param automationId
     * @param isTestStart
     */
    private void captureAutomationScriptExecutionTime(Dut dut, String automationId, boolean isTestStart) {

	JSONObject capturedJsonData = new JSONObject();

	String targetUrl = null;
	String jmdId = System.getProperty(AutomaticsConstants.JOB_MANAGER_DETAILS_ID, "0");
	Calendar currentDateTimeEST = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
	RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(AutomaticsConstants.THIRTY_SECONDS_INT)
		.setConnectionRequestTimeout(3 * AutomaticsConstants.ONE_MINUTE_INT).build();
	HttpClient client = null;
	HttpPost post = null;
	HttpEntity entity = null;
	HttpResponse responseFromAutomatics = null;
	StatusLine statusLine = null;

	try {

	    if (CommonMethods.isNull(jmdId)) {
		jmdId = "0";
	    }

	    capturedJsonData.put("jobId", Integer.parseInt(jmdId));
	    capturedJsonData.put("automationId", automationId);
	    capturedJsonData.put("macAddress", dut.getHostMacAddress());

	    if (isTestStart) {
		capturedJsonData.put("startDateTimeEST", currentDateTimeEST.getTimeInMillis());
		capturedJsonData.put("endDateTimeEST", 0);
	    } else {
		capturedJsonData.put("startDateTimeEST", 0);
		capturedJsonData.put("endDateTimeEST", currentDateTimeEST.getTimeInMillis());

		capturedJsonData.put("deviceExecutionLogUrl", ((Device) dut).getDeviceExecutionLogUrl());
	    }

	    targetUrl = AutomaticsTapApi.getSTBPropsValue("automatics.url") + "captureTestTriggerTime.htm";

	    LOGGER.info(targetUrl);
	    LOGGER.info(capturedJsonData.toString());

	    // creating a post request.
	    client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
	    post = new HttpPost(targetUrl);

	    // setting json object to post request.
	    entity = new StringEntity(capturedJsonData.toString());

	    post.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	    post.setEntity(entity);

	    // this is your response:
	    responseFromAutomatics = client.execute(post);

	    statusLine = responseFromAutomatics.getStatusLine();

	    LOGGER.info("Capture execution time - Response : " + statusLine);

	} catch (JSONException jsonException) {
	    LOGGER.error("Capture execution time - Json exception ", jsonException);
	} catch (ClientProtocolException cpex) {
	    LOGGER.error("Capture execution time - Client protocol exception occured ", cpex);
	} catch (IOException ioex) {
	    LOGGER.error("Capture execution time - IO exception occured ", ioex);
	}
    }

    /**
     * Method to copy logs to specified test folder after each test. For easiness of analysis
     * 
     * @param testDetailsAnnotation
     */
    private void copyLogsToUidRelatedFolders(String testUID, Dut dut, ITestResult testResult) {

	// Copy trace log
	String outputDirTrace = getLogRedirectionFolder(dut, testUID, true);
	copyFileAndRemoveUnwantedContent(testUID, true, outputDirTrace, dut, testResult, false);

	// Copy other execution log
	String outputDirLog = getLogRedirectionFolder(dut, testUID, false);
	copyFileAndRemoveUnwantedContent(testUID, false, outputDirLog, dut, testResult, true);

    }

    /**
     * Get the image redirection directory.
     * 
     * @param dut
     *            The dut instance
     * @param testUId
     *            The currently executing test id.
     * @param isImageCompare
     *            True if the image location is for IC, else OCR.
     * 
     * @return name of redirection folder.
     */
    private static String getLogRedirectionFolder(Dut dut, String testUId, boolean isTraceLog) {
	String logFolder = isTraceLog ? TraceProviderConstants.SETTOP_TRACE_DIRECTORY_NAME : ReportsConstants.LOG_DIR;
	String logSaveLocation = System.getProperty(ReportsConstants.USR_DIR) + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + testUId
		+ AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsUtils.getCleanMac(dut.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR
			+ logFolder.replace("/", "") + AutomaticsConstants.PATH_SEPARATOR);

	LOGGER.info("NewLocation for log saving logs  " + logSaveLocation);

	return logSaveLocation;
    }

    /**
     * Method to copy full log from the location and save in test specific folder.
     * 
     * @param testDetailsAnnotation
     */
    private void copyFileAndRemoveUnwantedContent(String testUID, boolean isSettopTrace, String destinationDirectory,
	    Dut dut, ITestResult testResult, boolean isLog) {

	if (isSettopTrace && NonRackUtils.disableSettopTrace()) {
	    return;
	}

	File destinationDir = new File(destinationDirectory);
	String iterationCount = String.valueOf(((Device) dut).getTestSessionDetails().getIteration());

	File summaryLogFile = null;
	String logFileName = null;
	String summarFileName = null;
	StringBuilder sb = new StringBuilder();

	if (!isSettopTrace) {
	    logFileName = testUID + ReportsConstants.LOG_EXTN;
	    summarFileName = testUID + "_Summary_Log" + ReportsConstants.LOG_EXTN;
	    sb.append(AutomaticsConstants.SETTOP_LOG_DIRECTORY).append(dut.getModel())
		    .append(AutomaticsConstants.HYPHEN).append(AutomaticsUtils.getCleanMac(dut.getHostMacAddress()))
		    .append(ReportsConstants.LOG_EXTN);
	}

	if (isSettopTrace) {
	    logFileName = testUID + ReportsConstants.LOG_EXTN;
	}
	if (!destinationDir.isDirectory()) {
	    LOGGER.info("created a new directory " + destinationDir.mkdirs());
	}
	// .log filename
	File outputFile = new File(destinationDir, logFileName);

	if (!outputFile.exists()) {

	    try {
		destinationDir.createNewFile();

	    } catch (IOException e) {
		LOGGER.error("Exception configuring tests", e);
	    }
	}
	// SummaryLogsFIleName
	if (isLog) {
	    summaryLogFile = new File(destinationDir, summarFileName);
	}
	if (isLog && !summaryLogFile.exists()) {

	    try {
		destinationDir.createNewFile();
		LOGGER.info("Destination Summary file doesn't exist. Creating one!");
	    } catch (IOException e) {
		LOGGER.error("Exception configuring tests", e);
	    }
	}

	if (isSettopTrace) {

	    sb.append(TraceProviderConstants.SETTOP_TRACE_DIRECTORY)
		    .append(AutomaticsUtils.getCleanMac(dut.getHostMacAddress())).append("settop_trace")
		    .append(ReportsConstants.LOG_EXTN);
	    LOGGER.info("New trace location from the strings " + sb.toString());
	}

	File sourceDir = new File(sb.toString());
	if (sourceDir.exists()) {
	    BufferedWriter writer = null;
	    BufferedReader reader = null;
	    BufferedWriter summaryWriter = null;
	    boolean starteWriting = false;

	    try {

		reader = new BufferedReader(new FileReader(sourceDir));
		writer = new BufferedWriter(new FileWriter(outputFile));

		if (isLog) {
		    summaryWriter = new BufferedWriter(new FileWriter(summaryLogFile));
		}

		String currentLine = null;
		String logStart = "<a name=\"" + testResult.getTestClass().getName() + "." + testResult.getName();

		while ((currentLine = reader.readLine()) != null) {

		    if (currentLine.contains(logStart)) {
			starteWriting = true;
			LOGGER.info("Writing stated from line");
		    }

		    if (starteWriting) {

			writer.write(currentLine);
			writer.write(System.getProperty("line.separator"));
			if (isLog) {
			    if (!(currentLine.contains("com.automatics."))) {
				summaryWriter.write(currentLine);
				summaryWriter.write(System.getProperty("line.separator"));
			    }
			}
		    }
		}
	    } catch (Exception e) {
		LOGGER.info("Exception while writing in copyFileAndRemoveUnwantedContent", e);

	    } finally {

		try {
		    if (writer != null) {
			writer.close();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		    LOGGER.info("Failed to close the writer stream" + e);
		}
		if (isLog) {
		    try {
			if (summaryWriter != null) {
			    summaryWriter.close();
			}
		    } catch (IOException e) {

			LOGGER.error("Failed to close the writer stream", e);
		    }
		}

		try {
		    if (reader != null) {
			reader.close();
		    }
		} catch (IOException e) {

		    LOGGER.error("Failed to close the reader stream", e);
		}

	    }

	    if (isHtmlLoggingEnabled() && isLog) {
		(new HtmlLogGenerator()).parseAndGenerateHTMLLog(testUID, dut.getHostMacAddress(),
			outputFile.getAbsolutePath(), destinationDirectory, iterationCount, dut.getFirmwareVersion());
	    }
	}
    }

    /**
     * Check if need to enable/disable html logging.
     * 
     * Default value will be enabled. Need to disable HTML parsing only if enableHtmlLog="N" or enableHtmlLog="false"
     * 
     * @return true if need to enable html log, false else
     * 
     * 
     */
    private static boolean isHtmlLoggingEnabled() {

	boolean isHtmlLogEnabled = true;

	final String valueHtmlLog = System.getProperty(SYSTEM_PROPS_ENABLE_HTML_LOG, "Y");

	if (valueHtmlLog.equalsIgnoreCase("N") || valueHtmlLog.contains("false")) {
	    isHtmlLogEnabled = false;
	}

	return isHtmlLogEnabled;
    }

    /**
     * Start device trace
     * 
     * @param device
     */
    private void startDeviceTrace(Dut device) {

	try {
	    TraceProvider traceProvider = device.getTrace();
	    if (null != traceProvider) {
		LOGGER.info(">>>[BEFORE_SUITE]: Starting device connection trace");
		traceProvider.startTrace();
		traceProvider.startBuffering();
	    }
	} catch (Exception e) {
	    LOGGER.error("Error starting trace");
	}

	// Start serial trace
	try {
	    TraceProvider traceProvider = device.getSerialTrace();
	    if (null != traceProvider) {
		LOGGER.info(">>>[BEFORE_SUITE]: Starting device connection trace");
		traceProvider.startTrace();
		traceProvider.startBuffering();
	    }
	} catch (Exception e) {
	    LOGGER.error("Error starting trace");
	}
    }

    /**
     * Perform AV check
     * 
     * @param device
     * @return
     */
    private boolean performAVCheck(Dut device, boolean isBeforeTestCheck) {
	if (isBeforeTestCheck) {
	    LOGGER.info(">>>[BEFORE_SUITE]: Starting AV check");
	} else {
	    LOGGER.info(">>>[AFTER_SUITE]: Starting AV check");
	}

	// Resetting AV status as false
	boolean isAvPresent = false;
	if (!TestType.isQt(AutomaticsTestBase.testType.name())) {

	    LOGGER.info("INIT-{} Going to perform AV check for device", device.getHostMacAddress());

	    // Validate AV status
	    isAvPresent = tapEnv.validateAV(device);
	    LOGGER.info("Status of AV {}", isAvPresent);

	    if (!isAvPresent) {
		LOGGER.error(
			"PRECONDITION#Unable to verify AV status as good for the STB " + device.getHostMacAddress());

		if (isBeforeTestCheck) {
		    AutomaticsTestBase.updateJobStatus(device.getHostMacAddress(), JobStatusValue.NO_AV_BEFORE_TEST);
		} else {
		    AutomaticsTestBase.updateJobStatus(device.getHostMacAddress(), JobStatusValue.NO_AV_AFTER_TEST);
		}
		LOGGER.info("Capturing and Saving Image");
		tapEnv.captureAndSaveImage(device, AutomaticsConstants.IMAGENAME_BEFORE_TEST, true);

		LOGGER.info("Capturing and Saving Image");
		tapEnv.captureAndSaveImage(device, AutomaticsConstants.IMAGENAME_BEFORE_TEST, true);

	    }

	} else {
	    LOGGER.info("AV check not needed for {}", AutomaticsTestBase.testType.name());
	    isAvPresent = true;
	}
	return isAvPresent;
    }

}
