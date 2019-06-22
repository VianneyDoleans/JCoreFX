package JCoreFX.core.manager;

import JCoreFX.core.serviceConstruction.annotationsServices.AddService;
import JCoreFX.core.serviceConstruction.annotationsServices.RemoveService;
import JCoreFX.core.log.Log;
import JCoreFX.core.managerConstruction.AManager;
import JCoreFX.core.notificationConstruction.INotificationListener;
import JCoreFX.core.notificationConstruction.Notification;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.tools.ExceptionsCatch;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * ServiceManager is used to store/get service
 * @see JCoreFX.core.serviceConstruction.IService
 */
public class ServiceManager extends AManager<IService>
{
    private List<INotificationListener> listeners;

    /**
     * This method is used to add a listener on ServiceManager.
     * The listener will receive a notification when serviceManager add, or delete a service
     * @see JCoreFX.core.notificationConstruction.Notification
     * @see JCoreFX.core.serviceConstruction.annotationsServices.RemoveService
     * @see JCoreFX.core.serviceConstruction.annotationsServices.AddService
     * @param listener This is the listener which will receive the notification
     */
    public void addListeners(INotificationListener listener)
    {
        listeners.add(listener);
    }

    /**
     * generate a notification for all the listeners
     * @param notification notification that you want to send for all listeners
     */
    private void createNotification(Notification<Annotation> notification)
    {
        for (INotificationListener listener : listeners)
            listener.Notify(notification);
    }

    /**
     * Return all the JCoreFX.services stored in ServiceManager
     */
    public ArrayList<IService> getServices() {
        return this.getResources();
    }

    /**
     * Instantiation of ServiceManager.
     * Exit the program if ServiceManager can't be instantiate.
     */
    public ServiceManager()
    {
        try {
            listeners = new ArrayList<>();
        }
        catch (Exception ex) {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }

    /**
     * Store a service in the serviceManager.
     * Generate AddService notification
     * @see JCoreFX.core.serviceConstruction.annotationsServices.AddService
     * @param res service added in ServiceManager
     */
    @Override
    public void addResource(IService res)
    {
        super.addResource(res);
        Log.getInstance().write(Level.INFO, "ServiceManager : addService " + res.getName());
        Notification notification = new Notification<>(res, "new JCoreFX.core.serviceConstruction in JCoreFX.core.serviceConstruction manager", AddService.class);
        createNotification(notification);
    }

    /**
     * Store a service in the serviceManager.
     * Generate AddService notification for each Service in the collection parameter.
     * @see JCoreFX.core.serviceConstruction.annotationsServices.AddService
     * @param resources JCoreFX.services added in ServiceManager
     */
    @Override
    public void addResources(Collection<IService> resources) {
        super.addResources(resources);
        for (IService res : resources) {
            Log.getInstance().write(Level.INFO, "ServiceManager : addService " + res.getName());
            Notification notification = new Notification<>(res, "new JCoreFX.core.serviceConstruction in JCoreFX.core.serviceConstruction manager", AddService.class);
            createNotification(notification);
        }
    }

    /**
     * Remove a service from the serviceManager.
     * Generate RemoveService notification
     * @see JCoreFX.core.serviceConstruction.annotationsServices.RemoveService
     * @param name name of service removed
     */
    @Override
    public void removeResource(String name) {
        IService removeService = super.getResources().stream()
                .filter(service -> name.equals(service.getName()))
                .findAny()
                .orElse(null);
        if (removeService != null)
        {
            Log.getInstance().write(Level.INFO, "ServiceManager : RemoveService " + name);
            Notification notification = new Notification<>(removeService, "new JCoreFX.core.serviceConstruction in JCoreFX.core.serviceConstruction manager", RemoveService.class);
            createNotification(notification);
            super.getResources().remove(removeService);
        }
    }
}
