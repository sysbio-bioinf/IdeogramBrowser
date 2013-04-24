/*
 * Created on 26.07.2005
 *
 */
package ideogram.db;

import util.FileFormatException;
import util.GlobalConfig;
import ideogram.Marker;
import ideogram.MarkerCollection;
import ideogram.IProgressNotifier;
import ideogram.tree.Interval;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * 
 * Loads the NCBI gene database seq_gene.md
 * 
 * @author muellera
 *
 */
public class GeneDB
{
	
	protected MarkerCollection[] genes;
    
    public GeneDB()
    {
        clear();
    }
	
	public MarkerCollection[] getGenes()
	{
		return genes;
	}
	
	/**
	 * Creates a new set of empty IntervalTrees.
	 *
	 */
	public void clear()
	{
		genes =  new MarkerCollection[GlobalConfig.getInstance().getChromosomeCount()];
		for(int i=0; i<genes.length; ++i)
		{
			genes[i] = new MarkerCollection();
		}
	}
	
    public boolean read( Reader reader ) throws FileFormatException, IOException
    {
        return read(reader,null);
    }
    
    /**
     * Loads a gene database from a stream.
     * 
     * @param reader Input stream
     * @throws IOException
     * @throws FileFormatException
     */
	public boolean read(Reader reader, IProgressNotifier notifier )
		throws IOException, FileFormatException
	{
		if( notifier != null )
			notifier.setText("loading genes ...");
		
		clear();
		
		LineNumberReader in = new LineNumberReader(reader);
		
		boolean header = true;
		long geneCount = 0;
		Band band = new Band();

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
			
			String[] splitLine = line.split("\t");
			
			if( splitLine.length < 13 )
			    throw new FileFormatException("Not enough columns in line " + in.getLineNumber() );
			
			//CHROMO make this somehow dynamic. originally looking for "GENE" in colmn 11 and "reference" in colmn 12.

			if( splitLine[11].compareToIgnoreCase("GENE")==0 && splitLine[12].compareToIgnoreCase(GlobalConfig.getInstance().getGeneReferenceString())==0)
			{
                long gene_id;
                String gene_name;
                Interval interval;
                
			    // parse line
                String[] lst = splitLine[1].split("[|]");
                if( lst.length != 1 )
                    continue;
                
			    try
			    {
			        band.setChromosome(lst[0]);
                }
                catch( IllegalArgumentException e )
                {
                    continue;
                }
                    
                interval = new Interval();
			    interval.from	= Long.parseLong(splitLine[2]);
			    interval.to		= Long.parseLong(splitLine[3]);
			    if( ! splitLine[10].startsWith("GeneID:") )
			        throw new FileFormatException("Error in line "+in.getLineNumber()+" illegal gene id");				    
			    gene_id 		= Long.parseLong(splitLine[10].split(":")[1]);
			    gene_name		= splitLine[9];
				
				// insert new gene
			    if( band.chromosome < 1 || band.chromosome > genes.length )
			        throw new FileFormatException("Error in line "+in.getLineNumber()+" illegal chromosome");
			    
                Marker m = new Marker();
                
                m.interval = interval;
                m.info = new GeneInfo(gene_id,gene_name);
                
				genes[band.chromosome-1].add(m);
				
				++geneCount;
				if( (notifier != null) && (geneCount % 100 == 0) )
					notifier.setText("loading genes ... ("+(geneCount/1000)+"."+((geneCount%1000)/100)+"k)");
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
	
    /**
     * Structure containing all important gene information.
     * 
     * @author muellera
     *
     */
	public static class GeneInfo
	{
	    public long gene_id;
	    public String gene_name;
	    
	    public GeneInfo(long gene_id, String gene_name )
	    {
	        this.gene_id = gene_id;
	        this.gene_name = gene_name;
	    }
        
        public String toString()
        {
            return "GeneID:"+this.gene_id+"  '" + this.gene_name + "'";
        }
	}
}
