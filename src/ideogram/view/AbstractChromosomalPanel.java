package ideogram.view;

import ideogram.tree.Interval;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class AbstractChromosomalPanel extends JComponent implements IChromosomalView
{
	public static final long MAX_BASEPAIRS = 250000000;
	private boolean selected; 
	private Color	selectedBackground;

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	public AbstractChromosomalPanel()
	{
		selectedBackground = new Color(0xee,0xee,0xee);
		selected = false;

	}
	
	/**
	 * Transforms the basepair location <var>pos</var> to y-coordinates of the canvas.
	 * @param pos
	 * @return y coordinate.
	 */
	public int convertBaseToYCoord(long pos)
	{
		return (int) (((double)pos * (double)getHeight())/(double)getViewLength());
	}

	/**
	 * Transforms a canvas y coordinate to basepairs.
	 * @param y
	 * @return Basepair offset.
	 */
	public long convertYCoordToBase(int y)
	{
		return (long) (((double)y * (double) getViewLength()) / (double) getHeight());
	}	

	/* Methods to implement */

    public long getViewLength()
    {
    	return MAX_BASEPAIRS;
    }
    
    public Interval getVisibleInterval()
    {
    	Rectangle rect = getVisibleRect();
    	return new Interval(convertYCoordToBase(rect.y),
    						convertYCoordToBase(rect.y+rect.height));
    }
    
    public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		if( this.selected != selected )
		{
			firePropertyChange("isSelected", this.selected, selected);
			this.selected = selected;
			repaint();
		}
	}

	public Color getSelectedBackground()
	{
		return selectedBackground;
	}

	public void setSelectedBackground(Color selectedBackground)
	{
		this.selectedBackground = selectedBackground;
	}    
	
	public void paintComponent(Graphics g)
	{
		if( isSelected() )
		{
			g.setPaintMode();
			g.setColor(getSelectedBackground());
			g.fillRect(0, 0, getWidth()-1, getHeight()-1);
		}
	}	
}
