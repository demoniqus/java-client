package Data;

import Interfaces.IDataModelItem;
import jdk.jfr.Description;

@Description("Элемент модели данных из источника")
public class DataModelItem implements IDataModelItem {
    private String name;
    private Class<?> className;
    private String title;
    private int width = 50;

    public DataModelItem(
            Class<?> className,
            String name,
            String title
    ) {
        this.className = className;
        this.name = name;
        this.title = title;
    }
    @Override
    public String Name() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public IDataModelItem setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public IDataModelItem setWidth(int width) {
        this.width = width;
        return this;
    }


    @Override
    public Class<?> Type() {
        return className;
    }
}
