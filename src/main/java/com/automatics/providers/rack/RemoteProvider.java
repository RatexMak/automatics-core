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
package com.automatics.providers.rack;

import com.automatics.enums.RemoteControlType;

/**
 * Interface that provides various remote operations
 * 
 * @author Raja M
 *
 */
public abstract interface RemoteProvider extends BaseProvider {

    /**
     * This method is to do tune operation for remote
     * 
     * @param string
     * @param integer
     * @param boolean
     * @param enum
     * @return boolean
     */
    public abstract boolean tune(String paramString, Integer paramInteger, boolean isAutoTune, RemoteControlType type);
    
    /**
     * This method is to do pressKey operation for remote
     * 
     * @param string
     * @param integer
     * @param boolean
     * @param enum
     * @return boolean
     */
    public abstract boolean pressKey(String command, RemoteControlType type);

    /**
     * This method is to do pressKeyAndHold operation for remote
     * 
     * @param string
     * @param integer
     * @param boolean
     * @param enum
     * @return boolean
     */
    public abstract boolean pressKeyAndHold(String command, Integer count, RemoteControlType type);

    /**
     * This method is to send text for remote based on the parameter
     * 
     * @param string
     * @param integer
     * @param boolean
     * @param enum
     * @return boolean
     */
    public abstract boolean sendText(String paramString, RemoteControlType type);
    
}