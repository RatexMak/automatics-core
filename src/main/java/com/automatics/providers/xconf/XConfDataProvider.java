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

package com.automatics.providers.xconf;

import com.automatics.device.Dut;

/**
 * 
 * Provider for providing data to XConf Simulator. Partner can override data provided to Xconf by Simulator implementing
 * this provider
 *
 */
public interface XConfDataProvider {

    /**
     * Gets the device mac address for which configuration data to be updated in XConf Simulator.
     * 
     * @param dut
     *            Device
     * @return device mac address
     */
    public String getDeviceMacAddress(Dut dut);

    /**
     * Gets the firmware filename to be configured in XConf Simulator.
     * 
     * @param dut
     *            Device
     * @param firmwareName
     *            Firmware file name
     * @return Firmware file name
     */
    public String getFirmwareFileName(Dut dut, String firmwareName);

    /**
     * Gets the firmware name to be configured in XConf Simulator
     * 
     * @param dut
     *            Device
     * @param firmwareName
     *            Firmware name
     * @return Firmware name
     */
    public String getFirmwareName(Dut dut, String firmwareName);

    /**
     * Gets the location where firmware are placed for download
     * 
     * @param dut
     *            Device
     * @param firmwareName
     *            firmware name
     * @param protocol
     *            Firmware location hosted with given protocol
     * @return FirmwareLocation
     */
    public String getFirmwareLocation(Dut dut, String firmwareName, String protocol);

}
