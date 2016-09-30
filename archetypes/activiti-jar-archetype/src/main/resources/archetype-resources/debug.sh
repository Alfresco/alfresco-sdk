#!/bin/bash

MAVEN_OPTS="-Xms1G -Xmx2G" mvnDebug clean install alfresco:run
