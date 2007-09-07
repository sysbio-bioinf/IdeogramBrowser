package util;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * A sorted container which can contain more equal objects.
 * 
 * @author mueller
 *
 * @param <T>
 */
public class TreeSortedMultiSet<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T>
{
	private class Record implements Comparable<Record> 
	{
		final long	id;
		final T		object;
		
		public Record( long id, T object )
		{
			this.id = id;
			this.object = object;
		}
		
		public int compareTo(Record o)
		{
			int t = object.compareTo(o.object);
			if( t != 0 ) return t;
			if( id < o.id ) return -1;
			if( id > o.id ) return +1;
			
			return 0;
		}
	}
	
	
	
	TreeSet<Record>		data;
	TreeMap<T,Long>		idMap;		// maps an object to an id, multiple equivalent objects gets different ids
	
	public TreeSortedMultiSet()
	{
		data = new TreeSet<Record>();
		idMap = new TreeMap<T,Long>();
	}
	

	public T first()
	{
		return data.first().object;
	}

	/**
	 * Unwraps a sorted set of Records.
	 * 
	 * @param s
	 * @return
	 */
	private SortedSet<T> unwrap( SortedSet<Record> s )
	{
		LinkedSortedMultiSet<T> tmp = new LinkedSortedMultiSet<T>();
		
		for( Record t : s )
		{
			tmp.add( t.object );
		}
		return tmp;
	}

	public SortedSet<T> headSet(T toElement)
	{
		return unwrap( data.headSet( new Record(Long.MAX_VALUE,toElement) ) );
	}

	public T last()
	{
		return data.last().object;
	}

	public SortedSet<T> subSet(T fromElement, T toElement)
	{
		return unwrap( 
				data.subSet(
						new Record(Long.MIN_VALUE,fromElement), 
						new Record(Long.MAX_VALUE,toElement)) );
	}

	public SortedSet<T> tailSet(T fromElement)
	{
		return unwrap( data.tailSet(new Record(Long.MIN_VALUE,fromElement) ));
	}

	@Override
	public boolean add(T o)
	{
		Long  id = idMap.get(o);
		long  new_id = 0;
		
		if( id != null )
		{
			new_id = id.longValue() + 1;
		}
		idMap.put(o, new_id);
		
		return data.add( new Record(new_id,o) );
	}

	@Override
	public void clear()
	{
		data.clear();
		idMap.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return idMap.containsKey( o );
	}

	@Override
	public Iterator<T> iterator()
	{
		return unwrap(data).iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove( Object o )
	{
		Long  id = idMap.get(o);
		if( id != null )
		{
			long i = id.longValue();
			data.remove( new Record(i,(T)o) );		// if true, success
			if( i > 0 )
			{
				idMap.put((T)o, i-1);
			}
			else
			{
				idMap.remove(o);
			}
			return true;
		}
		return false;
	}

	@Override
	public int size()
	{
		return data.size();
	}


	public Comparator<? super T> comparator()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
