package ideogram.input;

import java.util.LinkedList;

import util.IntervalScale;

/**
 * Represents a copy number
 * 
 * @author mueller
 *
 */
public class CopyNumberRecord implements IntervalScale<CopyNumberRecord>
{
	public int				id;					// data set id
	public Locus			locus;
	public int				copy_number;		// 0=double loss, 1=single loss, 2=diploid, 3=gain, 4=amplification
	public float			confidence;			// confidence value
	public String			info;
	public LinkedList<CopyNumberRecord>	ref;	// references to the origin

	public CopyNumberRecord()
	{
		id = -1;
		locus = new Locus();
		copy_number = -1;
		confidence = Float.NaN;
		info = "";
		ref = new LinkedList<CopyNumberRecord>();
	}
	
	@SuppressWarnings({ "unchecked" })
	public CopyNumberRecord( CopyNumberRecord record )
	{
		id = record.id;
		locus = new Locus(record.locus);
		copy_number = record.copy_number;
		confidence = record.confidence;
		info = record.info;
		ref = (LinkedList<CopyNumberRecord>)record.ref.clone();
	}
	
	
	/**
	 * Unknown copy_number
	 * 
	 * @return
	 */
	public boolean isIndifferent()
	{
		return copy_number < 0;
	}

	public int compareTo(CopyNumberRecord o)
	{
		return locus.compareTo(o.locus);
	}

	public long from()
	{
		return locus.interval.from;
	}

	public long to()
	{
		return locus.interval.to;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("LOCUS = "+locus.toString()+"\n");
		buf.append("CN    = "+copy_number+"\n");
		buf.append("CONF  = "+confidence+"\n");
		buf.append("INFO  = "+info+"\n");
		
		return buf.toString();
	}
}