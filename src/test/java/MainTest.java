import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class MainTest {

    @BeforeClass
    public static void initToolkit() throws InterruptedException
    {
        Platform.startup(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Test
    public void simpleInitJCoreFX() {
        TestJavaFX testJavaFX = new TestJavaFX() {
            @Override
            public void unitTest(JCoreFX jCoreFX, Stage stage) {
                assertTrue(jCoreFX.init(stage));
            }
        };
        testJavaFX.run();
    }

    @Test
    public void simpleInitJCoreFX2() {
        TestJavaFX testJavaFX = new TestJavaFX() {
            @Override
            public void unitTest(JCoreFX jCoreFX, Stage stage) {
                jCoreFX.init(stage);
                assertTrue(true);
            }
        };
        testJavaFX.run();
    }
}
