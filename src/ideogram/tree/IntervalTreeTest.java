/*
 * Created on 01.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram.tree;

/**
 * @author muellera
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public  class IntervalTreeTest {
	public static void main(String[] argv)
	{
		try 
		{
			IntervalTree tree = new IntervalTree();
			
			System.out.println("\ninsert [201,500]");
			tree.insert(new Interval(201,500));
			tree.accept( new OutputStreamVisitor() );
			System.out.println("\n");		
			

			System.out.println("\ninsert [0,10]");					
			tree.insert(new Interval(0,10));
			tree.accept( new OutputStreamVisitor() );
			System.out.println("\n");		

			System.out.println("\ninsert [0,200]");
			tree.insert(new Interval(0,200));
			tree.accept( new OutputStreamVisitor() );
						
			
			System.out.println("\ninsert [11,20]");
			tree.insert(new Interval(11,20));
			tree.accept( new OutputStreamVisitor() );
			System.out.println("\n");		
			
			System.out.println("\ninsert [0,50]");
			tree.insert(new Interval(0,50));
			tree.accept( new OutputStreamVisitor() );
			System.out.println("\n");
			
			System.out.println("\ninsert [5,10]");
			tree.insert(new Interval(5,10));
			tree.accept( new OutputStreamVisitor() );
			System.out.println("\n");		
			
		}
		catch(InsertNodeException e)
		{
			System.err.println(e.toString());
		}		
	}
}
