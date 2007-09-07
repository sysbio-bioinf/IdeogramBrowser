package ideogram.input;

import java.util.HashMap;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Assert;

public class ChromosomeSplitter implements ChangeListener
{
	private ICopyNumberModel model;
	private boolean	valid;
	private HashMap<Byte, MemoryCopyNumberModel > map;

	public ChromosomeSplitter( ICopyNumberModel model )
	{
		Assert.assertNotNull( model );
		this.model = model;
		map = new HashMap<Byte, MemoryCopyNumberModel >();
		valid = false;
		
		model.addChangeListener( this );
	}

	public void stateChanged(ChangeEvent e)
	{
		invalidate();
	}

	private void invalidate()
	{
		if( valid )
		{
			valid = false;
		}
	}
	
	private void validate()
	{
		if( !valid )
		{
			map.clear();
			for( CopyNumberRecord rec : model )
			{
				Byte key = new Byte(rec.locus.chromosome);
				MemoryCopyNumberModel m = map.get(key); 
				if( m == null )
				{
					m = new MemoryCopyNumberModel();
					map.put(key, m);
				}
				m.getRecords().add( rec );				
			}
		}
	}
	
	public MemoryCopyNumberModel getModel( Byte key )
	{
		validate();
		return map.get(key);
	}
	
	public Set<Byte> getChromosomes()
	{
		validate();
		return map.keySet();
	}
}
