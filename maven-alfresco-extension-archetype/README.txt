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
   
NOTE: This file maybe obsolete. Please refer to documentation site.


Maven2/Ant Alfresco extension archetype
---------------------------------------

What is an Alfresco extension?
------------------------------
We define an "Alfresco extension" (or customization) a custom build of Alfresco which does not impact on Alfresco source code (while depends on binary releases of the libraries and webapp).
This is realized overriding (and in certain particular cases overwriting) Alfresco default configuration using Convention over Configuration hooks (e.g. classpath:alfresco/extension/*-context.xml autoloading), in order to ease integration in enterprise environments and to simplify full lifecycle management (from scratch-start to release and deploy) also for non experienced developers, resulting in a less error prone controlled process.
Alternative to an "Alfresco extension" is the "Alfresco Module" (or AMP) which is meant to be deployed on an existing Alfresco instance and above all to coexist with other modules on top of this instance: as a general rule of thumb an extension is an exclusive customization while a module is one of many customizations which have a separate lifecycle (and owner).
For an maven2 archetype of AMP Alfresco build please have a look at:

https://dev.sourcesense.com/cargo/maven2-sites/maven-alfresco-extension-archetype


Quick Start:
------------
Wanna skip all the discussion (or you know already the thing) and want a zero conf maven2 alfresco customization project generation?
Just run:

mvn  org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-7:create -DarchetypeGroupId=${pom.groupId} -DarchetypeArtifactId=${pom.artifactId} -DarchetypeVersion=${pom.version} \
  					  -DgroupId=mycompany.com  -DartifactId=my-alfresco-customization -Dversion=0.1-SNAPSHOT 
					  -DremoteRepositories=http://repository.sourcesense.com/maven2 (archetype is deployed there)
					  -DpomRemoteRepositories=http://repository.sourcesense.com/maven2 (wagon-dav ssh version patched is here)


Introduction:
-------------

This archetype developed within Sourcesense (http://www.sourcesense.com) aims to provide a standardized approach to development, release and deployment of Alfresco extensions (as opposed to AMP builds). Using standard m2 lifecycle commands (mvn compile package deploy) and generally available plugins (cargo, release, assembly) we are able to cover a very high percentage of Alfresco lifecycle common use cases.
It can be used both with Maven2 and Ant  build systems, but it must be clear that *only* the Maven2 approach provides all the automation features we will describe in the next paragraphs. 
In addition to that the m2 approach provides a zero-conf approach while ant requires (as usual) manual gathering and selection of required alfresco libraries and webapp (please refer to README-ant.txt for further info about the Ant build). Last but not the least, m2 build is more likely to be maintained and improved (especially in the likely case Alfresco moves to maven2). 
For more details on the m2 apprach please refer instead to README-m2.txt.



Features (maven2 only):
----------------------

-- Easy dependency management and upgrade testing

Webapp WAR dependency on Alfresco community/enterprise wars (or even environment dependent builds, e.g. WAR without log4j for Jboss-4.0.X) can be easily switched and build
for different platforms in custom maven2 build profiles. 
Alfresco JAR dependency are only included for compilation (scope="provided") and IDE configuration and must manually be kept in sync with the WAR version (we used a maven style library naming, renaming Alfresco distributed libraries).

-- Single source environment dependent properties filtering:

One of the biggest flaws (and most frequent cause for runtime errors) for Alfresco (and more in general for webapplications) is a poor build system which requires manual editing of properties in the source code in a developer/environment dependent way. With this build system just by specifying the -Denv=<yourEnv> command line property your able to switch between different buildtime/runtime properties configuration and also have all common properties in one file for simple edit. No more messing up with Spring files when what you need to change is just a property.
Properties files are stored in src/main/properties/<yourEnv> and get automatically copied over the classpath to provide complete code and configuration separation. Typical examples of developer/environment dependent properties are: db connection, LDAP server connection, alf_data location, SMB/FTP server enabling and configuration.

-- Automated (convention over configuration based) full Alfresco repository restore (boostrap)

Alfresco bootstrap process requires manual actions to be taken, and namely to include the restore-context.xml and the 6 full repo export ACP files to be included in the alfresco/extension/restore package. With this build this becomes as easy as specifying the -DrestoreVersion=<myVersion> build property which will automatically pick up ACP files (alfresco package name = "export") found in tools/export/<myVersion> and include them in the right position in the classpath. When the property is specified also the restore-context.xml to load them is automatically included

-- Automated LDAP configuration (and associated personService behaviors)

Fill in your application.properties with appropriate LDAP connection and synchronization values and by specifying the -Denterprise build property on the command line you will have the LDAP authentication and sychronization context already configured. No more hassles in digging into huge Spring files ;)

-- Jboss/Tomcat cargo based deployment

Using cargo we're able to provide (local) Jboss and (local/remote) Tomcat deployment. This comes handy both in the development phases and in the release phases when we want the release operation (scm tagging, m2 repo deployment, version update, appserver deployment) to be transactional and managed by maven. For additional configuration please refer to maven-cargo-plugin configuration and fine tune your $M2_HOME/conf/settings.xml (for system level settings) or ~/.m2/settings.xml (for user level settings) in order to match your host/credentials configuration

-- Zero conf startup

Community artifacts (alfresco 2.x) artifacts are available on Sourcesense public repository (http://repository.sourcesense.com/maven2) and allow all the default dependencies specified in this POM to be successfully retrieved with no additional configuration. If you want to use instead enterprise dependencies you either need to have a private access to Sourcesense private repositories or push alfresco for releasing this artifacts on public Alfresco maven repositories 

--- Configurable log location and easy database setup
By editing the <log.dir> POM property in the appropriate profile you can have the setting applied where it makes sense (jboss configures everything centrally).
Database creation/remove scripts are filtered according to the build profile, so you can directly run them for the different environment they were 'built' for.

--- Easy ide (Eclipse) integration
As difficult as running mvn eclipse:eclipse and hitting "Refresh in your eclipse proje"

--- Release process
One command release deploy for this artifact and for the project generated from it. See README-m2.txt for more details.

--- Integrated documentation system
Documentation is integrated and can be modified with one of the supported formats (Xdoc, APT, FML): project resources can be also linked (e.g.physical documents) . 
To test an example typing 'mvn site:run' will run your site in jetty embedded on port 8080, with redeployment of sited source files. 
To stage the site just type 'mvn site:stage' and to deploy to remote site location, guess what, 'mvn site:deploy'

TODO:
----
See TODO.txt


ROADMAP:
--------
See ROADMAP.txt


REFERENCES:
----------
http://www.mortbay.org/maven-plugin
http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin
http://maven.apache.org/plugins/maven-war-plugin/war-mojo.html
http://maven.apache.org/guides/getting-started/index.html#How_do_I_filter_resource_files
http://maven.apache.org/pom.html#Final
http://maven.apache.org/plugins/maven-resources-plugin/resources-mojo.html
http://jira.codehaus.org/browse/CARGO-416
http://www.javaworld.com/javaworld/jw-05-2006/jw-0529-maven.html
http://cargo.codehaus.org/Maven2+Plugin+Installation
http://cargo.codehaus.org/Deploying+to+a+running+container
http://maven.apache.org/plugins/maven-release-plugin/
http://www.javaworld.com/javaworld/jw-02-2006/jw-0227-maven.html?page=2
http://maven.apache.org/doxia/references/apt-format.html
http://maven.apache.org/guides/mini/guide-site.html
