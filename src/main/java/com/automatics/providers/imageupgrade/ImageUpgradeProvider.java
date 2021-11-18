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
package com.automatics.providers.imageupgrade;

import com.automatics.device.Dut;

/**
 * 
 * Provider for image upgrdae
 *
 */
public interface ImageUpgradeProvider {

    /**
     * Perform image upgrade
     * @param request ImageRequestParams
     * @param dut Device
     * @return response of image upgrade
     */
    public String performImageUpgrade(ImageRequestParams request, Dut dut);

    /**
     * Perform image upgrade and do a device reboot 
     * @param rebootImmediately
     * @param firmwareToBeDownloaded
     * @param dut
     * @return  response of image upgrade
     */
    public String performImageUpgrade(boolean rebootImmediately, String firmwareToBeDownloaded, Dut dut);

    /**
     * Validate the image file existence in server
     *
     * @param dut
     *            {@link Dut}
     * @param buildNameForCDL
     *            Build name
     * @return True is the image available in server
     */
    public boolean isImageAvailableInCDL(Dut dut,String buildNameForCDL);
    
}
