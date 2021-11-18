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
package com.automatics.cron.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.enums.SshMechanism;
import com.automatics.providers.connection.DeviceConnectionProvider;
import com.automatics.utils.CommonMethods;
import com.automatics.utils.AutomaticsPropertyUtility;
import com.automatics.utils.TestUtils;
import com.automatics.utils.BeanUtils;

/**
 * @author rohinic
 *
 *         This class holds the cron job which cleans any unused allocation of ports in reverse ssh jump servers
 *         periodically
 */
public class ReverseSshCron implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseSshCron.class);

    private static final String NO_SUCH_FILE = "No such file";

    private static final String STAT_CONNECTING_FILES = "stat *_Connecting -c %n-%Y";

    private static final String STAT_CONNECTED_FILES = "stat *_Connected -c %n-%Y";

    private static final String LINE_SPLITTER = "\\r?\\n";

    private static DeviceConnectionProvider connectionProvider;

    /**
     * Configures the trigger and schedules it
     * 
     * @param jobClass
     * @param intervalInMinutes
     */
    public static void startCron(Class jobClass, int intervalInMinutes) {
	SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	// Get schedular
	Scheduler scheduler;
	try {
	    LOGGER.debug("Starting cron for protCleaning");
	    connectionProvider = BeanUtils.getDeviceConnetionProvider();
	    scheduler = schedulerFactory.getScheduler();
	    JobDetail jobDetail = JobBuilder.newJob(jobClass)
		    .withIdentity("portCleaner_ " + jobClass.getSimpleName(), "ReverseSsh").build();
	    // Associate Trigger to the Job
	    Trigger trigger = TriggerBuilder
		    .newTrigger()
		    .withIdentity("myTrigger", "myTriggerGroup")
		    .withSchedule(
			    SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(intervalInMinutes)
				    .repeatForever()).build();

	    // Pass JobDetail and trigger dependencies to scheduler
	    scheduler.scheduleJob(jobDetail, trigger);
	    // Start scheduler
	    TestUtils.setReverseSshSchedulerForTheJob(scheduler);
	    scheduler.start();

	    LOGGER.debug(trigger.getStartTime().toString());
	} catch (SchedulerException e) {
	    LOGGER.error("Exception during trigger " + e.getMessage());
	}
    }

    /*
     * The action performed during trigger goes here (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	List<String> reverseSshServers = new ArrayList<String>();
	List<String> portsToBeReleased = new ArrayList<String>();
	String reverseSshJumpServer = AutomaticsPropertyUtility
		.getProperty(AutomaticsConstants.PROPERTY_REVERSE_SSH_HOST_SERVER);
	if (CommonMethods.isNotNull(reverseSshJumpServer)) {
	    reverseSshServers.addAll(Arrays.asList(reverseSshJumpServer.split(",")));
	    long tenMinutes = AutomaticsConstants.ONE_MINUTE * 10; // millis
	    long thirtyMinutes = AutomaticsConstants.ONE_MINUTE * 15 * 2; // millis
	    for (String eachHost : reverseSshServers) {
		try {
		    LOGGER.debug("Cleaning host : " + eachHost);
		    portsToBeReleased.addAll(getPortsToBeReleased(eachHost, STAT_CONNECTING_FILES, tenMinutes));
		    portsToBeReleased.addAll(getPortsToBeReleased(eachHost, STAT_CONNECTED_FILES, thirtyMinutes));
		    if (portsToBeReleased != null && portsToBeReleased.size() > 0) {
			LOGGER.debug("Removing unsed ports from " + eachHost);
			StringBuffer command = new StringBuffer(AutomaticsConstants.COMMAND_REMOVE);
			for (String port : portsToBeReleased) {
			    command.append(port).append(AutomaticsConstants.SPACE);
			}
			executeCommandAndDisconnectConnection(eachHost, command.toString());
		    } else {
			LOGGER.debug("No ports to release");
		    }

		} catch (Exception e) {
		    LOGGER.error("Error while executing clean up job : " + e.getMessage());
		}
	    }
	}
    }

    /**
     * Method fetched ports to be released by comparing the last modified timestamp and current time. For connecting
     * files, the time of expirey is set as 10 and for connected files, it is 30 minutes
     * 
     * @param host
     *            Reverse ssh host server
     * @param command
     *            Command to be executed to check the last modified time of files
     * @param expiryTime
     *            Expiry time for that particular file
     * @return
     */
    private List<String> getPortsToBeReleased(String host, String command, long expiryTime) {
	List<String> portsToBeReelased = new ArrayList<String>();
	String response = executeCommandAndDisconnectConnection(host, command);
	if (CommonMethods.isNotNull(response) && !response.contains(NO_SUCH_FILE)) {
	    List<String> portsConnecting = Arrays.asList(response.split(LINE_SPLITTER));
	    for (String eachPort : portsConnecting) {
		long lastAccessTime = Long.parseLong(eachPort.split(AutomaticsConstants.HYPHEN)[1]) * 1000;
		Date currentDate = new Date();
		long currentTime = currentDate.getTime();
		LOGGER.debug("Current time from clean job :" + currentTime + " " + currentDate);
		LOGGER.debug("Last Access time of " + eachPort + " " + new Date(lastAccessTime));
		LOGGER.debug("Current Time time of " + eachPort + " " + new Date(currentTime));
		if (currentTime - lastAccessTime > expiryTime) {
		    portsToBeReelased.add(eachPort.substring(0, eachPort.indexOf(AutomaticsConstants.HYPHEN)));
		}
	    }
	}
	return portsToBeReelased;
    }

    /**
     * Method executes the command and disconnects the session after use
     * 
     * @param host
     *            Reverse ssh host
     * @param command
     *            Command to be excuted
     * @return Returns the response
     */
    private String executeCommandAndDisconnectConnection(String host, String command) {
	String response = null;
	if (null == connectionProvider) {
	    connectionProvider = BeanUtils.getDeviceConnetionProvider();
	}
	if (null == connectionProvider) {
	    LOGGER.error("ConnectionProvider is null. Could not perform Reverse Ssh port clean up");
	} else {
	    response = connectionProvider.execute(host, command, AutomaticsConstants.THIRTY_SECONDS,
		    SshMechanism.SSH.name());
	}
	LOGGER.debug("Reverse ssh cleanup response: {}", response);
	return response;

    }
}
