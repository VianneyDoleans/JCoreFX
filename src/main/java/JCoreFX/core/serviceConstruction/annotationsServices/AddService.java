package JCoreFX.core.serviceConstruction.annotationsServices;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for calling a method when a service is store in the serviceManager (action done in the initialisation of the program).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddService {
    /**
     * @return an integer for knowing the order of called methods when ServiceUpdater called methods after a AddService notification.
     */
    int order() default 0;
}
