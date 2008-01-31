/*
 * Created on 02.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram;

import java.util.*;
import java.awt.Color;
import ideogram.tree.Interval;

/**
 * A collection of chromosomal Markers belonging to one chromosome in one loaded
 * data set. This means that for each loaded CNT File 23 
 * {@link MarkerCollection}s will be created. Each {@link MarkerCollection} will
 * belong to one {@link IdeogramView}.
 * 
 * @author muellera
 * 
 */
public class MarkerCollection extends Observable
{
	protected ArrayList<Marker>	markers;

	protected LinkedList<String>	filename;

	protected Color		color;

	public MarkerCollection()
	{
		markers = new ArrayList<Marker>();
	}

	/**
	 * Gives the MarkerCollection a name.
	 * 
	 * @param name
	 */
	public MarkerCollection(LinkedList<String> name)
	{
		this();
		this.filename = name;
	}

	public List<Marker> getMarkers()
	{
		return markers;
	}

	/**
	 * Adds a marker to the collection
	 * 
	 * @param marker
	 */
	public void add(Marker marker)
	{
		if( marker == null ) {
			return;
		}
		markers.add(marker);
		setChanged();
	}

	/**
	 * Find all Markers that are located in the given interval.
	 * @param interval
	 * @return A LinkedList with all markers which intersect with the given
	 *         interval.
	 */
	public List<Marker> find(Interval interval)
	{
		LinkedList<Marker> result = new LinkedList<Marker>();

		Iterator<Marker> iter = markers.iterator();
		while( iter.hasNext() )
		{
			Marker m = iter.next();

			if( m.interval.intersects(interval) )
			{
				result.add(m);
			}
		}

		// TODO
		// java.util.Collections.binarySearch(markers,new Long(interval.to));

		return result;
	}

	/**
	 * Finds the marker lying nearest to the given bp position
	 * 
	 * @param bp
	 * @return A pointer to the marker or null if nothing was found.
	 */
	public Marker findNearest( long bp )
	{
		Iterator iter = markers.iterator();
		long mindist = 0;
		Marker bestmarker = null;

		while( iter.hasNext() )
		{
			Marker m = (Marker) iter.next();
			long dist = m.interval.distance(bp);
			if( bestmarker == null
					|| dist < mindist
					|| ((bestmarker != null && dist == mindist && 
							m.interval.borderDist(bp) < bestmarker.interval.borderDist(bp))) )
			{
				bestmarker = m;
				mindist = dist;
			}
		}

		return bestmarker;
	}

	/**
	 * Find nearest marker index with orientation.
	 * 
	 * @param bp
	 * @param right_side
	 * @return Index of the nearest marker.
	 */
	public int findNearest(long bp, boolean right_side)
	{
		Iterator iter = markers.iterator();
		long mindist = 0;
		Marker bestmarker = null;
		int bestindex = -1, index = 0;

		while( iter.hasNext() )
		{
			Marker m = (Marker) iter.next();
			boolean mside = (m.value > 2) || (m.value < 0);
			if( m.value != 2 && mside == right_side )
			{
				long dist = m.interval.distance(bp);
				if( bestmarker == null || dist < mindist )
				{
					bestmarker = m;
					bestindex = index;
					mindist = dist;
				}
			}
			++index;
		}

		return bestindex;
	}

	/**
	 * Find nearest with orientation.
	 * 
	 * @param bp
	 * @param right_side
	 * @return The nearest marker.
	 */
	public Marker findNeighbour(Marker target, boolean right_side,
			boolean forward)
	{
		Iterator iter = markers.iterator();
		Marker neighbour = null;
		Marker last = target;
		boolean found = false;

		while( iter.hasNext() )
		{
			neighbour = (Marker) iter.next();
			if( forward )
			{
				if( right_side )
				{
					if( found && neighbour.value > 2 )
						return neighbour;
					if( neighbour.equals(target) )
					{
						found = true;
						last = neighbour;
					}
				} else
				{
					if( found && neighbour.value < 2 )
						return neighbour;
					if( neighbour.equals(target) )
					{
						found = true;
					}
				}
			} else
			{
				if( right_side )
				{
					if( neighbour.equals(target) )
					{
						return last;
					}
					if( !found && neighbour.value > 2 )
						last = neighbour;
				} else
				{
					if( neighbour.equals(target) )
					{
						return last;
					}
					if( neighbour.value < 2 )
						last = neighbour;
				}
			}
		}
		return last;
	}

	public void clear()
	{
		markers.clear();
		setChanged();
	}

	public LinkedList<String> getName()
	{
		return filename;
	}

	public void setName(LinkedList<String> name)
	{
		this.filename = name;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public int size()
	{
		if( markers == null )
			return 0;
		return markers.size();
	}

	public void add(MarkerCollection collection)
	{
		Iterator iter = collection.getMarkers().iterator();
		while( iter.hasNext() )
		{
			add((Marker) iter.next());
		}
	}
}
