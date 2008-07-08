#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#    
#    http://www.apache.org/licenses/LICENSE-2.0
#    
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

Ant/m2 runtime properties management
------------------------------------

Both build systems will comply to the following convention for properties:

- if -Denv=<yourEnv> property is specified at build time properties will be 
  looked up in folder 
  
  src/main/properties/<yourEnv>/application.properties
  
  and copied in the classpath under
  
  alfresco/extension/application.properties
  
- if no "env" system property is specified env=local default value will be used
  
  
Buildtime properties management - Note for Ant Users:
-----------------------------------------------------
Here you can also configure buildtime properties which will be loaded in ant
build context with the same aforementioned convention.
This is done for tomcat ATM.

Buildtime properties management - Note for Maven Users:
-----------------------------------------------------
You should configure your buildtime properties as suggested by the maven 
cascading build properties system, i.e. externalizing them from the project 
by the means of settings.xml file.