/**
 * Copyright (C) 2016 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.maven.plugin.config;

/**
 * A Maven Dependency containing the Group ID, Artifact ID, and Version (GAV).
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0.0
 */
public abstract class MavenDependency {
    private String groupId;
    private String artifactId;
    private String version;

    public MavenDependency() {}

    public MavenDependency(String g, String a, String v) {
        this.groupId = g;
        this.artifactId = a;
        this.version = v;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MavenDependency)) return false;

        MavenDependency that = (MavenDependency) o;

        if (!groupId.equals(that.groupId)) return false;
        if (!artifactId.equals(that.artifactId)) return false;
        return version.equals(that.version);

    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MavenDependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
