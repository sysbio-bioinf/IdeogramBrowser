/*
 * Created on 02.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram.db;

import util.FileFormatException;
import ideogram.tree.*;
import java.io.*;

/**
 * @author muellera
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IdeogramDBTest {
	public static void main(String[] argv)
	{
		IdeogramDB db = new IdeogramDB();
		
		try 
		{
			db.LoadFromFile("c:\\tmp\\ideogram");
			
		}
		catch(IOException e)
		{
			System.err.println(e.toString());			
		}
		catch(FileFormatException e)
		{
			System.err.println(e.toString());	
		}
		
		OutputStreamVisitor visitor = new OutputStreamVisitor();
		for(int i=0; i<db.getTree().length; ++i)
		{
			System.out.println("<<<<<<<<<< CHROMOSOME "+i+" >>>>>>>>>>>>");
			db.getTree()[i].accept(visitor);
		}		
	}

}
