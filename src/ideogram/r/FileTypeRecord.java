/*
 * File: FileTypeWrapper.java 
 * 
 * Created: 25.02.2008 
 * 
 * Author: Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

import ideogram.r.rlibwrappers.RLibraryWrapper;

/**
 * Simple class used to specify one (of possibly many) file types an R package
 * accepts.
 * 
 * @author Ferdinand Hofherr
 */
public class FileTypeRecord {

    private FileTypeRecord.FileTypeRegistry fileType;
    private boolean multipleAccepted;

    /**
     * Create a new {@link FileTypeRecord}.
     * 
     * @param fileType
     * @param multipleAccepted
     */
    public FileTypeRecord(FileTypeRecord.FileTypeRegistry fileType,
            boolean multipleAccepted) {
        this.fileType = fileType;
        this.multipleAccepted = multipleAccepted;
    }

    /**
     * Get the file type this {@link FileTypeRecord} stands for.
     * 
     * @return
     */
    public FileTypeRecord.FileTypeRegistry getFileType() {
        return fileType;
    }

    /**
     * Check whether multiple file types are accepted.
     * 
     * @return
     */
    public boolean areMultipleAccepted() {
        return multipleAccepted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("FileTypeRecord-");
        if (multipleAccepted) {
            buf.append("MultiplePathsAccepted-");
        }
        else {
            buf.append("MultiplePathsNotAccepted-");
        }
        buf.append("FileExtension:");
        buf.append(getFileType().extension);

        return buf.toString();
    }

    /**
     * Lists all possible file types that a {@link RLibraryWrapper} might need.
     * 
     * @author Ferdinand Hofherr
     */
    public static enum FileTypeRegistry {
        /**
         * Dummy type. This type may be used to indicate that no file types are
         * accepted.
         */
        NONE("nonenonenone" + 0xBADF00D, "You can't select any files!"),

        /**
         * Affymetrix CEL files.
         */
        CEL("cel", "Select multiple CEL files."),

        /**
         * Affymetrix CDF files.
         */
        CDF("cdf", "Select a single CDF file.");

        private final String extension;
        private final String buttonLabel;

        private FileTypeRegistry(String extension, String buttonLabel) {
            this.extension = extension;
            this.buttonLabel = buttonLabel;
        }

        /**
         * Get the extension of the file type.
         * 
         * @return
         */
        public String extension() {
            return extension;
        }

        public String buttonLabel() {
            return buttonLabel;
        }

        /**
         * Check whether the given file name ends with the correct postfix
         * (e.g. with .cel).
         * 
         * @param fileName
         * @return
         */
        public boolean check(String fileName) {
            return fileName.matches("^.*\\." + extension) ? true : false;
        }
    }

}
