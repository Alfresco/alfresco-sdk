---
Title: Working with a Share project
Added: v4.0.0
Last reviewed: 2021-02-09
---

# Working with a Share project

Before you continue make sure that you have read and completed the tasks in the
[Getting started](../getting-started.md) tutorial to generate an Alfresco Share project,
which means selecting the `org.alfresco.maven.archetype:alfresco-share-jar-archetype`
Maven archetype when generating the project. The following information assumes that
the Share project was generated with the name `my-share-project`.

-   [Introduction](#introduction)
-   [Configuration properties](#configuration-properties)
-   [Building and running the project](#building-and-running-the-project)
-   [Trying out the sample code](#trying-out-the-sample-code)
-   [Looking inside the containers](#looking-inside-the-containers)
-   [Updating extension code](#updating-extension-code)
-   [Stopping the project](#stopping-the-project)

## Introduction
An Alfresco Sharte project is used to build extensions for [Alfresco Share UI](https://docs.alfresco.com/6.2/concepts/dev-extensions-share.html).
The runtime environment for ACS is Docker so not only is this project building the source code for your extensions but also the
custom Docker image for Alfresco Share. The custom Docker images includes the
JARs, or AMPs, with your extension code.

Looking into the generated Share project we can see that we got a Docker Compose file (**my-share-project/docker/docker-compose.yml**)
that will be used to build custom Docker images and run the project. We also got a directory for our extension source code:
**my-share-project/src/main/java** and one directory with the Docker related stuff, such as the **Dockerfile** used to
build the custom Alfresco Share Docker image: **my-share-project/src/main/docker**.

## Configuration properties
There are a number of properties that we can customise when we run the Alfresco SDK project.
These configuration properties are defined in the **my-share-project/pom.xml** project file.

The following table explains some of these properties:

| Name | Type | Default value | Description |
| ---- | ---- | ------------- | ----------- |
| alfresco.platform.version | `string` | 7.0.0-A20 | The version of the ACS Repository (i.e. alfresco.war) that the Repository Extension should be applied to. This also specifies the version of the ACS Repository Docker Image that the custom built Repository image should be based on. See **my-share-project-platform-docker/src/main/docker/Dockerfile** |
| alfresco.share.version | `string` | 7.0.0-M3 | The version of Alfresco Share (i.e. share.war) that the Share Extension should be applied to. This also specifies the version of the Alfresco Share Docker Image that the custom built Share image should be based on. See **my-share-project-share-docker/src/main/docker/Dockerfile**|
| docker.acs.image | `string` | alfresco/alfresco-content-repository-community | The name of the ACS Repository Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| docker.share.image | `string` | alfresco/alfresco-share | The name of the Alfresco Share Docker image in Docker Hub. This changes if you switch to Enterprise Edition.|
| share.port | `number` | 8180 | The external port (i.e. outside container) for the Alfresco Share webapp.|
| share.debug.port | `number` | 9898 | The external port (i.e. outside container) for Alfresco Share remote debugging.|
| acs.host | `string` | my-share-project-acs | This is the name (host) that the ACS Repository is available at. This maps to the service name for the ACS Repository in the Docker Compose file **my-share-project/docker/docker-compose.yml**. The name is only useful for communication between containers on the default Docker network that is created. |
| acs.port | `number` | 8080 | The external port (i.e. outside container) for the ACS Repository.|
| acs.debug.port | `number` | 8888 | The external port (i.e. outside container) for ACS Repository remote debugging.|
| postgres.port | `number` | 5555 | The external port (i.e. outside container) for PostgreSQL database.|

There are some ACS Repository related properties listed here, such as `acs.host` and `acs.port`. Alfresco Share will use those
to connect to the Alfresco Repository. This is however a bit tricky when we are running in a container environment. You cannot
just start the Repository and make it available on `localhost:8080`. It would not be accessible like that from inside the
Share container. For Share to be able to connect to the Repository both containers need to be attached to the same
Docker Network. This way you can just use the Docker Compose service name for the Repository, such as `my-share-project-acs`.
So the best way to test your Share extension is to uncomment the code in the Docker Compose file
(**my-share-project/docker/docker-compose.yml**) to also run the ACS Repository container, Search, and Postgres.

## Building and running the project
The first thing you need to do before you can run anything is to build the custom Share Docker image with the custom extensions.
We can build the image and extensions at the same time as we start (run) the project by using the `./run.sh build_start` script
(on Windows use the `run.bat build_start` script instead).

Note that if you have another Alfresco SDK project running, then you need to stop it first. Also, make sure that the
following ports are free: 8180 (Share), 8080 (Alfresco Repo - if enabled in Docker Compose), 9898 (Share Debug), 8888 (Alfresco Repo Debug - if enabled), 5555 (Postgres).
If you want to change the ports see the properties section of **my-share-project/pom.xml**. This project file also
contains the versions of Alfresco Repository (if enabled) and Alfresco Share that will be used.  

When I run the project I have uncommented the code (make sure to also remove the "# Optional" line) that starts
the Repository, Search, and PostgresSQL in the (**my-share-project/docker/docker-compose.yml**) file, so I can test the Share extension:  

```
$ cd my-share-project
my-share-project mbergljung$ ./run.sh build_start
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Building my-share-project Share JAR Module 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.856 s
[INFO] Finished at: 2019-03-27T10:07:14Z
[INFO] Final Memory: 29M/104M
[INFO] ------------------------------------------------------------------------
my-share-project-acs-volume
my-share-project-db-volume
my-share-project-ass-volume
Creating network "docker_default" with the default driver
Building my-share-project-share
...
Successfully tagged alfresco-share-my-share-project:development...
...
my-share-project-acs_1       | 27-Mar-2019 10:09:01.158 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
my-share-project-acs_1       | 27-Mar-2019 10:09:01.175 INFO [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
my-share-project-acs_1       | 27-Mar-2019 10:09:01.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 95189 ms
```

The `./run.sh build_start` script will do the following:

* Stop anything running already with this project's Docker Compose file: **my-share-project/docker/docker-compose.yml**
* Build the Share Extension JARs so we are sure to get the latest changes
* Assemble/Aggregate all Share extension JARs into the **my-share-project/target/extensions** directory
* Create Docker Volumes for Repository (alf_data), Search index, and Database so data is persisted outside the containers
* Run the project via the Docker Compose file and instruct Docker Compose to build the custom Docker images first
* Tail the logs of all containers

This will build the following Docker image:

```
$ docker image ls
REPOSITORY                                                       TAG                                          IMAGE ID            CREATED              SIZE
alfresco-share-my-share-project                                  development                                  b8b9acdb3425        About a minute ago   749MB
```

The different web applications should now be accessible:

* **ACS Repository**: http://localhost:8080/alfresco
* **ACS Share**: http://localhost:8180/share/ - login with admin/admin

## Trying out the sample code
The Share project has some sample extension code that you can try out.

The Share extension is a custom Aikau page with a custom widget, you reach it with the following URL: `http://localhost:8180/share/page/hdp/ws/simple-page`.
The source code for the Page and Widget is located here: **my-share-project/src/main/resources/alfresco/web-extension/site-webscripts/com/example/pages**
and here: **my-share-project/src/main/resources/META-INF/resources/my-share-project-share/js/tutorials/widgets**.

## Looking inside the containers
Sometimes it's good to be able to look at what has actually been deployed in the containers. For example, how do I
access the Share container and list the custom Share extension JARs that have been deployed?

You can do that as follows:

First **Ctrl-C** out of the log tailing:
```
my-share-project-acs_1       | 27-Mar-2019 10:09:01.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 95189 ms
my-share-project-acs_1       |  2019-03-27 10:09:30,278  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-5] Starting 'Transformers' subsystem, ID: [Transformers, default]
my-share-project-acs_1       |  2019-03-27 10:09:30,618  INFO  [management.subsystems.ChildApplicationContextFactory] [http-nio-8080-exec-5] Startup of 'Transformers' subsystem, ID: [Transformers, default] complete
my-share-project-share_1     | 2019-03-27 10:11:50,150  INFO  [web.site.EditionInterceptor] [http-nio-8080-exec-1] Successfully retrieved license information from Alfresco.
my-share-project-share_1     | 2019-03-27 10:12:11,652  INFO  [web.scripts.ImapServerStatus] [http-nio-8080-exec-7] Successfully retrieved IMAP server status from Alfresco: disabled
^CERROR: Aborting.
my-share-project mbergljung$
```

Then check the name of the Alfresco Share container:

```
$ docker container ls
CONTAINER ID        IMAGE                                                     COMMAND                  CREATED             STATUS              PORTS                                                      NAMES
dda89172506c        alfresco/alfresco-content-repository-community:6.1.2-ga   "catalina.sh run -se…"   6 minutes ago       Up 6 minutes        0.0.0.0:8080->8080/tcp                                     docker_my-share-project-acs_1
2b4fa4b4a3f6        alfresco-share-my-share-project:development               "/usr/local/tomcat/s…"   6 minutes ago       Up 6 minutes        8000/tcp, 0.0.0.0:8180->8080/tcp, 0.0.0.0:9898->8888/tcp   docker_my-share-project-share_1
ad8857f3574b        postgres:9.6                                              "docker-entrypoint.s…"   6 minutes ago       Up 6 minutes        0.0.0.0:5555->5432/tcp                                     docker_my-share-project-postgres_1
92902d7ae624        alfresco/alfresco-search-services:2.0.3                   "/bin/sh -c '$DIST_D…"   6 minutes ago       Up 6 minutes        0.0.0.0:8983->8983/tcp                                     docker_my-share-project-ass_1
```

Then open up a shell into the Alfresco Share container:

```
my-share-project mbergljung$ docker exec -it docker_my-share-project-share_1 /bin/bash
[root@2b4fa4b4a3f6 tomcat]# pwd
/usr/local/tomcat
[root@2b4fa4b4a3f6 tomcat]# ls -l webapps/share/WEB-INF/lib/ | grep "my-sh"
-rw-r--r-- 1 root root    18920 Mar 27 10:07 my-share-project-1.0-SNAPSHOT.jar
[root@2b4fa4b4a3f6 tomcat]# exit
exit
```

## Updating extension code
So now you probably want to write some new code, or update the existing code, and see how that works with the containers running.
What do you need to do, restart etc. First just update the code. For example, let's update the Share Page title.
Open up the **my-share-project/src/main/resources/alfresco/web-extension/site-webscripts/com/example/pages/simple-page.get.js**
file and change it to look as follows:

```
model.jsonModel = {
    widgets: [{
        id: "SET_PAGE_TITLE",
        name: "alfresco/header/SetTitle",
        config: {
            title: "This is an UPDATED PAGE Title"
        }
    },
        {
            id: "MY_HORIZONTAL_WIDGET_LAYOUT",
            name: "alfresco/layout/HorizontalWidgets",
            config: {
                widgetWidth: 50,
                widgets: [
                    {
                        id: "DEMO_SIMPLE_LOGO",
                        name: "alfresco/logo/Logo",
                        config: {
                            logoClasses: "alfresco-logo-only"
                        }
                    },
                    {
                        id: "DEMO_SIMPLE_MSG",
                        name: "tutorials/widgets/TemplateWidget"
                    }
                ]
            }
        }]
};
```

To get this code update deployed we just have to run the following command in another console then where we are tailing the logs,
and stand in the directory where the `run.sh` script is located:

```
my-share-project mbergljung$ ./run.sh reload_share
```

What this will do is the following:

* Kill the `my-share-project-acs` container
* Remove the killed (stopped) `my-share-project-acs` container, so a new Docker image can be created with `development` tag
* Build the Share extension JAR
* Copy the newly built Share extension JAR over to the **my-share-project/target/extensions** where it will be picked up when the new Docker image is built.
* Build a new `alfresco-share-my-share-project:development` image
* Start up the `my-share-project-acs` container based on new image  

You will be left with the console tailing the logs, but you can **Ctrl-C** out of this as you are already tailing the logs
in the initial console where we started things up.

You can now check if the change took effect by accessing the `http://localhost:8180/share/page/hdp/ws/simple-page` Web Script.

## Stopping the project
To stop the solution you need to first `Ctrl-C` out of the log tailing. This does not stop the containers
as they run in daemon mode in the background. Check this by executing the following command that lists running containers:

```
$ docker container ls
CONTAINER ID        IMAGE                                                     COMMAND                  CREATED             STATUS              PORTS                                                      NAMES
59f02060955a        alfresco-share-my-share-project:development               "/usr/local/tomcat/s…"   4 minutes ago       Up 4 minutes        8000/tcp, 0.0.0.0:8180->8080/tcp, 0.0.0.0:9898->8888/tcp   docker_my-share-project-share_1
dda89172506c        alfresco/alfresco-content-repository-community:6.1.2-ga   "catalina.sh run -se…"   16 minutes ago      Up 16 minutes       0.0.0.0:8080->8080/tcp                                     docker_my-share-project-acs_1
ad8857f3574b        postgres:9.6                                              "docker-entrypoint.s…"   16 minutes ago      Up 16 minutes       0.0.0.0:5555->5432/tcp                                     docker_my-share-project-postgres_1
92902d7ae624        alfresco/alfresco-search-services:2.0.3                   "/bin/sh -c '$DIST_D…"   16 minutes ago      Up 16 minutes       0.0.0.0:8983->8983/tcp                                     docker_my-share-project-ass_1
```

Now, standing in the directory where the `run.sh` script is located execute the following command to stop and remove the containers:

```
my-share-project mbergljung$ ./run.sh stop
Stopping docker_my-share-project-share_1    ... done
Stopping docker_my-share-project-acs_1      ... done
Stopping docker_my-share-project-postgres_1 ... done
Stopping docker_my-share-project-ass_1      ... done
Removing docker_my-share-project-share_1    ... done
Removing docker_my-share-project-acs_1      ... done
Removing docker_my-share-project-postgres_1 ... done
Removing docker_my-share-project-ass_1      ... done
Removing network docker_default
```
