FROM ${docker.acs.image}:${alfresco.platform.version}

ARG TOMCAT_DIR=/usr/local/tomcat

# Copy Dockerfile to avoid an error if no JARs exist
COPY Dockerfile extensions/*.jar $TOMCAT_DIR/webapps/alfresco/WEB-INF/lib/

# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force

COPY alfresco-global.properties $TOMCAT_DIR/shared/classes/alfresco-global.properties
COPY dev-log4j.properties $TOMCAT_DIR/shared/classes/alfresco/extension
COPY disable-webscript-caching-context.xml $TOMCAT_DIR/shared/classes/alfresco/extension

# Copy Dockerfile to avoid an error if no license file exists
COPY Dockerfile license/*.* $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes/alfresco/extension/license/

# Alfresco configuration for running locally with PostgreSQL Database
RUN echo -e '\n\
# Alfresco Repo Webapp (alfresco.war) context, ports etc\n\
alfresco.context=alfresco\n\
alfresco.host=localhost\n\
alfresco.port=8080\n\
alfresco.protocol=http\n\
\n\
# Alfresco Share Webapp (share.war) context, ports etc\n\
share.context=share\n\
share.host=localhost\n\
share.port=8180\n\
share.protocol=http\n\
\n\
# Don''t try and recover any index\n\
index.recovery.mode=NONE\n\
\n\
# These jobs seem to require Lucene (Unsupported Operation with Solr) so we disable them / set to future date\n\
# See https://forums.alfresco.com/en/viewtopic.php?f=52&t=41597\n\
# If you want to enable them (and so full WQS functionality), please also set index.subsystem.name=lucene\n\
wcmqs.dynamicCollectionProcessor.schedule=0 30 2 * * ? 2060\n\
wcmqs.feedbackProcessor.schedule=0 40 2 * * ? 2060\n\
wcmqs.publishQueueProcessor.schedule=0 50 2 * * ? 2060\n\
\n\
# Fail or not when there are node integrity checker errors\n\
integrity.failOnError=true\n\
\n\
# Alfresco Repository PostgreSQL Database configuration.\n\
db.driver=org.postgresql.Driver\n\
\n\
# This Alfresco Platform Configuration file should be used for custom properties that are introduced by this module.\n\
# Define default values for all properties here.\n\
# System Administrators can override these values in environment specific configurations in\n\
# alfresco/tomcat/shared/classes/alfresco-global.properties.\n\
#\n\
index.subsystem.name=solr6\n\
solr.host=${rootArtifactId}-ass\n\
solr.port=8983\n\
solr.secureComms=none\n\
\n\
db.username=alfresco\n\
db.password=alfresco\n\
db.pool.initial=10\n\
db.pool.max=100\n\
\n\
db.url=jdbc:postgresql://${rootArtifactId}-postgres:5432/alfresco\n\
\n\
# File servers related properties\n\
# For local runs we disable CIFS and FTP\n\
cifs.enabled=false\n\
ftp.enabled=false\n\
\n\
csrf.filter.enabled=false\n\
\n\
# Embedded broker without persistence\n\
messaging.broker.url=vm://localhost?broker.persistent=false\n\
\n\
' >> /usr/local/tomcat/shared/classes/alfresco-global.properties
