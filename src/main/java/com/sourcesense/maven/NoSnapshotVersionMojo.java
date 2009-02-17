package com.sourcesense.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.text.MessageFormat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which pushes a stripped version number in the maven project properties (any -SNAPSHOT suffix is removed)
 *
 * @goal strip
 * @phase initialize
 * 
 */
public class NoSnapshotVersionMojo
    extends AbstractMojo
{
    /**
     * Current version of the project
     * @parameter expression="${project.version}"
     * @required
     */
    private String version;
    
    /**
    * The Maven project
    * @parameter expression="${project}"
    * @required
    */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
    	int indexOfDash = -1;
    	String noSnapshotVersion = version;
    	if((indexOfDash = version.indexOf("-SNAPSHOT")) != -1)
    	{
    		noSnapshotVersion = version.substring(0, indexOfDash);
    	}
        getLog().info(
                MessageFormat.format( "Storing noSnapshotVersion: {0} ", new Object[] {
                    noSnapshotVersion} ) );
    	project.getProperties().put("noSnapshotVersion", noSnapshotVersion);
    }
}
