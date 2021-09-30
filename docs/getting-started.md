---
Title: Getting started with Alfresco SDK 4.x
Added: v2.1.1
Last reviewed: 2021-09-30
---
# Getting started with Alfresco SDK 4.x

Use these instructions to get started with using Alfresco SDK 4.x.

## Prerequisites
   
There are a number of software requirements for using Alfresco SDK 4.x.
* Java Development Kit (JDK) - Version 11
* Maven - Version 3.3
* Docker - Latest stable version
* JRebel (optional) for hot reloading of web resources, configuration, and classes
* HotSwap Agent (optional) for hot reloading of web resources, configuration, and classes

### Java

ACS 6.0 is compiled and executed using Java 8, but it is highly recommended to work with ACS 6.1+ which uses Java 11.

1. Download [JDK 11](https://jdk.java.net/11/), unzip it and configure it as the default Java installation.

2. Verify the installation for both JDK and JRE.

```
$ javac -version
javac 11.0.1

$ java -version
openjdk version "11.0.1" 2018-10-16
OpenJDK Runtime Environment 18.9 (build 11.0.1+13)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.1+13, mixed mode)
```

3. Make sure JAVA_HOME is setup correctly, so other tools like Maven will use the correct version.

```
$ env|grep JAVA_HOME
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home
```

### Maven

Alfresco recommends that you keep up-to-date with all the Maven releases. Linux distributions and package managers tend to bundle older releases and this is 
the most common pitfall.

Alfresco SDK 4.x requires Maven 3.3.0+, but you are recommended to download the latest version.

1. Download and install [Apache Maven](https://maven.apache.org/download.cgi) and make sure it is configured correctly on your path.

2. Verify the installation.

```
$ mvn -v
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T17:41:47+01:00)
Maven home: /usr/local/Cellar/maven/3.3.9/libexec
Java version: 11.0.1, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home
Default locale: en_ES, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.4", arch: "x86_64", family: "mac"
```

### Docker

Alfresco recommends that you keep up-to-date with all the Docker releases. If you're using an older version of Windows or Mac you'll have to use 
[Docker Toolbox](https://docs.docker.com/toolbox/) which has some known issues.

1. Download and install [Docker](https://docs.docker.com/install/).

2. Verify the installation of Docker.

```
$ docker -v
Docker version 18.06.1-ce, build e68fc7a
``` 

3. [Docker Compose](https://docs.docker.com/compose/install/) is included as part of some Docker installers. If it's not part of your installation, then 
install it separately after you've installed Docker.

4. Verify the installation of Docker Compose.

```
$ docker-compose -v
docker-compose version 1.22.0, build f46880f
```

## Generate your project from the archetypes

1. After you've successfully configured Java and Maven, it's time to generate your project.

```
mvn archetype:generate -Dfilter=org.alfresco:
```

You'll be prompted to select the archetype you want. The previously available archetypes, alfresco-amp-archetype and share-amp-archetype will still show up 
as an option, however these archetypes are not part of Alfresco SDK 4.x.

Attention: You'll need double quotes around the filter part if you are using Windows Powershell: mvn archetype:generate "-Dfilter=org.alfresco:".

The output looks something like this:

```
[INFO] Generating project in Interactive mode
[INFO] No archetype defined. Using maven-archetype-quickstart (org.apache.maven.archetypes:maven-archetype-quickstart:1.0)
Choose archetype:
1: remote -> org.alfresco.maven.archetype:alfresco-platform-jar-archetype (Sample project with full support for lifecycle and rapid development of Platform/Repository JARs and AMPs (Alfresco Module Packages))
2: remote -> org.alfresco.maven.archetype:alfresco-share-jar-archetype (Share project with full support for lifecycle and rapid development of JARs and AMPs (Alfresco Module
        Packages))
3: remote -> org.alfresco.maven.archetype:alfresco-allinone-archetype (Sample multi-module project for All-in-One development on the Alfresco platform. Includes modules for Platform/Repository JAR and Share JAR)
...
```

2. Select one of the following archetypes:

* `org.alfresco.maven.archetype:alfresco-allinone-archetype`
* `org.alfresco.maven.archetype:alfresco-platform-jar-archetype`
* `org.alfresco.maven.archetype:alfresco-share-jar-archetype`

3. Choose the latest version, such as 4.3.0.

```
Choose org.alfresco.maven.archetype:alfresco-allinone-archetype version:
1: 2.0.0-beta-1
2: 2.0.0-beta-2
3: 2.0.0-beta-3
4: 2.0.0-beta-4
5: 2.0.0
6: 2.1.0
7: 2.1.1
8: 2.2.0
9: 3.0.0
10: 3.0.1
11: 3.1.0
12: 4.0.0-beta-1
13: 4.0.0
14: 4.1.0
15: 4.2.0
16: 4.3.0
```

4. Next you will be prompted for additional values, like groupId, artifactId, and package, as shown below:

```
Define value for property 'groupId':
Define value for property 'artifactId':
[INFO] Using property: version = 1.0-SNAPSHOT
Define value for property 'package':
```

5. After you have specified the information according to your project, a final confirmation will appear.

```
Confirm properties configuration:
groupId: com.acme
artifactId: my-all-in-one
version: 1.0-SNAPSHOT
package: com.acme
Y: :
```

6. Press **Y** and then press **Enter**.

If everything has been configured correctly, you should see something similar to this:

```
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: alfresco-allinone-archetype:4.3.0
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: com.acme
[INFO] Parameter: artifactId, Value: my-all-in-one
[INFO] Parameter: version, Value: 1.0-SNAPSHOT
[INFO] Parameter: package, Value: com.acme
[INFO] Parameter: packageInPathFormat, Value: com/acme
[INFO] Parameter: package, Value: com.acme
[INFO] Parameter: groupId, Value: com.acme
[INFO] Parameter: artifactId, Value: my-all-in-one
[INFO] Parameter: version, Value: 1.0-SNAPSHOT
[INFO] Parent element not overwritten in /Users/Alfresco/my-all-in-one/my-all-in-one-platform/pom.xml
[INFO] Parent element not overwritten in /Users/Alfresco/my-all-in-one/my-all-in-one-share/pom.xml
[INFO] Parent element not overwritten in /Users/Alfresco/my-all-in-one/my-all-in-one-integration-tests/pom.xml
[INFO] Parent element not overwritten in /Users/Alfresco/my-all-in-one/my-all-in-one-platform-docker/pom.xml
[INFO] Parent element not overwritten in /Users/Alfresco/my-all-in-one/my-all-in-one-share-docker/pom.xml
[INFO] Executing META-INF/archetype-post-generate.groovy post-generation script
[INFO] Project created from Archetype in dir: /Users/Alfresco/my-all-in-one
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 04:11 min
[INFO] Finished at: 2019-01-10T16:21:46+01:00
[INFO] Final Memory: 17M/1024M
[INFO] ------------------------------------------------------------------------
```

7. You have successfully generated your first SDK 4.x project.

Inside the project, you will find the `run.bat` and `run.sh` scripts. These are convenience scripts for you to quickly compile / test / run your project.

In the terminal window, use:
* `./run.sh build_start` for Mac OS X or Linux.
* `run.bat build_start` for Windows.

If this is the first time you are doing this, it will take a while for Maven to download all the required dependencies and for Docker to download all the
required images.

For more information about how to work with the projects, please visit [Working with generated projects](working-with-generated-projects/README.md).
