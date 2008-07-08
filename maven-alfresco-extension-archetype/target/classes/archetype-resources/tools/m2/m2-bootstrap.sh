#!/bin/bash
#
#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#    
#    http://www.apache.org/licenses/LICENSE-2.0
#    
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#   
#
# This script is needed *only* in case you *don't* have you don't have alfresco artifacts available in any public repo,
# and you can't connect to Sourcesense public repo. 
#
# So you can manually downlaod JAR and WAR alfresco artifacts in $BASE_DIR
# and have them deployed to $TARGET_REPO (id here) and $TARGET_REPO_URL (url here)
# by running this script and passing the 3 params in the command line
#
# Note: This script works for alfresco 2.1.0 community artifacts. It must be modified for
# other versions.
#
# TODO: Make alfresco version and release parametric 
#

# 1st command line param: directory where jar and war dependencies are stored
BASE_DIR=$1
# 2st command line param: target repo id (matches in settings)
TARGET_REPO=$2
# 3rd command line param: target repo url
TARGET_REPO_URL=$3

mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco-core.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=alfresco.community -DartifactId=alfresco-core -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=jar
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco-repository.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=alfresco.community -DartifactId=alfresco-repository -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=jar
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco.war  -DrepositoryId=${TARGET_REPO}  -DgroupId=alfresco.community -DartifactId=alfresco -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=war
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco-web-client.jar   -DrepositoryId=${TARGET_REPO} -DgroupId=alfresco.community -DartifactId=alfresco-web-client -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=jar
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco-mbeans.jar  -DrepositoryId=${TARGET_REPO}  -DgroupId=alfresco.community -DartifactId=alfresco-mbeans -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=jar
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco-remote-api.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=alfresco.community -DartifactId=alfresco-remote-api -Dversion=2.1.0 -Durl=${TARGET_REPO_URL} -Dpackaging=jar
