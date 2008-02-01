package ideogram.input;

import ideogram.IColorMapper;
import ideogram.MarkerCollection;
import ideogram.event.IChangeNotifier;
import ideogram.tree.Interval;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author jkraus
 * gives filtered copy number data.
 */
public interface ICopyNumberModel extends IChangeNotifier, Iterable<CopyNumberRecord>
{
	/**
	 * 
	 * @return Returns the name of the loaded files.
	 */
    LinkedList<String> getFileName();
    
    
    /**
     * @return Returns the header information of the loaded files.
     */
    LinkedList<String> getHeader();
    
	/**
	 * @return Returns Chip Type of the loaded files.
	 */
	LinkedList<String> getChipType();
	
	/**
	 * 
	 * @return Returns the overall number of markers.
	 */
    int size();
   
    /**
     * Clear the {@link ICopyNumberModel}.
     *
     */
    public void clear();
    
    /**
     * 
     * @param idx
     * @return A marker's name for the given Index.
     */
    String getMarkerName( int idx );
       
    /**
     * 
     * @param idx
     * @return A chromosome for the given Index.
     */
    byte getChromosome( int idx );
    
    
    byte getChromosomeIndex( int idx );
    
    /**
     * 
     * @param idx
     * @return An Interval from: start base pair to: stop base pair for the given Index.
     */
    Interval getInterval( int idx );
    
    
    /**
     * 
     * @param idx
     * @return A copy number value for the given Index:
     *    0 = double loss, 1 = loss, 2 = normal,
     *    3 = gain, 4 = amplification
     *    -1 = illegal value (NA)
     */
    int getCopyNumber( int idx );
      
    /**
     * 
     * @param idx
     * @return Returns Double.NaN if no confidence values are specified
     */
    double getConfidence( int idx );
    
    /**
     * Return the Log2Ratio of this marker.
     * TODO It might be necessary to return Double.NaN if the log2 ratio is 
     * unknown. 
     *
     * @param idx
     * @return
     */
    public double getLogRatio(int idx);
    
    String getMarkerInfo(int i);
    
    MarkerCollection[] convertToMarkerCollection( IColorMapper colorMapper );

	CopyNumberRecord get(int j);
	
	Collection<CopyNumberRecord> toCollection();

	void detach();
}
