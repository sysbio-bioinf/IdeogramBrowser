package ideogram.input;

import ideogram.event.IChangeNotifier;

public interface IGenomicMarkerModel extends IChangeNotifier, Iterable<GenomicMarker>
{
	long getLength();
	public GenomicMarker findNearest( long bp );	
}
