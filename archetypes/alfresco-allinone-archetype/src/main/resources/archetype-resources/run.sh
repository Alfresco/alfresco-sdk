#!/bin/bash
# Downloads the spring-loaded lib if not existing and runs the full all-in-one
# (Alfresco + Share + Solr) using the runner project
springloadedfile=~/.m2/repository/org/springframework/springloaded/@@springloaded.version@@/springloaded-@@springloaded.version@@.jar

if [ ! -f $springloadedfile ]; then
mvn validate -Psetup
fi
# Spring loaded does not work very well with 5.1 at the moment, breaks the H2 db after first run and then restart
#MAVEN_OPTS="-javaagent:$springloadedfile -noverify -Xms256m -Xmx2G" mvn clean install -Prun
MAVEN_OPTS="-noverify -Xms256m -Xmx2G" mvn clean install -Prun