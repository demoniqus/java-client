package Data;

import Interfaces.IDataItem;
import Interfaces.IDataModel;
import Interfaces.IDataModelItem;
import com.google.gson.*;
import jdk.jfr.Description;
import org.jabricks.widgets.treetable.TreeTableModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Description("Менеджер загрузки данных из источника")
public class JTreeTableDataManager {

    private JsonArray getJsonData(String modelName)
    {
        try {

            Gson gson = new Gson();
            String lp = System.getProperty("user.dir");
            Path p = Path.of(lp + "/src/main/resources/json/" + modelName + "/habarovsk/data.json");

            String s = Files.readString(p);

            return gson.fromJson(s, JsonObject.class).get("rows").getAsJsonArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<IDataItem> loadData(Map<String, Object> params, IDataModel dataModel)
    {
        List<IDataItem> dataItems = new ArrayList<>();
        List<IDataModelItem> model = dataModel.getList();
        JsonArray rows = getJsonData((String) params.get("modelName"));
        Integer l = rows.size();

        for (int i = 0; i < l; i++) {
            Map<String, Object> cells = new HashMap<>();
            Map<String, Object> attrs = new HashMap<>();

            JsonObject row = rows.get(i).getAsJsonObject();

            JsonObject jsonCells = row.get("cell").getAsJsonObject();

            JsonElement cellValue = null;
            for (IDataModelItem dataModelItem : model) {
                cellValue = jsonCells.get(dataModelItem.Name());
                if (cellValue instanceof JsonPrimitive) {
                    if (dataModelItem.Type() == String.class) {
                        cells.put(dataModelItem.Name(), cellValue.getAsString());
                    } else if (dataModelItem.Type() == Integer.class) {
                        cells.put(dataModelItem.Name(), cellValue.getAsInt());
                    } else if (dataModelItem.Type() == Float.class) {
                        cells.put(dataModelItem.Name(), cellValue.getAsFloat());
                    } else if (dataModelItem.Type() == Boolean.class) {
                        cells.put(dataModelItem.Name(), cellValue.getAsBoolean());
                    } else if (dataModelItem.Type() == ArrayList.class) {
                        List<Object> list = new ArrayList<>();
                        JsonArray cellValues = cellValue.getAsJsonArray();
                        for (int j = 0; j < cellValues.size(); j++) {
                            list.add(cellValues.get(j).getAsString());
                        }
                    } else if (dataModelItem.Type() == TreeTableModel.class) {
                        cells.put(dataModelItem.Name(), cellValue);
                    }
                } else if (cellValue instanceof JsonNull) {
                    cells.put(dataModelItem.Name(), null);
                }
                else {
                    //TODO JsonArray, JsonObject
                }
                //TODO Избавиться от JsonPrimitive
//                    data.put(dataModelItem.Name(), cellValue);

            }
            //TODO Это вспомогательные системные поля - можно сделать отдельный класс dataSystemModel,
            // либо с сервака получать системные поля,
            // либо к самим полям на серваке приписывать флаг "системное поле",
            // либо придерживаться формата entityAlias.fieldName - поле сущности, fieldName - системное поле
            String key = "level";
            cells.put(key, jsonCells.get(key).getAsInt());

            key = "lft";
            cells.put(key, jsonCells.get(key).getAsInt());

            key = "rgt";
            cells.put(key, jsonCells.get(key).getAsInt());

            key = "expanded";
            cells.put(key, jsonCells.get(key).getAsBoolean());

            key = "isLeaf";
            cells.put(key, jsonCells.get(key).getAsBoolean());

            JsonObject jsonAttrs = row.get("attrs").getAsJsonObject();
            Integer id = jsonAttrs.get("entity").getAsInt();
            String type = jsonAttrs.get("entityClass").getAsString();
            attrs.put("entityId", id);
            attrs.put("entityClass", type);

            dataItems.add(new DataItem(i, cells, attrs));
        }

        return dataItems;
    }
}
