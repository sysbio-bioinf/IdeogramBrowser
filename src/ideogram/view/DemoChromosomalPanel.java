package ideogram.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class DemoChromosomalPanel extends AbstractChromosomalPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public DemoChromosomalPanel()
	{
		Dimension dim = new Dimension(100,400);
		setPreferredSize(dim);
		setSize(dim);
	}
	
	/**
	 * Dummy paintComponent
	 */
	public void paintComponent(Graphics g)
	{
		g.setPaintMode();
		long delta = 100000;
		g.setColor(Color.RED);
		for(long pos=0; pos<MAX_BASEPAIRS; pos+=delta)
		{
			int y1 = convertBaseToYCoord(pos),
				y2 = convertBaseToYCoord(pos+delta/2);
			g.fillRect(0, y1, getWidth(), y2-y1);
		}
	}
}
