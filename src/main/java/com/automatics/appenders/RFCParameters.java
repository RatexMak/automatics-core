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

import org.codehaus.jettison.json.JSONObject;

/**
 * Bean class that represents the RFC feature that is to be enabled in box. The details of feature to be enabled should
 * be provided in Automatics.props.
 * 
 * To enable any particular feature, mention the requirements in stb.props like below: <br>
 * 1. any service restart is enough - mention service name in "restartService"<br>
 * 2. any process restart is enough - mention service name in "restartProcess"<br>
 * 3. we just need one or two reboots - mention the reboot count here "rebootCount"
 * 
 * rfc.feature.pxscene.enable=rebootCount-1,restartService-abcd,restartProcess-xyz,feature-{XXXX}
 * 
 * 
 * 
 */
public class RFCParameters {
    // name of RFC feature
    private String featureName;
    // name of process if any to be restarted to implement the feature in box
    private String restartProcess;
    // no: of times the box should be restarted to implement the feature in box
    private int rebootCount;
    // name of service if any to be restarted to implement the feature in box
    private String restartService;
    // details of RFC feature
    private String feature;
    // details of command
    private String command;
    private String valueToVerify;
    // details of wait time
    private String waitTill;
    // details of time interval
    private String timeInterval;
    // boolean to search in dut trace or direct file
    private String searchInSettopTrace;

    private JSONObject featuresList;

    public JSONObject getConfigData() {
	return featuresList;
    }

    public void setConfigData(JSONObject configData) {
	this.featuresList = configData;
    }

    /**
     * @return the features
     */
    public String getFeature() {
	return feature;
    }

    /**
     * @param features
     *            the features to set
     */
    public void setFeature(String feature) {
	this.feature = feature;
    }

    /**
     * @return the rebootCount
     */
    public int getRebootCount() {
	return rebootCount;
    }

    /**
     * @param rebootCount
     *            the rebootCount to set
     */
    public void setRebootCount(int rebootCount) {
	this.rebootCount = rebootCount;
    }

    /**
     * @return the restartService
     */
    public String getRestartService() {
	return restartService;
    }

    /**
     * @param restartService
     *            the restartService to set
     */
    public void setRestartService(String restartService) {
	this.restartService = restartService;
    }

    /**
     * @return the featureName
     */
    public String getFeatureName() {
	return featureName;
    }

    /**
     * @param featureName
     *            the featureName to set
     */
    public void setFeatureName(String featureName) {
	this.featureName = featureName;
    }

    /**
     * @return the restartProcess
     */
    public String getRestartProcess() {
	return restartProcess;
    }

    /**
     * @param restartProcess
     *            the restartProcess to set
     */
    public void setRestartProcess(String restartProcess) {
	this.restartProcess = restartProcess;
    }

    /**
     * @return the command
     */
    public String getCommand() {
	return command;
    }

    /**
     * @param command
     *            the command to set
     */
    public void setCommand(String command) {
	this.command = command;
    }

    /**
     * @return the valueToVerify
     */
    public String getValueToVerify() {
	return valueToVerify;
    }

    /**
     * @param valueToVerify
     *            the valueToVerify to set
     */
    public void setValueToVerify(String valueToVerify) {
	this.valueToVerify = valueToVerify;
    }

    /**
     * @return the waitTill
     */
    public String getWaitTill() {
	return waitTill;
    }

    /**
     * @param waitTill
     *            the waitTill to set
     */
    public void setWaitTill(String waitTill) {
	this.waitTill = waitTill;
    }

    /**
     * @return the timeInterval
     */
    public String getTimeInterval() {
	return timeInterval;
    }

    /**
     * @param timeInterval
     *            the timeInterval to set
     */
    public void setTimeInterval(String timeInterval) {
	this.timeInterval = timeInterval;
    }

    /**
     * @return the searchInSettopTrace
     */
    public String getSearchInSettopTrace() {
	return searchInSettopTrace;
    }

    /**
     * @param searchInSettopTrace
     *            the searchInSettopTrace to set
     */
    public void setSearchInSettopTrace(String searchInSettopTrace) {
	this.searchInSettopTrace = searchInSettopTrace;
    }

}
