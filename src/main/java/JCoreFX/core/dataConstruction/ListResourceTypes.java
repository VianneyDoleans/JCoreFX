package JCoreFX.core.dataConstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * List all Resource with its class and its name associated.
 * Resources : need to be declared in this class to be Load.
 */
public class ListResourceTypes
{
    private static ListResourceTypes _oneInstance;
    private List<TypeResource> _list;

    public class TypeResource
    {
        String _name;
        Class _classType;

        public String getName()
        {
            return _name;
        }
        public <T extends DataElement> Class<T> getClassType()
        {
            return _classType;
        }

        public <T extends DataElement> void setClassType(Class<T> classType)
        {
            _classType = classType;
        }

        public void setName(String name)
        {
            _name = name;
        }

        <T extends DataElement> TypeResource(String name, Class<T> classType)
        {
            _name = name;
            _classType = classType;
        }
    }

    private ListResourceTypes()
    {
        _list = new ArrayList<>();
        /*_list.add(new TypeResource("IMAGE", ImageScene.class));
        _list.add(new TypeResource("FOLDER", null));
        _list.add(new TypeResource("SOUND", SoundScene.class));
        _list.add(new TypeResource("ANIMATION", AnimationScene.class));*/
    }

    public List<TypeResource> getList()
    {
        return _list;
    }

    public static ListResourceTypes getInstance() {
        if (_oneInstance == null)
        {
            _oneInstance = new ListResourceTypes();
        }
        return _oneInstance;
    }
}
