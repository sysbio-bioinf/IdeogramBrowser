/*
 * File:	RLibrary.java
 * Created: 08.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.RDataSetWrapper;
import ideogram.r.RException;

import java.util.List;

import org.rosuda.JRI.REXP;

/**
 * Every valid R library wrapper must implement this interface. As the class
 * will be instantiated using reflection every implementation must implement
 * an empty public constructor.
 *
 * @author Ferdinand Hofherr
 *
 */
public interface RLibraryWrapper {
    
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
     * Tell R to load the library.
     *
     * @throws RException
     */
    public void loadLibrary() throws RException;
    
    /** 
     * Unload the library.
     *
     * @throws RException
     */
    public void unloadLibrary() throws RException;

    /**
     * Load the specified data set.
     *
     * @param data
     * @return
     */
    public void loadSampleData(RDataSetWrapper data) throws RException;
    
    public REXP getResult() throws RException;
}
