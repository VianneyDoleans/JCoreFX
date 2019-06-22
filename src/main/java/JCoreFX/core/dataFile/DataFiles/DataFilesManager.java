package JCoreFX.core.dataFile.DataFiles;

import JCoreFX.core.dataFile.DataCenter;

import java.io.*;

/**
 * class used to manage DataCenter Files
 */
public class DataFilesManager
{
    private SaveDataFile _saveDataFile;
    private LoadDataFile _loadData;

    public DataFilesManager()
    {
        _saveDataFile = new SaveDataFile();
        _loadData = new LoadDataFile();
    }

    public void saveSoftware(String path, DataCenter data)
    {
        _saveDataFile.saveAssets(path, data);
        _saveDataFile.saveInfoData(path, data);
        _saveDataFile.saveScenes(path, data);
        // TODO : saveWorkSpace
    }

    public DataCenter loadSoftware(String path)
    {
        DataCenter dataCenter =  _loadData.loadData(path);
        File directory = new File(path);
        _loadData.loadAssets(directory.getParent(), dataCenter);
        return dataCenter;
    }
}
