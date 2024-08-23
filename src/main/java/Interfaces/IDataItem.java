package Interfaces;

import jdk.jfr.Description;

import java.util.Map;

public interface IDataItem {
    @Description("Возвращает порядковый номер элемента в исходном наборе данных")
    Integer Index();
    @Description("Возвращает значение поля")
    Object Field(String key);
    @Description("Возвращает все поля объекта")
    Map<String, Object> Fields();
    @Description("Возвращает значение аттрибута")
    Object Attr(String key);
    @Description("Возвращает все аттрибуты объекта")
    Map<String, Object> Attrs();
}
