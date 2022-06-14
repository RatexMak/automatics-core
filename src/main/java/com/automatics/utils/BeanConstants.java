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

public class BeanConstants {

    /** Spring xml config file name for Core **/
    public static final String CORE_SPRING_CONFIG_FILE_NAME = "applicationContext.xml";

    /** Spring xml config file name for Partner **/
    public static final String PARTNER_SPRING_CONFIG_FILE_NAME = "partner-applicationContext.xml";

    /** Bean name for test initializer **/
    public static final String BEAN_ID_TEST_INITIALIZER = "testInitializer";

    /** Bean name for image upgrade provider **/
    public static final String BEAN_ID_IMAGE_UPGRADE_PROVIDER = "imageUpgradeProviderFactory";

    /** Bean name for remote provider factory **/
    public static final String BEAN_ID_REMOTE_PROVIDER_FACTORY = "remoteProviderFactory";

    /** Bean name for device provider **/
    public static final String BEAN_ID_DEVICE_PROVIDER = "deviceProvider";

    /** Bean name for snmp provider factory **/
    public static final String BEAN_ID_SNMP_PROVIDER_FACTORY = "snmpProviderFactory";

    /** Bean name for snmp data provider **/
    public static final String BEAN_ID_SNMP_DATA_PROVIDER = "snmpDataProvider";

    /** Bean name for credential crypto **/
    public static final String BEAN_ID_CREDENTIAL_CRYPTO_PROVIDER = "credentialCryptoProvider";

    /** Bean name for device connection provider **/
    public static final String BEAN_ID_DEVICE_CONNECTION_PROVIDER = "deviceConnectionProvider";

    /** Bean name for webpa provider **/
    public static final String BEAN_ID_WEBPA_PROVIDER = "webpaProvider";
    
    /** Bean name for XConf data provider **/
    public static final String BEAN_ID_XCONF_DATA_PROVIDER = "xconfDataProvider";

    /** Bean name for device connection provider **/
    public static final String BEAN_ID_SERIAL_COMMAND_EXECUTION_PROVIDER = "serialCommandExecutionProvider";

    /** Bean name for serial based trace provider **/
    public static final String BEAN_ID_SERIAL_TRACE_PROVIDER = "serialConnectionBasedTrace";

    /** Bean name for device connection based trace provider **/
    public static final String BEAN_ID_DEVICE_CONNECTION_TRACE_PROVIDER = "deviceConnectionBasedTrace";

    /** Bean name for device access validator **/
    public static final String BEAN_ID_DEVICE_ACCESS_VALIDATOR = "deviceAccessValidator";

    /** Bean name for power provider **/
    public static final String BEAN_ID_POWER_PROVIDER = "powerProvider";

    /** Bean name for video device provider **/
    public static final String BEAN_ID_VIDEO_DEVICE_PROVIDER = "rdkVideoDeviceProvider";

    /** Bean name for code download provider **/
    public static final String BEAN_ID_CODE_DOWNLOAD_PROVIDER = "codeDownloadProvider";

    /** Bean name for build appender provider **/
    public static final String BEAN_ID_BUILD_APPENDER_PROVIDER = "buildAppenderProvider";

    /** Bean name for TR69 provider **/
    public static final String BEAN_ID_TR69_PROVIDER = "tr69Provider";

    /** Bean name for video provider **/
    public static final String BEAN_ID_VIDEO_PROVIDER = "videoProvider";

    /** Bean name for image compare provider **/
    public static final String BEAN_ID_IMAGE_COMPARE_PROVIDER = "imageCompareProvider";

    /** Automatics props name for device management **/
    public static final String PROP_KEY_DEVICE_MANAGER = "partner.impl.deviceManager";

    /** Automatics props name for power provider **/
    public static final String PROP_KEY_POWER_PROVIDER = "partner.impl.powerProvider";

    /** Automatics props name for ocr provider **/
    public static final String PROP_KEY_OCR_PROVIDER = "partner.impl.ocrProvider";

    /** Automatics props name for snmp factory provider **/
    public static final String PROP_KEY_SNMP_FACTORY_PROVIDER = "partner.impl.snmpFactoryProvider";

    /** Automatics props name for av analysis provider **/
    public static final String PROP_KEY_AV_ANALYSIS_PROVIDER = "partner.impl.avAnalysisProvider";

    /** Bean name for crash provider **/
    public static final String BEAN_ID_CRASH_ANALYSIS_PROVIDER = "crashAnalysisProvider";

    /** Bean name for issue management provider **/
    public static final String BEAN_ID_ISSUE_MANAGEMENT_PROVIDER = "issueManagementProvider";

    /** Bean name for ocr provider **/
    public static final String BEAN_ID_OCR_PROVIDER = "ocrProvider";

    /** Bean name for ocr service provider **/
    public static final String BEAN_ID_OCR_SERVICE_PROVIDER = "ocrServiceProvider";

    /** Bean name for AV Analysis provider **/
    public static final String BEAN_ID_AV_ANALYSIS_PROVIDER = "avAnalysisProvider";

    /** Bean name for device log upload provider **/
    public static final String BEAN_ID_DEVICE_LOG_UPLOAD_PROVIDER = "deviceLogUploadProvider";

    /** Bean not initialized/found error message */
    public static final String BEAN_NOT_FOUND_LOG = "{} not configured for bean '{}'";
    
    /** Automatics props name for device connection provider **/
    public static final String PROP_KEY_DEVICE_CONNECTION_PROVIDER = "partner.impl.deviceConnectionProvider";

}
