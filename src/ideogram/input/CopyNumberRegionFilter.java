package ideogram.input;

import ideogram.AllParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import junit.framework.Assert;

/**
 * Returns a new CopyNumberDataModel with a reduced number of copy numbers.
 * Filters out small regions.
 * 
 * @author mueller
 *
 */
public class CopyNumberRegionFilter extends AbstractCopyNumberDataModel implements ChangeListener
{
	private ArrayList<CopyNumberRecord> 	copy_numbers;
	private ICopyNumberModel				sourceModel;
	private AllParameters					parameters;
	private boolean							valid;
	

	public CopyNumberRegionFilter(ICopyNumberModel model, AllParameters parameters)
	{
		copy_numbers = new ArrayList<CopyNumberRecord>();

		sourceModel = model;
		this.parameters = parameters;

		sourceModel.addChangeListener(this);
		parameters.addChangeListener(this);

		valid = false;		
	}

	/**
	 * O(n) algorithm for finding contigous regions. Removes all SNPs (sets the
	 * copy number to 2) which are to short or for which the count of SNPs is
	 * too small. Pre-conditions: the extension field of the data model must
	 * contain a Properties entry.
	 * 
	 * Modifies: sourceModel records
	 */
	private void find_contigous_regions()
	{
		copy_numbers.clear();
		
		int group_copy_number = 0, group_start = 0, group_chromosome = 0;
		boolean in_group = false;

		// minimum number of SNPs for a group
		int group_lim = parameters.group_lim;

		// minimum length of a group in base pairs
		long min_length = (long) (parameters.min_length * 1000000.0);

		int i = 0;
		while( i < sourceModel.size() )
		{
			boolean advance = true;
			CopyNumberRecord rec = sourceModel.get(i);
			
			Assert.assertNotNull(rec);

			if( !in_group && (rec.copy_number != 2) )
			{ // start a new group
				in_group = true;
				group_chromosome = rec.locus.chromosome;
				group_copy_number = rec.copy_number;
				group_start = i;
			}

			if( in_group )
			{
				boolean terminate_group = false;
				int group_end = 0;

				if( (rec.copy_number != group_copy_number)
						|| (rec.locus.chromosome != group_chromosome) )
				{ // the current frame i belongs NOT to the group => do not
					// increment i
					group_end = i - 1;
					advance = false;
					Assert.assertTrue(group_end >= group_start);
					terminate_group = true;
				}

				if( !terminate_group && (i == sourceModel.size() - 1) )
				{ // this is the last frame of this chromosome => increment i
					group_end = i;
					terminate_group = true;
				}

				if( terminate_group )
				{ // current group will be terimnated
					in_group = false;
					// length of the whole group
					long length = sourceModel.get(group_end).locus.interval.to
							- sourceModel.get(group_start).locus.interval.from
							+ 1;

					if( (group_end - group_start + 1 >= group_lim) && (length >= min_length) )
					{ 	// group fulfills all conditions => copy group
						for( int j = group_start; j <= group_end; ++j )
						{
							copy_numbers.add( sourceModel.get(j) );
						}
					}
				}
			}

			if( advance )
				++i;
		}
	}


	public int size()
	{
		validate();
		return copy_numbers.size();
	}

	public void stateChanged(ChangeEvent e)
	{
		invalidate();
	}

	public CopyNumberRecord get(int j)
	{
		validate();
		return copy_numbers.get(j);
	}

	public void validate()
	{
		if( ! valid )
		{
			find_contigous_regions();
			valid = true;
		}
	}
	
	public void invalidate()
	{
		if( valid )
		{
			valid = false;
			fireChangeEvent();
		}
	}

	public LinkedList<String> getFileName()
	{
		if( sourceModel != null ) {
			return sourceModel.getFileName();
		}
		else return null;
	}

	public Iterator<CopyNumberRecord> iterator()
	{
		validate();
		return copy_numbers.iterator();
	}

	public Collection<CopyNumberRecord> toCollection()
	{
		validate();
		return copy_numbers;
	}
	
	public void detach()
	{
		if( sourceModel != null )
		{
			sourceModel.removeChangeListener( this );
			sourceModel = null;
		}
		parameters.removeChangeListener(this);
	}

	public LinkedList<String> getHeader() {
		if(sourceModel!=null) {
			return sourceModel.getHeader();
		}
		else return null;
	}

	public LinkedList<String> getChipType() {
		if(sourceModel!=null) {
			return sourceModel.getChipType();
		}
		else return null;
	}
	
	public void clear()
	{
		detach();
	}
}
