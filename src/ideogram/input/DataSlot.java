package ideogram.input;


import ideogram.AllParameters;

import java.util.Collection;
import java.util.LinkedList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A DataSlot encapsulates one or more ICopyNumberModel datasets with all configurations.
 * It is a "Strategy" in the sense that it encapsulates different (transparent) data flows.
 * 
 * @author mueller
 *
 */
public class DataSlot extends AbstractCopyNumberDataModel implements ChangeListener
{
	private LinkedList<ICopyNumberModel>	sourceModels;
	private ICopyNumberModel				output;
	private CopyNumberMerger				merger,
											all;
	private boolean							valid;
	private AllParameters					parameters;
	
	public DataSlot( AllParameters parameters )
	{
		sourceModels = new LinkedList<ICopyNumberModel>();
		output = new MemoryCopyNumberModel();
		this.parameters = parameters;
		parameters.addChangeListener( this );
		
		merger = new CopyNumberMerger();
		all = new CopyNumberMerger();
		
		valid = false;
	}

	/**
	 * Updates the current data routing
	 *
	 */
	private void updateConfiguration()
	{
		// create a new model with the correct routing
		if( output != merger )
		{
			output.removeChangeListener( this );
			output.detach();
			output = null;
		}
		
		merger.removeChangeListener( this );
		merger.detach();
		merger.setDifferenceMode( parameters.method == AllParameters.Method.RELIABILITY );
		
		if( parameters.preFilter )
		{
			for( ICopyNumberModel model : sourceModels )
			{
				merger.add( new CopyNumberRegionFilter( model,  parameters ) );
			}
		}
		else
		{
			for( ICopyNumberModel model : sourceModels )
			{
				merger.add( model );
			}			
		}
		
		if( parameters.postFilter )
		{
			output = new CopyNumberRegionFilter( merger, parameters );
		}
		else
		{
			output = merger;
		}
		
		output.addChangeListener( this );
	}

	public void addModel( ICopyNumberModel model )
	{
		if( model == null )
			return;
		
		if( !sourceModels.contains(model) )
		{
			sourceModels.add( model );
			model.addChangeListener( this );
			all.add(model);
			invalidate();
		}
	}
	
	public void detach()
	{
		parameters.removeChangeListener(this);
	}
	
	public void clear()
	{
		detach();
		for( ICopyNumberModel model : sourceModels )
		{
			model.removeChangeListener( this );
		}
		sourceModels.clear();
		merger.detach();
		all.detach();
		invalidate();
	}
	
	public Collection<ICopyNumberModel> getModels()
	{
		return sourceModels;
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
		if( ! valid )
		{
			updateConfiguration();
			valid = true;
		}
	}
	
	
	public CopyNumberRecord get(int j)
	{
		validate();
		return output.get(j);
	}


	public LinkedList<String> getFileName()
	{
		LinkedList<String> info = new LinkedList<String>();
		validate();
		for( ICopyNumberModel model : sourceModels )
		{
			info.addAll(model.getFileName());
		}
		return info;
	}

	public int size()
	{
		validate();
		return output.size();
	}

	public void stateChanged(ChangeEvent e)
	{
		invalidate();
	}

	public Collection<CopyNumberRecord> toCollection()
	{
		validate();
		return output.toCollection(); 
	}

	public Collection<CopyNumberRecord> getAll()
	{
		validate();
		return all.toCollection();
	}

	public LinkedList<String> getHeader() {
		LinkedList<String> header = new LinkedList<String>();
		validate();
		for( ICopyNumberModel model : sourceModels )
		{
			header.addAll(model.getHeader());
		}
		return header;
	}

	public LinkedList<String> getChipType() {
		LinkedList<String> chip = new LinkedList<String>();
		validate();
		for( ICopyNumberModel model : sourceModels )
		{
			chip.addAll(model.getChipType());
		}
		return chip;
	}

}
