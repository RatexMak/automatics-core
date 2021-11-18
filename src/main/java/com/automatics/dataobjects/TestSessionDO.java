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
package com.automatics.dataobjects;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.utils.TestUtils;

public class TestSessionDO {
    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestSessionDO.class);

    String startTime;
    String endTime;
    ZoneId id;
    DateTimeFormatter formatter;
    String testCaseLastExecuted;
    String testCaseTobeExecuted;
    boolean shouldPerformCrashAnalysis;
    private int iteration = 0;

    public void setIteration(int iteration) {
	this.iteration = iteration;
    }

    public int getIteration() {
	return iteration;
    }

    public void incrementIteration() {
	iteration = iteration + 1;
    }

    public boolean isShouldPerformCrashAnalysis() {
	return shouldPerformCrashAnalysis;
    }

    public void setShouldPerformCrashAnalysis(boolean shouldPerformCrashAnalysis) {
	this.shouldPerformCrashAnalysis = shouldPerformCrashAnalysis;
    }

    public String getTestCaseTobeExecuted() {
	return testCaseTobeExecuted;
    }

    public void setTestCaseTobeExecuted(String testCaseTobeExecuted) {
	this.testCaseTobeExecuted = testCaseTobeExecuted;
	setShouldPerformCrashAnalysis(!TestUtils.isTestCaseExcluded(testCaseTobeExecuted));
    }

    public String getTestCaseLastExecuted() {
	return testCaseLastExecuted;
    }

    public void setTestCaseLastExecuted(String testCaseLastExecuted) {
	this.testCaseLastExecuted = testCaseLastExecuted;
    }

    public DateTimeFormatter getFormatter() {
	return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
	this.formatter = formatter;
    }

    public ZoneId getId() {
	return id;
    }

    public void setId(ZoneId id) {
	this.id = id;
    }

    public String getStartTime() {
	return startTime;
    }

    public void setStartTime(String zone, String dateTimePattern) {
	LOGGER.info("Setting dattime in patter " + dateTimePattern);
	setId(ZoneId.of(zone));
	ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of(zone));
	setFormatter(DateTimeFormatter.ofPattern(dateTimePattern));
	this.startTime = zdt.format(formatter);
	LOGGER.info("startTime " + startTime);
    }

    public String getEndTime() {
	return endTime;
    }

    public void setEndTime() {
	ZonedDateTime zdt = ZonedDateTime.now(getId());
	this.endTime = zdt.format(formatter);
    }
}
