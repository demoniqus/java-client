package Interfaces;

public interface IDataModelItem {
    Class<?>getClassName();
    String getName();
    String getTitle();
    IDataModelItem setTitle(String title);
    int getWidth();
    IDataModelItem setWidth(int width);
}
