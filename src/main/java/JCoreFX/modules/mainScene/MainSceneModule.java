package JCoreFX.modules.mainScene;

import JCoreFX.core.manager.LinkManager;
import JCoreFX.core.manager.ServiceManager;
import JCoreFX.core.moduleConstruction.AModule;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainSceneModule extends AModule<MainSceneController> {

    public MainSceneModule(String path, LinkManager lm, ServiceManager sm) throws IOException {
        super(MainSceneModule.class.getSimpleName(), "layout", path, lm, sm);
    }

    public void setRoot(BorderPane border) {
        this._controller.setRoot(border);
    }

}
