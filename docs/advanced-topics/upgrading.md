---
Title: Upgrading
Added: v4.0.0
Last reviewed: 2019-01-29
---
# Upgrading an SDK 3.0 project to SDK 4.0

In these instructions, "base" refers to a freshly instantiated SDK 4.0 and "target" refers to the SDK 3.0.1 project that is being upgraded.

## Download a base

Download or instantiate an SDK 4.0 project to use as a base for copying files.

For example, you might create a new project called "test-aio-400" to use as a base from which to copy files into the target project that is to be upgraded.

## Remove unnecessary files

1. Remove the root src directory from the root of the target project.

2. Remove run.\* and debug.\* from the root of the target project.

## Copy run scripts from base to target

1. Copy run.bat and run.sh from base to target project.

## Copy the docker directory from base into target

1. Recursively copy the docker directory from base into target.

2. Clean up references in the docker-compose.yml file.

In the docker directory copied from the base, edit the docker-compose.yml file to change references to the base project name to the target project name.

## Copy the platform-docker directory into target

1. Recursively copy the \*-platform-docker directory into target

2. Rename the directory. The name of the directory should follow the same pattern as the existing project. For example, if the existing project is upgrade-test then the platform-docker directory should be called upgrade-test-platform-docker.

## Clean up references in the platform-docker directory in target

1. If you copied a target directory from the base, remove it.
2. Change references in alfresco-global.properties from the base project name to the target.
3. Change references in pom.xml from the base project name to the target.
4. Change references in hotswap-agent.properties from the base project name to the target.

## Copy the share-docker directory into target

1. Recursively copy the \*-share-docker directory into target

Similar to previous step, the directory should follow the same pattern as the existing project.

## Clean up references in the share-docker directory in target

1. If you copied a target directory from the base, remove it.
2. Change references in alfresco-global.properties from the base project name to the target.
3. Change references in pom.xml from the base project name to the target.
4. Change references in hotswap-agent.properties from the base project name to the target.

## Merge the root pom.xml file

Need to smartly do this merge so that target project maintains its dependencies, name, version, description, etc.

1. Copy the entire `<properties>` element from the base 4.0 pom.xml into the target 3.0 pom.xml, replacing the existing one completely.

2. Change the `acs.host` property to match the target project name.

3. Replace the `alfresco-repository` dependency as follows. Change

    ```
    <dependency>
        <groupId>${alfresco.groupId}</groupId>
        <artifactId>alfresco-repository</artifactId>
    </dependency>
    ```

    to:

    ```
    <dependency>
        <groupId>${alfresco.groupId}</groupId>
        <artifactId>alfresco-remote-api</artifactId>
        <scope>provided</scope>
    </dependency>
    ```

3. Remove the `spring-context` dependency from the target pom.xml:

    ```
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>3.2.17.RELEASE</version>
        <scope>test</scope>
    </dependency>
    ```

4. Under dependencyManagement, the platform distribution dependency needs to have its artifactId updated. Change:

    ```
    <dependency>
        <groupId>${alfresco.groupId}</groupId>
        <artifactId>alfresco-platform-distribution</artifactId>
        <version>${alfresco.platform.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    ```

    to:

    ```
    <dependency>
        <groupId>${alfresco.groupId}</groupId>
        <artifactId>${alfresco.bomDependencyArtifactId}</artifactId>
        <version>${alfresco.platform.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    ```

5. The `maven-resources-plugin` needs the outputDirectory adjusted. Change:

        <outputDirectory>${project.build.testOutputDirectory}</outputDirectory>

    to:

        <outputDirectory>${project.build.outputDirectory}/docker</outputDirectory>

6. Change the `testResources` directory as follows. Change:

        <directory>src/test/resources</directory>

    to:

        <directory>docker</directory>

7. Remove the alfresco-maven plugin. HOWEVER, there may be some "platformModules" and some "shareModules" that are listed here which are third-party AMPs or JARs that need to be deployed to the image. These should be moved to their respective docker module (platform or share) and added to the Maven dependencies in the module's pom.xml file.

For example, in the root pom.xml of the target project there might be a module dependency in the list of platform modules like:

```
<!-- JavaScript Console -->
<moduleDependency>
    <groupId>de.fmaul</groupId>
    <artifactId>javascript-console-repo</artifactId>
    <type>amp</type>
    <version>0.7-SNAPSHOT</version>
</moduleDependency>
```

That needs to be moved to the platform docker module into the list of dependencies, like:

```
<!-- JavaScript Console -->
<dependency>
    <groupId>de.fmaul</groupId>
    <artifactId>javascript-console-repo</artifactId>
    <type>amp</type>
    <version>0.7-SNAPSHOT</version>
</dependency>
```

Similarly, if the target pom.xml has a share module dependency like:

```
<!-- JavaScript Console -->
<moduleDependency>
    <groupId>de.fmaul</groupId>
    <artifactId>javascript-console-share</artifactId>
    <type>amp</type>
    <version>0.7-SNAPSHOT</version>
</moduleDependency>
```

Then that would need to be moved into the share docker module's pom.xml file in its list of dependencies like:

```
<!-- JavaScript Console -->
<dependency>
    <groupId>de.fmaul</groupId>
    <artifactId>javascript-console-share</artifactId>
    <type>amp</type>
    <version>0.7-SNAPSHOT</version>
</dependency>
```

8. Remove the spring-surf dependencies. Remove the following:

    ```
    <dependency>
        <groupId>org.alfresco.surf</groupId>
        <artifactId>spring-surf</artifactId>
        <version>${alfresco.surf.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.alfresco.surf</groupId>
        <artifactId>spring-surf-api</artifactId>
        <version>${alfresco.surf.version}</version>
        <scope>provided</scope>
    </dependency>
    ```

9. Add the "docker" modules to the list of modules at the end of the pom.xml file. For example, if the target project is named "test-aio-400" the list of modules would be:

    ```
    <modules>
      <module>test-aio-400-platform-jar</module>
      <module>test-aio-400-share-jar</module>
      <module>integration-tests</module>
      <module>test-aio-400-platform-docker</module>
      <module>test-aio-400-share-docker</module>
    </modules>
    ```

## Changes to the integration-tests module

1. Completely replace the pom.xml file with the pom.xml file from the 4.0 pom.xml file under integration-tests.

2. Edit the integration-tests pom.xml to replace references to the base project name with references to the target project name.

2. Remove the src/test/properties directory

3. Remove the src/test/resources directory

## Changes to the platform-jar module

Smartly merge the pom.xml file from the 4.0 platform-jar module into the existing platform-jar module pom.xml file.

Maintain the dependencies from the 3.0 platform-jar module pom.xml.

Any old "platformModule" dependencies, which are typically AMPs or JARs that need to be installed need to be copied into this pom.xml's dependencies.

## Changes to the share-jar module

Smartly merge the pom.xml file from the 4.0 share-jar module into the existing share-jar module pom.xml file.

Maintain the dependencies from the 3.0 share-jar module pom.xml.

Any old "shareModule" dependencies, which are typically AMPs or JARs that need to be installed in the Share tier need to be copied into this pom.xml's depdencies.

Remove the spring-surf-api dependency from the 4.0 share-jar module. Remove:

```
<dependency>
    <groupId>org.alfresco.surf</groupId>
    <artifactId>spring-surf-api</artifactId>
</dependency>
```

## Remove alf_data_dev

The data in your SDK content repository will not come over to the upgraded project. Therefore the alf_data_dev directory can be deleted.

## Testing

To test, try doing a `mvn clean` or a `mvn clean package`. If that goes okay, try to run the project using `run.sh build_start`.
