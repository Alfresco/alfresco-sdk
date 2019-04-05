#set( $symbol_dollar = '$' )
#!/bin/sh

export COMPOSE_FILE_PATH="${symbol_dollar}{PWD}/target/classes/docker/docker-compose.yml"

if [ -z "${symbol_dollar}{M2_HOME}" ]; then
  export MVN_EXEC="mvn"
else
  export MVN_EXEC="${symbol_dollar}{M2_HOME}/bin/mvn"
fi

start() {
    docker volume create ${rootArtifactId}-acs-volume
    docker volume create ${rootArtifactId}-db-volume
    docker volume create ${rootArtifactId}-ass-volume
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" up --build -d
}

start_share() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" up --build -d ${rootArtifactId}-share
}

start_acs() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" up --build -d ${rootArtifactId}-acs
}

down() {
    if [ -f "${symbol_dollar}COMPOSE_FILE_PATH" ]; then
        docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" down
    fi
}

purge() {
    docker volume rm -f ${rootArtifactId}-acs-volume
    docker volume rm -f ${rootArtifactId}-db-volume
    docker volume rm -f ${rootArtifactId}-ass-volume
}

build() {
    ${symbol_dollar}MVN_EXEC clean package
}

build_share() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" kill ${rootArtifactId}-share
    yes | docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" rm -f ${rootArtifactId}-share
    ${symbol_dollar}MVN_EXEC clean package -pl ${rootArtifactId}-share,${rootArtifactId}-share-docker
}

build_acs() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" kill ${rootArtifactId}-acs
    yes | docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" rm -f ${rootArtifactId}-acs
    ${symbol_dollar}MVN_EXEC clean package -pl ${rootArtifactId}-integration-tests,${rootArtifactId}-platform,${rootArtifactId}-platform-docker
}

tail() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" logs -f
}

tail_all() {
    docker-compose -f "${symbol_dollar}COMPOSE_FILE_PATH" logs --tail="all"
}

prepare_test() {
    ${symbol_dollar}MVN_EXEC verify -DskipTests=true -pl ${rootArtifactId}-platform,${rootArtifactId}-integration-tests,${rootArtifactId}-platform-docker
}

test() {
    ${symbol_dollar}MVN_EXEC verify -pl ${rootArtifactId}-platform,${rootArtifactId}-integration-tests
}

case "${symbol_dollar}1" in
  build_start)
    down
    build
    start
    tail
    ;;
  build_start_it_supported)
    down
    build
    prepare_test
    start
    tail
    ;;
  start)
    start
    tail
    ;;
  stop)
    down
    ;;
  purge)
    down
    purge
    ;;
  tail)
    tail
    ;;
  reload_share)
    build_share
    start_share
    tail
    ;;
  reload_acs)
    build_acs
    start_acs
    tail
    ;;
  build_test)
    down
    build
    prepare_test
    start
    test
    tail_all
    down
    ;;
  test)
    test
    ;;
  *)
    echo "Usage: ${symbol_dollar}0 {build_start|build_start_it_supported|start|stop|purge|tail|reload_share|reload_acs|build_test|test}"
esac