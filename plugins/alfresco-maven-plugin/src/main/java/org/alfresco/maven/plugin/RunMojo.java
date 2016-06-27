/**
 * Copyright (C) 2016 Alfresco Software Limited.
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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.util.*;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Runner component that can be used to test your extension.
 *
 * @author ole.hejlskov, martin.bergljung
 * @version 1.0
 * @since 3.0.0
 */
@Mojo(  name = "run",
        defaultPhase = LifecyclePhase.TEST,
        aggregator = true,
        requiresDependencyResolution = ResolutionScope.TEST)
public class RunMojo extends AbstractMojo {
    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * The following properties that start with 'maven.' are used to control the
     * Alfresco Maven plugin itself.
     * <p/>
     * For example:
     * <pre>
     *    {@code
     *      <plugin>
     *          <groupId>org.alfresco.maven.plugin</groupId>
     *          <artifactId>alfresco-maven-plugin</artifactId>
     *          <version>3.0.0</version>
     *          <configuration>
     *              <enableRepository>true</enableRepository>
     *              <enableShare>false</enableShare>
     *              <enableSolr>true</enableSolr>
     *              <enableH2>true</enableH2>
     *          </configuration>
     *      </plugin>
     *    }
     */

    /**
     * Switch to enable/disable the Apache Solr 4 web application when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableSolr", defaultValue = "true")
    protected boolean enableSolr;

    /**
     * Switch to enable/disable the H2 database when running embedded Tomcat.
     * This also brings in the needed H2 database scripts.
     */
    @Parameter(property = "maven.alfresco.enableH2", defaultValue = "true")
    protected boolean enableH2;

    /**
     * Switch to enable/disable the Repository (alfresco.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableRepository", defaultValue = "true")
    protected boolean enableRepository;

    /**
     * Switch to enable/disable the Share (share.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableShare", defaultValue = "true")
    protected boolean enableShare;

    /**
     * Switch to enable/disable the Alfresco REST API Explorer (api-explorer.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableApiExplorer", defaultValue = "false")
    protected boolean enableApiExplorer;

    /**
     * Switch to enable/disable test properties when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableTestProperties", defaultValue = "true")
    protected boolean enableTestProperties;

    /**
     * Switch to enable/disable running embedded Apache Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableTomcat", defaultValue = "true")
    protected boolean enableTomcat;

    /**
     * Directory containing test files that should be used when running embedded Tomcat
     */
    @Parameter(property = "maven.alfresco.testFolder", defaultValue = "src/test/properties/${env}")
    protected String testFolder;

    /**
     * Test files in testFolder that should be included when running embedded Tomcat
     */
    @Parameter(property = "maven.alfresco.testInclude", defaultValue = "**")
    protected String testInclude;

    /**
     * Maven GAV properties for standard Alfresco web applications.
     */
    @Parameter(property = "alfresco.groupId", defaultValue = "org.alfresco")
    protected String alfrescoGroupId;

    @Parameter(property = "alfresco.platform.war.artifactId", defaultValue = "alfresco")
    protected String alfrescoRepoWarArtifactId;

    @Parameter(property = "alfresco.share.war.artifactId", defaultValue = "share")
    protected String alfrescoShareWarArtifactId;

    @Parameter(property = "alfresco.solr.artifactId", defaultValue = "alfresco-solr4")
    protected String alfrescoSolrArtifactId;

    @Parameter(property = "alfresco.api.explorer.artifactId", defaultValue = "api-explorer")
    protected String alfrescoApiExplorerArtifactId;

    @Parameter(property = "alfresco.platform.version", defaultValue = "5.1.g")
    protected String alfrescoRepoVersion;

    @Parameter(property = "alfresco.share.version", defaultValue = "5.1.f")
    protected String alfrescoShareVersion;

    @Parameter(property = "alfresco.api.explorer.version", defaultValue = "1.0")
    protected String alfrescoApiExplorerVersion;

    /**
     * Maven GAV properties for customized alfresco.war and share.war
     * Used by the Maven Tomcat 7 Plugin
     */
    @Parameter(property = "runner.alfresco.groupId", defaultValue = "${alfresco.groupId}")
    protected String runnerAlfrescoGroupId;

    @Parameter(property = "runner.alfresco.platform.war.artifactId", defaultValue = "${alfresco.platform.war.artifactId}")
    protected String runnerAlfrescoRepoWarArtifactId;

    @Parameter(property = "runner.alfresco.share.war.artifactId", defaultValue = "${alfresco.share.war.artifactId}")
    protected String runnerAlfrescoShareWarArtifactId;

    @Parameter(property = "runner.alfresco.platform.version", defaultValue = "${alfresco.platform.version}")
    protected String runnerAlfrescoRepoVersion;

    @Parameter(property = "runner.alfresco.share.version", defaultValue = "${alfresco.share.version}")
    protected String runnerAlfrescoShareVersion;

    /**
     * Directory that contains the Alfresco Solr 4 configuration
     */
    @Parameter(property = "solr.home", defaultValue = "${project.basedir}/${alfresco.data.location}/solr")
    protected String solrHome;

    /**
     * Database JDBC connection URL
     * TODO: Is this parameter needed here?
     */
    @Parameter(property = "alfresco.db.url", defaultValue = "jdbc:h2:./${alfresco.data.location}/h2_data/${alfresco.db.name};${alfresco.db.params}")
    protected String alfrescoDbUrl;

    private ExecutionEnvironment execEnv;

    /**
     * Have to have this setter as it does not seem to work to assign a default value to a property
     * where the value comes from a property that is not used (i.e. alfrescoRepoWarArtifactId)
     *
     * @param artifactId

    public void setRunnerAlfrescoRepoArtifactId(String artifactId) {
        getLog().info("setRunnerAlfrescoRepoArtifactId [artifactId=" + artifactId +
                "][alfrescoRepoArtifactId=" + this.alfrescoRepoArtifactId + "]");
        if (StringUtils.isBlank(artifactId)) {
            if (StringUtils.isNotBlank(this.alfrescoRepoArtifactId)) {
                this.runnerAlfrescoRepoArtifactId = this.alfrescoRepoArtifactId;
            } else {
                getLog().error("Alfresco Repository WAR Maven Artifact ID is not set via either " +
                        "runner.alfresco.platform.war.artifactId or alfresco.platform.war.artifactId");
            }
        } else {
            this.runnerAlfrescoRepoArtifactId = artifactId;
        }
    }
     */

    /**
     * Have to have this setter as it does not seem to work to assign a default value to a property
     * where the value comes from a property that is not used (i.e. alfrescoShareWarArtifactId)
     *
     * @param artifactId

    public void setRunnerAlfrescoShareArtifactId(String artifactId) {
        getLog().info("setRunnerAlfrescoShareArtifactId [artifactId=" + artifactId +
                "][alfrescoShareWarArtifactId=" + this.alfrescoShareWarArtifactId + "]");
        if (StringUtils.isBlank(artifactId)) {
            if (StringUtils.isNotBlank(this.alfrescoShareWarArtifactId)) {
                this.runnerAlfrescoShareWarArtifactId = this.alfrescoShareWarArtifactId;
            } else {
                getLog().error("Alfresco Share WAR Maven Artifact ID is not set via either " +
                        "runner.alfresco.share.artifactId or alfresco.share.artifactId");
            }
        } else {
            this.runnerAlfrescoShareWarArtifactId = artifactId;
        }
    }
     */

    public void execute() throws MojoExecutionException {
        // The runner repo artifact ID might not be set, initialize in that case
/*        if (StringUtils.isBlank(this.runnerAlfrescoRepoWarArtifactId)) {
            setRunneralfrescoRepoWarArtifactId(null);
        }

        // The runner share artifact ID might not be set, initialize in that case
        if (StringUtils.isBlank(this.runnerAlfrescoShareWarArtifactId)) {
            setRunnerAlfrescoShareArtifactId(null);
        }
*/
        execEnv = executionEnvironment(
                project,
                session,
                pluginManager
        );

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

    /**
     * Download and unpack the Solr 4 configuration as we don't have it in the project.
     * It will reside under /alf_data_dev/solr
     *
     * @throws MojoExecutionException
     */
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
                                        element(name("artifactId"), alfrescoSolrArtifactId),
                                        element(name("version"), alfrescoRepoVersion),
                                        element(name("classifier"), "config"),
                                        element(name("type"), "zip")
                                )
                        )
                ),
                execEnv
        );

    }

    /**
     * Replace property placeholders in configuration files for the cores, so the
     * index files can be found for each core when Solr starts up.
     *
     * @throws MojoExecutionException
     */
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
                                element(name("include"), solrHome + "/archive-SpacesStore/conf/solrcore.properties"),
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

    /**
     * Copy the alfresco-global.properties file that will be used when
     * running Alfresco. It contains database connection parameters and
     * other general configuration for Alfresco Repository (alfresco.war)
     *
     * @throws MojoExecutionException
     */
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

    /**
     * Start up the embedded Tomcat server with the webapps that has been
     * configured in the SDK project.
     *
     * @throws MojoExecutionException
     */
    protected void startTomcat() throws MojoExecutionException {
        getLog().info("Starting Tomcat");

        List<Dependency> tomcatDependencies = new ArrayList<Dependency>();
        ArrayList webapps2Deploy = new ArrayList<Element>();

        // Add the basic Tomcat dependencies
        tomcatDependencies.add(
                // Packaging goes faster with this lib
                dependency("org.codehaus.plexus", "plexus-archiver", "2.3"));
        tomcatDependencies.add(
                // The following dependency is needed, otherwise you get
                //  Caused by: java.lang.NoSuchMethodError:
                //      javax.servlet.ServletContext.getSessionCookieConfig()Ljavax/servlet/SessionCookieConfig
                // This method is in Servlet API 3.0
                dependency("javax.servlet", "javax.servlet-api", "3.0.1"));

        if (enableH2) {
            Dependency h2ScriptsDependency = dependency(alfrescoGroupId, "alfresco-repository", alfrescoRepoVersion);
            h2ScriptsDependency.setClassifier("h2scripts");

            tomcatDependencies.add(
                    // Bring in the flat file H2 database
                    dependency("com.h2database", "h2", "1.4.190"));
            tomcatDependencies.add(
                    // Bring in the H2 Database scripts for the Alfresco version we use
                    h2ScriptsDependency);
        }

        if (enableRepository) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoRepoWarArtifactId, runnerAlfrescoRepoVersion,
                    "/alfresco", "${project.build.testOutputDirectory}/tomcat/context-repo.xml"));
        }

        if (enableShare) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoShareWarArtifactId, runnerAlfrescoShareVersion,
                    "/share", "${project.build.testOutputDirectory}/tomcat/context-share.xml"));
        }

        if (enableSolr) {
            webapps2Deploy.add(createWebAppElement(alfrescoGroupId, alfrescoSolrArtifactId, alfrescoRepoVersion,
                    "/solr4", "${project.build.testOutputDirectory}/tomcat/context-solr.xml"));
        }

        if (enableApiExplorer) {
            webapps2Deploy.add(createWebAppElement(alfrescoGroupId, alfrescoApiExplorerArtifactId, alfrescoApiExplorerVersion,
                    "/api-explorer", null));
        }

        // This might be ugly, the MojoExecuter will only accept Element[] and we need this list to be dynamic
        // to avoid NPEs. If there's a better way to do this, then feel free to change it!
        Element[] webapps = new Element[webapps2Deploy.size()];
        webapps2Deploy.toArray(webapps);

        executeMojo(
                plugin(
                        groupId("org.apache.tomcat.maven"),
                        artifactId("tomcat7-maven-plugin"),
                        version("2.2"),
                        tomcatDependencies
                ),
                goal("run"),
                configuration(
                        /*
                         * SDK Projects doesn't have packaging set to 'war', they are JARs or POMs, this setting ignores that fact.
                         */
                        element(name("ignorePackaging"), "true"),

                        /*
                         * Make sure Catalina classes are picked up when we got virtual webapp contexts with classpaths.
                         *
                         * If true a new classLoader separated from maven core will be created to start.
                         * This does not work to run with, getting :
                         *      NoSuchMethodError: javax.servlet.ServletContext.getSessionCookieConfig
                         *      (which lives in Servlet API 3)
                         *
                         */
                        //element(name("useSeparateTomcatClassLoader"), "true"),

                        /*
                         * Bring in stuff in the test classpath, such as the alfresco-global.properties that should be used
                         */
                        element(name("useTestClasspath"), "true"),

                        /**
                         * Set up where Solr Home directory is
                         */
                        element(name("systemProperties"),
                                element(name("java.io.tmpdir"), "${project.build.directory}"),
                                element(name("solr.solr.home"), solrHome)
                        ),

                        /*  Should this class loader delegate to the parent class loader before searching its
                            own repositories (i.e. the usual Java2 delegation model).
                            Prevent parent classloader delegation, each webapp loads.
                            If set to true then you will get a truckload of Solr logging as
                            the alf_data_dev/solr4/config/log4j-solr.properties file is not picked up.
                            This also fixes issues with the Google Guava Library, which this tomcat plugin uses
                            version 10.0.1 of but Solr uses 14.0.1 */
                        element(name("delegate"), "false"),

                        /*
                         * Encode url in UTF-8 for proper character handling -->
                         */
                        element(name("uriEncoding"), "UTF-8"),

                        /*
                         * Bring in the webapps that should be deployed and run
                         */
                        element(name("webapps"), webapps)

                ),
                execEnv
        );
    }

    /**
     * Create a webapp element that can be added as a webapp configuration to the Tomcat plug-in.
     * <p/>
     * For example:
     * <pre>
     *    {@code
     *    <webapp>
     *        <groupId>${project.groupId}</groupId>
     *        <artifactId>share</artifactId>
     *        <version>${project.version}</version>
     *        <type>war</type>
     *        <asWebapp>true</asWebapp>
     *        <contextPath>/share</contextPath>
     *        <contextFile>${project.build.directory}/contexts/context-share.xml</contextFile>
     *    </webapp>
     *    }
     * </pre>
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @param contextPath
     * @param contextFile
     * @return
     */
    private Element createWebAppElement(String groupId,
                                        String artifactId,
                                        String version,
                                        String contextPath,
                                        String contextFile) {
        String errorStr = "cannot be null when creating webapp element for Tomcat 7 plugin";
        if (StringUtils.isBlank(groupId)) {
            getLog().error("Maven Group Id " + errorStr);
        }
        if (StringUtils.isBlank(artifactId)) {
            getLog().error("Maven Artifact Id " + errorStr);
        }
        if (StringUtils.isBlank(version)) {
            getLog().error("Maven Version number " + errorStr);
        }

        Element e = element(name("webapp"),
                element(name("groupId"), groupId),
                element(name("artifactId"), artifactId),
                element(name("version"), version),
                element(name("type"), "war"),

                // Make sure webapp is loaded with context and everything,
                // if set to 'false' then you will get 404 when trying to access the webapp from browser
                element(name("asWebapp"), "true"),

                element(name("contextPath"), contextPath),
                StringUtils.isNotBlank(contextFile) ? element(name("contextFile"), contextFile) : null);

        getLog().info(e.toDom().toUnescapedString());

        return e;
    }
}
