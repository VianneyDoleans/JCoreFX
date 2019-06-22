package JCoreFX.core.manager;

import JCoreFX.core.log.Log;
import JCoreFX.core.notificationConstruction.INotificationListener;
import JCoreFX.core.notificationConstruction.Notification;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.core.serviceConstruction.ServiceState;
import javafx.util.Pair;
import JCoreFX.tools.ExceptionsCatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * <pre>
 * ServiceUpdater is the JCoreFX.core of the software.
 * It is executed at the beginning of the program.
 * Its part in the program is to guarantee that all the service are properly running.
 * Because of its part, the software cannot run without it.
 * When an specific action is done somewhere in the software (like a new service is added, or a error has occurred)
 * and a notification is generated,
 * ServiceUpdater will called methods with the corresponding annotation, in the specific order.
 *
 * For example, we can imagine that if an emergency error is occurred in the instantiation of the program,
 * a "OnCriticalError" notification can be generated.
 * All the methods in the programs with the annotation "OnCriticalError" will be called, in the specify order,
 * For example, each service can be stop with their methods "stop" in the specific order :
 *
 * "
 * public class Service1
 * {
 * #@OnCriticalError(order = 1)
 * stop() { // code }
 * }
 *
 * public class Service2
 * {
 * #@OnCriticalError(order = 2)
 * stop() { // code }
 * }"
 *
 * Then, all JCoreFX.services are restarted with their methods "start" in the specific order.
 * "
 * public class Service1
 * {
 * "@OnCriticalError(order = 3)
 * start() { // code }
 * }
 *
 * public class Service2
 * {
 * "@OnCriticalError(order = 4)
 * start() { // code }
 * }
 *
 * Information :
 * - Each methods of each JCoreFX.services stored in serviceManager are checked.
 * - In case of some methods have same number in order, passage order will be indeterminate between themselves.
 * - It's not a problem if some numbers are missing in the order (like for example, order : 0, 1, 5, 9),
 *      because method are sorted by order, then executed
 *
 *</pre>
 * @see Notification
 */
public class ServiceUpdater implements INotificationListener {
    private Lock _runMutex;
    private Boolean _run;
    private UpdateThread updateThread;
    //private NotificationManager _notificationManager;
    private ServiceManager _serviceManager;


    /**
     * look on each methods of JCoreFX.services, and run method which have the match annotation, run by order
     * @param mapAnnotation
     * @param notification
     * @param <T>
     */
    private <T extends  Annotation> void RetrieveAnnotation(Map<Pair<Method, IService>, Integer> mapAnnotation, Notification<T> notification)
    {
        for (IService service : _serviceManager.getServices()) {
            Method[] methods = service.getClass().getMethods();
            for (Method method : methods) {
                T annotation = method.getAnnotation(notification.getType());
                if (annotation != null) {
                    int order = -1;
                    Method GetOrder = null;
                    try {
                        GetOrder = annotation.annotationType().getMethod("order");
                        order = (int) GetOrder.invoke(annotation);
                        mapAnnotation.put(new Pair<>(method, service), order);
                    } catch (Exception e) {
                        ExceptionsCatch.PrintErrors(e);
                    }
                }
            }
        }
    }

    /**
     * sort methods by order
     * @param mapAnnotation
     * @return
     */
    private List<Map.Entry<Pair<Method, IService>, Integer>> LiSortAnnotationByOrder(Map<Pair<Method, IService>, Integer> mapAnnotation)
    {
        List<Map.Entry<Pair<Method, IService>, Integer>> mapEntryAnnotation = new LinkedList<>(mapAnnotation.entrySet());
        Collections.sort(mapEntryAnnotation, Comparator.comparingInt(Map.Entry::getValue));
        return (mapEntryAnnotation);
    }

    /**
     * run methods by order
     * @param mapEntryAnnotation
     */
    private void RunMethodInOrder(List<Map.Entry<Pair<Method, IService>, Integer>> mapEntryAnnotation)
    {
        for (Map.Entry<Pair<Method, IService>, Integer> element : mapEntryAnnotation) {
            try {
                element.getKey().getKey().invoke(element.getKey().getValue());
            } catch (Exception e) {
                ExceptionsCatch.PrintErrors(e);
            }
        }
    }

    /**
     * called when a service submit a notification to their subscribers (currently, only serviceUpdater is subscribed on JCoreFX.services)
     * it will called all methods with the specific annotation
     * @see Notification
     * @param notification
     * @param <T>
     */
    public <T extends  Annotation> void Notify(Notification<T> notification)
    {
        Log.getInstance().write(Level.INFO, "\"Service updater : I receive a notification, I will do something\"");

        Map<Pair<Method, IService>, Integer> mapAnnotation = new HashMap<>();
        RetrieveAnnotation(mapAnnotation, notification);
        List<Map.Entry<Pair<Method, IService>, Integer>> mapEntryAnnotation = LiSortAnnotationByOrder(mapAnnotation);
        RunMethodInOrder(mapEntryAnnotation);
    }

    /**
     * state of ServiceUpdater
     * @return Boolean
     */
    public Boolean GetRun() {
        _runMutex.lock();
        Boolean copy = _run;
        _runMutex.unlock();
        return copy;
    }

    /**
     * ServiceUpdater already do this now,
     * by callback when a notification of a service is generated (notify is called)
     * will probably be delete in the future
     *
     * Execute thread or stop it, depending of the Boolean parameter
     * it will check every service's state, and restart them if they are down.
     * @deprecated
     *
     * @param run
     * @throws InterruptedException
     */
    public void setRun(Boolean run) throws InterruptedException {
        _runMutex.lock();
        _run = run;
        _runMutex.unlock();
        if (GetRun())
        {
            updateThread = new UpdateThread();
            updateThread.start();
        }
        else
        {
            updateThread.join();
            Log.getInstance().write(Level.INFO, "Stop serviceUpdater");
        }
    }

    /**
     * Need ServiceManager to be instantiate
     * ServiceUpdater will add itself to listeners list of serviceManager, and receive a notification if a service is added.
     * Then, ServiceUpdater will add itself to listeners list of these new JCoreFX.services.
     * If these JCoreFX.services in the future send a notification, serviceUpdater will be notify, and can react
     * @see ServiceUpdater#notify()
     * @param serviceManager
     */
    public ServiceUpdater(ServiceManager serviceManager/*, NotificationManager notificationManager*/) {
        try {
           // _notificationManager = notificationManager;
            _runMutex = new ReentrantLock(true);
            _serviceManager = serviceManager;

            // this listeners will subscribe ServiceUpdater to each new service of serviceManager
            class NewServiceUpdater implements INotificationListener
            {
                private ServiceUpdater serviceUpdater;

                @Override
                public void Notify(Notification notification)
                {
                    notification.getService().addListeners(serviceUpdater);
                }

                public NewServiceUpdater(ServiceUpdater su)
                {
                    serviceUpdater = su;
                }
            }

            _serviceManager.addListeners(new NewServiceUpdater(this));

        } catch (Exception ex) {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }

    /**
     * @deprecated since program works with callback, so auto-managed,
     * it will probably be deleted in the future.
     *  Loop which verify if a service have an error state, and restart the service.
     */
    private class UpdateThread extends Thread {
        public void run() {
            while (GetRun()) {
                for (IService IService : _serviceManager.getServices()) {
                   if (IService.GetState() == ServiceState.State.ERROR) {
                        System.out.println("JCoreFX.core.serviceConstruction updater loop make a correction of a JCoreFX.core.serviceConstruction");
                        IService.Restart();
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    ExceptionsCatch.PrintErrors(ex);
                    System.exit(-1);
                }
            }
        }
    }
}
