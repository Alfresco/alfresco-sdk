package org.alfresco.demoamp.integrationtest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.avm.util.RawServices;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;

import org.alfresco.maven.rad.testframework.AlfrescoIntegrationTestCase;

/**
 * This is a test class that does some very light testing to provide some examples
 * of things that can be done with the ZiaAlfrescoTestRunner and ZiaAlfrescoTestCase.
 * For a test case that doesn't extend from ZiaAlfrescoTestCase you can check out
 * DemoBasicIT.java.
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
public class DemoAlfrescoIT extends AlfrescoIntegrationTestCase
{
    @Rule public MethodRule testAnnouncer = new MethodRule() {
        @Override
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            System.out.println("Running IntegrationTest: " + method.getName() + "()");
            return base;
        }
    };
    
    /**
     * Alfresco provides a singleton called RawServices that provides access to the
     * main spring context. Given this context we can get beans that allow us to
     * interact with the repository. In your test classes, you might want to put
     * this code in a @Before method and make the context and serviceRegistry
     * class members that can be used from your tests. Otherwise you are likely
     * to repeat this a bunch.
     */
    @Test public void captureSpringContext()
    {
        ApplicationContext ctx = RawServices.Instance().getContext();
        assertThat(ctx, notNullValue());

        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean("ServiceRegistry");
        assertThat(serviceRegistry, notNullValue());
    }
    
    /**
     * Building on the previous tests, this uses the ActionService to run the
     * DemoAction that is provided as part of the zia-alfresco-quickstart. It
     * does this on the Company Home node which it obtains using the
     * NodeLocatorService. As an added bonus this makes sure that the we receive
     * a return value from the action when we execute it.
     * 
     * This test requires the example-alfresco-module. Enable the dependency in the pom to run.
     */
    /*@Test public void executeActionOnCompanyHome()
    {
        ServiceRegistry serviceRegistry = this.getAlfrescoServiceRegistry();
        assertThat(serviceRegistry, notNullValue());

        ActionService actionService = serviceRegistry.getActionService();
        assertThat(actionService, notNullValue());

        NodeService nodeService = serviceRegistry.getNodeService();
        assertThat(nodeService, notNullValue());

        NodeRef nodeRef = serviceRegistry.getNodeLocatorService().getNode(CompanyHomeNodeLocator.NAME, null, null);
        assertThat(nodeRef, notNullValue());

        Action action = actionService.createAction("example-alfresco-module-demoAction");
        assertThat(action, notNullValue());
        actionService.executeAction(action, nodeRef);
        Serializable result = action.getParameterValue("result");
        assertThat(result, instanceOf(String.class));
        assertThat((String)result, is("result"));
    }*/

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