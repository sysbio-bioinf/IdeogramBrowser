package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.junit.Assert;

/**
 * Iterates over a set of sorted lists.
 * 
 * 
 * @author mueller
 *
 * @param <T>
 */
public class SortedMultiIterator< T extends Comparable<T> > 
implements Iterator<T>
{
	Collection<Iterator<T>>		iterators;
	Vector<T>					current;
	
	/**
	 * All iterators of the input argument must be ascending
	 * 
	 * @param iterators An array of iterators
	 */
	public SortedMultiIterator( Collection<Iterator<T>> iterators )
	{
		Assert.assertNotNull(iterators);
		this.iterators = iterators;
		current = new Vector<T>(iterators.size());
		current.setSize(iterators.size());
	}
	

	public boolean hasNext()
	{		
		int i=0;
		for( Iterator<T> iter : iterators )
		{
			if( (current.get(i) != null) || iter.hasNext() ) return true;
			
			++i;
		}
		return false;
	}

	/**
	 * Ensures that all values are updated.
	 *
	 */
	private void updateCurrent()
	{
		int i=0;
		for( Iterator<T> iter : iterators )
		{
			if( current.get(i)==null && iter.hasNext() )
			{
				current.set(i, iter.next() );
			}
			++i;
		}
	}

	/**
	 * 
	 * @return The current values of all iterators.
	 */
	public Vector<T> getCurrent()
	{
		updateCurrent();
		return current;
	}
	

	public void remove()
	{
		throw new IllegalAccessError("unimplemented");
	}
	
	public T next()
	{
		if( ! hasNext() )
			throw new NoSuchElementException();

		updateCurrent();
		
		// find minimum
		T min = null;
		int min_i = -1;
		for( int i=0; i<current.size(); ++i )
		{
			T t = current.get(i);
			if( t != null )
			{
				if( (min == null) || t.compareTo(min) < 0 )
				{
					min = t;
					min_i = i;
				}
			}
		}
		Assert.assertNotNull(min);
		Assert.assertTrue(min_i >= 0);
		current.set(min_i,null);
		
		return min;
	}
	
	/**
	 * 
	 * @return A list of smallest iterators which are equivalent
	 */
	public List<T> nextAll()
	{
		if( ! hasNext() )
			throw new NoSuchElementException();
		
		updateCurrent();
		
		LinkedList<T> lst = new LinkedList<T>();
		boolean[] useIt = new boolean[current.size()];
		
		Arrays.fill(useIt,false);
		for( int i=0; i<current.size(); ++i )
		{
			T t = current.get(i); 
			if( t != null )
			{
				boolean add_current = false;
				
				if( lst.size() == 0 )
				{
					add_current = true;
				}
				else
				{
					int cmp = t.compareTo( lst.getFirst() );
					
					if( cmp <= 0 ) add_current = true;
					if( cmp < 0 )
					{	// the current element i is smaller => clear list
						lst.clear();
						Arrays.fill(useIt,false);
						add_current = true;
					}
				}
				
				if( add_current )
				{
					lst.add( current.get(i) );
					useIt[i] = true;
				}
			}
		}
		
		// Mark all used iterators
		for( int i=0; i<useIt.length; ++i )
		{
			if( useIt[i] )
				current.set(i, null);
		}
		
		return lst;
	}
}
