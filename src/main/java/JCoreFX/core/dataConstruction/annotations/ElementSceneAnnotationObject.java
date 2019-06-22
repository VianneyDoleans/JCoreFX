package JCoreFX.core.dataConstruction.annotations;

import java.lang.reflect.Method;

/**
 * This class is used to store data of ElementSceneAnnotation annotation when read it.
 * @see ElementSceneAnnotation
 */
public class ElementSceneAnnotationObject
{
    private Method method;
    private int order;
    private String name;
    private ElementSceneAnnotation.Type type;
    private ElementSceneAnnotation.GetterSetter getterSetter;
    private String displayText;

    public ElementSceneAnnotationObject(Method method, int order, String name,
                                        ElementSceneAnnotation.Type type,
                                        ElementSceneAnnotation.GetterSetter getterSetter, String displayText) {
        this.method = method;
        this.order = order;
        this.name = name;
        this.type = type;
        this.getterSetter = getterSetter;
        this.displayText = displayText;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementSceneAnnotation.Type getType() {
        return type;
    }

    public void setType(ElementSceneAnnotation.Type type) {
        this.type = type;
    }

    public ElementSceneAnnotation.GetterSetter getGetterSetter() {
        return getterSetter;
    }

    public void setGetterSetter(ElementSceneAnnotation.GetterSetter getterSetter) {
        this.getterSetter = getterSetter;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        displayText = displayText;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
