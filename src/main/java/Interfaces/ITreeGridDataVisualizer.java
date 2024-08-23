package Interfaces;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface ITreeGridDataVisualizer {
    void visualize(
            JPanel container,
            IDataModel dataModel,
            List<IDataItem> dataItems,
            Map<String, Object> params
    );
//    <T> T OptionsAdapter();
}
