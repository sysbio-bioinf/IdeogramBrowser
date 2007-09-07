/*
 * Created on 02.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram;

import java.awt.*;
import java.awt.event.*;

/**
 * Helper class for drawing round rectangles.
 * 
 * @author muellera
 *
 */
public class GraphUtil 
{
	public static void roundRectangle(
		Graphics g,
		int x1,
		int y1,
		int x2,
		int y2,
		int rtl,
		int rtr,
		int rbl,
		int rbr,
		boolean top,
		boolean right,
		boolean bottom,
		boolean left ) 
	{
		// top left edge
		if (top && left) {
			g.drawArc( x1 , y1 , 2*rtl, 2*rtl, 90, 90 );
		}

		// top right edge
		if (top && right) {
			g.drawArc(x2 - 2*rtr, y1 , 2*rtr, 2*rtr, 0, 90 );
		}

		// bottom left edge
		if (bottom && left) {
			g.drawArc(x1 , y2 - 2*rbl, 2*rbl, 2*rbl, 180, 90 );
		}

		// bottom right edge
		if (bottom && right) {
			g.drawArc( x2 - 2*rbr, y2 - 2*rbr, 2*rbr, 2*rbr, 270, 90 );
		}

		// horizontal
		if (top) 
		{
			g.drawLine( x1 + rtl, y1, x2 - rtr, y1 );
		} else 
		{
			// draw dashed line
			if (left) {
				g.drawLine( x1, y1, x1, y1 + rtl );
			}

			if (right) {
				g.drawLine( x2, y1, x2, y1 + rtr );
			}
		}

		if (bottom) 
		{
			g.drawLine( x1 + rbl, y2, x2 - rbr, y2 );
		} 
		else 
		{
			// draw dashed line
			if (left) {
				g.drawLine(x1, y2 - rbl, x1, y2 );
			}

			if (right) {
				g.drawLine( x2, y2 - rbr, x2, y2 );
			}
		}

		// vertical
		if (left) {
			g.drawLine( x1, y1 + rtl, x1, y2 - rbl );
		}

		if (right) {
			g.drawLine( x2, y1 + rtr, x2, y2 - rbr );
		}
	}
/*
	public static void drawTerminator(
		Graphics g,
		int x1,
		int y1,
		int x2,
		int y2,
		int bg_color,
		int fg_color) 
	{
		int dx = x2 - x1, dy = y2 - y1;

		gdImageFilledRectangle(im, x1, y1 + dy / 2, x1 + dx / 2, y2, fg_color);
		gdImageFilledArc(
			im,
			x1 + dx / 4,
			y1 + dy / 2,
			dx / 2,
			dy,
			0,
			180,
			bg_color,
			gdArc);

		gdImageFilledRectangle(im, x2 - dx / 2, y1, x2, y2 - dy / 2, bg_color);
		gdImageFilledArc(
			im,
			x2 - dx / 4,
			y1 + dy / 2,
			dx / 2,
			dy,
			180,
			360,
			fg_color,
			gdArc);
	}
*/

	public static void roundFilledRectangle(
		Graphics g,
		int x1,
		int y1,
		int x2,
		int y2,
		int rtl,
		int rtr,
		int rbl,
		int rbr ) 
	{
		// top left edge
		g.fillArc(
			x1 ,
			y1 ,
			2*rtl,
			2*rtl,
			90,
			90 );

		// top right edge
		g.fillArc(
			x2 - 2*rtr,
			y1 ,
			2*rtr,
			2*rtr,
			0,
			90 );

		// bottom left edge
		g.fillArc(
			x1,
			y2 - 2*rbl,
			2*rbl,
			2*rbl,
			180,
			90 );

		// bottom right edge
		g.fillArc(
			x2 - 2*rbr,
			y2 - 2*rbr,
			2*rbr,
			2*rbr,
			270,
			90 );

		//
		Polygon p = new Polygon();
		p.addPoint(x1,y1 + rtl);
		p.addPoint(x1 + rtl,y1 + rtl);
		p.addPoint(x1 + rtl,y1);
		p.addPoint(x2 - rtr,y1);
		p.addPoint(x2 - rtr,y1 + rtr);
		p.addPoint(x2,y1 + rtr);
		p.addPoint(x2,y2 - rbr);
		p.addPoint(x2 - rbr,y2 - rbr);
		p.addPoint(x2 - rbr,y2);
		p.addPoint(x1 + rbl,y2);
		p.addPoint(x1 + rbl,y2 - rbl);
		p.addPoint(x1,y2 - rbl);

		g.fillPolygon(p);
	}
	
	public static void main(String[] args)
	{
		Frame frm = new Frame();
		frm.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent event)
				{
				   event.getWindow().setVisible(false);
				   event.getWindow().dispose();
				   System.exit(0);
				}				
			});
		frm.setSize(200,200);
		frm.setVisible(true);
		
		Graphics g = frm.getGraphics();
		
		g.setColor(Color.blue);
		roundFilledRectangle(g,10,50,180,180,20,20,20,20 );
		
		g.setColor(Color.red);
		roundRectangle(g,10,50,180,180,20,20,20,20,true,true,true,true);
	}
}
