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
package com.automatics.device;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.RemoteControlType;
import com.automatics.providers.rack.AudioProvider;
import com.automatics.providers.rack.BaseProvider;
import com.automatics.providers.rack.EASProvider;
import com.automatics.providers.rack.ImageCompareProvider;
import com.automatics.providers.rack.MotionDetectionProvider;
import com.automatics.providers.rack.OcrProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RFControlProvider;
import com.automatics.providers.rack.RemoteProvider;
import com.automatics.providers.rack.VideoProvider;
import com.automatics.providers.rack.VideoSelectionProvider;
import com.automatics.providers.rack.exceptions.PowerProviderException;
import com.automatics.providers.rack.exceptions.RFControlProviderException;
import com.automatics.providers.rack.exceptions.SNMPException;
import com.automatics.providers.trace.ConnectionTraceProvider;
import com.automatics.providers.trace.TraceProvider;

/**
 * 
 * Abstract Dut
 *
 */
public class AbstractDut implements Dut {

    protected String id;

    protected String name;

    protected String hostMacAddress;

    protected String make;

    protected String manufacturer;

    protected String model;

    protected String serialNumber;

    protected String unitAddress;

    protected String firmwareVersion;

    protected String hardwareRevision;

    protected String content;

    protected String hostIp4Address;

    protected String hostIp6Address;

    protected String hostIpAddress;

    protected URI audioPath;

    protected URI clickstreamPath;

    protected URI clusterPath;

    protected URI powerPath;

    protected URI remotePath;

    protected URI rfRemotePath;

    protected URI tracePath;

    protected URI videoPath;

    protected URI videoSelectionPath;

    protected String rackName;

    protected String slotName;

    protected String slotNumber;

    protected String serverHost;

    protected Integer serverPort;

    protected URI remoteLocator;

    protected String remoteType;

    protected RemoteControlType defaultRemoteControlType;

    protected boolean locked;

    protected VideoSelectionProvider videoSelection;

    protected String mcardMacAddress;

    protected String mCardSerialNumber;

    protected Object rackObject;

    protected Map<RemoteControlType, RemoteProvider> remotes = new HashMap<RemoteControlType, RemoteProvider>();

    protected Map<String, String> extraProperties = new HashMap<String, String>();

    protected Map<Class<? extends BaseProvider>, BaseProvider> additionalProviders = new HashMap<Class<? extends BaseProvider>, BaseProvider>();

    protected PowerProvider powerProvider;

    protected RemoteProvider remoteProvider;

    protected AudioProvider audioProvider;

    protected TraceProvider traceProvider;

    protected TraceProvider serialTraceProvider;

    protected VideoProvider videoProvider;

    protected ImageCompareProvider imageCompareProvider;

    protected MotionDetectionProvider motionDetectionProvider;

    protected OcrProvider ocrProvider;

    protected RFControlProvider rfControlProvider;

    protected EASProvider easProvider;

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDut.class);

    public AbstractDut() {
    }

    public AbstractDut(String id, String make, String model, String manufacturer, String content, String hostMacAddress,
	    String hostIp4Address, String hostIp6Address, String componentType, String firmwareVersion,
	    String hardwareRevision, String environmentId) {

	this.id = id;
	this.make = make;
	this.manufacturer = manufacturer;
	this.model = model;
	this.content = content;
	this.hostMacAddress = hostMacAddress;
	this.hostIp4Address = hostIp4Address;
	this.hostIp6Address = hostIp6Address;
	this.firmwareVersion = firmwareVersion;
	this.hardwareRevision = hardwareRevision;
    }

    public AbstractDut(RemoteProvider remoteProvider, PowerProvider powerProvider, AudioProvider audioProvider,
	    TraceProvider traceProvider, VideoProvider videoProvider, VideoSelectionProvider videoSelection) {
	this.remoteProvider = remoteProvider;
	this.powerProvider = powerProvider;
	this.audioProvider = audioProvider;
	this.traceProvider = traceProvider;
	this.videoProvider = videoProvider;
	this.videoSelection = videoSelection;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
	return serialNumber;
    }

    /**
     * @param serialNumber
     *            the serialNumber to set
     */
    public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
    }

    /**
     * @return the powerPath
     */
    public URI getPowerPath() {
	return powerPath;
    }

    /**
     * @param powerPath
     *            the powerPath to set
     */
    public void setPowerPath(URI powerPath) {
	this.powerPath = powerPath;
    }

    /**
     * @return the remotePath
     */
    public URI getRemotePath() {
	return remotePath;
    }

    /**
     * @param remotePath
     *            the remotePath to set
     */
    public void setRemotePath(URI remotePath) {
	this.remotePath = remotePath;
    }

    /**
     * @return the rfRemotePath
     */
    public URI getRfRemotePath() {
	return rfRemotePath;
    }

    /**
     * @param rfRemotePath
     *            the rfRemotePath to set
     */
    public void setRfRemotePath(URI rfRemotePath) {
	this.rfRemotePath = rfRemotePath;
    }

    /**
     * Get extra properties for the device
     * 
     * @return Extra properties for the device
     */
    public Map<String, String> getExtraProperties() {
	return this.extraProperties;
    }

    /**
     * Find value for given property
     * 
     * @param key
     * @return property value
     */
    public String findExtraProperty(String key) {
	LOGGER.info("Find value for extra property  : " + key);
	if (this.extraProperties != null) {
	    return this.extraProperties.get(key);
	} else {
	    LOGGER.error("Extra property", key, "is not configured in device object");
	    return null;
	}
    }

    public RemoteProvider getRemote(RemoteControlType type) {
	return ((RemoteProvider) this.remotes.get(type));
    }

    public Collection<RemoteProvider> getRemotes() {
	return this.remotes.values();
    }

    public boolean tune(String channel, boolean isAutoTune, RemoteControlType type) {
	LOGGER.info(" Tune to the channel : " + channel);
	boolean toReturn = this.remoteProvider.tune(channel, AutomaticsConstants.DEFAULT_DELAY, isAutoTune, type);
	LOGGER.info("tune: " + channel + " returned: " + toReturn);
	return toReturn;
    }

    public boolean tune(String channel, Integer delay, boolean isAutoTune, RemoteControlType type) {
	LOGGER.info(" Tune to the channel : " + channel + " with delay: " + delay);
	boolean toReturn = this.remoteProvider.tune(channel, delay, isAutoTune, type);
	LOGGER.info("tune: " + channel + " returned: " + toReturn);
	return toReturn;
    }

    public boolean tune(Integer channel, boolean isAutoTune, RemoteControlType type) {
	LOGGER.info(" Tune to the channel : " + channel);
	boolean toReturn = this.remoteProvider.tune(channel.toString(), AutomaticsConstants.DEFAULT_DELAY, isAutoTune,
		type);
	LOGGER.info("tune: " + channel + " returned: " + toReturn);
	return toReturn;
    }

    public boolean tune(Integer channel, Integer delay, boolean isAutoTune, RemoteControlType type) {
	LOGGER.info(" Tune to the channel : " + channel + " with delay: " + delay);
	boolean toReturn = this.remoteProvider.tune(channel.toString(), delay, isAutoTune, type);
	LOGGER.info("tune: " + channel + " returned: " + toReturn);
	return toReturn;
    }

    public boolean pressKey(String command, RemoteControlType type) {
	boolean toReturn = this.remoteProvider.pressKey(command, type);
	LOGGER.info("pressKey: " + command + " returned: " + toReturn);
	return toReturn;
    }

    public boolean pressKey(String command, Integer delay, RemoteControlType type) {
	boolean toReturn = this.remoteProvider.pressKeyAndHold(command, delay, type);
	LOGGER.info("pressKey: " + command + " with delay: " + delay + " returned: " + toReturn);
	return toReturn;
    }

    public boolean pressKeyAndHold(String command, Integer count, RemoteControlType type) {
	boolean toReturn = this.remoteProvider.pressKeyAndHold(command, count, type);
	LOGGER.info("pressKeyAndHold: " + command + " count: " + count + " returned: " + toReturn);
	return toReturn;
    }

    public boolean pressKeyAndHoldDuration(String command, Integer durationSec, RemoteControlType type) {
	boolean toReturn = this.remoteProvider.pressKeyAndHold(command, durationSec, type);
	LOGGER.info("pressKeyAndHold: " + command + " durationSec: " + durationSec + " returned: " + toReturn);
	return toReturn;
    }

    public void setRemote(RemoteProvider remote) {
	this.remoteProvider = remote;
    }

    public void setRemotes(RemoteProvider remote, RemoteControlType remoteControlType) {
	this.remotes.put(remoteControlType, remote);
    }

    public URI getPowerLocator() {
	return this.powerProvider.getPowerLocator();
    }

    public PowerProvider getPower() {
	return this.powerProvider;
    }

    public void setPower(PowerProvider power) {
	this.powerProvider = power;
    }

    public void powerOn() throws PowerProviderException {
	LOGGER.info("powerOn");
	this.powerProvider.powerOn();
    }

    public void powerOff() throws PowerProviderException {
	LOGGER.info("powerOff");
	this.powerProvider.powerOff();
    }

    public void reboot() throws PowerProviderException {
	LOGGER.info("reboot");
	this.powerProvider.reboot();
    }

    public String getPowerStatus() {
	return this.powerProvider.getPowerStatus();
    }

    public AudioProvider getAudio() {
	return this.audioProvider;
    }

    public void setAudio(AudioProvider audio) {
	this.audioProvider = audio;
    }

    public TraceProvider getTrace() {
	return this.traceProvider;
    }

    public void setTrace(TraceProvider trace) {
	this.traceProvider = trace;
    }

    public VideoProvider getVideo() {
	return this.videoProvider;
    }

    public void setVideo(VideoProvider video) {
	this.videoProvider = video;
    }

    public VideoSelectionProvider getVideoSelection() {
	return this.videoSelection;
    }

    public void setVideoSelection(VideoSelectionProvider videoSelection) {
	this.videoSelection = videoSelection;
    }

    public void setPowerLocator(URI path) {
	this.powerPath = path;
    }

    public void setRemoteLocator(URI path) {
	this.remotePath = path;
    }

    public void setImageCompareProvider(ImageCompareProvider imageCompare) {
	this.imageCompareProvider = imageCompare;
    }

    public ImageCompareProvider getImageCompareProvider() {
	return this.imageCompareProvider;
    }

    public MotionDetectionProvider getMotionDetectorProvider() {
	return this.motionDetectionProvider;
    }

    public void setMotionDetectorProvider(MotionDetectionProvider motionDetectionProvider) {
	this.motionDetectionProvider = motionDetectionProvider;
    }

    public void setOCRProvider(OcrProvider ocr) {
	this.ocrProvider = ocr;
    }

    public OcrProvider getOCRProvider() {
	return this.ocrProvider;
    }

    public void setLocked(boolean locked) {
	LOGGER.info(" Set locked status as : " + locked);
	this.locked = locked;
    }

    public boolean isLocked() {
	LOGGER.info(" Get the locked status: " + this.locked);
	return this.locked;
    }

    public boolean sendText(String text, RemoteControlType type) {
	boolean toReturn = this.remoteProvider.sendText(text, type);
	LOGGER.info("sendText returned: " + toReturn);
	return toReturn;
    }

    public RFControlProvider getRfControl() {
	return this.rfControlProvider;
    }

    public void setRfControl(RFControlProvider rfControl) {
	this.rfControlProvider = rfControl;
    }

    public void connectRF() throws RFControlProviderException {
	LOGGER.info("connectRF");
	if (this.rfControlProvider == null)
	    return;
	this.rfControlProvider.connectRF();
    }

    public void disconnectRF() throws RFControlProviderException {
	LOGGER.info("disconnectRF");
	if (this.rfControlProvider == null)
	    return;
	this.rfControlProvider.disconnectRF();
    }

    public void attenuate(Integer dbLevel) throws RFControlProviderException {
	LOGGER.info("attenuate");
	if (this.rfControlProvider == null)
	    return;
	this.rfControlProvider.attenuate(dbLevel);
    }

    public void setMotionDetectionDiffImageSaveLocation(String location) {
	this.motionDetectionProvider.setMotionDetectionDiffImageSaveLocation(location);
    }

    public void setColorTolerance(float colorTolerancePct) {
	this.motionDetectionProvider.setColorTolerance(colorTolerancePct);
    }

    public void setLocationTolerance(float locationTolerancePct) {
	this.motionDetectionProvider.setLocationTolerance(locationTolerancePct);
    }

    public void setMotionTolerance(float motionTolerancePct) {
	this.motionDetectionProvider.setMotionTolerance(motionTolerancePct);
    }

    public float getMotionTolerance() {
	return this.motionDetectionProvider.getMotionTolerance();
    }

    public float getLocationTolerance() {
	return this.motionDetectionProvider.getLocationTolerance();
    }

    public float getColorTolerance() {
	return this.motionDetectionProvider.getColorTolerance();
    }

    // public EASProvider getEASProvider() {
    // return this.easProvider;
    // }
    //
    // public void setEASProvider(EASProvider easProvider) {
    // this.easProvider = easProvider;
    // }
    //
    // public void setProviders(Class<? extends BaseProvider> clazz,
    // BaseProvider provider) {
    // this.additionalProviders.put(clazz, provider);
    // }
    //
    // public <T extends BaseProvider> T getProvider(Class<T> clazz) {
    // BaseProvider result = null;
    //
    // Collection collection = this.additionalProviders.get(clazz);
    //
    // if (!(collection.isEmpty())) {
    // BaseProvider baseProvider = (BaseProvider) Iterables.get(collection, 0);
    // result = (BaseProvider) clazz.cast(baseProvider);
    // }
    // return result;
    // }
    //
    // public <T extends BaseProvider> List<T> getProviders(Class<T> clazz) {
    // Collection collection = this.additionalProviders.get(clazz);
    //
    // List resultProviders = new ArrayList();
    //
    // for (BaseProvider baseProvider : collection) {
    // resultProviders.add(clazz.cast(baseProvider));
    // }
    //
    // return resultProviders;
    // }
    //
    // public Metric getMetricsData() {
    // logWarn(" Get Metric Data at AbstractDut is not supported in 3.5.x");
    // throw new
    // UnsupportedOperationException(" Get Metric Data at AbstractDut is not supported in 3.5.x ");
    // }
    //
    // public String getRackName() {
    // return getSettopInfo().getRackName();
    // }
    //
    // public String getSlotName() {
    // return getSettopInfo().getSlotName();
    // }
    //
    // public Integer getSlotNumber() {
    // return getSettopInfo().getSlotNumber();
    // }
    //
    // public String getServerHost() {
    // return getSettopInfo().getServerHost();
    // }
    //
    // public Integer getServerPort() {
    // return getSettopInfo().getServerPort();
    // }

    public RemoteProvider getRemote() {
	return this.remoteProvider;
    }

    public EASProvider getEASProvider() {
	return this.easProvider;
    }

    public java.util.logging.Logger getLogger() {
	// TODO Auto-generated method stub
	return null;
    }

    public void logInfo(String paramString) {
	// TODO Auto-generated method stub

    }

    public void logError(String paramString) {
	// TODO Auto-generated method stub

    }

    public void logWarn(String paramString) {
	// TODO Auto-generated method stub

    }

    public void logDebug(String paramString) {
	// TODO Auto-generated method stub

    }

    public void logTrace(String paramString) {
	// TODO Auto-generated method stub

    }

    public String getLogDirectory() {
	// TODO Auto-generated method stub
	return null;
    }

    public String getRackName() {
	return this.rackName;
    }

    public String getSlotName() {
	return this.slotName;
    }

    public String getSlotNumber() {
	return this.slotNumber;
    }

    public String getServerHost() {
	return this.serverHost;
    }

    public Integer getServerPort() {
	return this.serverPort;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return this.name;
    }

    public String getHostIp4Address() {
	return this.hostIp4Address;
    }

    public String getHostIp6Address() {
	return this.hostIp6Address;
    }

    public String getHostMacAddress() {
	return this.hostMacAddress;
    }

    public String getMcardMacAddress() {
	return null;
    }

    public String getManufacturer() {
	return this.manufacturer;
    }

    public String getModel() {
	return this.model;
    }

    public String getMake() {
	return this.make;
    }

    public String getFirmwareVersion() {
	return null;
    }

    public String getMCardSerialNumber() {
	return null;
    }

    public String getUnitAddress() {
	return this.unitAddress;
    }

    public String getHardwareRevision() {
	return this.hardwareRevision;
    }

    public String getRemoteType() {
	return this.remoteType;
    }

    public String getContent() {
	return this.content;
    }

    public URI getAudioPath() {
	return this.audioPath;
    }

    public URI getClickstreamPath() {
	return this.clickstreamPath;
    }

    public URI getTracePath() {
	return this.tracePath;
    }

    public URI getVideoPath() {
	return this.videoPath;
    }

    public URI getVideoSelectionPath() {
	return this.videoSelectionPath;
    }

    public URI getClusterPath() {
	return this.clusterPath;
    }

    public URI getRemoteLocator() {
	return this.remoteLocator;
    }

    public boolean pressKey(Integer paramInteger, RemoteControlType type) {
	return this.remoteProvider.pressKey(paramInteger.toString(), type);
    }

    public List<String> getAllRemoteTypes() {
	return null;
    }

    public void setRemoteType(String paramString) {
	this.remoteType = paramString;

    }

    public void snmpSet(String paramString1, String paramString2, String paramString3, String paramString4)
	    throws SNMPException {
	// TODO Auto-generated method stub

    }

    public boolean detectMotion(long paramLong) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean detectMotion(long paramLong1, long paramLong2) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean detectMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1,
	    long paramLong2) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean detectMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong) {
	// TODO Auto-generated method stub
	return false;
    }

    public Map<Class<? extends BaseProvider>, BaseProvider> getAdditionalProviders() {
	return additionalProviders;
    }

    public void setAdditionalProviders(Map<Class<? extends BaseProvider>, BaseProvider> additionalProviders) {
	this.additionalProviders = additionalProviders;
    }

    public PowerProvider getPowerProvider() {
	return powerProvider;
    }

    public void setPowerProvider(PowerProvider powerProvider) {
	this.powerProvider = powerProvider;
    }

    public RemoteProvider getRemoteProvider() {
	return remoteProvider;
    }

    public void setRemoteProvider(RemoteProvider remoteProvider) {
	this.remoteProvider = remoteProvider;
    }

    public AudioProvider getAudioProvider() {
	return audioProvider;
    }

    public void setAudioProvider(AudioProvider audioProvider) {
	this.audioProvider = audioProvider;
    }

    public TraceProvider getTraceProvider() {
	return traceProvider;
    }

    public void setTraceProvider(ConnectionTraceProvider traceProvider) {
	this.traceProvider = traceProvider;
    }

    public void setVideoProvider(VideoProvider videoProvider) {
	this.videoProvider = videoProvider;
    }

    public MotionDetectionProvider getMotionDetectionProvider() {
	return motionDetectionProvider;
    }

    public void setMotionDetectionProvider(MotionDetectionProvider motionDetectionProvider) {
	this.motionDetectionProvider = motionDetectionProvider;
    }

    public OcrProvider getOcrProvider() {
	return ocrProvider;
    }

    public void setOcrProvider(OcrProvider ocrProvider) {
	this.ocrProvider = ocrProvider;
    }

    public RFControlProvider getRfControlProvider() {
	return rfControlProvider;
    }

    public void setRfControlProvider(RFControlProvider rfControlProvider) {
	this.rfControlProvider = rfControlProvider;
    }

    public EASProvider getEasProvider() {
	return easProvider;
    }

    public void setEasProvider(EASProvider easProvider) {
	this.easProvider = easProvider;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setHostMacAddress(String hostMacAddress) {
	this.hostMacAddress = hostMacAddress;
    }

    public void setMake(String make) {
	this.make = make;
    }

    public void setManufacturer(String manufacturer) {
	this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public void setUnitAddress(String unitAddress) {
	this.unitAddress = unitAddress;
    }

    public void setFirmwareVersion(String firmwareVersion) {
	this.firmwareVersion = firmwareVersion;
    }

    public void setHardwareRevision(String hardwareRevision) {
	this.hardwareRevision = hardwareRevision;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public void setHostIp4Address(String hostIp4Address) {
	this.hostIp4Address = hostIp4Address;
    }

    public void setHostIp6Address(String hostIp6Address) {
	this.hostIp6Address = hostIp6Address;
    }

    public void setAudioPath(URI audioPath) {
	this.audioPath = audioPath;
    }

    public void setClickstreamPath(URI clickstreamPath) {
	this.clickstreamPath = clickstreamPath;
    }

    public void setClusterPath(URI clusterPath) {
	this.clusterPath = clusterPath;
    }

    public void setTracePath(URI tracePath) {
	this.tracePath = tracePath;
    }

    public void setVideoPath(URI videoPath) {
	this.videoPath = videoPath;
    }

    public void setVideoSelectionPath(URI videoSelectionPath) {
	this.videoSelectionPath = videoSelectionPath;
    }

    public void setRackName(String rackName) {
	this.rackName = rackName;
    }

    public void setSlotName(String slotName) {
	this.slotName = slotName;
    }

    public void setSlotNumber(String slotNumber) {
	this.slotNumber = slotNumber;
    }

    public void setServerHost(String serverHost) {
	this.serverHost = serverHost;
    }

    public void setServerPort(Integer serverPort) {
	this.serverPort = serverPort;
    }

    public void setRemotes(Map<RemoteControlType, RemoteProvider> remotes) {
	this.remotes = remotes;
    }

    public void setExtraProperties(Map<String, String> extraProperties) {
	this.extraProperties = extraProperties;
    }

    @Override
    public void setMcardMacAddress(String mcardMacAddress) {
	this.mcardMacAddress = mcardMacAddress;

    }

    @Override
    public void setMCardSerialNumber(String mCardSerialNumber) {
	this.mCardSerialNumber = mCardSerialNumber;

    }

    @Override
    public String getHostIpAddress() {
	return this.hostIpAddress;
    }

    @Override
    public void setHostIpAddress(String hostIpAddress) {
	this.hostIpAddress = hostIpAddress;

    }

    /**
     * @return the rackObject
     */
    public Object getRackObject() {
	return rackObject;
    }

    /**
     * @param rackObject
     *            the rackObject to set
     */
    public void setRackObject(Object rackObject) {
	this.rackObject = rackObject;
    }

    @Override
    public Dut getDevice() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setDevice(Dut device) {
	// TODO Auto-generated method stub

    }

    @Override
    public TraceProvider getSerialTrace() {
	return serialTraceProvider;
    }

    public void setSerialTrace(TraceProvider traceProvider) {
	this.serialTraceProvider = traceProvider;
    }

    /**
     * @return the defaultRemoteControlType
     */
    public RemoteControlType getDefaultRemoteControlType() {
	return defaultRemoteControlType;
    }

    /**
     * @param defaultRemoteControlType
     *            the defaultRemoteControlType to set
     */
    public void setDefaultRemoteControlType(RemoteControlType defaultRemoteControlType) {
	this.defaultRemoteControlType = defaultRemoteControlType;
    }

}
