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
package com.automatics.image.imagick;

import java.util.ArrayList;

import org.im4java.core.IMOperation;

/**
 * ImageMagick operation wrapper.<br>
 * All the image processing operation configurations are defined by this clas.
 * 
 * @author Rahul R.Prasad
 */
public class Operation {

    /**
     * Configure the comparison configurations. Comparisons can be either <code>RMSE</code> or <code>NCC</code>.
     * 
     * @param metric
     *            Comparison metric to be applied.
     * @return {@link IMOperation} for comparison.
     */
    public IMOperation configureCompareOps(Metric metric) {
	ArrayList<String> arguments = new ArrayList<String>();
	IMOperation operation = getIMOps();

	arguments.add("-metric");
	arguments.add(metric.name());
	operation.addRawArgs(arguments);
	return operation;
    }

    /**
     * Configure the arguments for Hue extraction from images.
     * 
     * @return {@link IMOperation} for Hue extraction.
     */
    public IMOperation configureHueExtractionArgs() {
	ArrayList<String> arguments = new ArrayList<String>();
	IMOperation operation = getIMOps();

	arguments.add("-colorspace");
	arguments.add("HSL");
	arguments.add("-channel");
	arguments.add("r");
	arguments.add("-separate");
	arguments.add("+channel");
	operation.addRawArgs(arguments);
	return operation;
    }

    /**
     * Configure arguments for grayscale conversion in ImageMagick.
     * 
     * @return {@link IMOperation} for grayscale conversion
     */
    public IMOperation configureGrayScaleArgs() {
	ArrayList<String> arguments = new ArrayList<String>();
	IMOperation operation = getIMOps();

	arguments.add("-colorspace");
	arguments.add("Gray");
	operation.addRawArgs(arguments);
	return operation;
    }

    /**
     * Configure arguments for taking difference image b/w two input images.
     * 
     * @return {@link IMOperation} for diff. image computation.
     */
    public IMOperation configureDiffImageArgs() {
	ArrayList<String> arguments = new ArrayList<String>();
	IMOperation operation = getIMOps();

	arguments.add("-compose");
	arguments.add("difference");
	arguments.add("-composite");
	operation.addRawArgs(arguments);
	return operation;
    }

    /**
     * Configure arguments for scaling the input image.
     * 
     * @return {@link IMOperation} for scaling operation.
     */
    public IMOperation configureScaledImageArgs() {
	ArrayList<String> arguments = new ArrayList<String>();
	IMOperation operation = getIMOps();

	arguments.add("-alpha");
	arguments.add("off");
	arguments.add("-depth");
	arguments.add("8");
	// arguments.add("-scale");
	// arguments.add("1x1");
	operation.addRawArgs(arguments);
	return operation;
    }

    /**
     * Creates a new instance of {@link IMOperation} class. Mainly used for dependency injection in testing environment.
     * 
     * @return new instance of {@link IMOperation} class.
     */
    protected IMOperation getIMOps() {
	return new IMOperation();
    }
}