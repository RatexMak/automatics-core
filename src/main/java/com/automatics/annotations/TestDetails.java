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

/**
 *
 */
package com.automatics.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.enums.RackType;
import com.automatics.enums.RetryMode;

/**
 * Annotation class to handle the Test Details to complement the test annotations supported by TestNG.
 *
 * @author nagendra
 */
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface TestDetails {

    /** The unique identifier for the test */
    String testUID();

    /** Tags applied for the test */
    String[] tags() default {};

    /**
     * Reserved for future use. Box types on which this test can run on. The box type must match the box type defined in
     * URMS/CHIMPS
     */
    @Deprecated
    String[] runOnBoxTypes() default {};

    /**
     * Type of test. Types can be 1. FUNCTIONAL 2. SMOKE 3. PERFORMANCE 4. MANUAL Refer @see {@link AutomaticsTestTypes}
     * 
     * @return
     */
    AutomaticsTestTypes[] testType() default {};

    /**
     * Type of racks.
     */
    RackType[] runOnRackType() default {};

    /**
     * This property is not being used now. Retained for backward compatibility.
     */
    @Deprecated
    boolean retry() default false;

    /**
     * Number of retries if the test fails. If retry is true the retryCount has no effect. The default retry count is 1
     * 
     * @return
     */
    int retryCount() default 1;

    /**
     * Retry mode for the tests
     * 
     * @return The retry mode for the test
     */
    RetryMode retryMode() default RetryMode.FALSE;

    /** The category to which the test belongs to */
    String testCategory() default "";

    /** Brief description of the test case */
    String testDecription() default "";
}
