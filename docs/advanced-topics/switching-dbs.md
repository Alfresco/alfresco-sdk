---
Title: Switching Alfresco Content Services database
Added: v4.0.0
Last reviewed: 2019-10-18
---
# Switching Alfresco Content Services database

By default, the projects generated making use of the archetypes provided by the Alfresco SDK 4.x are pre-configured to work with a specific database, which is 
PostgreSQL. 

Anyway, ACS is developed and tested to [support a wide range of platforms and languages](https://www.alfresco.com/services/subscription/supported-platforms). 
That includes a set of supported databases.

In this article, we are going to detail the process to modify a project generated from the SDK's archetypes to use a different database. In this case, we're 
going to show how to configure a project to work with MySQL instead of PostgreSQL.

So, the steps to configure a MySQL database in an All-In-One project are:

1. Modify the `Dockerfile` of the platform module (`PROJECT_ROOT_PATH/PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile`) to add the MySQL driver 
library to the tomcat lib folder:

```
FROM ${docker.acs.image}:${alfresco.platform.version}
...
# Copy MySQL driver to Tomcat lib folder
RUN curl -L -o $TOMCAT_DIR/lib/mysql-db-connector.jar "https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.47/mysql-connector-java-5.1.47.jar"
``` 

If you've created a corporate ACS Docker image extending the official one, you can include the download and installation of the MySQL driver in that Docker
image to avoid this installation on every compilation of the project.

2. Modify the ACS configuration to use the MySQL driver and connection URL. This configuration is set in the file 
`PROJECT_ROOT_PATH/PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/alfresco-global.properties`:

```
db.driver=com.mysql.jdbc.Driver
db.url=jdbc:mysql://sample-aio-mysql:3306/alfresco?useUnicode=yes&characterEncoding=UTF-8&useSSL=false
```

Remember that the database URL must contain the name of the MySQL container configured in the Docker compose file.

3. Modify the Docker compose file (`PROJECT_ROOT_PATH/docker/docker-compose.yml`) to delete the PostgreSQL container and configure the new MySQL container:

```
version: '3.4'
services:
...
  sample-aio-mysql:
    image: mysql:5.7
    command: mysqld --character-set-server=utf8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: alfresco
      MYSQL_USER: alfresco
      MYSQL_PASSWORD: alfresco
    expose:
      - "3306"
    volumes:
      - sample-aio-db-volume:/var/lib/mysql
...
```

4. Modify the Docker compose file (`PROJECT_ROOT_PATH/docker/docker-compose.yml`) to change the dependency of ACS container from the PostgreSQL container to the
MySQL container:

```
version: '3.4'
services:
...
  sample-aio-acs:
    image: alfresco-content-services-sample-aio:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-aio-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8888"
    ports:
      - "${acs.port}:8080"
      - "${acs.debug.port}:8888"
    volumes:
      - sample-aio-acs-volume:/usr/local/tomcat/alf_data
    depends_on:
      - sample-aio-mysql
...
``` 

Once all these steps are done, remove all the old data from the project (`run.sh/run.bat purge`) and rebuild and restart the project 
(`run.sh/run.bat build_start`). That's everything required to switch from a PostgreSQL to a MySQL database. The process is the same with the rest of supported
databases. 
