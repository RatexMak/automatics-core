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

package com.automatics.providers.imageupgrade;

/**
 * Request for Image upgrade via XConf
 *
 *
 */
public class XConfRequestParams extends ImageRequestParams {

    private String xConfServerUrl;

    /**
     * @return the xConfServerUrl
     */
    public String getxConfServerUrl() {
	return xConfServerUrl;
    }

    /**
     * @param xConfServerUrl
     *            the xConfServerUrl to set
     */
    public void setxConfServerUrl(String xConfServerUrl) {
	this.xConfServerUrl = xConfServerUrl;
    }

}
