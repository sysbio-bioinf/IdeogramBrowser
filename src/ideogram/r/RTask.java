/*
 * File:	RTask.java
 * 
 * Created: 	06.03.2008
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.gui.DefaultMessageDisplayModel;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.rosuda.JRI.Rengine;

/**
 * Execute the specified R function call in its own thread. Use this class, if
 * the calls to such functions might take a long time.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class RTask implements Callable<RTask.RTaskResult> {

    private Method funcall;
    private RLibraryWrapper wrapper;
    private Object[] funcallArgs;

    /**
     * Create a new {@link RTask}. It is assumed that the passed {@link Method}
     * funcall does not expect any arguments.
     * 
     * @param wrapper
     *                The {@link RLibraryWrapper} that funcall belongs to.
     * @param funcall
     *                Method of wrapper that shall be called.
     */
    public RTask(RLibraryWrapper wrapper, Method funcall) {
	this(wrapper, funcall, (Object[]) null);
    }

    /**
     * Create a new {@link RTask}. If the funcall requires any arguments they
     * can be passed via funcallArgs. Pass null, if funcall does not requre
     * arguments.
     * 
     * @param wrapper
     *                The {@link RLibraryWrapper} that funcall belongs to.
     * @param funcall
     *                Method of wrapper that shall be called.
     * @param funcallArgs
     *                Arguments to funcall.
     */
    public RTask(RLibraryWrapper wrapper, Method funcall, Object... funcallArgs) {
	this.funcall = funcall;
	this.wrapper = wrapper;
	this.funcallArgs = funcallArgs;
    }

    /**
     * Neatly format a time difference given in milliseconds.
     * 
     * @param millis
     *                Time difference in milliseconds.
     * @return Neatly formated time difference.
     */
    public static String formatTimeDelta(long millis) {
	long seconds = millis / 1000;
	long hours = seconds / 3600;
	long mins = (seconds % 3600) / 60;
	seconds -= hours * 3600 + mins * 60;

	return String.format("%2dh %2dmin %2dsec", hours, mins, seconds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    public RTask.RTaskResult call() throws Exception {
	long start;
	long stop;
	Rengine engine;
	Object funcallReturn;
	String delta;
	String msg;

	engine = RController.getInstance().getEngine();
	RController.getInstance().getRMainLoopModel().rBusy(engine, 1);
	start = System.currentTimeMillis();
	funcallReturn = funcall.invoke(wrapper, funcallArgs);
	stop = System.currentTimeMillis();
	RController.getInstance().getRMainLoopModel().rBusy(engine, 0);

	delta = formatTimeDelta(stop - start);
	msg = "The function call finished after: " + delta;
	DefaultMessageDisplayModel.getInstance().displayMessage(msg);
	RController.getInstance().toRwriteln(msg);

	// System.out.println("Simulating a long running task!");
	// TimeUnit.SECONDS.sleep(10); // simulate long running task

	return new RTaskResult(funcallReturn, delta);
    }

    /**
     * Wrapper class, which holds the result returned by the function call, and
     * a neatly formatted string with the duration of the call.
     */
    public class RTaskResult {
	
	/** The function calls return value. */
	public final Object funcallReturn;

	/** Time it took R to evaluate the function. * */
	public final String time;

	/** Create new RTaskResult. */
	public RTaskResult(Object funcallReturn, String time) {
	    this.funcallReturn = funcallReturn;
	    this.time = time;
	}
    }
}