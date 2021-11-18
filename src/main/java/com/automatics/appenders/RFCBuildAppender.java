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
package com.automatics.appenders;

import java.util.Iterator;

import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.SnmpConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Dut;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.CommonMethods;

/**
 * The RFC features to be implemented are mentioned in stb.properties
 * "rfc.features" This class will enable or disable mentioned features. If you
 * need to add new features, just add those to stb.properties with value
 * 
 * @author malu.s
 * 
 */

public class RFCBuildAppender implements BuildAppender {

	/** constant to hold string '.backup' */
	private static final String BACKUP = ".backup";

	/** constant to hold string 'destination' */
	private static final String DESTINATION = "destination";

	/** constant to hold string 'source' */
	private static final String SOURCE = "source";

	/** constant to fetch the log file from command */
	private static final String PATTERN_OF_FILE = "\" (.*.log)";

	/** constant to hold command to create back up of a file */
	private static String CREATE_BACK_UP = "mv source destination";

	/** constant to hold delimiter _# */
	private static final String UNDERSCORE = "_#";

	/** constant to hold json delimiter _# */
	private static final String JSON_DELIMITER = "-#";

	/** constant to hold string rfc param - waitTill value */
	private static final String WAIT_TILL = "waitTill";

	/** constant to hold string rfc param - interval */
	private static final String INTERVAL = "interval";

	/** constant to hold string rfc param - valueToVerify */
	private static final String VALUE_TO_VERIFY = "valueToVerify";

	/** constant to hold string rfc param - validation */
	private static final String VALIDATION = "validation";

	/** constant to hold string rfc param - searchFromSettopTrace */
	private static final String SEARCH_IN_SETTOP_TRACE = "searchInSettopTrace";

	/** SLF4j logger instance. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RFCBuildAppender.class);

	/** Feature name that needs to be enabled/disabled. */
	private String rfcFeature = null;

	/** variable to hold the property key for enabling RFC feature. */
	private static final String RFC_FEATURE_ENABLE = "rfc.settings.featureName.enable";

	private static final String RFC_FEATURE_DISABLE = "rfc.settings.featureName.disable";

	/**
	 * variable to hold the property key for the url used to enabling RFC
	 * feature.
	 */
	private static final String RFC_FEATURE_ENABLE_SETTINGS_URL = "proxy.xconf.rfc.update.immutable.url";

	private static final String RFC_FEATURE_UPDATE_SETTINGS_URL = "proxy.xconf.rfc.update.url";

	/** constant to hold string command */
	private static final String COMMAND = "command";

	/** constant to value 30 */
	private static final int CONSTANT_30 = 30;

	/** constant to value 1 */
	private static final int CONSTANT_1 = 1;

	/** constant to store value restartProcess */
	private static final String RESTARTPROCESS = "restartprocess";

	/** constant to store value restartService */
	private static final String RESTARTSERVICE = "restartservice";

	/** constant to store value rebootCount */
	private static final String REBOOTCOUNT = "rebootcount";

	/**
	 * Returns the current RFC feature
	 * 
	 * @return
	 */
	public String getRfcFeature() {
		return rfcFeature;
	}

	/**
	 * @param rfcFeature
	 */
	public void setRfcFeature(String rfcFeature) {
		this.rfcFeature = rfcFeature;
	}

	/**
	 * parses out the RFC feature to be enabled
	 * 
	 * @param appender
	 */
	public RFCBuildAppender(String appender) {
		// Extract the rfc feature to be enabled in box
		if (null != appender && appender.contains(UNDERSCORE)) {
			appender = appender.split(UNDERSCORE)[1];
			if (CommonMethods.isNotNull(appender)) {
				LOGGER.info("RFC feature to be enabled in box is : " + appender);
				this.setRfcFeature(appender);
			} else {
				LOGGER.error("RFC feature is not properly mentioned in user input : "
						+ appender);
			}
		} else {
			LOGGER.error("RFC feature is not properly mentioned in user input : "
					+ appender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.comcast.aap.apps.ecats.buildappender.BuildAppender#applySettings(
	 * com.comcast.ccp.apps.ecats.tap.ECatsTapApi, com.comcast.cats.Settop)
	 */
	@Override
	public String applySettings(AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		String xconfUrl = AutomaticsConstants.EMPTY_STRING;
		if (SupportedModelHandler.isRDKB(dut)) {
			xconfUrl = AutomaticsPropertyUtility.getProperty(RFC_FEATURE_UPDATE_SETTINGS_URL);
		} else {
			xconfUrl = AutomaticsPropertyUtility.getProperty(RFC_FEATURE_ENABLE_SETTINGS_URL);
		}
		// if (SupportedModelHandler.isRDKV(dut) ||
		// SupportedModelHandler.isRDKC(dut)) {
		if (null != rfcFeature) {
			String enableRfcFeature = null;
			if (!rfcFeature.toUpperCase().contains("DISABLE")) {
				enableRfcFeature = RFC_FEATURE_ENABLE.replace("featureName",
						rfcFeature);
			} else {
				enableRfcFeature = RFC_FEATURE_DISABLE.replace("featureName",
						rfcFeature);
			}
			LOGGER.info("Fetching RFC feature corresponding to : "
					+ enableRfcFeature);
			LOGGER.info("Fetching XConfURL corresponding to " + xconfUrl);
			try {
				statusMessage = enableRFCFeature(tapEnv, dut,
						enableRfcFeature, xconfUrl);
				if (!statusMessage.equals(AutomaticsConstants.OK)) {
					statusMessage = "FAILED TO ENABLE THE RFC FEATURE : "
							+ rfcFeature + statusMessage;
				} else if (statusMessage.equals(AutomaticsConstants.OK)
						&& rfcFeature.toLowerCase().equals("snmpv3")) {
					System.setProperty(
						SnmpConstants.SYSTEM_PARAM_SNMP_VERSION, "v3");
					LOGGER.info("[PROPERTY SET] : Set snmp version to ="
							+ System.getProperty(SnmpConstants.SYSTEM_PARAM_SNMP_VERSION));
				}
			} catch (Exception e) {
				statusMessage = e.getMessage();
			}
		} else {
			statusMessage = "FAILED TO ENABLE THE RFC FEATURE";
		}
		// } else {
		// LOGGER.info("Skipping as build Appender is not applicable");
		// }
		return statusMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.comcast.aap.apps.ecats.buildappender.BuildAppender#verifySettings
	 * (com.comcast.ccp.apps.ecats.tap.ECatsTapApi, com.comcast.cats.Settop)
	 */
	@Override
	public String verifySettings(AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		// if (SupportedModelHandler.isRDKV(dut) ||
		// SupportedModelHandler.isRDKC(dut)) {
		boolean status = false;
		if (null != rfcFeature) {
			try {
				String enableRfcFeature = null;
				if (!rfcFeature.toUpperCase().contains("DISABLE")) {
					enableRfcFeature = RFC_FEATURE_ENABLE.replace(
							"featureName", rfcFeature);
				} else {
					enableRfcFeature = RFC_FEATURE_DISABLE.replace(
							"featureName", rfcFeature);
				}
				RFCParameters rfcParam = fetchRFCParameterDetails(tapEnv,
						enableRfcFeature, dut);
				if (SupportedModelHandler.isRDKB(dut)) {
					JSONObject json = rfcParam.getConfigData();
					LOGGER.debug("cobfig data=" + json);
					JSONArray array = new JSONArray(json.getString("features"));
					for (int i = 0; i < array.length(); i++) {
						JSONObject rfcFeatureDatalist = array.getJSONObject(i);
						JSONObject tr181params = rfcFeatureDatalist
								.getJSONObject("configData");
						Iterator<String> it = tr181params.keys();
						while (it.hasNext()) {
							String key = it.next();
							String value = tr181params.get(key).toString();
							String response = tapEnv.executeWebPaCommand(
									dut, key.replaceAll("tr181.",
											AutomaticsConstants.EMPTY_STRING));
							if (CommonMethods.isNotNull(response)
									&& response.equals(value)) {
								status = true;
							} else {
								LOGGER.error("Unable to config RFC :" + key);
								status = false;
								statusMessage = "Unable to config RFC :" + key;
								break;
							}
						}
					}

				} else {
					if (null != rfcParam.getFeatureName()) {
						LOGGER.info("Obtained RFC feature name as : "
								+ rfcParam.getFeatureName());
						status = CommonMethods
								.isRFCIniFileCreated(tapEnv, dut,
										rfcParam.getFeatureName());
					}
					if (!status) {
						LOGGER.info(" Wait for 5 minutes and recheck .ini files");
						tapEnv.waitTill(AutomaticsConstants.FIVE_MINUTES);
						status = CommonMethods
								.isRFCIniFileCreated(tapEnv, dut,
										rfcParam.getFeatureName());
						LOGGER.info("RFC INI file status :" + status);
					}
					if (!status) {
						statusMessage = "RFCIniFile is unavailable in device";
					}
				}
				if (status) {
					if (CommonMethods.isNotNull(rfcParam.getValueToVerify())) {
						LOGGER.info("Verify the property corresponding to "
								+ rfcParam.getFeatureName());
						statusMessage = validateRFCParam(rfcParam, tapEnv,
								dut);
					}
				}
			} catch (Exception e) {
				statusMessage = "FAILED TO VERIFY RFC FEATURE ENABLED STATUS IN THE DEVICE "
						+ e.getMessage();
			}
		} else {
			statusMessage = "FAILED TO VERIFY RFC FEATURE ENABLED STATUS IN THE DEVICE";
		}
		return statusMessage;
	}

	/**
	 * Method that does verification of RFC feature based on the validation
	 * parameter mentioned in configuration.
	 * 
	 * @param rfcParam
	 * @param tapEnv
	 * @param dut
	 * @return response, OK if validation was success else error message
	 */
	private String validateRFCParam(RFCParameters rfcParam, AutomaticsTapApi tapEnv,
			Dut dut) {
		String statusMessage = null;
		if (null != rfcParam && null != rfcParam.getSearchInSettopTrace()
				&& rfcParam.getSearchInSettopTrace().equals("true")) {
			statusMessage = validateRFCParamFromSettopTrace(rfcParam, tapEnv,
					dut);
		} else {
			statusMessage = validateRFCParamFromLog(rfcParam, tapEnv, dut);
		}
		return statusMessage;
	}

	/**
	 * Method that does verification of RFC feature, by checking the
	 * verification string in dut trace. This ensures we get the latest
	 * value.
	 * 
	 * @param rfcParam
	 * @param tapEnv
	 * @param dut
	 * @return response, OK if validation was success else error message
	 */
	private String validateRFCParamFromSettopTrace(RFCParameters rfcParam,
			AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		long waitTill = AutomaticsConstants.ONE_MINUTE;
		try {
			// fetch waitTill value provided by user in minutes
			waitTill = Long.parseLong(rfcParam.getWaitTill());
			// convert the value to milli seconds.
			waitTill = waitTill * AutomaticsConstants.ONE_MINUTE;
			LOGGER.info("validateRFCParamFromSettopTrace() waitTill : "
					+ waitTill + " milliseconds");
		} catch (Exception e) {
			// do nothing here, take default value of 1minute
		}
		boolean status = tapEnv.searchAndWaitForTrace(dut,
				rfcParam.getValueToVerify(), waitTill);
		statusMessage = status ? AutomaticsConstants.OK : (rfcParam.getFeatureName()
				.toUpperCase() + " IS NOT ENABLED IN THE DEVICE");
		return statusMessage;
	}

	/**
	 * Method that does verification of RFC feature, by executing the command
	 * given in stb.props :
	 * {"command":"XXX","valueToVerify":"YYY","waitTill":"ZZZ","interval":"ZZ"}
	 *
	 * 
	 * @param rfcParam
	 * @param tapEnv
	 * @param dut
	 * @return response, OK if validation was success else error message
	 */
	private String validateRFCParamFromLog(RFCParameters rfcParam,
			AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = rfcParam.getFeatureName().toUpperCase()
				+ " IS NOT ENABLED IN THE DEVICE. ";
		// maximum wait time, in minutes, default value 1m
		long waitTill = CONSTANT_1;
		// value to store the interval after which search should continue, in
		// seconds, default 30 second
		long timeInterval = CONSTANT_30;
		String commandToBeExecuted = rfcParam.getCommand();
		String responseExpected = rfcParam.getValueToVerify();
		// 1. Get the max time to execute the command
		String waitTillValue = rfcParam.getWaitTill();
		if (CommonMethods.isNotNull(waitTillValue)) {
			try {
				// default unit should be in minutes
				waitTill = Long.parseLong(waitTillValue);
			} catch (NumberFormatException e) {

			}
		}
		// 2. Get the time interval after which the command should be repeated,
		// in case its a failure
		String timeIntervalValue = rfcParam.getTimeInterval();
		if (CommonMethods.isNotNull(timeIntervalValue)) {
			try {
				// default unit should be in seconds
				timeInterval = Long.parseLong(timeIntervalValue);
			} catch (NumberFormatException e) {

			}
		}
		long maxWaitTimeSecondsinSeconds = waitTill * 60;
		long index = timeInterval;
		statusMessage += "Failed to verify the string " + responseExpected
				+ " using command : " + commandToBeExecuted
				+ " though we waited for " + waitTill + " minutes";

		// Now execute the command for given time interval
		LOGGER.info("Verifying property corresponding to RFC Feature : "
				+ rfcFeature);
		LOGGER.info("Command to execute : " + commandToBeExecuted);
		do {
			String response = tapEnv.executeCommandUsingSsh(dut,
					commandToBeExecuted);
			if (CommonMethods.isNotNull(response)
					&& response.trim().toLowerCase()
							.contains(responseExpected.trim().toLowerCase())) {
				statusMessage = AutomaticsConstants.OK;
				LOGGER.info("Received matching response. So quiting");
				break;
			} else if (CommonMethods.isNotNull(response)) {
				if (CommonMethods.patternMatcher(response,
						responseExpected.trim())) {
					statusMessage = AutomaticsConstants.OK;
					LOGGER.info("Received matching pattern in response. So quiting");
					break;
				}
			} else {
				LOGGER.info(
						"Wait for {} seconds, max wait time is {} seconds or {} minutes. ",
						timeInterval, maxWaitTimeSecondsinSeconds,
						(maxWaitTimeSecondsinSeconds / 60));
				tapEnv.waitTill(timeInterval * AutomaticsConstants.ONE_SECOND);
			}
			index += timeInterval;
		} while (index <= maxWaitTimeSecondsinSeconds);

		return statusMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.comcast.aap.apps.ecats.buildappender.BuildAppender#resetSettings(
	 * com.comcast.ccp.apps.ecats.tap.ECatsTapApi, com.comcast.cats.Settop)
	 */
	@Override
	public String resetSettings(AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		if (SupportedModelHandler.isRDKV(dut)
				|| SupportedModelHandler.isRDKC(dut)) {
			if (null != rfcFeature) {
				try {
					statusMessage = disableRFCFeature(tapEnv, dut);
					if (!statusMessage.equals(AutomaticsConstants.OK)) {
						statusMessage = "FAILED TO DISABLE THE RFC FEATURE : "
								+ rfcFeature + statusMessage;
					}

					if (rfcFeature.toLowerCase().equals("snmpv3")) {
						System.setProperty(
							SnmpConstants.SYSTEM_PARAM_SNMP_VERSION, "v2");
						LOGGER.info("[PROPERTY SET] : Set snmp version to ="
								+ System.getProperty(SnmpConstants.SYSTEM_PARAM_SNMP_VERSION));
					}
				} catch (Exception e) {
					statusMessage = e.getMessage();
				}
			} else {
				statusMessage = "FAILED TO RESET THE RFC FEATURE ";
			}
		} else {
			LOGGER.info("Skipping as build appender not applicable");
		}
		return statusMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.comcast.aap.apps.ecats.buildappender.BuildAppender#verifyReset(com
	 * .comcast.ccp.apps.ecats.tap.ECatsTapApi, com.comcast.cats.Settop)
	 */
	@Override
	public String verifyReset(AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		try {
			// if (SupportedModelHandler.isRDKV(dut) ||
			// SupportedModelHandler.isRDKC(dut)) {
			if (null != rfcFeature) {
				String enableRfcFeature = null;
				if (!rfcFeature.toUpperCase().contains("DISABLE")) {
					enableRfcFeature = RFC_FEATURE_ENABLE.replace(
							"featureName", rfcFeature);
				} else {
					enableRfcFeature = RFC_FEATURE_DISABLE.replace(
							"featureName", rfcFeature);
				}
				// String enableRfcFeature =
				// RFC_FEATURE_ENABLE.replace("featureName", rfcFeature);
				RFCParameters rfcParam = fetchRFCParameterDetails(tapEnv,
						enableRfcFeature, dut);
				if (null != rfcParam.getFeatureName()) {
					String verifyRfc = "RFC_ENABLE_" + rfcFeature + "=false";
					boolean status = CommonMethods
							.verifyRfcParam(tapEnv, dut, verifyRfc);
					if (status) {
						statusMessage = "RFC feature reset verification failed. Could find an entry "
								+ verifyRfc
								+ " in "
								+ CommonMethods
										.getRFCPath(dut, tapEnv)
								+ "/rfcVariable.ini. This means the RFC feature is not yet properly disabled";
					}
				} else {
					statusMessage = "FAILED TO VERIFY THE RFC FEATURE RESET";
				}
			} else {
				statusMessage = "FAILED TO VERIFY THE RFC FEATURE RESET";
			}
			// } else {
			// LOGGER.info("Skipping as build appender not applicable");
			// }
		} catch (Exception e) {
			statusMessage = "FAILED TO VERIFY THE RFC FEATURE RESET "
					+ e.getMessage();
		}
		return statusMessage;
	}

	/**
	 * Method that calls the REST APIs to enable the given RFC feature in box
	 * 
	 * @param tapEnv
	 * @param dut
	 * @param rfcFeature
	 * @param url
	 * @return
	 */
	protected String enableRFCFeature(AutomaticsTapApi tapEnv, Dut dut,
			String rfcFeature, String url) {
		String statusMessage = AutomaticsConstants.OK;
		boolean status = false;
		try {
			String payLoadData = null;
			// extract feature parameter specific to payLoadData
			BuildAppenderManager manager = BuildAppenderManager.getInstance();
			RFCParameters rfcParam = fetchRFCParameterDetails(tapEnv,
					rfcFeature, dut);
			if (rfcParam.getRebootCount() > 0) {
				LOGGER.info("Adding reboot count " + rfcParam.getRebootCount()
						+ "to list");
				manager.getRebootCountList().add(rfcParam.getRebootCount());
			}
			if (CommonMethods.isNotNull(rfcParam.getRestartProcess())) {
				LOGGER.info("Adding restart process "
						+ rfcParam.getRestartProcess() + "to list");
				manager.getRestartProcessList().add(
						rfcParam.getRestartProcess());
			}
			if (CommonMethods.isNotNull(rfcParam.getRestartService())) {
				LOGGER.info("Adding restart service "
						+ rfcParam.getRestartService() + "to list");
				manager.getRestartServiceList().add(
						rfcParam.getRestartService());
			}
			LOGGER.info("postRfcSettingsToXconf() : RFC Param Details");
			if (null != rfcParam) {
				payLoadData = rfcParam.getFeature();
				LOGGER.info("postRfcSettingsToXconf : Feature corresponding to "
						+ rfcParam.getFeatureName()
						+ " is : "
						+ rfcParam.getFeature());
				// Clear out the log mentioned in 'command' of
				// validation parameter, to ensure we get the
				// latest log.
				// Use pattern - "\" (.*.log)" to parse out the log file.
				String command = rfcParam.getCommand();
				if (CommonMethods.isNotNull(command)) {
					String logFile = CommonMethods.patternFinder(command,
							PATTERN_OF_FILE);
					LOGGER.info("Clear out file : " + logFile);
					if (CommonMethods.isNotNull(logFile)) {
						CREATE_BACK_UP = CREATE_BACK_UP
								.replace(SOURCE, logFile).replace(DESTINATION,
										logFile + BACKUP);
						tapEnv.executeCommandUsingSsh(dut, CREATE_BACK_UP);
					}
				}
			}
			status = CommonMethods
					.postRfcSettingsToXconf(tapEnv, dut, payLoadData, url);
			if (!status) {
				statusMessage = "Failed to get a proper response after setting the RFC params";
				LOGGER.error(statusMessage);
			}

		} catch (Exception e) {
			statusMessage = "RFC Feature Rules for " + rfcFeature
					+ " is not properly configured in stb.props against key : "
					+ rfcFeature;
		}
		return statusMessage;
	}

	/**
	 * Method that calls the REST APIs to disable the given RFC feature from the
	 * box
	 * 
	 * @param tapEnv
	 * @param dut
	 * @param rfcFeature
	 * @param url
	 * @return
	 */
	protected String disableRFCFeature(AutomaticsTapApi tapEnv, Dut dut) {
		String statusMessage = AutomaticsConstants.OK;
		int responseCode = 0;
		try {
			String enableRfcFeature = RFC_FEATURE_ENABLE.replace("featureName",
					rfcFeature);
			// fetch the REST API to enable the RFC feature, from stb.properties
			RFCParameters rfcParam = fetchRFCParameterDetails(tapEnv,
					enableRfcFeature, dut);
			if (null != rfcParam) {
				// Fetch the feature name from stb properties as it is
				String featureName = rfcParam.getFeatureName();
				LOGGER.info("About to delete call the REST API to delete the RFC settings corresponding to feature - "
						+ featureName);
				responseCode = CommonMethods
						.clearParamsInServer(dut, tapEnv,
								false, featureName);
				if (responseCode != HttpStatus.SC_OK) {
					statusMessage = "Failed to get a proper response after resetting the RFC params. Obtained response as :  "
							+ responseCode;
					LOGGER.error(statusMessage);
				}
			} else {
				statusMessage = "RFC Feature Rules for "
						+ rfcFeature
						+ " is not properly configured in stb.props against key : "
						+ rfcFeature;
			}
		} catch (Exception e) {
			statusMessage = "RFC Feature Rules for " + rfcFeature
					+ " is not properly configured in stb.props against key : "
					+ rfcFeature + ". Error Message : " + e.getMessage();
		}

		return statusMessage;
	}

	/**
	 * Method that fetches the REST API to enable the RFC feature, from
	 * stb.properties
	 * 
	 * @param tapEnv
	 * @param rfcFeature
	 * @param dut
	 * @return
	 */
	public static RFCParameters fetchRFCParameterDetails(AutomaticsTapApi tapEnv,
			String rfcFeature, Dut dut) throws JSONException {
		RFCParameters rfcParam = new RFCParameters();
		String rfcFeatureFromStbProps = AutomaticsPropertyUtility.getProperty(rfcFeature);
		LOGGER.info("Obtained details corresponding to RFC feature : "
				+ rfcFeature + " as : " + rfcFeatureFromStbProps);
		if (null != rfcFeatureFromStbProps
				&& rfcFeatureFromStbProps.contains(AutomaticsConstants.SEMICOLON)) {
			String[] rfcDetails = rfcFeatureFromStbProps
					.split(AutomaticsConstants.SEMICOLON);
			// iterate the elements given in stb.props
			for (String rfc : rfcDetails) {
				LOGGER.info("Parsing details of : " + rfc);
				if (rfc.toLowerCase().contains("feature-#")) {
					String feature = rfc.split(JSON_DELIMITER)[1];
					rfcParam.setFeature(feature);
					JSONObject js = new JSONObject(feature);
					rfcParam.setConfigData(js);
					// parse out the feature name as it is( case sensitive)
					String pattern = "\"name\":\"(.*?)\",";
					String name = CommonMethods.patternFinder(feature, pattern);
					LOGGER.info("Feature Name : " + name);
					if (null != name) {
						rfcParam.setFeatureName(name);
					}
				} else if (rfc.toLowerCase().contains("validation-#")) {
					String validationDetails = rfc.split(JSON_DELIMITER)[1];
					parseRFCValidationDetails(validationDetails, rfcParam);
				} else {
					try {
						JSONObject response = new JSONObject(rfc);
						Iterator<String> keyIterator = response.keys();
						while (keyIterator.hasNext()) {
							String key = (String) keyIterator.next();
							LOGGER.info("Iterating items : " + key);
							if (key.contains(REBOOTCOUNT)) {
								if (response.getString(REBOOTCOUNT) != null) {
									String count = response
											.getString(REBOOTCOUNT);
									LOGGER.info("rebootcount : " + count);
									rfcParam.setRebootCount(Integer
											.parseInt(count));
								}
							} else if (key.contains(RESTARTSERVICE)) {
								if (response.getString(RESTARTSERVICE) != null) {
									String restartService = response
											.getString(RESTARTSERVICE);
									LOGGER.info("restart Service : "
											+ restartService);
									rfcParam.setRestartService(restartService);
								}
							} else if (key.contains(RESTARTPROCESS)) {
								if (response.getString(RESTARTPROCESS) != null) {
									String restartProcess = response
											.getString(RESTARTPROCESS);
									LOGGER.info("restart Process  : "
											+ restartProcess);
									rfcParam.setRestartProcess(restartProcess);
								}
							}
						}
					} catch (JSONException e) {
						LOGGER.error("Failed to fetch value from stb.props corresponding to : "
								+ rfcFeature + e.getMessage());
					}
				}
			}
		}
		return rfcParam;
	}

	/**
	 * Method that parses the validation details from RFC configuration defined
	 * in stb.props.
	 * 
	 * @param validationDetails
	 * @param rfcParam
	 * @throws JSONException
	 * @author malu
	 */
	private static void parseRFCValidationDetails(String validationDetails,
			RFCParameters rfcParam) {
		try {
			JSONObject response = new JSONObject(validationDetails);
			String validationCommand = response.getString(COMMAND);
			rfcParam.setCommand(validationCommand);
			String valueToVerify = response.getString(VALUE_TO_VERIFY);
			rfcParam.setValueToVerify(valueToVerify);
			String waitTill = response.getString(WAIT_TILL);
			rfcParam.setWaitTill(waitTill);
			String timeInterval = response.getString(INTERVAL);
			rfcParam.setTimeInterval(timeInterval);
			String searchInSettopTrace = response
					.getString(SEARCH_IN_SETTOP_TRACE);
			rfcParam.setSearchInSettopTrace(searchInSettopTrace);
		} catch (Exception e) {
			LOGGER.error("Exception while parsing out RFC validation details");
		}
	}

}
