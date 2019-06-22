package JCoreFX.services.data;

import JCoreFX.core.dataFile.Scene;
import JCoreFX.core.serviceConstruction.IService;
import JCoreFX.core.serviceConstruction.ServiceState;
import JCoreFX.core.dataFile.DataCenter;
import JCoreFX.core.dataFile.DataFiles.DataFilesManager;

import java.util.List;

public class DataService extends IService {

    private DataFilesManager _dataFilesManager;
    private DataCenter _data;
    private Scene _selectedScene;

    private static int _id = 0;
    private static int _order = 0;

    //TODO : need to make it inside getScenes.add() @override/heritage list<Scene> like listScene<Scene>
    public int generateOrder()
    {
        while (getSceneByOrder(_order) != null)
            _order += 1;
        return _order;
    }

    //TODO : need to be remove for a better system later
    private boolean checkExistIdScenes(String id)
    {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getId().equals(id))
                return false;
        }
        return true;
    }

    public String generateId()
    {
        _id += 1;
        //TODO : problem if a node have a generated Id, but is not register in DataService
        // ALL ID have to be register in a list ?
        while (!(getNodeFromAllScenes(Integer.toString(_id)) == null
                && checkExistIdScenes(Integer.toString(_id)))) {
            _id += 1;
        }
        return Integer.toString(_id);
    }

    public Scene getSceneByOrder(int order)
    {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getOrder() == order)
                return scene;
        }
        return null;
    }

    public Scene.Node getNodeFromAllScenes(String id)
    {
        for (Scene scene : _data.getScenes())
        {
            Scene.Node node = scene.getNode(id);
            if (node != null)
                return node;
        }
        return null;
    }

    public DataService()
    {
        super(DataService.class.getSimpleName());
        _dataFilesManager = new DataFilesManager();
        _data = new DataCenter();
        _state.set(ServiceState.State.WAITING);
    }

    @Override
    public ServiceState.State GetState() {
        return _state.get();
    }

    public Scene getSelectedScene()
    {
        return _selectedScene;
    }

    private Scene findScene(String id)
    {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getId().equals(id))
                return scene;
        }
        return null;
    }

    public void removeScene(String id)
    {
        Scene scene = findScene(id);
        removeScene(scene);
    }

    public void removeScene(Scene scene)
    {
        _data.getScenes().remove(scene);
    }

    public void setSelectedScene(int order)
    {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getOrder() == order)
            {
                _selectedScene = scene;
                break;
            }
        }
    }

    public void setSelectedScene(String id)
    {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getId().equals(id))
            {
                _selectedScene = scene;
                break;
            }
        }
    }

    public List<Scene> getScenes() { return _data.getScenes(); }

    public Scene getScene(String id) {
        for (Scene scene : _data.getScenes())
        {
            if (scene.getId().equals(id))
                return scene;
        }
        return null;
    }

    public DataCenter getData()
    {
        return _data;
    }

    public Scene addNewScene()
    {
        Scene scene = new Scene("Scene", generateId());
        scene.setOrder(generateOrder());
        if (_data.getScenes().isEmpty()) {
            _selectedScene = scene;
        }
        _data.getScenes().add(scene);
        return scene;
    }

    public void saveData(String path)
    {
        _dataFilesManager.saveSoftware(path, _data);
    }


    public void loadData(String path)
    {
        _data = _dataFilesManager.loadSoftware(path);
        for (Scene scene : _data.getScenes()) {
            scene.printSceneDebug();
        }
        _order = 0;
        _id = 0;
        if (_data.getScenes() != null)
            _selectedScene = _data.getScenes().get(0);

        else
        {
            addNewScene();
        }
    }

    @Override
    public boolean Restart() {
        return true;
    }

    @Override
    public boolean Stop() {
        return true;
    }

    @Override
    public void Start() {
    }
}
