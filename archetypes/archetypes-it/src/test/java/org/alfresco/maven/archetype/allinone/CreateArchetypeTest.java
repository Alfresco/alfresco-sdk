package org.alfresco.maven.archetype.allinone;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CreateArchetypeTest {

    public static final File ROOT = new File("target/test-classes/");

    @Before
    public void setUp() throws VerificationException, IOException {
        Verifier verifier;

        /*
         * We must first make sure that any artifact created
         * by this test has been removed from the local
         * repository. Failing to do this could cause
         * unstable test results. Fortunately, the verifier
         * makes it easy to do this.
         */
        verifier = new Verifier(ROOT.getAbsolutePath());
        // Deleting a former created artefact from the archetype to be tested
        verifier.deleteArtifact(Constants.TEST_GROUP_ID, Constants.TEST_ARTIFACT_ID, Constants.TEST_VERSION, null);

        // Delete the created maven project
        verifier.deleteDirectory(Constants.TEST_ARTIFACT_ID);
    }

    @Test
    public void testGenerateArchetype() throws Exception {
        Verifier verifier = new Verifier(ROOT.getAbsolutePath());
        verifier.setSystemProperties(Constants.getSystemProperties());
        verifier.setAutoclean(false);

        /*
         * The Command Line Options (CLI) are passed to the
         * verifier as a list.
         */
        verifier.executeGoal("archetype:generate");

        verifier.verifyErrorFreeLog();

        // Since creating the archetype was successful, we now want to actually build the generated project
        ProcessBuilder pb = new ProcessBuilder(ROOT.getAbsolutePath() + "/" + Constants.TEST_ARTIFACT_ID + "/" + "run.sh","test");
        pb.directory(new File(ROOT.getAbsolutePath() + "/" + Constants.TEST_ARTIFACT_ID));
        pb.redirectOutput(new File(ROOT.getAbsolutePath() + "/" + Constants.TEST_ARTIFACT_ID + "/" + "log.txt"));
        int exitvalue = pb.start().exitValue();
        System.out.println(exitvalue);

        verifier = new Verifier(ROOT.getAbsolutePath() + "/" + Constants.TEST_ARTIFACT_ID);

        verifier.setAutoclean(false);
        verifier.setLogFileName("log.txt");

        verifier.verifyErrorFreeLog();
    }
}
