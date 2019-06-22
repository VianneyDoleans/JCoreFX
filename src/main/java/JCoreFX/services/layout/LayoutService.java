package JCoreFX.services.layout;

import JCoreFX.core.manager.LayoutManager;
import JCoreFX.core.moduleConstruction.AModule;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.core.serviceConstruction.ServiceState;
import javafx.concurrent.Task;

import java.util.function.Function;

public class LayoutService extends IService {

    private LayoutManager _sm;

    public LayoutService(LayoutManager sceneManager) {
        super(LayoutService.class.getSimpleName());

        this._sm = sceneManager;
    }

    @Override
    public ServiceState.State GetState() {
        return this._state.get();
    }

    @Override
    public boolean Restart() {
        return true;
    }

    @Override
    public boolean Stop() {
        return true;
    }

    /* Never call ? */
    @Override
    public void Start() {
    }

    public <T extends AModule> Task<T> getModuleRef(String name) {
        LayoutManager sm = this._sm;
        Task<T> task = new Task<T>() {
            @Override
            protected T call() {
                return sm.getResourceByName(name);
            }
        };

        task.run();
        return task;
    }

    /*
        Return Task to let the controller who call this service to handle by his way the task ? Or let the service all the control on it ?
    */
    public void moveTo(
            LayoutManager.POSITION position,
            AModule module,
            LayoutManager.MoveOptions options
    ) {
        LayoutManager sm = this._sm;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                sm.moveTo(position, module, options);
                return null;
            }
        };

        task.run();
    }

    public void moveToAndExec(
            LayoutManager.POSITION position,
            AModule module,
            LayoutManager.MoveOptions options,
            Function<Void, Void> callback
    ) {
        LayoutManager sm = this._sm;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                sm.moveTo(position, module, options);
                return null;
            }
        };

        task.setOnSucceeded(worker -> callback.apply(null));
        task.run();
    }
}
