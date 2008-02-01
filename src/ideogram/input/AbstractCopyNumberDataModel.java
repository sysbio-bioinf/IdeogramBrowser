package ideogram.input;

import java.util.Iterator;
import ideogram.IColorMapper;
import ideogram.IdeogramMainWindow;
import ideogram.Marker;
import ideogram.MarkerCollection;
import ideogram.db.Band;
import ideogram.tree.Interval;

import javax.swing.event.ChangeListener;

import util.ChangeNotifier;

/**
 * @author mueller
 *
 */
public abstract class AbstractCopyNumberDataModel implements ICopyNumberModel
{
    
	private MarkerCollection[] mc;
	private Band band;
	private Marker m;
    
    private ChangeNotifier changeNotifier;
    /**
     * 
     */
    public AbstractCopyNumberDataModel()
    {
    	mc = new MarkerCollection[IdeogramMainWindow.NUM_OF_CHROMOSOMES];
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
     * Converts a copy number model into a MarkerCollection.
     * TODO: This intermediate step should be removed in future versions.
     * 
     * @param colorMapper
     * @return A marker collection.
     */
    public MarkerCollection[] convertToMarkerCollection( IColorMapper colorMapper )
    {
        band = new Band();
        
        for(int i = 0; i < mc.length; ++i)
        {
            mc[i] = new MarkerCollection();
            mc[i].setName( getFileName());
        }
        
    	for(int i=0; i<size(); ++i)
        {
    		band.chromosome = (byte) getChromosomeIndex(i);
            m = new Marker(getInterval(i),getCopyNumber(i));
            m.color = colorMapper.map( getConfidence(i) );
            
            // TODO Why that??
            // StringBuffer buf = new StringBuffer();
            // buf.append( getMarkerInfo(i) );
            m.setInfo(getMarkerInfo(i) /*buf.toString()*/);
            m.setLog2Ratio(getLogRatio(i));
            
            mc[band.chromosome-1].add(m);
        }

    	return mc;
    }
    
	public String getMarkerName(int idx)
	{
		return get(idx).info;
	}

	public byte getChromosome(int idx)
	{
		return get(idx).locus.chromosome;
	}

	public Interval getInterval(int idx)
	{
		return get(idx).locus.interval;
	}

	public int getCopyNumber(int idx)
	{
		return get(idx).copy_number;
	}

	public double getConfidence(int idx)
	{
		return get(idx).confidence;
	}
    
	public byte getChromosomeIndex(int idx)
	{
		return get(idx).locus.chromosome;
	}
	
	public String getMarkerInfo(int idx)
	{
		return get(idx).info;
	}
	
	/* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#getLog2Ratio(int)
     */
    public double getLogRatio(int idx) {
        return get(idx).getLogRatio();
    }
		
	public void detach()
	{
		// nothing
	}
	
	public Iterator<CopyNumberRecord> iterator()
	{
		return toCollection().iterator();
	}
}
