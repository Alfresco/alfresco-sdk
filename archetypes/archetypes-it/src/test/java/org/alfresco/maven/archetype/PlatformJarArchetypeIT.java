package org.alfresco.maven.archetype;

import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * Integration tests for the platform jar archetype.
 */
public class PlatformJarArchetypeIT extends AbstractArchetypeIT {

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
        generateProjectFromArchetype();

        // Since creating the archetype was successful, we now want to actually build the generated project
        Verifier verifier = new Verifier(projectPath);
        verifier.setAutoclean(false);
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
    }
}
