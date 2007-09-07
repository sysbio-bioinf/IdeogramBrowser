/*
 * Created on 11.10.2005
 *
 */
package ideogram;

public interface IProgressNotifier 
{
	/**
	 * 
	 * @return True if the user pressed on the cancel button.
	 */
    public boolean isCancelled();
    
    /**
     * Sets the text in the progress bar.
     * 
     * @param text
     */
    public void setText(String text);
}
