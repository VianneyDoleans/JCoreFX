package JCoreFX.core.dataConstruction;

import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * DataElement is the parent of all element in the layout.
 * DataElement contains all basic methods, common to each Element of the layout (like image, sound...).
 */
public interface DataElement extends Serializable
{
    boolean isDisplay();
    void setDisplay(boolean display);
    String getPath();
    void setPath(String path);
    void changeLocation(String path);
    String getType();
    String getName();
    String getId();
    String showAt();
    String getDuration();
    JSONObject toJSON();
    void setFromJSON(JSONObject jsonObject);
}
