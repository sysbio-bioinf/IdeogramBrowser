package ideogram.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ideogram.IColorMapper;
import ideogram.IdeogramMainWindow;
import ideogram.Marker;
import ideogram.MarkerCollection;
import ideogram.db.Band;
import ideogram.tree.Interval;

import javax.swing.event.ChangeListener;

import util.ChangeNotifier;

/**
 * @author mueller
 *
 */
public abstract class AbstractCopyNumberDataModel implements ICopyNumberModel
{

    private MarkerCollection[] mc;
    private Band band;
    private Marker m;

    private ChangeNotifier changeNotifier;
    /**
     * 
     */
    public AbstractCopyNumberDataModel()
    {
        mc = new MarkerCollection[IdeogramMainWindow.NUM_OF_CHROMOSOMES];
        changeNotifier = new ChangeNotifier();
    }

    public void addChangeListener(ChangeListener listener) 
    {
        changeNotifier.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeNotifier.removeChangeListener(listener);
    }

    protected void fireChangeEvent()
    {
        changeNotifier.fireChangeEvent();
    }

    /**
     * Converts a copy number model into a MarkerCollection.
     * TODO: This intermediate step should be removed in future versions.
     * 
     * @param colorMapper
     * @return A marker collection.
     */
    public MarkerCollection[] convertToMarkerCollection( IColorMapper colorMapper )
    {
        band = new Band();

        for(int i = 0; i < mc.length; ++i)
        {
            mc[i] = new MarkerCollection();
            mc[i].setName( getFileName());
        }

        for(int i=0; i<size(); ++i)
        {
            band.chromosome = (byte) getChromosomeIndex(i);
            m = new Marker(getInterval(i),getCopyNumber(i));
            m.color = colorMapper.map( getConfidence(i) );

            // TODO Why that??
            // StringBuffer buf = new StringBuffer();
            // buf.append( getMarkerInfo(i) );
            m.setInfo(getMarkerInfo(i) /*buf.toString()*/);
            m.setLog2Ratio(getLogRatio(i));

            mc[band.chromosome-1].add(m);
        }

        return mc;
    }

    public String getMarkerName(int idx)
    {
        return get(idx).info;
    }

    public byte getChromosome(int idx)
    {
        return get(idx).locus.chromosome;
    }

    public Interval getInterval(int idx)
    {
        return get(idx).locus.interval;
    }

    public int getCopyNumber(int idx)
    {
        return get(idx).copy_number;
    }

    public double getConfidence(int idx)
    {
        return get(idx).confidence;
    }

    public byte getChromosomeIndex(int idx)
    {
        return get(idx).locus.chromosome;
    }

    public String getMarkerInfo(int idx)
    {
        return get(idx).info;
    }

    /* (non-Javadoc)
     * @see ideogram.input.ICopyNumberModel#getLog2Ratio(int)
     */
    public double getLogRatio(int idx) {
        return get(idx).getLogRatio();
    }

    public void detach()
    {
        // nothing
    }

    public Iterator<CopyNumberRecord> iterator()
    {
        return toCollection().iterator();
    }


    /**
     * Apply the specified normalization method to the log ratios in the given
     * list of copy number records.
     *
     * @param copyNumberRecords
     * @param normalizationFrame
     * @param normalizationMethod
     */
    protected void applyNormalization(List<CopyNumberRecord> copyNumberRecords,
            int normalizationFrame, NormalizationMethods normalizationMethod) {
        System.out.println("Applying normalization");
        // No normalization when frame is 0
        if (normalizationFrame == 0) { return; }

        double normVal;
        int from, to;
        List<CopyNumberRecord> sl;

        from = 0;
        while (from < copyNumberRecords.size()) {
            to = from + normalizationFrame;
            to = to < copyNumberRecords.size() ? to : copyNumberRecords.size();
            sl = copyNumberRecords.subList(from, to);
            switch (normalizationMethod) {
                case MEAN:
                    normVal = calcMean(sl, normalizationFrame);
                    break;
                case MEDIAN:
                    normVal = calcMedian(sl, normalizationFrame);
                    break;
                default:
                    // Use the mean as default. Should never happen!
                    System.out.println("No legal normalization mehtod set!" +
                    " Using mean.");
                normVal = calcMean(sl, normalizationFrame);
                break;  
            }
            for (CopyNumberRecord cRec: sl) {
                cRec.setLogRatio(normVal);
            }
            from = to;
        }
    }

    /**
     * Calculate the median of all logRatios in the given list.
     *
     * @param list
     * @return
     */
    private double calcMedian(List<CopyNumberRecord> list, int normalizationFrame) {
        double[] logRatios = new double[normalizationFrame];

        int i = 0;
        for (CopyNumberRecord cnRec: list) {
            logRatios[i] = cnRec.getLogRatio();
            i++;
        }
        Arrays.sort(logRatios);
        if (logRatios.length % 2 == 0) { 
            return logRatios[logRatios.length / 2]; 
        }
        else {
            double a = logRatios[logRatios.length / 2];
            double b = logRatios[logRatios.length / 2 + 1];
            return (a + b) / 2;
        }
    }

    /**
     * Calculate the mean of all log ratios in the given list of copy number 
     * records.
     *
     * @param list
     * @param normalizationFrame
     * @return
     */
    private double calcMean(List<CopyNumberRecord> list, int normalizationFrame) {
        double sum = 0;
        for (CopyNumberRecord cRec: list) {
            sum += cRec.getLogRatio();
        }
        return sum / normalizationFrame;
    }


    /**
     * Lists all normalization methods, the {@link RResultTransformer} is able
     * to apply.
     *
     * @author Ferdinand Hofherr
     *
     */
    public enum NormalizationMethods {
        MEDIAN("Median"),
        MEAN("Mean");

        private final String methodName;

        private NormalizationMethods(String methodName) {
            this.methodName = methodName;
        }

        public String methodName() { return methodName; }

        @Override
        public String toString() { return methodName; }
    }
}
