package org.alfresco.plexus.archiver;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.archiver.*;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.zip.ZipOutputStream;

public class AmpArchiver extends JarArchiver
{

    public AmpArchiver()
    {
        super.archiveType = "amp";
    }

    public void setModuleProperties(File descr)
    throws ArchiverException
    {
//        deploymentDescriptor = descr;
//        if(!deploymentDescriptor.exists())
//        {
//            throw new ArchiverException("Deployment descriptor: " + deploymentDescriptor + " does not exist.");
//        } else
//        {
//            addFile(descr, "config/AMP-INF/module.properties");
//            return;
//        }
    }

    public void addLib(File fileName)
        throws ArchiverException
    {
        addDirectory(fileName.getParentFile(), "lib/", new String[] {
            fileName.getName()
        }, null);
    }

    public void addLibs(File directoryName, String includes[], String excludes[])
        throws ArchiverException
    {
        addDirectory(directoryName, "lib/", includes, excludes);
    }

    public void addClass(File fileName)
        throws ArchiverException
    {
        addDirectory(fileName.getParentFile(), "classes/", new String[] {
            fileName.getName()
        }, null);
    }

    public void addClasses(File directoryName, String includes[], String excludes[])
        throws ArchiverException
    {
        addDirectory(directoryName, "classes/", includes, excludes);
    }


    protected void initZipOutputStream(ZipOutputStream zOut)
        throws IOException, ArchiverException
    {
//        if(deploymentDescriptor == null && !isInUpdateMode())
//        {
//            throw new ArchiverException("module properies attribute is required");
//        } 
//        else
//        {
            super.initZipOutputStream(zOut);
            return;
//        }
    }

    protected void zipFile(ArchiveEntry entry, ZipOutputStream zOut, String vPath, int mode)
        throws IOException, ArchiverException
    {
        if(vPath.equalsIgnoreCase("config/AMP-INF/module.properties"))
        {
            if(deploymentDescriptor == null || !deploymentDescriptor.getCanonicalPath().equals(entry.getFile().getCanonicalPath()) || descriptorAdded)
            {
                getLogger().warn("Warning: selected " + super.archiveType + " files include a config/AMP-INF/module.properites which will be ignored " + "(please use webxml attribute to " + super.archiveType + " task)");
            } 
            else
            {
              super.zipFile(entry, zOut, vPath);
                descriptorAdded = true;
            }
        } 
        else
        {
            super.zipFile(entry, zOut, vPath);
        }
    }

    
    protected void cleanUp()
    {
        descriptorAdded = false;
        super.cleanUp();
    }

    private File deploymentDescriptor;
    private boolean descriptorAdded;
    /**
     * @see org.codehaus.plexus.archiver.AbstractArchiver#addDirectory(java.io.File, java.lang.String, java.lang.String[], java.lang.String[])
     */
    public void addDirectory(File pArg0, String pArg1, String[] pArg2, String[] pArg3)
        throws ArchiverException
    {
        /* */
        getLogger().info("adding directory [ '"+pArg0+"' '"+pArg1+"']");
        super.addDirectory(pArg0, pArg1, pArg2, pArg3);
    }
}
