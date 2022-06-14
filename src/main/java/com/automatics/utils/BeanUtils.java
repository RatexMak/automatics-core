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
package com.automatics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.automatics.device.Device;
import com.automatics.providers.CodeDownloadProvider;
import com.automatics.providers.DeviceAccessValidator;
import com.automatics.providers.RdkVideoDeviceProvider;
import com.automatics.providers.TestInitilizationProvider;
import com.automatics.providers.appender.BuildAppenderProvider;
import com.automatics.providers.avanlyzer.AVAnalysisProvider;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.SerialCommandExecutionProvider;
import com.automatics.providers.connection.auth.ICrypto;
import com.automatics.providers.crashanalysis.CrashAnalysisProvider;
import com.automatics.providers.imageupgrade.ImageUpgradeProviderFactory;
import com.automatics.providers.issuemanagement.IssueManagementProvider;
import com.automatics.providers.logupload.DeviceLogUploadProvider;
import com.automatics.providers.ocr.OcrServiceProvider;
import com.automatics.providers.rack.ImageCompareProvider;
import com.automatics.providers.rack.OcrProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RemoteProviderFactory;
import com.automatics.providers.rack.VideoProvider;
import com.automatics.providers.snmp.SnmpDataProvider;
import com.automatics.providers.snmp.SnmpProviderFactory;
import com.automatics.providers.tr69.TR69Provider;
import com.automatics.providers.trace.TraceProvider;
import com.automatics.providers.webpa.WebpaProvider;
import com.automatics.providers.xconf.XConfDataProvider;

/**
 * 
 * Utility class for initiating beans
 *
 */
public class BeanUtils {

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

    private static ClassPathXmlApplicationContext partnerContext = null;

    private static ClassPathXmlApplicationContext coreContext = null;

    /**
     * Gets the provider implementation object via spring xml bean configuration
     * 
     * @param beanName
     *            bean name in spring xml file
     * @param path
     *            clazz Class that is loading via spring
     * @param configFileName
     *            spring xml config file name
     * @return Bean object, if no bean definition in xml file, then null will be returned
     */
    public static Object getProviderImplFromConfigFile(String beanName, Class clazz, String configFileName) {
	Object bean = null;

	try {
	    LOGGER.info("Reading bean '{}' from {}", beanName, configFileName);
	    if (BeanConstants.CORE_SPRING_CONFIG_FILE_NAME.equals(configFileName)) {
		bean = coreContext.getBean(beanName, clazz);
	    } else {
		bean = partnerContext.getBean(beanName, clazz);
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception while loading bean {} from {}", beanName, configFileName, e);
	}
	return bean;
    }

    public static void startContext() {
	if (null == partnerContext) {
	    try {
		LOGGER.info("Initializing application context {}", BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
		partnerContext = new ClassPathXmlApplicationContext(BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    } catch (Exception e) {
		LOGGER.info("Could not parse partner {} : {}", BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME,
			e.getMessage());
	    }
	}

	if (null == coreContext) {
	    LOGGER.info("Initializing application context {}", BeanConstants.CORE_SPRING_CONFIG_FILE_NAME);
	    coreContext = new ClassPathXmlApplicationContext(BeanConstants.CORE_SPRING_CONFIG_FILE_NAME);
	}
    }

    public static void closeContext() {
	if (null != partnerContext) {
	    LOGGER.info("Closing partner application context");
	    partnerContext.close();
	}

	if (null != coreContext) {
	    LOGGER.info("Closing core application context");
	    coreContext.close();
	}
    }

    /**
     * Gets the provider implementation object via spring xml bean configuration
     * 
     * @param beanName
     *            bean name in spring xml file
     * @param path
     *            clazz Class that is loading via spring
     * @param configFileName
     *            spring xml config file name
     * @return Bean object, if no bean definition in xml file, then null will be returned
     */
    public static Object getProviderImpl(String beanName, Class clazz, String configFileName) {

	if (BeanConstants.CORE_SPRING_CONFIG_FILE_NAME.equals(configFileName)) {
	    return coreContext.getBean(beanName, clazz);
	} else {
	    return partnerContext.getBean(beanName, clazz);
	}
    }

    /**
     * Gets the provider implementation object via spring xml bean configuration
     * 
     * @param beanName
     *            bean name in spring xml file
     * @param path
     *            clazz Class that is loading via spring
     * @param configFileName
     *            spring xml config file name
     * @return Bean object, if no bean definition in xml file, then null will be returned
     */
    public static Object getPartnerProviderImpl(String stbPropsName, String beanName, Class clazz) {

	String configFileName = BeanConstants.CORE_SPRING_CONFIG_FILE_NAME;
	boolean isPartnerImplReq = Boolean.parseBoolean(AutomaticsPropertyUtility.getProperty(stbPropsName, "false"));

	if (isPartnerImplReq) {
	    configFileName = BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME;
	    LOGGER.info("Reading implementation from partner for {}", beanName);
	} else {
	    LOGGER.info("Reading implementation from core for {}", beanName);
	}

	if (BeanConstants.CORE_SPRING_CONFIG_FILE_NAME.equals(configFileName)) {
	    return coreContext.getBean(beanName, clazz);
	} else {
	    return partnerContext.getBean(beanName, clazz);
	}

    }
    
    /**
     * Gets the provider implementation object via spring xml bean configuration
     * 
     * @param beanName
     *            bean name in spring xml file
     * @param path
     *            clazz Class that is loading via spring
     * @param configFileName
     *            spring xml config file name
     * @return Bean object, if no bean definition in xml file, then null will be returned
     */
    public static Object getPartnerProviderImplasDefault(String stbPropsName, String beanName, Class clazz) {

	String configFileName = BeanConstants.CORE_SPRING_CONFIG_FILE_NAME;
	boolean isPartnerImplReq = Boolean.parseBoolean(AutomaticsPropertyUtility.getProperty(stbPropsName, "true"));

	if (isPartnerImplReq) {
	    configFileName = BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME;
	    LOGGER.info("Reading implementation from partner for {}", beanName);
	} else {
	    LOGGER.info("Reading implementation from core for {}", beanName);
	}

	if (BeanConstants.CORE_SPRING_CONFIG_FILE_NAME.equals(configFileName)) {
	    return coreContext.getBean(beanName, clazz);
	} else {
	    return partnerContext.getBean(beanName, clazz);
	}

    }

    /**
     * Gets DeviceAccessValidator instance
     * 
     * @return DeviceAccessValidator instance
     */
    public static DeviceAccessValidator getDeviceAccessValidator() {
	DeviceAccessValidator provider = null;

	// Get the device access validator bean from partner.
	try {
	    provider = (DeviceAccessValidator) getProviderImpl(BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR,
		    DeviceAccessValidator.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info(
		    "Bean {} is not configured in partner : {}. Hence reading default implementation of DeviceAccessValidator.",
		    BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    try {
		provider = (DeviceAccessValidator) getProviderImpl(BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR,
			DeviceAccessValidator.class, BeanConstants.CORE_SPRING_CONFIG_FILE_NAME);
	    } catch (Exception e1) {
		LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_DEVICE_ACCESS_VALIDATOR);
	    }
	}
	return provider;
    }

    /**
     * Gets RdkVideoDeviceProvider instance
     * 
     * @return RdkVideoDeviceProvider instance
     */
    public static RdkVideoDeviceProvider getRdkVideoDeviceProvider() {
	RdkVideoDeviceProvider provider = null;
	try {
	    provider = (RdkVideoDeviceProvider) getProviderImpl(BeanConstants.BEAN_ID_VIDEO_DEVICE_PROVIDER,
		    RdkVideoDeviceProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_VIDEO_DEVICE_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets CodeDownloadProvider instance
     * 
     * @return CodeDownloadProvider instance
     */
    public static CodeDownloadProvider getCodeDownloadProvider() {
	CodeDownloadProvider provider = null;
	try {
	    provider = (CodeDownloadProvider) getProviderImpl(BeanConstants.BEAN_ID_CODE_DOWNLOAD_PROVIDER,
		    CodeDownloadProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_CODE_DOWNLOAD_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets BuildAppenderProvider instance
     * 
     * @return BuildAppenderProvider instance
     */
    public static BuildAppenderProvider getBuildAppenderProvider() {
	BuildAppenderProvider provider = null;
	try {
	    provider = (BuildAppenderProvider) getProviderImpl(BeanConstants.BEAN_ID_BUILD_APPENDER_PROVIDER,
		    BuildAppenderProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_BUILD_APPENDER_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets TR69Provider instance
     * 
     * @return TR69Provider instance
     */
    public static TR69Provider getTR69Provider() {
	TR69Provider provider = null;
	try {
	    provider = (TR69Provider) getProviderImpl(BeanConstants.BEAN_ID_TR69_PROVIDER, TR69Provider.class,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_TR69_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets device connection provider instance
     * 
     * @return DeviceConnectionProvider instance
     */
    public static DeviceConnectionProvider getDeviceConnetionProvider() {
	DeviceConnectionProvider provider = null;
	try {
	    provider = (DeviceConnectionProvider) getPartnerProviderImplasDefault(BeanConstants.PROP_KEY_DEVICE_CONNECTION_PROVIDER,
		    BeanConstants.BEAN_ID_DEVICE_CONNECTION_PROVIDER,DeviceConnectionProvider.class);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_DEVICE_CONNECTION_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets serialcommand execution provider instance
     * 
     * @return SerialCommandExecutionProvider instance
     */
    public static SerialCommandExecutionProvider getSerialCommandExecutionProvider() {

	SerialCommandExecutionProvider provider = null;
	try {
	    provider = (SerialCommandExecutionProvider) getProviderImpl(
		    BeanConstants.BEAN_ID_SERIAL_COMMAND_EXECUTION_PROVIDER, SerialCommandExecutionProvider.class,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_SERIAL_COMMAND_EXECUTION_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets TestInitilizationProvider instance
     * 
     * @return TestInitilizationProvider instance
     */
    public static TestInitilizationProvider getTestInitializationProvider() {
	TestInitilizationProvider provider = null;
	try {
	    provider = (TestInitilizationProvider) getProviderImpl(BeanConstants.BEAN_ID_TEST_INITIALIZER,
		    TestInitilizationProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_TEST_INITIALIZER);

	}
	return provider;
    }

    /**
     * Gets ImageUpgradeProviderFactory instance
     * 
     * @return ImageUpgradeProviderFactory instance
     */
    public static ImageUpgradeProviderFactory getImageUpgradeProviderFactory() {
	ImageUpgradeProviderFactory provider = null;
	try {
	    provider = (ImageUpgradeProviderFactory) getProviderImpl(BeanConstants.BEAN_ID_IMAGE_UPGRADE_PROVIDER,
		    ImageUpgradeProviderFactory.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_IMAGE_UPGRADE_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets CrashAnalysis provider instance
     * 
     * @return CrashAnalysis provider instance
     */
    public static CrashAnalysisProvider getCrashAnalysisProvider() {
	CrashAnalysisProvider provider = null;
	try {
	    provider = (CrashAnalysisProvider) getProviderImpl(BeanConstants.BEAN_ID_CRASH_ANALYSIS_PROVIDER,
		    CrashAnalysisProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_CRASH_ANALYSIS_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets SerialTraceProvider instance based on serial connection provider
     * 
     * @return TraceProvider provider instance
     */
    public static TraceProvider getTraceProviderInstance(Device device) {
	TraceProvider traceProvider = null;
	try {
	    traceProvider = (TraceProvider) getProviderImpl(BeanConstants.BEAN_ID_SERIAL_TRACE_PROVIDER,
		    TraceProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    if (null != traceProvider) {
		traceProvider.initializeTraceForDevice(device);
	    }
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_SERIAL_TRACE_PROVIDER);

	}
	return traceProvider;
    }

    /**
     * Gets TraceProvider instance based on device connection provider
     * 
     * @return TraceProvider provider instance
     */
    public static TraceProvider getDeviceConnectionTraceProviderInstance(Device device) {
	TraceProvider traceProvider = null;
	try {
	    traceProvider = (TraceProvider) getProviderImpl(BeanConstants.BEAN_ID_DEVICE_CONNECTION_TRACE_PROVIDER,
		    TraceProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    if (null != traceProvider) {
		traceProvider.initializeTraceForDevice(device);
	    }
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_DEVICE_CONNECTION_TRACE_PROVIDER);

	}
	return traceProvider;
    }

    /**
     * Gets the provider implementation object via spring xml bean configuration
     * 
     * @return Bean object, if no bean definition in xml file, then null will be returned
     */
    public static SnmpProviderFactory getSnmpFactoryProvider() {
	SnmpProviderFactory snmpProviderFactory = null;
	try {
	    snmpProviderFactory = (SnmpProviderFactory) getPartnerProviderImpl(
		    BeanConstants.PROP_KEY_SNMP_FACTORY_PROVIDER, BeanConstants.BEAN_ID_SNMP_PROVIDER_FACTORY,
		    SnmpProviderFactory.class);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_SNMP_PROVIDER_FACTORY);
	}
	return snmpProviderFactory;
    }

    /**
     * Gets SnmpDataProvider instance
     * 
     * @return SnmpDataProvider instance
     */
    public static SnmpDataProvider getSnmpDataProvider() {
	SnmpDataProvider dataProvider = null;
	try {
	    dataProvider = (SnmpDataProvider) getProviderImpl(BeanConstants.BEAN_ID_SNMP_DATA_PROVIDER,
		    SnmpDataProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_SNMP_DATA_PROVIDER);

	}
	return dataProvider;
    }

    /**
     * Gets CredentialCrypto instance
     * 
     * @return CredentialCrypto instance
     */
    public static ICrypto getCredentialCrypto() {
	ICrypto provider = null;
	try {
	    provider = (ICrypto) getProviderImpl(BeanConstants.BEAN_ID_CREDENTIAL_CRYPTO_PROVIDER, ICrypto.class,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_CREDENTIAL_CRYPTO_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets WebpaProvider instance
     * 
     * @return WebpaProvider instance
     */
    public static WebpaProvider getWebpaProvider() {
	WebpaProvider provider = null;
	try {
	    LOGGER.info("Reading {} from partner {}", BeanConstants.BEAN_ID_WEBPA_PROVIDER,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    provider = (WebpaProvider) getProviderImpl(BeanConstants.BEAN_ID_WEBPA_PROVIDER, WebpaProvider.class,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Since {} not configured in partner, reading default implementation in Automatics.",
		    BeanConstants.BEAN_ID_WEBPA_PROVIDER);
	    try {
		provider = (WebpaProvider) getProviderImpl(BeanConstants.BEAN_ID_WEBPA_PROVIDER, WebpaProvider.class,
			BeanConstants.CORE_SPRING_CONFIG_FILE_NAME);
	    } catch (Exception e1) {
		LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_WEBPA_PROVIDER);

	    }
	}
	return provider;
    }

    /**
     * Gets XConfDataProvider instance
     * 
     * @return XConfDataProvider instance
     */
    public static XConfDataProvider getXConfDataProvider() {
	XConfDataProvider provider = null;
	try {
	    LOGGER.info("Reading {} from partner {}", BeanConstants.BEAN_ID_XCONF_DATA_PROVIDER,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	    provider = (XConfDataProvider) getProviderImpl(BeanConstants.BEAN_ID_XCONF_DATA_PROVIDER,
		    XConfDataProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Since {} not configured in partner, reading default implementation in Automatics.",
		    BeanConstants.BEAN_ID_XCONF_DATA_PROVIDER);
	    try {
		provider = (XConfDataProvider) getProviderImpl(BeanConstants.BEAN_ID_XCONF_DATA_PROVIDER,
			XConfDataProvider.class, BeanConstants.CORE_SPRING_CONFIG_FILE_NAME);
	    } catch (Exception e1) {
		LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_XCONF_DATA_PROVIDER);

	    }
	}
	return provider;
    }

    /**
     * Gets PowerProvider
     * 
     * @return PowerProvider instance
     */
    public static PowerProvider getPowerProvider() {
	PowerProvider provider = null;
	try {
	    provider = (PowerProvider) getPartnerProviderImpl(BeanConstants.PROP_KEY_POWER_PROVIDER,
		    BeanConstants.BEAN_ID_POWER_PROVIDER, PowerProvider.class);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_POWER_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets VideoProvider
     * 
     * @return VideoProvider instance
     */
    public static VideoProvider getVideoProvider() {
	VideoProvider provider = null;
	try {
	    provider = (VideoProvider) getProviderImpl(BeanConstants.BEAN_ID_VIDEO_PROVIDER, VideoProvider.class,
		    BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_VIDEO_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets ImageUpgradeProviderFactory instance
     * 
     * @return ImageUpgradeProviderFactory instance
     */
    public static RemoteProviderFactory getRemoteProviderFactory() {
	RemoteProviderFactory provider = null;
	try {
	    provider = (RemoteProviderFactory) getProviderImpl(BeanConstants.BEAN_ID_REMOTE_PROVIDER_FACTORY,
		    RemoteProviderFactory.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_REMOTE_PROVIDER_FACTORY);

	}
	return provider;
    }

    /**
     * Gets OcrProvider
     * 
     * @return OcrProvider instance
     */
    public static OcrProvider getOcrProvider() {
	OcrProvider ocrProvider = null;
	try {
	    ocrProvider = (OcrProvider) getPartnerProviderImpl(BeanConstants.PROP_KEY_OCR_PROVIDER,
		    BeanConstants.BEAN_ID_OCR_PROVIDER, OcrProvider.class);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_OCR_PROVIDER);

	}
	return ocrProvider;
    }

    /**
     * Gets OcrServiceProvider
     * 
     * @return OcrServiceProvider instance
     */
    public static OcrServiceProvider getOcrServiceProvider() {
	OcrServiceProvider provider = null;
	try {
	    provider = (OcrServiceProvider) getProviderImpl(BeanConstants.BEAN_ID_OCR_SERVICE_PROVIDER,
		    OcrServiceProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_OCR_SERVICE_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets ImageCompareProvider
     * 
     * @return ImageCompareProvider instance
     */
    public static ImageCompareProvider getImageCompareProvider() {
	LOGGER.info("Loading IC class");
	ImageCompareProvider provider = null;
	try {
	    provider = (ImageCompareProvider) getProviderImpl(BeanConstants.BEAN_ID_IMAGE_COMPARE_PROVIDER,
		    ImageCompareProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_IMAGE_COMPARE_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets IssueManagementProvider instance
     * 
     * @return IssueManagementProvider instance
     */
    public static IssueManagementProvider getIssueManagementProvider() {
	IssueManagementProvider provider = null;
	try {
	    provider = (IssueManagementProvider) getProviderImpl(BeanConstants.BEAN_ID_ISSUE_MANAGEMENT_PROVIDER,
		    IssueManagementProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_ISSUE_MANAGEMENT_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets AV AnalysisProvider
     * 
     * @return AnalysisProvider instance
     */
    public static AVAnalysisProvider getAVAnalysisProvider() {
	AVAnalysisProvider provider = null;
	try {
	    provider = (AVAnalysisProvider) getPartnerProviderImpl(BeanConstants.PROP_KEY_AV_ANALYSIS_PROVIDER,
		    BeanConstants.BEAN_ID_AV_ANALYSIS_PROVIDER, AVAnalysisProvider.class);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_AV_ANALYSIS_PROVIDER);

	}
	return provider;
    }

    /**
     * Gets DeviceLogUploadProvider
     * 
     * @return DeviceLogUploadProvider instance
     */
    public static DeviceLogUploadProvider getDeviceLogUploadProvider() {
	DeviceLogUploadProvider provider = null;
	try {
	    provider = (DeviceLogUploadProvider) getProviderImpl(BeanConstants.BEAN_ID_DEVICE_LOG_UPLOAD_PROVIDER,
		    DeviceLogUploadProvider.class, BeanConstants.PARTNER_SPRING_CONFIG_FILE_NAME);
	} catch (Exception e) {
	    LOGGER.info("Bean {} is not configured.", BeanConstants.BEAN_ID_DEVICE_LOG_UPLOAD_PROVIDER);

	}
	return provider;
    }

}
