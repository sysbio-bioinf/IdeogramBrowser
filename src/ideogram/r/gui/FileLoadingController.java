/*
 * File: FileLoadingController.java Created: 25.02.2008 Author: Ferdinand
 * Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import static ideogram.r.FileTypeRecord.FileTypeRegistry.CDF;
import static ideogram.r.rlibwrappers.ParserRegistry.AFFXPARSER;
import static ideogram.r.rlibwrappers.ParserRegistry.UNKNOWN_PARSER;
import ideogram.CommonFileFilter;
import ideogram.r.FileTypeRecord;
import ideogram.r.FileTypeRecord.FileTypeRegistry;
import ideogram.r.exceptions.RException;
import ideogram.r.exceptions.UnsupportedFileTypeException;
import ideogram.r.rlibwrappers.AffxparserWrapper;
import ideogram.r.rlibwrappers.ParserRegistry;
import ideogram.r.rlibwrappers.RFileParser;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import sun.security.x509.AVA;

/**
 * Provides everything which is necessary to load files into R. This includes
 * providing the {@link JFileChooser}s with correct file filters set, and
 * passing the chosen files to the correct parser object, which then takes care
 * of loading the files into R.
 * 
 * @author Ferdinand Hofherr
 */
public class FileLoadingController {

    private static final String NO_PARSER_MESSAGE = "No parser for the set file types exists.";
    private static final String NO_FILES_ACCEPTED_MESSAGE = "This package does not accept any files!";

    private RFileParser parser;
    private List<FileTypeRecord> acceptedFileTypes;
    private HashMap<String, JFileChooser> fileChoosers;

    public FileLoadingController() {
        this.acceptedFileTypes = null; // set by setAcceptedFileTypes()
        this.parser = null; // will be determined automatically according to
        // the accepted file types.
        this.fileChoosers = null; // will be created by getFileChoosers();
    }

    /**
     * Get the selected {@link RFileParser}.
     * 
     * @return the parser
     */
    public RFileParser getParser() {
        return parser;
    }

    /**
     * Get a list of currently accepted file types.
     * 
     * @return the acceptedFileTypes
     */
    public List<FileTypeRecord> getAcceptedFileTypes() {
        return acceptedFileTypes;
    }

    /**
     * Set a list of file types that shall be accepted by the parser. Depending
     * upon the set list, the correct parser will be loaded.
     * 
     * @param acceptedFileTypes
     *            the acceptedFileTypes to set
     * @throws RException 
     */
    public void setAcceptedFileTypes(List<FileTypeRecord> acceptedFileTypes)
            throws RException {
        reset();
        this.acceptedFileTypes = acceptedFileTypes;

        // Determine the correct parser and set it.
        ParserRegistry parserType = determineParser();
        switch (parserType) {
            case AFFXPARSER:
                parser = new AffxparserWrapper();
                break;
            default:
                parser = null;
                throw new UnsupportedFileTypeException(NO_PARSER_MESSAGE);
        }
        parser.loadLibrary();
    }

    /**
     * Reset the {@link FileLoadingController}.
     */
    private void reset() {
        acceptedFileTypes = null;
        parser = null;
        fileChoosers = null;
    }

    /**
     * Determine the correct parser type by looking at <strong>all</strong>
     * accepted file types.
     * 
     * @return
     * @throws UnsupportedFileTypeException
     */
    private ParserRegistry determineParser()
            throws UnsupportedFileTypeException {
        ParserRegistry ret = null;
        for (FileTypeRecord rec : acceptedFileTypes) {
            switch (rec.getFileType()) {
                case CDF: // fall through
                case CEL:
                    ret = (ret == null || ret == AFFXPARSER) ? AFFXPARSER
                            : UNKNOWN_PARSER;
                    break;
                case NONE:
                    throw new UnsupportedFileTypeException(
                            NO_FILES_ACCEPTED_MESSAGE);
                default:
                    throw new UnsupportedFileTypeException(NO_PARSER_MESSAGE);
            }
        }
        return ret;
    }

    /**
     * Generate {@link JFileChooser}s for all accepted fileTypes if the
     * necessary preconditions are met. The preconditions are:
     * <ul>
     * <li>List of accepted file types has been set</li>
     * <li>Correct parser has been chosen.</li>
     * </ul>
     * If the file choosers have already been generated, they will not be
     * regenerated until the next call to
     * {@link FileLoadingController#setAcceptedFileTypes(List)} occures.
     * 
     * @return the fileChoosers or null if the preconditions are not met.
     */
    public Map<String, JFileChooser> getFileChoosers() {
        if (acceptedFileTypes == null && parser == null) {
            return null;
        }

        if (fileChoosers == null) {
            CommonFileFilter ff;
            JFileChooser fc;
            fileChoosers = new HashMap<String, JFileChooser>();
            for (FileTypeRecord ft : acceptedFileTypes) {
                ff = new CommonFileFilter();
                ff.addExtension(ft.getFileType().extension());
                fc = new JFileChooser();
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileFilter(ff);
                fc.setMultiSelectionEnabled(ft.areMultipleAccepted());
                fileChoosers.put(ft.toString(), fc);
            }
        }
        return fileChoosers;
    }
}
