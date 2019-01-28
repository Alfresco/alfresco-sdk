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
package org.alfresco.rad;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This Spring bean is aware of the application context in which
 * it is initialized.
 *
 * @author martin.bergljung@alfresco.com
 * @since 3.0
 */
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * The instance of SpringContextHolder
     */
    private static SpringContextHolder springContextHolderInstance;

    /**
     * The Alfresco Spring Application Context.
     */
    private ApplicationContext applicationContext;

    /**
     * Default constructor.
     */
    public SpringContextHolder() {
        //System.out.println("Initializing the SpringContextHolder class.");
        springContextHolderInstance = this;
    }

    /**
     * Return the singleton instance
     *
     * @return the singleton instance
     */
    public static SpringContextHolder Instance() {
        return springContextHolderInstance;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //System.out.println("Setting current Spring Application Context in SpringContextHolder class.");
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
