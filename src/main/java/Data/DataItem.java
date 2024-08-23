package Data;

import Interfaces.IDataItem;
import jdk.jfr.Description;

import java.util.HashMap;
import java.util.Map;

@Description("Базовый элемент данных из источника")
public class DataItem implements IDataItem {
    @Description("Порядковый номер элемента в базовом наборе")
    private Integer index = 0;
    @Description("Поля объекта")
    private Map<String, Object> fields = new HashMap<String, Object>();
    @Description("Аттрибуты объекта (класс, id и т.п.)")
    private Map<String, Object> attrs = new HashMap<String, Object>();

    public DataItem(Integer index, Map<String, Object> fields, Map<String, Object> attrs) {
        //Args.notNull(index);
        this.index = index;
        this.fields = fields;
        this.attrs = attrs;
    }

    public Integer Index() {
        return index;
    }

    public Map<String, Object> Fields() {
        return new HashMap<>(fields);
    }

    public Object Field(String key) {

        return fields.get(key);
    }
    public Map<String, Object> Attrs() {
        return new HashMap<>(attrs);
    }

    public Object Attr(String key) {

        return attrs.get(key);
    }

}
