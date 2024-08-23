package Interfaces;

import java.util.List;
import java.util.Map;

public interface IDataModel extends Map {
    List<IDataModelItem> getList();
}
