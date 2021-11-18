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
package com.automatics.restclient;

/**
 * Interface for all rest client APIs
 * 
 * @author Arun V S
 *
 */
public interface RestClient {

    /**
     * Method to execute any rest API and get the response
     * 
     * @param request
     *            {@code RestRequest}
     * @return {@code Response}
     */
    RestResponse executeAndGetResponse(RestRequest request) throws RestClientException;

}
