/*
 * File:	RLibrary.java
 * 
 * Created:	08.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.FileTypeRecord;
import ideogram.r.exceptions.RException;

import java.util.List;

/**
 * Every valid R library wrapper must implement this interface. <em>As the class
 * will be instantiated using reflection every implementation must implement an
 * empty public constructor. Furthermore any of the methods in classes,
 * implementing this interface or any subinterfaces of it, may be called from 
 * an other thread. Thread safety must therefore be ensured.</em>
 * 
 * @author Ferdinand Hofherr
 * 
 */
public interface RLibraryWrapper {

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
     * Get a list containing all file types that are accepted by this wrapper,
     * or null if none are accepted.
     * 
     * @return
     */
    public List<FileTypeRecord> getAcceptedFileTypes();
}
