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
package com.automatics.enums;

/**
 * Enum type to identify various state changes during build check after test suite.
 * 
 */
public enum BuildTypeChanges {

    BUILD_CHANGED,
    /** BUILD CHANGED TO PROD **/
    BUILD_CHANGED_TO_PROD,
    /** BUILD CHANGED TO VBN/DEV or any other type **/
    BUILD_CHANGED_TO_NON_PROD,
    /** NO CHANGE IN BUILD **/
    NO_CHANGE,
    /** Cannot determine build due to ssh exception **/
    UNABLE_TO_DETERMINE,
    /** Skip build check **/
    SKIP_CHECK;

}
