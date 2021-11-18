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

import com.automatics.region.OcrRegionInfo;

/**
 * 
 * Provider for OCR related operations.
 * 
 * @Radhika
 *
 */
public interface OcrProvider extends BaseProvider {

    /**
     * Gets text from current video image. The region info details are provided via OcrRegionInfo. When
     * readFullImageRegion is true, then the full video image region will be considered for ocr processing. Otherwise,
     * the region height and width mentioned in OcrRegionInfo will be considered for ocr.
     * 
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @param readFullImageRegion
     *            if true, full image will be considered for ocr
     * @return Return text from given region
     */
    String getOcrTextFromCurrentVideoImage(OcrRegionInfo ocrRegionInfo, boolean readFullImageRegion);

    /**
     * Gets text from given image. The region info details are provided via OcrRegionInfo. When readFullImageRegion is
     * true, then the full video image region will be considered for ocr processing. Otherwise, the region height and
     * width mentioned in OcrRegionInfo will be considered for ocr.
     * 
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @param image
     *            Image from which ocr is read
     * @param readFullImageRegion
     *            if true, full image will be considered for ocr
     * @return Return text from given region
     */
    String getOcrTextFromImage(OcrRegionInfo ocrRegionInfo, BufferedImage image, boolean readFullImageRegion);

    /**
     * Extract screen color from given image
     * 
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @param image
     *            Image from which ocr is read
     * @return Return screen color
     */
    String extractColorFromGivenImage(OcrRegionInfo ocrRegionInfo, BufferedImage image);

    /**
     * Wait and verify if given OCR region is displayed in current video image. The wait is configured in field
     * 'OCRtimeout' of region info xml.
     * 
     * @param ocrRegionInfo
     *            OcrRegionInfo
     * @return Return true if given ocr region is displayed in screen
     */
    boolean waitForOcrRegion(OcrRegionInfo ocrRegionInfo);

    /**
     * Sets the image save location
     * 
     * @param directoryPath
     */
    void setImageSaveLocation(String directoryPath);

    /**
     * Gets the image save location
     * 
     * @return Return the image save location
     */
    String getImageSaveLocation();

}
