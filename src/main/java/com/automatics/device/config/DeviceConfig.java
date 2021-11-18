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
package com.automatics.device.config;

public class DeviceConfig {

    public String name;
    public String automaticsModelName;
    public String[] rackModelNames;
    public String inventoryModelName;
    public String[] groups;
    public String category;
    public String accessibleMechanism;
    public boolean accessbilityCheck;    
    private int waitTimeAfterHardReboot;
    public String outputPath;

    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * @param outputPath the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * @return the waitTimeAfterHardReboot
     */
    public int getWaitTimeAfterHardReboot() {
	return waitTimeAfterHardReboot;
    }

    /**
     * @param waitTimeAfterHardReboot
     *            the waitTimeAfterHardReboot to set
     */
    public void setWaitTimeAfterHardReboot(int waitTimeAfterHardReboot) {
	this.waitTimeAfterHardReboot = waitTimeAfterHardReboot;
    }

    // getters , setters, some boring stuff
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAutomaticsModelName() {
	return automaticsModelName;
    }

    public void setAutomaticsModelName(String automaticsModelName) {
	this.automaticsModelName = automaticsModelName;
    }

    public String[] getRackModelNames() {
	return rackModelNames;
    }

    public void setRackModelNames(String[] rackModelNames) {
	this.rackModelNames = rackModelNames;
    }

    public String getInventoryModelName() {
        return inventoryModelName;
    }

    public void setInventoryModelName(String inventoryModelName) {
        this.inventoryModelName = inventoryModelName;
    }

    public String[] getGroups() {
	return groups;
    }

    public void setGroups(String[] groups) {
	this.groups = groups;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public String getAccessibleMechanism() {
	return accessibleMechanism;
    }

    public void setAccessibleMechanism(String accessibleMechanism) {
	this.accessibleMechanism = accessibleMechanism;
    }

    public boolean isAccessbilityCheck() {
	return accessbilityCheck;
    }

    public void setAccessbilityCheck(boolean accessbilityCheck) {
	this.accessbilityCheck = accessbilityCheck;
    }
}
