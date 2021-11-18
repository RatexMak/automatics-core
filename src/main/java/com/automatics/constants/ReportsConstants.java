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
 * Constants used in automated report generation
 *
 */
public class ReportsConstants {
    
    /** Core Log. */
    public static final String AUTOMATICS_CORE_LOG = "AutomaticsCoreLog";
    
    /** Property for html log for test propject. */
    public static final String PROPERTY_REGEX_FOR_TEST_LOG = "regex.test.log";

    /** Property for html log for utils propject. */
    public static final String PROPERTY_REGEX_FOR_UTILS_LOG = "regex.utils.log";

    /** Property for html log for partner propject. */
    public static final String PROPERTY_REGEX_FOR_PARTNER_LOG = "regex.partner.log";

    /** Property for html log for package propject. */
    public static final String PROPERTY_REGEX_FOR_PACKAGE_LOG = "regex.package.log";
    
    /** User Directory system property. */
    public static final String USR_DIR = "user.dir";

    /** Test Classes Directory. */
    public static final String TC_DIR = "/target/test-classes";

    /** Log Classes Directory. */
    public static final String LOG_DIR = "/logs/";

    /** Class extension. */
    public static final String CLASS_EXTN = ".class";

    /** Length of the extension string. */
    public static final int CLASS_EXTENSION_LENGTH = CLASS_EXTN.length();

    /** Log extension. */
    public static final String LOG_EXTN = ".log";
    
    /** Rolling file for Core Log. */
    public static final String RLGFILE_CORE = "RollingFileCore";

    /** Test Log. */
    public static final String TEST_LOG = "TestLog";

    /** Rolling file for Test Log. */
    public static final String RLGFILE_TEST = "RollingFileTests";

    /** Log Analyzer Log. */
    public static final String ANALYZER_LOG = "LogAnalyzerLog";

    /** Rolling file for Log Analyzer. */
    public static final String RLGFILE_ANALYZER = "RollingFileLogAnalyzer";

}
