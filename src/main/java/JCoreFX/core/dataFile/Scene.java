package JCoreFX.core.dataFile;

import JCoreFX.core.dataConstruction.DataElement;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Class used to store a scene of a DataCenter
 */
public class Scene
{
    private int _order;
    private Node _root;
    private String _name;
    private String _id;

        public static class Node
        {
            private String _type;
            private String _id;
            private String _name;
            private DataElement _content;
            private ListNode<Node> _children;
            Node _parent;

            public class ListNode<T extends Node> extends ArrayList<T>
            {
                @Override
                public boolean add(T e)
                {
                    boolean result = super.add(e);
                    e._parent = Node.this;
                    return (result);
                }

                @Override
                public void add(int position, T e)
                {
                    super.add(position, e);
                    e._parent = Node.this;
                }

                public boolean remove(String id)
                {
                    for (T child : this)
                    {
                        if (child.getId().equals(id)) {
                            this.remove(child);
                            return (true);
                        }
                    }
                    return (false);
                }

                @Override
                public boolean remove(Object o)
                {
                    boolean result = super.remove(o);
                    return (result);
                }
            }

            public Node(String type, String id, String name, DataElement content)
            {
                _children = new ListNode<>();
                _type = type;
                _id = id;
                _name = name;
                _content = content;
            }

            public Node getParent() { return (_parent);}

            public ListNode<Node> getChildren()
            {
                return (_children);
            }

            public String getType()
            {
                return _type;
            }

            public String getId()
            {
                return _id;
            }

            public String getName()
            {
                return _name;
            }

            public DataElement getContent() { return _content; }

            public String print()
            {
                return ("id : <" + _id + ">, name : <" + _name + ">, type : <" + _type + ">");
            }

        }

    public Scene(String name, String id)
    {
        _root = new Node("NONE", "-1", "Root", null);
        _name = name;
        _id = id;
    }

    public void removeNode(String id)
    {
        Node search = getNode(id);
        if (search != null)
        {
            search.getParent().getChildren().remove(search);
        }
    }

    public Node getNode(String id) {
        Stack<Node> stack = new Stack<>();

        stack.push(_root);
        while (!stack.isEmpty()) {
            Node parent = stack.pop();
            if (parent.getId().equals(id))
                return parent;
            for (Node child : parent.getChildren())
                stack.push(child);
        }
        return null;
    }

    public Node getRoot() { return _root; }

    public String getName() { return _name; }

    public String getId() { return _id; }

    public int getOrder() { return _order; }

    public void setOrder(int order) { _order = order; }

    public void printSceneDebug()
    {
        System.out.println("Representation of Scene, name : <" + _name + "> - id : <" + _id + "> :");
        Stack<Node> stack = new Stack<>();

        stack.push(_root);
        while (!stack.isEmpty())
        {
            String display;
            Node node = stack.pop();

            display = "node : [" + node.print();
            if (!node.getChildren().isEmpty())
                display += "], children : [";
            boolean firstTime = true;
            for (Node child : node.getChildren())
            {
                if (firstTime)
                {
                    display += child.print();
                    firstTime = false;
                }
                else
                    display += " || " + child.print();
                stack.push(child);
            }
            display += "]";
            System.out.println(display);
        }
    }
}
