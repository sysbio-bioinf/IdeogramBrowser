package util;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinkedSortedMultiSetTest
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
		LinkedSortedMultiSet<Long> S = new LinkedSortedMultiSet<Long>();
		
		S.add( new Long(1) );
		S.add( new Long(2) );
		S.add( new Long(2) );
		S.add( new Long(3) );
		S.add( new Long(3) );
		S.add( new Long(3) );
		S.add( new Long(10) );
		S.add( new Long(4) );
		S.add( new Long(12) );
		S.add( new Long(4) );

		
		for( Long e : S )
		{
			System.out.println(e);
		}
	}
}
