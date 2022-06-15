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
package com.automatics.selenium;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Device;
import com.automatics.enums.Browser;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.FrameworkHelperUtils;

/**
 * 
 * This class is used to set properties of browsers to perform cross browser testing of web applications. It stores the
 * capabilities as key-value pairs and these capabilities are used to set browser properties like browser name, browser
 * version, path of browser driver in the system, etc. to determine the behaviour of browser at run time.
 * 
 * @author Shameem M Shereef
 *
 */
public class CustomizableBrowserCapabilities extends DesiredCapabilities {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String BROWSER_CAPABILITY_NO_SANDBOX = "--no-sandbox";
    private static final String BROWSER_CAPABILITY_HEADLESS = "headless";
    private static final String BROWSER_CAPABILITY_MARIONETTE = "marionette";

    public CustomizableBrowserCapabilities(Browser browser, Platform platform, Device device) {
	super(browser.getValue(), "", platform);

	// Setting default capabilities here
	switch (browser) {
	case CHROME:
	    ChromeOptions chromeOptions = new ChromeOptions();
	    if (platform.is(Platform.WINDOWS)) {
		chromeOptions.addArguments(
			AutomaticsConstants.HYPHEN + AutomaticsConstants.HYPHEN + BROWSER_CAPABILITY_HEADLESS);
		chromeOptions.addArguments(BROWSER_CAPABILITY_NO_SANDBOX);
	    }
	    this.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
	    break;
	case FIREFOX:
	    boolean isMarionette = isMarionetteRequiredForBrowserVersion(device);
	    if (platform.is(Platform.WINDOWS)) {
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments(AutomaticsConstants.HYPHEN + BROWSER_CAPABILITY_HEADLESS);
		this.setCapability(BROWSER_CAPABILITY_MARIONETTE, true);
		this.setCapability(ACCEPT_INSECURE_CERTS, false);
		this.merge(firefoxOptions);
	    } else if (platform.is(Platform.LINUX)) {
		this.setCapability(BROWSER_CAPABILITY_HEADLESS, true);
		this.setCapability(BROWSER_CAPABILITY_MARIONETTE, isMarionette);
	    }
	    break;
	case IE:
	    if (platform.is(Platform.WINDOWS)) {
		EdgeOptions edgeOptions = new EdgeOptions();
		this.setCapability(ChromeOptions.CAPABILITY, edgeOptions);
	    }
	    break;
	case SAFARI:
	    if (platform.is(Platform.MAC)) {
		SafariOptions safariOptions = new SafariOptions();
		this.setCapability(ChromeOptions.CAPABILITY, safariOptions);
	    }
	    break;
	default:
	    break;
	}
    }

    private boolean isMarionetteRequiredForBrowserVersion(Device device) {
	String deviceFirefoxVersion = AutomaticsTapApi.getInstance().executeCommandOnOneIPClients(device,
		"firefox --version");
	String xpiSUpportedVersionList = AutomaticsPropertyUtility.getProperty("xpi.supported.firefox.versions",
		"52.9.0");
	boolean isMarionetteRequired = !FrameworkHelperUtils.splitAndCheckSubstring(xpiSUpportedVersionList,
		deviceFirefoxVersion);
	return isMarionetteRequired;
    }

    public CustomizableBrowserCapabilities overrideCapabilities(Map<String, Object> capabilities) {
	if (capabilities != null) {
	    for (String capability : capabilities.keySet()) {
		this.setCapability(capability, capabilities.get(capability));
	    }
	}
	return this;
    }
}
