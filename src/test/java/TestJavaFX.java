import JCoreFX.JCoreFX;
import JCoreFX.core.log.Log;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.logging.Level;

public abstract class TestJavaFX {
    private boolean run = false;

    private AssertionError error;
    public abstract void unitTest(JCoreFX jCoreFX, Stage stage);

    public AssertionError getError()
    {
        return error;
    }

    private Stage initWindow() {
        Pane root = new Pane();
        Stage window = new Stage();
        Scene scene = new Scene(root, 200, 200);

        window.setTitle("test");
        window.setScene(scene);
        window.show();
        return window;
    }

    private void waitUI() {
        while (!run) {
            try {
                synchronized (this) {
                    Log.getInstance().write(Level.INFO,"wait for end of UI");
                    this.wait();
                    Log.getInstance().write(Level.INFO,"received a notification, end of wait UI");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCloseWindow(Stage window) {
        window.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            Log.getInstance().write(Level.INFO,"Stage is closing");
            run = true;
            synchronized (TestJavaFX.this) {
                TestJavaFX.this.notify();
                Log.getInstance().write(Level.INFO,"Notify the end of UI");
            }
        });
    }

    private void initCloseRequestWindow(Stage window) {
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Log.getInstance().write(Level.INFO,"Stage is closing by request");
            run = true;
            synchronized (TestJavaFX.this) {
                TestJavaFX.this.notify();
                Log.getInstance().write(Level.INFO,"Notify the end of UI");
            }
        });
    }

    public void run() {
        Log.getInstance().write(Level.INFO, "Run of JavaFX's test");
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                Log.getInstance().write(Level.INFO,"Test in progress");
                JCoreFX jCoreFX = new JCoreFX();
                Stage window = initWindow();
                initCloseWindow(window);
                initCloseRequestWindow(window);
                try {
                    unitTest(jCoreFX, window);
                }
                catch (AssertionError e)
                {
                    error = e;
                }
                finally {
                    Log.getInstance().write(Level.INFO,"Test done");
                    Log.getInstance().write(Level.INFO,"Ask for close Test's window");
                    window.close();
                }
            }
        });
        waitUI();
    }
}
