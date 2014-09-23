package org.alfresco.maven.rad.testframework;

import org.alfresco.repo.avm.util.RawServices;
import org.alfresco.service.ServiceRegistry;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

/**
 * This is a base class that can be extended in order to get the ZiaAlfrescoTestRunner,
 * Spring Context and Alfresco Service registry setup for a test class.
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
@RunWith(value = AlfrescoTestRunner.class)
public abstract class AlfrescoIntegrationTestCase
{

    private ApplicationContext m_springApplicationContext = null;
    private ServiceRegistry m_alfrescoServiceRegistry = null;

    public ApplicationContext getSpringApplicationContext()
    {
    	if (null == m_springApplicationContext) {
    		RawServices rawServices = RawServices.Instance();
    		if (null != rawServices) {
    			m_springApplicationContext = rawServices.getContext();
    		}
    	}
    	return m_springApplicationContext;
    }
    
    public ServiceRegistry getAlfrescoServiceRegistry()
    {
    	if (null == m_alfrescoServiceRegistry) {
    		ApplicationContext ctx = getSpringApplicationContext();
    		if (null != ctx) {
    			Object bean = ctx.getBean("ServiceRegistry");
    			if (null != bean && bean instanceof ServiceRegistry) {
    				m_alfrescoServiceRegistry = (ServiceRegistry) bean;
    			}
    		}
    	}
    	return m_alfrescoServiceRegistry;
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