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
package com.automatics.executor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

public class AutomaticsAnnotationTransformer implements IAnnotationTransformer {
    static final Logger LOGGER = LoggerFactory.getLogger(AutomaticsAnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation itestannotation, Class classSource, Constructor constructor, Method method) {
	IRetryAnalyzer retry = itestannotation.getRetryAnalyzer();	
	if (retry == null) {
	    itestannotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

	/*
	 * all the scripts enable/disable actions will be handled in Manage Script module of Automatics orchestration.
	 * 
	 * So even if a test case is disabled in java file, we need to enable it.
	 */
	if (!itestannotation.getEnabled()) {
	    itestannotation.setEnabled(true);
	}
	
    }

}
