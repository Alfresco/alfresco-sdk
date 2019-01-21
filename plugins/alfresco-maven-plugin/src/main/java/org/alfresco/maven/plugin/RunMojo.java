/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Alfresco Plugin mojo that are used when you want to run the Alfresco server with all the webapps.
 * The server blocks after startup.
 *
 * @author martin.bergljung
 * @version 1.0
 * @since 3.0.0
 */
@Mojo(name = "run",
        defaultPhase = LifecyclePhase.TEST,
        aggregator = true, // Only run against the top-level project in a Maven build
        requiresDependencyResolution = ResolutionScope.TEST)
public class RunMojo extends AbstractRunMojo {

    public void execute() throws MojoExecutionException {
        execEnv = executionEnvironment(
                project,
                session,
                pluginManager
        );

        if (enableActivitiApp || enableActivitiAdmin) {
            getLog().warn("*************************************************************************************************************");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*\tWARNING" );
            getLog().warn("*\tThe Activiti features in SDK 3.x are UNSUPPORTED.");
            getLog().warn("*\tIt has been marked for deprecation (SDK 3.1) and will be removed in SDK 4.0.");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*");
            getLog().warn("*************************************************************************************************************");

        }

        if (enableSolr) {
            unpackSolrConfig();
            fixSolrHomePath();
            copySolrCustomConfig();
            replaceSolrConfigProperties();
            installSolr10InLocalRepo();
        }

        if (enableTestProperties && enablePlatform) {
            copyAlfrescoGlobalProperties();
            renameAlfrescoGlobalProperties();
        }

        if (enablePlatform) {
            buildPlatformWar();
        }

        if (enableShare) {
            buildShareWar();
        }

        if (enableActivitiApp) {
            buildActivitiAppWar();
        }

        if (startTomcat) {
            checkDatabaseConfig();
            boolean fork = false;
            startTomcat(fork);
        }
    }
}
