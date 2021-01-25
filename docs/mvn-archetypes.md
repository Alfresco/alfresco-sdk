---
Title: Alfresco SDK Maven archetypes
Added: v2.1.1
Last reviewed: 2019-10-18
---
# Alfresco SDK Maven archetypes

The Alfresco SDK 4.2 comes with a number of Maven archetypes that can be used to generate Alfresco extension projects.

For more details, see [Getting started with Alfresco SDK 4.2](getting-started.md).

These archetypes are available during the creation of a brand new project. In short, a [Maven archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html) 
is a project templating toolkit. It's defined as an original pattern or model from which all other things of the same kind are made. Using archetypes 
provides a great way to enable developers to quickly follow best practice in a consistent way. This is valid for every project built with Apache Maven and 
it's valid in particular when using Alfresco SDK 4.2.

In this section we are going to introduce all the available archetypes in Alfresco SDK 4.2, with a brief description of their purpose and main use. 
After reading this information, you should be able to understand the various possibilities that Alfresco SDK 4.2 can offer to developers, in terms of 
projects.

When generating your project, you'll be prompted to select the Maven archetype you want to use through an interactive menu, similar to what you can see below.

```
[INFO] Generating project in Interactive mode
[INFO] No archetype defined. Using maven-archetype-quickstart (org.apache.maven.ar
chetypes:maven-archetype-quickstart:1.0)
Choose archetype:
1: remote -> org.alfresco.maven.archetype:activiti-jar-archetype (Sample project w
ith full support for lifecycle and rapid development of Activiti JARs)
2: remote -> org.alfresco.maven.archetype:alfresco-allinone-archetype (Sample mult
i-module project for All-in-One development on the Alfresco platform. Includes mod
ules for Platform/Repository JAR and Share JAR)
3: remote -> org.alfresco.maven.archetype:alfresco-amp-archetype (Sample project w
ith full support for lifecycle and rapid development of Repository AMPs (Alfresco 
Module Packages))
4: remote -> org.alfresco.maven.archetype:alfresco-platform-jar-archetype (Sample 
project with full support for lifecycle and rapid development of Platform/Reposit
ory JARs and AMPs (Alfresco Module Packages))
5: remote -> org.alfresco.maven.archetype:alfresco-share-jar-archetype (Share pro
ject with full support for lifecycle and rapid development of JARs and AMPs (Alfr
esco Module
Packages))
6: remote -> org.alfresco.maven.archetype:share-amp-archetype (Share project with 
full support for lifecycle and rapid development of AMPs (Alfresco Module
Packages))
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive con
tains): : 
```

The menu shows 6 possible options, where each option corresponds to a different Maven archetype that you can select by using the listed numbers. 
Please note that the numbering is not sequential and some numbers may be skipped.

## Existing archetypes

### org.alfresco.maven.archetype:alfresco-allinone-archetype

This archetype allows a developer to implement the All-In-One project on Alfresco Content Services. The All-In-One project (also called AIO) is provided in 
this and previous versions of Alfresco SDK, but in SDK 4.1 it has been reshaped to leverage on Docker.

The All-In-One archetype allows a developer to create a multi-module project on Alfresco Content Services. The All-In-One project mainly includes a module for 
the core repository in ACS and a module for the Share client. This includes:

* ACS Repository JAR
* Alfresco Share JAR
* ACS Repository Docker image configuration
* Alfresco Share Docker image configuration
* Integration tests
* Docker containers (ACS, Share, Alfresco Search Service, PostgreSQL) configuration and orchestration via Docker compose
* (Optional) AMP deployment configuration (JAR is the recommended artifact type and the default)

The project created using the All-In-One Maven archetype includes some sample code (by default) to show you how to develop with the Alfresco Content Services 
Repository and the Alfresco Share client. The samples included in the project are basic and straightforward, and can help you to take the first steps into 
Alfresco development.

The All-In-One project is recommended to be used if you have to develop a customization of the Alfresco Content Services Repository together with 
customizations on Alfresco Share client. If your plan to develop a project on the Alfresco Content Services Repository only, use the Platform JAR Maven 
archetype. If you plan to develop a project on the Alfresco Share client only, use the Share JAR Maven archetype.

For more information about the All-In-One project, see [All-In-One project structure](working-with-generated-projects/structure-aio.md).

### org.alfresco.maven.archetype:alfresco-platform-jar-archetype

This archetype allows a developer to implement the Platform JAR project on Alfresco Content Services. It has been reshaped in SDK 4.1 to leverage on Docker.

The Platform JAR Maven archetype allows a developer to create a module on Alfresco Content Services, in particular on the Repository side, and includes:

* ACS Repository JAR
* ACS Repository Docker image configuration
* Docker containers (ACS, Share (optional), Alfresco Search Service, PostgreSQL) configuration and orchestration via Docker compose
* (Optional) AMP deployment configuration (JAR is the recommended artifact type and the default)

The project created using the Platform JAR Maven archetype includes some sample code (by default) to show you how to develop with the Alfresco Content 
Services Repository. The samples included in the project are basic and straightforward, and can help you to take the first steps into Alfresco development.

The Platform JAR project is recommended to be used if you have to develop a customization of the Alfresco Content Services Repository. If you also plan to 
develop a customization of the Alfresco Share client, use the All-In-One Maven archetype instead.

For more information about the Platform JAR project, see [Platform JAR project structure](working-with-generated-projects/structure-platform.md).

### org.alfresco.maven.archetype:alfresco-share-jar-archetype

This archetype allows a developer to implement the Share JAR project on an Alfresco Share client. It has been reshaped in SDK 4.1 to leverage on Docker.

The Share JAR Maven archetype allows a developer to create a module on an Alfresco Share client, and includes:

* Alfresco Share JAR
* Alfresco Share Docker image configuration
* Docker containers (ACS, Share, Alfresco Search Service, PostgreSQL) configuration and orchestration via Docker compose
* (Optional) AMP deployment configuration (JAR is the recommended artifact type and the default)

The project created using the Share JAR Maven archetype includes some sample code (by default) to show you how to develop with the Alfresco Share client. 
The samples included in the project are basic and straightforward, and can help you to take the first steps into Alfresco development.

The Share JAR project is recommended to be used if you have to develop a customization of the Alfresco Share client. If you also plan to develop a 
customization of the Alfresco Content Services Repository, use the All-In-One Maven archetype instead.

For more information about the Share JAR project, see [Share JAR project structure](working-with-generated-projects/structure-share.md).

### org.alfresco.maven.archetype:activiti-jar-archetype (for use with SDK 2.2 only)

This Maven archetype is related to an older version of the Alfresco SDK and should not be used. For technical reasons this archetype can't be hidden and is 
still listed.

### org.alfresco.maven.archetype:alfresco-amp-archetype (for use with SDK 2.2 only)

This Maven archetype is related to an older version of the Alfresco SDK and should not be used. For technical reasons this archetype can't be hidden and is 
still listed.

### org.alfresco.maven.archetype:share-amp-archetype (for use with SDK 2.2 only)

This Maven archetype is related to an older version of the Alfresco SDK and should not be used. For technical reasons this archetype can't be hidden and is 
still listed.
