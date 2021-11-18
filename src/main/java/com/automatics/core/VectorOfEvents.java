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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.Map;
import java.util.HashMap;

/**
 * A POJO class to set the values for nested Events in MicroEvents
 * 
 * @author gcheru200
 * 
 */

// Vector of Events
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "i", "b", "d", "o" })
public class VectorOfEvents {
    // public class V {
    // Fragment index
    @JsonProperty("i")
    private Integer i;
    // beginning
    @JsonProperty("b")
    private Integer b;
    // duration
    @JsonProperty("d")
    private Integer d;
    // Output of Event
    @JsonProperty("o")
    private Integer o;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("i")
    public Integer getI() {
	return i;
    }

    @JsonProperty("i")
    public void setI(Integer i) {
	this.i = i;
    }

    @JsonProperty("b")
    public Integer getB() {
	return b;
    }

    @JsonProperty("b")
    public void setB(Integer b) {
	this.b = b;
    }

    @JsonProperty("d")
    public Integer getD() {
	return d;
    }

    @JsonProperty("d")
    public void setD(Integer d) {
	this.d = d;
    }

    @JsonProperty("o")
    public Integer getO() {
	return o;
    }

    @JsonProperty("o")
    public void setO(Integer o) {
	this.o = o;
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
