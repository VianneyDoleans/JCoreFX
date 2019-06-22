package JCoreFX.core.moduleConstruction.dragAndDrop;

import javafx.scene.Node;

import java.io.Serializable;

public interface NodeCell extends Serializable {
    Node getGraphics();
    String getName();
    String getId();
    String getType();
    void setType(String type);
    void setId(String id);
    void setName(String name);
    NodeCell New();
}
