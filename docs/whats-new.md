---
Title: What's new?
Added: v2.1.1
Last reviewed: 2021-02-09
---
# What's new?

Alfresco SDK 4.0 brought some changes oriented to assist the way the customizations are built, packaged, run and tested for Alfresco Content Services 6 and 
Alfresco Share 6.

This was a mayor release oriented to support Alfresco 6, so it is not compatible with previous versions of the SDK.

Alfresco SDK 4.6 extends the support to Alfresco 7.4. 
Use an older SDK version for Alfresco 6. 

## Embracing containers and Docker

The main change included in SDK 4.0 is the addition of container technologies. Specifically, the new SDK is highly based on [Docker](https://www.docker.com/) 
and [Docker compose](https://docs.docker.com/compose/) to offer a solution aligned with the architectural decisions made in Alfresco for version 6: moving
towards microservices-oriented solutions.

Working with Docker images gives the developers the opportunity to easily customise the deployment of the local environment to adapt it to their requirements.
Adding, removing and configuring services in the environment is as easy as modifying the Docker compose descriptor file.

## Support for Java 11

[Java 11](https://openjdk.java.net/projects/jdk/11/) is the next Long Term Support (LTS) version that provides support for 3 years. Alfresco 6.1 already offers
support for this version of the Java platform.

Alfresco SDK 4.0 has been modified to add support for Java 11 as well. This way, if you're working as a developer in customisations for Alfresco 6.1 you must
now use SDK 4.0 + JDK 11 to work on them. The Apache Maven plugins included in the archetypes has been updated to avoid any issue with Java 11.

## Easy dependency configuration

The configuration of the Maven dependency management has been greatly improved thanks to the addition of a _bill of materials_ (BOM). 

The inclusion of the BOM dependency in the `dependencyManagement` section of the `pom.xml` file of the projects generated using the archetypes imports all 
artifacts in the selected Alfresco platform version. It is still needed to define dependencies in the POM files, but the version can be omitted as it's 
enforced by this `dependencyManagement`. 

That incredibly eases the management of the versions of the different Alfresco platform's dependencies required in a customisation project.

## Alfresco Maven Plugin no longer needed

Alfresco SDK 4.0 manages the lifecycle of the generated projects making use of proper [utility scripts](working-with-generated-projects/README.md#run-script) 
(`run.sh` / `run.bat`). That avoids the need of using the Alfresco Maven Plugin and eases the process to modify the lifecycle of the customisation projects.

If a development team has straightforward requirements and doesn't want to worry about the complexity of working with containers, it can use the utility scripts
as they are. But, if any development team has a requirement or a development process that requires a customisation in the project development lifecycle, it is 
easy to modify the utility scripts, the Docker files or the Docker compose descriptor to adapt the SDK projects to their needs.

The Alfresco Maven Plugin is only required in those cases in which it is required to package the customisation project as an AMP. For more information about 
how to work with AMPs, please visit [Working with AMPs](advanced-topics/amps.md).

## Integration testing

The integration tests and the mechanisms to execute them in an Alfresco Content Service instance remains the same as in the previous version of the SDK. 

However, the inclusion of Docker and the utility scripts provides a different perspective about the environment on which the integration tests are executed.
In this version, the integration tests are run against the dockerised environment defined using Docker and Docker compose. By doing so, the integration test
environment can be more similar to a real one, including whatever other service is required for a full featured integration test execution. 

## Support for Alfresco 6.2.x

Alfresco SDK 4.1 provides support for Alfresco 6.2.x.

## Support for Alfresco 7.0.x, 7.1.x, 7.2.x, 7.3.x and 7.4.x

Alfresco SDK 4.6 provides support for Alfresco 7.0.x, 7.1.x, 7.2.x, 7.3.x and 7.4.x.

## Support for Java 17
[Java 17](https://openjdk.java.net/projects/jdk/17/) is the latest stable long term (8 year support) version after Java 11. Alfresco 7.3.x already offers
support for this version of the Java platform.

Alfresco SDK 4.5 has been modified to add support for Java 17 as well. This way, if you're working as a developer in customisations for Alfresco 7.3.x or 7.4.x you can
now use SDK 4.5 (or greater) + JDK 17 to work on them. The Apache Maven plugins included in the archetypes has been updated to avoid any issue with Java 17.