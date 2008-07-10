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

Sourcesense Alfresco Customization (Ant build):
------------------------------------------------

The project can be built by the means of two build systems:

- Ant (suggested for fast startup and offline building)
- Maven2 (suggested for structured team work and release)

Both build systems provide Alfresco customized build and deploy on tomcat.
WARNING: Make sure you run tomcat with appropriate JVM size (JAVA_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m")

Ant Build Prerequisites:
------------------------
In order the build to work properly and resolve alfresco jar and war dependencies, 
you have to download a version of alfresco in the folder

$PROJECT_HOME/tools/ant/deps/alfresco

or conveniently run the script:

$PROJECT_HOME/tools/ant/ant-boostrap.sh

which basically downloads a specified (Community) Alfresco distribution from Sourcesense public Maven repositories,
and unpacks it in the mentioned folder. The WEB-INF/lib jars is added to build classpath.


Alfresco ant WAR build:
-----------------------

- use build.xml in the root folder
- configure your BUILDTIME/RUNTIME properties in 
  src/main/properties/<yourEnv>/application.properties and build with -Denv=<yourEnv> 
  (you can either commit this file or add it to svn:ignore for local usage)

----
NB: Before you can actually use the build you have to specify where to find the alfresco war (this is not needed for maven build who retrieves dependencies from public sourcesense maven repo, in case of community artifacts, from private maven repo for enterprise artifacts). To do so please create a build.properties file on the project root and fill it with the property:

alfresco.dir

pointing to the exploded war directory of the alfresco instance you want to use 
---


Common usage tasks:
-------------------

ant clean package
ant install (in tomcat)
ant remove (from tomcat)

from the command line calls the default target (package) .

For fast one-shot deploy: ANT_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m" ant [remove] install

FAQ:
----

WARNING: the ant tomcat plugin is pretty memory intensive so please use ant with appropriate JVM memory size (ANT_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m")


TODO:
----
- Support content restore
