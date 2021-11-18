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

package com.automatics.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.DeviceAccountInfo;
import com.automatics.manager.device.DeviceManager;

/**
 * This class interact with rack and retrieve account level info
 * 
 *
 * @author reena
 * 
 */
public class DeviceAccountRestClient {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAccountRestClient.class);

    /**
     * Used to retrieve home account by specified device mac
     * 
     * @param settopMac
     * @return
     */
    public static final String getHomeAccount(String deviceMac) {

	String homeAct = "";

	DeviceManager deviceManager = DeviceManager.getInstance();
	com.automatics.device.Device device = deviceManager.findRackDevice(deviceMac);
	if (null != device) {
	    homeAct = device.getHomeAccountNumber();
	}
	LOGGER.info("Device account {} for device {}", homeAct, deviceMac);
	return homeAct;
    }

    /**
     * Used to retrieve list of dut macs by specified home account
     * 
     * @param homeAct
     * @return
     */
    public static final List<String> getSettopMacsFromHomeAccount(String homeAct) {
	DeviceAccountInfo deviceAccount = null;
	List<String> macIds = new ArrayList<String>();

	deviceAccount = getAccountDetails(homeAct);
	if (null != deviceAccount && null != deviceAccount.getDevices()) {
	    for (com.automatics.device.Device device : deviceAccount.getDevices()) {
		macIds.add(device.getHostMacAddress());
	    }
	}

	return macIds;
    }

    public static DeviceAccountInfo getAccountDetails(String deviceAccount) {

	DeviceManager deviceManager = DeviceManager.getInstance();
	return deviceManager.getAccountDetailsForDevice(deviceAccount);

    }

    /**
     * Used to retrieve QuadAtten url from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String retrieveQuadAttenURL(String homeAct, DeviceAccountInfo deviceAccount) {

	String quadAttenURL = null;
	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten URL from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		quadAttenURL = extraProps.get("QUAD_ATTEN_URL");
		LOGGER.info("quadAttenURL = {}", quadAttenURL);
	    } else {
		LOGGER.info("No Field extraProperties/QUAD_ATTEN_URL in account details " + homeAct);
	    }
	}

	return quadAttenURL;
    }

    /**
     * Used to retrieve QuadAtten device id from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String retrieveQuadAttenDeviceId(String homeAct, DeviceAccountInfo deviceAccount) {

	String quadAttenId = null;

	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten id from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		quadAttenId = extraProps.get("QUAD_ATTEN_ID");
		LOGGER.info("quadAttenId = {}", quadAttenId);
	    } else {
		LOGGER.info("No Field extraProperties/QUAD_ATTEN_ID in account details " + homeAct);
	    }
	}

	return quadAttenId;
    }

    /**
     * Used to retrieve QuadAtten DeviceConfig alias from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String retrieveQuadAttenDeviceAlias(String homeAct, DeviceAccountInfo deviceAccount) {

	String quadAttenAlias = null;

	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten alias from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		quadAttenAlias = extraProps.get("QUAD_ATTEN_ALIAS");
		LOGGER.info("quadAttenAlias = {}", quadAttenAlias);
	    } else {
		LOGGER.info("No Field extraProperties/QUAD_ATTEN_ALIAS in account details " + homeAct);
	    }
	}
	return quadAttenAlias;
    }

    /**
     * Used to retrieve Profile name to be activated for pod tests from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String retrieveProfileNameToBeActivatedForPODTests(String homeAct,
	    DeviceAccountInfo deviceAccount) {

	String activateProfile = null;

	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten profile from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		activateProfile = extraProps.get("profileForPODTests");
		LOGGER.info("activateProfile = {}", activateProfile);
	    } else {
		LOGGER.info("No Field extraProperties/profileForPODTests in account details " + homeAct);
	    }
	}

	return activateProfile;
    }

    /**
     * Used to retrieve Profile name to be activated for pod tests from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String retrieveDefaultProfileToBeActivated(String homeAct, DeviceAccountInfo deviceAccount) {

	String activateProfile = null;

	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten profile from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		activateProfile = extraProps.get("defaultProfile");
		LOGGER.info("defaultProfile = {}", activateProfile);
	    } else {
		LOGGER.info("No Field extraProperties/defaultProfile in account details " + homeAct);
	    }
	}

	return activateProfile;
    }

    /**
     * Used to retrieve rack name from specified home account
     * 
     * @param homeAct
     * @return
     */

    public static final String getRackName(String homeAct, DeviceAccountInfo deviceAccount) {

	String rackName = null;

	if (null == deviceAccount) {
	    deviceAccount = getAccountDetails(homeAct);
	}

	if (null != deviceAccount) {
	    // get the Quad atten profile from extra Properties
	    Map<String, String> extraProps = deviceAccount.getExtraProperties();
	    if (null != extraProps) {
		rackName = extraProps.get("rackName");
		LOGGER.info("Rack name = {}", rackName);
	    } else {
		LOGGER.info("No Field extraProperties/rackName in account details " + homeAct);
	    }
	}

	return rackName;
    }

    /**
     * Used to retrieve list of dut macs by specified home account
     * 
     * @param homeAct
     * @return
     */
    public static final List<String> getSpecificModelsFromHomeAccount(String homeAct, String model) {
	List<String> macIds = new ArrayList<String>();
	DeviceAccountInfo deviceAccount = getAccountDetails(homeAct);
	if (null != deviceAccount && null != deviceAccount.getDevices()) {
	    for (com.automatics.device.Device device : deviceAccount.getDevices()) {
		if (model.equalsIgnoreCase(device.getModel())) {
		    macIds.add(device.getHostMacAddress());
		}
	    }
	}
	return macIds;
    }
}