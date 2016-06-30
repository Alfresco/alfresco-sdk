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

package org.alfresco.maven.plugin;

import org.apache.commons.lang.StringUtils;

/**
 * Defines an Alfresco extension module dependency (JAR or AMP) to be
 * overlayed on an Alfresco webapp file via Maven WAR plugin.
 * <p/>
 * This is so we can skip the WAR projects in the AIO project,
 * and so we can include the Share Services AMP when running
 * with a simple platform JAR.
 * <p/>
 * This class is used by the RunMojo class.
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0.0
 */
public class ModuleDependency {
    public static final String TYPE_JAR = "jar";
    public static final String TYPE_AMP = "amp";

    private String groupId;
    private String artifactId;
    private String version;
    private String type = TYPE_JAR;

    public ModuleDependency() {}

    public ModuleDependency(String g, String a, String v) {
        this.groupId = g;
        this.artifactId = a;
        this.version = v;
    }

    public ModuleDependency(String g, String a, String v, String t) {
        this.groupId = g;
        this.artifactId = a;
        this.version = v;
        this.type = t;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAmp() {
        return StringUtils.equalsIgnoreCase(this.type, TYPE_AMP);
    }

    public boolean isJar() {
        return StringUtils.equalsIgnoreCase(this.type, TYPE_JAR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleDependency)) return false;

        ModuleDependency that = (ModuleDependency) o;

        if (!groupId.equals(that.groupId)) return false;
        if (!artifactId.equals(that.artifactId)) return false;
        if (!version.equals(that.version)) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleDependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
