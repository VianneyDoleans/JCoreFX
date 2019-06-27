import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class MainTest {

    @Test
    public void simpleInitJCoreFX() throws InterruptedException {
        TestJavaFX testJavaFX = new TestJavaFX() {
            @Override
            public void unitTest(JCoreFX jCoreFX, Stage stage) {
                assertTrue(jCoreFX.init(stage));
            }
        };
        testJavaFX.run();
    }
}
