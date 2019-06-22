package JCoreFX.core.linkConstruction;

import JCoreFX.core.managerConstruction.AManagerItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * <pre>
 * Link is used to insure communication between Modules.
 * Modules work independently, but need by moment to communicate between themselves to exchange data or information.
 * Link insure this part.
 * A link is composed of a list of observables.
 * When a module push data, it will be store in the observable list,
 * and all subscribers (module(s)) will receive a notification.
 * They could retrieve the data, and do something with it.
 *
 * It's important to note that it's a list of observables, so data stored inside will not be delete itself.
 * It's necessary that after a module push data inside a link, it remove it.
 * </pre>
 * @see JCoreFX.core.moduleConstruction.AModule
 * @param <T>
 */
public class Link<T> extends AManagerItem {
    public ObservableList<T> list;

    /**
     *
     * @param name name of the link, used for reference
     */
    public Link(String name) {
        super(name);
        list = FXCollections.observableArrayList();
    }

    /**
     *
     * @return return the list of elements
     */
    public ObservableList<T> getObservableList() {
        return list;
    }

    /**
     * add a listener to be notified
     * @param listener listener added
     */
    public void AddListeners(ListChangeListener<T> listener) {
        list.addListener(listener);
    }

    /**
     * remove a listener
     * @param listener listener removed
     */
    public void RemoveListener(ListChangeListener<T> listener) {
        list.removeListener(listener);
    }

    /**
     * clear all elements in the link
     */
    public void Clear() {
        list.clear();
    }

    /**
     * remove an element
     * @param element element removed
     */
    public void RemoveItem(T element) {
        list.remove(element);
    }

    /**
     * add an item
     * @param element element added
     */
    public void AddItem(T element) {
        list.add(element);
    }

    /**
     *
     * @param i index of the item you want to get
     * @return an item that you asked
     */
    public T GetItem(int i) {
        return (list.get(i));
    }

    /**
     *
     * @param index position of the item that you want to remove in the list
     */
    public void RemoveItemAt(int index)
    {
        list.remove(index);
    }
}
