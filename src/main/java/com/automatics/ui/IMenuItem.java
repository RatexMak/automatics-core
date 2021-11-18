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

package com.automatics.ui;

/**
 * Interface represents the menu items.
 * 
 * @author Selvaraj Mariyappan
 */
public interface IMenuItem {

    /**
     * Get the name of the menu item.
     * 
     * @return name of the menu item
     */
    String getName();

    /**
     * Get the name of the resource file name.
     * 
     * @return The resource file name.
     */
    String getResourceFileName();

    /**
     * Get the region name specified in the resource file.
     * 
     * @return The region name.
     */
    String getRegionName();

    /**
     * Get the sub option for navigation
     * 
     * @return The sub option name
     */
    String getNavigatorOption();

    /**
     * Get the option for deep link
     * 
     * @return deep link url
     */
    String getDeepLinkOption();
}
