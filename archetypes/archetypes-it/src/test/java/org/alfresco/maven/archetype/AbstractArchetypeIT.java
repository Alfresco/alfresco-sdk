package org.alfresco.maven.archetype.allinone;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public abstract class AbstractArchetypeIT {

    protected static final File ROOT = new File("target/test-classes/");
    protected static final String LOG_FILENAME = "log.txt";
    protected static final String ERROR_FILENAME = "error.txt";
    protected static final String LINUX_EXEC = "run.sh";
    protected static final String WINDOWS_EXEC = "run.bat";

    protected ArchetypeProperties archetypeProperties;
    protected String projectPath;

    @Before
    public void setUp() throws VerificationException, IOException {
        archetypeProperties = createArchetypeProperties();
        projectPath = ROOT.getAbsolutePath() + File.separator + archetypeProperties.getProjectArtifactId();

        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        // Deleting a former created artifact from the archetype to be tested
        verifier.deleteArtifact(archetypeProperties.getProjectGroupId(), archetypeProperties.getProjectArtifactId(), archetypeProperties.getProjectVersion(), null);
        // Delete the created maven project
        verifier.deleteDirectory(archetypeProperties.getProjectArtifactId());
    }

    protected abstract ArchetypeProperties createArchetypeProperties();

    protected void generateProject() throws Exception {
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.setSystemProperties(archetypeProperties.getSystemProperties());
        verifier.setAutoclean(false);
        verifier.executeGoal("archetype:generate");
        verifier.verifyErrorFreeLog();
    }

    protected ProcessBuilder getProcessBuilder() {
        ProcessBuilder pb = new ProcessBuilder(getCommand(),"build_test");
        pb.directory(new File(projectPath));
        pb.redirectOutput(new File(projectPath + File.separator + LOG_FILENAME));
        pb.redirectError(new File(projectPath + File.separator + ERROR_FILENAME));
        return pb;
    }

    private String getCommand() {
        return projectPath + File.separator + (System.getProperty( "os.name" ).startsWith( "Windows" ) ? WINDOWS_EXEC : LINUX_EXEC);
    }
}
