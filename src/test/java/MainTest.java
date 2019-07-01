import JCoreFX.JCoreFX;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class MainTest {

    @BeforeClass
    public static void initToolkit() {
        Platform.startup(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public void throwError(AssertionError error)
    {
        if (error != null)
            throw error;
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
        throwError(testJavaFX.getError());
    }

    @Test
    public void simpleInitJCoreFX2() {
        TestJavaFX testJavaFX = new TestJavaFX() {
            @Override
            public void unitTest(JCoreFX jCoreFX, Stage stage) {
                assertFalse(jCoreFX.init(stage));
            }
        };
        testJavaFX.run();
        throwError(testJavaFX.getError());
    }

    @Test
    public void simpleInitJCoreFX3() {
        TestJavaFX testJavaFX = new TestJavaFX() {
            @Override
            public void unitTest(JCoreFX jCoreFX, Stage stage) {
                assertTrue(jCoreFX.init(stage));
            }
        };
        testJavaFX.run();
        throwError(testJavaFX.getError());
    }

    @Test
    public void simpletest() {
        assertTrue(true);
    }
}
