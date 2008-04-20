/*
 * File:	RVariableNameFactory.java
 *
 * Created: 	19.04.2008
 *
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>RVariableNameRegistry</code> keeps track of the variable
 * names created so far. It is therefore possible to store an annotation string
 * for each variable name, which hopefully increases usability. Additionally it
 * provides methods for easy creation of unique variable names.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public class RVariableNameRegistry {

    private static final RVariableNameRegistry INSTANCE = new RVariableNameRegistry();

    private volatile Map<String, String> variableNameRegistry = Collections
    .synchronizedMap(new HashMap<String, String>());

    private RVariableNameRegistry() {
    }
    
    public static RVariableNameRegistry getInstance() {
	return INSTANCE;
    }
    
    /**
     * Create a variable name, that points to an object storing a file's
     * contents. The variable name will look this way:
     * FILEEXTENSION_FILENAME_TIME
     * 
     * Thus if for some reason the file is read twice, it there will be two
     * variable names referencing its contents.
     * 
     * @param fileName
     * @param varNameAnnotation
     * @return
     */
    public static String createFileContentName(String fileName,
	    String varNameAnnotation) {
	StringBuilder builder = new StringBuilder();
	String[] parts = fileName.split(File.separator);
	String[] tmp = parts[parts.length - 1].split("\\.");
	String varName;

	/*
	 * Place the file type extension in front of the variable name, if it
	 * exists.
	 */
	if (tmp.length - 1 != 0) {
	    builder.append(removeUnwantedCharacters(tmp[tmp.length - 1]));
	    builder.append("_");
	}

	// Append everything else.
	for (int i = 0; i < tmp.length - 1; i++) {
	    builder.append(removeUnwantedCharacters(tmp[i]));
	    builder.append("_");
	}

	/*
	 * Append time in milliseconds, to make file name unique. NOTE: On some
	 * very rare occasions (i.e. if multiple threads want to create a
	 * variable name for the same file at the same time, the same variable
	 * name might be returned twice).
	 */
	builder.append(System.currentTimeMillis());

	varName = builder.toString();
	getInstance().registerVariableName(varName, varNameAnnotation);

	return varName;
    }

    /**
     * Create a variable name, that points to an object storing a file's
     * contents. The variable name will look this way:
     * FILEEXTENSION_FILENAME_TIME
     * 
     * Thus if for some reason the file is read twice, it there will be two
     * variable names referencing its contents.
     * 
     * @param fileName
     * @return
     */
    public static String createFileContentName(String fileName) {
	return createFileContentName(fileName, "Contents of file " + fileName);
    }
    
    /**
     * Lookup the specified variable name in the registry.
     * 
     * @param variableName
     * @return
     */
    public String lookupVariableName(String variableName) {
	return getVariableNameRegistry().get(variableName);
    }

    /**
     * Clear the variable name registry. Only call this method if the R
     * workspace is cleared.
     * 
     */
    public void clearRegistry() {
	getVariableNameRegistry().clear();
    }

    /**
     * Replace all sequences of characters other than [0-9A-Za-z] with _.
     * 
     * @param s
     * @return
     */
    private static String removeUnwantedCharacters(String s) {
	return s.replaceAll("(\\W)+", "_");
    }

    /**
     * Get the variable name registry.
     * 
     * @return
     */
    private Map<String, String> getVariableNameRegistry() {
	return variableNameRegistry;
    }

    private void registerVariableName(String varName, String annotation) {
	variableNameRegistry.put(varName, annotation);
    }
}
