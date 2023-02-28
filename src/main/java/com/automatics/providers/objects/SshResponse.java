/**
 * Copyright 2022 Comcast Cable Communications Management, 

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * Holds SSH Connection Details
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SshResponse {

    private String sshUserName;

    private String sshPassword;

    private String sshAuthType;

    private String sshPrivateKeyLocation;
    
    private String sshPortNumber;

    /**
     * 
     * @return sshUsername
     */

    public String getSshUserName() {
	return sshUserName;
    }

    /**
     * 
     * @param sshUserName
     *            the sshUserName to set
     */

    public void setSshUserName(String sshUserName) {
	this.sshUserName = sshUserName;
    }

    /**
     * 
     * @return sshPassword
     */
    public String getSshPassword() {
	return sshPassword;
    }

    /**
     * 
     * @param sshPassword
     *            the sshPassword to set
     */
    public void setSshPassword(String sshPassword) {
	this.sshPassword = sshPassword;
    }

    /**
     * 
     * @return sshAuthType
     */

    public String getSshAuthType() {
	return sshAuthType;
    }

    /**
     * 
     * @param sshAuthType
     *            the sshAuthType to set
     */
    public void setSshAuthType(String sshAuthType) {
	this.sshAuthType = sshAuthType;
    }

    /**
     * 
     * @return sshPrivatekeyLocation
     */
    public String getSshPrivateKeyLocation() {
	return sshPrivateKeyLocation;
    }

    /**
     * 
     * @param sshPrivateKeyLocation
     *            the sshPrivateKeyLocation to set
     */
    public void setSshPrivateKeyLocation(String sshPrivateKeyLocation) {
	this.sshPrivateKeyLocation = sshPrivateKeyLocation;
    }
    /**
     * 
     * @return sshPortnumber
     */
    public String getSshPortNumber() {
        return sshPortNumber;
    }
    /**
     * 
     * @param sshPortNumber
     * 		the sshPortnumber to set
     */
    public void setSshPortNumber(String sshPortNumber) {
        this.sshPortNumber = sshPortNumber;
    }
    
    
}
