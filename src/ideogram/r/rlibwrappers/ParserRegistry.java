/*
 * File:	ParserRegistry.java
 * 
 * Created: 	26.02.2008
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

/**
 * Contains constants for all available file parsers.
 * 
 * @author Ferdinand Hofherr
 * 
 */
public enum ParserRegistry {
    /**
     * Dummy entry to mark that no parser is available!
     */
    UNKNOWN_PARSER,

    /**
     * affxparser R library.
     */
    AFFXPARSER;
}
