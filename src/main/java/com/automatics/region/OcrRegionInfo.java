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
package com.automatics.region;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * 
 * Represents an OCR Region. Contains details about image region where character reading to be performed
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OCRRegionInfo")
@JsonPropertyOrder(alphabetic = true)
public class OcrRegionInfo extends RegionInfo {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -6246806932542605252L;

    @XmlElement(name = "url")
    protected String url;

    @XmlElement(name = "ExpectedResultText")
    protected String expectedText;

    @XmlElement(name = "OCRtimeout")
    protected Integer timeout;

    @XmlElement(name = "Tolerance")
    protected Integer successTolerance;

    @XmlElementWrapper(name = "imageEnhancementTypes")
    @XmlElement(name = "imageEnhancementType")
    protected List<String> imageEnhancementTypes;

    /**
     * Default timeout
     */
    public static final Integer DEFAULT_TIMEOUT = Integer.valueOf(5);

    /**
     * Default success tolerance
     */
    public static final Integer DEFAULT_SUCCESS_TOLERANCE = Integer.valueOf(80);

    public OcrRegionInfo() {
	this("", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
    }

    public OcrRegionInfo(String name, Integer x, Integer y, Integer w, Integer h) {
	super(name, x, y, w, h);

	this.imageEnhancementTypes = new ArrayList<String>();
	this.expectedText = "";
	this.timeout = DEFAULT_TIMEOUT;
	this.successTolerance = DEFAULT_SUCCESS_TOLERANCE;
	this.url = "";
    }

    /**
     * @return the url
     */    
    public String getUrl() {
	return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
	this.url = url;
    }

    /**
     * @return the expectedText
     */    
    public String getExpectedText() {
	return expectedText;
    }

    /**
     * @param expectedText
     *            the expectedText to set
     */
    public void setExpectedText(String expectedText) {
	this.expectedText = expectedText;
    }

    /**
     * @return the timeout
     */   
    public Integer getTimeout() {
	return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(Integer timeout) {
	this.timeout = timeout;
    }

    /**
     * @return the successTolerance
     */   
    public Integer getSuccessTolerance() {
	return successTolerance;
    }

    /**
     * @param successTolerance
     *            the successTolerance to set
     */
    public void setSuccessTolerance(Integer successTolerance) {
	this.successTolerance = successTolerance;
    }

    /**
     * @return the imageEnhancementTypes
     */   
    public List<String> getImageEnhancementTypes() {
	return imageEnhancementTypes;
    }

    /**
     * @param imageEnhancementTypes
     *            the imageEnhancementTypes to set
     */
    public void setImageEnhancementTypes(List<String> imageEnhancementTypes) {
	this.imageEnhancementTypes = imageEnhancementTypes;
    }
    
    /**
     * Gets object data in string format
     */
    public String toString() {
	StringBuilder builder = new StringBuilder(super.toString());
	builder.append(", OCRtimeout: ").append(this.timeout).append(", ExpectedResultText: ").append(this.expectedText).append(", url: ").append(this.url);
	if (null != imageEnhancementTypes){
	    for (String imageEnhancement: imageEnhancementTypes){
		builder.append(", imageEnhancementType: ").append(imageEnhancement);
	    }
	}
	
	return builder.toString();
    }

}
