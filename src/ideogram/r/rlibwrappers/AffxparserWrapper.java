/*
 * File: AffxparserWrapper.java Created: 23.02.2008 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import static ideogram.r.FileTypeRecord.FileTypeRegistry.CDF;
import static ideogram.r.FileTypeRecord.FileTypeRegistry.CEL;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import ideogram.r.FileTypeRecord;
import ideogram.r.RController;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.exceptions.RException;
import ideogram.r.gui.DefaultMessageDisplayModel;

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

    private final static Logger logger = Logger
            .getLogger(AffxparserWrapper.class.getName());

    private HashMap<FileTypeRecord.FileTypeRegistry, ArrayList<String>> fileNames;
    private boolean libraryLoaded;

    private Future<List<String>> varNameFuture;

    /**
     * Create a new {@link AffxparserWrapper} object.
     */
    public AffxparserWrapper() {
        fileNames = new HashMap<FileTypeRegistry, ArrayList<String>>();
        fileNames.put(CDF, new ArrayList<String>());
        fileNames.put(CEL, new ArrayList<String>());
        this.libraryLoaded = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RLibraryWrapper#getAcceptedFileTypes()
     */
    public List<FileTypeRecord> getAcceptedFileTypes() {
        ArrayList<FileTypeRecord> ret = new ArrayList<FileTypeRecord>(2);
        ret.add(new FileTypeRecord(CDF, false));
        ret.add(new FileTypeRecord(CEL, true));
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#addFileName(ideogram.r.FileTypeRecord,
     *      java.lang.String)
     */
    public synchronized void addFileName(FileTypeRecord fileType,
            String fileName) {
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
    public synchronized void clearListOfFileNames(FileTypeRecord fileType) {
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
    public synchronized void loadLibrary() throws RException {
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
    public synchronized void unloadLibrary() throws RException {
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
        StringBuffer buf = new StringBuffer();
        String[] tmp = celFileName.split(File.separator);
        buf.append(tmp[tmp.length - 1]);
        buf.append("_");
        buf.append(System.currentTimeMillis());
        String ret = buf.toString();
        /*
         * Replace sequences of characters other than A-Z, a-z, or 0-9 by a
         * single _.
         */
        ret = ret.replaceAll("(\\W)+", "_");
        return ret;
    }

    // Wrapper methods around functions in package affxparser.

    /**
     * Use the function </code>readCelUnits()</code> if all preconditions are
     * met. If any of the preconditions is not met, an empty list of variable
     * names is returned. The file loading operation will be executed in its
     * own thread.
     * 
     * @param useMultipleVariables
     *            Set this to true if you want one variable for each CEL file.
     * @throws RException
     * @return List containing the variable names.
     */
    public List<String> readCelUnits(boolean useMultipleVariables)
            throws RException {

        // Return an empty list if preconditions are not met.
        if (!checkPreconditions()) {
            return getVariableNames();
        }

        CelReader reader = new CelReader(fileNames.get(CDF).get(0), fileNames
                .get(CEL), useMultipleVariables);
        varNameFuture = RController.getInstance().submitTask(reader);
        return getVariableNames();

    }

    /*
     * (non-Javadoc)
     * 
     * @see ideogram.r.rlibwrappers.RFileParser#getVariableNames()
     */
    public List<String> getVariableNames() {
        List<String> ret;

        if (varNameFuture.isDone()) {
            try {
                ret = varNameFuture.get();
            } catch (InterruptedException e) {
                ret = new ArrayList<String>();
            } catch (ExecutionException e) {
                ret = new ArrayList<String>();
            }
        }
        else {
            ret = new ArrayList<String>();
        }

        return ret;
    }

    private class CelReader implements Callable<List<String>> {

        private String cdfFileName;
        private ArrayList<String> celFileNames;
        boolean useMultipleVariables;
        private List<String> variableNamesSyncList;

        /**
         * Create a new CelReader. The passed objects will be copied.
         * 
         * @param cdfFileName
         *            Name of the CDF File, will be copied.
         * @param celFileNames
         *            Name of the CEL file, will be copied.
         * @param useMultipleVariables
         *            true if multiple variables shall be created.
         */
        public CelReader(String cdfFileName, List<String> celFileNames,
                boolean useMultipleVariables) {
            this.cdfFileName = new String(cdfFileName);
            this.celFileNames = new ArrayList<String>(celFileNames);
            this.useMultipleVariables = useMultipleVariables;
            variableNamesSyncList = Collections
                    .synchronizedList(new ArrayList<String>());
        }

        public List<String> call() {
            try {
                String varName, funcall;
                Rengine engine = RController.getInstance().getEngine();
                REXP rRes;

                long startTime = System.currentTimeMillis();

                if (useMultipleVariables) {
                    // Create one variable for each celFile
                    for (String celFileName : celFileNames) {
                        varName = makeVarName(celFileName);
                        variableNamesSyncList.add(varName);
                        funcall = varName + " <- readCelUnits('" + celFileName
                                + "'," + "cdf = '" + cdfFileName
                                + "', units=NULL, reorder=FALSE)";

                        RController.getInstance().getRMainLoopModel().rBusy(
                                engine, 1);
                        rRes = engine.eval(funcall);
                        RController.getInstance().getRMainLoopModel().rBusy(
                                engine, 0);
                        // TODO Check whether rRes == null ==> An error might
                        // have
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
                    variableNamesSyncList.add(varName);
                    funcall = varName + " <- readCelUnits('" + sb.toString()
                            + "'," + "cdf = '" + cdfFileName
                            + "', units=NULL, reorder=FALSE)";
                    RController.getInstance().getRMainLoopModel().rBusy(
                            engine, 1);
                    rRes = engine.eval(funcall);
                    RController.getInstance().getRMainLoopModel().rBusy(
                            engine, 0);
                    // TODO Check whether rRes == null ==> An error might have
                    // occured.
                }

                long stopTime = System.currentTimeMillis();
                long seconds = (stopTime - startTime) / 1000;
                long hours = seconds / 3600;
                long mins = (seconds % 3600) / 60;
                seconds -= hours * 3600 + mins * 60;

                String msg = "Loaded files in "
                        + String.format("%2dh %2dmin %2dsec", hours, mins,
                                seconds) + ".";
                DefaultMessageDisplayModel.getInstance().displayMessage(msg);
                RController.getInstance().toRwriteln(msg);

            } catch (RException e) {
                // TODO enable message dialog!
                e.printStackTrace();
            }
            return variableNamesSyncList;
        }

    }

}
