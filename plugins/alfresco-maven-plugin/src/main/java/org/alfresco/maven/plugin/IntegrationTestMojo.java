/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.maven.plugin;

import org.alfresco.maven.plugin.config.ModuleDependency;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Alfresco Plugin mojo that are used when you want to run Integration Tests.
 * It will package up all the Integration Test classes and execute contained
 * tests with the Maven Failsafe plugin. The test classes will be added
 * to the platform war so they can be executed remotely via
 * the ${@link org.alfresco.rad.test.AlfrescoTestRunner}
 * <p/>
 * The Alfresco RAD module is also added to the Platform WAR so
 * the Alfresco Test runner classes are available.
 *
 * @author martin.bergljung@alfresco.com
 * @since 3.0
 */
@Mojo(name = "it",
        defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST,
        aggregator = true, // Only run against the top-level project in a Maven build
        requiresDependencyResolution = ResolutionScope.TEST)
public class IntegrationTestMojo extends AbstractRunMojo {

    @Override
    public void execute() throws MojoExecutionException {
        execEnv = executionEnvironment(
                project,
                session,
                pluginManager
        );

        // Check if we should skip this Mojo execution, i.e. if tests have been
        // skipped by the user
        Properties sysProperties = execEnv.getMavenSession().getSystemProperties();
        boolean skipThisMojo = sysProperties.containsKey("skipTests") ||
                sysProperties.containsKey("skipITs") ||
                sysProperties.containsKey("maven.test.skip");
        if (skipThisMojo) {
            getLog().info("Skipping integration testing.");
            return;
        }

        List<String> goals = execEnv.getMavenSession().getGoals();
        if (goals.contains("alfresco:run")) {
            sysProperties.put("skipTests", "true");
            getLog().info("Skipping integration testing as alfresco:run is active.");
            return;
        }

//        execEnv.getMavenSession().getGoals().add("alfresco:it");

        getLog().info("Checking if Tomcat is already running on port " + "");

        if (enableSolr) {
            unpackSolrConfig();
            fixSolrHomePath();
            replaceSolrConfigProperties();
            installSolr10InLocalRepo();
        }

        if (enableTestProperties && enablePlatform) {
            copyAlfrescoGlobalProperties();
            renameAlfrescoGlobalProperties();
        }

        String testJarArtifactId = null;
        if (enablePlatform) {
            // Add alfresco-rad module to platform WAR
            // So we got access to Alfresco Test Runner in the server
            platformModules.add(
                    new ModuleDependency(
                            "org.alfresco.maven",
                            "alfresco-rad",
                            "${alfresco.sdk.version}",
                            ModuleDependency.TYPE_JAR));

            // Create a JAR with all tests and add to module dependencies for platform WAR
            // So we can run tests on the server
            // TODO: remove    different approach at the moment with separate integration-tests module   copyTestClassesFromSubModules2Parent();
            // TODO: remove    testJarArtifactId = packageAndInstallTestsJar();
            // TODO: remove platformModules.add(
            // TODO:         new ModuleDependency(
            // TODO:                 project.getGroupId(),
            // TODO:                 testJarArtifactId,
            // TODO:                 project.getVersion(),
            // TODO:                 ModuleDependency.TYPE_JAR));
            // Now build the platform WAR
            buildPlatformWar();
        }

        if (enableShare) {
            buildShareWar();
        }

        if (enableActivitiApp) {
            buildActivitiAppWar();
        }

        if (startTomcat) {
            boolean fork = true;
            startTomcat(fork);
            // TODO: remove    different approach at the moment with separate integration-tests module   runIntegrationTests(testJarArtifactId);
            // TODO: remove    stopTomcat();
        }
    }

    /**
     * In an AIO project copy all integration test (IT) test-classes from sub projects/modules
     * to the parent target/test-classes
     *
     * @throws MojoExecutionException
     */
    protected void copyTestClassesFromSubModules2Parent() throws MojoExecutionException {
        // Get sub-module names, so we can see where to copy test classes from
        List<String> childModules = project.getModules();
        if (childModules == null || childModules.size() <= 0) {
            // Running in a single JAR module, nothing to copy,
            // all test classes are already in top level target/test-classes
            return;
        }

        for (String module : childModules) {
            getLog().info("Copying integration test-classes (*IT.class) from module '" + module +
                    "' to parent target/test-classes");

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(MAVEN_RESOURCE_PLUGIN_VERSION)
                    ),
                    goal("copy-resources"),
                    configuration(
                            element(name("outputDirectory"), "${project.build.testOutputDirectory}"),
                            element(name("resources"),
                                    element(name("resource"),
                                            element(name("directory"), module + "/target/test-classes"),
                                            element(name("includes"),
                                                    element(name("include"), "**/*.class")
                                            ),
                                            element(name("filtering"), "false")
                                    )
                            )
                    ),
                    execEnv
            );
        }
    }

    /**
     * Package all IT Tests in JAR file and install it in local maven repo.
     *
     * @return the customized JAR file artifactId, to be used by the failsafe plugin
     * @throws MojoExecutionException
     */
    protected String packageAndInstallTestsJar() throws MojoExecutionException {
        final String jarArtifactId = project.getArtifactId() + "-IT-classes";

        // Package the JAR file with all the Integration Tests (IT)
        // Note. don't use the maven-war-plugin here as it will package the module files twice, once from the
        // target/classes dir and once via the JAR
        final String jarPath = project.getBuild().getDirectory() + "/" + jarArtifactId + ".jar";
        final String jarSourceDir = project.getBuild().getDirectory() + "/test-classes";

        ZipUtil.pack(new File(jarSourceDir), new File(jarPath));

        getLog().info("Installing integration test JAR into local Maven repo.");

        // Install the Test JAR file in the local maven repo
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-install-plugin"),
                        version(MAVEN_INSTALL_PLUGIN_VERSION)
                ),
                goal("install-file"),
                configuration(
                        element(name("file"), jarPath),
                        element(name("groupId"), "${project.groupId}"),
                        element(name("artifactId"), jarArtifactId),
                        element(name("version"), "${project.version}"),
                        element(name("packaging"), "jar") // Don't forget, otherwise installed as .POM
                )
                , execEnv
        );

        return jarArtifactId;
    }

    /**
     * Run all IT tests contained in JAR with passed in artifact ID. Group ID and Version will be the same
     * as the project.
     *
     * @param testJarArtifactId
     * @throws MojoExecutionException
     */
    protected void runIntegrationTests(String testJarArtifactId) throws MojoExecutionException {
        getLog().info("Executing integration tests (*IT.class)...");

        // JUnit runner
        List<Dependency> failsafePluginDependencies = new ArrayList<Dependency>();
        failsafePluginDependencies.add(dependency("org.apache.maven.surefire", "surefire-junit47", "2.19.1"));

        // Add dependencies to classes under test
        List<Element> classpathElements = new ArrayList<>();
        List<String> childModules = project.getModules();
        if (childModules != null && childModules.size() > 0) {
            // Get sub-module names, so we can see what libs to include
            for (String module : childModules) {
                String classpathElement = project.getBasedir() + "/" + module + "/target/" + module + "-1.0-SNAPSHOT.jar";
                getLog().info("Adding module '" + classpathElement + "' to test classpath");
                classpathElements.add(element(name("additionalClasspathElement"), classpathElement));
            }
        }

        // Execute Failsafe Mojo
        Plugin failsafePlugin = plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-failsafe-plugin"),
                version("2.19.1"),
                failsafePluginDependencies
        );
        getLog().info("Start executing failsafe Mojo...");

        // This might be ugly, the MojoExecuter will only accept Element[] and we need this list to be dynamic
        // to avoid NPEs. If there's a better way to do this, then feel free to change it!
        Element[] classpathElementArray = new Element[classpathElements.size()];
        classpathElements.toArray(classpathElementArray);

        executeMojo(failsafePlugin,
                goal("integration-test"),
                configuration(
                        element(name("includes"),
                                element(name("include"), "**/*IT.class")
                        ),
                        element(name("additionalClasspathElements"),
                                classpathElementArray
                        ),
                        // IT Test dependency to scan
                        element(name("dependenciesToScan"),
                                element(name("dependency"), project.getGroupId() + ":" + testJarArtifactId)
                        )
                ),
                execEnv
        );

        getLog().info("Stopped executing failsafe Mojo");
    }

    protected void stopTomcat() throws MojoExecutionException {
        getLog().info("Stopping Tomcat...");

        Plugin tomcatPlugin = plugin(
                groupId("org.apache.tomcat.maven"),
                artifactId("tomcat7-maven-plugin"),
                version(MAVEN_TOMCAT7_PLUGIN_VERSION)
        );

        executeMojo(tomcatPlugin,
                goal("shutdown") ,
                configuration(),
                execEnv
        );
    }
}