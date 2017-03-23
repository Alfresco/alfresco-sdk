/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.rad.test;

import org.alfresco.error.AlfrescoRuntimeException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Web Script works in consort with the ${@link AlfrescoTestRunner}. When a test is run from an IDE or
 * command line, the Alfresco test runner sends a proxied request to perform the test to this script. This runs
 * the test and wraps the results up so that the test initiator can be fooled into thinking they are
 * running the tests locally.
 * <p/>
 * Integration testing framework donated by Zia Consulting.
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 * @author martin.bergljung@alfresco.com (some editing)
 * @since 3.0
 */
public class RunTestWebScript extends DeclarativeWebScript {

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        System.out.println("RunTestWebScript: Start executing ...");
        String result = AlfrescoTestRunner.FAILURE;
        String clazzAndMethod = null;
        String clazz = null;    // Test class
        String method = null;   // Test method
        Result junitRunnerResult = null;

        // Example invocation:
        //  /service/testing/test.xml?clazz=org.alfresco.test.platformsample.DemoComponentIT%23testChildNodesCount

        // First, try and get the test class, including method, from a template var, i.e. /clazz/{class%23method}
        clazzAndMethod = req.getServiceMatch().getTemplateVars().get("clazz");
        if (clazzAndMethod == null) {
            // Not found in template var, try parameter, i.e. ?clazz={class%23method}
            clazzAndMethod = req.getParameter("clazz");
        }
        System.out.println("RunTestWebScript: clazzAndMethod = " + clazzAndMethod);

        // Do we have a test class and method now?
        if (clazzAndMethod == null) {
            // No, set class and method as not provided
            clazzAndMethod = "not provided";
        } else {
            // We got a test class and method, proceed
            Class c = null;
            // Split class and method on %23 = #
            String[] clazzAndMethodArray = clazzAndMethod.split("#");
            if (clazzAndMethodArray.length > 1) {
                clazz = clazzAndMethodArray[0];
                method = clazzAndMethodArray[1];
            }
            System.out.println("RunTestWebScript: [clazz=" + clazz + "][method=" + method + "]");

            try {
                // Load the Java class that will be run by JUnit
                c = Class.forName(clazz);
            } catch (ClassNotFoundException ex) {
                throw new AlfrescoRuntimeException("Could not find test class: " + clazzAndMethod);
            }

            // See if JUnit should run test for whole class, or just specified method
            if (method == null) {
                // No method, run all tests in class
                junitRunnerResult = JUnitCore.runClasses(c);
            } else {
                // We got one specific test method to run
                Request jreq = Request.method(c, method);
                junitRunnerResult = new JUnitCore().run(jreq);
            }

            // Check if test was successful
            if (junitRunnerResult.wasSuccessful()) {
                result = AlfrescoTestRunner.SUCCESS;
            }
        }

        // Set up model to send to Web Script template
        //
        // What test did we run
        Map<String, Object> model = new HashMap<String, Object>();
        if (method == null) {
            // We don't have a test method...
            model.put("test", clazzAndMethod);
        } else {
            model.put("test", clazzAndMethod + "#" + method);
        }
        // Overall Alfresco Test runner result
        model.put("result", result);
        // JUnit Runner stats
        if (junitRunnerResult != null) {
            model.put("resultObject", junitRunnerResult);
            model.put("failures", junitRunnerResult.getFailures());
            model.put("failureCount", junitRunnerResult.getFailureCount());
            model.put("ignoreCount", junitRunnerResult.getIgnoreCount());
            model.put("runCount", junitRunnerResult.getRunCount());
            model.put("runTime", junitRunnerResult.getRunTime());
            List<String> throwables = new ArrayList<>();
            if (null != junitRunnerResult.getFailures()) {
                for (Failure failure : junitRunnerResult.getFailures()) {
                    try {
                        throwables.add(AlfrescoTestRunner.serializableToString(failure.getException()));
                    } catch (IOException e) {
                        try {
                            throwables.add(AlfrescoTestRunner.serializableToString(
                                    "Unable to serialize exception."));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            model.put("throwables", throwables);
            model.put("wasSuccessful", junitRunnerResult.wasSuccessful());
        }

        System.out.println("RunTestWebScript: model = " + model);
        System.out.println("RunTestWebScript: Stopped executing");

        return model;
    }
}