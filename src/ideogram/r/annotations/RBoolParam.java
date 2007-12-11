/*
 * File:	RBoolParam.java
 * Created: 09.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RBoolParam {
    
//    /**
//     * Name of the R function the input field belongs to.
//     *
//     * @return
//     */
//    String funcname();
    
    /**
     * Label which the corresponding input fields in the GUI shall have.
     * Default: RBool.
     */
    String name() default "RBool";
        
//    /**
//     * Default value of the parameter.
//     * Default: false 
//     */
//    String value() default "FALSE";
    
    /**
     * Specify whether the user has to fill out this field.
     *
     * @return
     */
    boolean mandatory();
}
