/*
 * File:	RResultTransformer.java
 * Created: 29.01.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.input;

import ideogram.AllParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TODO INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RResultTransformer extends AbstractCopyNumberDataModel implements
        ChangeListener {
    
    private RResultReaderModel sourceModel;
    private AllParameters parameters;
    private ArrayList<CopyNumberRecord> copyNumberRecords;
    private boolean stateValid;
    
    /**
     * TODO INSERT DOCUMENTATION HERE!
     *
     * @param sourceModel
     * @param parameters
     */
    public RResultTransformer(RResultReaderModel sourceModel, 
            AllParameters parameters) {
        this.sourceModel = sourceModel;
        this.parameters = parameters;
        this.copyNumberRecords = 
            new ArrayList<CopyNumberRecord>(sourceModel.size());
        this.stateValid = false;
        
        sourceModel.addChangeListener(this);
    }
    
    /*
     * TODO
     *      - The drawAberations() method must then draw this log2 ratio. 
     */

    
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        invalidate();
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#get(int)
     */
    public CopyNumberRecord get(int j) {
        validate();
        return copyNumberRecords.get(j);
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#getChipType()
     */
    public LinkedList<String> getChipType() {
        validate();
        return sourceModel.getChipType();
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#getFileName()
     */
    public LinkedList<String> getFileName() {
        validate();
        return sourceModel.getFileName();
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#getHeader()
     */
    public LinkedList<String> getHeader() {
        // RResult files ain't got no header!
        // validate();
        LinkedList<String> l = new LinkedList<String>();
        l.add("RRESULT FILES HAVE NO HEADER!!!");
        return l;
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#size()
     */
    public int size() {
        validate();
        return copyNumberRecords.size();
    }

    /**
     * Clear the {@link RResultTransformer} and the associated 
     * {@link RResultReaderModel}.
     */
    public void clear() {
        sourceModel.clear(); 
        copyNumberRecords.clear();
        invalidate();
    }
    
    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#toCollection()
     */
    public Collection<CopyNumberRecord> toCollection() {
        validate();
        return copyNumberRecords;
    }

    /**
     * Validate the {@link RResultTransformer}'s state.
     *
     */
    private void validate() {
        if (!stateValid) {
            updateCopyNumbers();
            stateValid = true;
        }
    }
    
    /**
     * Invalidate the {@link RResultTransformer}'s state.
     *
     */
    private void invalidate() {
        if (stateValid) {
            stateValid = false;
            fireChangeEvent();
        }
    }
    
    private void updateCopyNumbers() {
        CopyNumberRecord cnRec;
        
        copyNumberRecords.clear();
        copyNumberRecords.ensureCapacity(sourceModel.size());
        for (RResultReaderModel.RResultRecord resRec: sourceModel.toCollection()) {
            cnRec = new CopyNumberRecord();
            
            cnRec.locus = resRec.getLocus();
            cnRec.setLogRatio(resRec.getLogRatio());
            
            /*
             * TODO: 
             *  - Set copy number.
             *  - Set info.
             *  - Set confidence.
             *  - Set id.
             *  - Set ref.
             */
            copyNumberRecords.add(cnRec);
        }
        
        // Don't waste more memory than necessary.
        copyNumberRecords.trimToSize();
    }
    
}
