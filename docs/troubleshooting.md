---
Title: Troubleshooting
Added: v3.0.0
Last reviewed: 2019-01-14
---
# Troubleshooting

This article describes a list of common issues with the projects generated from the Alfresco SDK 4.0 archetypes and the way to troubleshoot them.

* [Incorrect JDK version](#incorrect-jdk-version)
* [Containers synchronization](#containers-synchronization)
* [Ports conflict](#ports-conflict)

## Incorrect JDK version

### Problem

The ACS container is not starting properly and it is showing Java compatibility errors in the logs:

```
org.springframework.beans.factory.CannotLoadBeanClassException: 
Error loading class [com.example.platformsample.Demo] for bean with name 'com.example.Demo' defined in class path resource 
[alfresco/module/sample-project-platform-jar/context/service-context.xml]: 
problem with class file or dependent class; nested exception is java.lang.UnsupportedClassVersionError: 
com/example/platformsample/Demo has been compiled by a more recent version of the Java Runtime (class file version 55.0), 
this version of the Java Runtime only recognizes class file versions up to 52.0 (unable to load class [com.example.platformsample.Demo])
```

This error represents that the source code was compiled using the wrong version of the JDK. This issue can happen if the generated project is compiled using 
JDK 11 and it is deployed in an ACS 6.0 container (which uses JRE 8).

### Solution

To solve this issue you can follow several approaches:
* Compile the project using a JDK version lower than 11 (and equal to or newer than 8).
* Remove the `java11` profile in the `pom.xml` file of the base project (this is not recommended if you plan to move to ACS 6.1 in the future).
* Move to ACS 6.1+. This is highly recommended due to the fact that it uses JRE 11 (JDK 8 has already reached its end of support time).

## Containers synchronization

### Problem

ACS depends on the readiness of the database in order to start properly. If the database is not ready when ACS reaches the startup phase that requires it, then
it fails showing error messages in the log:

```
sample-project-acs_1    | Jan 10, 2019 10:58:06 AM org.postgresql.core.v3.ConnectionFactoryImpl log
sample-project-acs_1    | WARNING: IOException occurred while connecting to sample-project-postgres:5432
sample-project-acs_1    | java.net.UnknownHostException: sample-project-postgres
sample-project-acs_1    | 	at java.base/java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:220)
sample-project-acs_1    | 	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:403)
sample-project-acs_1    | 	at java.base/java.net.Socket.connect(Socket.java:591)
sample-project-acs_1    | 	at org.postgresql.core.PGStream.<init>(PGStream.java:69)
...
sample-project-acs_1    | 10-Jan-2019 10:58:06.281 SEVERE [localhost-startStop-1] org.postgresql.Driver.connect Connection error: 
sample-project-acs_1    |  org.postgresql.util.PSQLException: The connection attempt failed.
sample-project-acs_1    | 	at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:259)
sample-project-acs_1    | 	at org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:49)
sample-project-acs_1    | 	at org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:195)
...
```

The projects generated using the Alfresco SDK archetypes are configured in a way that the ACS container _depends on_ the database container (PostgreSQL). 

```
services:
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    ...
    depends_on:
      - sample-project-postgres
  sample-project-postgres:
    image: postgres:9.6
    ...
``` 

The problem is Docker compose only ensures that the dependant container will be started before the one that declares the dependency. But that doesn't ensure 
that the PostgreSQL (or any other database) service will be ready when the ACS script reaches the point in which the database is required. 

Usually, the database service starts before ACS requires it, but there are some infrequent cases (an environment with low resources or high load) in which this
synchronization issue appears.

### Solution

In these cases, you can follow the [recommendation in the official Docker documentation](https://docs.docker.com/compose/startup-order/), which is to use a 
scripting sync solution like _wait-for-it_ or _dockerize_.

Let's see how you can configure the ACS container to use [wait-for-it](https://github.com/vishnubob/wait-for-it) to wait for the database service to be ready 
to accept connections:

1. Download the last version of the [wait-for-it](https://github.com/vishnubob/wait-for-it) bash script and save it into the folder 
`PROJECT_ARTIFACT_ID-platform-docker/src/main/docker`.

2. Modify the file `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile` to include the addition of the script to the ACS container and granting 
execution permission to it.

```
# Copy wait-for-it.sh script to wait for other services
COPY wait-for-it.sh /tmp/wait-for-it.sh
RUN chmod +x /tmp/wait-for-it.sh
```

4. Modify the file `docker/docker-compose.yml` to change the ACS container command to use the _wait-for-it_ script to wait for the PostgreSQL service to be 
ready.

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8888"
    command: ["/tmp/wait-for-it.sh", "sample-project-postgres:5432", "--", "catalina.sh", "run"]
    ports:
      - "${acs.port}:8080"
      - "${acs.debug.port}:8888"
```

With this configuration in place, when the project is rebuilt and restarted the ACS container will wait for the database service to be ready.

## Ports conflict

### Problem

The docker-based development environment started by a project generated using the Alfresco SDK archetypes exposes a set of different ports to the hosted 
machine (i.e. ACS http port, ACS debug port or PostgreSQL port). 

If one of these ports is already in use in the hosted machine (by another service) when you start the development environment, then the startup process will fail 
and the container that wanted to expose the busy port won't start.

### Solution

The docker compose file under `docker/docker-compose.yml` is the source file that, in the compile phase of the project, will be filtered by the 
`maven-resource-plugin` in order to produce the final copy of the docker compose file.

That allows you to modify the number of the exposed ports through maven properties in the `pom.xml` file of the main project.

```
    <!-- Environment configuration properties -->
    <share.port>8180</share.port>
    <share.debug.port>9898</share.debug.port>
    <acs.port>8080</acs.port>
    <acs.debug.port>8888</acs.debug.port>
    <postgres.port>5555</postgres.port>
```

That way, if you face a port conflict, you only need to change the port in the corresponding maven property and rebuilt and restart the project.
