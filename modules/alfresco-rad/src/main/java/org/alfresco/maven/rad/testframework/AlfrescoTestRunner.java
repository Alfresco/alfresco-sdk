package org.alfresco.maven.rad.testframework;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.repo.avm.util.RawServices;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This is a JUnit test runner that is designed to work with an Alfresco repository.
 * It detects if it's executing a test inside of a running Alfresco instance. If that 
 * is the case the tests are all run normally. If however the test is being run from
 * outside the repository, from the maven command line or from an IDE
 * such as IntelliJ or STS/Eclipse for example, then instead of running the actual
 * test an http request is made to a webscript in a running Alfresco instance. This
 * webscript runs the test and returns enough information to this class so we can
 * emulate having run the test locally.
 * 
 * By doing this, we are able to create Integration Tests (IT) using standard JUnit
 * capabilities. These can then be run from our IDEs with the associated visualizations,
 * support for re-running failed tests, etc.
 * 
 * Integration testing framework donated by Zia Consulting
 * 
 * @author Bindu Wavell <bindu@ziaconsulting.com>
 */
public class AlfrescoTestRunner extends BlockJUnit4ClassRunner
{
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";

    public AlfrescoTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
    
    public static String serializableToString( Serializable serializable ) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( serializable );
        oos.close();
        
        String string =  Base64.encodeBase64URLSafeString(baos.toByteArray());
        return string;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (areWeRunningInAlfresco()) {
            super.runChild(method, notifier);
        } else {
            Description desc = describeChild(method);
            if (method.getAnnotation(Ignore.class) != null) {
                notifier.fireTestIgnored(desc);
            } else {
                callProxiedChild(method, notifier, desc);
            }
        }
    }

    protected void callProxiedChild(FrameworkMethod method, RunNotifier notifier, Description desc)
    {

        notifier.fireTestStarted(desc);

        String className = method.getMethod().getDeclaringClass().getCanonicalName();
        String methodName = method.getName();
        if (null != methodName) {
            className += "#" + methodName;
        }

        DefaultHttpClient client = new DefaultHttpClient();

        HttpHost targetHost = new HttpHost("localhost", 8080, "http");
        client.getCredentialsProvider().setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("admin", "admin"));

        HttpGet get = new HttpGet(getContextRoot(method) + "/service/testing/test.xml?clazz=" + className.replace("#", "%23"));

        try {

            // Send proxied request and read response
            HttpResponse resp = client.execute(get);
            InputStream is = resp.getEntity().getContent();
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            String body = "";
            String line;
            while ((line = br.readLine()) != null) {
                body += line+"\n";
            }

            // Process response
            if (body.length() > 0) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = null;
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(body)));

                Element root = doc.getDocumentElement();
                NodeList results = root.getElementsByTagName("result");
                if (null != results && results.getLength() > 0) {
                    String result = results.item(0).getFirstChild().getNodeValue();
                    if (SUCCESS.equals(result)) {
                        notifier.fireTestFinished(desc);
                    } else {
                        boolean failureFired = false;
                        NodeList throwableNodes = root.getElementsByTagName("throwable");
                        for (int tid = 0; tid < throwableNodes.getLength(); tid++) {
                            String throwableBody = null;
                            Object object = null;
                            Throwable throwable = null;
                            throwableBody = throwableNodes.item(tid).getFirstChild().getNodeValue();
                            if (null != throwableBody) {
                                try {
                                    object = objectFromString(throwableBody);
                                } catch (ClassNotFoundException e) { }
                                if (null != object && object instanceof Throwable) {
                                    throwable = (Throwable) object;
                                }
                            }
                            if (null == throwable) {
                                throwable = new Throwable("Unable to process exception body: " + throwableBody);
                            }

                            notifier.fireTestFailure(new Failure(desc, throwable));
                            failureFired = true;
                        }
                        if (!failureFired) {
                            notifier.fireTestFailure(new Failure(desc, new Throwable("There was an error but we can't figure out what it was, sorry!")));
                        }
                    }
                } else {
                    notifier.fireTestFailure(new Failure(desc, new Throwable("Unable to process response for proxied test request: " + body)));
                }
            } else {
                notifier.fireTestFailure(new Failure(desc, new Throwable("Attempt to proxy test into running Alfresco instance failed, no response received")));
            }
        } catch (IOException e) {
            notifier.fireTestFailure(new Failure(desc, e));
        } catch (ParserConfigurationException e) {
            notifier.fireTestFailure(new Failure(desc, e));
        } catch (SAXException e) {
            notifier.fireTestFailure(new Failure(desc, e));
        }

    }

    protected static Object objectFromString( String string ) throws IOException , ClassNotFoundException
    {
        byte[] buffer = Base64.decodeBase64(string);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  buffer ) );
        Object object  = ois.readObject();
        ois.close();
        return object;
    }

    protected boolean areWeRunningInAlfresco()
    {
	    Object rawService = RawServices.Instance();
	    return (null != rawService);
    }
    
    protected String getContextRoot(FrameworkMethod method)
    {
    		Class<?> declaringClass = method.getMethod().getDeclaringClass();
    		boolean annotationPresent = declaringClass.isAnnotationPresent(Remote.class);
    		if (annotationPresent) {
    			Remote annotation = declaringClass.getAnnotation(Remote.class);
    			return annotation.endpoint();
    		}
    		return "http://localhost:8080/alfresco";
    }
    
}
/*
	Licensed to the Apache Software Foundation (ASF) under one or more
	contributor license agreements.  See the NOTICE file distributed with
	this work for additional information regarding copyright ownership.
	The ASF licenses this file to You under the Apache License, Version 2.0
	(the "License"); you may not use this file except in compliance with
	the License.  You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/