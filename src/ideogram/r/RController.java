/*
 * File:	RController.java
 * Created: 28.11.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RController {

    private static boolean available = false;

    private Rengine rEngine;
    private RMainLoopModel mainLoopModel;
    private boolean running;

    private RController() {
        rEngine = null;
        mainLoopModel = new RMainLoopModel();
        running = false;
    }

    private static class InstanceHolder {
        private final static RController INSTANCE = new RController();
    }

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
    protected Rengine getEngine() {
        return engineRunning() ? rEngine : null;
    }
    
    /**
     * Start the Rengine. 
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
            // TODO This is only an intermediate version! Add possibility to set commandline arguments for R and create Model seperately!
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
     * Get the Rengine's RMainLoopModel, if the engine is running.
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
     * @param pkgName
     * @return true if package was loaded, false if package could not be found.
     * @throws RException if R is not availiable.
     */
    protected boolean loadRLibrary(String pkgName) throws RException {
        if (engineRunning()) {
            /* require() is used, as it returns true on success and false on
             * error. */
            REXP re = getEngine().eval("require('" + pkgName + "')");
            return re.asBool().isTRUE(); // Returns true if package was loaded.
        }
        return false;
    }

}
