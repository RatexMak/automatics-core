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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.error.GeneralError;
import com.automatics.exceptions.FailedTransitionException;
import com.automatics.providers.connection.auth.Credential;
import com.automatics.providers.connection.auth.CredentialFactory;
import com.automatics.providers.connection.auth.ICrypto;
import com.automatics.utils.BeanUtils;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.ExpectImplementationUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * Class is used to handle SSH connections, sending and receiving of data through the provided connection.
 *
 * @author Selvaraj Mariyappan
 * @author Arjun P
 */
public class SshConnection implements Connection {

    /** Default ssh port number. */
    private static int DEFAULT_SSH_PORT_NUMBER = 22;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SshConnection.class);

    /** Custom port number. */
    protected int portNumber = 0;

    /** The default login user name. */
    private String defaultUsername = null;

    /** The default password. */
    private String defaultPassword = null;

    /** The root password. */
    private String rootPassword = null;

    /** The host name. */
    private String hostName = null;

    /** location of the private key. */
    private String privateKeyLocation = null;

    protected InputStream defaultInputStream = null;
    protected OutputStream defaultOutputStream = null;
    protected InputStream defaultErrorInputStream = null;
    protected InputStream inputStream = null;
    protected InputStream errorInputStream = null;
    protected OutputStream outputStream = null;
    protected Channel channel = null;
    protected Channel channelExec = null;
    protected Session session = null;
    protected ICrypto crypto = null;

    protected StringBuffer commandResponse = new StringBuffer();

    public SshConnection() {
	try {
	    crypto = BeanUtils.getCredentialCrypto();
	} catch (Exception e) {
	    LOGGER.error("Credential decryptor not configured. Hence using the credential directly from config file.",
		    e.getMessage());
	}
    }

    /**
     * Create an SSH object that will not connect to a host at initialization time.
     *
     * @param creds
     *            List of credentials for known hosts
     * @param username
     *            Default username (non root user)
     * @param password
     *            Default users password
     * @param host
     *            FQDN or IP of the host to connect to
     * @param port
     *            SSH port on the target host
     */
    public SshConnection(final Credential creds, final String username, final String password, final String host,
	    final int port, final String privateKeyLocation) {
	this();
	rootPassword = creds.getPassword("root");

	if (null != username) {
	    defaultUsername = username;
	} else {
	    defaultUsername = creds.getDefaultLogin();
	}

	if (null != password) {
	    defaultPassword = password;
	} else {
	    defaultPassword = creds.getPassword(defaultUsername);
	}

	if (defaultPassword == null) {
	    throw new FailedTransitionException(GeneralError.FAILED_RESOURCE_READ, "Unable to find password for "
		    + defaultUsername);
	}

	if (CommonMethods.isNotNull(privateKeyLocation)) {
	    this.privateKeyLocation = privateKeyLocation;
	} else {
	    this.privateKeyLocation = creds.getPrivateKeyLocation();
	}

	this.hostName = host;
	LOGGER.trace("SSH Host IP from Constructor " + host);
	if (port <= 0 || port >= 65536) {
	    this.portNumber = DEFAULT_SSH_PORT_NUMBER;
	} else {
	    this.portNumber = port;
	}
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     *
     * @param username
     *            Default username (non root user)
     * @param password
     *            Default users password
     * @param host
     *            FQDN or IP of the host to connect to
     * @param privateKeyLocation
     *            - the private key location for key based authentication
     */
    public SshConnection(String username, String password, String host, String privateKeyLocation) {
	this(CredentialFactory.get().getServerCredentials(host), username, password, host, 0, privateKeyLocation);
	connect();
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     *
     * @param username
     *            Default username (non root user)
     * @param password
     *            Default users password
     * @param host
     *            FQDN or IP of the host to connect to
     */
    public SshConnection(String username, String password, String host) {
	this(CredentialFactory.get().getServerCredentials(host), username, password, host, 0, null);
	connect();
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     *
     * @param username
     *            Default username (non root user)
     * @param host
     *            FQDN or IP of the host to connect to
     */
    public SshConnection(String username, String host) {
	this(CredentialFactory.get().getServerCredentials(host), username, null, host, 0, null);
	connect();
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     *
     * @param host
     *            FQDN or IP of the host to connect to
     */
    public SshConnection(String host) {
	this(CredentialFactory.get().getServerCredentials(host), null, null, host, 0, null);
	connect();
    }

    /**
     * This method created a SSH conenction of given type defined in enum in ServerUtils
     * 
     * @param host
     *            Host to which connection has to be established
     * @param connectionType
     *            enum definind for type of channel to be openend
     */
    public SshConnection(String host, ConnectionType connectionType) {
	this(CredentialFactory.get().getServerCredentials(host), null, null, host, 0, null);
	connect(connectionType);
    }

    /**
     * 
     * This method opens the required connection
     * 
     * @param connectionType
     *            - enum definind for type of channel to be openend
     */
    private void connect(ConnectionType connectionType) {
	try {
	    connect(defaultUsername, defaultPassword, hostName, portNumber, privateKeyLocation, connectionType);
	} catch (JSchException jsche) {
	    throw new FailedTransitionException(GeneralError.SSH_CONNECTION_FAILURE, jsche);
	} catch (InterruptedException ie) {
	    throw new FailedTransitionException(GeneralError.SSH_CONNECTION_FAILURE, ie);
	}
    }

    /**
     * 
     * Create connection
     * 
     * @param username
     *            Username
     * @param password
     *            Passsord
     * @param host
     *            Host to which connection has to be established
     * @param port
     *            Port
     * @param privateKeyLocation2
     *            Key file location
     * @param connectionType
     *            Type of connection
     * @throws JSchException
     * @throws InterruptedException
     */
    private void connect(String username, String password, String host, int port, String privateKeyLocation2,
	    ConnectionType connectionType) throws JSchException, InterruptedException {
	JSch sftp = new JSch();
	if (CommonMethods.isNotNull(privateKeyLocation)) {
	    // This is required only if the authentication is key based
	    if (null != crypto) {
		sftp.addIdentity(privateKeyLocation, crypto.decrypt(password));
	    } else {
		sftp.addIdentity(privateKeyLocation, password);
	    }
	}
	session = sftp.getSession(username, host, port);
	if (CommonMethods.isNull(privateKeyLocation)) {
	    // This is required only if the authentication is password based
	    if (null != crypto) {
		session.setPassword(crypto.decrypt(password));
	    } else {
		session.setPassword(password);
	    }
	}
	java.util.Properties config = new java.util.Properties();
	config.put("StrictHostKeyChecking", "no");
	session.setConfig(config);
	session.connect();
	channel = session.openChannel("sftp");
	((ChannelSftp) channel).setPty(true);
	channel.connect();
	Thread.sleep(1000);
    }

    /**
     * Set default port to connect to for constructors that don't take it as an argument.
     */
    static void setDefaultPort(final int port) {
	DEFAULT_SSH_PORT_NUMBER = port;
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     * 
     * @param ipaddress
     *            The IP address of host to connect.
     * 
     * @param port
     *            SSH port on the target host
     * 
     * @param username
     *            The user name of target host.
     * @param password
     *            The login password.
     *
     */
    public SshConnection(String ipaddress, int portNumber, String userName, String password, String privateKeyLocation) {
	this.defaultUsername = userName;
	this.defaultPassword = password;
	this.hostName = ipaddress;
	this.portNumber = portNumber;
	this.privateKeyLocation = privateKeyLocation;
	connectWithDefaultCredentials();
    }

    /**
     * Used to create an SSH shell to a remote machine so commands can be passed through and responses received.
     * 
     * @param ipaddress
     *            The IP address of host to connect.
     * 
     * @param port
     *            SSH port on the target host
     * 
     * @param username
     *            The user name of target host.
     * @param password
     *            The login password.
     *
     */
    public SshConnection(String ipaddress, int portNumber, String userName, String password) {
	this.defaultUsername = userName;
	this.defaultPassword = password;
	this.hostName = ipaddress;
	this.portNumber = portNumber;
	connectWithDefaultCredentials();
    }

    /**
     * Method to initialize the session and connect to the channel using default credentials.
     *
     *
     */
    private void connectWithDefaultCredentials() {
	try {
	    JSch shell = new JSch();
	    session = shell.getSession(this.defaultUsername, this.hostName, this.portNumber);
	    session.setPassword(this.defaultPassword);
	    java.util.Properties config = new java.util.Properties();
	    config.put("StrictHostKeyChecking", "no");
	    session.setConfig("PreferredAuthentications", "password,keyboard-interactive,publickey,gssapi-with-mic");
	    session.setConfig(config);
	    // Setting timeout for session to close after max of 30 seconds.
	    session.connect((int) AutomaticsConstants.THIRTY_SECONDS);
	    if (!session.isConnected()) {
		// wait for 2 seconds to establish connection to remove server.
		Thread.sleep((int) AutomaticsConstants.TWO_SECONDS);
	    }
	} catch (Exception exe) {
	    throw new FailedTransitionException(GeneralError.SSH_CONNECTION_FAILURE, exe);
	}
    }

    /**
     * Connect to the configured host.
     */
    public void connect() {

	try {
	    connect(defaultUsername, defaultPassword, hostName, portNumber, privateKeyLocation);
	} catch (JSchException jsche) {
	    throw new FailedTransitionException(GeneralError.SSH_CONNECTION_FAILURE, jsche);
	} catch (InterruptedException ie) {
	    throw new FailedTransitionException(GeneralError.SSH_CONNECTION_FAILURE, ie);
	}

    }

    /**
     * Method to initialize the session and connect to the channel using given credentials.
     *
     * @param username
     *            The login user name.
     * @param password
     *            The login password.
     * @param host
     *            FQDN or IP of the host to connect to
     * @param port
     *            SSH port on the target host
     * @param privateKeyLocation
     *            - location of the private key for Key based login
     *
     * @throws JSchException
     * @throws InterruptedException
     */
    private void connect(String username, String password, String host, int port, String privateKeyLocation)
	    throws JSchException, InterruptedException {
	JSch shell = new JSch();

	if (CommonMethods.isNotNull(privateKeyLocation)) {
	    // This is required only if the authentication is key based
	    if (null != crypto) {
		shell.addIdentity(privateKeyLocation, crypto.decrypt(password));
	    } else {
		shell.addIdentity(privateKeyLocation, password);
	    }
	}

	session = shell.getSession(username, host, port);

	if (CommonMethods.isNull(privateKeyLocation)) {
	    // This is required only if the authentication is password based
	    if (null != crypto) {
		session.setPassword(crypto.decrypt(password));
	    } else {
		session.setPassword(password);
	    }
	}

	java.util.Properties config = new java.util.Properties();
	config.put("StrictHostKeyChecking", "no");
	session.setConfig(config);
	session.connect((int) AutomaticsConstants.THIRTY_SECONDS);
	if (!session.isConnected()) {
	    Thread.sleep(AutomaticsConstants.TWO_SECONDS);
	}
    }

    /**
     * Check the status of default user channel.
     *
     * @return True if the default user channel is connected.
     */
    private boolean isDefaultChannelConnected() {
	return (channel != null && channel.isConnected());
    }

    /**
     * Check the status of super user channel.
     *
     * @return True if the super user channel is connected.
     */
    private boolean isSuChannelConnected() {
	return (channelExec != null && channelExec.isConnected());
    }

    /**
     * Used to disconnect all SSH connections.
     */
    public void disconnect() {
	LOGGER.trace("With in SSHConnection Disconnect method");

	if (isDefaultChannelConnected()) {
	    try {
		channel.sendSignal("2");
		channel.sendSignal("9");
		defaultInputStream.close();
		defaultErrorInputStream.close();
		defaultOutputStream.close();
	    } catch (Exception e) {
		LOGGER.trace("Failed to send Interrupt & kill Signal to jump server" + e.getMessage());
	    }

	    channel.disconnect();
	    LOGGER.trace("With in SSHConnection Disconnect method channel disconnected");
	}

	if (isSuChannelConnected()) {
	    try {
		channelExec.sendSignal("2");
		channelExec.sendSignal("9");
		inputStream.close();
		errorInputStream.close();
		outputStream.close();
	    } catch (Exception e) {
		LOGGER.trace("Failed to send Interrupt & kill Signal to jump server" + e.getMessage());
	    }
	    channelExec.disconnect();
	    LOGGER.trace("With in SSHConnection Disconnect method channelExec disconnected");
	}

	session.disconnect();
	LOGGER.trace("With in SSHConnection Session disconnected" + session.isConnected());

    }

    /**
     * Use this to send commands as the user that the initial connection was created for.
     *
     * @param command
     *            Command to execute
     *
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH Connection error
     */
    public void send(String command) throws IOException, InterruptedException, JSchException {
	doSend(command, "user", 1000);
    }

    /**
     * Use this to send commands as the user that the initial connection was created for. This is the overloaded version
     * which allows you to specify a wait timeout for commands you know are really slow (eg: command console
     * interactions).
     *
     * @param command
     *            Command to execute
     * @param timeout
     *            Overloaded timeout if you need more then 1 second wait time
     *
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH Connection error
     */
    public void send(String command, int timeout) throws IOException, InterruptedException, JSchException {
	doSend(command, "user", timeout);
    }

    /**
     * Private function to actually preform the sending. It takes in the command, the buffers, and a timeout and makes
     * the magic happen.
     *
     * @param command
     *            Command to execute
     * @param type
     *            This is the type of command we're running, either "root" or anything else. It is to deal with a
     *            problem where I can't get the objects to reassign, like with a pointer.
     * @param sleepTime
     *            The time to wait for output to appear on the terminal
     *
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH Connection error
     */
    private void doSend(String command, String type, int sleepTime) throws IOException, InterruptedException,
	    JSchException {
	if ("root".equals(type)) {
	    channelExec = session.openChannel("exec");
	    ((ChannelExec) channelExec).setPty(true);
	    ((ChannelExec) channelExec).setCommand(command);
	    inputStream = channelExec.getInputStream();
	    errorInputStream = ((ChannelExec) channel).getErrStream();
	    outputStream = channelExec.getOutputStream();
	    channelExec.connect(sleepTime);
	} else {
	    channel = session.openChannel("exec");
	    ((ChannelExec) channel).setPty(true);
	    ((ChannelExec) channel).setCommand(command);
	    defaultInputStream = channel.getInputStream();
	    defaultErrorInputStream = ((ChannelExec) channel).getErrStream();
	    defaultOutputStream = channel.getOutputStream();
	    channel.connect(sleepTime);
	}
	// ATOM Side SSH takes some time to establish the connection, so we are getting
	// partial response, which causing test case failure.
	if (command.contains("sshtoatom") || command.contains("sshtoqtn")) {
	    Thread.sleep(3 * AutomaticsConstants.ONE_SECOND);
	}
    }

    public InputStream getDefaultInputStream() {
	return defaultInputStream;
    }

    public void setDefaultInputStream(InputStream defaultInputStream) {
	this.defaultInputStream = defaultInputStream;
    }

    public OutputStream getDefaultOutputStream() {
	return defaultOutputStream;
    }

    public void setDefaultOutputStream(OutputStream defaultOutputStream) {
	this.defaultOutputStream = defaultOutputStream;
    }

    /**
     * Used when executing command as root user.
     *
     * @param command
     *            Command to execute as the root user
     *
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH connection error
     */
    public void sendAsRoot(String command) throws IOException, InterruptedException, JSchException {
	sendAsRoot(command, 1000);
    }

    /**
     * Used when executing command as root user. This is overloaded as well to allow the user to specify longer timeouts
     * for really slow commands (and/or virtual machines I suppose).
     *
     * @param command
     *            Command to execute
     * @param timeout
     *            The time to wait for output to appear on the console
     *
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH Connection error
     */
    public void sendAsRoot(String command, int timeout) throws IOException, InterruptedException, JSchException {
	doSend("su - -c '" + command + "'", "root", timeout);
    }

    /**
     * Used to read the buffer from the su'd user. You must provide the password for the su'd user here as we need to
     * read the buffer for the password prompt.
     *
     * @return The buffer received as a string
     *
     * @throws IOException
     *             Buffer error
     */
    public String getSuResponse() throws IOException {
	StringBuffer builder = new StringBuffer();
	byte[] tmp = new byte[1024];

	while (true) {
	    int i = inputStream.read(tmp, 0, tmp.length);

	    if (i >= 0) {
		String check = new String(tmp, 0, i);
		builder.append(new String(tmp, 0, i));

		if (check.indexOf("Password: ") != -1) {
		    byte[] passwd = (rootPassword + "\n").getBytes();
		    outputStream.write(passwd);
		    outputStream.flush();
		}
	    } else {
		break;
	    }
	}

	return builder.toString();
    }

    /**
     * Used to read the buffer from the default connection.
     *
     * @return the buffer received as a string
     *
     * @throws IOException
     *             Buffer error
     */
    public String getDefaultResponse() throws IOException {
	StringBuffer builder = new StringBuffer();
	byte[] tmp = new byte[1024];
	try {
	    while (true) {
		// Getting actual response for command execution.
		while (defaultInputStream.available() > 0) {
		    int noChars = defaultInputStream.read(tmp, 0, tmp.length);

		    if (noChars >= 0) {
			String check = new String(tmp, 0, noChars);
			builder.append(check);
			if (check.indexOf("password: ") != -1) {
			    byte[] passwd = (defaultPassword + "\n").getBytes();
			    defaultOutputStream.write(passwd);
			    defaultOutputStream.flush();
			}
			if (check.indexOf(AutomaticsConstants.WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1
				|| check.indexOf(AutomaticsConstants.ALTERNATE_WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1) {
			    String yesKeyword = check.contains(AutomaticsConstants.WARNING_MESSAGE_FOR_NEW_CONNECTION) ? "yes"
				    : "y";
			    byte[] passwd = (yesKeyword + "\n").getBytes();
			    defaultOutputStream.write(passwd);
			    defaultOutputStream.flush();
			}
		    } else {
			break;
		    }
		}
		// Getting Error messages during command execution.
		while (defaultErrorInputStream.available() > 0) {
		    int noChars = defaultErrorInputStream.read(tmp, 0, tmp.length);

		    if (noChars >= 0) {
			builder.append(new String(tmp, 0, noChars));
		    } else {
			break;
		    }
		}
		if (channel.isClosed() || !session.isConnected() || channel.isEOF()) {
		    if (channel.getExitStatus() != 0) {
			LOGGER.error("Command exited with error code " + channel.getExitStatus());
		    }
		    break;
		}
		try {
		    Thread.sleep(100);
		} catch (InterruptedException iex) {
		    LOGGER.debug("100 milli seconds sleep completed checking again for response = "
			    + iex.getLocalizedMessage());
		}
	    }
	} catch (IOException ioex) {
	    LOGGER.error("Exception occured while executing the command - " + ioex.getLocalizedMessage());
	}

	return builder.toString();
    }

    /**
     * Used to read the buffer from the dut. You must provide the password for the sudo'ed user here as we need to read
     * the buffer for the password prompt.
     *
     * @return The buffer received as a string
     *
     * @throws IOException
     *             Buffer error
     */
    public String getSettopResponse() throws IOException {
	return getSettopResponse(AutomaticsConstants.THIRTY_SECONDS);
    }

    /**
     * Used to read the buffer from the dut. You must provide the password for the sudo'ed user here as we need to read
     * the buffer for the password prompt.
     * 
     * @param responseTimeout
     *            Response timeout
     *
     * @return The buffer received as a string
     *
     * @throws IOException
     *             Buffer error
     */
    public String getSettopResponse(long responseTimeout) throws IOException {
	LOGGER.debug("Command Response  at start" + commandResponse.toString());
	Thread responseReadThread = new Thread() {

	    public void run() {
		byte[] tmp = new byte[1024];
		try {
		    while (true) {
			// Getting actual response for command execution.
			String check = AutomaticsConstants.EMPTY_STRING;
			while (defaultInputStream.available() > 0) {
			    int noChars = defaultInputStream.read(tmp, 0, tmp.length);

			    if (noChars >= 0) {
				check = new String(tmp, 0, noChars);
				commandResponse.append(check);
				if (check.indexOf(AutomaticsConstants.SUDO_PASS_WORD_PROMPT + defaultUsername
					+ AutomaticsConstants.COLON_WITH_SPACE) != -1) {
				    String passwordText = (null != crypto) ? crypto.decrypt(defaultPassword)
					    : defaultPassword;
				    byte[] passwd = (passwordText + "\n").getBytes();
				    defaultOutputStream.write(passwd);
				    defaultOutputStream.flush();
				}
				if (check.indexOf(AutomaticsConstants.WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1
					|| check.indexOf(AutomaticsConstants.ALTERNATE_WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1) {
				    String yesKeyword = check
					    .contains(AutomaticsConstants.WARNING_MESSAGE_FOR_NEW_CONNECTION) ? "yes"
					    : "y";
				    byte[] passwd = (yesKeyword + "\n").getBytes();
				    defaultOutputStream.write(passwd);
				    defaultOutputStream.flush();
				}
			    } else {
				break;
			    }
			}
			if (channel instanceof ChannelShell && check.contains("logout")) {
			    break;
			}
			// Getting Error messages during command execution.
			while (defaultErrorInputStream != null && defaultErrorInputStream.available() > 0) {
			    int noChars = defaultErrorInputStream.read(tmp, 0, tmp.length);

			    if (noChars >= 0) {
				commandResponse.append(new String(tmp, 0, noChars));
			    } else {
				break;
			    }
			}
			if (channel.isClosed() || !session.isConnected() || channel.isEOF()) {
			    if (channel.getExitStatus() != 0) {
				LOGGER.debug("Command exited with error code " + channel.getExitStatus());
			    }
			    break;
			}
			try {
			    Thread.sleep(100);
			} catch (InterruptedException iex) {
			    LOGGER.debug("100 milli seconds sleep completed checking again for response = "
				    + iex.getLocalizedMessage());
			}
		    }
		} catch (IOException ioex) {
		    LOGGER.error("Exception occured while executing the command - " + ioex.getLocalizedMessage());
		}

	    }

	};

	try {

	    responseReadThread.start();

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 40 seconds. If no response is
	     * obtained, continue the below lines of code. If this thread is not used, reading response from socket will
	     * block the execution until it gets any data.
	     */
	    responseReadThread.join(responseTimeout);

	    // Interrupt the thread.
	    responseReadThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace(" Read  operation interrupted.");
	}
	LOGGER.debug("Response from reader thread " + commandResponse.toString());
	return commandResponse.toString().replaceAll(AutomaticsConstants.ANSI_REGEX, AutomaticsConstants.EMPTY_STRING)
		.replaceAll(AutomaticsConstants.EMPTY_LINE_REMOVER_REGEX, AutomaticsConstants.EMPTY_STRING);
    }

    /**
     * Copies remote file(s) to the specified location on the local machine. The file is overwritten if already exists.
     *
     * @param remoteFile
     *            Remote file
     * @param localFile
     *            Local destination, can be a file or a directory
     *
     * @throws JSchException
     *             connection error
     * @throws SftpException
     *             sftp error
     */
    public void getFile(String remoteFile, String localFile) throws JSchException, SftpException {
	ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
	sftp.connect();
	sftp.get(remoteFile, localFile);
	sftp.disconnect();
    }

    /**
     * Transfers local file(s) over to the specified remote location. The file is overwritten if already exists.
     *
     * @param localFile
     *            Local file
     * @param remoteFile
     *            Remote destination, can be a file or a directory
     *
     * @throws JSchException
     *             connection error
     * @throws SftpException
     *             sftp error
     */
    public void putFile(String localFile, String remoteFile) throws JSchException, SftpException {
	ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
	sftp.connect();
	sftp.put(localFile, remoteFile);
	sftp.disconnect();
    }

    /**
     * Get the current username.
     *
     * @return The default user name.
     */
    public String getDefaultUsername() {
	return defaultUsername;
    }

    /**
     * Get the default password.
     *
     * @return The default password.
     */
    public String getDefaultPassword() {
	return defaultPassword;
    }

    /**
     * Get the current host name.
     *
     * @return The host name.
     */
    public String getHostName() {
	return hostName;
    }

    /**
     * Method to indicate whether key based authentication is enabled.
     * 
     * @return isKeyAthenticationEnabled
     */
    public boolean isKeyEnabledAccess() {

	boolean isKeyAthenticationEnabled = false;

	if (CommonMethods.isNotNull(privateKeyLocation)) {
	    isKeyAthenticationEnabled = true;
	}

	return isKeyAthenticationEnabled;
    }

    /**
     * Use this to send commands as the user that the initial connection was created for. This is the overloaded version
     * which allows you to pass options that would be provided as a part of command execution (e.g.
     * /usr/bin/wifiSrvMgrTestClient)
     * 
     * @param command
     *            Command to be executed.
     * @param expectStr
     *            String to expected during interactive execution.
     * @param options
     *            Options to be passed during interactive execution.
     * 
     * @return
     * 
     * @throws IOException
     *             Buffer error
     * @throws InterruptedException
     *             Interrupted sleep
     * @throws JSchException
     *             SSH Connection error
     */
    public String send(String command, String expectStr, String[] options) throws IOException, InterruptedException,
	    JSchException {

	StringBuffer responseBuilder = null;
	Channel channel = session.openChannel("shell");

	String[] arrExpectStr = null;
	int expectReturn = 0;
	// Added to accommodate if multiple values to be entered against
	// multiple exepect strings.
	if (expectStr.contains(AutomaticsConstants.COMMA)) {
	    LOGGER.debug("Multiple Expect Strings to be validated");
	    arrExpectStr = expectStr.split(AutomaticsConstants.COMMA);
	    expectStr = arrExpectStr[0];
	}

	ExpectImplementationUtils expectImpl = new ExpectImplementationUtils(channel.getInputStream(),
		channel.getOutputStream());
	channel.connect();
	expectImpl.expectPattern("$");
	LOGGER.debug("Test Expect Command Prompt Validation: " + expectImpl.getMatch());

	expectImpl.send(command);
	expectImpl.expectPattern(expectStr);
	LOGGER.info("Expect String = " + expectStr + ", Expect Match Result = " + expectImpl.getMatch());
	if (expectImpl.isSuccess()) {
	    responseBuilder = new StringBuffer();
	    for (int iCounter = 0; iCounter < options.length; iCounter++) {
		expectImpl.send(options[iCounter]);
		expectReturn = expectImpl.expectPattern(expectStr);
		// Added to accommodate if multiple values to be entered against
		// multiple expect strings.
		if (expectReturn < 0 && arrExpectStr != null) {
		    for (int jCounter = 1; jCounter < arrExpectStr.length; jCounter++) {
			expectReturn = expectImpl.expectPattern(arrExpectStr[jCounter]);
			if (expectReturn >= 0) {
			    break;
			}
		    } // End For Loop jCounter
		} // End if expectReturn < 0 && arrExpectStr != null
		responseBuilder.append(expectImpl.getBefore());
		LOGGER.debug("Expect Result = " + expectImpl.isSuccess());
		LOGGER.debug("Before Expect = " + expectImpl.getBefore());
		LOGGER.debug("Expect Match = " + expectImpl.getMatch());
	    } // End For Loop iCounter
	} // End if (expectImpl.isSuccess)

	LOGGER.info("Expect Response = " + responseBuilder.toString());
	return responseBuilder.toString();
    }

    public String getPrivateKeyLocation() {
	return privateKeyLocation;
    }

    public boolean copy(String remoteFileName, String remoteLocation) {
	boolean copyStatus = doCopy(remoteFileName, remoteLocation);
	return copyStatus;
    }

    /**
     * Copies the file to server
     * 
     * @param remoteFileName
     *            Name of remote file to be created
     * @param remoteLocation
     *            Location where file has to be created
     * @return Returns status of copying
     */
    private boolean doCopy(String remoteFileName, String remoteLocation) {
	LOGGER.debug("Inside doCopy ");
	boolean copyStatus = false;
	try {
	    channel = (ChannelSftp) session.openChannel("sftp");
	    channel.connect();
	    LOGGER.info("Copying file to " + remoteLocation);
	    SftpATTRS attrs = ((ChannelSftp) channel).stat(remoteLocation);
	    try {
		if (attrs != null && attrs.isDir()) {
		    ((ChannelSftp) channel).cd(remoteLocation);
		    InputStream resourceInputStream = (CommonMethods.class.getClassLoader()
			    .getResourceAsStream(remoteFileName));
		    ((ChannelSftp) channel).put(resourceInputStream, remoteFileName);
		    ((ChannelSftp) channel).ls(remoteLocation + remoteFileName);
		    copyStatus = true;
		} else {
		    LOGGER.error("Selenium home folder seems to be missing.Please re-check Selenium configurations manually");
		}
	    } catch (SftpException e1) {
		LOGGER.error("Caught exceptin while trying to copy sftp: [{}:{}]", e1.id, e1.getMessage());
	    }
	} catch (SftpException e) {
	    if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
		LOGGER.error("Selenium home folder seems to be missing.Please re-check Selenium configurations manually");
	    } else {
		LOGGER.error("Unexpected exception during ls files on sftp: [{}:{}]", e.id, e.getMessage());
	    }
	} catch (JSchException e) {
	    LOGGER.error("Unexpected exception during ls files on sftp: {}", e.getMessage());
	}
	LOGGER.debug("Exit doCopy");
	return copyStatus;
    }

    /**
     * Use this to send commands as the user that the initial connection was created for.
     * 
     * @param command
     *            Command to execute
     * @param connectionTimeout
     *            Max wait time for connection
     * @throws IOException
     *             Buffer error
     * @throws JSchException
     *             SSH Connection error
     */
    public void sendCommand(String command, int connectionTimeout) throws IOException, JSchException {
	int retry = 5;
	channel = session.openChannel("exec");
	((ChannelExec) channel).setPty(true);
	// ((ChannelExec) channel).setPtyType("dumb");
	((ChannelExec) channel).setCommand(command);
	defaultInputStream = channel.getInputStream();
	defaultErrorInputStream = ((ChannelExec) channel).getErrStream();
	defaultOutputStream = channel.getOutputStream();
	channel.connect(connectionTimeout);
	while (retry > 0) {
	    if (!channel.isConnected()) {
		try {
		    LOGGER.debug(
			    "<--- Sleeping for 2 seconds as channel is not yet connected ---> RETRY COUNTDOWN: {} ",
			    retry);
		    Thread.sleep(AutomaticsConstants.TWO_SECONDS);
		} catch (InterruptedException e) {
		    LOGGER.debug("TWO seconds of Wait time completed, try to get the response.");
		}
	    } else {
		break;
	    }
	    retry--;
	}
    }

    /**
     * Used to flush the old content in String buffer
     *
     */
    public void flushOldResponse() {

	if (null != commandResponse) {
	    commandResponse.delete(0, commandResponse.length());
	    commandResponse.setLength(0);
	}
    }

    public void sendToShell(String[] commandList) throws IOException, InterruptedException, JSchException {
	channel = session.openChannel("shell");
	channel.connect(AutomaticsConstants.THIRTY_SECONDS_INT);
	defaultInputStream = channel.getInputStream();
	// defaultErrorInputStream = channel.getInputStream();
	PrintStream out = new PrintStream(channel.getOutputStream());
	for (String eachCommand : commandList) {
	    out.print(eachCommand + "\n");
	}
	out.print("exit" + "\n");
	out.flush();
    }

    public void bufferResponse() throws IOException {

	Thread responseReadThread = new Thread() {
	    public void run() {
		try {
		    // LOGGER.debug("Command Response  for executing command {} start", commandToExecute);
		    byte[] tmp = new byte[1024];
		    try {
			while (true) {
			    // Getting actual response for command execution.
			    String check = AutomaticsConstants.EMPTY_STRING;
			    while (defaultInputStream.available() > 0) {
				int noChars = defaultInputStream.read(tmp, 0, tmp.length);

				if (noChars >= 0) {
				    check = new String(tmp, 0, noChars);
				    // commandResponse.append(check);
				    LOGGER.info(check);
				    if (check.indexOf(AutomaticsConstants.SUDO_PASS_WORD_PROMPT + defaultUsername
					    + AutomaticsConstants.COLON_WITH_SPACE) != -1) {
					String passwordText = (null != crypto) ? crypto.decrypt(defaultPassword)
						: defaultPassword;
					byte[] passwd = (passwordText + "\n").getBytes();
					defaultOutputStream.write(passwd);
					defaultOutputStream.flush();
				    }
				    if (check.indexOf(AutomaticsConstants.WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1
					    || check.indexOf(AutomaticsConstants.ALTERNATE_WARNING_MESSAGE_FOR_NEW_CONNECTION) != -1) {
					byte[] passwd = ("yes" + "\n").getBytes();
					defaultOutputStream.write(passwd);
					defaultOutputStream.flush();
				    }
				} else {
				    break;
				}
			    }
			    if (channel instanceof ChannelShell && check.contains("logout")) {
				break;
			    }
			    // Getting Error messages during command execution.
			    while (defaultErrorInputStream != null && defaultErrorInputStream.available() > 0) {
				int noChars = defaultErrorInputStream.read(tmp, 0, tmp.length);

				if (noChars >= 0) {
				    // commandResponse.append(new String(tmp, 0, noChars));
				    LOGGER.info(new String(tmp, 0, noChars));
				} else {
				    break;
				}
			    }
			    if (channel.isClosed() || !session.isConnected() || channel.isEOF()) {
				if (channel.getExitStatus() != 0) {
				    LOGGER.debug("Command exited with error code " + channel.getExitStatus());
				}
				break;
			    }
			    try {
				Thread.sleep(100);
			    } catch (InterruptedException iex) {
				LOGGER.info("100 milli seconds sleep completed checking again for response = "
					+ iex.getLocalizedMessage());
			    }
			}
		    } catch (IOException ioex) {
			LOGGER.error("Exception occured while executing the command - " + ioex.getLocalizedMessage());
		    }

		} catch (Throwable t) {
		    LOGGER.error("Exception occured while executing the command - " + t.getStackTrace());
		}
	    }

	};

	try {
	    responseReadThread.setName("CommandResponseReader");
	    responseReadThread.start();

	    /*
	     * setting a timeout for this thread. That is, wait for response only for 40 seconds. If no response is
	     * obtained, continue the below lines of code. If this thread is not used, reading response from socket will
	     * block the execution until it gets any data.
	     */
	    responseReadThread.join();

	    // Interrupt the thread.
	    responseReadThread.interrupt();

	} catch (InterruptedException e) {
	    LOGGER.trace(" Read  operation interrupted.");
	}
	LOGGER.debug("Response from reader thread " + commandResponse.toString());
	// return commandResponse.toString().replaceAll(AutomaticsConstants.ANSI_REGEX,
	// AutomaticsConstants.EMPTY_STRING)
	// .replaceAll(AutomaticsConstants.EMPTY_LINE_REMOVER_REGEX, AutomaticsConstants.EMPTY_STRING);
    }

    public boolean isConnected() {
	boolean isConnected = true;
	if (!this.session.isConnected() || !this.isDefaultChannelConnected()) {
	    isConnected = false;
	}
	return isConnected;
    }
}
