package JCoreFX.core.serviceConstruction.annotationsServices;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for calling a method when a critical error is generate and notify by a method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnCriticalError
{
    /**
     * @return an integer for knowing the order of called methods when ServiceUpdater called methods after a OnCriticalError notification.
     */
    int order() default 0;
}
