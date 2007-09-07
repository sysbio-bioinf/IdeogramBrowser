package ideogram.input;

import javax.swing.event.ChangeListener;

import util.ChangeNotifier;

public abstract class AbstractGenomicMarkerModel implements IGenomicMarkerModel
{
    
    private ChangeNotifier changeNotifier;

    public AbstractGenomicMarkerModel()
    {
        changeNotifier = new ChangeNotifier();
    }

    public void addChangeListener(ChangeListener listener) 
    {
    	changeNotifier.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
    	changeNotifier.removeChangeListener(listener);
    }
    
	protected void fireChangeEvent()
	{
		changeNotifier.fireChangeEvent();
	}
	
	/**
	 * Find nearest marker
	 * 
	 * @param bp
	 * @param right_side
	 * @return Index of the nearest marker.
	 */
	public GenomicMarker findNearest( long bp )
	{		
		long mindist = 0;
		GenomicMarker bestmarker = null;

		for( GenomicMarker m : this )
		{
			long dist = m.interval.distance(bp);
			if( bestmarker == null || dist < mindist )
			{
				bestmarker = m;
				mindist = dist;
			}
		}

		return bestmarker;
	}
	

}
