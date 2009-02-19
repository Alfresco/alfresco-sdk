package org.alfresco.maven.plugin.amp.packaging;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.alfresco.maven.plugin.amp.Overlay;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;

/**
 * Handles the classes directory that needs to be packaged in the web application.
 * <p/>
 * Based on the {@link AmpPackagingContext#archiveClasses()} flag the resources
 * either copied into to <tt>WEB-INF/classes</tt> directory or archived in a jar
 * within the <tt>WEB-INF/lib</tt> directory.
 *
 * @author Stephane Nicoll
 */
public class ClassesPackagingTask
    extends AbstractAmpPackagingTask
{

    private static final String LIB_PATH = "lib/";

	public void performPackaging( AmpPackagingContext context )
        throws MojoExecutionException
    {

                /* AMP Files do not have a classes folder  */
                generateJarArchive( context );

    }

    protected void generateJarArchive( AmpPackagingContext context )
        throws MojoExecutionException
    {
        //TODO use ArtifactFactory and resolve the final name the usual way instead
        final String archiveName = context.getProject().getBuild().getFinalName() + ".jar";
        
        final String targetFilename = LIB_PATH + archiveName;

        if ( context.getAmpStructure().registerFile( Overlay.currentProjectInstance().getId(), targetFilename ) )
        {

            final File libDirectory = new File( context.getAmpDirectory(), LIB_PATH );
            final File jarFile = new File( libDirectory, archiveName );

            try
            {
                final MavenArchiver archiver = new MavenArchiver();
                archiver.setArchiver( context.getJarArchiver() );
                archiver.setOutputFile( jarFile );
                archiver.getArchiver().addDirectory( context.getClassesDirectory(), context.getAmpJarIncludes(),
                                                     context.getAmpJarExcludes());
                context.getLog().debug("Archiving AMP classes JAR with default excludes: " + context.getAmpJarExcludes());
                archiver.createArchive( context.getProject(), context.getArchive() );
            }
            catch ( ArchiverException e )
            {
                throw new MojoExecutionException( "Could not create classes archive", e );
            }
            catch ( ManifestException e )
            {
                throw new MojoExecutionException( "Could not create classes archive", e );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Could not create classes archive", e );
            }
            catch ( DependencyResolutionRequiredException e )
            {
                throw new MojoExecutionException( "Could not create classes archive", e );
            }
        }
        else
        {
            context.getLog().warn(
                "Could not generate archive classes file[" + targetFilename + "] has already been copied." );
        }
    }
}
