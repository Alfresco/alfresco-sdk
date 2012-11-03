package org.alfresco.demoamp;

import java.util.logging.Logger;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A basic component that will be started for this module.
 * This is a sample taken from Alfresco SDK
 * 
 * @author Derek Hulley
 */
public class DemoComponent extends AbstractModuleComponent
{
	Log log = LogFactory.getLog(DemoComponent.class);
	
    @Override
    protected void executeInternal() throws Throwable
    {
        System.out.println("DemoComponent has been executed");
        log.debug("Test debug logging is working");
        log.info("This should not be outputted by default");
    }
}
