package ideogram;

import java.awt.Color;

/**
 * Maps a value to a color
 * 
 * @author mueller
 *
 */
public interface IColorMapper 
{
		
	/**
	 * 
	 * @param value Has to be in the interval [-1,1]
	 * 
	 * @return A single color
	 */
	public Color map( double value );
}
