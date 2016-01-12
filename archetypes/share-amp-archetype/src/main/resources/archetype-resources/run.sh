#!/bin/bash
# Downloads the spring-loaded lib if not existing and runs the Share AMP applied to Share WAR
# Note. requires Alfresco.war to be running in another Tomcat on port 8080
springloadedfile=~/.m2/repository/org/springframework/springloaded/@@springloaded.version@@/springloaded-@@springloaded.version@@.jar

if [ ! -f $springloadedfile ]; then
mvn validate -Psetup
fi

# Spring loaded does not work very well with 5.1 at the moment, breaks the H2 db after first run and then restart
# MAVEN_OPTS="-javaagent:$springloadedfile -noverify" mvn integration-test -Pamp-to-war
MAVEN_OPTS="-noverify" mvn integration-test -Pamp-to-war
