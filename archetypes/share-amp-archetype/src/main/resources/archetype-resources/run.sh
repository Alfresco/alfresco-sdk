#!/bin/bash
# Downloads the spring-loaded lib if not existing and runs the Share AMP applied to Share WAR
# Note. requires Alfresco.war to be running in another Tomcat on port 8080
springloadedfile=~/.m2/repository/org/springframework/springloaded/@@springloaded.version@@/springloaded-@@springloaded.version@@.jar

if [ ! -f $springloadedfile ]; then
mvn validate -Psetup
fi

# Spring loaded can be used with the Share AMP project
MAVEN_OPTS="-javaagent:$springloadedfile -noverify" mvn integration-test -Pamp-to-war
