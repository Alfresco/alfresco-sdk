#set( $symbol_dollar = '$' )
#!/bin/sh

export COMPOSE_FILE_PATH=${symbol_dollar}{PWD}/target/classes/docker/docker-compose.yml



${symbol_dollar}M2_HOME/bin/mvn -v 2>&1

env

start() {
    docker volume create ${rootArtifactId}-acs-volume
    docker volume create ${rootArtifactId}-db-volume
    docker volume create ${rootArtifactId}-ass-volume
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH up --build -d
}

start_share() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH up --build -d ${rootArtifactId}-share
}

start_acs() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH up --build -d ${rootArtifactId}-acs
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
    docker rmi alfresco-share-${rootArtifactId}:development
    ${symbol_dollar}M2_HOME/bin/mvn clean install -DskipTests=true
}

build_share() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH kill ${rootArtifactId}-share
    yes | docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH rm -f ${rootArtifactId}-share
    docker rmi alfresco-share-${rootArtifactId}:development
    ${symbol_dollar}M2_HOME/bin/mvn clean install -DskipTests=true -pl ${rootArtifactId}-share-jar
}

build_acs() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH kill ${rootArtifactId}-acs
    yes | docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH rm -f ${rootArtifactId}-acs
    docker rmi alfresco-content-services-${rootArtifactId}:development
    ${symbol_dollar}M2_HOME/bin/mvn clean install -DskipTests=true -pl ${rootArtifactId}-platform-jar
}

tail() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH logs -f
}

tail_all() {
    docker-compose -f ${symbol_dollar}COMPOSE_FILE_PATH logs --tail="all"
}

test() {
    ${symbol_dollar}M2_HOME/bin/mvn verify -pl integration-tests
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
    start
    test
    tail_all
    down
    ;;
  test)
    test
    ;;
  *)
    echo "Usage: ${symbol_dollar}0 {build_start|start|stop|purge|tail|reload_share|reload_acs|build_test|test}"
esac