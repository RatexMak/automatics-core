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
package com.automatics.connection.handler;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.LinuxCommandConstants;
import com.automatics.constants.SeleniumConstants;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.enums.Browser;
import com.automatics.enums.ProcessRestartOption;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.selenium.CustomizableBrowserCapabilities;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;

/**
 * 
 * @author Muthu_Sakthivel01
 *
 */
public class SeleniumNodeConnectionHandler {

    /** SLF4j logger. */
    private static final java.util.logging.Logger LOGGER = LoggerFactory.getLogger(SeleniumNodeConnectionHandler.class);

    private static final String COMMAND_HOME_FOLDER_CLIENT = "cut -d : -f6 < <(getent passwd $USER)";

    private static final String NAME_SELENIUM_HOME = "linux.folder.home.selenium";

    private static final String LOGGER_PREFIX_CONFIG_VALIDATION = "[Selenium Configuration] : ";

    private static final String BROWSER_CAPABILITY_MARIONETTE = "marionette";

    private static final String BROWSER_CAPABILITY_HEADLESS = "headless";

    private static final String BROWSER_CAPABILITY_NO_SANDBOX = "--no-sandbox";

    private static final String BROWSER_NAME_FIREFOX = "firefox";

    private static final String BROWSER_NAME_CHROME = "chrome";

    private static final String BROWSER_NAME_EDGE = "MicrosoftEdge";

    private static final String BROWSER_NAME_SAFARI = "safari";

    private static final int SELENIUM_RETRY_COUNT = 1;

    /** Selenium configuration path to be read from property file for Mac os */
    private static final String SELENIUM_CONFIG_PATH_FOR_MAC_OS = "selenium.config.path.mac.os";

    /** Selenium configuration path to be read from property file for windows os */
    private static final String SELENIUM_CONFIG_PATH_FOR_WINDOWS_OS = "selenium.config.path.windows.os";

    /** Selenium configuration windows vbs file name to be read from property file */
    private static final String SELENIUM_CONFIG_WIN_VBS_FILE_NAME = "selenium.config.win.vbs.file.name";

    /** Selenium configuration linux os sh file name to be read from property file */
    private static final String SELENIUM_CONFIG_LINUX_OS_SH_FILE_NAME = "selenium.config.linux.sh.file.name";

    /** Selenium configuration mac os sh file name to be read from property file */
    private static final String SELENIUM_CONFIG_MAC_OS_SH_FILE_NAME = "selenium.config.mac.sh.file.name";

    /** Selenium configuration windows bat file name to be read from property file */
    private static final String SELENIUM_CONFIG_WIN_BAT_FILE_NAME = "selenium.config.win.bat.file.name";
    
    /** Selenium configuration windows schbat file name to be read from property file */
    private static final String SELENIUM_CONFIG_SCH_VBS_FILE_NAME = "selenium.config.win.schbat.file.name";

    /** Selenium configuration for windows/liunx os supporting jar file name to be read from property file */
    private static final String SELENIUM_CONFIG_WIN_LIUNX_OS_JAR_FILE_NAME = "selenium.config.win.linux.os.jar.file.name";

    /** Selenium configuration for mac os supporting jar file name to be read from property file */
    private static final String SELENIUM_CONFIG_MAC_OS_JAR_FILE_NAME = "selenium.config.macos.jar.file.name";

    /** Selenium configuration chrome driver file name to be read from property file */
    private static final String SELENIUM_CONFIG_CHROME_DRIVER_FILE_NAME = "selenium.config.chrome.driver.file.name";

    /** Selenium configuration chrome driver file name to be read from property file */
    private static final String SELENIUM_CONFIG_FIREFOX_DRIVER_FILE_NAME = "selenium.config.firefox.driver.file.name";

    /** Selenium configuration chrome driver file name to be read from property file */
    private static final String SELENIUM_CONFIG_EDGE_DRIVER_FILE_NAME = "selenium.config.edge.driver.file.name";

    private static final String ERROR_CALCS_DEPRECATED = "NOTE: Cacls is now deprecated, please use Icacls.";
    
    /** Selenium configuration windows bat file name to be read from property file */
    private static final String SELENIUM_SCHEDULE_CR_TASK_CMD = "selenium.schedule.task.windows";    
    private static final String SELENIUM_SCHEDULE_LS_TASK_CMD = "selenium.list.task.windows";
    private static final String SELENIUM_SCHEDULE_RUN_TASK_CMD = "selenium.run.task.windows"; 

    private static String SED_COMMAND_REPLACE = LinuxCommandConstants.SED + LinuxCommandConstants.OPTION_I
	    + AutomaticsConstants.SYMBOL_SINGLE_QUOTE + "s" + AutomaticsConstants.UNDERSCORE + "<valueToReplace>"
	    + AutomaticsConstants.UNDERSCORE + "<replacement>" + AutomaticsConstants.UNDERSCORE
	    + AutomaticsConstants.SYMBOL_SINGLE_QUOTE + AutomaticsConstants.SINGLE_SPACE_CHARACTER + "<filename>";
    /** Web Driver */
    protected WebDriver driver = null;

    public WebDriver getDriver() {
	return driver;
    }

    /**
     * Method to invoke browser in the Connected client PC and set all the necessary preconditions
     * 
     * @param dut
     * @throws Exception
     */
    public WebDriver invokeBrowserInNode(Dut dut) {
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: invokeBrowserInNode()");
	String nodeIP = null;
	String nodePort = null;
	String osType = null;
	int reTryCount = 0;
	try {
	    AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();
	    Device ecatsSettop = (Device) dut;
	    nodeIP = ecatsSettop.getNatAddress();
	    nodePort = ecatsSettop.getNodePort();
	    osType = ecatsSettop.getOsType();
	    driver = validateSeleniunNodeRegisteredStatus(nodeIP, nodePort, osType);
	    if (driver == null) {
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION
			+ "SELENIUM SERVER IS DOWN IN THE CLIENT.TRYING TO START THE SERVER ON {}:{}", nodeIP, nodePort);
		do {
		    reTryCount++;
		    if (ecatsSettop.isLinux() || ecatsSettop.isRaspbianLinux()) {
			driver = registerSeleniumNode(ecatsSettop, tapEnv, Browser.FIREFOX);
		    } else {
			driver = registerSeleniumNode(ecatsSettop, tapEnv, Browser.CHROME);
		    }
		} while (driver == null && reTryCount <= SELENIUM_RETRY_COUNT);
	    }
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "[Selenium Node Connection Failed] : {}:{} {}", nodeIP,
		    nodePort, e.getMessage());
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: invokeBrowserInNode()");
	return driver;
    }

    /**
     * Method to invoke browser in the Connected client PC and set all the necessary preconditions
     * 
     * @param dut
     * @throws Exception
     */
    public WebDriver invokeBrowserInNode(Dut dut, Browser name) {
	LOGGER.info("STARTING METHOD: invokeBrowserInNode()");
	String nodeIP = null;
	String nodePort = null;
	int reTryCount = 0;
	try {
	    AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();
	    Device ecatsSettop = (Device) dut;
	    nodeIP = ecatsSettop.getNatAddress();
	    nodePort = ecatsSettop.getNodePort();
	    if (validateBrowserSupportForNode(ecatsSettop, name)) {
		driver = validateSeleniunNodeRegisteredStatus(ecatsSettop, nodeIP, nodePort, name);
		if (driver == null) {
		    LOGGER.info("SELENIUM NODE STATUS VERIFICATION FAILED.TRYING TO REGISTER THE " + nodeIP + ":"
			    + nodePort + "IN SELENIUM HUB");
		    do {
			reTryCount++;
			driver = registerSeleniumNode(ecatsSettop, tapEnv, name);
		    } while (driver == null && reTryCount <= AutomaticsConstants.RETRY_COUNT);
		}
	    } else {
		LOGGER.error("Invalid browser selected for this OS type!!");
	    }
	} catch (Exception e) {
	    LOGGER.error("[Selenium Node Connection Failed] : " + nodeIP + ":" + nodePort + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: invokeBrowserInNode()");
	return driver;
    }

    /**
     * Method to invoke browser in the Connected client PC and set all the necessary preconditions
     * 
     * @param dut
     * @param name
     * @param capabilities
     * @return
     */
    public WebDriver invokeBrowserInNode(Dut dut, Browser name, CustomizableBrowserCapabilities capabilities) {
	LOGGER.info("STARTING METHOD: invokeBrowserInNode()");
	String nodeIP = null;
	String nodePort = null;
	String osType = null;
	int reTryCount = 0;
	try {
	    AutomaticsTapApi tapEnv = AutomaticsTapApi.getInstance();
	    Device device = (Device) dut;
	    nodeIP = device.getNatAddress();
	    nodePort = device.getNodePort();
	    osType = device.getOsType();
	    if (osType.equalsIgnoreCase(SeleniumConstants.OS_WINDOWS)) {
		copyFilesForscheduling(device, tapEnv, Browser.CHROME);
	    }

	    if (validateBrowserSupportForNode(device, name)) {
		validateSeleniunNodeRegisteredStatus(device, nodeIP, nodePort, name, capabilities);
		if (driver == null) {
		    LOGGER.info("SELENIUM NODE STATUS VERIFICATION FAILED.TRYING TO REGISTER THE " + nodeIP + ":"
			    + nodePort + "IN SELENIUM HUB");
		    do {
			reTryCount++;
			// forceConfigInstall = true;
			driver = registerSeleniumNode(device, tapEnv, name);
		    } while (driver == null && reTryCount <= AutomaticsConstants.RETRY_COUNT);
		}
	    } else {
		LOGGER.error("Invalid browser selected for this OS type!!");
	    }
	} catch (Exception e) {
	    LOGGER.error("[Selenium Node Connection Failed] : " + nodeIP + ":" + nodePort + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: invokeBrowserInNode()");
	return driver;
    }
    
    /**
     * Scheduling selenium process as windows scheduled task
     * 
     * @param dut
     * @param tapEnv
     * @param name
     */
    private static void copyFilesForscheduling(Device dut, AutomaticsTapApi tapEnv, Browser name) {
	LOGGER.info("Entering copyFilesForScehduling");
	boolean status = false;
	String command = null;
	String response = null;
	try {
	    if (!checkScheduledTaskRunning(dut, tapEnv)) {

		String seleniumHomeLocation = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_PATH_FOR_WINDOWS_OS,
			"C:\\Selenium\\");
		String windowsBatFile = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_WIN_BAT_FILE_NAME,
			"Node.bat");
		String nodeVbsFile = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_SCH_VBS_FILE_NAME,
			"SeleniumSchedule.vbs");
		if (copyAndModifyResourceToClient(seleniumHomeLocation, nodeVbsFile, dut)) {
		    if (copyAndModifyResourceToClient(seleniumHomeLocation, windowsBatFile, dut)) {
			if (editScript(dut, seleniumHomeLocation, windowsBatFile, tapEnv, name)) {
			    status = true;
			    grantFullFilePermission(tapEnv, dut, seleniumHomeLocation);
			}
		    }
		}
		if (status) {
		    LOGGER.info("Copied the files successfully");
		    command = AutomaticsPropertyUtility.getProperty(SELENIUM_SCHEDULE_CR_TASK_CMD,
			    "cmd /c schtasks /create /tn \"SeleniumTask\" /sc minute /mo 5 /tr \"wscript.exe C:/Selenium/SeleniumSchedule.vbs\" /ST 00:00:01");
		    tapEnv.executeCommandOnOneIPClients(dut, command);
		    LOGGER.info("Created Scheduled Task and checking the status");
		    command = AutomaticsPropertyUtility.getProperty(SELENIUM_SCHEDULE_LS_TASK_CMD,
			    "cmd /c schtasks /Query | grep \"SeleniumTask\"");
		    response = tapEnv.executeCommandOnOneIPClients(dut, command);
		    LOGGER.info("Scheduled Task Created" + response.toString());
		    command = AutomaticsPropertyUtility.getProperty(SELENIUM_SCHEDULE_RUN_TASK_CMD,
			    "cmd /c  schtasks /Run /TN \"SeleniumTask\"");
		    response = tapEnv.executeCommandOnOneIPClients(dut, command);
		    LOGGER.info("Scheduled Task executed first time manually" + response.toString());
		}
	    }

	} catch (Exception e) {
	    LOGGER.info("Exception in Method copyFilesForScheduling" + e.getMessage());
	}

	LOGGER.info("Copied the files and created task successfully " + status);

    }
    
    /**
     * 
     * @param device
     * @param tapEnv
     * @return
     */
    private static boolean checkScheduledTaskRunning(Device device,AutomaticsTapApi tapEnv)
    {
    	LOGGER.info("Entering checkScheduledTaskRunning");
    	boolean status = false;
    	try
    	{
    		String command = AutomaticsPropertyUtility.getProperty(SELENIUM_SCHEDULE_LS_TASK_CMD,"cmd /c schtasks /Query | grep \"SeleniumTask\"");
    		String response = tapEnv.executeCommandOnOneIPClients(device, command);
    		LOGGER.info("Whether Scheduled Task already running"+response);
    		if(CommonMethods.isNotNull(response)) {
    			LOGGER.info("Scheduled Task is already running");
    			status = true;
    		}
    		LOGGER.info("Scheduled Task running status "+status);
    	}
    	catch(Exception e)
    	{
    		LOGGER.info("Exception in checkScheduledTaskRunning "+e.getMessage());
    		
    	}
    	return status;
    }
    
    private boolean validateBrowserSupportForNode(Device device, Browser name) {
	String supportedBrowsers = AutomaticsConstants.EMPTY_STRING;
	boolean isValidSelection = false;
	if (device.isWindows()) {
	    supportedBrowsers = AutomaticsPropertyUtility.getProperty("broswer.support.windows",
		    "firefox_gecko,chrome,edge");
	} else if (device.isLinux() || device.isRaspbianLinux()) {
	    supportedBrowsers = AutomaticsPropertyUtility.getProperty("broswer.support.linux", "firefox_gecko,chrome");
	} else if (device.isMacOS()) {
	    supportedBrowsers = AutomaticsPropertyUtility.getProperty("broswer.support.mac", "safari,chrome");
	}
	if (supportedBrowsers.contains(name.getValue())) {
	    isValidSelection = true;
	}
	LOGGER.info("Validating browser selection for client - " + isValidSelection);
	return isValidSelection;
    }

    /**
     * Method to close the Driver
     */
    public void closeBrowser() {
	driver.quit();
    }

    /**
     * Method used to validate the Selenium node registered status
     * 
     * @param nodeIP
     *            Selenium node IP for connected client
     * @param nodePort
     *            Selenium node port for connected client
     * @param osType
     *            Connected client OS type
     * @return driver instance of{@link WebDriver}
     * 
     * @throws Exception
     */
    public WebDriver validateSeleniunNodeRegisteredStatus(String nodeIP, String nodePort, String osType) {
	LOGGER.debug("STARTING METHOD: validateSeleniunNodeRegisteredStatus()");
	URL nodeURL = null;
	try {
	    if (CommonMethods.isNotNull(nodeIP) && CommonMethods.isNotNull(nodePort) && CommonMethods.isNotNull(osType)) {
		nodeURL = new URL(AutomaticsConstants.STRING_HTTP + nodeIP + ":" + nodePort
			+ SeleniumConstants.STRING_HUB);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Connecting to Selenium :" + nodeURL);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Client OS Type:" + osType);
		if (osType.equalsIgnoreCase(SeleniumConstants.OS_LINUX)
			|| osType.equalsIgnoreCase(SeleniumConstants.OS_RASPBIAN_LINUX)) {
		    DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		    capabilities.setCapability(BROWSER_CAPABILITY_MARIONETTE, false);
		    capabilities.setPlatform(Platform.LINUX);
		    capabilities.setBrowserName(BROWSER_NAME_FIREFOX);
		    driver = new RemoteWebDriver(nodeURL, capabilities);
		} else {
		    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		    driver = new RemoteWebDriver(nodeURL, capabilities);
		}
	    } else {
		LOGGER.error("NODE PORT OR NODE IP OR OS TYPE NOT CONFIURED PROPERLY IN DEVICE INVENTORY");
	    }
	} catch (MalformedURLException ex) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "[Selenium Node Connection Failed] : {} : {} Problem with the URL formed", nodeIP, nodePort);
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred validateSeleniunNodeRegisteredStatus {} : {} {}", nodeIP, nodePort,
		    e.getMessage());
	    // e.printStackTrace();
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: validateSeleniunNodeRegisteredStatus()");
	return driver;
    }

    /**
     * Method used to validate the Selenium node registered status
     * 
     * @param nodeIP
     *            Selenium node IP for connected client
     * @param nodePort
     *            Selenium node port for connected client
     * @param osType
     *            Connected client OS type
     * @return driver instance of{@link WebDriver}
     * 
     * @throws Exception
     */
    public WebDriver validateSeleniunNodeRegisteredStatus(Device ecatsSettop, String nodeIP, String nodePort,
	    Browser name) {
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: validateSeleniunNodeRegisteredStatus()");
	URL nodeURL = null;
	try {
	    if (CommonMethods.isNotNull(nodeIP) && CommonMethods.isNotNull(nodePort) && name != null) {
		nodeURL = new URL(AutomaticsConstants.STRING_HTTP + nodeIP + ":" + nodePort
			+ SeleniumConstants.STRING_HUB);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Connecting to Selenium Node:" + nodeURL);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Browser Type:" + name.getValue());
		DesiredCapabilities capabilities = null;
		switch (name) {
		case FIREFOX:
		    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Setting capabilities for Firefox driver");
		    capabilities = DesiredCapabilities.firefox();
		    capabilities.setBrowserName(BROWSER_NAME_FIREFOX);

		    // firefox version to be used against

		    if (ecatsSettop.isLinux() || ecatsSettop.isRaspbianLinux()) {
			capabilities.setPlatform(Platform.LINUX);
			capabilities.setCapability(BROWSER_CAPABILITY_HEADLESS, true);
			capabilities.setCapability(BROWSER_CAPABILITY_MARIONETTE, false);
		    } else if (ecatsSettop.isWindows()) {
			FirefoxOptions options = new FirefoxOptions();
			options.addArguments(AutomaticsConstants.HYPHEN + BROWSER_CAPABILITY_HEADLESS);
			capabilities.setPlatform(Platform.WINDOWS);
			capabilities.setCapability(BROWSER_CAPABILITY_MARIONETTE, true);
			capabilities.merge(options);
		    }
		    break;
		case CHROME:
		    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Setting capabilities for chrome driver");
		    capabilities = DesiredCapabilities.chrome();
		    ChromeOptions options = new ChromeOptions();
		    options.addArguments(AutomaticsConstants.HYPHEN + AutomaticsConstants.HYPHEN
			    + BROWSER_CAPABILITY_HEADLESS);
		    options.addArguments(BROWSER_CAPABILITY_NO_SANDBOX);
		    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		    if (ecatsSettop.isLinux() || ecatsSettop.isRaspbianLinux()) {
			capabilities.setPlatform(Platform.LINUX);
		    } else if (ecatsSettop.isWindows()) {
			capabilities.setPlatform(Platform.WINDOWS);
		    }
		    capabilities.setBrowserName(BROWSER_NAME_CHROME);
		    break;
		case SAFARI:

		    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Setting capabilities for safari driver");
		    capabilities = DesiredCapabilities.safari();
		    capabilities.setPlatform(Platform.MAC);
		    capabilities.setBrowserName(BROWSER_NAME_SAFARI);
		    break;
		case IE:
		    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Setting capabilities for Microsoft edge driver");
		    capabilities = DesiredCapabilities.edge();
		    if (ecatsSettop.isWindows()) {
			capabilities.setPlatform(Platform.WINDOWS);
			capabilities.setBrowserName(BROWSER_NAME_EDGE);

		    }
		    break;
		default:
		    break;
		}
		LOGGER.info("+++++++++++++++++++++ DEBUG RemoteWebDriver +++++++++++++++++++")
		LOGGER.info(nodeURL);
		LOGGER.info(capabilities.getValue);
		LOGGER.info("+++++++++++++++++++++ DEBUG RemoteWebDriver +++++++++++++++++++")
		driver = new RemoteWebDriver(nodeURL, capabilities);
	    } else {
		LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
			+ "NODE PORT OR NODE IP OR OS TYPE NOT CONFIURED PROPERLY IN DEVICE INVENTORY");
	    }
	} catch (MalformedURLException ex) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "[Selenium Node Connection Failed] : " + nodeIP + ":"
		    + nodePort + " Problem with the URL formed");
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred validateSeleniunNodeRegisteredStatus() {} : {} {}", nodeIP, nodePort,
		    e.getMessage());
	    e.printStackTrace();
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: validateSeleniunNodeRegisteredStatus()");
	return driver;
    }
    
    /**
     * Method used to validate the Selenium node registered status
     * 
     * @param nodeIP
     *            Selenium node IP for connected client
     * @param nodePort
     *            Selenium node port for connected client
     * @param osType
     *            Connected client OS type
     * @return driver instance of{@link WebDriver}
     * 
     * @throws Exception
     */
    private void validateSeleniunNodeRegisteredStatus(Device ecatsSettop, String nodeIP, String nodePort,
	    Browser name, CustomizableBrowserCapabilities capabilities) {
	Platform platform = null;
	if (ecatsSettop.isLinux() || ecatsSettop.isRaspbianLinux()) {
	    platform = Platform.LINUX;
	} else if (ecatsSettop.isWindows()) {
	    platform = Platform.WINDOWS;
	} else if (ecatsSettop.isMacOS()) {
	    platform = Platform.MAC;
	}
	if (capabilities == null) {
	    // Browser initialized with default framework parameters
	    LOGGER.info("Confguring driver Options to default configurations in framework");
	    capabilities = new CustomizableBrowserCapabilities(name, platform,ecatsSettop);
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: validateSeleniunNodeRegisteredStatus()");
	URL nodeURL = null;
	try {
	    if (CommonMethods.isNotNull(nodeIP) && CommonMethods.isNotNull(nodePort) && name != null) {
		nodeURL = new URL(AutomaticsConstants.STRING_HTTP + nodeIP + ":" + nodePort + AutomaticsConstants.STRING_HUB);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Connecting to Selenium Node:" + nodeURL);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Browser Type:" + name.getValue());
		driver = new RemoteWebDriver(nodeURL, capabilities);
	    } else {
		LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
			+ "NODE PORT OR NODE IP OR OS TYPE NOT CONFIURED PROPERLY IN CATS INVENTORY");
	    }
	} catch (MalformedURLException ex) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "[Selenium Node Connection Failed] : " + nodeIP + ":"
		    + nodePort + " Problem with the URL formed");
	} catch (Exception e) {
	    LOGGER.error(
		    LOGGER_PREFIX_CONFIG_VALIDATION
			    + "Exception Occurred validateSeleniunNodeRegisteredStatus() {} : {} {}",
		    nodeIP, nodePort, e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Method used to register the Selenium node for connected client
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return driver instance of{@link WebDriver}
     * 
     * @throws Exception
     */
    public WebDriver registerSeleniumNode(Device device, AutomaticsTapApi tapEnv, Browser name) {
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: registerSeleniumNode()");
	String command = null;
	String response = null;
	driver = null;
	String nattedIpAddress = null;
	try {
	    nattedIpAddress = device.getNatAddress() + AutomaticsConstants.COLON + device.getNodePort();
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "NATTED IP ADDRESS {}", nattedIpAddress);
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "USER NAME : {} ", device.getUsername());
	    if ((device.getOsType().equalsIgnoreCase(SeleniumConstants.OS_LINUX) || device.getOsType()
		    .equalsIgnoreCase(SeleniumConstants.OS_RASPBIAN_LINUX))
		    && validateXvfbProcessInEthernetClient(device, tapEnv, name)) {
		command = getSeleniumRegisterCommandForLinuxRaspberrypi(device, tapEnv);
		tapEnv.executeCommandOnOneIPClients(device, command);
		tapEnv.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);
		// verifying the Selenium Process on LINUX
		response = CommonMethods.patternFinder(
			tapEnv.executeCommandOnOneIPClients(device, SeleniumConstants.PID_OF_SELENIUM),
			SeleniumConstants.EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_LINUX);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "PROCESS ID FOR SELENIUM ON LINUX :" + response);
	    } else if (device.getOsType().equalsIgnoreCase(AutomaticsConstants.OS_MAC)
		    && CommonMethods.isNotNull(device.getUsername())
		    && validateSeleniumConfigurationFiles(device, tapEnv, name)
		    && validateProcessRunningStatus(device, tapEnv, SeleniumConstants.PID_OF_SELENIUM,
			    SeleniumConstants.EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_MAC)) {
		command = getSeleniumRegisterCommandForMac(device, tapEnv);
		tapEnv.executeCommandOnOneIPClients(device, command);
		tapEnv.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);
		// verifying the Selenium Process on MAC
		response = CommonMethods.patternFinder(
			tapEnv.executeCommandOnOneIPClients(device, SeleniumConstants.PID_OF_SELENIUM),
			SeleniumConstants.EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_MAC);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "PROCESS ID FOR SELENIUM ON MAC :" + response);
	    } else if (device.getOsType().equalsIgnoreCase(SeleniumConstants.OS_WINDOWS)
		    && validateSeleniumConfigurationFiles(device, tapEnv, name)) {
		command = getSeleniumRegisterCommandForWindows(device, tapEnv);
		int retry = 11;
		do {
		    tapEnv.executeCommandOnOneIPClients(device, command);
		    tapEnv.waitTill(AutomaticsConstants.FIFTEEN_SECONDS);
		    // verifying the Java Process on Windows
		    response = tapEnv.executeCommandOnOneIPClients(device,
			    SeleniumConstants.GREP_CMD_JAVA_PROCESS_FOR_WINDOWS);
		    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "PROCESS ID FOR JAVA ON WINDOWS :" + response);
		    if (CommonMethods.isNotNull(command) && response.contains("java.exe")) {
			retry = 0;
		    } else {
			AutomaticsUtils.sleep(AutomaticsConstants.ONE_MINUTE);
			retry--;
		    }
		} while (retry > 0);
	    }
	    LOGGER.info("Response :" + response);
	    if (CommonMethods.isNotNull(command)
		    && (response.contains("java.exe") || NumberUtils.isCreatable(response))) {
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "SELENIUM NODE REGISTER COMMAND EXECUTED FOR "
			+ nattedIpAddress);
		driver = validateSeleniunNodeRegisteredStatus(device, device.getNatAddress(), device.getNodePort(),
			name);
	    }
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred registerSeleniumNode() Natted IpAddress:" + nattedIpAddress + " UserName:"
		    + device.getUsername() + " " + e.getMessage());
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: registerSeleniumNode()");
	return driver;
    }

    /**
     * Method used to get the register command for Linux/Raspberrypi Clients
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return command Command to register
     * @throws Exception
     */
    public static String getSeleniumRegisterCommandForLinuxRaspberrypi(Device settop, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD: getSeleniumRegisterCommandForLinuxRaspberrypi()");
	String command = null;
	String homePath = null;
	StringBuffer sbCommand = new StringBuffer();
	try {
	    // Selenium Grid register command for Linux/Raspberrypi OS/kali
	    String homeLocation = tapEnv.executeCommandOnOneIPClients(settop, COMMAND_HOME_FOLDER_CLIENT);
	    homePath = CommonMethods.stripNewLine(homeLocation) + AutomaticsConstants.FORWARD_SLASH
		    + AutomaticsPropertyUtility.getProperty(NAME_SELENIUM_HOME, "Selenium/");

	    LOGGER.info("LINUX OS HOME PATH :" + homePath);
	    tapEnv.executeCommandOnOneIPClients(settop, AutomaticsConstants.COMMAND_REMOVE + homePath
		    + SeleniumConstants.SELENIUM_STDOUT_LOG_FILENAME);
	    String shellScriptToExecute = homePath + AutomaticsConstants.FORWARD_DOUBLE_SLASH
		    + AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_LINUX_OS_SH_FILE_NAME);
	    tapEnv.executeCommandOnOneIPClients(settop, "count=$(grep -c wait " + shellScriptToExecute
		    + ");if [ $count != 1 ];then echo 'wait' >> " + shellScriptToExecute + ";fi");
	    command = AutomaticsTapApi.getSTBPropsValue(SeleniumConstants.SELENIUM_REGISTER_CMD_FOR_LIUNX_OS);
	    sbCommand
		    .append(getFilePathCommand(command, SeleniumConstants.REPLACE_STRING_SH_FILE_LOCATION, homePath))
		    .append(AutomaticsConstants.FORWARD_DOUBLE_SLASH)
		    .append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_LINUX_OS_SH_FILE_NAME))

		    .append(">> ").append(homePath).append(SeleniumConstants.SELENIUM_STDOUT_LOG_FILENAME);
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred getSeleniumRegisterCommandForLinuxRaspberrypi() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getSeleniumRegisterCommandForLinuxRaspberrypi()");
	return sbCommand.toString();
    }

    /**
     * Method used to get the register command for MAC Clients
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return command Command to register
     * @throws Exception
     */
    public static String getSeleniumRegisterCommandForMac(Device settop, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD: getSeleniumRegisterCommandForMac()");
	String command = null;
	String homePath = null;
	StringBuffer sbCommand = new StringBuffer();
	try {
	    // Selenium Grid register command for MAC OS
	    homePath = AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_PATH_FOR_MAC_OS);
	    homePath = homePath.replace(AutomaticsConstants.REPLACE_STRING_USER_NAME, settop.getUsername());
	    tapEnv.executeCommandOnOneIPClients(settop, AutomaticsConstants.COMMAND_REMOVE + homePath
		    + SeleniumConstants.SELENIUM_STDOUT_LOG_FILENAME);
	    tapEnv.executeCommandOnOneIPClients(settop, command);
	    command = AutomaticsTapApi.getSTBPropsValue(SeleniumConstants.SELENIUM_REGISTER_CMD_FOR_MAC_OS);
	    sbCommand
		    .append(getFilePathCommand(command, SeleniumConstants.REPLACE_STRING_SH_FILE_LOCATION, homePath))
		    .append(AutomaticsConstants.FORWARD_DOUBLE_SLASH)
		    .append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_MAC_OS_SH_FILE_NAME)).append(">> ")
		    .append(homePath).append(SeleniumConstants.SELENIUM_STDOUT_LOG_FILENAME);
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred getSeleniumRegisterCommandForMac() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getSeleniumRegisterCommandForMac()");
	return sbCommand.toString();
    }

    /**
     * Method used to get the register command for Windows Clients
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return command Command to register
     * @throws Exception
     */
    public static String getSeleniumRegisterCommandForWindows(Device settop, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD getSeleniumRegisterCommandForWindows()");
	String command = null;
	StringBuffer filePath = new StringBuffer();
	try {
	    // Selenium Grid register command for Windows OS
	    killAndValidateJavaProcessRunningStatusOnWindos(settop, tapEnv);
	    command = AutomaticsTapApi.getSTBPropsValue(SeleniumConstants.SELENIUM_REGISTER_CMD_FOR_WONDOWS_OS);
	    filePath.append(AutomaticsConstants.SYMBOL_QUOTES)
		    .append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_PATH_FOR_WINDOWS_OS))
		    .append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_WIN_VBS_FILE_NAME))
		    .append(AutomaticsConstants.SYMBOL_QUOTES);
	    command = getFilePathCommand(command, SeleniumConstants.REPLACE_STRING_VBS_FILE_LOCATION,
		    filePath.toString());
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred getSeleniumRegisterCommandForWindows() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD registerSeleniumNode()");
	return command;
    }

    /**
     * Method used to replace the command placeholder values to actual command
     * 
     * @param command
     *            Actual command
     * @param placeHolder
     *            Placeholder to replace
     * @param commandToReplace
     *            Command to replace
     * @return replacedCommand
     */
    public static String getFilePathCommand(String command, String placeHolder, String commandToReplace) {
	LOGGER.debug("STARTING METHOD: getFilePathCommand()");
	String replacedCommand = null;
	try {
	    replacedCommand = command.replace(placeHolder, commandToReplace);
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred getFilePathCommand() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: getFilePathCommand()");
	return replacedCommand;
    }

    /**
     * Method used to validate the XVFB process running status in Ethernet connected client
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return status True - Process Running. False -Process not running
     * 
     * @throws Exception
     */
    public static boolean validateXvfbProcessInEthernetClient(Device settop, AutomaticsTapApi tapEnv, Browser name) {
	LOGGER.debug("STARTING METHOD : validateXvfbProcessInEthernetClient()");
	boolean status = false;
	try {
	    status = CommonMethods.isNotNull(settop.getUsername())
		    && validateSeleniumConfigurationFiles(settop, tapEnv, name)
		    && validateJavaProcessRunningStatusOnLinux(settop, tapEnv)
		    && validateProcessRunningStatus(settop, tapEnv, SeleniumConstants.GET_PID_OF_XVFB,
			    SeleniumConstants.EXPECTED_PATTERN_FOR_XVFB_PROCESS_NOHUB)
		    && validateProcessRunningStatus(settop, tapEnv, SeleniumConstants.GET_PID_OF_XVFB,
			    SeleniumConstants.EXPECTED_PATTERN_FOR_XVFB_PROCESS)
		    && validateProcessRunningStatus(settop, tapEnv, SeleniumConstants.PID_OF_SELENIUM,
			    SeleniumConstants.EXPECTED_PATTERN_FOR_SELENIUM_PROCESS_LINUX);
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred validateXvfbProcessInEthernetClient() : {}", e.getMessage());
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD : validateXvfbProcessInEthernetClient()");
	return status;
    }

    /**
     * Method used to validate the Selenium configuration files
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return status True - Configuration validation Success. False -Configuration validation Failed
     * 
     * @throws Exception
     */
    public static boolean validateSeleniumConfigurationFiles(Device settop, AutomaticsTapApi tapEnv, Browser name) {
	LOGGER.debug("STARTING METHOD: validateSeleniumConfigurationFiles()");
	boolean status = false;
	String nattedIpAddress = null;
	try {
	    nattedIpAddress = settop.getNatAddress() + AutomaticsConstants.COLON + settop.getNodePort();
	    if (settop.getOsType().equalsIgnoreCase(SeleniumConstants.OS_LINUX)
		    || settop.getOsType().equalsIgnoreCase(SeleniumConstants.OS_RASPBIAN_LINUX)) {
		status = configurationValidationForLinuxRaspberrypi(settop, tapEnv, nattedIpAddress);
	    } else if (settop.getOsType().equalsIgnoreCase(AutomaticsConstants.OS_MAC)) {
		status = configurationValidationForMac(settop, tapEnv, nattedIpAddress);
	    } else if (settop.getOsType().equalsIgnoreCase(SeleniumConstants.OS_WINDOWS)) {
		status = configurationValidationForWindows(settop, tapEnv, nattedIpAddress, name);
	    } else {
		LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "PREFERRED OS TYPE {}  NOT SUPPORTED FOR : {}",
			settop.getOsType(), nattedIpAddress);
	    }
	} catch (Exception e) {
	    LOGGER.error(
		    LOGGER_PREFIX_CONFIG_VALIDATION + "Exception Occurred validateSeleniumConfigurationFiles() {}",
		    e.getMessage());
	}
	LOGGER.info(
		LOGGER_PREFIX_CONFIG_VALIDATION + "Validtaion of Selenium configurations complete with status : {}",
		status);
	return status;
    }

    /**
     * Method used to validate the configuration validation for Linux/Raspberrypi
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param nattedIpAddress
     *            Natted IP Address
     * @return status True - Configuration validation Success. False -Configuration validation Failed
     * 
     * @throws Exception
     */
    public static boolean configurationValidationForLinuxRaspberrypi(Device settop, AutomaticsTapApi tapEnv,
	    String nattedIpAddress) {
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: configurationValidationForLinuxRaspberrypi()");
	boolean status = false;

	// Configuration validation for Linux/Raspberrypi Client
	// Selenium Grid register command for Linux/Raspberrypi OS/Kali Linux
	String homeLocation = tapEnv.executeCommandOnOneIPClients(settop, COMMAND_HOME_FOLDER_CLIENT);
	String seleniumhomePath = CommonMethods.stripNewLine(homeLocation) + AutomaticsConstants.FORWARD_SLASH
		+ AutomaticsPropertyUtility.getProperty(NAME_SELENIUM_HOME, "Selenium/");
	String linuxConfigFile = AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_LINUX_OS_SH_FILE_NAME);
	LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "VALIDATING CONFIGURATION IN LINUX/RASPBERRYBI OS CLIENT :"
		+ nattedIpAddress);
	try {
	    if (copyAndModifyResourceToClient(seleniumhomePath, linuxConfigFile, settop)) {
		if (editScript(settop, seleniumhomePath, linuxConfigFile, tapEnv, Browser.FIREFOX)) {
		    status = true;
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred configurationValidationForLinuxRaspberrypi() {}", e.getMessage());
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: configurationValidationForLinuxRaspberrypi()");
	return status;
    }

    /**
     * Method used to validate the configuration validation for MAC
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param nattedIpAddress
     *            Natted IP Address
     * @return status True - Configuration validation Success. False -Configuration validation Failed
     * 
     * @throws Exception
     */
    public static boolean configurationValidationForMac(Device settop, AutomaticsTapApi tapEnv, String nattedIpAddress) {
	LOGGER.debug("STARTING METHOD: configurationValidationForMac()");
	boolean status = false;
	String command = null;
	String response = null;
	StringBuffer sbCommand = new StringBuffer();
	try {
	    // Configuration validation for MAC Client
	    LOGGER.info("VALIDATING CONFIGURATION IN MAC OS CLIENT :" + nattedIpAddress);
	    sbCommand.append(LinuxCommandConstants.CMD_LIST_FOLDER_FILES).append(AutomaticsConstants.SPACE)
		    .append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_PATH_FOR_MAC_OS));
	    command = sbCommand.toString().replace(AutomaticsConstants.REPLACE_STRING_USER_NAME, settop.getUsername());
	    response = tapEnv.executeCommandOnOneIPClients(settop, command);
	    status = CommonMethods.isNotNull(response)
		    && CommonMethods.validateTextUsingRegularExpression(response,
			    AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_MAC_OS_JAR_FILE_NAME))
		    && CommonMethods.validateTextUsingRegularExpression(response,
			    AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_CHROME_DRIVER_FILE_NAME))
		    && CommonMethods.validateTextUsingRegularExpression(response,
			    AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_MAC_OS_SH_FILE_NAME));
	    if (status) {
		sbCommand = new StringBuffer();
		sbCommand.append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_PATH_FOR_MAC_OS))
			.append(AutomaticsConstants.FORWARD_DOUBLE_SLASH)
			.append(AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_MAC_OS_SH_FILE_NAME));
		command = sbCommand.toString().replace(AutomaticsConstants.REPLACE_STRING_USER_NAME,
			settop.getUsername());
		command = LinuxCommandConstants.COMMAND_CAT + AutomaticsConstants.SPACE + command;
		status = validateNattedIpAddressInConfigFile(settop, tapEnv, command, nattedIpAddress);
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred configurationValidationForMac() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: configurationValidationForMac()");
	return status;
    }

    /**
     * Method used to validate the configuration validation for Windows
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param nattedIpAddress
     *            Natted IP Address
     * @return status True - Configuration validation Success. False -Configuration validation Failed
     * 
     * @throws Exception
     */
    public static boolean configurationValidationForWindows(Device settop, AutomaticsTapApi tapEnv,
	    String nattedIpAddress, Browser name) {
	LOGGER.debug("STARTING METHOD: configurationValidationForWindows()");
	boolean status = false;
	String seleniumHomeLocation = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_PATH_FOR_WINDOWS_OS,
		"C:\\Selenium\\");
	String windowsBatFile = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_WIN_BAT_FILE_NAME, "Node.bat");
	String nodeVbsFile = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_WIN_VBS_FILE_NAME,
		"NodeRegister.vbs");
	try {
	    // Configuration validation for Windows Client
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "VALIDATING CONFIGURATION IN WINDOWS OS CLIENT : {}",
		    nattedIpAddress);
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "Proceeding to copy required configuration files");

	    if (copyAndModifyResourceToClient(seleniumHomeLocation, nodeVbsFile, settop)) {
		if (copyAndModifyResourceToClient(seleniumHomeLocation, windowsBatFile, settop)) {
		    if (editScript(settop, seleniumHomeLocation, windowsBatFile, tapEnv, name)) {
			status = true;
		    }
		}
	    }

	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "Exception Occurred configurationValidationForWindows() {}",
		    e.getMessage());
	}
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: configurationValidationForWindows()");
	return status;
    }

    private static boolean checkForRequiredFilesInWindows(Dut dut, AutomaticsTapApi tapEnv,
	    String seleniumHomeLocation, Browser name) {
	boolean status = false;
	String browserDriver = getDriverForWindows(name);
	String seleniumExecuterPackage = AutomaticsPropertyUtility.getProperty(
		SELENIUM_CONFIG_WIN_LIUNX_OS_JAR_FILE_NAME, "selenium-server-standalone-3.11.0.jar");
	String seleniumConfigBatFIle = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_WIN_BAT_FILE_NAME,
		"Node.bat");
	String seleniumConfigVbsFile = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_WIN_VBS_FILE_NAME,
		"NodeRegister.vbs");

	StringBuffer sbCommand = new StringBuffer();
	sbCommand.append(LinuxCommandConstants.CMD_LIST_FOLDER_FILES).append(AutomaticsConstants.SPACE)
		.append(AutomaticsConstants.SYMBOL_QUOTES).append(seleniumHomeLocation)
		.append(AutomaticsConstants.SYMBOL_QUOTES);
	String response = tapEnv.executeCommandOnOneIPClients(dut, sbCommand.toString());
	status = CommonMethods.isNotNull(response)
		&& CommonMethods.validateTextUsingRegularExpression(response, seleniumExecuterPackage)
		&& CommonMethods.validateTextUsingRegularExpression(response, seleniumConfigBatFIle)
		&& CommonMethods.validateTextUsingRegularExpression(response, seleniumConfigVbsFile)
		&& CommonMethods.validateTextUsingRegularExpression(response, browserDriver);

	return status;
    }

    private static void grantFullFilePermission(AutomaticsTapApi tapEnv, Device settop, String seleniumHome) {
	String response = tapEnv.executeCommandOnOneIPClients(settop, "echo Y|cacls \"" + seleniumHome
		+ "*\" /T /grant \"" + settop.getUsername() + "\":F");
	if (CommonMethods.isNotNull(response) && response.contains(ERROR_CALCS_DEPRECATED)) {
	    tapEnv.executeCommandOnOneIPClients(settop,
		    "icacls \"" + seleniumHome + "*\" /grant " + settop.getUsername() + ":F /T");
	}
    }

    /**
     * @param name
     * @return
     */
    private static String getDriverForWindows(Browser name) {
	String drivarName = AutomaticsConstants.EMPTY_STRING;
	switch (name) {
	case CHROME:
	    drivarName = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_CHROME_DRIVER_FILE_NAME,
		    "chromedriver.exe");
	    break;
	case IE:
	    drivarName = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_EDGE_DRIVER_FILE_NAME,
		    "C://Windows//System32//MicrosoftWebdriver.exe");
	    break;
	case FIREFOX:
	    drivarName = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_FIREFOX_DRIVER_FILE_NAME,
		    "geckodriver.exe");
	    break;
	default:
	    drivarName = AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_CHROME_DRIVER_FILE_NAME,
		    "chromedriver.exe");
	    break;
	}
	LOGGER.info("Driver " + drivarName);
	return drivarName;
    }

    /**
     * Method used to validate the natted IP Address in configuration file
     * 
     * @param dut
     *            instance of{@link Device}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param command
     *            command to read the configuration file
     * @param nattedIpAddress
     *            Natted Ip Address
     * @return status True - Natted Ip Address verification Success. False -Natted Ip Address verification Failed
     * @throws Exception
     * 
     */
    public static boolean validateNattedIpAddressInConfigFile(Device settop, AutomaticsTapApi tapEnv, String command,
	    String nattedIpAddress) {
	LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: validateNattedIpAddressInConfigFile()");
	boolean status = false;
	String response = null;
	try {
	    response = tapEnv.executeCommandOnOneIPClients(settop, command);
	    status = CommonMethods.isNotNull(response)
		    && CommonMethods.validateTextUsingRegularExpression(response, nattedIpAddress);
	    if (!status) {
		LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "INVALID NATTED IP AND PORT FOR CONNECTED CLIENT : {}",
			nattedIpAddress);
	    }
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred validateNattedIpAddressInConfigFile() {}", e.getMessage());
	}
	LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "ENDING METHOD: validateNattedIpAddressInConfigFile()");
	return status;
    }

    /**
     * Method used to validate the process running status
     * 
     * @param dut
     *            instance of{@link Dut}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @param processName
     *            Name of the process to validate
     * @param ExpcetedPattern
     *            Pattern to match
     * @return status True - Process not running. False -Still process running
     * 
     * @throws Exception
     */

    public static boolean validateProcessRunningStatus(Device settop, AutomaticsTapApi tapEnv, String processName,
	    String ExpcetedPattern) {
	LOGGER.debug(LOGGER_PREFIX_CONFIG_VALIDATION + "STARTING METHOD: validateProcessRunningStatus()");
	boolean status = false;
	String pidOfProcess = null;
	StringBuffer sbCommand = new StringBuffer();
	try {
	    // verifying the Java Process
	    pidOfProcess = CommonMethods.patternFinder(tapEnv.executeCommandOnOneIPClients(settop, processName),
		    ExpcetedPattern);
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "PID :{}", pidOfProcess);
	    if (CommonMethods.isNotNull(pidOfProcess)) {
		sbCommand.append(ProcessRestartOption.KILL_9.getCommand()).append(AutomaticsConstants.SPACE)
			.append(pidOfProcess);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "KILL CMD :{}", sbCommand.toString());
		tapEnv.executeCommandOnOneIPClients(settop, sbCommand.toString());
		pidOfProcess = CommonMethods.patternFinder(tapEnv.executeCommandOnOneIPClients(settop, processName),
			ExpcetedPattern);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "PID AFTER KILL :{}", pidOfProcess);
	    }
	    status = CommonMethods.isNull(pidOfProcess);
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION + "Exception Occurred validateProcessRunningStatus() {}",
		    e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: validateProcessRunningStatus()");
	return status;
    }

    /**
     * Method used to validate the java process running status on Linux or Mac Os
     * 
     * @param dut
     *            instance of{@link Device}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return status True - Process not running. False -Still process running
     * 
     * @throws Exception
     */
    public static boolean validateJavaProcessRunningStatusOnLinux(Device settop, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD: validateJavaProcessRunningStatusOnLinux()");
	boolean status = false;
	String pidOfJavaProcess = null;
	try {
	    // verifying the Java Process on Linux OS
	    pidOfJavaProcess = tapEnv.executeCommandOnOneIPClients(settop, SeleniumConstants.GET_PID_OF_JAVA);
	    LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "JAVA PID ON LINUX  : {} ", pidOfJavaProcess);
	    if (CommonMethods.isNotNull(pidOfJavaProcess)) {
		tapEnv.executeCommandOnOneIPClients(settop, ProcessRestartOption.KILL_9.getCommand()
			+ AutomaticsConstants.SPACE + pidOfJavaProcess);
		pidOfJavaProcess = tapEnv.executeCommandOnOneIPClients(settop, SeleniumConstants.GET_PID_OF_JAVA);
		LOGGER.info(LOGGER_PREFIX_CONFIG_VALIDATION + "JAVA PID AFTER KILL ON LINUX/MAC:" + pidOfJavaProcess);
	    }
	    status = CommonMethods.isNull(pidOfJavaProcess);
	} catch (Exception e) {
	    LOGGER.error(LOGGER_PREFIX_CONFIG_VALIDATION
		    + "Exception Occurred validateJavaProcessRunningStatusOnLinux() {}", e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: validateJavaProcessRunningStatusOnLinux()");
	return status;
    }

    /**
     * Method used to validate the java process on Windows client
     * 
     * @param dut
     *            instance of{@link Device}
     * @param tapEnv
     *            instance of {@link AutomaticsTapApi}
     * @return status True - Process not running. False -Still process running
     * @throws Exception
     */
    public static boolean killAndValidateJavaProcessRunningStatusOnWindos(Device settop, AutomaticsTapApi tapEnv) {
	LOGGER.debug("STARTING METHOD: killAndValidateJavaProcessRunningStatusOnWindos()");
	String response = null;
	boolean status = false;
	try {
	    // verifying the Java Process on Windows
	    response = tapEnv.executeCommandOnOneIPClients(settop,
		    SeleniumConstants.GREP_CMD_JAVA_PROCESS_FOR_WINDOWS);
	    if (CommonMethods.isNotNull(response)) {
		response = tapEnv.executeCommandOnOneIPClients(settop, SeleniumConstants.JAVA_KILL_CMD_FOR_WINDOWS);
		LOGGER.info("JAVA KILL SUCCESS RESPONSE ON WINDOWS :" + response);
		// verifying the Java Process on Windows after the kill process
		response = tapEnv.executeCommandOnOneIPClients(settop,
			SeleniumConstants.GREP_CMD_JAVA_PROCESS_FOR_WINDOWS);
		LOGGER.info("PROCESS ID FOR JAVA AFTER KILL PROCESS ON WINDOWS :" + response);
		status = CommonMethods.isNull(response);
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception Occurred killAndValidateJavaProcessRunningStatusOnWindos() " + e.getMessage());
	}
	LOGGER.debug("ENDING METHOD: killAndValidateJavaProcessRunningStatusOnWindos()");
	return status;
    }

    /**
     * Method to copy resource file in core src/main/resources folder and copy it to natted client. Edit the resource
     * with proper values and move it to Selenium home in clients.
     * 
     * @param fileToCopy
     *            FileName to copy.
     * @param dut
     *            Connected client dut obj
     * @return Status of copying is returned
     */
    private static boolean copyAndModifyResourceToClient(String remoteLocation, String fileToCopy, Dut dut) {
	boolean status = false;
	try {
	    Device ecatsSettop = (Device) dut;
	    if (((Device) dut).isWindows()) {
		String driver = CommonMethods.patternFinder(remoteLocation, "([A-Za-z]):");
		remoteLocation = remoteLocation.replace(driver + ":", "/cygdrive/" + driver.toLowerCase() + "/");
	    }
	    DeviceConnectionProvider connectionProvider = BeanUtils.getDeviceConnetionProvider();
	    if (null != connectionProvider) {
		connectionProvider.copyFile(ecatsSettop, fileToCopy, remoteLocation);
	    } else {
		LOGGER.error("Cannot copy file because DeviceConnectionProvider is not configured");
	    }

	} catch (Exception e) {
	    LOGGER.error("Error while copying configuration files to client : " + e.getMessage());
	}
	return status;
    }

    /**
     * @param dut
     *            Dut Obj
     * @param seleniumhomePath
     *            Selenium Path in clients
     * @param fileName
     *            File name to be editted and placed with correct commands to start selenium service
     * @param tapApi
     *            tapObj
     * @return
     */
    private static boolean editScript(Dut dut, String seleniumhomePath, String fileName, AutomaticsTapApi tapApi,
	    Browser name) {
	boolean returnStatus = false;
	StringBuilder commandToExecute = new StringBuilder();
	commandToExecute
		.append(SED_COMMAND_REPLACE
			.replace("<valueToReplace>", "<chromedriver>")
			.replace(
				"<replacement>",
				seleniumhomePath
					+ AutomaticsPropertyUtility.getProperty(
						SELENIUM_CONFIG_CHROME_DRIVER_FILE_NAME, seleniumhomePath
							+ "chromedriver.exe"))
			.replace("<filename>", seleniumhomePath + fileName))
		.append(AutomaticsConstants.SEMICOLON)
		.append(SED_COMMAND_REPLACE
			.replace("<valueToReplace>", "<geckodriver>")
			.replace(
				"<replacement>",
				seleniumhomePath
					+ AutomaticsPropertyUtility.getProperty(
						SELENIUM_CONFIG_FIREFOX_DRIVER_FILE_NAME, seleniumhomePath
							+ "geckodriver.exe"))
			.replace("<filename>", seleniumhomePath + fileName))
		.append(AutomaticsConstants.SEMICOLON)
		.append(SED_COMMAND_REPLACE
			.replace("<valueToReplace>", "<edgedriver>")
			.replace(
				"<replacement>",
				seleniumhomePath
					+ AutomaticsPropertyUtility.getProperty(SELENIUM_CONFIG_EDGE_DRIVER_FILE_NAME,
						seleniumhomePath + "C://Windows//System32//MicrosoftWebdriver.exe"))
			.replace("<filename>", seleniumhomePath + fileName))
		.append(AutomaticsConstants.SEMICOLON)
		.append(SED_COMMAND_REPLACE
			.replace("<valueToReplace>", "<seleniumjar>")
			.replace(
				"<replacement>",
				seleniumhomePath
					+ AutomaticsPropertyUtility.getProperty(
						SELENIUM_CONFIG_WIN_LIUNX_OS_JAR_FILE_NAME, seleniumhomePath
							+ "selenium-server-standalone-3.11.0.jar"))
			.replace("<filename>", seleniumhomePath + fileName));
	tapApi.executeCommandOnOneIPClients(dut, commandToExecute.toString());
	commandToExecute.setLength(0);
	if (((Device) dut).isWindows()) {
	    returnStatus = checkForRequiredFilesInWindows(dut, tapApi, seleniumhomePath, name);
	    grantFullFilePermission(tapApi, (Device) dut, seleniumhomePath);
	} else if (((Device) dut).isRaspbianLinux() || ((Device) dut).isLinux()) {
	    commandToExecute.append(LinuxCommandConstants.CMD_LIST_FOLDER_FILES).append(AutomaticsConstants.SPACE)
		    .append(seleniumhomePath);
	    String response = tapApi.executeCommandOnOneIPClients(dut, commandToExecute.toString());
	    returnStatus = CommonMethods.isNotNull(response)
		    && CommonMethods.validateTextUsingRegularExpression(response,
			    AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_WIN_LIUNX_OS_JAR_FILE_NAME))
		    && CommonMethods.validateTextUsingRegularExpression(response,
			    AutomaticsTapApi.getSTBPropsValue(SELENIUM_CONFIG_LINUX_OS_SH_FILE_NAME));
	}
	return returnStatus;
    }
}
