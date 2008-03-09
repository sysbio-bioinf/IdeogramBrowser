/*
 * File:	RTask.java
 * Created: 06.03.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.exceptions.RException;
import ideogram.r.gui.DefaultMessageDisplayModel;
import ideogram.r.gui.MessageDisplay;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Execute the specified R function call in its own thread. Use this class,
 * if the calls to such functions might take a long time.
 *
 * @author Ferdinand Hofherr
 *
 */
public class RTask implements Callable<RTask.RTaskResult> {
    
    private String funcall;
    private String failureMessage;  // null if no checking needs to be done.
    private Future<RTaskResult> dependingOn;
    
    /**
     * Create a new {@link RTask}. Creating a task this way means
     * that it will not be checked whether the requested operation was 
     * successful.
     *
     * @param funcall String containing the correct call to the R function.
     */
    public RTask(String funcall) {
        this(funcall, null, null);
    }
    

    /**
     * Create a new {@link RTask}.
     *
     * @param funcall String containing the correct call to the R function.
     * @param failureMessage Message that will be displayed upon failure.
     */
    public RTask(String funcall, String failureMessage) {
        this(funcall, failureMessage, null);
    }
    
    /**
     * Create a new {@link RTask}. If failureMessage is not null, this 
     * message will get displayed upon failure. If dependingOn is not null,
     * {@link RTask} will check whether the encapsulated 
     * </code>{@link RTaskResult#rexp} != null</code> before executung the
     * funcall.
     *
     * @param funcall
     * @param failureMessage
     * @param dependingOn
     */
    public RTask(String funcall, String failureMessage, 
            Future<RTaskResult> dependingOn) {
        this.funcall = funcall;
        this.failureMessage = failureMessage;
        this.dependingOn = dependingOn;
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
    public RTask.RTaskResult call() throws Exception {
        if (dependingOn != null && dependingOn.get().rexp == null) {
            RController.getInstance().toRwriteln(failureMessage);
            DefaultMessageDisplayModel.getInstance().
                displayMessage(failureMessage);
            return new RTaskResult(null, formatTimeDelta(0));
        }
        
        Rengine engine = RController.getInstance().getEngine();
        RController.getInstance().getRMainLoopModel().rBusy(engine, 1);
        long start = System.currentTimeMillis();
        REXP rexp = RController.getInstance().getEngine().eval(funcall);
        long stop = System.currentTimeMillis();
        RController.getInstance().getRMainLoopModel().rBusy(engine, 0);

        String delta = formatTimeDelta(stop - start);
        String msg = "The function call finished after: " + delta;
        DefaultMessageDisplayModel.getInstance().displayMessage(msg);
        RController.getInstance().toRwriteln(msg);
        
        if (rexp == null && failureMessage != null) {
            System.out.println(failureMessage);
            RController.getInstance().toRwriteln(failureMessage);
            DefaultMessageDisplayModel.getInstance().displayMessage(
                    failureMessage);
        }
        
//        System.out.println("Simulating a long running task!");
//        TimeUnit.SECONDS.sleep(10);  // simulate long running task
        
        return new RTaskResult(rexp, delta);
    }

    /**
     * Wrapper class, which holds the result returned by the function call, and
     * a neatly formatted string with the duration of the call.
     */
    public class RTaskResult {
        public final REXP rexp;
        public final String time;
        
        public RTaskResult(REXP rexp, String time) {
            this.rexp = rexp;
            this.time = time;
        }
    }
}