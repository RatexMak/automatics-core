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
 * This enum class defines the different states a job can conclude in.
 * 
 * @author rohinic
 *
 */
public enum JobStatusValue {
    ALREADY_LOCKED,
    SSH_FAIL,
    NO_AV_BEFORE_TEST,
    NO_AV_AFTER_TEST,
    BUILD_CHANGED_AFTER_TEST,
    BUILD_CHANGED_BEFORE_TEST,
    BOXES_UNUSABLE,
    FAILURE,
    INVALID_DEVICE,
    COMPLETED
}
