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

/**
 * Interface to have server details.
 */
public interface IServer {

    /**
     * Provides the host IP of the server.
     *
     * @return the host IP.
     */
    public abstract String getHostIp();

    /**
     * Provides the user ID for establishing the connection.
     *
     * @return the user ID.
     */
    public abstract String getUserId();

    /**
     * Provides the password for establishing the connection.
     *
     * @return the password of server.
     */
    public abstract String getPassword();
}
