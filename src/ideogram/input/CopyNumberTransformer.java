package ideogram.input;

import ideogram.AllParameters;
import ideogram.AllParameters.FieldA;
import ideogram.AllParameters.FieldB;
import ideogram.input.AffymetrixCntReaderModel.AffyCntRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Transforms a AffymetrixCntReadModel into an ICopyNumberDataModel via thresholding
 * and filtering techniques. 
 * 
 * @author mueller
 * 
 */
public class CopyNumberTransformer extends AbstractCopyNumberDataModel
		implements ChangeListener
{
	private ArrayList<CopyNumberRecord> copy_numbers;
	private boolean						valid;			// valid state?
	private AffymetrixCntReaderModel	dataModel;
	private AllParameters				parameters;

	/**
	 * Transforms an IIdeogramDataModel with an IFilterModel into a
	 * ICopyNumberModel.
	 * 
	 */
	public CopyNumberTransformer(	AffymetrixCntReaderModel sourceModel, 
									AllParameters parameters )
	{
		super();
		
		copy_numbers = new ArrayList<CopyNumberRecord>();

		dataModel = sourceModel;
		this.parameters = parameters;

		valid = false;
		
		dataModel.addChangeListener(this);
		parameters.addChangeListener(this);

		//parameters.removeChangeListener(this);
		//valid=false;
	}

	/**
	 * Creates a copy number (1=loss,2=normal,3=gain) from a floating point value.
	 * 
	 * @param lower_bound
	 * @param upper_bound
	 * @param value 1 = loss, 2 = normal, 3 = gain, -1 = illegal value
	 * @return
	 */
	private int threshold(float lower_bound, float upper_bound, float value)
	{
		if( Float.isNaN(value) )
			return -1;
		
		int copy_number = 2;
		if( value < lower_bound )
		{
			copy_number = 1;
		} else
		{
			if( value <= upper_bound )
				copy_number = 2;
			else
				copy_number = 3;
		}
		return copy_number;
	}

	/*
	 * Computes a 90% cutoff at the quantile of the absolute values of the given
	 * ArrayList of doubles.
	 * 
	 * @param The value name (see DataModel)
	 * @return The cutoff value
	 */
	/*private double compute_cutoff(AllParameters.Field field)
	{
		double[] ar = dataModel.getAllValues(field);
		return MathUtility.quantile(ar, 0.9f);
	}*/



	private AllParameters.FieldA getWhichConfidence(AllParameters.FieldA field)
	{
		if (field==null) return null;
		switch( field )
		{
			case SPA:	return AllParameters.FieldA.SPA_P;
			case GSA:	return AllParameters.FieldA.GSA_P;
			// case LOH:	return AllParameters.Field.GSA;
		}
		
		return null;
		// throw new IllegalArgumentException("value = "+field.name());
	}

	/**
	 * Updates the copy number values in the array <var>aCopyNumber</var>.
	 * AM-2006-08-03
	 */
	private void updateCopyNumbers()
	{
		copy_numbers.clear();
		if( dataModel == null )
			return;
	
		copy_numbers.ensureCapacity(dataModel.getNumMarkers());
		float bounds[] = parameters.getBounds();
		
		for( int i = 0; i < dataModel.getNumMarkers(); ++i )
		{
			float value;
			CopyNumberRecord cn = new CopyNumberRecord();
			AffyCntRecord rec = dataModel.getRecord(i);

			cn.copy_number = 2;
			cn.confidence = Float.NaN;
			cn.id = i;
			cn.locus = rec.locus;
			cn.info = internalGetInfo(i);
			cn.setLogRatio(rec.log2ratio);
			
			/*
			 * TODO As the CopyNumberRecords are now able to store the log ratio
			 * enable this method to actually store them in the records. This 
			 * would make it possible to display the actual log ratios in 
			 * ideogramb browser. 
			 */
			switch(dataModel.getVersion()) {
			case V1_0:
				// thresholding
				value = dataModel.getValue(parameters.fieldA, i);
				cn.copy_number = threshold(bounds[0], bounds[1], value);
				
				FieldA whichConfidence = getWhichConfidence(parameters.fieldA);
				if( parameters.fieldA == FieldA.LOH )
				{	// save SPA copy number to confidence value for LOH mode
					// -1 = LOSS , 0 = NORMAL, 1 = GAIN
					// LOH + GAIN => inconsistent information!
					cn.confidence = threshold( parameters.spa_lower, parameters.spa_upper, rec.spa_cn ) - 2;
				} else if( whichConfidence != null ) {
					float pValue = Math.abs(dataModel.getValue(whichConfidence, i));
					if( !Float.isNaN(pValue) && (pValue < parameters.pValue_upper) )
					{	// aberration not significant
						cn.copy_number = 2;
					}
				}
			break;
			default:
				// thresholding
				if(parameters.fieldB==FieldB.CNSTATE)
					// directly pass through CNState, if CNState is selected
					cn.copy_number = rec.cnstate;
				else {
					value = dataModel.getValue(parameters.fieldB, i);
					cn.copy_number = threshold(bounds[0], bounds[1], value);
				}
			
				// TODO check if this is implemented correctly
				if ( parameters.fieldB == FieldB.LOHPROB ) {
					if(Float.isNaN(rec.lohProb)) {
						cn.copy_number = 2;		// don't show if no loh data avail.
					}
					else {
						if(rec.cnstate==-1) {		// no CN-data avail.
							cn.confidence = 0;
						}
						else {
							switch(rec.cnstate) {
							case 0:	cn.confidence = -1;
								break;
							case 1: cn.confidence = -1;
								break;
							case 2: cn.confidence = 0;
								break;
							case 3: cn.confidence = 1;
								break;
							case 4: cn.confidence = 1;
								break;
							default:
								cn.copy_number = 2;
							}
						}
					}
		
				} /*else if ( parameters.fieldB == FieldB.LOHSTATE) {
					if(rec.lohState==AffyCntRecord.UNKNOWN) {
						cn.copy_number = 2;		// don't show if no loh data avail.
					}
					else {
						switch(rec.lohState) {
						case 0:	cn.confidence = -1;
							break;
						case 1: cn.confidence = 0;
							break;
						default:cn.copy_number = 2;
						
						}
					}
				}*/
			}
			copy_numbers.add(cn);
		}
	}
	
	public void detach()
	{
		dataModel.removeChangeListener(this);
		parameters.removeChangeListener(this);
	}

	public void clear()
	{
		detach();
		copy_numbers.clear();
		dataModel.clear();
		invalidate();
	}

	public LinkedList<String> getFileName()
	{
		validate();
		if( dataModel != null )
			return dataModel.getFileName();
		else
			return null;
	}

	public boolean isValid()
	{
		return valid;
	}

	/**
	 * Validates the internal state.
	 * 
	 * AM-2006-08-03
	 * 
	 */
	protected void validate()
	{
		if( !valid )
		{
			updateCopyNumbers();
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


	public int size()
	{
		validate();
		return copy_numbers.size();
	}

	public void stateChanged(ChangeEvent e)
	{
		//System.err.println("CopyNumberTransformer.stateChanged");

		/*
		if( e.getSource() instanceof AllParameters )
		{
			invalidate();
			return; // AM-2006-08-03
		}

		if( e.getSource() instanceof AffymetrixCntReaderModel )
		{
			invalidate();
			return; // AM-2006-08-03
		}

		if( e.getSource() instanceof TabbedTextReaderModel )
		{
			invalidate();
			return; // AM-2006-08-03
		}
		*/
		

		
		invalidate();
	}

	public AffymetrixCntReaderModel getDataModel()
	{
		return dataModel;
	}

	private String internalGetInfo(int i)
	{	
		StringBuffer buf = new StringBuffer();
		//String whichValue = parameters.getWhichValue();
		//String whichConfidence = getWhichConfidence(whichValue);

		buf.append( dataModel.getMarkerName(i) );
		buf.append("\n");
		//buf.append(parameters.getFieldName()  + "=");
		//buf.append(dataModel.getValue(parameters.field, i));
		AffymetrixCntReaderModel.AffyCntRecord rec = dataModel.getRecord(i);
		
		buf.append("Log2Ratio = "+rec.log2ratio+"\n");
		
		if( !Float.isNaN(rec.gsa_cn) )
		{
			buf.append("LOH = "+rec.loh+"\n");
			buf.append("SPA_CN = "+rec.spa_cn+"\n");
			buf.append("SPA_pVal = "+rec.spa_pval+"\n");
			buf.append("GSA_CN = "+rec.gsa_cn+"\n");
			buf.append("GSA_pVal = "+rec.gsa_pval+"\n");
			buf.append("CPA_pVal = "+rec.cpa_pval+"\n");			
		}
		
		if(!Float.isNaN(rec.hmmmedianlog2ratio)) {
			buf.append("HmmMedianLog2Ratio = "+rec.hmmmedianlog2ratio+"\n");
			buf.append("NegLog10PValue = "+rec.neglog10pvalue+"\n");
		}
		
		//paired
		/*if(!Float.isNaN(rec.hmmmedianlog2ratiomin)) buf.append("HmmMedianLog2RatioMin = "+rec.hmmmedianlog2ratiomin+"\n");
		if(!Float.isNaN(rec.hmmmedianlog2ratiomax)) buf.append("HmmMedianLog2RatioMax = "+rec.hmmmedianlog2ratiomax+"\n");
		if(!Float.isNaN(rec.neglog10pvaluemin)) buf.append("NegLog10PValueMin = "+rec.neglog10pvaluemin+"\n");
		if(!Float.isNaN(rec.neglog10pvaluemax)) buf.append("NegLog10PValueMax = "+rec.neglog10pvaluemax+"\n");
		if(!Float.isNaN(rec.cnstatemin)) buf.append("CNStateMin = "+rec.cnstatemin+"\n");
		if(!Float.isNaN(rec.cnstatemax)) buf.append("CNStateMax = "+rec.cnstatemax+"\n");
		if(!Float.isNaN(rec.log2ratiomin)) buf.append("Log2RatioMin = "+rec.log2ratiomin+"\n");
		if(!Float.isNaN(rec.log2ratiomax)) buf.append("Log2RatioMax = "+rec.log2ratiomax+"\n");*/
		
		// 1.1 loh
		if(!Float.isNaN(rec.refHetRate)) {
			buf.append("RefHetRate = "+rec.refHetRate+"\n");
		}
		if(!Float.isNaN(rec.lohProb)) {
			//buf.append("Call = "+rec.call[0]+rec.call[1]+"\n");
			buf.append("LohState = "+rec.lohState+"\n");
			buf.append("LohProb = "+rec.lohProb+"\n");
			buf.append("RetProb = "+rec.retProb+"\n");
		}
		
		if(rec.chipnum!=-1) buf.append("ChipNum = "+rec.chipnum+"\n");

		return buf.toString();
	}

	public CopyNumberRecord get(int j)
	{
		validate();
		return copy_numbers.get(j);
	}

	public Collection<CopyNumberRecord> toCollection()
	{
		validate();
		return copy_numbers;
	}

	public LinkedList<String> getHeader() {
		validate();
		if (dataModel!=null) {
			return dataModel.getHeader();
		}
		else {
			return null;
		}
	}

	public LinkedList<String> getChipType() {
		if(dataModel!=null) {
			return dataModel.getChipType();
		}
		else {
			return null;
		}
	}


}
