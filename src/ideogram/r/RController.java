/*
 * File:	RController.java
 * Created: 28.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.exceptions.RLibraryWrapperException;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Central controlling class for interaction with R. This class is implemented
 * as a singleton. It ensures that only one running R thread exists.
 * 
 * <strong>TODO: Fill bug report about stopEngine. JRI seems to have a bug there. </strong>
 * @author Ferdinand Hofherr
 *
 */
public class RController {

    private static boolean available = false;

    private Rengine rEngine;
    private RMainLoopModel mainLoopModel;
    private boolean running;
    private HashMap<String, String> availableLibraries;
    private RLibraryWrapper loadedWrapper;

    private RController() {
        rEngine = null;
        mainLoopModel = new RMainLoopModel();
        running = false;
        availableLibraries = new HashMap<String, String>();
        loadedWrapper = null;
        // add available Libraries
        
        // TODO Externalize this, so that nobody has to mess around with this
        // code.
        registerRLibraryWrapper("GLAD", "ideogram.r.rlibwrappers.GLADWrapper");
    }

    private static class InstanceHolder {
        private final static RController INSTANCE = new RController();
    }
    
//    public static String asRBoolString(int i) {
//        switch (i) {
//            case 0:  return("FALSE");
//            case 1:  return("TRUE");
//            case 2:  return("NA");
//            default: return("FALSE");
//        }
//    }

    /**
     * Check whether the versions of JRI.jar and the JRI native library match.
     * Throw an JRIVersionException if they do not. If they match mark
     * R as usable. If this method is not called at least once, R will not be
     * usable.
     *
     * @throws JRIVersionException
     */
    public static void checkVersion() throws JRIVersionException {
        if (!Rengine.versionCheck()) {
            // Versions of JRI.jar and native library don't match!
            available = false;
            throw new JRIVersionException("Versions of JRI.jar and native " +
            		"library do not match!");
            // TODO: Tell the user, what he can do about this issue.
        }
        else {
            available = true;
        }
    }

    /**
     * Check whether R is usable from within IdeogramBrowser. R will 
     * <strong>not</strong> be usable if 
     * {@link ideogram.r.RController#checkVersion()} is not called at least 
     * once.
     *
     * @return true if R is usable, else false.
     */
    public static boolean rAvailable() {
        return available;
    }
    
    /**
     * Get the only instance of RController.
     *
     * @return The only instance of RController.
     */
    public static RController getInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    /**
     * Check whether Rengine is running.
     *
     * @return true when Rengnine is running, else false.
     */
    public boolean engineRunning() {
        return running;
    }

    /**
     * Get the only instance of Rengine.
     *
     * @return The only instance of Rengine, or null if it is not running.
     */
    public Rengine getEngine() {
        return engineRunning() ? rEngine : null;
    }
    
    /**
     * Start the Rengine. The Rengine must be started, before any interaction 
     * with R is possible.
     *
     * @return true if starting the Rengine succeeded, else false.
     * @throws RException
     */
    public boolean startEngine() throws RException {
        if (!RController.rAvailable()) {
            throw new RException("R is not usable. Try to call " +
                    "RController.checkVersion()");
        }
        
        if (!engineRunning()) {
            // TODO This is only an intermediate version! Add possibility to set command line arguments for R and create Model seperately!
            rEngine = new Rengine(new String[] {"--vanilla"}, 
                    false, mainLoopModel);
            /*
             * Wait for R process to start. Throw exception if something goes
             * wrong.
             */
            if (!(running = rEngine.waitForR())) {
                throw new RException("The R process was started, but died" +
                        "immedeately!");
            }
            return true;
        }
        return false;
    }
    
    /**
     * Get the Rengine's RMainLoopModel. If the Rengine is not running, the 
     * RMainLoopModel will be returned anyway.
     *
     * @return The Rengine's RMainLoopModel even though the engine might not be
     *         running.
     */
    public RMainLoopModel getRMainLoopModel() {
            return mainLoopModel;
    }

    /**
     * Stop the running Rengine. This terminates the R process. Use this method
     * with care! R might be still evaluating an expression!
     * 
     * @deprecated This method causes serious problems. It is not possible to 
     * restart R after it has been stopped. Somehow loading packages then fails.
     * This might be a bug in JRI. As long as any uncertainity remains this 
     * method is deprecated.
     * 
     * @return true on success, false if Rengine could not be stopped.
     * 
     * @throws RException if R is not available.
     */
    @Deprecated
    protected boolean stopEngine() throws RException {
        if (engineRunning()) {
            Rengine e = getEngine();
            e.end();
            running = e.waitForR();
            return true;
        }
        return false;
    }

    /** 
     * Allows to load the specified R library. 
     *
     * @param libName
     * @return true if package was loaded, false if package could not be found.
     * @throws RException if R is not running.
     */
    public void loadRLibrary(String libName) throws RException {
        if (engineRunning()) {
            /* require() is used, as it returns true on success and false on
             * error. */
            REXP re = getEngine().eval("require('" + libName + "')");
            if (!re.asBool().isTRUE()) {
                throw new RException("Error loading the library" + 
                        libName +  "!");
            }
        }
        else {
            throw new RException("R not running");
        }
    }
    
    /**
     * Unload the specified R library.
     *
     * @param libName
     * @throws RException
     */
    public void unloadRLibrary(String libName) throws RException {
        if (engineRunning()) {
            getEngine().eval("detach('package:" + libName + "')");
        }
        else {
            throw new RException("R not running!");
        }
    }
    
    /**
     * Load the specified data set into R. An R library must provide this data
     * set already.
     * 
     * TODO: Add possibility to load data from files.
     *
     * @param dsName Name of the data set to load.
     * @throws RException When R is not running, or when an error occured, while
     *                    loading the package.
     */
    public void loadDataSet(String dsName) throws RException {
        if (engineRunning()) {
            REXP res = getEngine().eval("data('" + dsName + "')");
            if (!res.asString().equalsIgnoreCase(dsName)) {
                throw new RException("Error loading data set " + dsName + "!");
            }
        }
        else {
            throw new RException("R not running!");
        }
    }
    
//    protected void unloadDataSet(String dsName) throws RException {
//        if (engineRunning()) {
//            getEngine().eval("detach('" + dsName + "')");
//        }
//        else {
//            throw new RException("R not running!");
//        }
//    }
    
    /* TODO Provide a way to find all available packages.
     * This involves, that the packages must somehow register themselves and 
     * provide a way to communicate there available procedures.
     */
    
    /**
     * Register a wrapper for a R library. All {@link RLibraryWrapper}s must 
     * implement an empty Constructor.
     * 
     * @param libName Name of the library, e.g. GLAD
     * @param fullyQualifiedName Fully quialified Name of the wrapper class, e.g
     *        ideogram.r.GLADWrapper
     */
    public void registerRLibraryWrapper(String libName, String fullyQualifiedName) {
        availableLibraries.put(libName, fullyQualifiedName);
    }
    
    /**
     * Load the selected R library. The corresponding {@link RLibraryWrapper}
     * must be registered with {@link RController}, else loading it will fail.
     *
     * @param name
     * @return Instance of the freshly loaded {@link RLibraryWrapper}
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws RException
     */
    public RLibraryWrapper loadRLibraryWrapper(String name) 
    throws ClassNotFoundException, IllegalArgumentException, 
    InstantiationException, IllegalAccessException, InvocationTargetException, 
    RException {
        String selClass = availableLibraries.get(name);
        if (selClass == null) throw new ClassNotFoundException("The " +
                "RLibraryWrapper " + name + " must be registered with the " +
                "RController");
        Class<?> c = Class.forName(selClass);
        Constructor<?> ctor = getEmptyConstructor(c);
        if (ctor == null) {
            throw new IllegalArgumentException(c.toString() + 
                    " must implement an empty constructor!");
        }
        loadedWrapper = (RLibraryWrapper)ctor.newInstance();
        loadedWrapper.loadLibrary();
        
        return loadedWrapper;
    }
    
    /**
     * Returns null if no wrapper is loaded, else a reference to the instance 
     * of the loaded wrapper is returned.
     *
     * @return
     */
    public RLibraryWrapper getLoadedWrapper() {
        return loadedWrapper;
    }
    
    /*
     * Find the empty public constructor.
     */
    private Constructor<?> getEmptyConstructor(Class<?> c) {
        Constructor<?>[] allConstructors = c.getConstructors();
        Constructor<?> ret = null;
        for (Constructor<?> ctor: allConstructors) {
            if (ctor.getParameterTypes().length == 0) {
                ret = ctor;
                break;
            }
        }
        
        return ret;
    }

    /**
     * List all available library wrappers. The returned array is sorted 
     * into alphabetically ascending order.
     * 
     * @return Sorted array containing the names of all available 
     *         {@link RLibraryWrapper}s.
     */
    public String[] listLibraryWrappers() {
        String[] keys = new String[availableLibraries.size()];
        keys = availableLibraries.keySet().toArray(keys);
        Arrays.sort(keys); // OK, as strings implement Comparable.
        return keys;
    }
}
