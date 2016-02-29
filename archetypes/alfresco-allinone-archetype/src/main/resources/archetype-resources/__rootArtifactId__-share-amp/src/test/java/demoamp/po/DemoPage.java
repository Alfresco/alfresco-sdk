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
package ${package}.demoamp.po;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Demo of simple share page object that encapsulates the demo of simple page.
 *
 * @author Michael Suzuki
 * @since 2.0.1
 */
public class DemoPage extends SharePage {
    @FindBy(id="DEMO_SIMPLE_LOGO")
    WebElement logo;

    @FindBy(id="DEMO_SIMPLE_MSG")
    WebElement msg;

    @SuppressWarnings("unchecked")
    @Override
    public DemoPage render(RenderTime timer) {

        // Wait for logo and message to display, then consider page rendered
        while (true) {
            timer.start();
            try {
                if (isSimpleLogoDisplayed() && isMessageDisplayed()) {
                    break;
                }
            } catch (NoSuchElementException nse) {
            } finally {
                timer.end();
            }
        }

        return this;
    }

    public boolean isSimpleLogoDisplayed() {
        return isDisplayed(logo);
    }

    public boolean isMessageDisplayed() {
        return isDisplayed(msg);
    }

    public String getMessage() {
        return msg.getText();
    }
}