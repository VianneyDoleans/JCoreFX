package JCoreFX.core.moduleConstruction.dragAndDrop;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.Callback;
import JCoreFX.services.data.DataService;

import java.util.Objects;

public abstract class ACellFactoryListView <T extends NodeCell> extends ACellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    private JavafxView.JavafxTree javafxTree;
    private JavafxView.JavafxList javafxList;
    private JavafxItem.JavafxNodeItem javafxNodeItem;

    public ACellFactoryListView(DataService dataService)
    {
        super(dataService);
        this.javafxTree = new JavafxView.JavafxTree();
        this.javafxNodeItem = new JavafxItem.JavafxNodeItem();
        this.javafxList = new JavafxView.JavafxList();
    }

    private ListCell<T> newCell() {
        ListCell<T> cell = new ListCell<T>() {
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
    public ListCell<T> call(ListView<T> treeView) {
            ListCell<T> cell = newCell();
            cell.setOnDragDetected((MouseEvent event) -> {
                JavafxView<T> javafxView = new JavafxView<>(treeView);  dragDetected(event, cell, javafxView); });
            cell.setOnDragOver((DragEvent event) -> {
                JavafxView<T> javafxView = new JavafxView<>(treeView);  dragOver(event, cell, javafxView); });
            cell.setOnDragDropped((DragEvent event) -> {
                JavafxView<T> javafxView = new JavafxView<>(treeView);  drop(event, cell, javafxView); });
            cell.setOnDragDone((DragEvent event) -> clearDropLocation());
            return cell;
    }

    protected void dragOver(DragEvent event, IndexedCell<T> dragOverThisCell, JavafxView<T> listView)
    {
        System.out.println("check drag over");
        T dragOverThisItem = dragOverThisCell.getItem();
        if (!event.getDragboard().hasString())
            return;
        String idDraggedItem = event.getDragboard().getString();
        T draggedItem = generateItem(idDraggedItem, dragOverThisCell.getItem());

        System.out.println("will check isDroppable");
        JavafxItem<T> draggedItemJavaFx = new JavafxItem<>(draggedItem, _dataService, listView.get(this.javafxTree));
        JavafxItem<T> dragOverThisItemJavaFx = new JavafxItem<>(dragOverThisItem, _dataService, listView.get(this.javafxTree));
        //Bug Selection
        listView.get(new JavafxView.JavafxList()).getSelectionModel().select(draggedItemJavaFx.get(this.javafxNodeItem));
        if (!isDroppable(event,
                draggedItemJavaFx,
                dragOverThisItemJavaFx))
            return;
        System.out.println("can drag over");
        /*if (!isDraggable(draggedItem)) {
            clearDropLocation();
            return;
        }*/
        System.out.println("clear");
        event.acceptTransferModes(TransferMode.ANY);
        if (!Objects.equals(dropZone, dragOverThisCell)) {
            clearDropLocation();
            this.dropZone = dragOverThisCell;
            dropZone.setStyle(DROP_HINT_STYLE);
        }
    }

    protected void drop(DragEvent event, IndexedCell<T> listCell, JavafxView<T> listView) {
        boolean success = false;
        if (!event.getDragboard().hasString())
            return;
        String idDraggedItem = event.getDragboard().getString();
        T draggedItem = generateItem(idDraggedItem, listCell.getItem());//.get(new JavafxItem.JavafxNodeItem()));
        T dropOverThisItem = listCell.getItem();//.get(new JavafxItem.JavafxNodeItem());

        newLocation(new JavafxItem<>(dropOverThisItem, _dataService, listView.get(this.javafxTree)),
                new JavafxItem<>(draggedItem, _dataService, listView.get(this.javafxTree)),
                listView);
        listView.get(this.javafxList).getSelectionModel().select(draggedItem);
        event.setDropCompleted(success);
    }
}
