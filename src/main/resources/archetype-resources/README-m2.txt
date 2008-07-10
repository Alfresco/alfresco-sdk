---------------------------------------
Archetype Release: Archetype - maven-alfresco-archetype
Author: g.columbro@sourcesense.com
Contacts: alfresco@sourcesense.com
---------------------------------------
M2 Instructions for Alfresco Extension:


The project can be built by the means of two build systems:

- Ant (suggested for fast startup and offline building) --> see README-ant.txt
- Maven2 (suggested for structured team work and release) --> explained in this file

Both build systems provide Alfresco customized build and deploy on tomcat.
WARNING: Make sure you run tomcat/jboss with appropriate memory size (JAVA_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m")



Alfresco maven2 WAR build
-------------------------

FEATURES:
---------
- centralized environment aware properties based configuration (common use cases covered with just properties file editing, contributions are more than welcome!)
- Content Bootstrap
- WAR customized build
- environment dependent deploy
- easy switch of alfresco version
- jetty embedded build
- jboss (local) and tomcat (local/remote) deployment support

PROJECT LAYOUT
--------------

src --------------------------------------------------------> (source folder)
		|
		|__ main ___ __ resources --------------------------> mapped in the classpath  
		|			|			  |
		|			|			  |__ alfresco/extension ---> alfresco overriding Spring contexts
		|			|
		|			|__ properties ------------------------->  environment aware application properties files  
		|			|				|
		|			|				|__ local  ------------->  default application.properties
		|			|
		|			|__ java ------------------------------->  customization java classes
		|			|
		|			|__ webapp -----------------------------> alfresco webapp overlay folder - this folder is overlayed (so may overwrite alfresco war)
		|						|
		|						|__ WEB-INF	----------------> drop in this folder WEB-INF custom / overwriting files (e.g. faces-config-custom.xml / web.xml)	
		|			
		|__ test

tools --
		|__ ant ---------------------------------------------> ant tools
		|__ m2  ---------------------------------------------> maven2 tools
		|__ export ------------------------------------------> support folder for automatic restore (drop acp+xml files here)
		|__ mysql -------------------------------------------> mysql setup/remove scripts, filtered based on the profile into  

target - Project build dir



Lifecycle HOWTO - Usage
-----------------------

--- Fast one shot build and embedded jetty run-war|run-exploded  [and restore bootstrap] [and customize webappName] [and integrate LDAP]:

' MAVEN_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m mvn integration-test [-Denv=yourEnv] [-DrestoreVersion=versionToRestore] [-DwebappName=yourCustomWebappName] [-Denterprise] ' 

(default env=src/main/properties/local/application.properties)

--- [Re]Deploy on Jboss (locally) [or on Tomcat locally or remotely]:

'  mvn clean package [cargo:undeploy] cargo:deploy -Pjboss [-Ptomcat] ' 

Note: Jboss or Tomcat must be running prior to deployment

--- Packaging and Manual Deploy:
- Invoke  ' mvn clean package -Denv=targetEnv ' 
from the root project's folder; for a list of available environments check src/main/properties/<env>/application.properties
or create a custom src/main/properties/<env>/application.properties
- Copy target/${webapp.name}.war in $JBOSS_HOME/server/default/deploy or in $CATALINA_HOME/webapps
	
--- Import data exports:
- Invoke the maven package goal using -DrestoreVersion=<version>, where <version> is the name of one of the folders listed below tools/export

- NB: Due to http://forums.alfresco.com/viewtopic.php?p=29429#29429 with embedded jetty run we run into this bug, which prevents boostrap from working.
Restore properly works and was tested under Jboss and Tomcat.

--- Release process
Just one command: ' mvn release:prepare release:perform '

You can customize goals to be executed in both phases in the maven-release-plugin configuration section in the POM.

FAQ:
----
--- Eclipse configuration
-Run ' mvn eclipse:eclipse ' 
-hit "Refresh" on your Eclipse project

--- Db access problems:
Remember to setup appropriate permissions for selected db / build profile.
You can either edit accordingly and then run: mysql -u root < tools/mysql/db_setup.sql 

or if you use POM property 'alfresco.db.name' you have sql files already filtered (after process-resources phase) in

mysql -u root -p < target/classes/tools/[db_setup,db_remove].sql   

--- Out of memory errors:
Run your build with :
MAVEN_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m" mvn ... ...

--- Content integrity errors on restore running with jetty embedded
Did you remove also alf_data_jetty apart from the alf_jetty db ?

--- Release Problems with LC_ALL
If underlying svn complains about LC_ALL variable please consider running your release prepending:
LC_ALL="C"  (macosx environments, see http://svn.haxx.se/users/archive-2006-07/0320.shtml)

--- Install manually JTA (if needed):
- Download jta-1_0_1B-classes.zip from http://java.sun.com/products/jta/
- mvn install:install-file -Dfile=./jta-1_0_1B-classes.zip -DgroupId=jta -DartifactId=jta -Dversion=1.0.1B -Dpackaging=jar
