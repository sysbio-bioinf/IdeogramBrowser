/*
 * File:	RResultReaderModel.java
 * Created: 29.01.2008
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.input;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import ideogram.tree.Interval;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import util.FileFormatException;

/**
 * Reads an *.RResult file. An *.RResult file contains the results of an 
 * analysis conducted in R. The first line of the *.RResult file contains the
 * following column names (in the given order):
 * <ul>
 *  <li>Chromosome - The chromosome name (23 == X and 24 == Y)</li>
 *  <li>PosBase - Position on the chromosome.</li>
 *  <li>LogRatio - Log2Ratio at position PosBase</li>
 * </ul> 
 *
 * @author Ferdinand Hofherr
 *
 */
public class RResultReaderModel extends AbstractIdeogramDataModel {

    private static final int ASSUMED_NO_RECORDS = 1000;
    private static final int INTERVAL_RANGE = 25;

    private ArrayList<RResultRecord> data; // TODO: A linked list might use less memory!
    private File file;
    
    public enum RResultColumnNames {
        CHROMOSOME, POS_BASE, LOG_RATIO
    }

    public RResultReaderModel() {
        data = new ArrayList<RResultRecord>(ASSUMED_NO_RECORDS);
        file = null;
    }

    /* (non-Javadoc)
     * @see ideogram.input.AbstractIdeogramDataModel#getFileName()
     */
    @Override
    public LinkedList<String> getFileName() {
        LinkedList<String> l = new LinkedList<String>();
        if (file != null) { l.add(file.getAbsolutePath()); }
        return l;
    }

    /**
     * Load the results calculated in R from the specified file. The filename 
     * should end with *.RResult.
     *
     * @param rResultFile
     * @throws IOException 
     * @throws FileFormatException 
     */
    public void loadFromFile(File rResultFile) 
    throws IOException, FileFormatException {
        RResultRecord rres;
        String s;
        String[] colVals;
        LineNumberReader lr = new LineNumberReader(new FileReader(rResultFile));

        file = rResultFile;
        // Assure the ArrayList containing the read data is empty!
        clear();
        // Read the first line containing the column names:
        s = lr.readLine();
        if (s.trim().split("\t").length != 
            RResultColumnNames.values().length) {
            throw new FileFormatException("Invalid number of columns in" +
                    " RResult file: " + rResultFile.getAbsolutePath());
        }

        while (lr.ready()) {
            s = lr.readLine();
            colVals = s.trim().split("\t");
            rres = new RResultRecord();
            for (RResultColumnNames col: RResultColumnNames.values()) {
                s = colVals[col.ordinal()].trim();
                switch (col) {
                    case CHROMOSOME:
                        rres.setChromosome(parseInt(s));
                        break;
                    case POS_BASE:
                        rres.setPosBase(parseLong(s));
                        break;
                    case LOG_RATIO:
                        rres.setLogRatio(parseDouble(s));
                }
            }
            data.add(rres);
        }

        // Don't waste more memory than necessary.
        data.trimToSize();
        fireChangeEvent();
    }

    /**
     * Empty the {@link RResultReaderModel}. 
     *
     */
    public void clear() {
        data.clear();
    }
    
    /**
     * Get all read {@link RResultRecord}s as a {@link Collection}.
     *
     * @return
     */
    protected Collection<RResultRecord> toCollection() {
        return data;
    }
    
    /**
     * Get a list of the used chip types.
     * TODO <strong>At the moment the chip type is not stored in the RResult
     * file! If it is necessary, this should be introduced as soon as possible!</strong>
     *
     * @return
     */
    public LinkedList<String> getChipType() {
        LinkedList<String> ret = new LinkedList<String>();
        //for (RResultRecord rec: data) {
            /*
             * TODO As no chip type gets stored at the moment, simply add the
             * string UNKNOWN to the list.
             */
        //    ret.add("UNKNOWN");
        //}
        ret.add("UNKNOWN");
        return ret;
    }

    /**
     * Get the current size of the {@link RResultReaderModel}.
     *
     * @return
     */
    public int size() {
        return data.size();
    }
    
    protected class RResultRecord {
        private int chromosome;
        private long posBase;
        private double logRatio;

        public RResultRecord() {}

        /**
         * Get the chromosome number. X == 23, and Y == 24.
         * 
         * @return the chromosome
         */
        public int getChromosome() {
            return chromosome;
        }

        /**
         * Set the chromosome number. X == 23, and Y == 24.
         *
         * @param chromosome the chromosome to set
         */
        private void setChromosome(int chromosome) {
            this.chromosome = chromosome;
        }

        /**
         * Get the position on the chromosome.
         *
         * @return the posBase
         */
        public long getPosBase() {
            return posBase;
        }

        /**
         * Set the position on the chromosome.
         *
         * @param posBase the posBase to set
         */
        private void setPosBase(long posBase) {
            this.posBase = posBase;
        }

        /**
         * Get the log ratio.
         *
         * @return the logRatio
         */
        public double getLogRatio() {
            return logRatio;
        }

        /**
         * Set the log ratio.
         *
         * @param logRatio the logRatio to set
         */
        private void setLogRatio(double logRatio) {
            this.logRatio = logRatio;
        }
        
        /**
         * Get the chromosomal locus of this {@link RResultRecord}. 
         *
         * @return
         */
        public Locus getLocus() {
            Locus l = new Locus();
            l.chromosome = (byte)getChromosome();
            
            l.interval = new Interval(posBase - INTERVAL_RANGE / 2,
                                      posBase + INTERVAL_RANGE / 2);
            return l;
        }
    }
}