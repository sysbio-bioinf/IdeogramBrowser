/*
 * Created on 08.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 * @author muellera
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LinkedQueue implements Queue
{
	protected LinkedList<Object> objects;	

	LinkedQueue()
	{
		objects = new LinkedList<Object>();
	}

	/* (non-Javadoc)
	 * @see util.Queue#add(java.lang.Object)
	 */
	public void add(Object object)
	{
		objects.addLast(object);
	}

	/* (non-Javadoc)
	 * @see util.Queue#retrieve()
	 */
	public Object get() throws NoSuchElementException
	{
		return objects.getFirst();
	}

	/* (non-Javadoc)
	 * @see util.Queue#size()
	 */
	public int size()
	{
		return objects.size();
	}

	/* (non-Javadoc)
	 * @see util.Queue#clear()
	 */
	public void clear()
	{
		objects.clear();

	}

	/* (non-Javadoc)
	 * @see util.Queue#iterator()
	 */
	public Iterator iterator()
	{
		return objects.iterator();
	}

}
