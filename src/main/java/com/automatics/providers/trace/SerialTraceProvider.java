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

import com.automatics.device.Dut;

/**
 * 
 * Additional APIs for serial trace provider
 *
 */
public interface SerialTraceProvider extends TraceProvider {

    /**
     * Sends the data to serial console
     * 
     * @param data
     *            Data to be sent to serial console
     * @param isHexString
     *            true if data is in hex format
     */
    public abstract void sendTraceString(String data, boolean isHexString);

    /**
     * Send data bytes to serial console
     * 
     * @param dataBytes
     */
    public abstract void sendTraceBytes(byte[] dataBytes);

    /**
     * Gets the serial trace path to device
     * 
     * @param dut
     * @return Serial trace path to device
     */
    public String getTracePathForDevice(Dut dut);

    /**
     * Returns buffered data and then clears the current buffer
     * 
     * @return buffered data
     */
    public String getBufferData();

}
