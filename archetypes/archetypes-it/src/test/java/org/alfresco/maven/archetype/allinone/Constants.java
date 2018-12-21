package org.alfresco.maven.archetype.allinone;

import java.util.Properties;

public class Constants {// GAV for the archetype

    public static final String ARCHETYPE_GROUP_ID = "org.alfresco.maven.archetype";
    public static final String ARCHETYPE_ARTEFACT_ID = "alfresco-allinone-archetype";
    public static final String ARCHETYPE_VERSION = "4.0.0-SNAPSHOT";


    // GAV for the created artefact from the archetype which we want to test
    public static final String TEST_GROUP_ID = "archetype.it";

    public static final String TEST_ARTIFACT_ID = "allinone-test-run";

    public static final String TEST_VERSION = "0.1-SNAPSHOT";


    public static Properties getSystemProperties() {
        Properties props = new Properties(System.getProperties());
        props.put("archetypeGroupId", Constants.ARCHETYPE_GROUP_ID);
        props.put("archetypeArtifactId", Constants.ARCHETYPE_ARTEFACT_ID);
        props.put("archetypeVersion", Constants.ARCHETYPE_VERSION);
        props.put("groupId", Constants.TEST_GROUP_ID);
        props.put("artifactId", Constants.TEST_ARTIFACT_ID);
        props.put("version", Constants.TEST_VERSION);
        props.put("interactiveMode", "false");

        return props;
    }

}
