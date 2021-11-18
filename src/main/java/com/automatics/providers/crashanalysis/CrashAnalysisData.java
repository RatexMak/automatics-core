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

import java.util.ArrayList;
import java.util.List;

public class CrashAnalysisData {

    /** Crash details **/
    private String analysisStatus;
    private String macAddress;
    private String model;
    private String jiraTicket;
    private boolean crashExists;
    private String imageName;
    private List<CrashDAO> crashLists = new ArrayList<CrashDAO>();

    public CrashAnalysisData(String macAddress, String model, String imageName, String analysisStatus) {
	super();
	this.analysisStatus = analysisStatus;
	this.macAddress = macAddress;
	this.imageName = imageName;
	this.model = model;
    }

    /**
     * @return the model
     */
    public String getModel() {
	return model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
	this.model = model;
    }

    /**
     * @return the analysisStatus
     */
    public String getAnalysisStatus() {
	return analysisStatus;
    }

    /**
     * @param analysisStatus
     *            the analysisStatus to set
     */
    public void setAnalysisStatus(String analysisStatus) {
	this.analysisStatus = analysisStatus;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
	return macAddress;
    }

    /**
     * @param macAddress
     *            the macAddress to set
     */
    public void setMacAddress(String macAddress) {
	this.macAddress = macAddress;
    }

    /**
     * @return the jiraTicket
     */
    public String getJiraTicket() {
	return jiraTicket;
    }

    /**
     * @param jiraTicket
     *            the jiraTicket to set
     */
    public void setJiraTicket(String jiraTicket) {
	this.jiraTicket = jiraTicket;
    }

    /**
     * @return the crashExists
     */
    public boolean isCrashExists() {
	return crashExists;
    }

    /**
     * @param crashExists
     *            the crashExists to set
     */
    public void setCrashExists(boolean crashExists) {
	this.crashExists = crashExists;
    }

    /**
     * @return the imageName
     */
    public String getImageName() {
	return imageName;
    }

    /**
     * @param imageName
     *            the imageName to set
     */
    public void setImageName(String imageName) {
	this.imageName = imageName;
    }

    /**
     * @return the crashLists
     */
    public List<CrashDAO> getCrashLists() {
	return crashLists;
    }

    /**
     * @param crashLists
     *            the crashLists to set
     */
    public void setCrashLists(List<CrashDAO> crashLists) {
	this.crashLists = crashLists;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "CrashAnalysisData [analysisStatus=" + analysisStatus + ", macAddress=" + macAddress + ", model="
		+ model + ", jiraTicket=" + jiraTicket + ", crashExists=" + crashExists + ", imageName=" + imageName
		+ ", crashLists=" + crashLists + "]";
    }

}
