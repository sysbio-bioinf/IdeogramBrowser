/*
 * IdeogramBrowser/util/MathExt.java
 * 
 * Created on 16.06.2004
 * 
 */
package util;

/**
 * @author muellera
 */
public class MathExt
{
	public static int sign(int value)
	{
		if( value > 0 )
		{
			return +1;
		}
		else
		{
			return (value<0) ? -1 : 0;
		}
	}
}
