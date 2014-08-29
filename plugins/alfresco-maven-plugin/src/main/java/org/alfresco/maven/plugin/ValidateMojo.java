package org.alfresco.maven.plugin;

import java.io.File;

import org.alfrescolabs.technical.validation.AlfrescoTechnicalValidation;
import org.alfrescolabs.technical.validation.impl.AlfrescoTechnicalValidationImpl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * This goal provides the EXPERIMENTAL feature of validating your AMP module (source and binaries)
 * against Alfresco (Repository and Share) development best practices, providing validation and 
 * alerts that can be used to improve quality, longevity and supportability of your code.
 * 
 * It uses the ATV (Alfresco Technical Validation https://github.com/AlfrescoLabs/technical-validation)
 *
 * @version $Id:$
 * @goal validate
 * @phase verify
 * @requiresProject
 * @since 2.0.0-beta-2
 * @threadSafe
 * @description Invokes the Alfresco Technical Validation (https://github.com/AlfrescoLabs/technical-validation) of your customization 
 */
public class ValidateMojo extends AbstractMojo {

    /**
     * This parameter skips validation (the feature is experimental so disabled by default)
     *
     * @parameter property="maven.alfresco.validation.skip" default-value="true"
     * @required
     */
    protected boolean skip;
    //
    
    /**
     * The directory where the source project is stored. Should not include /target otherwise checks will be duplicated.
     *
     * @parameter property="maven.alfresco.validation.sourceLocation" default-value="${project.build.directory}/${project.build.finalName}-src"
     * @required
     */
    protected String sourceLocation;
    
    /**
     * The directory where the binary AMP package is to be found
     *
     * @parameter property="maven.alfresco.validation.binaryLocation" default-value="${project.build.directory}/${project.build.finalName}.amp"
     * @required
     */
    protected String binaryLocation;
    
    /**
     * The directory where the binary AMP package is to be found
     *
     * @parameter property="maven.alfresco.validation.neo4jUrl" default-value="http://localhost:7474/db/data/"
     * @required
     */
	protected String neo4jUrl;

    /**
     * [Read Only] The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    	if(!skip)
    	{
    		AlfrescoTechnicalValidation atv = new AlfrescoTechnicalValidationImpl();
    		atv.validate(sourceLocation, binaryLocation, neo4jUrl);
    	}
    }
}
