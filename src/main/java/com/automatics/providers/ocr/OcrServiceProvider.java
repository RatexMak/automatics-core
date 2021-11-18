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
package com.automatics.providers.ocr;

import com.automatics.device.Dut;
import com.automatics.providers.rack.exceptions.OcrException;
import com.automatics.region.OcrRegionInfo;

/**
 * Provider for Ocr relates services
 * 
 * @author Radhika
 *
 */
public interface OcrServiceProvider {

    /**
     * Reads text from given image region
     * 
     * @param dut
     *            Device under test
     * @param textRegionSize
     *            Holds width and height of region respectively
     * @param textRegionCoordinates
     *            Holds x-coordinate and y-coordinate respectively
     * @param imageLocation
     *            Location where image is saved. This image is used ocr.
     * @return Returns text in given image region
     * @throws OcrException
     */
    public String readTextFromGivenImageRegion(Dut dut, String[] textRegionSize, String[] textRegionCoordinates,
	    String imageLocation) throws OcrException;

    /**
     * Gets screen color from given image region
     * 
     * @param dut
     *            Device under test
     * @param colorRegionSize
     *            Holds width and height of region respectively
     * @param colorRegionCoordinates
     *            Holds x-coordinate and y-coordinate respectively
     * @param imageLocation
     *            Location where image is saved. This image is used ocr.
     * @return Returns screen color from given image region
     * @throws OcrException
     */
    public String extractColorFromGivenImageRegion(Dut dut, String[] colorRegionSize, String[] colorRegionCoordinates,
	    String imageLocation) throws OcrException;

    /**
     * Verify if given ocr region is currently displayed device
     * 
     * @param dut
     *            Device under test
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @return Returns true if ocr region is displayed in screen, otherwise false
     * @throws OcrException
     */
    public boolean waitForOcrRegion(Dut dut, OcrRegionInfo ocrRegionInfo) throws OcrException;

}
