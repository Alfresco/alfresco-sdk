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
@Mojo(name = "run",
        defaultPhase = LifecyclePhase.TEST,
        aggregator = true,
        requiresDependencyResolution = ResolutionScope.TEST)
public class RunMojo extends AbstractMojo {
    public static final String MAVEN_DEPENDENCY_PLUGIN_VERSION = "2.10";
    public static final String MAVEN_WAR_PLUGIN_VERSION = "2.6";
    public static final String MAVEN_INSTALL_PLUGIN_VERSION = "2.5.2";
    public static final String MAVEN_REPLACER_PLUGIN_VERSION = "1.5.3";
    public static final String MAVEN_RESOURCE_PLUGIN_VERSION = "2.7";
    public static final String MAVEN_TOMCAT7_PLUGIN_VERSION = "2.2";
    public static final String MAVEN_ALFRESCO_PLUGIN_VERSION = "3.0.0-SNAPSHOT";

    public static final String PLATFORM_WAR_PREFIX_NAME = "platform";
    public static final String SHARE_WAR_PREFIX_NAME = "share";

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
     *    {@code
     *      <plugin>
     *          <groupId>org.alfresco.maven.plugin</groupId>
     *          <artifactId>alfresco-maven-plugin</artifactId>
     *          <version>3.0.0</version>
     *          <configuration>
     *              <enableH2>true</enableH2>
     *              <enablePlatform>true</enablePlatform>
     *              <enableSolr>true</enableSolr>
     *              <enableShare>false</enableShare>*
     *              <platformModules>
     *                  <moduleDependency>
     *                      <groupId>${alfresco.groupId}</groupId>
     *                      <artifactId>alfresco-share-services</artifactId>
     *                      <version>${alfresco.share.version}</version>
     *                      <type>amp</type>
     *                  </moduleDependency>
     *              </platformModules>
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
     * Switch to enable/disable the Platform/Repository (alfresco.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enablePlatform", defaultValue = "true")
    protected boolean enablePlatform;

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
     * Control if Tomcat 7 Plugin should be kicked off and start Apache Tomcat
     */
    @Parameter(property = "maven.alfresco.startTomcat", defaultValue = "true")
    protected boolean startTomcat;

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
     * JARs and AMPs that should be overlayed/applied to the Platform/Repository WAR (i.e. alfresco.war)
     */
    @Parameter(property = "maven.alfresco.platform.modules", defaultValue = "")
    protected List<ModuleDependency> platformModules;

    /**
     * JARs and AMPs that should be overlayed/applied to the Share WAR (i.e. share.war)
     */
    @Parameter(property = "maven.alfresco.share.modules", defaultValue = "")
    protected List<ModuleDependency> shareModules;

    /**
     * Maven GAV properties for standard Alfresco web applications.
     */
    @Parameter(property = "alfresco.groupId", defaultValue = "org.alfresco")
    protected String alfrescoGroupId;

    @Parameter(property = "alfresco.platform.war.artifactId", defaultValue = "alfresco")
    protected String alfrescoPlatformWarArtifactId;

    @Parameter(property = "alfresco.share.war.artifactId", defaultValue = "share")
    protected String alfrescoShareWarArtifactId;

    @Parameter(property = "alfresco.solr.artifactId", defaultValue = "alfresco-solr4")
    protected String alfrescoSolrArtifactId;

    @Parameter(property = "alfresco.api.explorer.artifactId", defaultValue = "api-explorer")
    protected String alfrescoApiExplorerArtifactId;

    @Parameter(property = "alfresco.platform.version", defaultValue = "5.1.g")
    protected String alfrescoPlatformVersion;

    @Parameter(property = "alfresco.share.version", defaultValue = "5.1.f")
    protected String alfrescoShareVersion;

    @Parameter(property = "alfresco.api.explorer.version", defaultValue = "1.0")
    protected String alfrescoApiExplorerVersion;

    /**
     * Directory that contains the Alfresco Solr 4 configuration
     */
    @Parameter(property = "solr.home", defaultValue = "${project.basedir}/${alfresco.data.location}/solr")
    protected String solrHome;

    /**
     * Maven GAV properties for customized alfresco.war and share.war
     * Used by the Maven Tomcat 7 Plugin
     */
    private String runnerAlfrescoGroupId;
    private String runnerAlfrescoPlatformWarArtifactId;
    private String runnerAlfrescoShareWarArtifactId;
    private String runnerAlfrescoPlatformVersion;
    private String runnerAlfrescoShareVersion;

    /**
     * Database JDBC connection URL
     * TODO: Is this parameter needed here?
     */
//    @Parameter(property = "alfresco.db.url", defaultValue = "jdbc:h2:./${alfresco.data.location}/h2_data/${alfresco.db.name};${alfresco.db.params}")
    //  protected String alfrescoDbUrl;

    /**
     * The Maven environment that this mojo is executed in
     */
    private ExecutionEnvironment execEnv;

    public void execute() throws MojoExecutionException {
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

        if (enablePlatform) {
            buildPlatformWar();
        }

        if (enableShare) {
            buildShareWar();
        }

        if (startTomcat) {
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
                        version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                ),
                goal("unpack"),
                configuration(
                        element(name("outputDirectory"), solrHome),
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), alfrescoGroupId),
                                        element(name("artifactId"), alfrescoSolrArtifactId),
                                        element(name("version"), alfrescoPlatformVersion),
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
                        version(MAVEN_REPLACER_PLUGIN_VERSION)
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
     * Replaces web.xml where applicable in platform webapp (alfresco.war),
     * commenting out the security-constraints.
     * <p/>
     * This is only needed for 5.0 (5.1 handles it automatically when turning off ssl via props)
     *
     * @throws MojoExecutionException
     */
    protected void commentOutSecureCommsInPlatformWebXml() throws MojoExecutionException {
        if (isPlatformVersionGtOrEqTo51()) {
            return;
        }

        String webInfPath = getWarOutputDir(PLATFORM_WAR_PREFIX_NAME) + "/WEB-INF/";
        String webXmlFilePath = webInfPath + "web.xml";

        getLog().info("Commenting out the security-constraints in " + webXmlFilePath);

        executeMojo(
                plugin(
                        groupId("com.google.code.maven-replacer-plugin"),
                        artifactId("replacer"),
                        version(MAVEN_REPLACER_PLUGIN_VERSION)
                ),
                goal("replace"),
                configuration(
                        element(name("ignoreErrors"), "true"),
                        element(name("file"), webXmlFilePath),
                        element(name("outputDir"), webInfPath),
                        element(name("preserveDir"), "false"),
                        element(name("replacements"),
                                element(name("replacement"),
                                        element(name("token"), "<!-- Toggle securecomms placeholder start -->"),
                                        element(name("value"), "<!-- ")
                                ),
                                element(name("replacement"),
                                        element(name("token"), "<!-- Toggle securecomms placeholder end -->"),
                                        element(name("value"), " -->")
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
                        version(MAVEN_RESOURCE_PLUGIN_VERSION)
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
     * Build the customized Platform webapp (i.e. the Repository, alfresco.war)
     * that should be deployed by Tomcat by applying all AMPs and JARs from
     * the {@code <platformModules> } configuration.
     */
    protected void buildPlatformWar() throws MojoExecutionException {
        buildCustomWar(PLATFORM_WAR_PREFIX_NAME, platformModules,
                alfrescoPlatformWarArtifactId, alfrescoPlatformVersion);

        commentOutSecureCommsInPlatformWebXml();

        String platformWarArtifactId = packageAndInstallCustomWar(PLATFORM_WAR_PREFIX_NAME);

        // Set up the custom platform war to be run by Tomcat plugin
        runnerAlfrescoGroupId = "${project.groupId}";
        runnerAlfrescoPlatformWarArtifactId = platformWarArtifactId;
        runnerAlfrescoPlatformVersion = "${project.version}";
    }

    /**
     * Build the customized Share webapp (i.e. the share.war)
     * that should be deployed by Tomcat by applying all AMPs and JARs from
     * the {@code <shareModules> } configuration.
     */
    protected void buildShareWar() throws MojoExecutionException {
        buildCustomWar(SHARE_WAR_PREFIX_NAME, shareModules, alfrescoShareWarArtifactId, alfrescoShareVersion);

        String shareWarArtifactId = packageAndInstallCustomWar(SHARE_WAR_PREFIX_NAME);

        // Set up the custom share war to be run by Tomcat plugin
        runnerAlfrescoGroupId = "${project.groupId}";
        runnerAlfrescoShareWarArtifactId = shareWarArtifactId;
        runnerAlfrescoShareVersion = "${project.version}";
    }

    /**
     * Build a customized webapp, applying a number of AMPs and JARs from alfresco maven plugin configuration.
     *
     * @param warName               the name of the custom war
     * @param modules               the modules that should be applied to the custom war
     * @param originalWarArtifactId the artifactId for the original war file that should be customized
     * @param originalWarVersion    the version for the original war file that should be customized
     * @throws MojoExecutionException
     */
    protected void buildCustomWar(String warName,
                                  List<ModuleDependency> modules,
                                  String originalWarArtifactId,
                                  String originalWarVersion) throws MojoExecutionException {
        final String warOutputDir = getWarOutputDir(warName);
        final String ampsOutputDir = "${project.build.directory}/modules/" + warName + "/amps";
        List<Element> ampModules = new ArrayList<>();
        List<Element> jarModules = new ArrayList<>();

        if (modules != null) {
            for (ModuleDependency moduleDep : modules) {
                Element el = element(name("artifactItem"),
                        element(name("groupId"), moduleDep.getGroupId()),
                        element(name("artifactId"), moduleDep.getArtifactId()),
                        element(name("version"), moduleDep.getVersion()),
                        element(name("type"), moduleDep.getType()),
                        element(name("overWrite"), "true"));

                if (moduleDep.getArtifactId().equalsIgnoreCase("alfresco-share-services")) {
                    // Skip if we are not running a 5.1 version of Alfresco, 'Alfresco Share Services'
                    // was not used in earlier versions
                    if (!isPlatformVersionGtOrEqTo51()) {
                        continue;
                    }
                }

                if (moduleDep.isAmp()) {
                    ampModules.add(el);
                } else if (moduleDep.isJar()) {
                    jarModules.add(el);
                } else {
                    throw new MojoExecutionException(
                            "Unknown module type: " + moduleDep.getType() +
                                    " when building custom " + warName +
                                    " war, only 'jar' and 'amp' types are allowed");
                }
            }
        }

        // Convert from list to array so we can add these elements below
        Element[] ampModuleArray = new Element[ampModules.size()];
        ampModules.toArray(ampModuleArray);
        Element[] jarModuleArray = new Element[jarModules.size()];
        jarModules.toArray(jarModuleArray);

        // Unpack the original war to /target/<warName>-war
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                ),
                goal("unpack"),
                configuration(
                        element(name("outputDirectory"), warOutputDir),
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), alfrescoGroupId),
                                        element(name("artifactId"), originalWarArtifactId),
                                        element(name("version"), originalWarVersion),
                                        element(name("type"), "war")
                                )
                        )
                ),
                execEnv
        );

        if (ampModuleArray.length > 0) {
            // Copy AMPs to target/modules/<warName>/amps so we can install them onto the WAR
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                    ),
                    goal("copy"),
                    configuration(
                            element(name("outputDirectory"), ampsOutputDir),
                            element(name("artifactItems"), ampModuleArray)
                    ),
                    execEnv
            );

            // Then apply all these amps to the unpacked war
            executeMojo(
                    plugin(
                            groupId("org.alfresco.maven.plugin"),
                            artifactId("alfresco-maven-plugin"),
                            version(MAVEN_ALFRESCO_PLUGIN_VERSION)
                    ),
                    goal("install"),
                    configuration(
                            element(name("ampLocation"), ampsOutputDir),
                            element(name("warLocation"), warOutputDir)
                    ),
                    execEnv
            );
        }

        // Then copy all JAR dependencies to the unpacked war /target/<warName>-war/WEB-INF/lib
        if (jarModuleArray.length > 0) {
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                    ),
                    goal("copy"),
                    configuration(
                            element(name("outputDirectory"), warOutputDir + "/WEB-INF/lib"),
                            element(name("artifactItems"), jarModuleArray)
                    ),
                    execEnv
            );
        }
    }

    /**
     * Package customized war file and install it in local maven repo.
     *
     * @param warName the name of the custom war
     * @return the customized war file artifactId, to be used by the tomcat7 plugin
     * @throws MojoExecutionException
     */
    protected String packageAndInstallCustomWar(String warName) throws MojoExecutionException {
        final String warArtifactId = "${project.artifactId}-" + warName;
        final String warOutputDir = getWarOutputDir(warName);

        // Package the customized war file
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-war-plugin"),
                        version(MAVEN_WAR_PLUGIN_VERSION)
                ),
                goal("war"),
                configuration(
                        element(name("warName"), warName),
                        element(name("warSourceDirectory"), warOutputDir),
                        // Specifically tell the archiver where the manifest file is,
                        // so a new manifest is not generated.
                        // We are picking the manifest from the original war.
                        // If we don't do this, then customized share.war will not start properly.
                        element(name("archive"),
                                element(name("manifestFile"), warOutputDir + "/META-INF/MANIFEST.MF")
                        )
                )
                , execEnv
        );

        // Install the customized war file in local maven  repo
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-install-plugin"),
                        version(MAVEN_INSTALL_PLUGIN_VERSION)
                ),
                goal("install-file"),
                configuration(
                        element(name("file"), "${project.build.directory}/" + warName + ".war"),
                        element(name("groupId"), "${project.groupId}"),
                        element(name("artifactId"), warArtifactId),
                        element(name("version"), "${project.version}")
                )
                , execEnv
        );

        return warArtifactId;
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
            Dependency h2ScriptsDependency = dependency(alfrescoGroupId, "alfresco-repository", alfrescoPlatformVersion);
            h2ScriptsDependency.setClassifier("h2scripts");

            tomcatDependencies.add(
                    // Bring in the flat file H2 database
                    dependency("com.h2database", "h2", "1.4.190"));
            tomcatDependencies.add(
                    // Bring in the H2 Database scripts for the Alfresco version we use
                    h2ScriptsDependency);
        }

        if (enablePlatform) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoPlatformWarArtifactId, runnerAlfrescoPlatformVersion,
                    "/alfresco", null));
        }

        if (enableShare) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoShareWarArtifactId, runnerAlfrescoShareVersion,
                    "/share", null));
        }

        if (enableSolr) {
            webapps2Deploy.add(createWebAppElement(alfrescoGroupId, alfrescoSolrArtifactId, alfrescoPlatformVersion,
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
                        version(MAVEN_TOMCAT7_PLUGIN_VERSION),
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

        Element groupIdEl = element(name("groupId"), groupId);
        Element artifactIdEl = element(name("artifactId"), artifactId);
        Element versionEl = element(name("version"), version);
        Element typeEl = element(name("type"), "war");
        // Make sure webapp is loaded with context and everything,
        // if set to 'false' then you will get 404 when trying to access the webapp from browser
        Element asWebappEl = element(name("asWebapp"), "true");
        Element contextPathEl = element(name("contextPath"), contextPath);

        Element e;
        if (StringUtils.isNotBlank(contextFile)) {
            e = element(name("webapp"),
                    groupIdEl, artifactIdEl, versionEl, typeEl, asWebappEl, contextPathEl,
                    element(name("contextFile"), contextFile));

        } else {
            e = element(name("webapp"),
                    groupIdEl, artifactIdEl, versionEl, typeEl, asWebappEl, contextPathEl);
        }

        getLog().info(e.toDom().toUnescapedString());

        return e;
    }

    /**
     * Returns true if current platform version (i.e. version of alfresco.war) is
     * >= 5.1
     *
     * @return true if platform version >= 5.1
     */
    private boolean isPlatformVersionGtOrEqTo51() {
        int versionNumber = Integer.parseInt(
                alfrescoPlatformVersion.replaceAll("[^0-9]", "").substring(0, 2));
        if (versionNumber >= 51) {
            return true;
        }

        return false;
    }

    /**
     * The directory where the custom war will be assembled
     *
     * @param warName a war prefix, such as 'platform' or 'share'
     * @return a directory such as: .../aio/target/platform-war
     */
    private String getWarOutputDir(String warName) {
        return "${project.build.directory}/" + warName + "-war";
    }
}
