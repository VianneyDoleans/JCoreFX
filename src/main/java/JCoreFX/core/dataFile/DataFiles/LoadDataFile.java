package JCoreFX.core.dataFile.DataFiles;

import JCoreFX.core.dataFile.DataCenter;
import JCoreFX.core.jsonConstruction.JSONFile;
import JCoreFX.core.dataConstruction.DataElement;
import JCoreFX.core.dataFile.Scene;
import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import JCoreFX.core.dataConstruction.ListResourceTypes;

import java.util.Stack;

/**
 * class used to load a DataCenter from DataCenter JSON files
 */
public class LoadDataFile
{
    private Scene loadInfoScene(JSONObject infoSceneJSON)
    {
        Scene scene;

        scene = new Scene(infoSceneJSON.get("Name").toString(), infoSceneJSON.get("Id").toString());
        scene.setOrder(Integer.parseInt(infoSceneJSON.get("Order").toString()));
        return scene;
    }

    private void generateNode(Object element, Scene.Node parentNode)  {
        ListResourceTypes listResourceTypes = ListResourceTypes.getInstance();
        Scene.Node newNode;

        for (ListResourceTypes.TypeResource resourceType : listResourceTypes.getList())
        {
            if (resourceType.getName().equals(((JSONObject)element).get("Type").toString()))
            {
                try {
                    DataElement classResourceType =  resourceType.getClassType().newInstance();
                    classResourceType.setFromJSON((JSONObject)((JSONObject)element).get("Content"));
                     newNode = new Scene.Node(((JSONObject) element).get("Type").toString(),
                            ((JSONObject) element).get("Id").toString(),
                            ((JSONObject) element).get("Name").toString(),
                             classResourceType);
                } catch (Exception e) {
                    newNode = new Scene.Node(((JSONObject) element).get("Type").toString(),
                            ((JSONObject) element).get("Id").toString(),
                            ((JSONObject) element).get("Name").toString(),
                            null);
                }
                loadGraph((JSONArray) (((JSONObject) element).get("Children")), newNode);
                parentNode.getChildren().add(newNode);
                break;
            }
        }
    }

    private void loadGraph(JSONArray graphJSON, Scene.Node parentNode)
    {
        if (graphJSON != null) {
            graphJSON.forEach(element -> {
                generateNode(element, parentNode);
            });
        }
    }

    private Scene loadScene(String path, JSONObject infoSceneJSON)
    {
        System.out.println(path + infoSceneJSON.get("Path").toString());
        JSONFile sceneJSON = new JSONFile(path + "/" + infoSceneJSON.get("Path").toString(),
                JSONFile.JSONFileType.Object);
        sceneJSON.parse();
        Scene scene = loadInfoScene(infoSceneJSON);
        JSONObject graphJSON = sceneJSON.get("Scene");
        loadGraph((JSONArray)(graphJSON.get("Children")),
                scene.getRoot());
        return scene;
    }

    void loadAssets(String path, DataCenter dataCenter) {
        for (Scene scene : dataCenter.getScenes()) {
            Stack<Scene.Node> stack = new Stack<>();
            stack.push(scene.getRoot());
            while (!stack.isEmpty()) {
                Scene.Node node = stack.pop();
                if (!(node.getType().equals("FOLDER") || node.getId().equals("-1"))) {
                    node.getContent().setPath(path + node.getContent().getPath());
                }
                for (Scene.Node child : node.getChildren()) {
                    stack.push(child);
                }
            }
        }
    }

    DataCenter loadData(String path)
    {
        DataCenter data = new DataCenter();
        JSONFile jsonFile = new JSONFile(path, JSONFile.JSONFileType.Object);
        jsonFile.parse();

        JSONObject dataJSON = jsonFile.get("DataCenter");
        JSONArray scenesJSON = (JSONArray)dataJSON.get("Scenes");
        System.out.println(path);
        String pathDirectory = SystemUtils.IS_OS_WINDOWS ? path.substring(0, path.lastIndexOf("\\")) : path.substring(0, path.lastIndexOf("/"));
        if (scenesJSON != null) {
            scenesJSON.forEach(infoSceneJSON -> {
                data.getScenes().add(loadScene(pathDirectory, (JSONObject) infoSceneJSON));
            });
        }
        return data;
    }
}
