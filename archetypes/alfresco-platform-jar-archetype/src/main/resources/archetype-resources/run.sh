#set( $symbol_dollar = '$' )
#!/bin/sh

export COMPOSE_FILE_PATH=${symbol_dollar}{PWD}/target/classes/docker/docker-compose.yml

if [ -z "${symbol_dollar}{M2_HOME}" ]; then
  export MVN_EXEC="mvn"
else
  export MVN_EXEC="${symbol_dollar}{M2_HOME}/bin/mvn"
fi

start() {
    docker volume create ${rootArtifactId}-acs-volume
    docker volume create ${rootArtifactId}-db-volume
    docker volume create ${rootArtifactId}-ass-volume
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH up --build -d
}

down() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH down
}

purge() {
    docker volume rm ${rootArtifactId}-acs-volume
    docker volume rm ${rootArtifactId}-db-volume
    docker volume rm ${rootArtifactId}-ass-volume
}

build() {
    docker rmi alfresco-content-services-${rootArtifactId}:development
    ${symbol_dollar}MVN_EXEC clean install -DskipTests=true
}

tail() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH logs -f
}

tail_all() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH logs --tail="all"
}

test() {
    ${symbol_dollar}MVN_EXEC verify
}

case "${symbol_dollar}1" in
  build_start)
    down
    build
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
  build_test)
    down
    build
    start
    test
    tail_all
    down
    ;;
  test)
    test
    ;;
  *)
    echo "Usage: ${symbol_dollar}0 {build_start|start|stop|purge|tail|build_test|test}"
esac