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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.device.Dut;
import com.automatics.utils.AutomaticsUtils;

/**
 * This is a wrapper class for creating loggers according to each calling class requirement.
 *
 * @author smithabg
 */
public class AutomaticsLogger {

    private static final Logger LOGGER = AutomaticsLogger.getFrameworkLogger(AutomaticsLogger.class);

    /** The list of dut loggers created. */
    public static List<Logger> stbLoggers = new ArrayList<Logger>();
    @SuppressWarnings("unchecked")
    Class requestorClass;

    /**
     * Constructor taking in the name of the Logger.
     *
     * @param clazz
     *            - Input Class
     */
    @SuppressWarnings("unchecked")
    public AutomaticsLogger(Class clazz) {
	requestorClass = clazz;
    }

    /**
     * Get the AutomaticsLogger.
     *
     * @param clazz
     *            The class for which logger is required
     *
     * @return AutomaticsLogger
     */
    @SuppressWarnings("unchecked")
    public static AutomaticsLogger getAutomaticsLogger(Class clazz) {
	AutomaticsLogger automaticsLogger = new AutomaticsLogger(clazz);

	return automaticsLogger;
    }

    /**
     * This method gives a logger for each testcase class taking in the class as the input parameter.
     *
     * @param clazz
     *            - Class for which logger is to be created
     *
     * @return logger - Logger object
     */
    @SuppressWarnings("unchecked")
    public static Logger getFrameworkLogger(Class clazz) {
	Logger frameworkLogger = Logger.getLogger(clazz);
	Logger logger = Logger.getLogger(ReportsConstants.AUTOMATICS_CORE_LOG);
	Appender appender = logger.getAppender(ReportsConstants.RLGFILE_CORE);
	frameworkLogger.addAppender(appender);

	return frameworkLogger;
    }

    /**
     * This method gives a logger for each test class taking in the test class as the input parameter.
     *
     * @param clazz
     *            - Test class for which logger is to be created
     *
     * @return logger - Logger object
     */
    @SuppressWarnings("unchecked")
    public static Logger getTestLogger(Class clazz) {
	Logger testLogger = Logger.getLogger(clazz);
	Logger logger = Logger.getLogger(ReportsConstants.TEST_LOG);
	Appender appender = logger.getAppender(ReportsConstants.RLGFILE_TEST);
	testLogger.addAppender(appender);

	return testLogger;
    }

    /**
     * This method gives a logger for the log analyzer class taking in the class as the input parameter.
     *
     * @param clazz
     *            - Class for which logger is to be created
     *
     * @return logger - Logger object
     */
    @SuppressWarnings("unchecked")
    public static Logger getAnalyzerLogger(Class clazz) {
	Logger analyzerLogger = Logger.getLogger(clazz);
	Logger logger = Logger.getLogger(ReportsConstants.ANALYZER_LOG);
	Appender appender = logger.getAppender(ReportsConstants.RLGFILE_ANALYZER);
	analyzerLogger.addAppender(appender);

	return analyzerLogger;
    }

    /**
     * This method obtains and returns the stb logger corresponding the given stb.
     *
     * @param dut
     *            - stb name
     *
     * @return stbLogger - Logger object
     */
    public static Logger getStbLogger(Dut dut) {

	Logger stbLogger = null;
	String stb = "";

	if (dut != null) {
	    stb = AutomaticsUtils.getCleanMac(dut.getHostMacAddress() + "_testTrace");
	}

	synchronized (stbLoggers) {

	    for (Logger stbLog : stbLoggers) {

		if (stb.equals(stbLog.getName())) {
		    LOGGER.info("Stb logger name is: " + stbLog.getName() + " and stb name is: " + stb);
		    stbLogger = stbLog;

		    if (null == stbLog.getAppender(stb)) {

			// Creating an appender for the logger if its not
			// already there.
			RollingFileAppender appender = getStbAppender(stb);
			stbLogger.addAppender(appender);
		    }
		}
	    }

	    if (null == stbLogger) {
		LOGGER.info("Creating an stb logger.");
		stbLogger = Logger.getLogger(stb);

		if (null == stbLogger.getAppender(stb)) {

		    // Creating an appender for the logger if its not already
		    // there.
		    RollingFileAppender appender = getStbAppender(stb);
		    stbLogger.addAppender(appender);
		}

		stbLoggers.add(stbLogger);
	    }
	}

	return stbLogger;
    }

    /**
     * This method creates the appender for stb logger with the given stb name.
     *
     * @param stb
     *            - stb name
     *
     * @return appender - RollingFileAppender object
     */
    public static RollingFileAppender getStbAppender(String stb) {

	RollingFileAppender appender = null;

	try {
	    PatternLayout patternLayout = new PatternLayout("%-5p %d %c %x - %m%n");
	    appender = new RollingFileAppender(patternLayout, System.getProperty(ReportsConstants.USR_DIR)
		    + ReportsConstants.LOG_DIR + stb + ReportsConstants.LOG_EXTN, true);
	    appender.setName(stb);
	    appender.setThreshold(AutomaticsConstants.THRESHOLD);
	    appender.setMaxBackupIndex(AutomaticsConstants.MAXBACKUPINDEX);
	    appender.setMaxFileSize(AutomaticsConstants.MAXFILESIZE);
	} catch (IOException ioe) {
	    LOGGER.error("Exception occurred while adding appender to dut logger: ", ioe);
	}

	return appender;
    }
}
