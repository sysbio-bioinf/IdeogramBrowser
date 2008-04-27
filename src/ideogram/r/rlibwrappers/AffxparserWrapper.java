/*
 * File: 	AffxparserWrapper.java 
 * 
 * Created:	23.02.2008 
 * 
 * Author: 	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import static ideogram.r.FileTypeRecord.FileTypeRegistry.CDF;
import static ideogram.r.FileTypeRecord.FileTypeRegistry.CEL;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.rosuda.JRI.REXP;
import ideogram.r.FileTypeRecord;
import ideogram.r.RController;
import ideogram.r.RVariableNameRegistry;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.exceptions.RException;

/**
 * Wrapper around the affxparser library available at bioconductor.org. This
 * wrapper is used to read CEL and CDF files. Depending on the file size such
 * reading operations can be very time consuming.
 * 
 * @author Ferdinand Hofherr
 */
public class AffxparserWrapper implements RFileParser {

    private static volatile boolean libraryLoaded = false;
    private static List<FileTypeRecord> acceptedFileTypes = null;

    static {
	acceptedFileTypes = new ArrayList<FileTypeRecord>(2);
	acceptedFileTypes.add(new FileTypeRecord(FileTypeRegistry.CDF, true));
	acceptedFileTypes.add(new FileTypeRecord(FileTypeRegistry.CEL, true));
    }

    private List<String> celFiles = Collections
	    .synchronizedList(new ArrayList<String>());
    private List<String> cdfFiles = Collections
	    .synchronizedList(new ArrayList<String>());
    private List<String> variableNames = Collections
	    .synchronizedList(new ArrayList<String>());

    public AffxparserWrapper() {
    }

    /*
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#getAcceptedFileTypes()
     */
    public List<FileTypeRecord> getAcceptedFileTypes() {
	return acceptedFileTypes;
    }

    /*
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#loadLibrary()
     */
    public synchronized void loadLibrary() throws RException {
	if (!libraryLoaded) {
	    RController.getInstance().loadRLibrary("affxparser");
	    libraryLoaded = true;
	}
    }

    /*
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#unloadLibrary()
     */
    public synchronized void unloadLibrary() throws RException {
	if (libraryLoaded) {
	    RController.getInstance().unloadRLibrary("affxparser");
	    libraryLoaded = false;
	}
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#addFileName(ideogram.r.FileTypeRecord,
     *      java.lang.String)
     */
    public synchronized void addFileName(FileTypeRecord fileType,
	    String fileName) {
	List<String> list = null;

	switch (fileType.getFileType()) {
	case CEL:
	    list = celFiles;
	    break;
	case CDF:
	    list = cdfFiles;
	    break;
	default: // Ignore file, if file type is not supported.
	    return;
	}

	if (!fileType.areMultipleAccepted() && list.size() > 0) {
	    list.set(0, fileName);
	} else {
	    list.add(fileName);
	}
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames()
     */
    public synchronized void clearListOfFileNames() {
	clearListOfFileNames(CDF);
	clearListOfFileNames(CEL);
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames(ideogram.r.FileTypeRecord)
     */
    public void clearListOfFileNames(FileTypeRecord fileType) {
	clearListOfFileNames(fileType.getFileType());
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames(ideogram.r.FileTypeRecord.FileTypeRegistry)
     */
    public void clearListOfFileNames(FileTypeRegistry fileType) {
	switch (fileType) {
	case CDF:
	    cdfFiles.clear();
	    break;
	case CEL:
	    celFiles.clear();
	    break;
	default: // Ignore call, if file type is not supported.
	    return;
	}
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#getVariableNames()
     */
    public List<String> getVariableNames() {
	return variableNames;
    }

    /*
     * @see ideogram.r.rlibwrappers.RFileParser#loadFiles()
     */
    public void loadFiles() {
	for (String s : celFiles) {
	    readCELFile(s);
	}

	System.out.println("Number of CDF files: " + cdfFiles.size());
	for (String s : cdfFiles) {
	    readCDFFile(s);
	}

    }

    private void readCDFFile(String s) {
	String varName = RVariableNameRegistry.createFileContentName(s);
	String funcall = varName + " <- readCdfUnits('" + s + "', "
		+ "units=NULL, readXY=TRUE, readBases=TRUE, "
		+ "readExpos=TRUE, readType=TRUE, "
		+ "readDirection=TRUE, stratifyBy='nothing', "
		+ "readIndices=FALSE, verbose=0)";
	
	System.out.println("Funcall: " + funcall);
	try {
	    RController.getInstance().toRwriteln("START");
	    RController.getInstance().getEngine().eval(funcall);
	    RController.getInstance().toRwriteln("DONE");
	} catch (RException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void readCELFile(String s) {
	String varName = RVariableNameRegistry.createFileContentName(s);
	String funcall = varName + " <- readCel('" + s + "', "
		+ "indices=NULL, " + "readHeader=TRUE, "
		+ "readXY=TRUE, readIntensities=TRUE, "
		+ "readStdvs = FALSE, readPixels = FALSE, "
		+ "readOutliers = TRUE, readMasked = TRUE, "
		+ "readMap = NULL, " + "reorder = TRUE, " + "verbose = 0)";

	try {
	    RController.getInstance().getEngine().eval(funcall);
	} catch (RException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    // private List<String> cdfFilesSyncList = Collections
    // .synchronizedList(new ArrayList<String>());
    // private List<String> celFilesSyncList = Collections
    // .synchronizedList(new ArrayList<String>());
    // private List<String> variableNamesSyncList = Collections
    // .synchronizedList(new ArrayList<String>());
    //
    // /**
    // * True if affxparser library already loaded. Will be read by two threads.
    // * The volatile ensures visibility.
    // */
    // private volatile boolean libraryLoaded = false;
    //
    // /**
    // * True when all function calls were successfull. Will be read by two
    // * threads. The volatile ensures visibility.
    // */
    // private volatile boolean callsSuccessful = false;
    //
    // /**
    // * Create a new {@link AffxparserWrapper} object.
    // */
    // public AffxparserWrapper() {
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RLibraryWrapper#getAcceptedFileTypes()
    // */
    // public List<FileTypeRecord> getAcceptedFileTypes() {
    // List<FileTypeRecord> ret = new ArrayList<FileTypeRecord>(2);
    // ret.add(new FileTypeRecord(CDF, false));
    // ret.add(new FileTypeRecord(CEL, true));
    // return ret;
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // ideogram.r.rlibwrappers.RFileParser#addFileName(ideogram.r.FileTypeRecord,
    // * java.lang.String)
    // */
    // public synchronized void addFileName(FileTypeRecord fileType,
    // String fileName) {
    // List<String> list;
    //
    // switch (fileType.getFileType()) {
    // case CEL:
    // list = celFilesSyncList;
    // break;
    // case CDF:
    // list = cdfFilesSyncList;
    // break;
    // default: // Ignore call if file type not supported.
    // return;
    // }
    //
    // if (!fileType.areMultipleAccepted() && list.size() > 0) {
    // // Overwrite the entry at the first position
    // list.set(0, fileName);
    // } else {
    // // Just add the fileName
    // list.add(fileName);
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames()
    // */
    // public synchronized void clearListOfFileNames() {
    // // Thread safety. Synchronization assures that both lists are cleared.
    // clearListOfFileNames(CDF);
    // clearListOfFileNames(CEL);
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames(ideogram.r.FileTypeRecord)
    // */
    // public synchronized void clearListOfFileNames(FileTypeRecord fileType) {
    // clearListOfFileNames(fileType.getFileType());
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames(ideogram.r.FileTypeRecord.FileTypeRegistry)
    // */
    // public synchronized void clearListOfFileNames(
    // FileTypeRecord.FileTypeRegistry fileType) {
    // switch (fileType) {
    // case CEL:
    // celFilesSyncList.clear();
    // break;
    // case CDF:
    // cdfFilesSyncList.clear();
    // default:
    // return; // Ignore unknown file type.
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RFileParser#loadFile()
    // */
    // public synchronized void loadFiles() {
    // // try {
    // // TODO use readCELFile here!
    // // } catch (RException e1) {
    // // // TODO Auto-generated catch block
    // // e1.printStackTrace();
    // // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RLibraryWrapper#loadLibrary()
    // */
    // public synchronized void loadLibrary() throws RException {
    // if (!libraryLoaded) {
    // RController.getInstance().loadRLibrary("affxparser");
    // libraryLoaded = true;
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RLibraryWrapper#unloadLibrary()
    // */
    // public synchronized void unloadLibrary() throws RException {
    // if (libraryLoaded) {
    // RController.getInstance().unloadRLibrary("affxparser");
    // libraryLoaded = false;
    // }
    // }
    //
    // /**
    // * Check whether all preconditions necessary to use one of the affxparser
    // * functions are met.
    // *
    // * @return true if everything is ok.
    // */
    // private synchronized boolean checkPreconditions() {
    // return (!cdfFilesSyncList.isEmpty() && !celFilesSyncList.isEmpty());
    // }
    //
    // /**
    // * Create an unique variable name from the given CEL file name.
    // *
    // * @param celFileName
    // * @return
    // */
    // private String makeVarName(String celFileName) {
    // StringBuffer buf = new StringBuffer();
    // String[] tmp = celFileName.split(File.separator);
    // buf.append(tmp[tmp.length - 1]);
    // buf.append("_");
    // buf.append(System.currentTimeMillis());
    // String ret = buf.toString();
    // /*
    // * Replace sequences of characters other than A-Z, a-z, or 0-9 by a
    // * single _.
    // */
    // ret = ret.replaceAll("(\\W)+", "_");
    // return ret;
    // }
    //
    // // Wrapper methods around functions in package affxparser.
    //
    // /**
    // * Use the function {@code readCelUnits()} if all preconditions are met.
    // If
    // * any of the preconditions is not met, an empty list of variable names is
    // * returned.
    // * <em>This method is public for implementational (eases the detection
    // with
    // * reflection, as no security manager has to be considered) reasons. It
    // * should <strong>not</strong> be called directly!</em>
    // *
    // * @param useMultipleVariables
    // * Set this to true if you want one variable for each CEL
    // * file.
    // * @throws RException
    // * @return List containing the variable names.
    // */
    // public synchronized List<String> readCelUnits(boolean
    // useMultipleVariables)
    // throws RException {
    // callsSuccessful = false; // No calls performed => none successful
    //
    // // Return an empty list if preconditions are not met.
    // if (!checkPreconditions()) {
    // return getVariableNames();
    // }
    //
    // List<String> functionCalls = prepareFuncalls(useMultipleVariables);
    // boolean tmpSuccess = true; // Assume success initially.
    // for (String funcall : functionCalls) {
    // REXP rexp = RController.getInstance().getEngine().eval(funcall);
    //
    // // If one call to eval() fails, tmpSuccess has to be set to false.
    // tmpSuccess = tmpSuccess && (rexp != null);
    // }
    // callsSuccessful = tmpSuccess;
    //
    // return getVariableNames();
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ideogram.r.rlibwrappers.RFileParser#getVariableNames()
    // */
    // public synchronized List<String> getVariableNames() {
    // ArrayList<String> ret;
    // if (callsSuccessful) {
    // // Synchronize, as copying might iterate over the list.
    // synchronized (variableNamesSyncList) {
    // // Return copy in order to assure thread safety.
    // ret = new ArrayList<String>(variableNamesSyncList);
    // }
    // } else {
    // ret = new ArrayList<String>();
    // }
    // return ret;
    // }
    //
    // private synchronized List<String> prepareFuncalls(
    // boolean useMultipleVariables) {
    // String funcall, varName;
    // List<String> functionCalls = new ArrayList<String>();
    //
    // variableNamesSyncList.clear();
    // if (useMultipleVariables) {
    // synchronized (celFilesSyncList) {
    // for (String celFileName : celFilesSyncList) {
    // varName = makeVarName(celFileName);
    // variableNamesSyncList.add(varName);
    //
    // funcall = varName + " <- readCelUnits('" + celFileName
    // + "'," + "cdf = '" + cdfFilesSyncList.get(0)
    // + "', units=NULL, reorder=FALSE)";
    // functionCalls.add(funcall);
    // }
    // }
    // } else {
    // StringBuffer sb = new StringBuffer();
    // synchronized (celFilesSyncList) {
    // ListIterator<String> it = celFilesSyncList.listIterator();
    //
    // sb.append("c('"); // Open the R vector.
    // sb.append(it.next()); // Add the first element.
    // while (it.hasNext()) { // Add all other elements.
    // sb.append("', '");
    // sb.append(it.next());
    // }
    // sb.append("')"); // Close the R vector;
    //
    // varName = makeVarName(cdfFilesSyncList.get(0));
    // variableNamesSyncList.add(varName);
    //
    // funcall = varName + " <- readCelUnits('" + sb + "',"
    // + "cdf = '" + cdfFilesSyncList.get(0)
    // + "', units=NULL, reorder=FALSE)";
    // functionCalls.add(funcall);
    // }
    // }
    //
    // return functionCalls;
    //
    // }
}
