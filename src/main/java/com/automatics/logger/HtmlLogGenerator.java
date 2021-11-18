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
package com.automatics.logger;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.CommonMethods;

/**
 * Class contains utilities which will convert an input log file into formatted html log file with various levels of
 * indentation for logs coming from test,utils and framework
 * 
 * @author rohinic
 *
 */
public class HtmlLogGenerator {

    /** SLF4J logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlLogGenerator.class);

    /**
     * Image extension
     */
    static final String IMAGE_EXTENSION = ".png";
    /**
     * HTML extension
     */
    static final String HTML_EXTENSION = ".htm";
    /**
     * HTML button text default value
     */
    static final String HTML_DEFAULT_BUTTON_TEXT = "...";
    /**
     * Few identifiers used for regex macthing and replacement
     * 
     */
    static final String IDENTIFIER_TIMESTAMP = "<timestamp>";
    static final String IDENTIFIER_MAC = "<mac>";
    static final String IDENTIFIER_ID = "<id>";
    static final String IDENTIFIER_TESTCASEID = "<testcaseid>";
    static final String IDENTIFIER_PACKAGE = "<package>";
    static final String IDENTIFIER_LOG = "<log>";
    static final String IDENTIFIER_PASS = "status : PASS";
    static final String IDENTIFIER_NR = "status : NR";
    static final String IDENTIFIER_FAIL = "status : FAIL";
    static final String IDENTIFIER_NT = "status : NT";
    static final String IDENTIFIER_NA = "status : NA";
    static final String IDENTIFIER_STATUS_CLASS_IN_CSS = "<successClass>";
    static final String IDENTIFIER_LOCATION = "<location>";
    static final String IDENTIFIER_TARGET = "target";
    static final String IDENTIFIER_ARTIFACT = "artifact";
    static final String IDENTIFIER_FIRSTINDENT = "firstIndent";
    static final String IDENTIFIER_SECONDINDENT = "secondIndent";
    static final String IDENTIFIER_BUILD_URL = "BUILD_URL";
    static final String IDENTIFIER_TEST_LOG_START = "<a name=";
    static final String IDENTIFIER_MANUAL_ID = "manualid";
    static final String IDENTIFIER_STEP = "step";
    static final String IDENTIFIER_IMAGE_FOLDER_NAME = "image-compare";

    /**
     * REGEX to identify test case ID in consolidated logs.
     */
    private static final String REGEX_FOR_TEST_CASE_ID = ".*[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}.*?]\\[*.*?\\|(.*?) :.*?\\|.*?\\]";

    /**
     * REGEX to identify log names for consolidated logs.
     */
    private static final String REGEX_FOR_LOGNAME = ".*?" + AutomaticsConstants.HYPHEN + IDENTIFIER_MAC + ".*"
	    + ReportsConstants.LOG_EXTN;
    /**
     * REGEX to identify logs coming from packages containing test scripts.
     */
    private String[] regexForTestLog = { "core\\.test" };
    /**
     * REGEX to identify logs coming from packages containing utils.
     */
    private String[] regexForUtilsLog = { };

    /**
     * REGEX to identify logs coming from partner.
     */
    private String[] regexForPartnerLog = {};

    /**
     * REGEX to identify logs coming from modules.
     */
    private String[] regexForPackageLog = { "\\|com\\.automatics.*?.(.*?):.*?](.*)" };

    /**
     * REGEX Patterns to identify logs coming from modules.
     */
    private List<Pattern> regexPackagePatterns = null;
    /**
     * REGEX to identify logs coming from core.
     */
    private static final String REGEX_FOR_FRAMEWORK_LOG = "\\.automatics\\.";
    /**
     * REGEX to identify timestamp.
     */
    private static final String REGEX_FOR_TIMESTAMP = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}";
    /**
     * Test status log entry appearing in test logs
     * 
     */
    static final String REGEX_FOR_TEST_STEP_STATUS_LOG = "Manual test ID : (.*?)].*step Number : (.*?)].*Execution status : (.*?)]";

    /**
     * Test status log entry appearing in test logs
     * 
     */
    static final String REGEX_FOR_IMAGE_NAME = ".*" + IDENTIFIER_MANUAL_ID + AutomaticsConstants.DOT + IDENTIFIER_STEP
	    + ".*" + IMAGE_EXTENSION;

    /**
     * Value indicated where the logs should go in html template
     */
    static final String TEMPLATE_LOG_MARKER = "<####logs####>";
    /**
     * Value indicated where the mac should go in html template
     */
    static final String TEMPLATE_MAC_MARKER = "##mac##";
    /**
     * Value indicated where the test case id should go in html template
     */
    static final String TEMPLATE_TEST_ID_MARKER = "##id##";

    static final String TEMPLATE_FIRMWARE_MARKER = "##firmware##";
    /**
     * HTML log template file's name
     */
    static final String FILENAME_HTML_TEMPLATE = "htmlLogsTemplate.txt";
    /**
     * HTML log javascrpt file's name
     */
    static final String FILENAME_HTML_JAVASCRIPT = "htmlParserJS.js";
    /**
     * HTML log CSS file's name
     */
    static final String FILENAME_HTML_CSS = "HtmlParserStyles.css";
    /**
     * Formatter
     */
    static final String FORMATTING_APPENDERS = System.getProperty("line.separator") + "<br>";
    /**
     * HTML tags for test log
     */
    static final String TEST_LOG_OPENER = "<div class=\"test\"><p>";
    /**
     * CLose tags for html log file
     */
    static final String HTML_LOG_CLOSURES = "</div></body></html>";
    /**
     * CLose tags for p
     */
    static final String HTML_P_CLOSURE = "</p>";
    /**
     * CLose tags for div
     */
    static final String HTML_DIV_CLOSURES = "</div>";
    /**
     * CLose tags for newline
     */
    static final String HTML_NEWLINE = "<br>";
    /**
     * Open tags for html p
     */
    static final String HTML_P_OPENER = "<p>";
    /**
     * DEBUG LOGS
     */
    static final String DEBUG_LOGGER = "DEBUG";
    /**
     * marker to indicate hyperlink in the logs
     */
    static final String MARKER = " --- > &nbsp;";

    /**
     * Enum holding different log types
     *
     */
    private enum LOG_TYPES {
	TEST,
	UTILS,
	FRAMEWORK,
	UNKNOWN,
	STATUS,
	PARTNER
    }

    /**
     * CSS Span tag classes
     */
    static final String HTML_SPAN_TAG_CSS_CLASS_PASS = "pass";
    static final String HTML_SPAN_TAG_CSS_CLASS_FAIL = "fail";
    static final String HTML_SPAN_TAG_CSS_CLASS_NR = "notrun";
    static final String HTML_SPAN_TAG_CSS_CLASS_NA = "notapplicable";
    static final String HTML_SPAN_TAG_CSS_CLASS_NT = "nottested";

    static final String LOCATION_PARSER_DEPENDENCIES = AutomaticsConstants.PATH_SEPARATOR + "htmlParserDependencies"
	    + AutomaticsConstants.PATH_SEPARATOR;
    /**
     * Boolean holding status for whether test log found
     */
    boolean isTestLogFound;
    /**
     * Boolean holding status for whether log applicable for first indentation is found or not
     */
    boolean isFirstIndent;
    /**
     * Boolean holding status for whether log applicable for second indentation is found or not
     */
    boolean isSecondIndent;
    /**
     * Boolean holding status for whether debuglog is found .This is used to prevend appending of logs without timestamp
     * coming after debug logs to info types in html logs
     */
    boolean isDebugFound;
    /**
     * Enum type holding log type of first indented log
     */
    LOG_TYPES firstIndentType;
    /**
     * Enum type holding log type of second indented log
     */
    LOG_TYPES secondIndentType;
    /**
     * Enum type holding log type of previous iteration while iterating through entire log file during parsing
     */
    LOG_TYPES previousType;
    /**
     * Modified logging format.
     */
    String LOG_FORMAT = "[" + IDENTIFIER_TIMESTAMP + "][" + IDENTIFIER_PACKAGE + "] : " + IDENTIFIER_LOG;
    /**
     * Button tags for html
     */
    String HTML_BUTTON = "<button class=\"accordion\" id=\"accordion\">" + HTML_DEFAULT_BUTTON_TEXT + "</button>";
    /**
     * Open tag for div class panel
     */
    String HTML_DIV_PANEL_OPENENR = "<div class=\"panel\" id=\"" + IDENTIFIER_ID + "\">";
    /**
     * Span tag
     */
    String HTML_SPAN_TAG = "<span class=\"" + IDENTIFIER_STATUS_CLASS_IN_CSS + "\">" + IDENTIFIER_LOG + "</span>";
    /**
     * Table tag
     */
    String HTML_LOG_INFO_TABLE_TAG = "<br><table><tr><td class=\"header\">DEVICE MAC ADDRESS</td><td class=\"symbol\">:</td><td class=\"data\">"
	    + TEMPLATE_MAC_MARKER
	    + "</td></tr><tr><td class=\"header\">TEST CASE ID</td><td class=\"symbol\">:</td><td class=\"data\">"
	    + TEMPLATE_TEST_ID_MARKER
	    + "</td></tr><tr><td class=\"header\">FIRMWARE</td><td class=\"symbol\">:</td><td class=\"data\">"
	    + TEMPLATE_FIRMWARE_MARKER + "</td></tr></table><br>";
    /**
     * Hyperlink tag for link to image captured during different steps of the test
     * 
     */
    String HTML_HYPERLINK_TAG = "<a href=\"" + IDENTIFIER_LOCATION + "\">STB GUI SnapShot</a>";

    /**
     * This method initializes all required parameters for parsing the log
     */
    private void initializeParser() {
	this.isFirstIndent = false;
	this.isSecondIndent = false;
	this.isTestLogFound = false;
	this.isDebugFound = false;
	this.previousType = LOG_TYPES.UNKNOWN;
	this.firstIndentType = LOG_TYPES.UNKNOWN;
	this.secondIndentType = LOG_TYPES.UNKNOWN;

	List<String> regExValues = null;
	// Override the regular expression with values from Automatics Props
	String propRegExValue = AutomaticsPropertyUtility.getProperty(ReportsConstants.PROPERTY_REGEX_FOR_TEST_LOG);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    regexForTestLog = regExValues.toArray(new String[0]);
	}

	propRegExValue = AutomaticsPropertyUtility.getProperty(ReportsConstants.PROPERTY_REGEX_FOR_UTILS_LOG);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    regexForUtilsLog = regExValues.toArray(new String[0]);
	}

	propRegExValue = AutomaticsPropertyUtility.getProperty(ReportsConstants.PROPERTY_REGEX_FOR_PACKAGE_LOG);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    regexPackagePatterns = new ArrayList<Pattern>();
	    for (String reg : regExValues) {

		try {
		    regexPackagePatterns.add(Pattern.compile(reg));
		} catch (Exception e) {
		    LOGGER.error("Exception while parsing pattern: {}", e);
		}
	    }
	}

	propRegExValue = AutomaticsPropertyUtility.getProperty(ReportsConstants.PROPERTY_REGEX_FOR_PARTNER_LOG);
	if (CommonMethods.isNotNull(propRegExValue)) {
	    regExValues = CommonMethods.splitStringByDelimitor(propRegExValue, AutomaticsConstants.COMMA);
	    regexForPartnerLog = regExValues.toArray(new String[0]);
	}
    }

    /**
     * 
     * This is the log parser method for consolidated logs.
     * 
     * @param testCaseIDs
     *            List of test case ids
     * @param dut
     *            List of devic emac addresses
     * @param logLocation
     *            Location where consolidated logs will be generated
     */
    public void parseAndGenerateHTMLLog(List<String> testCaseIDs, List<String> settop, String logLocation,
	    String firmware) {
	LOGGER.debug("[ HTML LOG PARSER ] : Starting LOG PARSER for Consolidated log " + new java.util.Date());
	List<String> logsToProcess = getLogstoProcess(settop, logLocation);
	BufferedReader bisLogInput = null;
	BufferedWriter bwsHtmlOuput = null;
	for (String eachLog : logsToProcess) {
	    try {
		initializeParser();
		String macUnderProcess = eachLog.substring(eachLog.lastIndexOf(AutomaticsConstants.HYPHEN) + 1,
			eachLog.indexOf(AutomaticsConstants.DOT));
		LOGGER.debug("[ HTML LOG PARSER ] : Starting parsing log " + eachLog + " for mac " + macUnderProcess);
		String htmllogLocation = eachLog.replace(ReportsConstants.LOG_EXTN, HTML_EXTENSION);
		bwsHtmlOuput = getHTMLWiter(htmllogLocation);
		LOGGER.debug("[ HTML LOG PARSER ] : HTML log to ccreate " + htmllogLocation);
		LOGGER.debug("[ HTML LOG PARSER ] : Copying dependencies from "
			+ logLocation.substring(0, logLocation.lastIndexOf(File.separator) + 1));
		String dependencyLocation = logLocation.substring(0, logLocation.lastIndexOf(File.separator) + 1);
		copyFile(FILENAME_HTML_CSS, dependencyLocation);
		copyFile(FILENAME_HTML_JAVASCRIPT, dependencyLocation);
		File fp = new File(eachLog);
		bisLogInput = new BufferedReader(new FileReader(fp));
		String testCaseID = null;
		String line = bisLogInput.readLine();
		boolean newTestFound = false;
		boolean isTemplateCopied = false;
		StringBuilder imageLocation = new StringBuilder();
		while (line != null) {
		    if (!line.contains(DEBUG_LOGGER)) {
			if (CommonMethods.patternMatcher(line, REGEX_FOR_TIMESTAMP)) {
			    isDebugFound = false;
			}
			if (line.contains(IDENTIFIER_TEST_LOG_START)) {
			    if (newTestFound) {
				imageLocation.setLength(0);
				addNecessaryDivClosures(bwsHtmlOuput, LOG_TYPES.UNKNOWN, true);
				bwsHtmlOuput.write(HTML_NEWLINE);
				testCaseID = null;
				bwsHtmlOuput.newLine();
			    } else {
				newTestFound = true;
			    }
			    imageLocation.append(System.getProperty(ReportsConstants.USR_DIR))
				    .append(AutomaticsConstants.PATH_SEPARATOR)
				    .append(AutomaticsConstants.TARGET_FOLDER)
				    .append(AutomaticsConstants.PATH_SEPARATOR);
			    // line = bisLogInput.readLine();
			    if (line != null) {
				while (CommonMethods.isNull(testCaseID) && line != null) {
				    // LOGGER.info("Checking line ="+line);
				    line = bisLogInput.readLine();
				    testCaseID = CommonMethods.patternFinder(line, REGEX_FOR_TEST_CASE_ID);
				}
				if (CommonMethods.isNotNull(testCaseID)) {
				    if (!isTemplateCopied) {
					bwsHtmlOuput = copyTemplate(bwsHtmlOuput, logLocation, htmllogLocation);
					isTemplateCopied = true;
				    }
				    imageLocation.append(testCaseID).append(AutomaticsConstants.PATH_SEPARATOR)
					    .append(AutomaticsUtils.getCleanMac(macUnderProcess))
					    .append(AutomaticsConstants.PATH_SEPARATOR)
					    .append(IDENTIFIER_IMAGE_FOLDER_NAME)
					    .append(AutomaticsConstants.PATH_SEPARATOR);
				    LOGGER.debug("[ HTML LOG PARSER ] : imageLocation " + imageLocation);
				    bwsHtmlOuput.write(HTML_BUTTON.replace(HTML_DEFAULT_BUTTON_TEXT, testCaseID));
				    bwsHtmlOuput.newLine();
				    bwsHtmlOuput.write(HTML_DIV_PANEL_OPENENR.replace(IDENTIFIER_ID,
					    IDENTIFIER_FIRSTINDENT));
				    bwsHtmlOuput.newLine();
				    bwsHtmlOuput = addTestDetailsHeader(bwsHtmlOuput, macUnderProcess, testCaseID,
					    firmware);
				}
			    }
			}
			if (CommonMethods.isNotNull(testCaseID)) {
			    bwsHtmlOuput = commonParser(line, testCaseID, macUnderProcess, imageLocation.toString(),
				    bwsHtmlOuput);
			}
		    } else {
			isDebugFound = true;
		    }
		    line = bisLogInput.readLine();
		}
		bwsHtmlOuput.write(HTML_LOG_CLOSURES);
		bwsHtmlOuput.flush();
	    } catch (IOException e) {
		LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
		e.printStackTrace();
	    } catch (Exception e) {
		LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
		e.printStackTrace();
	    } finally {
		try {
		    if (bisLogInput != null) {
			bisLogInput.close();
		    }
		    if (bwsHtmlOuput != null) {
			bwsHtmlOuput.close();
		    }
		} catch (IOException e) {
		    LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
		    e.printStackTrace();
		}

	    }
	}
	LOGGER.debug("[ HTML LOG PARSER ] : End of log parser for Consolidated log" + new java.util.Date());
    }

    /**
     * Method which returns the Buffered write for output html file
     * 
     * @param htmllogLocation
     *            Location where output html log has to be generated
     * @return Returns the Buffered write for output html file
     * @throws IOException
     */
    private BufferedWriter getHTMLWiter(String htmllogLocation) throws IOException {
	File fpd = new File(htmllogLocation);
	BufferedWriter bwsHtmlOuput = new BufferedWriter(new FileWriter(fpd));
	return bwsHtmlOuput;
    }

    /**
     * LOG PARSING LOGIC
     * 
     * @param line
     *            LOG entry to be parsed
     * @param testCaseID
     *            TEST CASE ID
     * @param macAddress
     *            MAC ADDRESS
     * @param imageLocation
     *            LOCATIOn of image to be hyperlinked
     * @param bwsHtmlOuput
     *            Buffered writer obj for output html file
     * @return Returns the modified buffered writer object
     * @throws IOException
     */
    private BufferedWriter commonParser(String line, String testCaseID, String macAddress, String imageLocation,
	    BufferedWriter bwsHtmlOuput) throws IOException {
	String parsedContent = parseHTMLLog(testCaseID, escapeHtml4(line), macAddress) + FORMATTING_APPENDERS;
	LOG_TYPES type = identifyLogType(line);
	if (type == LOG_TYPES.STATUS) {
	    parsedContent = appendImageLocation(parsedContent, imageLocation);
	}
	if (type == LOG_TYPES.TEST || type == LOG_TYPES.STATUS) {
	    addNecessaryDivClosures(bwsHtmlOuput, type, false);
	    if (previousType == LOG_TYPES.TEST || previousType == LOG_TYPES.STATUS) {
		bwsHtmlOuput.write(parsedContent);
	    } else {
		isTestLogFound = true;
		bwsHtmlOuput.write(TEST_LOG_OPENER + parsedContent);
	    }
	    if (type == LOG_TYPES.STATUS) {
		previousType = LOG_TYPES.STATUS;
	    } else {
		previousType = LOG_TYPES.TEST;
	    }
	} else if (type == LOG_TYPES.UTILS) {
	    bwsHtmlOuput = createNextIndentedLog(parsedContent, LOG_TYPES.UTILS, bwsHtmlOuput);
	} else if (type == LOG_TYPES.FRAMEWORK) {
	    bwsHtmlOuput = createNextIndentedLog(parsedContent, LOG_TYPES.FRAMEWORK, bwsHtmlOuput);
	} else if (type == LOG_TYPES.PARTNER) {
	    bwsHtmlOuput = createNextIndentedLog(parsedContent, LOG_TYPES.PARTNER, bwsHtmlOuput);
	}
	bwsHtmlOuput.flush();
	return bwsHtmlOuput;
    }

    /**
     * This method identified all the logs to be process while processing consolidated logs of a test session
     * 
     * @param dut
     *            List of mac address for the session
     * @param logLocation
     *            location where logs for the test will be generated
     * @return Returns the list absolute locations where consolidated logs are found
     */
    private static List<String> getLogstoProcess(List<String> settop, String logLocation) {
	List<String> logsToProcess = new ArrayList<String>();
	File logDirectory = new File(logLocation);
	if (logDirectory.isDirectory()) {
	    File[] list = logDirectory.listFiles();
	    for (File file : list) {
		for (String mac : settop) {
		    if (file.getName().matches(
			    REGEX_FOR_LOGNAME.replace(IDENTIFIER_MAC, AutomaticsUtils.getCleanMac(mac)))) {
			logsToProcess.add(file.getAbsolutePath());
			LOGGER.debug("[ HTML LOG PARSER ] : Identified log :  " + file.getAbsolutePath());
		    }
		}
	    }
	}
	return logsToProcess;
    }

    /**
     * 
     * This method copies the pre-defined html template to htm log file.All css formatting is defined in the template
     * 
     * @param bwsHtmlOuput
     *            Buffered writer for output html file
     * @param logLocation
     *            Location of log to be parsed
     * @param htmllogLocation
     *            Location where html file has to be generated
     * @param testCaseID
     *            Test case ID
     * @param mac
     *            MAc under process
     * @return Returns the modified buffered writer
     * @throws IOException
     */
    private BufferedWriter copyTemplate(BufferedWriter bwsHtmlOuput, String logLocation, String htmllogLocation)
	    throws IOException {
	LOGGER.debug("[ HTML LOG PARSER ] : Inside copytemplate");
	InputStream is = (new HtmlLogGenerator()).getClass().getClassLoader()
		.getResourceAsStream(FILENAME_HTML_TEMPLATE);
	BufferedInputStream bis = new BufferedInputStream(is);
	BufferedReader bisTemplate = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
	/*
	 * String line = bisTemplate.readLine(); while (line != null) { if (line.contains(TEMPLATE_LOG_MARKER)) { break;
	 * } bwsHtmlOuput.write(line); bwsHtmlOuput.newLine(); line = bisTemplate.readLine(); }
	 */
	while (true) {
	    final String line = bisTemplate.readLine();
	    if (line == null)
		break;
	    if (line.contains(TEMPLATE_LOG_MARKER)) {
		break;
	    }
	    bwsHtmlOuput.write(line);
	    bwsHtmlOuput.newLine();
	}
	bisTemplate.close();
	bwsHtmlOuput.flush();
	LOGGER.debug("[ HTML LOG PARSER ] : Returning form copytemplate");
	return bwsHtmlOuput;
    }

    /**
     * This method is used to copy the js and css files associated with the html parser
     * 
     * @param fileName
     *            Name of file
     * @param destination
     *            Destination location
     * @throws IOException
     */
    private void copyFile(String fileName, String destination) throws IOException {
	InputStream inputStream = (new HtmlLogGenerator()).getClass().getClassLoader().getResourceAsStream(fileName);
	String dependencyLocation = destination + LOCATION_PARSER_DEPENDENCIES;
	LOGGER.debug("Copying dependencies to " + dependencyLocation + fileName);
	File dependencyDirectoryObject = new File(dependencyLocation);
	File dependencyFileObject = null;
	if (!dependencyDirectoryObject.exists()) {
	    if (dependencyDirectoryObject.mkdir()) {
		dependencyFileObject = new File(dependencyLocation + fileName);
	    }

	} else {
	    dependencyFileObject = new File(dependencyLocation + fileName);
	}
	BufferedWriter bwsHtmlOuput = new BufferedWriter(new FileWriter(dependencyFileObject));
	BufferedInputStream bufferedInputSTream = new BufferedInputStream(inputStream);
	BufferedReader bisTemplate = new BufferedReader(new InputStreamReader(bufferedInputSTream,
		StandardCharsets.UTF_8));
	/*
	 * String line = bisTemplate.readLine(); while (line != null) { bwsHtmlOuput.write(line);
	 * bwsHtmlOuput.newLine(); line = bisTemplate.readLine(); }
	 */

	while (true) {
	    final String line = bisTemplate.readLine();
	    if (line == null)
		break;
	    bwsHtmlOuput.write(line);
	    LOGGER.debug("Writing=" + line);
	    bwsHtmlOuput.newLine();
	}
	bisTemplate.close();
	bwsHtmlOuput.flush();
	bwsHtmlOuput.close();
	LOGGER.debug("Copying dependencies to " + dependencyLocation + fileName + " completed");
    }

    /**
     * 
     * Adds info regarding test ID and mac
     * 
     * @param bwsHtmlOuput
     *            Buffered Writer for output file
     * @param mac
     *            Mac under process
     * @param testCaseID
     *            Test case ID
     * @return Returns the modified buffered writer
     * @throws IOException
     */
    private BufferedWriter addTestDetailsHeader(BufferedWriter bwsHtmlOuput, String mac, String testCaseID,
	    String firmware) throws IOException {
	bwsHtmlOuput.write(HTML_LOG_INFO_TABLE_TAG.replace(TEMPLATE_MAC_MARKER, mac)
		.replace(TEMPLATE_TEST_ID_MARKER, testCaseID).replace(TEMPLATE_FIRMWARE_MARKER, firmware));
	bwsHtmlOuput.newLine();
	return bwsHtmlOuput;
    }

    /**
     * This is the main api which parses and generates HTML log .
     * 
     * @param testCaseID
     * @param dut
     * @throws IOException
     */
    public void parseAndGenerateHTMLLog(String testCaseID, String macAddress, String logLocation, String destination,
	    String iteration, String firmware) {
	LOGGER.debug("[ HTML LOG PARSER ] : Starting LOG PARSER " + new java.util.Date());
	String htmllogLocation = destination + File.separator + testCaseID + HTML_EXTENSION;

	// String htmllogLocation = destination + File.separator + testCaseID + HTML_EXTENSION;
	String imageLocation = destination.substring(0,
		destination.indexOf(AutomaticsUtils.getCleanMac(macAddress)) + 13)
		+ IDENTIFIER_IMAGE_FOLDER_NAME
		+ File.separator;
	LOGGER.debug("[ HTML LOG PARSER ] : HTML LOG LOCATION --> " + htmllogLocation);
	LOGGER.debug("[ HTML LOG PARSER ] : IMAGE LOCATION --> " + imageLocation);

	BufferedReader bisLogInput = null;
	BufferedWriter bwsHtmlOuput = null;
	initializeParser();
	try {
	    LOGGER.debug("[ HTML LOG PARSER ] : Copying dependencies from "
		    + logLocation.substring(0, logLocation.lastIndexOf(File.separator) + 1));
	    String dependencyLocation = logLocation.substring(0, logLocation.lastIndexOf(File.separator) + 1);
	    copyFile(FILENAME_HTML_CSS, dependencyLocation);
	    copyFile(FILENAME_HTML_JAVASCRIPT, dependencyLocation);
	    bwsHtmlOuput = getHTMLWiter(htmllogLocation);
	    bwsHtmlOuput = copyTemplate(bwsHtmlOuput, logLocation, htmllogLocation);
	    bwsHtmlOuput = addTestDetailsHeader(bwsHtmlOuput, macAddress, testCaseID, firmware);

	    File fp = new File(logLocation);
	    LOGGER.debug("[ HTML LOG PARSER ] : Size LOG FIle  : " + logLocation + " : " + fp.length());
	    bisLogInput = new BufferedReader(new FileReader(fp));
	    String line = bisLogInput.readLine();
	    while (line != null) {
		if (!line.contains(DEBUG_LOGGER)) {
		    if (CommonMethods.patternMatcher(line, REGEX_FOR_TIMESTAMP)) {
			isDebugFound = false;
		    }
		    if (!line.contains(IDENTIFIER_TEST_LOG_START)) {
			bwsHtmlOuput = commonParser(line, testCaseID, macAddress, imageLocation, bwsHtmlOuput);
		    }
		} else {
		    isDebugFound = true;
		}
		line = bisLogInput.readLine();
	    }
	    bwsHtmlOuput.write(HTML_LOG_CLOSURES);
	    bwsHtmlOuput.flush();
	} catch (FileNotFoundException e) {
	    LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
	} catch (IOException e) {
	    LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
	} catch (Exception e) {
	    LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
	} finally {
	    try {
		if (bisLogInput != null) {
		    bisLogInput.close();
		}
		if (bwsHtmlOuput != null) {
		    bwsHtmlOuput.close();
		}
	    } catch (IOException e) {
		LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
		e.printStackTrace();
	    }

	}
	LOGGER.info("[ HTML LOG PARSER ] : End of log parser " + new java.util.Date());
    }

    /**
     * This method compares given log entry with regex and returns the type of log
     * 
     * @param parsedContent
     *            Parsed log entry
     * 
     * @return Returns log type enum
     */

    private LOG_TYPES identifyLogType(String parsedContent) {
	LOG_TYPES returnType = LOG_TYPES.UNKNOWN;
	if (CommonMethods.patternMatcher(parsedContent, REGEX_FOR_TIMESTAMP)) {
	    if (matchPatternFromArray(parsedContent, regexForTestLog)) {
		returnType = LOG_TYPES.TEST;
	    } else if (matchPatternFromArray(parsedContent, regexForUtilsLog)) {
		returnType = LOG_TYPES.UTILS;
	    } else if (CommonMethods.patternMatcher(parsedContent, REGEX_FOR_TEST_STEP_STATUS_LOG)) {
		returnType = LOG_TYPES.STATUS;
	    } else if (CommonMethods.patternMatcher(parsedContent, REGEX_FOR_FRAMEWORK_LOG)) {
		returnType = LOG_TYPES.FRAMEWORK;
	    } else if (matchPatternFromArray(parsedContent, regexForPartnerLog)) {
		returnType = LOG_TYPES.PARTNER;		
	    }
	} else {
	    if (!isDebugFound) {
		returnType = previousType;
	    }
	}
	return returnType;
    }

    /**
     * This method is used to match pattern from an array of pattern
     * 
     * @param parsedContent
     *            parsed log
     * @param patternArray
     *            Array of patterns for identifyinf test/utils logs
     * @return boolean status of whether type is found or not
     */
    private boolean matchPatternFromArray(String parsedContent, String[] patternArray) {
	boolean isMatched = false;
	for (String eachPattern : patternArray) {
	    if (CommonMethods.patternMatcher(parsedContent, eachPattern)) {
		isMatched = true;
		break;
	    }
	}
	return isMatched;
    }

    /**
     * Method generated the html content for indexed logs.
     * 
     * @param parsedContent
     *            Parsed log entry
     * @param type
     *            Log type of parsed entry
     * @param bw
     *            Buffered writer Obj for html file
     * @throws IOException
     */
    private BufferedWriter createNextIndentedLog(String parsedContent, LOG_TYPES type, BufferedWriter bw)
	    throws IOException {
	if (isTestLogFound) {
	    bw.write(HTML_P_CLOSURE + HTML_DIV_CLOSURES);
	    isTestLogFound = false;
	}
	if (isFirstIndent) {
	    if (type == firstIndentType) {
		if (previousType == type) {
		    bw.write(parsedContent);
		} else {
		    addNecessaryDivClosures(bw, type, false);
		    bw.write(parsedContent);
		}
	    } else {
		if (isSecondIndent) {
		    if (previousType == type) {
			bw.write(parsedContent);
		    } else {
			addNecessaryDivClosures(bw, type, false);
		    }
		} else {
		    // bw.write(HTML_P_CLOSURE + HTML_BUTTON.replaceAll("\\[previewlog\\]", parsedContent) +
		    // HTML_DIV_PANEL_OPENENR.replaceAll("<id>", "secondIndent") + HTML_P_OPENER
		    // + parsedContent);
		    bw.write(HTML_P_CLOSURE + HTML_BUTTON
			    + HTML_DIV_PANEL_OPENENR.replace(IDENTIFIER_ID, IDENTIFIER_SECONDINDENT) + HTML_P_OPENER
			    + parsedContent);
		    isSecondIndent = true;
		    secondIndentType = type;
		}
	    }
	} else {
	    // bw.write(HTML_BUTTON.replaceAll("\\[previewlog\\]", parsedContent) +
	    // HTML_DIV_PANEL_OPENENR.replaceAll("<id>", "firstIndent") + HTML_P_OPENER
	    // + parsedContent);
	    bw.write(HTML_BUTTON + HTML_DIV_PANEL_OPENENR.replace(IDENTIFIER_ID, IDENTIFIER_FIRSTINDENT)
		    + HTML_P_OPENER + parsedContent);
	    isFirstIndent = true;
	    firstIndentType = type;
	}
	previousType = type;
	return bw;
    }

    /**
     * This method closes divs openend for each logtypes appropriate;ly based on conditions
     * 
     * @param bw
     *            Buffered writer type for html log file.
     * @param currentType
     *            Log type of current log being parsed
     * @param isConsolidated
     *            Boolean passed to perform special closured while parsing consolidated logs
     * @throws IOException
     */
    private void addNecessaryDivClosures(BufferedWriter bw, LOG_TYPES currentType, boolean isConsolidated)
	    throws IOException {
	if ((currentType == LOG_TYPES.UTILS && previousType == LOG_TYPES.FRAMEWORK)
		|| (currentType == LOG_TYPES.FRAMEWORK && previousType == LOG_TYPES.UTILS)) {
	    bw.write(HTML_P_CLOSURE + HTML_DIV_CLOSURES + HTML_P_OPENER);
	    isSecondIndent = false;
	}
	if (currentType == LOG_TYPES.TEST || currentType == LOG_TYPES.STATUS || isConsolidated) {
	    if (isSecondIndent) {
		bw.write(HTML_P_CLOSURE + HTML_DIV_CLOSURES + HTML_DIV_CLOSURES);
		isSecondIndent = false;
		isFirstIndent = false;
	    } else if (isFirstIndent) {
		bw.write(HTML_P_CLOSURE + HTML_DIV_CLOSURES);
		isFirstIndent = false;
	    }
	    if (isConsolidated) {
		if (previousType == LOG_TYPES.TEST || previousType == LOG_TYPES.STATUS) {
		    bw.write(HTML_P_CLOSURE + HTML_DIV_CLOSURES);
		}
		previousType = LOG_TYPES.UNKNOWN;
		isSecondIndent = false;
		isFirstIndent = false;
		bw.write(HTML_DIV_CLOSURES);
	    }
	}
    }

    /**
     * Method parses log into required format
     * 
     * @param testCaseID
     *            Test case ID
     * @param logEntry
     *            Log to be parsed
     * @param mac
     *            MAC adderess of DUT
     * 
     * @return Parsed log
     */
    private String parseHTMLLog(String testCaseID, String logEntry, String mac) {
	String log = LOG_FORMAT;
	boolean isTimeStampFound = false;
	// for (String timeStamp : REGEX_FOR_TIMESTAMP) {
	Pattern p = Pattern.compile(REGEX_FOR_TIMESTAMP);
	Matcher matcher = p.matcher(logEntry);
	if (matcher.find()) {
	    isTimeStampFound = true;
	    log = log.replace(IDENTIFIER_TIMESTAMP, matcher.group());
	    if (null != regexPackagePatterns) {
		for (Pattern pattern : regexPackagePatterns) {
		    matcher = pattern.matcher(logEntry);		   
		    try {
			if (matcher.find()) {
			    LOG_TYPES type = identifyLogType(logEntry);
			    if (type == LOG_TYPES.UTILS || type == LOG_TYPES.FRAMEWORK || type == LOG_TYPES.PARTNER) {
				log = log.replace(IDENTIFIER_PACKAGE, matcher.group(1));
			    } else if (type == LOG_TYPES.TEST) {
				log = log.replace(
					IDENTIFIER_PACKAGE,
					matcher.group(1).substring(
						matcher.group(1).lastIndexOf(AutomaticsConstants.DOT) + 1,
						matcher.group(1).length()));
			    } else if (type == LOG_TYPES.STATUS) {
				log = log.replace(IDENTIFIER_PACKAGE, matcher.group(1));
				log = log.replace(IDENTIFIER_LOG, getSpanTag(matcher.group(2)));
				LOGGER.debug("Status log identified =" + log);
			    }
			    if (type != LOG_TYPES.STATUS) {
				log = log.replace(IDENTIFIER_LOG, matcher.group(2));
			    }
			    break;
			}
		    } catch (Exception e) {
			LOGGER.error("[ HTML LOG PARSER ] : Exception --> " + e.getMessage());
		    }
		}
	    }
	}

	if (!isTimeStampFound) {
	    log = logEntry;
	}
	return log;
    }

    /**
     * This method generates span tag for giving test color based on test status -pass/fail/nr/na
     * 
     * @param log
     *            -Status log
     * @return Span tag with appropriate class value
     */
    private String getSpanTag(String log) {
	if (log != null) {
	    if (log.contains(IDENTIFIER_PASS)) {
		log = HTML_SPAN_TAG.replace(IDENTIFIER_STATUS_CLASS_IN_CSS, HTML_SPAN_TAG_CSS_CLASS_PASS).replace(
			IDENTIFIER_LOG, log);
	    } else if (log.contains(IDENTIFIER_FAIL)) {
		log = HTML_SPAN_TAG.replace(IDENTIFIER_STATUS_CLASS_IN_CSS, HTML_SPAN_TAG_CSS_CLASS_FAIL).replace(
			IDENTIFIER_LOG, log);
	    } else if (log.contains(IDENTIFIER_NR)) {
		log = HTML_SPAN_TAG.replace(IDENTIFIER_STATUS_CLASS_IN_CSS, HTML_SPAN_TAG_CSS_CLASS_NR).replace(
			IDENTIFIER_LOG, log);
	    } else if (log.contains(IDENTIFIER_NT)) {
		log = HTML_SPAN_TAG.replace(IDENTIFIER_STATUS_CLASS_IN_CSS, HTML_SPAN_TAG_CSS_CLASS_NT).replace(
			IDENTIFIER_LOG, log);
	    } else if (log.contains(IDENTIFIER_NA)) {
		log = HTML_SPAN_TAG.replace(IDENTIFIER_STATUS_CLASS_IN_CSS, HTML_SPAN_TAG_CSS_CLASS_NA).replace(
			IDENTIFIER_LOG, log);
	    }
	}
	LOGGER.debug("[ HTML LOG PARSER ] : Log --> " + log);
	return log;
    }

    /**
     * This method is used to append the image location to log containing status of test step executed
     * 
     * @param log
     *            Log entry that is being parsed.
     * @param imageLoc
     *            Location where image will be saved
     * @return Returns the appended log
     */
    private String appendImageLocation(String log, String imageLoc) {
	Matcher m = null;
	String step = null;
	String manualTestID = null;
	Pattern p = Pattern.compile(REGEX_FOR_TEST_STEP_STATUS_LOG);
	StringBuilder logModified = new StringBuilder(log);
	m = p.matcher(log);
	if (m.find()) {
	    step = m.group(2);
	    manualTestID = m.group(1);
	    String imagehyperlink = getImageLink(imageLoc, step, manualTestID);
	    // LOGGER.info("[ HTML LOGGER ] : IMage hyperlink : "+imagehyperlink);
	    if (CommonMethods.isNotNull(imagehyperlink)) {
		logModified.append(MARKER).append(HTML_HYPERLINK_TAG.replace(IDENTIFIER_LOCATION, imagehyperlink));
	    }
	}
	// LOGGER.debug("Returning Result : " + logModified);
	return logModified.toString();
    }

    /**
     * 
     * This method generates the hyperlink for images created for the job under concern
     * 
     * @param imageLoc
     *            Location of image generated
     * @param step
     *            Step number
     * @param manualTestID
     *            Manual test case ID matched from log entry
     * @return Returns the image link
     */
    private String getImageLink(String imageLoc, String step, String manualTestID) {
	File allImages = new File(imageLoc);
	String imageLink = null;
	if (allImages != null && allImages.isDirectory()) {
	    String[] fileList = allImages.list();
	    for (String fileName : fileList) {
		LOGGER.debug("[ HTML LOG PARSER ] : Image fileNames - " + fileName);
		LOGGER.debug("[ HTML LOG PARSER ] : Pattern to be matched - " + ".*" + manualTestID + "." + step + ".*"
			+ IMAGE_EXTENSION);
		if (fileName.matches(REGEX_FOR_IMAGE_NAME.replace(IDENTIFIER_MANUAL_ID, manualTestID).replace(
			IDENTIFIER_STEP, step))) {
		    String imageRelativePath = (imageLoc + fileName).substring(
			    (imageLoc + fileName).indexOf(IDENTIFIER_TARGET), (imageLoc + fileName).length());
		    LOGGER.debug("[ HTML LOG PARSER ] : Relative path -" + imageRelativePath);
		    if (CommonMethods.isNotNull(imageRelativePath)) {
			String buildUrl = System.getProperty(IDENTIFIER_BUILD_URL);
			imageLink = buildUrl + IDENTIFIER_ARTIFACT + File.separator;
			imageLink = imageLink + imageRelativePath;
			LOGGER.info("[ HTML LOG PARSER ] : IMagelink created : " + imageLink);
		    }
		    break;
		}
	    }
	}
	return imageLink;
    }
}
