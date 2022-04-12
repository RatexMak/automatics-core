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

import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.providers.trace.AbstractTraceProviderImpl;
import com.automatics.providers.trace.TraceProvider;

/**
 * @author RCHATH001C
 * 
 *         Generic client to communicate with remote TTS Server
 *
 */
@ClientEndpoint
public class WebSocketClient {

    static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);
    static final String TRACE_PREFIX = "[ SOCKET CLIENT ] :";

    static final String CONNECTION_SUCCESS = "Both TTC and you are ready to exchange the messages";
    StringBuffer response = new StringBuffer();

    /**
     * Session object
     */
    Session userSession = null;
    /**
     * Event handler for messages
     */
    private MessageHandler messageHandler;

    /**
     * Singleton websocket client
     */
    private static WebSocketClient socketConnection = null;
    /**
     * Type of server instantiated for 'socketConnection'
     */
    private WEBSOCKET_SERVER serverInstanceType;

    /**
     * Trace logger object of dut for logging responses to trace
     */
    private Logger settopTraceLogger = null;

    /**
     * boolean attribute to enable/disable trace logging
     */
    private boolean isTraceLogging = false;

    /**
     * @author RCHATH001C
     * 
     *         Message handler interface to be impleneted for handling message events from socket connection
     *
     */
    public interface MessageHandler {
	public String handleMessage(String message);
    }

    /**
     * @author RCHATH001C Enum to indicate type of Server.
     *
     */
    public enum WEBSOCKET_SERVER {
	THUNDER,
	OTHER;
    }

    /**
     * @param uri
     *            - Server URI
     * 
     *            Constructor method establishes connection and injects new client object
     */
    private WebSocketClient(URI uri) {
	try {
	    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	    container.connectToServer(this, uri);
	} catch (Exception e) {
	    LOGGER.error("Connection unsuccessful : " + e.getMessage());
	}
    }

    /**
     * @param inputString
     *            - Input string to be passed during connection instantiation In the case of thunder server, mac address
     *            of STb needs to be passed to get valid connection
     * @param server
     *            - Server type
     * @return WebSocketClient is returned on success.else null
     */
    public static synchronized WebSocketClient getWebSocketClient(String inputString, WEBSOCKET_SERVER server) {
	try {
	    if (socketConnection == null
		    || (socketConnection != null && !socketConnection.serverInstanceType.equals(server))) {
		if (server.equals(WEBSOCKET_SERVER.THUNDER)) {
		    if (CommonMethods.isNotNull(inputString)) {
			String serverUrl = AutomaticsPropertyUtility.getProperty("socket.server.thunder.url");
			LOGGER.info("Connecting to server : " + serverUrl);
			if (CommonMethods.isNotNull(serverUrl))
			    socketConnection = new WebSocketClient(new URI(serverUrl));
			if (socketConnection != null) {
			    addCustomMessageHandler();
			    String result = socketConnection.sendMessage(inputString.toUpperCase(),
				    AutomaticsConstants.FIVE_SECONDS);
			    if (CommonMethods.isNull(result)
				    || (CommonMethods.isNotNull(result) && !result.contains(CONNECTION_SUCCESS))) {
				socketConnection = null;
				LOGGER.error("Connection unsuccessful : " + result);
			    }
			}
		    } else {
			LOGGER.error("Cannot create thunder instance without device MAC !!");
			socketConnection = null;
		    }
		} else {
		    // String serverUrl = STBPropertyUtility.getProperty("socket.server.url",
		    // AutomaticsConstants.EMPTY_STRING);
		    String serverUrl = inputString;
		    LOGGER.info("Connecting to server : " + serverUrl);
		    if (CommonMethods.isNotNull(serverUrl) && socketConnection == null) {
			socketConnection = new WebSocketClient(new URI(serverUrl));
			addCustomMessageHandler();
		    } else {
			LOGGER.error("No server configured for connection!!!");
		    }
		}
	    }
	} catch (URISyntaxException e) {
	    LOGGER.error("Invalid Server URL!!!");
	}
	return socketConnection;
    }

    /**
     * Method that add message handler
     */
    private static void addCustomMessageHandler() {
	socketConnection.addMessageHandler(new WebSocketClient.MessageHandler() {
	    public String handleMessage(String message) {
		return message;
	    }
	});
    }

    /**
     * @param string
     *            String to send to server is passed Send the message to TTS server
     */
    private void sendMessage(String string) {
	this.userSession.getAsyncRemote().sendText(string);

    }

    /**
     * @param message
     *            - Message/JSson to send
     * @param waitTimeInSeconds
     *            - Time to wait for complete response
     * @return Returns the response as String
     * 
     *         Send messages and wait for sepcified timeout so that on message event are completely captured
     */
    public String sendMessage(String message, long waitTimeInSeconds) {
	response.setLength(0);
	sendMessage(message);
	AutomaticsUtils.sleep(waitTimeInSeconds);
	return String.valueOf(response);
    }

    /**
     * @param dut
     *            - Dut object
     * @param message
     *            - Message/JSson to send
     * @param waitTimeInSeconds
     *            - Time to wait for complete response
     * 
     *            Send messages and wait for sepcified timeout so that on message event are completely captured.Captured
     *            output will be logged to dut trace.
     */
    public void sendMessage(Dut dut, String message, long waitTimeInSeconds) {
	if (settopTraceLogger == null) {
	    TraceProvider traceProvider = dut.getTrace();
	    if (traceProvider instanceof AbstractTraceProviderImpl) {
		AbstractTraceProviderImpl sshTrace = (AbstractTraceProviderImpl) traceProvider;

		settopTraceLogger = sshTrace.getSettopTraceLogger();
		isTraceLogging = true;
	    } else {
		LOGGER.error("Unable to log in trace");
		isTraceLogging = false;
	    }
	}
	sendMessage(message);
	AutomaticsUtils.sleep(waitTimeInSeconds);
    }

    private void addMessageHandler(MessageHandler messageHandler2) {
	this.messageHandler = messageHandler2;

    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession
     *            the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
	LOGGER.debug("<<<< Opening socket sesssion with server >>>>");
	this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
	this.userSession = null;
	LOGGER.debug("Close reason <<<< " + reason.getReasonPhrase() + " " + reason.getCloseCode().getCode() + " >>>>");
    }

    @OnMessage
    public void onMessage(String message) {
	if (this.messageHandler != null) {
	    if (isTraceLogging) {
		settopTraceLogger.trace(TRACE_PREFIX + message);
	    } else {
		response.append(this.messageHandler.handleMessage(message) + System.lineSeparator());
	    }
	}
    }

    @OnError
    public void error(Session session, Throwable t) {
	LOGGER.error("<<<< Error during socket communication session >>>>");
	LOGGER.error("<<<< " + t.toString() + " >>>>");
    }
}
