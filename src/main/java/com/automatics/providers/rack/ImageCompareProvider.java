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
package com.automatics.providers.rack;

import java.awt.image.BufferedImage;

import com.automatics.device.Dut;
import com.automatics.region.ImageCompareRegionInfo;

/**
 * Provider for Image Compare related operations.
 * 
 * @author Raja
 *
 */
public abstract interface ImageCompareProvider extends BaseProvider {

    /**
     * Waits for the specified region to be on screen for the specified timeout. This function uses the match percent,
     * RGB tolerances, and x & y tolerances from the imgXMLPath file.
     * 
     * @param imageXml
     *            The image xml file name.
     * @param regionName
     *            The name of the region to be compared.
     * @param timeOut
     *            The timeout in milliseconds.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public abstract boolean waitForImageRegion(String imageXml, String regionName, long timeOut);

    /**
     * Checks if the expected image from the specified region is on the current screen. Timeouts are disregarded.
     * 
     * @param preCapturedImage
     *            The precaptured reference image
     * @param icRegionInfo
     *            The image compare region info
     * 
     * @return true if the expected image for the region is on the current screen. Otherwise false is returned
     */
    public abstract boolean isRegionOnScreenNow(BufferedImage preCapturedImage, ImageCompareRegionInfo icRegionInfo);

    /**
     * Compares the current image with specified image to be on screen. This function uses the match percent, RGB
     * tolerances, and x & y tolerances from the imgXMLPath file.
     * 
     * @param dut
     *            The {@link Dut} object
     * @param sourceImage
     *            The source image.
     * @param regionInfo
     *            The name of the region to be compared.
     * 
     * @return True if the region is on the screen within the timeout, else false.
     */
    public abstract boolean compareImages(BufferedImage sourceImage, BufferedImage referenceImage,
	    ImageCompareRegionInfo screenInfo);

    /**
     * Method to get the closed caption image save location.
     *
     * @return closed caption image save location
     */
    public String getClosedCaptionImageSaveLocation();

    /**
     * Method to set the closed caption image save location.
     *
     * @param imageSaveLocation
     *            The closed caption image save location.
     */
    public void setClosedCaptionImageSaveLocation(String closedCaptionImageSaveLocation);

    void setImageSaveLocation(String directoryPath);

    String getImageSaveLocation();

}