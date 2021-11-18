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
package com.automatics.core;

import com.automatics.exceptions.TestException;

/**
 * @author nipun
 */
public class Assert {

    /**
     * Throws an exception if the actual null.
     *
     * @param actual
     */
    public static void assertNotNull(Object actual, String message) {

	assertNotEquals(actual, null, message);
    }

    /**
     * Throws an exception if the actual is not null.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertNull(Object actual, String message) {

	assertEquals(actual, null, message);
    }

    /*************************************************************************************************************************************/
    /**
     * Throws an exception if the actual and expected are equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertNotEquals(int actual, int expected, String message) {

	assertNotEquals(actual, expected, message);
    }

    /**
     * Throws an exception if the actual and expected are equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertNotEquals(String actual, String expected, String message) {

	assertNotEquals(actual, expected, message);
    }

    /**
     * Throws an exception if the actual and expected are equal.
     *
     * @param actual
     * @param expected
     */
    public static void assertNotEquals(Object actual, Object expected) {

	assertNotEquals(actual, expected, "");
    }

    /**
     * Throws an exception if the success value is false.
     *
     * @param actual
     * @param message
     */
    public static void assertNotEquals(boolean actual, String message) {

	assertNotEquals(actual, true, message);
    }

    /**
     * Throws an exception if the actual and expected are equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertNotEquals(Object actual, Object expected, String message) {

	if (actual instanceof Integer || actual instanceof Long) {

	    if (actual != expected) {
		return;
	    }

	} else if (!actual.equals(expected)) {
	    return;
	}

	fail(message);
    }

    /*************************************************************************************************************************************/

    /**
     * Throws an exception if the actual and expected are not equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertEquals(int actual, int expected, String message) {

	assertEquals(actual, expected, message);
    }

    /**
     * Throws an exception if the actual and expected are not equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertEquals(String actual, String expected, String message) {

	assertEquals(actual, expected, message);
    }

    /**
     * Throws an exception if the actual and expected are not equal.
     *
     * @param actual
     * @param expected
     */
    public static void assertEquals(Object actual, Object expected) {

	assertEquals(actual, expected, "");
    }

    /**
     * Throws an exception if the success value is false.
     *
     * @param actual
     * @param message
     */
    public static void assertTrue(boolean actual, String message) {

	assertEquals(actual, true, message);
    }

    /**
     * Throws an exception if the success value is true.
     *
     * @param actual
     * @param message
     */
    public static void assertFalse(boolean actual, String message) {

	assertEquals(actual, false, message);
    }

    /**
     * Throws an exception if the actual and expected are not equal.
     *
     * @param actual
     * @param expected
     * @param message
     */
    public static void assertEquals(Object actual, Object expected, String message) {

	if (actual instanceof Integer || actual instanceof Long) {

	    if (actual == expected) {
		return;
	    }

	} else if (actual.equals(expected)) {
	    return;
	}

	fail(message);
    }

    /**
     * Throwing exception.
     *
     * @param error
     * @param message
     */
    public static void fail(String message) {
	throw new TestException("AssertionError: " + message);

    }

}
