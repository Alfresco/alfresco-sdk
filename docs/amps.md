---
Title: Working with AMPs
Added: v3.0.0
Last reviewed: 2019-01-14
---
# Working with AMPs

Since the early days of the Alfresco SDK, the Alfresco Module Packages (AMP) have been the way customizations were packaged. In Alfresco SDK 4.0 everything 
is packaged as a JAR by default, while the AMPs are still available as an optional assembly. This gives you much more control over packaging, and simple 
modules can easily be deployed as JARs.

The [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) allows you to control the final artifacts that Maven builds. You add the 
plugin configuration and point it to an XML file that contains the full configuration on the artifact we want to produce.

## Building AMPs with Alfresco SDK 4

To build AMPs the SDK ships a default assembly XML file that will tell the assembly plugin how to produce an AMP file. You will find this file in 
`src/main/assembly/amp.xml` (in the case of All-In-One project you'll find one descriptor for the platform JAR module and another for the share JAR module). 
The plugin configuration is already present in your `pom.xml` file, as shown:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>2.6</version>
    <executions>
        <execution>
            <id>build-amp-file</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
            <configuration>
                <appendAssemblyId>false</appendAssemblyId>
                <descriptor>src/main/assembly/amp.xml</descriptor>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.alfresco.maven.plugin</groupId>
            <artifactId>alfresco-maven-plugin</artifactId>
            <version>${alfresco.sdk.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

This section is commented out by default.

1. To produce both a JAR file and an AMP, remove the comments and run the `mvn package` command. 

Now you have full control over how your AMPs are built. If you want to change the content of the AMP, you can change the assembly `amp.xml` and tailor it 
to your needs.

## Installing AMPs with the SDK

The projects created from the Alfresco SDK 4.0 archetypes are configured to deploy either JARs or AMPs to the ACS / Share docker container. The only thing to
do is modify the `pom.xml` file of the corresponding docker module / project in order to properly configure the dependencies and the Maven dependency plugin.

### All-In-One project

1. Modify the platform JAR dependency from the file `PROJECT_ARTIFACT_ID-platform-docker/pom.xml` to set the type of dependency to `amp`:

```
<dependencies>
    <dependency>
        <groupId>org.alfresco</groupId>
        <artifactId>sample-module-platform-jar</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>amp</type>
    </dependency>
</dependencies>
```

2. Modify the Maven Dependency Plugin in the file `PROJECT_ARTIFACT_ID-platform-docker/pom.xml` to set the platform JAR dependency type to `amp`:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <!-- Copy the repository extension and the dependencies required for execute integration tests -->
        <execution>
            <id>copy-repo-extension</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>org.alfresco</groupId>
                        <artifactId>sample-module-platform-jar</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                        <type>amp</type>
                    </artifactItem>
                    <!-- Test dependencies -->
                    ...
                </artifactItems>
            </configuration>
        </execution>
        <!-- Copy other dependencies (JARs or AMPs) declared in the platform module -->
        ...
    </executions>
</plugin>
```

3. Repeat these steps for the share module in the file `PROJECT_ARTIFACT_ID-share-docker/pom.xml`.

### Platform / Share project

1. Modify the Maven Dependency Plugin in the file `pom.xml` to set the platform / share JAR dependency type to `amp`:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <!-- Copy the repository extension and the dependencies required for execute integration tests -->
        <execution>
            <id>copy-repo-extension</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>org.alfresco</groupId>
                        <artifactId>sample-platform-jar</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                        <type>amp</type>
                    </artifactItem>
                    <!-- Test dependencies -->
                    ...
                </artifactItems>
            </configuration>
        </execution>
        <!-- Copy other dependencies (JARs or AMPs) declared in the platform module -->
        ...
    </executions>
</plugin>
```

Once this configuration is in place, you simply need to rebuild and restart the project. The new configuration will make the Docker images automatically 
install the packaged AMPs in ACS / Share.

## Installing 3rd party AMPs

Installing 3rd party AMPs to the projects is pretty simple. The only requirement is adding the dependency to the project. The default configuration installs
any AMPs set as a maven dependency in the corresponding Docker image. It is important to remember that ACS and Share are separated containers, so you'll need
to add the dependency in the corresponding module in case of an All-In-One project. 

Here is an example of how to install Florian Maul's Javascript Console.

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <artifactId>sample-module-platform-jar</artifactId>
    <name>Alfresco Platform/Repository JAR Module</name>
    <description>Platform/Repo JAR Module (to be included in the alfresco.war) - part of AIO - SDK 4.0
    </description>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.alfresco</groupId>
        <artifactId>sample-module</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- Add here any JAR or AMP dependency that needs to be deployed to ACS -->
        <!-- Javascript Console AMP -->
        <dependency>
            <groupId>de.fmaul</groupId>
            <artifactId>javascript-console-repo</artifactId>
            <version>0.6</version>
            <type>amp</type>
        </dependency>
    </dependencies>
    
    ...
</project>
```
