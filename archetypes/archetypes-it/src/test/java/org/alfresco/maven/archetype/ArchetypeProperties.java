package org.alfresco.maven.archetype;

import java.util.Properties;

/**
 * Represent the properties of a project generation execution from a maven archetype.
 *
 * @author Jose Luis Osorno <joseluis.osorno@ixxus.com>
 */
public class ArchetypeProperties {

    private final String archetypeGroupId;
    private final String archetypeArtifactId;
    private final String archetypeVersion;
    private final String projectGroupId;
    private final String projectArtifactId;
    private final String projectVersion;

    private ArchetypeProperties(String archetypeGroupId, String archetypeArtifactId, String archetypeVersion, String projectGroupId,
            String projectArtifactId, String projectVersion) {
        this.archetypeGroupId = archetypeGroupId;
        this.archetypeArtifactId = archetypeArtifactId;
        this.archetypeVersion = archetypeVersion;
        this.projectGroupId = projectGroupId;
        this.projectArtifactId = projectArtifactId;
        this.projectVersion = projectVersion;
    }

    public static ArchetypePropertiesBuilder builder() {
        return new ArchetypePropertiesBuilder();
    }

    public Properties getSystemProperties() {
        Properties props = new Properties(System.getProperties());
        props.put("archetypeGroupId", archetypeGroupId);
        props.put("archetypeArtifactId", archetypeArtifactId);
        props.put("archetypeVersion", archetypeVersion);
        props.put("groupId", projectGroupId);
        props.put("artifactId", projectArtifactId);
        props.put("version", projectVersion);
        props.put("interactiveMode", "false");
        return props;
    }

    public String getArchetypeGroupId() {
        return archetypeGroupId;
    }

    public String getArchetypeArtifactId() {
        return archetypeArtifactId;
    }

    public String getArchetypeVersion() {
        return archetypeVersion;
    }

    public String getProjectGroupId() {
        return projectGroupId;
    }

    public String getProjectArtifactId() {
        return projectArtifactId;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public static class ArchetypePropertiesBuilder {
        private String archetypeGroupId;
        private String archetypeArtifactId;
        private String archetypeVersion;
        private String projectGroupId;
        private String projectArtifactId;
        private String projectVersion;

        private ArchetypePropertiesBuilder() {
        }

        public ArchetypePropertiesBuilder withArchetypeGroupId(final String archetypeGroupId) {
            this.archetypeGroupId = archetypeGroupId;
            return this;
        }

        public ArchetypePropertiesBuilder withArchetypeArtifactId(final String archetypeArtifactId) {
            this.archetypeArtifactId = archetypeArtifactId;
            return this;
        }

        public ArchetypePropertiesBuilder withArchetypeVersion(final String archetypeVersion) {
            this.archetypeVersion = archetypeVersion;
            return this;
        }

        public ArchetypePropertiesBuilder withProjectGroupId(final String projectGroupId) {
            this.projectGroupId = projectGroupId;
            return this;
        }

        public ArchetypePropertiesBuilder withProjectArtifactId(final String projectArtifactId) {
            this.projectArtifactId = projectArtifactId;
            return this;
        }

        public ArchetypePropertiesBuilder withProjectVersion(final String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public ArchetypeProperties build() {
            return new ArchetypeProperties(archetypeGroupId, archetypeArtifactId, archetypeVersion, projectGroupId,
                    projectArtifactId, projectVersion);
        }
    }
}
