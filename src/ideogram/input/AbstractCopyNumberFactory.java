package ideogram.input;

import javax.swing.event.ChangeListener;

import util.ChangeNotifier;



/**
 * Depending on a Configuration
 * @author mueller
 *
 */
public class AbstractCopyNumberFactory
{
	private ChangeNotifier notifier;
	
	public AbstractCopyNumberFactory()
	{
		notifier = new ChangeNotifier();
	}

	public void addChangeListener( ChangeListener listener )
	{
		notifier.addChangeListener(listener);
	}

	public ICopyNumberModel createOutputModel()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
