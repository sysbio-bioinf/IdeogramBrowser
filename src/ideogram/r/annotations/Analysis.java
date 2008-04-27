/*
 * File:	Analysis.java
 * 
 * Created: 	10.12.2007
 * 
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation to: a) Mark the methods that perform an analysis in R. b)
 * Mark the fields that store parameters for an analysis in R.
 * 
 * Example: If there exists an method annotated with "@Analysis("analysis")" and
 * the corresponding R function needs some parameters, the fields that store
 * parameters for this function should be annotated with
 * "@Analysis("analysis")", too!
 * 
 * @author Ferdinand Hofherr
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Analysis {
    String name();
}
