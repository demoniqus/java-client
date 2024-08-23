package Data;

import Interfaces.IDataItem;
import jdk.jfr.Description;
import org.jabricks.widgets.treetable.ObjectRecord;

@Description("Базовая заготовка элемента данных для компонента Jabrics")
public class Record extends ObjectRecord {
    private IDataItem data;
    private String type;
    private String shortType;
    private Integer id;
    private boolean leaf = true;
    private String nameField;
//    private Record parent = null;
//    private List<Record> children = new ArrayList<>();
    private Integer index;

    public Record(Integer id, String type, String nameField, IDataItem data) {
        super();
        this.id = id;
        this.type = type;
        this.data = data;
        String[] parts = type.split("\\\\");
        shortType = parts[parts.length - 1];
    }

//    public void setParent(Record parent) {
//        this.parent = parent;
//    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    public String getShortType()
    {
        return shortType;
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

//    public void addChild(Record child)
//    {
//        children.add(child);
//    }

    public String getName()
    {
        return data.Field(nameField).toString();
    }

    public Object get(String key) {
        return data.Field(key);
    }
}
