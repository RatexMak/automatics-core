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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * 
 * Represents ImageRegionInfo
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "region_info_list")
@JsonPropertyOrder(alphabetic = true)
public class ImageRegionInfo implements Serializable, Cloneable {
    
    private static final long serialVersionUID = -1L;

    @XmlElement(name = "image_path")
    protected String imagePath = "";

    @XmlElement(name = "image_compare_region")
    protected List<ImageCompareRegionInfo> icRegionList;

    @XmlElement(name = "ocr_region")
    protected List<OcrRegionInfo> ocrRegionList;

    @XmlElement(name = "search_region")
    protected List<RegionInfo> searchRegionList;

    public ImageRegionInfo() {
	this.icRegionList = new ArrayList<ImageCompareRegionInfo>();
	this.ocrRegionList = new ArrayList<OcrRegionInfo>();
	this.searchRegionList = new ArrayList<RegionInfo>();
    }

    public final void addRegionInfo(RegionInfo info) {
	if (info == null) {
	    throw new IllegalArgumentException("info cannot be null");
	}
	if (info instanceof ImageCompareRegionInfo) {
	    this.icRegionList.add((ImageCompareRegionInfo) info);
	} else if (info instanceof OcrRegionInfo) {
	    this.ocrRegionList.add((OcrRegionInfo) info);
	} else {
	    this.searchRegionList.add(info);
	}
    }

    public final void addRegionInfoAt(int idx, RegionInfo info) {
	if (info == null) {
	    throw new IllegalArgumentException("info cannot be null");
	}
	if (info instanceof ImageCompareRegionInfo) {
	    this.icRegionList.add(idx, (ImageCompareRegionInfo) info);
	} else if (info instanceof OcrRegionInfo) {
	    this.ocrRegionList.add(idx, (OcrRegionInfo) info);
	} else {
	    this.searchRegionList.add(idx, info);
	}
    }

    public final void setImagePath(String imagePath) {
	if (imagePath == null) {
	    throw new IllegalArgumentException("imagePath cannot be null");
	}
	this.imagePath = imagePath;
    }

    public final String getImagePath() {
	return this.imagePath;
    }

    public final List<ImageCompareRegionInfo> getImageCompareRegionInfoList() {
	return this.icRegionList;
    }

    protected final List<OcrRegionInfo> getOcrRegionInfoList() {
	return this.ocrRegionList;
    }

    protected final List<RegionInfo> getSearchRegionInfoList() {
	return this.searchRegionList;
    }

    public List<RegionInfo> getRegionInfoList() {
	List<RegionInfo> regionInfoList = new ArrayList<RegionInfo>();
	regionInfoList.addAll(getOcrRegionInfoList());
	regionInfoList.addAll(getImageCompareRegionInfoList());

	regionInfoList.addAll(getSearchRegionInfoList());
	return regionInfoList;
    }

    public final void setRegionInfoList(List<RegionInfo> regionInfoList) {
	for (RegionInfo info : regionInfoList) {
	    addRegionInfo(info);
	}
    }

    public final RegionInfo getRegion(String regionName) {
	RegionInfo retVal = null;
	synchronized (this) {
	    Iterator<RegionInfo> iter = getRegionInfoList().iterator();
	    while (iter.hasNext()) {
		RegionInfo next = (RegionInfo) iter.next();
		if (next.getName().equalsIgnoreCase(regionName)) {
		    retVal = next;
		    break;
		}
	    }
	}
	return retVal;
    }

    public final synchronized void deleteRegion(String regionName) {
	RegionInfo info = getRegion(regionName);
	if (null == info)
	    return;
	if (info instanceof ImageCompareRegionInfo) {
	    this.icRegionList.remove((ImageCompareRegionInfo) info);
	} else if (info instanceof OcrRegionInfo) {
	    this.ocrRegionList.remove((OcrRegionInfo) info);
	} else {
	    this.searchRegionList.remove(info);
	}
    }

    public boolean equals(Object o) {
	boolean ret = false;
	if (!(o instanceof ImageRegionInfo)) {
	    ret = false;
	} else if (this == o) {
	    ret = true;
	} else {
	    ImageRegionInfo imgInfo = (ImageRegionInfo) o;

	    if (this.imagePath.equals(imgInfo.getImagePath())) {
		EqualsBuilder eBuilder = new EqualsBuilder().append(this.imagePath, imgInfo.getImagePath());
		List<RegionInfo> infoList = imgInfo.getRegionInfoList();

		synchronized (this) {
		    List<ImageCompareRegionInfo> icRegionInfoList = this.icRegionList;
		    for (int i = 0; i != icRegionInfoList.size(); ++i) {
			eBuilder.append(icRegionInfoList.get(i), (ImageCompareRegionInfo) infoList.get(i));
		    }
		    List<OcrRegionInfo> ocrRegionInfoList = this.ocrRegionList;
		    for (int i = 0; i != ocrRegionInfoList.size(); ++i) {
			eBuilder.append(ocrRegionInfoList.get(i), (OcrRegionInfo) infoList.get(i));
		    }
		    List<RegionInfo> searchRegionInfoList = this.searchRegionList;
		    for (int i = 0; i != searchRegionInfoList.size(); ++i) {
			eBuilder.append(searchRegionInfoList.get(i), infoList.get(i));
		    }
		}
		ret = eBuilder.isEquals();
	    }
	}
	return ret;
    }

    public void setRefImage(BufferedImage refImage) {
	if (this.icRegionList == null)
	    return;
	for (ImageCompareRegionInfo region : this.icRegionList) {
	    region.setRefImage(refImage);
	}
    }

    public BufferedImage getRefImage() {
	BufferedImage refImage = null;
	if ((this.icRegionList != null) && (!(this.icRegionList.isEmpty()))) {
	    RegionInfo regionInfo = (RegionInfo) this.icRegionList.get(0);
	    refImage = regionInfo.getRefImage();
	}
	return refImage;
    }

}
