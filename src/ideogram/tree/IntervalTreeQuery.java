/*
 * Created on 01.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram.tree;

import java.util.*;

/**
 * Find nodes in an IntervalTree.
 * 
 * @author muellera
 *
 */
public class IntervalTreeQuery  
{
	protected IntervalTree tree;
	
	public IntervalTreeQuery(IntervalTree tree)
	{
        if( tree == null )
            throw new IllegalArgumentException("non null argument required");
		this.tree = tree;	
	}
	
	/**
	 * Finds all node intersecting the given Interval <var>interval</var>
	 * and with a level between <var>minLevel</var> and <var>maxLevel</var>. 
	 * @param interval
	 * @param minLevel
	 * @param maxLevel
	 * @return A list of IntervalTreeNodes
	 */
	public ArrayList Query(Interval interval, byte minLevel, byte maxLevel)
	{
		FilterVisitor visitor = new FilterVisitor(interval,minLevel,maxLevel);
		
		// start query
		tree.accept(visitor);
		
		return visitor.getQueryResult();
	}
	
	protected class FilterVisitor
		implements IntervalTreeVisitor
	{
		
		ArrayList<IntervalTreeNode> list;
		Interval interval;
		byte minLevel, maxLevel;
		
		FilterVisitor( Interval interval, byte minLevel, byte maxLevel )
		{
			if( interval == null )
			{
				throw new IllegalArgumentException("interval must not be null");
			}
			list = new ArrayList<IntervalTreeNode>();
			this.interval = interval;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
		}
		
		public ArrayList getQueryResult()
		{
			return list;
		}
		
		/* (non-Javadoc)
		 * @see IntervalTree.IntervalTreeVisitor#visit(int, IntervalTree.IntervalTreeNode)
		 */
		public void visit(int depth, IntervalTreeNode node) 
		{
            if( node == null )
                return;
            
            if( node.interval == null )
                return;
            
			if( (depth>=minLevel) && (depth<=maxLevel) && node.interval.intersects(interval) )
			{
				list.add(node);
			}
		}
	}
}
