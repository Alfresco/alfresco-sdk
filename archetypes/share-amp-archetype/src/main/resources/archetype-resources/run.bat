@ECHO OFF

IF "%MAVEN_OPTS%" == "" (
    ECHO The environment variable 'MAVEN_OPTS' is not set, setting it for you

    SET springloadedfile=%HOME%\.m2\repository\org\springframework\springloaded\1.2.5.RELEASE\springloaded-1.2.5.RELEASE.jar

    if not exist %springloadedfile% (
      mvn validate -Psetup
    )

    :: Spring loaded can be used with the Share AMP project in 5.1
    :: (i.e. it does not have the same problem as Repo AMP and AIO)
    SET MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify
)
ECHO MAVEN_OPTS is set to '%MAVEN_OPTS%'
mvn clean install -Pamp-to-war