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
package com.automatics.rack;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.core.DeviceAccountHandler;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Device;
import com.automatics.device.DeviceAccount;
import com.automatics.device.Dut;
import com.automatics.device.DutAccount;
import com.automatics.device.DutInfo;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.TestType;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.FrameworkHelperUtils;

public class RackInitializer {

    private DeviceManager deviceManager;

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RackInitializer.class);

    /** AED version date format. */
    public static SimpleDateFormat AED_TIME_FORMAT = new SimpleDateFormat("ddMMMyyyy_HHmm_ss");

    /** The flag to check the status of framework initialization. */
    private boolean isRackIntialized = false;

    /** Devices that were locked by this instance of RackInitializer for use in test case. */
    private static List<Dut> settopsLockedForUse = new ArrayList<Dut>();

    /** Home account that were locked by this instance of CatsInitializer for use in test case. */
    private List<DutAccount> homeAccountsLockedForUse = new ArrayList<DutAccount>();
    /** Map to store firmware details of the locked STBs. */
    private Map<String, String> firmwareMap = new HashMap<String, String>();

    private Map<String, String> xreVersionMap = new HashMap<String, String>();

    /** List of dut boxes which we tried to lock but due to accessibility issue failed. */
    private List<Dut> badSettopsWhichTriedToLock = new ArrayList<Dut>();

    /** holds the start date time of the usage */
    public Date usageStartDateTime = null;

    /**
     * Constructor which initializes the test framework.
     */
    public RackInitializer() {
	getAutomaticsCoreVersion();
	deleteOldFilesAndFolders();
	deviceManager = DeviceManager.getInstance();
    }

    /**
     * Method to release the dut's.
     */
    public void releaseSettops() {
	if (isRackIntialized()) {
	    if (settopsLockedForUse != null && !settopsLockedForUse.isEmpty()) {
		for (Dut dut : settopsLockedForUse) {

		    releaseSettop(dut);
		}
	    }

	    settopsLockedForUse.clear();
	}
    }

    /**
     * Release the given dut instance
     * 
     * @param dut
     */
    public void releaseSettop(Dut device) {

	StringBuilder rdkbConnectedLockedDevice = null;

	try {
	    stopSerialTraceLogger(device);

	    if (SupportedModelHandler.isRDKVClient(device)) {
		deviceManager.release(device);
		boolean isAccountTest = Boolean.parseBoolean(
			System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION, "false"));
		String macList = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SETTOP_LIST);

		if (!isAccountTest) {
		    /** START ADDING/ MODIFYING CODE FOR MULTIPLE GATEWAY SUPPORT **/
		    ArrayList<Dut> gatewayDevices = ((Device) device).getGatewaySettops();
		    /** Adding Null check as cases may arise when Gateway Settops are not available **/
		    if (gatewayDevices != null) {

			for (Dut gatewayDevice : gatewayDevices) {
			    try {
				if (!FrameworkHelperUtils.splitAndCheckValuePresent(macList,
					gatewayDevice.getHostMacAddress())) {
				    LOGGER.info("### GOING TO RELEASE GATEWAY SETTOP: "
					    + gatewayDevice.getHostMacAddress());
				    deviceManager.release(gatewayDevice);

				    LOGGER.debug("### SUCCESSFULLY RELEASED GATEWAY SETTOP: "
					    + gatewayDevice.getHostMacAddress());
				}
			    } catch (Exception exception) {
				LOGGER.info("Failed to release the gateway dut: " + gatewayDevice.getHostMacAddress()
					+ "." + exception.getMessage());
			    }
			}
		    }

		    /** END ADDING/ MODIFYING CODE FOR MULTIPLE GATEWAY SUPPORT **/
		}
	    }
	    // Non-RDKV Client device
	    else {
		deviceManager.release(device);

		// For XB3, we need to unlock all its connected devices.
		if (SupportedModelHandler.isRDKB(device)) {
		    Device dut = (Device) device;

		    rdkbConnectedLockedDevice = new StringBuilder();

		    if (dut.getConnectedDeviceList() != null && !dut.getConnectedDeviceList().isEmpty()) {
			for (Dut connectedDevice : dut.getConnectedDeviceList()) {
			    try {
				// Try to unlock the connected devices for XB3
				boolean releaseSuccess = deviceManager.release(connectedDevice);
				if (!releaseSuccess) {
				    LOGGER.error("Failed to deallocate for the RDKB ("
					    + connectedDevice.getHostMacAddress() + ") connected "
					    + connectedDevice.getModel() + " - " + connectedDevice.getHostMacAddress());
				}
				if (rdkbConnectedLockedDevice.length() > 0) {
				    rdkbConnectedLockedDevice.append(", ");
				}

				rdkbConnectedLockedDevice.append("(");
				rdkbConnectedLockedDevice.append(connectedDevice.getModel());
				rdkbConnectedLockedDevice.append(") - ");
				rdkbConnectedLockedDevice.append(connectedDevice.getHostMacAddress());
			    } catch (Exception exception) {
				LOGGER.error("Failed to deallocate for the RDKB (" + connectedDevice.getHostMacAddress()
					+ ") connected " + connectedDevice.getModel() + " - "
					+ connectedDevice.getHostMacAddress() + ", with reason - "
					+ exception.getMessage(), exception);
			    }
			}
		    }

		    LOGGER.info("Successfully unlocked RDKB (" + device.getHostMacAddress() + ") mapped components - "
			    + rdkbConnectedLockedDevice);
		}
	    }

	    LOGGER.info("SETTOP - " + device.getHostMacAddress() + " RELEASED.");

	} catch (Exception e) {
	    LOGGER.info("failed to release the dut - " + device.getHostMacAddress() + "." + e.getMessage());
	}
    }

    /**
     * Checks whether the RackEnvironment initialized.
     * 
     * @return true if environment initialized.
     */
    public boolean isRackIntialized() {
	return this.isRackIntialized;
    }

    /**
     * Checks whether the the execution is Automation Escape Defect.
     * 
     * @return true
     */
    public boolean isAutomationEscapeDefect() {
	return Boolean.parseBoolean(
		System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_AED_STATUS, AutomaticsConstants.STRING_FALSE));
    }

    /**
     * Adds the firmware details to the firmware HashMap. This method should only be used by the FirwareDetailsCollector
     * job
     * 
     * @param macId
     *            Host MAC address of the STB
     * @param firmwareName
     *            Firmware name
     */
    public void addFirmwareDetailsToMap(String macId, String firmwareName) {
	LOGGER.info("Adding firmware details to map " + macId + " " + firmwareName);
	firmwareMap.put(macId, firmwareName);
    }

    /**
     * Gets the firmware map stored in RackInitializer instance.
     * 
     * @return The firmware details map
     */
    public Map<String, String> getFirmwareMap() {
	return firmwareMap;
    }

    /**
     * Gets the xre version map stored in RackInitializer instance.
     * 
     * @return The firmware details map
     */
    public Map<String, String> getXreVersionMap() {
	return xreVersionMap;
    }

    /**
     * Get the List of dut boxes which we tried to lock but failed due to access issue with the box
     * 
     * @return List of settops
     */
    public List<Dut> getBadSettopsWhichTriedToLock() {
	return badSettopsWhichTriedToLock;
    }

    /**
     * Set the List of dut boxes which we tried to lock but failed due to access issues with the box
     * 
     * @return List of settops
     */
    public void setBadSettopsWhichTriedToLock(List<Dut> badSettopsWhichTriedToLock) {
	this.badSettopsWhichTriedToLock = badSettopsWhichTriedToLock;
    }

    /**
     * Returns a copy of the locked dut list.
     * 
     * @return List of settops locked by this instance of CatsIntializer
     */
    public static List<Dut> getLockedSettops() {

	// we are not exposing the locked dut list to the caller. Instead a copy will be created.
	// This is to make sure that the operation on the dut list is thread safe.
	return new ArrayList<Dut>(settopsLockedForUse);
    }

    /**
     * Returns a copy of the locked home accounts list
     * 
     * @return List of locked home accounts by this instance of CatsIntializer
     */
    public List<DutAccount> getLockedHomeAccounts() {

	return new ArrayList<DutAccount>(homeAccountsLockedForUse);
    }

    /**
     * Helper method to get all the pivot devices in a home account
     * 
     * @return
     */
    public List<Dut> getLockedPivotDevicesInAccountBasedTest() {

	List<Dut> lockedDevices = new ArrayList<Dut>();
	List<DutAccount> dutAccounts = getLockedHomeAccounts();
	Dut pivotDevice = null;

	for (DutAccount dutAccount : dutAccounts) {

	    pivotDevice = (Device) dutAccount.getPivotDut();
	    if (settopsLockedForUse.contains(pivotDevice)) {
		lockedDevices.add(pivotDevice);
	    } else {
		LOGGER.error("Pivot DUT" + pivotDevice.getHostMacAddress() + " Not Available");
	    }
	}

	return lockedDevices;
    }

    /**
     * Helper method to get all the devices in a home account
     * 
     * @param catsInitializerInstance
     * @return
     */
    public List<Dut> getLockedDevicesInAccountBasedTest() {
	List<Dut> lockedSettops = new ArrayList<Dut>();
	List<DutAccount> dutAccounts = getLockedHomeAccounts();
	for (DutAccount dutAccount : dutAccounts) {
	    for (DutInfo device : dutAccount.getDevices()) {
		lockedSettops.add((Device) device);
	    }
	}

	return lockedSettops;
    }

    /**
     * Utility method to remove given dut from list
     * 
     * @param dut
     */
    public void removeLockedSettop(Dut dut) {
	settopsLockedForUse.remove(dut);
    }

    /**
     * Get dut list
     * 
     * @param settops
     * @return
     */
    public List<String> getSettopList(String settops) {

	List<String> requiredSettopList = AutomaticsUtils.splitStringToList(settops, ",");

	String serverSettopMac = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SERVER_SETTOP);

	if (null != serverSettopMac && !serverSettopMac.isEmpty()) {
	    requiredSettopList.add(serverSettopMac);
	}

	return requiredSettopList;
    }

    /**
     * Initialises the Rack environment. Retrieves all the available devices and locks them for use in tests.
     */
    public void initializeRack() {
	isRackIntialized = true;
	Boolean isAccountTest = new Boolean(System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
	String deviceMacs = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SETTOP_LIST);

	// In case of isAccountTest we will be getting only locked home accounts instead of locked devices.
	// So getting the devices and passing as a parameter
	List<Dut> lockedSettops = null;

	LOGGER.info("Is Account based test: {}", isAccountTest);

	List<DutInfo> lockedSettopList = null;

	if (isAccountTest) {
	    DeviceAccountHandler homeActHandler = new DeviceAccountHandler(this);

	    homeAccountsLockedForUse = homeActHandler.getHomeAccountLockedForUse(deviceMacs);
	    lockedSettops = getLockedDevicesInAccountBasedTest();

	    settopsLockedForUse = new ArrayList<Dut>(lockedSettops);
	    settopsLockedForUse = new ArrayList<Dut>(getLockedPivotDevicesInAccountBasedTest());
	} else {
	    List<String> macList = AutomaticsUtils.splitStringToList(deviceMacs, AutomaticsConstants.COMMA);

	    RackDeviceValidationManager deviceValidationManager = new RackDeviceValidationManager(this);
	    lockedSettopList = deviceValidationManager.manageSettopLocking(macList);

	    Device device = null;
	    lockedSettops = new ArrayList<Dut>();

	    for (DutInfo dutObject : lockedSettopList) {

		device = (Device) dutObject;
		lockedSettops.add(device);
	    }

	    settopsLockedForUse = new ArrayList<Dut>(lockedSettops);

	}
	LOGGER.info("Collecting dut info");

	// Updating hdmi status
	updateHdmiStatus(lockedSettops);

	/**
	 * Changes the included group names for Component Based Execution
	 */
	changeIncludedGroupsIfComponentBasedExecution();
    }

    /**
     * Method to handle included groups and components to run for component execution Removes the included groups
     * parameter for QUICK_CI as RDK portal trigger with changes component
     * 
     * 
     * This method will filter the component names provided by the test manager in case of component trigger for QT
     * 
     * Differentiates both components and grouped runs. Sets the Framework specific groups run for component
     * 
     */
    private void changeIncludedGroupsIfComponentBasedExecution() {

	String testFilterType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);
	String component = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_INCLUDED_GROUP, "");
	if (CommonMethods.isNotNull(testFilterType)) {
	    TestType testType = TestType.valueOf(testFilterType);

	    if (TestType.QUICK_CI.equals(testType) || TestType.FAST_QUICK_CI.equals(testType)) {
		System.setProperty(AutomaticsConstants.SYSTEM_PROPERTY_INCLUDED_GROUP, "");
	    }
	} else {
	    if (CommonMethods.isNotNull(component)) {
		component = component.replace("\"", "");
		Pattern special = Pattern.compile(AutomaticsConstants.PATTERN_TO_IDENTIFY_COMPONENT_RUN,
			Pattern.CASE_INSENSITIVE);
		Matcher m = special.matcher(component);
		if (m.find()) {
		    component = component.substring(1, component.length() - 1);
		    System.setProperty(AutomaticsConstants.SYSTEM_PROPERTY_INCLUDED_GROUP, "");
		    AutomaticsTestBase.setComponentRun(true);
		    AutomaticsTestBase.setComponentsToRun(component);

		}
	    }
	}
    }

    /**
     * Update hdmi status for device.
     * 
     * @param lockedSettops
     */
    public void updateHdmiStatus(List<Dut> lockedSettops) {
	for (Dut dut : lockedSettops) {
	    if (SupportedModelHandler.isRDKV(dut)) {
		LOGGER.info("Updating HDMI status for RDKV");
		updateHdmiConnectionStatus(dut);
	    }

	}

    }

    /**
     * Update the HDMI connection status.
     * 
     * @param device
     *            DeviceConfig instance.
     */
    private void updateHdmiConnectionStatus(Dut device) {
	boolean isHdmiConnected = false;

	if (SupportedModelHandler.isRDKVClient(device)) {
	    /**
	     * Xi boxes only supports HDMI connection.
	     */
	    isHdmiConnected = true;
	} else {
	    isHdmiConnected = Boolean.parseBoolean(
		    System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_HDMI_CONNECTION_STATUS, "false"));
	}

	((Device) device).setHdmiConnected(isHdmiConnected);
    }

    /**
     * Unlocks the settops previously locked. This method is here just in case we need to unlock Explicitly. Normally
     * the framework will take care of unlocking during the termination.
     * 
     * @param settopsToUnlock
     *            Settops that need to be unlocked.
     */
    protected void unlockSettops(List<Dut> settopsToUnlock) {

	if ((null == settopsToUnlock) || settopsToUnlock.isEmpty()) {
	    LOGGER.warn("No settops to unlock");
	} else {
	    LOGGER.info("Unlocking settops");

	    for (Dut dut : settopsToUnlock) {

		try {
		    deviceManager.release(dut);
		} catch (Exception aex) {
		    String errorMessage = String.format("Unlock failed for : %S due to %S", dut.getHostMacAddress(),
			    aex.getCause());
		    LOGGER.error(errorMessage, aex);
		}
	    }
	}
    }

    /**
     * Add the dut host MAC address to the supplied StringBuilder.
     * 
     * @param settopInfo
     *            The StringBuilder instance to which dut host MAC need to be appended
     * @param dut
     *            The dut instance whose Host MAC has to be appended.
     */
    public void addSettopToBuilder(StringBuilder settopInfo, Dut dut) {

	if (settopInfo.length() == 0) {
	    settopInfo.append(dut.getHostMacAddress());
	} else {
	    settopInfo.append(",").append(dut.getHostMacAddress());
	}
    }

    /**
     * Restarts the trace capturing for all the locked settops.
     */
    public void resartTraceCaptureForAllLockedSettops() {

	synchronized (settopsLockedForUse) {

	    for (Dut dut : settopsLockedForUse) {

		try {
		    dut.getTrace().stopTrace();
		    dut.getTrace().startTrace();
		} catch (Exception e) {
		    LOGGER.error("Error while restarting trace capturing for device", dut.getHostMacAddress());
		}

	    }
	}
    }

    /**
     * Method to delete old files and folders
     * 
     * Deletes the entire workspace and for report generation purpose maintains the content
     * 
     * all .csv and .xls files, and reports folder
     */
    private void deleteOldFilesAndFolders() {

	File rootFolder = new File(".");

	for (File fileTobeDeleted : CommonMethods.getChildFilesWithSpecifiedExtension(rootFolder, ".csv")) {
	    LOGGER.info("deleteOldFilesAndFolders - removing (.csv) " + fileTobeDeleted.getName());
	    fileTobeDeleted.delete();
	}

	for (File fileTobeDeleted : CommonMethods.getChildFilesWithSpecifiedExtension(rootFolder, ".xlsx")) {
	    LOGGER.info("deleteOldFilesAndFolders - removing (.xlsx) " + fileTobeDeleted.getName());
	    fileTobeDeleted.delete();
	}

	rootFolder = new File("reports");

	if (rootFolder.exists()) {
	    if (!System.getProperty("filterTestIds", "").equalsIgnoreCase("TC-REPORT-GEN")) {
		LOGGER.info("Report gen test so not deleting " + rootFolder.getAbsolutePath());
		for (File fileTobeDeleted : CommonMethods.getChildFilesWithSpecifiedExtension(rootFolder, null)) {
		    LOGGER.info(
			    "deleteOldFilesAndFolders - removing from reports folder - " + fileTobeDeleted.getName());
		    fileTobeDeleted.delete();

		}
	    }
	}

	rootFolder = new File("reports_aed");

	if (rootFolder.exists()) {
	    for (File fileTobeDeleted : CommonMethods.getChildFilesWithSpecifiedExtension(rootFolder, null)) {
		LOGGER.info(
			"deleteOldFilesAndFolders - removing from reports_aed folder - " + fileTobeDeleted.getName());
		fileTobeDeleted.delete();
	    }
	}
    }

    /**
     * Method to print the Automatics Core version version
     */
    public synchronized void getAutomaticsCoreVersion() {

	String version = null;

	// try to load from maven properties first
	try {
	    Properties versionProperties = new Properties();
	    InputStream inputStream = getClass()
		    .getResourceAsStream("/META-INF/maven/releases.com.comcast.ccp.apps/ecats/pom.properties");
	    if (inputStream != null) {
		versionProperties.load(inputStream);
		version = versionProperties.getProperty("version", "");
	    }
	} catch (Exception exception) {
	    // ignore
	}

	// fallback to using Java API
	if (CommonMethods.isNull(version)) {
	    Package aPackage = getClass().getPackage();
	    if (aPackage != null) {
		version = aPackage.getImplementationVersion();
		if (version == null) {
		    version = aPackage.getSpecificationVersion();
		}
	    }
	}

	if (CommonMethods.isNotNull(version)) {
	    LOGGER.info("\n\n\n***************************************************************\n"
		    + "               Automatics Core VERSION - " + version + "                     "
		    + "\n***************************************************************\n\n\n");
	}
    }

    /**
     * Method written specifically to add the client devices to array, settopsLockedForUse.
     * 
     * @param dut
     */
    public void addToSettopLockedForUse(Dut dut) {
	settopsLockedForUse.add(dut);
    }

    /**
     * Gets the Pi from Rack based on the MAC ID provided.
     * 
     * @param requiredSettopMac
     *            MAC ID for STB that need to be retrieved
     * 
     * @return STB retrieved
     */
    public Device getSettopForPi(String macAddress) {
	Device device = deviceManager.findRackDevice(macAddress);
	if (null != device) {
	    device.setDeviceCategory(DeviceCategory.NON_RDK.name());
	}
	return device;
    }

    /**
     * Locks the dut for use in tests. Note:- The dut locked will be unlocked only if JVM comes to a normal exit. IF JVM
     * crashes or is forcefully exited the dut locked (allocated) through this call will remain locked.
     * 
     * @param settopToLock
     *            dut that need to be locked
     * @return true if lock success
     */
    public boolean lockPi(Dut eCatsSettop) {
	boolean isLocked = false;
	try {

	    if (deviceManager.isLocked(eCatsSettop)) {

		LOGGER.debug("******* TEMP ALLOCATION FAILED FOR " + eCatsSettop.getHostMacAddress());

	    } else {
		// locked the dut for this time span
		deviceManager.lock(eCatsSettop);
		LOGGER.debug("******* TEMP ALLOCATION DONE FOR " + eCatsSettop.getHostMacAddress());
		isLocked = true;
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception occured while locking device: " + eCatsSettop.getHostMacAddress() + ": "
		    + e.getMessage());
	}

	return isLocked;

    }

    /**
     * Method to release the dut.
     */
    public boolean releasePi(Dut eCatsSettop) {
	boolean isReleased = false;
	try {
	    deviceManager.release(eCatsSettop);
	    isReleased = true;
	} catch (Exception e) {
	    LOGGER.error("Exception occured while releasing device: " + eCatsSettop.getHostMacAddress() + ": "
		    + e.getMessage());
	}

	return isReleased;
    }

    public void stopSerialTraceLogger() {

	List<Dut> devices = getLockedSettops();

	TraceProvider traceProvider = null;
	for (Dut device : devices) {
	    LOGGER.info("Stopping serial trace for device " + device.getHostMacAddress());
	    traceProvider = device.getSerialTrace();
	    if (null != traceProvider) {
		traceProvider.stopBuffering();
		try {
		    traceProvider.stopTrace();
		} catch (Exception e) {
		    LOGGER.error("Exception while stopping serial trace", e.getMessage());
		}
	    }
	}
    }

    public void stopSerialTraceLogger(Dut device) {
	TraceProvider traceProvider = device.getSerialTrace();

	if (null != traceProvider) {
	    traceProvider.stopBuffering();
	    try {
		traceProvider.stopTrace();
	    } catch (Exception e) {
		LOGGER.error("Exception while stopping serial trace", e.getMessage());
	    }
	}
    }

    public DeviceManager getDeviceManager() {
	return this.deviceManager;
    }

    /**
     * Utility method to release all the settops in the home account corresponding to given dut
     * 
     * @param dut
     */
    public void releaseHomeAccount(Dut dut) {
	DutAccount homeAccountToRelease = getHomeAccount(dut);

	if (null != homeAccountToRelease) {
	    if (dut.getHostMacAddress().equals(homeAccountToRelease.getPivotDut().getHostMacAddress())) {
		for (DutInfo device : homeAccountToRelease.getDevices()) {
		    releaseSettop((Device) device);
		}
	    } else {
		releaseSettop(dut);
	    }
	}
    }

    /**
     * Helper method to get the home account corresponding to a dut.
     * 
     * @param dut
     *            The Dut under test
     * @return The DutAccount object
     */
    private DutAccount getHomeAccount(Dut dut) {
	DutAccount reqdHomeAccount = null;
	List<DutInfo> devices = null;

	for (DutAccount dutAccount : homeAccountsLockedForUse) {
	    devices = ((DeviceAccount) dutAccount).getDevices();
	    for (DutInfo device : devices) {
		if (device.getHostMacAddress().equalsIgnoreCase(dut.getHostMacAddress())) {
		    reqdHomeAccount = dutAccount;
		    break;
		}
	    }
	}

	return reqdHomeAccount;
    }

    /**
     * Utility method to remove the home account corresponding to given dut from the list
     * 
     * @param dut
     */
    public void removeLockedHomeAccount(Dut dut) {
	DutAccount homeAccountToRemove = getHomeAccount(dut);

	if (null != homeAccountToRemove) {
	    homeAccountsLockedForUse.remove(homeAccountToRemove);
	}
    }

    public void releaseDevices() {
	Boolean isAccountTest = new Boolean(System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
	if (isAccountTest) {
	    for (Dut dut : getLockedPivotDevicesInAccountBasedTest()) {
		releaseHomeAccount(dut);
		removeLockedHomeAccount(dut);
	    }
	} else {
	    releaseSettops();
	}

    }

}
