package Data;

import Interfaces.IDataModel;
import Interfaces.IDataModelItem;
import jdk.jfr.Description;

import java.util.*;

@Description("Модель данных из источника")
public class DataModel implements IDataModel {
    private Map<String, IDataModelItem> map = new HashMap<>();
    private List<IDataModelItem> list = new ArrayList<>();
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        checkKeyType(key);
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        checkValueType(value);
        return list.contains(value);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return map.get(key);
        } else if (key instanceof Integer) {
            return list.get((Integer) key);
        }
        throw new ClassCastException("Key has incorrect type");
    }

    @Override
    public Object put(Object key, Object value) {
        checkKeyType(key);
        if (!(map.containsKey(key))) {
            map.put((String) key, (IDataModelItem) value);
            list.add((IDataModelItem) value);
        }
        return null;
    }

    @Override
    public Object remove(Object key) {
        checkKeyType(key);
        IDataModelItem item = null;
        if ((map.containsKey(key))) {
            item = map.remove(key);
            int index;
            for (index = 0; index < list.size(); index++) {
                if (list.get(index) == item) {
                    break;
                }
            }
            list.remove(index);
        }
        return null;
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        list.clear();
        map.clear();
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection values() {
        //clone
        return list.stream().toList();
    }

    @Override
    public Set<Entry> entrySet() {
        throw new UnsupportedOperationException();
    }

    public List<IDataModelItem> getList() {
        //clone
        return list.stream().toList();
    }



    private void checkKeyType(Object key) {
        if (!(key instanceof String)) {
            throw new ClassCastException("Key must be type of String");
        }
    }

    private void checkValueType(Object value) {
        if (!(value instanceof IDataModelItem)) {
            throw new ClassCastException("Value must be type of IDataModelItem");
        }
    }
}
