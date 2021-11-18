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

package com.automatics.providers.trace;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.TraceProviderConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.dataobjects.TestSessionDO;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.providers.connection.Connection;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.providers.crashanalysis.CrashAnalysisHandler;
import com.automatics.providers.crashanalysis.CrashAnalysisProvider;
import com.automatics.providers.crashanalysis.CrashPortalRequest;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.NonRackUtils;
import com.automatics.utils.TestUtils;

/**
 * DeviceConfig connection based trace provider. This implementation of provider contacts the dut via device connection
 * provider and reads the trace
 * 
 * @author naveenb
 * @author arjunp
 */
public abstract class AbstractTraceProviderImpl implements ConnectionTraceProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractTraceProviderImpl.class);

    protected final static int MAX_LOG_REPETIONS_ALLOWABLE = 100;

    protected volatile boolean loopAlive = false;
    protected volatile boolean disConnectFlag = false;
    protected volatile boolean keepPollingThreadAlive = true;
    protected volatile boolean keepPollingXg1ThreadAlive = true;
    protected boolean enableCrashAnalysis = false;

    protected String command = null;
    protected Dut dut;
    protected volatile boolean monitoringStarted = false;
    protected InputStream clientDeviceInputStream = null;
    protected InputStream gateWayDeviceInputStream = null;
    protected InputStream gateWayDeviceInputStreamAddlLogger = null;

    protected OutputStream gateWayDeviceOutputStream = null;
    protected OutputStream gateWayDeviceOutputStreamAddlLogger = null;
    protected OutputStream clientDeviceOutputStream = null;

    protected DeviceConnectionProvider connectionProvider;

    protected CrashAnalysisProvider crashAnalysisProvider;

    /** Connection to STB (Gateway) */
    protected Connection connectionGateway;
    protected Connection connectionGatewayAdditionalLogger = null;
    /** Connection to STB (client) */
    protected Connection connectionClient = null;

    protected ConnectionThread connectionThread;

    protected PollingThread pollingThread;

    protected volatile boolean isLostConnection = false;
    protected TraceServerConnectionStatus connectionStatus = TraceServerConnectionStatus.DISCONNECTED;

    protected Logger settopTraceLogger;

    protected AdditionalTraceLogger additionalTraceLogger;

    protected ConcurrentLinkedQueue<String> clQueue;

    protected int markOffset;

    protected boolean bufferTrace = false;

    protected volatile int reConnectCount = 0;

    protected volatile int reconnectAttempt = 500;

    protected String previousLog = null;

    protected int consecutiveDuplicateLogsReceived = 0;

    static final int FIlE_SIZE_LIMIT_KB = 204800; // 200MB

    protected long currentCrashTime = 0;

    /**
     * Get trace logger for device
     * 
     * @return trace logger for device
     */
    public Logger getSettopTraceLogger() {
	return settopTraceLogger;
    }

    /**
     * Gets trace connection status
     */
    @Override
    public String getTraceStatus() throws Exception {
	return connectionStatus.toString();
    }

    /**
     * 
     * Method to check if trace has started buffering
     * 
     * @return
     */
    public boolean isMonitoringStarted() {
	return monitoringStarted;
    }

    /**
     * Initialize trace for device
     */
    @Override
    public void initializeTraceForDevice(Dut device) {
	connectionProvider = BeanUtils.getDeviceConnetionProvider();
	this.dut = device;
	clQueue = new ConcurrentLinkedQueue<String>();
	setupDeviceTraceLogger();
	// Trace for ATOM console initialized based on config in Automatics Props
	additionalTraceLogger = new AdditionalTraceLogger(dut);
	LOGGER.debug("isenabled=" + additionalTraceLogger.isEnabled);

	enableCrashAnalysis = TestUtils.isCrashAnalysisEnabled();

	if (enableCrashAnalysis) {
	    crashAnalysisProvider = BeanUtils.getCrashAnalysisProvider();
	    LOGGER.info("Crash Analysis enable during trace monitoring");
	} else {
	    LOGGER.info("Crash Analysis not enabled during trace monitoring");
	}

    }

    /**
     * Starts trace logging for device
     */
    @Override
    public void startTrace() throws Exception {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	command = getTraceStartCommand(dut);
	LOGGER.info("Starting trace with command : {}", command);
	loopAlive = true;
	disConnectFlag = false;
	isLostConnection = false;

	// Clean up all existing trace processes before starting new trace
	cleanupDeviceTrace();

	java.util.concurrent.CountDownLatch synchronizationLatch = new CountDownLatch(1);
	connectionThread = new ConnectionThread(synchronizationLatch);
	connectionThread.setName("ConnectionThread_" + connectionThread.getId() + " (" + dut.getHostMacAddress() + ")");

	// Connecting to device for trace
	connectionThread.start();
	synchronizationLatch.await();
	/*
	 * Observed delay in starting connection thread. Synchronized with Count down latch and additional 30 seconds
	 * delay to start trace provider properly.
	 */
	AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);

	if (isTraceRequiredForConnectedGateway(dut)) {
	    AbstractTraceProviderImpl connectedGatewayTraceProvider = getGatewayTraceProvider(dut);
	    if (connectedGatewayTraceProvider != null && !connectedGatewayTraceProvider.loopAlive) {
		LOGGER.info("Starting Trace for connected gateway");
		connectedGatewayTraceProvider.startTrace();
		LOGGER.info("Completed starting trace for connected gateway");
	    }
	}
    }

    /**
     * Starts trace logging for device
     */
    public void startTrace(String ocapRiLog) throws Exception {
	this.startTrace();
    }

    /**
     * Stops trace
     */
    @Override
    public void stopTrace() throws Exception {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	LOGGER.debug("Stopping Trace Provider as stop tace called");
	keepPollingThreadAlive = false;
	keepPollingXg1ThreadAlive = false;
	disConnectFlag = true;
	loopAlive = false;

	// Clean up and stop all existing trace processes before starting new trace
	cleanupDeviceTrace();

	if (null != pollingThread) {
	    pollingThread.interrupt();
	}

	if (null != connectionThread) {
	    connectionThread.interrupt();
	}

	// Close stream and connection
	disconnect();
	clQueue.clear();
	monitoringStarted = false;
	if (isTraceRequiredForConnectedGateway(dut)) {
	    AbstractTraceProviderImpl provider = getGatewayTraceProvider(dut);
	    if (provider != null)
		provider.stopTrace();
	}

    }

    /**
     * Waits for given trace string
     */
    @Override
    public boolean waitForTraceString(String regEx, long timeoutInMilliseconds) throws IOException {

	boolean bufferStatusBackup = bufferTrace;
	bufferTrace = true;

	int markBackup = this.markOffset;
	this.markOffset = clQueue.size();

	String val = searchAndWaitForTrace(regEx, timeoutInMilliseconds, false, false, true);

	this.markOffset = markBackup;
	bufferTrace = bufferStatusBackup;

	return (null != val);
    }

    /**
     * Clears the buffer
     */
    @Override
    public void clearBuffer() {
	clQueue.clear();
    }

    /**
     * Initialize the trace logger for file writing
     */
    public void setupDeviceTraceLogger() {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}
	settopTraceLogger = Logger.getLogger(dut.getHostMacAddress());

	try {

	    StringBuilder sb = new StringBuilder();
	    sb.append(TraceProviderConstants.SETTOP_TRACE_DIRECTORY)
		    .append(AutomaticsUtils.getCleanMac(dut.getHostMacAddress()))
		    .append(TraceProviderConstants.TRACE_LOG_FILE_NAME);

	    // Setting trace logger file name
	    String logFileName = sb.toString();
	    LOGGER.info("Log File location " + logFileName);
	    FileAppender fileAppender = new FileAppender(TraceProviderConstants.LOG_PATTERN, logFileName);
	    fileAppender.setThreshold(Level.TRACE);
	    fileAppender.setAppend(true);
	    fileAppender.activateOptions();
	    settopTraceLogger.addAppender(fileAppender);
	    settopTraceLogger.setLevel(Level.TRACE);
	    settopTraceLogger.setAdditivity(false);
	} catch (IOException e) {
	    LOGGER.error("Unable to create the trace log appender for dut : " + dut.getHostMacAddress(), e);
	}
    }

    /**
     * Verify if trace connection is alive
     * 
     * @param device
     * @return true if trace connection is alive
     */
    public abstract boolean isTraceConnectionAlive(Device device);

    @Override
    public void startBuffering() {
	clQueue.clear();
	markOffset = clQueue.size();
	bufferTrace = true;
	if (isTraceRequiredForConnectedGateway(dut)) {
	    AbstractTraceProviderImpl gatewayTraceProvider = getGatewayTraceProvider(dut);
	    if (null != gatewayTraceProvider) {
		LOGGER.info("Start bufferring gateway trace for {}", gatewayTraceProvider.getDevice()
			.getHostMacAddress());
		gatewayTraceProvider.startBuffering();
	    } else {
		LOGGER.info("The gateway dut trace is empty, hence cannot start trace buffer!!");
	    }
	}
    }

    /**
     * Stops trace buffering
     */
    @Override
    public void stopBuffering() {
	bufferTrace = false;
	clQueue.clear();
	markOffset = clQueue.size();
	if (isTraceRequiredForConnectedGateway(dut)) {
	    AbstractTraceProviderImpl gatewayTraceProvider = getGatewayTraceProvider(dut);
	    if (null != gatewayTraceProvider) {
		LOGGER.info("Stop bufferring gateway trace for {}", gatewayTraceProvider.getDevice()
			.getHostMacAddress());
		gatewayTraceProvider.stopBuffering();
	    } else {
		LOGGER.info("The gateway dut trace is empty, hence cannot stop trace buffer!!");
	    }

	}
    }

    /**
     * Inserts data to trace output file
     */
    @Override
    public void insertIntoTrace(String textToInsert, Level logLevel) {
	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	settopTraceLogger.log(logLevel, textToInsert);
    }

    /**
     * Gets the last trace line
     */
    @Override
    public String getLastTraceLine() {

	String lastTrace = null;
	String[] queue_array = clQueue.toArray(new String[0]);

	if (queue_array != null) {
	    int length = queue_array.length;

	    lastTrace = length == 0 ? null : queue_array[length - 1];

	    if (length > 2) {
		lastTrace = lastTrace + queue_array[length - 2];
	    }
	}

	return lastTrace;
    }

    /**
     * Stops trace
     */
    public void stopTrace(boolean isConnectionThread) throws Exception {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.debug("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	LOGGER.debug("Stopping SSH Trace Provider as connection is closed notified by polling thread");
	keepPollingThreadAlive = false;
	keepPollingXg1ThreadAlive = false;
	isLostConnection = false;

	disConnectFlag = true;
	loopAlive = false;

	// Clean up all existing trace processes before starting new trace
	cleanupDeviceTrace();

	if (isConnectionThread && null != pollingThread) {
	    pollingThread.interrupt();
	} else if (!isConnectionThread && null != connectionThread) {
	    connectionThread.interrupt();
	}

	disconnect();
	clQueue.clear();
	monitoringStarted = false;
	if (isTraceRequiredForConnectedGateway(dut)) {
	    getGatewayTraceProvider(dut).stopTrace(isConnectionThread);
	}
    }

    /**
     * Stop trace if device goes down
     * 
     * @throws Exception
     */
    public void stopTraceOnConnectionLoss() throws Exception {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	LOGGER.info("Stopping Trace Provider as connection Loss");
	keepPollingThreadAlive = true;
	keepPollingXg1ThreadAlive = true;
	disConnectFlag = true;
	isLostConnection = true;

	if (null != connectionThread) {
	    connectionThread.interrupt();
	}

	disconnect();
	clQueue.clear();
    }

    /**
     * Search and wait for particular trace pattern.
     * 
     * @param regEx
     *            The regular expression for particular trace.
     * @param timeoutInMilliseconds
     *            The trace timeout in milliseconds
     * @param start
     *            The start marker
     * @param isFirstOccurrence Is first match to be found
     * 
     * @param shouldPrintLogs if trace search data to be printed in log or not
     * 
     * @return The corresponding log message if the particular pattern is present, otherwise null.
     * 
     * @throws IOException
     *             in the case of exception happens.
     */
    
    public String searchAndWaitForTrace(String regEx, long timeoutInMilliseconds, boolean start,
	    boolean isFirstOccurrence, boolean shouldPrintLogs) throws IOException {

	if ((null == regEx) || regEx.isEmpty()) {
	    throw new IllegalArgumentException("regEx cannot be null or empty");
	}

	if (timeoutInMilliseconds <= 0) {
	    throw new IllegalArgumentException("timeoutInMilliseconds must be > 0");
	}

	long endTime;
	Pattern pattern;
	String matchingString = null;

	if (shouldPrintLogs)
	    LOGGER.info("\n[TRACE-PROVIDER] : SEARCH STRING - " + regEx);

	try {
	    pattern = Pattern.compile(regEx);
	} catch (PatternSyntaxException pse) {
	    LOGGER.warn("Unable to compile the regular expression: " + pse.getMessage()
		    + "\nPlease make sure regEx is assigned a valid regular expression.");
	    throw new IllegalArgumentException(pse);
	}
	if (shouldPrintLogs) {
	    LOGGER.info("\n>>>>> : monitoringStarted : " + monitoringStarted + "\n>>>>> : clQueue.size() : "
		    + clQueue.size());

	    LOGGER.info("\n>>>>> :clQueue.size() : " + clQueue.size());
	    LOGGER.info("\n>>>>> :this.markOffset  : " + this.markOffset);
	}

	if (clQueue.size() <= 0) {

	    LOGGER.info("Waiting for {} to buffer since buffer is empty", timeoutInMilliseconds);
	    AutomaticsUtils.sleep(timeoutInMilliseconds);
	    if (clQueue.size() <= 0) {
		throw new IOException(
			"SSH Trace provider not working. SSH connection lost or trace not restarted after a connection loss");
	    }
	}

	if (monitoringStarted) {

	    // Some initial values
	    endTime = timeoutInMilliseconds + System.currentTimeMillis();

	    while ((null == matchingString) && (0 < (endTime - System.currentTimeMillis()))) {

		if (this.markOffset >= clQueue.size()) {

		    /*
		     * The mark packet which we are wanting to look at is not in the queue. Continue looping.
		     */
		    continue;
		}

		String[] queue_array = clQueue.toArray(new String[0]);
		int searchOffset = 0;

		if (!start) {
		    searchOffset = this.markOffset;
		}

		// LOGGER.info("\n>>>>> :searchOffset : " + searchOffset);
		for (; searchOffset < queue_array.length; searchOffset++) {

		    String item = queue_array[searchOffset];
		    // LOGGER.info(">>>>> regEx :"+regEx+"   item : " + item);

		    if (pattern.matcher(item).find()) {
			matchingString = item;
			this.markOffset++;
			// Removing break to find the last occurrence of the
			// required string
			if (isFirstOccurrence) {
			    break;
			}
		    }
		}
	    }
	} else {
	    throw new IOException("Connection With Dut Not Established Yet."
		    + " Please make sure this connection is established before trying to search in trace.");
	}
	if (shouldPrintLogs)
	    LOGGER.info("\n[TRACE-PROVIDER] : <========================= SEARCH RESPONSE =======================>\n "
		    + matchingString
		    + "\n[TRACE-PROVIDER] : <=================================================================>");

	return matchingString;
    }

    /**
     * Method to initiate connection to STBs and read response
     */
    private void connectAndRead() {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	LOGGER.debug("Entering into connectAndRead()");

	if (disConnectFlag) {
	    LOGGER.info("current disconnect flag is true");
	} else {

	    ++reConnectCount;

	    LOGGER.info("Going to connect device for trace monitoring .....!");
	    LOGGER.info("XX Host IP Address : " + dut.getHostIp4Address());
	    LOGGER.info("XX Host IP6 Address : " + dut.getHostIp6Address());
	    LOGGER.info("XX Host MAC Address : " + dut.getHostMacAddress());

	    /*
	     * Once stop monitoring called from the panel then thread should be interrupted and it should stop the
	     * reconnect attempts if this thread is in reconnect mode.
	     */
	    monitoringStarted = true;
	    Device device = (Device) dut;
	    try {

		// Start trace for RDKV client device
		if (SupportedModelHandler.isRDKVClient(device)) {
		    // Get connection to RDKV client device
		    connectionClient = connectionProvider.getConnection(device);
		    keepPollingThreadAlive = true;

		    // Execute trace command
		    connectionProvider.execute(device, connectionClient, ExecuteCommandType.TRACE_INIT_COMMAND_GATEWAY,
			    command);
		    connectionStatus = TraceServerConnectionStatus.CONNECTED;

		    if (reConnectCount > 1) {
			AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
		    }
		    if (connectionClient != null) {
			// retrieve output and input streams
			clientDeviceOutputStream = connectionClient.getDefaultOutputStream();
			clientDeviceInputStream = connectionClient.getDefaultInputStream();
		    }
		    if (clientDeviceOutputStream == null) {
			LOGGER.error("Client out stream is Null " + (clientDeviceInputStream == null));
		    }
		    reConnectCount = 0;
		}

		// Start trace for non-RDKV client device
		else {
		    // Get connection to non-RDKV client device
		    connectionGateway = connectionProvider.getConnection(device);
		    keepPollingXg1ThreadAlive = true;

		    // Execute trace command
		    connectionProvider.execute(device, connectionGateway,
			    ExecuteCommandType.TRACE_INIT_COMMAND_GATEWAY, command);

		    if (additionalTraceLogger.isEnabled) {
			connectionGatewayAdditionalLogger = connectionProvider.getConnection(device);
			connectionProvider.execute(device, connectionGatewayAdditionalLogger,
				ExecuteCommandType.ADDLN_TRACE_INIT_COMMAND_GATEWAY,
				additionalTraceLogger.commandToExecute);

		    }
		    connectionStatus = TraceServerConnectionStatus.CONNECTED;

		    try {
			Thread.sleep(2000);
		    } catch (InterruptedException interruptedException) {
			LOGGER.error("Exception", interruptedException);
		    }

		    /*
		     * Sleep added for avoiding lock. There is a chance of deadlock when connect is not completely
		     * release the lock and at the same time output steam is getting created.
		     */
		    if (reConnectCount > 1) {
			AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
		    }

		    reConnectCount = 0;

		    // retrieve output and input streams
		    if (null != connectionGateway) {
			gateWayDeviceOutputStream = connectionGateway.getDefaultOutputStream();
			gateWayDeviceInputStream = connectionGateway.getDefaultInputStream();
		    }

		    LOGGER.debug("is Additional logger enabled : " + additionalTraceLogger.isEnabled);
		    if (additionalTraceLogger.isEnabled) {
			gateWayDeviceOutputStreamAddlLogger = connectionGatewayAdditionalLogger
				.getDefaultOutputStream();
			gateWayDeviceInputStreamAddlLogger = connectionGatewayAdditionalLogger.getDefaultInputStream();
		    }
		}

		// Starts the polling thread
		if (pollingThread == null || !pollingThread.isAlive() || pollingThread.isInterrupted()) {
		    pollingThread = new PollingThread();
		    pollingThread.setName("PollingThread_" + pollingThread.getId() + "_(" + dut.getHostMacAddress()
			    + ")");
		    pollingThread.setPriority(Thread.MAX_PRIORITY);
		    pollingThread.startPolling();
		}

		if (SupportedModelHandler.isRDKVClient(dut)) {
		    readOutputFromChannel(true);
		} else {
		    readOutputFromChannel(false);
		}
	    } catch (Exception e) {

		LOGGER.error("Exception occured while connecting to device", e);
		LOGGER.info("Reconnect Count : " + reConnectCount);

		if (reConnectCount < reconnectAttempt) {

		    if (!disConnectFlag) {

			LOGGER.debug("Connecting again as read not established... Reconnect Count" + reConnectCount);
			connectionStatus = TraceServerConnectionStatus.CONNECTING;
			try {
			    stopTrace(true);
			    Thread.sleep(10000);
			    startTrace();
			} catch (Exception ex) {
			    LOGGER.error("Error:" + ex, ex);
			}
		    }
		}
	    } finally {
		try {
		    if (connectionGateway != null) {
			connectionGateway.disconnect();
		    }
		    if (connectionClient != null) {
			connectionClient.disconnect();
		    }
		} catch (Exception ex) {
		    LOGGER.error("Error:" + ex, ex);
		}
	    }
	}

	LOGGER.debug("Exiting from connectAndRead()");
    }

    /**
     * Method to read output stream
     * 
     * @param rdkvClient
     *            true if RDKV Client else false
     */
    private void readOutputFromChannel(boolean rdkvClient) {
	try {
	    BufferedReader bufferedReader = null;
	    BufferedReader bufferedReaderAdditionalLogger = null;
	    String eventData = "";

	    LOGGER.info("Reading RDKV Client trace", rdkvClient);
	    do {
		// Read trace for non-RDKV Client device
		if (!rdkvClient) {
		    if (null != gateWayDeviceInputStream) {
			if (bufferedReader == null) {
			    bufferedReader = new BufferedReader(new InputStreamReader(gateWayDeviceInputStream));
			}
		    }

		    if (null != gateWayDeviceInputStreamAddlLogger) {
			if (bufferedReaderAdditionalLogger == null) {
			    LOGGER.info("Buffered Reader for additional trace initialized");
			    bufferedReaderAdditionalLogger = new BufferedReader(new InputStreamReader(
				    gateWayDeviceInputStreamAddlLogger));
			}
		    }

		}
		// Read trace for RDKV Client device
		else {
		    if (null != clientDeviceInputStream) {
			if (bufferedReader == null) {
			    bufferedReader = new BufferedReader(new InputStreamReader(clientDeviceInputStream));
			}
		    }
		}

		if (bufferedReader != null && bufferedReader.ready()) {
		    // Read trace data
		    eventData = bufferedReader.readLine();

		    // Provide input if waiting for user input by trace command
		    if (null != connectionGateway) {
			if (gateWayDeviceOutputStream != null) {
			    writeDataToTraceOutputStream(dut, connectionGateway, gateWayDeviceOutputStream, eventData);
			}
		    }
		    if (null != connectionClient) {
			if ((null != clientDeviceOutputStream)) {
			    writeDataToTraceOutputStream(dut, connectionClient, clientDeviceOutputStream, eventData);
			}
		    }

		    // adding test case ID and Mac address details too to dut trace
		    String automationId = ((Device) dut).getAutomationTestId();
		    if (CommonMethods.isNull(automationId)) {
			automationId = "TC-RDK-INIT-1000";
		    }
		    String formattedEventData = new StringBuilder().append("[").append(dut.getHostMacAddress())
			    .append("][").append(automationId).append("]").toString();
		    // Write the trace to file
		    addToBufferSafely(eventData, formattedEventData);

		    // Verify for crash in currently read trace
		    if (enableCrashAnalysis) {
			checkForCrash((Device) dut, eventData);
		    }
		    processTraceData(dut, eventData);

		}

		if (additionalTraceLogger.isEnabled && bufferedReaderAdditionalLogger.ready()) {

		    eventData = bufferedReaderAdditionalLogger.readLine();
		    if (null != connectionGatewayAdditionalLogger && (eventData != null)) {
			writeDataToTraceOutputStream(dut, connectionGatewayAdditionalLogger,
				gateWayDeviceOutputStreamAddlLogger, eventData);
		    }

		    String prefix = "[" + additionalTraceLogger.traceName + "]";

		    // Getting logs from ATOM console
		    String formattedEventData = new StringBuffer().append("[").append(dut.getHostMacAddress())
			    .append("][").append(prefix).append("]").toString();
		    LOGGER.info("Adding atom log to trace :" + eventData);
		    addToBufferSafely(eventData, formattedEventData);
		}
	    } while (loopAlive);

	} catch (Exception e) {
	    LOGGER.debug("Read is interrupted may be due to connection loss or purposefully stop the trace."
		    + " Out of readOutputFromChannel loop.This will affect logs getting appended to settoptrace");
	    LOGGER.error("readOutputFromChannel - Exception Details ------ ", e);
	}

	if (rdkvClient) {
	    keepPollingThreadAlive = false;
	} else {
	    keepPollingXg1ThreadAlive = false;
	}

	LOGGER.info("Out of readOutputFromChannel loop");
	disconnect();
    }

    /**
     * Get gateway trace provider
     * 
     * @param dut
     * @return
     */
    private AbstractTraceProviderImpl getGatewayTraceProvider(Dut dut) {
	Dut gateway = ((Device) dut).getGateWaySettop();
	LOGGER.info("Gateway mac={}", gateway.getHostMacAddress());
	AbstractTraceProviderImpl gatewayTraceProvider = (AbstractTraceProviderImpl) gateway.getTrace();
	return gatewayTraceProvider;
    }

    /**
     * 
     * This method prevents overflow of buffer with duplicate logs..If logs are flooding, after 100 count,they wont be
     * further added.Only when a new log comes after flooding, will it add to buffer
     * 
     * @param eventData
     */
    private void addToBufferSafely(String eventData, String prefix) {
	// LOGGER.info("Inside addToBufferSafely");
	int maxLogsRepetetionAloowedValue = MAX_LOG_REPETIONS_ALLOWABLE;
	String evenDataLogToCompare = stripOffTime(eventData);
	String maxLogsRepetetionAloowedProperty = AutomaticsPropertyUtility.getProperty("duplicate.line.limit",
		String.valueOf(MAX_LOG_REPETIONS_ALLOWABLE));
	try {
	    maxLogsRepetetionAloowedValue = Integer.parseInt(maxLogsRepetetionAloowedProperty);
	} catch (Exception e) {
	    //
	}
	LOGGER.debug("Event = " + (prefix + eventData));
	if (CommonMethods.isNotNull(evenDataLogToCompare)) {
	    if (!evenDataLogToCompare.equals(previousLog)) {
		settopTraceLogger.trace(prefix + eventData);
		if (bufferTrace) {
		    clQueue.add(prefix + eventData);
		}
		previousLog = evenDataLogToCompare;
		consecutiveDuplicateLogsReceived = 0;
	    } else {
		if (consecutiveDuplicateLogsReceived < maxLogsRepetetionAloowedValue) {
		    settopTraceLogger.trace(prefix + eventData);
		    if (bufferTrace) {
			clQueue.add(prefix + eventData);
		    }
		    LOGGER.debug("Duplicate log entry received..But within allowable limit");
		    consecutiveDuplicateLogsReceived++;
		} else {
		    consecutiveDuplicateLogsReceived++;
		    if (consecutiveDuplicateLogsReceived == maxLogsRepetetionAloowedValue)
			LOGGER.error("Logs are repeating...!!! Skipping adding to buffer" + eventData);
		}
	    }
	}
    }

    private String stripOffTime(String eventData) {
	String timeStrippedOffData = null;
	if (CommonMethods.isNotNull(eventData)) {
	    boolean isTimeSTampAvailable = CommonMethods.patternMatcher(eventData,
		    "^(\\d{4} [a-zA-z]{3} \\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.[0-9]{6}).*");
	    if (isTimeSTampAvailable) {
		timeStrippedOffData = eventData.substring(28, eventData.length());
	    } else if (CommonMethods.patternMatcher(eventData, "^(\\d{6}-\\d{2}:\\d{2}:\\d{2}\\.[0-9]{6}).*")) {
		// 190318-05:17:38.304828
		timeStrippedOffData = eventData.substring(22, eventData.length());
	    } else {
		timeStrippedOffData = eventData;
	    }
	}
	return timeStrippedOffData;
    }

    /**
     * Method to close stream and disconnect Connection to the device
     */
    public synchronized void disconnect() {

	if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
	    LOGGER.info("Native flip test enabled. Skipping Trace operations");
	    return;
	}

	try {
	    disconnect(connectionGateway, gateWayDeviceInputStream, gateWayDeviceOutputStream);
	    connectionStatus = TraceServerConnectionStatus.DISCONNECTED;
	} catch (Exception exception) {
	    LOGGER.error("Exception occured while disconnecting Connection (Server)", exception);
	}

	try {
	    disconnect(connectionGatewayAdditionalLogger, gateWayDeviceInputStreamAddlLogger,
		    gateWayDeviceOutputStreamAddlLogger);
	} catch (Exception exception) {
	    LOGGER.error("Exception occured while disconnecting Addln Connection (Server)", exception);
	}

	try {
	    disconnect(connectionClient, clientDeviceInputStream, clientDeviceOutputStream);
	    connectionStatus = TraceServerConnectionStatus.DISCONNECTED;
	} catch (Exception exception) {
	    LOGGER.error("Exception occured while disconnecting Connection (Client)", exception);
	}
    }

    private void disconnect(Connection connection, InputStream inputStream, OutputStream outputStream)
	    throws IOException {
	if (null != connection) {
	    if (inputStream != null) {
		inputStream.close();
	    }
	    if (outputStream != null) {
		outputStream.close();
	    }
	    connection.disconnect();

	} else {
	    LOGGER.info("Connection instance is Null");
	}
    }

    /**
     * Start crash analysis for device
     * 
     * @param device
     *            Device
     */
    private void startCrashAnalysis(final Device device, final String logLine) {
	LOGGER.error("Observed crash for device {}", device.getHostIpAddress());

	TestSessionDO testSessionDetails = device.getTestSessionDetails();
	testSessionDetails.setEndTime();
	String startTime = testSessionDetails.getStartTime();
	if (currentCrashTime != 0) {
	    startTime = CommonMethods.formatTimeInMillisToUTC(testSessionDetails.getFormatter(), currentCrashTime,
		    ZoneId.of("UTC"));
	}

	// Creating crash process request
	CrashPortalRequest requestObject = new CrashPortalRequest(startTime, testSessionDetails.getEndTime(),
		testSessionDetails.getFormatter(), testSessionDetails.getId(), testSessionDetails.getEndTime(),
		testSessionDetails.getTestCaseTobeExecuted(), device, device.getFirmwareVersion(), null);
	CrashAnalysisHandler crashAnalysisHandler = new CrashAnalysisHandler(requestObject, logLine);
	LOGGER.info("Submitting crash process request for device {}", device.getHostIpAddress());
	AutomaticsTestBase.futures.add(AutomaticsTestBase.crashAnalysisExecutor.submit(crashAnalysisHandler));
    }

    /**
     * 
     * Method to check for crashes
     * 
     * @param dut
     */
    private void checkForCrash(Device settop, String currentTrace) {

	final String crashUploadSuccessRegex = crashAnalysisProvider.getRegexForCrashLog(settop);

	try {
	    if (CommonMethods.isNotNull(currentTrace) && settop.getTestSessionDetails() != null
		    && settop.getTestSessionDetails().isShouldPerformCrashAnalysis()) {
		if (CommonMethods.patternMatcher(currentTrace, crashUploadSuccessRegex)) {
		    LOGGER.info("Current line =" + currentTrace);
		    String regexForCoreLog = null;
		    if (SupportedModelHandler.isRDKB(settop) || SupportedModelHandler.isRDKC(settop)) {
			regexForCoreLog = "[0-9]{4}/[0-9]{2}/[0-9]{2}-[0-9]{2}:[0-9]{2}:[0-9]{2}";
			currentCrashTime = CommonMethods.getTimeInMillisFromTimesStampString(currentTrace,
				regexForCoreLog, "yyyy/MM/dd-HH:mm:ss", ZoneId.of("UTC"));
		    } else if (SupportedModelHandler.isRDKV(settop)) {
			regexForCoreLog = "[0-9]{4} [a-zA-Z]{3} [0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
			currentCrashTime = CommonMethods.getTimeInMillisFromTimesStampString(currentTrace,
				regexForCoreLog, "yyyy MMM dd HH:mm:ss", ZoneId.of("UTC"));
		    }
		    startCrashAnalysis(settop, currentTrace);
		}
	    }

	} catch (Exception e) {
	    LOGGER.error("Could not create crash analysis due to exception " + e.getMessage());
	}
    }

    /**
     * Verifies if trace to be enabled for client connected gateway device
     * 
     * @param dut
     * @return true if client connected gateway trace to be enabled; otherwise false
     */
    private boolean isTraceRequiredForConnectedGateway(Dut dut) {
	boolean required = false;
	if (SupportedModelHandler.isRDKVClient(dut) && ((Device) dut).getGateWaySettop() != null) {
	    required = true;
	}
	LOGGER.info("Is trace required for connected gateway: {}", required);
	return required;
    }

    /**
     * Method to check file size and prevent excessive logging in dut trace. File size set is 200MB
     * 
     * @throws Exception
     */
    private void checkFileSize() throws Exception {
	double fileSizeLimitInKb = FIlE_SIZE_LIMIT_KB;
	File traceFile = new File(TraceProviderConstants.SETTOP_TRACE_DIRECTORY
		+ AutomaticsUtils.getCleanMac(dut.getHostMacAddress()) + TraceProviderConstants.TRACE_LOG_FILE_NAME);
	String fileSizeLimitPropertyValue = AutomaticsPropertyUtility.getProperty("trace.size.limit",
		String.valueOf(FIlE_SIZE_LIMIT_KB));
	try {
	    fileSizeLimitInKb = Double.parseDouble(fileSizeLimitPropertyValue);
	} catch (Exception e) {
	    //
	}
	if (traceFile.exists()) {
	    LOGGER.debug("trace file=" + traceFile.getAbsolutePath());
	    double fileSizeInBytes = traceFile.length();
	    double kilobytes = (fileSizeInBytes / 1024.0);
	    LOGGER.debug("trace SIZ in KB=" + kilobytes);
	    if (kilobytes >= fileSizeLimitInKb) {
		try {
		    LOGGER.error("========================================================");
		    LOGGER.error("Trace size is increasing beyond 200Mb");
		    LOGGER.error("========================================================");
		    LOGGER.error("Stopping trace to prevent OOM!!");
		    stopTrace();
		} catch (Exception e) {
		    LOGGER.error("========================================================");
		    LOGGER.error("Unable to stop trace.This might lead to OOM.Check trace for excessive logging in devices");
		    LOGGER.error("========================================================");
		    throw new Exception(e);
		}
	    }
	}
    }

    private class ConnectionThread extends Thread {

	private CountDownLatch countDownLatch = null;

	public ConnectionThread(CountDownLatch latch) {
	    this.countDownLatch = latch;
	}

	@Override
	public void run() {
	    LOGGER.info("Connection Thread");
	    this.countDownLatch.countDown();
	    connectAndRead();
	}
    }

    private class PollingThread extends Thread {

	public void startPolling() {
	    keepPollingThreadAlive = true;
	    keepPollingXg1ThreadAlive = true;
	    start();
	}

	@Override
	public void run() {

	    if (CommonMethods.isNativeFlipTest() || NonRackUtils.disableSettopTrace()) {
		LOGGER.debug("Native flip test enabled. Skipping Trace operations");
		return;
	    }

	    int deviceConnectionLostDetectionCount = 0;
	    Connection connectionPoll = null;
	    Device device = (Device) dut;
	    boolean traceConnectionAlive = false;

	    while (keepPollingThreadAlive || keepPollingXg1ThreadAlive) {
		try {
		    LOGGER.info("Creating polling connection");
		    connectionPoll = connectionProvider.getConnection(device);

		    if (connectionPoll != null) {
			LOGGER.info("Checking if trace connection alive");
			traceConnectionAlive = isTraceConnectionAlive(device);
			checkFileSize();

			if (traceConnectionAlive) {
			    deviceConnectionLostDetectionCount = 0;

			    if (isLostConnection) {
				LOGGER.info("Resetting polling reconnect count to 0");
				LOGGER.info("Polling thread identified the device comes online after connection loss. Restarting the Trace provider");
				if (null != connectionThread) {
				    connectionThread.interrupt();
				}
				disconnect();
				isLostConnection = false;
				startTrace();
			    }
			} else {
			    deviceConnectionLostDetectionCount++;
			    /*
			     * If device connection lost for 2 times continuously, stop the trace provider. This is to
			     * avoid unnecessary restart of trace provider.
			     */
			    if (deviceConnectionLostDetectionCount >= 1) {
				isLostConnection = true;

				if (!disConnectFlag) {
				    LOGGER.error("Polling thread detected a connection loss, Stopping the trace provider and will be restarted after establishing connection with device.");
				    stopTraceOnConnectionLoss();
				}
				connectionStatus = TraceServerConnectionStatus.CONNECTING;
			    }
			}

			LOGGER.info("ConnectionPoll disconnected");
			if (deviceConnectionLostDetectionCount > 0) {
			    AutomaticsUtils.sleep(AutomaticsConstants.THIRTY_SECONDS);
			} else {
			    AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
			}

		    } else {
			LOGGER.error("Polling connection is not yet established with device.");
		    }
		} catch (InterruptedException e) {
		    LOGGER.debug("Polling thread sleep is interrupted");
		} catch (Exception e) {
		    LOGGER.error("Error:", e);
		} finally {
		    if (null != connectionPoll) {
			connectionPoll.disconnect();
			LOGGER.debug("ConnectionPoll disconnected");
		    }
		}
	    }
	}
    }

    @Override
    public Dut getDevice() {
	return this.dut;
    }

    @Override
    public void setDevice(Dut device) {
	this.dut = device;

    }
}
