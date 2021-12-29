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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.CommonMethods;

/**
 * This class is a credential factory. The idea is that it will load up environment config files in order to determine
 * the username and password for a given machine. At the time of writing this only Credentials are implemented. The
 * expected credentials are admin and root as a hashmap, although this should be extended to include ssh keys.
 *
 * <p>
 * Internally this class keeps a hash map of hash maps. So given a machine you can get a hashmap of username/passwords.
 * This is used to build a Credential object representing the machine.
 * </p>
 *
 * @author cpeglar
 */
public class CredentialFactory {

    /** The single ton instance of {@link CredentialFactory} class. */
    private static CredentialFactory credentialFactory = null;

    /** The Hash map holds the server details. */
    private final Map<String, Object> SERVER_DETAILS = new HashMap<String, Object>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialFactory.class);

    /**
     * Create a CredentialFactory.
     */
    private CredentialFactory() {
	loadCredentials();
    }

    /**
     * Method to get the {@link CredentialFactory} single ton instance.
     *
     * @return {@link CredentialFactory} instance.
     */
    public static synchronized CredentialFactory get() {

	if (null == credentialFactory) {
	    credentialFactory = new CredentialFactory();
	}

	return credentialFactory;
    }

    /**
     * This will return the Credential object which represents the machine given. All machines need to be fully
     * qualified domain names. If the machine is not found then an IllegalStateException will be thrown.
     *
     * @param machineName
     *            The fully qualified domain name of a TVP Server
     *
     * @return A Credential object. This is essentially a mapping of usernames to passwords in a big hash map. In
     *         general this will hold admin and root in order to get onto the machine and become root. From there one
     *         may su into any user they please.
     */
    public synchronized Credential getServerCredentials(String machineName) {
	Credential ret = null;

	ret = (Credential) SERVER_DETAILS.get(machineName);

	if (ret == null) {
	    throw new FailedTransitionException(GeneralError.PROVIDED_RESOURCE_NOT_FOUND, "Unable to find "
		    + machineName + " in the credentials file.");
	}

	return ret;
    }

    /**
     * This method is purely for unit testing. Although there may be a situation when you would want to clear all the
     * credentials, I really can't picture that right now. It would make more sense to keep the credentials you have and
     * if you want some refreshed just write over top of them.
     */
    void clearAllCredentials() {
	SERVER_DETAILS.clear();
    }

    /**
     * This method parse the configuration files and update the server details with required informantions.
     */
    private synchronized void loadCredentials() {
	String configFile = null;
	String configFileFromProperties = AutomaticsPropertyUtility.getProperty("serverConfig.path");
	try {
	    InputStream is = null;
	    if (CommonMethods.isNotNull(configFileFromProperties)) {
		configFile = configFileFromProperties;
		LOGGER.info("Reading server-config.xml from {}", configFile);
		is = new FileInputStream(configFile);
	    } else {
		LOGGER.info("Reading server-config.xml from classpath");
		configFile = "server-config.xml";
		is = this.getClass().getClassLoader().getResourceAsStream(configFile);
	    }
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(is);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("authorisation");

	    for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		String machineFqdn = nNode.getParentNode().getAttributes().getNamedItem("name").getNodeValue();

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element eElement = (Element) nNode;

		    String auth = eElement.getAttribute("auth-type");
		    String user = eElement.getAttribute("username");
		    String pass = eElement.getAttribute("password");

		    String privateKeyLocation = eElement.getAttribute("private-key");
		    String defaultUserName = eElement.getAttribute("default-user");

		    if (auth.equals("password") || auth.equals("private-key")) {
			Credential m = (Credential) SERVER_DETAILS.get(machineFqdn);

			if (m == null) {
			    m = new Credential(machineFqdn);
			}

			m.addLogin(user, pass);

			if (defaultUserName != null && !defaultUserName.trim().isEmpty()) {
			    m.setDefaultLogin(defaultUserName);
			} else {
			    m.setDefaultLogin(user);
			}

			if (auth.equals("private-key")) {
			    if (privateKeyLocation != null && !privateKeyLocation.trim().isEmpty()) {
				m.setPrivateKeyLocation(privateKeyLocation);
			    } else {
				LOGGER.error(
					"***** PRIVATE KEY LOCATION NOT DEFINED FOR {}  IN CONFIGURATION FILE *******",
					machineFqdn);
			    }
			}

			SERVER_DETAILS.put(machineFqdn, m);
		    }
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error reading {}: {}", configFile, e.getMessage(), e);
	}
    }

    /**
     * This function returns an ArrayList of all the machines used. Internally it has a hashmap of fqdns
     * and the objects associated with them. This function will iterate through those and return only the keys
     * associated with Credential objects.
     *
     * @return An ArrayList of strings. Each string is a fully qualified domain name for the server. It is also the key
     *         used to return a Credential through the function getServerCredentials(fqdn).
     */

    public synchronized List<String> getServerList() {
	List<String> servers = new ArrayList<String>();

	for (String key : SERVER_DETAILS.keySet()) {

	    if (SERVER_DETAILS.get(key) instanceof Credential) {
		servers.add(key);
	    }
	}

	return servers;
    }
}
