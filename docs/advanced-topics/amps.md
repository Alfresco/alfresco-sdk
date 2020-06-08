---
Title: Working with AMPs
Added: v3.0.0
Last reviewed: 2019-10-18
---
# Working with AMPs

Since the early days of the Alfresco SDK, the Alfresco Module Packages (AMP) have been the way customizations were packaged. In Alfresco SDK 4.1 everything 
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

The projects created from the Alfresco SDK 4.1 archetypes are configured to deploy either JARs or AMPs to the ACS / Share docker container. The only thing to
do is modify the `pom.xml` file of the corresponding docker module / project in order to properly configure the dependencies and the Maven dependency plugin.

### All-In-One project

1. Modify the platform JAR dependency from the file `PROJECT_ARTIFACT_ID-platform-docker/pom.xml` to set the type of dependency to `amp`:

```
<dependencies>
    <dependency>
        <groupId>org.alfresco</groupId>
        <artifactId>sample-module-platform</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>amp</type>
    </dependency>
</dependencies>
```

2. Add the `<includeTypes>amp</includeTypes>` to the `collect-extensions` execution in maven-dependency-plugin plugin build configuration in the same file:

```
<!-- Collect extensions (JARs or AMPs) declared in this module do be deployed to docker -->
<execution>
    <id>collect-extensions</id>
    <phase>package</phase>
    <goals>
        <goal>copy-dependencies</goal>
    </goals>
    <configuration>
        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
        <includeScope>runtime</includeScope>
        <includeTypes>amp</includeTypes>
    </configuration>
</execution>
```

3. Repeat these steps for the share module in the file `PROJECT_ARTIFACT_ID-share-docker/pom.xml`.

### Platform / Share project

1. Modify the Maven Resource Plugin in the file `pom.xml` to set the platform / share JAR artifact to copy to `amp`:

```
<execution>
    <id>copy-repository-extension</id>
    <phase>package</phase>
    <goals>
        <goal>copy-resources</goal>
    </goals>
    <configuration>
        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
        <resources>
            <resource>
                <directory>target</directory>
                <includes>
                    <include>${project.build.finalName}.amp</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </configuration>
</execution>
```

Once this configuration is in place, you simply need to rebuild and restart the project. The new configuration will make the Docker images automatically 
install the packaged AMPs in ACS / Share.

## Installing 3rd party AMPs

Installing 3rd party AMPs to the projects is pretty simple. The only requirement is adding the dependency to the project. The default configuration installs
any AMPs set as a maven dependency in the corresponding Docker image. It is important to remember that ACS and Share are separated containers, so you'll need
to add the dependency in the corresponding docker module in case of an All-In-One project. 

Here is an example of how to install Florian Maul's Javascript Console.

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <artifactId>sample-module-platform-docker</artifactId>
    <name>Alfresco Platform/Repository Docker Module</name>
    <description>Platform/Repo Docker Module to generate the final Docker image</description>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.alfresco</groupId>
        <artifactId>sample-module</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <properties>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.alfresco</groupId>
            <artifactId>sample-module-platform</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

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

Another option for installing 3rd party AMPs to the projects is to have the amps local to the project. In this approach the amp file, the amp local to the project will be copied and applied from the Docker file. To use local amp you need to:
    1. Modify the <project>-platform-docker/pom.xml
    2. Create directory: <project>-platform-docker/src/main/docker/extensions
    3. Copy the amp file into <project>-platform-docker/src/main/docker/extensions

The project Dockerfile contains directives to apply amp to Alfresco image  


in the build element of ./workshop-sdk4-platform-docker/pom.xml,
exclude *.amp in the copy-and-filter-docker-resources execution step:
```
                <artifactId>maven-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-and-filter-docker-resources</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>src/main/docker</directory>
                                    <filtering>true</filtering>
                                    <excludes>
                                        <exclude>**/*.jar</exclude>
                                        <exclude>**/*.so</exclude>
                                        <exclude>**/*.gz</exclude>
                                        <exclude>**/*.amp</exclude>            
                                    </excludes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
```

include *.amp in the copy-and-filter-docker-resources-non-filtered

```
                    <execution>
                        <id>copy-and-filter-docker-resources-non-filtered</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>src/main/docker</directory>
                                    <filtering>false</filtering>
                                    <includes>
                                        <include>**/*.jar</include>
                                        <include>**/*.so</include>
                                        <include>**/*.gz</include>
                                        <include>**/*.amp</include>
                                    </includes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
```


## Controlling the order AMPs are applied

Under some specific circumstances it is necessary to apply different AMPs in a development project in a precise order. The default configuration of the 
projects generated using the Alfresco SDK 4.1 archetypes doesn't specify any concrete order applying the AMPs to the ACS/Share installation.

Anyway, that order can be controlled modifying slightly the configuration of the custom Docker images in the project. For instance, let's say we have three
third party AMPs that we want to apply in the next order `third-party-amp-01.amp -> third-party-amp-02.amp -> third-party-amp-03.amp`. In this example, we're
going to consider we need to apply them to a platform JAR module (the process would be the same for a Share module, simply changing the path of the files).

1. Follow the steps described in the section [Installing 3rd party AMPs](#installing-3rd-party-amps) to include all the AMPs dependencies.

2. Locate the `Dockerfile` under the folder `src/main/docker`. In this file, there is a section that copies and applies the AMPs to the ACS installation.

```
# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force
```

3. Replace the `RUN` command to execute one installation of AMP each time and copy it three times, ensuring the installation is executed in the required
order:

```
# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps/
# Install third-party-amp-01
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps/third-party-amp-01.amp $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force
# Install third-party-amp-02
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps/third-party-amp-02.amp $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force
# Install third-party-amp-03
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps/third-party-amp-03.amp $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force
```

4. Rebuild and restart the project (use `run.bat` instead in Windows):

```
$ ./run.sh build_start
```

At this point, you have configured your project to apply the AMPs in a specific order.
