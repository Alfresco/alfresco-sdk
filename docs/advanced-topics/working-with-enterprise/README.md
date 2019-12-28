---
Title: Working with Enterprise
Added: v3.0.0
Last reviewed: 2019-10-18
---
# Working with Enterprise

By default the Alfresco SDK will use Community Edition releases but it can be configured to use Enterprise Edition releases. Here you will learn how to 
set up a project to work with an Enterprise Edition release, highlighting the changes required to make it work.

If you would like to work with the Alfresco Enterprise Edition, then this requires just a few property changes and a license installation. You also need 
to have access to the private Alfresco Nexus repository and the private Alfresco Quay.io Docker registry. See:
* [How to configure private Alfresco Nexus repository](enterprise-mvn-repo.md).
* [How to configure private Alfresco Docker registry](enterprise-docker-registry.md).

## Installing the license

The very first task to complete is about installing an enterprise license, otherwise the server will remain in read-only mode. This task is required if and 
only if you used the All-In-One archetype or the Platform JAR archetype to generate your project. If you used the Share JAR archetype to generate your project, 
feel free to ignore this task and move on the next one.

If you are an Alfresco Partner or Customer, you can request an enterprise license by you opening a ticket on the [Alfresco Support Portal](http://support.alfresco.com). 
The Enterprise license is nothing more and nothing less than a file with `lic` extension. The Enterprise license file goes into `src/main/docker/license` 
folder (this folder will be located under the platform JAR submodule if you're using the All-In-One archetype). The license will be copied into the ACS Docker 
container before it is started. The license file name doesn't matter, but make sure that you keep it simple and maintain the `lic` extension.

## Configuring the Enterprise release

The configuration of the Enterprise version is straightforward when using the `pom.xml` configuration file stored in the root folder of your project. 
You'll need to update the following settings in the `pom.xml` file:

* Change the _bill of materials_ (BOM) dependency name:

```
<alfresco.bomDependencyArtifactId>acs-packaging</alfresco.bomDependencyArtifactId>
```

* Change the Docker ACS image name:

```
<docker.acs.image>alfresco/alfresco-content-repository</docker.acs.image>
```

Changing these parameters instructs the project to use the proper maven dependencies and Docker images.

Depending on the needs of your project, it will probably be necessary to change the `org.alfresco:alfresco-remote-api` dependency to 
`org.alfresco:alfresco-enterprise-remote-api` or adding any other enterprise dependency like `org.alfresco:alfresco-enterprise-repository`. In any case, 
it won't be necessary to include the version of any of these dependencies due to the addition of the BOM dependency in the `dependencyManagement` 
section of the parent `pom.xml` file. 

## Configuring the Enterprise version

The configuration of the Enterprise version is straightforward when using the `pom.xml` configuration file stored in the root folder of your project. 
You'll need to update the following settings in the `pom.xml` file:

```
<alfresco.platform.version>6.2.0</alfresco.platform.version>
<alfresco.share.version>6.2.0</alfresco.share.version>
```

Making use of the Alfresco SDK 4 it is no longer required the configuration of the Alfresco Surf versions. The inclusion of the BOM and the custom Docker
images will take care of that task automatically for you.

## Purging the project data and running the project

Once all the previous configuration is done, you only need to purge any possible old data (persistent data from the Docker containers), rebuild and restart
the project.

```
$ ./run.sh purge
$ ./run.sh build_start
```  

If you're using Windows, you'll need to use the `run.bat` script instead of `run.sh`.
