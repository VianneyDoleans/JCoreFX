package JCoreFX.core.moduleConstruction.dragAndDrop;

import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;

public class JavafxView<T extends NodeCell> {
    private ListView<T> list;
    private TreeView<T> tree;

    public static class JavafxTree {}

    public static class JavafxList {}

    JavafxView(ListView<T> listView)
    {
        list = listView;
    }

    JavafxView(TreeView<T> treeView)
    {
        tree = treeView;
    }

    public TreeView<T> get(JavafxTree javafxList)
    {
        return tree;
    }

    public ListView<T> get(JavafxList javafxList)
    {
        return list;
    }
}
