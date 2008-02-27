/*
 * File: AffxparserWrapper.java Created: 23.02.2008 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import static ideogram.r.FileTypeRecord.FileTypeRegistry.CDF;
import static ideogram.r.FileTypeRecord.FileTypeRegistry.CEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import ideogram.r.FileTypeRecord;
import ideogram.r.RController;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.exceptions.RException;

/**
 * Wrapper class for the affxparser R package available from bioconductor.org.
 * This is a low level package. It should therefore not show up in
 * {@link RGuiWindow}'s "Select R Library" ComboBox. Consequently it is not
 * necessary to add it to the {@link AvailableRLibraries} enum. As another
 * consequence the following operations are not supported:
 * <ul>
 * <li> {@link AffxparserWrapper#listSampleData()} </li>
 * </ul>
 * 
 * @author Ferdinand Hofherr
 */

/**
 * TODO INSERT DOCUMENTATION HERE!
 * 
 * @author Ferdinand Hofherr
 */
public class AffxparserWrapper implements RFileParser {

    private HashMap<FileTypeRecord.FileTypeRegistry, ArrayList<String>> fileNames;
    private boolean libraryLoaded;
    private ArrayList<String> variableNames;

    /**
     * Create a new {@link AffxparserWrapper} object.
     */
    public AffxparserWrapper() {
        fileNames = new HashMap<FileTypeRegistry, ArrayList<String>>();
        fileNames.put(CDF, new ArrayList<String>());
        fileNames.put(CEL, new ArrayList<String>());
        this.libraryLoaded = false;
        variableNames = new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#getAcceptedFileTypes()
     */
    public List<FileTypeRecord> getAcceptedFileTypes() {
        ArrayList<FileTypeRecord> ret = new ArrayList<FileTypeRecord>();
        ret.add(new FileTypeRecord(CDF, false));
        ret.add(new FileTypeRecord(CEL, true));
        ret.trimToSize();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#addFileName(ideogram.r.FileTypeRecord,
     *      java.lang.String)
     */
    public void addFileName(FileTypeRecord fileType, String fileName) {
        ArrayList<String> list = fileNames.get(fileType.getFileType());

        if (!fileType.areMultipleAccepted() && list.size() > 0) {
            // Overwrite the entry at the first position
            list.set(0, fileName);
        }
        else {
            // Just add the fileName
            list.add(fileName);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#clearListOfFileNames(ideogram.r.FileTypeRecord)
     */
    public void clearListOfFileNames(FileTypeRecord fileType) {
        fileNames.get(fileType.getFileType()).clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#loadFile()
     */
    public List<String> loadFiles(boolean useMultipleVariables)
            throws RException {
        return readCelUnits(useMultipleVariables);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#loadLibrary()
     */
    public void loadLibrary() throws RException {
        if (!libraryLoaded) {
            RController.getInstance().loadRLibrary("affxparser");
            libraryLoaded = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#unloadLibrary()
     */
    public void unloadLibrary() throws RException {
        if (libraryLoaded) {
            RController.getInstance().unloadRLibrary("affxparser");
            libraryLoaded = false;
        }
    }

    /**
     * Check whether all preconditions necessary to use one of the affxparser
     * functions are met.
     * 
     * @return true if everything is ok.
     */
    private boolean checkPreconditions() {
        boolean ret = true;

        for (Collection<String> col : fileNames.values()) {
            ret = ret && col.isEmpty() ? false : true;
        }

        return ret;
    }

    /**
     * Create an unique variable name from the given CEL file name.
     * 
     * @param celFileName
     * @return
     */
    private String makeVarName(String celFileName) {
        return null;
    }

    // Wrapper methods around functions in package affxparser.

    /**
     * Use the function </code>readCelUnits()</code> if all preconditions are
     * met. If any of the preconditions is not met, an empty list of variable
     * names is returned.
     * 
     * @param useMultipleVariables
     *            Set this to true if you want one variable for each CEL file.
     * @throws RException
     * @return List containing the variable names.
     */
    public List<String> readCelUnits(boolean useMultipleVariables)
            throws RException {
        variableNames.clear();

        // Return an empty list if preconditions are not met.
        if (!checkPreconditions()) {
            return variableNames;
        }

        ArrayList<String> celFileNames = fileNames.get(CEL);
        String cdfFileName = fileNames.get(CDF).get(0); // only one CDF file
        // accepted!
        String varName, funcall;
        Rengine engine;
        REXP rRes;
        if (useMultipleVariables) {
            // Create one variable for each celFile
            for (String celFileName : celFileNames) {
                varName = makeVarName(celFileName);
                variableNames.add(varName);
                funcall = varName + " <- readCelUnits('" + celFileName + "',"
                        + "cdf = '" + cdfFileName
                        + "', units=NULL, reorder=FALSE)";
                engine = RController.getInstance().getEngine();
                rRes = engine.eval(funcall);
                // TODO Check whether rRes == null ==> An error might have
                // occured.
            }
        }
        else {
            ListIterator<String> it = celFileNames.listIterator();
            StringBuffer sb = new StringBuffer();

            sb.append("c('"); // Open the R vector.
            sb.append(it.next()); // Add the first element.
            while (it.hasNext()) { // Add all other elements.
                sb.append("', '");
                sb.append(it.next());
            }
            sb.append("')"); // Close the R vector;

            varName = makeVarName(celFileNames.get(0));
            variableNames.add(varName);
            funcall = varName + " <- readCelUnits('" + sb.toString() + "',"
                    + "cdf = '" + cdfFileName
                    + "', units=NULL, reorder=FALSE)";
            engine = RController.getInstance().getEngine();
            rRes = engine.eval(funcall);
            // TODO Check whether rRes == null ==> An error might have occured.
        }

        return variableNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#getVariableNames()
     */
    public List<String> getVariableNames() {
        return variableNames;
    }

}
