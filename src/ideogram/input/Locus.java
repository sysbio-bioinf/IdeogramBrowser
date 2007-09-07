package ideogram.input;

import ideogram.tree.Interval;


/**
 * A genomic locus: chromosome and start and stop basepairs.
 * 
 * @author mueller
 *
 */
public class Locus implements Comparable<Locus>
{
	public byte chromosome; // 1 ... 22, X=23, Y=24
	public Interval interval;
	//static final long	FIXED_SNP_SIZE	= 20;	// the size of a SNP
	
	public Locus()
	{
		chromosome = 0;
		interval = new Interval();		
	}
	
	public Locus(Locus locus)
	{
		chromosome = locus.chromosome;
		//interval = (Interval)locus.interval.clone();
		interval = new Interval(locus.interval);
	}
	
	public void setPosition( long position )
	{
		interval.from = position;
		interval.to = position;
	}
	
	public long getPosition()
	{
		return interval.from;
	}
	
	/*void setChromosome( Byte b )
	{
		chromosome=b;
		chromosome = 0;
		if( str.equalsIgnoreCase("X") )
		{
			chromosome = 23;
			return;
		}
		if( str.equalsIgnoreCase("Y") )
		{
			chromosome  = 24;
			return;
		}

		try {
			chromosome = Byte.parseByte(str);
		}
		catch( NumberFormatException e )
		{
		}
	}*/
	
	/*Byte getChromosome()
	{
		return chromosome;
		if( chromosome >= 1 || chromosome <= 22 )
			return Integer.toString(chromosome);
			
		if( chromosome == 23 )
			return "X";
		
		if( chromosome == 24 )
			return "Y";
			
		return "<NA>";
	}*/
	

	
	public int compareTo(Locus other) 
	{		
		if( chromosome < other.chromosome ) {
			return -1;
		}
		if( chromosome > other.chromosome ) {
			return +1;
		}
		if( getPosition() < other.getPosition() ) {
			return -1;
		}
		if( getPosition() > other.getPosition() ) {
			return +1;
		}
		
		return 0;			
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		switch(chromosome) {
		case 24:	buf.append("X");
			break;
		case 25:	buf.append("Y");
			break;
		default:
			buf.append(chromosome);
		}
		
		buf.append(" ");
		buf.append(interval.toString());
		return buf.toString();
	}
	
	/*public Object clone()
	{
		return (Object)new Locus(this);
	}*/

	/**
	 * 
	 * @param locus
	 * @return True if this locus builds a non-empty intersection with the given locus.
	 */
	public boolean intersects( Locus locus )
	{
		if( chromosome != locus.chromosome ) {
			return false;
		}
		
		return interval.intersects( locus.interval );
	}
}
