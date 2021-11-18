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

import com.automatics.providers.rack.AudioProvider;
import com.automatics.providers.rack.PowerProvider;
import com.automatics.providers.rack.RemoteProvider;
import com.automatics.providers.rack.VideoProvider;
import com.automatics.providers.rack.VideoSelectionProvider;
import com.automatics.providers.trace.TraceProvider;

public class DutImpl extends AbstractDut {

    public DutImpl() {
    }

    public DutImpl(RemoteProvider remote, PowerProvider power, AudioProvider audio, TraceProvider trace,
	    VideoProvider video, VideoSelectionProvider videoSelection) {
	super(remote, power, audio, trace, video, videoSelection);
    }

    public DutImpl(String id, String make, String model, String manufacturer, String content, String hostMacAddress,
	    String hostIp4Address, String hostIp6Address, String componentType, String firmwareVersion,
	    String hardwareRevision, String environmentId) {
	super(id, make, model, manufacturer, content, hostMacAddress, hostIp4Address, hostIp6Address, componentType,
		firmwareVersion, hardwareRevision, environmentId);
    }
}