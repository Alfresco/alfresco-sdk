/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.demoamp;

import org.alfresco.demoamp.po.DemoSimplePage;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
/**
 * Functional test that demonstrates how to write and
 * extend an alfresco share page object. 
 * This test shows the interaction with page objects
 * brought from share-po project along with newly created ones
 * that are present in the Demo amp.
 * 
 * @author Michael Suzuki
 *
 */
public class DemoSimpleTestIT extends AbstractTest
{
    DemoSimplePage page;
    
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        //Login to share
        drone.navigateTo("http://localhost:8081/share/page/hdp/ws/simple-page");
        LoginPage loginPage = new LoginPage(drone);
        loginPage.loginAs(username, password);
    }
    
    @BeforeMethod
    public void loadPage()
    {
        //Goto demo page
        drone.navigateTo("http://localhost:8081/share/page/hdp/ws/simple-page");
        page = new DemoSimplePage(drone);
    }
    
    @Test
    public void findLogo()
    {
        Assert.assertTrue(page.isSimpleLogoDisplayed());
    }
    @Test
    public void messageIsDisplayed()
    {
        page.render();
        String msg = page.getSimpleMessage();
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello from i18n!", msg);
    }
    
    @Test
    /**
     * Example of test reusing abstract share page objects.
     */
    public void titleDisplayed()
    {
        //Invoke render when ready to use page object.
        page.render();
        Assert.assertNotNull(page);
        Assert.assertTrue(page.getTitle().contains("This is a simple page"));
    }
    @Test
    /**
     * Test that show how we are able to reuse share page objects
     * objects in particular the navigation object.
     */
    public void navigate()
    {
        Assert.assertNotNull(page.getNav());
        PeopleFinderPage peoppleFinderPage = page.getNav().selectPeople().render();
        Assert.assertNotNull(peoppleFinderPage);
    }
}
