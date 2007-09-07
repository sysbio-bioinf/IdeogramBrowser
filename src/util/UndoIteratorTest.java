package util;

import static org.junit.Assert.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class UndoIteratorTest
{
	LinkedList<Integer> list;
	
	@Before
	public void setUp()
	{
		list = new LinkedList<Integer>();
		list.add( new Integer(1) );
		list.add( new Integer(2) );
		list.add( new Integer(3) );
		list.add( new Integer(4) );
		
		
	}
	
	@Test
	public void test1()
	{
		UndoIterator<Integer> iter = new UndoIterator<Integer>(list.iterator());
		
		assertEquals( iter.next().intValue(), 1 );
		assertEquals( iter.next().intValue(), 2 );
		iter.undo();
		assertEquals( iter.next().intValue(), 2 );
		assertEquals( iter.next().intValue(), 3 );
		assertEquals( iter.next().intValue(), 4 );
		iter.undo();
		assertEquals( iter.next().intValue(), 4 );
		iter.undo();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next().intValue(), 4 );
		
		assertFalse( iter.hasNext() );		
	}
	
	@Test(expected=NoSuchElementException.class)
	public void test2()
	{
		UndoIterator<Integer> iter = new UndoIterator<Integer>(list.iterator());
		iter.undo();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void test3()
	{
		UndoIterator<Integer> iter = new UndoIterator<Integer>(list.iterator());
		iter.next();
		iter.next();
		iter.undo();
		iter.undo();
	}
	
	@Test(expected=IllegalStateException.class)
	public void test4()
	{
		UndoIterator<Integer> iter = new UndoIterator<Integer>(list.iterator());
		iter.next();
		iter.next();
		iter.undo();
		iter.remove();
	}	
	
}
