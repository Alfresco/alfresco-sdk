package org.alfresco.maven.plugin;

import org.alfresco.repo.module.tool.ModuleManagementTool;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Performs a AMP to WAR overlay invoking the Alfresco Repository POJO
 * ModuleManagementTool.installModules() and therefore emulating the same
 * WAR overlay performed by Alfresco Repository during bootstrap.
 * <p/>
 * The AMP files overlaid are all AMP runtime dependencies defined in the
 * current project's build.
 * <p/>
 * Additionally (and optionally) you can define the full path of a single AMP file that needs to
 * be overlaid, using the <simpleAmp> configuration element.
 *
 * @version $Id:$
 * @requiresDependencyResolution
 * @goal install
 */
public class InstallMojo extends AbstractMojo {

    private static final String AMP_OVERLAY_FOLDER_NAME = "ampoverlays_temp";

    /**
     * Name of the generated AMP and JAR artifacts
     *
     * @parameter expression="${ampFinalName}" default-value="${project.build.finalName}"
     * @required
     * @readonly
     */
    protected String ampFinalName;

    /**
     * The WAR file or exploded dir to install the AMPs in.  If specified
     * Defaults to <code>outputDirectory/${ampFinalName}-war</code>
     *
     * @parameter expression="${warLocation}" default-value="${project.build.directory}/${project.build.finalName}-war"
     */
    private File warLocation;

    /**
     * One single amp file that, if exists, gets included into the list
     * of modules to install within the Alfresco WAR, along with other AMP
     * defined as (runtime) Maven dependencies
     *
     * @parameter expression="${singleAmp}" default-value="${project.build.directory}/${project.build.finalName}.amp"
     */
    private File singleAmp;

    /**
     * [Read Only] The target/ directory.
     *
     * @parameter expression="${project.build.directory}"
     * @readonly
     * @required
     */
    private String outputDirectory;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public InstallMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File overlayTempDir = new File(this.outputDirectory, AMP_OVERLAY_FOLDER_NAME);
        getLog().debug("Setting AMP Destination dir to " + overlayTempDir.getAbsolutePath());

        /**
         * Collect all AMP runtime dependencies and copy all files
         * in one single build folder, *ampDirectoryDir*
         */
        try {
            for (Object artifactObj : project.getRuntimeArtifacts()) {
                if (artifactObj instanceof Artifact) {
                    Artifact artifact = (Artifact) artifactObj;
                    if ("amp".equals(artifact.getType())) {
                        File artifactFile = artifact.getFile();
                        FileUtils.copyFileToDirectory(artifactFile, overlayTempDir);
                        getLog().debug(String.format("Copied %s into %s", artifactFile, overlayTempDir));
                    }
                }
            }
            if (this.singleAmp != null && this.singleAmp.exists()) {
                if (!overlayTempDir.exists()) {
                    overlayTempDir.mkdirs();
                }

                FileUtils.copyFileToDirectory(this.singleAmp, overlayTempDir);
                getLog().debug(String.format("Copied %s into %s", this.singleAmp, overlayTempDir));
            }
        } catch (IOException e) {
            getLog().error(
                    String.format(
                            "Cannot copy AMP module to folder %s",
                            overlayTempDir));
        }

        // Locate the WAR file to overlay - the one produced by the current project
//        if (warLocation == null) {
//	        String warLocation = this.outputDirectory + File.separator + this.ampFinalName + "-war" + File.separator;
//	        this.warLocation = new File(warLocation);
//        }
        if (!warLocation.exists()) {
            getLog().info(
              "No WAR file found in " + warLocation.getAbsolutePath() + " - skipping overlay.");
        } else if (overlayTempDir == null ||
          !overlayTempDir.exists()) {
          getLog().info(
              "No ampoverlay folder found in " + overlayTempDir + " - skipping overlay.");
        } else if (overlayTempDir.listFiles().length == 0) {
            getLog().info(
              "No runtime AMP dependencies found for this build - skipping overlay.");
        } else {
            /**
             * Invoke the ModuleManagementTool to install AMP modules on the WAR file;
             * so far, no backup or force flags are enabled
             */
            ModuleManagementTool mmt = new ModuleManagementTool();
            mmt.setVerbose(true);
            try {
                mmt.installModules(
                        overlayTempDir.getAbsolutePath(),
                        warLocation.getAbsolutePath(),
                        false,  //preview
                        true,   //force install
                        false); //backup
            } catch (IOException e) {
                throw new MojoExecutionException("Problems while installing " + 
            overlayTempDir.getAbsolutePath() + " onto " + warLocation.getAbsolutePath(), e);
            }
        }
    }
}