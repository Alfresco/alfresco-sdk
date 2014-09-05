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

import org.alfresco.demoamp.HelloWorldMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**s
 * Hello world demo unit test, show the basics of junit and unit testing.
 * Create the message to display on hello world webscript.
 * @author Michael Suzuki
 *
 */
@RunWith(JUnit4.class)
public class HelloWorldMessageTest
{
    @Test
    public void init()
    {
        HelloWorldMessage msg = new HelloWorldMessage("bla", 0);
        Assert.assertNotNull(msg);
    }
    
    @Test(expected = RuntimeException.class)
    public void initWithNull()
    {
        new HelloWorldMessage(null, 0);
    }
    @Test
    public void toStringTest()
    {
        HelloWorldMessage msg = new HelloWorldMessage("Home", 10);
        Assert.assertNotNull(msg);
        Assert.assertEquals("Home has 10 folders",msg.toString());
    }
    
}
