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
package com.automatics.enums;

/**
 * Enum to map STB Process
 * 
 * @author styles mangalasseri, TATA Elxsi
 * 
 */
public enum StbProcess {

    /** nxserver process for RDKV */
    NXSERVER_RDKV(
	    "nxserver",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_nxserver.*\\.core\\.tgz)"),

    /** netsrvmgr process for RDKV */
    NETSRVMGR_RDKV(
	    "netsrvmgr",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_netsrvmgr.*\\.core\\.tgz)"),

    /** webpavideo process */
    WEBPA_VIDEO("webpavideo"),

    /** authservice process for RDKV */
    AUTHSERVICE_RDKV(
	    "authservice",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_authservice.*\\.core\\.tgz)"),

    /** fogcli process for RDKV */
    FOGCLI_RDKV(
	    "fogcli",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_fogcli.*\\.core\\.tgz)"),

    /** xdiscovery process for RDKV */
    XDISCOVERY_RDKV(
	    "xdiscovery",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_xdiscovery.*\\.core\\.tgz)"),

    /** pwrMgrMain process for RDKV */
    PWR_MGR_MAIN_RDKV(
	    "pwrMgrMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_pwrMgrMain.*\\.core\\.tgz)"),

    /** mfrMgrMain process for RDKV */
    MFR_MGR_MAIN_RDKV(
	    "mfrMgrMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_mfrMgrMain.*\\.core\\.tgz)"),

    /** tr69BusMain process for RDKV */
    TR69_BUS_MAIN_RDKV(
	    "tr69BusMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_tr69BusMain.*\\.core\\.tgz)"),

    /** sysMgrMain process for RDKV */
    SYS_MGR_MAIN_RDKV(
	    "sysMgrMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_sysMgrMain.*\\.core\\.tgz)"),

    /** irMgrMain process for RDKV */
    IR_MGR_MAIN_RDKV(
	    "irMgrMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_irMgrMain.*\\.core\\.tgz)"),

    /** CecDaemonMain process for RDKV */
    CEC_DAEMON_MAIN_RDKV(
	    "CecDaemonMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\w+_mod\\w+.*_core.prog_CecDaemonMain.*\\.core\\.tgz)"),

    /** IARMDaemonMain process for RDKV */
    IARM_DAEMON_MAIN_RDKV(
	    "IARMDaemonMain",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_IARMDaemonMain.*\\.core\\.tgz)"),

    /** controlMgr process for RDKV */
    CONTROL_MGR(
	    "controlMgr",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_IARMDaemonMain.*\\.core\\.tgz)"),

    /** rmfStreamer process */
    RMF_STREAMER("rmfStreamer"),

    /** PandM process */
    PANDM("CcspPandMSsp"),

    /** CcspCommon library process */
    CCSP_COMMON_LIBRARY("CcspCommon library"),

    /** Utopia process */
    UTOPIA("gw_prov_utopia"),

    /** CcspTr069 process */
    CCSP_TR069("CcspTr069PaSsp"),

    /** CcspPsm process */
    CCSP_PSM("PsmSsp"),

    /** CcspCr process */
    CCSP_CR("CcspCrSsp", CrashFileGenerationDelay.HIGH),

    /** webpa process */
    WEBPA("webpa"),

    /** CcspWifiAgent process */
    CCSP_WIFI_AGENT("CcspWifiSsp"),

    /** CcspWecbController process */
    CCSP_WECB_CONTROLLER("CcspWecbController"),

    /** xsmart process */
    XSMART("xsmartd"),

    /** MeshAgent process */
    MESH_AGENT("meshAgent"),

    /** bluez process */
    BLUEZ("bluetoothd"),

    /** btmgr process */
    BTMGR("btMgrBus"),

    /** btrleappmgr process */
    BTRLEAPPMGR("btrLeAppMgr"),

    /** rf4ceMgr process */
    RF4CE_MGR("rf4ceMgr", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_RF4CE.*\\.core\\.tgz)"),

    /** IARMDaemonMain process */
    IARM_DAEMON_MAIN("IARMDaemonMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_IARMDaemonMain.*\\.core\\.tgz)"),

    /** CecDaemonMain process */
    CEC_DAEMON_MAIN("CecDaemonMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_CecDaemonMain.*\\.core\\.tgz)"),

    /** irMgrMain process */
    IR_MGR_MAIN("irMgrMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_irMgrMain.*\\.core\\.tgz)"),

    /** sysMgrMain process */
    SYS_MGR_MAIN("sysMgrMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_sysMgrMain.*\\.core\\.tgz)"),

    /** tr69BusMain process */
    TR69_BUS_MAIN("tr69BusMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_tr69BusMain.*\\.core\\.tgz)"),

    /** mfrMgrMain process */
    MFR_MGR_MAIN("mfrMgrMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_mfrMgrMain.*\\.core\\.tgz)"),

    /** pwrMgrMain process */
    PWR_MGR_MAIN("pwrMgrMain", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_pwrMgrMain.*\\.core\\.tgz)"),

    /** xcal-device process */
    XCAL_DEVICE("xcal-device", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_xcal-device.*\\.core\\.tgz)"),

    /** xdiscovery process */
    XDISCOVERY("xdiscovery", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_xdiscovery.*\\.core\\.tgz)"),

    /** fogcli process */
    FOGCLI("fogcli", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_fogcli.*\\.core\\.tgz)"),

    /** authservice process */
    AUTHSERVICE("authservice", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_authservice.*\\.core\\.tgz)"),

    /** nxserver process */
    NXSERVER("nxserver", "\\d+\\s+\\d+:\\d+\\s+(.*_core.prog_authservice.*\\.core\\.tgz)"),

    /** Parodus process */
    PARODUS("parodus"),

    /** Storage Manager Main process name */
    STORAGE_MGR_MAIN("storageMgrMain"),

    /** sys mg main process name */
    SYS_MGR("sysMgrMain"),

    /** ds Mgr Main process name */
    DS_MGR_MAIN("dsMgrMain"),

    /** irMgrMain process name */
    IR_MGRMAIN("irMgrMain"),

    /** mgrMgrMain process name */
    MFR_MGRMAIN("mfrMgrMain"),

    /** WPEFramework process name */
    WPE_FRAMEWORK("WPEFramework"),

    /** nrdpluginApp process name */
    NRD_PLUGINAPP("nrdPluginApp"),

    /** tr69hostif process name */
    TR69_HOSTIF("tr69hostif"),

    /** lightpd process name */
    LIGHTTPD_PROCESS(
	    "lighttpd",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\w+_mod\\w+.*_core.prog_lighttpd.*\\.core\\.tgz)"),

    /** tail process name */
    TAIL(
	    "tail",
	    "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*_core.prog_tail.*\\.core\\.tgz)"),

    /** RDKC PROCESSES **/
    XFINITY_POLLING_CONFIG("xfinity_polling_config"),
    UPNPD("upnpd"),
    METRICS("metrics"),
    DROPBEAR("dropbear"),
    NTPD("ntpd"),
    DSPDAE("dsp_dae"),
    THUMBNAILUPLOAD("thumbnail_upload"),
    CVR_DAEMON("cvr_daemon"),
    IVA_DAEMON("iva_daemon"),
    LIGHTTPD("lighttpd"),
    XVISIOND("xvisiond"),
    HYDRA("hydra"),
    XCONFIGD("xconfigd"),
    SENSOR_DAEMONS("sensor_daemon"),
    RTROUTED("rtrouted"),
    NTPCLIENT("ntpclient"),
    CVR_DAEMONGST("cvr_daemongst"),
    PROVPOLLD("provpolld"),
    SMARTTHUMBNAIL("smartthumbnail"),
    CVR_DAEMON_KVS("cvr_daemon_kvs"),
    XVISIONDGST("xvisiondgst"),
    CAM_STREAM_SERVER("cam_stream_server"),
    XCVAAD("xcvaad"),
    SMARTRC("smartrc"),
    /** BLE process */
    BLEPROCESS("bleconfd"),
    /** CcspHotspot process */
    CCSP_HOTSPOT("CcspHotspot"),
    /** snmp_subagent process */
    SNMP_SUBAGENT("snmp_subagent"),
    /** CcspEPONAgentSsp process */
    CCSP_EPONAGENT("CcspEPONAgentSsp"),
    /** trigger process */
    CCSP_TRIGGER("trigger"),
    /** rdkbPowerMgr process */
    CCSP_RDKBPWRMGR("rdkbPowerMgr"),
    /** IGD process */
    CCSP_IGD("IGD"),
    /** psmcli process */
    CCSP_PSMCLI("psmcli"),
    /** dmcli process */
    CCSP_DMCLI("dmcli"),
    /** dobby daemon */
    DOBBY_DAEMON("DobbyDaemon"),
    /** Audio capture manager **/
    AUDIO_CAPTURE_MGR("audiocapturemgr"),
    /** nlmon **/
    NLMON("nlmon");

    private static final String DEFUALT_REGEX_FOR_MINI_DUMP_FILE = "\\s*((\\w+_)?mac\\w+_dat\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_box\\d+-\\d+-\\d+-\\d+-\\d+-\\d+_mod\\w+.*dmp.tgz)";

    /** Process name */
    private String processName;

    /** core dump file format */
    private String regexForCoreDumpFileFormat;

    /** mini dump file format */
    private String regexForMiniDumpFileFormat;

    /** crash file generation delay */
    private CrashFileGenerationDelay crashFileGenerationDelay;

    /**
     * Default values
     * 
     * regexForMiniDumpFileFormat - \d+\s+\d+:\d+\s+(.*\.dmp\.tgz)
     * 
     * crashFileGenerationDelay - LOW
     * 
     * @param processName
     *            process name
     */
    private StbProcess(String processName) {
	this.processName = processName;
	this.regexForMiniDumpFileFormat = DEFUALT_REGEX_FOR_MINI_DUMP_FILE;
	// TODO
	// add default core dump file format
	this.crashFileGenerationDelay = CrashFileGenerationDelay.LOW;
    }

    private StbProcess(String processName, String coreDumpFileFormat) {
	this.processName = processName;
	this.regexForCoreDumpFileFormat = coreDumpFileFormat;
	this.regexForMiniDumpFileFormat = DEFUALT_REGEX_FOR_MINI_DUMP_FILE;
	this.crashFileGenerationDelay = CrashFileGenerationDelay.LOW;
    }

    private StbProcess(String processName, CrashFileGenerationDelay crashFileGenerationDelay) {
	this.processName = processName;
	// TODO
	// add default core dump file format
	this.regexForMiniDumpFileFormat = DEFUALT_REGEX_FOR_MINI_DUMP_FILE;
	this.crashFileGenerationDelay = crashFileGenerationDelay;
    }

    private StbProcess(String processName, String coreDumpFileFormat,
	    CrashFileGenerationDelay crashFileGenerationDelay) {
	this.processName = processName;
	this.regexForCoreDumpFileFormat = coreDumpFileFormat;
	this.regexForMiniDumpFileFormat = DEFUALT_REGEX_FOR_MINI_DUMP_FILE;
	this.crashFileGenerationDelay = crashFileGenerationDelay;
    }

    private StbProcess(String processName, String coreDumpFileFormat, String miniDumpFileFormat,
	    CrashFileGenerationDelay crashFileGenerationDelay) {
	this.processName = processName;
	this.regexForCoreDumpFileFormat = coreDumpFileFormat;
	this.regexForMiniDumpFileFormat = miniDumpFileFormat;
	this.crashFileGenerationDelay = crashFileGenerationDelay;
    }

    @Override
    public String toString() {
	return this.processName;
    }

    public String getProcessName() {
	return processName;
    }

    public String getRegexForCoreDumpFileFormat() {
	return regexForCoreDumpFileFormat;
    }

    public String getRegexForMiniDumpFileFormat() {
	return regexForMiniDumpFileFormat;
    }

    public CrashFileGenerationDelay getCrashFileGenerationDelay() {
	return crashFileGenerationDelay;
    }

}
