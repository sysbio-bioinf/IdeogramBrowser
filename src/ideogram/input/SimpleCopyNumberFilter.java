package ideogram.input;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Filters copy numbers such that only losses/gains/both or all markers are shown.
 * 
 * @author muellera
 *
 */
public class SimpleCopyNumberFilter extends AbstractGenomicMarkerModel implements ChangeListener
{
	/**
	 * Filter Type
	 * LOSS     return only losses
	 * GAIN     return only gains
	 * BOTH     return losses and gains
	 * ALL      return all markers (unfiltered)
	 * 
	 * @author muellera
	 *
	 */
	public enum Type { LOSS, GAIN, BOTH, ALL }

	private ICopyNumberModel	source;
	private Type				type;
	private boolean	valid;
	private LinkedList<GenomicMarker>	data;
	
	
	public SimpleCopyNumberFilter( ICopyNumberModel source, Type type )
	{
		this.source = source;
		this.type = type;
		valid = false;
		data = new LinkedList<GenomicMarker>();
		
		source.addChangeListener( this );
	}
	
	public ICopyNumberModel getSource()
	{
		return source;
	}


	public void stateChanged(ChangeEvent arg0)
	{
		invalidate();
	}

	private void invalidate()
	{
		if( valid )
		{
			valid = false;
			fireChangeEvent();
		}
	}

	private void validate()
	{
		if( valid )
			return;

		data.clear();
				
		for( CopyNumberRecord rec : source )
		{
			if( type == Type.ALL )
			{
				data.add(new GenomicMarker(rec.locus.interval,rec));
				continue;
			}
			if( rec.copy_number < 0 )
				continue;
			
			if( rec.copy_number < 2 )
			{
				if( type == Type.BOTH || type == Type.LOSS )
					data.add(new GenomicMarker(rec.locus.interval,rec));
			} else 
			{
				if( type == Type.BOTH || type == Type.GAIN )
					data.add(new GenomicMarker(rec.locus.interval,rec));				
			}
		}
		
		valid = true;
	}
	
	public List<GenomicMarker> getData()
	{
		validate();
		return data;
	}

	public Iterator<GenomicMarker> iterator()
	{
		return getData().iterator();
	}

	public long getLength()
	{
		return 250000000;
	}
}
