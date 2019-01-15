---
Title: Share JAR project structure
Added: v3.0.0
Last reviewed: 2019-01-15
---
# Share JAR project structure

The following page provides a detailed description of the Share client JAR project, including the project structure and folder content.

Now that you know what a Share JAR project is, let’s introduce the structure of the project, once it is created using the 
`org.alfresco.maven.archetype:alfresco-share-jar-archetype`.

Below is an example directory structure of a Share JAR created with `com.example` as `groupId` and `my-share-jar-project` as `artifactId`.

```
my-share-jar-project
├── pom.xml
├── README.md
├── run.bat
├── run.sh
└── src
    ├── main
    │   ├── assembly
    │   │   ├── amp.xml
    │   │   ├── file-mapping.properties
    │   │   └── web
    │   │       └── README.md
    │   ├── docker
    │   │   ├── Dockerfile
    │   │   ├── hotswap-agent.properties
    │   │   ├── log4j.properties
    │   │   └── share-config-custom.xml
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   └── resources
    │       ├── alfresco
    │       │   ├── module
    │       │   │   └── my-share-jar-project
    │       │   │       └── module.properties
    │       │   └── web-extension
    │       │       ├── messages
    │       │       │   └── my-share-jar-project.properties
    │       │       ├── my-share-jar-project-slingshot-application-context.xml
    │       │       ├── site-data
    │       │       │   └── extensions
    │       │       │       └── my-share-jar-project-example-widgets.xml
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
    │           │   └── my-share-jar-project
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
* `my-platform-jar-project` (the root of the project) contains the whole project. It can easily be pushed into a version control repository and/or an internet 
hosting service like GitHub, SVN, CVS, etc.
* The files stored into the root of the project are mainly related to actions and commands (running, debugging, etc.), technical configuration (`pom.xml`), 
and documentation (`README.md`).
* `src` contains the source code, tests, configurations, settings and resources that are entirely dedicated to the customization of the Alfresco Share client.

After this brief introduction of the Platform JAR project, let’s focus on the content of the folders.

## Project root folder

Below is a description of the files in the root of the project (in this case, `my-share-jar-project`).

File | Description
--- | ---
`run` (`sh` and `bat`) | Utility script to work with the project (compile, run, test, show logs, etc.). For detailed information about it, check [Working with generated projects](projects-usage.md).
`pom.xml` | This XML file contains information about the project and configuration details used by Apache Maven to build the project.
`README.md` | File in Markdown format containing the documentation for the project.

## `src` folder

Below is a description of the content in the `src` folder, which contains the source code, tests, configuration, settings, and resources entirely dedicated to 
the customization of the Alfresco Share client.

Content | Description
--- | ---
`src/main/assembly` | In this folder you can find everything that's needed to fully control creating the AMP artifact in the platform project. The main file to check is `amp.xml`.
`src/main/docker` | In this folder you can find everything that's needed to fully configure the custom Alfresco Share Docker image.
`src/main/docker/Dockerfile` | This is the file that define the custom Alfresco Share Docker image. The default configuration installs all the existing JARs and AMPs under `${project.build.directory}/extensions` folder and adds custom configuration files.
`src/main/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code. Here you should put all the custom classes, interfaces, and Java source code in general.
`src/main/resources/alfresco/module/<artifactId>` | This folder contains all the configuration files and settings for the Alfresco Share module. Here you can find the property file for the module.
`src/main/resources/alfresco/web-extension` | In this folder you can find the extensions to the web client (Alfresco Share) and it's where you store Spring configurations that extend and override the system Share configuration. There are two important sub-directories here: `site-data` and `site-webscripts`.
`src/main/resources/alfresco/META-INF/resources` | This folder hosts the content that will be placed in the `META-INF` folder of a standard Java web application. It is best practice to use a further subdirectory based on the module name. This allows you to manage multiple modules, so that their web resources don't conflict with each other.
`src/main/resources/alfresco/META-INF/share-config-custom.xml` | This file is a relevant Alfresco Share file used to configure the sub-project with the correct settings, depending on your environment. For more details, see [Share configuration](http://docs.alfresco.com/5.2/concepts/dev-extensions-share-configuration.html).
`src/test/java/<groupId>...` | This folder contains the same content you can find in a regular Java project, i.e. the Java source code for tests. Here you should put all the custom classes, interfaces, and Java source code related to tests.
