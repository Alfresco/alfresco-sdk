#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
${symbol_pound}    Licensed to the Apache Software Foundation (ASF) under one or more
${symbol_pound}    contributor license agreements.  See the NOTICE file distributed with
${symbol_pound}    this work for additional information regarding copyright ownership.
${symbol_pound}    The ASF licenses this file to You under the Apache License, Version 2.0
${symbol_pound}    (the "License"); you may not use this file except in compliance with
${symbol_pound}    the License.  You may obtain a copy of the License at
${symbol_pound}    
${symbol_pound}    http://www.apache.org/licenses/LICENSE-2.0
${symbol_pound}    
${symbol_pound}    Unless required by applicable law or agreed to in writing, software
${symbol_pound}    distributed under the License is distributed on an "AS IS" BASIS,
${symbol_pound}    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
${symbol_pound}    See the License for the specific language governing permissions and
${symbol_pound}    limitations under the License.

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