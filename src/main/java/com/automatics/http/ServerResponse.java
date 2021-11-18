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
package com.automatics.http;

/**
 * Class to hold the server response values
 * 
 * @author surajmathew
 *
 */
public class ServerResponse {

    int responseCode = -1;
    String responseStatus = "DONE";
    long timeTaken = -1;

    public int getResponseCode() {
	return responseCode;
    }

    public void setResponseCode(int responseCode) {
	this.responseCode = responseCode;
    }

    public String getResponseStatus() {
	return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
	this.responseStatus = responseStatus;
    }

    public long getTimeTaken() {
	return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
	this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ServerResponse [responseCode=");
	builder.append(responseCode);
	builder.append(", responseStatus=");
	builder.append(responseStatus);
	builder.append(", timeTaken=");
	builder.append(timeTaken);
	builder.append("]");
	return builder.toString();
    }
}
