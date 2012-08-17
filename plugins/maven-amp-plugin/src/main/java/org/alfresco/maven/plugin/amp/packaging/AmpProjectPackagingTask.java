package org.alfresco.maven.plugin.amp.packaging;

import org.alfresco.maven.plugin.amp.Overlay;
import org.alfresco.maven.plugin.amp.util.PathSet;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles the project own resources, that is:
 * <ul
 * <li>The list of web resources, if any</li>
 * <li>The content of the webapp directory if it exists</li>
 * <li>The custom deployment descriptor(s), if any</li>
 * <li>The content of the classes directory if it exists</li>
 * <li>The dependencies of the project</li>
 * </ul>
 *
 * @author Stephane Nicoll
 */
public class AmpProjectPackagingTask
    extends AbstractAmpPackagingTask
{
    private static final String MODULE_PROPERTIES = "module.properties";
    private static final String FILE_MAPPING     = "file-mapping.properties";

	private static final String WEB_PATH = "web/";

	private static final String CONFIG_PATH = "config/";
	
	private static final String FILE_MAPPING_CONTENT = "# Add mapping for /WEB-INF/, since MMT doesn't support it by default\n" +
                                                        "/web/WEB-INF=/WEB-INF\n";


	private Resource[] webResources = new Resource[0];

    private final File moduleProperties;
    
    private final String id;


    public AmpProjectPackagingTask( Resource[] webResources, File moduleProperties)
    {
        if ( webResources != null )
        {
            this.webResources = webResources;
        }
        this.moduleProperties = moduleProperties;
        this.id = Overlay.currentProjectInstance().getId();
    }

    public void performPackaging( AmpPackagingContext context )
        throws MojoExecutionException, MojoFailureException
    {

        context.getLog().info( "Processing amp project" );

        File metainfDir = new File( context.getAmpDirectory(), META_INF_PATH );
        
        metainfDir.mkdirs();

        handleArtifacts( context );
        
        handleWebResources( context );
        
        handleClassesDirectory( context );

        handeAmpConfigDirectory( context );

        handeWebAppSourceDirectory( context );
        
        synthesiseFileMapping(context);
        
        
        // Notice: this will work only in case we are copying only this AMP or this AMP is
        // set as last overlay of the maven-amp-plugin
        handleDeploymentDescriptors(context);
        
    }


	/**
     * Handles the web resources.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if a resource could not be copied
     */
    protected void handleWebResources( AmpPackagingContext context )
        throws MojoExecutionException
    {
        for ( int i = 0; i < webResources.length; i++ )
        {
            Resource resource = webResources[i];
            if ( !( new File( resource.getDirectory() ) ).isAbsolute() )
            {
                resource.setDirectory( context.getProject().getBasedir() + File.separator + resource.getDirectory() );
            }

            // Make sure that the resource directory is not the same as the webappDirectory
            if ( !resource.getDirectory().equals( context.getAmpDirectory().getPath() ) )
            {

                try
                {
                    copyResources( context, resource );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Could not copy resource[" + resource.getDirectory() + "]", e );
                }
            }
        }
    }

    /**
     * Handles the webapp sources.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if the sources could not be copied
     */
    protected void handeWebAppSourceDirectory( AmpPackagingContext context )
        throws MojoExecutionException
    {
        if ( !context.getAmpWebDirectory().exists() )
        {
            context.getLog().debug( "AMP sources directory does not exist - skipping." );
        }
        else
        if ( !context.getAmpWebDirectory().getAbsolutePath().equals( context.getAmpDirectory().getPath() ) )
        {
            final PathSet sources = getFilesToIncludes( context.getAmpWebDirectory(),
                                                        context.getAmpWebIncludes(),
                                                        context.getAmpWebExcludes() );

            try
            {
            	context.getLog().info("Copying AMP web data into " + WEB_PATH);
                copyFiles( id, context, context.getAmpWebDirectory(), sources, WEB_PATH );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException(
                    "Could not copy AMP sources[" + context.getAmpDirectory().getAbsolutePath() + "]", e );
            }
        }
    }
    
    
    /**
     * Synthesizes (creates) a standard file-mapping.properties file so that resources in webapp/WEB-INF
     * get written to the correct location by the MMT.
     * 
     * @param context The packaging context
     * @throws MojoExecutionException if the file could not be created
     */
    protected void synthesiseFileMapping(final AmpPackagingContext context)
        throws MojoExecutionException
    {
        try
        {
            File         fileMapping = new File(context.getAmpDirectory(), FILE_MAPPING);
            OutputStream out          = null;
            
            fileMapping.createNewFile();  // Note: ignore if the file already exists - we simply overwrite its existing contents
            
            try
            {
                out = new FileOutputStream(fileMapping, false);
                
                out.write(FILE_MAPPING_CONTENT.getBytes());
            }
            finally
            {
                if (out != null)
                {
                    out.flush();
                    out.close();
                }
            }
        }
        catch (final IOException ioe)
        {
            throw new MojoExecutionException("Could not create file-mapping.properties in [" + context.getAmpDirectory().getAbsolutePath() + "]", ioe);
        }
    }
    

    /**
     * Handles the webapp sources.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if the sources could not be copied
     */
    protected void handeAmpConfigDirectory( AmpPackagingContext context )
        throws MojoExecutionException
    {
        if ( !context.getAmpConfigDirectory().exists() )
        {
            context.getLog().debug( "AMP config directory does not exist - skipping." );
        }
        else
        {
            if ( !context.getAmpConfigDirectory().getAbsolutePath().equals( context.getAmpDirectory().getPath() ) )
            {
                final PathSet sources = getFilesToIncludes( context.getAmpConfigDirectory(),
                                                            new String[0],
                                                            new String[] {"**/*.class","META-INF/**"} );

                try
                {
                    context.getLog().info("packaging AMP config items into " + CONFIG_PATH);
                    copyFiles( id, context, context.getAmpConfigDirectory(), sources, CONFIG_PATH );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException(
                        "Could not copy AMP config [" + context.getAmpDirectory().getAbsolutePath() + "]", e );
                }
               
                
            }
            

        }
    }
    
    /**
     * Handles the webapp artifacts.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if the artifacts could not be packaged
     */
    protected void handleArtifacts( AmpPackagingContext context )
        throws MojoExecutionException
    {
        ArtifactsPackagingTask task = new ArtifactsPackagingTask( context.getProject().getArtifacts() );
        task.performPackaging( context );
    }

    /**
     * Handles the webapp classes.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if the classes could not be packaged
     */
    protected void handleClassesDirectory( AmpPackagingContext context )
        throws MojoExecutionException
    {
        ClassesPackagingTask task = new ClassesPackagingTask();
        task.performPackaging( context );
    }

    /**
     * Handles the deployment descriptors, if specified. Note that the behavior
     * here is slightly different since the customized entry always win, even if
     * an overlay has already packaged a web.xml previously.
     *
     * @param context    the packaging context
     * @param webinfDir  the web-inf directory
     * @param metainfDir the meta-inf directory
     * @throws MojoFailureException   if the web.xml is specified but does not exist
     * @throws MojoExecutionException if an error occured while copying the descriptors
     */
    protected void handleDeploymentDescriptors( AmpPackagingContext context)
        throws MojoFailureException, MojoExecutionException
    {
        try
        {
            if ( moduleProperties != null && StringUtils.isNotEmpty( moduleProperties.getName() ) )
            {
                if ( !moduleProperties.exists() )
                {
                    throw new MojoFailureException( "The specified module.properties file '" + moduleProperties + "' does not exist" );
                }
                
                if(new File(context.getAmpDirectory().getPath() + "/module.properties").exists())
                {
                	context.getLog().warn("A module.properties was already present, possibly due to previous overlay. Unexpected results may happen, check your target dir");
                }

                //rename to module.properties
                context.getLog().info("copying " +  moduleProperties + " into " + MODULE_PROPERTIES);
                copyFilteredFile(id, context, moduleProperties, MODULE_PROPERTIES);
            }

        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to copy deployment descriptor", e );
        }
    }


    /**
     * Copies webapp webResources from the specified directory.
     *
     * @param context  the war packaging context to use
     * @param resource the resource to copy
     * @throws IOException            if an error occured while copying the resources
     * @throws MojoExecutionException if an error occured while retrieving the filter properties
     */
    public void copyResources( AmpPackagingContext context, Resource resource )
        throws IOException, MojoExecutionException
    {
        if ( !context.getAmpDirectory().exists() )
        {
            context.getLog().warn( "Not copying AMP ampResources[" + resource.getDirectory() +
                "]: amp directory[" + context.getAmpDirectory().getAbsolutePath() + "] does not exist!" );
        }

        context.getLog().info( "Copy AMP ampResources[" + resource.getDirectory() + "] to[" +
            context.getAmpDirectory().getAbsolutePath() + "]" );
        String[] fileNames = getFilesToCopy( resource );
        for ( int i = 0; i < fileNames.length; i++ )
        {
            String targetFileName = fileNames[i];
            if ( resource.getTargetPath() != null )
            {
                //TODO make sure this thing is 100% safe
                targetFileName = resource.getTargetPath() + File.separator + targetFileName;
            }
            if ( resource.isFiltering() )
            {
                copyFilteredFile( id, context, new File( resource.getDirectory(), fileNames[i] ), targetFileName );
            }
            else
            {
                copyFile( id, context, new File( resource.getDirectory(), fileNames[i] ), targetFileName );
            }
        }
    }


    /**
     * Returns a list of filenames that should be copied
     * over to the destination directory.
     *
     * @param resource the resource to be scanned
     * @return the array of filenames, relative to the sourceDir
     */
    private String[] getFilesToCopy( Resource resource )
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( resource.getDirectory() );
        if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
        {
            scanner.setIncludes(
                (String[]) resource.getIncludes().toArray( new String[resource.getIncludes().size()] ) );
        }
        else
        {
            scanner.setIncludes( DEFAULT_INCLUDES );
        }
        if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
        {
            scanner.setExcludes(
                (String[]) resource.getExcludes().toArray( new String[resource.getExcludes().size()] ) );
        }

        scanner.addDefaultExcludes();

        scanner.scan();

        return scanner.getIncludedFiles();
    }
}
