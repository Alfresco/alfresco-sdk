package org.alfresco.demoamp.integrationtest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.alfresco.demoamp.Demo;
import org.alfresco.maven.rad.testframework.AlfrescoTestRunner;
import org.alfresco.repo.avm.util.RawServices;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;

/**
 * This is a test class that does some very light testing to provide some examples
 * of things that can be done with the ZiaAlfrescoTestRunner. It does not use the
 * ZiaAlfrescoTestCase.
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
@RunWith(value = AlfrescoTestRunner.class)
public class DemoBasicIT
{
    @Rule public MethodRule testAnnouncer = new MethodRule() {
        @Override
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            System.out.println("Running IntegrationTest: " + method.getName() + "()");
            return base;
        }
    };

    /**
     * This test makes a new instance of the Demo bean, it does not use spring to do this.
     * This could be interesting because the init method writes some output that you can
     * then look for in the Alfresco log files.
     */
    @Test public void bootstrapInitializingBean()
    {
        Demo b = new Demo();
        b.init();
        assertThat(b, notNullValue());
    }

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
     * Similar to the springContextTest, this uses the ServiceRegistry to get
     * a handle on the DictionaryService and then uses that to make sure we
     * are in a live repository that has at least one aspect defined.
     *
     * ASSUMPTION: At least one Aspect exists in any deployment of Alfresco.
     */
    @Test public void locateAlfrescoServices()
    {
        ApplicationContext ctx = RawServices.Instance().getContext();
        assertThat(ctx, notNullValue());

        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean("ServiceRegistry");
        assertThat(serviceRegistry, notNullValue());

        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
        assertThat(dictionaryService, notNullValue());

        Boolean haveAspects = (dictionaryService.getAllAspects().size() > 0);
        assertThat(haveAspects, is(Boolean.TRUE));
    }
    
    // This is intentionally ignored to show that we honor this annotation
    @Ignore
    @Test public void ignored()
    {
        fail("This test should have been ignored!");
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
