---
Title: Working with generated projects
Added: v3.0.0
Last reviewed: 2019-01-15
---
# Working with generated projects

After generating a project using one of the Alfresco SDK 4.0 Maven archetypes, it is important to know how to build / run / test these projects.

The Alfresco Platform 6 deployment architecture is highly based on container technologies, specifically in 
[Docker](http://docs.alfresco.com/6.0/concepts/master-deploy.html). Due to that, the projects generated using the Alfresco SDK 4.0 archetypes set up their
local environment making an intensive use of Docker and Docker compose technologies.

If you're not familiar with these technologies, it is highly recommended visiting the [Docker documentation website](https://docs.docker.com). This site offers
a great quantity of training resources about [Docker](https://docs.docker.com/get-started/) and [Docker compose](https://docs.docker.com/compose/gettingstarted/).

## Project structures

After generating your project, using one of the Maven archetypes, review the project structure. The directory structure and content of each folder and file 
can help you to understand how to start developing with the Alfresco SDK 4.0. Before continuing, make sure that you have read and completed the tasks in the 
[Getting started](../getting-started.md) tutorial.

The structure of the project and the purpose of the files it contains vary according to the [Maven archetype](../mvn-archetypes.md) used to generate the project 
itself. The following links provide detailed descriptions of the different project types.

* [All-In-One project structure](structure-aio.md)
* [Platform JAR project structure](structure-platform.md)
* [Share JAR project structure](structure-share.md)

## Run script

All the projects generated using the Alfresco SDK 4.0 archetypes provide a utility script to work with the project. This script is `run.sh` for Unix systems
and `run.bat` for Windows systems.

The execution of this script must be followed by a parameter that dictates the task to be executed in the project. The list of available tasks is:

Task | Description
--- | ---
`build_start` | Build the whole project, recreate the ACS and Share docker images, start the dockerised environment composed by ACS, Share, ASS and PostgreSQL and tail the logs of all the containers.
`build_start_it_supported` | Build the whole project including dependencies required for IT execution, recreate the ACS and Share docker images, start the dockerised environment composed by ACS, Share, ASS and PostgreSQL and tail the logs of all the containers.
`start` | Start the dockerised environment without building the project and tail the logs of all the containers.
`stop` | Stop the dockerised environment.
`purge` | Stop the dockerised environment and delete all the persistent data (docker volumes).
`tail` | Tail the logs of all the containers.
`reload_share` | Build the Share module, recreate the Share docker image and restart the Share container (not available in the Alfresco Platform JAR archetype).
`reload_acs` | Build the ACS module, recreate the ACS docker image and restart the ACS container (only available in the All-In-One archetype).
`build_test` | Build the whole project, recreate the ACS and Share docker images, start the dockerised environment, execute the integration tests from the `integration-tests` module and stop the environment.
`test` | Execute the integration tests (the environment must be already started).

This utility script uses `mvn`, `docker` and `docker-compose` commands, so make sure you have properly installed Maven, Docker and Docker compose and you have 
configured them properly to be accessible in the path.

In the case of Maven, it is not necessary that the `mvn` executable is in the path if you've properly configured the environment variable `M2_HOME`. The script
looks for the `M2_HOME` environment variable to build the path to the `mvn` executable. 
