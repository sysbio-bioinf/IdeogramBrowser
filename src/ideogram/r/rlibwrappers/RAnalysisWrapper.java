/*
 * File:	RAnalysisWrapper.java
 * Created: 26.02.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.RDataSetWrapper;
import ideogram.r.exceptions.RException;

import java.util.List;

import org.rosuda.JRI.REXP;

/**
 * All classes, which are wrappers around analysis libraries in R, must implement
 * this interface.
 *
 * @author Ferdinand Hofherr
 *
 */
public interface RAnalysisWrapper extends RLibraryWrapper {

    /**
     * Check whether the R package provides sample data.
     *
     * @return true if sample data is available, else false.
     */
    public boolean hasSampleData();
    
    /**
     * If sample data is availabe, list it. 
     *
     * @return List of sample data, or null if none is available.
     */
    public List<RDataSetWrapper> listSampleData();
    
    /**
     * Load the specified data set.
     *
     * @param data
     * @return
     */
    public void loadSampleData(RDataSetWrapper data) throws RException;
    
    /**
     * Get the result of the performed calculation.
     *
     * @return
     * @throws RException
     */
    public REXP getResult() throws RException;
}
