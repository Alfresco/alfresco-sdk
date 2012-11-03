package org.alfresco.demoamp.test;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;

import org.alfresco.demoamp.DemoComponent;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * A simple class demonstrating how to run out-of-container tests 
 * loading Alfresco application context. 
 * 
 * @author columbro
 *
 */
public class DemoComponentTest {
    
    private static final String ADMIN_USER_NAME = "admin";

    static Logger log = Logger.getLogger(DemoComponentTest.class);

    protected static ApplicationContext applicationContext;
    
    protected static DemoComponent demoComponent;
    
    protected static NodeService nodeService;
    
    @BeforeClass
    public static void initAppContext()
    {
        // TODO: Make testing properly working without need for helpers
        // TODO: Provide this in an SDK base class
        ApplicationContextHelper.setUseLazyLoading(false);
        ApplicationContextHelper.setNoAutoStart(true);
        applicationContext = ApplicationContextHelper.getApplicationContext(new String[] { "classpath:alfresco/application-context.xml" });
        demoComponent = (DemoComponent) applicationContext.getBean("changeme.exampleComponent");
        nodeService = (NodeService) applicationContext.getBean("NodeService");
        AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER_NAME);
        log.debug("Sample test logging: If you see this message, means your unit test logging is properly configured. Change it in test-log4j.properties");
        log.debug("Sample test logging: Application Context properly loaded");
    }
    


    @Test
    public void testWiring() {
        assertNotNull(demoComponent);
    }
    
    @Test
    public void testGetCompanyHome() {
        NodeRef companyHome = demoComponent.getCompanyHome();
        assertNotNull(companyHome);
        String companyHomeName = (String) nodeService.getProperty(companyHome, ContentModel.PROP_NAME);
        assertNotNull(companyHomeName);
        assertEquals("Company Home", companyHomeName);
    }
    
    @Test
    public void testChildNodesCount() {
        NodeRef companyHome = demoComponent.getCompanyHome();
        int childNodeCount = demoComponent.childNodesCount(companyHome);
        assertNotNull(childNodeCount);
        // There are 5 folders by default under Company Home
        assertEquals(5, childNodeCount);
    }

}
