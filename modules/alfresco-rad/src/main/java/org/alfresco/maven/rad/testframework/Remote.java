package org.alfresco.maven.rad.testframework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By default the AlfrescoTestRunner will attempt to find a
 * running Alfresco instance at http://localhost:8080/alfresco
 * This annotation can be applied to a test class to have the
 * proxy calls go to a different host/port.
 * 
 * Integration testing framework donated by Zia Consulting
 *
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {

    String endpoint() default "http://localhost:8080/alfresco";
    
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