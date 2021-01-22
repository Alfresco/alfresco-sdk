# [![Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features) Alfresco SDK

This is the home of the Alfresco SDK. The Alfresco SDK is used by developers to build extensions for the Alfresco Digital Business Platform. It is based on 
[Apache Maven](http://maven.apache.org/), compatible with major IDEs and enables [Rapid Application Development (RAD)](https://en.wikipedia.org/wiki/Rapid_application_development) 
and [Test Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development).

## License
This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.
If you are an Enterprise customer check the [Support](#alfresco-enterprise-customers-and-partners-support) section.

## News

- 2019-10: Alfresco SDK 4.1.0 released
- 2019-03: Alfresco SDK 4.0.0 released
- 2019-03: Alfresco SDK 3.1.0 released
- 2017-06-23: Alfresco SDK 3.0.1 released, [containing a critical bugfix](https://github.com/Alfresco/alfresco-sdk/issues/461)
- 2017-04-01: Alfresco SDK 3.0.0 released
- 2017-03-27: After years of hard work, countless iterations and gathering feedback, SDK 3.0 has finally been merged into the master branch, ready for release in the coming days
- 2016-02-20: SDK 2.2.0 released to Maven Central. Docs for [Community](http://docs.alfresco.com/community/concepts/alfresco-sdk-intro.html), [Enterprise](http://docs.alfresco.com/5.1/concepts/alfresco-sdk-intro.html), [Release notes](https://artifacts.alfresco.com/nexus/content/repositories/alfresco-docs/alfresco-sdk-aggregator/latest/github-report.html)
- 2015-10-19: SDK 2.1.1 released to Maven Central. Docs for [Community](http://docs.alfresco.com/community/concepts/alfresco-sdk-intro.html), [Enterprise](http://docs.alfresco.com/5.0/concepts/alfresco-sdk-intro.html)
- 2015-05-20: SDK 2.1.0 released to Maven Central. Docs for [Community](http://docs.alfresco.com/community/concepts/alfresco-sdk-intro.html), [Enterprise](http://docs.alfresco.com/5.0/concepts/alfresco-sdk-intro.html)
- 2014-12-23: SDK 2.0.0 release to Maven Central. [Docs](http://docs.alfresco.com/sdk2.0/concepts/alfresco-sdk-intro.html), [Release Notes](https://artifacts.alfresco.com/nexus/content/repositories/alfresco-docs/alfresco-sdk-aggregator/archive/2.0.0/github-report.htm)
- 2014-08-22: First SNAPSHOT of SDK 2.0.0 in the [OSS Sonatype Repository](https://oss.sonatype.org/content/repositories/snapshots/org/alfresco/maven/alfresco-sdk-parent/2.0.0-SNAPSHOT/)!
- 2014-07: Project fully migrated from [Google Code](https://code.google.com/p/maven-alfresco-archetypes).

## User Getting Started

### Latest Documentation
To get started with **Alfresco SDK 4.2.x** (latest) visit the [Alfresco Documentation](docs/README.md).

#### Documentation about Previous Versions
| SDK Version  | Alfresco Enterprise Version       |  Alfresco Community Version       | Documentation  |
| ------------- |:-------------:| :-----:|:-----|
| SDK 4.1   | Alfresco 6.0.x / 6.1.x / 6.2.x | Alfresco 6.0.x / 6.1.x / 6.2.x | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md |
| SDK 4.0   | Alfresco 6.0.x / 6.1.x | Alfresco 6.0.x / 6.1.x | https://github.com/Alfresco/alfresco-sdk/blob/sdk-4.0/docs/README.md |
| SDK 3.1   | Alfresco 5.2.x | Alfresco 5.2.x | http://docs.alfresco.com/5.2/concepts/sdk-intro.html |
| SDK 3.0   | Alfresco 5.2.x | Alfresco 5.2.x | http://docs.alfresco.com/5.2/concepts/sdk-intro.html |
| SDK 2.2   | Alfresco 5.1.x | Alfresco 5.1.x | https://docs.alfresco.com/5.1/concepts/alfresco-sdk-intro.html |
| SDK 2.1   | Alfresco 5.0.1 | Alfresco 5.0.d | https://docs.alfresco.com/sdk2.1/concepts/alfresco-sdk-intro.html |
| SDK 2.0   | Alfresco 5.0.0 | Alfresco 5.0.c | https://docs.alfresco.com/sdk2.0/concepts/alfresco-sdk-intro.html |
| SDK 1.1.1 | Alfresco 4.2.x | Alfresco 4.2.x | https://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html |

## Reporting Issues and Community Support
Report issues (and contribute!) [here](https://github.com/Alfresco/alfresco-sdk/issues?milestone=1&state=open) or join us on the [IRC Channel](http://chat.alfresco.com/).

## Alfresco Enterprise Customers and Partners Support
If you are an Alfresco Customer
please check the [SDK Support status](http://www.alfresco.com/services/subscription/technical-support/product-support-status)
for the version you are using.  If your version is in Limited or Full Support and you need help, visit the [Support Portal](http://support.alfresco.com).

## Maven repositories
- As of version 2.0-beta-1, The Alfresco SDK is released in [Maven Central](http://search.maven.org/#search|ga|1|alfresco-sdk). Previous versions are available 
in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/).
- Alfresco (Community and Enterprise) artifacts are  hosted in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/).
- Alfresco Community artifacts (JARs, WARs, AMPs, poms) and SDK artifacts are publicly available.

*NOTE:* By default the Alfresco SDK will use Community Edition releases but it can be configured to use Enterprise Edition releases. Enterprise and Premier 
customers can use the SDK with Enterprise Edition releases by following the process described in [Working with Enterprise](docs/advanced-topics/working-with-enterprise/README.md).

### Alfresco Artifacts Repository

#### Alfresco Releases
You can use the following snippet in your pom.xml to access releases from the Alfresco Artifact repository:

```xml
<repository>
    <id>alfresco-public</id>
    <url>https://artifacts.alfresco.com/nexus/content/groups/public</url>
</repository>
```

#### SDK Snapshots
To test new unreleased (unsupported) features, you can use the following snippet in your pom.xml to access SDK nightlies (SNAPSHOTS) from the OSS Sonatype repository:

```xml
<repository>
    <id>oss-sonatype-snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

## Docker registries
- Alfresco Community Docker images are publicly available at the [Alfresco's Docker Hub profile](https://hub.docker.com/u/alfresco/).
- Alfresco Enterprise Docker images are hosted at [Quay.io](https://quay.io/). Its configuration process is described in detail in [How to configure private Alfresco Docker registry](docs/advanced-topics/working-with-enterprise/enterprise-docker-registry.md).

## For Developers that want to contribute to the SDK
See the [Developers Wiki page](https://github.com/Alfresco/alfresco-sdk/wiki/Developer-Wiki).
