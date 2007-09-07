package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Iterates over multiple iterators
 * 
 * @author mueller
 *
 */
public class MultiIterator <T> implements Iterator<Vector<T> >
{
	Iterator<T>[]		iterators;
	
	public MultiIterator( Iterator<T>[] iterators )
	{
		this.iterators = iterators;
	}

	public boolean hasNext()
	{
		for( int i=0; i<iterators.length; ++i )
		{
			if( ! iterators[i].hasNext() ) {
				return false;
			}
		}
		return true;
	}

	public Vector<T> next()
	{
		if( !hasNext() )
			throw new NoSuchElementException();
		
		Vector<T> result = new Vector<T>(iterators.length);
		result.setSize(iterators.length);
		
		for( int i=0; i<iterators.length; ++i )
		{
			result.set(i, iterators[i].next());
		}
		
		return result;
	}

	public void remove()
	{
		for( int i=0; i<iterators.length; ++i )
		{
			iterators[i].remove();
		}
	}
	
}
