::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::      Dev environment startup script for Alfresco Community     ::
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@echo off

set springloadedfile=%HOME%\.m2\repository\org\springframework\springloaded\1.2.3.RELEASE\springloaded-1.2.3.RELEASE.jar

if not exist %springloadedfile% (
  mvn validate -Psetup
)

set MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify -Xms256m -Xmx2G -XX:PermSize=300m

mvn install -Prun -nsu
:: mvn install -Prun 
