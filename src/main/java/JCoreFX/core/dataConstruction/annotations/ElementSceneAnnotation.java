package JCoreFX.core.dataConstruction.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     Annotation used to precise information on methods of DataElement(s) :
 *              - order : define the order of display
 *              - name : define the name
 *              - type : define which control should be used to set/get the method
 *              - getterSetter : define if the method is a getter or a setter
 *              - displayText : define the text which have to be display
 *
 *     This annotation has to be put to every method that have to be accessible for the user
 *
 *     In case the method is a Setter, only name and getterSetter (define as Setter)
 *     have to be present, others variables are ignored.
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementSceneAnnotation
{
        public enum GetterSetter {
                Getter, Setter
        }

        // have to be completed in the future to respond new demand.
        public enum Type {
                TextField,
                IntegerField,
                DoubleField,
                Label,
                CheckBox,
                TimePicker
        }

        int order() default 0;
        String name();
        Type type() default Type.Label;
        GetterSetter getterSetter();
        String displayText() default "";
}
