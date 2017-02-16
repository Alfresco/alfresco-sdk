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

import org.alfresco.rad.SpringContextHolder;
import org.alfresco.service.ServiceRegistry;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;

/**
 * Abstract Integration Test class to be used
 * by Alfresco Integration Tests. Gives access to
 * Alfresco Spring Application context and the
 * {@link ServiceRegistry} that should be used when
 * accessing Alfresco Services.
 *
 * @author martin.bergljung@alfresco.com
 * @since 3.0
 */
public abstract class AbstractAlfrescoIT {
    private ApplicationContext applicationContext = null;
    private ServiceRegistry serviceRegistry = null;

    /**
     * Print the test we are currently running, useful if the test is running remotely
     * and we don't see the server logs
     */
    @Rule
    public MethodRule testAnnouncer = new MethodRule() {
        @Override
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            System.out.println("Running " + getClassName() + " Integration Test: " + method.getName() + "()");
            return base;
        }
    };

    protected String getClassName() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getName();
        } else {
            return getClass().getName();
        }
    }

    protected ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            SpringContextHolder springContextHolder = SpringContextHolder.Instance();
            if (springContextHolder != null) {
                applicationContext = springContextHolder.getApplicationContext();
            }
        }

        return applicationContext;
    }

    protected ServiceRegistry getServiceRegistry() {
        if (serviceRegistry == null) {
            ApplicationContext ctx = getApplicationContext();
            if (ctx != null) {
                Object bean = ctx.getBean("ServiceRegistry");
                if (bean != null && bean instanceof ServiceRegistry) {
                    serviceRegistry = (ServiceRegistry) bean;
                }
            }
        }

        return serviceRegistry;
    }
}