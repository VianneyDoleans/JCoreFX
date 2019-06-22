package JCoreFX.core.notificationConstruction;

import java.lang.annotation.Annotation;

/**
 * <pre>
 * class that need to subscribe (AddListener) to a service need to extends this method,
 * certifying that Notify method exist
 * Have to do it for receiving notification
 * </pre>
 * @see Notification
 * @see JCoreFX.core.serviceConstruction.IService
 */
public interface INotificationListener
{
    /**
     *
     * @param notification
     * @param <T>
     */
    <T extends Annotation> void Notify(Notification<T> notification);
}
