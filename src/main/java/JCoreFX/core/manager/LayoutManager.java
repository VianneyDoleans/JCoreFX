package JCoreFX.core.manager;

import JCoreFX.core.jsonConstruction.JSONFile;
import JCoreFX.core.log.Log;
import JCoreFX.core.managerConstruction.AManager;
import JCoreFX.core.moduleConstruction.AModule;
import JCoreFX.services.data.DataService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.*;
import JCoreFX.modules.mainScene.MainSceneModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import JCoreFX.tools.ExceptionsCatch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * LayoutManager is used for managing the JCoreFX.modules in software's mainScene.
 * Layout is divided in 5 containers : Top, Bottom, Right, Left, Center.
 * Each JCoreFX.modules can be in one of these containers.
 * In case there is already a module in destination container, the module will be add in a tabulation.
 * It's possible to split a container, multiple times, for displaying some JCoreFX.modules in the mainScene at the same time.
 * Currently, actions can only be done by context menu (right click).
 * @see AModule
 */
public class LayoutManager extends AManager<AModule> {

    public class Container<T extends Region> {
        public String type;
        public T ref;
        protected ArrayList<AModule> _modules = new ArrayList<>();

        public Container(T container) {
            this.ref = container;
            this.type = container.getClass().getSimpleName();
        }

        protected <T extends AModule> void addModule(T module) {
            this._modules.add(module);
        }

        protected void removeModule(String name) {
            this._modules.removeIf(item -> item.getName().equals(name));
        }

    }

    public class TabContainer extends Container<TabPane> {
        private ArrayList<Tab> _tabs = new ArrayList<>();

        public TabContainer(TabPane container) {
            super(container);
        }

        /**
         * Push the module at the end of the tabs
         */

        public void pushModule(AModule module) {
            super.addModule(module);
            this._tabs.add(new Tab(module.tabName, module.getFXML()));
            this.ref.getTabs().setAll(this._tabs);
        }

        /**
         * Pop the tab who contains the module in parameters
         */

        public boolean popModule(AModule module) {
            super.removeModule(module.getName());
            boolean result = this._tabs.removeIf(item -> item.getText().equals(module.tabName));

            if (result) {
                this.ref.getTabs().setAll(this._tabs);
            }
            return result;
        }

        /**
         * Pop the last tab
         */

        public void popLast() {
            if (this._tabs.size() == 0) {
                return;
            }
            Tab last = this._tabs.get(this._tabs.size() - 1);
            this._modules.removeIf(item -> item.tabName.equals(last.getText()));
            this._tabs.remove(last);
            this.ref.getTabs().setAll(this._tabs);
        }

        /**
         * Add the module to the start of the tabs
         */

        public void addModuleToBeginning(AModule module) {
            super.addModule(module);
            this._tabs.add(0, new Tab(module.tabName, module.getFXML()));
            this.ref.getTabs().setAll(this._tabs);
        }

        public boolean isEmpty() {
            return this._tabs.isEmpty();
        }
    }

    public class SplitContainer extends Container<SplitPane> {
        private Container _left;
        private Container _right;

        public SplitContainer(SplitPane container) {
            super(container);

            this._left = new TabContainer(new TabPane());
            this.ref.getItems().add(this._left.ref);
        }

        /**
         * Add module to the split container
         */

        public void addModuleTo(SPLIT_POSITION position, AModule module, MoveOptions options) {
            if (options.splitOrientation != this.ref.getOrientation()) {
                this.ref.setOrientation(options.splitOrientation);
            }
            if (position == SPLIT_POSITION.LEFT) {
                this._left = this.checkContainerBuild(this._left);
                this.addModuleToContainer(this._left, position, module, options);
            } else {
                this._right = this.checkContainerBuild(this._right);
                this.addModuleToContainer(this._right, position, module, options);
            }
        }

        public boolean removeModule(AModule module) {
            return this.removeModuleToContainer(this._left, this._right, module);
        }

        private boolean removeModuleToContainer(Container left, Container right, AModule module) {
            if (left.type.equals(TabPane.class.getSimpleName())) {
                TabContainer tabs = (TabContainer) left;
                boolean result = tabs.popModule(module);

                if (!result && right != null) {
                    return this.removeModuleToContainer(right, null, module);
                }
                if (tabs.isEmpty()) {
                    this.removeTabContainer(tabs);
                }
                return result;
            } else {
                SplitContainer split = (SplitContainer) left;
                boolean result = split.removeModule(module);

                if (!result && right != null) {
                    return this.removeModuleToContainer(right, null, module);
                }
                return result;
            }
        }

        private void addModuleToContainer(Container c, SPLIT_POSITION position, AModule module, MoveOptions options) {
            if (c.type.equals(TabPane.class.getSimpleName())) {
                TabContainer tabs = (TabContainer) c;

                tabs.pushModule(module);
            } else {
                SplitContainer split = (SplitContainer) c;

                split.addModuleTo(position, module, options);
            }
        }

        private Container checkContainerBuild(Container c) {
            if (c == null) {
                c = new TabContainer(new TabPane());
                this.ref.getItems().add(c.ref);
                return c;
            } else {
                return c;
            }
        }

        private void removeTabContainer(TabContainer tabs) {
            if (this._left == tabs) {
                this.ref.getItems().remove(this._left.ref);
                this._left = null;
            } else if (this._right == tabs) {
                this.ref.getItems().remove(this._right.ref);
                this._right = null;
            }
        }
    }

    public class BorderContainer extends Container<BorderPane> {

        private SplitContainer _topSplit;
        private SplitContainer _bottomSplit;
        private SplitContainer _leftSplit;
        private SplitContainer _rightSplit;
        private SplitContainer _centerSplit;

        public BorderContainer(BorderPane container) {
            super(container);

            this._topSplit = new SplitContainer(new SplitPane());
            this.ref.setTop(this._topSplit.ref);
            this._bottomSplit = new SplitContainer(new SplitPane());
            this.ref.setBottom(this._bottomSplit.ref);
            this._leftSplit = new SplitContainer(new SplitPane());
            this.ref.setLeft(this._leftSplit.ref);
            this._rightSplit = new SplitContainer(new SplitPane());
            this.ref.setRight(this._rightSplit.ref);
            this._centerSplit = new SplitContainer(new SplitPane());
            this.ref.setCenter(this._centerSplit.ref);
        }

        public <T extends AModule> void addModuleTo(POSITION position, T module, MoveOptions options) {
            this.getSplitFrom(position).addModuleTo(
                    options.splitMode ? SPLIT_POSITION.RIGHT : options.splitTo,
                    module,
                    options
            );
        }

        public <T extends AModule> void removeModuleTo(POSITION position, T module) {
            this.getSplitFrom(position).removeModule(module);
        }

        private SplitContainer getSplitFrom(POSITION position) {
            SplitContainer split;

            switch (position) {
                case TOP: {
                    split = this._topSplit;
                    break;
                }
                case BOTTOM: {
                    split = this._bottomSplit;
                    break;
                }
                case LEFT: {
                    split = this._leftSplit;
                    break;
                }
                case RIGHT: {
                    split = this._rightSplit;
                    break;
                }
                case CENTER: {
                    split = this._centerSplit;
                    break;
                }
                default: {
                    split = this._centerSplit;
                    break;
                }
            }
            return split;
        }

    }

    public enum SPLIT_POSITION {
        LEFT,
        RIGHT
    }

    public enum POSITION {
        TOP,
        BOTTOM,
        CENTER,
        LEFT,
        RIGHT
    }

    public class MoveEvent {
        public boolean isAlone = false;
        public boolean isSplit = false;
        public Container parent = null;
    }

    public static class RootDefaults {
        public AModule defaultBottom;
        public AModule defaultLeft;
        public AModule defaultRight;
        public AModule defaultCenter;

        public RootDefaults() {
        }

        public RootDefaults(
                AModule defaultBottom,
                AModule defaultLeft,
                AModule defaultRight,
                AModule defaultCenter
        ) {
            this.defaultBottom = defaultBottom;
            this.defaultLeft = defaultLeft;
            this.defaultRight = defaultRight;
            this.defaultCenter = defaultCenter;
        }
    }

    public static class MoveOptions {
        // If the move is a call to split the container
        public boolean splitMode = false;
        // Set the orientation of the split
        public Orientation splitOrientation = Orientation.HORIZONTAL;
        // Set the position in the split where the component will be
        public SPLIT_POSITION splitTo = SPLIT_POSITION.LEFT;

        /**
         * By default, just move a component to another place
         */
        public MoveOptions() {
        }

        public MoveOptions(
                boolean splitMode,
                Orientation splitOrientation,
                SPLIT_POSITION splitTo
        ) {
            this.splitMode = splitMode;
            this.splitOrientation = splitOrientation;
            this.splitTo = splitTo;
        }
    }

    private MainSceneModule _Main_sceneModule;
    private BorderContainer _root;

    /**
     * First method to call to create the layout of the JCoreFXSoftware
     * Setup all the containers and the default places of the JCoreFX.modules from the configuration
     * @param path
     * @param defaults
     */
    public void initRoot(String path, RootDefaults defaults, ServiceManager serviceManager) {
        this._root = new BorderContainer(new BorderPane());
        this.buildMenuBar(serviceManager);
        try {
            this.loadConfiguration(path);
        } catch (Exception e) {
            Log.getInstance().write(Level.WARNING, "Error while loading the configuration path : " + path);
            Log.getInstance().write(Level.INFO, "Loading default configuration");
            this.checkDefault(POSITION.BOTTOM, defaults.defaultBottom);
            this.checkDefault(POSITION.LEFT, defaults.defaultLeft);
            this.checkDefault(POSITION.RIGHT, defaults.defaultRight);
            this.checkDefault(POSITION.CENTER, defaults.defaultCenter);
        }
    }
    /**
     * Second method to call to create the layout of the JCoreFXSoftware
     * Call the MainSceneModule who is the main module to display the software
     * @param stage
     * @param config
     * @param lm
     * @param sm
     * @throws IOException
     */
    public void initScene(Stage stage, JSONFile config, LinkManager lm, ServiceManager sm) throws IOException {
        double minWidth = Double.parseDouble(config.get("window.minWidth"));
        double minHeight = Double.parseDouble(config.get("window.minHeight"));
        double offset = Double.parseDouble(config.get("window.offset"));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        this._Main_sceneModule = new MainSceneModule("/views/MainScene.fxml", lm, sm);
        this._Main_sceneModule.setRoot(this._root.ref);
        stage.setScene(
                new Scene(
                        this._Main_sceneModule.getFXML(),
                        primaryScreenBounds.getWidth() - offset,
                        primaryScreenBounds.getHeight() - offset
                )
        );
        stage.setTitle(config.get("name"));
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.show();
    }

    public <T extends AModule> void moveTo(POSITION position, T module, MoveOptions options) {
        MoveEvent m = new MoveEvent();

        if (module.currentPosition != null) {
            this._root.removeModuleTo(module.currentPosition, module);
        }
        this._root.addModuleTo(position, module, options);
        module.hasMoved(position, m);
    }

    public static Stage createPopup(String title, double width, double height, AModule module) {
        Stage popupwindow = new Stage();
        Scene scene1 = new Scene(module.getFXML(), width, height);

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(title);
        popupwindow.setScene(scene1);
        return popupwindow;
    }

    public void saveConfiguration(String path) {
        JSONObject root = new JSONObject();

        root.put("border", this.saveBorder());
        try (FileWriter file = new FileWriter(path)) {
            file.write(root.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDefault(POSITION position, AModule module) {
        if (module != null) {
            this.moveTo(position, module, new MoveOptions());
        }
    }

    private void openMenu(DataService dataService, MenuItem open)
    {
        open.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                try {
                    dataService.loadData(selectedFile.getAbsolutePath());
                }
                catch (Exception e) {
                    ExceptionsCatch.PrintErrors(e);
                }
            }
        }});
    }

    private void saveAsMenu(DataService dataService, MenuItem saveAs)
    {
        saveAs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedFile = directoryChooser.showDialog(null);

                if (selectedFile != null)
                {
                    try {
                        dataService.saveData(selectedFile.getAbsolutePath());
                    }
                    catch (Exception e) {
                        ExceptionsCatch.PrintErrors(e);
                    }
                }
            }});
    }

    private void buildMenuBar(ServiceManager serviceManager) {
        MenuBar menuBar = new MenuBar();
        MenuItem saveAs = new MenuItem("Save as...");
        MenuItem open = new MenuItem("Open");

        Menu menuFile = new Menu("File");
        //menuFile.getItems().add(new MenuItem("New"));
        openMenu(serviceManager.getResourceByName(DataService.class.getSimpleName()), open);
        menuFile.getItems().add(open);
        //menuFile.getItems().add(new MenuItem("Save"));
        saveAsMenu(serviceManager.getResourceByName(DataService.class.getSimpleName()), saveAs);
        menuFile.getItems().add(saveAs);
        //Menu menuEdit = new Menu("Edit");
        //menuEdit.getItems().add(new MenuItem("Find file"));
        //menuEdit.getItems().add(new MenuItem("Test"));
        //Menu menuView = new Menu("View");
        //menuView.getItems().add(new MenuItem("Settings"));
        menuBar.getMenus().addAll(menuFile);//, menuEdit, menuView);
        this._root.ref.setTop(menuBar);
    }

    private JSONObject saveBorder() {
        JSONObject border = new JSONObject();

        border.put("left", this.saveSplit(this._root._leftSplit));
        border.put("right", this.saveSplit(this._root._rightSplit));
        border.put("bottom", this.saveSplit(this._root._bottomSplit));
        border.put("center", this.saveSplit(this._root._centerSplit));
        return border;
    }

    private JSONObject saveSplit(SplitContainer splitContainer) {
        JSONObject split = new JSONObject();

        if (splitContainer._left != null && splitContainer._left.getClass() == TabContainer.class) {
            split.put("left", this.saveTabs((TabContainer)splitContainer._left));
        }
        if (splitContainer._right != null && splitContainer._right.getClass() == TabContainer.class) {
            split.put("right", this.saveTabs((TabContainer)splitContainer._right));
        }
        split.put("orientation", splitContainer.ref.getOrientation().toString());
        return split;
    }

    private JSONArray saveTabs(TabContainer tabContainer) {
        JSONArray tabs = new JSONArray();

        tabContainer._modules.forEach(m -> tabs.add(m.getName()));
        return tabs;
    }

    private void loadConfiguration(String path) {
        System.out.println(path);
        JSONFile jsonFile = new JSONFile(path, JSONFile.JSONFileType.Object);
        jsonFile.parse();
        JSONObject border = jsonFile.get("border");

        this.loadSplit((JSONObject)border.get("bottom"), POSITION.BOTTOM);
        this.loadSplit((JSONObject)border.get("left"), POSITION.LEFT);
        this.loadSplit((JSONObject)border.get("right"), POSITION.RIGHT);
        this.loadSplit((JSONObject)border.get("center"), POSITION.CENTER);
    }

    private void loadSplit(JSONObject splitJSON, POSITION position) {
        System.out.println(splitJSON.get("orientation"));
        Orientation orientation = splitJSON.get("orientation").toString().equals("VERTICAL") ?
                Orientation.VERTICAL : Orientation.HORIZONTAL;

        System.out.println(position.toString());
        System.out.println(orientation.toString());
        if (splitJSON.get("left") != null) {
            this.loadTabs((JSONArray)splitJSON.get("left"), position, SPLIT_POSITION.LEFT, orientation);
        }
        if (splitJSON.get("right") != null) {
            this.loadTabs((JSONArray)splitJSON.get("right"), position, SPLIT_POSITION.RIGHT, orientation);
        }
    }

    private void loadTabs(JSONArray tabsJSON, POSITION position, SPLIT_POSITION splitPosition, Orientation orientation) {
        if (tabsJSON != null) {
            tabsJSON.forEach(module -> {
                AModule moduleRef = this.getResourceByName(module.toString());
                MoveOptions options = new MoveOptions();

                options.splitOrientation = orientation;
                options.splitTo = splitPosition;
                this.moveTo(position, moduleRef, options);
            });
        }
    }
}