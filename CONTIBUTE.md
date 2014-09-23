# For installing archetype
mvn install -Dmaven.test.skip=true


# For instantiating amp archtype locally and spinning it up
cd ..
rm -rf testamp
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.alfresco.maven.archetype -DarchetypeGroupId=org.alfresco.maven.archetype -DarchetypeArtifactId=alfresco-amp-archetype -DarchetypeVersion=2.0.0-SNAPSHOT -DgroupId=org.alfresco.archetype.test -DartifactId=testamp -DinteractiveMode=false
cd testamp
chmod 755 run.sh
./run.sh
