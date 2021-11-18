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

package com.automatics.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.rack.RackInitializer;
import com.automatics.tap.AutomaticsTapApi;

public class FirmwareDetailsCollector implements Runnable {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareDetailsCollector.class);

    private RackInitializer rackInitializerInstance;
    private Dut settopInstance;

    /**
     * Creates an instance.
     *
     * @param rackInitializer
     *            The RackInitializer instance to which firmware details has to be stored
     * @param dut
     *            STB whose firmware details need to be fetched
     */
    public FirmwareDetailsCollector(RackInitializer rackInitializer, Dut dut) {
	rackInitializerInstance = rackInitializer;
	settopInstance = dut;
    }

    @Override
    public void run() {
	LOGGER.info("Collecting Firmware Details For " + settopInstance.getHostMacAddress());

	String firmwareName = AutomaticsTapApi.getInstance().getFirmwareVersion(settopInstance);
	rackInitializerInstance.addFirmwareDetailsToMap(settopInstance.getHostMacAddress(), firmwareName);

	/*
	 * Added for reporting purpose.
	 */
	((Device) settopInstance).setFirmwareVersion(firmwareName);
    }

}
