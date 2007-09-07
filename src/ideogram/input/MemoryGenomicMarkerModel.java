package ideogram.input;

import java.util.Iterator;
import java.util.LinkedList;

public class MemoryGenomicMarkerModel extends AbstractGenomicMarkerModel
{
	LinkedList<GenomicMarker> data;
	private boolean	valid;
	private long length;
	
	public MemoryGenomicMarkerModel()
	{
		valid = false;
		length = 1;
		data = new LinkedList<GenomicMarker>();
	}

	public Iterator<GenomicMarker> iterator()
	{
		validate();
		return data.iterator();
	}
	
	public void add( GenomicMarker marker )
	{
		data.add( marker );
		invalidate();
	}
	
	public void clear()
	{
		data.clear();
		invalidate();
	}
	
	protected void invalidate()
	{
		if( valid )
		{
			valid = false;
			fireChangeEvent();
		}
	}
	
	protected void validate()
	{
		if( !valid )
		{
			length = 1;
			for( GenomicMarker m : data )
			{
				if( m.interval.to > length )
				{
					length = m.interval.to;
				}
			}
			valid = true;
		}
	}
	
	public long getLength()
	{
		validate();
		return length;
	}
}
