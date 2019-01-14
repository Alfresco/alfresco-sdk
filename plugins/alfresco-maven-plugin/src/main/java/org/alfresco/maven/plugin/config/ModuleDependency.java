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

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Defines an Alfresco extension module dependency (JAR or AMP) to be
 * overlayed on an Alfresco webapp file.
 * </p>
 * <p>
 * This is so we can skip the WAR projects in the AIO project,
 * and so we can include the Share Services AMP when running
 * with a simple platform JAR.
 * </p>
 * <p>
 * This class is used by the RunMojo class.
 * </p>
 * Alfresco Maven Plugin config looks something like this:
 * <pre>
 *    {@code
 *    <platformModules>
 *      <moduleDependency>
 *          <groupId>${alfresco.groupId}</groupId>
 *          <artifactId>alfresco-share-services</artifactId>
 *          <version>${alfresco.share.version}</version>
 *          <type>amp</type>
 *      </moduleDependency>
 *      <moduleDependency>
 *          <groupId>${project.groupId}</groupId>
 *          <artifactId>${project.artifactId}</artifactId>
 *          <version>${project.version}</version>
 *      </moduleDependency>
 *    </platformModules>
 *    }
 * </pre>
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0.0
 */
public class ModuleDependency extends MavenDependency {
    public static final String TYPE_JAR = "jar";
    public static final String TYPE_AMP = "amp";

    private String type = TYPE_JAR;

    public ModuleDependency() {
        super();
    }

    public ModuleDependency(String g, String a, String v, String t) {
        super(g,a,v);

        this.type = t;
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
        if (!super.equals(o)) return false;

        ModuleDependency that = (ModuleDependency) o;

        return !(type != null ? !type.equals(that.type) : that.type != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleDependency{" +
                "groupId='" + getGroupId() + '\'' +
                ", artifactId='" + getArtifactId() + '\'' +
                ", version='" + getVersion() + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
