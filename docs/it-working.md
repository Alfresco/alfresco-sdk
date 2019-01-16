---
Title: How SDK's integration tests work
Added: v3.0.0
Last reviewed: 2019-01-16
---
# How SDK's integration tests work

The Alfresco SDK's integration tests are primarily supported by a utility module included in the SDK called [Alfresco Rapid Application Development](https://github.com/Alfresco/alfresco-sdk/tree/master/modules/alfresco-rad) 
(alfresco-rad). This module basically enables the execution of the integration tests within the context of a running Alfresco Content Service (ACS) instance.

## Alfresco Rapid Application Development (Alfresco RAD)

The Alfresco RAD is an Alfresco module which main functionality is offering the ability to execute integration tests in a real ACS context. The core classes
that conforms the Alfresco RAD module are:
* [AlfrescoTestRunner](https://github.com/Alfresco/alfresco-sdk/blob/master/modules/alfresco-rad/src/main/java/org/alfresco/rad/test/AlfrescoTestRunner.java). 
A JUnit test runner that is designed to work with an ACS instance. It detects if it's executing a test inside of a running ACS instance. 
If that is the case the tests are all run normally. If the test is being run from outside the repository, then, instead of running the actual test, an HTTP 
request is made to a Web Script (`RunTestWebScript`) in a running Alfresco instance.
* [RunTestWebScript](https://github.com/Alfresco/alfresco-sdk/blob/master/modules/alfresco-rad/src/main/java/org/alfresco/rad/test/RunTestWebScript.java).
This Web Script works in consort with the `AlfrescoTestRunner`. When a test is run from outside the repository, the Alfresco test runner sends a proxied 
request to perform the test to this script. This runs the test and wraps the results up so that the test initiator can be fooled into thinking they are
running the tests locally.
* [AbstractAlfrescoIT](https://github.com/Alfresco/alfresco-sdk/blob/master/modules/alfresco-rad/src/main/java/org/alfresco/rad/test/AbstractAlfrescoIT.java).
Abstract integration test class that gives access to the Alfresco Spring Application context and the `ServiceRegistry` that should be used when accessing 
Alfresco Services.
* [Remote](https://github.com/Alfresco/alfresco-sdk/blob/master/modules/alfresco-rad/src/main/java/org/alfresco/rad/test/Remote.java). The `AlfrescoTestRunner`
class has to determine where the ACS instance endpoint is exposed to send the proxied request to the `RunTestWebScript`. It uses, in order, the next three
mechanisms:
    * The `Remote` annotation. If the test is annotated with `@Remote`, then it uses the `endpoint` property to determine the ACS endpoint.
    * The `acs.endpoint.path` Java system property. If the Java system property is set, then its value is used as the ACS endpoint.
    * A default value. If none of the previous mechanisms returned a value, then the default value `http://localhost:8080/alfresco` is used.
    
In summary, if you want to execute your integration tests inside an existing ACS instance, you'll need to annotate them with the JUnit `RunWith` annotation 
and set the value to `AlfrescoTestRunner.class`. If you want to customise the default ACS endpoint location, you can either annotate your tests with `Remote` 
or set the Java system property `acs.endpoint.path`.

## Integration tests configuration in the All-In-One project

So, taking into account the previous section, let's see how the integration tests are configured in a project generated from the SDK 4.0 All-In-One archetype.

* The maven dependencies required to execute the integration tests are deployed to the ACS Docker image in the `PROJECT_ARTEFACTID-platform-docker` maven 
module using the `maven-dependency-plugin`. The configuration is done in the file `PROJECT_ARTEFACTID-platform-docker/pom.xml`: 

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
                    ...
                    <!-- Test dependencies -->
                    <!-- We need these dependencies installed in ACS in order to execute the test remotely making use of the Alfresco RAD module -->
                    <artifactItem>
                        <groupId>org.alfresco.maven</groupId>
                        <artifactId>alfresco-rad</artifactId>
                        <version>${alfresco.sdk.version}</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                    </artifactItem>
                    <artifactItem>
                        <groupId>org.alfresco</groupId>
                        <artifactId>integration-tests</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <classifier>tests</classifier>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                    </artifactItem>
                    <artifactItem>
                        <groupId>junit</groupId>
                        <artifactId>junit</artifactId>
                        <version>4.12</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                    </artifactItem>
                    <artifactItem>
                        <groupId>org.mockito</groupId>
                        <artifactId>mockito-all</artifactId>
                        <version>1.9.5</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                    </artifactItem>
                    <artifactItem>
                        <groupId>org.apache.httpcomponents</groupId>
                        <artifactId>httpclient</artifactId>
                        <version>4.5.2</version>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/extensions</outputDirectory>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
        ...
    </executions>
</plugin>
```  

* The `integration-tests` maven module include the definition of all the integration test classes to be executed against the existing ACS instance. The test
classes are included in the folder `integration-tests/src/test/java`.

* The `integration-tests` maven `pom.xml` file adds the configuration of the `acs.endpoint.path` in case it is required. This is done using the 
`maven-failsafe-plugin`:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <acs.endpoint.path>${test.acs.endpoint.path}</acs.endpoint.path>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

This is specially useful when the ACS endpoint is not exposed at the default location (`http://localhost:8080/alfresco`). This property is important when the
development environment is run using Docker Toolbox (old Windows and MacOS versions). In this case, the container exposed ports are not mapped to localhost, 
but to a custom IP provided by the Virtual Box virtual machine (i.e. `http://192.168.99.100:8080/alfresco`).

* The All-In-One project utility scripts (`run.sh` / `run.bat`) offer two different tasks to execute the integration tests:
    * `build_test`. It builds the whole project, recreates the ACS and Share docker images, starts the dockerised environment, executes the integration tests 
    from the `integration-tests` module and stops the environment.
    * `test`. It simply executes the integration tests (the environment must be already started).   

## Sample tests included in the generated project

The All-In-One archetype includes some basic integration tests that demonstrates the way you can implement the integration tests of your custom module. This 
section illustrates what they check.

### `CustomContentModelIT`: Checking the correct existence and setup of a custom model

This integration test verifies the existence of the `{http://www.acme.org/model/content/1.0}contentModel` in the Alfresco Content Services instance. It also 
creates a new node in the repository with the following features:
* The node is named `AcmeFile.txt`.
* The node type is set to `{http://www.acme.org/model/content/1.0}document`.
* The node property `securityClassification` is set to `Company Confidential`.
* The aspect `cm:titled` is added to the new node.

Once created, some Java assertions are raised to check the correct definition of the node. As a last task, the node is deleted from the repository to clean 
the environment.

### `DemoComponentIT`: Checking the Alfresco Content Services DemoComponent component

This integration test verifies the existence of the `DemoComponent` component deployed in the Alfresco Content Services instance. You can find the definition 
of the `DemoComponent` as a custom component of a project created with the All-In-One archetype. For more details, see the class definition in
`PROJECT_ARTEFACTID-platform-jar/src/main/java/com/example/platformsample/DemoComponent.java`.

The integration test retrieves the `DemoComponent` bean from the Alfresco Content Services instance (see `testGetCompanyHome()`), and requests the Company 
Home component. In addition, some Java assertions check if Company Home is identified correctly and has seven children stored in it.

### `HelloWorldWebScriptIT`: Checking the Alfresco Content Services helloworld webscript

This integration test is the simplest one, and verifies the existence and the response of the `helloworld` web script in the Alfresco Content Services instance. 
The test invokes the web script at the URL `http://localhost:8080/alfresco/service/sample/helloworld` and checks the response using some Java assertions.
