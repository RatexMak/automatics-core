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

package com.automatics.constants;

/**
 * Contains constants that will be used by Data providers.
 *
 * @author nagendra
 */
public class DataProviderConstants {

    /** String constant to specify a data provider type as parallel. */
    public static final String PARALLEL_DATA_PROVIDER = "PARALLEL_DATA_PROVIDER";

    /** String constant to specify a data provider type as sequential. */
    public static final String SEQUENTIAL_DATA_PROVIDER = "SEQUENTIAL_DATA_PROVIDER";    
    
    /** String constant to specify a data provider dedicated AccountTest setup. */
    public static final String ACCOUNT_DATA_PROVIDER = "ACCOUNT_DATA_PROVIDER";

    /** String constant to specify a data provider dedicated connected client setup. */
    public static final String CONNECTED_CLIENTS_DATA_PROVIDER = "CONNECTED_CLIENTS_DATA_PROVIDER";
   
    /** parallel data provider thread count. */
    public static final int DATA_PROVIDER_PARALLEL_THREAD_COUNT = 200;


}
