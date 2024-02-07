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

import org.alfresco.maven.plugin.config.ModuleDependency;
import org.alfresco.maven.plugin.config.TomcatDependency;
import org.alfresco.maven.plugin.config.TomcatWebapp;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Abstract runner component used by both the standard runner that blocks after startup and the
 * integration test runner that stops container after each sub-project execution.
 *
 * @author ole.hejlskov, martin.bergljung
 * @version 1.0
 * @since 3.0
 */
public abstract class AbstractRunMojo extends AbstractMojo {
    public static final String MAVEN_DEPENDENCY_PLUGIN_VERSION = "2.10";
    public static final String MAVEN_INSTALL_PLUGIN_VERSION = "2.5.2";
    public static final String MAVEN_REPLACER_PLUGIN_VERSION = "1.5.3";
    public static final String MAVEN_RESOURCE_PLUGIN_VERSION = "2.7";
    public static final String MAVEN_TOMCAT7_PLUGIN_VERSION = "2.2";

    public static final String PLATFORM_WAR_PREFIX_NAME = "platform";
    public static final String SHARE_WAR_PREFIX_NAME = "share";


    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    protected BuildPluginManager pluginManager;



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
     *              <enablePlatform>true</enablePlatform>
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
     * Switch to enable/disable the Platform/Repository (alfresco.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enablePlatform", defaultValue = "true")
    protected boolean enablePlatform;

    /**
     * Enable or disable generation of Hotswap Agent configuration
     */
    @Parameter(property = "maven.alfresco.copyHotswapAgentConfig", defaultValue = "true")
    protected boolean copyHotswapAgentConfig;


    /**
     * Switch to enable/disable the Share (share.war) when running embedded Tomcat.
     */
    @Parameter(property = "maven.alfresco.enableShare", defaultValue = "true")
    protected boolean enableShare;

    /**
     * Enables the use of custom context path for the Share webapp.
     * Some solution integrators uses a custom context path for Share in their projects.
     * This property enables them to continue to do that in SDK 3 without having to completely override the
     * Maven Tomcat plugin configuration, or not use it at all and go back the good old runner project again...
     */
    @Parameter(property = "maven.alfresco.shareContextPath", defaultValue = "/share")
    protected String shareContextPath;

    /**
     * Share Log4j2.properties configuration cannot be customized via extension
     * put on the classpath, like on the platform side.
     * So we need to override the log4j2.properties in share/WEB-INF/classes
     * to be able to log from custom code.
     * This property can be used to turn off this overriding, to produce a WAR with
     * the standard Share log4j2.properties file.
     */
    @Parameter(property = "maven.alfresco.useCustomShareLog4jConfig", defaultValue = "true")
    protected boolean useCustomShareLog4jConfig;

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
     * Tomcat Dependencies that should be added to the Embedded Tomcat configuration before start.
     * Normally there would not be any extra dependencies, but could be if you run an Enterprise database
     * such as Oracle, for which there's no quick config, such as enableH2, enableMySQL, or enablePostgreSQL.
     */
    @Parameter(property = "maven.alfresco.tomcat.dependencies", defaultValue = "")
    protected List<TomcatDependency> tomcatDependencies;

    /**
     * System Properties to feed the Tomcat plugin before start.
     * Normally there would not be any extra dependencies, but we could run a custom webapp that needed
     * a custom sys prop set.
     */
    @Parameter(property = "maven.alfresco.tomcat.system.properties", defaultValue = "")
    protected Map<String, String> tomcatSystemProperties;

    /**
     * Custom webapps that should be deployed to the embedded Tomcat engine.
     * Normally there would not be any extra webapps, but we could run a bigger project that uses
     * some custom webapp.
     */
    @Parameter(property = "maven.alfresco.tomcat.custom.webapps", defaultValue = "")
    protected List<TomcatWebapp> tomcatCustomWebapps;

    /**
     * Port to run Tomcat on
     */
    @Parameter(property = "maven.alfresco.tomcat.port", defaultValue = "8080")
    protected String tomcatPort;

    /**
     * Legacy to be compatible with maven.tomcat.port
     */
    @Parameter(property = "maven.tomcat.port", defaultValue = "")
    protected String mavenTomcatPort;


    /**
     * Maven GAV properties for standard Alfresco web applications.
     */
    @Parameter(property = "alfresco.groupId", defaultValue = "org.alfresco")
    protected String alfrescoGroupId;

    @Parameter(property = "alfresco.platform.war.artifactId", defaultValue = "content-services-community")
    protected String alfrescoPlatformWarArtifactId;

    @Parameter(property = "alfresco.share.war.artifactId", defaultValue = "share")
    protected String alfrescoShareWarArtifactId;


    @Parameter(property = "alfresco.api.explorer.artifactId", defaultValue = "api-explorer")
    protected String alfrescoApiExplorerArtifactId;


    @Parameter(property = "alfresco.platform.version", defaultValue = "6.0.7-ga")
    protected String alfrescoPlatformVersion;

    @Parameter(property = "alfresco.share.version", defaultValue = "6.0.7-ga")
    protected String alfrescoShareVersion;

    @Parameter(property = "alfresco.api.explorer.version", defaultValue = "6.0.7-ga")
    protected String alfrescoApiExplorerVersion;



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
    protected ExecutionEnvironment execEnv;

    /**
     * Get the Tomcat port. By default the port is changed by using the maven.alfresco.tomcat.port property
     * but for legacy and external configuration purposes maven.tomcat.port will override if defined
     *
     * @return the Tomcat port
     */
    protected String getPort() {
        String port = tomcatPort;
        if (mavenTomcatPort != null) {
            port = mavenTomcatPort;
            getLog().info( "Tomcat Port overridden by property maven.tomcat.port");
        }

        return port;
    }

    /**
     * Check if Tomcat is already running.
     *
     * @return <code>true</code> if Tomcat is already running, <code>false</code> otherwise
     */
    protected boolean tomcatIsRunning()  {

        CloseableHttpClient client= HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            HttpGet httpget = new HttpGet("http://localhost:" + getPort() + "/alfresco");
            response = client.execute(httpget);
            getLog().info("Tomcat is running on port "+ getPort());
            return true;

        } catch (Exception ex) {
            getLog().info("Tomcat is not running on port " + getPort() );
            return false;
        }

    }






    /**
     * Copy the alfresco-global.properties files. It contains database connection parameters and
     * other general configuration for Alfresco Repository (alfresco.war)
     *
     * @throws MojoExecutionException when any problem appears copying the Alfresco global properties file
     */
    protected void copyAlfrescoGlobalProperties() throws MojoExecutionException {
        getLog().info("Copying and filtering alfresco-global.properties files to target/test-classes");
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
                                element(name("include"), "${project.build.testOutputDirectory}/*.properties")
                        ),
                        element(name("replacements"),
                                element(name("replacement"),
                                        element(name("token"), "alfresco.port=8080"),
                                        element(name("value"), "alfresco.port=" + getPort())
                                ),
                                element(name("replacement"),
                                        element(name("token"), "share.port=8080"),
                                        element(name("value"), "share.port=" + getPort())
                                ),
                                element(name("replacement"),
                                        element(name("token"), "solr.port=8080"),
                                        element(name("value"), "solr.port=" + getPort())
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
     * @throws MojoExecutionException when any problem appears copying the Alfresco license
     */
    protected void copyAlfrescoLicense() throws MojoExecutionException {

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
     * Copy Share Config Custom in order to have global overrides for development and dynamic port
     *
     * @throws MojoExecutionException when any problem appears copying share config custom file
     */
    protected void copyShareConfigCustom() throws MojoExecutionException {
        final String warOutputDir = getWarOutputDir(SHARE_WAR_PREFIX_NAME);
        final String distDir = warOutputDir + "/WEB-INF/classes/alfresco/web-extension/";
        String repoUrl = project.getProperties().getProperty("alfresco.repo.url");
        if (repoUrl == null) {
            project.getProperties().setProperty("alfresco.repo.url", "http://localhost:" + getPort() + "/alfresco");
        }

        getLog().info("Copying Share config custom to: " + distDir);

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin"),
                        version(MAVEN_RESOURCE_PLUGIN_VERSION)
                ),
                goal("copy-resources"),
                configuration(
                        element(name("outputDirectory"), distDir),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), "src/test/resources/share"),
                                        element(name("includes"),
                                                element(name("include"), "*.xml")
                                        ),
                                        element(name("filtering"), "true")
                                )
                        )
                ),
                execEnv
        );
    }


    /**
     * Copy a custom Share Log4J2 config into the share-war/WEB-INF/classes dir.
     * There is no custom classpath resolve mechanism for Share log4j2,
     * to log custom stuff overriding standard log4j2.properties is needed.
     *
     * @throws MojoExecutionException when any problem appears copying the share log4j configuration
     */
    protected void copyShareLog4jConfig() throws MojoExecutionException {
        if (!useCustomShareLog4jConfig) {
            getLog().info("NOT overriding share/WEB-INF/classes/log4j2.properties");
            return;
        }

        final String warOutputDir = getWarOutputDir(SHARE_WAR_PREFIX_NAME);
        final String logConfDestDir = warOutputDir + "/WEB-INF/classes";

        getLog().info("Copying Share log4j2.properties to: " + logConfDestDir);

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-resources-plugin"),
                        version(MAVEN_RESOURCE_PLUGIN_VERSION)
                ),
                goal("copy-resources"),
                configuration(
                        element(name("outputDirectory"), logConfDestDir),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), "src/test/resources/share"),
                                        element(name("includes"),
                                                element(name("include"), "log4j2.properties")
                                        ),
                                        element(name("filtering"), "true")
                                )
                        )
                ),
                execEnv
        );
    }


    /**
     * Copy and Build hotswap-agent.properties
     *
     * @param warPrefix path to the war root directory
     *
     * @throws MojoExecutionException when any problem appears copying hotswap agent properties
     */
    protected void copyHotswapAgentProperties(String warPrefix) throws MojoExecutionException {
        if ( copyHotswapAgentConfig == false ) {
            return;
        }

        final String warOutputDir = getWarOutputDir(warPrefix) + "/WEB-INF/classes/";


        getLog().info("Copying " + warPrefix + "-hotswap-agent.properties to " + warOutputDir);

        executeMojo(
                plugin(
                        groupId("com.coderplus.maven.plugins"),
                        artifactId("copy-rename-maven-plugin"),
                        version("1.0")
                ),
                goal("rename"),
                configuration(
                        element(name("sourceFile"), project.getBuild().getTestOutputDirectory() + "/" + warPrefix + "-hotswap-agent.properties"),
                        element(name("destinationFile"), warOutputDir + "hotswap-agent.properties")
                ),
                execEnv
        );



    }

    /**
     * Build the customized Platform webapp (i.e. the Repository, alfresco.war)
     * that should be deployed by Tomcat by applying all AMPs and JARs from
     * the {@code <platformModules> } configuration.
     *
     * @throws MojoExecutionException when any problem appears building the platform war
     */
    protected void buildPlatformWar() throws MojoExecutionException {
        buildCustomWarInDir(PLATFORM_WAR_PREFIX_NAME, platformModules,
                alfrescoGroupId, alfrescoPlatformWarArtifactId, alfrescoPlatformVersion);

        copyAlfrescoLicense();
        copyHotswapAgentProperties(PLATFORM_WAR_PREFIX_NAME);

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
     *
     * @throws MojoExecutionException when any problem appears building the share war
     */
    protected void buildShareWar() throws MojoExecutionException {
        buildCustomWarInDir(SHARE_WAR_PREFIX_NAME, shareModules,
                alfrescoGroupId, alfrescoShareWarArtifactId, alfrescoShareVersion);

        copyShareLog4jConfig();
        copyShareConfigCustom();
        copyHotswapAgentProperties(SHARE_WAR_PREFIX_NAME);


        String shareWarArtifactId = packageAndInstallCustomWar(SHARE_WAR_PREFIX_NAME);

        // Set up the custom share war to be run by Tomcat plugin
        runnerAlfrescoGroupId = "${project.groupId}";
        runnerAlfrescoShareWarArtifactId = shareWarArtifactId;
        runnerAlfrescoShareVersion = "${project.version}";
    }


    /**
     * Build a customized webapp in a directory,
     * applying a number of AMPs and/or JARs from alfresco maven plugin configuration.
     *
     * @param warName               the name of the custom war
     * @param modules               the modules that should be applied to the custom war
     * @param originalWarGroupId    the Maven groupId for the original war file that should be customized
     * @param originalWarArtifactId the Maven artifactId for the original war file that should be customized
     * @param originalWarVersion    the Maven version for the original war file that should be customized
     * @throws MojoExecutionException when any problem appears building the custom war
     */
    protected void buildCustomWarInDir(String warName,
                                       List<ModuleDependency> modules,
                                       String originalWarGroupId,
                                       String originalWarArtifactId,
                                       String originalWarVersion) throws MojoExecutionException {
        final String warOutputDir = getWarOutputDir(warName);
        final String ampsModuleDir = "modules/" + warName + "/amps";
        final String ampsOutputDir = "${project.build.directory}/" + ampsModuleDir;
        List<Element> ampModules = new ArrayList<>();
        List<Element> jarModules = new ArrayList<>();

        if (modules != null && modules.size() > 0) {
            for (ModuleDependency moduleDep : modules) {
                Element el = element(name("artifactItem"),
                        element(name("groupId"), moduleDep.getGroupId()),
                        element(name("artifactId"), moduleDep.getArtifactId()),
                        element(name("version"), moduleDep.getVersion()),
                        element(name("classifier"), moduleDep.getClassifier()),
                        element(name("type"), moduleDep.getType()),
                        element(name("overWrite"), "true"));

                if (moduleDep.getArtifactId().equalsIgnoreCase("alfresco-share-services")) {
                    // Skip if we are not running a 5.1 version of Alfresco, 'Alfresco Share Services'
                    // was not used in earlier versions
                    if (!isPlatformVersionGtOrEqTo51()) {
                        getLog().info("Skipping alfresco-share-services, only needed for 5.1+");
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
                                        element(name("groupId"), originalWarGroupId),
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
     * @throws MojoExecutionException when any problem appears packaging or installing the custom war
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
     * @param fork true if tomcat process should be forked
     * @throws MojoExecutionException when any problem appears starting tomcat
     */
    protected void startTomcat(boolean fork) throws MojoExecutionException {
        getLog().info("Starting Tomcat, fork = " + fork);

        List<Dependency> tomcatPluginDependencies = new ArrayList<Dependency>();
        ArrayList webapps2Deploy = new ArrayList<Element>();

        // Add the basic Tomcat dependencies
        tomcatPluginDependencies.add(
                // Packaging goes faster with this lib
                dependency("org.codehaus.plexus", "plexus-archiver", "2.3"));

        tomcatPluginDependencies.add(
                // The following dependency is needed, otherwise you get
                //  Caused by: java.lang.NoSuchMethodError:
                //      javax.servlet.ServletContext.getSessionCookieConfig()Ljavax/servlet/SessionCookieConfig
                // This method is in Servlet API 3.0
                dependency("javax.servlet", "javax.servlet-api", "3.0.1"));

        // Do we have any extra Tomcat Plugin dependencies to include?
        if (tomcatDependencies != null && tomcatDependencies.size() > 0) {
            for (TomcatDependency tomcatDep : tomcatDependencies) {
                tomcatPluginDependencies.add(
                        dependency(tomcatDep.getGroupId(), tomcatDep.getArtifactId(), tomcatDep.getVersion()));
            }
        }

        tomcatPluginDependencies.add(
                // Bring in the MySQL JDBC Driver
                dependency("mysql", "mysql-connector-java", "5.1.32"));

        tomcatPluginDependencies.add(
                // Bring in the PostgreSQL JDBC Driver
                dependency("org.postgresql", "postgresql", "9.4-1201-jdbc41"));


        if (enablePlatform) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoPlatformWarArtifactId, runnerAlfrescoPlatformVersion,
                    "/alfresco", null));
        }

        if (enableShare) {
            webapps2Deploy.add(createWebAppElement(
                    runnerAlfrescoGroupId, runnerAlfrescoShareWarArtifactId, runnerAlfrescoShareVersion,
                    shareContextPath, null));
        }

        if (enableApiExplorer) {
            webapps2Deploy.add(createWebAppElement(
                    alfrescoGroupId, alfrescoApiExplorerArtifactId, alfrescoApiExplorerVersion,
                    "/api-explorer", null));
        }


        if (tomcatCustomWebapps != null && !tomcatCustomWebapps.isEmpty()) {
            // We got extra custom webapps to deploy
            for (TomcatWebapp customWebapp: tomcatCustomWebapps) {
                webapps2Deploy.add(createWebAppElement(
                        customWebapp.getGroupId(), customWebapp.getArtifactId(), customWebapp.getVersion(),
                        customWebapp.getContextPath(), customWebapp.getContextFile()));
            }
        }

        // This might be ugly, the MojoExecuter will only accept Element[] and we need this list to be dynamic
        // to avoid NPEs. If there's a better way to do this, then feel free to change it!
        Element[] webapps = new Element[webapps2Deploy.size()];
        webapps2Deploy.toArray(webapps);

        // Set up the system properties that should be set for Tomcat
        ArrayList systemProps = new ArrayList<Element>();

        // Add custom system properties defined in plugin config
        if (tomcatSystemProperties != null && !tomcatSystemProperties.isEmpty()) {
            for (Map.Entry<String, String> sysProp : tomcatSystemProperties.entrySet()) {
                systemProps.add(element(name(sysProp.getKey()), sysProp.getValue()));
            }
        }
        // This might be ugly, the MojoExecuter will only accept Element[] and we need this list to be dynamic
        // to avoid NPEs. If there's a better way to do this, then feel free to change it!
        Element[] systemPropArray = new Element[systemProps.size()];
        systemProps.toArray(systemPropArray);

        executeMojo(
                plugin(
                        groupId("org.apache.tomcat.maven"),
                        artifactId("tomcat7-maven-plugin"),
                        version(MAVEN_TOMCAT7_PLUGIN_VERSION),
                        tomcatPluginDependencies
                ),
                goal("run"),
                configuration(
                        element(name("fork"), fork ? "true" : "false"),

                        /*
                         * Path
                         * In case there is a src/main/webapp in AIO it's nice to serve it up
                         */
                        element(name( "path"), "/"),

                        /*
                         * Port
                         */
                        element(name( "port" ), getPort()),

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


                        element(name("systemProperties"), systemPropArray),

                        /*  Should this class loader delegate to the parent class loader before searching its
                            own repositories (i.e. the usual Java2 delegation model).
                            Prevent parent classloader delegation, each webapp loads.
                            If set to true then you will get a truckload of Solr logging as
                            the alf_data_dev/solr4/config/log4j2-solr.properties file is not picked up.
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
