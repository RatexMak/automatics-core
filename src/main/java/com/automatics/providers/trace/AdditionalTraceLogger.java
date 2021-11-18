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
package com.automatics.providers.trace;

import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.device.Dut;
import com.automatics.utils.AutomaticsPropertyUtility;

public class AdditionalTraceLogger {
    String traceName;

    String model;

    String utilityToUse;

    String commandToExecute;

    public boolean isEnabled = false;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AdditionalTraceLogger.class);

    public AdditionalTraceLogger() {

    }

    public AdditionalTraceLogger(Dut settopObj) {
	try {
	    String additionaTraceSupport = AutomaticsPropertyUtility.getProperty("additional.trace.support"); // <model>-<consolename>-<utilitytouse>-<tracecommand>
	    LOGGER.info("Additional trace support : " + additionaTraceSupport);
	    LOGGER.info("settopObj.getModel() = " + settopObj.getModel());
	    String[] parametrs = additionaTraceSupport.split(AutomaticsConstants.COMMA);
	    if (parametrs.length == 4) {
		model = parametrs[0];
		traceName = parametrs[1];
		utilityToUse = parametrs[2];
		commandToExecute = parametrs[3];
		if (settopObj.getModel().equals(model)) {
		    LOGGER.info("Assigning isENabled to true");
		    isEnabled = true;
		}
	    }
	} catch (Exception e) {
	    LOGGER.info("Additional logging requirement if any enabled will be skipped due to configuration issue");
	}

    }

}
