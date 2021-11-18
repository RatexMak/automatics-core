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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Dut;

/**
 * 
 * Represents CrashPortalRequest
 *
 */
public class CrashPortalRequest {

    /** SLF4j logger instance. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(CrashPortalRequest.class);

    private String startTime;
    private String endTime;
    private String lastFetchTime;
    private String testCaseId;
    private Dut dut;
    private String imagename;
    private CrashDAO result;
    private DateTimeFormatter requestTimeFormat;
    private ZoneId zoneId;

    public String getImagename() {
	return imagename;
    }

    public void setImagename(String imagename) {
	this.imagename = imagename;
    }

    public CrashPortalRequest(String startTime, String endTime, DateTimeFormatter timeFormat, ZoneId id,
	    String lastFetchTime, String testCaseId, Dut dut, String imagename, CrashDAO result) {
	super();
	this.startTime = convertToDateTimeFormat(startTime, timeFormat, id);
	this.endTime = convertToDateTimeFormat(endTime, timeFormat, id);
	this.lastFetchTime = convertToDateTimeFormat(lastFetchTime, timeFormat, id);
	this.testCaseId = testCaseId;
	this.dut = dut;
	this.imagename = imagename;
	this.result = result;
	this.zoneId = id;
    }

    private String convertToDateTimeFormat(String dateString, DateTimeFormatter timeFormat, ZoneId id) {
	requestTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
	LOGGER.debug("format" + timeFormat);
	LOGGER.debug("date" + dateString);
	LocalDateTime ldt = LocalDateTime.parse(dateString, timeFormat);
	ZonedDateTime availableDateTime = ldt.atZone(id);
	ZoneId utcId = ZoneId.of("UTC");
	ZonedDateTime utcDateTime = availableDateTime.withZoneSameInstant(utcId);
	String formattedUtcDateTime = requestTimeFormat.format(utcDateTime);
	return formattedUtcDateTime;
    }

    public CrashDAO getResult() {
	return result;
    }

    public void setResult(CrashDAO result) {
	this.result = result;
    }

    public String getStartTime() {
	return startTime;
    }

    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    public String getEndTime() {
	return endTime;
    }

    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    public String getLastFetchTime() {
	return lastFetchTime;
    }

    public void setLastFetchTime(String lastFetchTime) {
	this.lastFetchTime = lastFetchTime;
    }

    public String getTestCaseId() {
	return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
	this.testCaseId = testCaseId;
    }

    public Dut getSettop() {
	return dut;
    }

    public void setSettop(Dut dut) {
	this.dut = dut;
    }

    @Override
    public String toString() {
	return "startTime = " + startTime + "\nendTime : " + endTime + "\nimagename:" + imagename + "\nmac Address :"
		+ dut.getHostMacAddress();
    }

    public String addTime(long timeToAdd, String timeString) {
	LocalDateTime ldt = LocalDateTime.parse(timeString, requestTimeFormat);
	ZonedDateTime dateTime = ldt.atZone(ZoneId.of("UTC"));
	ZonedDateTime updatedTime = dateTime.plusMinutes(timeToAdd);
	String formattedUtcDateTime = requestTimeFormat.format(updatedTime);
	LOGGER.info("Added TIme = " + formattedUtcDateTime);
	return formattedUtcDateTime;
    }

    /**
     * @return the dut
     */
    public Dut getDut() {
	return dut;
    }

    /**
     * @param dut
     *            the dut to set
     */
    public void setDut(Dut dut) {
	this.dut = dut;
    }

    /**
     * @return the requestTimeFormat
     */
    public DateTimeFormatter getRequestTimeFormat() {
	return requestTimeFormat;
    }

    /**
     * @param requestTimeFormat
     *            the requestTimeFormat to set
     */
    public void setRequestTimeFormat(DateTimeFormatter requestTimeFormat) {
	this.requestTimeFormat = requestTimeFormat;
    }

    /**
     * @return the zoneId
     */
    public ZoneId getZoneId() {
	return zoneId;
    }

    /**
     * @param zoneId
     *            the zoneId to set
     */
    public void setZoneId(ZoneId zoneId) {
	this.zoneId = zoneId;
    }
}
