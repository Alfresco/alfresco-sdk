package org.alfresco.maven.plugin.amp.packaging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.alfresco.maven.plugin.amp.util.AmpStructureSerializer;

import java.io.File;
import java.io.IOException;

/**
 * Saves the webapp structure cache.
 *
 * @author Stephane Nicoll
 */
public class SaveAmpStructurePostPackagingTask
    implements AmpPostPackagingTask
{

    private final File targetFile;

    private final AmpStructureSerializer serialier;


    public SaveAmpStructurePostPackagingTask( File targetFile )
    {
        this.targetFile = targetFile;
        this.serialier = new AmpStructureSerializer();
    }

    public void performPostPackaging( AmpPackagingContext context )
        throws MojoExecutionException, MojoFailureException
    {
        if ( targetFile == null )
        {
            context.getLog().debug( "Cache usage is disabled, not saving webapp structure." );
        }
        else
        {
            try
            {
                serialier.toXml( context.getAmpStructure(), targetFile );
                context.getLog().debug( "Cache saved successfully." );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Could not save webapp structure", e );
            }
        }
    }
}
