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
 * Tomcat Dependency used in Embedded Tomcat Maven plugin configuration.
 * <p/>
 * Alfresco Maven Plugin config looks something like this:
 * <pre>
 *    {@code
 *    <tomcatDependencies>
 *      <tomcatDependency>
 *          <groupId>mysql</groupId>
 *          <artifactId>mysql-connector-java</artifactId>
 *          <version>5.1.32</version>
 *      </tomcatDependency>
 *    <tomcatDependencies>
 *    }
 * </pre>
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0.0
 */
public class TomcatDependency extends MavenDependency {

    public TomcatDependency() {
        super();
    }

    public TomcatDependency(String g, String a, String v) {
        super(g,a,v);
    }

    @Override
    public String toString() {
        return "TomcatDependency{" +
                "groupId='" + getGroupId() + '\'' +
                ", artifactId='" + getArtifactId() + '\'' +
                ", version='" + getVersion() + '\'' +
                '}';
    }
}
