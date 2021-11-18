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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * 
 * Represents ImageCompareRegionInfo
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageCompareRegionInfo")
@JsonPropertyOrder(alphabetic = true)
public class ImageCompareRegionInfo extends RegionInfo {    
   
    private static final long serialVersionUID = 2454694123680934576L;

    public static final Float DEFAULT_MATCH_PERCENT = Float.valueOf(85.0F);

    public static final Float DEFAULT_COLOR_MATCH_PERCENT = Float.valueOf(70.0F);

    public static final Integer DEFAULT_RED_TOLERANCE = Integer.valueOf(20);

    public static final Integer DEFAULT_GREEN_TOLERANCE = Integer.valueOf(20);

    public static final Integer DEFAULT_BLUE_TOLERANCE = Integer.valueOf(20);

    public static final Integer MAX_RGB_TOLERANCE = Integer.valueOf(255);

    public static final Integer MIN_RGB_TOLERANCE = Integer.valueOf(0);

    public static final Float MAX_MATCH_PERCENT = Float.valueOf(100.0F);

    public static final Float MIN_MATCH_PERCENT = Float.valueOf(0.0F);

    public static final Float MAX_COLOR_MATCH_PERCENT = Float.valueOf(100.0F);

    public static final Float MIN_COLOR_MATCH_PERCENT = Float.valueOf(0.0F);

    @XmlElement(name = "red_tolerance")
    protected Integer redTolerance;

    @XmlElement(name = "green_tolerance")
    protected Integer greenTolerance;

    @XmlElement(name = "blue_tolerance")
    protected Integer blueTolerance;

    @XmlElement(name = "match_percentage")
    protected Float matchPct;

    @XmlElement(name = "color_match_percentage")
    protected Float colorMatchPct;

    public ImageCompareRegionInfo() {
	this("", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
    }

    public ImageCompareRegionInfo(String name, Integer x, Integer y, Integer w, Integer h) {
	super(name, x, y, w, h);

	this.redTolerance = DEFAULT_RED_TOLERANCE;

	this.greenTolerance = DEFAULT_GREEN_TOLERANCE;

	this.blueTolerance = DEFAULT_BLUE_TOLERANCE;

	this.matchPct = DEFAULT_MATCH_PERCENT;

	this.colorMatchPct = DEFAULT_COLOR_MATCH_PERCENT;
    }

    public ImageCompareRegionInfo(String name, Integer x, Integer y, Integer w, Integer h, String filepath) {
	super(name, x, y, w, h);

	this.redTolerance = DEFAULT_RED_TOLERANCE;

	this.greenTolerance = DEFAULT_GREEN_TOLERANCE;

	this.blueTolerance = DEFAULT_BLUE_TOLERANCE;

	this.matchPct = DEFAULT_MATCH_PERCENT;

	this.colorMatchPct = DEFAULT_COLOR_MATCH_PERCENT;

	this.filepath = filepath;
    }

    public final void setRedTolerance(Integer redTolerance) {
	this.redTolerance = redTolerance;
    }

    public final Integer getRedTolerance() {
	return this.redTolerance;
    }

    public final void setBlueTolerance(Integer blueTolerance) {
	this.blueTolerance = blueTolerance;
    }

    public final Integer getBlueTolerance() {
	return this.blueTolerance;
    }

    public final void setGreenTolerance(Integer greenTolerance) {
	this.greenTolerance = greenTolerance;
    }

    public final Integer getGreenTolerance() {
	return this.greenTolerance;
    }

    public final void setMatchPct(Float matchPct) {
	this.matchPct = matchPct;
    }

    public final Float getMatchPct() {
	return this.matchPct;
    }

    public static Float getDefaultMatchPercent() {
	return DEFAULT_MATCH_PERCENT;
    }

    public static Integer getDefaultRedTolerance() {
	return DEFAULT_RED_TOLERANCE;
    }

    public static Integer getDefaultGreenTolerance() {
	return DEFAULT_GREEN_TOLERANCE;
    }

    public static Integer getDefaultBlueTolerance() {
	return DEFAULT_BLUE_TOLERANCE;
    }

    public static Float getDefaultColorMatchPct() {
	return DEFAULT_COLOR_MATCH_PERCENT;
    }

    public static Integer getMaxRgbTolerance() {
	return MAX_RGB_TOLERANCE;
    }

    public static Integer getMinRgbTolerance() {
	return MIN_RGB_TOLERANCE;
    }

    public static Float getMaxMatchPercent() {
	return MAX_MATCH_PERCENT;
    }

    public static Float getMinColorMatchPercent() {
	return MIN_COLOR_MATCH_PERCENT;
    }

    public static Float getMaxColorMatchPercent() {
	return MAX_COLOR_MATCH_PERCENT;
    }

    public static Float getMinMatchPercent() {
	return MIN_MATCH_PERCENT;
    }

    public Float getColorMatchPct() {
	return this.colorMatchPct;
    }

    public void setColorMatchPct(Float colorMatchPct) {
	this.colorMatchPct = colorMatchPct;
    }

    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Name: ").append(this.name).append(", X: ").append(this.x).append(", Y: ").append(this.y)
		.append(", Width: ").append(this.width).append(", Height: ").append(this.height)
		.append(", Blue Tolerance: ").append(this.blueTolerance).append(", Red Tolerance: ")
		.append(this.redTolerance).append(", Green Tolerance: ").append(this.greenTolerance)
		.append(", X Tolerance: ").append(this.xTolerance).append(", Y Tolerance: ").append(this.yTolerance)
		.append(", Match Pct: ").append(this.matchPct).append(", Color Match Pct: ").append(this.colorMatchPct);
	return builder.toString();
    }

    public boolean equals(Object o) {
	boolean ret = false;
	if (!(o instanceof ImageCompareRegionInfo)) {
	    ret = false;
	} else if (this == o) {
	    ret = true;
	} else {
	    ImageCompareRegionInfo info = (ImageCompareRegionInfo) o;

	    ret = new EqualsBuilder().append(this.name, info.getName()).append(this.x, info.getX())
		    .append(this.y, info.getY()).append(this.width, info.getWidth())
		    .append(this.height, info.getHeight()).append(this.matchPct, info.getMatchPct())
		    .append(this.greenTolerance, info.getGreenTolerance())
		    .append(this.redTolerance, info.getRedTolerance())
		    .append(this.blueTolerance, info.getBlueTolerance()).append(this.xTolerance, info.getXTolerance())
		    .append(this.yTolerance, info.getYTolerance()).append(this.colorMatchPct, info.getColorMatchPct())
		    .isEquals();
	}
	return ret;
    }

    public int hashCode() {
	int TWENTY = 20;
	int TWENTY_ONE = 21;
	return new HashCodeBuilder(TWENTY, TWENTY_ONE).append(this.name).toHashCode();
    }

    public Object clone() {
	return super.clone();
    }
}