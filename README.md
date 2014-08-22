# [![Maven Alfresco SDK logo](https://github.com/Alfresco/alfresco-sdk/raw/master/src/site/resources/img/alfresco-maven-logo.jpg)](#features) Maven Alfresco SDK

The Alfresco SDK based on Apache Maven, includes support for rapid and standard development, testing, packaging, versioning and release of your Alfresco integration and extension projects. 

It is composed of:

- An [SDK Parent POM](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/poms/alfresco-sdk-parent/index.html) which you can use in your projects to enable rapid Alfresco development features
- An [Alfresco Platform Distribution POM](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-platform-distribution/latest/index.html) which pre-configures versions of Alfresco and common 3rd party dependency libraries, for stability purposes
- An Alfresco [Maven Plugin](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/plugins/alfresco-maven-plugin/index.html) which provides AMP packaging and installation facitilites (a la MMT) and other common goals for Alfresco development
- A number of *Maven Archetypes* (sample projects) including:
	1. [Alfresco Repository AMP Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/alfresco-amp-archetype/index.html)
	2. [Alfresco Share AMP Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/share-amp-archetype/index.html)
	3. [Alfresco All-in-One Archetype](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/archetypes/alfresco-allinone-archetype/index.html)

## Resources 

### Developer docs

**SDK 2.x**: Full documentation is available at the [Maven Alfresco SDK site](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-sdk-aggregator/latest/index.html).

**SDK 1.x**: Full documentation is available at the [Maven Alfresco SDK site](https://artifacts.alfresco.com/nexus/content/groups/public/alfresco-lifecycle-aggregator/latest/index.html).

### Alfresco Official docs

Additional documentation for Alfresco Community and Enterprise, tutorials and examples is available in the [Alfresco Docs](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html).

### Samples 

Advanced sample projects are maintained by @ohej in the [Alfresco SDK Samples project](https://github.com/Alfresco/alfresco-sdk-samples/).


### Contribute

Report issues (and contribute!) [here](https://github.com/Alfresco/alfresco-sdk/issues?milestone=1&state=open). You can also join the [Maven Alfresco list on Google Groups](https://groups.google.com/forum/#!forum/maven-alfresco).

## Maven Repositories

The Alfresco Maven SDK is released in Maven Central as of version 2.0-beta-1. Alfresco (Community and Enterprise) artifacts are instead hosted in the [Alfresco Artifacts Repository](https://artifacts.alfresco.com/nexus/). 

Alfresco Community artifacts (JARs, WARs, AMPs, poms) and SDK artifacts are publicly available. 

for Enterprise and Premiere licensed software access you need to get credential via the Alfresco Enterprise Support. See [public docs](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-tutorials-alfresco-enterprise.html) or [this KB (login required)](https://myalfresco.force.com/support/articles/en_US/Technical_Article/Where-can-I-find-the-repository-for-Enterprise-Maven-artifacts) for more details.

## License and Support
This project is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). It's a community driven project which is supported for Alfresco Development (please refere to the official [Alfresco Docs](http://docs.alfresco.com/4.2/concepts/dev-extensions-maven-sdk-intro.html) for supported features.

## News

- 2014-08-22: First SNAPSHOT of SDK 2.0.0 in the [OSS Sonatype Repository](https://oss.sonatype.org/content/repositories/snapshots/org/alfresco/maven/alfresco-sdk-parent/2.0.0-SNAPSHOT/)!
- 2014-07: Project fully migrated from [Google Code](https://code.google.com/p/maven-alfresco-archetypes), including tags, branches, issues. Allow a little time for a full cleanup of issue labels and to sort repository permissions. Please update obsolete references and bear with us as we update Alfresco Docs to this change.

