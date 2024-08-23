package Data.Adapter;


import Data.DataItem;
import Data.Record;
import Interfaces.IDataAdapter;
import Interfaces.IDataItem;
import jdk.jfr.Description;

import java.util.*;
@Description("адаптер данных для компонента Jabrics")
public class JabricsTreeGridDataAdapter implements IDataAdapter {
    @Override
    public <T> T adapt(List<IDataItem> dataItems , Map<String, Object> params) {
        String modelName = (String) params.get("modelName");

        //dataItems.clear();
        switch (modelName)
        {
            case "budget":
                return (T)adaptBudgetData(dataItems);
            case "estimate":
                return (T)adaptEstimateData(dataItems);
            case "bdr_customer":
                return (T)adaptBdrCustData(dataItems);
        }
        throw new RuntimeException("Cannot adapt " + modelName);
    }

    private Record adaptBudgetData(List<IDataItem> dataItems) {
        Record rootRecord = null;

        Map<String, Record> stagesDictByNumber = new HashMap<>();

        Integer l = dataItems.size();
        Record lastStage = null;
        for (int i = 0; i < l; i++) {
            IDataItem row = dataItems.get(i);
            Record rec = new Record(
                    (Integer) row.Attr("entityId"),
                    (String) row.Attr("entityClass"),
                    "incomeStage.name",
                    row
            );
            if (Objects.equals(rec.getShortType(), "IncomeStage")) {
                lastStage = rec;
                String number = (String)rec.get("incomeStage.number");
                number = number.replaceAll("\\.$", "");
                if (Objects.equals(number, "main")) {
                    rootRecord = rec;
                    stagesDictByNumber.put(number, rec);
                    // has no parent
                    continue;
                }
                //Если есть Глава или Раздел, то этапы с простым номером (1, 2, ..., N ) надо встраивать в дерево по особым правилам
                number = number.replaceAll("[^0-9.]", "").replaceAll("\\.$", "");
                stagesDictByNumber.put(number, rec);
                String[] parts = number.split("\\.");
                Record parent;
                if (parts.length == 1) {
                    parent = rootRecord;
                }
                else {
                    String parentNumber = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
                    parent = stagesDictByNumber.get(parentNumber);
                }
                parent.setLeaf(false);
                parent.getChildren().add(rec);

            }
            else {
                lastStage.setLeaf(false);
                lastStage.getChildren().add(rec);
            }
        }

        return rootRecord;
    }
    private Record adaptEstimateData(List<IDataItem> dataItems) {
        Integer l = dataItems.size();
        Record lastEstimatesGroup = null;

        Record rootRecord = new Record(0, "Root", "estimate.number", getRootDataItem()/*TODO new DataItem*/);
        rootRecord.setLeaf(false);
        for (int i = 0; i < l; i++) {
            IDataItem row = dataItems.get(i);
            Record rec = new Record(
                    (Integer) row.Attr("entityId"),
                    (String) row.Attr("entityClass"),
                    "incomeStage.name",
                    row
            );
            if (Objects.equals(rec.getShortType(), "EstimateGroup")) {
                lastEstimatesGroup = rec;
                rootRecord.getChildren().add(rec);

            } else {
                lastEstimatesGroup.setLeaf(false);
                lastEstimatesGroup.getChildren().add(rec);
            }
        }
        return rootRecord;
    }

    private IDataItem getRootDataItem()
    {
        return new DataItem(-1, new HashMap<String, Object>(),new HashMap<String, Object>());
    }

    private Record adaptBdrCustData(List<IDataItem> dataItems) {
        throw new RuntimeException("Has no implementation");
    }


}
