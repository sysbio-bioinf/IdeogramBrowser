package ideogram.r.rlibwrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import ideogram.r.FileTypeRecord;
import ideogram.r.exceptions.RException;

public interface RFileParser extends RLibraryWrapper {

    /**
     * Remove all file names of a certain type.
     * 
     * @param fileType
     */
    public void clearListOfFileNames(FileTypeRecord fileType);

    /**
     * Tell the file parser about the file to parse. The fileNames will be
     * stored according to their file type extensions. <strong>You must pass
     * absolute path names!</strong>
     * 
     * @param fileType
     * @param fileName
     */
    public void addFileName(FileTypeRecord fileType, String fileName);

    /**
     * Tell the underlying parser to load the files into R. If
     * multipleVariables is set to true, or the parser is not capable of
     * parsing multiple files into a single R data structure, the parser will
     * create a unique variable for each loaded file. If it multipleVariables
     * is set to false the parser will attempt to parse multiple files into a
     * single data structure.
     * 
     * @param multipleVariables
     *            Shall the parser create multiple variables in R?
     * @return List of variable names in R.
     * @throws RException
     */
    public List<String> loadFiles(boolean multipleVariables) throws RException;
    
    /**
     * Return the list of variable names that were created in the R process. 
     *
     * @return
     */
    public List<String> getVariableNames();
}
