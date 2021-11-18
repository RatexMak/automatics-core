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
package com.automatics.device;

import java.util.Collection;
import java.util.logging.Logger;

import com.automatics.providers.rack.AudioProvider;
import com.automatics.providers.rack.EASProvider;
import com.automatics.providers.rack.ImageCompareProvider;
import com.automatics.providers.rack.MotionDetectionProvider;
import com.automatics.providers.rack.OcrProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RFControlProvider;
import com.automatics.providers.rack.RemoteProvider;
import com.automatics.providers.rack.VideoProvider;
import com.automatics.providers.rack.VideoSelectionProvider;
import com.automatics.providers.trace.TraceProvider;

/**
 * 
 * Dut interface
 *
 */
public abstract interface Dut
	extends DutInfo, PowerProvider, RemoteProvider, RFControlProvider, MotionDetectionProvider {
    public abstract PowerProvider getPower();

    public abstract RemoteProvider getRemote();

    public abstract Collection<RemoteProvider> getRemotes();

    public abstract AudioProvider getAudio();

    public abstract TraceProvider getTrace();

    public abstract TraceProvider getSerialTrace();

    public abstract VideoProvider getVideo();
    
    public abstract VideoSelectionProvider getVideoSelection();

    public abstract EASProvider getEASProvider();

    public abstract ImageCompareProvider getImageCompareProvider();

    public abstract OcrProvider getOcrProvider();

    public abstract MotionDetectionProvider getMotionDetectorProvider();

    public abstract Logger getLogger();

    public abstract void logInfo(String paramString);

    public abstract void logError(String paramString);

    public abstract void logWarn(String paramString);

    public abstract void logDebug(String paramString);

    public abstract void logTrace(String paramString);

    public abstract String getLogDirectory();

    public abstract boolean isLocked();

    public abstract void setLocked(boolean paramBoolean);

    public abstract String getRackName();

    public abstract String getSlotName();

    public abstract String getSlotNumber();

    public abstract String getServerHost();

    public abstract Integer getServerPort();
}