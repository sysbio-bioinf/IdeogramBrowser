package util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator which can step back a single step with the method {@link #undo()}
 * 
 * @author mueller
 *
 * @param <Iterator<T>>
 */
public class UndoIterator< T > implements Iterator<T>
{
	private Iterator<T>	inputIterator;
	T					current;
	int					filled;		// number of items in the undo buffer
	int					undoSteps;	// number of undo steps to be executed

	public UndoIterator( Iterator<T> inputIterator )
	{
		this.inputIterator = inputIterator;

		current = null;
		filled = 0;
		undoSteps = 0;
	}

	public boolean hasNext()
	{
		return (undoSteps>0) || inputIterator.hasNext();
	}

	public T next()
	{
		if( undoSteps > 0 )
		{
			--undoSteps;
			++filled;
		} else
		{
			filled = 1;
			current = inputIterator.next();			
		}
		return current;
	}

	public void remove()
	{
		if( undoSteps > 0 )
			throw new IllegalStateException();

		inputIterator.remove();
	}
	
	public void undo()
	{
		if( undoSteps+1 > filled )
			throw new NoSuchElementException();
		
		++undoSteps;
		--filled;
	}
}
