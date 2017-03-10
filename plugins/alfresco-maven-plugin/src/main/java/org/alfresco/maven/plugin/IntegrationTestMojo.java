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

        getLog().info("Checking if Tomcat is already running on port " + "");
        if ( ! tomcatIsRunning() ) {

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
            }
        }

    }

}