package Interfaces;

import java.util.Map;

public interface IDataModelManager {
    IDataModel getModel(Map<String, Object> params);
}
