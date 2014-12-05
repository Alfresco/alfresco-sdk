[![Maven Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features)
#Maven Alfresco SDK

##Introduction

Maven based SDK (Software Development Kit) for Alfresco Development, Testing, packaging and custom project lifecycle management. 

The **Maven Alfresco SDK** Includes support for standard development, rapid development, testing, packaging, versioning and release of your Alfresco integration and extension projects.

##Usage
This section describes how you can use Maven Alfresco SDK for your Alfresco related projects.

###Generate a New Alfresco Project using the Maven Alfresco SDK

Since the Maven Alfresco SDK archetypes are now available from the Maven Central, you can run the following command to interactively generate a new Alfresco project that uses the Maven Alfresco SDK.

`mvn archetype:generate -Dfilter=org.alfresco.maven.archetype:`

###Video: How to use the Maven Alfresco SDK with an IDE
This example video shows how you can generate and run a new Alfresco based project using Eclipse IDE. 

[![Alfresco SDK 2.x howto](http://img.youtube.com/vi/utYZaVe9Nd0/0.jpg)](https://www.youtube.com/watch?v=utYZaVe9Nd0)

You can use the same steps with any other IDE including IntelliJ IDEA.


##Components

The SDK is composed of:

- [Maven Alfresco SDK - Parent POM](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/poms/alfresco-sdk-parent/index.html) - Use in your projects to enable rapid Alfresco development features.
- [Alfresco Platform Distribution POM](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-platform-distribution/latest/index.html) - Pre-configures versions of Alfresco and common 3rd party dependency libraries, for stability purposes.
- [Maven Alfresco Plugin](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/plugins/alfresco-maven-plugin/index.html) - Provides AMP packaging and installation facilities (a la MMT) and other common goals for Alfresco development.
- **Maven Archetypes** (project skeletons) including:
	1. [Alfresco Repository AMP Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/alfresco-amp-archetype/index.html)
	2. [Alfresco Share AMP Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/share-amp-archetype/index.html)
	3. [Alfresco All-in-One Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/alfresco-allinone-archetype/index.html)


## Maven Repositories

###Maven Alfresco SDK artifacts
- The Maven Alfresco SDK is released in Maven Central as of version 2.0-beta-1. 

###Alfresco Enterprise and Community Edition Artifacts
- Alfresco Community artifacts (JARs, WARs, AMPs, poms) and Maven Alfresco SDK artifacts are publicly available. 
- Alfresco (Community and Enterprise) artifacts are hosted in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/nexus/). 
- For Enterprise and Premiere licensed software access you need to get credential via the Alfresco Enterprise Support. See [public docs](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-tutorials-alfresco-enterprise.html) or [this KB (login required)](https://myalfresco.force.com/support/articles/en_US/Technical_Article/Where-can-I-find-the-repository-for-Enterprise-Maven-artifacts) for more details.


## Resources

### Developer Documentation

- Full documentation is available at the [Maven Alfresco SDK site](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-sdk-aggregator/latest/index.html).

### Alfresco Official Documentation
- Additional documentation for Alfresco Community and Enterprise, tutorials and examples is available in the [Alfresco Documentation](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html).

### Samples 
- Advanced sample projects are maintained by @ohej in the [Maven Alfresco SDK Samples project](https://github.com/Alfresco/alfresco-sdk-samples/).

## News and Updates

- 2014-09-10: SDK 2.0.0-beta-4 released to Maven Central and in the [Archetype Catalog](http://repo1.maven.org/maven2/archetype-catalog.xml)!
- 2014-09-09: SDK 2.0.0-beta-3 released to Maven Central
- 2014-08-30: SDK 2.0.0-beta-1 and 2.0.0-beta-2 available in [Maven Central](http://search.maven.org/#search|ga|1|org.alfresco.maven)
- 2014-08-22: First SNAPSHOT of SDK 2.0.0 in the [OSS Sonatype Repository](https://oss.sonatype.org/content/repositories/snapshots/org/alfresco/maven/alfresco-sdk-parent/2.0.0-SNAPSHOT/)!
- 2014-07: Project fully migrated from [Google Code](https://code.google.com/p/maven-alfresco-archetypes), including tags, branches, issues. Allow a little time for a full cleanup of issue labels and to sort repository permissions. Please update obsolete references and bear with us as we update Alfresco Documentation to this change.


##Glossary
- AMP : Alfresco Module Package - A deployable package of customizations into Alfresco.
- SDK : Software Development Kit - A collection of libraries and utilities that make software development and customization easier.
- MMT : Module Management Tool - A utility provided by Alfresco for managing custom installed Alfresco modules and customizations.
- POM : Project Object Model - A file containing project and configuration details used by Maven to build the project.
- IDE : Integrated Development Environment - A software tool used by developers to write and interact with code.
- KB : Knowledge Base - A hub used to gather specific knowledge on a particular topic.
- Maven : Apache Maven - A tool used to compile and package Java based code for deployment and testing.


## License and Support
- This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). 
- It's a community driven project which is supported for Alfresco Development (please refer to the official [Alfresco Documentation](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html) for supported features.


## Contribute to the Maven Alfresco SDK

Report issues (and contribute!) [here](https://github.com/Alfresco/alfresco-sdk/issues?milestone=1&state=open). You can also join the [Alfresco list on Google Groups](https://groups.google.com/forum/#!forum/maven-alfresco).