/*
 * Created on 08.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package util;


/**
 * @author muellera
 */
public class TaskQueue extends Thread
{
	private Queue tasks;
		
	/**
	 * Each task must be derived from the abstract Task class.
	 *  
	 * @author muellera
	 *
	 */
	static public abstract class Task
	{
		private Object context;
		
		public Task()
		{
			context = null;
		}
		public Task(Object context)
		{
			this.context = context;
		}
		public abstract void run(Object context);
	};
	
	
	public TaskQueue()
	{
		this.tasks = new LinkedQueue();
	}
	
	public void run() 
	{
		System.out.println("queue started");
		while( ! isInterrupted() )
		{
			try 
			{				
				synchronized( this )
				{
					System.out.println("queue processing ...");
					while( tasks.size() > 0 )
					{
						Task task = (Task)tasks.get();
						task.run(task.context);
					}
					wait();
				}
			}
			catch( InterruptedException e )
			{
				System.out.println("Interrupt " + e.toString() );
			}
		}
		System.out.println("queue stopped");
	}
	
	public void add(Task task)
	{
		/*
		if( ! isAlive() )
			throw new IllegalStateException("cannot add a task - the queue is already stopped");
			*/
			
		synchronized( this )
		{
			tasks.add( task );
			notifyAll();
		}
	}
	
	public void stopTask()
	{
		interrupt();
		notifyAll();		
	}
};
