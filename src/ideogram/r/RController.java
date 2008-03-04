/*
 * File: RController.java Created: 28.11.2007 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.exceptions.JRIVersionException;
import ideogram.r.exceptions.RException;
import ideogram.r.rlibwrappers.RLibraryRegistry;
import ideogram.r.rlibwrappers.GLADWrapper;
import ideogram.r.rlibwrappers.RAnalysisWrapper;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Central controlling class for interaction with R. This class is implemented
 * as a singleton. It ensures that only one running R thread exists.
 * <strong>Note</strong>: This class allows the registration of a single
 * {@link ChangeListener}. Further registrations will be ignored until the
 * {@link RController#removeSingleChangeListener(ChangeListener)} method is
 * called! <strong>TODO: Fill bug report about stopEngine. JRI seems to have a
 * bug there. </strong>
 * 
 * @author Ferdinand Hofherr
 */
public class RController {

    private static boolean available = false;

    private Rengine rEngine;
    private RMainLoopModel mainLoopModel;
    private boolean running;
    private HashMap<String, String> availableLibraries;
    private RAnalysisWrapper loadedAnalysisWrapper;
    private ChangeListener changeListener;
    private ChangeEvent changeEvent;
    private ExecutorService executorService;

    /*
     * List containing the names of all currently exsisting RResult files. Will
     * be set to null at the beginning and after the list of the RResult files
     * is retrieved. It will by instanciated by the addRResultFile() method if
     * necessary.
     */
    private ArrayList<String> rResultFiles;

    private boolean librariesRegistered;

    public static final String R_STORAGE_PATH = System.getProperty("user.dir")
            + File.separator + "IdeogramBrowser" + File.separator + "RResult";

    private RController() {
        // Will be set by addChangeListener()!
        changeListener = null;
        changeEvent = new ChangeEvent(this);

        rResultFiles = null;

        rEngine = null;
        mainLoopModel = new RMainLoopModel();
        running = false;
        availableLibraries = new HashMap<String, String>();
        loadedAnalysisWrapper = null;
        librariesRegistered = false;
        executorService = Executors.newCachedThreadPool();
    }

    private static class InstanceHolder {
        private final static RController INSTANCE = new RController();
    }

    /**
     * Wrapper for {@link GLADWrapper#createUniqueRResultFileName(String)} with
     * empty prefix.
     * 
     * @return
     */
    public static String createUniqueRResultFileName() {
        return createUniqueRResultFileName("");
    }

    /**
     * Create a new RResult file name, consisting of date and time. As the file
     * name includes milliseconds it should be pretty unique.
     * 
     * @param prefix
     *            Prefix to insert before timestamp.
     * @return
     */
    public static String createUniqueRResultFileName(String prefix) {
        Format f = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS");
        if (prefix.equals("")) {
            return f.format(new Date()) + ".RResult";
        }
        else {
            return prefix + "_" + f.format(new Date()) + ".RResult";
        }
    }

    /**
     * Test whether the passed string can be casted to a valid R numeric value.
     * This is achieved by a call to {@link Double#parseDouble(String)}.
     * 
     * @param s
     * @return true if s is a valid R numeric value.
     */
    public static boolean isValidRNumeric(String s) {
        try {
            Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Test whether the passed string is a valid R identifier. See the <a
     * href="http://cran.r-project.org/doc/manuals/R-lang.html#Identifiers"> R
     * Language Definition </a> for details.
     * 
     * @param s
     * @return true if s is a valid R identifier.
     */
    public static boolean isValidRIdentifier(String s) {
        char[] cArr = s.toCharArray();

        if (!Character.isLetter(cArr[0])) {
            // The first character must be a letter!
            return false;
        }
        for (char c : cArr) {
            if (c != '.' && c != '_' && !Character.isLetterOrDigit(c)) {
                /*
                 * The others may be letters, digits, the underscore, or the
                 * colon.
                 */
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether the passed String is a valid R String. This method just
     * tests, wheter the first an the last character of the passed String are
     * equal to ' or ".
     * 
     * @param s
     * @return true if s is a valid R string.
     */
    public static boolean isValidRString(String s) {
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if (first != last && (first != '\'' || first != '"')) {
            return false;
        }
        return true;
    }

    /**
     * Check whether the versions of JRI.jar and the JRI native library match.
     * Throw an JRIVersionException if they do not. If they match mark R as
     * usable. If this method is not called at least once, R will not be
     * usable.
     * 
     * @throws JRIVersionException
     */
    public static void checkVersion() throws JRIVersionException {
        if (!Rengine.versionCheck()) {
            // Versions of JRI.jar and native library don't match!
            available = false;
            throw new JRIVersionException("Versions of JRI.jar and native "
                    + "library do not match!");
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
     * Submit a task to be run by {@link RController}'s executor service.
     *
     * @return
     */
    public <T> Future<T> submitTask(Callable<T> task) {
        return executorService.submit(task);
    }
    
    /**
     * Register a {@link ChangeListener} if no other listener is registered. If
     * you want to register an other listener you must first unregister the old
     * listener by calling {@link RController#removeChangeListener()}.
     * 
     * @param listener
     */
    public void addSingleChangeListener(ChangeListener listener) {
        if (changeListener == null) {
            changeListener = listener;
        }
    }

    /**
     * Remove the only allowed {@link ChangeListener}.
     * 
     * @param listener
     */
    public void removeSingleChangeListener() {
        changeListener = null;
    }

    /**
     * Notify all registered {@link ChangeListener}s.
     */
    protected void fireStateChanged() {
        if (changeListener != null) {
            changeListener.stateChanged(changeEvent);
        }
    }

    /**
     * Add a new RResult file to the list of existing files. You must specify
     * the absolute path to the file.
     * 
     * @param absoluteFileName
     */
    public void addRResultFile(String absoluteFileName) {
        if (rResultFiles == null) {
            rResultFiles = new ArrayList<String>();
        }
        rResultFiles.add(absoluteFileName);
        fireStateChanged();
    }

    /**
     * Return the list of newly available RResult files. Reset it to null
     * afterwards. If no RResult files are available return null.
     * 
     * @return List of newly available RResult files or null.
     */
    public ArrayList<String> getRResultFiles() {
        if (rResultFiles != null) {
            ArrayList<String> tmp = rResultFiles;
            rResultFiles = null;
            return tmp;
        }
        return null;
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
     * Get the only instance of Rengine. This method will check if the
     * {@link Rengine} is running. If this is not the case, an
     * {@link RException} will be thrown.
     * 
     * @return The only instance of Rengine.
     * @throws RException
     *             if Rengine is not running.
     */
    public synchronized Rengine getEngine() throws RException {
        if (!engineRunning()) {
            throw new RException("R not running!");
        }
        else {
            return rEngine;
        }
        // return engineRunning() ? rEngine : null;
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
            throw new RException("R is not usable. Try to call "
                    + "RController.checkVersion()");
        }

        if (!engineRunning()) {
            // TODO This is only an intermediate version! Add possibility to
            // set command line arguments for R and create Model seperately!
            rEngine = new Rengine(new String[] { "--vanilla" }, false,
                    mainLoopModel);
            /*
             * Wait for R process to start. Throw exception if something goes
             * wrong.
             */
            if (!(running = rEngine.waitForR())) {
                throw new RException("The R process was started, but died"
                        + "immedeately!");
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
     *             restart R after it has been stopped. Somehow loading
     *             packages then fails. This might be a bug in JRI. As long as
     *             any uncertainity remains this method is deprecated.
     * @return true on success, false if Rengine could not be stopped.
     * @throws RException
     *             if R is not available.
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
     * @throws RException
     *             if R is not running.
     */
    public void loadRLibrary(String libName) throws RException {
        /*
         * require() is used, as it returns true on success and false on error.
         */
        REXP re = getEngine().eval("require('" + libName + "')");
        if (!re.asBool().isTRUE()) {
            throw new RException("Error loading the library" + libName + "!");
        }
    }

    /**
     * Unload a previously loaded library if there is one.
     * 
     * @throws RException
     */
    public void unloadPreviousWrapper() throws RException {
        if (getLoadedAnalysisWrapper() != null) {
            loadedAnalysisWrapper.unloadLibrary();
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
            getEngine().eval(
                    "cat('Unloading previously loaded library " + libName
                            + "\n')");
            getEngine().eval("detach(package: " + libName + ")");
        }
        else {
            throw new RException("R not running!");
        }
    }

    /**
     * List the workspace contents.
     * 
     * @throws RException
     */
    public void listWorkspace() throws RException {
        getEngine().eval("cat(ls(), sep='\n')");
    }

    /**
     * Clear all workspace contents. This removes everything, a call to ls()
     * would show.
     * 
     * @throws RException
     */
    public void clearWorkspace() throws RException {
        getEngine().eval("rm(list=ls())");
    }

    /**
     * Load the specified data set into R. An R library must provide this data
     * set already.
     * 
     * @param dsName
     *            Name of the data set to load.
     * @throws RException
     *             When R is not running, or when an error occured, while
     *             loading the package.
     */
    public void loadDataSet(String dsName) throws RException {
        REXP res = getEngine().eval("data('" + dsName + "')");
        if (!res.asString().equalsIgnoreCase(dsName)) {
            throw new RException("Error loading data set " + dsName + "!");
        }
    }

    // protected void unloadDataSet(String dsName) throws RException {
    // if (engineRunning()) {
    // getEngine().eval("detach('" + dsName + "')");
    // }
    // else {
    // throw new RException("R not running!");
    // }
    // }

    /*
     * TODO Provide a way to find all available packages. This involves, that
     * the packages must somehow register themselves and provide a way to
     * communicate there available procedures.
     */

    /**
     * Register a wrapper for a R library. All {@link RLibraryWrapper}s must
     * implement an empty Constructor.
     * 
     * @param libName
     *            Name of the library, e.g. GLAD
     * @param fullyQualifiedName
     *            Fully quialified Name of the wrapper class, e.g
     *            ideogram.r.GLADWrapper
     */
    public void registerRLibraryWrapper(String libName,
            String fullyQualifiedName) {
        availableLibraries.put(libName, fullyQualifiedName);
    }

    /**
     * Load the selected R library. The corresponding {@link RAnalysisWrapper}
     * must be registered with {@link RController}, else loading it will fail.
     * 
     * @param name
     * @return Instance of the freshly loaded {@link RAnalysisWrapper}
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws RException
     */
    public RAnalysisWrapper loadRAnalysisWrapper(String name)
            throws ClassNotFoundException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException, RException {
        String selClass = availableLibraries.get(name);
        if (selClass == null)
            throw new ClassNotFoundException("The " + "RLibraryWrapper "
                    + name + " must be registered with the " + "RController");
        Class<?> c = Class.forName(selClass);
        Constructor<?> ctor = getEmptyConstructor(c);
        if (ctor == null) {
            throw new IllegalArgumentException(c.toString()
                    + " must implement an empty constructor!");
        }
        loadedAnalysisWrapper = (RAnalysisWrapper) ctor.newInstance();
        loadedAnalysisWrapper.loadLibrary();

        return loadedAnalysisWrapper;
    }

    /**
     * Returns null if no wrapper is loaded, else a reference to the instance
     * of the loaded wrapper is returned.
     * 
     * @return Reference to instance of loaded wrapper, or null if no wrapper
     *         is loaded.
     */
    public RLibraryWrapper getLoadedAnalysisWrapper() {
        return loadedAnalysisWrapper;
    }

    /*
     * Find the empty public constructor.
     */
    private Constructor<?> getEmptyConstructor(Class<?> c) {
        Constructor<?>[] allConstructors = c.getConstructors();
        Constructor<?> ret = null;
        for (Constructor<?> ctor : allConstructors) {
            if (ctor.getParameterTypes().length == 0) {
                ret = ctor;
                break;
            }
        }

        return ret;
    }

    /**
     * List all available library wrappers. The returned array is sorted into
     * alphabetically ascending order.
     * 
     * @return Sorted array containing the names of all available
     *         {@link RLibraryWrapper}s.
     */
    public String[] listLibraryWrappers() {
        if (!librariesRegistered) {
            /*
             * If not already done, register all available libraries first!
             */
            for (RLibraryRegistry lib : RLibraryRegistry.values()) {
                System.out.println(lib.toString());
                lib.register();
            }
            librariesRegistered = true;
        }
        String[] keys = new String[availableLibraries.size()];
        keys = availableLibraries.keySet().toArray(keys);
        Arrays.sort(keys); // OK, as strings implement Comparable.
        return keys;
    }
}
