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

* [What's new?]() **TODO**
* [Getting started with Alfresco SDK 4.0](getting-started.md)
* [Alfresco SDK Maven archetypes](mvn-archetypes.md)
* [Working with generated projects](projects-usage.md)
    * [All-In-One project structure](structure-aio.md)
    * [Platform JAR project structure](structure-platform.md)
    * [Share JAR project structure](structure-share.md)
* Setting up your development environment
    * [Setting up your development environment using Eclipse](dev-env-eclipse.md)
    * [Setting up your development environment using Intellij IDEA](dev-env-intellij.md)
* Advanced topics
    * [Switching Alfresco Content Services and Share versions](switching-versions.md)
    * [Working with Enterprise](enterprise.md)
        * [How to configure private Alfresco Nexus repository](enterprise-mvn-repo.md)
        * [How to configure private Alfresco Docker registry](enterprise-docker-registry.md)
        * [How to set up Alfresco Transform Service](alfresco-transform-service.md)
    * [Working with AMPs](amps.md)
    * Debugging
        * [Remote debugging using Eclipse](debug-eclipse.md)
        * [Remote debugging using IntelliJ](debug-intellij.md)
    * [Integration testing](integration-testing.md)
        * [How SDK's integration tests work](it-working.md)
        * [How to run SDK's integration tests](it-running.md)
    * Hot reloading
        * [How to configure and use JRebel](jrebel.md)
        * [How to configure and use Hotswap Agent](hotswap-agent.md)
* [Troubleshooting](troubleshooting.md)
