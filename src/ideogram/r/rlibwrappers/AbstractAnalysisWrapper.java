/*
 * File:	AbstractRWrapper.java
 * Created: 08.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.FileTypeRecord;
import ideogram.r.RDataSetWrapper;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.exceptions.RException;

import java.util.HashMap;
import java.util.List;

import org.rosuda.JRI.REXP;

/**
 * Convenience class for the RLibrary interface. Provides a default 
 * implementation.
 * 
 * @author Ferdinand Hofherr
 *
 */
public abstract class AbstractAnalysisWrapper implements RAnalysisWrapper {

    /* (non-Javadoc)
     * @see ideogram.r.RLibrary#hasSampleData()
     */
    public boolean hasSampleData() {
        return false;
    }

    /* (non-Javadoc)
     * @see ideogram.r.RLibrary#listSampleData()
     */
    public List<RDataSetWrapper> listSampleData() {
        return null;
    }

    /* (non-Javadoc)
     * @see ideogram.r.RLibrary#getResult()
     */
    public REXP getResult() throws RException {
        return null;
    }

}
