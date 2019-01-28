---
Title: Alfresco SDK 4.0
Added: v3.0.0
Last reviewed: 2019-01-17
---
# Alfresco SDK 4.0

Alfresco SDK 4.0 is a Maven based development kit that provides an easy to use approach to developing applications and extensions for Alfresco. With this 
SDK you can develop, package, test, run, document and release your Alfresco extension project.

For earlier releases of the Alfresco SDK, see the Previous versions section of [http://docs.alfresco.com](http://docs.alfresco.com).

The Alfresco Software Development Kit (Alfresco SDK) is a fundamental tool provided by Alfresco to developers to build customizations and extensions for 
the Alfresco Digital Business Platform. It is based on [Apache Maven](http://maven.apache.org/) and [Docker](https://www.docker.com/) and is compatible with 
major IDEs. This enables Rapid Application Development (RAD) and Test Driven Development (TDD).

Alfresco SDK 4.0 is released under [Apache License version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) and supports Alfresco Content Services both 
in Community Edition and Enterprise Edition. If you're an Enterprise customer, please check the [Alfresco SDK Support status](https://www.alfresco.com/alfresco-product-support-status) 
for the version you're using. If your version is in Limited or Full Support and you need help, contact our Support team [http://support.alfresco.com](http://support.alfresco.com/).

Alfresco SDK 4.0 is a major update to the SDK and provides several improvements on the previous releases.

The 4.0 release takes advantage of Semantic Versioning ([SEMVER](http://semver.org/)), which means that this new release is not directly compatible with the 
previous releases of the SDK.

If you have existing projects that you wish to upgrade to SDK 4.0.x, the recommended approach is to generate a new project from our archetypes and move your 
code into place.

## Documentation Content

* [What's new?](whats-new.md)
* [Getting started with Alfresco SDK 4.0](getting-started.md)
* [Alfresco SDK Maven archetypes](mvn-archetypes.md)
* [Working with generated projects](working-with-generated-projects/README.md)
    * [All-In-One project structure](working-with-generated-projects/structure-aio.md)
    * [Platform JAR project structure](working-with-generated-projects/structure-platform.md)
    * [Share JAR project structure](working-with-generated-projects/structure-share.md)
* [Setting up your development environment](setting-up-your-development-environment/README.md)
    * [Setting up your development environment using Eclipse](setting-up-your-development-environment/dev-env-eclipse.md)
    * [Setting up your development environment using Intellij IDEA](setting-up-your-development-environment/dev-env-intellij.md)
* [Advanced topics](advanced-topics/README.md)
    * [Switching Alfresco Content Services and Share versions](advanced-topics/switching-versions.md)
    * [Working with Enterprise](advanced-topics/working-with-enterprise/README.md)
        * [How to configure private Alfresco Nexus repository](advanced-topics/working-with-enterprise/enterprise-mvn-repo.md)
        * [How to configure private Alfresco Docker registry](advanced-topics/working-with-enterprise/enterprise-docker-registry.md)
        * [How to set up Alfresco Transform Service](advanced-topics/working-with-enterprise/alfresco-transform-service.md)
    * [Working with AMPs](advanced-topics/amps.md)
    * [Debugging](advanced-topics/debugging/README.md)
        * [Remote debugging using Eclipse](advanced-topics/debugging/debug-eclipse.md)
        * [Remote debugging using IntelliJ](advanced-topics/debugging/debug-intellij.md)
    * [Integration testing](advanced-topics/integration-testing/README.md)
        * [How SDK's integration tests work](advanced-topics/integration-testing/it-working.md)
        * [How to run SDK's integration tests](advanced-topics/integration-testing/it-running.md)
    * [Hot reloading](advanced-topics/hot-reloading/README.md)
        * [How to configure and use JRebel](advanced-topics/hot-reloading/jrebel.md)
        * [How to configure and use Hotswap Agent](advanced-topics/hot-reloading/hotswap-agent.md)
* [Troubleshooting](troubleshooting.md)
