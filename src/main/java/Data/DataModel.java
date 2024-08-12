package Data;

import Interfaces.IDataModelItem;
import org.jabricks.widgets.treetable.AbstractTreeTableModel;
import org.jabricks.widgets.treetable.ObjectNode;
import org.jabricks.widgets.treetable.ObjectRecord;

import java.lang.reflect.Field;
import java.util.List;

public class DataModel extends AbstractTreeTableModel {

    private List<IDataModelItem> items;

    public DataModel() {super((Object)null);}
    public DataModel(List<IDataModelItem> items) {
        super((Object)null);
        this.items = items;
    }

    public int getColumnCount() {
        return items.size();
    }

    public String getColumnName(int column) {
        return items.get(column).getTitle();
    }

    public Class<?> getColumnClass(int column) {
        return items.get(column).getClass();
    }

    public boolean isLeaf(Object node) {
        return ((ObjectNode)node).isLeaf();
    }

    public String getColumnTitle(int column) {
        return items.get(column).getTitle();
    }

    public void setRootNode(ObjectNode root) {
        this.root = root;
    }

    private Object getValue(String fldName, Object object) {
        Object value = null;

        try {
            Field field = object.getClass().getDeclaredField(fldName);
            field.setAccessible(true);
            value = field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException var5) {
        }

        return value;
    }

    public Object getValueAt(Object node, int column) {
        if (this.items == null) {
            return null;
        } else {
            Object obj = this.getValue(this.items.get(column).getName(), ((ObjectNode)node).getRecord());
            return obj;
        }
    }
    public Object getChild(Object node, int i) {
        return this.getChildren(node)[i];
    }

    private Object[] getChildren(Object node) {
        return ((ObjectNode)node).getChildren();
    }

    public int getChildCount(Object node) {
        Object[] children = this.getChildren(node);
        return children == null ? 0 : children.length;
    }

    private static void convertRecord2Node(ObjectNode node) {
        ObjectRecord record = (ObjectRecord)node.getRecord();
        if (record.getChildren() != null) {
            node.setChildren(new ObjectNode[record.getChildren().size()]);
            if (record.getChildren().size() > 0) {
                for(int i = 0; i < record.getChildren().size(); ++i) {
                    ObjectRecord child = (ObjectRecord)record.getChildren().get(i);
                    ObjectNode obj_node = new ObjectNode(child);
                    obj_node.setChildren(new ObjectNode[0]);
                    obj_node.setParent(node);
                    node.getChildren()[i] = obj_node;
                    if (child.getChildren().size() > 0) {
                        obj_node.setChildren(new ObjectNode[child.getChildren().size()]);

                        for(int j = 0; j < child.getChildren().size(); ++j) {
                            ObjectRecord descendant = (ObjectRecord)child.getChildren().get(j);
                            ObjectNode onode = new ObjectNode(descendant);
                            onode.setChildren(new ObjectNode[0]);
                            onode.setParent(obj_node);
                            obj_node.getChildren()[j] = onode;
                            convertRecord2Node(onode);
                        }
                    }
                }
            }
        }

    }

    public static void setSignificantFields(String fieldName, String fieldLeaf) {
        ObjectNode.setSignificantFields(fieldName, fieldLeaf);
    }

    public static ObjectNode convertRecord2Node(ObjectRecord record) {
        ObjectNode main = new ObjectNode(record);
        main.setChildren(new ObjectNode[record.getChildren().size()]);

        for(int i = 0; i < record.getChildren().size(); ++i) {
            ObjectNode obj_node = new ObjectNode(record.getChildren().get(i));
            obj_node.setChildren(new ObjectNode[0]);
            obj_node.setParent(main);
            main.getChildren()[i] = obj_node;
            convertRecord2Node(obj_node);
        }

        return main;
    }

}
