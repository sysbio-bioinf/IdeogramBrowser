/*
 * Created on 08.06.2004
 *
 */
package util;

import java.util.*;

/**
 * @author muellera
 *
 */
public interface Queue
{
	public void add(Object object);
	public Object get() throws NoSuchElementException;
	public int size();
	public void clear();
	public Iterator iterator();	
}
