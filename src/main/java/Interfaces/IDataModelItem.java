package Interfaces;

public interface IDataModelItem {
    Class<?> Type();
    String Name();
    String getTitle();
    IDataModelItem setTitle(String title);
    int getWidth();
    IDataModelItem setWidth(int width);
}
