package JCoreFX.core.moduleConstruction;

import JCoreFX.core.manager.LayoutManager;
import JCoreFX.core.manager.LinkManager;
import JCoreFX.core.manager.ServiceManager;
import JCoreFX.core.managerConstruction.AManagerItem;
import JCoreFX.services.layout.LayoutService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Modules are composed of a graphic (FXML) and a backend (Controller)
 * Controllers are extended of this class
 * Modules are the parts that composed the software.
 * You have the timeline module, file explorer module, etc...
 *
 * Modules are independent, and can work without the existence of another module.
 * Because Modules communicate themselves with link,
 *
 * it's possible to switch a module by another, and the software still works.
 * Don't have to change other part of the program each time a JCoreFX.modules is exchanged/modified (software is modular).
 *
 * Modules use JCoreFX.services to do generic actions (like use networkSimulation service for making a request),
 * So, when a new module is programming, have to check on JCoreFX.services for a generic action.
 * If this generic action is not already done by a service
 * (like use networkSimulation service for request, or use sound service to make a sound when click on a button),
 * have to create a service for it, instead of implement it inside the module.
 * @see JCoreFX.core.linkConstruction.Link
 * @see JCoreFX.core.serviceConstruction.IService
 *
 * @param <Controller>
 */
public abstract class AModule<Controller extends AModuleController> extends AManagerItem {

    protected Parent _fxml;
    protected Controller _controller;
    protected LayoutManager.Container _parent;
    protected LayoutService _sceneService;
    public LayoutManager.POSITION currentPosition;
    public String tabName;

    public AModule(String name, String tabName, String path, LinkManager lm, ServiceManager sm) throws IOException {
        super(name);

        System.out.println(path);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        this._fxml = loader.load();
        this._controller = loader.getController();
        this._controller.setManagers(lm, sm);
        this.tabName = tabName;
        this._sceneService = sm.getResourceByName(LayoutService.class.getSimpleName());
        Pane root = (Pane) this._fxml.lookup("#root");
        if (root != null) {
            this.setupContextMenu(root);
        }
    }

    public Parent getFXML() {
        return this._fxml;
    }

    public Controller getController() {
        return this._controller;
    }

    /*
        LifeCycles
        Feel free to override this methods (Call the super ofc) to add features to your JCoreFX.modules
     */

    public void hasMoved(LayoutManager.POSITION position, LayoutManager.MoveEvent event) {
        this.currentPosition = position;
        this._parent = event.parent;
    }

    /*
        Method who setup the context menu of every module, the module need a Pane with the id 'root'
        Feel free to override this method to add some feature to the context menu of your module
     */

    protected void setupContextMenu(Pane root) {
        root.setOnContextMenuRequested(e -> {
            ContextMenu m = new ContextMenu();

            if (this.currentPosition != LayoutManager.POSITION.BOTTOM) {
                m.getItems().add(
                        this.buildMenuMoveItem(
                                "Move to bottom",
                                LayoutManager.POSITION.BOTTOM,
                                this.getClass(),
                                new LayoutManager.MoveOptions()
                        )
                );
            }
            if (this.currentPosition != LayoutManager.POSITION.LEFT) {
                m.getItems().add(
                        this.buildMenuMoveItem(
                                "Move to left",
                                LayoutManager.POSITION.LEFT,
                                this.getClass(),
                                new LayoutManager.MoveOptions()
                        )
                );
            }
            if (this.currentPosition != LayoutManager.POSITION.RIGHT) {
                m.getItems().add(
                        this.buildMenuMoveItem(
                                "Move to right",
                                LayoutManager.POSITION.RIGHT,
                                this.getClass(),
                                new LayoutManager.MoveOptions()
                        )
                );
            }
            if (this.currentPosition != LayoutManager.POSITION.CENTER) {
                m.getItems().add(
                        this.buildMenuMoveItem(
                                "Move to center",
                                LayoutManager.POSITION.CENTER,
                                this.getClass(),
                                new LayoutManager.MoveOptions()
                        )
                );
            }
            m.getItems().add(this.buildSubMenuSplit("Split vertical with...", Orientation.HORIZONTAL));
            m.getItems().add(this.buildSubMenuSplit("Split horizontal with...", Orientation.VERTICAL));
            m.show(root, e.getScreenX(), e.getScreenY());
        });
    }

    protected Menu buildSubMenuSplit(String label, Orientation splitOrientation) {
        Menu subSplit = new Menu(label);

        return subSplit;
    }

    protected <T extends AModule> MenuItem buildMenuMoveItem(
            String label,
            LayoutManager.POSITION position,
            Class<T> cls,
            LayoutManager.MoveOptions options
    ) {
        AModule self = this;
        MenuItem item = new MenuItem(label);

        item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (cls.equals(self.getClass())) {
                    self.callMoveTo(position, self, options);
                } else {
                    Task<T> task = self._sceneService.getModuleRef(cls.getSimpleName());
                    task.setOnSucceeded(value -> self.callMoveTo(position, task.getValue(), options));
                }
            }
        });
        return item;
    }

    private void callMoveTo(LayoutManager.POSITION position, AModule module, LayoutManager.MoveOptions options) {
        this._sceneService.moveTo(position, module, options);
    }

    private boolean isCurrentClass(Class cls) {
        return cls.getSimpleName().equals(this.getClass().getSimpleName());
    }
}
