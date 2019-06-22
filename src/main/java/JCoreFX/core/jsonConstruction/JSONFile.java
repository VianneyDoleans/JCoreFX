package JCoreFX.core.jsonConstruction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.regex.Pattern;

/**
 * Class used for saving and loading data in JSON File, like software's configuration
 */
public class JSONFile {

    public enum JSONFileType {
        Object,
        Array
    }

    public String path;
    public JSONFileType type;

    private JSONParser _parser;
    private Object _body;

    /**
     * Type: 'Object', 'Array' by default Object
     * @param path path of the JSON File
     * @param type Type of JSONFileType element
     */
    public JSONFile(String path, JSONFileType type) {
        this._parser = new JSONParser();
        this.path = path;
        this.type = type;
    }

    public void parse() {
        try {
            this._body = this._parser.parse(
                    new FileReader(this.path)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String key) {
        if (this.type == JSONFileType.Object) {
            String[] parts = key.split(Pattern.quote("."));
            int partIdx = 0;
            JSONObject json = (JSONObject) this._body;
            /*
            No way to do a better type check in Java: http://www.informit.com/articles/article.aspx?p=2861454&seqNum=2
            Reason: This method is used to get data from a JSON file,
            we are assuming here that the developer know the data structure of the file.
            */
            while (partIdx + 1 < parts.length) {
                System.out.println("PART : " + parts[partIdx]);
                json = (JSONObject) json.get(parts[partIdx]);
                partIdx += 1;
            }
            try {
                @SuppressWarnings("unchecked") T result = (T) json.get(parts[partIdx]);
                return result;
            } catch (NullPointerException e) {
                return null;
            }
        } else {
            JSONArray array = (JSONArray) this._body;
            @SuppressWarnings("unchecked") T result = (T) array.get(Integer.parseInt(key));

            return result;
        }
    }

}
