package org.alfresco.plexus.archiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;

import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.archiver.ArchiveFinalizer;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.archiver.zip.AbstractZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipEntry;
import org.codehaus.plexus.archiver.zip.ZipFile;
import org.codehaus.plexus.util.FileUtils;

public class AmpUnArchiver extends AbstractZipUnArchiver {

	private static final String NATIVE_ENCODING = "native-encoding";

	private String encoding = "UTF8";

	private FilterSupport filterSupport;

	private List finalizers;

	private static String fileSeparator = System.getProperty("file.separator");
	
	
	public void setArchiveFilters( List filters )
	{
		filterSupport = new FilterSupport( filters, getLogger() );
	}

	/**
	 * Sets the encoding to assume for file names and comments.
	 * <p/>
	 * <p>Set to <code>native-encoding</code> if you want your
	 * platform's native encoding, defaults to UTF8.</p>
	 */
	public void setEncoding( String encoding )
	{
		if ( NATIVE_ENCODING.equals( encoding ) )
		{
			encoding = null;
		}
		this.encoding = encoding;
	}


	private static Map<String,String> ampMapping = new HashMap<String,String>();

	@Override
	protected void execute()
	throws ArchiverException, IOException
	{

		getLogger().info( "Expanding: " + getSourceFile() + " into " + getDestDirectory() );
		ZipFile zf = null;

		

		try
		{
			zf = new ZipFile( getSourceFile(), encoding );
			Enumeration e = zf.getEntries();
			
			
			String moduleId = getModuleId(zf); 

			//	Based on the current AMP name creates the appropriate mapping to alfresco/module/<ampname>
			createAmpMapping(moduleId);
			
			while ( e.hasMoreElements() )
			{
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (!ze.getName().startsWith("META-INF"))
				{
				String fileInAmp = getAmpMapping(ze.getName()); 

				extractFileIfIncluded( getSourceFile(), getDestDirectory(), zf.getInputStream( ze ), fileInAmp, new Date( ze.getTime() ), ze.isDirectory() );
				}
			}

			runArchiveFinalizers();

			getLogger().debug( "expand complete" );
		}
		catch ( IOException ioe )
		{
			throw new ArchiverException( "Error while expanding " + getSourceFile().getAbsolutePath(), ioe );
		}
		finally
		{
			if ( zf != null )
			{
				try
				{
					zf.close();
				}
				catch ( IOException e )
				{
					//ignore
				}
			}
		}
	}

	private String getModuleId(ZipFile zf) throws IOException, ZipException, ArchiverException {
		ZipEntry modulePropertiesEntry = zf.getEntry("module.properties");
		Properties moduleProperties = new Properties();
		moduleProperties.load(zf.getInputStream(modulePropertiesEntry));
		String moduleId = moduleProperties.getProperty("module.id");
		if(moduleId == null || "".equals(moduleId))
			throw new ArchiverException("module.id property not found in module.properties");
		return moduleId;
	}


	private String getAmpMapping(String name) {
		if(name.startsWith("web/") && !name.startsWith("web/licenses"))
		{
			return name.substring(4);
		}
		
		for (Map.Entry<String, String> mapElelement : ampMapping.entrySet()) {
			if(name.startsWith(mapElelement.getKey()))
			{
				String relativePath = "";
				
				if((name.startsWith("config/")))
				{
					relativePath = name.substring(7);
				}
				else
					relativePath = FileUtils.removePath(name);

				return mapElelement.getValue() + relativePath; 
			}
				
		}
		return "";
	}


	private void createAmpMapping(String moduleId) {
		File zipFile = getSourceFile();

		String ampName = zipFile.getName();
		ampName = FileUtils.removeExtension(FileUtils.removePath(ampName));
		ampName = ampName.substring(0, ampName.lastIndexOf('-'));
		
		ampMapping.put("module.properties", "WEB-INF"+ fileSeparator +"classes" + fileSeparator +"alfresco" + fileSeparator +"module" + fileSeparator + moduleId + fileSeparator);
		ampMapping.put("config", "WEB-INF"+fileSeparator+"classes"+ fileSeparator);
		ampMapping.put("lib", "WEB-INF" + fileSeparator + "lib" +fileSeparator);
		ampMapping.put("web"+ fileSeparator +"licenses", "WEB-INF" +fileSeparator);
		
	}

	private void extractFileIfIncluded( File sourceFile, File destDirectory, InputStream inputStream, String name,
			Date time, boolean isDirectory )
	throws IOException, ArchiverException
	{
		try
		{
			if ( filterSupport == null || filterSupport.include( inputStream, name ) )
			{
				extractFile( sourceFile, destDirectory, inputStream, name, time, isDirectory );
			}
		}
		catch ( ArchiveFilterException e )
		{
			throw new ArchiverException( "Error verifying \'" + name + "\' for inclusion: " + e.getMessage(), e );
		}
	}

	protected void extractFile( File srcF, File dir, InputStream compressedInputStream, String entryName,
			Date entryDate, boolean isDirectory )
	throws IOException
	{
		File f = null;
		if (entryName != null && !"".equals(entryName))
			f = FileUtils.resolveFile( dir, entryName );
		else
			return;
		
		try
		{
			if ( !isOverwrite() && f.exists() && f.lastModified() >= entryDate.getTime() )
			{
				getLogger().debug( "Skipping " + f + " as it is up-to-date" );
				return;
			}

			getLogger().debug( "expanding " + entryName + " to " + f );
//			create intermediary directories - sometimes zip don't add them
			File dirF = f.getParentFile();
			if ( dirF != null )
			{
				dirF.mkdirs();
			}

			if ( isDirectory )
			{
				f.mkdirs();
			}
			else
			{
				byte[] buffer = new byte[1024];
				int length;
				FileOutputStream fos = null;
				try
				{
					fos = new FileOutputStream( f );

					while ( ( length = compressedInputStream.read( buffer ) ) >= 0 )
					{
						fos.write( buffer, 0, length );
					}

					fos.close();
					fos = null;
				}
				finally
				{
					if ( fos != null )
					{
						try
						{
							fos.close();
						}
						catch ( IOException e )
						{
//							ignore
						}
					}
				}
			}

			f.setLastModified( entryDate.getTime() );
		}
		catch ( FileNotFoundException ex )
		{
			getLogger().warn( "Unable to expand to file " + f.getPath() );
		}
	}

	public void setArchiveFinalizers( List archiveFinalizers )
	{
		this.finalizers = archiveFinalizers;
	}

	protected void runArchiveFinalizers()
	throws ArchiverException
	{
		if ( finalizers != null )
		{
			for ( Iterator it = finalizers.iterator(); it.hasNext(); )
			{
				ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

				finalizer.finalizeArchiveExtraction( this );
			}
		}
	}
}
