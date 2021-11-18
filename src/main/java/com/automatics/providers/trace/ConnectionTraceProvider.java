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
import java.io.OutputStream;

import com.automatics.device.Dut;
import com.automatics.providers.connection.Connection;

/**
 * 
 * Additional APIs for connection trace provider
 *
 */
public interface ConnectionTraceProvider extends TraceProvider {

    /**
     * Clean up device
     */
    void cleanupDeviceTrace();

    /**
     * Process the trace data
     * 
     * @param device
     *            Device
     * @param eventData
     *            Trace data
     */
    void processTraceData(Dut device, String eventData);

    /**
     * Gets the command to be executed for initiating trace monitoring
     * 
     * @param dut
     *            Device
     * @return command to be executed for initiating trace monitoring
     */
    String getTraceStartCommand(Dut dut);

    /**
     * Write data to trace output stream
     * 
     * @param device
     *            Device
     * @param connection
     *            Connection to device
     * @param outputStream
     *            Output stream
     * @param eventData
     *            Trace data
     * @throws IOException
     */
    void writeDataToTraceOutputStream(Dut device, Connection connection, OutputStream outputStream, String eventData)
	    throws IOException;

    /**
     * Return the last buffered trace.
     * 
     * @return Last line of the trace
     */
    String getLastTraceLine();

}
