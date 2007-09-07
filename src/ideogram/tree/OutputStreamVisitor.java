/*
 * Created on 01.06.2004
 *
 */
package ideogram.tree;

/**
 * @author muellera
 *
 */
public class OutputStreamVisitor
	implements IntervalTreeVisitor 
{
	public void visit(int depth, IntervalTreeNode node)
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<depth;++i)
		{
			buf.append("   ");
		}
		if( node.interval != null )
		{
			buf.append(node.interval.toString() );
			if( node.content != null )
			{
				buf.append(" : ");
				buf.append(node.content.toString());
			}
		}
		else
		{
			buf.append("[]");
		}
		
		System.out.println(buf);		
	}
}
