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
package com.automatics.providers.trace;

import java.io.IOException;

import org.apache.log4j.Level;

import com.automatics.device.Dut;
import com.automatics.providers.rack.BaseProvider;

/**
 * 
 * Provider for device trace
 *
 */
public interface TraceProvider extends BaseProvider {

    /**
     * Perform trace initialization before starting trace
     * 
     * @param device
     */
    void initializeTraceForDevice(Dut device);

    /**
     * Regular expression for crash to be checked in device trace
     * 
     * @return Crash regular expression
     */
    public abstract String getRegexForCrashDetection();

    /**
     * Searches the buffered trace and Waits till the expected expression is found in the incoming dut trace. Searching
     * will start from the start of the buffer. To enable searching in buffer startBuffering() should be called. Returns
     * false if the expression not found within the timeout specified.
     *
     * @param regEx
     *            Regular expression string to wait for
     * @param timeoutInMilliseconds
     *            Time in milliseconds to wait for the expression
     * @param searchFromStart
     *            true if search to be done from start of trace
     * @param isFirstOccurrence
     *            true then search will return back immediately on first match
     * @return true if the specified expression is found
     *
     * @throws IOException
     *             If an IO Exception occurs while communicating with trace source
     */

    String searchAndWaitForTrace(String regEx, long timeoutInMilliseconds, boolean searchFromStart,
	    boolean isFirstOccurrence, boolean shouldPrintLogs) throws IOException;

    /**
     * Waits till the expected expression is found in the incoming dut trace. Returns false if the expression not found
     * within the timeout specified.
     *
     * @param regEx
     *            Regular expression string to wait for
     * @param timeoutInMilliseconds
     *            Time in milliseconds to wait for the expression
     *
     * @return true if the specified expression is found
     *
     * @throws IOException
     *             If an IO Exception occurs while communicating with trace source
     */
    boolean waitForTraceString(String regEx, long timeoutInMilliseconds) throws IOException;

    /**
     * Clears the internal buffer to free up the memory. Its adviced to clear buffer after a search operation is
     * completed
     */
    void clearBuffer();

    /**
     * Starts buffering the trace received from dut.
     */
    void startBuffering();

    /**
     * Stops buffering the trace and clears the trace buffer.
     */
    void stopBuffering();

    /**
     * Inserts the provided trace inside the dut trace file.
     *
     * @param textToInsert
     *            Text that need to be inserted
     * @param logLevel
     *            The logging level
     */
    void insertIntoTrace(String textToInsert, Level logLevel);

    /**
     * Returns true if trace monitoring started
     * 
     * @return
     */
    public boolean isMonitoringStarted();

    /**
     * Starts trace monitoring
     * 
     * @throws Exception
     */
    public abstract void startTrace() throws Exception;

    /**
     * Stops trace monitoring
     * 
     * @throws Exception
     */
    public abstract void stopTrace() throws Exception;

    /**
     * Gets the device for which trace monitoring to be done
     */
    public abstract Dut getDevice();

    /**
     * Sets the device for trace monitoring
     */
    public abstract void setDevice(Dut device);

    /**
     * Gets the trace monitoring status
     * 
     * @return trace monitoring status
     * @throws Exception
     */
    public abstract String getTraceStatus() throws Exception;

}
