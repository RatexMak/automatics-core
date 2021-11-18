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
 * Data object class to hold the channel details
 * 
 * @author surajmathew
 *
 */
public class ChannelDetailsDO {

    /** Holds the channel number */
    String channelNumber = "0";

    /** Holds the locator uri */
    String locatorUri;

    /** Holds the locator uri of CIF */
    String locatorUriCIF;

    /** Holds the ocap locator */
    String ocapLocator;

    /** Indicates whether HD or SD channel */
    boolean isHdChannel;

    /** Indicates whether vod channel */
    boolean isVodChannel;

    /**
     * @return the channelNumber
     */
    public String getChannelNumber() {
	return channelNumber;
    }

    /**
     * @param channelNumber
     *            the channelNumber to set
     */
    public void setChannelNumber(String channelNumber) {
	this.channelNumber = channelNumber;
    }

    /**
     * @return the locatorUri
     */
    public String getLocatorUri() {
	return locatorUri;
    }

    /**
     * @param locatorUri
     *            the locatorUri to set
     */
    public void setLocatorUri(String locatorUri) {
	this.locatorUri = locatorUri;
    }

    /**
     * @return the locatorUriCIF
     */
    public String getLocatorUriCIF() {
	return locatorUriCIF;
    }

    /**
     * @param locatorUriCIF
     *            the locatorUriCIF to set
     */
    public void setLocatorUriCIF(String locatorUriCIF) {
	this.locatorUriCIF = locatorUriCIF;
    }

    /**
     * @return the ocapLocator
     */
    public String getOcapLocator() {
	return ocapLocator;
    }

    /**
     * @param ocapLocator
     *            the ocapLocator to set
     */
    public void setOcapLocator(String ocapLocator) {
	this.ocapLocator = ocapLocator;
    }

    /**
     * @return the isHdChannel
     */
    public boolean isHdChannel() {
	return isHdChannel;
    }

    /**
     * @param isHdChannel
     *            the isHdChannel to set
     */
    public void setHdChannel(boolean isHdChannel) {
	this.isHdChannel = isHdChannel;
    }

    /**
     * @return the isVodChannel
     */
    public boolean isVodChannel() {
	return isVodChannel;
    }

    /**
     * @param isVodChannel
     *            the isVodChannel to set
     */
    public void setVodChannel(boolean isVodChannel) {
	this.isVodChannel = isVodChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ChannelDetailsDO [channelNumber=");
	builder.append(channelNumber);
	builder.append(", isHdChannel=");
	builder.append(isHdChannel);
	builder.append(", isVodChannel=");
	builder.append(isVodChannel);
	builder.append(", locatorUri=");
	builder.append(locatorUri);
	builder.append(", locatorUriCIF=");
	builder.append(locatorUriCIF);
	builder.append(", ocapLocator=");
	builder.append(ocapLocator);
	builder.append("]");
	return builder.toString();
    }
}
