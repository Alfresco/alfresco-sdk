---
Title: Integration testing
Added: v3.0.0
Last reviewed: 2021-02-09
---
# Integration testing

_"Integration testing is the phase in software testing where individual software modules are combined and tested as a group. It occurs after unit testing and 
before validation testing. Integration testing takes as its input modules that have been unit tested, groups them in larger aggregates, applies tests defined 
in an integration test plan to those aggregates, and delivers as its output the integrated system ready for system testing. [Wikipedia]."_

Even if the definition of integration testing is a general description, the concept is also valid for Alfresco projects. 

The Alfresco SDK 4.x keeps the same general idea of integration testing provided by SDK 3.0, but this new version reshapes it slightly to leverage on a 
Docker-oriented environment.

Here are the basics to understanding and using integration testing in the context of projects created with the SDK, from a technical perspective:
* SDK 4.x develops integration tests for the platform only. Currently, the integration tests that the SDK is able to manage by default is related to 
Alfresco Content Services (ACS) only.
* Integration tests require an ACS instance to be up and running. You will see that all the scripts and commands are designed to easily manage this 
requirement, but the prerequisite for the SDK is that an ACS instance is available.
* If you're running a project created with a Platform JAR archetype, integration tests are not provided by default. However, you can copy them from your 
All-In-One project.

## How SDK's integration tests work

If you want to know how the SDK's integration tests work and what are the basic tests included in the archetypes, then visit [How SDK's integration tests work](it-working.md).

## How to run SDK's integration tests

If you want to know how you can execute the SDK's integration tests in different environments and some considerations about it, then visit 
[How to run SDK's integration tests](it-running.md).
