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
 * Constants for crash related features
 *
 */
public class CrashConstants {

    /** Success string to verify the crash file upload */
    public static final String CORE_FILE_UPLOAD_SUCCESS_STRING = "Success uploading file";

    /** Success string to verify the core dump file upload */
    public static final String CORE_DUMP_UPLOAD_SUCCESS_STRING = "S3 coredump Upload is successful with TLS1.2";

    /** Success string to verify the mini dump file upload */
    public static final String MINI_DUMP_UPLOAD_SUCCESS_STRING = "S3 minidump Upload is successful with TLS1.2";

    /** core file log path in RDKB */
    public static final String LOG_FILE_FOR_CRASHES_RDKB = "/rdklogs/logs/core_log.txt";

    /** core file log path in RDKV */
    public static final String LOG_FILE_FOR_CRASHES_RDKV = "/opt/logs/core_log.txt";
    
    /** Property to enable crash handling. */
    public static final String PROPERTY_ENABLE_CRASH_ANALYSIS = "crash.analysis.enable";
    
    /** Property key for path of RDKC Boxes in crash folder */
    public static final String PROPERTY_CRASH_FOLDER_RDKC = "c.";

}
