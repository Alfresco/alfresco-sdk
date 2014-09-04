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
package org.alfresco.demoamp.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
/**
 * Hello World Page object used in Summit Demo.
 * @author Michael Suzuki
 *
 */
public class HelloWorldPage
{
    private static final By TITLE_LOCATOR = By.id("demo-title");
    private static final By MESSAGE_LOCATOR = By.id("demo-message");
    
    private WebDriver driver;
    /**
     * Constructor
     * @param driver
     */
    public HelloWorldPage(WebDriver driver)
    {
        this.driver = driver;
    }
    
    public boolean isTitleVisible()
    {
        return driver.findElement(TITLE_LOCATOR).isDisplayed();
    }
    
    public String getTitle()
    {
        return driver.findElement(TITLE_LOCATOR).getText();
    }
    public boolean isMessageVisible()
    {
        return driver.findElement(MESSAGE_LOCATOR).isDisplayed();
    }
    public String getMessage()
    {
        return driver.findElement(MESSAGE_LOCATOR).getText();
    }
}
