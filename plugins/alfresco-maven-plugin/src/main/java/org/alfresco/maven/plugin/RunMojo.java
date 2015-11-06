/**
 * Copyright (C) 2015 Alfresco Software Limited.
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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.*;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;




@Mojo( name = "run", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class RunMojo extends AbstractMojo
{
    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    @Parameter(property = "maven.alfresco.enableSolr", defaultValue = "true")
    protected boolean enableSolr;

    @Parameter(property = "maven.alfresco.solrHome", defaultValue = "${project.basedir}/${alfresco.data.location}/solr")
    protected String solrHome;

    @Parameter(property = "alfresco.version", defaultValue = "5.1-a-EA")
    protected String alfrescoVersion;

    @Parameter(property = "alfresco.groupId", defaultValue = "org.alfresco")
    protected String alfrescoGroupId;

    @Parameter(property = "alfresco.db.url", defaultValue = "jdbc:h2:./${alfresco.data.location}/h2_data/${alfresco.db.name};${alfresco.db.params}")
    protected String alfrescoDbUrl;


    @Parameter(property = "maven.alfresco.enableH2", defaultValue = "true")
    protected boolean enableH2;


    @Parameter(property = "maven.alfresco.enableRepository", defaultValue = "true")
    protected boolean enableRepository;

    @Parameter(property = "maven.alfresco.enableShare", defaultValue = "true")
    protected boolean enableShare;

    @Parameter(property = "maven.alfresco.enableTestProperties", defaultValue = "true")
    protected boolean enableTestProperties;

    @Parameter(property = "maven.alfresco.enableTomcat", defaultValue = "true")
    protected boolean enableTomcat;


    @Parameter(property = "maven.alfresco.testFolder", defaultValue = "src/test/properties/${env}")
    protected String testFolder;


    @Parameter(property = "maven.alfresco.testInclude", defaultValue = "**")
    protected String testInclude;

    @Parameter(property = "maven.alfresco.pgsqlDialectFolder", defaultValue = "${project.build.directory}/pg-dialect-tmp")
    protected String pgsqlDialectFolder;




    private ExecutionEnvironment execEnv;

    public void execute() throws MojoExecutionException {



        execEnv = executionEnvironment(
                project,
                session,
                pluginManager
        );


        if (enableH2) {
            unpackH2Config();
            copyH2Config();
        }

        if (enableSolr) {
            unpackSolrConfig();
            replaceSolrConfigProperties();

        }

        if (enableTestProperties) {
            copyAlfrescoGlobalProperties();
        }

        if (enableTomcat) {
            startTomcat();
        }

    }

    protected void unpackSolrConfig() throws MojoExecutionException {
        getLog().info("Unpacking Solr config");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version("2.9")
                ),
                goal("unpack"),
                configuration(
                        element(name("outputDirectory"), solrHome),
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), alfrescoGroupId),
                                        element(name("artifactId"), "alfresco-solr4"),
                                        element(name("version"), alfrescoVersion),
                                        element(name("classifier"), "config"),
                                        element(name("type"), "zip")
                                )
                        )
                ),
                execEnv
        );

    }

    protected void replaceSolrConfigProperties() throws MojoExecutionException {
        getLog().info("Replacing Solr config properties");
        executeMojo(
                plugin(
                        groupId("com.google.code.maven-replacer-plugin"),
                        artifactId("replacer"),
                        version("1.5.3")
                ),
                goal("replace"),
                configuration(
                        element(name("regex"), "false"),
                        element(name("includes"),
                                element(name("include"), solrHome + "/archive-SpacesStore/conf/solrcore.properties" ),
                                element(name("include"), solrHome + "/workspace-SpacesStore/conf/solrcore.properties")
                        ),
                        element(name("replacements"),
                                element(name("replacement"),
                                        element(name("token"), "@@ALFRESCO_SOLR4_DATA_DIR@@"),
                                        element(name("value"), solrHome + "/index")
                                )
                        )
                ),
                execEnv
        );

    }

    // @TODO We don't need this anymore! As per https://issues.alfresco.com/jira/browse/ACE-4563 we will get an
    // alfresco-repository artifact with h2scripts classifier which we simply slap in the Tomcat classpath
    // and we're good!
    protected void unpackH2Config() throws MojoExecutionException {
        // @TODO Check if H2 is enabled, if not no need to do this step

        getLog().info("Unpacking H2 config");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version("2.9")
                ),
                goal("unpack"),
                configuration(
                        element(name("outputDirectory"), pgsqlDialectFolder),
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), alfrescoGroupId),
                                        element(name("artifactId"), "alfresco-repository"),
                                        element(name("version"), alfrescoVersion),
                                        element(name("includes"), "alfresco/dbscripts/create/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/dbscripts/upgrade/*/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/ibatis/org.hibernate.dialect.PostgreSQLDialect/*")
                                )
                        )
                ),
                execEnv
        );

    }

    protected void copyH2Config() throws MojoExecutionException {
        // @TODO Check if H2 is enabled, if not no need to do this step
        getLog().info("Extracting H2 Dialect");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin"),
                        version("2.7")
                ),
                goal("copy-resources"),
                configuration(
                        element(name("outputDirectory"), "${project.build.testOutputDirectory}"),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), pgsqlDialectFolder + "/alfresco/dbscripts/create/org.hibernate.dialect.PostgreSQLDialect"),
                                        element(name("includes"),
                                                element(name("include"), "*")
                                        ),
                                        element(name("targetPath"), "alfresco/dbscripts/create/org.hibernate.dialect.H2Dialect")
                                ),
                                element(name("resource"),
                                        element(name("directory"), pgsqlDialectFolder + "/alfresco/ibatis/org.hibernate.dialect.PostgreSQLDialect"),
                                        element(name("includes"),
                                                element(name("include"), "*")
                                        ),
                                        element(name("targetPath"), "alfresco/ibatis/org.hibernate.dialect.H2Dialect")
                                )
                        )
                ),
                execEnv
        );

    }

    protected void copyAlfrescoGlobalProperties() throws MojoExecutionException {
        getLog().info("Copying alfresco-global.properties to test resources");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin"),
                        version("2.7")
                ),
                goal("copy-resources"),
                configuration(
                        element(name("outputDirectory"), "${project.build.testOutputDirectory}"),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), testFolder),
                                        element(name("includes"),
                                                element(name("include"), testInclude)
                                        ),
                                        element(name("filtering"), "true")
                                )
                        )
                ),
                execEnv
        );
    }

    protected void startTomcat() throws MojoExecutionException {
        getLog().info("Starting Tomcat");

        List<Dependency> tomcatDependencies = null;
        Element solrElement = null;
        Element repoElement = null;
        Element shareElement = null;
        ArrayList tmpWebapps = new ArrayList<Element>();



        if (enableH2) {
            tomcatDependencies = dependencies(
                    dependency("com.h2database", "h2", "1.4.190"),
                    dependency("org.codehaus.plexus", "plexus-archiver", "2.3")
            );
        }

        if (enableSolr) {
            solrElement = element(name("webapp"),
                    element(name("groupId"), alfrescoGroupId),
                    element(name("artifactId"), "alfresco-solr4"),
                    element(name("version"), alfrescoVersion),
                    element(name("type"), "war"),
                    element(name("asWebapp"), "true"),
                    element(name("contextPath"), "/solr4"),
                    element(name("contextFile"), "${project.build.directory}/context-solr.xml")
            );
            tmpWebapps.add(solrElement);
        }

        if (enableRepository) {
            repoElement = element(name("webapp"),
                    element(name("groupId"), alfrescoGroupId),
                    element(name("artifactId"), "${alfresco.repo.artifactId}"),
                    element(name("version"), alfrescoVersion),
                    element(name("type"), "war"),
                    element(name("asWebapp"), "true"),
                    element(name("contextFile"), "${project.build.directory}/context-repo.xml")
            );
            tmpWebapps.add( repoElement );
        }


        if (enableShare) {
            shareElement = element(name("webapp"),
                    element(name("groupId"), alfrescoGroupId),
                    element(name("artifactId"), "${alfresco.share.artifactId}"),
                    element(name("version"), alfrescoVersion),
                    element(name("type"), "war"),
                    element(name("asWebapp"), "true"),
                    element(name("contextFile"), "${project.build.directory}/context-share.xml")
            );
            tmpWebapps.add( shareElement );


        }

        // This might be ugly, the MojoExecuter will only accept Element[] and we need this list to be dynamic
        // to avoid NPEs. If there's a prettier way to do the above please feel free to change it!
        Element[] webapps = new Element[tmpWebapps.size()];
        tmpWebapps.toArray(webapps);

        executeMojo(
                plugin(
                        groupId("org.apache.tomcat.maven"),
                        artifactId("tomcat7-maven-plugin"),
                        version("2.2"),
                        tomcatDependencies
                ),
                goal("run"),
                configuration(
                        element(name("ignorePackaging"), "true"),
                        element(name("useSeparateTomcatClassLoader"), "true"),
                        element(name("useTestClasspath"), "true"),
                        element(name("systemProperties"),
                                element(name("java.io.tmpdir"), "${project.build.directory}")
                        ),
                        element(name("delegate"), "false"),

                        element(name("webapps"), webapps)

                ),
                execEnv
        );
    }



}
