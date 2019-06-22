package JCoreFX.core.dataFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to store a DataCenter
 */
public class DataCenter
{
    private List<Scene> _scenes;

    public DataCenter()
    {
        _scenes = new ArrayList<>();
    }
    public List<Scene> getScenes() { return _scenes;}
}
