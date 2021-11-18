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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Dut;

/**
 * General Multi-platform resource selector implementation.
 *
 * @author Pratheesh TK
 */
public class ResourceLocatorImpl implements IResourceLocator {

    /** SLF4J logger instance. */
    protected final Logger LOGGER = LoggerFactory.getLogger(ResourceLocatorImpl.class);

    /** Project Name for the given resource selector. */
    protected String projectName = null;

    /** Base package for the given resource selector. */
    protected String basePackage = null;

    /**
     * Constructor - projectName refers to your project name.
     *
     * @param projectName
     *            Project name.
     */
    public ResourceLocatorImpl(String projectName) {
	this.projectName = projectName;
	this.basePackage = PACKAGE_PREFIX;
    }

    /**
     * Provide the platform of the set-top box.
     *
     * @param dut
     *            Set-top object.
     */
    @Override
    public String getPlatform(Dut dut) {

	return (dut.getManufacturer() + '_' + dut.getModel()).toLowerCase();
    } // end method getPlatform

    /**
     * Return the resource path.
     *
     * @param resourceName
     *            Resource name.
     * @param dut
     *            Dut box object.
     *
     * @return complete resource path of the resource name provided.
     */
    @Override
    public String getResource(String resourceName, Dut dut) {
	LOGGER.info("Selecting resource: " + basePackage + projectName + "/" + resourceName);
	return basePackage + projectName + "/" + getPlatform(dut) + "/" + resourceName;

    }

    @Override
    public String getResourceBasePath() {
	return basePackage;
    }

    @Override
    public void setResourceBasePath(String basePackage) {
	this.basePackage = basePackage;

    }

} // end class ResourceLocatorImpl
