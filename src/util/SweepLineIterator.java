package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;

import org.junit.Assert;

/**
 * Iterates over a set of sorted interval lists.
 * 
 * T must implement the interfaces {@link IntervalScale} and {@link java.util.Comparable}
 * @author mueller
 *
 * @param <T>
 */
public class SweepLineIterator< T extends IntervalScale<T> > 
implements Iterator< SweepLineIterator<T>.CutLine >
{
	/**
	 * Represents a cut line of the line sweep. Each cut consists of an interval 
	 * [from,to] with an associated (possible empty) list of elements.
	 * 
	 * @author mueller
	 *
	 */
	public class CutLine implements IntervalScale<CutLine>
	{
		public final long 	from;
		public final long	to;
		public final SortedSet<T> result;

		public CutLine(long from, long to, SortedSet<T> result )
		{
			this.from = from;
			this.to = to;
			this.result = result;
		}

		public long from()
		{
			return from;
		}

		public long to()
		{
			return to;
		}
		
		public SortedSet<T> result()
		{
			return result;
		}

		public int compareTo(CutLine c)
		{
			if( from < c.from() ) {
				return -1;
			}
			if( from > c.from() ) {
				return +1;
			}
			
			if( to < c.to() ) {
				return -1;
			}
			                   
			if( to > c.to() ) {
				return +1;
			}
			
			return 0;
		}
	}
	/**
	 * SweepLine event
	 * @author mueller
	 *
	 */
	private class Event implements Comparable<Event>
	{
		public final long		position;
		public final boolean 	isBegin;	// if true the interval T begins
											// if false the interval T stops
		public final T			record;
		
		public Event( long position, boolean isBegin, T record )
		{
			this.position = position;
			this.isBegin = isBegin;
			this.record = record;
		}

		public int compareTo(Event o)
		{
			if( position < o.position ) return -1;
			if( position > o.position ) return +1;
			return 0;
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append(position);
			if( isBegin )
				buf.append(" + ");
			else
				buf.append(" - ");
			buf.append(record.toString());
			return buf.toString();
		}
	};

	
	private UndoIterator<T>			inputIterator;
	private TreeSortedMultiSet<T> 	cut;
	private PriorityQueue<Event>	queue;			// queue of pending events
	
	public SweepLineIterator( Iterator<T> iterator )
	{
		inputIterator = new UndoIterator<T>( iterator );
		cut = new TreeSortedMultiSet<T>();
		queue = new PriorityQueue<Event>();
	}

	public boolean hasNext()
	{
		return !queue.isEmpty() || inputIterator.hasNext();
	}

	
	/**
	 * Ensures that all values are updated.
	 *
	 */
	private void updateQueue()
	{	
		while( inputIterator.hasNext() )
		{
			T t = inputIterator.next();
			
			Assert.assertNotNull( t );
			
			if( queue.isEmpty() || ( t.from() <= to() ) )
			{	
				queue.add( new Event(t.from(),true,t) );	// begin
				queue.add( new Event(t.to(),false,t) );		// end
			}
			else
			{
				inputIterator.undo();
				break;
			}
		}
	}

	public void remove()
	{
		throw new IllegalAccessError("unimplemented");
	}
	
	/**
	 * Do NOT change the return value!!
	 * 
	 * @return A list of records (the smallest first). 
	 */
	public CutLine next()
	{
		if( ! hasNext() )
			throw new NoSuchElementException();
		
		updateQueue();
		
		long from = Long.MIN_VALUE,
			to = Long.MAX_VALUE;

		if( ! queue.isEmpty() )
			from = queue.peek().position;
				
		while( !queue.isEmpty() )
		{
			Event e = queue.peek();
			
			if( e.position > from)
			{
				to = e.position;
				break;
			}

			if( e.isBegin )
			{	// insert interval
				cut.add( e.record );
			}
			else
			{	// remove interval
				cut.remove( e.record );
			}
			
			queue.remove();	// remove event
		}

		return new CutLine( from, to, cut );
	}
	
	public PriorityQueue<Event> getQueue()
	{
		return queue;
	}
	
	private long to()
	{
		if( queue.isEmpty() )
			return Long.MAX_VALUE;
		
		long from = queue.peek().position;
		
		for( Event e : queue )
		{
			if( e.position > from )
				return e.position;
		}
		return Long.MAX_VALUE;
	}
}
