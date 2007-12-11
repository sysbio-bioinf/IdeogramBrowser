/*
 * File:	RDsNameParam.java
 * Created: 10.12.2007
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
public @interface RDsNameParam {
    
//    /**
//     * Name of the R function the input field belongs to.
//     *
//     * @return
//     */
//    String funcname();
    
    /**
     * Label the corresponding input field in the GUI shall have.
     * Default: RBool.
     */
    String name();

//    /**
//     * Default value of the parameter.
//     * Default: ""
//     */
//    String value() default "";
    
    /**
     * Specify whether the user has to fill out this field.
     *
     * @return
     */
    boolean mandatory();
}
