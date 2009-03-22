package com.sourcesense.maven.plugins.test;


import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.sourcesense.maven.StripMojo;

public class StripTest extends org.apache.maven.plugin.testing.AbstractMojoTestCase {

	File testPom;
	MavenProject project = new MavenProject();
	
	protected void setUp() throws Exception {
		// required for mojo lookups to work
        super.setUp();

    }

    /**
     * tests the proper discovery and configuration of the mojo
     *
     * @throws Exception
     */
    public void testMojoDefaultEnvironment() throws Exception {
    	testPom = new File( getBasedir(), "target/test-classes/snapshot-pom.xml" );
        StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
        assertNotNull( mojo );
        
    }
	
	public void testSnapshotVersion() throws Exception {
		testPom = new File( getBasedir(), "target/test-classes/snapshot-pom.xml" );
        StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
        setVariableValueToObject(mojo, "version", "3.0.0-SNAPSHOT");
        setVariableValueToObject(mojo, "project", project);
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			fail("Mojo execution exception" + e);
		}
		assertEquals("3.0.0", mojo.getProject().getProperties().getProperty(
				mojo.getPropertyName()));
	}

	public void testReleaseCandidateVersion() throws Exception {
		testPom = new File( getBasedir(), "target/test-classes/relcandidate-pom.xml" );
        StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
        setVariableValueToObject(mojo, "version", "3.0.0-RC1");
        setVariableValueToObject(mojo, "project", project);
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			fail("Mojo execution exception" + e);
		}
		assertEquals("3.0.0", mojo.getProject().getProperties().getProperty(
				mojo.getPropertyName()));
	}

	public void testIdempotent() throws Exception {
		testPom = new File( getBasedir(), "target/test-classes/snapshot-pom.xml" );
		StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
		setVariableValueToObject(mojo, "version", "3.0.0");
		setVariableValueToObject(mojo, "project", project);
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			fail("Mojo execution exception" + e);
		}
		assertEquals("3.0.0", mojo.getProject().getProperties().getProperty(
				mojo.getPropertyName()));
	}

	public void testUnderscore() throws Exception {
		testPom = new File( getBasedir(), "target/test-classes/underscore-pom.xml" );
        StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
        setVariableValueToObject(mojo, "version", "3.0.0_RC1");
        setVariableValueToObject(mojo, "project", project);
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			fail("Mojo execution exception" + e);
		}
		assertEquals("3.0.0", mojo.getProject().getProperties().getProperty(
				mojo.getPropertyName()));
	}

	public void testCustomPropName() throws Exception {
		testPom = new File( getBasedir(), "target/test-classes/customprop-pom.xml" );
		StripMojo mojo = (StripMojo) lookupMojo ("strip", testPom );
		setVariableValueToObject(mojo, "version", "3.0.0-RC1");
		setVariableValueToObject(mojo, "project", project);
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			fail("Mojo execution exception" + e);
		}
		assertEquals("3.0.0", mojo.getProject().getProperties().getProperty(
				"foobar"));
	}

}
