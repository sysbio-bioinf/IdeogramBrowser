/*
 * Created on 01.06.2004
 *
 * Encapsulates a chromosomal basepair interval.
 */
package ideogram.tree;

import util.IntervalScale;

/**
 * @author muellera
 *
 */
public class Interval implements IntervalScale<Interval>
{
	
    public long from;
    public long to;

	/**
	 * The priority of this interval. If there are two equivalent intervals the order 
	 * value declares which interval is "bigger" than the other.
	 * The smaller the order the higher is the priority. 
	 */
	public long order;

	public Interval() 
	{
		from = 0;
		to = 0;
		order = 0;
	}

	public Interval(long from, long to) 
	{
		this.from = Math.min(from, to);
		this.to = Math.max(from, to);
		this.order = 0;
	}

	public Interval(long from, long to, long order) 
	{
		this.from = Math.min(from, to);
		this.to = Math.max(from, to);
		this.order = order;
	}
	
    public Interval( Interval interval )
	{
    	this(interval.from,interval.to,interval.order);
	}

	/**
     * 
     * @return The length of the interval.
     */
	public long getLength()
	{
		return (to-from);
	}

    /**
     * 
     * @param x
     * @return True if the value x lies in the interval [A,B]
     */
    public boolean containsValue(long x)
    {
        return (from <= x) && (x <= to);
    }

	/**
	 *  
	 * @param I
	 * @return Returns true if this interval contains I. 
	 */
	public boolean contains(Interval I) 
	{
		return (from <= I.from) && (I.to <= to);
	}

	/**
	 * 
	 * @param I
	 * @return Returns true if the two intervals intersects.
	 */
	public boolean intersects(Interval I) 
	{
		return (I.from <= to) && (I.to >= from);
	}
	
	public String toString()
	{
		return "[" + from + "," + to + "]"; 
	}
	
	public boolean equals(Object obj)
	{
		Interval I = (Interval)obj;
		return (I.from == from) && (I.to == to);
	}
	
	public int compareTo(Interval interval)
	{
		if( from < interval.from )	return -1;
		if( from > interval.from ) return +1;
		if( to < interval.to ) return -1;
		if( to > interval.to ) return +1;
		if( order < interval.order ) return -1;
		if( order > interval.order ) return +1;
		return 0;
	}
	
	public Interval union(Interval interval)
	{
		return new Interval(Math.min(from,interval.from),
							Math.max(to,interval.to));
	}

    /**
     * 
     * @param x
     * @return The distance of a given point to the interval borders 
     *  min(|x-A|,|x-B|).
     */
    public long borderDist(long x) 
    {
        return Math.min(Math.abs(x-from),Math.abs(x-to));
    }

    /**
     * 
     * @param x
     * @return The distance of a given point to an interval which is 0 if x is in [A,B]
     * or the smaller distance from the ends of the interval: min(|x-A|,|x-B|).
     */
    public long distance(long x) 
    {
        if(containsValue(x))
            return 0;
        return Math.min(Math.abs(x-from),Math.abs(x-to));
        //return Math.abs(x-(from+to)/2);
    }
    
    /*public Object clone()
    {
    	return (Object)new Interval(this);
    }*/

	public Interval intersection(Interval interval)
	{
		if( ! intersects(interval) )
			return null;
		
		Interval ret = new Interval();
		ret.from = Math.max( from, interval.from );
		ret.to = Math.min( to, interval.to );
		
		return ret;
	}

	public long from()
	{
		return from;
	}

	public long to()
	{
		return to;
	}

	public long getCenter()
	{
		return (from+to)/2;
	}
}
