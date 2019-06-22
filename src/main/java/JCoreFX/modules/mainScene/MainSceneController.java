package JCoreFX.modules.mainScene;

import JCoreFX.core.manager.LayoutManager;
import JCoreFX.core.moduleConstruction.AModule;
import JCoreFX.core.moduleConstruction.AModuleController;
import javafx.stage.Stage;
import JCoreFX.services.layout.LayoutService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainSceneController extends AModuleController {

    private LayoutService _sceneService;

    @FXML
    private AnchorPane anchor;

    @FXML
    private void initialize() {
    }

    public <T extends AModule> void addPopUp(String title, int width, int height, T module)
    {
        Task<AModule> task = this._sceneService.getModuleRef(module.getClass().getSimpleName());
        task.setOnSucceeded(t -> {
            AModule lp = task.getValue();
            Stage popup = LayoutManager.createPopup(title, width, height, lp);
//            lp.setPopupRef(popup);
            popup.showAndWait();
        });
    }

    @Override
    protected void managersReady() {
        this._sceneService = this._serviceManager.getResourceByName(LayoutService.class.getSimpleName());
    }

    public void setRoot(BorderPane border) {
        this.anchor.getChildren().add(border);
        border.prefWidthProperty().bind(this.anchor.widthProperty());
        border.prefHeightProperty().bind(this.anchor.heightProperty());
    }
}
