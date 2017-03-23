/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK.
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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Refresh Alfresco Share (share.war) Mojo.
 * Will refresh the Web Script container so new and changed
 * Spring Surf Web Scripts are detected.
 * Will also clear the dependency caches for web resources (CSS, JS, etc).
 * <p/>
 * It is important to execute the refresh calls in the compile phase,
 * otherwise the files will be copied after this and the refresh calls
 * will not be recognized.
 *
 * @author martin.bergljung@alfresco.com
 * @since 2.1.0
 */
@Mojo(name = "refresh-share", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true, requiresProject = true)
public class RefreshShareWebappMojo extends AbstractRefreshWebappMojo {

    /**
     * Call the Share Webapp and refresh web scripts and clear caches.
     */
    protected void executeRefresh() {
        _refreshShare();
    }
}
