---
Title: All-In-One project structure
Added: v3.0.0
Last reviewed: 2019-01-15
---
# All-In-One project structure

This page provides a detailed description of the All-In-One (AIO) project, including the project structure and folder contents.

Now that you know what an All-In-One project is, let’s introduce the structure of the project, once it is created using the 
`org.alfresco.maven.archetype:alfresco-allinone-archetype`.

Below is an example directory structure of an All-In-One project created with `com.example` as `groupId` and `my-all-in-one-project` as `artifactId`.

```
my-all-in-one-project
├── pom.xml
├── README.md
├── run.bat
├── run.sh
├── docker
|   └── docker-compose.yml
├── integration-tests
|   ├── pom.xml
│   └── src
│       ├── main
│       │   └── java
│       │       └── com
│       │           └── example
│       └── test
│           └── java
│               └── com
│                   └── example
│                       └── platformsample
│                           ├── CustomContentModelIT.java
│                           ├── DemoComponentIT.java
│                           └── HelloWorldWebScriptIT.java
├── my-all-in-one-project-platform-docker
|   ├── pom.xml
│   └── src
│       └── main
│           └── docker
│               ├── alfresco-global.properties
│               ├── dev-log4j.properties
│               ├── disable-webscript-caching-context.xml
│               ├── Dockerfile
│               ├── hotswap-agent.properties
│               └── license
│                   └── README.md
├── my-all-in-one-project-platform-jar
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── assembly
│       │   │   ├── amp.xml
│       │   │   ├── file-mapping.properties
│       │   │   └── web
│       │   │       └── README.md
│       │   ├── java
│       │   │   └── com
│       │   │       └── example
│       │   │           └── platformsample
│       │   │               ├── DemoComponent.java
│       │   │               ├── Demo.java
│       │   │               └── HelloWorldWebScript.java
│       │   └── resources
│       │       ├── alfresco
│       │       │   ├── extension
│       │       │   │   └── templates
│       │       │   │       └── webscripts
│       │       │   │           └── alfresco
│       │       │   │               └── tutorials
│       │       │   │                   ├── helloworld.get.desc.xml
│       │       │   │                   ├── helloworld.get.html.ftl
│       │       │   │                   └── helloworld.get.js
│       │       │   └── module
│       │       │       └── my-all-in-one-project-platform-jar
│       │       │           ├── alfresco-global.properties
│       │       │           ├── context
│       │       │           │   ├── bootstrap-context.xml
│       │       │           │   ├── service-context.xml
│       │       │           │   └── webscript-context.xml
│       │       │           ├── messages
│       │       │           │   └── content-model.properties
│       │       │           ├── model
│       │       │           │   ├── content-model.xml
│       │       │           │   └── workflow-model.xml
│       │       │           ├── module-context.xml
│       │       │           ├── module.properties
│       │       │           └── workflow
│       │       │               └── sample-process.bpmn20.xml
│       │       └── META-INF
│       │           └── resources
│       │               └── test.html
│       └── test
│           └── java
│               └── com
│                   └── example
│                       └── platformsample
│                           └── HelloWorldWebScriptControllerTest.java
├── my-all-in-one-project-share-docker
|   ├── pom.xml
│   └── src
│       └── main
│           └── docker
│               ├── Dockerfile
│               ├── hotswap-agent.properties
│               ├── log4j.properties
│               └── share-config-custom.xml
└── my-all-in-one-project-share-jar
    ├── pom.xml
    └── src
        ├── main
        │   ├── assembly
        │   │   ├── amp.xml
        │   │   ├── file-mapping.properties
        │   │   └── web
        │   │       └── README.md
        │   ├── java
        │   │   └── com
        │       ├── alfresco
        │       │   ├── module
        │       │   │   └── my-all-in-one-project-share-jar
        │       │   │       └── module.properties
        │       │   └── web-extension
        │       │       ├── messages
        │       │       │   └── my-all-in-one-project-share-jar.properties
        │       │       ├── my-all-in-one-project-share-jar-slingshot-application-context.xml
        │       │       ├── site-data
        │       │       │   └── extensions
        │       │       │       └── my-all-in-one-project-share-jar-example-widgets.xml
        │       │       └── site-webscripts
        │       │           ├── com
        │       │           │   └── example
        │       │           │       └── pages
        │       │           │           ├── simple-page.get.desc.xml
        │       │           │           ├── simple-page.get.html.ftl
        │       │           │           └── simple-page.get.js
        │       │           └── org
        │       │               └── alfresco
        │       │                   └── README.md
        │       └── META-INF
        │           ├── resources
        │           │   └── my-all-in-one-project-share-jar
        │           │       └── js
        │           │           └── tutorials
        │           │               └── widgets
        │           │                   ├── css
        │           │                   │   └── TemplateWidget.css
        │           │                   ├── i18n
        │           │                   │   └── TemplateWidget.properties
        │           │                   ├── templates
        │           │                   │   └── TemplateWidget.html
        │           │                   └── TemplateWidget.js
        │           └── share-config-custom.xml
        └── test
            └── java
                └── com
                    └── example
```

From a high level standpoint, we can describe the content of the project as follows:

* `my-all-in-one-project` (the root of the project) contains the whole project. It can easily be pushed into a version control repository and/or an internet 
hosting service like GitHub, SVN, CVS, etc.
* The files stored into the root of the project are mainly related to actions and commands (running, debugging, etc.), technical configuration (`pom.xml`), 
and documentation (`README.md`).
* `integration-tests` contains a sub-project entirely dedicated to integration tests.
* `my-all-in-one-project-platform-docker` (typically named `<artefactId-platform-docker>`) contains a sub-project dedicated to the configuration of a custom
Docker image with the Alfresco Content Services Repository and the customization module `my-all-in-one-project-platform-jar` installed.
* `my-all-in-one-project-platform-jar` (typically named `<artefactId-platform-jar>`) contains a sub-project entirely dedicated to the customization of the 
Alfresco Content Services Repository.
* `my-all-in-one-project-share-docker` (typically named `<artefactId-share-docker>`) contains a sub-project dedicated to the configuration of a custom
Docker image with the Alfresco Share client and the customization module `my-all-in-one-project-share-jar` installed.
* `my-all-in-one-project-share-jar` (typically named `<artefactId-share-jar>`) contains a sub-project entirely dedicated to the customization of the 
Alfresco Share client.

After this brief introduction of the All-In-One project, let’s focus on the content of the folders.

## Project root folder

Below is a description of the files in the root of the project (in this case, `my-all-in-one-project`).

File | Description
--- | ---
`run` (`sh` and `bat`) | Utility script to work with the project (compile, run, test, show logs, etc.). For detailed information about it, check [Working with generated projects](projects-usage.md).
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. You can define all the configurations, parameters, and settings in this file for projects as well as for sub-projects.
`README.md` | File in Markdown format containing the documentation for the project.

## my-all-in-one-project-platform-jar

Below is a description of the content in the `my-all-in-one-project-platform-jar` (typically named `<artefactId-platform-jar>`) sub-project. This sub-project 
contains the source code entirely dedicated to the customizing the Alfresco Content Services Repository.

Content | Description
--- | ---
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. You can define all the configurations, parameters, and settings in this file even if it depends on the parent pom in the root folder. For the majority of use cases, settings and configurations are directly inherited from the parent pom, and this file can work in its default version.
`src/main/assembly` | In this folder you can find everything that's needed to fully control creating the AMP artifact in the platform project. The main file to check is `amp.xml`.
`src/main/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code. Here you should put all the custom classes, interfaces, and Java source code in general.
`src/main/resources/alfresco/extension/templates/webscripts` | In this folder you can find the extensions to the REST API related to Web Scripts . Repository Web Scripts are defined in XML, JavaScript, and FreeMarker files. These are referred to as Data Web Scripts as they usually return JSON or XML. The default project contains a Hello World example.
`src/main/resources/alfresco/module/<artifactId>` | This folder contains all the configuration files and settings for the Alfresco platform module. Here you can find context files, the `alfresco-global.properties` file, Content Model examples, and Activiti workflow examples.
`src/main/resources/META-INF` | This folder hosts the content that will be placed in the `META-INF` folder of a standard Java web application.
`src/test/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code for tests. Here you should put all the custom classes, interfaces, and Java source code related to tests.

## my-all-in-one-project-platform-docker

Below is a description of the content in the `my-all-in-one-project-platform-docker` (typically named `<artefactId-platform-docker>`) sub-project. This 
sub-project contains the resources required to define a custom Docker image with the Alfresco Content Services Repository and the customization module 
`my-all-in-one-project-platform-jar` installed.

Content | Description
--- | ---
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. It adds the dependency to the `my-all-in-one-project-platform-jar` module and configures the `maven-dependency-plugin` to copy all the artifacts required in the Docker image into the folder `${project.build.directory}/extensions`.
`src/main/docker` | In this folder you can find everything that's needed to fully configure the custom ACS Docker image.
`src/main/docker/Dockerfile` | This is the file that define the custom ACS Docker image. The default configuration installs all the existing JARs and AMPs under `${project.build.directory}/extensions` folder and adds custom configuration and license files.
`src/main/docker/license` | This folder contains the licenses required for running an Enterprise project.

## my-all-in-one-project-share-jar

Below is a description of the content in the `my-all-in-one-project-share-jar` (typically named `<artefactId-share-jar>`) sub-project. This sub-project 
contains the source code entirely dedicated to the customizing the Alfresco Share client.

Content | Description
--- | ---
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. You can define all the configurations, parameters, and settings in this file even if it depends on the parent pom in the root folder. For the majority of use cases, settings and configurations are directly inherited from the parent pom, and this file can work in its default version.
`src/main/assembly` | In this folder you can find everything that's needed to fully control creating the AMP artifact in the platform project. The main file to check is `amp.xml`.
`src/main/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code. Here you should put all the custom classes, interfaces, and Java source code in general.
`src/main/resources/alfresco/module/<artifactId>` | This folder contains all the configuration files and settings for the Alfresco Share module. Here you can find the property file for the module.
`src/main/resources/alfresco/web-extension` | In this folder you can find the extensions to the web client (Alfresco Share) and it's where you store Spring configurations that extend and override the system Share configuration. There are two important sub-directories here: `site-data` and `site-webscripts`.
`src/main/resources/alfresco/META-INF/resources` | This folder hosts the content that will be placed in the `META-INF` folder of a standard Java web application. It is best practice to use a further subdirectory based on the module name. This allows you to manage multiple modules, so that their web resources don't conflict with each other.
`src/main/resources/alfresco/META-INF/share-config-custom.xml` | This file is a relevant Alfresco Share file used to configure the sub-project with the correct settings, depending on your environment. For more details, see [Share configuration](http://docs.alfresco.com/5.2/concepts/dev-extensions-share-configuration.html).
`src/test/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code for tests. Here you should put all the custom classes, interfaces, and Java source code related to tests.

## my-all-in-one-project-share-docker

Below is a description of the content in the `my-all-in-one-project-share-docker` (typically named `<artefactId-share-docker>`) sub-project. This 
sub-project contains the resources required to define a custom Docker image with the Alfresco Share Client and the customization module 
`my-all-in-one-project-share-jar` installed.

Content | Description
--- | ---
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. It adds the dependency to the `my-all-in-one-project-share-jar` module and configures the `maven-dependency-plugin` to copy all the artifacts required in the Docker image into the folder `${project.build.directory}/extensions`.
`src/main/docker` | In this folder you can find everything that's needed to fully configure the custom Alfresco Share Docker image.
`src/main/docker/Dockerfile` | This is the file that define the custom Alfresco Share Docker image. The default configuration installs all the existing JARs and AMPs under `${project.build.directory}/extensions` folder and adds custom configuration files.

## integration-tests

Below is a description of the content in the `integration-tests` sub-project. This sub-project contains all the source code and resources needed to run the integration tests.

Content | Description
--- | ---
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project. You can define all the configurations, parameters, and settings in this file even if it depends on the parent pom in the root folder. For the majority of use cases, settings and configurations are directly inherited from the parent pom, and this file can work in its default version.
`src/main/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code. Here you should put all the custom classes, interfaces, and Java source code in general. The folder is empty by default.
`src/test/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code for tests. Here you should put all the custom classes, interfaces, and Java source code in general related to tests. By default you can find three different tests related to content modelling, custom components, and web scripts.
