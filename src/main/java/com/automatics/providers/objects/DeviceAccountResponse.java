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
package com.automatics.providers.objects;

import java.util.List;

/**
 * DeviceAccountDetails class
 * 
 * 
 * @author Raja M
 */

public class DeviceAccountResponse {

    private String id;

    private String name;

    private String accountNumber;

    private String phoneNumber;

    private String address;

    private String homeAccountGroup;

    private List<DeviceObject> deviceObjects;

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
	return accountNumber;
    }

    /**
     * @param accountNumber
     *            the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
	return phoneNumber;
    }

    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    /**
     * @return the address
     */
    public String getAddress() {
	return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(String address) {
	this.address = address;
    }

    /**
     * @return the homeAccountGroup
     */
    public String getHomeAccountGroup() {
	return homeAccountGroup;
    }

    /**
     * @param homeAccountGroup
     *            the homeAccountGroup to set
     */
    public void setHomeAccountGroup(String homeAccountGroup) {
	this.homeAccountGroup = homeAccountGroup;
    }

    /**
     * @return the deviceObjects
     */
    public List<DeviceObject> getDevices() {
	return deviceObjects;
    }

    /**
     * @param deviceObjects
     *            the deviceObjects to set
     */
    public void setDevices(List<DeviceObject> deviceObjects) {
	this.deviceObjects = deviceObjects;
    }

}
