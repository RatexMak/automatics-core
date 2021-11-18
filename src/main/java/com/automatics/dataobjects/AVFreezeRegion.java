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


/**
 * AVFreezeRegion
 *
 */
public class AVFreezeRegion {

    private String deviceName;

    private long startTime;

    private long endTime;

    private long duration;

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
	return deviceName;
    }

    /**
     * @param deviceName
     *            the deviceName to set
     */
    public void setDeviceName(String deviceName) {
	this.deviceName = deviceName;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
	return startTime;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(long startTime) {
	this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public long getEndTime() {
	return endTime;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(long endTime) {
	this.endTime = endTime;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
	return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(long duration) {
	this.duration = duration;
    }

    @Override
    public String toString() {
	StringBuilder data = new StringBuilder();
	data.append("{deviceName=").append(deviceName).append(",startTime=").append(startTime).append(",endTime=")
		.append(endTime).append(",duration=").append(duration);
	return data.toString();
    }
}
