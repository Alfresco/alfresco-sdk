# [![Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features) Alfresco SDK


[![Build Status](https://github.com/Alfresco/alfresco-sdk/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/Alfresco/alfresco-sdk/actions/workflows/ci.yml)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit&logoColor=white)](https://github.com/pre-commit/pre-commit)
![GitHub](https://img.shields.io/github/license/Alfresco/alfresco-sdk?color=brightgreen)

This is the home of the Alfresco SDK. The Alfresco SDK is used by developers to build extensions for the Alfresco Digital Business Platform. It is based on 
[Apache Maven](http://maven.apache.org/), compatible with major IDEs and enables [Rapid Application Development (RAD)](https://en.wikipedia.org/wiki/Rapid_application_development) 
and [Test Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development).

## License
This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.
If you are an Enterprise customer check the [Support](#alfresco-enterprise-customers-and-partners-support) section.

## News
- 2024-08: Alfresco SDK 4.9.0 released
- 2024-03: Alfresco SDK 4.8.0 released
- 2023-11: Alfresco SDK 4.7.0 released
- 2023-06: Alfresco SDK 4.6.0 released
- 2022-10: Alfresco SDK 4.5.0 released
- 2022-03: Alfresco SDK 4.4.0 released
- 2021-10: Alfresco SDK 4.3.0 released
- 2021-02: Alfresco SDK 4.2.0 released
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

### Important Notice about ACS 7.2

Please refer to https://github.com/Alfresco/alfresco-sdk/issues/635 to fix the Search Services 403 problem.

### Important Notice about ACS 7.1 

#### Share Version Number

Since ACS 7.1, Share build pipeline has been refactored, and you now have to specify the internal 
Share version number, in addition to the version of the image, because they are not the same anymore.

This number can be located in the main pom.xml of the project used to build Share, hence: 

- property `<alfresco-community-share.version>` of [acs-community-packaging](https://github.com/Alfresco/acs-community-packaging/blob/7.1.0/pom.xml#L17) (Community)  
- property `<alfresco-enterprise-share.version>`of [acs-packaging](https://github.com/Alfresco/acs-packaging/blob/7.1.0.1/pom.xml#L18) (Enterprise) 

So, if for example you want to use the community version of `7.1.0.1`, you can go on acs-community-packaging, 
open the `7.1.0.1` release tag, browse its files, open the pom.xml in the root, 
then copy the value of `<alfresco-community-share.version>`. 

You'll then need to paste this value inside the SDK property `<alfresco.share.version>`.

#### Alternate Docker User

- ACS 6 used to run everything as `root`.
- ACS 7 introduced an `alfresco` user, that should've been used after the `root` user completed its configurations in the Dockerfile.

Due to a bug, this wasn't working in earlier SDKs, and has been fixed in SDK 4.3.

In order to be retro-compatible with ACS 6, however, the user is specified in the SDK property `<alfresco.platform.docker.user>`.

Hence, its values (already in place) are `root` for ACS 6, and `alfresco` for ACS 7+.

#### Log file location

To prevent writing permission problems when logging with the non-root user, 
the `alfresco.log` file has also been moved to a more appropriate location (Tomcat logs instead of Tomcat root).

### Important Notice about Version Numbers

In Q4 2020, Alfresco Platform has undergone a major structural refactoring.

Depending on the Platform version desired, you might need to use SDK 4.1, 4.3, 4.4, 4.5, 4.6, 4.7 SDK 4.8 instead of SDK 4.9.

- For Enterprise and Community versions of 7.x, SDK 4.4 or higher must be used
- For Enterprise versions of 6.0.x, 6.1.x, 6.2.x newer than November 2020, SDK 4.3 must be used
- For Enterprise and Community versions of 6.0.x, 6.1.x, 6.2.x older than November 2020, SDK 4.1 must be used

It's also important to remember that:

- Community Platform versions are built by [acs-community-packaging](https://github.com/Alfresco/acs-community-packaging)
- Community Docker images are published on [Docker Hub](https://hub.docker.com/r/alfresco/alfresco-content-repository-community/tags?page=1&ordering=last_updated)
- Enterprise Platform versions are built by [acs-packaging](https://github.com/Alfresco/acs-packaging)
- Enterprise Docker images are published on *Quay.io*

### Latest Documentation
To get started with **Alfresco SDK 4.9.x** (latest) visit the [Alfresco Documentation](docs/README.md).

#### Documentation about Previous Versions
| SDK Version |                       Alfresco Enterprise Version                        |                    Alfresco Community Version                    | Documentation                                                                                                                                |
|-------------|:------------------------------------------------------------------------:|:----------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------|
| SDK 4.9     | Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.1.x/ 23.2.x / 23.3.x | Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.2.x / 23.3.x | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.8     |     Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.1.x/ 23.2.x      |     Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.2.x      | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.7     |         Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.1.x          |     Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x / 23.1.x      | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.6     |              Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x              |          Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x / 7.4.x          | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.5     |                  Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x                  |              Alfresco 7.0.x / 7.1.x / 7.2.x / 7.3.x              | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.4     |                      Alfresco 7.0.x / 7.1.x / 7.2.x                      |                  Alfresco 7.0.x / 7.1.x / 7.2.x                  | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.3     |              Alfresco 6.0.x / 6.1.x / 6.2.x / 7.0.x / 7.1.x              |                      Alfresco 7.0.x / 7.1.x                      | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.2     |                  Alfresco 6.0.x / 6.1.x / 6.2.x / 7.0.x                  |                          Alfresco 7.0.x                          | https://github.com/Alfresco/alfresco-sdk/tree/master/docs/README.md                                                                          |
| SDK 4.1     |                      Alfresco 6.0.x / 6.1.x / 6.2.x                      |                  Alfresco 6.0.x / 6.1.x / 6.2.x                  | https://github.com/Alfresco/alfresco-sdk/blob/sdk-4.1/docs/README.md                                                                         |
| SDK 4.0     |                          Alfresco 6.0.x / 6.1.x                          |                      Alfresco 6.0.x / 6.1.x                      | https://github.com/Alfresco/alfresco-sdk/blob/sdk-4.0/docs/README.md                                                                         |
| SDK 3.1     |                              Alfresco 5.2.x                              |                          Alfresco 5.2.x                          | http://docs.alfresco.com/5.2/concepts/sdk-intro.html                                                                                         |
| SDK 3.0     |                              Alfresco 5.2.x                              |                          Alfresco 5.2.x                          | http://docs.alfresco.com/5.2/concepts/sdk-intro.html                                                                                         |
| SDK 2.2     |                              Alfresco 5.1.x                              |                          Alfresco 5.1.x                          | https://docs.alfresco.com/5.1/concepts/alfresco-sdk-intro.html                                                                               |
| SDK 2.1     |                              Alfresco 5.0.1                              |                          Alfresco 5.0.d                          | https://docs.alfresco.com/sdk2.1/concepts/alfresco-sdk-intro.html                                                                            |
| SDK 2.0     |                              Alfresco 5.0.0                              |                          Alfresco 5.0.c                          | https://docs.alfresco.com/sdk2.0/concepts/alfresco-sdk-intro.html                                                                            |
| SDK 1.1.1   |                              Alfresco 4.2.x                              |                          Alfresco 4.2.x                          | https://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html                                                                   |

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
