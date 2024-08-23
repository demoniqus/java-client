package Data.Model.Adapters;

import Data.JTreeTableDataModel;
import Interfaces.IDataModel;
import Interfaces.IDataModelAdapter;
import Interfaces.IDataModelItem;
import jdk.jfr.Description;

import java.util.List;
@Description("адаптер модели для компонента Jabrics")
public class JabricsTreeGridModelAdapter implements IDataModelAdapter {
    @Override
     public <T> T adapt(IDataModel dataModel)
    {
        List<IDataModelItem> dataModelItems = dataModel.getList();

        return (T)new JTreeTableDataModel(dataModelItems);
    }
}
