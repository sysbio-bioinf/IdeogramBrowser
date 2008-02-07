/*
 * Created on 02.06.2004
 *
 */
package ideogram;

import ideogram.tree.Interval;
import java.awt.Color;

/**
 * An IdeogramMarker will be shown as colored lines in the Ideogram.
 * It has a chromosomal range {@link #interval} and a value {@link #value} 
 * 
 * @author muellera, Ferdinand Hofherr
 */
public class Marker implements Comparable
{
	/**
	 * Basepair range.
	 */
	public Interval interval;
	
	/**
	 * < 2 for loss (0 double loss, 1 single loss)
	 * 2 for normal (diploid)
	 * > 2 for gain (3 gain, 4 amplification)
	 */
	public int value;
	
	/*
	 * Store the log2 ratio belonging to this marker.
	 */
	private double log2ratio;
	
	/**
	 * An optional pointer to an information object.
	 */	
	public Object info;
	
	/**
	 * If color is set the marker will be drawn in a special color.
	 */
	public Color color;
	
	
	public Marker()
	{
	}
		
	public Marker(Interval interval, int value)
	{
		this.interval = interval;
		this.value = value; 
	}
	
	/**
	 * Create a new Marker with an associated copy number value and an 
	 * log2ratio.
	 *
	 * @param interval Start and stop position of the marker.
	 * @param value The marker's copy number value.
	 * @param log2ratio The marker's log2 ratio.
	 */
	public Marker(Interval interval, int value, double log2ratio) {
	    this(interval, value);
	    this.log2ratio = log2ratio;
	}
	
	/**
	 * Get the associated log2 ratio.
	 *
	 * @return The marker's log2 ratio.
	 */
	public double getLogRatio() {
	    return log2ratio;
	}
	
	/**
	 * Set the log2 ratio associated with this marker.
	 *
	 * @param log2Ratio
	 */
	public void setLog2Ratio(double log2Ratio) {
	    this.log2ratio = log2Ratio;
	}
	
	/*public Marker(long from, long to, int value)
	{
		this.interval = new Interval(from,to);
		this.value = value;
	}*/
	
	public void setInfo(Object info)
	{
		this.info = info;
	}
	
	public Object getInfo()
	{
		return info;
	}

    public int compareTo(Object obj) 
    {
        Marker m = (Marker)obj;
        if( interval != null )
        {
            if( m.interval != null ) {
                return interval.compareTo(m.interval);
            }
            else {
                return -1;
            }
        }
        
        return 0;
    }
}
