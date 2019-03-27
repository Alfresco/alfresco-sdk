package org.alfresco.maven.archetype;

import org.apache.maven.it.Verifier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for the platform jar archetype.
 */
public class PlatformJarArchetypeIT extends AbstractArchetypeIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformJarArchetypeIT.class);

    @Override
    protected ArchetypeProperties createArchetypeProperties() {
        return ArchetypeProperties.builder()
                .withArchetypeGroupId("org.alfresco.maven.archetype")
                .withArchetypeArtifactId("alfresco-platform-jar-archetype")
                .withArchetypeVersion(System.getProperty("projectArtifactId"))
                .withProjectGroupId("archetype.it")
                .withProjectArtifactId("repojar-test-run")
                .withProjectVersion("0.1-SNAPSHOT")
                .build();
    }

    @Test
    public void whenGenerateProjectFromArchetypeThenAProperProjectIsCreated() throws Exception {
        generateProjectFromArchetype(LOGGER);

        LOGGER.info("---------------------------------------------------------------------");
        LOGGER.info("Building the generated project {}", archetypeProperties.getProjectArtifactId());
        LOGGER.info("---------------------------------------------------------------------");

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
        printVerifierLog("PROJECT BUILD", verifier, LOGGER);
        verifier.verifyErrorFreeLog();
        verifier.verifyTextInLog("Tests run: 5, Failures: 0, Errors: 0, Skipped: 0");
    }
}
