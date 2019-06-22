package JCoreFX.core.serviceConstruction.annotationsServices;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for calling a method when a service is remove from the serviceManager
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoveService {
    /**
     * @return an integer for knowing the order of called methods when ServiceUpdater called methods after a RemoveService notification.
     */
    int order() default 0;
}
