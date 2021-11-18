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

package com.automatics.server;

import com.automatics.resource.IServer;
import com.automatics.tap.AutomaticsTapApi;

/**
 * @author selvarajm
 */
public class ProdVM implements IServer {

    private static ProdVM prodVm;

    private String hostIp;
    private String password;
    private String userId;

    private ProdVM(AutomaticsTapApi eCatsTap) {
	hostIp = AutomaticsTapApi.getSTBPropsValue("ci.server.ip");
	password = AutomaticsTapApi.getSTBPropsValue("ci.server.password");
	userId = AutomaticsTapApi.getSTBPropsValue("ci.server.user.id");
    }

    public static ProdVM getInstance(AutomaticsTapApi eCatsTap) {

	if (null == prodVm) {
	    prodVm = new ProdVM(eCatsTap);
	}

	return prodVm;
    }

    @Override
    public String getHostIp() {
	return hostIp;
    }

    @Override
    public String getPassword() {
	return password;
    }

    @Override
    public String getUserId() {
	return userId;
    }

}
