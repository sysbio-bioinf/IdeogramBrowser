package util;


import ideogram.tree.Interval;

import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SweepLineIteratorTest
{
	LinkedList<Interval>		list1;
	
	
	@Before
	public void setUp() throws Exception
	{
		list1 = new LinkedList<Interval>();
		
		list1.add( new Interval(0,2) );
		list1.add( new Interval(3,4) );
		list1.add( new Interval(5,6) );
		list1.add( new Interval(5,6) );
		list1.add( new Interval(5,8) );
		list1.add( new Interval(7,10) );
		list1.add( new Interval(9,10) );
		
		
		
		/*
		list1.add( new Interval(0,2) );
		list1.add( new Interval(5,8) );
		list1.add( new Interval(7,10) );
		list1.add( new Interval(5,6) );
		list1.add( new Interval(9,10) );
		list1.add( new Interval(3,4) );
		list1.add( new Interval(5,6) );
		*/
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void test1()
	{
		for( Interval i : list1 )
		{
			System.out.println(i);
		}
		System.out.println();

		SweepLineIterator<Interval> iter = new SweepLineIterator<Interval>(list1.iterator());
		
		while( iter.hasNext() )
		{
			SweepLineIterator<Interval>.CutLine cut = iter.next();
			
			System.out.print("["+cut.from+","+cut.to+"]   =>   ");
			for( Interval i : cut.result )
			{
				System.out.print( i.toString() + " ");
			}
			System.out.println();
		}
	}
}
