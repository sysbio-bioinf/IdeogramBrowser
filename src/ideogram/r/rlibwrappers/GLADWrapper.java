/*
 * File:	GLADWrapper.java
 * 
 * Created: 	08.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.FileTypeRecord;
import ideogram.r.RController;
import ideogram.r.RDataSetWrapper;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.annotations.Analysis;
import ideogram.r.annotations.RBoolParam;
import ideogram.r.annotations.RDsNameParam;
import ideogram.r.annotations.RNumericParam;
import ideogram.r.annotations.RStringParam;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.DefaultMessageDisplayModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.rosuda.JRI.REXP;

/**
 * Wrapper class around the GLAD R library. The functions glad() and daglad()
 * can be used.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class GLADWrapper extends AbstractAnalysisWrapper {

    private static final String LIB_NAME = "GLAD";
    // private static final String PROFILE_CGH_VARNAME = "profileCGH";
    private static final String ALGO_RES_VARNAME = "res";

    private static final String RES_WRITER = "writeProfileCGH <- function(profileCGH, path, fileName) {\n"
	    + "cat('Writing...', path, fileName, '\\n')\n"
	    + "colNames <- c('Chromosome', 'PosBase', 'LogRatio')\n"
	    + "absPath <- paste(path, fileName, sep='/')\n"
	    + "# Open a connection for writing\n"
	    + "con <- file(absPath, open='w', encoding='UTF8')\n"
	    + "# Write the column names\n"
	    + "cat(colNames, file=con, sep='\t')\n"
	    + "cat('\\n', file=con, sep='')\n"
	    + "# Write the contents of the profileCGH$profileValues\n"
	    + "prfVals <- profileCGH$profileValues\n"
	    + "for (i in 1:nrow(prfVals)) {\n"
	    + "cat(as.numeric(prfVals$Chromosome[i]),\n"
	    + "as.integer(prfVals$PosBase[i]),\n"
	    + "as.numeric(prfVals$LogRatio[i]),\n"
	    + "file=con, sep='\\t')\n"
	    + "cat('\\n', file=con, sep='')\n" + "}\n" + "close(con)\n" + "}\n";

    private List<RDataSetWrapper> sampleData;
    private boolean writerFunctionLoaded;
    private boolean libraryLoaded;

    // Parameters for glad function.

    @Analysis(name = "glad")
    @RDsNameParam(name = "Data set", mandatory = true)
    public String gladDataSet;

    @Analysis(name = "glad")
    @RBoolParam(name = "mediancenter", mandatory = false)
    public String gladMediancenter;

    @Analysis(name = "glad")
    @RBoolParam(name = "base", mandatory = false)
    public String gladBase;

    @Analysis(name = "glad")
    @RBoolParam(name = "verbose", mandatory = false)
    public String gladVerbose;

    @Analysis(name = "glad")
    @RStringParam(name = "smoothfunc", mandatory = false)
    public String gladSmoothfunc;

    @Analysis(name = "glad")
    @RStringParam(name = "model", mandatory = false)
    public String gladModel;

    @Analysis(name = "glad")
    @RStringParam(name = "lkern", mandatory = false)
    public String gladLkern;

    @Analysis(name = "glad")
    @RStringParam(name = "type", mandatory = false)
    public String gladType;

    @Analysis(name = "glad")
    @RStringParam(name = "method", mandatory = false)
    public String gladMethod;

    @Analysis(name = "glad")
    @RNumericParam(name = "bandwidth", mandatory = false)
    public String gladBandwidth;

    @Analysis(name = "glad")
    @RNumericParam(name = "round", mandatory = false)
    public String gladRound;

    @Analysis(name = "glad")
    @RNumericParam(name = "qlambda", mandatory = false)
    public String gladQlambda;

    @Analysis(name = "glad")
    @RNumericParam(name = "lambdabreak", mandatory = false)
    public String gladLambdabreak;

    @Analysis(name = "glad")
    @RNumericParam(name = "lambdacluster", mandatory = false)
    public String gladLambdacluster;

    @Analysis(name = "glad")
    @RNumericParam(name = "lambdaClusterGen", mandatory = false)
    public String gladLambdaClusterGen;

    @Analysis(name = "glad")
    @RNumericParam(name = "param", mandatory = false)
    public String gladParam;

    @Analysis(name = "glad")
    @RNumericParam(name = "alpha", mandatory = false)
    public String gladAlpha;

    @Analysis(name = "glad")
    @RNumericParam(name = "msize", mandatory = false)
    public String gladMsize;

    @Analysis(name = "glad")
    @RNumericParam(name = "nmax", mandatory = false)
    public String gladNmax;

    @Analysis(name = "daglad")
    @RDsNameParam(name = "Data set", mandatory = true)
    public String dagladDataSet;

    @Analysis(name = "daglad")
    @RBoolParam(name = "mediancenter", mandatory = false)
    public String dagladMediancenter;

    @Analysis(name = "daglad")
    @RBoolParam(name = "normalrefcenter", mandatory = false)
    public String dagladNormalrefcenter;

    @Analysis(name = "daglad")
    @RBoolParam(name = "genomestep", mandatory = false)
    public String dagladGenomestep;

    @Analysis(name = "daglad")
    @RStringParam(name = "smoothfunc", mandatory = false)
    public String dagladSmoothfunc;

    @Analysis(name = "daglad")
    @RStringParam(name = "lkern", mandatory = false)
    public String dagladLkern;

    @Analysis(name = "daglad")
    @RStringParam(name = "model", mandatory = false)
    public String dagladModel;

    @Analysis(name = "daglad")
    @RNumericParam(name = "qlambda", mandatory = false)
    public String dagladQlambda;

    @Analysis(name = "daglad")
    @RNumericParam(name = "bandwidth", mandatory = false)
    public String dagladBandwidth;

    @Analysis(name = "daglad")
    @RBoolParam(name = "base", mandatory = false)
    public String dagladBase;

    @Analysis(name = "daglad")
    @RNumericParam(name = "round", mandatory = false)
    public String dagladRound;

    @Analysis(name = "daglad")
    @RNumericParam(name = "lambdabreak", mandatory = false)
    public String dagladLambdabreak;

    @Analysis(name = "daglad")
    @RNumericParam(name = "lambdaclusterGen", mandatory = false)
    public String dagladLambdaclusterGen;

    @Analysis(name = "daglad")
    @RNumericParam(name = "param", mandatory = false)
    public String dagladParam;

    @Analysis(name = "daglad")
    @RNumericParam(name = "alpha", mandatory = false)
    public String dagladAlpha;

    @Analysis(name = "daglad")
    @RNumericParam(name = "msize", mandatory = false)
    public String dagladMsize;

    @Analysis(name = "daglad")
    @RStringParam(name = "method", mandatory = false)
    public String dagladMethod;

    @Analysis(name = "daglad")
    @RNumericParam(name = "nmin", mandatory = false)
    public String dagladNmin;

    @Analysis(name = "daglad")
    @RNumericParam(name = "nmax", mandatory = false)
    public String dagladNmax;

    @Analysis(name = "daglad")
    @RNumericParam(name = "amplicon", mandatory = false)
    public String dagladAmplicon;

    @Analysis(name = "daglad")
    @RNumericParam(name = "deletion", mandatory = false)
    public String dagladDeletion;

    @Analysis(name = "daglad")
    @RNumericParam(name = "deltaN", mandatory = false)
    public String dagladDeltaN;

    @Analysis(name = "daglad")
    @RNumericParam(name = "forceGL", mandatory = false)
    public String dagladForceGL;

    @Analysis(name = "daglad")
    @RNumericParam(name = "nbsigma", mandatory = false)
    public String dagladNbsigma;

    @Analysis(name = "daglad")
    @RNumericParam(name = "MinBkbWeight", mandatory = false)
    public String dagladMinBkpWeight;

    @Analysis(name = "daglad")
    @RBoolParam(name = "CheckBkpPos", mandatory = false)
    public String dagladCheckBkpPos;

    public GLADWrapper() throws RException {
	sampleData = new ArrayList<RDataSetWrapper>();
	writerFunctionLoaded = false;
	libraryLoaded = false;

	// set default values for glad function. If null, no default value
	// available
	gladDataSet = null;
	gladMediancenter = "FALSE";
	gladSmoothfunc = "'lawsglad'";
	gladBandwidth = "10";
	gladRound = "1.5";
	gladModel = "'Gaussian'";
	gladLkern = "'Exponential'";
	gladQlambda = "0.999";
	gladBase = "FALSE";
	gladLambdabreak = "8";
	gladLambdacluster = "8";
	gladLambdaClusterGen = "40";
	gladType = "'tricubic'";
	gladParam = "6";
	gladAlpha = "0.001";
	gladMsize = "5";
	gladMethod = "'centroid'";
	gladNmax = "8";
	gladVerbose = "FALSE";

	dagladDataSet = null;
	dagladMediancenter = "FALSE";
	dagladNormalrefcenter = "FALSE";
	dagladGenomestep = "FALSE";
	dagladSmoothfunc = "'lawsglad'";
	dagladLkern = "'Exponential'";
	dagladModel = "'Gaussian'";
	dagladQlambda = "0.999";
	dagladBandwidth = "10";
	dagladBase = "FALSE";
	dagladRound = "1.5";
	dagladLambdabreak = "8";
	dagladLambdaclusterGen = "40";
	dagladParam = "d = 6";
	dagladAlpha = "0.001";
	dagladMsize = "5";
	dagladMethod = "'centroid'";
	dagladNmin = "1";
	dagladNmax = "8";
	dagladAmplicon = "1";
	dagladDeletion = "-5";
	dagladDeltaN = "0.1";
	dagladForceGL = "-0.15, 0.15";
	dagladNbsigma = "3";
	dagladMinBkpWeight = "0.35";
	dagladCheckBkpPos = "TRUE";

	// Add the names of the available sample data.
	RDataSetWrapper dw;
	dw = new RDataSetWrapper("veltman", "Public CGH data of Veltman");
	dw.addElement("P20");
	dw.addElement("P9");
	sampleData.add(dw);

	dw = new RDataSetWrapper("arrayCGH", "Bladder cancer CGH data");
	dw.addElement("array1");
	dw.addElement("array2");
	dw.addElement("array3");
	sampleData.add(dw);

	dw = new RDataSetWrapper("cytoband", "Cytogenetic banding");
	dw.addElement("cytoband");
	sampleData.add(dw);

	dw = new RDataSetWrapper("snijders", "Public CGH data of Snijders");
	dw.addElement("gm00143");
	dw.addElement("gm01524");
	dw.addElement("gm01535");
	dw.addElement("gm01750");
	dw.addElement("gm02948");
	dw.addElement("gm03134");
	dw.addElement("gm03563");
	dw.addElement("gm03576");
	dw.addElement("gm04435");
	dw.addElement("gm05296");
	dw.addElement("gm07081");
	dw.addElement("gm07408");
	dw.addElement("gm10315");
	dw.addElement("gm13031");
	dw.addElement("gm13330");
	sampleData.add(dw);

	if (!RController.getInstance().engineRunning()) {
	    throw new RException("R not running!");
	}

	File f = new File(RController.R_STORAGE_PATH);
	if (!f.exists()) {
	    if (!f.mkdirs()) {
		throw new RException("Failed to create R storage directory "
			+ RController.R_STORAGE_PATH + "!");
	    }
	}
    }

    /* Methods required by ideogram.r.RLibrary and ideogram.r.AbstractRWrapper */

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.AbstractRWrapper#hasSampleData()
     */
    public boolean hasSampleData() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.AbstractRWrapper#listSampleData()
     */
    public List<RDataSetWrapper> listSampleData() {
	return sampleData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.AbstractRWrapper#load()
     */
    public void loadLibrary() throws RException {
	if (!libraryLoaded) {
	    RController.getInstance().unloadPreviousWrapper();
	    RController.getInstance().loadRLibrary(LIB_NAME);
	    libraryLoaded = true;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.AbstractRWrapper#unload()
     */
    public void unloadLibrary() throws RException {
	if (libraryLoaded) {
	    RController.getInstance().unloadRLibrary(LIB_NAME);
	    libraryLoaded = false;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.RLibrary#loadSampleData(ideogram.r.RDataSetWrapper)
     */
    public void loadSampleData(RDataSetWrapper data) throws RException {
	RController.getInstance().loadDataSet(data.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.AbstractRWrapper#getResult()
     */
    @Override
    public REXP getResult() throws RException {
	System.out.println("Getting result");
	RController rc = RController.getInstance();
	REXP res = null;
	// res = rc.getEngine().eval(ALGO_RES_VARNAME);
	rc.getEngine().eval("print(" + ALGO_RES_VARNAME + ")");
	return res;
    }

    /**
     * Tell R to evaluate the specified funcall, and write its results to a
     * RResult file if the operation was successfull. Return the RResult file's
     * name upon success, or null else.
     * 
     * @param funcall
     *                The R function to execute.
     * @return The RResult file's name on success, else <code>null</code>.
     * @throws RException
     */
    private String evalAndWriteFuncall(String funcall) throws RException {
	RController rc = RController.getInstance();
	REXP rexp = rc.getEngine().eval(funcall);
	
	if (rexp != null) {
	    loadWriterFunction();
	    String resFile = RController.createUniqueRResultFileName();
	    writeResult(RController.R_STORAGE_PATH, resFile);
	    return resFile;
	} else {
	    DefaultMessageDisplayModel.getInstance().displayMessage(
		    "Function glad() returned with null! "
			    + "Is the data set's name correct?");
	    return null;
	}
    }
    
    /**
     * Call the writeProfileCGH() function (in R) to store the result of the the
     * last analysis.
     * 
     * @param path
     * @param fileName
     * @param dependingOn
     * @throws RException
     */
    private void writeResult(String path, String fileName) throws RException {
	String funcall = "writeProfileCGH(" + ALGO_RES_VARNAME + ", '" + path
		+ "', '" + fileName + "')";
	RController.getInstance().getEngine().eval(funcall);

	RController.getInstance().addRResultFile(
		RController.R_STORAGE_PATH + File.separator + fileName);
    }

    /* Other methods, which will be discovered via reflection */

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.AbstractRWrapper#getAcceptedFileTypes()
     */
    public List<FileTypeRecord> getAcceptedFileTypes() {
	ArrayList<FileTypeRecord> ret = new ArrayList<FileTypeRecord>(1);
	// ret.add(new FileTypeRecord(FileTypeRegistry.NONE, false));

	// JUST FOR TESTING PURPOSES:
	ret.add(new FileTypeRecord(FileTypeRegistry.CEL, true));
	ret.add(new FileTypeRecord(FileTypeRegistry.CDF, false));
	return ret;
    }

    /**
     * Call the glad function in the R package GLAD. This method should not be
     * called directly but by reflection.
     * 
     * @return
     * @throws RException
     */
    @Analysis(name = "glad")
    public String useGladFunction() throws RException {
	RController rc = RController.getInstance();

	if (!rc.engineRunning()) {
	    throw new RException("R not running!");
	}
	if (gladDataSet == null || gladDataSet.equals("")) {
	    throw new RException("No data set specified!");
	}

	// createProfileCGH(dsName);
	String wrappedParam = "c(d = " + gladParam + ")";
	String funcall = ALGO_RES_VARNAME + " <- glad(as.profileCGH("
		+ gladDataSet + ")" + ", mediancenter = " + gladMediancenter
		+ ", smoothfunc = " + gladSmoothfunc + ", bandwidth = "
		+ gladBandwidth + ", round = " + gladRound + ", model = "
		+ gladModel + ", lkern = " + gladLkern + ", qlambda = "
		+ gladQlambda + ", base = " + gladBase + ", lambdabreak = "
		+ gladLambdabreak + ", lambdacluster = " + gladLambdacluster
		+ ", lambdaClusterGen = " + gladLambdaClusterGen + ", type = "
		+ gladType + ", param = " + wrappedParam + ", alpha = "
		+ gladAlpha + ", msize = " + gladMsize + ", method = "
		+ gladMethod + ", nmax = " + gladNmax + ", verbose = "
		+ gladVerbose + ")";

	return evalAndWriteFuncall(funcall);
    }

    @Analysis(name = "daglad")
    public String useDagladFunction() throws RException {
	RController rc = RController.getInstance();

	if (!rc.engineRunning()) {
	    throw new RException("R not running!");
	}
	if (dagladDataSet == null || dagladDataSet.equals("")) {
	    throw new RException("No data set specified!");
	}
	String funcall = ALGO_RES_VARNAME + " <- daglad(as.profileCGH("
		+ dagladDataSet + ")" + ", mediancenter = "
		+ dagladMediancenter + ", normalrefcenter = "
		+ dagladNormalrefcenter + ", genomestep = " + dagladGenomestep
		+ ", smoothfunc = " + dagladSmoothfunc + ", lkern = "
		+ dagladLkern + ", model = " + dagladModel + ", qlambda = "
		+ dagladQlambda + ", bandwidth = " + dagladBandwidth
		+ ", base = " + dagladBase + ", round = " + dagladRound
		+ ", lambdabreak = " + dagladLambdabreak
		+ ", lambdaclusterGen = " + dagladLambdaclusterGen
		+ ", param = c(" + dagladParam + ")" + ", alpha = "
		+ dagladAlpha + ", msize = " + dagladMsize + ", method = "
		+ dagladMethod + ", nmin = " + dagladNmin + ", nmax = "
		+ dagladNmax + ", amplicon = " + dagladAmplicon
		+ ", deletion = " + dagladDeletion + ", deltaN = "
		+ dagladDeltaN + ", forceGL = c(" + dagladForceGL + ")"
		+ ", nbsigma = " + dagladNbsigma + ", MinBkpWeight = "
		+ dagladMinBkpWeight + ", CheckBkpPos = " + dagladCheckBkpPos
		+ ")";

	return evalAndWriteFuncall(funcall);
    }

    /**
     * Load the writer function if necessary.
     * 
     * @throws RException
     * 
     */
    private void loadWriterFunction() throws RException {
	if (!writerFunctionLoaded) {
	    RController rc = RController.getInstance();
	    rc.getEngine().eval(RES_WRITER);
	}
    }
}
