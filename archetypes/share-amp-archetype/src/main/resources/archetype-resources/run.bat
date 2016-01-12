::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::      Dev environment startup script for Alfresco Community.    ::
::                                                                ::
::      Downloads the spring-loaded lib if not existing and       ::
::      runs the Share AMP applied to Share WAR.                  ::
::      Note. requires Alfresco.war to be running in another      ::
::      Tomcat on port 8080.                                      ::
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@echo off

set springloadedfile=%HOME%\.m2\repository\org\springframework\springloaded\@@springloaded.version@@\springloaded-@@springloaded.version@@.jar

if not exist %springloadedfile% (
  mvn validate -Psetup
)

:: Spring loaded does not work very well with 5.1 at the moment, breaks the H2 db after first run and then restart
:: set MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify
set MAVEN_OPTS=-noverify

mvn integration-test -Pamp-to-war -nsu
