package JCoreFX.core.moduleConstruction;

import JCoreFX.core.linkConstruction.DataLink;
import JCoreFX.core.manager.LinkManager;
import JCoreFX.core.manager.ServiceManager;
import JCoreFX.core.linkConstruction.Link;
import javafx.collections.ListChangeListener;

public abstract class AModuleController {

    protected LinkManager _linkManager;
    protected ServiceManager _serviceManager;

    public void setManagers(LinkManager lm, ServiceManager sm) {
        this._linkManager = lm;
        this._serviceManager = sm;
        this.managersReady();
    }

    protected abstract void managersReady();
}
