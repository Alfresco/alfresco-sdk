---
Title: How to configure and use Hotswap Agent
Added: v3.0.0
Last reviewed: 2021-02-09
---
# How to configure and use Hotswap Agent

[HotSwapAgent](http://hotswapagent.org/index.html) is the agent that enables you to do hot reloading. This allows you to modify the application code, and 
view the changes without having to restart Alfresco Tomcat (or the ACS Docker container).

A prerequisite for this tutorial is to have a project created with the Alfresco SDK 4, using the All-In-One archetype or the Platform JAR archetype. It's 
worth noting that hot reloading is only supported on the platform, and not in Alfresco Share.

As an alternative to the HotSwapAgent you can also try out JRebel. It has more features but isn't free.

The way to configure HotSwapAgent in case of using Java 8 or Java 11 is pretty different. By default, ACS 6.0 uses Java 8 and ACS 6.1+ uses Java 11.

## Issue with Docker Toolbox

It's worth noting that the HotSwapAgent's hot reloading mechanism is not working for [Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/) 
at the moment. Docker Toolbox is for older Mac and Windows systems that do not meet the requirements of [Docker for Mac](https://docs.docker.com/docker-for-mac/) 
and [Docker for Windows](https://docs.docker.com/docker-for-windows/).

This is due to an issue with the component used by HotSwapAgent to notify the changes in the compiled class files. HotSwapAgent uses the class 
[WatcherNIO2.java](https://github.com/HotswapProjects/HotswapAgent/blob/master/hotswap-agent-core/src/main/java/org/hotswap/agent/watch/nio/WatcherNIO2.java) to 
watch for the changes in the `extraClasspath` folder. That class is based on the Java class [WatchDir.java](https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java) 
that, in Linux systems, is implemented using [inotify](http://man7.org/linux/man-pages/man7/inotify.7.html). It seems that inotify is not working properly
with mounted volumes over Docker Toolbox (which internally uses VirtualBox).
 
You can track the evolution of this issue [here](https://github.com/moby/moby/issues/18246). 

## Configuring HotSwapAgent in the project (Java 8)

1. Modify the file `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile` to copy the HotSwapAgent configuration file into the ACS container 
classpath:

```
# Hot reload - Hotswap agent
COPY hotswap-agent.properties $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes
```

2. Modify the file `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile` to append the commands to install and configure [DCEVM](http://dcevm.github.io/) 
and the HotSwapAgent java agent in the ACS container:

```
# Download and Install the more capable DCEVM, which will allow more changes to classes, such as new methods
RUN mkdir -p dcevm \
    && curl -L -o dcevm/DCEVM-8u181-installer.jar "https://github.com/dcevm/dcevm/releases/download/light-jdk8u181%2B2/DCEVM-8u181-installer-build2.jar" \
    && cd dcevm \
    && jar -xvf DCEVM-8u181-installer.jar \
    && cp linux_amd64_compiler2/product/libjvm.so /usr/java/default/jre/lib/amd64/server

# Download HotSwap Agent - it is used in the Docker Compose file.
RUN cd /usr/local/tomcat \
    && mkdir -p hotswap-agent \
    && curl -L -o lib/hotswap-agent-1.3.0.jar "https://github.com/HotswapProjects/HotswapAgent/releases/download/RELEASE-1.3.0/hotswap-agent-1.3.0.jar"
```

3. Modify the file `docker/docker-compose.yml` to change the ACS container `CATALINA_OPTS` environment property to use the HotSwap java agent:

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888 -javaagent:/usr/local/tomcat/lib/hotswap-agent-1.3.0.jar"
...
```

4. Modify the file `docker/docker-compose.yml` to change the ACS container command to avoid the execution of Tomcat with the Security Manager enabled (it makes 
the hot reloading tools fail):

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888 -javaagent:/usr/local/tomcat/lib/hotswap-agent-1.3.0.jar"
    command: ["catalina.sh", "run"]
...
```

5. Modify the file `docker/docker-compose.yml` to mount the target folders into the folder `/usr/local/tomcat/hotswap-agent` inside the ACS container:

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888 -javaagent:/usr/local/tomcat/lib/hotswap-agent-1.3.0.jar"
    command: ["catalina.sh", "run"]
    ports:
      - "${acs.port}:8080"
      - "${acs.debug.port}:8888"
    volumes:
      - alf-acs-volume:/usr/local/tomcat/alf_data
      - ../../../sample-project-platform/target/classes:/usr/local/tomcat/hotswap-agent/sample-project-platform/target/classes
      - ../../../sample-project-integration-tests/target/test-classes:/usr/local/tomcat/hotswap-agent/sample-project-integration-tests/target/test-classes
...
```

For more information about HotSwapAgent configuration for Java 8, please check the [HotSwapAgent documentation](http://hotswapagent.org/mydoc_quickstart.html).

## Configuring HotSwapAgent in the project (Java 11)

Using Java 11 and HotSwapAgent, it isn't necessary to configure the java agent and the alternative JVM as in previous versions. Instead, it is required 
to use an alternative pre-built JDK distribution. That JDK is based on OpenJDK and includes all the required modifications to run the HotSwapAgent properly.

In the context of the Alfresco SDK 4, this change is an issue because the JDK installation is inherited from the [Alfresco java docker image](https://github.com/Alfresco/alfresco-docker-base-java). 
It is necessary to modify the project ACS docker image to change the default java installation of the container's OS to the one provided by HotSwapAgent.

A way to implement the required modifications would be:

1. Download the last release of the Trava OpenJDK (Linux distribution) from [here](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases) and save it 
into the folder `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker`.
2. Modify the file `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile` to append the commands required to install and configure the custom JDK 
for the HotSwapAgent:

```
# HOTSWAP AGENT
# Install and configure Trava OpenJDK (OpenJDK pre-built with DCEVM and hotswap agent for Java 11)
COPY trava-jdk-11-dcevm.tar.gz $TOMCAT_DIR
RUN tar -xvf $TOMCAT_DIR/trava-jdk-11-dcevm.tar.gz -C /usr/java/ && \
    rm $TOMCAT_DIR/trava-jdk-11-dcevm.tar.gz && \
    alternatives --install /usr/bin/java java /usr/java/dcevm-11.0.1+7/bin/java 40000 && \
    alternatives --install /usr/bin/javac javac /usr/java/dcevm-11.0.1+7/bin/javac 40000 && \
    alternatives --install /usr/bin/jar jar /usr/java/dcevm-11.0.1+7/bin/jar 40000 && \
    alternatives --set java /usr/java/dcevm-11.0.1+7/bin/java && \
    alternatives --set javac /usr/java/dcevm-11.0.1+7/bin/javac && \
    alternatives --set jar /usr/java/dcevm-11.0.1+7/bin/jar && \
    ln -sfn /usr/java/dcevm-11.0.1+7 /usr/java/latest && \
    ln -sfn /usr/java/dcevm-11.0.1+7 /usr/java/default
```

3. Modify the file `PROJECT_ARTIFACT_ID-platform-docker/src/main/docker/Dockerfile` to copy the HotSwapAgent configuration file into the ACS container 
classpath:

```
# Copy the configuration properties file in the classpath
COPY hotswap-agent.properties $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes
```

4. Modify the file `docker/docker-compose.yml` to change the ACS container command to avoid the execution of Tomcat with the Security Manager enabled (it makes 
the hot reloading tools fail):

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888"
    command: ["catalina.sh", "run"]
...
```

5. Modify the file `docker/docker-compose.yml` to mount the target folders into the folder `/usr/local/tomcat/hotswap-agent` inside the ACS container:

```
  sample-project-acs:
    image: alfresco-content-services-sample-project:development
    build:
      dockerfile: ./Dockerfile
      context: ../../../sample-project-platform-docker/target
    environment:
      CATALINA_OPTS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888"
    command: ["catalina.sh", "run"]
    ports:
      - "${acs.port}:8080"
      - "${acs.debug.port}:8888"
    volumes:
      - alf-acs-volume:/usr/local/tomcat/alf_data
      - ../../../sample-project-platform/target/classes:/usr/local/tomcat/hotswap-agent/sample-project-platform/target/classes
      - ../../../sample-project-integration-tests/target/test-classes:/usr/local/tomcat/hotswap-agent/sample-project-integration-tests/target/test-classes
...
```

Instead of downloading the Trava OpenJDK distribution file and copying it to the container, the Dockerfile script could include directly the download of the 
file (via `curl` for instance), but that would slow down the creation of the ACS image each time it is rebuilt.

### Creating a custom HotSwapAgent ACS docker image

Another alternative to avoid this time overhead, due to the installation of the Trava OpenJDK distribution, is to create a custom docker image that installs 
and sets that custom JDK up.

A sample `Dockerfile` for that custom image for ACS 6.0 Community could be:

```
FROM alfresco/alfresco-content-repository-community:6.0.7-ga

# HOTSWAP AGENT
# Install and configure Trava OpenJDK (OpenJDK pre-built with DCEVM and hotswap agent for Java 11)
COPY trava-jdk-11-dcevm.tar.gz $TOMCAT_DIR
RUN tar -xvf $TOMCAT_DIR/trava-jdk-11-dcevm.tar.gz -C /usr/java/ && \
    rm $TOMCAT_DIR/trava-jdk-11-dcevm.tar.gz && \
    alternatives --install /usr/bin/java java /usr/java/dcevm-11.0.1+7/bin/java 40000 && \
    alternatives --install /usr/bin/javac javac /usr/java/dcevm-11.0.1+7/bin/javac 40000 && \
    alternatives --install /usr/bin/jar jar /usr/java/dcevm-11.0.1+7/bin/jar 40000 && \
    alternatives --set java /usr/java/dcevm-11.0.1+7/bin/java && \
    alternatives --set javac /usr/java/dcevm-11.0.1+7/bin/javac && \
    alternatives --set jar /usr/java/dcevm-11.0.1+7/bin/jar && \
    ln -sfn /usr/java/dcevm-11.0.1+7 /usr/java/latest && \
    ln -sfn /usr/java/dcevm-11.0.1+7 /usr/java/default
```

That docker image can be built and pushed to your company Docker registry.

* Go to the folder where the `Dockerfile` is located and build the docker image:

```
> docker build -t "alfresco/alfresco-content-repository-community-hotswap-agent:6.0.7-ga" .
```

* Tag and push the image to your company Docker registry:

```
> docker tag DOCKER_REGISTRY_URL/alfresco/alfresco-content-repository-community-hotswap-agent:6.0.7-ga alfresco/alfresco-content-repository-community-hotswap-agent:6.0.7-ga
> docker push DOCKER_REGISTRY_URL/alfresco/alfresco-content-repository-community-hotswap-agent:6.0.7-ga
```

Once the new image is available in the Docker registry, the maven property `docker.acs.image` can be modified in the main `pom.xml` file of the project to use 
that custom image:

```
<docker.acs.image>alfresco/alfresco-content-repository-community-hotswap-agent</docker.acs.image>
```

For more information about HotSwapAgent configuration for Java 11, please check the [HotSwapAgent documentation](http://hotswapagent.org/mydoc_quickstart-jdk11.html).

## Reloading changes in source code

1. Rebuild and restart the whole project (`run.sh/run.bat build_start`).

You'll recognize HotSwapAgent is working when you see similar log messages:

```
 HOTSWAP AGENT: 14:08:07.154 DEBUG (org.hotswap.agent.util.classloader.URLClassLoaderHelper) - Added extraClassPath URLs [file:/usr/local/tomcat/hotswap-agent/] to classLoader ParallelWebappClassLoader
   context: alfresco
   delegate: false
 ----------> Parent Classloader:
 java.net.URLClassLoader@4c402120
```

2. Before making any changes, let's run the sample webscript by opening your browser and typing `http://localhost:8080/alfresco/s/sample/helloworld`.

This is a sample webscript generated in every project created using SDK 4.x and the platform artifact.

![Alt text](../../docassets/images/sdk-hellofromjava.png "Hello World webscript original result")

3. Locate `HelloWorldWebScript.java` in the `src/main/java/.../platformsample` folder of your project (If you are using an All-In-One project, the folder is 
located in the platform sub-project).

4. Edit it using your preferred editor and change the code so that `HelloFromJava` becomes `HelloFromMe`:

```
model.put(“fromJava”,”HelloFromMe”);
```

5. Save the file and compile the Java class (using your preferred IDE or the `mvn compile` command).

A number of log messages appear in the Alfresco project terminal, for example:

```
 HOTSWAP AGENT: 14:10:29.887 DEBUG (org.hotswap.agent.watch.nio.WatcherNIO2) - Watch event 'ENTRY_MODIFY' on '/usr/local/tomcat/hotswap-agent/sample-project-platform/target/classes/com/example/platformsample/HelloWorldWebScript.class' --> HelloWorldWebScript.class
 HOTSWAP AGENT: 14:10:30.319 DEBUG (org.hotswap.agent.command.impl.SchedulerImpl) - Executing pluginManager.hotswap([class com.example.platformsample.HelloWorldWebScript])
 HOTSWAP AGENT: 14:10:30.368 RELOAD (org.hotswap.agent.config.PluginManager) - Reloading classes [com.example.platformsample.HelloWorldWebScript] (autoHotswap)
 HOTSWAP AGENT: 14:10:30.387 DEBUG (org.hotswap.agent.plugin.jdk.JdkPlugin) - Flushing com.example.platformsample.HelloWorldWebScript from introspector
 HOTSWAP AGENT: 14:10:30.394 DEBUG (org.hotswap.agent.plugin.jdk.JdkPlugin) - Flushing com.example.platformsample.HelloWorldWebScript from ObjectStreamClass caches
 HOTSWAP AGENT: 14:10:30.399 DEBUG (org.hotswap.agent.plugin.jvm.ClassInitPlugin) - Adding $ha$$clinit to class: com.example.platformsample.HelloWorldWebScript
 HOTSWAP AGENT: 14:10:30.422 DEBUG (org.hotswap.agent.plugin.jvm.ClassInitPlugin) - Skipping old field logger
 HOTSWAP AGENT: 14:10:33.312 DEBUG (org.hotswap.agent.config.PluginManager) - ... reloaded classes [com.example.platformsample.HelloWorldWebScript] (autoHotswap)
```

6. Refresh the browser to see the updated message:

![alt text](../../docassets/images/sdk-hellofromme.png "Hello World webscript modified result")

By changing the code and compiling it again, the changes have been dynamically received from Alfresco Content Services.
