package Interfaces;

import java.util.List;
import java.util.Map;

public interface IDataAdapter {
    <T> T adapt(List<IDataItem> dataItems, Map<String, Object> params);
}
