package JCoreFX.core.managerConstruction;

import JCoreFX.core.log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * abstract of manager, that handle some generics resources like Links, Services, Modules.
 * @param <T>
 * @see AManagerItem
 */
public abstract class AManager<T extends AManagerItem> {

    private ArrayList<T> _resources = new ArrayList<>();

    protected ArrayList<T> getResources() {
        return _resources;
    }

    public <R extends T> R getResourceByName(String name) {
        Optional<T> resource = this._resources.stream().filter(item -> item.getName().equals(name)).findFirst();

        if (resource.isPresent()) {
            /*
            No way to do a better type check in Java: http://www.informit.com/articles/article.aspx?p=2861454&seqNum=2
            Reason: This method is use to get at the initialization of the JCoreFX.modules the resource that it need from a manager,
            So we are assuming that it will be always valid.
            */
            @SuppressWarnings("unchecked") R result = (R) resource.get();

            return result;
        }
        Log.getInstance().write(Level.SEVERE, "Error: No ressource with name : " + name);
        return null;
    }

    // TODO check if all the methods are valids

    public boolean hasRessource(String name) {
        return this._resources.stream().anyMatch(item -> item.getName().equals(name));
    }

    public void addResource(T res)
    {
        this._resources.add(res);
    }

    public void addResources(Collection<T> resources) {
        this._resources.addAll(resources);
    }

    public void removeResource(String name) {
        this._resources.removeIf(item -> item.getName().equals(name));
    }

    public void removeResources(Collection<String> resourceNames) {
        this._resources.removeIf(item -> resourceNames.contains(item.getName()));
    }

    public List<T> getRessourcesMatchBy(Predicate<T> predicate) {
        return this._resources.stream().filter(predicate).collect(Collectors.toList());
    }

}
