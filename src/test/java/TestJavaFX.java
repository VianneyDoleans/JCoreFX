import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class TestJavaFX {
    private boolean run = false;

    public abstract void unitTest(JCoreFX jCoreFX, Stage stage);

    private Stage initWindow() {
        Pane root = new Pane();
        System.out.println("Test still in progress");
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
                    System.out.println("wait");
                    this.wait();
                    System.out.println("wait done");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCloseWindow(Stage window) {
        window.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            System.out.println("Stage is closing");
            run = true;
            synchronized (TestJavaFX.this) {
                TestJavaFX.this.notify();
                System.out.println("notify");
            }
        });
    }

    private void initCloseRequestWindow(Stage window) {
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            System.out.println("Stage is closing");
            run = true;
            synchronized (TestJavaFX.this) {
                TestJavaFX.this.notify();
                System.out.println("notify");
            }
        });
    }

    public void run() {
        System.out.println("enter run");
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                System.out.println("Test in progress");
                JCoreFX jCoreFX = new JCoreFX();
                Stage window = initWindow();
                initCloseWindow(window);
                initCloseRequestWindow(window);
                try {
                    unitTest(jCoreFX, window);
                } finally {
                    window.close();
                }
                System.out.println("Test done");
            }
        });
        waitUI();
    }
}
