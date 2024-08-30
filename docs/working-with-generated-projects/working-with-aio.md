---
Title: Working with an All-In-One project
Added: v4.0.0
Last reviewed: 2021-02-09
---

# Working with an All-In-One project

Before you continue make sure that you have read and completed the tasks in the
[Getting started](../getting-started.md) tutorial to generate an All-In-One (AIO) project,
which means selecting the `org.alfresco.maven.archetype:alfresco-allinone-archetype`
Maven archetype when generating the project. The following information assumes that
the AIO project was generated with the name `my-all-in-one-project`.

-   [Introduction](#introduction)
-   [Configuration properties](#configuration-properties)
-   [Building and running the project](#building-and-running-the-project)
-   [Trying out the sample code](#trying-out-the-sample-code)
-   [Looking inside the containers](#looking-inside-the-containers)
-   [Updating extension code](#updating-extension-code)
-   [Stopping the project](#stopping-the-project)

## Introduction
An AIO SDK project is used to build extensions for both [Alfresco Content Services (ACS) Repository](https://docs.alfresco.com/6.2/concepts/dev-platform-extensions.html)
and [Alfresco Share UI](https://docs.alfresco.com/6.2/concepts/dev-extensions-share.html). The runtime environment
for ACS is Docker so not only is this project building the source code for your extensions but also the
custom Docker images for the Alfresco Repository and Alfresco Share. The custom Docker images includes the
JARs, or AMPs, with your extension code.

Looking into the generated AIO parent project we can see that we got a Docker Compose file (**my-all-in-one-project/docker/docker-compose.yml**)
that will be used to build custom Docker images and run the project, one sub-project called `my-all-in-one-project-platform` that will be
used to build Repository customizations, and one sub-project called `my-all-in-one-project-share` that can be used to build Alfresco Share UI customizations.

There are also the `my-all-in-one-project-platform-docker` and `my-all-in-one-project-share-docker` projects that are
used to assemble (aggregate) all the Repository and Share extensions
(there are usually more than one of each in a bigger project) and then build the custom Docker images with the
extension(s) applied.

The Repository and Share extensions that are aggregated can either be extensions that you develop locally or extensions
that are available in a Maven repository somewhere.

## Configuration properties
There are a number of properties that we can customise when we run the Alfresco SDK project.
These configuration properties are defined in the **my-all-in-one-project/pom.xml** project file.

The following table explains some of these properties:

| Name | Type | Default value | Description |
| ---- | ---- | ------------- | ----------- |
| alfresco.platform.version | `string` | 7.0.0-A20 | The version of the ACS Repository (i.e. alfresco.war) that the Repository Extension should be applied to. This also specifies the version of the ACS Repository Docker Image that the custom built Repository image should be based on. See **my-all-in-one-project-platform-docker/src/main/docker/Dockerfile** |
| alfresco.share.version | `string` | 7.0.0-M3 | The version of Alfresco Share (i.e. share.war) that the Share Extension should be applied to. This also specifies the version of the Alfresco Share Docker Image that the custom built Share image should be based on. See **my-all-in-one-project-share-docker/src/main/docker/Dockerfile**|
| docker.acs.image | `string` | alfresco/alfresco-content-repository-community | The name of the ACS Repository Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| docker.share.image | `string` | alfresco/alfresco-share | The name of the Alfresco Share Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| share.port | `number` | 8180 | The external port (i.e. outside container) for the Alfresco Share webapp.|
| share.debug.port | `number` | 9898 | The external port (i.e. outside container) for Alfresco Share remote debugging.|
| acs.host | `string` | my-all-in-one-project-acs | This is the name (host) that the ACS Repository is available at. This maps to the service name for the ACS Repository in the Docker Compose file **my-all-in-one-project/docker/docker-compose.yml**. The name is only useful for communication between containers on the default Docker network that is created. |
| acs.port | `number` | 8080 | The external port (i.e. outside container) for the ACS Repository.|
| acs.debug.port | `number` | 8888 | The external port (i.e. outside container) for ACS Repository remote debugging.|
| postgres.port | `number` | 5555 | The external port (i.e. outside container) for PostgreSQL database.|

When you first start out you don't need to change any of these properties, just use the defaults and try it out.

## Building and running the project
The first thing you need to do before you can run anything is to build the custom ACS Docker images with the custom extensions.
We can build images and extensions at the same time as we start (run) the project by using the `./run.sh build_start` script
(on Windows use the `run.bat build_start` script instead).

Note that if you have another Alfresco SDK project running, then you need to stop it first. Also, make sure that the
following ports are free: 8180 (Share), 8080 (Alfresco Repo), 9898 (Share Debug), 8888 (Alfresco Repo Debug), 5555 (Postgres).
If you want to change the ports see the properties section of **my-all-in-one-project/pom.xml**. This project file also
contains the versions of Alfresco Repository and Alfresco Share that will be used.  

```
$ cd my-all-in-one-project
my-all-in-one-project mbergljung$ ./run.sh build_start
[INFO] Scanning for projects...
[WARNING] The project com.example:my-all-in-one-project:pom:1.0-SNAPSHOT uses prerequisites which is only intended for maven-plugin projects but not for non maven-plugin projects. For such purposes you should use the maven-enforcer-plugin. See https://maven.apache.org/enforcer/enforcer-rules/requireMavenVersion.html
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO]
[INFO] AIO - SDK 4.9
[INFO] Alfresco Platform/Repository JAR Module
[INFO] Alfresco Share JAR Module
[INFO] Integration Tests Module
[INFO] Alfresco Platform/Repository Docker Module
[INFO] Alfresco Share Docker Module
...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] AIO - SDK 4.9 ...................................... SUCCESS [  0.680 s]
[INFO] Alfresco Platform/Repository JAR Module ............ SUCCESS [  5.461 s]
[INFO] Alfresco Share JAR Module .......................... SUCCESS [  0.557 s]
[INFO] Integration Tests Module ........................... SUCCESS [  0.900 s]
[INFO] Alfresco Platform/Repository Docker Module ......... SUCCESS [  0.760 s]
[INFO] Alfresco Share Docker Module ....................... SUCCESS [  0.139 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
...
my-all-in-one-project-acs-volume
my-all-in-one-project-db-volume
my-all-in-one-project-ass-volume
...
Building my-all-in-one-project-share
Step 1/8 : FROM alfresco/alfresco-share:7.0.0-M3
...
Successfully tagged alfresco-share-my-all-in-one-project:development
Building my-all-in-one-project-acs
Step 1/9 : FROM alfresco/alfresco-content-repository-community:7.0.0-A20
...
Successfully tagged alfresco-content-services-my-all-in-one-project:development
...
my-all-in-one-project-acs_1       | 27-Mar-2019 06:53:39.191 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
my-all-in-one-project-acs_1       | 27-Mar-2019 06:53:39.233 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
my-all-in-one-project-acs_1       | 27-Mar-2019 06:53:39.249 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 84022 ms
```

The `./run.sh build_start` script will do the following:

* Stop anything running already with this project's Docker Compose file: **my-all-in-one-project/docker/docker-compose.yml**
* Build the Repository and Share Extension JARs so we are sure to get the latest changes
* Assemble/Aggregate all Repository extension JARs into the **my-all-in-one-project/my-all-in-one-project-platform-docker/target/extensions** directory
* Assemble/Aggregate all Share extension JARs into the **my-all-in-one-project/my-all-in-one-project-share-docker/target/extensions** directory
* Create Docker Volumes for Repository (alf_data), Search index, and Database so data is persisted outside the containers
* Run the project via the Docker Compose file and instruct Docker Compose to build the custom Docker images first
* Tail the logs of all containers

This will build the following two Docker images:

```
$ docker image ls|more
REPOSITORY                                                       TAG                                          IMAGE ID            CREATED             SIZE
alfresco-content-services-my-all-in-one-project                  development                                  48e61e882567        16 hours ago        2.07GB
alfresco-share-my-all-in-one-project                             development                                  d6cbb6143578        16 hours ago        749MB
```

The different web applications should now be accessible:

* **ACS Repository**: http://localhost:8080/alfresco
* **ACS Share**: http://localhost:8180/share/ - login with admin/admin

## Trying out the sample code
The AIO project has some sample extension code that you can try out. There is a one Repository extension and one Share extension
that you can test to make sure the extension JARs have been applied properly.

The Repository extension is a Web Script that can be called with the following URL: `http://localhost:8080/alfresco/service/sample/helloworld`.
The source code for the Web Script is located here: **my-all-in-one-project/my-all-in-one-project-platform/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials**
and here: **my-all-in-one-project/my-all-in-one-project-platform/src/main/java/ com/example/platformsample/HelloWorldWebScript.java**.

The Share extension is a custom Aikau page with a custom widget, you reach it with the following URL: `http://localhost:8180/share/page/hdp/ws/simple-page`.
The source code for the Page and Widget is located here: **my-all-in-one-project/my-all-in-one-project-share/src/main/resources/alfresco/web-extension/site-webscripts/com/example/pages**
and here: **my-all-in-one-project/my-all-in-one-project-share/src/main/resources/META-INF/resources/my-all-in-one-project-share/js/tutorials/widgets**.


## Looking inside the containers
Sometimes it's good to be able to look at what has actually been deployed in the containers. For example, how do I
access the Repository container and list the custom Repository extension JARs that have been deployed?

You can do that as follows:

First **Ctrl-C** out of the log tailing:
```
my-all-in-one-project-acs_1       | 27-Mar-2019 07:26:23.893 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
my-all-in-one-project-acs_1       | 27-Mar-2019 07:26:23.914 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
my-all-in-one-project-acs_1       | 27-Mar-2019 07:26:23.940 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 83197 ms
my-all-in-one-project-acs_1       |  2019-03-27 07:26:24,304  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-3] Starting 'Search' subsystem, ID: [Search, managed, solr6]
my-all-in-one-project-acs_1       |  2019-03-27 07:26:25,555  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-3] Startup of 'Search' subsystem, ID: [Search, managed, solr6] complete
^CERROR: Aborting.
my-all-in-one-project mbergljung$
```

Then check the name of the ACS Repository container:

```
$ docker container ls
CONTAINER ID        IMAGE                                                         COMMAND                  CREATED             STATUS              PORTS                                                      NAMES
733867a70117        alfresco-content-services-my-all-in-one-project:development   "catalina.sh run -se…"   5 minutes ago       Up 5 minutes        0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp             docker_my-all-in-one-project-acs_1
1f197e52b4f2        alfresco/alfresco-search-services:2.0.3                       "/bin/sh -c '$DIST_D…"   5 minutes ago       Up 5 minutes        0.0.0.0:8983->8983/tcp                                     docker_my-all-in-one-project-ass_1
4eff0cc9cc25        alfresco-share-my-all-in-one-project:development              "/usr/local/tomcat/s…"   5 minutes ago       Up 5 minutes        8000/tcp, 0.0.0.0:8180->8080/tcp, 0.0.0.0:9898->8888/tcp   docker_my-all-in-one-project-share_1
a7854ff16d72        postgres:9.6                                                  "docker-entrypoint.s…"   5 minutes ago       Up 5 minutes        0.0.0.0:5555->5432/tcp                                     docker_my-all-in-one-project-postgres_1
```

Then open up a shell into the ACS Repository container:

```
my-all-in-one-project mbergljung$ docker exec -it docker_my-all-in-one-project-acs_1 /bin/bash
[root@733867a70117 tomcat]# pwd
/usr/local/tomcat
[root@733867a70117 tomcat]# ls -l webapps/alfresco/WEB-INF/lib | grep "my-all"
-rw-r--r-- 1 root root    17220 Mar 27 07:24 my-all-in-one-project-platform-1.0-SNAPSHOT.jar
[root@733867a70117 tomcat]# exit
exit
```

## Updating extension code
So now you probably want to write some new code, or update the existing code, and see how that works with the containers running.
What do you need to do, restart etc. First just update the code. For example, let's update the Repository Web Script
to return a different message. Open up the **my-all-in-one-project/my-all-in-one-project-platform/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials/helloworld.get.html.ftl**
file and change it to look as follows:

```
Message: '${fromJS}' '${fromJava}' UPDATED!
```

To get this code update deployed we just have to run the following command in another console then where we are tailing the logs,
and stand in the directory where the `run.sh` script is located:

```
my-all-in-one-project mbergljung$ ./run.sh reload_acs
```

What this will do is the following:

* Kill the `my-all-in-one-project-acs` container
* Remove the killed (stopped) `my-all-in-one-project-acs` container, so a new Docker image can be created with `development` tag
* Build the Repository extension JAR: **my-all-in-one-project/my-all-in-one-project-platform**
* Copy the newly built Repository extension JAR over to the **my-all-in-one-project/my-all-in-one-project-platform-docker/target/extensions** where it will be picked up when the new Docker image is built.
* Build a new `alfresco-content-services-my-all-in-one-project:development` image
* Start up the `my-all-in-one-project-acs` container based on new image  

You will be left with the console tailing the logs, but you can **Ctrl-C** out of this as you are already tailing the logs
in the initial console where we started things up.

You can now check if the change took effect by accessing the `http://localhost:8080/alfresco/service/sample/helloworld` Web Script.

## Stopping the project
To stop the solution you need to first `Ctrl-C` out of the log tailing. This does not stop the containers
as they run in daemon mode in the background. Check this by executing the following command that lists running containers:

```
$ docker container ls
CONTAINER ID        IMAGE                                                         COMMAND                  CREATED             STATUS              PORTS                                                      NAMES
49015432f1b2        alfresco-content-services-my-all-in-one-project:development   "catalina.sh run -se…"   20 minutes ago      Up 20 minutes       0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp             docker_my-all-in-one-project-acs_1
edb9ea129a5d        postgres:9.6                                                  "docker-entrypoint.s…"   20 minutes ago      Up 20 minutes       0.0.0.0:5555->5432/tcp                                     docker_my-all-in-one-project-postgres_1
6992d183986f        alfresco/alfresco-search-services:2.0.3                       "/bin/sh -c '$DIST_D…"   20 minutes ago      Up 20 minutes       0.0.0.0:8983->8983/tcp                                     docker_my-all-in-one-project-ass_1
107d00733efd        alfresco-share-my-all-in-one-project:development              "/usr/local/tomcat/s…"   20 minutes ago      Up 20 minutes       8000/tcp, 0.0.0.0:8180->8080/tcp, 0.0.0.0:9898->8888/tcp   docker_my-all-in-one-project-share_1
```

Now, standing in the directory where the `run.sh` script is located execute the following command to stop and remove the containers:

```
my-all-in-one-project mbergljung$ ./run.sh stop
Stopping docker_my-all-in-one-project-acs_1      ... done
Stopping docker_my-all-in-one-project-postgres_1 ... done
Stopping docker_my-all-in-one-project-ass_1      ... done
Stopping docker_my-all-in-one-project-share_1    ... done
Removing docker_my-all-in-one-project-acs_1      ... done
Removing docker_my-all-in-one-project-postgres_1 ... done
Removing docker_my-all-in-one-project-ass_1      ... done
Removing docker_my-all-in-one-project-share_1    ... done
Removing network docker_default
```
