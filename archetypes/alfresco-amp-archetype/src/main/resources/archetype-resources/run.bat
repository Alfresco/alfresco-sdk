::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::      Dev environment startup script for Alfresco Community.    ::
::                                                                ::
::      Downloads the spring-loaded lib if not existing and       ::
::      runs the Repo AMP applied to Alfresco WAR.                ::
::      Note. the Share WAR is not deployed.                      ::
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@echo off

set springloadedfile=%HOME%\.m2\repository\org\springframework\springloaded\@@springloaded.version@@\springloaded-@@springloaded.version@@.jar

if not exist %springloadedfile% (
  mvn validate -Psetup
)

:: Use these settings if you're using JDK7
:: set MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify -Xms256m -Xmx2G -XX:PermSize=300m

:: Spring loaded does not work very well with 5.1 at the moment, breaks the H2 db after first run and then restart
::set MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify -Xms256m -Xmx2G
set MAVEN_OPTS=-noverify -Xms256m -Xmx2G

mvn integration-test -Pamp-to-war -nsu

