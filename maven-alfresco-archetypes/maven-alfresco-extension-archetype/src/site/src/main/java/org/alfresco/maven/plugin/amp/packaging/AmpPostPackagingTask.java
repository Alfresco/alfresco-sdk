package org.alfresco.maven.plugin.amp.packaging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Defines tasks that should be performed after the packaging.
 *
 * @author Stephane Nicoll
 */
public interface AmpPostPackagingTask
{

    /**
     * Executes the post packaging task.
     * <p/>
     * The packaging context hold all information regarding the webapp that
     * has been packaged.
     *
     * @param context the packaging context
     * @throws MojoExecutionException if an error occured
     * @throws MojoFailureException   if a falure occured
     */
    void performPostPackaging( AmpPackagingContext context )
        throws MojoExecutionException, MojoFailureException;

}
