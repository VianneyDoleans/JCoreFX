package JCoreFX.services.layout.tasks;

import javafx.concurrent.Task;

public class MoveToTask extends Task<Boolean> {
    protected Boolean _result = false;

    @Override protected Boolean call() throws Exception {
        System.out.println("Try call task");
        return this._result;
    }
}
