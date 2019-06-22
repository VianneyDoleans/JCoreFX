package JCoreFX.core.dataFile.DataFiles;

import JCoreFX.core.dataFile.Scene;
import JCoreFX.core.dataFile.DataCenter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import JCoreFX.tools.ExceptionsCatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * class used to save a DataCenter on JSON files
 */
class SaveDataFile
{
    private JSONObject saveInfoScene(Scene scene)
    {
        JSONObject sceneJSON = new JSONObject();
        sceneJSON.put("Path", '/' + scene.getName() + scene.getId() + ".json");
        sceneJSON.put("Id", scene.getId());
        sceneJSON.put("Order", scene.getOrder());
        sceneJSON.put("Name", scene.getName());
        return sceneJSON;
    }

    private JSONArray saveInfoScenes(List<Scene> scenes)
    {
        JSONArray ScenesJSON = new JSONArray();

        for (Scene scene : scenes)
        {
            ScenesJSON.add(saveInfoScene(scene));
        }
        return ScenesJSON;
    }

    private JSONObject saveInfoData(DataCenter data)
    {
        JSONObject dataJSON = new JSONObject();

        List<Scene> scenes = data.getScenes();
        dataJSON.put("Scenes", saveInfoScenes(scenes));
        return dataJSON;
    }

    private JSONObject recursiveGraph(Scene.Node parent)
    {
        JSONObject parentJSON = new JSONObject();
        JSONArray childrenJSON = new JSONArray();

        parentJSON.put("Type", parent.getType());
        parentJSON.put("Id", parent.getId());
        parentJSON.put("Name", parent.getName());
        if (parent.getContent() != null)
            parentJSON.put("Content", parent.getContent().toJSON());

        if (!parent.getChildren().isEmpty()) {
            for (Scene.Node node : parent.getChildren())
                childrenJSON.add(recursiveGraph(node));
        }
        parentJSON.put("Children", childrenJSON);
        return parentJSON;
    }

    private JSONObject saveGraph(Scene.Node root)
    {
        return (recursiveGraph(root));
    }

    private JSONObject saveScene(Scene scene)
    {
        JSONObject sceneJSON = new JSONObject();

        sceneJSON.put("Scene", saveGraph(scene.getRoot()));
        return sceneJSON;
    }

    void saveScenes(String path, DataCenter dataCenter)
    {
        for (Scene scene : dataCenter.getScenes())
        {
            JSONObject sceneJSON = saveScene(scene);
            writeFile(sceneJSON, path + '/' + scene.getName() + scene.getId() + ".json");
        }
    }

    void saveAssets(String path, DataCenter dataCenter)
    {
        for (Scene scene : dataCenter.getScenes())
        {
            Stack<Scene.Node> stack = new Stack<>();
            stack.push(scene.getRoot());
            while (!stack.isEmpty())
            {
                Scene.Node node = stack.pop();
                if (!(node.getType().equals("FOLDER") || node.getId().equals("-1"))) {
                    node.getContent().changeLocation(path);
                }
                for (Scene.Node child : node.getChildren())
                {
                    stack.push(child);
                }
            }
        }
    }

    private void writeFile(JSONObject json, String path)
    {
        try (FileWriter file = new FileWriter(path)) {
            file.write(json.toJSONString());
            file.flush();
        } catch (IOException e) {
            ExceptionsCatch.PrintErrors(e);
        }
    }

    void saveInfoData(String path, DataCenter data)
    {
        JSONObject dataInfo = new JSONObject();

        dataInfo.put("DataCenter", this.saveInfoData(data));
        writeFile(dataInfo, path + "/data.json");
    }
}
