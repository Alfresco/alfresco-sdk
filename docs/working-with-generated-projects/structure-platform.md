---
Title: Platform JAR project structure
Added: v3.0.0
Last reviewed: 2019-10-18
---
# Platform JAR project structure

This page provides a detailed description of the Platform JAR project, including the project structure and folder content.

Now that you know what a Platform JAR project is, let’s introduce the structure of the project, once it is created using the 
`org.alfresco.maven.archetype:alfresco-platform-jar-archetype`.

Below is an example directory structure of a Platform JAR created with `com.example` as `groupId` and `my-platform-jar-project` as `artifactId`.

```
my-platform-jar-project
├── README.md
├── docker
│   └── docker-compose.yml
├── pom.xml
├── run.bat
├── run.sh
└── src
    ├── main
    │   ├── assembly
    │   │   ├── amp.xml
    │   │   ├── file-mapping.properties
    │   │   └── web
    │   │       └── README.md
    │   ├── docker
    │   │   ├── Dockerfile
    │   │   ├── alfresco-global.properties
    │   │   ├── dev-log4j.properties
    │   │   ├── disable-webscript-caching-context.xml
    │   │   ├── hotswap-agent.properties
    │   │   └── license
    │   │       └── README.md
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── platformsample
    │   │               ├── Demo.java
    │   │               ├── DemoComponent.java
    │   │               └── HelloWorldWebScript.java
    │   └── resources
    │       ├── META-INF
    │       │   └── resources
    │       │       └── test.html
    │       └── alfresco
    │           ├── extension
    │           │   └── templates
    │           │       └── webscripts
    │           │           └── alfresco
    │           │               └── tutorials
    │           │                   ├── helloworld.get.desc.xml
    │           │                   ├── helloworld.get.html.ftl
    │           │                   └── helloworld.get.js
    │           └── module
    │               └── my-platform-jar-project
    │                   ├── alfresco-global.properties
    │                   ├── context
    │                   │   ├── bootstrap-context.xml
    │                   │   ├── service-context.xml
    │                   │   └── webscript-context.xml
    │                   ├── log4j.properties
    │                   ├── messages
    │                   │   ├── content-model.properties
    │                   │   └── workflow-messages.properties
    │                   ├── model
    │                   │   ├── content-model.xml
    │                   │   └── workflow-model.xml
    │                   ├── module-context.xml
    │                   ├── module.properties
    │                   └── workflow
    │                       └── sample-process.bpmn20.xml
    └── test
        └── java
            └── com
                └── example
                    └── platformsample
                        ├── CustomContentModelIT.java
                        ├── DemoComponentIT.java
                        ├── HelloWorldWebScriptControllerTest.java
                        └── HelloWorldWebScriptIT.java
```

From a high level standpoint, we can describe the content of the project as follows:
* `my-platform-jar-project` (the root of the project) contains the whole project. It can easily be pushed into a version control repository and/or an internet 
hosting service like GitHub, SVN, CVS, etc.
* The files stored into the root of the project are mainly related to actions and commands (running, debugging, etc.), technical configuration (`pom.xml`), 
and documentation (`README.md`).
* `src` contains the source code, tests, configurations, settings and resources that are entirely dedicated to the customization of the Alfresco Content 
Services Repository.

After this brief introduction of the Platform JAR project, let’s focus on the content of the folders.

## Project root folder

Below is a description of the files in the root of the project (in this case, `my-platform-jar-project`).

File | Description
--- | ---
`run` (`sh` and `bat`) | Utility script to work with the project (compile, run, test, show logs, etc.). For detailed information about it, check [Working with generated projects](README.md).
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project.
`README.md` | File in Markdown format containing the documentation for the project.

## `src` folder

Below is a description of the content in the `src` folder. This folder contains the source code, tests, configuration, settings, and resources entirely 
dedicated to the customization of the Alfresco Content Services Repository.

Content | Description
--- | ---
`src/main/assembly` | In this folder you can find everything that's needed to fully control creating the AMP artifact in the platform project. The main file to check is `amp.xml`.
`src/main/docker` | In this folder you can find everything that's needed to fully configure the custom ACS Docker image.
`src/main/docker/Dockerfile` | This is the file that define the custom ACS Docker image. The default configuration installs all the existing JARs and AMPs under `${project.build.directory}/extensions` folder and adds custom configuration and license files.
`src/main/docker/license` | This folder contains the licenses required for running an Enterprise project.
`src/main/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code. Here you should put all the custom classes, interfaces, and Java source code in general.
`src/main/resources/alfresco/extension/templates/webscripts` | In this folder you can find the extensions to the REST API related to Web Scripts . Repository Web Scripts are defined in XML, JavaScript, and FreeMarker files. These are referred to as Data Web Scripts as they usually return JSON or XML. The default project contains a Hello World example.
`src/main/resources/alfresco/module/<artifactId>` | This folder contains all the configuration files and settings for the Alfresco platform module. Here you can find context files, the `alfresco-global.properties` file, Content Model examples, and Activiti workflow examples.
`src/main/resources/META-INF` | This folder hosts the content that will be placed in the `META-INF` folder of a standard Java web application.
`src/test/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code for tests. Here you should put all the custom classes, interfaces, and Java source code related to tests.
