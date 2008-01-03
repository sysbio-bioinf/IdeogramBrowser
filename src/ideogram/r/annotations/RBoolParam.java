/*
 * File:	RBoolParam.java
 * Created: 09.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation to annotate an boolean parameter expected by an R
 * function.
 * @author Ferdinand Hofherr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RBoolParam {
        
    /**
     * Label which the corresponding input fields in the GUI shall have.
     * Default: RBool.
     */
    String name() default "RBool";
            
    /**
     * Specify whether the user has to fill out this field.
     *
     * @return
     */
    boolean mandatory();
}
