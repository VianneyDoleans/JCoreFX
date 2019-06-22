import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class MainTest {
    @Test
    public void testSomeLibraryMethod() throws InterruptedException  {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        JCoreFX classUnderTest = new JCoreFX();

                        Pane root = new Pane();
                        Stage window = new Stage();
                        Scene scene = new Scene(root, 200, 200);

                        window.setTitle("test");
                        window.setScene(scene);
                        assertFalse("JCoreFX return 'true'", classUnderTest.init(window));
                        assertTrue("JCoreFX return 'true'", classUnderTest.init(window));
                        assertTrue("false", false);
                    }
                });
            }
        });
        thread.start();
        sleep(100);
    }
}
