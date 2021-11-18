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

public abstract interface MotionDetectionProvider extends BaseProvider {
    public abstract boolean detectMotion(long paramLong);

    public abstract boolean detectMotion(long paramLong1, long paramLong2);

    public abstract boolean detectMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong1,
	    long paramLong2);

    public abstract boolean detectMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong);

    public abstract void setColorTolerance(float paramFloat);

    public abstract void setLocationTolerance(float paramFloat);

    public abstract void setMotionTolerance(float paramFloat);

    public abstract void setMotionDetectionDiffImageSaveLocation(String paramString);

    public abstract float getMotionTolerance();

    public abstract float getLocationTolerance();

    public abstract float getColorTolerance();
}