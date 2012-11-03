package org.alfresco.maven.plugin.archiver;

import java.io.File;

import org.alfresco.repo.module.tool.ModuleManagementTool;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.AbstractZipUnArchiver;
import org.codehaus.plexus.component.annotations.Requirement;

public class AmpUnArchiver extends AbstractZipUnArchiver {

    public AmpUnArchiver()
    {
    }
    @Requirement
    private LegacySupport legacySupport;

    @Override
    public File getDestDirectory() {
        MavenSession session = legacySupport.getSession();
        MavenProject project = session.getCurrentProject();
        return new File(project.getBuild().getDirectory() + File.separator + project.getBuild().getFinalName());
    }

    @Override
    protected void execute() throws ArchiverException {
        try {
            /**
             * Invoke the ModuleManagementTool to install AMP modules on the WAR file;
             * so far, no backup or force flags are enabled
             */
            ModuleManagementTool mmt = new ModuleManagementTool();
            mmt.setVerbose(true);
            getLogger().info("getDestFile ():" + getDestFile());
            getLogger().info("getDestFile ():" + getDestFile());
            getLogger().info("getDestDirectory ():" + getDestDirectory());
            
            File destLocation = (getDestFile() == null || !getDestFile().exists() ) ? getDestDirectory() : getDestFile();
            
            getLogger().info("Installing " + getSourceFile() + " into " + destLocation);
            try {
                mmt.installModule(
                        getSourceFile().getAbsolutePath(),
                        destLocation.getAbsolutePath(),
                        false,  //preview
                        true,   //force install
                        false); //backup
            } catch (Exception e) {
                throw new MojoExecutionException("Problems while installing " + 
            getSourceFile().getAbsolutePath() + " onto " + destLocation.getAbsolutePath(), e);
            }
            getLogger().debug("MMT invocation for " +  getSourceFile().getAbsolutePath() + "complete");
        } catch (Exception e) {
            throw new ArchiverException("Error while expanding "
                    + getSourceFile().getAbsolutePath(), e);
        } finally {
      
        }
    }

}
