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
 
package com.automatics.manager.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automatics.providers.impl.DeviceManagerRestImpl;
import com.automatics.providers.objects.DeviceAccountRequest;
import com.automatics.providers.objects.DeviceAccountResponse;
import com.automatics.providers.objects.DeviceAllocationResponse;
import com.automatics.providers.objects.DevicePropsRequest;
import com.automatics.providers.objects.DeviceRequest;
import com.automatics.providers.objects.DeviceResponse;
import com.automatics.providers.objects.DeviceUpdateDurationRequest;
import com.automatics.providers.objects.StatusResponse;
import com.automatics.providers.objects.enums.DeviceAllocationStatus;
import com.automatics.providers.objects.enums.StatusMessage;
import com.automatics.utils.AutomaticsPropertyUtility;

public class DeviceManagerRestImplTest {

    private DeviceManagerRestImpl deviceManager;

    @BeforeClass
    public void beforeClass() {
	System.setProperty("automatics.properties.file", "http://localhost/AutomaticsProps/stb.properties");
	AutomaticsPropertyUtility.loadProperties();
	deviceManager = new DeviceManagerRestImpl();
    }

    @Test(enabled = false)
    public void getDeviceSuccessTest() {
	DeviceRequest request = new DeviceRequest();
	request.setMac("");
	DeviceResponse resp = deviceManager.getDevice(request);
	Assert.assertEquals(resp.getDevices().get(0).getHostMacAddress(), "");
    }

    @Test(enabled = false)
    public void getAccountDetailsForDeviceSuccessTest() {
	DeviceAccountRequest request = new DeviceAccountRequest();
	request.setAccountNumber("");
	DeviceAccountResponse resp = deviceManager.getAccountDetailsForDevice(request);
	Assert.assertEquals(resp.getAccountNumber(), "");
    }

    @Test(enabled = false)
    public void getDevicePropsSuccessTest() {
	DevicePropsRequest request = new DevicePropsRequest();
	request.setMac("");
	List<String> lst = new ArrayList<String>();
	lst.add("HEAD_END");
	request.setRequestedPropsName(lst);
	Map<String, String> resp = deviceManager.getDeviceProperties(request);
	Assert.assertEquals(resp.get("HEAD_END"), "");
    }

    @Test(enabled = false)
    public void isLockedSuccessTest() {
	DeviceRequest request = new DeviceRequest();
	request.setMac("");
	DeviceAllocationResponse resp = deviceManager.isLocked(request);
	if (resp.getAllocationStatus().toString().equalsIgnoreCase("LOCKED")) {
	    Assert.assertEquals(resp.getAllocationStatus(), DeviceAllocationStatus.LOCKED);
	} else if (resp.getAllocationStatus().toString().equalsIgnoreCase("AVAILABLE")) {
	    Assert.assertEquals(resp.getAllocationStatus(), DeviceAllocationStatus.AVAILABLE);
	}
    }

    @Test(enabled = false)
    public void lockSuccessTest() {
	DeviceRequest request = new DeviceRequest();
	request.setMac("");
	DeviceAllocationResponse statusResp = deviceManager.isLocked(request);
	if (statusResp.getAllocationStatus().toString().equalsIgnoreCase("LOCKED")) {
	    deviceManager.release(request);
	}
	StatusResponse resp = deviceManager.lock(request);
	Assert.assertEquals(resp.getStatus(), StatusMessage.SUCCESS);
    }

    @Test(enabled = false)
    public void releaseSuccessTest() {
	DeviceRequest request = new DeviceRequest();
	request.setMac("");

	DeviceAllocationResponse statusResp = deviceManager.isLocked(request);
	if (statusResp.getAllocationStatus().toString().equalsIgnoreCase("AVAILABLE")) {
	    deviceManager.lock(request);
	}
	StatusResponse resp = deviceManager.release(request);
	Assert.assertEquals(resp.getStatus(), StatusMessage.SUCCESS);
    }

    @Test(enabled = false)
    public void updateLockTimeSuccessTest() {
	DeviceUpdateDurationRequest durationRequest = new DeviceUpdateDurationRequest();
	DeviceRequest request = new DeviceRequest();
	request.setMac("");
	durationRequest.setMac("");
	durationRequest.setLockDurationInMins(60);
	DeviceAllocationResponse statusResp = deviceManager.isLocked(request);
	if (statusResp.getAllocationStatus().toString().equalsIgnoreCase("AVAILABLE")) {
	    deviceManager.lock(request);
	}
	StatusResponse resp = deviceManager.updateLockTime(durationRequest);
	Assert.assertEquals(resp.getStatus(), StatusMessage.SUCCESS);
	deviceManager.release(request);
    }

}
