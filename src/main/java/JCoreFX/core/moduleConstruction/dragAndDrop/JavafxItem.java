package JCoreFX.core.moduleConstruction.dragAndDrop;

import JCoreFX.core.dataFile.Scene;
import JCoreFX.services.data.DataService;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.Stack;

public class JavafxItem<T extends NodeCell> {
    private TreeItem<T> item;

    public static class JavafxTreeItem {}

    public static class JavafxNodeItem {}

    JavafxItem(TreeItem<T> javafxItem) { item = javafxItem; }

    private int getPositionChild(String id, TreeItem<T> parent)
    {
        int position = -1;
        int i = 0;
        for (TreeItem<T> child : parent.getChildren())
        {
            if (child.getValue().getId().equals(id)) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    protected void removeChildById(String id, TreeItem<T> parent)
    {
        for (TreeItem<T> child : parent.getChildren())
        {
            if (child.getValue().getId().equals(id)) {
                parent.getChildren().remove(child);
                break;
            }
        }
    }

    protected TreeItem<T> findParentInTree(String idSearched, TreeItem<T> root) {
        Stack<TreeItem<T>> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeItem<T> parent = stack.pop();
            if (parent.getValue().getId().equals(idSearched))
                return parent;
            for (TreeItem<T> child : parent.getChildren()) { stack.push(child); }
        }
        return null;
    }

    private void replaceItemInParentTree(TreeItem<T> item, TreeView<T> treeView, DataService dataService)
    {
        Scene.Node parentDataNode = dataService.getSelectedScene().getNode(item.getValue().getId()).getParent();

        if (parentDataNode != null) {
            TreeItem<T> parent = findParentInTree(parentDataNode.getId(), treeView.getRoot());
            if (parent != null)
            {
                int position = getPositionChild(item.getValue().getId(), parent);
                removeChildById(item.getValue().getId(), parent);
                if (position != -1)
                    parent.getChildren().add(position, item);
            }
        }
    }

    T newItem(String id, T type, DataService DataService)
    {
        T newItem = (T)type.New();
        newItem.setId(id);
        if (DataService.getSelectedScene().getNode(id) != null) {
            if (!(DataService.getSelectedScene().getNode(id).getType().equals("FOLDER") ||
                    DataService.getSelectedScene().getNode(id).equals("-1")))
            {
                newItem.setName(DataService.getSelectedScene().getNode(id).getContent().getName());
            }
            else
                newItem.setName(DataService.getSelectedScene().getNode(id).getName());
            newItem.setType(DataService.getSelectedScene().getNode(id).getType());
        }
        return newItem;
    }

    //TODO : NEED TO CHANGE LATER RECURSIVE TO STACK SYSTEM
    private void generateChildrenItemTree(TreeItem<T> parent, Scene.Node parentNode, DataService dataService)
    {
        for (Scene.Node child : parentNode.getChildren())
        {
            TreeItem<T> childTree = new TreeItem<>();
            childTree.setValue(newItem(child.getId(), parent.getValue(), dataService));
            generateChildrenItemTree(childTree, child, dataService);
            parent.getChildren().add(childTree);
        }
    }

    //TODO : need to take a JavaFxView, not a TreeView (encapsulation), wait another type of view for doing it
    JavafxItem(T javafxItem, DataService dataService, TreeView<T> treeView) {
        item = new TreeItem<>();
        item.setValue(javafxItem);
        Scene.Node itemNode = dataService.getSelectedScene().getNode(javafxItem.getId());
        if (itemNode != null)
        {
            generateChildrenItemTree(item, itemNode, dataService);
            if (treeView != null)
                replaceItemInParentTree(item, treeView, dataService);
        }
    }

    public TreeItem<T> get(JavafxTreeItem javafxTreeItem) { return item; }

    public T get(JavafxNodeItem javafxNodeItem)
    {
        return item.getValue();
    }
}
