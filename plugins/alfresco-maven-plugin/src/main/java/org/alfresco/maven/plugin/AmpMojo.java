package org.alfresco.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.alfresco.maven.plugin.archiver.AmpArchiver;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Builds an AMP archive of the current project's contents. 
 * The location of the AMP contents is ${project.build.directory}/${project.build.finalName}.
 * Java resources (in src/main/java and src/main/resources) are packaged in a standard JAR file
 * that is automatically bundled in the /lib folder of the AMP archive and it's treated as build artifact
 * (i.e. distributed on Maven repositories during deploy).
 * Maven transitive dependencies are by default added into the /lib folder of the AMP archive
 *
 * @author Gabriele Columbro, Maurizio Pillitu
 * @version $Id:$
 * @goal amp
 * @phase package
 * @requiresProject
 * @threadSafe
 * @since 1.0
 * @requiresDependencyResolution runtime
 * @description Packages an Alfresco AMP file in ${project.build.directory} using the content found in ${project.build.directory}/${project.build.finalName}
 */
public class AmpMojo extends AbstractMojo {

    /**
     * Name of the generated AMP and JAR artifacts
     *
     * @parameter property="maven.alfresco.ampFinalName" default-value="${project.build.finalName}"
     * @required
     * @readonly
     */
    protected String ampFinalName;

    /**
     * Target folder used to aggregate content then packaged into the AMP
     *
     * @parameter property="maven.alfresco.ampBuildDirectory" default-value="${project.build.directory}/${project.build.finalName}"
     * @required
     *
     */
    protected File ampBuildDirectory;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will be attached.
     * If this is not given,it will merely be written to the output directory
     * according to the finalName.
     *
     * @parameter property="maven.alfresco.classifier"
     */
    protected String classifier;

    /**
     * Whether (runtime scoped) JAR dependencies (including transitive) should be added or not to the generated AMP /lib folder. 
     * By default it's true so all direct and transitive (runtime) dependencies will be added
     * 
     * @parameter property="maven.alfresco.includeDependencies" default-value="true"
     * @required
     */
    protected boolean includeDependencies;

    /**
     * Directory the build produces the AMP file in 
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File outputDirectory;

    /**
     * (Read Only) Directory containing the classes and resource files that should be packaged into the JAR.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    protected File classesDirectory;

    /**
     * (Read Only) The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * (Read Only) The Maven session
     *
     * @parameter default-value="${session}"
     * @readonly
     * @required
     */
    protected MavenSession session;

    /**
     * The archive configuration to use.
     * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     *
     * @parameter
     */
    protected MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * @component
     */
    protected MavenProjectHelper projectHelper;

    public void execute()
            throws MojoExecutionException {

        if(includeDependencies) {
        	gatherDependencies();
        }

        File ampFile = createArchive();
        if (this.classifier != null) {
            this.projectHelper.attachArtifact(this.project, "amp", this.classifier, ampFile);
        } else {
            this.project.getArtifact().setFile(ampFile);
        }
    }

    /**
     * Creates and returns the AMP archive, invoking the AmpArchiver
     *
     * @return a File pointing to an existing AMP package, contained
     *         in ${project.build.outputDirectory}
     */
    protected File createArchive()
            throws MojoExecutionException {
        File jarFile = getFile(
                new File(this.ampBuildDirectory, AmpModel.AMP_FOLDER_LIB),
                this.ampFinalName,
                this.classifier,
                "jar");

        File ampFile = getFile(
                this.outputDirectory,
                this.ampFinalName,
                this.classifier,
                "amp"
        );

        MavenArchiver jarArchiver = new MavenArchiver();
        jarArchiver.setArchiver(new JarArchiver());
        jarArchiver.setOutputFile(jarFile);

        MavenArchiver ampArchiver = new MavenArchiver();
        ampArchiver.setArchiver(new AmpArchiver());
        ampArchiver.setOutputFile(ampFile);

        if (!this.ampBuildDirectory.exists()) {
            getLog().warn("ampBuildDirectory does not exist - AMP will be empty");
        } else {
            try {
                jarArchiver.getArchiver().addDirectory(this.classesDirectory, new String[]{}, new String[]{});
                jarArchiver.createArchive(this.session, this.project, this.archive);
              } catch (Exception e) {
                    throw new MojoExecutionException("Error creating JAR", e);
              }
              try {
                ampArchiver.getArchiver().addDirectory(this.ampBuildDirectory, new String[]{"**"}, new String[]{});
                ampArchiver.createArchive(this.session, this.project, this.archive);
              }
              catch (Exception e) {
                  throw new MojoExecutionException("Error creating AMP", e);
              }
        }
        return ampFile;
    }

    /**
     * Builds a File object pointing to the target AMP package; the pointer to the File is created taking into
     * account the (optional) artifact classifier defined
     *
     * @param basedir    the Base Directory of the currently built project
     * @param finalName  the Final Name of the artifact being built
     * @param classifier the optional classifier of the artifact being built
     * @return a File object pointing to the target AMP package
     */
    protected static File getFile(File basedir, String finalName, String classifier, String extension) {
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }
        return new File(basedir, finalName + classifier + "." + extension);
    }
    
    /**
     * Copies all runtime dependencies to AMP lib. By default transitive runtime dependencies are retrieved.
     * This behavior can be configured via the transitive parameter
     * @throws MojoExecutionException
     */
    protected void gatherDependencies() throws MojoExecutionException
    {
        Set<Artifact> dependencies = null;
        // Whether transitive deps should be gathered or not
        dependencies = project.getArtifacts();

        ScopeArtifactFilter filter = new ScopeArtifactFilter( Artifact.SCOPE_RUNTIME );
        
        for (Artifact artifact : dependencies) {
            if ( !artifact.isOptional() && filter.include( artifact ) )
            {
                String type = artifact.getType();

                if (AmpModel.EXTENSION_LIST.contains(type))
                {
                    File targetFile = new File(ampBuildDirectory + File.separator + AmpModel.AMP_FOLDER_LIB + File.separator + artifact.getFile().getName());
                    String targetFilePath = targetFile.getPath();
                    try {
                        FileUtils.copyFile(artifact.getFile(), targetFile);
                    } catch (IOException e) {
                        throw new MojoExecutionException("Error copying transitive dependency " + artifact.getId() + " to file: " + targetFilePath);
                    }
                }
            }
        }
    }
}