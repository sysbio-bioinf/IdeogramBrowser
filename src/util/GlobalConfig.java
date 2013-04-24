package util;

import java.io.File;

/**
 * This class holds global configuration values. These can be changed at
 * execution time. All methods are thread safe.
 * 
 * @author behrens
 * 
 */
public class GlobalConfig {

	private static GlobalConfig instance = null;

	protected GlobalConfig() {
		// Exists only to defeat instantiation.
	}

	public static GlobalConfig getInstance() {
		if (instance == null) {
			instance = new GlobalConfig();
		}
		return instance;
	}

	byte chromosomeCount;
	byte autosomeCount;
	byte xChromosomeNr;
	byte yChromosomeNr;
	String geneReferenceString;
	File dataDir;
	//CHROMO make input
	private String speciesString;


	public void setSpeciesString(String speciesString) {
		this.speciesString = speciesString;
	}

	public String getGeneReferenceString() {
		synchronized (this) {
			return geneReferenceString;
		}
	}

	public void setGeneReferenceString(String geneReferenceString) {
		synchronized (this) {
			this.geneReferenceString = geneReferenceString;
		}
	}

	public byte getChromosomeCount() {
		synchronized (this) {
			return chromosomeCount;
		}
	}

	/**
	 * will set the total chromosome cound as well as the autosome count and the
	 * index for X and Y Chromosome
	 * 
	 * @param chromosomeCount
	 */
	public void setChromosomeCount(byte chromosomeCount) {

		synchronized (this) {
			this.chromosomeCount = chromosomeCount;
			this.autosomeCount = (byte) (chromosomeCount - 2);
			this.xChromosomeNr = (byte) (chromosomeCount - 1);
			this.yChromosomeNr = chromosomeCount;

		}

	}

	/**
	 * retrieve the number of autosomes. equivalent to getChromosomeCount()-2
	 * 
	 * @return
	 */
	public byte getAutosomeCount() {
		synchronized (this) {
			return autosomeCount;
		}
	}

	/**
	 * retrieve the index for the X chromosome. equivalent to
	 * getChromosomeCount()-1
	 * 
	 * @return
	 */
	public byte getXChromosomeNr() {
		synchronized (this) {
			return xChromosomeNr;
		}
	}

	/**
	 * retrieve the index for the X chromosome. equivalent to
	 * getChromosomeCount()
	 * 
	 * @return
	 */

	public byte getYChromosomeNr() {
		synchronized (this) {
			return yChromosomeNr;
		}
	}

	public String getSpeciesString() {
		return speciesString;
	}

}
