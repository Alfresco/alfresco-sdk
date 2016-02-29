#!/bin/bash
# Downloads the spring-loaded lib if not existing and      
# runs the Repo AMP applied to Alfresco WAR.           
# Note. the Share WAR is not deployed.              
springloadedfile=~/.m2/repository/org/springframework/springloaded/@@springloaded.version@@/springloaded-@@springloaded.version@@.jar

if [ ! -f $springloadedfile ]; then
mvn validate -Psetup
fi

# Use these settings if you're using JDK7
# MAVEN_OPTS="-javaagent:$springloadedfile -noverify -Xms256m -Xmx2G -XX:PermSize=300m" mvn install -Prun

# Spring loaded does not work very well with 5.1 at the moment, breaks the H2 db after first run and then restart
#MAVEN_OPTS="-javaagent:$springloadedfile -noverify -Xms256m -Xmx2G" mvn integration-test -Pamp-to-war
MAVEN_OPTS="-noverify -Xms256m -Xmx2G" mvn integration-test -Pamp-to-war