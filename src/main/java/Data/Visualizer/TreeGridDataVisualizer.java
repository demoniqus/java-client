package Data.Visualizer;

import Data.Adapter.JabricsTreeGridDataAdapter;
import Data.JTreeTableDataModel;
import Data.Model.Adapters.JabricsTreeGridModelAdapter;
import Data.Record;
import Interfaces.*;
import app.Main;
import org.jabricks.widgets.renderers.FloatRenderer;
import org.jabricks.widgets.treetable.JTreeTable;
import org.jabricks.widgets.treetable.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TreeGridDataVisualizer implements ITreeGridDataVisualizer {

    @Override
    public void visualize(
            JPanel container,
            IDataModel dataModel,
            List<IDataItem> dataItems,
            Map<String, Object> params
    ) {
        IAdaptersManager adaptersManager = (IAdaptersManager) Main.ServiceContainer().GetService(IAdaptersManager.class);
        IDataModelAdapter modelAdapter = adaptersManager.getAdapter(IDataModelAdapter.class, this.getClass());
        IDataAdapter dataAdapter = adaptersManager.getAdapter(IDataAdapter.class, this.getClass());

        JTreeTableDataModel model = modelAdapter.adapt(dataModel);
        Record rootRecord = (Record) dataAdapter.adapt(dataItems, params);

        List<IDataModelItem> dataModelItems = model.getItems();
        ObjectNode rootNode = JTreeTableDataModel.convertRecord2Node (rootRecord);
        model.setRootNode(rootNode);

        JTreeTableDataModel.setSignificantFields("name", "leaf");


        JTreeTable treeTable = new JTreeTable(model);

        int[] width = new int[dataModel.size()];
        int counter = 0;
        for (IDataModelItem dataModelItem : dataModelItems) {
            width[counter++] = dataModelItem.getWidth();
        }
        treeTable.setColumnsWidth(width);

        treeTable.setRowHeight(22);

        treeTable.setDefaultRenderer(Float.class, new FloatRenderer());
        treeTable.setAutoResizeColumn (JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane jsp = new JScrollPane(treeTable);
        jsp.setBackground(Color.decode("0xff00ff"));
        container.add(jsp, BorderLayout.CENTER);
        treeTable.drawTableHeaderRaised();
        treeTable.updateUI();

    }
}
