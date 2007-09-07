/*
 * Created on 01.06.2004
 *
 */
package ideogram.tree;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author muellera
 *
 */
public class IntervalTreeNode 
{
	Interval interval;
	LinkedList children;
	IntervalTreeNode parent;
	public Object content;
	
	public IntervalTreeNode()
	{
		parent = null;
		children = new LinkedList();
	}
	
	public IntervalTreeNode(Interval interval)
	{
		this();
		this.interval = interval;		 		
	}
	
	public Interval getInterval()
	{
		return interval;
	}
	
	
	
	public void accept(int depth,IntervalTreeVisitor visitor)
	{
		visitor.visit(depth,this);
		ListIterator iter = (ListIterator)children.iterator();
		while( iter.hasNext() )
		{
			((IntervalTreeNode)iter.next()).accept(depth+1,visitor);
		}		
	}
}
