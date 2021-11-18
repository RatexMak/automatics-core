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

public abstract interface AudioProvider extends BaseProvider {

    public abstract boolean isNormalAudioPresent();

    public abstract void setParent(Object paramObject);

    public abstract double getCurrentAudioDecibelLevel();

    public abstract double[] getCurrentAudioDecibelForAllChannels();

    public abstract double getAudioDecibelLevel(int paramInt);

    public abstract boolean isAudioAboveThreshold(double paramDouble);

    public abstract boolean isAudioAboveThreshold(double paramDouble, int paramInt);

    public abstract boolean isAudioBelowThreshold(double paramDouble);

    public abstract boolean isAudioBelowThreshold(double paramDouble, int paramInt);

    public abstract double getPriorAudioDecibelLevel(int paramInt);

    public abstract double getPriorAudioDecibelLevel(int paramInt1, int paramInt2);

}