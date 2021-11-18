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

package com.automatics.resource;

import com.automatics.device.Dut;

/**
 * Multi-platform support resource selector.
 *
 * @author Pratheesh T.K.
 */
public interface IResourceLocator {
    
    /** Package prefix for applications. */
    public static final String PACKAGE_PREFIX = "com/automatics/";
    
    
    /**
     * Returns the resource base path depending on the platform.    
     * @return The resolved resource path based on platform.
     */
    String getResourceBasePath();
    
    /**
     * Sets the resource base path depending on the platform.
     *
     * @param basePackage
     *            Base package name.   
     */
    void setResourceBasePath(String basePackage);

    /**
     * Returns the full resource path depending on the platform.
     *
     * @param name
     *            Resource name.
     * @param dut
     *            Device object.
     *
     * @return The resolved resource path based on platform.
     */
    String getResource(String name, Dut dut);    
    
    /**
     * Returns the platform.
     *
     * @param dut
     *            Set-top object.
     *
     * @return the platform of the box.
     */
    String getPlatform(Dut dut);
} // end interface IResourceSelector
