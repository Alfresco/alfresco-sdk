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
# This script is needed *only* in case you use Ant.
# You *need* to run this script otherwise build will fail without finding JAR and WAR artifacts.
# It will fetch alfresco dependencies specified in this file
# and download them when the Ant build process is able to find them,
# e.g.
# - the alfresco war unpacked in the  $PROJECT_HOME/tools/ant/deps/alfresco folder
# - jars in $PROJECT_HOME/tools/ant/deps/alfresco/WEB-INF/lib will be used as jar dependencies by the build process. Change ALFRESCO_DIR accordingly to corresponding build.xml property.
#
# Use these properties to specify alfresco version/release from command line

ALFRESCO_DIR=deps/alfresco
ALFRESCO_VERSION="2.1.0"
ALFRESCO_RELEASE="community"
  
  
if [ $1 ]; then       
	echo "Using command line specified ALFRESCO_VERSION=${1}"
	ALFRESCO_VERSION=$1
else
	echo "Defaulting at ALFRESCO_VERSION=${ALFRESCO_VERSION}" 
fi
if [ $2 ]; then       
		echo "Using command line specified ALFRESCO_RELEASE=${2}"
		ALFRESCO_RELEASE=$2
	else
		echo "Defaulting at ALFRESCO_RELEASE=${ALFRESCO_RELEASE}" 
fi
   
    
echo "[INFO]" - Downloading Alfresco ${ALFRESCO_RELEASE} ${ALFRESCO_VERSION} WAR into ${ALFRESCO_DIR}
# Go to the target folder
cd ${ALFRESCO_DIR}
# Downloads alfresco 
echo "[INFO]" - Unzipping Alfresco ${ALFRESCO_RELEASE} ${ALFRESCO_VERSION} WAR into ${ALFRESCO_DIR}
wget http://repository.sourcesense.com/maven2/alfresco/${ALFRESCO_RELEASE}/alfresco/${ALFRESCO_VERSION}/alfresco-${ALFRESCO_VERSION}.war
unzip alfresco-${ALFRESCO_VERSION}.war
rm -Rf alfresco-${ALFRESCO_VERSION}.war