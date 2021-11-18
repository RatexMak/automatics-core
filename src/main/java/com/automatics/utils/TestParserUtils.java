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

package com.automatics.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.automatics.annotations.TestDetails;
import com.automatics.constants.AutomaticsConstants;
import com.automatics.constants.ReportsConstants;
import com.automatics.core.TestMethod;
import com.automatics.enums.AutomaticsTestTypes;
import com.automatics.test.AutomaticsTestBase;

/**
 * Parses the test class to find the included tests and its properties.
 *
 * @author nagendra
 */
public class TestParserUtils {

    private static final Class[] PARAMETERS = new Class[] { URL.class };
    private static final Logger LOGGER = LoggerFactory.getLogger(TestParserUtils.class);

    /**
     * Parses the test class and identifies the test methods. Return the list of methods that are marked using @Test and @TestDetails
     * annotation
     *
     * @param classToparse
     *            The class to parse and identify test methods
     *
     * @return The list of test methods found in the specified class
     */
    public static List<TestMethod> parseTestClass(Class classToparse) {
	// LOGGER.debug("Parsing Test class " + classToparse);

	List<TestMethod> testMethods = new ArrayList<TestMethod>();

	if (!classToparse.isInterface()) {

	    try {

		/**
		 * Check whether the class has AutomaticsBaseTest as its super class if not the asSubclass() will throw a
		 * class cast exception and hence the parsing of methods in the provided class will be skipped
		 */
		classToparse.asSubclass(AutomaticsTestBase.class);

		String filterTestClass = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_CLASS);
		String includedGroups = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_INCLUDED_GROUP);
		String filterTestTags = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TAG);
		String filterTestIds = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_ID);
		String excludedGroups = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_EXCLUDED_GROUP);
		String filterTestType = System.getProperty(AutomaticsConstants.SYSTEM_PROPERTY_FILTER_TEST_TYPE);

		List<String> excludedGroupList = AutomaticsUtils.splitStringToList(excludedGroups, ",");
		List<String> includedGroupList = AutomaticsUtils.splitStringToList(includedGroups, ",");
		List<String> filterTestClassList = AutomaticsUtils.splitStringToList(filterTestClass, ",");
		List<String> filterTestIdList = AutomaticsUtils.splitStringToList(filterTestIds, ",");
		List<String> filterTestTagList = AutomaticsUtils.splitStringToList(filterTestTags, ",");
		List<String> filterTestTypeList = AutomaticsUtils.splitStringToList(filterTestType, ",");

		// Apply class level filters

		// if filter class list is non empty then check whether this class is specified in
		// filter test class
		String className = classToparse.getSimpleName();

		if (!filterTestClassList.isEmpty() && !filterTestClassList.contains(className)) {
		    // LOGGER.debug("Skipping class " + className + " since excluded by filterTestClass");

		    return testMethods;
		}

		Test classLevelTestAnnotation = (Test) classToparse.getAnnotation(Test.class);
		List<String> classGroupList = null;

		if (classLevelTestAnnotation != null) {
		    String[] classGroups = classLevelTestAnnotation.groups();
		    classGroupList = Arrays.asList(classGroups);

		    // check if the class belongs to included group if not immediately return
		    if (!includedGroupList.isEmpty() && Collections.disjoint(includedGroupList, classGroupList)) {
			LOGGER.debug("Skipping class " + className + " since class name not included in includedGroups");

			return testMethods;
		    }

		    // check if class belongs to excluded group if yes immediately return
		    if (!excludedGroupList.isEmpty() && !Collections.disjoint(excludedGroupList, classGroupList)) {
			LOGGER.debug("Skipping class " + className + " since class is excluded through excludedGroups");

			return testMethods;
		    }
		}

		Method[] methods = classToparse.getMethods();

		for (Method method : methods) {
		    // LOGGER.debug("Validating method " + method.getName());

		    Test testAnnotation = method.getAnnotation(Test.class);

		    if (testAnnotation == null) {

			if (Modifier.isPublic(method.getModifiers())) {
			    testAnnotation = (Test) classToparse.getAnnotation(Test.class);
			}
		    }

		    TestDetails testDetailsAnnotation = method.getAnnotation(TestDetails.class);

		    if ((null != testAnnotation) && (null != testDetailsAnnotation)) {
			String testName = method.getName();

			/*
			 * Check if test id filters are specified if yes check whether the test id is included in the
			 * filter list if test id is not specified then skip this test
			 */
			String testUID = testDetailsAnnotation.testUID();
			// LOGGER.debug("Checking whether test method " + testName + " is included in filter test ID");

			if (!filterTestIdList.isEmpty() && !filterTestIdList.contains(testUID)) {
			    // LOGGER.debug("Skipping test " + testName +
			    // " since class name not included in filterTestIds");

			    continue;
			}

			/*
			 * Check if test tag filters are specified if yes check whether the tag of this test is included
			 * in the filter list if test tag is not included then skip this test
			 */
			String[] tags = testDetailsAnnotation.tags();
			// LOGGER.debug("Checking whether test method " + testName + " is included in filter test tag");

			if (!filterTestTagList.isEmpty()
				&& Collections.disjoint(filterTestTagList, Arrays.asList(tags))) {
			    // LOGGER.debug("Skipping test " + testName + " since tag not included filterTags");

			    continue;
			}

			/*
			 * Check if test type filters are specified if yes check whether the test type of this test is
			 * included in the filter list if test type is not included then skip this test
			 */
			if (CommonMethods.isNull(filterTestIds)) {
			    /**
			     * Since the test cases are auto assigned from test manager, we need to consider this
			     * 'filterTestType' only if 'filterTestIds' is absent.
			     */

			    AutomaticsTestTypes[] testTypes = testDetailsAnnotation.testType();
			    List<String> filterTestTypes = new ArrayList<String>();

			    // Convert the filter
			    for (AutomaticsTestTypes type : testTypes) {
				filterTestTypes.add(type.value());
			    }

			    // LOGGER.debug("Checking whether test method " + testName +
			    // " is included in filter test type");

			    if (!filterTestTypeList.isEmpty()
				    && Collections.disjoint(filterTestTypeList, filterTestTypes)) {
				LOGGER.debug("Skipping test " + testName
					+ " since test type not included filterTestType");

				continue;
			    }
			} else {
			    // LOGGER.info("filterTestType check skipped as filterTestId is specified");
			}

			/**
			 * Check if test is included in includedGroups tag if not included then skip the test. Filter
			 * need to be applied only if includedGroups property is specified
			 */
			String[] testGroups = testAnnotation.groups();
			List<String> testGroupList = new ArrayList(Arrays.asList(testGroups));

			if (classGroupList != null) {
			    testGroupList.addAll(classGroupList);
			}

			// LOGGER.debug("Checking whether test method " + testName + " is included in included groups");

			if (!includedGroupList.isEmpty() && Collections.disjoint(includedGroupList, testGroupList)) {
			    // LOGGER.debug("Skipping test " + testName + " since not included in includedGroups");

			    continue;
			}

			// Remove methods belonging to excluded groups
			LOGGER.debug("Checking whether test method " + testName + " is included in excluded groups");

			if (!excludedGroupList.isEmpty() && !Collections.disjoint(excludedGroupList, testGroupList)) {
			    // LOGGER.debug("Skipping test " + testName + " since excluded through excludedGroups");

			    continue;
			}

			// LOGGER.debug("Adding test method " + testName + " to the valid list");

			// TODO::can skip a test method if the UID is not defined for test (Empty
			// string).
			TestMethod testMethod = new TestMethod();
			testMethod.setTestMethodName(method.getName());
			testMethod.setTestUID(testDetailsAnnotation.testUID());
			testMethod.setTestName(testAnnotation.testName());
			testMethod.addTags(testDetailsAnnotation.tags());
			testMethod.addRunOnBoxType(testDetailsAnnotation.runOnBoxTypes());
			testMethod.setEnabled(testAnnotation.enabled());
			testMethod.setTestType(Arrays.asList(testDetailsAnnotation.testType()));
			testMethods.add(testMethod);
		    } else {
			// LOGGER.debug("Skipping method " + method.getName() + " Since it is not tagged as Test");
		    }
		}
	    } catch (ClassCastException cce) {
		LOGGER.debug(classToparse.getName() + " is not a subclass of AutomaticsTestBase an hence skipping..");
	    }

	} else {
	    LOGGER.warn("Skipping Class " + classToparse + " Since it is an interface");
	}

	return testMethods;
    }

    /**
     * Scans the test class folder and retrieves the test classes.
     *
     * @return The classes identified under the test folder
     */
    public static ArrayList<Class<?>> obtainTestClasses() {

	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

	try {
	    File file = new File(System.getProperty(ReportsConstants.USR_DIR) + ReportsConstants.TC_DIR);

	    // Convert the file to URL format. This handles the spaces in path.
	    URL url = file.toURI().toURL();
	    addURL(url);
	    loadClassesFromDirectory(file, AutomaticsConstants.EMPTY_STRING, classes);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return classes;
    }

    /**
     * Load classes in the specified directory to memory using the class loader specified. This is a recursive method
     * which will search all the sub directories also. The found classes will be added to the classes list that is
     * provided as a output parameter
     *
     * @param directory
     *            The directory to search
     * @param pkgname
     *            The package name (this parameter should be empty, it is used for recursive calls)
     * @param classes
     *            The output list into which the classes will be added
     * @param classLoader
     *            The class loader to use, can be null (used by recursive call only)
     */
    private static void loadClassesFromDirectory(File directory, String packageName, ArrayList<Class<?>> classList) {

	// Get the list of the files contained in the package
	String[] fileArray = directory.list();

	for (int index = 0; index < fileArray.length; index++) {
	    String file = fileArray[index];
	    String clazzName = null;

	    // Filter the class files
	    if (file.endsWith(ReportsConstants.CLASS_EXTN)) {

		// strip off the extension
		clazzName = packageName + '.'
			+ file.substring(0, file.length() - ReportsConstants.CLASS_EXTENSION_LENGTH);
		classList.add(loadClass(clazzName));
	    }

	    File subdir = new File(directory, file);

	    if (subdir.isDirectory()) {

		if (packageName.isEmpty()) {
		    loadClassesFromDirectory(subdir, file, classList);
		} else {
		    loadClassesFromDirectory(subdir, packageName + '.' + file, classList);
		}
	    }
	}
    }

    private static Class<?> loadClass(String className) {

	try {

	    /*
	     * URL[] urls = ((URLClassLoader)TestParserUtils.class.getClassLoader()).getURLs(); for (URL url : urls) {
	     * LOGGER.debug("***CLASSPATH " + url.toExternalForm());}
	     */
	    return TestParserUtils.class.getClassLoader().loadClass(className);
	    // return ClassLoader.getSystemClassLoader().loadClass(className);

	} catch (ClassNotFoundException e) {
	    throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'", e);
	}
    }

    /**
     * Adds the specified URL as classpath to the class loader.
     *
     * @param u
     *
     * @throws IOException
     */
    private static void addURL(URL url) throws IOException {
	LoggerFactory.getLogger("Framework").debug("***** Adding URL To Class Path **** " + url.toExternalForm());

	URLClassLoader classLoader = (URLClassLoader) TestParserUtils.class.getClassLoader();
	Class classObj = URLClassLoader.class;

	try {
	    Method methodObj = classObj.getDeclaredMethod("addURL", PARAMETERS);
	    methodObj.setAccessible(true);
	    methodObj.invoke(classLoader, new Object[] { url });
	} catch (Throwable t) {
	   LOGGER.error("Error occurred: ", t);
	    throw new IOException("Error, could not add url to classloader");
	}
    }
}
