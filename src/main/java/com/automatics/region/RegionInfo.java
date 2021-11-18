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

import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * Represents a region in image. Contains details about image region.
 *
 */
public class RegionInfo implements Serializable, Cloneable {

    
    private static final long serialVersionUID = -556434976215147686L;

    public static final Integer DEFAULT_X_TOLERANCE = Integer.valueOf(10);

    public static final Integer DEFAULT_Y_TOLERANCE = Integer.valueOf(10);
    protected String name;
    protected Integer x;
    protected Integer y;
    protected Integer width;
    protected Integer height;   
    protected Integer xTolerance;
    protected Integer yTolerance;
    protected String filepath;
    protected BufferedImage refImage;

    public RegionInfo() {
	this("", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
    }

    public RegionInfo(String name, Integer x, Integer y, Integer w, Integer h) {
	this.name = name;
	this.x = x;
	this.y = y;
	this.width = w;
	this.height = h;
	this.xTolerance = DEFAULT_X_TOLERANCE;
	this.yTolerance = DEFAULT_Y_TOLERANCE;
    }

    public final void setWidth(Integer width) {
	this.width = width;
    }

    @XmlElement(name = "Width")
    @JsonProperty("Width")
    public final Integer getWidth() {
	return this.width;
    }

    public final void setFilepath(String filepath) {
	this.filepath = filepath;
    }

    @JsonProperty("filepath")
    public final String getFilepath() {
	return this.filepath;
    }

    public final void setHeight(Integer height) {
	this.height = height;
    }

    @XmlElement(name = "Height")
    @JsonProperty("Height")
    public final Integer getHeight() {
	return this.height;
    }

    public final void setY(Integer y) {
	this.y = y;
    }

    @XmlElement(name = "Y")
    @JsonProperty("Y")
    public final Integer getY() {
	return this.y;
    }

    public void setX(Integer x) {
	this.x = x;
    }

    @XmlElement(name = "X")
    @JsonProperty("X")
    public final Integer getX() {
	return this.x;
    }

    public final void setName(String name) {
	this.name = name;
    }

    @JsonProperty("name")
    public final String getName() {
	return this.name;
    }

    public final void setXTolerance(Integer xTolerance) {
	this.xTolerance = xTolerance;
    }

    @JsonProperty("XTolerance")
    @XmlElement(name = "XTolerance")
    public final Integer getXTolerance() {
	return this.xTolerance;
    }

    public final void setYTolerance(Integer yTolerance) {
	this.yTolerance = yTolerance;
    }
    
    @JsonProperty("YTolerance")
    @XmlElement(name = "YTolerance")
    public final Integer getYTolerance() {
	return this.yTolerance;
    }

    public void setRefImage(BufferedImage image) {
	this.refImage = image;
    }

    public BufferedImage getRefImage() {
	return this.refImage;
    }

    /**
     * Gets object data in string format
     */
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Name: ").append(this.name).append(", X: ").append(this.x).append(", Y: ").append(this.y)
		.append(", Width: ").append(this.width).append(", Height: ").append(this.height)
		.append(", X Tolerance: ").append(this.xTolerance).append(", Y Tolerance: ").append(this.yTolerance)
		.append(", File path: ").append(this.filepath);
	return builder.toString();
    }

    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    throw new InternalError(e.toString());
	}
    }

}
