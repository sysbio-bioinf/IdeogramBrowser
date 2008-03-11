package ideogram.r.rlibwrappers;

import java.util.List;
import ideogram.r.FileTypeRecord;
import ideogram.r.exceptions.RException;

public interface RFileParser extends RLibraryWrapper {

    /**
     * Remove all file names of all types that are known to this parser.
     * 
     */
    public void clearListOfFileNames();

    /**
     * Remove all file names of a certain type.
     * 
     * @param fileType
     */
    public void clearListOfFileNames(FileTypeRecord fileType);

    /**
     * Remove all file names of the specified type, which are known to the
     * parser.
     * 
     * @param fileType
     */
    public void clearListOfFileNames(
	    FileTypeRecord.FileTypeRegistry fileType);

    /**
     * Tell the file parser about the file to parse. The fileNames will be
     * stored according to their file type extensions. <strong>You must pass
     * absolute path names</strong>! If the specified file type is not accepted
     * by the RFileParser, the call to this method will be ignored.
     * 
     * @param fileType
     * @param fileName
     */
    public void addFileName(FileTypeRecord fileType, String fileName);

    /**
     * Tell the underlying parser to load the files into R. If multipleVariables
     * is set to true, or the parser is not capable of parsing multiple files
     * into a single R data structure, the parser will create a unique variable
     * for each loaded file. If it multipleVariables is set to false the parser
     * will attempt to parse multiple files into a single data structure.
     * 
     * @param multipleVariables
     *                Shall the parser create multiple variables in R?
     * @throws RException
     */
    public void loadFiles(boolean multipleVariables);

    /**
     * Return the list of variable names that were created in the R process. If
     * a call to a R function failed, return an empty list.
     * 
     * @return List containing variable names in R, or empty list upon failure.
     */
    public List<String> getVariableNames();
}
