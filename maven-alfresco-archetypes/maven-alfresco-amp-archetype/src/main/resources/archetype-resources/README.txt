======================================================================
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
======================================================================


---------------------------------------
Archetype Release: Archetype - maven-amp-archetype
Author: g.columbro@sourcesense.com
http://forge.alfresco.com/m2alfresco

Contacts: 
- alfresco-dev@lists.sourcesense.com (developer)
- alfresco@sourcesense.com (commercial)

See Also:
http://wiki.alfresco.com/wiki/Module_Management_Tool
-----------------------------------------------------



M2 Instructions for Alfresco AMP:
--------------------------------

The project can be built using Maven2.


Alfresco maven2 AMP build
-------------------------


One command quickstart:
$ MAVEN_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m" mvn  clean integration-test -P webapp 

Runs the jetty embedded with overlayed the currently developed AMP


FEATURES:
---------

- AMP customized build : 										 mvn clean package
- AMP dependencies management: 									'mvn clean package' can take care of overlay deps
- Alfresco webapp integration via war creation					 mvn clean package -P webapp
- jetty embedded build for fast testing,						 mvn clean integration-test -P webapp
- install/deployment of Alfresco compatible AMPs on a m2 repo    mvn install / deploy  [ params ]

- using the maven-amp-plugin (http://forge.alfresco.com/maven4alfresco) WAR can depend on AMP artifacts and have them overlayed within the maven
lifecycle, avoiding the boring MMT run.

Specific dependencies and different Alfresco versions can be specified in 'webapp' profile dependecies. Alfresco test webapp is overlayed (apart with 
all AMP and WAR dependencies specified) also with a sensible default 'alfresco/extension' folder to have a clean safe reproduceable and portable 
default alfresco run. Find these test configuaration files in 'src/test/resources' and environment dependent properties in src/test/properties/<env>/application.properties


PROJECT LAYOUT
--------------

src --------------------------------------------------------> (source folder)
		|
		|__ main ___ __ resources --------------------------> mapped in the classpath. Add here classpath resources
		|			|
		|			|__ config-----   alfresco module config goes here.
		|			|			  |	  Get's copied (by best practice) by build into package alfresco/module/artifactId.
		|			|			  |   This convention is used by the archetype and by the AMP unpacker.
		|			|			  |__ ex: alfresco/module/mymodule ---> Example Alfresco module config 
		|			|
		|			|
		|			|__ java ------------------------------->  customization java classes
		|			|
		|			|__ webapp -----------------------------> AMP overlay folder - this folder is overlayed (so may overwrite other dependent AMPs)

		|			
		|__ test __ __ resources 
						  |
		|			|	  |__ alfresco/extension ---> alfresco overriding Spring contexts
		|			|__ properties ------------------------->  environment aware application properties files  
		|			|			  |
		|			|			  |__ local  ------------->  default application.properties
		|			|
		|
		target - Project build dir


