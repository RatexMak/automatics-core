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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.device.Device;
import com.automatics.device.Dut;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author rohinic
 * 
 *         Common apis for natted clients
 *
 */
public class NattedClientUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NattedClientUtils.class);
    static volatile NattedClientUtils nattedClientUtils = null;

    private NattedClientUtils() {
	if (nattedClientUtils != null) {
	    throw new RuntimeException("Creation of multiple handlers is prohibitted");
	}
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
	return super.clone();
    };

    public static NattedClientUtils getInstance() {
	if (nattedClientUtils == null) {
	    synchronized (NattedClientUtils.class) {
		if (nattedClientUtils == null) {
		    nattedClientUtils = new NattedClientUtils();
		}
	    }
	}
	return nattedClientUtils;
    }

    /**
     * 
     * This method copied the created profile to given device
     * 
     * @param fileToCopy
     *            Fullpath of file to be copied
     * @param dut
     *            Dut Object
     */

    public boolean copyfileToClient(String fileToCopy, Dut dut, String remoteFileName) {
	boolean status = false;
	Device ecatsSettop = (Device) dut;
	if (CommonMethods.isNull(remoteFileName)) {
	    remoteFileName = fileToCopy.substring(fileToCopy.lastIndexOf(File.separator) + 1);
	} else {
	    remoteFileName = fileToCopy.substring(0, fileToCopy.lastIndexOf(File.separator)) + File.separator
		    + remoteFileName;
	}
	String userName = ecatsSettop.getUsername();
	String host = ecatsSettop.getNatAddress();
	String port = ecatsSettop.getNatPort();
	String password = ecatsSettop.getPassword();
	JSch jsch = new JSch();
	LOGGER.info("Copying source file {} to  host {} port {} ", fileToCopy, host, port);
	OutputStream out = null;
	InputStream in = null;
	Session session = null;
	FileInputStream fis = null;
	Channel channel = null;
	try {
	    if (CommonMethods.isNotNull(userName) && CommonMethods.isNotNull(host) && CommonMethods.isNotNull(port)
		    && CommonMethods.isNotNull(password)) {
		int portValue = Integer.parseInt(port);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session = jsch.getSession(userName, host, portValue);
		session.setConfig(config);
		LOGGER.debug("Creating session");
		session.setPassword(password);
		session.connect();

		// exec 'scp -t remoteFileName' remotely
		String command = "scp " + " -t " + remoteFileName;
		channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		out = channel.getOutputStream();
		in = channel.getInputStream();

		channel.connect();

		File fp = new File(fileToCopy);
		long fileSize = fp.length();
		command = "C0644 " + fileSize + " " + remoteFileName + "\n";
		out.write(command.getBytes());
		out.flush();

		ischeckCopyAcknowledgementSuccess(in);

		fis = new FileInputStream(fileToCopy);
		byte[] buf = new byte[1024];
		while (true) {
		    int len = fis.read(buf, 0, buf.length);
		    if (len <= 0)
			break;
		    out.write(buf, 0, len); // out.flush();
		}
		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();
		ischeckCopyAcknowledgementSuccess(in);
		LOGGER.info("[TEST LOG] : SCP Complete : " + remoteFileName);
		status = true;
	    } else {
		LOGGER.error("DeviceConfig " + dut.getHostMacAddress() + " is not properly configured ");
	    }
	} catch (JSchException e) {
	    LOGGER.error("Error while copying profile " + e.getMessage());
	    e.printStackTrace();
	} catch (Exception e) {
	    LOGGER.error("Error while copying profile " + e.getMessage());
	    e.printStackTrace();
	} finally {
	    try {
		if (fis != null) {
		    fis.close();
		}
		if (in != null) {
		    in.close();
		}
		if (out != null) {
		    out.close();
		}
		if (channel != null) {
		    channel.disconnect();
		}
		if (session != null) {
		    session.disconnect();
		}
	    } catch (IOException e) {
	    }
	}
	return status;
    }

    private void ischeckCopyAcknowledgementSuccess(InputStream is) throws IOException, JSchException {
	boolean status = false;
	int b = is.read();
	if (b == 0) {
	    status = true;
	}
	if (!status) {
	    throw new JSchException("Error while initiating or during scp for profile");
	}
    }
}
