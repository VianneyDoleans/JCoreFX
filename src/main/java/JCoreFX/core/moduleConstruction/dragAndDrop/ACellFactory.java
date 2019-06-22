package JCoreFX.core.moduleConstruction.dragAndDrop;

import JCoreFX.services.data.DataService;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.input.*;

public abstract class ACellFactory<T extends NodeCell> {

    protected IndexedCell<T> dropZone;
    protected DataService _dataService;

    /**
     * can be Override to redefine dragAndDrop style
     */
    protected String DROP_HINT_STYLE;

    public ACellFactory(DataService dataService)
    {
        _dataService = dataService;
        DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 2 2 2 2; -fx-padding: 3 3 1 3";
    }

    /**
     * Define if the item is draggable
     * @param item item dragged during drag and drop
     * @return return true if can drag, false other case
     */
    protected abstract boolean isDraggable(T item);

    /**
     * define if the item can be drop here
     * @param event event drag over
     * @param draggedItem item dragged during drag and drop
     * @param dragOverThisItem item where item dragged is over
     * @return return true if can drop, false other case
     */
    protected abstract boolean isDroppable(DragEvent event, JavafxItem<T> draggedItem, JavafxItem<T> dragOverThisItem);

    /**
     * define the graphics of the new cell created
     * @param item item of the new node created
     * @param <E> Javafx's node
     * @return Node returned, used for displaying of the cell
     */
    protected abstract <E extends Node> E defineGraphics(T item);

    /**
     * Generate a new node with id recovered of dragged item, because don't have its node,
     * can only transfer a string by event (serializable), not a node, so give id,
     * necessary for obtaining information about it
     * @param id id of dragItem
     * @param type a random Node with the good type is necessary to be given, for using New methods (factory)
     * @return recreate a node, fill it with information from DataService by id, or stay empty if
     * id isn't a DataCenter resource
     */
    protected T generateItem(String id, T type) {
        T newItem = (T)type.New();
        newItem.setId(id);
        if (_dataService.getSelectedScene().getNode(id) != null) {
            if (!(_dataService.getSelectedScene().getNode(id).getType().equals("FOLDER") ||
            _dataService.getSelectedScene().getNode(id).equals("-1")))
            {
                newItem.setName(_dataService.getSelectedScene().getNode(id).getContent().getName());
            }
            else
                newItem.setName(_dataService.getSelectedScene().getNode(id).getName());

            newItem.setType(_dataService.getSelectedScene().getNode(id).getType());
            //TODO : generalize all generateItem
        }
        return newItem;
    }

    protected void dragDetected(MouseEvent event, IndexedCell<T> draggedCell, JavafxView<T> treeView)
    {
        if (!isDraggable(draggedCell.getItem()))
            return;
        Dragboard db = draggedCell.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        String id = draggedCell.getItem().getId();
        content.putString(id);
        db.setContent(content);
        db.setDragView(draggedCell.snapshot(null, null));
        event.consume();
        System.out.println("start drag");
    }

    /**
     * define the new location of a item after a successful drag and drop action,
     * have to remove the item to the precedent location, then add it in the new location
     * @param droppedOver item where dragged item was drop
     * @param draggedItem item dragged in the drag and drop action
     * @param javafxItem
     */
    protected abstract void newLocation(JavafxItem<T> droppedOver, JavafxItem<T> draggedItem, JavafxView<T> javafxItem);

    /**
     * clear drop location
     */
    protected void clearDropLocation() { if (dropZone != null) dropZone.setStyle(""); }
}
