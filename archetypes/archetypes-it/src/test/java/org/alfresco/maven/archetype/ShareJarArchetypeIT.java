package org.alfresco.maven.archetype;

import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * Integration tests for the share jar archetype.
 */
public class ShareJarArchetypeIT extends AbstractArchetypeIT {

    @Override
    protected ArchetypeProperties createArchetypeProperties() {
        return ArchetypeProperties.builder()
                .withArchetypeGroupId("org.alfresco.maven.archetype")
                .withArchetypeArtifactId("alfresco-share-jar-archetype")
                .withArchetypeVersion("4.0.0-SNAPSHOT")
                .withProjectGroupId("archetype.it")
                .withProjectArtifactId("sharejar-test-run")
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
