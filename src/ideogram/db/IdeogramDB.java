/*
 * Created on 01.06.2004
 *
 */
package ideogram.db;

import util.FileFormatException;
import ideogram.IProgressNotifier;
import ideogram.tree.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Database class for the NCBI ideogram database.
 * 
 * @author muellera
 *
 */
public class IdeogramDB
	implements Serializable 
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int NUM_TREES = 24; // = number of chromosoms
	
	protected IntervalTree[] tree;

	public IdeogramDB()
	{
		clear();
	}
	
	public IntervalTree[] getTree()
	{
		return tree;
	}
	
	/**
	 * Creates a new set of empty IntervalTrees.
	 *
	 */
	public void clear()
	{
		tree =  new IntervalTree[NUM_TREES];
		for(int i=0; i<tree.length; ++i)
		{
			tree[i] = new IntervalTree();
		}
	}
	
    /**
     * 
     * @param reader
     * @return True on success
     * @throws IOException 
     * @throws FileFormatException 
     */
    public boolean read(Reader reader) throws FileFormatException, IOException
    {
        return read(reader,null);
    }
    
    /**
     * 
     * @param reader
     * @param notifier
     * @return True on sucess.
     * @throws IOException
     * @throws FileFormatException
     */
	public boolean read(Reader reader, IProgressNotifier notifier )
		throws IOException, FileFormatException
	{
		clear();
		
		LineNumberReader in = new LineNumberReader(reader);
		
		boolean header = true;

		Label:
		while( in.ready() )
		{
            if( notifier != null && notifier.isCancelled() )
            {
                clear();
                return false;
            }
			String line = in.readLine();
			if( header )
			{
				header = false;
				continue;
			}
			line.trim();
			
			// workaround for empty columns see below.
			// check only for unusual entries
			// StringTokenizer(line,"\t",true) didn't work, because it does't count empty entries too.
			StringTokenizer tok = new StringTokenizer(line,"\t");
			int field = 0;
			Band band = new Band();
			Interval interval = new Interval();
			while( tok.hasMoreTokens() )
			{
				String str = tok.nextToken().trim();
				if( str.length() > 0 )
				{
					try {
						switch(field) 
						{
							case 0: // chromosome
                                if( str.length() == 0 )
                                    continue Label;
								band.setChromosome(str);
								break;
								
							case 1: // arm
                                if( str.length() == 0 )
                                    continue Label;
                                if( str.charAt(0) == 'p' || str.charAt(0) == 'q' )
                                    band.setArm(str);
                                else
                                    continue Label;
								break;
								
							case 2: // band
                                if( str.length()==0 )
                                    continue Label;
								if( str.equalsIgnoreCase("0") )
                                    continue Label;
                                if( ! Character.isDigit(str.charAt(0)) )
                                    continue Label;
								int idx = str.indexOf(".");
								char subsub = str.charAt(str.length()-1);
								int idx2 = str.length();
								if(idx<0 && str.length()>2 && Character.isDigit(subsub))
									continue Label;
								if( ! Character.isDigit(subsub) )
								{
									band.setSubSubBand(subsub);
									idx2 = idx2-1;
								}								
								if( idx >= 0 )
								{
									band.setBand(str.substring(0,idx));
									band.setSubBand(str.substring(idx+1,idx2));
								}
								else
								{
									band.setBand(str.substring(0,idx2));
								}
								break;
								
							case 3: // ISCN-top
								break;
								
							case 4: // ISCN-bot
								break;
								
							case 5: // Bases-top
								interval.from = Long.parseLong(str);
								break;
								
							case 6: // Bases-bot
								interval.to = Long.parseLong(str);
								break;
								
							case 7: // stain density
								if(str.length()==0)
									continue Label;
								if( Character.isDigit(str.charAt(0)) )
                                    continue Label;
								band.setDensity(str);
								break;
								
							//default:
							//	throw new FileFormatException("Too many columns in line " + in.getLineNumber() );		
						}
					}
					catch( IllegalArgumentException e )
					{
						throw new FileFormatException("Error in line "+in.getLineNumber() + " field " + (field+1) );
					}
					++field;
				}
			}
			try 
			{
				interval.order = band.getResolution();
				IntervalTreeNode node = tree[band.chromosome-1].insert(interval);
				if( node != null )
				{
					node.content = band;
				}
			}
			catch( InsertNodeException e )
			{
				throw new FileFormatException("Invalid interval combination in line "+in.getLineNumber());
			}
		}
        return true;
	}
	
	public void LoadFromFile(String filename)
		throws IOException, FileFormatException
	{	
	    if( filename.endsWith(".gz") )
	        read(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
	    else
	        read(new FileReader(filename));
	}
}
