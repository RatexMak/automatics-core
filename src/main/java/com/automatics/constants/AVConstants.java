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
package com.automatics.constants;

/**
 * 
 * Constants for AV related features
 *
 */
public class AVConstants {

    /** Failed images redirection folder. */
    public static final String IMAGE_COMPARE_FOLDER = "image-compare";

    /** Failed images redirection folder. */
    public static final String OCR_IMAGE_FOLDER = "ocr-images";

    /** Folder to store the images taken for closed caption. */
    public static final String CLOSEDCAPTION_IMAGE_FOLDER = "cc-images";    

    /** Key for wait initiate channel tune. */
    public static final String WAIT_INITIATE_CHANNEL_TUNE_PROP_KEY = "wait.initiate.channel.tune_";

    /** Key for wait channel tune complete. */
    public static final String WAIT_CHANNEL_TUNE_COMPLETE_PROP_KEY = "wait.channel.tune.complete_";

    /** Key for wait launch any screen. */
    public static final String WAIT_LAUNCH_ANY_SCREEN_PROP_KEY = "wait.launch.any.screen_";

    /** Property key for wait after hard reboot initiated. */
    public static final String PROP_KEY_WAIT_AFTER_HARD_REBOOT_INITIATED = "wait.after.hard.reboot.initiated_";

    /** Property key for wait after hard reboot completed. */
    public static final String PROP_KEY_WAIT_AFTER_HARD_REBOOT_COMPLETED = "wait.after.hard.reboot.completed_";

    /** The status of AV command audio status "PRESENT". */
    public static final String AV_COMMAND_AUDIO_STATUS_PRESENT = "It appears as though audio is working fine.";

    /** Array of AV command audio status "PRESENT". */
    public static final String[] ARRAY_AV_COMMAND_AUDIO_STATUS_PRESENT = { AV_COMMAND_AUDIO_STATUS_PRESENT };

    /**
     * Holds the RMSE error percentage between two images used for comparison. From experiments it has been inferred
     * that for constant color images, RMSE error less than 2% can be considered as no closed caption state.
     */
    public static final double RMSE_ERROR_CLOSED_CAPTION = 0.020;  

    /** Image compare properties file name. */
    public static final String IMAGE_COMPARE_PROPERTIES_FILE_NAME = "imagecompare.properties";

    /** Command to copy the videoPrefs.json file */
    public static final String CMD_CAT_VIDEO_PREFS = "cat /opt/videoPrefs.json";
}
