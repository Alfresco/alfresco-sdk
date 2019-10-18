---
Title: How to set up Alfresco Transform Service (Community)
Added: v4.0.0
Last reviewed: 2019-10-18
---
# How to set up Alfresco Transform Service (Community)

By default, the _Alfresco Transform Service_ (from now ATS) is not included in the basic configuration of the projects generated making use of the Alfresco 
SDK archetypes. 

ATS is now supported in ACS Community and it is distributed as a composition of Docker containers. The docker images required for ATS are available in the 
Alfresco account at [Docker Hub](https://hub.docker.com/u/alfresco/).

In order to properly configure ATS in a project generated using the Alfresco SDK archetypes it is required to execute 2 steps:
1. Add the containers that conform ATS to the Docker compose file.
2. Configure the properties that are required to properly set up ATS.

## Adding the new containers

* Locate the Docker compose file (usually at `PROJECT_ROOT_PATH/docker/docker-compose.yml`) and add the containers that conform ATS (`alfresco-pdf-renderer`, 
`imagemagick`, `libreoffice`, `tika`, `transform-misc` and `activemq`):

```
services:
...
  alfresco-pdf-renderer:
    image: alfresco/alfresco-pdf-renderer:2.1.0-RC3
    environment:
      JAVA_OPTS: " -Xms256m -Xmx512m"
    ports:
      - 8090:8090
  imagemagick:
    image: alfresco/alfresco-imagemagick:2.1.0-RC3
    environment:
      JAVA_OPTS: " -Xms256m -Xmx512m"
    ports:
      - 8091:8090
  libreoffice:
    image: alfresco/alfresco-libreoffice:2.1.0-RC3
    environment:
      JAVA_OPTS: " -Xms256m -Xmx512m"
    ports:
      - 8092:8090
  tika:
    image: alfresco/alfresco-tika:2.1.0-RC3
    environment:
      JAVA_OPTS: " -Xms256m -Xmx512m"
    ports:
      - 8093:8090
  transform-misc:
    image: alfresco/alfresco-transform-misc:2.1.0-RC3
    environment:
      JAVA_OPTS: " -Xms256m -Xmx512m"
    ports:
      - 8094:8090    
  activemq:
    image: alfresco/alfresco-activemq:5.15.8
    ports:
      - 8161:8161 # Web Console
      - 5672:5672 # AMQP
      - 61616:61616 # OpenWire
      - 61613:61613 # STOMP
...
```

* Check that you haven't any port conflict with other services in the Docker compose file.

## Adding the required configuration

* Locate the _Alfresco global properties_ file for docker (usually at `PROJECT_ROOT_PATH/PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/alfresco-global.properties`) 
and add the ATS configuration properties:

```
# Alfresco Transform Service
local.transform.service.enabled=true
localTransform.pdfrenderer.url=http://alfresco-pdf-renderer:8090/
localTransform.imagemagick.url=http://imagemagick:8090/
localTransform.libreoffice.url=http://libreoffice:8090/
localTransform.tika.url=http://tika:8090/
localTransform.misc.url=http://transform-misc:8090/

legacy.transform.service.enabled=true
alfresco-pdf-renderer.url=http://alfresco-pdf-renderer:8090/
jodconverter.url=http://libreoffice:8090/
img.url=http://imagemagick:8090/
tika.url=http://tika:8090/
transform.misc.url=http://transform-misc:8090/

messaging.broker.url=failover:(nio://activemq:61616)?timeout=3000&jms.useCompression=true
```

* Remove the old value of the properties: `messaging.broker.url`, `transform.service.enabled`, `local.transform.service.enabled` and 
`legacy.transform.service.enabled` in the same `alfresco-global.properties` file.

Once these 2 modifications are done, rebuild and restart all the services (`run.sh/run.bat build_start`) and ACS will use ATS to execute remote transformations 
asynchronously whenever possible.
