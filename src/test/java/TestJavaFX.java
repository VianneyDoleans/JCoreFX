import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.Test;

public abstract class TestJavaFX
{
    private boolean run = false;

    public abstract void unitTest(JCoreFX jCoreFX, Stage stage);

    private Stage initWindow()
    {
        Pane root = new Pane();
        System.out.println("Test still in progress");
        Stage window = new Stage();
        Scene scene = new Scene(root, 200, 200);

        window.setTitle("test");
        window.setScene(scene);
        window.show();
        return window;
    }

    private void waitUI()
    {
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

    private void initCloseWindow(Stage window)
    {
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                run = true;
                synchronized (TestJavaFX.this) {
                    TestJavaFX.this.notify();
                    System.out.println("notify");
                }
            }
        });
    }

    @Test
    public void run() throws InterruptedException {
        Platform.startup(new Runnable() {

            @Override
            public void run() {
                System.out.println("Test in progress");
                JCoreFX jCoreFX = new JCoreFX();
                Stage window = initWindow();
                initCloseWindow(window);
                unitTest(jCoreFX, window);
                System.out.println("Test done");
            }
        });
        waitUI();
    }
}
