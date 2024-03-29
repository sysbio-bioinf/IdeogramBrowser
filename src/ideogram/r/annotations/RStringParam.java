/*
 * File:	RStringParam.java
 * 
 * Created:	09.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotaion to mark fields storing string parameters to R functions.
 * Valid R strings must be enclosed in single hyphens!
 * 
 * @author Ferdinand Hofherr
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RStringParam {

    // /**
    // * Name of the R function the input field belongs to.
    // *
    // * @return
    // */
    // String analysisFunction();

    /**
     * Label which the corresponding input field in the GUI shall have. Default:
     * RString.
     */
    String name() default "RString";

    // /**
    // * Default value of the parameter.
    // * Default: ""
    // */
    // String value() default "";

    /**
     * Specify whether the user has to fill out this field.
     * 
     * @return
     */
    boolean mandatory();
}
