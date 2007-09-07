package ideogram.input;

import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author jkraus
 *
 */
public abstract class AbstractFilterModel implements IFilterModel
{

    private LinkedList<ChangeListener> dataModelListeners;
    private boolean eventsActive = true;
    
    public AbstractFilterModel() {

        dataModelListeners = new LinkedList<ChangeListener>();
    }

    /* (non-Javadoc)
     * @see venn.VennDataModelInterface#addVennDataModelListener(venn.VennDataModelListener)
     */
    public synchronized void addChangeListener(ChangeListener listener) 
    {
        if( listener != null )
            dataModelListeners.add( listener );        
    }

    /* (non-Javadoc)
     * @see venn.VennDataModelInterface#removeVennDataModelListener(venn.VennDataModelListener)
     */
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
	

	public double[] getBounds()
	{
		return getFilterValues(getWhichValue());
	}
}
