*************
Prerequisites
*************

- The only prerequisite to follow reading this document is to have Apache Maven 3.0.3+ (or higher) installed
on your machine; there is no preferred IDE nor web container and database requirements needed.

- It is strongly advised to use the Apache Maven official binary distributions downloaded from maven.apache.org; avoid
OS-specific distributions as much as possible.

- add MAVEN_OPTS="-Xms256m -Xmx1G -XX:PermSize=300m" to your environment if your plan to launch tests / run embedded in Jetty

*****
Usage 
*****
This multi-module project manages an All-in-One Alfresco project and it's composed by the following modules:
- amp --> An Repository Tier AMP project, demonstrating sample project structure and demo component loading. Can be configured to work for Share 
- alfresco --> An alfresco.war Repository Extension, overlaying the Alfresco WAR with custom resources / classes and depending on the 'amp' project
- share --> A share.war extension, overlaying the Share WAR with custom resoruces / classes
- solr --> A solr.zip overlay / customization to configure solr cores properties

The project provides support for typical development lifecycle use cases like:
- packaging
- testing
- run embedded
- integration testing
- release and distribution

of your Alfresco (and platform components) related artifacts. The AMPs produced with this project are fully compatible with Alfresco MMT, 
in fact the Alfresco Maven plugin - used to manage AMPs in this SDK - embeds the official Alfresco MMT to install depended AMPs. 

Project layout:
--------------
parent-project
            |-> amp
            |-> alfresco
            |-> share
            |-> solr
            |-> wcmqs (Alfresco Web Quick Start)
            |-> runner (a Jetty embedded runner / integration test runner)
            
Useful commands
---------------
Running from parent project: 
- mvn package --> Runs unit tests (if present) and packages all modules in their respective target/ 
- mvn install --> Runs unit tests  (if present) , packages and installs AMP in local Maven repository
- mvn install -Dmaven.test.skip=true --> Packages and installs AMP in local Maven repository, skipping tests
- mvn integration-test -Prun --> Runs unit tests and packages all modules and the runner project runs Alfresco, Share and Solr in Jetty + H2
- mvn clean -Ppurge --> Removes DB, alf_data and log files

Running from runner:
- 'mvn jetty:run -Prun' or 'mvn integration-test -Prun' to quickly run already packaged webapps

Properties management 
---------------------
Properties are configurable at 2 levels:
- POM properties (in the parent POM or at single module level)
- direclty in each modules *.properties files, particularly
   - src/main/properties/<env>/alfresco-global.properties (environment dependent properties for Alfresco WARs)

Useful properties that can be fully controlled directly in the POM are:

alfresco.data.location (default = alf_data_dev) 
alfresco.db.name (default = alf_dev)
app.log.dir (default = target/)
app.log.root.level (default = INFO)

***************************
Maven Alfresco SDK Overview
***************************

The Maven Alfresco SDK is an effort that have been developing in the last 5 years - mostly driven by community efforts on
Google Code (http://code.google.com/p/maven-alfresco-archetypes) - which delivers archetypes for building Alfresco
integration project with Maven; builds are based on Alfresco Community and Enterprise artifacts released on the
Alfresco Artifacts Repository at https://artifacts.alfresco.com

The Maven Alfresco SDK is full rewriting of the Maven Alfresco Lifecyle (latest version 3.9.1) and it's composed of 3 efforts:

a) POM files: 
    - alfresco-sdk-parent: provides lifecycle features and behaviors for typical Alfresco development projects
    - alfresco-platform-distribution: describes and provides dependencyManagement for artifacts of each Alfresco release

b) alfresco-maven-plugin:
    - defines the AMP packaging type and lifecycle in Maven
    - emdeds Alfresco MMT to provide safe installation of single / multiple AMP -> WAR 
    - will potentially grow including more use cases around Alfresco / Maven
    
c) The definition of (initially 2) archetypes that show some simple project's configuration using
the parent POMs
    - AMP archetype (this archetype)
    - All-in-One multi-module archetype including
        - Alfresco Repository AMP
        - Alfresco Repository extension project (WAR, also depending on the AMP)
        - Share customization project (WAR)
        - Solr customization project (WAR)
        - Jetty embedded runner for the full platform (mvn clean install -Prun)


****************************************
Why using this SDK might be a good idea?
****************************************
The main reported advantages of using the Maven Alfresco SDK are:

- IDE-independent SDK, all build-related features are provided by Apache Maven, which is the only
prerequisite to use this SDK 

- No IDE manual configuration, all modern IDEs offer advanced Maven integrations, so 
feel free to use Eclipse, IntelliJ, or any other IDE to write your code and leverage Maven

- Process ready: scales from quick start rapid application development, to be seamlessly 
integrated in enterprise devleopment processes like Continuous Integration and Release

- Language independent, you don't like Maven? You can still use Ant, Ivy, Buildr, Gradle, Leiningen or any other build
system that is compatible with Maven artifact resolution mechanism; just configure artifacts.alfresco.com as (one of) your
Maven repositories and you're ready to go

- Javadoc and Sources support, provided by artifacts.alfresco.com related Maven artifacts; you don't need to manually
configure your IDE to attach (manually downloaded) sources to your (manually downloaded) binaries.

- Clean and readable, the build logic related with OOTB Alfresco features is wrapped in 50 lines of pom.xml

- Advanced build functionality, inherited by your parent POMs you can use embedded databases and j2ee
containers with (almost) no configuration at all, among other features exposed below.

- One mvn command to generate, one mvn command to run embedded, this is all you need to do to have a local Alfresco running
on an empty laptop

- Supports community and enterprise flawlessly, allowing to switch one to another very easily