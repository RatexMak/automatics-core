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
package com.automatics.providers.imageupgrade;

import java.util.ArrayList;
import java.util.List;

import com.automatics.constants.SnmpConstants;
import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.providers.connection.ExecuteCommandType;
import com.automatics.utils.BeanUtils;

/**
 * 
 * Performs image upgrade using snmp
 *
 */
public abstract class AbstractSnmpImageUpgradeProvider implements ImageUpgradeProvider {

    /**
     * 
     */
    @Override
    public String performImageUpgrade(boolean rebootImmediately, String firmwareToBeDownloaded, Dut device) {

	ImageRequestParams params = new ImageRequestParams();
	params.setCommunityString(this.getCommunityString(device));
	params.setFirmwareToBeDownloaded(firmwareToBeDownloaded);
	params.setSnmpImageDownloadServerIp(getImageDownloadServerIp(device));
	return performImageUpgrade(params, device);

    }

    /**
     * Gets the snmp community string
     * @param device
     * @return
     */
    public abstract String getCommunityString(Dut device);

    /**
     * Gets the image download server ip
     * @param device
     * @return
     */
    public abstract String getImageDownloadServerIp(Dut device);

    /**
     * Perform image upgrade using snmp
     */
    @Override
    public String performImageUpgrade(ImageRequestParams params, Dut device) {

	DeviceConnectionProvider deviceConnectionProvider = BeanUtils.getDeviceConnetionProvider();

	String ipAddress = this.getDeviceIpAddress(device);
	List<String> commandsToTriggerCdl = new ArrayList<String>();
	commandsToTriggerCdl.add(SnmpConstants.CMD_SNMP_SET_WITH_COMMUNITY + " " + params.getCommunityString() + " "
		+ ipAddress + " " + SnmpConstants.CMD_SNMP_SET_DOCS_DEV_SW_SERVER_OID + " "
		+ getImageDownloadServerIp(device));
	commandsToTriggerCdl.add(SnmpConstants.CMD_SNMP_SET_WITH_COMMUNITY + " " + params.getCommunityString() + " "
		+ ipAddress + " " + SnmpConstants.CMD_SNMP_SET_DOCS_DEV_SW_FILE_NAME_OID + " "
		+ params.getFirmwareToBeDownloaded());
	commandsToTriggerCdl.add(SnmpConstants.CMD_SNMP_SET_WITH_COMMUNITY + " " + ipAddress + " "
		+ SnmpConstants.CMD_SNMP_SET_DOCS_DEV_SW_ADMIN_STATUS_OID);
	return deviceConnectionProvider.execute((Device) device, ExecuteCommandType.SNMP_CODE_DOWNLOAD,
		commandsToTriggerCdl);
    }
    
    
    /**
     * Gets device ip address for snmp image upgrade
     * @param device
     * @return device ip address
     */
    public abstract String getDeviceIpAddress(Dut device);

}
