Is worth documenting how to get up and running if you want to
contribute to the archtypes themselves.

# For installing archetype
mvn clean install -Dmaven.test.skip=true

# For instantiating amp archtype locally and spinning it up
cd ..
rm -rf testamp
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.alfresco.maven.archetype -DarchetypeGroupId=org.alfresco.maven.archetype -DarchetypeArtifactId=alfresco-amp-archetype -DarchetypeVersion=2.0.0-SNAPSHOT -DgroupId=org.alfresco.archetype.test -DartifactId=testamp -DinteractiveMode=false
cd testamp
chmod 755 *.sh
./debug.sh
