/*
 * Created on 23.05.2005
 *
 */
package ideogram.input;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author jkraus
 *
 */
public abstract class AbstractIdeogramDataModel implements IIdeogramDataModel
{
    private LinkedList<ChangeListener> dataModelListeners;
    private boolean eventsActive = true;
    
    public AbstractIdeogramDataModel() {

        dataModelListeners = new LinkedList<ChangeListener>();
    }

    public synchronized void addChangeListener(ChangeListener listener) 
    {
        if( listener != null )
            dataModelListeners.add( listener );        
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        dataModelListeners.remove( listener );

    }
    
    public synchronized void setEventsActive( boolean eventsActive )
    {
        this.eventsActive = eventsActive;
    }
    
	protected synchronized void fireChangeEvent()
	{
	    if( ! eventsActive )
	        return;
	    
	    ChangeEvent event = new ChangeEvent(this);
		
		Iterator<ChangeListener> iter = dataModelListeners.iterator();
		while(iter.hasNext())
		{
			iter.next().stateChanged(event);
		}
	}
 
	public abstract LinkedList<String> getFileName();
}
