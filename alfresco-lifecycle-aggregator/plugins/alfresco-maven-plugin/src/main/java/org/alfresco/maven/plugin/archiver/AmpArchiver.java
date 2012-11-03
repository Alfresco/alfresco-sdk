package org.alfresco.maven.plugin.archiver;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;


/**
 * Emulates the JarArchiver only changing the extension name from .jar to .amp
 * It also adds a logging statement that can help debugging the build
 *
 * @author Gabriele Columbro, Maurizio Pillitu
 */
public class AmpArchiver extends JarArchiver {

    public AmpArchiver() {
        super.archiveType = "amp";
    }

    /**
     * @see org.codehaus.plexus.archiver.AbstractArchiver#addDirectory(java.io.File, String, String[], String[])
     */
    public void addDirectory(final File directory, final String prefix, final String[] includes,
                             final String[] excludes) {
        getLogger().info("Adding directory to AMP package [ '" + directory + "' '" + prefix + "']");
        super.addDirectory(directory, prefix, includes, excludes);
    }
}
