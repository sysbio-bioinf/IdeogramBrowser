/*
 * File:	RFunctionExecutor.java
 * Created: 06.03.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.gui.DefaultMessageDisplayModel;

import java.util.concurrent.Callable;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Execute the specified R function call in its own thread. Use this class,
 * if the calls to such functions might take a long time.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RFunctionTask implements Callable<RFunctionTask.ExecutorResult> {
    
    private String funcall;
    
    /**
     * Create a new {@link RFunctionTask}.
     *
     * @param funcall String containing the correct call to the R function.
     */
    public RFunctionTask(String funcall) {
        this.funcall = funcall;
    }
    
    /**
     * Neatly format a time difference given in milliseconds.
     *
     * @param millis
     * @return Neatly formated time difference.
     */
    public static String formatTimeDelta(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        seconds -= hours * 3600 + mins * 60;
        
        return String.format("%2dh %2dmin %2dsec", hours, mins, seconds);
    }
    
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public RFunctionTask.ExecutorResult call() throws Exception {
        Rengine engine = RController.getInstance().getEngine();
        
        RController.getInstance().getRMainLoopModel().rBusy(engine, 1);
        long start = System.currentTimeMillis();
        REXP rexp = RController.getInstance().getEngine().eval(funcall);
        long stop = System.currentTimeMillis();
        RController.getInstance().getRMainLoopModel().rBusy(engine, 0);

        String delta = formatTimeDelta(stop - start);
        String msg = "The function call succeded after: " + delta;
        DefaultMessageDisplayModel.getInstance().displayMessage(msg);
        RController.getInstance().toRwriteln(msg);
        
        return new ExecutorResult(rexp, delta);
    }

    /**
     * Wrapper class, which holds the result returned by the function call, and
     * a neatly formatted string with the duration of the call.
     * TODO INSERT DOCUMENTATION HERE!
     *
     * @author Ferdinand Hofherr
     *
     */
    public static class ExecutorResult {
        public final REXP rexp;
        public final String time;
        
        public ExecutorResult(REXP rexp, String time) {
            this.rexp = rexp;
            this.time = time;
        }
    }
}
