#set( $symbol_dollar = '$' )
@ECHO OFF

SET COMPOSE_FILE_PATH=%CD%\target\classes\docker\docker-compose.yml

IF [%1]==[] (
    echo "Usage: %0 {build_start|start|stop|purge|tail|build_test|test}"
    GOTO END
)

IF %1==build_start (
    CALL :down
    CALL :build
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==start (
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==stop (
    CALL :down
    GOTO END
)
IF %1==purge (
    CALL:down
    CALL:purge
    GOTO END
)
IF %1==tail (
    CALL :tail
    GOTO END
)
IF %1==build_test (
    CALL :down
    CALL :build
    CALL :start
    CALL :test
    CALL :down
    GOTO END
)
IF %1==test (
    CALL :test
    GOTO END
)
echo "Usage: %0 {build_start|start|stop|purge|tail|build_test|test}"
:END
EXIT /B %ERRORLEVEL%

:start
    docker volume create alf-acs-volume
    docker volume create alf-db-volume
    docker volume create alf-ass-volume
    docker-compose -f "%COMPOSE_FILE_PATH%" up --build -d
EXIT /B 0
:down
    docker-compose -f "%COMPOSE_FILE_PATH%" down
EXIT /B 0
:build
    docker rmi alfresco-content-services-${rootArtifactId}:development
	call mvn clean install -DskipTests
EXIT /B 0
:tail
    docker-compose -f "%COMPOSE_FILE_PATH%" logs -f
EXIT /B 0
:test
    call mvn verify
EXIT /B 0
:purge
    docker volume rm alf-acs-volume
    docker volume rm alf-db-volume
    docker volume rm alf-ass-volume
EXIT /B 0