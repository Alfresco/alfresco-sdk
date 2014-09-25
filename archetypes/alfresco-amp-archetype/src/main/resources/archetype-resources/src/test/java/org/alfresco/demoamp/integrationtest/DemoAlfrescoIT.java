package org.alfresco.demoamp.integrationtest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.alfresco.maven.rad.testframework.AlfrescoIntegrationTestCase;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.nodelocator.NodeLocator;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;

/**
 * This is a test class that does some very light testing to provide some examples
 * of things that can be done with the AlfrescoTestRunner and ZiaAlfrescoTestCase.
 * For a test case that doesn't extend from AlfrescoTestCase you can check out
 * DemoBasicIT.java.
 * 
 * Integration testing framework donated by Zia Consulting
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
public class DemoAlfrescoIT extends AlfrescoIntegrationTestCase
{
    @Rule public MethodRule testAnnouncer = new MethodRule() {
        @Override
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            System.out.println("Running DemoAlfrescoIT IntegrationTest: " + method.getName() + "()");
            return base;
        }
    };
    
    /**
     * As we have extended AlfrescoIntegrationTestCase we have some 
     * helpers for looking up the application context and the service
     * registry. Additionally by extending this class we don't need to
     * explicitly use the @RunWith annotation on the class.
     */
    @Test public void locateCompanyHome()
    {
    		// Not needed here, but FYI we have a getter for the spring context in case
    	    // you need to get at stuff that is not available from the ServiceRegistry.
    		ApplicationContext springApplicationContext = this.getSpringApplicationContext();
    		assertThat(springApplicationContext, notNullValue());
    		
        ServiceRegistry serviceRegistry = this.getAlfrescoServiceRegistry();
        assertThat(serviceRegistry, notNullValue());
        
        NodeRef companyHome = serviceRegistry.getNodeLocatorService().getNode(CompanyHomeNodeLocator.NAME, null, null);
        assertThat(companyHome, notNullValue());
        
        String companyHomeName = DefaultTypeConverter.INSTANCE.convert( String.class,
        		serviceRegistry.getNodeService().getProperty(companyHome, ContentModel.PROP_NAME) );
        assertThat(companyHomeName, is("Company Home"));
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