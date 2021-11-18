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

import java.util.HashMap;
import java.util.Map;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

/**
 * This class holds the login credentials for a single server machine. This object should be grabbed from the static
 * class CredentialFactory. A call similar to CredentialFactory.getServerCredentials(fqdn) would return this object.
 * Internally the username/password pairs are stored in a hash map and it is possible to get an empty response for an
 * unknown user.
 *
 * @author Selvaraj Mariyappan
 */
public class Credential {

    /** The fully qualified domain name of server. */
    private String qualifiedDomainName = null;

    /** Default login user-name. */
    private String defaultUserName = null;

    /** location of the private key. */
    private String privateKeyLocation = null;

    /** The hashmap which stores username/password pairs. */
    private final Map<String, String> CREDENTIALMAP;

    /**
     * Constructor which will instantiate an empty credential hashmap but it will properly setup the fqdn for the
     * machine. This is the minimum required information in order to create a Credential object.
     *
     * @param fqdn
     *            The fully qualified domain name of this particular server. The factory will also use this value to
     *            lookup this object but that is explained in the CredentialFactory java docs.
     */
    public Credential(String fqdn) {
	this(fqdn, new HashMap<String, String>());
    }

    /**
     * Constructor to be used when you already know everything this credential class requires. In this case this class
     * acts more as a handy place to store all this information for later.
     *
     * @param fqdn
     *            Fully Qualified Domain Name of this particular machine. This should never be empty.
     * @param defaultLogin
     *            A known user which can login to a system. On an opsware machine root for example would not be a
     *            default login (admin, or possibly the red user would be).
     * @param map
     *            A hashmap of username/password key/value pairs. This is what is used when getPassword() is called.
     */
    public Credential(String fqdn, String defaultLogin, Map<String, String> map) {
	this(fqdn, map);
	this.defaultUserName = defaultLogin;
    }

    /**
     * A constructor which does not have a default login. This is a bad constructor to use as all TVP Machines should
     * have a default login. Classes such as MachineImpl require a default user in order to provide a shell which is
     * automatically logged in.
     *
     * @param fqdn
     *            Fully Qualified Domain Name for this particular machine. This should not be null or empty.
     * @param map
     *            A Map of username/password key/value pairs.
     */
    public Credential(String fqdn, Map<String, String> map) {
	this.qualifiedDomainName = fqdn;
	CREDENTIALMAP = map;
    }

    /**
     * Add or replace a username/password pair to the Map. This is meant to assist in dynamically building this object.
     *
     * @param user
     *            Username to be stored as the key in the Map
     * @param pass
     *            Password for the associated username, this will be the value returned for the username key.
     */
    public void addLogin(String user, String pass) {
	CREDENTIALMAP.put(user, pass);
    }

    /**
     * This is used to retrieve passwords for known users. If a username is not known then this throw an
     * FailedTransitionException.
     *
     * @param user
     *            Username to lookup. If the password is not known for this user it will throw an
     *            FailedTransitionException.
     *
     * @return Password as a String variable. This is the known password for the given username.
     */
    public String getPassword(String user) {

	if (CREDENTIALMAP.isEmpty()) {
	    throw new FailedTransitionException(GeneralError.FAILED_RESOURCE_READ,
		    "Unable to locate the password for user: " + user);
	}

	return CREDENTIALMAP.get(user);
    }

    /**
     * Every Server is assumed to have a default login. Generally this would be the admin user, but it could also be the
     * red user, or any user which has ssh login privledges on the machine. This is what is used when
     * Machine.getSSHConnection() is called.
     *
     * @return The default login for this particular machine.
     */
    public String getDefaultLogin() {
	return defaultUserName;
    }

    /**
     * Every Server should have a default user to assist with logging in to the machine. This function is used to assist
     * dynamically creating the Credential object in the CredentialFactory class.
     *
     * @param user
     *            The default username to be used when logging into this machine.
     */
    public void setDefaultLogin(String user) {
	defaultUserName = user;
    }

    /**
     * This function is to find the fqdn of this particular machine. It isn't overly userful as you probably need the
     * FQDN to get this object, but it may be handy at some point. At the very least it will assist with unit testing.
     *
     * @return The fully qualified domain name for this particular machine
     */
    public String getMachineQualifiedDomainName() {
	return qualifiedDomainName;
    }

    /**
     * For a key based login mechanism, the private key location is required. This method sets that location.
     * 
     * @param privateKeyLocation
     */
    public void setPrivateKeyLocation(String privateKeyLocation) {
	this.privateKeyLocation = privateKeyLocation;
    }

    /**
     * For a key based login mechanism, the private key location is required. This method returns that location.
     * 
     * @return privateKeyLocation
     */
    public String getPrivateKeyLocation() {
	return privateKeyLocation;
    }

    /**
     * A string representation of this object. It will show the fully qualified domain name, default user, and then
     * comma separated username,password pairs. This function is primarily for debugging purposes.
     *
     * @return The string representation of this object. More details are shown above in the header for this function.
     */
    public String toString() {
	StringBuilder ret = new StringBuilder();

	ret.append("Server: " + qualifiedDomainName + "\n");
	ret.append("Default Login: " + defaultUserName + "\n");

	for (String key : CREDENTIALMAP.keySet()) {
	    ret.append("(Username,Password):(" + key + "," + CREDENTIALMAP.get(key) + ")\n");
	}

	return ret.toString();
    }
}
