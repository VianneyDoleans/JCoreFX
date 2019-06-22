package JCoreFX.core.serviceConstruction;

import JCoreFX.tools.ExceptionsCatch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * State of service, can be used for example by ServiceUpdater's invoke to do a specific action,
 * depending of this state.
 * @see JCoreFX.core.manager.ServiceUpdater
 * @see IService
 */
public class ServiceState
{
    public enum State
    {
        ERROR,
        WAITING,
        WORK,
        STOP
    }

    private Lock _mutex;
    private State _state;

    public void set(State state)
    {
        _mutex.lock();
        _state = state;
        _mutex.unlock();
    }

    public State get()
    {
        State copy;
        _mutex.lock();
        copy = _state;
        _mutex.unlock();
        return (copy);
    }

    /*
        Using lock and unlock to handle mutex and states, but is it really usefull when JavaFX has tasks ?
     */
    public void lock() {
        this._mutex.lock();
        this.set(State.WORK);
    }

    public void unlock() {
        this._mutex.unlock();
        this.set(State.WAITING);
    }

    public ServiceState()
    {
        try
        {
            _mutex = new ReentrantLock(true);
            _state = State.WORK;
        }
        catch (Exception ex)
        {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }
}
