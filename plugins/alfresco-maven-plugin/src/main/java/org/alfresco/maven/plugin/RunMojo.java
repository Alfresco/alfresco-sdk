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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
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
        aggregator = true, // Only run against the top-level project in a Maven build
        requiresDependencyResolution = ResolutionScope.TEST)
public class RunMojo extends AbstractMojo {
    public static final String MAVEN_DEPENDENCY_PLUGIN_VERSION = "2.10";
    public static final String MAVEN_INSTALL_PLUGIN_VERSION = "2.5.2";
    public static final String MAVEN_REPLACER_PLUGIN_VERSION = "1.5.3";
    public static final String MAVEN_RESOURCE_PLUGIN_VERSION = "2.7";
    public static final String MAVEN_TOMCAT7_PLUGIN_VERSION = "2.2";
    public static final String MAVEN_BUILD_HELPER_PLUGIN_VERSION = "1.12";

    public static final String PLATFORM_WAR_PREFIX_NAME = "platform";
    public static final String SHARE_WAR_PREFIX_NAME = "share";

    public static final String ALFRESCO_ENTERPRISE_EDITION = "enterprise";
    public static final String ALFRESCO_COMMUNITY_EDITION = "community";

    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * The following properties that start with 'maven.' are used to control the
     * Alfresco Maven plugin itself.
     * <pre>
     * For example:
     *    {@code
     *      <plugin>
     *          <groupId>org.alfresco.maven.plugin</groupId>
     *          <artifactId>alfresco-maven-plugin</artifactId>
     *          <version>3.0.0</version>
     *          <configuration>
     *              <alfrescoEdition>community</alfrescoEdition>
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
     * </pre>
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
     * Community Edition or Enterprise Edition? (i.e community or enterprise)
     */
    @Parameter(property = "maven.alfresco.edition", defaultValue = ALFRESCO_COMMUNITY_EDITION)
    protected String alfrescoEdition;

    /**
     * Maven GAV properties for standard Alfresco web applications.
     */
    @Parameter(property = "alfresco.groupId", defaultValue = "org.alfresco")
    protected String alfrescoGroupId;

    @Parameter(property = "alfresco.platform.war.artifactId", defaultValue = "alfresco-platform")
    protected String alfrescoPlatformWarArtifactId;

    @Parameter(property = "alfresco.share.war.artifactId", defaultValue = "share")
    protected String alfrescoShareWarArtifactId;

    @Parameter(property = "alfresco.solr.artifactId", defaultValue = "alfresco-solr4")
    protected String alfrescoSolrArtifactId;

    @Parameter(property = "alfresco.api.explorer.artifactId", defaultValue = "api-explorer")
    protected String alfrescoApiExplorerArtifactId;

    @Parameter(property = "alfresco.platform.version", defaultValue = "5.2.a-EA")
    protected String alfrescoPlatformVersion;

    @Parameter(property = "alfresco.share.version", defaultValue = "5.1.g")
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
            fixSolrHomePath();
            replaceSolrConfigProperties();
            installSolr10InLocalRepo();
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
                                        element(name("artifactId"), getSolrArtifactId()),
                                        element(name("version"), alfrescoPlatformVersion),
                                        // The Solr config is not in a special file with classifier config if <= 4.2
                                        isPlatformVersionLtOrEqTo42() ? element(name("classifier"), "") : element(name("classifier"), "config"),
                                        element(name("type"), "zip")
                                )
                        )
                ),
                execEnv
        );
    }

    /**
     * For windows paths, convert single \ to / for the ${alfresco.solr.data.dir} path,
     * by default it will be c:\bla\, we need forward slash or double backslash.
     *
     * @throws MojoExecutionException
     */
    protected void fixSolrHomePath() throws MojoExecutionException {
        getLog().info("Fix Solr Home Path to work in Windows");

        executeMojo(
                plugin(
                        groupId("org.codehaus.mojo"),
                        artifactId("build-helper-maven-plugin"),
                        version(MAVEN_BUILD_HELPER_PLUGIN_VERSION)
                ),
                goal("regex-property"),
                configuration(
                        element(name("name"), "solrDataDir"),
                        element(name("value"), solrHome),
                        element(name("regex"), "\\\\"),
                        element(name("replacement"), "/"),
                        element(name("failIfNoMatch"), "false")
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
                                        element(name("value"), "${solrDataDir}/index")
                                )
                        )
                ),
                execEnv
        );
    }

    /**
     * If we are in Alfresco version 4.2 or younger the Solr 1.0 WAR is not available as Maven artifact, just
     * as part of a ZIP file, so install it locally so we can deploy from embedded tomcat
     *
     * @throws MojoExecutionException
     */
    protected void installSolr10InLocalRepo() throws MojoExecutionException {
        if (isPlatformVersionLtOrEqTo42()) {
            getLog().info("Installing Solr 1.0 WAR in local Maven repo");

            // Install the Solr 1.0 war file in local maven  repo
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-install-plugin"),
                            version(MAVEN_INSTALL_PLUGIN_VERSION)
                    ),
                    goal("install-file"),
                    configuration(
                            element(name("file"), solrHome + "/apache-solr-1.4.1.war"),
                            element(name("groupId"), "${project.groupId}"),
                            element(name("artifactId"), getSolrArtifactId()),
                            element(name("version"), "${project.version}"),
                            element(name("packaging"), "war")
                    )
                    , execEnv
            );
        }
    }

    /**
     * Replaces web.xml where applicable in platform webapp (alfresco.war),
     * commenting out the security-constraints.
     * <p/>
     * This is only needed for 4.2, 5.0 (5.1 handles it automatically when turning off ssl via props)
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
     * Copy the Alfresco Enterprise license to its correct place in the Platform WAR, if it exists.
     * It is not enough to have it on the test classpath, then it will start up as Trial license...
     *
     * @throws MojoExecutionException
     */
    protected void copyAlfrescoLicense() throws MojoExecutionException {
        if (alfrescoEdition.equals(ALFRESCO_COMMUNITY_EDITION)) {
            getLog().info("NOT copying Alfresco Enterprise license, running Community edition");
            return;
        }

        final String warOutputDir = getWarOutputDir(PLATFORM_WAR_PREFIX_NAME);
        final String licDestDir = warOutputDir + "/WEB-INF/classes/alfresco/extension/license";

        getLog().info("Copying Alfresco Enterprise license to: " + licDestDir);

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin"),
                        version(MAVEN_RESOURCE_PLUGIN_VERSION)
                ),
                goal("copy-resources"),
                configuration(
                        element(name("outputDirectory"), licDestDir),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), "src/test/license"),
                                        element(name("includes"),
                                                element(name("include"), "*.lic")
                                        ),
                                        element(name("filtering"), "false")
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
        buildCustomWarInDir(PLATFORM_WAR_PREFIX_NAME, platformModules,
                getPlatformWarArtifactId(), alfrescoPlatformVersion);

        commentOutSecureCommsInPlatformWebXml();
        copyAlfrescoLicense();

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
        buildCustomWarInDir(SHARE_WAR_PREFIX_NAME, shareModules, alfrescoShareWarArtifactId, alfrescoShareVersion);

        String shareWarArtifactId = packageAndInstallCustomWar(SHARE_WAR_PREFIX_NAME);

        // Set up the custom share war to be run by Tomcat plugin
        runnerAlfrescoGroupId = "${project.groupId}";
        runnerAlfrescoShareWarArtifactId = shareWarArtifactId;
        runnerAlfrescoShareVersion = "${project.version}";
    }

    /**
     * Build a customized webapp in a directory,
     * applying a number of AMPs and JARs from alfresco maven plugin configuration.
     *
     * @param warName               the name of the custom war
     * @param modules               the modules that should be applied to the custom war
     * @param originalWarArtifactId the artifactId for the original war file that should be customized
     * @param originalWarVersion    the version for the original war file that should be customized
     * @throws MojoExecutionException
     */
    protected void buildCustomWarInDir(String warName,
                                       List<ModuleDependency> modules,
                                       String originalWarArtifactId,
                                       String originalWarVersion) throws MojoExecutionException {
        final String warOutputDir = getWarOutputDir(warName);
        final String ampsModuleDir = "modules/" + warName + "/amps";
        final String ampsOutputDir = "${project.build.directory}/" + ampsModuleDir;
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
            // Call the Alfresco Maven Plugin Install Mojo directly, so we don't have to keep SDK version info here
            String ampsLocation = project.getBuild().getDirectory() + "/" + ampsModuleDir;
            String warLocation = project.getBuild().getDirectory() + "/" + getWarName(warName);
            InstallMojo installMojo = new InstallMojo();
            installMojo.setAmpLocation(new File(ampsLocation));
            installMojo.setWarLocation(new File(warLocation));
            installMojo.setForce(true);
            try {
                installMojo.execute();
            } catch (MojoFailureException e) {
                e.printStackTrace();
            }
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
        final String warSourceDir = getWarOutputDir(warName);

        // Package the customized war file
        // Note. don't use the maven-war-plugin here as it will package the module files twice, once from the
        // target/classes dir and once via the JAR
        String warPath = project.getBuild().getDirectory() + "/" + warName + ".war";
        ZipUtil.pack(new File(warSourceDir), new File(warPath));

        // Install the customized war file in the local maven repo
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-install-plugin"),
                        version(MAVEN_INSTALL_PLUGIN_VERSION)
                ),
                goal("install-file"),
                configuration(
                        element(name("file"), warPath),
                        element(name("groupId"), "${project.groupId}"),
                        element(name("artifactId"), warArtifactId),
                        element(name("version"), "${project.version}"),
                        element(name("packaging"), "war") // Don't forget, otherwise installed as .POM
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
            tomcatDependencies.add(
                    // Bring in the flat file H2 database
                    dependency("com.h2database", "h2", "1.4.190"));

            // Copy the h2 scripts
            copyH2Dialect();
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
            webapps2Deploy.add(getSolrWebappElement());
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
                         * SDK Projects doesn't have packaging set to 'war', they are JARs or POMs,
                         * this setting ignores that fact.
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
                        element(name("useSeparateTomcatClassLoader"), "true"),

                        /*
                         * Bring in stuff in the test classpath, such as the alfresco-global.properties
                         * that should be used
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
        if (getPlatformVersionNumber() >= 51) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if current platform version (i.e. version of alfresco.war) is
     * <= 4.2
     *
     * @return true if platform version <= 4.2
     */
    private boolean isPlatformVersionLtOrEqTo42() {
        if (getPlatformVersionNumber() <= 42) {
            return true;
        }

        return false;
    }

    /**
     * Grabs the first 2 numbers in the version string
     *
     * @return major and minor version as int, such as 41,50,51
     */
    private int getPlatformVersionNumber() {
        return Integer.parseInt(alfrescoPlatformVersion.replaceAll("[^0-9]", "").substring(0, 2));
    }

    /**
     * Get the Solr artifactId, it changes when we move to Solr 4 in Alfresco version 5
     *
     * @return the Maven artifactId for Solr
     */
    private String getSolrArtifactId() {
        // artifactId for Solr defaults to version 4 = alfresco-solr4

        if (isPlatformVersionLtOrEqTo42()) {
            // Solr version 1 is used in Alfresco 4.0 -> 4.2, Solr version 4.0 was introduced in Alfresco version 5.0
            alfrescoSolrArtifactId = "alfresco-solr";
        }

        return alfrescoSolrArtifactId;
    }

    /**
     * Get the Alfresco Platform Webapp artifactId (i.e. for alfresco.war),
     * it changes from 'alfresco' to 'alfresco-platform' in 5.1.
     *
     * @return the Maven artifactId for Alfresco Platform webapp
     */
    private String getPlatformWarArtifactId() {
        // Default alfrescoPlatformWarArtifactId is 'alfresco-platform'

        if (isPlatformVersionGtOrEqTo51() == false) {
            // We are running version 4.2 or 5.0, so use older artifactId
            alfrescoPlatformWarArtifactId = "alfresco";
        } else if (alfrescoEdition.equals(ALFRESCO_ENTERPRISE_EDITION)) {
            alfrescoPlatformWarArtifactId = "alfresco-enterprise";
        }

        return alfrescoPlatformWarArtifactId;
    }

    /**
     * Get the Solr webapp element for use by Tomcat, it changes when we move to Solr 4 in Alfresco version 5
     *
     * @return tomcat webapp element
     */
    private Element getSolrWebappElement() {
        Element webappElement = null;

        if (isPlatformVersionLtOrEqTo42()) {
            // Solr version 1.0
            webappElement = createWebAppElement("${project.groupId}", getSolrArtifactId(), "${project.version}",
                    "/solr", "${project.build.testOutputDirectory}/tomcat/context-solr.xml");
        } else {
            // Solr version 4.0
            webappElement = createWebAppElement(alfrescoGroupId, getSolrArtifactId(), alfrescoPlatformVersion,
                "/solr4", "${project.build.testOutputDirectory}/tomcat/context-solr.xml");
        }

        return webappElement;
    }

    /**
     * TODO: From 5.1.e and onwards we have the alfresco-repository:h2scripts:jar artifact, so we potentially only need to do this for older than 5.1.e
     *
     *
     * Extract PostgreSQL dialect and ibatis from alfresco-repository, rename to H2Dialect in the test-classes
     *
     * @return
     */
    private void copyH2Dialect() throws MojoExecutionException {
        getLog().info("Unpacking DB Dialects and ibatis files from alfresco-repository artifact");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                ),
                goal("unpack"),
                configuration(
                        element(name("outputDirectory"), "${project.build.testOutputDirectory}"),
                        element(name("artifactItems"),
                                element(name("artifactItem"),
                                        element(name("groupId"), alfrescoGroupId),
                                        element(name("artifactId"), "alfresco-repository"),
                                        element(name("version"), alfrescoPlatformVersion),
                                        element(name("includes"), "alfresco/dbscripts/create/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/dbscripts/upgrade/*/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/ibatis/org.hibernate.dialect.PostgreSQLDialect/*")
                                )
                        )
                ),
                execEnv
        );

        // If we're in enterprise we need to make sure we grab everything
        if (this.alfrescoEdition.equals(ALFRESCO_ENTERPRISE_EDITION)) {
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version(MAVEN_DEPENDENCY_PLUGIN_VERSION)
                    ),
                    goal("unpack"),
                    configuration(
                            element(name("outputDirectory"), "${project.build.testOutputDirectory}"),
                            element(name("artifactItems"),
                                    element(name("artifactItem"),
                                            element(name("groupId"), alfrescoGroupId),
                                            element(name("artifactId"), "alfresco-enterprise-repository"),
                                            element(name("version"), alfrescoPlatformVersion),
                                            element(name("includes"), "alfresco/dbscripts/create/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/dbscripts/upgrade/*/org.hibernate.dialect.PostgreSQLDialect/*,alfresco/ibatis/org.hibernate.dialect.PostgreSQLDialect/*")
                                    )
                            )
                    ),
                    execEnv
            );
        }

        getLog().info("Copying H2 Dialect SQL create files into target/test-classes");
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
                                        element(name("directory"), "${project.build.testOutputDirectory}/alfresco/dbscripts/create/org.hibernate.dialect.PostgreSQLDialect"),
                                        element(name("includes"),
                                                element(name("include"), "*")
                                        ),
                                        element(name("targetPath"), "alfresco/dbscripts/create/org.hibernate.dialect.H2Dialect")
                                ),
                                element(name("resource"),
                                        element(name("directory"), "${project.build.testOutputDirectory}/alfresco/ibatis/org.hibernate.dialect.PostgreSQLDialect"),
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


    /**
     * The directory where the custom war will be assembled
     *
     * @param baseWarName a war base name, such as 'platform' or 'share'
     * @return a directory such as: .../aio/target/platform-war
     */
    private String getWarOutputDir(String baseWarName) {
        return project.getBuild().getDirectory() + "/" + getWarName(baseWarName);
    }

    /**
     * Get the war filename based on passed in war type
     *
     * @param baseWarName a war base name, such as 'platform' or 'share'
     * @return
     */
    private String getWarName(String baseWarName) {
        return baseWarName + "-war";
    }
}
