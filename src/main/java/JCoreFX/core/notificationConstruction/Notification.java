package JCoreFX.core.notificationConstruction;

import JCoreFX.core.log.Log;
import JCoreFX.core.serviceConstruction.IService;

import java.lang.annotation.Annotation;
import java.util.logging.Level;

/**
 * <pre>
 * Notification used by JCoreFX.services when something need to be communicate with ServiceUpdater (JCoreFX.core)
 * it composed of :
 *          a message (only used for logs or display information to user)
 *          a type (annotation), which represent the current state (like "OnCriticalError" or "AddService" annotations)
 *          a service (the caller, which generate this notification)
 * </pre>
 * @see JCoreFX.core.manager.ServiceUpdater
 * @see IService
 * @param <T>
 */
public class Notification<T extends Annotation> {
    private String _message;
    private Class<T> _type;
    private IService _I_service;


    /**
     *
     * @return
     */
    public IService getService() {
        return (_I_service);
    }

    /**
     *
     * @return
     */
    public Class<T> getType() {
        return _type;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return _message;
    }

    /**
     *
     * @param IService
     * @param message
     * @param state
     */
    public Notification(IService IService, String message, Class<T> state) {
        try {
            _I_service = IService;
            _message = message;
            _type = state;
        } catch (Exception ex) {
            Log.getInstance().write(Level.SEVERE, ex.getMessage());
            for (StackTraceElement e : ex.getStackTrace())
                Log.getInstance().write(Level.SEVERE, e.toString());
            System.exit(-1);
        }
    }
}
