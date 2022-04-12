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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Holds the constant that will be used by Trace providers.
 *
 * @author nagendra
 */
public class TraceProviderConstants {

    /** Message for unsupported trace operations. */
    public static final String WAIT_TRACE_METHOD_NOT_SUPPORTED = "Method Not Supported; Use waitForTraceString() or searchAndWaitForTraceString() methods";

    /** Date format to be used in trace logs. */
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    /** Name of the trace log file. */
    public static final String TRACE_LOG_FILE_NAME = "settop_trace.log";

    /** Name of additional trace log file. */
    public static final String ADDITIONAL_TRACE_LOG_FILE_NAME = "settop_trace_<name>.log";

    /** Waiting period for the trace to arrive when connected first. */
    public static final long TRACE_WAIT_PERIOD = 2000;

    /** SimpleDateFormatter to be applied for date time in trace logs. */
    public final DateFormat TRACE_DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    public static final String SETTOP_TRACE_DIRECTORY_NAME = "/settoptrace/";

    /** Directory to save dut trace. */
    public static final String SETTOP_TRACE_DIRECTORY = System.getProperty("user.dir")
	    + AutomaticsConstants.PATH_SEPARATOR + "target" + AutomaticsConstants.PATH_SEPARATOR + "settoptrace"
	    + AutomaticsConstants.PATH_SEPARATOR;
}
