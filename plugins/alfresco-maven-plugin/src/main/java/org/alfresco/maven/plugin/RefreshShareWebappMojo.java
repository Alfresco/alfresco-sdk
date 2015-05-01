/**
 * Copyright (C) 2015 Alfresco Software Limited.
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
import org.apache.maven.plugins.annotations.Parameter;

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
@Mojo(name = "refresh-share", defaultPhase = LifecyclePhase.COMPILE)
public class RefreshShareWebappMojo extends AbstractRefreshWebappMojo {
    public static final String DEFAULT_SHARE_REFRESH_URL = "/share/page/index";
    public static final String DEFAULT_SHARE_CLEAR_DEPENDENCY_CACHES_URL = "/share/page/caches/dependency/clear";

    /**
     * The URL to send the POST to when you want to refresh Alfresco Share Spring Surf Web Scripts container.
     */
    @Parameter(property = "refreshShareUrl", required = true, alias = "refreshShareUrl")
    private String _refreshShareUrl = DEFAULT_SHARE_REFRESH_URL;

    /**
     * The URL to send the POST to when you want to clear dependency caches for the Alfresco Share webapp.
     */
    @Parameter(property = "clearCacheShareUrl", required = true, alias = "clearCacheShareUrl")
    private String _clearCacheShareUrl = DEFAULT_SHARE_CLEAR_DEPENDENCY_CACHES_URL;

    public RefreshShareWebappMojo() {
        setRefreshUrl(_refreshShareUrl);
        setClearDependencyCachesUrl(_clearCacheShareUrl);
        setWebappName("Alfresco Share");
    }

    /**
     * Call the Share Webapp and refresh web scripts and clear caches.
     */
    protected void executeRefresh() {
        refreshWebScripts();
        clearDependencyCaches();
    }
}
