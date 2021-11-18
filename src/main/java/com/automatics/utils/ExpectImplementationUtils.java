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

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Level;

/**
 * Wrapper for Expect.java Expect.java APIs should be called from ExpectImplementationUtils.java
 *
 */
public class ExpectImplementationUtils {

    public Expect expect = null;

    /**
     * 
     * @param input
     * @param output
     */
    public ExpectImplementationUtils(InputStream input, OutputStream output) {
	this.expect = new Expect(input, output);
	Expect.log.setLevel(Level.DEBUG);
    }

    /**
     * @param data
     *            Convenience method to send a string to output handle
     */
    public void send(String data) {
	this.expect.send(data);
    }

    /**
     * @param data
     *            Write a byte array to the output handle, notice flush()
     */
    public void send(byte[] data) {
	this.expect.send(data);
    }

    /**
     * Convenience method, same as calling {@link #expect(int, Object...) expect(default_timeout, patterns)}
     * 
     * @param patterns
     * @return
     */
    public int expectPattern(Object... patterns) {
	return this.expect.expect(patterns);
    }

    /**
     * @return the before
     */
    public String getBefore() {
	return this.expect.before;
    }

    /**
     * @param before
     *            the before to set
     */
    public void setBefore(String before) {
	this.expect.before = before;
    }

    /**
     * @return the match
     */
    public String getMatch() {
	return this.expect.match;
    }

    /**
     * @param match
     *            the match to set
     */
    public void setMatch(String match) {
	this.expect.match = match;
    }

    /**
     * @return the isSuccess
     */
    public boolean isSuccess() {
	return this.expect.isSuccess;
    }

    /**
     * @param isSuccess
     *            the isSuccess to set
     */
    public void setSuccess(boolean isSuccess) {
	this.expect.isSuccess = isSuccess;
    }

}
