package JCoreFX.core.manager;

import JCoreFX.core.log.Log;
import JCoreFX.core.notificationConstruction.INotificationListener;
import JCoreFX.core.notificationConstruction.Notification;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.services.ExampleIService;
import JCoreFX.tools.ExceptionsCatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * used for manage notifications.
 * @deprecated since only serviceUpdater receive notifications and make actions by callback,
 * (in the current architecture).
 * not implemented in project, will be probably delete in the future.
 */
public class NotificationManager
{
    private Lock _mutexNotification;
    private List<Notification> _notification;
    private ServiceManager serviceManager;
    private List<INotificationListener> listeners;

    public void addListeners(INotificationListener listener)
    {
        listeners.add(listener);
    }

    private void createNotification(Notification notification)
    {
        for (INotificationListener listener : listeners)
            listener.Notify(notification);
    }

    public enum RegisterNotification
    {
        ExampleService,
        Test
    }

    public void Subscribe(INotificationListener notificationListener, RegisterNotification registerNotification)
    {
        switch (registerNotification) {
            case ExampleService:
                IService service = serviceManager.getResourceByName(ExampleIService.class.getSimpleName());
                if (service != null)
                    service.addListeners(notificationListener);
            case Test:
                //etc
        }
    }

    public NotificationManager(ServiceManager sm)
    {
        try {
            serviceManager = sm;
            listeners = new ArrayList<INotificationListener>();
            _mutexNotification = new ReentrantLock(true);
            _notification = new ArrayList<Notification>();
            //notificationManagerListener = new NotificationListenerServiceUpdater(this);
        } catch (Exception ex) {
            Log.getInstance().write(Level.SEVERE, ex.getMessage());
            for (StackTraceElement e : ex.getStackTrace())
            Log.getInstance().write(Level.SEVERE, e.toString());
            System.exit(-1);
        }
    }


    public void add(Notification notification) {
        try {
            _mutexNotification.lock();
            _notification.add(notification);
            _mutexNotification.unlock();
        } catch (Exception ex) {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }

    public void clearAll() {
        _mutexNotification.lock();
        _notification.clear();
        _mutexNotification.unlock();
    }

    public List<Notification> giveNotifications() {
        try {
            _mutexNotification.lock();
            List<Notification> save = _notification;
            List<Notification> _notification = new ArrayList<>();
            _mutexNotification.unlock();
            return (save);
        } catch (Exception ex) {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
            return (null);
        }
    }
}
