#!/bin/sh
MAVEN_OPTS="-Xms256m -Xmx2G -XX:PermSize=300m" mvn integration-test -Pamp-to-war
