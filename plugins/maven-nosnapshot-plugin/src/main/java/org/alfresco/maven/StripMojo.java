package org.alfresco.maven;

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
 * Goal which pushes a stripped version number in the maven project properties,
 * removing the suffix after the configured separator into the project variable
 * noSnapshotVersion
 * 
 * @goal strip
 * @phase initialize
 * 
 */
public class StripMojo extends AbstractMojo {
	private static final String RESULT_PROPERTY_NAME = "noSnapshotVersion";
	
	/**
	 * Current version of the project
	 * 
	 * @parameter expression="${project.version}" default-value="3.0.0-SNAPSHOT"
	 * @required
	 */
	private String version;

	public MavenProject getProject() {
		return project;
	}

	/**
	 * The Maven project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 */
	private MavenProject project;

	/**
	 * The separator used to identify and strip the suffix
	 * 
	 * @parameter expression="${separator}" default-value="-"
	 * @required
	 */
	private String separator;
	
	/**
	 * The Maven Projec property the stripped version is pushed into
	 * 
	 * @parameter expression="${propertyName}" default-value="noSnapshotVersion"
	 * @required
	 */
	private String propertyName;


	public void execute() throws MojoExecutionException {
		int separatorIndex = version.indexOf(separator);
		String noSnapshotVersion = version;
		if (separatorIndex > -1) {
			noSnapshotVersion = version.substring(0, separatorIndex);
		}
		getLog().info(
				MessageFormat.format("Storing " + propertyName
						+ ": {0} ", new Object[] { noSnapshotVersion }));
		project.getProperties().put(propertyName, noSnapshotVersion);
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
