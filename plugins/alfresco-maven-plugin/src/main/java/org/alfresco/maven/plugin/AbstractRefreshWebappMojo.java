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

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.Closeable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Maven Mojo with generic functionality common to
 * both the Refresh Repo and Refresh Share Mojos.
 *
 * @author martin.bergljung@alfresco.com
 * @since 2.1.0
 */
public abstract class AbstractRefreshWebappMojo extends AbstractMojo {

    @Component
    protected MavenProject project;

    /**
     * The mode for the refresh goal, current supported values are:
     * auto - Checks packaging and app.amp.client.war.artifactId to determine if it should refresh for repo or share
     * both - Forces it to refresh both for Repo and Share
     * share - Forces only to refresh share
     * repo  - Forces only to refresh repo
     * none - Disables refreshing web scripts
     */
    @Parameter(property = "maven.alfresco.refresh.mode", defaultValue = "auto")
    protected String refreshMode;

    /**
     * The hostname for where the Alfresco Tomcat server is running.
     */
    @Parameter(property = "maven.alfresco.refresh.host", defaultValue = "localhost")
    protected String refreshHost;

    /**
     * The port number for where the Alfresco Tomcat server is running.
     */
    @Parameter(property = "maven.alfresco.refresh.port", defaultValue = "${maven.tomcat.port}")
    private String refreshPort;

    /**
     * The username for authenticating against Alfresco Repo.
     */
    @Parameter(property = "maven.alfresco.refresh.username", defaultValue = "admin")
    private String refreshUsername;

    /**
     * The password for authenticating against Alfresco Repo.
     */
    @Parameter(property = "maven.alfresco.refresh.password", defaultValue = "admin")
    protected String refreshPassword;

    /**
     * The URL to send the POST to when you want to refresh Alfresco Repo Web Scripts container.
     */
    @Parameter(property = "maven.alfresco.refresh.repoUrl", defaultValue = "/alfresco/service/index")
    protected String refreshRepoUrl;


    /**
     * The URL to send the POST to when you want to refresh Alfresco Share Spring Surf Web Scripts container.
     */
    @Parameter(property = "maven.alfresco.refresh.shareUrl", defaultValue = "/share/page/index")
    protected String refreshShareUrl;

    /**
     * The URL to send the POST to when you want to clear dependency caches for the Alfresco Share webapp.
     */
    @Parameter(property = "maven.alfresco.refresh.clearCacheShareUrl", defaultValue = "/share/page/caches/dependency/clear")
    protected String clearCacheShareUrl;


    /**
     * The name of the web application we are refreshing, just for logging purpose
     */
    protected String refreshWebappName;

    @Parameter(defaultValue = "${app.amp.client.war.artifactId}")
    protected String alfrescoClientWar;

    /**
     * Mojo interface implementation
     */

    @Override
    public void execute() throws MojoExecutionException {
        // Do a ping to see if the server is up, if not, log and just exit
        if (!ping()) {
            getLog().warn("Connection failed to " + refreshHost + ":" + refreshPort + ", " + getAbortedMsg());
            return;
        }

        executeRefresh();
    }



    /**
     * To be implemented by webapp "refresh" Mojos 
     */
    protected abstract void executeRefresh();


    protected void _refreshRepo() {
        refreshWebappName = "Alfresco Repository";
        refreshWebScripts(refreshRepoUrl);
    }

    protected void _refreshShare() {
        refreshWebappName = "Alfresco Share";
        refreshWebScripts(refreshShareUrl);
        clearDependencyCaches(clearCacheShareUrl);
    }

    /**
     * Perform a Refresh of Web Scripts container in webapp.
     * Called by specific refresh mojo implementation.
     */
    protected void refreshWebScripts(String url) {
        // Create the Refresh URL for the Alfresco Tomcat server
        URL alfrescoTomcatUrl = buildFinalUrl(url);
        if (alfrescoTomcatUrl == null) {
            getLog().error("Could not build refresh URL for " + refreshWebappName + ", " + getAbortedMsg());
        }

        // Set up the data we need to POST to the server for the refresh to work
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("reset", "on"));
        postData.add(new BasicNameValuePair("submit", "Refresh Web Scripts"));

        // Do the refresh
        makePostCall(alfrescoTomcatUrl, postData, "Refresh Web Scripts");
    }

    /**
     * Perform a Clear Dependency Caches call on Share webapp.
     * Called by specific refresh mojo implementation, currently only applicable to Share webapp.
     */
    protected void clearDependencyCaches(String url) {
        // Create the Clear Cache URL for the Alfresco Tomcat server
        URL alfrescoTomcatUrl = buildFinalUrl(url);
        if (alfrescoTomcatUrl == null) {
            getLog().error("Could not build clear dependency caches URL for " +
                    refreshWebappName + ", " + getAbortedMsg());
        }

        // Do the refresh
        makePostCall(alfrescoTomcatUrl, null, "Clear Dependency Caches");
    }

    /**
     * Helper method to make a POST request to the Alfresco Webapp
     *
     * @param alfrescoTomcatUrl the URL for the webapp we want to post to
     * @param postData the POST data that should be sent
     * @param operation information about the operation we are performing
     */
    private void makePostCall(URL alfrescoTomcatUrl, List<NameValuePair> postData, String operation) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            // Set up a HTTP POST request to the Alfresco Webapp we are targeting
            HttpHost targetHost = new HttpHost(
                    alfrescoTomcatUrl.getHost(), alfrescoTomcatUrl.getPort(), alfrescoTomcatUrl.getProtocol());

            // Set up authentication parameters
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials(refreshUsername, refreshPassword));

            // Create the HTTP Client we will use to make the call
            client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();

            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            // Make the call to Refresh the Alfresco Webapp
            HttpPost httpPost = new HttpPost(alfrescoTomcatUrl.toURI());
            response = client.execute(httpPost);
            if (postData != null) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, "UTF-8");
                httpPost.setEntity(entity);
            }
            httpPost.setHeader("Accept-Charset", "iso-8859-1,utf-8");
            httpPost.setHeader("Accept-Language", "en-us");
            response = client.execute(httpPost);

            // If no response, no method has been passed
            if (response == null) {
                getLog().error("POST request failed to " + alfrescoTomcatUrl.toString() + ", " + getAbortedMsg());
                return;
            }

            // Check if we got a successful response or not
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                getLog().info("Successfull " + operation + " for " + refreshWebappName);
            } else {
                String reasonPhrase = response.getStatusLine().getReasonPhrase();
                getLog().warn("Failed to " + operation + " for " + refreshWebappName + ". Response status: " +
                        statusCode + ", message: " + reasonPhrase);
            }
        } catch (Exception ex) {
            getLog().error("POST request failed to " + alfrescoTomcatUrl.toString() + ", " + getAbortedMsg());
            getLog().error("Exception Msg: " + ex.getMessage());
        } finally {
            closeQuietly(response);
            closeQuietly(client);
        }
    }

    private URL buildFinalUrl(String specificRefreshUrlPath) {
        try {
            return new URL("http://" + refreshHost + ":" + refreshPort + specificRefreshUrlPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Utility method to ping/call the Alfresco Tomcat server and see if it is up (running)
     *
     * @return true if the Alfresco Tomcat server was reachable and up
     */
    private boolean ping() {
        try {
            URL alfrescoTomcatUrl = buildFinalUrl("");
            TelnetClient telnetClient = new TelnetClient();
            telnetClient.setDefaultTimeout(500);
            telnetClient.connect(alfrescoTomcatUrl.getHost(), alfrescoTomcatUrl.getPort());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Close down communication objects without any messages
     *
     * @param closeable
     */
    private void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            // swallow any exceptions
        }
    }

    /**
     * Helper to get consistent aborted message
     * @return
     */
    private String getAbortedMsg() {
        return refreshWebappName + " webapp refresh aborted";
    }
}
