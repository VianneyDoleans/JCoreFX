package JCoreFX.core.moduleConstruction.dragAndDrop;

import JCoreFX.services.data.DataService;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.util.Objects;

/**
 * Factory used for TreeView, override original factory of treeView from implemented grab and drop.
 * Need to implement some methods to work.
 */
public abstract class ACellFactoryTreeView<T extends NodeCell> extends ACellFactory<T> implements Callback<TreeView<T>, TreeCell<T>> {

    private JavafxItem.JavafxTreeItem javafxTreeItem;
    private JavafxView.JavafxTree javafxTree;

    public ACellFactoryTreeView(DataService dataService)
    {
        super(dataService);
        this.javafxTreeItem = new JavafxItem.JavafxTreeItem();
        this.javafxTree = new JavafxView.JavafxTree();
    }

    protected TreeCell<T> newCell() {
        TreeCell<T> cell = new TreeCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setItem(null);
                    setGraphic(null);
                    return;
                }
                Node display = defineGraphics(item);
                if (display != null)
                    setGraphic(display);
            }
        };
        return cell;
    }

    @Override
    public TreeCell<T> call(TreeView<T> treeView) {
        TreeCell<T> cell = newCell();
        cell.setOnDragDetected((MouseEvent event) -> {
            JavafxView<T> javafxView = new JavafxView<>(treeView);  dragDetected(event, cell, javafxView); });
        cell.setOnDragOver((DragEvent event) -> {
            JavafxView<T> javafxView = new JavafxView<>(treeView);  dragOver(event, cell, javafxView); });
        cell.setOnDragDropped((DragEvent event) -> {
            JavafxView<T> javafxView = new JavafxView<>(treeView);  drop(event, cell, javafxView); });
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());
        return cell;
    }

    protected JavafxItem<T> recoverDraggedItem(DragEvent event, T type, TreeView<T> treeView)
    {
        JavafxItem<T> draggedItemJavaFx;

        if (!event.getDragboard().hasString())
            return null;
        String idDraggedItem = event.getDragboard().getString();
        T draggedItem = generateItem(idDraggedItem, type);
        draggedItemJavaFx = new JavafxItem<T>(draggedItem, _dataService, treeView);
        return draggedItemJavaFx;
    }

    protected void dragOver(DragEvent event, TreeCell<T> dragOverThisCell, JavafxView<T> listView)
    {
        if (!event.getDragboard().hasString())
            return;
        String idDraggedItem = event.getDragboard().getString();
        T draggedItem = generateItem(idDraggedItem, dragOverThisCell.getItem());

        JavafxItem<T> draggedItemJavaFx = new JavafxItem<>(draggedItem, _dataService, listView.get(this.javafxTree));
        listView.get(new JavafxView.JavafxTree()).getSelectionModel().select(draggedItemJavaFx.get(javafxTreeItem));
        JavafxItem<T> dragOverThisItemJavaFx = new JavafxItem<>(dragOverThisCell.getTreeItem());
        //Bug Selection
        //listView.get(new JavafxView.JavafxTree()).getSelectionModel().select(draggedItemJavaFx.get(new JavafxItem.JavafxTreeItem()));
        /*if (!isDraggable(draggedItem)) {
            clearDropLocation();
            return;
        }*/
        if (!isDroppable(event,
                draggedItemJavaFx,
                dragOverThisItemJavaFx))
            return;
        event.acceptTransferModes(TransferMode.ANY);
        if (!Objects.equals(dropZone, dragOverThisCell)) {
            clearDropLocation();
            this.dropZone = dragOverThisCell;
            dropZone.setStyle(DROP_HINT_STYLE);
        }
    }

    protected void drop(DragEvent event, TreeCell<T> treeCellDragOverItem, JavafxView<T> javafxView) {
        boolean success = false;
        JavafxItem<T> draggedItemJavaFx;

        System.out.println("drop in progress");
        draggedItemJavaFx = recoverDraggedItem(event, treeCellDragOverItem.getItem(),  javafxView.get(this.javafxTree));
        System.out.println("5");
        if (draggedItemJavaFx == null) {
            System.out.println("draggedItem is null");
            return;
        }
        System.out.println("dragged item is not null");
        newLocation(new JavafxItem<>(treeCellDragOverItem.getTreeItem()), draggedItemJavaFx, javafxView);
        javafxView.get(this.javafxTree).getSelectionModel().select(draggedItemJavaFx.get(this.javafxTreeItem));
        event.setDropCompleted(success);
        _dataService.getSelectedScene().printSceneDebug();
    }
}
