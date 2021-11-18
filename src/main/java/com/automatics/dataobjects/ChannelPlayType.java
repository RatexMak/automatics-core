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
 * Data object class to hold the channel play type
 * 
 * @author Karthick Pandiyan
 */
public class ChannelPlayType {
    /** Indicates whether QAM channel is being played */
    public boolean isQamChannel;

    /** Indicates whether IP channel is being played */
    public boolean isIpChannel;

    public boolean isQamChannel() {
	return isQamChannel;
    }

    public void setQamChannel(boolean isQamChannel) {
	this.isQamChannel = isQamChannel;
    }

    public boolean isIpChannel() {
	return isIpChannel;
    }

    public void setIpChannel(boolean isIpChannel) {
	this.isIpChannel = isIpChannel;
    }
}
