package org.alfresco.maven.rad;

import com.tradeshift.test.remote.RemoteServer;

public class RemoteRunnerWrapper implements Runnable {

	public void run() {
		try {
	        RemoteServer.main(new String []{});
        } catch (Exception e) {
	        System.out.println("Could not start JUnit remoteServer");
        }

	}

}
