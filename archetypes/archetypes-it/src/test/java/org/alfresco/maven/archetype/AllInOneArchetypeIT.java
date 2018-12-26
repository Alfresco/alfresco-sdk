package org.alfresco.maven.archetype;

import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * Integration tests for the all-in-one archetype.
 */
public class AllInOneArchetypeIT extends AbstractArchetypeIT {

    @Override
    protected ArchetypeProperties createArchetypeProperties() {
        return ArchetypeProperties.builder()
                .withArchetypeGroupId("org.alfresco.maven.archetype")
                .withArchetypeArtifactId("alfresco-allinone-archetype")
                .withArchetypeVersion("4.0.0-SNAPSHOT")
                .withProjectGroupId("archetype.it")
                .withProjectArtifactId("allinone-test-run")
                .withProjectVersion("0.1-SNAPSHOT")
                .build();
    }

    @Test
    public void whenGenerateProjectFromArchetypeThenAProperProjectIsCreated() throws Exception {

        generateProjectFromArchetype();

        // Since creating the archetype was successful, we now want to actually build the generated project executing the integration tests
        // Execute a purge to ensure old data don't make the test fail
        ProcessBuilder purge = getProcessBuilder("purge");
        purge.start().waitFor();
        ProcessBuilder pb = getProcessBuilder("build_test");
        pb.start().waitFor();

        // Verify the execution of the integration tests of the project were successful
        Verifier verifier = new Verifier(projectPath);
        verifier.setAutoclean(false);
        verifier.setLogFileName(LOG_FILENAME);
        verifier.verifyErrorFreeLog();
        verifier.verifyTextInLog("Tests run: 5, Failures: 0, Errors: 0, Skipped: 0");
    }
}
