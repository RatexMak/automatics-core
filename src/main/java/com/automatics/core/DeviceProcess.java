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
package com.automatics.core;

import com.automatics.enums.CrashFileGenerationDelay;

/**
 * Represents process in device
 * 
 * @author Radhika
 *
 */
public class DeviceProcess {

    private String processName;

    private String coreDumpFileFormat;

    /** core dump file format */
    private String regexForCoreDumpFileFormat;

    /** mini dump file format */
    private String regexForMiniDumpFileFormat;

    /** crash file generation delay */
    private CrashFileGenerationDelay crashFileGenerationDelay;

    /**
     * @return the crashFileGenerationDelay
     */
    public CrashFileGenerationDelay getCrashFileGenerationDelay() {
	return crashFileGenerationDelay;
    }

    /**
     * @param crashFileGenerationDelay
     *            the crashFileGenerationDelay to set
     */
    public void setCrashFileGenerationDelay(CrashFileGenerationDelay crashFileGenerationDelay) {
	this.crashFileGenerationDelay = crashFileGenerationDelay;
    }

    /**
     * @return the processName
     */
    public String getProcessName() {
	return processName;
    }

    /**
     * @param processName
     *            the processName to set
     */
    public void setProcessName(String processName) {
	this.processName = processName;
    }

    /**
     * @return the coreDumpFileFormat
     */
    public String getCoreDumpFileFormat() {
	return coreDumpFileFormat;
    }

    /**
     * @param coreDumpFileFormat
     *            the coreDumpFileFormat to set
     */
    public void setCoreDumpFileFormat(String coreDumpFileFormat) {
	this.coreDumpFileFormat = coreDumpFileFormat;
    }

    /**
     * @return the regexForCoreDumpFileFormat
     */
    public String getRegexForCoreDumpFileFormat() {
	return regexForCoreDumpFileFormat;
    }

    /**
     * @param regexForCoreDumpFileFormat
     *            the regexForCoreDumpFileFormat to set
     */
    public void setRegexForCoreDumpFileFormat(String regexForCoreDumpFileFormat) {
	this.regexForCoreDumpFileFormat = regexForCoreDumpFileFormat;
    }

    /**
     * @return the regexForMiniDumpFileFormat
     */
    public String getRegexForMiniDumpFileFormat() {
	return regexForMiniDumpFileFormat;
    }

    /**
     * @param regexForMiniDumpFileFormat
     *            the regexForMiniDumpFileFormat to set
     */
    public void setRegexForMiniDumpFileFormat(String regexForMiniDumpFileFormat) {
	this.regexForMiniDumpFileFormat = regexForMiniDumpFileFormat;
    }

}
