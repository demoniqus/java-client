package Interfaces;

import jdk.jfr.Description;

import javax.swing.*;
@Description("Приложение имеет интерфейс основной рабочей области, которую можно настраивать под потребности конкретного сервера данных")
public interface IMainWorkarea {
    @Description("В любом случае основная рабочая область содержит основное меню")
    JMenuBar getMenuContainer();
    @Description("В любом случае основная рабочая область содержит основную панель для вывода данных")
    JPanel getWorkPanel();
}
