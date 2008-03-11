/*
 * File:	RDsNameParam.java
 * 
 * Created:	10.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation to mark the field, where the name of the currently loaded
 * data set will be stored.
 * 
 * @author Ferdinand Hofherr
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RDsNameParam {

    /**
     * Label the corresponding input field in the GUI shall have. Default:
     * RBool.
     */
    String name();

    /**
     * Specify whether the user has to fill out this field.
     * 
     * @return
     */
    boolean mandatory();
}
