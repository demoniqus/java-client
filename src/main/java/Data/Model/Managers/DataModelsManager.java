package Data.Model.Managers;

import Data.DataModel;
import Data.DataModelItem;
import Interfaces.IDataModel;
import Interfaces.IDataModelItem;
import Interfaces.IDataModelManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jdk.jfr.Description;
import org.jabricks.widgets.treetable.TreeTableModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Description("Менеджер моделей данных")
public class DataModelsManager implements IDataModelManager {
    @Override
    public IDataModel getModel(Map<String, Object> params) {
        //TODO Реализовать запрос данных с сервера. Зарегистрировать в Main::registerServices данный сервис как  IDataModelManager
        String modelName = (String) params.get("modelName");
        Map<String, Integer> orders = new HashMap<>();
        Map<String, IDataModelItem> headersDict = new HashMap<>();
        Map<Integer, String> dict = new HashMap<>();
        JsonObject o;
        IDataModel model;

        try {

            Gson gson = new Gson();
            String lp = System.getProperty("user.dir");
            Path p = Path.of(lp + "/src/main/resources/json/" + modelName + "/habarovsk/headers.json");
            String s = Files.readString(p);

            o = gson.fromJson(s, JsonObject.class);
            JsonObject fields = o.get("fields").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = fields.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                JsonObject fieldParams = entry.getValue().getAsJsonObject();
                Class<?> type = getType( modelName, entry.getKey());

                JsonPrimitive name = fieldParams.get("name").getAsJsonPrimitive();
                int width = 25;
                if (fieldParams.has("width")) {
                    width = fieldParams.get("width").getAsInt();
                    width = width < 1 ? 25 : width;
                }
                IDataModelItem dataModelItem = new DataModelItem(type, entry.getKey(), name.getAsString());
                dataModelItem.setWidth(width);

                Integer order = fieldParams.get("sequenceColumn").getAsInt();
                orders.put(entry.getKey(), order);
                headersDict.put(entry.getKey(), dataModelItem);
                dict.put(order, entry.getKey());

                //TODO Схлопнуть возможные пробелы в нумерации
                //TODO Для ошибок вывести отдельный значок на рабочей форме, открывать отдельное всплывающее окошко и вести лог в отдельный файл, который можно направить администратору
            }

            List<IDataModelItem> items = headersDict.values()
                .stream()
                .sorted(
                new Comparator<IDataModelItem>() {
                    public int compare(IDataModelItem item1, IDataModelItem item2) {
                        Integer o1 = orders.get(item1.Name());
                        Integer o2 = orders.get(item2.Name());
                        return o1.compareTo(o2);
                    };
                }
                )
                .collect(Collectors.toList());
            for (Map.Entry<String, Integer> entry : orders.entrySet()) {
                items.add(entry.getValue() - 1, headersDict.get(entry.getKey()));
            }

            model = new DataModel();
            for (IDataModelItem item : items) {
                model.put(item.Name(), item);
            }

        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    private Class<?> getType(String modelName, String headerName)
    {
        //TODO Заглушка. Сейчас нет нормального способа определения типа данных в колонке
        Map<String, Map<String, Class<?>>> map = new HashMap<>();

        Map<String, Class<?>> mapBudget = new HashMap<>();
        mapBudget.put("incomeStage.activity", String.class);
        mapBudget.put("tree", String.class);
        mapBudget.put("incomeStage.number", String.class);
        mapBudget.put("incomeStage.regNumber", String.class);
        mapBudget.put("incomeStage.disableAutoCalc", Boolean.class);
        mapBudget.put("incomeStage.hasBdrCustomer", Boolean.class);
        mapBudget.put("incomeStage.name", TreeTableModel.class);
        mapBudget.put("doc.number", String.class);
        mapBudget.put("estimate.number", String.class);
        mapBudget.put("estimate.regNumber", String.class);
        mapBudget.put("incomeStage.state", String.class);
        mapBudget.put("incStageSum.manualTotal", Float.class);
        mapBudget.put("incStageSum.manualEquipment", Float.class);
        mapBudget.put("incStageSum.manualSmr", Float.class);
        mapBudget.put("incStageSum.manualOther", Float.class);
        mapBudget.put("incomeStage.tkpEquipment", Float.class);
        mapBudget.put("incomeStage.tkpSmr", Float.class);
        mapBudget.put("incomeStage.tkpOther", Float.class);
        mapBudget.put("incStageSum.calcTotal", Float.class);
        mapBudget.put("incStageSum.calcEquipment", Float.class);
        mapBudget.put("incStageSum.calcSmr", Float.class);
        mapBudget.put("incStageSum.calcOther", Float.class);
        mapBudget.put("incomeStage.kind", ArrayList.class);
        mapBudget.put("upSum.estimate", Float.class);
        mapBudget.put("incomeStage.costs", Float.class);
        mapBudget.put("incomeStage.result", Float.class);
        mapBudget.put("delivery.request", Float.class);
        mapBudget.put("delivery.requestEquipment", Float.class);
        mapBudget.put("delivery.requestMaterial", Float.class);
        mapBudget.put("delivery.selling", Float.class);
        mapBudget.put("upSum.bdr", Float.class);
        mapBudget.put("delivery.volume", Float.class);
        mapBudget.put("delivery.measure", String.class);
        mapBudget.put("delivery.manHour", Float.class);
        mapBudget.put("delivery.smrPriceOfUnit", Float.class);
        mapBudget.put("delivery.equipmentPriceOfUnit", Float.class);
        mapBudget.put("delivery.otherPriceOfUnit", Float.class);
        mapBudget.put("downSum.estimate", Float.class);
        mapBudget.put("downEstimate.equipment", Float.class);
        mapBudget.put("downEstimate.smr", Float.class);
        mapBudget.put("downEstimate.pnr", Float.class);
        mapBudget.put("downEstimate.pir", Float.class);
        mapBudget.put("downEstimate.other", Float.class);
        mapBudget.put("downSum.bdr", Float.class);
        mapBudget.put("incomeStage.uid", String.class);
        mapBudget.put("incStageSum.limitTotal", Float.class);
        mapBudget.put("incStageSum.limitEquipment", Float.class);
        mapBudget.put("incStageSum.limitSmr", Float.class);
        mapBudget.put("incStageSum.limitOther", Float.class);

        map.put("budget", mapBudget);

        Map<String, Class<?>> mapEstimate = new HashMap<>();

        mapEstimate.put("tree", String.class);
        mapEstimate.put("estimate.priority", String.class);
        mapEstimate.put("estimate.regNumber", String.class);
        mapEstimate.put("estimate.number", TreeTableModel.class);
        mapEstimate.put("estimate.activity", String.class);
        mapEstimate.put("hasBudgetItems", Boolean.class);
        mapEstimate.put("workDoc.number", String.class);
        mapEstimate.put("workDoc.innerName", String.class);
        mapEstimate.put("estimate.bcSum", Float.class);
        mapEstimate.put("estimate.sumWoNds", Float.class);
        mapEstimate.put("estimate.sendSignDate", String.class);//TODO Дата
        mapEstimate.put("estimate.signDate", String.class);
        mapEstimate.put("estimate.note", String.class);
        mapEstimate.put("estimate.comment", String.class);
        mapEstimate.put("contragent.name", String.class);

        map.put("estimate", mapEstimate);

        Map<String, Class<?>> mapBdrCust = new HashMap<>();

        mapBdrCust.put("tree", String.class);
        mapBdrCust.put("item.id", TreeTableModel.class);
        mapBdrCust.put("item.stageNumber", String.class);
        mapBdrCust.put("item.stageName", String.class);
        mapBdrCust.put("item.estimateName", String.class);
        mapBdrCust.put("item.date", String.class);
        mapBdrCust.put("item.number", String.class);
        mapBdrCust.put("item.ks3", String.class);
        mapBdrCust.put("item.bcTotalSum", Float.class);
        mapBdrCust.put("item.bcEquipmentSum", Float.class);
        mapBdrCust.put("item.bcMaterialSum", Float.class);
        mapBdrCust.put("item.bcServiceSum", Float.class);
        mapBdrCust.put("item.sumWoNds", Float.class);
        mapBdrCust.put("item.equipmentSum", Float.class);
        mapBdrCust.put("item.materialSum", Float.class);
        mapBdrCust.put("item.serviceSum", Float.class);
        mapBdrCust.put("item.sumWithNds", Float.class);
        mapBdrCust.put("item.activity", String.class);
        mapBdrCust.put("item.contract", String.class);
        mapBdrCust.put("contract.project", String.class);

        map.put("bdr_customer", mapBdrCust);

        if (map.containsKey(modelName) && map.get(modelName).containsKey(headerName)) {
            Class<?> type = map.get(modelName).get(headerName);
            return type;
        }

        return String.class;

    }

}
