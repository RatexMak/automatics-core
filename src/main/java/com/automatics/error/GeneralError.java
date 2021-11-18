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

package com.automatics.error;

import java.io.IOException;
import java.util.Properties;

import com.automatics.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.automatics.error.ErrorType.*;

/**
 * General application error categorized.
 */
public enum GeneralError implements IError {

    OCR_COMPARISON_FAILURE(OCR) {

	@Override
	public String getCode() {
	    return "ER-09";
	}
    },

    IMAGE_COMPARISON_FAILURE(IC) {

	@Override
	public String getCode() {
	    return "ER-07";
	}

    },

    TRACE_COMPARISON_FAILURE(TRACE) {

	@Override
	public String getCode() {
	    return "ER-12";
	}

    },

    SNMP_COMPARISON_FAILURE(SNMP) {

	@Override
	public String getCode() {
	    return "ER-03";
	}

    },

    KEY_SEND_FAILURE(IR_KEY) {

	@Override
	public String getCode() {
	    return "ER-13";
	}

    },   

    TELNET_CONNECTION_FAILURE(CONNECTION) {

	@Override
	public String getCode() {
	    return "ER-01";
	}

    },

    IMAGE_RESOURCE_NOT_FOUND(RESOURCE_NOT_FOUND) {

	@Override
	public String getCode() {
	    return "ER-10";
	}

    },

    PROVIDED_RESOURCE_NOT_FOUND(RESOURCE_NOT_FOUND) {

	@Override
	public String getCode() {
	    return "ER-16";
	}

    },

    CONSOLE_OUTPUT_COMPARISON_FAILURE(CONSOLE_OUTPUT) {

	@Override
	public String getCode() {
	    return "ER-14";
	}

    },

    /** Error case in which the OCR region specified is not found on the XML file. */
    OCR_FAILURE(OCR) {

	@Override
	public String getCode() {
	    return "ER-11";
	}

    },

    /** Error case in which the image region specified is not found on the XML file. */
    IMAGE_COMPARE_FAILURE(IC) {

	@Override
	public String getCode() {
	    return "ER-15";
	}

    },

    /** Error case in which tuning fails. */
    TUNE_FAILURE(IR_KEY) {

	@Override
	public String getCode() {
	    return "ER-06";
	}

    },

    /** Error case in which resource reading operation fails. */
    FAILED_RESOURCE_READ(RESOURCE_NOT_READ) {

	@Override
	public String getCode() {
	    return "ER-16";
	}

    },    

    /** Error case where JSON parsing fails. */
    INCORRECT_JSON(JSON) {

	@Override
	public String getCode() {
	    return "ER-20";
	}
    },

    /** Error case where T2P command fails to execute. */
    FAILED_T2P(T2P) {

	@Override
	public String getCode() {
	    return "ER-21";
	}
    },

    /** Error case where configuration fails. */
    FAILED_CONFIGURATION(CONFIGURATION) {

	@Override
	public String getCode() {
	    return "ER-22";
	}
    },

    /** Error case while fail to setup a SSH connection to the server. */
    SSH_CONNECTION_FAILURE(CONNECTION) {

	@Override
	public String getCode() {
	    return "ER-23";
	}
    },

    /** Error case while fail to setup a pre condition. */
    PRE_CONDITION_FAILURE(PRE_CONDITION) {

	@Override
	public String getCode() {
	    return "ER-24";
	}
    },

    /** Error case while key not found in the properties resource file. */
    PROP_KEY_NOT_FOUND(RESOURCE_KEY_NOT_FOUND) {

	@Override
	public String getCode() {
	    return "ER-25";
	}
    },

    /** Error case while accessing server machine. */
    SECURITY_ISSUE(SECURITY) {

	@Override
	public String getCode() {
	    return "ER-26";
	}
    },

    TR_069_ACS_COMMINICATION_ERROR(ACS_COMMINICATION_ERROR) {

	@Override
	public String getCode() {
	    return "ER-27";
	}
    },

    TR_069_WEB_PA_COMMINICATION_ERROR(WEB_PA_COMMINICATION_ERROR) {

	@Override
	public String getCode() {
	    return "ER-28";
	}
    },
    ACTIVE_DEVICES_GATEWAY_LOCKED(PROVIDER_ALLOCATION) {

	@Override
	public String getCode() {
	    return "ER-29";
	}
    },
    ACTIVE_DEVICES_CLIENT_LOCKED(PROVIDER_ALLOCATION) {

	@Override
	public String getCode() {
	    return "ER-30";
	}
    },
    ACTIVE_DEVICES_CLIENT_SETTOPS_NOT_RECEIVED(PROVIDER_ALLOCATION) {

	@Override
	public String getCode() {
	    return "ER-31";
	}
    };

    /** Logger for general error. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralError.class);

    /** The error code message details. */
    private static Properties errorProperties = null;

    /** Property file for error code. */
    private static final String FILE_PROP_ERROR_CODE = "error-codes.props";

    /** Error type. */
    private ErrorType errorType = null;

    private GeneralError(ErrorType errorType) {
	this.errorType = errorType;
    }

    /**
     * Error message to be displayed.
     */
    @Override
    public String getMessage() {
	return getType().toString() + getProperties().getProperty(getCode());
    }

    /**
     * Type of error category.
     */
    @Override
    public ErrorType getType() {
	return errorType;
    }

    /**
     * Load the error code property file and return the error properties.
     *
     * @return error codes with messages.
     */
    private static synchronized Properties getProperties() {

	if (null == errorProperties) {

	    try {
		errorProperties = FileUtils.getPropertiesFromResource(FILE_PROP_ERROR_CODE);
	    } catch (IOException ioe) {
		LOGGER.error(String.format("Failed to read the property file %s.", FILE_PROP_ERROR_CODE), ioe);
	    }
	}

	return errorProperties;
    }

    /**
     * Return the classified error type with message.
     */
    @Override
    public String toString() {
	return getType().toString() + getMessage();
    }

}
