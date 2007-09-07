/*
 * IdeogramBrowser/ideogram/CommonFileFilter.java
 * 
 * Created on 21.06.2004
 * 
 */
package ideogram.input;
import java.io.File;
import java.util.*;
import javax.swing.filechooser.FileFilter;

/**
 * Easy standard file filter.
 * 
 * @author muellera
 */
public class CommonFileFilter extends FileFilter
{
	private String description;
	private LinkedList<String> extensions;

	public CommonFileFilter()
	{
		extensions = new LinkedList<String>();
	}

	public CommonFileFilter(String description)
	{
		this();
		this.description = description;
	}

	/**
	 * Adds an extension to this filter.
	 * @param ext
	 */
	public void addExtension(String ext)
	{
		extensions.add(ext);
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f)
	{
		if (f.isDirectory()) {
			return true;
		}
		Iterator iter = extensions.iterator();
		while(iter.hasNext())
		{
			String e = (String)iter.next();
			if( f.getName().endsWith(e))
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
}
