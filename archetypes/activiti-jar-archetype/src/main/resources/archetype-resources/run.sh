#!/bin/bash

MAVEN_OPTS="-Xms1G -Xmx2G" mvn clean install alfresco:run
