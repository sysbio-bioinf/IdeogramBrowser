package ideogram.input;

import ideogram.tree.Interval;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import util.FileFormatException;
import util.GlobalConfig;

public class TabbedTextReaderModel extends AbstractCopyNumberDataModel 
{

	
	private TreeSet<CopyNumberRecord>	 data;
	CopyNumberRecord[]					cache;

	private boolean 	valid;			// valid state
	private String		fileName; 		// *.txt file name
	
    public TabbedTextReaderModel() 
    {
        super();
        
        data = new TreeSet<CopyNumberRecord>();
        cache = null;
		valid = false;
		fileName = new String();
    }

	/**
	 * Read "Tabbed Text File" format.
	 *   
	 * @param the *txt file
	 */
	public void loadFromFile(File file)
		throws IOException, FileFormatException
	{
		clear();
		String filePath = file.getAbsolutePath(); 
		readElements(new FileReader(filePath));
		fileName = file.getName();
		fireChangeEvent();
	}	
	
	public void clear()
	{
		data.clear();
		valid = false;
		fileName = new String();
	}
    
    public boolean isValid()
    {
        return valid;
    }

	private void updateCache()
	{
		if( data == null )
		{
			cache = null;
			return;
		}
		if( (cache == null) || (cache.length != data.size()) )
		{
			cache = new CopyNumberRecord[ data.size() ];
		}
		data.toArray(cache);
	}

    public void validate()
    {
    	if( ! valid )
    	{
    		updateCache();
    		valid = true;
    	}
    	
    }
	
    /**
     * Loads a marker collection from a stream.
     * The input stream must be a tab delimited ASCII format with the following columns
     * CHROMOSOME 1..22,X,Y
     * START_BP   integer > 0
     * STOP_BP    integer > 0
     * NAME       string
     * VALUE      integer
     * 
     * @param reader
     * @throws IOException
     * @throws FileFormatException
     */
    public void readElements(Reader reader)
    throws IOException, FileFormatException
    {
    	invalidate();
        LineNumberReader in = new LineNumberReader(reader);
        while( in.ready() )
        {
            String line = in.readLine();
            line.trim();
            String[] L = line.split("\t");
            
            boolean empty = true;
            for(int i=0; i<L.length && empty; ++i )
            {
                if( L[i].trim().length() > 0 )
                    empty = false;
            }
            if( empty ) // ignore empty lines
                continue;
            
            if( L.length != 5 )
                throw new FileFormatException("Error: line "+ in.getLineNumber() + " has " +
                                L.length+" columns (should be 5)");
            
        	CopyNumberRecord record = new CopyNumberRecord();
        	
      	  if(L[0].equals("X")) record.locus.chromosome=GlobalConfig.getInstance().getXChromosomeNr();
       	  else if(L[0].equals("Y")) record.locus.chromosome=GlobalConfig.getInstance().getYChromosomeNr();
       	  else record.locus.chromosome =  Byte.parseByte(L[0]) ;

        	if( record.locus.chromosome == 0 )
        	{
        		throw new FileFormatException("Error in line "+in.getLineNumber()+" illegal chromosome '"+L[0]+"'");
        	}
            try {
                record.locus.interval = new Interval(Long.parseLong(L[1]),Long.parseLong(L[2]));
                record.copy_number = (int)Double.parseDouble(L[4]);
            }
            catch( NumberFormatException ex )
            {
                throw new FileFormatException("Error in line "+in.getLineNumber()+" illegal entry.");
            }
            record.info = L[3];
            
            data.add(record);
        }
    }
    
	private void invalidate() 
	{
		valid = false;
		
	}

	public LinkedList<String> getFileName()
	{
		LinkedList<String> l = new LinkedList<String>();
		l.add(fileName);
		return l;
	}
	
	public CopyNumberRecord get(int idx)
	{
		validate();
		if( cache == null || data == null )
			throw new IllegalStateException("no records available");
		
		if( idx < 0 || idx >= cache.length )
			throw new IndexOutOfBoundsException("Tried to access index "+idx+"in an array with size"+cache.length);
		return cache[idx];
	}
	
	public String getMarkerName(int idx)
	{
		return get(idx).info;
	}


	public byte getChromosome(int idx)
	{
		return get(idx).locus.chromosome;
	}

	
	public Interval getInterval(int idx)
	{
		return get(idx).locus.interval;
	}
	
		
	public int size()
	{
		return data.size();
	}


	public double getConfidence(int idx)
	{
		return 0;
	}

	public int getCopyNumber(int idx) 
	{
		return get(idx).copy_number;
	}

	public String getMarkerInfo(int i) 
	{
		return getMarkerName(i);
	}

	public byte getChromosomeIndex(int idx) 
	{
		return get(idx).locus.chromosome;
	}

	
	public Collection<CopyNumberRecord> toCollection()
	{
		return data;
	}
	
	public LinkedList<String> getHeader() {
		LinkedList<String> l = new LinkedList<String>();
		l.add("TabbedTextReader doesn't contain any header");
		return l;
	}

	public LinkedList<String> getChipType() {
		LinkedList<String> l = new LinkedList<String>();
		l.add("TabbedTextReader doesn't contain any ChipType");
		return l;
	}

	
}
