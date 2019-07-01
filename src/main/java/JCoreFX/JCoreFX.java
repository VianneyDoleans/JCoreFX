package JCoreFX;

import JCoreFX.core.jsonConstruction.JSONFile;
import JCoreFX.core.log.Log;
import JCoreFX.core.manager.LayoutManager;
import JCoreFX.core.manager.LinkManager;
import JCoreFX.core.manager.ServiceManager;
import JCoreFX.core.manager.ServiceUpdater;
import JCoreFX.core.linkConstruction.Link;
import JCoreFX.core.moduleConstruction.AModule;
import JCoreFX.services.data.DataService;
import JCoreFX.services.layout.LayoutService;
import JCoreFX.tools.ExceptionsCatch;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

public class JCoreFX {

    private LayoutManager layoutManager;
    private ServiceManager serviceManager;
    //private NotificationManager notificationManager;
    private ServiceUpdater serviceUpdater;
    private LinkManager linkManager;
    private JSONFile config = new JSONFile("./configuration/globalConfiguration.json", JSONFile.JSONFileType.Object);

    private void initService() throws InterruptedException, IOException {
        this.serviceManager = new ServiceManager();
        //notificationManager = new NotificationManager(serviceManager);
        this.serviceUpdater = new ServiceUpdater(serviceManager);//, notificationManager);
        this.layoutManager = new LayoutManager();
        this.serviceManager.addResource(new DataService());
        this.serviceManager.addResource(new LayoutService((this.layoutManager)));
        this.serviceUpdater.setRun(true);
    }

    private void initConfig() {
        this.config.parse();
    }

    private void InitEventCloseScene(Stage stage) {
        stage.setOnCloseRequest(t -> {
            try {
                this.layoutManager.saveConfiguration(this.config.get("layout.configPath"));
                serviceUpdater.setRun(false);
                Log.getInstance().write(Level.INFO,"close JCoreFX");
                Log.getInstance().close();
            } catch (InterruptedException e) {
                ExceptionsCatch.PrintErrors(e);
            }
        });
    }

    public void initLink()
    {
        linkManager = new LinkManager();
    }

    public void addLink(Link link)
    {
        linkManager.addResource(link);
    }

    public void addLinks(Collection<Link> links)
    {
        linkManager.addResources(links);
    }

    public void addModule(AModule module)
    {
        this.layoutManager.addResource(module);
    }

    public void addModules(Collection<AModule> modules)
    {
        this.layoutManager.addResources(modules);
    }

    public void initDefaultLayoutModules(LayoutManager.RootDefaults defaults)
    {
        this.layoutManager.initRoot(this.config.get("layout.configPath"), defaults, serviceManager);
    }

    public boolean init(Stage stage) {
       try {
            initConfig();
            initLink();
            initService();
            this.layoutManager.initRoot(this.config.get("layout.configPath"), new LayoutManager.RootDefaults(), serviceManager);
            this.layoutManager.initScene(stage, this.config, this.linkManager, this.serviceManager);
            InitEventCloseScene(stage);
        } catch (Exception ex) {
            ExceptionsCatch.PrintErrors(ex);
            return false;
        }
       return true;
    }
}
