package JCoreFX.core.managerConstruction;

/**
 * abstract an item handle by a manager.
 * Item can be something generics like Service, Module.
 * @see AManager
 */
public abstract class AManagerItem {

    protected String name;

    public AManagerItem(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
