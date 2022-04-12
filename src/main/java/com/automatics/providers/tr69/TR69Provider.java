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
package com.automatics.providers.tr69;

import java.util.List;

import com.automatics.device.Dut;

/**
 * Interface that provides various TR69 operations
 * 
 * @author Raja M
 *
 */
public interface TR69Provider {

    /**
     * Method to get TR69 parameter values.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            The array of TR69 parameter.
     * 
     * @return List of string values corresponding to each TR69 Parameter.
     */
    public List<String> getTr69ParameterValues(Dut dut, String[] parameters);

    /**
     * Method to set TR69 parameter values.
     * 
     * @param dut
     *            The dut to be used.
     * @param parameters
     *            list of TR69 parameter.
     * 
     * @return Status of set operation. Returns 'SUCCESS' if set operation was success else returns 'FAILURE'
     */
    public String setTr69ParameterValues(Dut dut, List<Parameter> tr69Param);
    
    /**
     * Method to get TR69 parameter name.
     * 
     * @param dut
     *            The dut to be validated.
     * @param parameterPath
     *            The parameter path name.
     *            
     * @return List of TR69 Parameter name response.
     */
    public List<Parameter> getTr69ParameterNamebyPath(Dut dut, String parameterPath);

}
