package ideogram.input;

import ideogram.tree.Interval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.junit.Assert;
import util.SortedMultiIterator;
import util.SweepLineIterator;
import util.TreeSortedMultiSet;


/**
 * Merges an array of ICopyNumberModels
 * @author mueller
 *
 */
public class CopyNumberMerger extends AbstractCopyNumberDataModel implements  ChangeListener
{
	private ArrayList<CopyNumberRecord>	 copy_numbers; 
	private ArrayList<ICopyNumberModel> models;
	private boolean	valid;
	private boolean removeOverlaps;
	private boolean	differenceMode;
	private int	threshold;
	
	public CopyNumberMerger()
	{	
		copy_numbers = new ArrayList<CopyNumberRecord>();
		models = new ArrayList<ICopyNumberModel>();
		removeOverlaps = false;
		differenceMode = false;
		threshold = 1;
	}

	public void clear()
	{
		detach();
		invalidate();
	}
	
	public void detach()
	{
		for( int i=0; i<models.size(); ++i )
		{
			if( models.get(i) != null )
			{
				models.get(i).removeChangeListener( this );
				models.get(i).detach();
			}
		}
		models.clear();
		copy_numbers.clear();
	}
	
	public void setModels( List<ICopyNumberModel> newModels )
	{
		detach();
		
		if( newModels != null )
		{
			for( int i=0; i<newModels.size(); ++i )
			{
				ICopyNumberModel m = newModels.get(i);
				if( m != null )
				{
					models.add( m );
					m.addChangeListener( this );
				}
			}
		}
		invalidate();
	}
	
	
	
	public void add( ICopyNumberModel model )
	{
		if( model != null )
		{
			if( !models.contains(model) )
			{
				models.add( model );
				model.addChangeListener( this );
				invalidate();
			}
		}
	}
		
	private void vote( Collection<CopyNumberRecord> set , int vote[] )
	{
		Assert.assertNotNull(vote);
		Assert.assertEquals(vote.length, 3);
		Arrays.fill(vote, 0);
		
		for( CopyNumberRecord rec : set )
		{			
			if( (rec.copy_number < 2) || (rec.copy_number < 0) ) 	++vote[0];
			if( rec.copy_number == 2 ) 								++vote[1];
			if( (rec.copy_number > 2) || (rec.copy_number < 0) ) 	++vote[2];
		}		
	}

	/**
	 * Does the whole merging work:
	 * 
	 * 1. Find overlapping (corresponding regions) and perform a voting - 
	 * that is count the numbers of models voting for +, 0, or - 
	 * i.e. copy_number > 2, copy_number = 2, copy_number < 2 
	 * 
	 * Indifferent regions are those for which no majority exists.
	 * In this case the copy_number is set to -1.
	 * A copy_number of 2 will be neutral for the voting (???).
	 * 
	 */	
	private void mergeModelsDifference()
	{
		if( this.models == null )
			return ;
		
		copy_numbers.clear();

		Vector<ChromosomeSplitter>	splitters = new Vector<ChromosomeSplitter>(models.size());
		Set<Byte>	chromosomes = new TreeSet<Byte>();
		
		// split all models into chromosomes
		for( ICopyNumberModel model : models )
		{
			ChromosomeSplitter s = new ChromosomeSplitter(model); 
			splitters.add( s );
			chromosomes.addAll( s.getChromosomes() );
		}
		
		// merge chromosome-wise
		for( Byte chr : chromosomes )
		{
			// find all nonempty data models
			Vector< Iterator<CopyNumberRecord> > iterators = new Vector< Iterator<CopyNumberRecord> >(models.size()); 
			for( ChromosomeSplitter s : splitters )
			{
				ICopyNumberModel model = s.getModel(chr);
				if( model != null )
					iterators.add( model.iterator() );
			}
						
			// iterate with a sweep line to find breakpoints and regions
			SweepLineIterator< CopyNumberRecord > piter = new SweepLineIterator< CopyNumberRecord >( new SortedMultiIterator<CopyNumberRecord>(iterators) );
			
			int[]	count = new int[3];
			
			while( piter.hasNext() )
			{
				SweepLineIterator<CopyNumberRecord>.CutLine res = piter.next();
				
				if( res.result.size() == 0 )
					continue;
	
				// voting: count the number of votes for each involved iterator
				Arrays.fill(count,0);
				StringBuffer info = new StringBuffer();
				
				// create a new (virtual) copy number record				
				CopyNumberRecord record = new CopyNumberRecord();
				
				record.locus.chromosome = chr.byteValue();
				record.locus.interval = new Interval(res.from,res.to);
				
				//System.out.println("-------------------------------");
				for( CopyNumberRecord rec : res.result )
				{
					record.ref.add( rec );
					info.append( rec.info );
					info.append("\n");
				}
				
				record.info = info.toString();
				
				// Majority voting
				vote(res.result,count);
				
				// TODO: introduce adjustable thresholds for all four cases +,0,-,?
				if( count[0] >= threshold && count[2] >= threshold )
				{	// inconsistent copy numbers => mark as inconsistent
					record.copy_number = -1;
				}
				else
				{
					record.copy_number = 2;
				
					if( count[0] >= threshold )  record.copy_number = 1;
					if( count[2] >= threshold )  record.copy_number = 3;
				}
				
				//System.out.println("VOTE : "+count[0]+","+count[1]+","+count[2]);
				//System.out.println(record.toString());
				
				if( record.copy_number != 2 )
				{
					copy_numbers.add( record );
				}
			}
		}
	}
	
	
	private void mergeModels()
	{
		if( this.models == null )
			return ;
		
		// create sorted list of copy numbers
		TreeSortedMultiSet< CopyNumberRecord > tmp = new TreeSortedMultiSet< CopyNumberRecord >();
		for( ICopyNumberModel model : models )
		{
			tmp.addAll( model.toCollection() );
		}
		
		copy_numbers.clear();
		copy_numbers.addAll( tmp );
		
		// adjust SNP lengths
		for( int i=0; i<copy_numbers.size(); ++i )
		{
			Locus 	current = copy_numbers.get(i).locus, 
					next = null;
			if( i+1<copy_numbers.size() )
				next = copy_numbers.get(i+1).locus;
			
			if( (next != null) && (next.chromosome == current.chromosome ) )
			{
				current.interval.to = next.interval.from-1;
			}
			else
			{
				current.interval.to = current.interval.from + 20; // TODO: find constant! 
			}
		}
	}

	protected void validate()
	{
		if( !valid )
		{
			if( models.size() > 1 )
			{
				if( differenceMode )
					mergeModelsDifference();
				else
					mergeModels();
			} else
			{
				if( models.size() > 0 )
					copy_numbers.addAll( models.get(0).toCollection() );
			}
			valid = true;
		}
	}
	
	protected void invalidate()
	{
		if( valid )
		{
			valid = false;
			fireChangeEvent();
		}
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

	public LinkedList<String> getFileName()
	{
		/*StringBuffer buf = new StringBuffer();
		buf.append("M: ");
		for( int i=0; i<models.size(); ++i )
		{
			buf.append( models.get(i).getFileName()  );
			if( i+1 < models.size() )
				buf.append(" + ");
		}
		return buf.toString();*/
		
		LinkedList<String> l =  new LinkedList<String>();

		for( int i=0; i<models.size(); i++ )
		{
			l.addAll(models.get(i).getFileName());
		}
		return l;
	}

	public int size()
	{
		validate();
		
		return copy_numbers.size();
	}

	public Iterator<CopyNumberRecord> iterator()
	{
		validate();
		return copy_numbers.iterator();
	}

	/**
	 * 
	 * @return True if overlaps are removed. The SNP intervals are shortened until they are contigously.
	 */
	public boolean isRemoveOverlaps()
	{
		return removeOverlaps;
	}

	public void setRemoveOverlaps(boolean removeOverlaps)
	{
		if( this.removeOverlaps != removeOverlaps )
		{
			this.removeOverlaps = removeOverlaps;
			invalidate();
		}
	}

	public Collection<CopyNumberRecord> toCollection()
	{
		validate();
		return copy_numbers;
	}

	public boolean isDifferenceMode()
	{
		return differenceMode;
	}

	public void setDifferenceMode(boolean differenceMode)
	{
		if( this.differenceMode != differenceMode )
		{
			this.differenceMode = differenceMode;
			invalidate();
		}
	}

	public void setThreshold( int threshold )
	{
		this.threshold = threshold;
		invalidate();
	}

	public LinkedList<String> getHeader() {
		LinkedList<String> l =  new LinkedList<String>();
		for( int i=0; i<models.size(); i++ )
		{
			l.addAll(models.get(i).getHeader());
		}
		return l;
	}

	public LinkedList<String> getChipType() {
		LinkedList<String> l =  new LinkedList<String>();
		for( int i=0; i<models.size(); i++ )
		{
			l.addAll(models.get(i).getChipType());
		}
		return l;
	}
	
	
}
