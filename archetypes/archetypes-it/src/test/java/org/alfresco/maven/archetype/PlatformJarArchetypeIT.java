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
                .withArchetypeVersion("4.0.0-SNAPSHOT")
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

        // Since creating the archetype was successful, we now want to actually build the generated project
        Verifier verifier = new Verifier(projectPath);
        verifier.setAutoclean(false);
        verifier.executeGoal("install");
        printVerifierLog("PROJECT BUILD", verifier, LOGGER);
        verifier.verifyErrorFreeLog();
    }
}
