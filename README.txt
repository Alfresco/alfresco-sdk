This document needs updating...

Archetypes using the SDK can be generated with:

mvn archetype:generate -DarchetypeCatalog=https://artifacts.alfresco.com/nexus/content/groups/public-snapshots/archetype-catalog.xml

**********
Disclaimer
**********

This codebase is currently work in progress; the effort have been split in three directions:
a) The definition of 2 POM files that can handle versions and common build behaviors/features
b) The writing of an alfresco-maven-plugin to enable amp-to-war overlay provided by Alfresco
Repository built-in features (ModuleManagementTool) and replace the maven-amp-plugin (big 
rewriting of the maven-war-plugin)
c) The definition of (initially 2) archetypes that show some simple project's configuration using
the parent POMs

Most of the build features have been successfully ported to a new structure, whose main advantage is
to keep pom.xml files extremely simple and readable (since a lot of logic have been moved to the parent).
Inherited behaviours are configurable simply defining specific properties in the project's or module's pom.xml
(see documentation below).

Here follows a list of build features that have not been ported yet:
- Maven SCM and release
- JBoss run
- Tomcat remote deployment
- Jetty Java source and resources reloading (Jetty currently runs .war files)
- Maven Site generation
- Maven reporting
- Maven distributionManagement

Apart from SCM and release, all the mentioned features are probably best suited for a more enterprise archetype,
using the Alfresco Web Integration POM as parent and adding more build features around Maven, Tomcat and JBoss.

Regarding the POM files, I'd like to see them deployed on maven.alfresco.com, hopefully generated (by the
Alfresco build?) and tested for each Alfresco release.

--- oOo ---

*************
Prerequisites
*************

- Maven 3.0.3 (official binary distribution from maven.apache.org)

--- oOo ---

*************
Build and Run
*************

-----
Build
-----
All the steps that follow are necessary because neither POMs nor
alfresco-maven-plugin are yet available on maven.alfresco.com; as soon
as their implementation is consolidated, you will be able to skip
this first part and just enjoy the second.
---
mvn clean install
(with empty repository, Maven will download 365Mb)

---

---
Run
---
cd archetypes/quickstart-allinone-archetype
MAVEN_OPTS="-Xms256m -Xmx1G -XX:PermSize=300m" mvn clean package -Drun
-> http://localhost:8080/alfresco and http://localhost:8080/share
--- OR
cd archetypes/quickstart-amp-archetype
MAVEN_OPTS="-Xms256m -Xmx1G -XX:PermSize=300m" mvn clean package -Drun-amp
-> http://localhost:8080/quickstart-amp-archetype
---

--- oOo ---

**********
Archetypes
**********

---------------------------
ALFRESCO ALLINONE ARCHETYPE
---------------------------
- run mvn clean install to package all apps
- run mvn clean install -Prun to run the full platform embedded in Jetty/H2
---

---
ALFRESCO AMP ARCHETYPE
---

---

--- oOo ---

*********
POM files
*********

-------------------------------
ALFRESCO PLATFORM POM
-------------------------------
* Describes the Alfresco platform. Lives in the Alfresco SVN and gets deployed at every release.
* DependencyManagement for all Alfresco commonly used JAR/WAR/AMP artifacts
* IInherits alfresco-developer-parent POM

---
ALFRESCO DEVELOPER PARENT POM
---
* Alfresco Repository Log and storage cleaning
---
Activation: built-in
---
When mvn clean is invoked, all files produced by Maven runs must be removed; this is the list of
filesets inherited from alfresco-developer-parent:
 * target/ (default behaviour)
 * *.log
 * ${alfresco.data.location}
---
Properties
---
<alfresco.data.location>alf_data_dev</alfresco.data.location>
---

---
* AMP overlay into an Alfresco (or Share) Extension
---
Activation: built-in
---
When your project (or sub-module) is a <packaging>war</packaging>, you can automatically include one or more
AMP files by defining the dependencies into the pom.xml, as follows:
<dependency>
    <groupId>com.mycompany</groupId>
    <artifactId>amp-module</artifactId>
    <version>1.0-SNAPSHOT</version>
    <type>amp</type>
</dependency>
The AMP files will be overlayed on top of your current WAR customizations, therefore
they can override the content of the original WAR.
---

---
* Multi-environment property filtering
---
Activation: exists src/main/properties
---
You can enable multi-environment property filtering by simply creating the
src/main/properties/${env}/${webapp.resource.filter} file with your property values;
all files included in src/main/resources and src/main/properties will be filtered
with your properties defined; in order to switch between environments,
simply attach -Denv=yourenv to your mvn commands.
---
Properties
---
<env>local</env>
<webapp.resource.filter>alfresco-global.properties</webapp.resource.filter>
<webapp.resource.build.folder>${project.build.outputDirectory}</webapp.resource.build.folder>
<webapp.name>${project.artifactId}</webapp.name>
---

---
* Jetty H2 configuration
---
Activation: exists jetty/jetty.xml
---
You can enable Jetty to run your application(s); by default Jetty will run all contexts
using jetty/jetty.xml as Jetty Server configuration, allowing to add the jndi resource
needed to start Alfresco Repository webapp; if you want to run multiple webapps - for 
example share and alfresco - follow the example listed below:
<plugin>
    <groupId>org.mortbay.jetty</groupId>
    <artifactId>maven-jetty-plugin</artifactId>
    <executions>
        <execution>
            <id>run</id>
            <goals><goal>run</goal></goals>
            <phase>package</phase>
            <configuration>
            	<contextPath>/</contextPath>
            	<webAppSourceDirectory>.</webAppSourceDirectory>
            	<webXml>jetty/root-web.xml</webXml>
                <contextHandlers>
                    <contextHandler implementation="org.mortbay.jetty.webapp.WebAppContext">
                        <war>${project.basedir}/../alfresco/target/alfresco.war</war>
                        <contextPath>/alfresco</contextPath>
                    </contextHandler>
                    <contextHandler implementation="org.mortbay.jetty.webapp.WebAppContext">
                        <war>${project.basedir}/../share/target/share.war</war>
                        <contextPath>/share</contextPath>
                    </contextHandler>
                </contextHandlers>
            </configuration>
        </execution>
    </executions>
</plugin>
---