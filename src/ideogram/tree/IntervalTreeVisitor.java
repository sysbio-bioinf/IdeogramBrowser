/*
 * Created on 01.06.2004
 *
 */
package ideogram.tree;

/**
 * @author muellera
 *
 */
public interface IntervalTreeVisitor 
{
	public void visit(int depth, IntervalTreeNode node);
}
