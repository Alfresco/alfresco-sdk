---
Title: Working with a Platform (Repository) project
Added: v4.0.0
Last reviewed: 2021-02-09
---

# Working with a Platform (Repository) project

Before you continue make sure that you have read and completed the tasks in the
[Getting started](../getting-started.md) tutorial to generate a Platform project,
which means selecting the `org.alfresco.maven.archetype:alfresco-platform-jar-archetype`
Maven archetype when generating the project. The following information assumes that
the Platform project was generated with the name `my-platform-project`.

-   [Introduction](#introduction)
-   [Configuration properties](#configuration-properties)
-   [Building and running the project](#building-and-running-the-project)
-   [Trying out the sample code](#trying-out-the-sample-code)
-   [Looking inside the containers](#looking-inside-the-containers)
-   [Updating extension code](#updating-extension-code)
-   [Stopping the project](#stopping-the-project)

## Introduction
A Platform project is used to build extensions for the [Alfresco Content Services (ACS) Repository](https://docs.alfresco.com/6.2/concepts/dev-platform-extensions.html).
The runtime environment for ACS is Docker so not only is this project building the source code for your extensions but also the
custom Docker image for the Alfresco Repository. The custom Docker images includes the JARs, or AMPs, with your extension code.

Looking into the generated Platform project we can see that we got a Docker Compose file (**my-platform-project/docker/docker-compose.yml**)
that will be used to build custom Docker images and run the project. We also got a directory for our extension source code:
**my-platform-project/src/main/java** and one directory with the Docker related stuff, such as the **Dockerfile** used to
build the custom ACS Repository Docker image: **my-platform-project/src/main/docker**.

## Configuration properties
There are a number of properties that we can customise when we run the Alfresco SDK project.
These configuration properties are defined in the **my-platform-project/pom.xml** project file.

The following table explains some of these properties:

| Name | Type | Default value | Description |
| ---- | ---- | ------------- | ----------- |
| alfresco.platform.version | `string` | 7.0.0-A20 | The version of the ACS Repository (i.e. alfresco.war) that the Repository Extension should be applied to. This also specifies the version of the ACS Repository Docker Image that the custom built Repository image should be based on. See **my-platform-project-platform-docker/src/main/docker/Dockerfile** |
| alfresco.share.version | `string` | 7.0.0-M3 | The version of Alfresco Share (i.e. share.war) that the Share Extension should be applied to. This also specifies the version of the Alfresco Share Docker Image that the custom built Share image should be based on. See **my-platform-project-share-docker/src/main/docker/Dockerfile**|
| docker.acs.image | `string` | alfresco/alfresco-content-repository-community | The name of the ACS Repository Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| docker.share.image | `string` | alfresco/alfresco-share | The name of the Alfresco Share Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| share.port | `number` | 8180 | The external port (i.e. outside container) for the Alfresco Share webapp.|
| share.debug.port | `number` | 9898 | The external port (i.e. outside container) for Alfresco Share remote debugging.|
| acs.host | `string` | my-platform-project-acs | This is the name (host) that the ACS Repository is available at. This maps to the service name for the ACS Repository in the Docker Compose file **my-platform-project/docker/docker-compose.yml**. The name is only useful for communication between containers on the default Docker network that is created. |
| acs.port | `number` | 8080 | The external port (i.e. outside container) for the ACS Repository.|
| acs.debug.port | `number` | 8888 | The external port (i.e. outside container) for ACS Repository remote debugging.|
| postgres.port | `number` | 5555 | The external port (i.e. outside container) for PostgreSQL database.|

There are some Alfresco Share related properties listed here, but they are not used unless you uncomment some code in the
Docker Compose file (**my-platform-project/docker/docker-compose.yml**) to run the Alfresco Share container.

When you first start out you don't need to change any of these properties, just use the defaults and try it out.

## Building and running the project
The first thing you need to do before you can run anything is to build the custom ACS Repository Docker image with the custom extensions.
We can build the image and extensions at the same time as we start (run) the project by using the `./run.sh build_start` script
(on Windows use the `run.bat build_start` script instead).

Note that if you have another Alfresco SDK project running, then you need to stop it first. Also, make sure that the
following ports are free: 8180 (Share - if enabled in Docker Compose), 8080 (Alfresco Repo), 9898 (Share Debug - if enabled in Docker Compose),
8888 (Alfresco Repo Debug), 5555 (Postgres).
If you want to change the ports see the properties section of **my-platform-project/pom.xml**. This project file also
contains the versions of Alfresco Repository and Alfresco Share (if enabled) that will be used.  

```
$ cd my-platform-project/
MBP512-MBERGLJUNG-0917:my-platform-project mbergljung$ ./run.sh build_start
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building my-platform-project Platform/Repository JAR Module 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 8.323 s
[INFO] Finished at: 2019-03-27T09:23:28Z
[INFO] Final Memory: 62M/227M
[INFO] ------------------------------------------------------------------------
my-platform-project-acs-volume
my-platform-project-db-volume
my-platform-project-ass-volume
Creating network "docker_default" with the default driver
Building my-platform-project-acs
...
Successfully tagged alfresco-content-services-my-platform-project:development
Creating docker_my-platform-project-postgres_1 ... done
Creating docker_my-platform-project-ass_1      ... done
Creating docker_my-platform-project-acs_1      ... done
Attaching to docker_my-platform-project-acs_1, docker_my-platform-project-ass_1, docker_my-platform-project-postgres_1
...
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.923 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.947 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.955 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 91144 ms```
```

The `./run.sh build_start` script will do the following:

* Stop anything running already with this project's Docker Compose file: **my-platform-project/docker/docker-compose.yml**
* Build the Repository Extension JARs so we are sure to get the latest changes
* Assemble/Aggregate all Repository extension JARs into the **my-platform-project/target/extensions** directory
* Create Docker Volumes for Repository (alf_data), Search index, and Database so data is persisted outside the containers
* Run the project via the Docker Compose file and instruct Docker Compose to build the custom Docker images first
* Tail the logs of all containers

This will build the following Docker image:

```
$ docker image ls|more
REPOSITORY                                                       TAG                                          IMAGE ID            CREATED             SIZE
alfresco-content-services-my-platform-project                    development                                  b2b9a7b730f5        5 minutes ago       2.07GB
```

The different web applications should now be accessible:

* **ACS Repository**: http://localhost:8080/alfresco
* And optionally (if enabled in Docker Compose file) **ACS Share**: http://localhost:8180/share/ - login with admin/admin

## Trying out the sample code
The Platform project has some sample extension code that you can try out.

The Repository extension is a Web Script that can be called with the following URL: `http://localhost:8080/alfresco/service/sample/helloworld`.
The source code for the Web Script is located here: **my-platform-project/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials**
and here: **my-platform-project/src/main/java/ com/example/platformsample/HelloWorldWebScript.java**.

## Looking inside the containers
Sometimes it's good to be able to look at what has actually been deployed in the containers. For example, how do I
access the Repository container and list the custom Repository extension JARs that have been deployed?

You can do that as follows:

First **Ctrl-C** out of the log tailing:
```
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.923 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.947 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
my-platform-project-acs_1       | 27-Mar-2019 09:25:12.955 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 91144 ms
my-platform-project-acs_1       |  2019-03-27 09:25:40,406  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-6] Starting 'Transformers' subsystem, ID: [Transformers, default]
my-platform-project-acs_1       |  2019-03-27 09:25:40,948  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-6] Startup of 'Transformers' subsystem, ID: [Transformers, default] complete
^[[B^CERROR: Aborting.
my-platform-project mbergljung$
```

Then check the name of the ACS Repository container:

```
$ docker container ls
CONTAINER ID        IMAGE                                                       COMMAND                  CREATED             STATUS              PORTS                                            NAMES
ba90b1648470        alfresco-content-services-my-platform-project:development   "catalina.sh run -se…"   8 minutes ago       Up 8 minutes        0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp   docker_my-platform-project-acs_1
0435b09e687c        alfresco/alfresco-search-services:2.0.3                     "/bin/sh -c '$DIST_D…"   8 minutes ago       Up 8 minutes        0.0.0.0:8983->8983/tcp                           docker_my-platform-project-ass_1
c9145e7cdb20        postgres:9.6                                                "docker-entrypoint.s…"   8 minutes ago       Up 8 minutes        0.0.0.0:5555->5432/tcp                           docker_my-platform-project-postgres_1
```

Then open up a shell into the ACS Repository container:

```
my-platform-project mbergljung$ docker exec -it docker_my-platform-project-acs_1 /bin/bash
[root@ba90b1648470 tomcat]# pwd
/usr/local/tomcat
[root@ba90b1648470 tomcat]# ls -l webapps/alfresco/WEB-INF/lib | grep "my-plat"
-rw-r--r-- 1 root root    21180 Mar 27 09:23 my-platform-project-1.0-SNAPSHOT.jar
-rw-r--r-- 1 root root    13692 Mar 27 09:23 my-platform-project-1.0-SNAPSHOT-tests.jar
[root@ba90b1648470 tomcat]# exit
exit
```

## Updating extension code
So now you probably want to write some new code, or update the existing code, and see how that works with the containers running.
What do you need to do, restart etc. First just update the code. For example, let's update the Repository Web Script
to return a different message. Open up the **my-platform-project/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials/helloworld.get.html.ftl**
file and change it to look as follows:

```
Message: '${fromJS}' '${fromJava}' UPDATED!
```

To get this code update deployed we have to run the following commands:

First `Ctrl-C` out of the log tailing.

Then stop the project:

```
my-platform-project mbergljung$ ./run.sh stop
Stopping docker_my-platform-project-acs_1      ... done
Stopping docker_my-platform-project-ass_1      ... done
Stopping docker_my-platform-project-postgres_1 ... done
Removing docker_my-platform-project-acs_1      ... done
Removing docker_my-platform-project-ass_1      ... done
Removing docker_my-platform-project-postgres_1 ... done
Removing network docker_default
```

Now build and start again:

```
my-platform-project mbergljung$ ./run.sh build_start
...
```

What this will do is the following:

* Kill the `my-platform-project-acs` container
* Removed the killed (stopped) `my-platform-project-acs` container, so a new Docker image can be created with `development` tag
* Build the Repository extension JAR
* Copy the newly built Repository extension JAR over to the **my-platform-project/target/extensions** where it will be picked up when the new Docker image is built.
* Build a new `alfresco-content-services-my-platform-project:development` image
* Start up the `my-platform-project-acs` container based on new image  

You can now check if the change took effect by accessing the `http://localhost:8080/alfresco/service/sample/helloworld` Web Script.

## Stopping the project
To stop the solution you need to first `Ctrl-C` out of the log tailing. This does not stop the containers
as they run in daemon mode in the background. Check this by executing the following command that lists running containers:

```
$ docker container ls
CONTAINER ID        IMAGE                                                       COMMAND                  CREATED             STATUS              PORTS                                            NAMES
61de829092f3        alfresco-content-services-my-platform-project:development   "catalina.sh run -se…"   3 minutes ago       Up 3 minutes        0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp   docker_my-platform-project-acs_1
07300ddb6714        alfresco/alfresco-search-services:2.0.3                     "/bin/sh -c '$DIST_D…"   3 minutes ago       Up 3 minutes        0.0.0.0:8983->8983/tcp                           docker_my-platform-project-ass_1
09922ce36d90        postgres:9.6                                                "docker-entrypoint.s…"   3 minutes ago       Up 3 minutes        0.0.0.0:5555->5432/tcp                           docker_my-platform-project-postgres_1
```

Now, standing in the directory where the `run.sh` script is located execute the following command to stop and remove the containers:

```
my-platform-project mbergljung$ ./run.sh stop
Stopping docker_my-platform-project-acs_1      ... done
Stopping docker_my-platform-project-ass_1      ... done
Stopping docker_my-platform-project-postgres_1 ... done
Removing docker_my-platform-project-acs_1      ... done
Removing docker_my-platform-project-ass_1      ... done
Removing docker_my-platform-project-postgres_1 ... done
Removing network docker_default
```
