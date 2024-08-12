package Interfaces;

import javax.swing.*;

public interface IDataSource {
    String getTitle();
    Icon getIcon();
    void load(JPanel workPanel);
}
