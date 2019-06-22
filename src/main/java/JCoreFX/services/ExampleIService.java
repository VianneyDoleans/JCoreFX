package JCoreFX.services;

import JCoreFX.core.serviceConstruction.annotationsServices.OnCriticalError;
import JCoreFX.core.log.Log;
import JCoreFX.core.notificationConstruction.Notification;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.core.serviceConstruction.ServiceState;
import JCoreFX.tools.ExceptionsCatch;

import java.util.Random;
import java.util.logging.Level;

public class ExampleIService extends IService {
    private TestThread _thread;
    private ServiceState _state;
    private boolean _run;

    private class TestThread extends Thread
    {
        Random rand;

        TestThread()
        {
            Log.getInstance().write(Level.INFO, "creation of TestThread");
            try
            {
                rand = new Random();
            }
            catch (Exception ex)
            {
                ExceptionsCatch.PrintErrors(ex);
                System.exit(-1);
            }
        }

        @Override
        public void run()
        {
            _state.set(ServiceState.State.WORK);
            Log.getInstance().write(Level.INFO,"exampleService : start to work");
            while (_run)
            {
                if (rand.nextInt() % 10 <= 3 && _state.get() != ServiceState.State.ERROR)
                {
                    Log.getInstance().write(Level.INFO, "exampleService : I'm broken");
                    _state.set(ServiceState.State.ERROR);
                    Log.getInstance().write(Level.INFO, "exampleService : I send a notification");
                    Notification notification = new Notification<>(ExampleIService.this,
                            "Error of Example JCoreFX.core.serviceConstruction", OnCriticalError.class);
                    createNotification(notification);
                }
                try
                {
                    int time = 500;
                    sleep(time);
                    Log.getInstance().write(Level.INFO, "exampleService : I'm still running");
                }
                catch (Exception ex)
                {
                    ExceptionsCatch.PrintErrors(ex);
                    System.exit(-1);
                }
            }
        }
    }

    public ExampleIService()
    {
        super(ExampleIService.class.getSimpleName());
        _state = new ServiceState();
    }

    public boolean Stop()
    {
        Log.getInstance().write(Level.INFO, "exampleService : Stop");
        _run = false;
        try
        {
            _thread.join();
        }
        catch (Exception ex)
        {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
        Log.getInstance().write(Level.INFO, "ExampleIService stop.");
        return (true);
    }

    public ServiceState.State GetState()
    {
        return _state.get();
    }

    @OnCriticalError(order = 2)
    public void doSomething2()
    {
        System.out.println("doSomething 2");
    }

    @OnCriticalError(order = 1)
    public void doSomething()
    {
        System.out.println("dosomething 1");
    }

    @OnCriticalError
    public boolean Restart()
    {
        Log.getInstance().write(Level.INFO, "Example JCoreFX.core.serviceConstruction : I restart");
        _state.set(ServiceState.State.WORK);
        return (true);
    }

    public  void Start()
    {
        try {
            _thread = new TestThread();
            _run = true;
            _thread.start();
        }
        catch (Exception ex)
        {
            ExceptionsCatch.PrintErrors(ex);
            System.exit(-1);
        }
    }
}
