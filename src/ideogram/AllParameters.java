/*
 * Created on 10.03.2006
 *
 */
package ideogram;

import ideogram.event.IChangeNotifier;
import ideogram.input.RResultTransformer;
import ideogram.input.AffymetrixCntReaderModel.FileVersion;
import java.awt.Color;
import java.io.Serializable;
import javax.swing.event.ChangeListener;
import util.ChangeNotifier;
import util.MathUtility;
import util.SystemUtility;

/**
 *  
 * @author muellera
 *
 */
public class AllParameters implements Serializable, IChangeNotifier, Cloneable
{
    private static final long serialVersionUID = 3L;
    
    /**
     * Amplification factor for the log ratio. This is multiplied with the 
     * log ratio in order to make it visible to the user.
     * TODO It might be better to make this a option, that can be set by the 
     *      user.
     */
    private static final double DEFAULT_LOG_RATIO_AMPLIFIER = 7.0;
    
    /**
     * Default size of the normalization frame. Any positive integer spezifies
     * the frame's size. A size of 0 indicates that no normalization should be
     * applied.
     */
    private static final int DEFAULT_NORMALIZATION_FRAME = 0;
    
    // default values for lower and upper bounds
    // V1.0 defaults
    private static final float LOG2RATIO_LOW 		=  -1.0f;
    private static final float LOG2RATIO_UP  		=   1.0f;
    private static final float GSA_LOW 				=   1.0f;
    private static final float GSA_UP  				=   3.0f;
    private static final float SPA_LOW 				=   1.0f;
    private static final float SPA_UP  				=   3.0f;
    private static final float LOH_UP 				=  10.0f;  
    // V1.1 defauls
    private static final float LOG2RATIO_LOW_NEW	=  -0.4f;
    private static final float LOG2RATIO_UP_NEW 	=   0.4f;
    private static final float HMMLOG2RATIO_LOW 	= -0.25f;
    private static final float HMMLOG2RATIO_UP  	=  0.25f;
    private static final float NEGLOG10PVALUE_LOW	=   0.0f;
    private static final float NEGLOG10PVALUE_UP 	=   3.0f;
	private static final float LOHPROB_UP 			=  0.25f;
	//
    private static final float MINLENGTH			= 	0.0f;
    private static final float PVALUE_UP			=   0.0f;
    private static final int   GROUPLIM				=      3;
    private static final int   CONSENSUSTHRESH		=      1;
    
    // minima and maxima for bounds
    // V1.0 defaults
    private static final float LOG2RATIO_MIN 		= -20.0f;
    private static final float LOG2RATIO_MAX  		=  20.0f;
    private static final float GSA_MIN 				=   0.0f;
    private static final float GSA_MAX  			=  10.0f;
    private static final float SPA_MIN 				=   0.0f;
    private static final float SPA_MAX  			=  10.0f;
    private static final float LOH_MAX 				= 100.0f;  
    // V1.1 defaults
    private static final float LOG2RATIO_MIN_NEW 	=  -1.0f;
    private static final float LOG2RATIO_MAX_NEW 	=   1.0f;
    private static final float HMMLOG2RATIO_MIN 	=  -1.0f;
    private static final float HMMLOG2RATIO_MAX 	=   1.0f;
    private static final float NEGLOG10PVALUE_MIN	=   0.0f;
    private static final float NEGLOG10PVALUE_MAX	= 100.0f;
    private static final float LOHPROB_MAX 			=   1.0f;
    //
    private static final float MINLENGTH_MAX		=  10.0f;
    private static final float PVALUE_MAX			=  20.0f;
    private static final int   GROUPLIM_MAX			=    100;
    private static final int   CONSENSUSTHRESH_MAX	=    100;
    
    // default color values
	private static final Color COL_DLOSS 	= Color.getHSBColor(0.25f, 1.0f, 0.8f);	// light green
	private static final Color COL_LOSS		= Color.getHSBColor(0.25f, 1.0f, 0.5f); // dark green
	private static final Color COL_GAIN		= Color.getHSBColor(0.0f, 1.0f, 0.6f);	// dark red
	private static final Color COL_AMP		= Color.getHSBColor(0.0f, 1.0f, 0.9f);	// light red
    
    private transient ChangeNotifier	notifier;

    // global parameters
    public enum Method { NORMAL, RELIABILITY };
    
    // Fields for Version 1.0
    public enum FieldA {
    	UNKNOWN, LOGRATIO, GSA, SPA, GSA_P, SPA_P, LOH;		// unused: CALL
    }
    
    // Fields for Version 1.1
    public enum FieldB {
    	UNKNOWN, LOGRATIO, HMMMEDIANLOG2RATIO, NEGLOG10PVALUE,
    	CNSTATE, REFHETRATE, LOHSTATE, LOHPROB, RETPROB
    	};
    /* unused 1.1 fields:
    	HMMMEDIANLOG2RATIOMIN, HMMMEDIANLOG2RATIOMAX,
    	CNSTATEMIN, CNSTATEMAX, NEGLOG10PVALUEMIN,
    	NEGLOG10PVALUEMAX, LOG2RATIOMIN, LOG2RATIOMAX,
    	CALL */
    	
    public FieldA		fieldA;			// selected value for 1.0 file format combobox
    public FieldB		fieldB;			// selected value for 1.1 file format combobox
    public float        log2ratio_lower,
						log2ratio_upper,
						log2ratio_lower_new,
						log2ratio_upper_new,
						gsa_lower,
						gsa_upper,
						spa_lower,
						spa_upper,
						loh_upper,
						min_length,
    					hmmlog2ratio_lower,
    					hmmlog2ratio_upper, 
    					neglog10pvalue_lower,
    					neglog10pvalue_upper,
    					pValue_upper,
    					lohprob_upper;
    
    public double       logRatioAmplifier;
    public int          normalizationFrame;
    public RResultTransformer.NormalizationMethods normalizationMethod;

    public int			group_lim;
	public boolean 		consensus_mode;
	public int	 		consensus_threshold;
		
	public Method 		method;			// filtering configuration
	public boolean 		preFilter, 
						postFilter;

	public boolean		condensed_mode;
	public boolean		show_profile_lines;
	
	public FileVersion	versionLoaded;
	
	public Color		doublelossColor, lossColor, gainColor, ampColor;
	
	public boolean 		invertColors;
	
    /**
     * default constructor
     */
    public AllParameters()
    {
    	restoreDefaultColors();
    	fieldA = FieldA.LOGRATIO;
    	fieldB = FieldB.CNSTATE;
    	log2ratio_lower=LOG2RATIO_LOW;
		log2ratio_upper=LOG2RATIO_UP;
		gsa_lower=GSA_LOW;
		gsa_upper=GSA_UP;
		spa_lower=SPA_LOW;
		spa_upper=SPA_UP;
		loh_upper=LOH_UP;
		min_length=MINLENGTH;
		
		logRatioAmplifier = DEFAULT_LOG_RATIO_AMPLIFIER;
		normalizationFrame = DEFAULT_NORMALIZATION_FRAME;
		normalizationMethod = RResultTransformer.NormalizationMethods.MEAN;
		// new for 1.1 file format
		log2ratio_lower_new = LOG2RATIO_LOW_NEW;
		log2ratio_upper_new = LOG2RATIO_UP_NEW;
		hmmlog2ratio_lower = HMMLOG2RATIO_LOW;
		hmmlog2ratio_upper = HMMLOG2RATIO_UP;
		neglog10pvalue_lower = NEGLOG10PVALUE_LOW;
		neglog10pvalue_upper = NEGLOG10PVALUE_UP;
		lohprob_upper = LOHPROB_UP;
		
		group_lim=GROUPLIM;
		pValue_upper = PVALUE_UP;
		
		consensus_threshold =CONSENSUSTHRESH;
		consensus_mode = false;
		
		method = Method.NORMAL;
		preFilter = true;
		postFilter = false;
		
		condensed_mode = false;
		show_profile_lines = true;
				
		invertColors = false;
		
		versionLoaded = FileVersion.UNKNOWN;
		
		initTransientState();
    }
    
    protected void initTransientState()
    {
		notifier = new ChangeNotifier();    	
    }

    /**
     * Adjust consistency of the parameters
     *
     */
    public void check()
    {
		log2ratio_lower = MathUtility.restrict(log2ratio_lower,LOG2RATIO_MIN,LOG2RATIO_MAX);
		log2ratio_upper = MathUtility.restrict(log2ratio_upper,log2ratio_lower,LOG2RATIO_MAX);
		gsa_lower = MathUtility.restrict(gsa_lower,GSA_MIN,GSA_MAX);
		gsa_upper = MathUtility.restrict(gsa_upper,gsa_lower,GSA_MAX);
		
		spa_lower = MathUtility.restrict(spa_lower,SPA_MIN,SPA_MAX);
		spa_upper = MathUtility.restrict(spa_upper,spa_lower,SPA_MAX);
		
		loh_upper = MathUtility.restrict(loh_upper, 0, LOH_MAX);
		min_length = MathUtility.restrict(min_length, 0, MINLENGTH_MAX);
		group_lim = MathUtility.restrict(group_lim, 1, GROUPLIM_MAX);
		// new
		log2ratio_lower_new = MathUtility.restrict(log2ratio_lower_new,LOG2RATIO_MIN_NEW,LOG2RATIO_MAX_NEW);
		log2ratio_upper_new = MathUtility.restrict(log2ratio_upper_new,log2ratio_lower_new,LOG2RATIO_MAX_NEW);
		hmmlog2ratio_lower = MathUtility.restrict(hmmlog2ratio_lower, HMMLOG2RATIO_MIN, HMMLOG2RATIO_MAX);
		hmmlog2ratio_upper = MathUtility.restrict(hmmlog2ratio_upper, hmmlog2ratio_lower, HMMLOG2RATIO_MAX);
		neglog10pvalue_lower = MathUtility.restrict(neglog10pvalue_lower, NEGLOG10PVALUE_MIN, NEGLOG10PVALUE_MAX);
		neglog10pvalue_upper = MathUtility.restrict(neglog10pvalue_upper, neglog10pvalue_lower, NEGLOG10PVALUE_MAX);
		lohprob_upper = MathUtility.restrict(lohprob_upper, 0, LOHPROB_MAX);
		
		consensus_threshold = MathUtility.restrict(consensus_threshold, 1, CONSENSUSTHRESH_MAX);
		pValue_upper = MathUtility.restrict(pValue_upper, 0.0f, PVALUE_MAX);
		
		if( preFilter && postFilter )
		{	// not both are allowed
			preFilter = false;
		}
		
    }

    public Object clone()
    {
        return SystemUtility.serialClone(this);
    }
    
    protected void fireChangeEvent()
    {
    	notifier.fireChangeEvent();
    }
    
	public void addChangeListener( ChangeListener listener )
	{
		notifier.addChangeListener( listener );
	}

	public void removeChangeListener(ChangeListener listener)
	{
		notifier.removeChangeListener( listener );
	}
	
	/**
	 * 
	 * @return Bounds depending on the selected field #field.
	 */
	public float[] getBounds() {
		float[] bounds = new float[2];
		if (versionLoaded == FileVersion.V1_0) {
			switch (fieldA) {
			case LOGRATIO:
				bounds[0] = log2ratio_lower;
				bounds[1] = log2ratio_upper;
				break;
			case LOH:
				bounds[0] = Float.NEGATIVE_INFINITY;
				bounds[1] = loh_upper;
				break;
			case GSA:
				bounds[0] = gsa_lower;
				bounds[1] = gsa_upper;
				break;
			case SPA:
				bounds[0] = spa_lower;
				bounds[1] = spa_upper;
				break;
			default:
				throw new IllegalStateException("illegal field selected");
			}
		} else {
			switch (fieldB) {
			case CNSTATE:
				bounds[0] = 0.0f;
				bounds[1] = 0.0f;
				break;
		
			case LOGRATIO:
				bounds[0] = log2ratio_lower_new;
				bounds[1] = log2ratio_upper_new;
				break;

			case HMMMEDIANLOG2RATIO:
				bounds[0] = hmmlog2ratio_lower;
				bounds[1] = hmmlog2ratio_upper;
				break;

			case NEGLOG10PVALUE:
				bounds[0] = neglog10pvalue_lower;
				bounds[1] = neglog10pvalue_upper;
				break;

			case LOHPROB:
				bounds[0] = 0.0f;
				bounds[1] = lohprob_upper;
				break;

			default:
				throw new IllegalStateException("illegal field selected");
			}
		}

		return bounds;
	}

	
	public void setBounds(float lower, float upper) {
		if (versionLoaded == FileVersion.V1_0) {
			switch (fieldA) {
			case LOGRATIO:
				log2ratio_lower = lower;
				log2ratio_upper = upper;
				break;

			case LOH:
				loh_upper = upper;
				break;

			case GSA:
				gsa_lower = lower;
				gsa_upper = upper;
				break;

			case SPA:
				spa_lower = lower;
				spa_upper = upper;
				break;

			default:
				throw new IllegalStateException("illegal field selected");
			}
		} else {
			switch (fieldB) {
			case CNSTATE:
				break;
				
			case LOGRATIO:
				log2ratio_lower_new = lower;
				log2ratio_upper_new = upper;
				break;

			case HMMMEDIANLOG2RATIO:
				hmmlog2ratio_lower = lower;
				hmmlog2ratio_upper = upper;
				break;

			case NEGLOG10PVALUE:
				neglog10pvalue_lower = lower;
				neglog10pvalue_upper = upper;
				break;

			case LOHPROB:
				lohprob_upper = upper;
				break;

			default:
				throw new IllegalStateException("illegal field selected");
			}
		}
	}

	public FieldA getFieldA()
	{
		return fieldA;
	}
	
	public FieldB getFieldB() {
		return fieldB;
	}

	public void setField(FieldA field )
	{
		this.fieldA = field;
	}
	
	public void setField(FieldB field) {
		this.fieldB = field;
	}

	public String getFieldName()
	{
		if(versionLoaded==FileVersion.V1_0)	return getFieldName(fieldA);
		else return getFieldName(fieldB);
	}

	static public String getFieldName(FieldA field)
	{
		return field.toString();
	}
	
	static public String getFieldName(FieldB field)
	{
		return field.toString();
	}	

	public void assign( AllParameters parameters )
	{		
		fieldA = parameters.fieldA;
		fieldB = parameters.fieldB;
		log2ratio_lower= parameters.log2ratio_lower;
		log2ratio_upper= parameters.log2ratio_upper;
		gsa_lower= parameters.gsa_lower;
		gsa_upper= parameters.gsa_upper;
		spa_lower= parameters.spa_lower;
		spa_upper= parameters.spa_upper;
		loh_upper= parameters.loh_upper;
		min_length= parameters.min_length;

		group_lim= parameters.group_lim;
		consensus_mode= parameters.consensus_mode;
		consensus_threshold= parameters.consensus_threshold;

		method= parameters.method;			// filtering configuration
		preFilter= parameters.preFilter; 
		postFilter= parameters.postFilter;
		
		condensed_mode = parameters.condensed_mode;
		show_profile_lines = parameters.show_profile_lines;
		pValue_upper = parameters.pValue_upper;
		
		log2ratio_lower_new = parameters.log2ratio_lower_new;
		log2ratio_upper_new = parameters.log2ratio_upper_new;
		hmmlog2ratio_lower = parameters.hmmlog2ratio_lower;
		hmmlog2ratio_upper = parameters.hmmlog2ratio_upper;
		neglog10pvalue_lower = parameters.neglog10pvalue_lower;
		neglog10pvalue_upper = parameters.neglog10pvalue_upper;
		lohprob_upper = parameters.lohprob_upper;
		
		doublelossColor = parameters.doublelossColor;
		lossColor = parameters.lossColor;
		gainColor = parameters.gainColor;
		ampColor = parameters.ampColor;		
		
		invertColors = parameters.invertColors;
		this.versionLoaded = parameters.versionLoaded;
		logRatioAmplifier = parameters.logRatioAmplifier;
		normalizationFrame = parameters.normalizationFrame;
		normalizationMethod = parameters.normalizationMethod;
		
			
		fireChangeEvent();
	}

	public void setInvertColors(boolean invertColors) {
		if(this.invertColors!=invertColors) {
			this.invertColors = invertColors;
			Color temp;
			temp = doublelossColor;
			doublelossColor = ampColor;
			ampColor = temp;
			temp = lossColor;
			lossColor = gainColor;
			gainColor = temp;
		}
	}

	public Color getColor(int cn, boolean marker_state) {
		Color c;
		switch (cn) {
		case 0:
			c = doublelossColor;
			break;
		case 1:
			c = lossColor;
			break;
		case 3:
			c = gainColor;
			break;
		case 4:
			c = ampColor;
			break;
		default:
			c = null;
		}
		if (c != null) {
			if (marker_state) {
				c = c.brighter();
			}
		}
		return c;
	}
	
	public void setColor(int i, Color c) {
		switch (i) {
		case 0:	doublelossColor = c;
		break;
		case 1: lossColor = c;
		break;
		case 3: gainColor = c;
		break;
		case 4: ampColor = c;
		break;
		default:
		}
	}
	
	public FileVersion getVersionLoaded() {
		return versionLoaded;
	}

	public void setVersionLoaded(FileVersion versionLoaded) {
		this.versionLoaded = versionLoaded;
	}

	public void restoreDefaultColors() {
    	doublelossColor = COL_DLOSS;
    	lossColor = COL_LOSS;
    	gainColor = COL_GAIN;
    	ampColor = COL_AMP;
	}

	public String getOptions() {
		String s = new String();
		if(getFieldName().equals("LOGRATIO")) {
			if(versionLoaded == FileVersion.V1_1)
				s=s+ "lower: "+log2ratio_lower_new+" upper: "+log2ratio_upper_new;
			else s=s+  "lower: "+log2ratio_lower+" upper: "+log2ratio_upper;
		}
		if(getFieldName().equals("HMMMEDIANLOG2RATIO")) s=s+"lower: "+hmmlog2ratio_lower+" upper: "+hmmlog2ratio_upper;
		if(getFieldName().equals("NEGLOG10PVALUE")) s=s+"lower: "+neglog10pvalue_lower+" upper: "+neglog10pvalue_upper;
		if(getFieldName().equals("LOHPROB")) s=s+"upper: "+lohprob_upper;
		if(getFieldName().equals("LOH")) s=s+"upper: "+loh_upper;		
		if(getFieldName().equals("GSA")) s=s+"lower: "+gsa_lower+" upper: "+gsa_upper;
		if(getFieldName().equals("SPA")) s=s+"lower: "+spa_lower+" upper: "+spa_upper;
		
		s = s + " group limit: "+group_lim+" min length: "+min_length; 
		return s;
	}

}

