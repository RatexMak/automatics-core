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

public abstract interface EASProvider extends BaseProvider {
    public abstract boolean runNormalEas(String paramString1, String paramString2, String paramString3,
	    String paramString4, String paramString5);

    public abstract boolean runNormalEasMonroe(String paramString1, String paramString2, String paramString3,
	    String paramString4, String paramString5, String paramString6);
}