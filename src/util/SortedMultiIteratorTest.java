package util;


import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class SortedMultiIteratorTest
{
	private ArrayList<Integer>	list1;
	private ArrayList<Integer>  list2;
	private ArrayList<Integer>	list3;
	
	@Before
	public void setUp() throws Exception
	{
		list1 = new ArrayList<Integer>();
		
		list1.add(new Integer(1));
		list1.add(new Integer(2));
		list1.add(new Integer(5));
		list1.add(new Integer(7));
		list1.add(new Integer(10));
		
		
		list2 = new ArrayList<Integer>();
		list2.add(new Integer(0));
		list2.add(new Integer(1));
		list2.add(new Integer(3));
		list2.add(new Integer(6));
		list2.add(new Integer(7));
		
		list3 = new ArrayList<Integer>();
		list3.add( new Integer(4) );
		list3.add( new Integer(7) );
		

	}
	
	@Test public void test1() 
	{
		Vector<Iterator<Integer>> iters = new Vector<Iterator<Integer>>();
		iters.add( list1.iterator() );
		iters.add( list2.iterator() );
		iters.add( list3.iterator() );
		
		List< List<Integer> > expected;
		expected = new LinkedList< List<Integer> >();
		List<Integer> tt;
		
		tt = new LinkedList<Integer>();
		tt.add(0);
		expected.add(tt);
		
		tt = new LinkedList<Integer>();
		tt.add(1);
		tt.add(1);
		expected.add(tt);

		tt = new LinkedList<Integer>();
		tt.add(2);
		expected.add(tt);
 
		tt = new LinkedList<Integer>();
		tt.add(3);
		expected.add(tt);
		
		tt = new LinkedList<Integer>();
		tt.add(4);
		expected.add(tt);
		
		tt = new LinkedList<Integer>();
		tt.add(5);
		expected.add(tt);
		
		tt = new LinkedList<Integer>();
		tt.add(6);
		expected.add(tt);
		
		
		tt = new LinkedList<Integer>();
		tt.add(7);
		tt.add(7);
		tt.add(7);
		expected.add(tt);
		
		tt = new LinkedList<Integer>();
		tt.add(10);
		expected.add(tt);		
		
		SortedMultiIterator< Integer > miter = new SortedMultiIterator<Integer>(iters);
		
		List< List<Integer> > result = new LinkedList<List<Integer>>();
		
		int idx = 0;
		while( miter.hasNext() )
		{
			List<Integer> rec = miter.nextAll();
			
			result.add(rec);
			++idx;
		}

		assertTrue(expected.equals(result));
	}
	
	@Test public void test2() 
	{
		Vector<Iterator<Integer>> iters = new Vector<Iterator<Integer>>();
		iters.add( list1.iterator() );
		iters.add( list2.iterator() );
		iters.add( list3.iterator() );	
		
		LinkedList<Integer> expected = new LinkedList<Integer>();
		expected.add( new Integer(0) );
		expected.add( new Integer(1) );
		expected.add( new Integer(1) );
		expected.add( new Integer(2) );
		expected.add( new Integer(3) );
		expected.add( new Integer(4) );
		expected.add( new Integer(5) );
		expected.add( new Integer(6) );
		expected.add( new Integer(7) );
		expected.add( new Integer(7) );
		expected.add( new Integer(7) );
		expected.add( new Integer(10) );
		
		SortedMultiIterator< Integer > miter = new SortedMultiIterator<Integer>(iters);
		
		List< Integer > result = new LinkedList<Integer>();
		
		int idx = 0;
		while( miter.hasNext() )
		{
			Integer rec = miter.next();
						
			result.add(rec);
			++idx;
		}

		assertTrue(expected.equals(result));
	}
	
	
}
