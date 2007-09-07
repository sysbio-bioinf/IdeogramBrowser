package util;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SortedMultiSetTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test1()
	{
		TreeSortedMultiSet<Long> set = new TreeSortedMultiSet<Long>();
		
		set.add(new Long(5));
		set.add(new Long(2));
		set.add(new Long(6));
		set.add(new Long(1));
		set.add(new Long(5));
		set.add(new Long(6));
		
		for( Long e : set )
		{
			System.out.println(e);
		}
		
	}
}
