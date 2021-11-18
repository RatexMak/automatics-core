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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.DeviceAccountInfo;
import com.automatics.device.DutAccount;
import com.automatics.device.DutInfo;
import com.automatics.exceptions.TestException;
import com.automatics.rack.RackDeviceValidationManager;
import com.automatics.rack.RackInitializer;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.CommonMethods;

/**
 * This class manages home account lock /unlock
 *
 * @author reena
 */
public class DeviceAccountHandler {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAccountHandler.class);

    private RackInitializer rackInitializer;

    /**
     * Constructor
     * 
     * @param rackInitializer
     */

    public DeviceAccountHandler(RackInitializer rackInitializer) {

	if (rackInitializer == null) {
	    this.rackInitializer = AutomaticsTapApi.getRackInitializerInstance();
	} else {
	    this.rackInitializer = rackInitializer;
	}
    }

    /**
     * Used to get list of locked home accounts
     * 
     * @param settops
     *            - device macs
     * @return list of locked home accounts
     */
    public List<DutAccount> getHomeAccountLockedForUse(String macsOfOneDutInHome) {

	final List<DutAccount> homeAccountList = new ArrayList<DutAccount>();

	List<String> requiredHomeList = AutomaticsUtils.splitStringToList(macsOfOneDutInHome,
		AutomaticsConstants.COMMA);

	if (null != requiredHomeList && !requiredHomeList.isEmpty()) {
	    ExecutorService execService_Home = Executors.newFixedThreadPool(4);
	    for (final String settopMac : requiredHomeList) {
		execService_Home.execute(new Runnable() {

		    @Override
		    public void run() {
			String homeAccountNumber = getHomeAccount(settopMac);
			if (homeAccountNumber == null) {
			    LOGGER.info("Account {} did not confirm to mac or accountNumber format continuing ",
				    homeAccountNumber);
			} else {
			    try {
				com.automatics.device.DutAccount ecatsHomeAct = lockHomeAccount(homeAccountNumber,
					settopMac);
				if (ecatsHomeAct.isLocked()) {
				    homeAccountList.add(ecatsHomeAct);
				    LOGGER.info("Locked home Account {}:", ecatsHomeAct.getAccountNumber());
				}

			    } catch (TestException e) {
				LOGGER.error("Failed to lock the devices in settopMac: {} accountNumber:{} ", settopMac,
					homeAccountNumber);
			    }
			}
		    }
		});
	    }

	    // Ensure no new tasks are submitted
	    execService_Home.shutdown();

	    // Exit only when all threads have completed execution
	    while (!execService_Home.isTerminated()) {

		try {
		    Thread.sleep(3000);
		} catch (InterruptedException e) {

		    LOGGER.trace(e.getMessage());
		}
	    }
	}

	return homeAccountList;
    }

    /**
     * Locks the home account (all devices under specified home account) for a specified account number
     * 
     * @param homeAccount
     *            number
     * @return home account object
     */
    public com.automatics.device.DutAccount lockHomeAccount(String homeAccountNumber, String settopMac) {
	List<DutInfo> lockedSettops = getLockedDevices(homeAccountNumber);
	DeviceAccountInfo accountNode = DeviceAccountRestClient.getAccountDetails(homeAccountNumber);
	com.automatics.device.DeviceAccount ecatsHomeAct = new com.automatics.device.DeviceAccount(homeAccountNumber,
		lockedSettops);
	// set the WISST and Quad Atten URL/properties if its defined for the account
	ecatsHomeAct.setQuadAttenUrl(DeviceAccountRestClient.retrieveQuadAttenURL(homeAccountNumber, accountNode));
	ecatsHomeAct.setQuadAttenDeviceId(
		DeviceAccountRestClient.retrieveQuadAttenDeviceId(homeAccountNumber, accountNode));
	ecatsHomeAct.setQuadAttenDeviceAlias(
		DeviceAccountRestClient.retrieveQuadAttenDeviceAlias(homeAccountNumber, accountNode));

	ecatsHomeAct.setProfileToBeActivatedForPODTests(
		DeviceAccountRestClient.retrieveProfileNameToBeActivatedForPODTests(homeAccountNumber, accountNode));
	ecatsHomeAct.setDefaultProfileToBeActiavted(
		DeviceAccountRestClient.retrieveDefaultProfileToBeActivated(homeAccountNumber, accountNode));
	ecatsHomeAct.setRackName(DeviceAccountRestClient.getRackName(homeAccountNumber, accountNode));
	if (null != lockedSettops && !lockedSettops.isEmpty()) {
	    ecatsHomeAct.setAllocatedDevicesList(lockedSettops);

	    if (CommonMethods.isMacValid(settopMac)) {
		for (DutInfo dutInfo : lockedSettops) {
		    if (dutInfo.getHostMacAddress().equalsIgnoreCase(settopMac)) {
			ecatsHomeAct.setPivotDut(dutInfo);
			LOGGER.info("Pivot DUT host MAC address: {} and model: {}:", dutInfo.getHostMacAddress(),
				dutInfo.getModel());
		    }
		}
	    }
	}

	return ecatsHomeAct;
    }

    /**
     * Method to get home account by specified dut mac
     * 
     * @param settopMac
     * @return
     */
    public String getHomeAccount(String settopMac) {

	String homeAccountNumber = null;
	if (CommonMethods.isMacValid(settopMac)) {
	    LOGGER.info("Getting device account no. for {}", settopMac);
	    homeAccountNumber = DeviceAccountRestClient.getHomeAccount(settopMac);
	} else {
	    // check if the value can be parsed into a long if so we are assuming it is an account number
	    try {
		homeAccountNumber = settopMac;
	    } catch (NumberFormatException e) {
		LOGGER.error("Account id could not be parsed to a mac or accNumber {}", settopMac);
	    }
	}
	return homeAccountNumber;
    }

    /**
     * Used to find list of DUTs under specified homeAccount and locks the settops for use in tests.
     * 
     * @param homeAct
     * @param isGatewayLockRequried
     * @return
     */
    protected List<DutInfo> getLockedDevices(String homeAccountNumber) {

	LOGGER.info("Locking the settops in account {}", homeAccountNumber);
	List<DutInfo> settopsToLock = null;
	if (null != homeAccountNumber && !homeAccountNumber.isEmpty()) {
	    List<String> settopMacs = DeviceAccountRestClient.getSettopMacsFromHomeAccount(homeAccountNumber);

	    if (null != settopMacs && !settopMacs.isEmpty()) {
		RackDeviceValidationManager rackDeviceValidationManager = new RackDeviceValidationManager(
			rackInitializer);
		settopsToLock = rackDeviceValidationManager.manageSettopLocking(settopMacs);
	    }
	}
	return settopsToLock;
    }

}
