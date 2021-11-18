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

import org.mockito.Mockito;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automatics.providers.impl.DeviceManagerRestImpl;
import com.automatics.providers.rack.DeviceProvider;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.BeanConstants;

public class DeviceManagerTest {

    private DeviceProvider deviceProvider;
    private ClassPathXmlApplicationContext partnerContext;

    @BeforeClass
    public void beforeClass() {
	System.setProperty("automatics.properties.file", "https://localhost/AutomaticsProps/stb.properties");
	AutomaticsPropertyUtility.loadProperties();
	deviceProvider = new DeviceManagerRestImpl();
	partnerContext = new ClassPathXmlApplicationContext(BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
    }

    @Test(enabled = false)
    public void getDeviceDetailsSuccessTest() {

	Mockito.when(partnerContext.getBean("deviceProvider", DeviceProvider.class)).thenReturn(deviceProvider);

	DeviceManager deviceManager = DeviceManager.getInstance();

	deviceManager.findRackDevice("");
    }

}
