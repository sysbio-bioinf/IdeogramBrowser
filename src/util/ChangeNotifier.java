package util;

import ideogram.event.IChangeNotifier;

import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ChangeNotifier implements IChangeNotifier
{
	private LinkedList<ChangeListener> dataModelListeners;
	
	public ChangeNotifier()
	{
		dataModelListeners = new LinkedList<ChangeListener>();
	}
	
    /* (non-Javadoc)
     * @see venn.VennDataModelInterface#addVennDataModelListener(venn.VennDataModelListener)
     */
    public synchronized void addChangeListener(ChangeListener listener) 
    {
        if( listener != null )
        {
        	if( !dataModelListeners.contains(listener) )
        	{
        		dataModelListeners.add( listener );
        	}
        }
    }

    /* (non-Javadoc)
     * @see venn.VennDataModelInterface#removeVennDataModelListener(venn.VennDataModelListener)
     */
    public synchronized void removeChangeListener(ChangeListener listener) 
    {
        dataModelListeners.remove( listener );

    }
    
	public synchronized void fireChangeEvent()
	{
	    ChangeEvent event = new ChangeEvent(this);
		
		for( ChangeListener listener : dataModelListeners )
		{
			listener.stateChanged(event);
		}
	}
	
}
