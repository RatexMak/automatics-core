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
package com.automatics.enums;

/**
 * Enum to define different classes of devices
 * 
 * @author divya.rs
 * 
 */
public enum DeviceClass {
    
    RDKV("RDKV"),

    RDKV_CLIENT("RDKV_CLIENT"),
   
    RDKB("RDKB"),
    // Includes PI2,PI3,R2,M2U and M2M
    PI("PI", "y"),
    // Includes NUC
    NUC("NUC"),
    // Includes EXTENDER
    EXTENDER("EXTENDER"),
    // Includes iPhone7 and GalaxyS7 devices
    MOBILE("MOBILE"),
    // Include SurfacePro4 and MAC Mini
    LAPTOP("LAPTOP"),
    
    // Includes WiFi Enabled device
    WIFI_CLIENT("WIFI_CLIENT");

    String value;
    String loginRequired;

    DeviceClass(String value) {
	this.value = value;
    }

    DeviceClass(String value, String loginRequired) {
	this.value = value;
	this.loginRequired = loginRequired;
    }

    public String getValue() {
	return this.value;
    }

    public String getloginRequired() {
	return this.loginRequired;
    }

}
