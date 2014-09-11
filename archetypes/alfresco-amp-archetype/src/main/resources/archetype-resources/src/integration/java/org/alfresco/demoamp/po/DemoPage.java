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

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Predicate;
/**
 * Demo page object that encapsulates the demo webscript hello world page.
 * @author Michael Suzuki
 *
 */
public class DemoPage
{
    private static final By TITLE_LOCATOR = By.id("demo-title");
    private static final By MESSAGE_LOCATOR = By.id("demo-message");
    
    private WebDriver driver;
    /**
     * Constructor
     * @param driver
     */
    public DemoPage(WebDriver driver)
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
    /**
     * Sample find with wait element to keep searching for a set time.
     * @return true if the logo is exists
     */
    public boolean hasLogo()
    {
        try
        {
            return findAndWait(By.id("logo"), 6000, 1000).isDisplayed();
            
        }
        catch(TimeoutException te){ }
        return false;
    }
    /**
     * Mechanism to keep looking for an element on the page.
     * @param by selector
     * @param limit max time to wait in ms
     * @param interval time to wait between calls in ms
     * @return
     */
    public WebElement findAndWait(final By by, final long limit, final long interval)
    {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(interval, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(limit, TimeUnit.MILLISECONDS);
        fluentWait.until(new Predicate<By>()
        {
            public boolean apply(By by)
            {
                try
                {
                    return driver.findElement(by).isDisplayed();
                }
                catch (NoSuchElementException ex)
                {
                    return false;
                }
            }
        });
        return driver.findElement(by);
    }
}
