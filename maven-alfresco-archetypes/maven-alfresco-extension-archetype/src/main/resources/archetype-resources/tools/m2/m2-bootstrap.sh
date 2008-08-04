#!/bin/bash
#
# Name:   m2-bootstrap.sh
# Author: g.columbro@sourcesense.com
#
# Description:
# This script is needed *only* in case you *don't* have you don't have alfresco artifacts available in any public repo,
# and you can't connect to Sourcesense public repo. 
# So you can manually downlaod JAR and WAR alfresco artifacts in $BASE_DIR (1st param)
# and have them deployed to $TARGET_REPO (2nd param) and $TARGET_REPO_URL (3rd param)
# by running this script and passing the 5 params in the command line. 4th param indicates the version
# while the 5th one the alfresco distro (labs vs enteprise) we're going to deploy.
#
# Note: 
# This script works for alfresco > 3.0 artifacts. It must be modified for
# earlier versions (as share.war won't be present)
#
# License:
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#    
# http://www.apache.org/licenses/LICENSE-2.0
#    
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Example run:  
#				./m2-bootstrap.sh 
#				/Users/mindthegab/downloads/alfresco/alfresco-labs-war-3a.1032_2 
#				ss-public 
#				scp://repository.sourcesense.com/var/www/demo.sourcesense.com/maven2 
#				3a.1032 
#				labs
#
# Artifacts will be deployed with the following pattern:
#
#  org.alfresco:alfresco[-*]:[jar|war]:[VERSION]:[RELEASE]
#
# To have this fully working you need to have the following alfresco BASE_DIR layout:
#
#   BASE_DIR
#		|____ alfresco.war
#		|____ alfresco
#		|			|__ WEB-INF
#		|					|__ lib
#		|						 |___ alfresco-*.jar
#		|____ share.war
#		|____ share
#				|__ WEB-INF
#						|__ alfresco-*.jar
#
# which you can easily obtain downloading an alfresco WAR distribution and unpacking both alfresco.war and share.war in folders with the same name


# 1st command line param: 
# directory where jar and war dependencies are stored
BASE_DIR=$1
# 2st command line param: 
# target repo id (matches in settings)
TARGET_REPO=$2
# 3rd command line param: 
# target repo url
TARGET_REPO_URL=$3
# 4th command line param: 
# Version 
VERSION=$4
# 5th command line param: 
# Release [labs|enterprise]
RELEASE=$5

echo "Starting Alfresco JARs uploading to repo ${TARGET_REPO} at ${TARGET_REPO_URL}

mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-core.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-core -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar  -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-deployment.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-deployment -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-jlan-embed.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-jlan-embed -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-linkvalidation.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-linkvalidation -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-mbeans.jar  -DrepositoryId=${TARGET_REPO}  -DgroupId=org.alfresco -DartifactId=alfresco-mbeans -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-remote-api.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-remote-api -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-repository.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-repository -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-web-client.jar   -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-web-client -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco/WEB-INF/lib/alfresco-webscript-framework.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-webscript-framework -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/share/WEB-INF/lib/alfresco-share.jar  -DrepositoryId=${TARGET_REPO}  -DgroupId=org.alfresco -DartifactId=alfresco-share -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/share/WEB-INF/lib/alfresco-web-framework.jar  -DrepositoryId=${TARGET_REPO} -DgroupId=org.alfresco -DartifactId=alfresco-web-framework -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=jar -Dclassifier=${RELEASE}

echo "Starting Alfresco WARs uploading to repo ${TARGET_REPO} at ${TARGET_REPO_URL}

mvn deploy:deploy-file -Dfile=${BASE_DIR}/alfresco.war  -DrepositoryId=${TARGET_REPO}  -DgroupId=org.alfresco -DartifactId=alfresco -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=war -Dclassifier=${RELEASE}
mvn deploy:deploy-file -Dfile=${BASE_DIR}/share.war  -DrepositoryId=${TARGET_REPO}  -DgroupId=org.alfresco -DartifactId=share -Dversion=${VERSION} -Durl=${TARGET_REPO_URL} -Dpackaging=war -Dclassifier=${RELEASE}

echo "Artifacts uploaded"
