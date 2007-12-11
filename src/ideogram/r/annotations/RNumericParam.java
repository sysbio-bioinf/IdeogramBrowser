/*
 * File:	RNumericParam.java
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
public @interface RNumericParam {
    
//    /**
//     * Name of the R function the input field belongs to.
//     *
//     * @return
//     */
//    String funcname();
    
    /**
     * Labels the corresponding input fields in the GUI shall have.
     * Default: RNumeric.
     */
    String name() default "RNumeric";
    
//    /**
//     * Default value of the parameter.
//     * Default: 0.0
//     */
//    double value() default 0.0;
    
    /**
     * Specify whether the user has to fill out this field.
     *
     * @return
     */
    boolean mandatory();
}
