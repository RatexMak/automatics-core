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
 * WebPa data types
 * 
 *
 */
public enum WebPaDataType {

    STRING(0),
    INTEGER(1),
    UNSIGNED_INT(2),
    BOOLEAN(3),
    DATETIME(4),
    BASE64(5),
    LONG(6),
    UNSIGNEDLONG(7),
    FLOAT(8),
    DOUBLE(9),
    BYTE(10),
    INVALID(11);

    private int type;

    WebPaDataType(int dataType) {
	this.type = dataType;
    }

    public int getValue() {
	return type;
    }

    public static WebPaDataType getType(int value) {
	WebPaDataType webPaDataType = null;

	for (WebPaDataType dataType : WebPaDataType.values()) {
	    if (dataType.getValue() == value) {
		webPaDataType = dataType;
		break;
	    }
	}
	return webPaDataType;
    }

}
