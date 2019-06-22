package JCoreFX.core.serviceConstruction;

import JCoreFX.core.managerConstruction.AManagerItem;
import JCoreFX.core.notificationConstruction.INotificationListener;
import JCoreFX.core.notificationConstruction.Notification;
import JCoreFX.tools.ExceptionsCatch;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Services are manager of the software's JCoreFX.core.
 * They are used by JCoreFX.modules to obtain some data/information/execute tasks...
 *
 * Services are the actions that have to be centralize,
 * so that is benefits for every JCoreFX.modules, not be re-implemented each time a new module is create.
 * For example, we have a networkSimulation service, JCoreFX.modules can used it to make for example a request,
 * without have to re-implement a networkSimulation / used a external library.
 *
 * They can generate notification to ServiceUpdater (JCoreFX.core) when something have to be communicate
 * </pre>
 * @see JCoreFX.core.moduleConstruction.AModule
 * @see JCoreFX.core.manager.ServiceUpdater
 * @see Notification
 */
public abstract class IService extends AManagerItem //implements INotificationListener //Not necessary for service to have possibility to subscribe others but can be, by remove "//"
{
    /* Adding State to all Service, but need to check if State is really usefull with JavaFx tasks */
    protected ServiceState _state;
    private List<INotificationListener> listeners;

    /**
     * add listerner to its list.
     * listeners will be called when a notification is generated
     * @see Notification
     * @param listener
     */
    public void addListeners(INotificationListener listener)
    {
        listeners.add(listener);
    }

    /**
     * generate a notification for listeners
     * @see Notification
     * @param notification
     */
    protected void createNotification(Notification notification)
    {
        for (INotificationListener listener : listeners)
            listener.Notify(notification);
    }

    /**
     *
     * @param name
     */
    public IService(String name) {
        super(name);
        try
        {
            listeners = new ArrayList<INotificationListener>();
            this._state = new ServiceState();
        }
        catch (Exception ex)
        {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }

    public abstract ServiceState.State GetState();
    public abstract boolean Restart();
    public abstract boolean Stop();
    public abstract void Start();

}
