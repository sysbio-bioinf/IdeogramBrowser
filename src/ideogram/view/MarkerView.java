package ideogram.view;

import ideogram.input.GenomicMarker;
import ideogram.input.IGenomicMarkerModel;
import ideogram.tree.Interval;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MarkerView extends AbstractChromosomalPanel implements
	MouseListener, ChangeListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	private IGenomicMarkerModel dataModel;
	
	// configuration variables
	//private boolean showMarkerEnds;
	private boolean showProfileLine;
	private boolean	hideIfEmpty;

	private Color 	selectionForeground,
					selectionBackground;
					//profileLineColor,
					//selectedProfileLineColor;
	
	
	// internal state
	private LinkedList<GenomicMarker> selectedValue;
	private Interval rangeSelection;
	private Point	beginPoint,
					endPoint;

	private boolean	isDragging;
	
	
	public MarkerView( IGenomicMarkerModel model )
	{
		isDragging = false;
		
		//showMarkerEnds = true;
		showProfileLine = true;
		hideIfEmpty = false;
		
		selectedValue = new LinkedList<GenomicMarker>();
		rangeSelection = null;
		selectionForeground = new Color(0x33,0x33,0xff);
		selectionBackground = new Color(0xaa,0xaa,0xff);
		//profileLineColor = new Color(0xdd,0xdd,0xdd);
		//selectedProfileLineColor = new Color(0x66,0x66,0xdd);
		// setFocusable(true);
		
		// enableEvents();
		
        setMinimumSize(new Dimension(3,100));
        setPreferredSize(new Dimension(5,100));
        setMaximumSize(new Dimension(20,Integer.MAX_VALUE));
	

		setModel( model );
		addMouseListener(this);
		addMouseMotionListener(this);
		// setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	public void setModel( IGenomicMarkerModel model )
	{
		dataModel = model;
		if( dataModel != null )
			dataModel.addChangeListener(this);
	}
	
    /*private void drawMarker( Graphics g, Rectangle clip, Interval interval, int lineWidth )
	{
    	int w = getWidth(),
    		dx = Math.max(w/3,1),
			y1 = convertBaseToYCoord(interval.from),
			y2 = convertBaseToYCoord(interval.to),
			x1 = 0,
			x2 = w,
			x = w/2;
		
		y1++;
		y2--;
			
	    if( y2 - y1 < 1 )
	        y2 = y1 + 1;
	        	
		g.fillRect( x-lineWidth/2, y1, lineWidth, y2-y1 );	// draw single marker		
			
		if( getShowMarkerEnds() )
		{
	    	g.drawLine(x1,y1,x2,y1);
	        g.drawLine(x1,y2,x2,y2);
		}
	}*/
	    
		
	/*private boolean getShowMarkerEnds()
	{
		return showMarkerEnds;
	}*/
	
	/*private void paintRangeSelection(Graphics g)
	{
		if( getRangeSelection() == null )
			return;
		
		int y1 = convertBaseToYCoord(getRangeSelection().from),
			y2 = convertBaseToYCoord(getRangeSelection().to);
		
		// g.setXORMode(getSelectionBackground());
		g.setPaintMode();
		g.setColor(getSelectionBackground());
		
		g.fillRect(0, y1, getWidth(), y2-y1+1);
	}*/
	
	/*private void paintMarkers(Graphics g)
	{
		Rectangle clip = g.getClipBounds();
		
		Interval range = new Interval( convertYCoordToBase(clip.y),
										convertYCoordToBase(clip.y+clip.height) );
		
		g.setPaintMode();
		g.setColor(getForeground());
		int lwd = Math.max(1, getWidth()/2);
		for( GenomicMarker m : dataModel )
		{
			// if( range.intersects(m.getInterval()) )
			{					
				drawMarker(g, clip, m.interval, lwd );
			}
		}
		
		// draw selected markers
		g.setColor( getSelectionForeground() );
		for( GenomicMarker m : getSelectedValue() )
		{
			if( m != null )
			{
				drawMarker(g, clip, m.interval, lwd );
			}
		}	
	}*/

	public void paintComponent(Graphics g)
	{
		/*super.paintComponent(g);
		paintRangeSelection(g);
		if( isShowProfileLine() )
		{
			g.setPaintMode();
			if( isSelected() )
				g.setColor(selectedProfileLineColor);
			else
				g.setColor(profileLineColor);
			int x = getWidth()/2;
			g.drawLine(x, 0, x, getHeight());
		}*/
		/*paintMarkers(g);*/ // TODO
	}
	
	public boolean isDragging()
	{
		return isDragging;
	}

	public void setShowMarkerEnds(boolean drawMarkerEnds)
	{
		//this.showMarkerEnds = drawMarkerEnds;
		repaint();
	}

	public Collection<GenomicMarker> getSelectedValue()
	{
		return selectedValue;
	}

	public void setSelectedValue(Collection<GenomicMarker> selectedValue)
	{
		this.selectedValue.clear();
		this.selectedValue.addAll(selectedValue);
		repaint();
		firePropertyChange("selectedValue", null, this.selectedValue);
	}

	public Color getSelectionForeground()
	{
		return selectionForeground;
	}

	public void setSelectionForeground(Color selectionForeground)
	{
		this.selectionForeground = selectionForeground;
		repaint();
	}
	
	

	public void mouseClicked(MouseEvent event)
	{
		Point p = event.getPoint();
		long pos = convertYCoordToBase( p.y );
		
		if( event.getButton() == MouseEvent.BUTTON1 )
		{	
			isDragging = false;
			setRangeSelection(null);
			selectedValue.clear();
			GenomicMarker marker = getDataModel().findNearest(pos);
			if( marker != null )
			{
				selectedValue.add(marker);
			}
			repaint();
			firePropertyChange("selectedValue", null, selectedValue);			
		}
	}

	

	public void mouseEntered(MouseEvent arg0)
	{
		//setSelected(true);
	}

	public void mouseExited(MouseEvent arg0)
	{
		//setSelected(false);
	}

	public void mousePressed(MouseEvent event)
	{
		setSelected(true);
		if( event.getButton() == MouseEvent.BUTTON1 )
		{
			beginSelection(event.getPoint());
		}
	}

	private void beginSelection(Point point)
	{
		beginPoint = point;
		endPoint = point;
		setRangeSelection(null);
	}
	
	private void endSelection(Point point)
	{
		endPoint = point;
		if( beginPoint == null )
			endPoint = point;
		Interval x = new Interval(convertYCoordToBase(beginPoint.y),
								  convertYCoordToBase(endPoint.y));
		setRangeSelection(x);
	}

	public void mouseReleased(MouseEvent event)
	{
		if( isDragging )
		{
			isDragging = false;
			firePropertyChange("isDragging", true, false);
		}
	}

	public IGenomicMarkerModel getDataModel()
	{
		return dataModel;
	}

	public void setDataModel(IGenomicMarkerModel dataModel)
	{
		if( this.dataModel != null )
			this.dataModel.removeChangeListener(this);
		
		this.dataModel = dataModel;
		if( this.dataModel != null )
			this.dataModel.addChangeListener(this);

		repaint();
	}

	public void stateChanged(ChangeEvent event)
	{
		if( event.getSource() == dataModel )
		{
			if( dataModel.getLength() == 0 && isHideIfEmpty() )
			{
				setVisible(false);
			}
			else
			{
				setVisible(true);
				repaint();
			}
		}
	}

	public void mouseDragged(MouseEvent event)
	{
		
		if( !isDragging )
		{
			isDragging = true;
			firePropertyChange("isDragging", false, true);
		}
		endSelection(event.getPoint());
	}

	public void mouseMoved(MouseEvent arg0)
	{
	
	}

	public Interval getRangeSelection()
	{
		return rangeSelection;
	}

	public void setRangeSelection(Interval selection)
	{
		rangeSelection = selection;
		selectedValue.clear();
		if( rangeSelection != null )
		{
			for( GenomicMarker m : dataModel )
			{
				if( rangeSelection.intersects(m.interval) )
				{
					selectedValue.add(m);
				}
			}
		}
		repaint();
		firePropertyChange("selectedValue", null, selectedValue);
	}

	public Color getSelectionBackground()
	{
		return selectionBackground;
	}

	public void setSelectionBackground(Color selectionBackground)
	{
		this.selectionBackground = selectionBackground;
		repaint();
	}

	public boolean isShowProfileLine()
	{
		return showProfileLine;
	}

	public void setShowProfileLine(boolean showProfileLine)
	{
		this.showProfileLine = showProfileLine;
		repaint();
	}

	public boolean isHideIfEmpty()
	{
		return hideIfEmpty;
	}

	public void setHideIfEmpty(boolean hideIfEmpty)
	{
		this.hideIfEmpty = hideIfEmpty;
		repaint();
	}
	
}
