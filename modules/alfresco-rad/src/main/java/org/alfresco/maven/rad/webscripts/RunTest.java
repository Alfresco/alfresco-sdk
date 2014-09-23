package org.alfresco.maven.rad.webscripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.maven.rad.testframework.AlfrescoTestRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This webscript works in consort with the ZiaAlfrescoTestRunner. When a test is run from an IDE or 
 * command line, our test runner sends a proxied request to perform the test to this script. This runs
 * the test and wraps the results up so that the test initiator can be fooled into thinking they are
 * running the tests locally.
 * 
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
public class RunTest extends DeclarativeWebScript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        String clazz = req.getServiceMatch().getTemplateVars().get("clazz");
        String method = null;
        String result = AlfrescoTestRunner.FAILURE;
        Result jres = null;
        if (null == clazz) {
            clazz = req.getParameter("clazz");
        }
        if (null == clazz) {
            clazz = "not provided";
        } else {
            Class c = null;
            try {
                String[] classAndMethod= clazz.split("#");
                if (classAndMethod.length > 1) {
                    clazz = classAndMethod[0];
                    method = classAndMethod[1];
                }
                c = Class.forName(clazz);
                if (null == method) {
                    jres = JUnitCore.runClasses(c);
                } else {
                    Request jreq = Request.method(c, method);
                    jres = new JUnitCore().run(jreq);
                }
                if (jres.wasSuccessful()) {
                    result = AlfrescoTestRunner.SUCCESS;
                }
            } catch (ClassNotFoundException ex) { }
        }

        Map<String, Object> model = new HashMap<String, Object>();
        if (null == method) {
            model.put("test", clazz);
        } else {
            model.put("test", clazz + "#" + method);
        }
        model.put("result", result);
        if (null != jres) {
            model.put("resultObject",jres);
            model.put("failures", jres.getFailures());
            model.put("failureCount",jres.getFailureCount());
            model.put("ignoreCount",jres.getIgnoreCount());
            model.put("runCount",jres.getRunCount());
            model.put("runTime",jres.getRunTime());
            List<String> throwables = new ArrayList<String>();
            if (null != jres.getFailures()) {
                for (Failure failure : jres.getFailures()) {
                    try {
                        throwables.add(AlfrescoTestRunner.serializableToString(failure.getException()));
                    } catch (IOException e) {
                        try {
                            throwables.add(AlfrescoTestRunner.serializableToString("Unable to serialize exception."));
                        } catch (IOException e1) { }
                    }
                }
            }
            model.put("throwables", throwables);
            model.put("wasSuccessful",jres.wasSuccessful());
        }
        return model;
    }
}
/*
	Licensed to the Apache Software Foundation (ASF) under one or more
	contributor license agreements.  See the NOTICE file distributed with
	this work for additional information regarding copyright ownership.
	The ASF licenses this file to You under the Apache License, Version 2.0
	(the "License"); you may not use this file except in compliance with
	the License.  You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/