/*
 * Created on 01.06.2004
 *
 */
package ideogram.tree;

import java.util.*;

/**
 * A tree with hierarchical intervals. Each child node must be fully contained
 * in its parent node interval.
 * 
 * @author muellera
 */
public class IntervalTree {
   
    protected IntervalTreeNode	root;
	
	public IntervalTree()
	{
		root = new IntervalTreeNode();
	}
	
	@SuppressWarnings("unchecked")
	protected IntervalTreeNode internalInsert(IntervalTreeNode node,Interval interval)
		throws InsertNodeException
	{
		if( node == null )
		{
			throw new InsertNodeException();
		}

		if( (node.equals(root)) || node.interval.contains(interval) )
		{
			ListIterator<IntervalTreeNode> iter = (ListIterator)node.children.iterator();
			IntervalTreeNode u = null;
			
			while( iter.hasNext() )
			{
				IntervalTreeNode child = (IntervalTreeNode)iter.next();
				if( child.interval.contains(interval) )
				{
					if( u != null )
					{
						throw new InsertNodeException("Cannot insert interval "+interval.toString()); 
					}
					if( child.interval.equals(interval) )
					{
						// replace interval if the given interval has a higher priority
						if( interval.order < child.interval.order )
						{
							return child;
						}
						else
						{
							return null;
						}
					}
					else
					{
						return internalInsert(child,interval);
					}
				}
				else
				{
					if( interval.contains(child.interval) )
					{
						// replace node with the given interval 
						// -> the current node will be a child of the new interval
						if( u == null )
						{
							u = new IntervalTreeNode(interval);
							u.parent = child.parent;
							child.parent = u;
							u.children.add(child);
							iter.set(u);
						}
						else
						{
							child.parent = u;
							u.children.add(child);
							iter.remove();
						}			
					}
				}
			}
			if( u != null )
			{
				return u;
			}
			// no children found -> so add new child to this node
			u = new IntervalTreeNode(interval);
			u.parent = node;
			node.children.add(u);
			return u;
		}
		else
		{
			if( interval.contains(node.interval) )
			{
				// replace node with this node
				IntervalTreeNode u = new IntervalTreeNode(interval);
				u.parent = node.parent;
				u.children.add(node);
				node.parent = u;
				return u;
			}
		}
		throw new InsertNodeException();
	}
	
	/**
	 * Inserts a new interval into the tree so that each children will be contained in its 
	 * parent interval.
	 * 
	 * @param interval
	 * @return The new generated interval node.
	 * @throws InsertNodeException
	 */
	public IntervalTreeNode insert(Interval interval)
		throws InsertNodeException
	{
		if( root == null )
		{
			root = new IntervalTreeNode();
		}
		return internalInsert(root,interval);
	}
	
	/**
	 * Recursively applies for each node the given visitor.
	 * 
	 * @param visitor
	 * @see IntervalTreeVisitor
	 */
	public void accept(IntervalTreeVisitor visitor)
	{
		if( root != null )
		{
			root.accept(0,visitor);
		}
	}
	
	
	/**
	 * 
	 * @return And interval specifying the whole range of this tree.
	 */
	public Interval getRange()
	{
		GetRangeVisitor visitor = new GetRangeVisitor();
		
		accept(visitor);
		
		return visitor.getRange();
	}
	
	private class GetRangeVisitor implements IntervalTreeVisitor
	{
		private Interval range;
		
		public GetRangeVisitor()
		{
			range = null;
		}
		
		/* (non-Javadoc)
		 * @see ideogram.tree.IntervalTreeVisitor#visit(int, ideogram.tree.IntervalTreeNode)
		 */
		public void visit(int depth, IntervalTreeNode node)
		{
			if( range == null )
			{
				range = node.interval;
			}
			else
			{
				range = range.union(node.interval);			
			}
		}
		
		public Interval getRange()
		{
			return range;
		}
		
	}
}
