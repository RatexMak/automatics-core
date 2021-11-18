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
package com.automatics.providers.issuemanagement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.IssueManagementConstants;
import com.automatics.http.ServerCommunicator;
import com.automatics.http.ServerResponse;
import com.automatics.providers.issuemanagement.objects.Jaws;
import com.automatics.tap.AutomaticsTapApi;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.TestUtils;
import com.google.gson.Gson;

/**
 * Class to raise issue tickets for the failed test steps.
 * 
 * @author surajmathew
 *
 */
public class IssueController {

    private boolean isIssueManagementEnabled = false;

    private String issueManagementUrl = null;

    /** SLF4j logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(IssueController.class);

    AutomaticsTapApi tapApi = AutomaticsTapApi.getInstance();

    private static final String CONTENT_TYPE_JSON = "application/json";

    /** Url prefix for creating a ticket */
    private static final String CREATE_TICKET_URL = "/create";

    /** Url prefix for commenting a ticket */
    private static final String COMMENT_TICKET_URL = "/comment";

    /** Url prefix for attaching a file to the ticket */
    private static final String ATTACH_TICKET_URL = "/attach?ticketNumber=";

    /** Url prefix for getting ticket creation status */
    public static final String ISSUE_CREATE_TICKET_STATUS_ENDPOINT = "/request_details/jawsReqId/";

    private static final String ATTACHMENT_NAME_KEYWORD = "AttachmentName";

    /** Holds the Issue management application host url property. */
    public static final String ISSUE_URL_PROPERTY = "issue.app.host.url";

    /** Holds the Issue management application user name property. */
    public static final String PROPERTY_ISSUE_USERNAME = "issue.app.username";

    /** Holds the Issue management application password property. */
    public static final String PROPERTY_ISSUE_PASSWORD = "issue.app.password";

    /** Holds the Issue management application QT ticket assignee property. */
    public static final String PROPERTY_ISSUE_QT_ASSIGNEE = "issue.QT.assignee";

    /** Holds the Issue management application 1hour ticket assignee property. */
    public static final String PROPERTY_ISSUE_1HOUR_ASSIGNEE = "issue.1hour.assignee";

    /** Holds the Issue management application 4hour ticket assignee property. */
    public static final String PROPERTY_ISSUE_4HOUR_ASSIGNEE = "issue.4hour.assignee";

    /** Holds the Issue management application 2days ticket assignee property. */
    public static final String PROPERTY_ISSUE_2DAYS_ASSIGNEE = "issue.2days.assignee";

    /** Holds the Issue management application unmapped (aed) ticket assignee property. */
    public static final String PROPERTY_ISSUE_DEFECT_AUTOMATION_ASSIGNEE = "issue.defectautomation.assignee";

    /** Holds the Issue management application project key property. */
    public static final String PROPERTY_ISSUE_PROJECT_KEY = "issue.app.project.key";

    /** Holds the Issue management application project name property. */
    public static final String PROPERTY_ISSUE_PROJECT_NAME = "issue.project.name";

    /** Holds the Issue management application ticket labels property. */
    public static final String PROPERTY_ISSUE_TICKET_LABELS = "issue.ticket.labels";

    /** Holds the Issue management application ticket priority property. */
    public static final String PROPERTY_ISSUE_TICKET_PRIORITY = "issue.ticket.priority";

    /** Holds the Issue management application watchers list property. */
    public static final String PROPERTY_ISSUE_TICKET_WATCHERS = "issue.ticket.watchers";

    /**
     * Creates instance of IssueTicketController
     */
    public IssueController() {
	isIssueManagementEnabled = TestUtils.isAutomatedIssueManagementEnabled();
	issueManagementUrl = AutomaticsPropertyUtility
		.getProperty(IssueManagementConstants.PROPERTY_ISSUE_MANAGEMENT_SERVICE_URL);
	if (CommonMethods.isNull(issueManagementUrl)) {
	    LOGGER.error("Issue management url not configured in Automatics Properties");
	}
    }

    /**
     * Method to create/update the issue ticket
     * 
     * @param issueDetails
     * @return issueTicket
     */
    public String createIssueTicket(IssueCreateTicketRequest issueRequest) {

	String issueTicket = null;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	Map<String, String> headers = null;

	if (isIssueManagementEnabled) {

	    if (CommonMethods.isNotNull(issueManagementUrl)) {
		try {

		    // Send request for ticket creation
		    Gson gson = new Gson();
		    String jsonString = gson.toJson(issueRequest);

		    serverCommunicator = new ServerCommunicator(LOGGER);
		    headers = new HashMap<String, String>();
		    headers.put("Content-Type", CONTENT_TYPE_JSON);
		    String url = issueManagementUrl + CREATE_TICKET_URL;

		    serverResponse = serverCommunicator.postDataToServer(url, jsonString, "POST", 200000, headers);
		    if (serverResponse != null && serverResponse.getResponseCode() == 200) {
			String response = serverResponse.getResponseStatus();
			LOGGER.info("Issue create response: {}", response);
			JSONObject jawsJsonResponse = new JSONObject(response);
			issueTicket = getTicketDetails(jawsJsonResponse);

		    } else {
			LOGGER.info("Failed to get response for create/update the Issue Tickets. Unexpected response obtained - "
				+ serverResponse.getResponseStatus());
		    }

		} catch (Exception e) {
		    LOGGER.error("Exception occurred " + e.getMessage());

		}
	    }
	}

	return issueTicket;
    }

    /**
     * 
     * Method queries the status of jaws' operation to create ticket Based on request id, the status is fetched
     * 
     * @param jawsJsonResponse
     *            Response from jaws which has reqId
     * @return Ticket number
     */
    private String getTicketDetails(JSONObject jawsJsonResponse) {
	String status;
	String ticket = AutomaticsConstants.EMPTY_STRING;
	try {
	    status = (String) jawsJsonResponse.get("status");
	    if (CommonMethods.isNotNull(status) && "STARTED".equals(status)) {
		String reqId = jawsJsonResponse.get("jawsReqId").toString();
		ticket = getIssueTicketId(reqId);
	    }
	} catch (JSONException e) {
	    LOGGER.error("Exception occurred :" + e.getMessage());
	} catch (Exception e) {
	    LOGGER.error("Exception occurred :" + e.getMessage());
	}
	return ticket;
    }

    /**
     * 
     * Method queries the status of jaws' operation to create ticket Based on request id, the status is fetched
     * 
     * @param jawsJsonResponse
     *            Response from jaws which has reqId
     * @return Ticket number
     */
    public String getIssueTicketId(String requestId) {

	String ticket = AutomaticsConstants.EMPTY_STRING;
	try {

	    String url = issueManagementUrl + ISSUE_CREATE_TICKET_STATUS_ENDPOINT + requestId;
	    int retry = 6;
	    while (retry > 0) {
		ServerCommunicator serverCommunicator = new ServerCommunicator(LOGGER);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", CONTENT_TYPE_JSON);
		ServerResponse serverResponse = serverCommunicator.postDataToServer(url, "", "GET", 200000, headers);
		if (serverResponse != null && serverResponse.getResponseCode() == 200) {
		    LOGGER.info("Issue ticket details: {}", serverResponse.getResponseStatus());
		    JSONObject issueTicketResponse = new JSONObject(serverResponse.getResponseStatus());
		    String ticketCreationStatus = issueTicketResponse.get("ticketCreationStatus").toString();
		    if ("COMPLETED".equals(ticketCreationStatus)) {
			ticket = issueTicketResponse.get("ticketCreated").toString();
			break;
		    } else if ("FAILED".equals(ticketCreationStatus)) {
			break;
		    }
		    try {
			Thread.sleep(AutomaticsConstants.TEN_SECONDS);
		    } catch (InterruptedException e) {
			LOGGER.error("Exception occurred", e);
		    }

		} else {
		    LOGGER.error("Ticket creation failed .Response from jaws {} -------> "
			    + serverResponse.getResponseCode());
		}
		retry--;
	    }

	} catch (JSONException e) {
	    LOGGER.error("Exception occurred :" + e.getMessage());
	} catch (Exception e) {
	    LOGGER.error("Exception occurred :" + e.getMessage());
	}
	return ticket;
    }

    /**
     * Method to attach the file to the ticket
     * 
     * @param ticketNumber
     * @param attachmentFile
     * @return
     */
    public boolean attachFile(String ticketNumber, File attachmentFile, Jaws jawsDetails) {

	boolean isAttached = false;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	Map<String, String> headers = null;

	if (isIssueManagementEnabled) {

	    if (CommonMethods.isNotNull(issueManagementUrl)) {

		headers = new HashMap<String, String>();
		headers.put(ATTACHMENT_NAME_KEYWORD, attachmentFile.getName());

		serverCommunicator = new ServerCommunicator(LOGGER);

		String url = issueManagementUrl + ATTACH_TICKET_URL + ticketNumber;

		// Send the request
		serverResponse = serverCommunicator.postFileToServer(url, attachmentFile, "POST", 200000, headers);

		if (serverResponse != null && serverResponse.getResponseCode() == 200) {
		    isAttached = true;
		    LOGGER.info("File attached to the Jira Ticket - " + ticketNumber);
		} else {
		    LOGGER.info("Failed to get response for attaching file to the Jira Ticket. Unexpected response obtained - "
			    + serverResponse.getResponseStatus());
		}
	    } else {
		LOGGER.info("Issue Management As Web Service (Issue Management) url is not specified.");
	    }
	} else {
	    LOGGER.info("Issue Management As Web Service is disabled.");
	}

	return isAttached;
    }

    /**
     * Method to comment the ticket
     * 
     * @param ticketNumber
     * @param commentMessage
     * @return isCommentAdded
     */
    public boolean addComment(String ticketNumber, String commentMessage) {

	boolean isCommentAdded = false;

	ServerCommunicator serverCommunicator = null;
	ServerResponse serverResponse = null;

	Map<String, String> headers = null;

	if (isIssueManagementEnabled) {

	    if (CommonMethods.isNotNull(issueManagementUrl)) {

		headers = new HashMap<String, String>();
		headers.put("Content-Type", CONTENT_TYPE_JSON);

		serverCommunicator = new ServerCommunicator(LOGGER);

		String url = issueManagementUrl + COMMENT_TICKET_URL;

		JSONObject jsonInput = new JSONObject();
		try {
		    jsonInput.put("ticket_number", ticketNumber);
		    jsonInput.put("comments", commentMessage);
		} catch (Exception e) {
		    LOGGER.error(e.getMessage());
		}

		// Send the request
		serverResponse = serverCommunicator
			.postDataToServer(url, jsonInput.toString(), "POST", 200000, headers);

		if (serverResponse != null && serverResponse.getResponseCode() == 200) {
		    isCommentAdded = true;
		    LOGGER.info("Comment added to the Issue Ticket - " + ticketNumber);
		} else {
		    LOGGER.info("Failed to get response for adding comment to the Issue Ticket. Unexpected response obtained - "
			    + serverResponse.getResponseStatus());
		}
	    } else {
		LOGGER.info("Issue Management As Web Service url is not specified.");
	    }
	} else {
	    LOGGER.info("Issue Management As Web Service is disabled.");
	}

	return isCommentAdded;
    }

    /**
     * @return the isIssueManagementEnabled
     */
    public boolean isIssueManagementEnabled() {
	return isIssueManagementEnabled;
    }

    /**
     * @param isIssueManagementEnabled
     *            the isIssueManagementEnabled to set
     */
    public void setIssueManagementEnabled(boolean isIssueManagementEnabled) {
	this.isIssueManagementEnabled = isIssueManagementEnabled;
    }
}
