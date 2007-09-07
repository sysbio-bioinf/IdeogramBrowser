package ideogram.input;

import ideogram.tree.Interval;

public class GenomicMarker implements Comparable<GenomicMarker>
{
	/**
	 * Basepair range.
	 */
	public Interval interval = null;
	
	private Object	info;

	public GenomicMarker( )
	{
	}

	public GenomicMarker( Interval interval )
	{
		this.interval = interval;
	}

	public GenomicMarker( Interval interval, Object info)
	{
		this.interval = interval;
		this.info = info;
	}
	

	public int compareTo(GenomicMarker other)
	{
		return interval.compareTo( other.interval );
	}
	
	public Object getInfo() {
		return info;
	}

	public Interval getInterval()
	{
		return interval;
	}
}
