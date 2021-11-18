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
package com.automatics.providers;

import java.util.TreeMap;

import com.automatics.dataobjects.ChannelDetailsDO;
import com.automatics.device.Dut;

/**
 * Interface that provides rdkv device support
 * 
 * @author Radhika and Raja M
 *
 */
public interface RdkVideoDeviceProvider {

    /**
     * Get the channel data
     * 
     * @param Dut
     */
    TreeMap<Integer, ChannelDetailsDO> getChannelData(Dut device);
    
    /**
     * Validate whether the device is having AV
     * 
     * @param Dut
     * @param string
     */
    boolean validateAV(Dut device);
    
    
    /**
     * Method to get current channel locator URL
     * 
     * @param dut
     *            instance of {@link Dut}
     * @return channel locator URL
     */
    String getCurrentChannelLocatorUrl(Dut dut);

}
