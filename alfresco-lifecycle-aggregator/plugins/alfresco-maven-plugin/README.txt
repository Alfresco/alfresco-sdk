***************
What does it do
***************

The alfresco-maven-plugin provides the following features:

* Packages an AMP starting from a simple (and configurable) Maven project folder structure
* Performs AMP to WAR overlay by using the Alfresco Repository ModuleManagementTool and emulating the same process
during Alfresco boostrap


*****
Usage
*****

+ In order to build an AMP file, you must:
----

1. Define your POM as <packaging>amp</packaging>

2. Specify a module.properties file in the src/main/amp folder, containing the following properties:
module.id=${project.artifactId}
module.title=${project.name}
module.description=${project.description}
module.version=${project.version}
As you can see, the file is filtered with Maven project placeholders

3. Declare the alfresco-maven-plugin in your POM build section

    <build>
        <plugins>
            <plugin>
                <groupId>org.alfresco.maven.plugin</groupId>
                <artifactId>alfresco-maven-plugin</artifactId>
                <version>0.7-SNAPSHOT</version>
            </plugin>
        </plugins>
        ...
    </build>

Always keep in mind the default project-to-AMP mapping:
* src/main/amp => /
* src/main/resources => /lib/amp-classes.jar
* src/main/java      => /lib/amp-classes.kar


+ In order to overlay an existing Alfresco WAR file, you'll need the following elements:
----

1. Define the type of Alfresco WAR you want to overlay: share or alfresco

  <properties>
    <alfresco.client.war>share</alfresco.client.war>
  </properties>

2. Define the following build behaviour

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
          <execution>
            <id>unpack-alfresco</id>
            <phase>prepare-package</phase>
            <goals>
              <goal>unpack</goal>
            </goals>
            <configuration>
              <outputDirectory>${project.build.directory}/${project.build.finalName}</outputDirectory>
              <artifactItems>
                <artifactItem>
                  <groupId>${alfresco.groupId}</groupId>
                  <artifactId>alfresco</artifactId>
                  <type>war</type>
                  <version>${alfresco.version}</version>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <!-- All artifacts with AMP extensions are unpacked -->
      <!-- using a WAR layout on an empty folder using alfresco-maven-plugin -->
      <plugin>
        <groupId>org.alfresco.maven.plugin</groupId>
        <artifactId>alfresco-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>amps-to-war-overlay</id>
            <phase>prepare-package</phase>
            <goals>
              <goal>install</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>