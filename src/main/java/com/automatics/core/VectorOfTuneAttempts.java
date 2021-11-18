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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A POJO class to set the values for Tune attempts in MicroEvents
 * 
 * @author gcheru200
 * 
 */

// Vector of tune Attempts
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "s", "td", "st", "u", "r", "v" })
public class VectorOfTuneAttempts {
    // public class Va {

    // UTC time (ms)
    @JsonProperty("s")
    private Long s;
    // Time duration
    @JsonProperty("td")
    private Integer td;
    // DASH/WV
    @JsonProperty("st")
    private String st;
    // mm ipvod
    @JsonProperty("u")
    private String u;
    // Result
    @JsonProperty("r")
    private Integer r;
    // Vector of events
    @JsonProperty("v")
    private List<VectorOfEvents> v = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    // UTC time
    @JsonProperty("s")
    public Long getS() {
	return s;
    }

    @JsonProperty("s")
    public void setS(Long s) {
	this.s = s;
    }

    @JsonProperty("td")
    public Integer getTd() {
	return td;
    }

    @JsonProperty("td")
    public void setTd(Integer td) {
	this.td = td;
    }

    @JsonProperty("st")
    public String getSt() {
	return st;
    }

    @JsonProperty("st")
    public void setSt(String st) {
	this.st = st;
    }

    @JsonProperty("u")
    public String getU() {
	return u;
    }

    @JsonProperty("u")
    public void setU(String u) {
	this.u = u;
    }

    @JsonProperty("r")
    public Integer getR() {
	return r;
    }

    @JsonProperty("r")
    public void setR(Integer r) {
	this.r = r;
    }

    @JsonProperty("v")
    public List<VectorOfEvents> getV() {
	return v;
    }

    @JsonProperty("v")
    public void setV(List<VectorOfEvents> v) {
	this.v = v;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }

}
