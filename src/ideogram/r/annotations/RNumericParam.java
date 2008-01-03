/*
 * File:	RNumericParam.java
 * Created: 09.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation to mark fields storing numeric parameters (i.e. int,
 * double ...) to R functions.
 *
 * @author Ferdinand Hofherr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RNumericParam {
    
    /**
     * Labels the corresponding input fields in the GUI shall have.
     * Default: RNumeric.
     */
    String name() default "RNumeric";
        
    /**
     * Specify whether the user has to fill out this field.
     *
     * @return
     */
    boolean mandatory();
}
