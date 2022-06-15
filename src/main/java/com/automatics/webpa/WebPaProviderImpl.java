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
package com.automatics.webpa.impl;

import java.util.HashMap;
import java.util.Map;

import com.automatics.device.Dut;
import com.automatics.providers.webpa.WebpaProvider;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.webpa.WebPaConnectionHandler.WebPaType;

/**
 * WebPa Provider implementation class
 * 
 * @author radhikas
 *
 */
public class WebPaProviderImpl implements WebpaProvider {

    private static final String PROPS_RDKM_WEBPA_AUTH_KEY = "WEBPA_AUTH_KEY";

    /**
     * Gets the mac address to be used for webpa communication
     */
    @Override
    public String getDeviceMacAddress(Dut device) {
	return device.getHostMacAddress();
    }

    /**
     * Gets the authorization header for webpa communication.
     */
    @Override
    public Map<String, String> getRequestHeaderAuthData(WebPaType webPaMethod) {

	String authKey = AutomaticsTapApi.getSTBPropsValue(PROPS_RDKM_WEBPA_AUTH_KEY);
	Map<String, String> authMap = new HashMap<String, String>();
	authMap.put("Authorization", authKey);
	return authMap;
    }

}
