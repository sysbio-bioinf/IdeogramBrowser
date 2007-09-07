package ideogram.input;

import ideogram.event.IChangeNotifier;

/**
 * @author jkraus
 *
 */
public interface IFilterModel extends IChangeNotifier
{
	/**
	 * 
	 * @param setValue
	 * @return Sets the lower bound for copy number values.
	 */
	void setWhichValue(String setValue);
	
	/**
	 * 
	 * @param whichValue, lower, upper
	 * @return Sets the lower bound for copy number values.
	 */
	void setFilterValues(String whichValue, double lower, double upper);
	
	/**
	 * 
	 * @param whichValue
	 * @return Gets the lower bound for copy number values.
	 */
	double[] getFilterValues(String whichValue);
	
	/**
	 * 
	 * @return
	 */
	double[] getBounds();
	
	/**
	 * 
	 * @return Gets the value name.
	 */
	String getWhichValue();
	
}
