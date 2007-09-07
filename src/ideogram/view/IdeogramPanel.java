/**
 * Ideogram bean.
 *
 */
package ideogram.view;

import ideogram.GraphUtil;
import ideogram.JMultiLineToolTip;
import ideogram.db.Band;
import ideogram.db.IdeogramDB;
import ideogram.tree.Interval;
import ideogram.tree.IntervalTree;
import ideogram.tree.IntervalTreeNode;
import ideogram.tree.IntervalTreeQuery;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JToolTip;

/**
 * Visual representation of a single ideogram.
 * To show the ideogram a database has to be loaded or attached
 * with IdeogramBean.LoadDatabase or IdeogramBean.setIdeogramDB
 * 
 * TODO: Split this component into different view sub-classes for
 *   1. the karyogram
 *   2. the gene markers
 *   3. the different marker lines
 *   4. the SNP markers
 * These different components could be synchronized (i.e. start/stop basepair) 
 * and could have their own routines for e.g. showing tooltips.   
 * 
 * 
 * @author muellera
 *
 */
public class IdeogramPanel extends AbstractChromosomalPanel 
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	protected static final int MAX_LABEL_LENGTH = 8;

	private IdeogramDB db;
	private byte chromosome;
	private long chromosomeLength;
    private boolean showDetails;
    
	public IdeogramPanel()
	{
		this(null,(byte)1);
	}

	public IdeogramPanel(IdeogramDB db,byte chromosome)
	{
		this.chromosome = chromosome;
		showDetails = false;
        setToolTipText("");       
        setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
        setMinimumSize(new Dimension(20,100));
        setPreferredSize(new Dimension(20,100));
        setMaximumSize(new Dimension(200,Integer.MAX_VALUE));
        
		this.db = db;
	}

	/**
	 * 
	 * @return The currently attached ideogram database 
	 * (or null if no database is attached).
	 */
	public IdeogramDB getIdeogramDB()
	{
		return db;
	}
	
	/**
	 * Attaches an ideogram database to the IdeogramBean.
	 * @param db
	 */
	public void setIdeogramDB(IdeogramDB db)
	{
		if (this.db != db)
		{
			this.db = db;
			repaint();
		}
	}
    	
	/**
	 * 
	 * @return True if a database is attached.
	 */
	public boolean isIdeogramDBLoaded()
	{
		return db != null;
	}

	/**
	 * 
	 * @return The shown chromosome (1..24)
	 */
	public int getChromosome()
	{
		return chromosome;
	}

	/**
	 * Sets the currently shown chromosome. <var>chromosome</var> has to be
	 * in the range 1..24 (where 23=X,24=Y).
	 * 
	 * @param chromosome
	 * @throws java.lang.IllegalArgumentException
	 */
	public void setChromosome(byte chromosome)
	{        
		if (chromosome < 1 || chromosome > 24)
			throw new IllegalArgumentException(
				"chromosome has to be in the range 1..24 '" + chromosome + "'");
		if( chromosome != this.chromosome )
		{
			this.chromosome = (byte) chromosome;
	        chromosomeLength = 0;
			repaint();
		}
	}
	
    
    public long getChromosomeLength()
    {
        if( chromosomeLength == 0 )
        {
            chromosomeLength = getTree().getRange().to;
        }
        
       return chromosomeLength; 
    }

    
    public void paintComponent(Graphics graphics)
    {
    	paintIdeogram(graphics);
    }
    
	/**
	 * Paints the ideogram into the given graphical context.
	 * 
	 * @param g
	 * @param width
	 * @param height
	 */
	public void paintIdeogram(Graphics g)
	{		
		Insets in = getInsets();
		int xofs = in.left,
			yofs = in.top,
			width = getWidth()-in.left-in.right, 
			height = getHeight()-in.top-in.bottom;
		
		//Interval view = getVisibleInterval();
		Interval view = new Interval(0,MAX_BASEPAIRS);
		
		g.setPaintMode();

		byte minLevel, maxLevel;

		if( isShowDetails() )
		{
			minLevel = 2;
			maxLevel = 2;
		}
		else
		{
			minLevel = 1;
			maxLevel = 1;
		}
		
		if (db == null)
		{	// no database: draw a cross
			g.setColor(Color.RED);
			g.drawLine(xofs, yofs, width - 1, height - 1);
			g.drawLine(width - 1, height - 1, xofs, yofs);
			return;
		}

        float fontSize = Math.min(20.0f,Math.max(6.0f,8.0f*(float)width/100));
		Font font = g.getFont().deriveFont(fontSize);
		g.setFont(font);
		
		// allocate colors
		Color[] col = new Color[6];

		col[Band.DENSITY_UNKNOWN] = new Color(255, 0, 0);
		col[Band.DENSITY_GNEG] = Color.white;
		col[Band.DENSITY_GPOS] = new Color(10, 10, 10);
		col[Band.DENSITY_GVAR] = new Color(150,150,200); // new Color(100, 255, 100);
		col[Band.DENSITY_ACEN] = new Color(32, 32, 255);
		col[Band.DENSITY_STALK] = Color.white;

		// find bands
		IntervalTreeQuery query = new IntervalTreeQuery(getTree());
		ArrayList list = query.Query(view, minLevel, maxLevel);

		int  r = width / 5,
		
		// radius of the edges
		y_start = -1,
		// contains the last upper end (or -1 if there was no end)
		count = 0; // number of shown bands

		byte 	last_arm = 0,
				next_arm = 0,
				last_type = Band.DENSITY_UNKNOWN,
				next_type = Band.DENSITY_UNKNOWN;

		boolean top_closed = true, 
				bottom_closed = true;

		for( int i = 0; i < list.size(); ++i )
		{
			IntervalTreeNode node = (IntervalTreeNode) list.get(i);
			Interval I = node.getInterval();
			Band band = (Band) node.content;

			++count;
			next_arm = 0;
			next_type = Band.DENSITY_UNKNOWN;
			for (int i2 = i + 1; i2 < list.size(); ++i2)
			{
				IntervalTreeNode node2 = (IntervalTreeNode) list.get(i2);
				Interval I2 = node2.getInterval();
				Band band2 = (Band) node2.content;

				if (I2.intersects(view)) //  (band2.subsubband == 0) && 
				{
					next_arm = band2.arm;
					next_type = band2.density;
					break;
				}
			}

			long start = I.from, 
				end = I.to;

			if (start < view.from)
			{
				start = view.from;
				top_closed = false;
			}
			if (end > view.to)
			{
				end = view.to;
				bottom_closed = false;
			}

			int y1 = convertBaseToYCoord(start), 
				y2 = convertBaseToYCoord(end);

			boolean upper_end = false, 
					lower_end = false;

			if (last_arm != band.arm)
			{
				upper_end = true;
			}

			if ((last_type == Band.DENSITY_STALK)
				&& (band.density != Band.DENSITY_STALK))
			{
				upper_end = true;
			}

			if (band.arm != next_arm)
			{
				lower_end = true;
			}

			if ((band.density != Band.DENSITY_STALK)
				&& (next_type == Band.DENSITY_STALK))
			{
				lower_end = true;
			}

			if ((band.density == Band.DENSITY_STALK)
				&& next_type != Band.DENSITY_STALK)
			{
				lower_end = true;
			}

			if (band.density != Band.DENSITY_STALK)
			{
				// adapt edge radius if y2-y1 is too small
				int rr = r;
				int cnt = 0;
				if (upper_end) ++cnt;
				if (lower_end) ++cnt;
				
				if (cnt * r * 2 > (y2 - y1 + 1))
				{
					rr = (y2 - y1 + 1) / (2*cnt);
				}
				int rt = upper_end ? rr : 0, 
					rb = lower_end ? rr : 0;

				g.setColor(col[band.density]);
				GraphUtil.roundFilledRectangle(
					g,
					xofs,
					y1,
					xofs + width,
					y2,
					(top_closed ? rt : 0),
					(top_closed ? rt : 0),
					(bottom_closed ? rb : 0),
					(bottom_closed ? rb : 0));

				if (y_start < 0)
				{
					y_start = y1;
				}
				if (lower_end && y_start >= 0)
				{
					// draw surrounding box if a lower end is given
					g.setColor(Color.black);
					GraphUtil.roundRectangle(
						g,
						xofs,
						y_start,
						xofs + width,
						y2,
						rr,
						rr,
						rr,
						rr,
						top_closed,
						true,
						bottom_closed,
						true);

					y_start = -1;
					top_closed = true;
					bottom_closed = true;
				}
			}
			else
			{
				// Band.DENSITY_STALK -> no bounding box
				y_start = -1;
				top_closed = true;
				bottom_closed = true;
			}

			last_arm = band.arm;
			last_type = band.density;
		}

	}

    /**
     * 
     * @return The current ideogram tree for the chromosome of this ideogram.
     */
	private IntervalTree getTree()
    {
        if( db == null )
            throw new IllegalStateException("Ideogram database is not initialized");
        
        if( chromosome < 1 || chromosome > db.getTree().length )
            throw new IllegalStateException("Chromosome "+chromosome+" is invalid!");
                
        return db.getTree()[chromosome-1];            
    }
	
	// EVENT LISTENERS
	public JToolTip createToolTip()
	{
	    return new JMultiLineToolTip();
	}
	
	/**
	 * Custom tooltip texts (showing markers and chromosomal regions).
	 */
	public String getToolTipText(MouseEvent e)
	{
		if( db == null )
			return null;
			
		Point p = e.getPoint();
		
		String tip = null;
		
		long bp = convertYCoordToBase(p.y);
		
		// find chromosomal location
		IntervalTreeQuery query = new IntervalTreeQuery(getTree());
		ArrayList list = query.Query(new Interval(bp,bp), (byte)2, (byte)2);
		if(list.size() > 0)
		{
			IntervalTreeNode node = (IntervalTreeNode)list.get(0);
			Band band = (Band)node.content;
			tip = " " + band.toString() + " ";
		}
        return tip;
	}
            
    /**
     * Selects a point in the ideogram (usually a left mouse click) 
     * @param p Point in component coordinates. 
     */
    public void selectPoint(long bp) 
    {
		// select chromosomal location with the mouse
		if( db != null )
		{
			IntervalTreeQuery query = new IntervalTreeQuery(db.getTree()[chromosome-1]);
			ArrayList list = query.Query(new Interval(bp,bp), (byte)2, (byte)2);
			if(list.size() > 0)
			{
                /*
				IntervalTreeNode node = (IntervalTreeNode)list.get(0);
				Band band = (Band)node.content;
                */
				// TODO: selected band
			}
		}
    }
    
	public boolean isShowDetails()
	{
		return showDetails;
	}

	public void setShowDetails(boolean showDetails)
	{
		this.showDetails = showDetails;
		repaint();
	}
}
