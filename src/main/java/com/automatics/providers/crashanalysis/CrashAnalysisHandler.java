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
package com.automatics.providers.crashanalysis;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LoggingConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.device.Device;
import com.automatics.enums.IssueCreationRequestor;
import com.automatics.providers.issuemanagement.IssueController;
import com.automatics.providers.issuemanagement.IssueCreateTicketRequest;
import com.automatics.providers.issuemanagement.IssueManagementProvider;
import com.automatics.providers.issuemanagement.objects.Jaws.Issues.IssueDetails.LabelsList;
import com.automatics.providers.issuemanagement.objects.Jaws.Issues.IssueDetails.SearchLabelsList;
import com.automatics.providers.issuemanagement.objects.Jaws.Issues.IssueDetails.WatcherList;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.CrashUtils;
import com.automatics.utils.FrameworkHelperUtils;
import com.automatics.utils.TestUtils;

/**
 * Holds all methods related to crash analysis , performed after test case execution
 *
 */
public class CrashAnalysisHandler implements Runnable {

    /** Date format in crash data provided by crash portal **/
    private static final String CRASH_PORTAL_DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    /** Date format in test logs **/
    private static final String TEST_LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    private static final String FOLDER_NAME = "Crash-Analysis";

    /** List of crashes found **/
    private List<CrashDetails> crashList = new ArrayList<CrashDetails>();

    /** Device **/
    private Device device = null;

    /** test case being processed **/
    private String testCaseId = null;

    static volatile HashMap<String, HashMap<String, Integer>> crashMap = new HashMap<String, HashMap<String, Integer>>();

    /** Request being processed **/
    private CrashPortalRequest crashPortalRequest = null;

    private CrashAnalysisProvider crashAnalysisProvider = null;

    private IssueManagementProvider issueManagementProvider = null;

    private IssueController issueController = null;

    private String crashLogLine = AutomaticsConstants.EMPTY_STRING;

    private Logger crashAnalysisLogger = null;

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrashAnalysisHandler.class);

    /** LOG FILE LOCATION **/
    static final String logFile = System.getProperty("user.dir") + AutomaticsConstants.PATH_SEPARATOR + "target"
	    + AutomaticsConstants.PATH_SEPARATOR;

    /** Crash portal login cookie **/
    Map<String, String> cookie = null;

    static int crashesFailed = 0;

    public CrashAnalysisHandler(CrashPortalRequest requestObject, String logLine) {
	this.crashPortalRequest = requestObject;
	this.crashLogLine = logLine;

	// Crash analysis provider
	crashAnalysisProvider = BeanUtils.getCrashAnalysisProvider();

	Device dut = (Device) requestObject.getDut();

	// Initialize issue management
	if (TestUtils.isAutomatedIssueManagementEnabled()) {
	    issueManagementProvider = BeanUtils.getIssueManagementProvider();
	    if (null == issueManagementProvider) {
		LOGGER.error("Issue management provider not initialized");
	    }

	    issueController = new IssueController();
	}
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     * 
     *      Thread which performs crash analysis. Request crashes from portal and process them. The details will be
     *      added as additional step sfinal in Automatics.
     */
    public void run() {
	Thread.currentThread()
		.setName("CrashAnalysis-" + crashPortalRequest.getTestCaseId() + "-"
			+ AutomaticsUtils.getCleanMac(crashPortalRequest.getSettop().getHostMacAddress()) + "-THREAD_"
			+ Thread.currentThread().getId());

	try {
	    device = (Device) crashPortalRequest.getSettop();
	    testCaseId = crashPortalRequest.getTestCaseId();

	    String logFileName = logFile + crashPortalRequest.getTestCaseId() + AutomaticsConstants.PATH_SEPARATOR;
	    logFileName = logFileName + AutomaticsUtils.getCleanMac(device.getHostMacAddress())
		    + AutomaticsConstants.PATH_SEPARATOR;

	    File directory = new File(logFileName + FOLDER_NAME);
	    if (!directory.exists()) {
		directory.mkdirs();
	    }

	    LOGGER.info("Configuring loggers.................");
	    System.setProperty(LoggingConstants.CRASH_ANALYSIS_FILE_PATH, logFileName);
	    MDC.put(LoggingConstants.CRASH_ANALYSIS_FILE_PATH, logFileName);
	    MDC.put(LoggingConstants.LOGGER_DEVICE_MAC_KEY,
		    AutomaticsUtils.getCleanMac(crashPortalRequest.getSettop().getHostMacAddress()));

	    crashAnalysisLogger = LoggerFactory.getLogger("crash-analysis");
	    crashAnalysisLogger.info("Inside startCrashAnalysis ------->");

	    List<CrashDetails> crashDetailsMinidumps = new ArrayList<CrashDetails>();
	    List<CrashDetails> crashDetailsCoredumps = new ArrayList<CrashDetails>();
	    Integer currentVal = 0;

	    String imagename = crashPortalRequest.getImagename().replaceAll("-signed.*",
		    AutomaticsConstants.EMPTY_STRING);
	    crashPortalRequest.setImagename(imagename);

	    LOGGER.info("Starting Crash Analysis with request \n{}", crashPortalRequest.toString());

	    boolean isProcessingStartedCore = false;
	    boolean isProcessingStartedMinidump = false;

	    if (crashLogLine.contains("minidump")) {
		isProcessingStartedMinidump = crashAnalysisProvider.sendMiniDumpProcessRequest(crashPortalRequest);
		crashAnalysisLogger.info("Status of processing mini dumps " + isProcessingStartedMinidump);
		AutomaticsUtils.sleep(AutomaticsConstants.ONE_SECOND);
	    } else if (crashLogLine.contains("coredump")) {
		crashAnalysisLogger.info("Starting prcessing core dumps if any");
		isProcessingStartedCore = crashAnalysisProvider.sendCoreDumpProcessRequest(crashPortalRequest);
		crashAnalysisLogger.info("Status of processing core dumps " + isProcessingStartedCore);
	    }
	    if (isProcessingStartedMinidump || isProcessingStartedCore) {
		int iterator = 1;

		// Setting crash request start time
		String startTime = crashPortalRequest.addTime(-1, crashPortalRequest.getStartTime());
		crashPortalRequest.setStartTime(startTime);

		while (iterator < 6) {
		    crashAnalysisLogger.info("ITERATION COUNT : " + iterator + ",sleeping for one minute");
		    Thread.sleep(AutomaticsConstants.ONE_MINUTE);

		    String endTime = crashPortalRequest.addTime(1, crashPortalRequest.getEndTime());
		    crashPortalRequest.setEndTime(endTime);

		    if (isProcessingStartedMinidump) {
			crashAnalysisLogger.info("Fetching details of minidump between " + startTime + " - " + endTime);
			crashDetailsMinidumps = crashAnalysisProvider.getMiniDumpData(crashPortalRequest);
		    }
		    if (isProcessingStartedCore) {
			crashAnalysisLogger.info("Fetching details of core between " + startTime + " - " + endTime);
			crashDetailsCoredumps = crashAnalysisProvider.getCoreDumpData(crashPortalRequest);
		    }

		    crashAnalysisLogger
			    .info("Total number of coredumps processed -------> " + crashDetailsCoredumps.size());
		    crashAnalysisLogger
			    .info("Total number of minidumps processed -------> " + crashDetailsMinidumps.size());

		    crashList.addAll(crashDetailsCoredumps);
		    crashList.addAll(crashDetailsMinidumps);
		    if (crashList.size() == 0) {
			crashAnalysisLogger.info("No crashes observed");
			iterator++;
		    } else {
			crashAnalysisLogger.info("Total Crashes observed " + crashList.size());
			if (crashMap.containsKey(device.getHostMacAddress())) {
			    HashMap<String, Integer> testIdCrashCoutMap = crashMap.get(device.getHostMacAddress());
			    currentVal = testIdCrashCoutMap.get(testCaseId);
			}
			for (int iteration = 0; iteration < crashList.size(); iteration++) {
			    String stepToBeAddedInAutomatics = "s-ca_" + String.valueOf(currentVal + iteration + 1);
			    CrashDetails crash = crashList.get(iteration);
			    crashAnalysisLogger.info("Dowloading and creating ticket for " + stepToBeAddedInAutomatics
				    + " : Details \n " + crash.toString());

			    // Adding step for crash in Automatics
			    if (addPostStepInAutomatics(stepToBeAddedInAutomatics, false)) {
				downloadCrash(crash);
				if (TestUtils.isAutomatedIssueManagementEnabled()) {
				    createTicket(crash, stepToBeAddedInAutomatics);
				}
			    }
			}
			crashAnalysisLogger.info("\nXXX -------------  Crash Analysis Completed ------------- XXX");
			break;
		    }
		}
		synchronized (device) {
		    HashMap<String, Integer> TestCaseCrashCountMap = new HashMap<String, Integer>();
		    TestCaseCrashCountMap.put(testCaseId, currentVal + crashList.size());
		    crashMap.put(device.getHostMacAddress(), TestCaseCrashCountMap);
		}
	    } else {
		LOGGER.error("Processing of cores was not successful .Creating ticket in response to failure");
		crashesFailed++;

		addPostStepInAutomatics("ca_fail_" + crashesFailed, true);

		createTicketOnFailure();

	    }
	} catch (Exception e) {
	    crashAnalysisLogger.error("Exception during thread execution : ", e);
	} finally {
	    MDC.clear();
	}

    }

    /**
     * Method downloads the crash from crash portal
     */
    private void downloadCrash(CrashDetails crash) {
	crashAnalysisLogger.debug("Inside downloadCrash------->");
	try {
	    String workspaceLocation = System.getProperty("WORKSPACE");
	    LOGGER.info("WORKSPACE - {}", workspaceLocation);
	    boolean shouldProceedToDownload = false;
	    if (CommonMethods.isNotNull(workspaceLocation)) {
		String defaultworkspaceName = workspaceLocation
			.substring(workspaceLocation.lastIndexOf(File.separator) + 1);
		if ("workspace".equals(defaultworkspaceName)) {
		    shouldProceedToDownload = true;
		}
	    }
	    LOGGER.info("Proceed to download dump - {}", shouldProceedToDownload);
	    if (shouldProceedToDownload) {
		String minidumpDownloadLocationInWorkspace = System.getProperty(ReportsConstants.USR_DIR)
			+ AutomaticsConstants.PATH_SEPARATOR + AutomaticsConstants.TARGET_FOLDER
			+ AutomaticsConstants.PATH_SEPARATOR + testCaseId + AutomaticsConstants.PATH_SEPARATOR
			+ AutomaticsUtils.getCleanMac(device.getHostMacAddress() + AutomaticsConstants.PATH_SEPARATOR
				+ FOLDER_NAME + AutomaticsConstants.PATH_SEPARATOR);

		boolean isDirectoryCreated = createDumpDirectory(minidumpDownloadLocationInWorkspace);
		if (isDirectoryCreated) {
		    CrashType crashType = CrashUtils.getCrashType(crash);
		    LOGGER.info("Downloading {} for id {}", crashType, crash.getId());
		    boolean isSuccess = crashAnalysisProvider.downloadDump(crashType, crash.getId(),
			    minidumpDownloadLocationInWorkspace);
		    LOGGER.info("Download status {}", isSuccess);
		    if (CrashUtils.getCrashType(crash).equals(CrashType.MINIDUMP)) {
			LOGGER.info("Downloading compressed files");
			crashAnalysisProvider.downloadCompressedDumpFiles(crashType, crash.getId(),
				minidumpDownloadLocationInWorkspace);
		    }
		    if (!isSuccess) {
			crashAnalysisLogger.info("## -----> Dump file could not be downloaded<----- ##");
		    }
		}
	    }
	} catch (Exception e) {
	    crashAnalysisLogger.error("Exception occurred ", e);
	}

	crashAnalysisLogger.info("Exiting downloadCrash");
    }

    /**
     * Method to create the directory where dump/corefiles are stored in jenkins server
     * 
     * @param minidumpDownloadLocationInWorkspace
     *            Location to be created
     * @return Status of operation
     */
    private boolean createDumpDirectory(String minidumpDownloadLocationInWorkspace) {
	boolean isDirectoryCreated = true;
	crashAnalysisLogger.info("Downloading crash to " + minidumpDownloadLocationInWorkspace);
	File dumpDownloadDirectory = new File(minidumpDownloadLocationInWorkspace);

	if (!dumpDownloadDirectory.isDirectory()) {
	    isDirectoryCreated = dumpDownloadDirectory.mkdirs();
	}
	return isDirectoryCreated;
    }

    /**
     * Creating Issue ticket
     * 
     * @param crash
     *            CrashDetails
     * @param stepNumber
     *            Step for which crash observed
     */
    private void createTicket(CrashDetails crash, String stepNumber) {

	try {
	    IssueCreateTicketRequest request = new IssueCreateTicketRequest(IssueCreationRequestor.CRASH_ANALYSIS);
	    request.issueDetails.setIssueSummary(getSummary(crash));
	    request.issueDetails.setIssueDescription(getDescription(crash));
	    request.issueDetails.setLabelsList(getLabelList(crash));
	    request.issueDetails.setPriority("P2");
	    request.issueDetails.setWatcherList(getWatcherList(crash));
	    request.issueDetails.setSearchList(getSearchList(crash));
	    request.issueDetails.setBuildName(device.getFirmwareVersion());
	    request.issueDetails.setManualId("_POSTCONDITION ");
	    request.issueDetails.setStepNumber(stepNumber);
	    request.issueDetails.setAutomationId(testCaseId);
	    request.issueDetails.setJenkinsUrl(System.getProperty("JOB_URL") + System.getProperty("BUILD_NUMBER"));
	    request.issueDetails.setBuildAppender(TestUtils.getBuildAppender());
	    request.issueDetails.setEnvironmentType(TestUtils.getExecutionModeName());
	    String jmid = System.getProperty("JMD_ID");
	    if (CommonMethods.isNotNull(jmid)) {
		request.issueDetails.setJobId(Long.parseLong(jmid));
	    }
	    request.issueDetails.setAttachments(getAttachments(crash));

	    String issueTicket = issueController.createIssueTicket(request);
	    crashAnalysisLogger.info("The Issue Tickets created/updated are  ------->" + issueTicket);
	    LOGGER.info("The Issue Tickets created/updated are  ------->" + issueTicket);

	} catch (Exception e) {
	    crashAnalysisLogger.error("Error creating issue ticket", e);
	}
    }

    /**
     * 
     * Creates the list of search strings to be included in tickets.
     * 
     * @return search list
     */
    private List<String> getSearchList(CrashDetails crash) {
	SearchLabelsList searchList = new SearchLabelsList();
	List<String> searchKeys = null;
	if (null != issueManagementProvider) {
	    searchKeys = issueManagementProvider.getSearchList(IssueCreationRequestor.CRASH_ANALYSIS, device, crash);
	}
	if (null == searchKeys) {
	    searchKeys = new ArrayList<String>();
	}
	searchKeys.add(testCaseId);
	searchKeys.add(device.getFirmwareVersion());

	for (String searchKey : searchKeys) {
	    searchList.getSearch().add(searchKey);
	}
	return searchList.getSearch();
    }

    /**
     * 
     * Creates the list of Label to be included in tickets
     * 
     * @return label list
     */
    private List<String> getLabelList(CrashDetails eachCrash) {
	LabelsList labelList = new LabelsList();
	List<String> labels = null;
	if (eachCrash != null) {
	    if (null != issueManagementProvider) {
		labels = issueManagementProvider.getLabels(IssueCreationRequestor.CRASH_ANALYSIS, device, eachCrash);
	    }
	    if (null == labels) {
		labels = new ArrayList<String>();
	    }
	    labels.add(testCaseId);

	    for (String eachLabel : labels) {
		labelList.getLabelName().add(eachLabel);
	    }
	}

	return labelList.getLabelName();
    }

    /**
     * 
     * Creates the description to be added in comment
     * 
     * @param eachCrash
     *            Crash data object
     * @return Descripton is returned
     */
    private String getDescription(CrashDetails eachCrash) {
	StringBuffer description = new StringBuffer();
	String buildUrl = System.getProperty("BUILD_URL") + "/consoleFull";
	description
		.append(CrashUtils.DESCRIPTION_TEMPLATE.replace("<step>", getTestStepOfCrash(eachCrash))
			.replace("<testcaseid>", testCaseId).replace("<buildurl>", buildUrl)
			.replace("<details>", getDeviceDetails(device)))
		.append("*+Crash Details+*").append(AutomaticsConstants.NEW_LINE).append(AutomaticsConstants.HYPHEN)
		.append(AutomaticsConstants.SINGLE_SPACE_CHARACTER).append("DEVICE MAC : ").append(eachCrash.getMac())
		.append(AutomaticsConstants.NEW_LINE).append(AutomaticsConstants.HYPHEN)
		.append(AutomaticsConstants.SINGLE_SPACE_CHARACTER).append("DEVICE MODEL : ")
		.append(eachCrash.getDeviceModel()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER)
		.append("FIRMWARE VERSION : ").append(eachCrash.getVersion()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER)
		.append("APP CRASHED : ").append(eachCrash.getApp()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER)
		.append("FINGERPRINT : ").append(eachCrash.getSignature()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER)
		.append("DATE CRASHED : ").append(eachCrash.getDateCrashed()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER)
		.append("FAILURE REASON : ").append(eachCrash.getFailedReason()).append(AutomaticsConstants.NEW_LINE)
		.append(AutomaticsConstants.HYPHEN).append(AutomaticsConstants.SINGLE_SPACE_CHARACTER).append("FILE : ")
		.append(eachCrash.getFilename()).append(AutomaticsConstants.NEW_LINE).append(AutomaticsConstants.HYPHEN)
		.append(AutomaticsConstants.SINGLE_SPACE_CHARACTER);

	if (CrashUtils.getCrashType(eachCrash).equals(CrashType.MINIDUMP)
		&& CommonMethods.isNotNull(eachCrash.getStackTrace())) {
	    description.append(AutomaticsConstants.NEW_LINE).append(AutomaticsConstants.NEW_LINE)
		    .append("*+STACKTRACE*+").append(AutomaticsConstants.NEW_LINE)
		    .append(CrashUtils.STACKTRACE_TEMPLATE.replace("<stacktrace>", eachCrash.getStackTrace()));
	}
	crashAnalysisLogger.info("Description : \n" + String.valueOf(description));
	return String.valueOf(description);
    }

    /**
     * Gets device details to be included in automated issueTicket
     * 
     * @param device
     * @return Device details
     */
    private String getDeviceDetails(Device device) {
	String details = AutomaticsConstants.EMPTY_STRING;
	if (null != issueManagementProvider) {
	    issueManagementProvider.getDeviceDetails(IssueCreationRequestor.CRASH_ANALYSIS, device);
	    crashAnalysisLogger.info("Device Details:" + details);
	}
	return details;
    }

    /**
     * Get step details for which crash occurred
     * 
     * @param crashdetails
     * @return Step details for which crash occurred
     */
    private String getTestStepOfCrash(CrashDetails crashdetails) {
	String stepNumber = "A STEP WHICH COULD NOT BE DETERMINED";
	crashAnalysisLogger.info(crashdetails.getDateCrashed().toString());
	// Fri May 17 14:10:44 UTC 2019 --> Sample format
	String testLogFormat = "yyyy-MM-dd hh:mm:ss,SSS"; // 2019-08-08 07:00:19,029
	List<String> executionsteps = fetchStepExecutionTimeFromLogs();
	crashAnalysisLogger.info("List of steps =" + executionsteps.size());
	if (executionsteps.size() == 0) {
	    stepNumber = "first step ";
	} else {
	    for (String eachStep : executionsteps) {
		LOGGER.info(eachStep);
		String stepExecutionEndTime = eachStep.split("=")[0];
		boolean isFound = compareTimeStampAndVerifySTep(testLogFormat,
			String.valueOf(crashdetails.getDateCrashed()), stepExecutionEndTime);
		if (isFound) {
		    LOGGER.info("Found crash step ");
		    stepNumber = eachStep.split("=")[1];
		    break;
		} else {
		    if (!isFound && executionsteps.indexOf(eachStep) == executionsteps.size() - 1) {
			stepNumber = " the next step after completing step number " + eachStep.split("=")[1];
		    }
		}
	    }
	}
	crashAnalysisLogger.info("Returning step number = " + stepNumber);
	return stepNumber;
    }

    /**
     * @param testLogFormat
     * @param dateCrashed
     * @param stepExecutionTime
     * @return
     */
    private boolean compareTimeStampAndVerifySTep(String testLogFormat, String dateCrashed, String stepExecutionTime) {
	boolean isValid = false;
	SimpleDateFormat formatter = new SimpleDateFormat(CRASH_PORTAL_DATE_FORMAT);
	SimpleDateFormat formatter1 = new SimpleDateFormat(TEST_LOG_DATE_FORMAT);
	try {
	    Date dateCrashedTime = formatter.parse(dateCrashed);
	    crashAnalysisLogger.info("dateCrashedTime = " + dateCrashedTime);

	    Date executionTime = formatter1.parse(stepExecutionTime);
	    crashAnalysisLogger.info("executionTime = " + executionTime);
	    if (executionTime.getTime() - dateCrashedTime.getTime() > 0) {
		isValid = true;
	    }
	} catch (ParseException e) {
	    LOGGER.error("Error parsing date", e);
	}
	return isValid;
    }

    /**
     * Creates attachments list o each crash
     * 
     * @param eachCrash
     * @return List of attachments
     */
    private List<String> getAttachments(CrashDetails eachCrash) {
	String crashLogBaseFolder = System.getProperty("JOB_URL") + "ws/target/" + testCaseId + "/"
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress()) + "/" + FOLDER_NAME + "/";

	crashAnalysisLogger.info("Fetching attachments--------------");
	List<String> attachments = null;
	if (null != issueManagementProvider) {
	    attachments = issueManagementProvider.getUrlsForAttachment(IssueCreationRequestor.CRASH_ANALYSIS,
		    crashLogBaseFolder, device, eachCrash);
	}
	if (null == attachments) {
	    attachments = new ArrayList<String>();
	}
	crashAnalysisLogger.debug("Attachments :" + attachments);
	return attachments;
    }

    /**
     * 
     * Creates the list of watcher to be included in tickets
     * 
     * @return Watcher list
     */
    private List<String> getWatcherList(CrashDetails crash) {
	WatcherList watcherList = new WatcherList();
	List<String> watchers = null;
	if (null != issueManagementProvider) {
	    watchers = issueManagementProvider.getWatcherList(IssueCreationRequestor.CRASH_ANALYSIS, device, crash);
	}
	if (null != watchers) {
	    for (String watcher : watchers) {
		watcherList.getWatcher().add(watcher);
	    }
	}
	return watcherList.getWatcher();
    }

    /**
     * 
     * Creates the Summary to be added in ticket
     * 
     * @param eachCrash
     *            Crash data object
     * @return Summary is returned
     */
    private String getSummary(CrashDetails eachCrash) {
	StringBuffer summary = new StringBuffer();
	String branch = FrameworkHelperUtils.getFirmwareVersionBranch(device);
	summary.append(CrashUtils.SUMMARY_TEMPLATE.replace("<model>", eachCrash.getDeviceModel())
		.replace("<branch>", branch).replace("<app>", eachCrash.getApp())
		.replace("<signature>", eachCrash.getSignature()).replace("<reason>", eachCrash.getFailedReason()));
	crashAnalysisLogger.info("Summary : " + String.valueOf(summary));
	return summary.toString();
    }

    /**
     * 
     * Method calls Automatics API to dynamically create a step in Test case for updating crash results
     * 
     * @return Status of operation is returned
     */
    private boolean addPostStepInAutomatics(String step, boolean forceUpdate) {
	crashAnalysisLogger.debug("Inside addPostStepInAutomatics");
	boolean isSuccess = false;
	String executionStatus = "FAIL";
	if (!forceUpdate) {
	    executionStatus = crashList.size() > 0 ? "FAIL" : "PASS";
	}
	isSuccess = AutomaticsUtils.addPostStepInAutomatics(step, executionStatus, testCaseId, device,
		"post condition after test cases to check crashes", "No crashes after test",
		"Crashes will be queried from crash portal", "Box crashed during test");
	crashAnalysisLogger.debug("Exiting addPostStepInAutomatics with status " + isSuccess);
	return isSuccess;
    }

    private void createTicketOnFailure() {
	try {
	    IssueCreateTicketRequest request = new IssueCreateTicketRequest(IssueCreationRequestor.CRASH_ANALYSIS);
	    request.issueDetails
		    .setIssueSummary("[MISSING CRASH][AUTO][CRASH_PORTAL]Crash obtained during test execution");
	    request.issueDetails.setIssueDescription("Observed crash in box logs during test execution of " + testCaseId
		    + " , but was unable to fetch its details from crash portal.\n JOB URL : "
		    + System.getProperty("JOB_URL") + System.getProperty("BUILD_NUMBER") + "\nImagename : "
		    + device.getFirmwareVersion());
	    request.issueDetails.setLabelsList(getLabelList(null));
	    request.issueDetails.setPriority("P2");
	    request.issueDetails.setWatcherList(getWatcherList(null));
	    request.issueDetails.setSearchList(getSearchList(null));
	    request.issueDetails.setBuildName(device.getFirmwareVersion());
	    request.issueDetails.setManualId("_POSTCONDITION ");
	    request.issueDetails.setStepNumber("ca_fail_" + crashesFailed);
	    request.issueDetails.setAutomationId(testCaseId);
	    request.issueDetails.setJenkinsUrl(System.getProperty("JOB_URL") + System.getProperty("BUILD_NUMBER"));
	    request.issueDetails.setBuildAppender(TestUtils.getBuildAppender());
	    request.issueDetails.setEnvironmentType(TestUtils.getExecutionModeName());
	    String jmid = System.getProperty("JMD_ID");
	    if (CommonMethods.isNotNull(jmid)) {
		request.issueDetails.setJobId(Long.parseLong(jmid));
	    }

	    String issueTicket = issueController.createIssueTicket(request);
	    crashAnalysisLogger.info("The Issue Tickets created/updated are  ------->" + issueTicket);
	    LOGGER.info("Created/Updated ticket {} on crash analysis failure ", issueTicket);

	} catch (Exception e) {
	    crashAnalysisLogger.error("Exception occurred " + e.getMessage());
	}
    }

    /**
     * Return the given step execution time from log file
     * 
     * @return List holding time during which test execution is done
     */
    private List<String> fetchStepExecutionTimeFromLogs() {
	List<String> executionStep = new ArrayList<String>();
	String logSaveLocation = AutomaticsConstants.EMPTY_STRING;

	logSaveLocation = AutomaticsConstants.DOT + AutomaticsConstants.PATH_SEPARATOR
		+ AutomaticsConstants.TARGET_FOLDER + AutomaticsConstants.PATH_SEPARATOR + "logs"
		+ AutomaticsConstants.PATH_SEPARATOR + device.getModel() + AutomaticsConstants.HYPHEN
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress()) + ReportsConstants.LOG_EXTN;

	String commandToExecute = "grep -ih \"^\\[INFO.*" + testCaseId + " : "
		+ AutomaticsUtils.getCleanMac(device.getHostMacAddress()) + ".*step Number :.*\" " + logSaveLocation
		+ " |sed -r \"s/\\[INFO\\].([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}).*step Number : (s[0-9]\\.{0,1}[0-9]*).*/\\1=\\2/i\"";
	crashAnalysisLogger.debug("Executing command in server =" + commandToExecute);
	String response = CommonMethods.executeCommandInExecutionServer(commandToExecute);
	if (CommonMethods.isNotNull(response)) {
	    executionStep.addAll(Arrays.asList(response.split(AutomaticsConstants.NEW_LINE)));
	}
	return executionStep;
    }

}
