package util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class SetUtil
{
	public static <T> Set<T> union( Collection< Set<T> > sets )
	{
		Set<T> result = new TreeSet<T>();

		for( Set<T> set : sets )
		{
			result.addAll( set );
		}
		
		return result;
	}
}
