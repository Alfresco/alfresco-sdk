package org.alfresco.maven.plugin.amp;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.alfresco.maven.plugin.amp.overlay.OverlayManager;
import org.alfresco.maven.plugin.amp.packaging.AmpPackagingContext;
import org.alfresco.maven.plugin.amp.packaging.AmpPackagingTask;
import org.alfresco.maven.plugin.amp.packaging.AmpPostPackagingTask;
import org.alfresco.maven.plugin.amp.packaging.AmpProjectPackagingTask;
import org.alfresco.maven.plugin.amp.packaging.OverlayPackagingTask;
import org.alfresco.maven.plugin.amp.packaging.SaveAmpStructurePostPackagingTask;
import org.alfresco.maven.plugin.amp.util.AmpStructure;
import org.alfresco.maven.plugin.amp.util.AmpStructureSerializer;
import org.alfresco.maven.plugin.amp.util.CompositeMap;
import org.alfresco.maven.plugin.amp.util.PropertyUtils;
import org.alfresco.maven.plugin.amp.util.ReflectionProperties;


import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractAmpMojo extends AbstractMojo
{

    /**
     * Returns a string array of the classes and resources to be excluded from the jar excludes  to be used
     * when assembling/copying the AMP.
     *
     * @return an array of tokens to exclude
     */
    protected String[] getExcludes()
    {
        List excludeList = new ArrayList();
        if ( StringUtils.isNotEmpty( mAmpJarExcludes ) )
        {
            excludeList.addAll( Arrays.asList( StringUtils.split( mAmpJarExcludes, "," ) ) );
        }

        return (String[]) excludeList.toArray( EMPTY_STRING_ARRAY );
    }

    /**
     * Returns a string array  of the classes and resources to be included from the jar assembling/copying the war.
     *
     * @return an array of tokens to include
     */
    protected String[] getIncludes()
    {
        return StringUtils.split( StringUtils.defaultString( mAmpJarIncludes ), "," );
    }
    
    /**
   	 * Returns a string array  of the resources to be included in the AMP web/ folder.
     *
     * @return an array of tokens to include
     */
    protected String[] getWebIncludes()
    {
        return StringUtils.split( StringUtils.defaultString( mAmpWebIncludes ), "," );
    }
    
    /**
   	 * Returns a string array  of the resources to be excluded in the AMP web/ folder.
   	 *  
     * @return an array of tokens to exclude
     */
    protected String[] getWebExcludes()
    {
    	List excludeList = new ArrayList();
        if ( StringUtils.isNotEmpty( mAmpWebExcludes ) )
        {
            excludeList.addAll( Arrays.asList( StringUtils.split( mAmpWebExcludes, "," ) ) );
        }

        return (String[]) excludeList.toArray( EMPTY_STRING_ARRAY );

    }
    

    /**
     * Returns a string array of the excludes to be used
     * when adding dependent AMPs as an overlay onto this AMP.
     *
     * @return an array of tokens to exclude
     */
    protected String[] getDependentAmpExcludes()
    {
        String[] excludes;
        if ( StringUtils.isNotEmpty( dependentAmpExcludes ) )
        {
            excludes = StringUtils.split( dependentAmpExcludes, "," );
        }
        else
        {
            excludes = EMPTY_STRING_ARRAY;
        }
        return excludes;
    }

    /**
     * Returns a string array of the includes to be used
     * when adding dependent AMP as an overlay onto this AMP.
     *
     * @return an array of tokens to include
     */
    protected String[] getDependentAmpIncludes()
    {
        return StringUtils.split( StringUtils.defaultString( dependentAmpIncludes ), "," );
    }

    public void buildExplodedAmp( File webappDirectory )
        throws MojoExecutionException, MojoFailureException
    {
        webappDirectory.mkdirs();

        try
        {
            buildAmp( mProject, webappDirectory );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not build AMP", e );
        }
    }


    /**
     * Builds the webapp for the specified project with the new packaging task
     * thingy
     * <p/>
     * Classes, libraries and tld files are copied to
     * the <tt>webappDirectory</tt> during this phase.
     *
     * @param project         the maven project
     * @param webappDirectory the target directory
     * @throws MojoExecutionException if an error occured while packaging the webapp
     * @throws MojoFailureException   if an unexpected error occured while packaging the webapp
     * @throws IOException            if an error occured while copying the files
     */
    public void buildAmp( MavenProject project, File webappDirectory )
        throws MojoExecutionException, MojoFailureException, IOException
    {

        AmpStructure cache;
        if ( mUseCache && mCacheFile.exists() )
        {
            cache = new AmpStructure( webappStructureSerialier.fromXml( mCacheFile ) );
        }
        else
        {
            cache = new AmpStructure( null );
        }

        final long startTime = System.currentTimeMillis();
        getLog().info( "Assembling AMP [" + project.getArtifactId() + "] in [" + webappDirectory + "]" );

        final OverlayManager overlayManager =
            new OverlayManager( mOverlays, project, dependentAmpIncludes, dependentAmpExcludes );
        final List packagingTasks = getPackagingTasks( overlayManager );
        final AmpPackagingContext context = new DefaultAmpPackagingContext( webappDirectory, cache, overlayManager );
        final Iterator it = packagingTasks.iterator();
        while ( it.hasNext() )
        {
            AmpPackagingTask ampPackagingTask = (AmpPackagingTask) it.next();
            ampPackagingTask.performPackaging( context );
        }

        // Post packaging
        final List postPackagingTasks = getPostPackagingTasks();
        final Iterator it2 = postPackagingTasks.iterator();
        while ( it2.hasNext() )
        {
            AmpPostPackagingTask task = (AmpPostPackagingTask) it2.next();
            task.performPostPackaging( context );

        }
        getLog().info( "AMP assembled in[" + ( System.currentTimeMillis() - startTime ) + " msecs]" );

    }

    /**
     * Returns a <tt>List</tt> of the {@link org.alfresco.maven.plugin.amp.packaging.AmpPackagingTask}
     * instances to invoke to perform the packaging.
     *
     * @param overlayManager the overlay manager
     * @return the list of packaging tasks
     * @throws MojoExecutionException if the packaging tasks could not be built
     */
    private List getPackagingTasks( OverlayManager overlayManager )
        throws MojoExecutionException
    {
        final List packagingTasks = new ArrayList();
        final List resolvedOverlays = overlayManager.getOverlays();
        final Iterator it = resolvedOverlays.iterator();
        while ( it.hasNext() )
        {
            Overlay overlay = (Overlay) it.next();
            if ( overlay.isCurrentProject() )
            {
                packagingTasks.add( new AmpProjectPackagingTask( mAmpResources, mModuleProperties) );
            }
            else
            {
                packagingTasks.add( new OverlayPackagingTask( overlay ) );
            }
        }
        return packagingTasks;
    }


    /**
     * Returns a <tt>List</tt> of the {@link org.alfresco.maven.plugin.amp.packaging.AmpPostPackagingTask}
     * instances to invoke to perform the post-packaging.
     *
     * @return the list of post packaging tasks
     */
    private List getPostPackagingTasks()
    {
        final List postPackagingTasks = new ArrayList();
        if ( mUseCache )
        {
            postPackagingTasks.add( new SaveAmpStructurePostPackagingTask( mCacheFile ) );
        }
        // TODO add lib scanning to detect duplicates
        return postPackagingTasks;
    }

    
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mProject;

    /**
     * The directory containing generated classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File mClassesDirectory;
    

    /**
     * The Jar archiver needed for archiving classes directory into jar file under WEB-INF/lib.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" role-hint="jar"
     * @required
     */
    private JarArchiver mJarArchiver;
    

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    private File mAmpDirectory;

    /**
     * Single directory for extra files to include in the AMP.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File mAmpConfigDirectory;

    /**
     * Single directory for extra files to include in the AMP.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File mAmpWebDirectory;

    /**
     * The list of webResources we want to transfer.
     *
     * @parameter
     */
    private Resource[] mAmpResources;

    /**
     * Filters (property files) to include during the interpolation of the pom.xml.
     *
     * @parameter expression="${project.build.filters}"
     */
    private List filters;

    /**
     * The path to the web.xml file to use.
     *
     * @parameter expression="${maven.amp.moduleProperties}" default-value="${project.basedir}/module.properties"
     */
    private File mModuleProperties;


    /**
     * Directory to unpack dependent AMPs into if needed
     *
     * @parameter expression="${project.build.directory}/amp/work"
     * @required
     */
    private File mWorkDirectory;

    /**
     * The file name mapping to use to copy libraries and tlds. If no file mapping is
     * set (default) the file is copied with its standard name.
     *
     * @parameter
     * @since 2.0.3
     */
    private String mOutputFileNameMapping;

    /**
     * The file containing the webapp structure cache.
     *
     * @parameter expression="${project.build.directory}/amp/work/amp-cache.xml"
     * @required
     * @since 2.1
     */
    private File mCacheFile;

    /**
     * Whether the cache should be used to save the status of the webapp
     * accross multiple runs.
     *
     * @parameter expression="${useCache}" default-value="true"
     * @since 2.1
     */
    private boolean mUseCache = true;

    
    
    /**
     * To look up Archiver/UnArchiver implementations
     *
     * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
     * @required
     */
    protected ArchiverManager mArchiverManager;

    private static final String META_INF = "META-INF";

    public static final String DEFAULT_FILE_NAME_MAPPING_CLASSIFIER =
        "${artifactId}-${version}-${classifier}.${extension}";

    public static final String DEFAULT_FILE_NAME_MAPPING = "${artifactId}-${version}.${extension}";

    /**
     * The comma separated list of tokens to include in the AMP internal JAR. Default **.
     * Default is '**'.
     *
     * @parameter alias="includes"
     */
    private String mAmpJarIncludes = "**";

    /**
     * The comma separated list of tokens to exclude from the AMP created JAR file. By default module configuration is left outside jars.
     *
     * @parameter alias="excludes" default-value="alfresco/module/**"
     */
    private String mAmpJarExcludes;
    
    
    /**
     * The comma separated list of tokens to include in the AMP internal JAR. Default **.
     * Default is '**'.
     *
     * @parameter alias="webIncludes" default-value="**"
     */
    private String mAmpWebIncludes;

    /**
     * The comma separated list of tokens to exclude from the AMP created JAR file. By default module configuration is left outside jars.
     *
     * @parameter alias="webExcludes"
     */
    private String mAmpWebExcludes;

    /**
     * The comma separated list of tokens to include when doing
     * a AMP overlay.
     * Default is '**'
     *
     * @parameter
     */
    private String dependentAmpIncludes = "**/**";

    /**
     * The comma separated list of tokens to exclude when doing
     * a AMP overlay.
     *
     * @parameter
     */
    private String dependentAmpExcludes = "META-INF/**";

    /**
     * The overlays to apply.
     *
     * @parameter
     * @since 2.1
     */
    private List mOverlays = new ArrayList();

    /**
     * The maven archive configuration to use.
     *
     * @parameter
     */
    protected MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    private static final String[] EMPTY_STRING_ARRAY = {};

    private final AmpStructureSerializer webappStructureSerialier = new AmpStructureSerializer();
  
    
    
        
    // AMP packaging implementation

    private class DefaultAmpPackagingContext
        implements AmpPackagingContext
    {


        private final AmpStructure webappStructure;

        private final File mAmpDirectory;

        private final OverlayManager overlayManager;

        public DefaultAmpPackagingContext( File webappDirectory, final AmpStructure webappStructure,
                                           final OverlayManager overlayManager )
        {
            this.mAmpDirectory = webappDirectory;
            this.webappStructure = webappStructure;
            this.overlayManager = overlayManager;

            // This is kinda stupid but if we loop over the current overlays and we request the path structure
            // it will register it. This will avoid wrong warning messages in a later phase
            final Iterator it = overlayManager.getOverlayIds().iterator();
            while ( it.hasNext() )
            {
                String overlayId = (String) it.next();
                webappStructure.getStructure( overlayId );
            }
        }

        public MavenProject getProject()
        {
            return mProject;
        }

        public File getAmpDirectory()
        {
            return mAmpDirectory;
        }

        public File getClassesDirectory()
        {
            return mClassesDirectory;
        }

        public Log getLog()
        {
            return AbstractAmpMojo.this.getLog();
        }

        public String getOutputFileNameMapping()
        {
            return mOutputFileNameMapping;
        }

        public File getAmpWebDirectory()
        {
            return mAmpWebDirectory;
        }

        public String[] getAmpJarIncludes()
        {
            return getIncludes();
        }

        public String[] getAmpJarExcludes()
        {
            return getExcludes();
        }

        public File getOverlaysWorkDirectory()
        {
            return mWorkDirectory;
        }

        public ArchiverManager getArchiverManager()
        {
            return mArchiverManager;
        }

        public MavenArchiveConfiguration getArchive()
        {
            return archive;
        }

        public JarArchiver getJarArchiver()
        {
            return mJarArchiver;
        }

        public List getFilters()
        {
            return filters;
        }

        public Map getFilterProperties()
            throws MojoExecutionException
        {
            Map filterProperties = new Properties();

            // System properties
            filterProperties.putAll( System.getProperties() );

            // Project properties
            filterProperties.putAll( mProject.getProperties() );

            for ( Iterator i = filters.iterator(); i.hasNext(); )
            {
                String filtersfile = (String) i.next();

                try
                {
                    Properties properties = PropertyUtils.loadPropertyFile( new File( filtersfile ), true, true );

                    filterProperties.putAll( properties );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Error loading property file '" + filtersfile + "'", e );
                }
            }

            // can't putAll, as ReflectionProperties doesn't enumerate - so we make a composite map with the project variables as dominant
            return new CompositeMap( new ReflectionProperties( mProject ), filterProperties );
        }

        public AmpStructure getAmpStructure()
        {
            return webappStructure;
        }

        public List getOwnerIds()
        {
            return overlayManager.getOverlayIds();
        }

        /**
         * @see org.alfresco.maven.plugin.amp.packaging.AmpPackagingContext#getAmpConfigDirectory()
         */
        public File getAmpConfigDirectory()
        {
            return mAmpConfigDirectory;
        }

		public String[] getAmpWebExcludes() {
			return getWebExcludes();
		}

		public String[] getAmpWebIncludes() {
			return getWebIncludes();
		}

    }

    public MavenProject getProject()
    {
        return mProject;
    }

    public void setProject( MavenProject project )
    {
        this.mProject = project;
    }

    public File getClassesDirectory()
    {
        return mClassesDirectory;
    }

    public void setClassesDirectory( File classesDirectory )
    {
        this.mClassesDirectory = classesDirectory;
    }

    public File getAmpDirectory()
    {
        return mAmpDirectory;
    }

    public void setAmpDirectory( File webappDirectory )
    {
        this.mAmpDirectory = webappDirectory;
    }

    public File getAmpSourceDirectory()
    {
        return mAmpWebDirectory;
    }

    public void setAmpSourceDirectory( File ampSourceDirectory )
    {
        this.mAmpWebDirectory = ampSourceDirectory;
    }

    public File getWebXml()
    {
        return mModuleProperties;
    }

    public void setWebXml( File webXml )
    {
        this.mModuleProperties = webXml;
    }

   
    public String getOutputFileNameMapping()
    {
        return mOutputFileNameMapping;
    }

    public void setOutputFileNameMapping( String outputFileNameMapping )
    {
        this.mOutputFileNameMapping = outputFileNameMapping;
    }

    public List getOverlays()
    {
        return mOverlays;
    }

    public void setOverlays( List overlays )
    {
        this.mOverlays = overlays;
    }

    public void addOverlay( Overlay overlay )
    {
        mOverlays.add( overlay );
    }

 
    public JarArchiver getJarArchiver()
    {
        return mJarArchiver;
    }

    public void setJarArchiver( JarArchiver jarArchiver )
    {
        this.mJarArchiver = jarArchiver;
    }

    public Resource[] getAmpResources()
    {
        return mAmpResources;
    }

    public void setAmpResources( Resource[] webResources )
    {
        this.mAmpResources = webResources;
    }

    public List getFilters()
    {
        return filters;
    }

    public void setFilters( List filters )
    {
        this.filters = filters;
    }

    public File getWorkDirectory()
    {
        return mWorkDirectory;
    }

    public void setWorkDirectory( File workDirectory )
    {
        this.mWorkDirectory = workDirectory;
    }

    public File getCacheFile()
    {
        return mCacheFile;
    }

    public void setCacheFile( File cacheFile )
    {
        this.mCacheFile = cacheFile;
    }

    public void setAmpSourceIncludes( String ampSourceIncludes )
    {
        this.mAmpJarIncludes = ampSourceIncludes;
    }

    public String getAmpJarExcludes()
    {
        return mAmpJarExcludes;
    }

    public void setAmpJarExcludes( String ampJarExcludes )
    {
        this.mAmpJarExcludes = ampJarExcludes;
    }


    public boolean isUseCache()
    {
        return mUseCache;
    }

    public void setUseCache( boolean useCache )
    {
        this.mUseCache = useCache;
    }
}
