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
 * <p>
 * Tomcat Webapp used in Embedded Tomcat Maven plugin configuration.
 * These are custom webapps that you would want to include for some reason
 * (The {webapp artifact}.war need to be available in a Maven Repo).
 * Note. the standard webapps are included with plugin properties such as enablePlatform and enableShare.
 * </p>
 * Alfresco Maven Plugin config looks something like this:
 * <pre>
 *    {@code
 *    <tomcatWebapps>
 *      <tomcatWebapp>
 *          <groupId>com.exari</groupId>
 *          <artifactId>exari-docgen-cmwar</artifactId>
 *          <version>${project.version}</version>
 *          <contextPath>/exari</contextPath>
 *          <contextFile>${project.build.directory}/contexts/context-docgen.xml</contextFile>
 *      </tomcatWebapp>
 *    <tomcatWebapps>
 *    }
 * </pre>
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0.0
 */
public class TomcatWebapp extends MavenDependency {
    private String contextPath;
    private String contextFile;

    public TomcatWebapp() {}

    public TomcatWebapp(String g, String a, String v, String contextPath, String contextFile) {
        super(g, a, v);
        this.contextPath = contextPath;
        this.contextFile = contextFile;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getContextFile() {
        return contextFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TomcatWebapp)) return false;
        if (!super.equals(o)) return false;

        TomcatWebapp that = (TomcatWebapp) o;

        if (!contextPath.equals(that.contextPath)) return false;
        return !(contextFile != null ? !contextFile.equals(that.contextFile) : that.contextFile != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + contextPath.hashCode();
        result = 31 * result + (contextFile != null ? contextFile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TomcatWebapp{" +
                "groupId='" + getGroupId() + '\'' +
                ", artifactId='" + getArtifactId() + '\'' +
                ", version='" + getVersion() + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", contextFile='" + contextFile + '\'' +
                '}';
    }
}
