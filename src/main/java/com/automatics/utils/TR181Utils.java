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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.WebPaConstants;
import com.automatics.enums.DmcliDataType;
import com.automatics.enums.TR181AccessMethods;
import com.automatics.enums.TR181DataType;
import com.automatics.enums.TR69DataType;
import com.automatics.enums.WebPaDataType;
import com.automatics.providers.tr69.Parameter;
import com.automatics.tr181.TR181Parameter;
import com.automatics.webpa.WebPaParameter;
import com.automatics.webpa.WebPaServerResponse;

/**
 * Utility class to handle TR181 related data mapping
 * 
 * @author radhikas
 *
 */
public class TR181Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TR181Utils.class);

    private static final String GET_RESPONSE_VALUE = "value:";

    /**
     * Gets the parameter value from dmcli getv command response
     * 
     * @param dmcliResponse
     *            Dmcli response
     * @return Parameter value
     */
    public static String parseResponseAndGetDmcliParamValues(String dmcliResponse) {

	String value = null;

	if (CommonMethods.isNotNull(dmcliResponse)) {

	    String[] paramValues = dmcliResponse.split("Parameter");

	    if (paramValues.length > AutomaticsConstants.CONSTANT_0) {

		for (String response : paramValues) {

		    // Parse parameter value
		    int valueOfIndex = response.indexOf(GET_RESPONSE_VALUE);

		    if (valueOfIndex >= AutomaticsConstants.CONSTANT_0) {
			valueOfIndex += GET_RESPONSE_VALUE.length();

			if (response.length() > valueOfIndex) {
			    value = response.substring(valueOfIndex).trim();
			    break;
			}
		    }

		}
	    }
	}

	return value;
    }

    /**
     * Convert WebPa Param Object to TR181 Param Object
     * 
     * @param webPaParameters
     * @return TR181 Param Objects
     */
    public static List<TR181Parameter> convertWebPaToTR181ParamObject(List<WebPaParameter> webPaParameters) {
	List<TR181Parameter> parameterList = null;

	TR181Parameter parameter = null;

	if (null != webPaParameters && !webPaParameters.isEmpty()) {
	    parameterList = new ArrayList<TR181Parameter>();

	    for (WebPaParameter webPaParameter : webPaParameters) {
		parameter = new TR181Parameter();
		parameter.setName(webPaParameter.getName());

		parameter.setTr181DataType(
			mapWebPaToTR181DataType(webPaParameter.getName(), webPaParameter.getDataType()));
		parameter.setValue(webPaParameter.getValue());
		parameterList.add(parameter);
	    }
	}

	return parameterList;
    }

    /**
     * Convert TR181 Param Object to WebPa Param Object
     * 
     * @param parameterList
     * @return WebPa Param Objects
     */
    public static List<WebPaParameter> convertTR181ToWebPaParamObject(List<TR181Parameter> parameterList) {

	List<WebPaParameter> webPaParameters = null;
	WebPaParameter webPaParameter = null;

	if (null != parameterList && !parameterList.isEmpty()) {
	    webPaParameters = new ArrayList<WebPaParameter>();

	    for (TR181Parameter parameter : parameterList) {
		webPaParameter = new WebPaParameter();
		webPaParameter.setName(parameter.getName());

		webPaParameter.setDataType(mapTR181ToWebPaDataType(parameter.getTr181DataType()).getValue());
		webPaParameter.setValue(parameter.getValue());
		webPaParameters.add(webPaParameter);
	    }
	}

	return webPaParameters;
    }

    /**
     * Map TR181 Param Object to protocol specific data type
     * 
     * @param tr181ParameterList
     * @return TR181 Param Objects
     */
    public static List<TR181Parameter> mapProtocolDetails(List<TR181Parameter> tr181ParameterList,
	    TR181AccessMethods accessMethod) {

	if (null != tr181ParameterList && !tr181ParameterList.isEmpty()) {

	    switch (accessMethod) {

	    case DMCLI:
		for (TR181Parameter parameter : tr181ParameterList) {

		    DmcliDataType dmcliDataType = mapTR181ToDmcliDataType(parameter.getName(),
			    parameter.getTr181DataType());
		    parameter.setProtocolSpecificDataType(dmcliDataType.getDataTypeValue());
		    parameter.setProtocolSpecificParamName(getProtocolParamName(parameter.getName(), accessMethod));
		}
		break;

	    default:
		LOGGER.error(
			"TR181AccessMethods {} not handled. Hence cannot map to appropriate protocol data type value",
			accessMethod);

		break;
	    }
	}

	return tr181ParameterList;
    }

    /**
     * Convert TR181 Param Object to TR69 Param Object
     * 
     * @param tr181ParameterList
     * @return TR69 Param Objects
     */
    public static List<Parameter> convertTR181ToTR69ParamObject(List<TR181Parameter> tr181ParameterList) {

	List<Parameter> tr69Parameters = null;
	Parameter tr69Parameter = null;

	if (null != tr181ParameterList && !tr181ParameterList.isEmpty()) {
	    tr69Parameters = new ArrayList<Parameter>();

	    for (TR181Parameter parameter : tr181ParameterList) {
		tr69Parameter = new Parameter();
		tr69Parameter.setParamName(parameter.getName());

		tr69Parameter.setDataType(mapTR181ToTR69DataType(parameter.getTr181DataType()).name());

		tr69Parameter.setParamValue(parameter.getValue());
		tr69Parameters.add(tr69Parameter);
	    }
	}

	return tr69Parameters;
    }

    /**
     * Convert TR181 Response to map
     * 
     * @param parameterList
     * @return TR181 Response as map
     */
    public static Map<String, String> convertTR181ResponseToMap(List<TR181Parameter> parameterList) {

	Map<String, String> responseMap = new HashMap<String, String>();

	if (null != parameterList && !parameterList.isEmpty()) {

	    for (TR181Parameter parameter : parameterList) {
		responseMap.put(parameter.getName(), parameter.getValue());
	    }
	}

	return responseMap;
    }

    /**
     * Convert TR181 Param Object to WebPa Param Object
     * 
     * @param responseMap
     * @return WebPa Param Objects
     */
    public static List<WebPaParameter> convertTR181ToWebPaParamObject(Map<String, String> responseMap) {

	List<WebPaParameter> webPaParameters = null;
	WebPaParameter webPaParameter = null;

	if (null != responseMap && !responseMap.isEmpty()) {
	    webPaParameters = new ArrayList<WebPaParameter>();

	    for (String parameterName : responseMap.keySet()) {
		webPaParameter = new WebPaParameter();
		webPaParameter.setName(parameterName);
		webPaParameter.setValue(responseMap.get(parameterName));
		if (null != responseMap.get(parameterName)) {
		    webPaParameter.setMessage(responseMap.get(parameterName).toLowerCase());
		}
		webPaParameters.add(webPaParameter);
	    }
	}

	return webPaParameters;
    }

    /**
     * Convert TR181 Param Object to WebPa Response Object
     * 
     * @param responseMap
     * @return WebPa Response Object
     */
    public static WebPaServerResponse convertTR181ToWebPaResponseObject(Map<String, String> responseMap) {

	List<WebPaParameter> webPaParamList = convertTR181ToWebPaParamObject(responseMap);
	WebPaServerResponse serverResponse = new WebPaServerResponse();

	if (null != webPaParamList) {

	    for (WebPaParameter webPaParameter : webPaParamList) {

		if (null != responseMap.get(webPaParameter.getName())
			&& webPaParameter.getMessage().equalsIgnoreCase(AutomaticsConstants.SUCCESS.toLowerCase())) {
		    serverResponse.setMessage(AutomaticsConstants.SUCCESS.toLowerCase());
		} else {
		    serverResponse.setMessage(AutomaticsConstants.FAILED.toLowerCase());
		    break;
		}

	    }
	}

	return serverResponse;
    }

    /**
     * Map WebPa data type to TR181 data type
     * 
     * @param webPaParamName
     * @param webPaDataType
     * @return TR181 data type
     */
    public static TR181DataType mapWebPaToTR181DataType(String webPaParamName, int webPaDataTypeValue) {

	TR181DataType tr181DataType = null;
	WebPaDataType webPaDataType = WebPaDataType.getType(webPaDataTypeValue);

	LOGGER.info("WebPa data type: {}", webPaDataType);

	if (null != webPaDataType) {
	    switch (webPaDataType) {
	    case STRING:
		tr181DataType = TR181DataType.STRING;
		break;

	    case UNSIGNED_INT:
		tr181DataType = TR181DataType.UNSIGNED_INT;
		break;

	    case INTEGER:
		tr181DataType = TR181DataType.INT;
		break;

	    case BOOLEAN:
		tr181DataType = TR181DataType.BOOLEAN;
		break;

	    case UNSIGNEDLONG:
		tr181DataType = TR181DataType.UNSIGNED_LONG;
		break;

	    case LONG:
		tr181DataType = TR181DataType.LONG;
		break;

	    case FLOAT:
		tr181DataType = TR181DataType.FLOAT;
		break;

	    default:
		tr181DataType = TR181DataType.STRING;
		break;
	    }
	}

	return tr181DataType;

    }

    /**
     * Map TR181 data type to WebPa data type
     * 
     * @param tr181DataType
     * @return WebPa data type
     */
    public static WebPaDataType mapTR181ToWebPaDataType(TR181DataType tr181DataType) {

	WebPaDataType webPaDataType = null;

	switch (tr181DataType) {
	case STRING:
	    webPaDataType = WebPaDataType.STRING;
	    break;

	case INT:
	    webPaDataType = WebPaDataType.INTEGER;

	    break;

	case UNSIGNED_INT:
	    webPaDataType = WebPaDataType.UNSIGNED_INT;

	    break;

	case BOOLEAN:
	    webPaDataType = WebPaDataType.BOOLEAN;
	    break;

	default:
	    webPaDataType = WebPaDataType.STRING;
	    break;
	}

	return webPaDataType;

    }

    /**
     * Map TR181 data type to Dmcli data type
     * 
     * @param tr181ParamName
     * @param tr181DataType
     * @return Dmcli data type
     */
    public static DmcliDataType mapTR181ToDmcliDataType(String tr181ParamName, TR181DataType tr181DataType) {

	DmcliDataType dmcliDataType = null;

	switch (tr181DataType) {
	case STRING:
	    dmcliDataType = DmcliDataType.STRING;
	    break;

	case INT:
	    dmcliDataType = DmcliDataType.INT;
	    break;

	case UNSIGNED_INT:
	    dmcliDataType = DmcliDataType.UNSIGNED_INT;

	    break;

	case BOOLEAN:
	    dmcliDataType = DmcliDataType.BOOLEAN;
	    break;

	default:
	    dmcliDataType = DmcliDataType.STRING;
	    break;
	}

	return dmcliDataType;

    }

    /**
     * Map TR181 data type to TR69 data type
     * 
     * @param tr181DataType
     * @return TR69 data type
     */
    public static TR69DataType mapTR181ToTR69DataType(TR181DataType tr181DataType) {

	TR69DataType tr69DataType = null;

	switch (tr181DataType) {
	case STRING:
	    tr69DataType = TR69DataType.STRING;
	    break;

	case INT:
	    tr69DataType = TR69DataType.INT;
	    break;

	case UNSIGNED_INT:
	    tr69DataType = TR69DataType.UNSIGNED_INT;

	    break;

	case BOOLEAN:
	    tr69DataType = TR69DataType.BOOLEAN;
	    break;

	default:
	    tr69DataType = TR69DataType.STRING;
	    break;
	}

	return tr69DataType;

    }

    /**
     * Map and return param name corresponding to given access method
     * 
     * @param tr181ParamName
     * @param tr181AccessMethod
     * @return Param name
     */
    public static String getProtocolParamName(String tr181ParamName, TR181AccessMethods tr181AccessMethod) {
	String paramName = tr181ParamName;
	// TR181 - webpa param name to dmcli param name
	paramName = convertRdkbWebPaWiFiParameterIndexToDmcliParameterIndex(tr181ParamName);
	return paramName;

    }

    /**
     * Map and return param names corresponding to given access method
     * 
     * @param tr181ParamName
     * @param tr181AccessMethod
     * @return Param name
     */
    public static List<TR181Parameter> getTR181ParameterObjects(List<String> tr181ParamNames,
	    TR181AccessMethods tr181AccessMethod) {

	List<TR181Parameter> tr181ParamList = new ArrayList<TR181Parameter>();

	TR181Parameter tr181Parameter = null;

	switch (tr181AccessMethod) {
	case DMCLI:
	    for (String tr181ParamName : tr181ParamNames) {
		// TR181 - webpa param name to dmcli param name

		tr181Parameter = new TR181Parameter();
		tr181Parameter.setName(tr181ParamName);
		tr181Parameter.setProtocolSpecificParamName(getProtocolParamName(tr181ParamName, tr181AccessMethod));
		tr181ParamList.add(tr181Parameter);

	    }
	    break;

	default:
	    break;
	}

	return tr181ParamList;

    }

    /**
     * Method to convert List of string to list of parameter
     * 
     * @param paramValues
     * @return
     */
    public static List<TR181Parameter> convertToTR181Response(List<String> paramValues) {
	List<TR181Parameter> response = null;
	if (paramValues != null) {
	    response = new ArrayList<TR181Parameter>();
	    for (String value : paramValues) {
		TR181Parameter param = new TR181Parameter();
		param.setValue(value);
		response.add(param);
	    }
	}
	return response;
    }

    /**
     * Verify if dmcli operation is success or not
     * 
     * @param dmcliResponse
     * @return
     */
    public static String isDmcliOperationSuccess(String dmcliResponse) {
	String status = AutomaticsConstants.FAILED;
	if (CommonMethods.isNotNull(dmcliResponse) && dmcliResponse.contains("Execution succeed")) {
	    status = AutomaticsConstants.SUCCESS;
	}

	return status;
    }

    /**
     * Utility method to convert RDK-B WebPA Wi-Fi parameter Index to compatible dmcli parameter index
     * 
     * @param tr181Parameter
     *            The WebPA parameter
     * @return The dmcli compatible parameter.
     * 
     */
    private static String convertRdkbWebPaWiFiParameterIndexToDmcliParameterIndex(String tr181Parameter) {
	if (tr181Parameter.contains(WebPaConstants.WEBPA_TABLE_DEVICE_WIFI)) {

	    if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PRIVATE_SSID)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PRIVATE_SSID, "1");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_5_GHZ_PRIVATE_SSID)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_5_GHZ_PRIVATE_SSID, "2");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PUBLIC_WIFI)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PUBLIC_WIFI, "5");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_5_GHZ_PUBLIC_WIFI)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_5_GHZ_PUBLIC_WIFI, "6");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PUBLIC_SSID_AP2)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_2_4_GHZ_PUBLIC_SSID_AP2, "9");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_5_GHZ_PUBLIC_SSID_AP2)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_5_GHZ_PUBLIC_SSID_AP2, "10");
	    } else if (tr181Parameter.contains(WebPaConstants.RADIO_24_GHZ_INDEX)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.RADIO_24_GHZ_INDEX, "1");
	    } else if (tr181Parameter.contains(WebPaConstants.RADIO_5_GHZ_INDEX)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.RADIO_5_GHZ_INDEX, "2");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10002)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10002, "3");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10102)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10102, "4");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_2_4_GHZ_OPEN_LNF_AP1)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_2_4_GHZ_OPEN_LNF_AP1, "7");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_5_GHZ_OPEN_LNF_AP2)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_5_GHZ_OPEN_LNF_AP2, "8");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10006)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10006, "11");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10106)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10106, "12");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10007)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10007, "13");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10107)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10107, "14");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10008)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10008, "15");
	    } else if (tr181Parameter.contains(WebPaConstants.WEBPA_INDEX_10108)) {
		tr181Parameter = tr181Parameter.replace(WebPaConstants.WEBPA_INDEX_10108, "16");
	    }
	}
	return tr181Parameter;
    }

}
