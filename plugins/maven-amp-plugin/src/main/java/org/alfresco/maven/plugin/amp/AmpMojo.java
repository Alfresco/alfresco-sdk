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

import org.alfresco.plexus.archiver.AmpArchiver;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;

/**
 * Build a amp/webapp.
 * <p>
 * Note this is a modification of Emmanuel Venisse's (evenisse@apache.org) WAR
 * Mojo and has been adapted to build an Alfresco AMP.
 * @version $Id:$
 * @goal amp
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class AmpMojo extends AbstractAmpMojo
{
    /* ====================================================================== */
    // constructors
    /* ====================================================================== */

    /**
     * default constructor
     */
    public AmpMojo()
    {
        this.setAmpArchiver(null);
        this.setAmpName(null);
        this.setOutputDirectory(null);
    }
    
    /* ====================================================================== */
    // properties
    /* ====================================================================== */

    /**
     * Whether this is the main artifact being built. Set to <code>false</code> if you don't want to install or
     * deploy it to the local repository instead of the default one in an execution.
     *
     * @parameter expression="${primaryArtifact}" default-value="true"
     */
    private boolean mPrimaryArtifact;

    /**
     * get the the internal value for the <code>ampArchiver</code> property.
     * <p>
     * The <code>ampArchiver</code> property 
     * @return Returns the internal value for the ampArchiver property.
     */
    protected AmpArchiver getAmpArchiver()
    {
        return this.mAmpArchiver;
    }

    /**
     * set the internal value for the <code>ampArchiver</code> property
     * @param pAmpArchiver The <code>ampArchiver</code> to set.
     */
    protected void setAmpArchiver(AmpArchiver pAmpArchiver)
    {
        this.mAmpArchiver = pAmpArchiver;
    }

    /**
     * get the the internal value for the <code>ampName</code> property.
     * <p>
     * The <code>ampName</code> property 
     * @return Returns the internal value for the ampName property.
     */
    protected String getAmpName()
    {
        return this.mAmpName;
    }

    /**
     * set the internal value for the <code>ampName</code> property
     * @param pAmpName The <code>ampName</code> to set.
     */
    protected void setAmpName(String pAmpName)
    {
        this.mAmpName = pAmpName;
    }

    /**
     * get the the internal value for the <code>outputDirectory</code> property.
     * <p>
     * The <code>outputDirectory</code> property 
     * @return Returns the internal value for the outputDirectory property.
     */
    protected String getOutputDirectory()
    {
        return this.mOutputDirectory;
    }

    /**
     * set the internal value for the <code>outputDirectory</code> property
     * @param pOutputDirectory The <code>outputDirectory</code> to set.
     */
    protected void setOutputDirectory(String pOutputDirectory)
    {
        this.mOutputDirectory = pOutputDirectory;
    }

    /**
     * get the the internal value for the <code>primaryArtifact</code> property.
     * <p>
     * The <code>primaryArtifact</code> property 
     * @return Returns the internal value for the primaryArtifact property.
     */
    protected boolean isPrimaryArtifact()
    {
        return this.mPrimaryArtifact;
    }

    /**
     * set the internal value for the <code>primaryArtifact</code> property
     * @param pPrimaryArtifact The <code>primaryArtifact</code> to set.
     */
    protected void setPrimaryArtifact(boolean pPrimaryArtifact)
    {
        this.mPrimaryArtifact = pPrimaryArtifact;
    }

    /**
     * get the the internal value for the <code>projectHelper</code> property.
     * <p>
     * The <code>projectHelper</code> property 
     * @return Returns the internal value for the projectHelper property.
     */
    protected MavenProjectHelper getProjectHelper()
    {
        return this.mProjectHelper;
    }

    /**
     * set the internal value for the <code>projectHelper</code> property
     * @param pProjectHelper The <code>projectHelper</code> to set.
     */
    protected void setProjectHelper(MavenProjectHelper pProjectHelper)
    {
        this.mProjectHelper = pProjectHelper;
    }

    /**
     * Overload this to produce a test-war, for example.
     */
    protected String getClassifier()
    {
        return mClassifier;
    }
    
    /**
     * set the internal value for the <code>classifier</code> property
     * @param pClassifier The <code>classifier</code> to set.
     */
    protected void setClassifier(String pClassifier)
    {
        this.mClassifier = pClassifier;
    }

    /* ====================================================================== */
    // public methods
    /* ====================================================================== */

    /**
     * Executes the WarMojo on the current project.
     *
     * @throws MojoExecutionException if an error occured while building the webapp
     */
    public void execute()
    throws MojoExecutionException, 
           MojoFailureException
    {

        File vAmpFile = AmpMojo.getAmpFile(new File( getOutputDirectory() ), getAmpName(), getClassifier());

        try
        {
            this.performPackaging(vAmpFile);
        }
        catch (Exception eAssemblyFailure)
        {
            /* behavior is the same for the following exceptions:
             *    DependencyResolutionRequiredException
             *    ManifestException
             *    IOException
             *    ArchiverException
             */
            throw new MojoExecutionException( "Error assembling AMP: " + eAssemblyFailure.getMessage(), eAssemblyFailure );
        }
    }

    /* ====================================================================== */
    // protected methods
    /* ====================================================================== */


    /**
     * composes the full file name for the AMP and gets a file handle for that file
     * TODO: what happens when nulls are passed in
     * TODO: what does a null response mean?
     * @param pBasedir  Base directory for AMP
     * @param pFinalName Final Name of AMP
     * @param pClassifier TODO: fill this in
     */
    protected static File getAmpFile( File pBasedir, String pFinalName, String pClassifier )
    {
        String vClassifier = pClassifier;

        if (vClassifier == null)
        {
            vClassifier = "";
        }
        else if (vClassifier.trim().length() > 0 && !vClassifier.startsWith( "-" ) )
        {
            vClassifier = "-" + vClassifier;
        }

        return new File(pBasedir, pFinalName + vClassifier + ".amp" );
    }


    /**
     * Generates the webapp according to the <tt>mode</tt> attribute.
     *
     * @param pAmpFile the target AMP file
     * @throws IOException
     * @throws ArchiverException
     * @throws ManifestException
     * @throws DependencyResolutionRequiredException
     *
     */
    protected void performPackaging(File pAmpFile)
    throws IOException, 
           ArchiverException, 
           ManifestException,
            DependencyResolutionRequiredException,
           MojoExecutionException, MojoFailureException
    {
        getLog().info( "Packaging Alfresco AMP (" + this.getAmpName() + ")" );

        
        this.buildExplodedAmp(this.getAmpDirectory());

        /* create and setup an archiver */
        MavenArchiver vArchiver = new MavenArchiver();
        vArchiver.setArchiver(this.getAmpArchiver());
        vArchiver.setOutputFile(pAmpFile);

        /* setup amp Archiver */
        this.getAmpArchiver().addDirectory(this.getAmpDirectory(), this.getIncludes(), this.getExcludes());

        // create archive
        vArchiver.createArchive(this.getProject(), archive );

        String vClassifier = this.getClassifier();
        
        if ( vClassifier != null )
        {
            this.getProjectHelper().attachArtifact( this.getProject(), "amp", vClassifier, pAmpFile );
        }
        else
        {
            Artifact vArtifact = this.getProject().getArtifact();
            
            if ( this.isPrimaryArtifact() )
            {
                vArtifact.setFile(pAmpFile);
            }
            else if(vArtifact.getFile() == null || vArtifact.getFile().isDirectory() )
            {
                vArtifact.setFile(pAmpFile);
            }
        }
    }


    /* ====================================================================== */
    // member fields
    /* ====================================================================== */
    
    /**
     * The directory for the generated AMP.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String mOutputDirectory;

    /**
     * The name of the generated AMP.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String mAmpName;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String mClassifier;

    /**
     * The AMP archiver.
     * @component role="${component.org.codehaus.plexus.archiver.Archiver}" roleHint="amp"
     * @required
     */
    private AmpArchiver mAmpArchiver;


    /**
     * @component
     */
    private MavenProjectHelper mProjectHelper;
}

