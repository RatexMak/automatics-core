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
package com.automatics.providers.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Device;
import com.automatics.enums.SshMechanism;
import com.automatics.exceptions.WebPaFailureException;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.BeanUtils;
import com.automatics.webpa.WebPaParameter;

/**
 * This class hold the reverse ssh connection parameters/methods required to establish a reverse ssh connection.
 * 
 * @author rohinic
 *
 */
public class ReverseSshConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseSshConnection.class);
    public static int portMinVal = 3000;
    public static int portMaxVal = 5000;
    public static String COMMAN_NETSTAT = "netstat -tuln |grep -i \"127.0.0.1:<port>\"";
    protected Device settop;

    // Reverse ssh port
    protected int port;

    // Idle timeout for revrse ssh connection.If the connection is not used for more than 15 minutes, it will be
    // terminated automatically
    protected long time = 900; // 15 min

    // Host name that is connecting to device
    protected String reverseSshHost;

    // IP Address of host used for reverse ssh connection to be passed as argument to webpa
    protected String reverseSshHostIpAddress;

    protected DeviceConnectionProvider connectionProvider = null;

    public String getReverseSshHostIpAddress() {
	return reverseSshHostIpAddress;
    }

    public String getReverseSshHost() {
	return reverseSshHost;
    }

    public void setReverseSshHost(String reverseHost) {
	this.reverseSshHost = reverseHost;
    }

    public Device getSettop() {
	return settop;
    }

    public void setSettop(Device settop) {
	LOGGER.debug("Setting dut object");
	this.settop = settop;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	LOGGER.debug("Setting port " + port);
	this.port = port;
    }

    public long getTime() {
	return time;
    }

    public void setTime(long time) {
	this.time = time;
    }

    /**
     * Constructor method which creates the connection
     * 
     * @param dut
     *            Dut Object
     * @throws IOException
     * @throws WebPaFailureException
     */
    public ReverseSshConnection(Device settop) {
	LOGGER.info("Creating new connection object from constructor");
	connectionProvider = BeanUtils.getDeviceConnetionProvider();

	LOGGER.info("Setting dut");
	setSettop(settop);

    }

    public void createConnection() throws IOException, WebPaFailureException {
	if (null == settop) {
	    LOGGER.error("Could not create connection as device is not set for ReverseSshConnection instance");
	    return;
	}
	if (null == reverseSshHost || null == reverseSshHostIpAddress) {
	    LOGGER.error("Could not create connection as ssh host/ipaddress is not set for ReverseSshConnection instance");
	    return;
	}
	LOGGER.info("Finding free port for device: {}", settop.getHostMacAddress());
	// Selecting and setting port
	int freePort = getFreePort();
	LOGGER.info("Free port obtained {} for reverse ssh connection for device {}", freePort,
		settop.getHostMacAddress());
	setPort(freePort);

    }

    /**
     * This method runs the web pA commands that actually opens up a reverse ssh connection
     * 
     * @throws WebPaFailureException
     */
    private void createReverseSshConnection() throws WebPaFailureException {
	boolean isSet = true;
	AutomaticsTapApi ecatsTap = AutomaticsTapApi.getInstance();
	LOGGER.info("Setting Reverse ssh Arguments for {}", this.getSettop().getHostMacAddress());
	try {
	    List<WebPaParameter> params = ecatsTap.setWebPaParamsWithWildCard(this.getSettop(),
		    "Device.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.ReverseSSH.xOpsReverseSshArgs",
		    getValue(this.getSettop(), getReverseSshHostIpAddress()), 0);
	    LOGGER.info("params set");
	    if (isSuccess(params)) {
		// start connection
		LOGGER.info("Starting Reverse ssh for {}", this.getSettop().getHostMacAddress());

		params = ecatsTap.setWebPaParamsWithWildCard((Device) this.getSettop(),
			"Device.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.ReverseSSH.xOpsReverseSshTrigger", "start",
			0);
		if (isSuccess(params)) {
		    // check connection status
		    int retry = 7;
		    while (retry > 0) {
			String response = ecatsTap.executeWebPaCommandWithWildCardSupport(this.getSettop(),
				"Device.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.ReverseSSH.xOpsReverseSshStatus");
			if (CommonMethods.isNull(response) || !response.equals("ACTIVE")) {
			    LOGGER.error("Connection is not active.Waiting for 20 seconds and checking again");
			    AutomaticsUtils.sleep(AutomaticsConstants.TWENTY_SECONDS);
			    isSet = false;
			} else {
			    boolean isConnected = executeNetStatAndVerifyConnectivity(Integer.toString(this.getPort()));
			    if (!isConnected) {
				LOGGER.error("REVERSE SSH STATUS IS ACTIVE.BUT HOST CONNECTION NOT ESTABLISHED YET!!");
				isSet = false;
				AutomaticsUtils.sleep(AutomaticsConstants.TWENTY_SECONDS);
			    } else {
				LOGGER.info("Connection is ACTIVE and ALIVE now after {} seconds wait time ",
					(7 - retry) * 20);
				isSet = true;
				break;
			    }
			}
			retry--;
		    }
		} else {
		    LOGGER.error("Unable to start reverse ssh connection for " + this.getSettop().getHostMacAddress());
		    isSet = false;
		}
	    } else {
		LOGGER.error("Unable to set reverse ssh params " + this.getSettop().getHostMacAddress());
		isSet = false;
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage());
	    isSet = false;
	}
	if (!isSet) {
	    LOGGER.info("Releasing server socket as connection is established on {} , for {}", port, this.getSettop()
		    .getHostMacAddress());
	    connectionProvider.execute(reverseSshHost, "rm " + port + "_Connecting" + ";ls -l " + port + "_Connecting",
		    AutomaticsConstants.THIRTY_SECONDS, "SSH");

	    throw new WebPaFailureException("Webpa execution failed");

	} else {
	    time = new Date().getTime();
	    LOGGER.info("Reverse ssh started for {} on port {}", this.getSettop().getHostMacAddress(), this.getPort());
	}
    }

    /**
     * This method creates the VM arguments to be passed inorder to start a reverse ssh connection from device to the
     * host
     * 
     * @param dut
     *            Dut Object
     * @param reverseHostIp
     *            Server Ip
     * @return Returns the argument
     */
    private String getValue(Device device, String reverseHostIp) {
	StringBuilder valueString = new StringBuilder();
	LOGGER.info("Sending request to webpa for initiating reverse ssh connection for device: {}",
		device.getHostMacAddress() + reverseHostIp);
	String sshPort = AutomaticsPropertyUtility.getProperty("reverse.ssh.port." + getReverseSshHost(), "9090");
	String webPaUser = AutomaticsPropertyUtility.getProperty("webpa.user", AutomaticsConstants.EMPTY_STRING);
	valueString.append("idletimeout=").append(this.time).append(";revsshport=").append(this.port)
		.append(";sshport=").append(sshPort).append(";user=").append(webPaUser).append(";host=")
		.append(reverseHostIp);
	return valueString.toString();
    }

    /**
     * Method to check for successful execution of webpa
     * 
     * @param params
     *            Results of web pa execution
     * @return Boolean value whether success or not - true/false
     */
    private static boolean isSuccess(List<WebPaParameter> params) {
	boolean success = false;
	if (params != null) {
	    WebPaParameter response = params.get(0);
	    LOGGER.info("Webpa response " + response);
	    if ("Success".equals(response.getMessage())) {
		success = true;
	    }
	}
	return success;
    }

    /**
     * Iterates from lowest port 3000 to 5000 each time checking if port is in use . It returns an unused port
     * 
     * @return Free port
     * @throws IOException
     * @throws WebPaFailureException
     */
    private int getFreePort() throws IOException, WebPaFailureException {
	int port = 0;
	int i = portMinVal;
	List<Integer> previousList = new ArrayList<Integer>();

	for (; i <= portMaxVal; i++) {
	    Random r = new Random();
	    int result = r.nextInt(portMaxVal - portMinVal) + portMinVal;
	    if (!previousList.contains(result)) {
		LOGGER.debug("Fetching random port =" + result);
		if (!isPortInUse(result)) {
		    port = result;
		    break;
		} else {
		    previousList.add(result);
		}
	    }
	    if (previousList.size() == 20) {
		previousList.clear();
	    }
	}

	if (port == 0 && i == portMaxVal) {
	    throw new IOException("All ports are in use");
	}
	LOGGER.info("Using port {} for device {}", port, settop.getHostMacAddress());
	return port;
    }

    /**
     * This method checks if port is in use or not by executing nc command to listen to a port // When we identify a
     * port, while webpas are executed, it can be taken up by different device .Hence we need to lock // the port.That
     * is why we are listening to the port WHen the port is being listenened to , it cannot be used by any other
     * device.While the netcat commands listen in background, we create a file <port>_Connecting in home folder of
     * svcpeqa01 useer. We cannot allocate the port for reverse ssh if listening continues.But when we stop listening it
     * can be allocated by some other device.To handle this situation , we are creating this file.While the litening is
     * done in background, we create the file and then stop listening
     * 
     * So before checking for a port, if we find <port>_Connecting file for that particular port, we dont go durthur to
     * check if port is free, becasue it is at this stage that port is alllocated by framework for a device. Now the
     * port will be set to device and web pa commands can be executed.Once webpa execution is complete, COnnectiong will
     * be removed and COnnected file will be created.
     * 
     * @param port
     * @return Booelan indicating port is free or not
     * @throws WebPaFailureException
     * @throws IllegalArgumentException
     */
    private boolean isPortInUse(int port) throws WebPaFailureException, IllegalArgumentException {
	boolean isPortInUse = false;

	String response = connectionProvider.execute(reverseSshHost, "ls -l " + port + "_Connecting" + " " + port
		+ "_Connected" + " 2>&1| grep -ic \"No such file\"", AutomaticsConstants.THIRTY_SECONDS, "SSH");

	if (CommonMethods.isNull(response) || response.contains("2")) {
	    String pid = connectionProvider
		    .execute(
			    reverseSshHost,
			    "(nc -k -l "
				    + port
				    + " &) && out=$(ps -ef |grep \"nc -k \") && processid=$(ps -ef |grep \"nc -k \"| grep -v bash|grep -v grep|sed -r 's/svccpeq\\+\\s+([0-9]+)\\s+.*/\\1/') && portValue=$(ps -ef |grep \"nc -k \" |grep -v bash| grep -v sed|grep -v grep|sed -r 's/svccpeq.*nc -k -l ([0-9]+).*$/\\1/'|tr -d \"\\r\\n\") && if [[ \"$processid\" != *\"QUITTING\"* ]]; then echo PORT=$portValue;echo PID=$processid;touch $portValue\"_Connecting\"; else echo \"Port is in use\"; fi; kill $processid;",
			    AutomaticsConstants.THIRTY_SECONDS, "SSH");

	    if (CommonMethods.isNotNull(pid) && !pid.contains("QUITTING.") && !pid.contains("Address already in use")) {
		this.setPort(port);
		createReverseSshConnection();

		connectionProvider.execute(reverseSshHost,
			"touch " + this.getPort() + "_Connected" + ";ls -l " + this.getPort() + "_Connected;" + "rm "
				+ this.getPort() + "_Connecting" + ";ls -l " + this.getPort() + "_Connecting",
			AutomaticsConstants.THIRTY_SECONDS, "SSH");

	    } else {
		LOGGER.debug("Port in use {} for device {}", port, settop.getHostMacAddress());
		isPortInUse = true;
	    }
	} else {
	    LOGGER.debug("Port in use {} for device {}", port, settop.getHostMacAddress());
	    isPortInUse = true;
	}

	return isPortInUse;
    }

    /**
     * This method closes the reverse ssh connection by executing webpa.It also teminates the ssh connection with jump
     * server
     * 
     * @param dut
     */

    public void closeConnection(Device settop) {

	AutomaticsTapApi ecatsTap = AutomaticsTapApi.getInstance();
	Map<String, Connection> connections = settop.getPersistentConnections();
	ReverseSshConnection reverSshConnection = null;
	if (null != connections && null != connections.get("revSsh")) {
	    reverSshConnection = (ReverseSshConnection) connections.get("revSsh");
	    // if (reverSshConnection.isAlive()) {
	    connectionProvider.execute(reverseSshHostIpAddress, "rm " + port + "*;ls -l " + port + "*",
		    AutomaticsConstants.THIRTY_SECONDS, SshMechanism.SSH.name());

	    List<WebPaParameter> params = ecatsTap.setWebPaParamsWithWildCard(settop,
		    "DeviceConfig.DeviceInfo.X_RDKCENTRAL-COM_xOpsDeviceMgmt.ReverseSSH.xOpsReverseSshTrigger", "stop",
		    0);
	    if (isSuccess(params)) {

		connections.remove("revSsh");
		LOGGER.info("Closing connection for {} on port {}", settop.getHostMacAddress(),
			reverSshConnection.getPort());
	    } else {
		LOGGER.error("Connection could not be closed for device " + settop.getHostMacAddress() + " on port "
			+ reverSshConnection.getPort());
	    }
	}

    }

    @Override
    public String toString() {
	StringBuffer log = new StringBuffer();
	log.append("Mac address :\n" + this.settop.getHostMacAddress());
	log.append("Port :" + this.getPort());
	return log.toString();
    }

    /**
     * Checks if port is being listenened to using netcat command
     * 
     * @param port
     * @return Returns boolean true or false based on if port is being listened to or not respectively
     */
    private boolean executeNetStatAndVerifyConnectivity(String port) {
	boolean isConnected = false;
	String command = COMMAN_NETSTAT.replaceAll("<port>", new String(port));

	String netStatOutput = connectionProvider.execute(reverseSshHost, command, AutomaticsConstants.THIRTY_SECONDS,
		reverseSshHost);
	if (CommonMethods.isNotNull(netStatOutput)) {
	    isConnected = true;
	}
	return isConnected;
    }

    @Override
    public OutputStream getDefaultOutputStream() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public InputStream getDefaultInputStream() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void disconnect() {
	// TODO Auto-generated method stub

    }

    @Override
    public String getDefaultUsername() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getDefaultPassword() {
	// TODO Auto-generated method stub
	return null;
    }
}
