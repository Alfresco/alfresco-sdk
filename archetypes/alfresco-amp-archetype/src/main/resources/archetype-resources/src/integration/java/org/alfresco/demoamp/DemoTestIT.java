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

import org.alfresco.demoamp.po.DemoPage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DemoTestIT
{
    private static WebDriver driver;
    
    @BeforeClass 
    public static void setup()
    {
        //Create WebDriver
        driver = new FirefoxDriver();
        //Login and obtain ticket
        driver.get("http://localhost:8080/alfresco/service/api/login?u=admin&pw=admin");
        WebElement ticket = driver.findElement(By.tagName("ticket"));
        String token =String.format("?alf_ticket=%s",ticket.getText());
        //Navigate to sample page with token
        driver.get("http://localhost:8080/alfresco/service/sample/helloworld" + token);
    }
    
    @AfterClass
    public static void teardown()
    {
        if(driver != null)
        {
            driver.quit();
        }
    }

    @Test
    public void titleDisplayed()
    {
        DemoPage page = new DemoPage(driver);
        //Invoke render when ready to use page object.
        page.render();
        Assert.assertNotNull(page);
        Assert.assertTrue(page.isTitleVisible());
        Assert.assertEquals("Welcome to Demoamp", page.getTitle());
    }
    
    /**
     * Message should include the directory name
     * and number of folders currently in that directory.
     */
    @Test
    public void messageIsDisplayed()
    {
        DemoPage page = new DemoPage(driver).render();
        Assert.assertNotNull(page);
        Assert.assertTrue(page.isMessageVisible());
        Assert.assertEquals("Company Home has 7 folders", page.getMessage());
    }

    @Test
    public void findLogo()
    {
        DemoPage page = new DemoPage(driver).render();
        Assert.assertFalse(page.hasLogo());
    }
}
