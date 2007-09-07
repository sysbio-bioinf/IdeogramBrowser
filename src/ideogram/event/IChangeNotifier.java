/*
 * Created on 23.05.2005
 *
 */
package ideogram.event;

import javax.swing.event.ChangeListener;

/**
 * @author muellera
 *
 */
public interface IChangeNotifier {
    

	/**
	 * All listeners have to be notified when this object changes.
	 * @param obj
	 */
	public void addChangeListener( ChangeListener obj );

	/**
	 * Removes the given listener from the list.
	 * @param obj
	 */	
	public void removeChangeListener( ChangeListener obj );
    
}
