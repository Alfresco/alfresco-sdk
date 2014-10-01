/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
/**
 * Demo page object that encapsulates the demo webscript hello world page.
 * @author Michael Suzuki
 *
 */
public class DemoPage implements HtmlPage
{
    private static final By TITLE_LOCATOR = By.id("demo-title");
    private static final By MESSAGE_LOCATOR = By.id("demo-message");
    private static final long DEFAULT_WAIT_TIME_MILLISECONDS = 6000;
    
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
            WebElement logo = driver.findElement(By.id("logo"));
            return logo.isDisplayed();
        }
        catch(NoSuchElementException te){ }
        return false;
    }
 
    @SuppressWarnings("unchecked")
    @Override
    public DemoPage render()
    {
        return render(DEFAULT_WAIT_TIME_MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DemoPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if(isTitleVisible() && isMessageVisible())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse){ }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DemoPage render(long time)
    {
        return render(new RenderTime(time));
    }
    
    @Override
    public void close()
    {
        driver.close();
    }
}
