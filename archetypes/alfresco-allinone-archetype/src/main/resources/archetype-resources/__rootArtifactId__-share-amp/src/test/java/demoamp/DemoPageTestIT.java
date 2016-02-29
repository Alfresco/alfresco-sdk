#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/*
 * Copyright (C) 2005-2016 Alfresco Software Limited.
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
package ${package}.demoamp;

import ${package}.demoamp.po.DemoPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.AbstractTest;
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
 * @since 2.0.1
 */
public class DemoPageTestIT extends AbstractTest {
    DemoPage page;

    @BeforeClass(groups = {"alfresco-one"})
    public void prepare() throws Exception {
        // Navigate to share, which will redirect to Login page
        driver.navigate().to(shareUrl + "/page");

        // Resolve/Bind current page to LoginPage object
        LoginPage loginPage = resolvePage(driver).render();
        loginPage.loginAs(username, password);
    }

    @BeforeMethod
    public void loadPage() {
        // Goto demo page
        driver.navigate().to(shareUrl + "/page/hdp/ws/simple-page");

        // We need to instantiate the page like this as it is not yet in
        // the factory known list of pages
        page = factoryPage.instantiatePage(driver, DemoPage.class);
    }

    @Test
    public void findLogo() {
        Assert.assertTrue(page.isSimpleLogoDisplayed());
    }

    @Test
    public void messageIsDisplayed() {
        page.render();
        String msg = page.getMessage();
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello from i18n!", msg);
    }

    /**
     * Example of test reusing methods in abstract share page objects.
     */
    @Test
    public void titleDisplayed() {
        // Invoke render when ready to use page object.
        page.render();
        Assert.assertNotNull(page);
        Assert.assertTrue(page.getTitle().contains("This is a simple page"));
    }

    /**
     * Test that show how we are able to reuse share page objects
     * objects in particular the navigation object.
     */
    @Test
    public void navigate() {
        Assert.assertNotNull(page.getNav());
        PeopleFinderPage peopleFinderPage = page.getNav().selectPeople().render();
        Assert.assertNotNull(peopleFinderPage);
    }
}