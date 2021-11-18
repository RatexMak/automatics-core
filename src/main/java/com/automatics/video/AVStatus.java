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

package com.automatics.video;

/**
 * Get the Audio video status.
 *
 * @author Selvaraj Mariyappan
 */
public class AVStatus {

    /** The status of audio presence. */
    private boolean isAudioAvailable = false;

    /** The status of video presence. */
    private boolean isVideoAvaliable = false;

    /**
     * Get the audio status.
     *
     * @return true if the audio is present. other wise false.
     */
    public boolean isAudioAvailable() {
	return isAudioAvailable;
    }

    /**
     * Set audio status based on commands output executed on box.
     *
     * @param isAudioAvailable
     *            true if audio present, otherwise false.
     */
    public void setAudioAvailable(boolean isAudioAvailable) {
	this.isAudioAvailable = isAudioAvailable;
    }

    /**
     * Get the video status.
     *
     * @return true if the video is present. otherwise false.
     */
    public boolean isVideoAvailable() {
	return isVideoAvaliable;
    }

    /**
     * Set video status based on command output executed on box.
     *
     * @param isVideoAvailable
     *            true if the video is present, otherwise false.
     */
    public void setVideoAvailable(boolean isVideoAvailable) {
	this.isVideoAvaliable = isVideoAvailable;
    }

}
