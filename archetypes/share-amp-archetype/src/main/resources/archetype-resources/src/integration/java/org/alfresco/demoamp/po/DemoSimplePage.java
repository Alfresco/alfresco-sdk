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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

/**
 * Demo of simple share page object that encapsulates the demo of simple page.
 * 
 * @author Michael Suzuki
 *
 */
public class DemoSimplePage extends SharePage
{
    private By SIMPLE_DEMO_LOGO = By.id("demo-simple-logo");
    private By SIMPLE_DEMO_MESSAGE = By.id("DEMO_SIMPLE_MSG");
    
    public DemoSimplePage(WebDrone drone)
    {
        super(drone);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DemoSimplePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DemoSimplePage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if(isSimpleLogoDisplayed() && isMessageDisplayed())
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
    public DemoSimplePage render(long time)
    {
        return render(new RenderTime(time));
    }
    public boolean isSimpleLogoDisplayed()
    {
        try
        {
            return drone.find(SIMPLE_DEMO_LOGO).isDisplayed();
        }
        catch(NoSuchElementException se) {}
        return false;
    }
    
    public boolean isMessageDisplayed()
    {
        try
        {
            return drone.find(SIMPLE_DEMO_MESSAGE).isDisplayed();
        }
        catch(NoSuchElementException se) {}
        return false;
    }
    
    public String getSimpleMessage()
    {
        return drone.find(SIMPLE_DEMO_MESSAGE).getText();
    }
}
