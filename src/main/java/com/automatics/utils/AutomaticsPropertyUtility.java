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
package com.automatics.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.TestType;
import com.automatics.exceptions.TestException;
import com.automatics.test.AutomaticsTestBase;

/**
 * Class to handle the property information for Automatics properties
 * 
 * @author surajmathew
 * 
 */
public final class AutomaticsPropertyUtility {

	static Properties properties = null;

	/** SLF4J logger. */
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AutomaticsPropertyUtility.class);
	public static final SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyy");

	public static synchronized void loadProperties() {
		if (properties == null) {
			new AutomaticsPropertyUtility();
		}
	}

	/**
	 * Constructor
	 */
	private AutomaticsPropertyUtility() {

		URLConnection connection = null;

		if (properties == null) {

			properties = new Properties();

			// if the system property is set, then its given 1st priority, else the default
			// value will be taken.
			String propertyFileLoc = System.getProperty("automatics.properties.file");

			if (CommonMethods.isNotNull(propertyFileLoc)) {
				LOGGER.info("AutomaticsPropertyUtility: Reading automatics.properties file from " + propertyFileLoc);

				try {

					CommonMethods.disableSSL();

					connection = new URL(propertyFileLoc).openConnection();

					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					connection.setUseCaches(false);

					properties.load(connection.getInputStream());

				} catch (FileNotFoundException e) {
					LOGGER.error("AutomaticsPropertyUtility: File Not Found ->" + e.getMessage(), e);
				} catch (IOException e) {
					LOGGER.error("AutomaticsPropertyUtility: IO error ->" + e.getMessage(), e);
				}
			} else {
				LOGGER.error("Automatics props url not configured.");
			}
		}
	}

	/**
	 * Method to obtain the property value
	 * 
	 * @param propertyName
	 * @return propertyValue
	 */
	public static String getProperty(String propertyName) {

		String propertyValue = null;
		StringBuffer appendedPropertyName = new StringBuffer(propertyName);
		String testType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
		// LOGGER.info("Fetching testType =" + testType);
		TestType testTypeEnum = TestType.QUICK;
		if (CommonMethods.isNotNull(testType)) {
			testTypeEnum = TestType.getIfPresent(testType);
		}
		// LOGGER.info("Fetching testTypeEnum =" + testTypeEnum);
		if (testTypeEnum != null && !TestType.isQt(testTypeEnum.name()) && propertyName.contains("channel")) {
			appendedPropertyName.append(AutomaticsConstants.DOT).append(AutomaticsTestBase.headEnd);
			LOGGER.info("Few head end specific property " + appendedPropertyName.toString());
			if (properties != null) {
				propertyValue = properties.getProperty(appendedPropertyName.toString());
			}
			if (CommonMethods.isNull(propertyValue)) {
				LOGGER.info("===>> " + appendedPropertyName
						+ " property is missing , proceeding with default value for property " + propertyName
						+ " <<=== ");
			}
		}
		if (CommonMethods.isNotNull(propertyValue)) {
			propertyValue = propertyValue.trim();
		} else {
			if (properties != null) {
				LOGGER.info("+++++++++++++++++++++++++ DEBUG getProperty-0 +++++++++++++++++++++++++");
				LOGGER.info("Properties is not null");
				LOGGER.info("PropertyName = " + propertyName);
				propertyValue = properties.getProperty(propertyName);
				LOGGER.info("PropertyValue = " + propertyValue);
				LOGGER.info("+++++++++++++++++++++++++ DEBUG getProperty-1 +++++++++++++++++++++++++");
			}
			if (CommonMethods.isNotNull(propertyValue)) {
				LOGGER.info("+++++++++++++++++++++++++ DEBUG getProperty-2 +++++++++++++++++++++++++");
				LOGGER.info("PropertyValue is not null");
				propertyValue = propertyValue.trim();
				LOGGER.info("PropertyValue = " + propertyValue);
				LOGGER.info("+++++++++++++++++++++++++ DEBUG getProperty-3 +++++++++++++++++++++++++");
			}

		}

		return propertyValue;
	}

	/**
	 * Method to obtain the property value. If not obtained ,the default value will
	 * be returned
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return propertyValue
	 */
	public static String getProperty(String propertyName, String defaultValue) {

		String propertyValue = null;

		if (properties != null) {
			propertyValue = properties.getProperty(propertyName, defaultValue);
		}

		if (CommonMethods.isNotNull(propertyValue)) {
			propertyValue = propertyValue.trim();
		}

		return propertyValue;
	}

	/**
	 * Method to set the property
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public static void setProperty(String propertyName, String propertyValue) {

		if (CommonMethods.isNotNull(propertyName)) {

			if (CommonMethods.isNotNull(propertyValue)) {
				propertyValue = propertyValue.trim();

				if (properties == null) {
					loadProperties();
				}

				if (properties != null) {
					properties.setProperty(propertyName, propertyValue);
				}
			}
		}
	}

	/**
	 * Utility method to get the property value with given prefix
	 * 
	 * @param propPrefix
	 *                   The required prefix
	 * @return The list of properties
	 */
	public static List<String> getPropsWithGivenPrefix(String propPrefix) throws TestException {
		List<String> propertyValues = new ArrayList<String>();
		if (properties != null) {
			Set<Object> set = properties.keySet();
			for (Object obj : set) {
				String str = obj.toString();
				if (str.startsWith(propPrefix)) {
					propertyValues.add(str);
				}
			}
		} else {
			throw new TestException("Failed to load properties");
		}
		Collections.sort(propertyValues);
		return propertyValues;
	}

	public static void readStbProps() throws Exception {

		Properties properties = new Properties();

		// if the system property is set, then its given 1st priority, else the default
		// value will be taken.
		String propertyFileLoc = System.getProperty("automatics.properties.file");

		if (CommonMethods.isNotNull(propertyFileLoc)) {
			LOGGER.info("AutomaticsPropertyUtility: Reading automatics.properties file from " + propertyFileLoc);

			try {

				CommonMethods.disableSSL();

				URLConnection connection = new URL(propertyFileLoc).openConnection();

				connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				properties.load(connection.getInputStream());
			} catch (FileNotFoundException e) {
				LOGGER.error("AutomaticsPropertyUtility: File Not Found: {}. Cannot continue execution.",
						e.getMessage());
				throw new Exception("AutomaticsPropertyUtility: File Not Found");
			} catch (IOException e) {
				LOGGER.error("AutomaticsPropertyUtility: IO error: {}", e.getMessage());
				throw new Exception("AutomaticsPropertyUtility: Exception reading file");
			}
		} else {
			LOGGER.error("Automatics props url not configured. Cannot continue execution.");
			throw new Exception("Automatics props url not configured. Cannot continue execution.");
		}

	}
}