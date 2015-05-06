# [![Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features) Alfresco SDK

This is the home of the source code for the Alfresco SDK. The Alfresco SDK is used by developers to build extensions for the Alfresco content management system. It is maven based and enables Rapid Application Development (RAD) and Test Driven Development (TDD).

## Gettin Started

To get started visit the offical [Alfresco Documentation](http://docs.alfresco.com/community/concepts/alfresco-sdk-intro.html). 

### Samples 

Sample extension projects that uses the SDK, maintained by @ohej in the [Alfresco SDK Samples project](https://github.com/Alfresco/alfresco-sdk-samples/).

### Want to get up to speed quickly, check out this video

For an overview of the SDK potential check out this video:
[TODO]
[![Alfresco SDK 2.1 howto](http://img.youtube.com/vi/utYZaVe9Nd0/0.jpg)](https://www.youtube.com/watch?v=utYZaVe9Nd0)

## Reporting Issues and getting Support

Report issues (and contribute!) [here](https://github.com/Alfresco/alfresco-sdk/issues?milestone=1&state=open). You can also join the [Alfresco list on Google Groups](https://groups.google.com/forum/#!forum/maven-alfresco).

## For Developers that want to contribute to the SDK

The following section descibes how to build the SDK from source and how to generate projects based on the local build.

### Prerequisites
Make sure you have setup and configured Spring Loaded, Maven, JDK, etc, see [Alfresco Documentation](http://docs.alfresco.com/5.0/concepts/alfresco-sdk-installing-prerequisite-software.html). 

### Building from Source

Get the SDK source:
```
$ git clone https://github.com/Alfresco/alfresco-sdk.git
```

Step into the parent SDK project:
```
$ cd alfresco-sdk
```

Build the SDK and skip tests (skipping tests is useful if you don't have access to Enterprise artifacts):
```
alfresco-sdk$ mvn clean install -Dmaven.test.skip=true
```

### Generating an extension project from the local build
```
$ mvn archetype:generate -DarchetypeCatalog=local
```

## License
This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). 

## News
- 2015-05-08: SDK 2.1.0-beta-1 released to Maven Central
- 2014-12-20: SDK 2.0.0 in staging. If you want early access to the release candidate build and want to participate to the testing process check [this page](https://github.com/Alfresco/alfresco-sdk/wiki/SDK-Supported-Features-and-Test-plan) and report issues [here](https://github.com/Alfresco/alfresco-sdk/issues).
- 2014-09-10: SDK 2.0.0-beta-4 released to Maven Central and in the [Archetype Catalog](http://repo1.maven.org/maven2/archetype-catalog.xml)!
- 2014-09-09: SDK 2.0.0-beta-3 released to Maven Central
- 2014-08-30: SDK 2.0.0-beta-1 and 2.0.0-beta-2 available in [Maven Central](http://search.maven.org/#search|ga|1|org.alfresco.maven)
- 2014-08-22: First SNAPSHOT of SDK 2.0.0 in the [OSS Sonatype Repository](https://oss.sonatype.org/content/repositories/snapshots/org/alfresco/maven/alfresco-sdk-parent/2.0.0-SNAPSHOT/)!
- 2014-07: Project fully migrated from [Google Code](https://code.google.com/p/maven-alfresco-archetypes), including tags, branches, issues. Allow a little time for a full cleanup of issue labels and to sort repository permissions. Please update obsolete references and bear with us as we update Alfresco Documentation to this change.

