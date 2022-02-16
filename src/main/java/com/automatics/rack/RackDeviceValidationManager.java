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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.core.SupportedModelHandler;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.device.DutImpl;
import com.automatics.device.DutInfo;
import com.automatics.enums.AutomaticsBuildType;
import com.automatics.enums.DeviceCategory;
import com.automatics.enums.ExecuteOnType;
import com.automatics.enums.JobStatusValue;
import com.automatics.manager.device.DeviceManager;
import com.automatics.providers.DeviceAccessValidator;
import com.automatics.providers.rack.ImageCompareProvider;
import com.automatics.providers.rack.OcrProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RemoteProvider;
import com.automatics.providers.rack.RemoteProviderFactory;
import com.automatics.providers.rack.VideoProvider;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.test.AutomaticsTestBase;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.FrameworkHelperUtils;
import com.automatics.utils.NonRackUtils;
import com.automatics.utils.TestUtils;

/**
 * Class to Manage Locking of Devices before TestCase Execution
 * 
 * 
 * 
 */
public class RackDeviceValidationManager {

    private static final String NON_RDKV_CLIENT_DEVICES = "nonRdkvClientDevices";

    private static final String RDKV_CLIENT_DEVICES = "rdkvClientDevices";

    boolean isGatewayAvailableForIp = false;

    protected DeviceAccessValidator deviceAccessValidator;

    protected DeviceManager deviceManager;

    /** Mutex Object while adding to Lists **/
    private static Object addToList_LockObject = new Object();

    private RackInitializer rackInitializer;

    /** Holds Locked Settops **/
    private List<DutInfo> lockedSettopsAfterInitialization = new ArrayList<DutInfo>();

    private String deviceMacs = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_SETTOP_LIST);

    private Boolean isAccountTest = new Boolean(
	    System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_ACCOUNT_EXECUTION));
    private Boolean initializeConnectedDevice = new Boolean(
	    System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_INITIALIZE_CONNECTED_DEVICES, "false"));

    /** Holds the list of failed Dut objects. */
    private List<Dut> failedSettopList = new ArrayList<Dut>();

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RackDeviceValidationManager.class);

    /**
     * Constructor
     * 
     * @param catsRackHandler
     */
    public RackDeviceValidationManager(RackInitializer rackInitializer) {

	if (rackInitializer != null) {

	    this.rackInitializer = rackInitializer;
	    deviceManager = DeviceManager.getInstance();
	    deviceAccessValidator = BeanUtils.getDeviceAccessValidator();

	} else {
	    LOGGER.error("RackInitializer needs to be initialized before trying to create dut object");
	}

    }

    /**
     * Method to initialize and lock devices before starting Tests in them
     * 
     * @param settopMacs
     * @param type
     *            of framework
     * @return
     */
    public List<DutInfo> manageSettopLocking(List<String> settopMacs) {

	Map<String, Map<String, Dut>> deviceList = splitRdkvClientAndOtherDevices(settopMacs);

	Map<String, Dut> nonClientDevices = deviceList.get(NON_RDKV_CLIENT_DEVICES);
	if (null != nonClientDevices && !nonClientDevices.isEmpty()) {
	    initializeNonClientDevices(nonClientDevices);
	}

	Map<String, Dut> clientDevices = deviceList.get(RDKV_CLIENT_DEVICES);
	if (null != clientDevices && !clientDevices.isEmpty()) {
	    initializeRdkvClientConnectedGatewayDevices(clientDevices);
	    initializeClientDevices(clientDevices);
	}

	rackInitializer.setBadSettopsWhichTriedToLock(failedSettopList);
	return lockedSettopsAfterInitialization;
    }

    /**
     * Gets the device. If the test environment is set to non-rack, then devices will be fetched from non-rack service,
     * otherwise fetch the device from rack service.
     * 
     * @param hostMac
     * @return Dut
     */
    public Dut getDevice(String hostMac) {

	Dut device = deviceManager.findRackDevice(hostMac);

	// Use device model in rack if model missing in props, else use from props
	if (device != null) {
	    SupportedModelHandler.mapModel(device);
	}
	return device;
    }

    /**
     * Categorize devices as Non-Client and Client and fetch the device from backend service.
     * 
     * @param deviceMacs
     * @return Map holding macs categorized as client & nonclient and its corresponding device object.
     */
    private Map<String, Map<String, Dut>> splitRdkvClientAndOtherDevices(List<String> deviceMacs) {

	Dut device = null;
	Map<String, Map<String, Dut>> devices = new HashMap<String, Map<String, Dut>>();

	// Map to hold IP macs, which are to be initialized
	Map<String, Dut> rdkvClientDeviceMap = new HashMap<String, Dut>();

	// Map to hold non-IP macs, which are to be initialized
	Map<String, Dut> nonRdkvClientDeviceMap = new HashMap<String, Dut>();

	// Get separate Non-IP & IP devices Lists
	for (String hostMac : deviceMacs) {

	    try {
		// Get the device
		device = getDevice(hostMac);
		if (null == device) {
		    LOGGER.error("Device returned is null for {}", hostMac);
		    AutomaticsTestBase.updateJobStatus(hostMac, JobStatusValue.INVALID_DEVICE);
		    continue;
		}

		// Categorize the devices as client and non-client devices
		if (SupportedModelHandler.isRDKVClient(device)) {
		    // Add the client devices
		    rdkvClientDeviceMap.put(hostMac, device);
		    LOGGER.info("[INIT LOG] : RDKV Client device");

		    // Add client connected gateways
		    if (NonRackUtils.isRack()) {
			addGatewaysToConnectedClient(device);
		    }
		} else {
		    LOGGER.info("[INIT LOG] : Non RDKV Client device");
		    nonRdkvClientDeviceMap.put(hostMac, device);
		}
	    } catch (Exception e) {
		LOGGER.error("Failed to fetch device", hostMac, e);
		AutomaticsTestBase.updateJobStatus(hostMac, JobStatusValue.INVALID_DEVICE);
	    }
	}

	devices.put(RDKV_CLIENT_DEVICES, rdkvClientDeviceMap);
	devices.put(NON_RDKV_CLIENT_DEVICES, nonRdkvClientDeviceMap);
	return devices;
    }

    /**
     * Adds the client connected gateway to client device
     * 
     * @param device
     *            client device
     */
    private void addGatewaysToConnectedClient(Dut device) {
	String gatewayHostMacs = null;
	Dut gatewayDevice = null;
	Device dut = (Device) device;

	// Client connected gateway is needed for home automation mode

	gatewayHostMacs = dut.getConnectedGateWaySettopMacs();

	LOGGER.info("[INIT LOG] : Connected gateway host mac address: {}", gatewayHostMacs);

	if (CommonMethods.isNotNull(gatewayHostMacs)) {

	    // Add client connected gateway devices
	    for (String gatewayMac : gatewayHostMacs.split(AutomaticsConstants.COMMA)) {

		// Get the gateway device
		gatewayDevice = getDevice(gatewayMac.trim());

		// Set the corresponding Gateway Dut for the Client
		if (null != gatewayDevice) {
		    dut.addGatewaySettops(gatewayDevice);
		}
	    }
	}
    }

    /**
     * Method to initialize Non-IP Devices in Parallel
     * 
     * @param nonIPDevices
     */
    private void initializeNonClientDevices(Map<String, Dut> nonIPDevices) {
	ExecutorService executorService = Executors.newFixedThreadPool(10);

	// Initialize Non-IP Devices in Parallel
	for (Map.Entry<String, Dut> entry : nonIPDevices.entrySet()) {
	    executorService.execute(new Runnable() {
		@Override
		public void run() {
		    boolean proceedWithOrWithoutLock = false;
		    String nonIPMac = entry.getKey();
		    Device device = (Device) entry.getValue();

		    // Get the device model name
		    boolean isAccessible = true;

		    // Lock the device
		    try {

			if (NonRackUtils.isNonRack()) {
			    LOGGER.info("INIT-{} Skipping locking device as Non-Rack device", nonIPMac);
			    proceedWithOrWithoutLock = true;
			} else {
			    boolean lockSuccess = performDeviceLock(device);
			    if (lockSuccess) {
				proceedWithOrWithoutLock = true;
			    } else {
				failedSettopList.add(device);

			    }
			}

			if (proceedWithOrWithoutLock) {
			    // Gets the device category
			    DeviceCategory deviceCategory = TestUtils.getDeviceCategory(device);

			    // Sets access mechanism for device
			    deviceAccessValidator.setAccessMechanismForDevice(deviceCategory, device);
			    LOGGER.info("INIT-{} Setting access method {}", nonIPMac, device.getAccessMechanism());

			    // Decides if device accessibility check to be performed or not
			    LOGGER.info("INIT-{} Checking if accessibility check required", nonIPMac);
			    boolean accessibilityCheckReq = deviceAccessValidator
				    .isAccessibilityCheckRequired(deviceCategory, device);
			    LOGGER.info("INIT-{} Checking if accessibility check required is {}", nonIPMac,
				    accessibilityCheckReq);
			    if (accessibilityCheckReq) {
				// Checks if device is accessible
				LOGGER.info("INIT-{} Checking if device is accessible ", nonIPMac);
				isAccessible = deviceAccessValidator.isDeviceAccessible(deviceCategory, device);
			    } else {
				isAccessible = true;
				LOGGER.info("INIT-{} Assuming device is accessible", nonIPMac);
			    }

			    LOGGER.info("INIT-{} Is device accessible {}", nonIPMac, isAccessible);
			    if (isAccessible) {
				// Perform hardware provider initialization
				if (NonRackUtils.isRack()) {
				    performPostAccessibilityCheckInitialization(deviceCategory, device);
				} else {
				    wireTraceProvider(device);
				}
				LOGGER.info("[INIT LOG] : Adding dut to locked list");
				addToLockedSettopList(device);

			    } else {
				// Release the device as it is not accessible
				if (NonRackUtils.isRack()) {
				    if (deviceManager.isLocked(device)) {
					LOGGER.error("INIT-{} Releasing device - not reachable", nonIPMac);
					rackInitializer.releaseSettop(device);
				    }
				    failedSettopList.add(device);
				    AutomaticsTestBase.updateJobStatus(nonIPMac, JobStatusValue.SSH_FAIL);
				}
			    }
			}

		    } catch (Exception e) {
			LOGGER.error("Unable to wire dut " + nonIPMac, e);
			AutomaticsTestBase.updateJobStatus(device.getHostMacAddress(), JobStatusValue.BOXES_UNUSABLE);
			addToFailedSettopListAndRelease((DutImpl) device);
		    }
		}
	    });
	}
	// Ensure no new tasks are submitted
	executorService.shutdown();

	// Exit only when all threads have completed execution
	while (!executorService.isTerminated())

	{
	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
		LOGGER.trace(e.getMessage());
	    }
	}
	LOGGER.info("Finished Non-IP Initialization Threads");
    }

    /**
     * Method to initialize Non-IP Devices in Parallel
     * 
     * @param settopMacs
     */
    private void initializeRdkvClientConnectedGatewayDevices(Map<String, Dut> clientDevices) {
	ExecutorService executorService = Executors.newFixedThreadPool(10);

	// Initialize Non-IP Devices in Parallel
	for (Map.Entry<String, Dut> entry : clientDevices.entrySet()) {

	    executorService.execute(new Runnable() {
		@Override
		public void run() {

		    boolean isAccessible = true;
		    String clientMac = entry.getKey();
		    Device clientDevice = (Device) entry.getValue();

		    List<Dut> gatewayList = clientDevice.getGatewaySettops();
		    if (null != gatewayList && !gatewayList.isEmpty()) {
			String gatewayMac = null;
			Device gatewayDevice = null;

			for (Dut gateway : gatewayList) {
			    gatewayDevice = (Device) gateway;
			    LOGGER.info("[INIT LOG] : Intializing gateway: {} {} for client device {}",
				    gatewayDevice.getModel(), gatewayDevice.getHostMacAddress(),
				    clientDevice.getModel(), clientMac);
			    gatewayMac = gatewayDevice.getHostMacAddress();

			    try {

				// Gets the device category
				DeviceCategory deviceCategory = TestUtils.getDeviceCategory(gatewayDevice);

				// Sets access mechanism for device
				deviceAccessValidator.setAccessMechanismForDevice(deviceCategory, gatewayDevice);
				LOGGER.info("[INIT LOG] : Access mechanism for device {} {} {}", gatewayMac,
					gatewayDevice.getModel(), gatewayDevice.getAccessMechanism());

				// Decides if device accessibility check to be performed or not
				boolean accessibilityCheckReq = deviceAccessValidator
					.isAccessibilityCheckRequired(deviceCategory, gatewayDevice);
				if (accessibilityCheckReq) {
				    // Checks if device is accessible
				    isAccessible = deviceAccessValidator.isDeviceAccessible(deviceCategory,
					    gatewayDevice);
				} else {
				    isAccessible = true;
				    LOGGER.info("INIT-{} Assuming device is accessible", gatewayMac);
				}

				if (isAccessible) {
				    // Perform hardware provider initialization
				    if (NonRackUtils.isRack()) {
					performPostAccessibilityCheckInitialization(deviceCategory, gatewayDevice);
				    } else {
					wireTraceProvider(gatewayDevice);
				    }
				}
			    } catch (Exception e) {
				LOGGER.error("Unable to wire dut " + gatewayMac, e);
			    }

			}

		    } else {
			LOGGER.info("[INIT LOG] : No gateway device available for initialization for client",
				clientDevice.getModel(), clientMac);
		    }

		}
	    });
	}
	// Ensure no new tasks are submitted
	executorService.shutdown();

	// Exit only when all threads have completed execution
	while (!executorService.isTerminated())

	{
	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
		LOGGER.trace(e.getMessage());
	    }
	}
	LOGGER.info("Finished Client Connected Gateway Initialization Threads");
    }

    private void addToFailedSettopListAndRelease(DutImpl eCatsSettop) {
	if (eCatsSettop != null && NonRackUtils.isRack() && deviceManager.isLocked(eCatsSettop)) {
	    LOGGER.error("Releasing device; as unable to ssh to device - " + eCatsSettop.getHostMacAddress());
	    failedSettopList.add(eCatsSettop);
	    rackInitializer.releaseSettop(eCatsSettop);
	}
    }

    /**
     * Performs lock on rack device.
     * 
     * @param device
     * @param proceedWithOrWithoutLock
     * @return
     */
    private boolean performDeviceLock(Dut device) {
	boolean isLockSuccess = false;
	LOGGER.info("INIT-{} Check if device already locked" + device.getHostMacAddress());
	if (deviceManager.isLocked(device)) {
	    LOGGER.info("INIT-{} Is device already locked :true" + device.getHostMacAddress());
	    AutomaticsTestBase.updateJobStatus(device.getHostMacAddress(), JobStatusValue.ALREADY_LOCKED);
	} else {
	    LOGGER.info("[INIT LOG] : Locking device " + device.getHostMacAddress());
	    isLockSuccess = deviceManager.lock(device);
	}
	return isLockSuccess;
    }

    /**
     * 
     * This method performs all those operations that need to be done as part of initialization , post accessibility
     * check for each of the device category . In future if furthur steps are to be added, they can be added here easily
     * for each device category
     * 
     * @param deviceCategory
     *            DeviceConfig Category
     * @param device
     *            DeviceConfig object
     * @throws IOException
     */
    private void performPostAccessibilityCheckInitialization(DeviceCategory deviceCategory, Dut device)
	    throws IOException {

	if (DeviceCategory.RDKV_CLIENT == deviceCategory || DeviceCategory.RDKV_GATEWAY == deviceCategory) {
	    /**
	     * Add the device type parameter to identify the DELIA and OCAP based builds.
	     **/
	    setSettopBoxDeviceType(device);
	}
	performProviderWiring(deviceCategory, (Device) device);
    }

    public void performProviderWiring(DeviceCategory category, Device device) {
	LOGGER.info("[INIT LOG] : Wiring device." + device.getHostMacAddress());
	LOGGER.info("INIT-{} Performing provider wiring", device.getHostMacAddress());
	switch (category) {
	case RDKV_GATEWAY:
	case RDKV_CLIENT:
	    wirePowerProvider(device);
	    wireTraceProvider(device);
	    wireRemoteProvider(device);
	    wireVideoProvider(device);
	    wireImageCompareProvider(device);
	    wireOcrProvider(device);
	    break;
	case RDKB:
	    wirePowerProvider(device);
	    wireTraceProvider(device);
	    break;
	case RDKC:
	    wirePowerProvider(device);
	    wireTraceProvider(device);
	    break;
	case ECB:
	    wirePowerProvider(device);
	    break;
	case NON_RDK:
	    wirePowerProvider(device);
	    break;
	default:
	    break;
	}
    }

    /**
     * Wires power provider
     * 
     * @param device
     *            Test device
     */
    public void wirePowerProvider(DutImpl device) {
	LOGGER.info("INIT-{} PowerProvider wiring", device.getHostMacAddress());
	PowerProvider powerProvider = (PowerProvider) BeanUtils.getPowerProvider();
	if (null != powerProvider) {
	    powerProvider.setDevice(device);
	    device.setPower(powerProvider);
	}
    }

    /**
     * Wires video provider
     * 
     * @param device
     *            Test device
     */
    public void wireVideoProvider(DutImpl device) {
	LOGGER.info("INIT-{} VideoProvider wiring", device.getHostMacAddress());
	VideoProvider videoProvider = (VideoProvider) BeanUtils.getVideoProvider();
	if (null != videoProvider) {
	    videoProvider.setDevice(device);
	    device.setVideo(videoProvider);
	}
    }

    /**
     * Wires remote provider
     * 
     * @param device
     *            Test device
     */
    public void wireRemoteProvider(DutImpl device) {
	LOGGER.info("INIT-{} RemoteProvider wiring", device.getHostMacAddress());
	RemoteProviderFactory remoteFactory = BeanUtils.getRemoteProviderFactory();
	if (null != remoteFactory) {
	    RemoteProvider remoteProvider = remoteFactory.getRemoteProvider(device);
	    if (null != remoteProvider) {
		remoteProvider.setDevice(device);
		device.setRemote(remoteProvider);
	    }
	}
    }

    /**
     * Wires ocr provider
     * 
     * @param device
     *            Test device
     */
    public void wireOcrProvider(DutImpl device) {
	LOGGER.info("INIT-{} OcrProvider wiring", device.getHostMacAddress());
	OcrProvider ocrProvider = BeanUtils.getOcrProvider();
	if (null != ocrProvider) {
	    ocrProvider.setDevice(device);
	    device.setOcrProvider(ocrProvider);
	}
    }

    /**
     * Wires wireImageCompare Provider
     * 
     * @param device
     *            Test device
     */
    public void wireImageCompareProvider(DutImpl device) {
	LOGGER.info("INIT-{} ImageCompareProvider wiring", device.getHostMacAddress());
	ImageCompareProvider imageCompareProvider = BeanUtils.getImageCompareProvider();

	if (null != imageCompareProvider) {
	    imageCompareProvider.setDevice(device);
	    device.setImageCompareProvider(imageCompareProvider);
	}
    }

    /**
     * Method to initialize Client Devices in Parallel
     */
    private void initializeClientDevices(Map<String, Dut> ipMacDevices) {

	ExecutorService executorService = Executors.newFixedThreadPool(10);
	for (Map.Entry<String, Dut> entry : ipMacDevices.entrySet()) {

	    executorService.execute(new Runnable() {

		@Override
		public void run() {

		    boolean proceedWithOrWithoutLock = false;
		    String mac = entry.getKey();
		    Device device = (Device) entry.getValue();

		    boolean isClientAccessible = false;

		    // Lock the device
		    try {
			if (NonRackUtils.isNonRack()) {
			    LOGGER.info("[INIT LOG] : Skipping locking client device as Non-Rack device", mac);
			    proceedWithOrWithoutLock = true;
			} else {
			    boolean lockSuccess = performDeviceLock(device);
			    if (lockSuccess) {
				LOGGER.info("[INIT LOG] : Successfully locked device: {}", mac);
				proceedWithOrWithoutLock = true;
			    } else {
				LOGGER.error("[INIT LOG] : Failed to lock the rack device: {}", mac);
				failedSettopList.add(device);

			    }
			}

			if (proceedWithOrWithoutLock) {

			    // Gets the device category
			    DeviceCategory deviceCategory = TestUtils.getDeviceCategory(device);
			    device.setDeviceCategory(deviceCategory.name());

			    // Sets access mechanism for device
			    deviceAccessValidator.setAccessMechanismForDevice(deviceCategory, device);
			    LOGGER.info("INIT-{} Setting access method {}", mac, device.getAccessMechanism());

			    // Decides if device accessibility check to be performed or not
			    LOGGER.info("INIT-{} Checking if accessibility check required", mac);
			    boolean accessibilityCheckReq = deviceAccessValidator
				    .isAccessibilityCheckRequired(deviceCategory, device);
			    LOGGER.info("INIT-{} Accessibility check required {}", mac, accessibilityCheckReq);

			    // Checks if device is accessible
			    if (accessibilityCheckReq) {
				LOGGER.info("INIT-{} Checking if device is accessible", mac);
				isClientAccessible = deviceAccessValidator.isDeviceAccessible(deviceCategory, device);
			    } else {
				isClientAccessible = true;
				LOGGER.info("INIT-{} Assuming device is accessible", device.getHostMacAddress());
			    }

			    LOGGER.info("INIT-{} Is device accessible {}", mac, isClientAccessible);
			    if (isClientAccessible) {
				// Perform hardware provider initialization
				if (NonRackUtils.isRack()) {
				    performPostAccessibilityCheckInitialization(deviceCategory, device);
				} else {
				    wireTraceProvider(device);
				}
				LOGGER.info("[INIT LOG] : Adding dut to locked list");
				addToLockedSettopList(device);

			    } else {
				// Release the device as it is not accessible
				if (NonRackUtils.isRack()) {
				    if (deviceManager.isLocked(device)) {
					LOGGER.error("INIT-{} Releasing device - not reachable", mac);
					rackInitializer.releaseSettop(device);
				    }
				    failedSettopList.add(device);
				    AutomaticsTestBase.updateJobStatus(mac, JobStatusValue.SSH_FAIL);
				}
			    }
			}
		    } catch (Exception e) {
			LOGGER.error("Unable to wire dut " + mac, e);
			AutomaticsTestBase.updateJobStatus(device.getHostMacAddress(), JobStatusValue.BOXES_UNUSABLE);
			addToFailedSettopListAndRelease((DutImpl) device);
		    }

		}
	    });

	}

	// Ensure no new tasks are submitted
	executorService.shutdown();

	// Exit only when all threads have completed execution
	while (!executorService.isTerminated()) {

	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e) {
		LOGGER.trace(e.getMessage());
	    }
	}

	LOGGER.info("Finished Client Devices Initialization Thread");
    }

    /**
     * Set Trace for Dut Instance
     * 
     * @param eCatsSettop
     */
    private void wireTraceProvider(Device device) {
	LOGGER.info("INIT-{} DeviceConfig Connection Based TraceProvider wiring", device.getHostMacAddress());
	TraceProvider traceProvider = BeanUtils.getDeviceConnectionTraceProviderInstance(device);
	if (null != traceProvider) {
	    device.setTrace(traceProvider);
	}

	// Wiring serial based trace provider
	String racks = AutomaticsPropertyUtility.getProperty("enable.serial.trace.logger.racks");
	boolean enableSerialTrace = FrameworkHelperUtils.splitAndCheckValuePresent(racks, device.getRackName());
	LOGGER.info("Serial based trace to be initialized: {}", enableSerialTrace);

	if (enableSerialTrace) {
	    TraceProvider serialTraceProvider = BeanUtils.getTraceProviderInstance(device);
	    if (null != serialTraceProvider) {
		device.setSerialTrace(serialTraceProvider);
	    }
	}
    }

    /**
     * Removes Gateway(s) from Locked List and Releases Gateway and Client Settops
     * 
     * @param eCatsSettop
     * @param isThirdPartyLock
     */
    protected void removeAndReleaseSettop(DutImpl clientSettop, boolean isThirdPartyLock) {

	List<Dut> gatewaySettops = ((Device) clientSettop).getGatewaySettops();

	if (gatewaySettops != null && !isAccountTest) {
	    // Removing gateways from locked list
	    for (Dut gateway_settop : gatewaySettops) {

		synchronized (addToList_LockObject) {
		    if (lockedSettopsAfterInitialization.contains(gateway_settop)) {
			lockedSettopsAfterInitialization.remove(gateway_settop);
		    }
		}
	    }
	}

	// Release client and gateway settops
	if (!isThirdPartyLock && NonRackUtils.isRack() && deviceManager.isLocked(clientSettop)) {
	    rackInitializer.releaseSettop(clientSettop);
	}

    }

    /**
     * Adds device to locked list
     * 
     * @param device
     */
    private void addToLockedSettopList(DutImpl device) {
	LOGGER.info("DeviceConfig Macs " + deviceMacs);
	if (!isAccountTest && !initializeConnectedDevice) {
	    // Need not add devices that are not in Dut List for Non-AccountTest setup
	    if (!deviceMacs.toLowerCase().contains(device.getHostMacAddress().toLowerCase())) {
		return;
	    }
	}
	synchronized (addToList_LockObject) {
	    if (!lockedSettopsAfterInitialization.contains(device)) {
		LOGGER.info("Dut Added to locked list " + device.getHostMacAddress());
		lockedSettopsAfterInitialization.add((DutInfo) device);
	    }
	}
    }

    /**
     * Set the device type for particular dut box.
     * 
     * @param device
     */

    private void setSettopBoxDeviceType(Dut device) {

	Device dut = ((Device) device);

	// Add the device type parameter to identify the DELIA and OCAP based builds.
	AutomaticsBuildType deviceType = TestUtils.getBuildType(device);
	dut.setBuildType(deviceType);

	ExecuteOnType executeOntype = TestUtils.getExecuteOnType(device);
	dut.setExecuteOn(executeOntype);
    }
}
