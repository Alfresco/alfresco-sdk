# [![Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features) Alfresco SDK

This is the home of the Alfresco SDK. The Alfresco SDK is used by developers to build extensions for the Alfresco Digital Business Platform. It is based on [Apache Maven](http://maven.apache.org/), compatible with major IDEs and enables Rapid Application Development (RAD) and Test Driven Development (TDD).

## License
This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). If you are an Enterprise customer check the [Support](#alfresco-enterprise-customers-and-partners-support) section.

## News

- 2017-06-23: Alfresco SDK 3.0.1 released, [containing a critial bugfix](https://github.com/Alfresco/alfresco-sdk/issues/461)
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
To get started with Alfresco SDK 2.2.0 (latest) visit the offical Alfresco Documentation for:

- [Alfresco Community 5.1 and above](http://docs.alfresco.com/community/concepts/alfresco-sdk-intro.html)
- [Alfresco Enterprise 5.1 and above](http://docs.alfresco.com/5.1/concepts/alfresco-sdk-intro.html)

#### Previous versions Documentation
- Documentation for [Alfresco SDK 2.1](http://docs.alfresco.com/sdk2.1/concepts/alfresco-sdk-intro.html) (compatible with Alfresco 5.0.d Community and 5.0.1 Enterprise)
- Documentation for [Alfresco SDK 2.0](http://docs.alfresco.com/sdk2.0/concepts/alfresco-sdk-intro.html) (compatible with Alfresco 5.0.c Community and 5.0.0 Enterprise)
- Documentation for [Alfresco SDK 1.1.1](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html) (compatible with Alfresco 4.2)




## Reporting Issues and Community Support
Report issues (and contribute!) [here](https://github.com/Alfresco/alfresco-sdk/issues?milestone=1&state=open) or join us on the [IRC Channel](http://chat.alfresco.com/).

## Alfresco Enterprise Customers and Partners Support
If you are an Alfresco Customer please check the [SDK Support status](http://www.alfresco.com/services/subscription/technical-support/product-support-status) for the version you are using and the [Compatibily Matrix](http://docs.alfresco.com/community/concepts/alfresco-sdk-compatibility.html) for the SDK / Alfresco compatibility. If your version is in Limited or Full Support, you can raise issues via the [Support Portal](http://support.alfresco.com).

## Maven repositories
- As of version 2.0-beta-1, The Alfresco SDK is released in [Maven Central](http://search.maven.org/#search|ga|1|alfresco-sdk). Previous versions are available in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/).
- Alfresco (Community and Enterprise) artifacts are  hosted in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/).
- Alfresco Community artifacts (JARs, WARs, AMPs, poms) and SDK artifacts are publicly available.

*NOTE:* For Enterprise and Premiere licensed software access you need to get credential via the Alfresco Enterprise Support. See [Maven Alfresco Enterprise setup](http://docs.alfresco.com/5.0/concepts/alfresco-sdk-using-enterprise-edition.html).

### Alfresco Artifacts Repository

#### Alfresco Releases
You can use the following snippet in your pom.xml to access releases on Alfresco Artifact repository:

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

## For Developers that want to contribute to the SDK
See the [Developers Wiki page](https://github.com/Alfresco/alfresco-sdk/wiki/Developer-Wiki).

