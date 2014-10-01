/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.demoamp;

/**
 * This is a simple class that generates a message for demo hello world,
 * and also outputs to <i>system.out</i> a message.
 * NB: This code is taken from Alfresco Eclipse SDK Samples 
 * @author Derek Hulley
 * @author Michael Suzuki
 */
public class Demo
{
    public void init()
    {
        System.out.println("SDK Demo AMP class has been loaded");
    }
    /**
     * Generates a message.
     * @param directoryName String directory identifier
     * @param folders int count of folders for that directory
     * @return String message 
     */
    public static String generateMessage(final String directoryName, final int folders)
    {
        if(directoryName == null || directoryName.isEmpty())
        {
            throw new RuntimeException("Directory name is required");
        }
        return String.format("%s has %d folders", directoryName, folders);
    }
}
