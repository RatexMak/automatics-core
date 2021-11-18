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
package com.automatics.providers.connection.auth;

import java.security.GeneralSecurityException;

public interface ICrypto {
    
    /**
     * Encrypt the value using given key.
     *
     * @param value
     *            The value to be encrypted.
     *
     * @return The encrypted value.
     *
     * @throws GeneralSecurityException
     *             If any up normal execution.
     */
    public String encrypt(String value);
    

    /**
     * Decrypt the given data using known key.
     *
     * @param data
     *            The data to be decrypted.
     *
     * @return The actual decrypted value.
     *
     * @throws GeneralSecurityException
     *             If any upnormal execution.
     */
    public String decrypt(String data);
    

}
