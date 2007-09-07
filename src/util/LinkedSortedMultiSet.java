package util;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;

public class LinkedSortedMultiSet<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T>
{
	private LinkedList<T>	data;
	
	
	public LinkedSortedMultiSet()
	{
		data = new LinkedList<T>();
	}
	
	@Override
	public boolean add( T t )
	{
		if( t == null )
			throw new IllegalArgumentException();
		
		if( data.size() == 0 )
		{
			return data.add(t);
		}
		
		// try first/last
		if( t.compareTo(first()) <= 0 )
		{
			data.addFirst(t);
			return true;
		}
		
		if( t.compareTo(last()) >= 0 )
		{
			data.addLast(t);
			return true;
		}
		
		// sequential search for the insertion point
		int idx = 0;
		for( T e : data )
		{
			if( e.compareTo(t) > 0 )
			{
				data.add(idx,t);
				return true;
			}
			++idx;
		}
		
		return false;
	}
	
	
	
	
	@Override
	public Iterator<T> iterator()
	{
		return data.iterator();
	}

	@Override
	public int size()
	{
		return data.size();
	}

	public Comparator<? super T> comparator()
	{
		return null;
	}

	public T first()
	{
		return data.getFirst();
	}

	public SortedSet<T> headSet(T toElement)
	{
		return subSet(first(),toElement);
	}

	public T last()
	{
		return data.getLast();
	}

	public SortedSet<T> subSet(T fromElement, T toElement)
	{
		if( fromElement != null || toElement != null )
			throw new IllegalArgumentException();
		
		LinkedSortedMultiSet<T>  tmp = new LinkedSortedMultiSet<T>();
		
		boolean inChunk = false;
		for( T e : data )
		{
			if( inChunk )
			{
				if( e.compareTo(toElement) > 0 )
					break;
			} else
			{
				if( e.compareTo(fromElement) >= 0 )
					inChunk = true;
			}
			
			if( inChunk )
				tmp.data.add(e);
		}
		
		return tmp;
	}

	public SortedSet<T> tailSet(T fromElement)
	{
		return subSet(fromElement,last());
	}


}